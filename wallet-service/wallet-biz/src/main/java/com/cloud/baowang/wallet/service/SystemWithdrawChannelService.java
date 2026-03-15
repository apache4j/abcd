package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.VIPRankVO;
import com.cloud.baowang.wallet.api.vo.recharge.ChannelQueryReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.SiteWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawWayPO;
import com.cloud.baowang.wallet.repositories.SiteWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawWayRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: qiqi
 **/
@Service
@Slf4j
public class SystemWithdrawChannelService extends ServiceImpl<SystemWithdrawChannelRepository, SystemWithdrawChannelPO> {

    @Resource
    private SystemWithdrawWayRepository systemWithdrawWayRepository;

    @Resource
    private SystemWithdrawChannelRepository systemWithdrawChannelRepository;

    @Resource
    private SiteWithdrawChannelRepository siteWithdrawChannelRepository;

    @Resource
    private VipRankApi vipRankApi;

    public ResponseVO<Page<SystemWithdrawChannelResponseVO>> selectPage(SystemWithdrawChannelRequestVO withdrawChannelRequestVO) {
        Page<SystemWithdrawChannelPO> page = new Page<>(withdrawChannelRequestVO.getPageNumber(), withdrawChannelRequestVO.getPageSize());
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.hasText(withdrawChannelRequestVO.getCurrencyCode()), SystemWithdrawChannelPO::getCurrencyCode, withdrawChannelRequestVO.getCurrencyCode());
        lqw.eq(StringUtils.hasText(withdrawChannelRequestVO.getWithdrawWayId()), SystemWithdrawChannelPO::getWithdrawWayId, withdrawChannelRequestVO.getWithdrawWayId());
        lqw.eq(StringUtils.hasText(withdrawChannelRequestVO.getChannelType()), SystemWithdrawChannelPO::getChannelType, withdrawChannelRequestVO.getChannelType());
        lqw.like(StringUtils.hasText(withdrawChannelRequestVO.getChannelName()), SystemWithdrawChannelPO::getChannelName, withdrawChannelRequestVO.getChannelName());
        if (StringUtils.hasText(withdrawChannelRequestVO.getChannelName())) {
            lqw.like(SystemWithdrawChannelPO::getChannelName, withdrawChannelRequestVO.getChannelName());
        }
        lqw.eq(null != withdrawChannelRequestVO.getStatus(), SystemWithdrawChannelPO::getStatus, withdrawChannelRequestVO.getStatus());
        lqw.orderByDesc(SystemWithdrawChannelPO::getStatus).orderByDesc(SystemWithdrawChannelPO::getUpdatedTime);
        IPage<SystemWithdrawChannelPO> withdrawChannelPOPage = this.baseMapper.selectPage(page, lqw);

