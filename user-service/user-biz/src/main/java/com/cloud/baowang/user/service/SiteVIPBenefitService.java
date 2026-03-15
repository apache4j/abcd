package com.cloud.baowang.user.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.common.core.utils.tool.vo.Comparison;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPBenefitOperationVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPBenefitRequestVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPBenefitVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPOperatorReqVO;
import com.cloud.baowang.user.api.vo.vip.VIPRebateVO;
import com.cloud.baowang.user.enums.ChangeOperationEnum;
import com.cloud.baowang.user.po.SiteVIPBenefitPO;
import com.cloud.baowang.user.po.SiteVIPOperationPO;
import com.cloud.baowang.user.repositories.VIPBenefitConfigRepository;
import com.cloud.baowang.user.api.vo.vip.VIPRankCodeQueryVO;
import com.cloud.baowang.user.repositories.VIPOperationRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 4/5/23 2:15 PM
 * @Version 1.0
 */
@Service
@Slf4j
@Transactional
public class SiteVIPBenefitService extends ServiceImpl<VIPBenefitConfigRepository, SiteVIPBenefitPO> {

    @Autowired
    private VIPBenefitConfigRepository vipBenefitConfigRepository;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private VIPOperationRepository vipOperationRepository;
    @Autowired
    private VIPOperationService vipOperationService;

//    private WalletServiceFeignResource walletServiceFeignResource;

