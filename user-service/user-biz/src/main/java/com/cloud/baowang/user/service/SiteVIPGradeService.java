package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.common.core.utils.tool.vo.Comparison;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeUpdateVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.enums.ChangeOperationEnum;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.SiteVIPOperationPO;
import com.cloud.baowang.user.po.SiteVIPRankPO;
import com.cloud.baowang.user.po.VIPGradePO;
import com.cloud.baowang.user.repositories.SiteVIPGradeRepository;
import com.cloud.baowang.user.repositories.SiteVIPRankRepository;
import com.cloud.baowang.user.repositories.VIPGradeRepository;
import com.cloud.baowang.user.util.MinioFileService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 2024/8/2 14:48
 * @Version : 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteVIPGradeService extends ServiceImpl<SiteVIPGradeRepository, SiteVIPGradePO> {

    private SiteVIPGradeRepository siteVIPGradeRepository;
    private VIPOperationService vipOperationService;
    private VIPGradeRepository vipGradeRepository;
    private final SiteVIPRankRepository vipRankRepository;
    private MinioFileService minioFileService;
    private SiteVipOptionService siteVipOptionService;
    private SiteApi siteApi;

    public ResponseVO<Page<SiteVIPGradeVO>> queryVIPGradePage(String siteCode, PageVO pageVO) {
        Page<SiteVIPGradeVO> page = new Page<>(pageVO.getPageNumber(), pageVO.getPageSize());
        Page<SiteVIPGradeVO> resultPage = siteVIPGradeRepository.queryVIPGradePage(page, siteCode);
        String minioDomain = minioFileService.getMinioDomain();
        resultPage.convert(item -> {
            String picIcon = item.getPicIcon();
            if (StringUtils.isNotBlank(picIcon)) {
                item.setPicIconImage(minioDomain + "/" + picIcon);
            }
            return item;
        });
        return ResponseVO.success(resultPage);
    }

    public ResponseVO<?> updateVIPGrade(VIPGradeUpdateVO vipGradeUpdateVO) {
        try {
            String operator = CurrReqUtils.getAccount();
            String siteCode = CurrReqUtils.getSiteCode();
            // 更新前旧数据
            SiteVIPGradePO oldVipGradePO = this.getOne(new LambdaQueryWrapper<SiteVIPGradePO>()
                    .eq(SiteVIPGradePO::getSiteCode, siteCode).eq(SiteVIPGradePO::getVipGradeCode,
                            vipGradeUpdateVO.getVipGradeCode()));
            LambdaUpdateWrapper<SiteVIPGradePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteVIPGradePO::getSiteCode, siteCode)
                    .eq(SiteVIPGradePO::getVipGradeCode, vipGradeUpdateVO.getVipGradeCode());
            updateWrapper.set(SiteVIPGradePO::getUpgradeXp, vipGradeUpdateVO.getUpgradeXp());
            updateWrapper.set(SiteVIPGradePO::getUpgradeBonus, vipGradeUpdateVO.getUpgradeBonus());
            updateWrapper.set(SiteVIPGradePO::getPicIcon, vipGradeUpdateVO.getPicIcon());
            // 更新数据
            this.update(null, updateWrapper);
            SiteVIPGradePO newSiteVIPGradePO = new SiteVIPGradePO();
            BeanUtils.copyProperties(vipGradeUpdateVO, newSiteVIPGradePO);
            // 刷新redis缓存
            refreshVIPGrade(null, siteCode);
            // 记录VIP操作日志
            recordOperation(newSiteVIPGradePO, oldVipGradePO, operator, vipGradeUpdateVO.getVipGradeCode());
        } catch (Exception e) {
            log.error("VIP配置保存发生异常", e);
            return ResponseVO.fail(ResultCode.VIP_RANK_SAVE_ERROR);
        }
        return ResponseVO.success();
    }

    private void recordOperation(SiteVIPGradePO newSiteVIPGradePO, SiteVIPGradePO oldVipGradePO,
                                 String operator, String vipGrade) {
        List<Comparison> list = Lists.newArrayList();
        List<Comparison> compareList = ValidateUtil.compareObj(oldVipGradePO, newSiteVIPGradePO);
        if (ObjectUtils.isNotEmpty(compareList)) {
            list.addAll(compareList);
        }
        List<SiteVIPOperationPO> vipOperationPOS = list.stream().map(obj -> {
            SiteVIPOperationPO po = new SiteVIPOperationPO();
            po.setOperationType(ChangeOperationEnum.VIP_GRADE.getCode());
            ImmutableList<CodeValueVO> paramVOS = ChangeOperationEnum.VIP_GRADE.getList();
            String changeField = paramVOS.stream().filter(vo -> obj.getField()
                    .equals(vo.getCode())).findFirst().orElse(new CodeValueVO()).getCode();
            if (ObjectUtil.isEmpty(changeField)) {
                return null;
            }
            po.setOperationItem(changeField);
            po.setAdjustLevel(vipGrade);
            po.setOperationBefore(null == obj.getBefore() ? null : obj.getBefore().toString());
            po.setOperationAfter(null == obj.getAfter() ? null : obj.getAfter().toString());
            po.setOperationTime(System.currentTimeMillis());
            // 操作人
            po.setOperator(operator);
            return po;
        }).filter(ObjectUtil::isNotEmpty).toList();
        vipOperationService.saveBatch(vipOperationPOS);
    }

    public List<CodeValueNoI18VO> getVipGrade() {
        // 先从redis获取
//        RList<VIPGradePO> list = RedisUtil.getList(RedisConstants.KEY_VIP_GRADE_CONFIG);
        RSet<VIPGradePO> list = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG );
        if (CollUtil.isEmpty(list)) {
            List<VIPGradePO> vipGradePOList = vipGradeRepository.selectList(new LambdaQueryWrapper<VIPGradePO>()
                    .orderByAsc(VIPGradePO::getVipGradeCode));
            list.addAll(vipGradePOList);
        }
        return list.stream().sorted(Comparator.comparing(VIPGradePO::getVipGradeCode)).map(item ->
                CodeValueNoI18VO.builder().code(item.getVipGradeCode().toString())
                        .value(item.getVipGradeName()).build()).toList();
    }

    public SiteVIPGradeVO queryVIPGradeByGrade(String vipGradeCode, String siteCode) {
        try {
            List<SiteVIPGradeVO> voList = queryAllVIPGrade(siteCode);
            Optional<SiteVIPGradeVO> optional = voList.stream()
                    .filter(obj -> String.valueOf(obj.getVipGradeCode()).equals(vipGradeCode)).findFirst();
            return optional.orElse(null);
        } catch (Exception e) {
            log.error("根据VIP等级查询VIP配置异常", e);
            return null;
        }
    }

    public List<SiteVIPGradeVO> queryAllVIPGrade(String siteCode) {
        try {
            List<SiteVIPGradeVO> list = new ArrayList<>();
            if (SiteHandicapModeEnum.China.getCode().equals(siteApi.getSiteInfo(siteCode).getData().getHandicapMode())){
                List<VIPGradeVO> data=siteVipOptionService.getInitVIPGrade();
                list = data.stream().map(e ->{
                    SiteVIPGradeVO vo=new SiteVIPGradeVO();
                    vo.setSiteCode(siteCode);
                    vo.setVipGradeCode(e.getVipGradeCode());
                    vo.setVipGradeName(e.getVipGradeName());
                    return vo;
                }).collect(Collectors.toList());
            }else{
                Set<SiteVIPGradeVO> voList = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG + siteCode);
                if (ObjectUtil.isEmpty(voList)) {
                    voList =refreshVIPGrade(null, siteCode);
                }
                list.addAll(voList);
                list = list.stream().sorted(Comparator.comparing(SiteVIPGradeVO::getVipGradeCode)).toList();
            }
            return list;
        } catch (Exception e) {
            log.error("查询所有VIP配置异常", e);
            return null;
        }
    }
    public Map<Integer,String> queryAllVIPGradeNameMap(String siteCode) {
        try {
            Set<SiteVIPGradeVO> voList = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG + siteCode);
            if (ObjectUtil.isEmpty(voList)) {
                voList =refreshVIPGrade(null, siteCode);
            }
            List<SiteVIPGradeVO> list = new ArrayList<>();
            list.addAll(voList);
            list = list.stream().sorted(Comparator.comparing(SiteVIPGradeVO::getVipGradeCode)).toList();
            Map<Integer,String> vipGradeMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(list)) {
                vipGradeMap = list.stream()
                        .filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName()))
                        .collect(Collectors.toMap(
                                SiteVIPGradeVO::getVipGradeCode,
                                SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
            }
           return vipGradeMap;
        } catch (Exception e) {
            log.error("查询所有VIP配置异常", e);
            return null;
        }
    }

    public List<SiteVIPGradeVO> queryVIPGradeByVIPS(String siteCode, List<Integer> vips) {
        try {
            return queryAllVIPGrade(siteCode).stream().filter(obj -> vips.contains(obj.getVipGradeCode())).toList();
        } catch (Exception e) {
            log.error("查询所有VIP配置异常", e);
            return null;
        }
    }

    @Async
    public Set<SiteVIPGradeVO> refreshVIPGrade(List<SiteVIPGradePO> list, String siteCode) {
        // 刷新redis缓存
        if (ObjectUtils.isEmpty(list)) {
            list = siteVIPGradeRepository.selectList(new LambdaQueryWrapper<SiteVIPGradePO>()
                    .eq(SiteVIPGradePO::getSiteCode, siteCode));
        }
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode);
        List<SiteVIPRankPO> siteVIPRankPOS = vipRankRepository.selectList(query);
        Map<Integer, String> vipRankMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPRankPOS)) {
            vipRankMap = siteVIPRankPOS.stream()
                    .collect(Collectors.toMap(
                            SiteVIPRankPO::getVipRankCode,
                            //存在站点刚创建没有编辑vip段位的情况,没有多语言默认使用默认中文保证不报错
                            siteVIPRankPO -> Optional.ofNullable(siteVIPRankPO.getVipRankNameI18nCode())
                                    .orElse(siteVIPRankPO.getVipRankName())));

        }
        RSet<SiteVIPGradeVO> rSet = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG + siteCode);