        Page<SystemWithdrawChannelResponseVO> withdrawChannelResponseVOPage = new Page<>();
        withdrawChannelResponseVOPage.setTotal(withdrawChannelPOPage.getTotal());
        withdrawChannelResponseVOPage.setPages(withdrawChannelPOPage.getPages());
        List<SystemWithdrawChannelResponseVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(withdrawChannelPOPage.getRecords())) {
            List<VIPRankVO> vipRankList = vipRankApi.getVipRankList();
            Map<Integer, String> vipRankMap = vipRankList.stream()
                    .collect(Collectors.toMap(VIPRankVO::getVipRankCode, VIPRankVO::getVipRankNameI18nCode));

            List<String> withdrawWayIds = withdrawChannelPOPage.getRecords().stream().map(o -> o.getWithdrawWayId()).collect(Collectors.toUnmodifiableList());
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemWithdrawWayPO::getId, withdrawWayIds);
            List<SystemWithdrawWayPO> systemWithdrawWayPOS = systemWithdrawWayRepository.selectList(lambdaQueryWrapper);
            for (SystemWithdrawChannelPO systemWithdrawChannelPO : withdrawChannelPOPage.getRecords()) {

                SystemWithdrawChannelResponseVO systemWithdrawChannelRespVO = new SystemWithdrawChannelResponseVO();
                BeanUtils.copyProperties(systemWithdrawChannelPO, systemWithdrawChannelRespVO);

                String useScope = systemWithdrawChannelRespVO.getUseScope();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(useScope)) {
                    List<String> scope = Arrays.asList(useScope.split(CommonConstant.COMMA));
                    String result = scope.stream()
                            .map(Integer::valueOf) // 将字符串转换为 Integer
                            .filter(vipRankMap::containsKey) // 过滤出存在于 map 中的键
                            .map(vipRankMap::get) // 获取对应的名称
                            .map(I18nMessageUtil::getI18NMessageInAdvice) // 获取国际化名称
                            .collect(Collectors.joining(",")); // 用逗号拼接
                    systemWithdrawChannelRespVO.setUseScopeText(result);
                }

                Optional<SystemWithdrawWayPO> systemWithdrawWayPOOptional = systemWithdrawWayPOS.stream().filter(o -> o.getId().equals(systemWithdrawChannelPO.getWithdrawWayId())).findFirst();
                systemWithdrawWayPOOptional.ifPresent(systemWithdrawWayPO -> systemWithdrawChannelRespVO.setWithdrawWayI18(systemWithdrawWayPO.getWithdrawWayI18()));
                resultLists.add(systemWithdrawChannelRespVO);
            }
            withdrawChannelResponseVOPage.setRecords(resultLists);
        }
        return ResponseVO.success(withdrawChannelResponseVOPage);
    }

    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBySort(SystemWithdrawChannelRequestVO withdrawChannelRequestVO) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getCurrencyCode, withdrawChannelRequestVO.getCurrencyCode());
        if (StringUtils.hasText(withdrawChannelRequestVO.getWithdrawWayId())) {
            lqw.eq(SystemWithdrawChannelPO::getWithdrawWayId, withdrawChannelRequestVO.getWithdrawWayId());
        }
        lqw.orderByAsc(SystemWithdrawChannelPO::getSortOrder);
        List<SystemWithdrawChannelPO> withdrawChannelPOS = this.baseMapper.selectList(lqw);
        List<SystemWithdrawChannelResponseVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(withdrawChannelPOS)) {
            List<String> withdrawWayIds = withdrawChannelPOS.stream().map(o -> o.getWithdrawWayId()).toList();
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemWithdrawWayPO::getId, withdrawWayIds);
            List<SystemWithdrawWayPO> systemWithdrawWayPOS = systemWithdrawWayRepository.selectList(lambdaQueryWrapper);
            for (SystemWithdrawChannelPO systemWithdrawChannelPO : withdrawChannelPOS) {
                SystemWithdrawChannelResponseVO systemWithdrawChannelRespVO = new SystemWithdrawChannelResponseVO();
                BeanUtils.copyProperties(systemWithdrawChannelPO, systemWithdrawChannelRespVO);
                Optional<SystemWithdrawWayPO> systemWithdrawWayPOOptional = systemWithdrawWayPOS.stream().filter(o -> o.getId().equals(systemWithdrawChannelPO.getWithdrawWayId())).findFirst();
                systemWithdrawWayPOOptional.ifPresent(systemWithdrawWayPO -> systemWithdrawChannelRespVO.setWithdrawWayI18(systemWithdrawWayPO.getWithdrawWayI18()));
                resultLists.add(systemWithdrawChannelRespVO);
            }
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<Void> insert(SystemWithdrawChannelAddVO withdrawChannelAddVO) {
        if (!checkChannelCodeUnique(withdrawChannelAddVO.getChannelCode(), withdrawChannelAddVO.getWithdrawWayId(),
                withdrawChannelAddVO.getMerNo(),withdrawChannelAddVO.getChannelName(),null)) {
            log.info("insert CHANNEL_CODE_IS_EXIST:{}", withdrawChannelAddVO.getChannelCode());
            throw new BaowangDefaultException(ResultCode.CHANNEL_CODE_IS_EXIST);
        }
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getCurrencyCode, withdrawChannelAddVO.getCurrencyCode());
        lqw.eq(SystemWithdrawChannelPO::getChannelCode, withdrawChannelAddVO.getChannelCode());
        SystemWithdrawChannelPO systemWithdrawChannelOld = this.baseMapper.selectOne(lqw);
        if (systemWithdrawChannelOld == null) {
            SystemWithdrawChannelPO withdrawChannelPO = new SystemWithdrawChannelPO();
            BeanUtils.copyProperties(withdrawChannelAddVO, withdrawChannelPO);
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(SystemWithdrawWayPO::getId, withdrawChannelAddVO.getWithdrawWayId());
            SystemWithdrawWayPO systemWithdrawWayPO = systemWithdrawWayRepository.selectOne(lambdaQueryWrapper);
            if (systemWithdrawWayPO == null) {
                log.info("WITHDRAW_WAY_IS_NOT_EXIST:{}", withdrawChannelAddVO.getWithdrawWayId());
                throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_IS_NOT_EXIST);
            }
            //String idNo = genIdNo(systemWithdrawWayPO.getWithdrawTypeCode());
            String idNo = OrderUtil.createNumber(12);
            withdrawChannelPO.setWithdrawChannelNo(idNo);
            withdrawChannelPO.setWithdrawTypeId(systemWithdrawWayPO.getWithdrawTypeId());
            withdrawChannelPO.setWithdrawTypeCode(systemWithdrawWayPO.getWithdrawTypeCode());
            withdrawChannelPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            withdrawChannelPO.setCreator(withdrawChannelAddVO.getOperatorUserNo());
            withdrawChannelPO.setUpdater(withdrawChannelAddVO.getOperatorUserNo());
            withdrawChannelPO.setCreatedTime(System.currentTimeMillis());
            withdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.insert(withdrawChannelPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
//        return ResponseVO.success();
    }

    private String genIdNo(String withdrawTypeCode) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<SystemWithdrawChannelPO>();
        lqw.eq(SystemWithdrawChannelPO::getWithdrawTypeCode, withdrawTypeCode);
        lqw.orderByDesc(SystemWithdrawChannelPO::getCreatedTime);
        lqw.last("limit 0,1 for update");
        SystemWithdrawChannelPO systemWithdrawChannelPO = this.baseMapper.selectOne(lqw);
        String lastId = withdrawTypeCode + "000";
        if (systemWithdrawChannelPO != null && StringUtils.hasText(systemWithdrawChannelPO.getWithdrawChannelNo())) {
            lastId = systemWithdrawChannelPO.getWithdrawChannelNo();
        }
        String lastIdStr = lastId.replace(withdrawTypeCode, "");
        Integer newId = Integer.valueOf(lastIdStr).intValue() + 1;
        return withdrawTypeCode.concat(String.format("%03d", newId));
    }

    /**
     * 提现通道代码检查
     *
     * @param channelCode
     * @param id
     * @return
     */
    private boolean checkChannelCodeUnique(String channelCode,String withdrawWayId,String merNo,String channelName, String id) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getChannelCode, channelCode);
        lqw.eq(SystemWithdrawChannelPO::getChannelName, channelName);
        lqw.eq(SystemWithdrawChannelPO::getWithdrawWayId,withdrawWayId);
        lqw.eq(SystemWithdrawChannelPO::getMerNo,merNo);
        lqw.ne(null != id, SystemWithdrawChannelPO::getId, id);
        List<SystemWithdrawChannelPO> withdrawChannelPOS = this.baseMapper.selectList(lqw);
        return withdrawChannelPOS.isEmpty();
    }

    public ResponseVO<Void> updateByInfo(SystemWithdrawChannelUpdateVO withdrawChannelUpdateVO) {
        if (!checkChannelCodeUnique(withdrawChannelUpdateVO.getChannelCode(), withdrawChannelUpdateVO.getWithdrawWayId(),
                withdrawChannelUpdateVO.getMerNo(),withdrawChannelUpdateVO.getChannelName(),
                withdrawChannelUpdateVO.getId())) {
            log.info("update CHANNEL_CODE_IS_EXIST:{},id:{}", withdrawChannelUpdateVO.getChannelCode(), withdrawChannelUpdateVO.getId());
            throw new BaowangDefaultException(ResultCode.CHANNEL_CODE_IS_EXIST);
        }
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getId, withdrawChannelUpdateVO.getId());
        SystemWithdrawChannelPO withdrawChannelPO = this.baseMapper.selectOne(lqw);
        if (withdrawChannelPO != null) {
            BeanUtils.copyProperties(withdrawChannelUpdateVO, withdrawChannelPO);
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(SystemWithdrawWayPO::getId, withdrawChannelUpdateVO.getWithdrawWayId());
            SystemWithdrawWayPO systemWithdrawWayPO = systemWithdrawWayRepository.selectOne(lambdaQueryWrapper);
            if (systemWithdrawWayPO == null) {
                log.info("update WITHDRAW_WAY_IS_NOT_EXIST:{}", withdrawChannelUpdateVO.getWithdrawWayId());
                throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_IS_NOT_EXIST);
            }
            withdrawChannelPO.setWithdrawTypeId(systemWithdrawWayPO.getWithdrawTypeId());
            withdrawChannelPO.setWithdrawTypeCode(systemWithdrawWayPO.getWithdrawTypeCode());
            withdrawChannelPO.setUpdater(withdrawChannelUpdateVO.getOperatorUserNo());
            withdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.updateById(withdrawChannelPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<Void> enableOrDisable(SystemWithdrawChannelStatusVO withdrawChannelStatusVO) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getId, withdrawChannelStatusVO.getId());
        SystemWithdrawChannelPO withdrawChannelPO = this.baseMapper.selectOne(lqw);
        if (withdrawChannelPO != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), withdrawChannelPO.getStatus())) {
                withdrawChannelPO.setStatus(EnableStatusEnum.DISABLE.getCode());
                LambdaUpdateWrapper<SiteWithdrawChannelPO> siteLqw = new LambdaUpdateWrapper<>();
                siteLqw.eq(SiteWithdrawChannelPO::getChannelId,withdrawChannelStatusVO.getId());
                siteLqw.set(SiteWithdrawChannelPO::getStatus,EnableStatusEnum.DISABLE.getCode());
                siteWithdrawChannelRepository.update(null,siteLqw);
            } else {
                withdrawChannelPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            withdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            withdrawChannelPO.setUpdater(withdrawChannelStatusVO.getOperatorUserNo());
            this.baseMapper.updateById(withdrawChannelPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    /**
     * 增加授权数量
     *
     * @param channelIds
     */
    public void addAuthNum(List<String> channelIds) {
        UpdateWrapper<SystemWithdrawChannelPO> updateWrapper = new UpdateWrapper<SystemWithdrawChannelPO>();
        updateWrapper.setSql(" auth_num = auth_num + 1");
        updateWrapper.in("id", channelIds);
        this.update(updateWrapper);
    }

    /**
     * 减少授权数量
     *
     * @param channelIds
     */
    public void subAuthNum(List<String> channelIds) {
        UpdateWrapper<SystemWithdrawChannelPO> updateWrapper = new UpdateWrapper<SystemWithdrawChannelPO>();
        updateWrapper.setSql(" auth_num = auth_num - 1");
        updateWrapper.in("id", channelIds);
        this.update(updateWrapper);
    }


    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SystemWithdrawChannelPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SystemWithdrawChannelPO systemWithdrawChannelPO = new SystemWithdrawChannelPO();
            systemWithdrawChannelPO.setId(sortNewReqVO.getId());
            systemWithdrawChannelPO.setSortOrder(sortNewReqVO.getSortOrder());
            //通道排序的目的就是为了保证权重 可视化编辑
            systemWithdrawChannelPO.setWeight(sortNewReqVO.getSortOrder());
            systemWithdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            systemWithdrawChannelPO.setUpdater(userAccount);
            batchLists.add(systemWithdrawChannelPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public SystemWithdrawChannelResponseVO getChannelInfo(ChannelQueryReqVO reqVO) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelName()), SystemWithdrawChannelPO::getChannelName, reqVO.getChannelName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelCode()), SystemWithdrawChannelPO::getChannelCode, reqVO.getChannelCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getCurrencyCode()), SystemWithdrawChannelPO::getCurrencyCode, reqVO.getCurrencyCode());
        queryWrapper.last("limit 1");
        SystemWithdrawChannelPO po = systemWithdrawChannelRepository.selectOne(queryWrapper);
        return ConvertUtil.entityToModel(po, SystemWithdrawChannelResponseVO.class);
    }

    public SystemWithdrawChannelResponseVO getChannelInfoByMerNo(String channelName, String merchantNo) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemWithdrawChannelPO::getMerNo, merchantNo);
        queryWrapper.eq(SystemWithdrawChannelPO::getChannelName, channelName);
        queryWrapper.last("limit 1");
        SystemWithdrawChannelPO po = systemWithdrawChannelRepository.selectOne(queryWrapper);
        return ConvertUtil.entityToModel(po, SystemWithdrawChannelResponseVO.class);
    }

    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectAll() {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(SystemWithdrawChannelPO::getSortOrder);
        List<SystemWithdrawChannelPO> withdrawChannelPOS = this.baseMapper.selectList(lqw);
        List<SystemWithdrawChannelResponseVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(withdrawChannelPOS)) {
            List<String> withdrawWayIds = withdrawChannelPOS.stream().map(o -> o.getWithdrawWayId()).toList();
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemWithdrawWayPO::getId, withdrawWayIds);
            List<SystemWithdrawWayPO> systemWithdrawWayPOS = systemWithdrawWayRepository.selectList(lambdaQueryWrapper);
            for (SystemWithdrawChannelPO systemWithdrawChannelPO : withdrawChannelPOS) {
                SystemWithdrawChannelResponseVO systemWithdrawChannelRespVO = new SystemWithdrawChannelResponseVO();
                BeanUtils.copyProperties(systemWithdrawChannelPO, systemWithdrawChannelRespVO);
                Optional<SystemWithdrawWayPO> systemWithdrawWayPOOptional = systemWithdrawWayPOS.stream().filter(o -> o.getId().equals(systemWithdrawChannelPO.getWithdrawWayId())).findFirst();
                systemWithdrawWayPOOptional.ifPresent(systemWithdrawWayPO -> systemWithdrawChannelRespVO.setWithdrawWayI18(systemWithdrawWayPO.getWithdrawWayI18()));
                resultLists.add(systemWithdrawChannelRespVO);
            }
        }
        return ResponseVO.success(resultLists);
    }

    public List<SystemWithdrawChannelResponseVO> selectByChannelIds(List<String> withdrawChannelIds) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(SystemWithdrawChannelPO::getId, withdrawChannelIds);
        List<SystemWithdrawChannelPO> withdrawChannelPOS = this.baseMapper.selectList(lqw);
        List<SystemWithdrawChannelResponseVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(withdrawChannelPOS)) {
            List<String> withdrawWayIds = withdrawChannelPOS.stream().map(o -> o.getWithdrawWayId()).toList();
            LambdaQueryWrapper<SystemWithdrawWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemWithdrawWayPO::getId, withdrawWayIds);
            List<SystemWithdrawWayPO> systemWithdrawWayPOS = systemWithdrawWayRepository.selectList(lambdaQueryWrapper);
            for (SystemWithdrawChannelPO systemWithdrawChannelPO : withdrawChannelPOS) {
                SystemWithdrawChannelResponseVO systemWithdrawChannelRespVO = new SystemWithdrawChannelResponseVO();
                BeanUtils.copyProperties(systemWithdrawChannelPO, systemWithdrawChannelRespVO);
                Optional<SystemWithdrawWayPO> systemWithdrawWayPOOptional = systemWithdrawWayPOS.stream().filter(o -> o.getId().equals(systemWithdrawChannelPO.getWithdrawWayId())).findFirst();
                systemWithdrawWayPOOptional.ifPresent(systemWithdrawWayPO -> systemWithdrawChannelRespVO.setWithdrawWayI18(systemWithdrawWayPO.getWithdrawWayI18()));
                resultLists.add(systemWithdrawChannelRespVO);
            }
        }
        return resultLists;
    }

    public List<SiteWithdrawChannelVO> getListByWayId(String wayId,
                                                      String siteCode) {

        ArrayList<String> wayIdArr = new ArrayList<>();
        wayIdArr.add(wayId);
        return siteWithdrawChannelRepository.selectSiteWithdrawChannelList(siteCode, wayIdArr, EnableStatusEnum.ENABLE.getCode());
    }

    public List<SystemWithdrawChannelResponseVO> getChannelByIdAndChannelType(String channelType, String currencyCode, List<String> systemChannelIds) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> systemQuery = Wrappers.lambdaQuery();
        systemQuery.eq(SystemWithdrawChannelPO::getChannelType, channelType)
                .eq(SystemWithdrawChannelPO::getCurrencyCode, currencyCode)
                .in(SystemWithdrawChannelPO::getId, systemChannelIds)
                .eq(SystemWithdrawChannelPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .orderByAsc(SystemWithdrawChannelPO::getSortOrder);
        //筛选当前站点的系统通道配置表
        List<SystemWithdrawChannelPO> systemWithdrawChannelPOS = systemWithdrawChannelRepository.selectList(systemQuery);
        return BeanUtil.copyToList(systemWithdrawChannelPOS,SystemWithdrawChannelResponseVO.class);
    }

    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBankAll(String currencyCode) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawChannelPO::getCurrencyCode, currencyCode);
        lqw.eq(SystemWithdrawChannelPO::getWithdrawTypeCode, WithdrawTypeEnum.BANK_CARD.getCode());
        lqw.orderByAsc(SystemWithdrawChannelPO::getSortOrder);
        List<SystemWithdrawChannelPO> withdrawChannelPOS = this.baseMapper.selectList(lqw);
        List<SystemWithdrawChannelResponseVO> resultLists = new ArrayList<>();
        try {
            resultLists = ConvertUtil.convertListToList(withdrawChannelPOS,new SystemWithdrawChannelResponseVO());
        } catch (Exception e) {
           return ResponseVO.success(resultLists);
        }
        List<SystemWithdrawChannelResponseVO> distinctList = resultLists.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(vo -> vo.getChannelCode() + "#" + vo.getChannelName()))),
                        ArrayList::new));

        return ResponseVO.success(distinctList);
    }

}