    public ResponseVO<?> saveVIPBenefit(final SiteVIPBenefitRequestVO requestVO) {
        try {
            SiteVIPBenefitPO oldBenefitPO = this.getOne(
                    new LambdaQueryWrapper<SiteVIPBenefitPO>().eq(SiteVIPBenefitPO::getSiteCode,
                            requestVO.getSiteCode()));
            LambdaUpdateWrapper<SiteVIPBenefitPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteVIPBenefitPO::getSiteCode, requestVO.getSiteCode());
            updateWrapper.eq(SiteVIPBenefitPO::getVipGradeCode, requestVO.getVipGradeCode());
//            updateWrapper.set(SiteVIPBenefitPO::getDailyWithdrawals, requestVO.getDailyWithdrawals());
//            updateWrapper.set(SiteVIPBenefitPO::getDayWithdrawLimit, requestVO.getDayWithdrawLimit());
            updateWrapper.set(SiteVIPBenefitPO::getUpgrade, requestVO.getUpgrade());
            updateWrapper.set(SiteVIPBenefitPO::getLuckTime, requestVO.getLuckTime());
//            updateWrapper.set(SiteVIPBenefitPO::getWithdrawFee, requestVO.getWithdrawFee());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekFlag(),
                    SiteVIPBenefitPO::getWeekRebate, requestVO.getWeekRebate());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekFlag(),
                    SiteVIPBenefitPO::getWeekMinBetAmount, requestVO.getWeekMinBetAmount());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekFlag(),
                    SiteVIPBenefitPO::getWeekBetMultiple, requestVO.getWeekBetMultiple());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getMonthFlag(),
                    SiteVIPBenefitPO::getMonthRebate, requestVO.getMonthRebate());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getMonthFlag(),
                    SiteVIPBenefitPO::getMonthMinBetAmount, requestVO.getMonthMinBetAmount());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getMonthFlag(),
                    SiteVIPBenefitPO::getMonthBetMultiple, requestVO.getMonthBetMultiple());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekSportFlag(),
                    SiteVIPBenefitPO::getWeekSportRebate, requestVO.getWeekSportRebate());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekSportFlag(),
                    SiteVIPBenefitPO::getWeekSportMinBet, requestVO.getWeekSportMinBet());
            updateWrapper.set(BigDecimal.ONE.intValue() == requestVO.getWeekSportFlag(),
                    SiteVIPBenefitPO::getWeekSportMultiple, requestVO.getWeekSportMultiple());
            // 前端传递数据为此次待生效数据
            this.update(null, updateWrapper);
            SiteVIPBenefitPO newBenefitPO = new SiteVIPBenefitPO();
            BeanUtils.copyProperties(requestVO, newBenefitPO);
            // 记录VIP操作日志
            recordOperation(newBenefitPO, oldBenefitPO, requestVO.getOperator(),
                    requestVO.getVipGradeCode().toString(), requestVO.getSiteCode());
            // 更新redis缓存
            refreshVIPBenefit(null, requestVO.getSiteCode());
        } catch (Exception e) {
            log.error("siteCode:{} VIP权益配置保存异常", requestVO.getSiteCode(), e);
            return ResponseVO.fail(ResultCode.VIP_BENEFIT_SAVE_ERROR);
        }
        return ResponseVO.success();
    }

    private void recordOperation(final SiteVIPBenefitPO newBenefitPO, final SiteVIPBenefitPO oldBenefitPO,
                                 final String operator, final String vipGrade, String siteCode) {
        List<Comparison> list = Lists.newArrayList();

        if (oldBenefitPO.getVipGradeCode().equals(newBenefitPO.getVipGradeCode())) {
            List<Comparison> compareList = ValidateUtil.compareObj(oldBenefitPO, newBenefitPO);
            if (ObjectUtils.isNotEmpty(compareList)) {
                list.addAll(compareList);
            }
        }
        List<SiteVIPOperationPO> vipOperationPOS = list.stream().map(obj -> {
            SiteVIPOperationPO po = new SiteVIPOperationPO();
            po.setOperationType(ChangeOperationEnum.VIP_BENEFIT.getCode());
            ImmutableList<CodeValueVO> paramVOS = ChangeOperationEnum.VIP_BENEFIT.getList();
            String changeField = paramVOS.stream().filter(vo -> obj.getField()
                    .equals(vo.getCode())).findFirst().orElse(new CodeValueVO()).getCode();
            if (ObjectUtil.isEmpty(changeField)) {
                return null;
            }
            po.setAdjustLevel(vipGrade);
            po.setSiteCode(siteCode);
            po.setOperationItem(changeField);
            po.setOperationBefore(null == obj.getBefore() ? null : obj.getBefore().toString());
            po.setOperationAfter(null == obj.getAfter() ? null : obj.getAfter().toString());
            po.setOperationTime(System.currentTimeMillis());
            // 操作人
            po.setOperator(operator);
            return po;
        }).filter(ObjectUtil::isNotEmpty).toList();
        vipOperationService.saveBatch(vipOperationPOS);
    }

    public ResponseVO<Page<SiteVIPBenefitVO>> queryVipBenefitConfig(PageVO pageVO) {
        try {
            Page<SiteVIPBenefitPO> page = new Page<>(pageVO.getPageNumber(), pageVO.getPageSize());
            Page<SiteVIPBenefitPO> resultPage = vipBenefitConfigRepository.selectPage(page, new LambdaQueryWrapper<>());
            Page<SiteVIPBenefitVO> result = new Page<>();
            List<SiteVIPBenefitVO> voList = Lists.newArrayList();
            if(null != resultPage && ObjectUtils.isNotEmpty(resultPage.getRecords())){
                resultPage.getRecords().forEach(obj->{
                    SiteVIPBenefitVO vo = new SiteVIPBenefitVO();
                    BeanUtils.copyProperties(obj, vo);
                    voList.add(vo);
                });
                BeanUtils.copyProperties(resultPage, result);
            }
            result.setRecords(voList);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询VIP权益配置异常", e);
            return ResponseVO.fail(ResultCode.VIP_BENEFIT_QUERY_ERROR);
        }
    }


    public List<SiteVIPBenefitVO> queryVIPBenefitForUse(String siteCode) {
        try {
            List<SiteVIPBenefitVO> voList = redissonClient.getList(RedisConstants.KEY_VIP_BENEFIT_CONFIG + siteCode);
            if(null == voList || voList.size() == 0){
                LambdaQueryWrapper<SiteVIPBenefitPO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SiteVIPBenefitPO::getSiteCode, siteCode);
                List<SiteVIPBenefitPO> siteVIPBenefitPOS = vipBenefitConfigRepository.selectList(queryWrapper);
                voList = ConvertUtil.convertListToList(siteVIPBenefitPOS, new SiteVIPBenefitVO());
                refreshVIPBenefit(siteVIPBenefitPOS, siteCode);
            }
            return voList;
        } catch (Exception e) {
           log.error("查询VIP权益信息发生异常", e);
           return null;
        }
    }

    public ResponseVO<SiteVIPBenefitVO> queryBenefitByAccount(final VIPRankCodeQueryVO requestVO) {
        try {
            SiteVIPBenefitVO vo = new SiteVIPBenefitVO();
            LambdaQueryWrapper<SiteVIPBenefitPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteVIPBenefitPO::getVipGradeCode, requestVO.getVipGradeCode());
            queryWrapper.eq(SiteVIPBenefitPO::getSiteCode, requestVO.getSiteCode());
            SiteVIPBenefitPO po = vipBenefitConfigRepository.selectOne(queryWrapper);
            BeanUtils.copyProperties(po, vo);
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("根据 siteCode:{}, VIPCode:{} 查询具体权益信息发生异常", requestVO.getSiteCode(),
                    requestVO.getVipGradeCode(), e);
            return ResponseVO.fail(ResultCode.VIP_BENEFIT_QUERY_ERROR);
        }
    }

    public List<VIPRebateVO> queryRebateConfig(Integer flag) {
        return null;
    }

    @Async
    public void refreshVIPBenefit(List<SiteVIPBenefitPO> list, String siteCode){
        // 刷新redis缓存
        if(ObjectUtils.isEmpty(list)){
            list = vipBenefitConfigRepository.selectList(new LambdaQueryWrapper<SiteVIPBenefitPO>()
                    .eq(SiteVIPBenefitPO::getSiteCode, siteCode));
        }
        RList<SiteVIPBenefitVO> rList = redissonClient.getList(RedisConstants.KEY_VIP_BENEFIT_CONFIG + siteCode);
        list.forEach(obj-> {
            SiteVIPBenefitVO vo = new SiteVIPBenefitVO();
            BeanUtils.copyProperties(obj, vo);
            rList.add(vo);
        });
    }

    public ResponseVO<Page<SiteVIPBenefitOperationVO>> queryVIPBenefitOperation(SiteVIPOperatorReqVO reqVO) {
        Page<SiteVIPOperationPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        LambdaQueryWrapper<SiteVIPOperationPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getOperatorType()),
                SiteVIPOperationPO::getOperationType,reqVO.getOperatorType());
        queryWrapper.in(ObjectUtils.isNotEmpty(reqVO.getOperatorItem()),
                SiteVIPOperationPO::getOperationItem,reqVO.getOperatorItem());
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getOperator()),
                SiteVIPOperationPO::getOperator,reqVO.getOperator());
        if(null != reqVO.getBeginTime() && null != reqVO.getEndTime()){
            queryWrapper.ge(SiteVIPOperationPO::getOperationTime, reqVO.getBeginTime());
            queryWrapper.le(SiteVIPOperationPO::getOperationTime, reqVO.getEndTime());
        }
        Page<SiteVIPOperationPO> resultPage = vipOperationRepository.selectPage(page, queryWrapper);
        Page<SiteVIPBenefitOperationVO> voPage = new Page<>();
        List<SiteVIPBenefitOperationVO> voList = Lists.newArrayList();
        if(null != resultPage && ObjectUtils.isNotEmpty(resultPage.getRecords())){
            resultPage.getRecords().forEach(obj->{
                SiteVIPBenefitOperationVO vo = new SiteVIPBenefitOperationVO();
                BeanUtils.copyProperties(obj, vo);
                vo.setChangeBefore(obj.getOperationBefore());
                vo.setChangeAfter(obj.getOperationAfter());
                voList.add(vo);
            });
            BeanUtils.copyProperties(resultPage, voPage);
            voPage.setRecords(voList);
        }
        return ResponseVO.success(voPage);
    }

    public ResponseVO<Long> getTotalCount(SiteVIPOperatorReqVO reqVO) {
        LambdaQueryWrapper<SiteVIPOperationPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getOperatorType()),
                SiteVIPOperationPO::getOperationType,reqVO.getOperatorType());
        queryWrapper.in(ObjectUtils.isNotEmpty(reqVO.getOperatorItem()),
                SiteVIPOperationPO::getOperationItem,reqVO.getOperatorItem());
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getOperator()),
                SiteVIPOperationPO::getOperator,reqVO.getOperator());
        if(null != reqVO.getBeginTime() && null != reqVO.getEndTime()){
            queryWrapper.ge(SiteVIPOperationPO::getOperationTime, reqVO.getBeginTime());
            queryWrapper.le(SiteVIPOperationPO::getOperationTime, reqVO.getEndTime());
        }
        return ResponseVO.success(vipOperationRepository.selectCount(queryWrapper));
    }
}