//        RList<SiteVIPGradeVO> rList = RedisUtil.getList(RedisConstants.KEY_VIP_GRADE_CONFIG + siteCode);
        //清空一下缓存
        rSet.clear();
        Map<Integer, String> finalVipRankMap = vipRankMap;
        list.forEach(obj -> {
            SiteVIPGradeVO vo = new SiteVIPGradeVO();
            BeanUtils.copyProperties(obj, vo);
            if (finalVipRankMap.containsKey(vo.getVipRankCode())) {
                vo.setVipRankName(finalVipRankMap.get(vo.getVipRankCode()));
            }
            rSet.add(vo);
        });
        return rSet;
    }

    /**
     * 更新vip等级数据
     *
     * @param newVipRankPo 当前段位数据
     */
    public void processGrade(SiteVIPRankPO newVipRankPo) {
        String newVipGradeCodes = newVipRankPo.getVipGradeCodes();
        Integer vipRankCode = newVipRankPo.getVipRankCode();

        //每次编辑，先还原当前段位对应的等级配置
        LambdaUpdateWrapper<SiteVIPGradePO> upd = Wrappers.lambdaUpdate();
        upd.eq(SiteVIPGradePO::getSiteCode, newVipRankPo.getSiteCode())
                .eq(SiteVIPGradePO::getVipRankCode, vipRankCode);
        upd.set(SiteVIPGradePO::getVipRankCode, null);
        this.update(upd);
        if (StringUtils.isNotBlank(newVipGradeCodes)) {
            //当前段位编辑关联了等级数据
            List<String> vipGradeCodes = Arrays.asList(newVipGradeCodes.split(CommonConstant.COMMA));
            if (CollectionUtil.isNotEmpty(vipGradeCodes)) {
                //判断当前vip等级是否被其他段位所使用过了
                String siteCode = newVipRankPo.getSiteCode();
                LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
                query.eq(SiteVIPGradePO::getSiteCode, siteCode)
                        .ne(SiteVIPGradePO::getVipRankCode, vipRankCode)
                        .in(SiteVIPGradePO::getVipGradeCode, vipGradeCodes);
                List<SiteVIPGradePO> siteVIPGradePOS = siteVIPGradeRepository.selectList(query);
                if (CollectionUtil.isNotEmpty(siteVIPGradePOS)) {
                    String msg = siteVIPGradePOS.stream()
                            .map(SiteVIPGradePO::getVipGradeName)
                            .collect(Collectors.joining(CommonConstant.COMMA));
                    throw new BaowangDefaultException("当前vip等级：" + msg + "已被其他段位使用，不能再次添加");
                }

                LambdaQueryWrapper<SiteVIPGradePO> oldQuery = Wrappers.lambdaQuery();
                oldQuery.eq(SiteVIPGradePO::getSiteCode, newVipRankPo.getSiteCode()).in(SiteVIPGradePO::getVipGradeCode, vipGradeCodes);
                List<SiteVIPGradePO> oldSiteVIPGradePOS = siteVIPGradeRepository.selectList(oldQuery);
                if (CollectionUtil.isNotEmpty(oldSiteVIPGradePOS)) {
                    oldSiteVIPGradePOS.forEach(item -> item.setVipRankCode(vipRankCode));
                    this.updateBatchById(oldSiteVIPGradePOS);
                }
            }
        }
    }

    public List<VIPGradeVO> getSystemVipGradeList() {
        List<VIPGradePO> vipGradePOS = vipGradeRepository.selectList(new LambdaQueryWrapper<>());
        return BeanUtil.copyToList(vipGradePOS, VIPGradeVO.class);
    }

    public SiteVIPGradeVO getFirstSiteVipGrade(String siteCode) {
        LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPGradePO::getSiteCode, siteCode).orderByAsc(SiteVIPGradePO::getVipGradeCode).last("limit 0,1");
        SiteVIPGradePO one = this.getOne(query);
        return BeanUtil.copyProperties(one, SiteVIPGradeVO.class);
    }

    public SiteVIPGradeVO getLastSiteVipGrade(String siteCode) {
        LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPGradePO::getSiteCode, siteCode).orderByDesc(SiteVIPGradePO::getVipGradeCode).last("limit 0,1");
        SiteVIPGradePO one = this.getOne(query);
        return BeanUtil.copyProperties(one, SiteVIPGradeVO.class);
    }

    public List<SiteVIPGradeVO> getSiteVipGradeList(String siteCode, String vipRankCode) {
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode).eq(SiteVIPRankPO::getVipRankCode, vipRankCode);
        SiteVIPRankPO one = vipRankRepository.selectOne(query);
        if (one == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String vipGradeCodes = one.getVipGradeCodes();
        List<String> gradeCodeList = Arrays.stream(vipGradeCodes.split(CommonConstant.COMMA)).toList();
        if (StringUtils.isNotBlank(vipGradeCodes)) {
            LambdaQueryWrapper<SiteVIPGradePO> gradeQuery = Wrappers.lambdaQuery();
            gradeQuery.eq(SiteVIPGradePO::getSiteCode, siteCode).in(SiteVIPGradePO::getVipGradeCode, gradeCodeList);
            List<SiteVIPGradePO> list = this.list(gradeQuery);
            return BeanUtil.copyToList(list, SiteVIPGradeVO.class);
        }
        return List.of();
    }

    public List<SiteVIPGradePO> getBetweenAndVipGrade(Integer beforeUpVipGrade, Integer upgradeVIP, String siteCode) {
        LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPGradePO::getSiteCode, siteCode)
                .gt(SiteVIPGradePO::getVipGradeCode, beforeUpVipGrade)
                .le(SiteVIPGradePO::getVipGradeCode, upgradeVIP);
        List<SiteVIPGradePO> list = this.list(query);
        return list;
    }

    public Map<String, List<SiteVIPGradeVO>> getAllSiteVipGrade() {
        List<SiteVIPGradePO> list = this.list();
        Map<String, List<SiteVIPGradeVO>> result = new HashMap<>();
        if (CollectionUtil.isNotEmpty(list)) {
            List<SiteVIPGradeVO> gradeVOS = BeanUtil.copyToList(list, SiteVIPGradeVO.class);
            result = gradeVOS.stream()
                    .collect(Collectors.groupingBy(SiteVIPGradeVO::getSiteCode));
        }
        return result;
    }

    /**
     * 获取前10个VIP等级
     * @return
     */
    public List<CodeValueNoI18VO> getVipGradeTopTen() {
        // 先从redis获取
        RSet<VIPGradePO> list = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG );
        if (CollUtil.isEmpty(list)) {
            List<VIPGradePO> vipGradePOList = vipGradeRepository.selectList(new LambdaQueryWrapper<VIPGradePO>()
                            .le(VIPGradePO::getVipGradeCode,CommonConstant.business_eleven)
                    .orderByAsc(VIPGradePO::getVipGradeCode));
            list.addAll(vipGradePOList);
        }
        return list.stream().sorted(Comparator.comparing(VIPGradePO::getVipGradeCode)).filter(item->item.getVipGradeCode() <= CommonConstant.business_eleven).map(item ->
                CodeValueNoI18VO.builder().code(item.getVipGradeCode().toString())
                        .value(item.getVipGradeName()).build()).toList();
    }

    public void initVIPGrade(String siteCode) {
        this.remove(new LambdaQueryWrapper<SiteVIPGradePO>().eq(SiteVIPGradePO::getSiteCode, siteCode));
           List<VIPGradePO> gradePOList = vipGradeRepository.selectList(new LambdaQueryWrapper<VIPGradePO>()
                .le(VIPGradePO::getVipGradeCode,11).orderByAsc(VIPGradePO::getVipGradeCode));
            List<SiteVIPGradePO> siteVIPGradePOS = Lists.newArrayList();
                        for (VIPGradePO po : gradePOList) {
                siteVIPGradePOS.add(SiteVIPGradePO.builder()
                        .vipGradeCode(po.getVipGradeCode())
                        .vipGradeName(po.getVipGradeName()).siteCode(siteCode)
                        .build()
                );
            }this.saveBatch(siteVIPGradePOS);
    }


}
