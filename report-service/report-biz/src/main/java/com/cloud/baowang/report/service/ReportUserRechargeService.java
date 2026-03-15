package com.cloud.baowang.report.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserWinLossParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.vo.UserRechargeWithdrawMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeUserRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.*;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawMqMessagePO;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawPO;
import com.cloud.baowang.report.repositories.ReportUserRechargeMqMessageRepository;
import com.cloud.baowang.report.repositories.ReportUserRechargeWithdrawRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawalVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawAllRecordVO;
import com.cloud.baowang.wallet.api.vo.report.user.UserInfoStatementVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportUserRechargeService extends ServiceImpl<ReportUserRechargeWithdrawRepository, ReportUserRechargeWithdrawPO> {

    private final ReportUserRechargeMqMessageRepository userRechargeMqMessageRepository;
    private final ReportUserRechargeWithdrawRepository reportUserRechargeWithdrawRepository;

    private final SiteApi siteApi;

    private final UserDepositWithdrawApi userDepositWithdrawApi;



    /**
     * 按照 日期(天)查询充值累计金额
     * @param vo
     * @return
     */
    public ResponseVO<Page<ReportUserRechargeResponseVO>> queryRechargeAmount(ReportUserRechargeRequestVO vo){
        Page<ReportUserRechargeResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<ReportUserRechargeResponseVO> reportUserRechargeResponseVO = baseMapper.queryRechargeAmount(page,vo);
        return ResponseVO.success(reportUserRechargeResponseVO);
    }

    /**
     * 按照 会员ID查询充值累计金额
     * @return
     */
    public ResponseVO<ReportUserRechargeResponseVO> queryRechargeAmountByUserId(ReportUserRechargeUserRequestVO vo){
        ReportUserRechargeResponseVO reportUserRechargeResponseVO = baseMapper.queryRechargeAmountByUserId(vo);
        return ResponseVO.success(reportUserRechargeResponseVO);
    }

    @DistributedLock(name = RedisConstants.REWARD_SPIN_WHEEL_LOCK, unique = "#vo.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public void addRechargeAmount(long start, String jsonStr, UserRechargeWithdrawMqVO vo){
        ReportUserRechargeWithdrawMqMessagePO reportUserRechargeWithdrawMqMessagePO = new ReportUserRechargeWithdrawMqMessagePO();
        reportUserRechargeWithdrawMqMessagePO.setJsonStr(jsonStr);
        userRechargeMqMessageRepository.insert(reportUserRechargeWithdrawMqMessagePO);

        LambdaQueryWrapper<ReportUserRechargeWithdrawPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportUserRechargeWithdrawPO::getUserId,vo.getUserId());
        lqw.eq(ReportUserRechargeWithdrawPO::getDayHourMillis,vo.getDayHourMillis());
        lqw.eq(ReportUserRechargeWithdrawPO::getType,vo.getType());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentId()),ReportUserRechargeWithdrawPO::getAgentId,vo.getAgentId());
        lqw.eq(StringUtils.isNotBlank(vo.getDepositWithdrawWayId()),ReportUserRechargeWithdrawPO::getDepositWithdrawWayId,vo.getDepositWithdrawWayId());
        ReportUserRechargeWithdrawPO reportUserRechargeWithdrawPO = baseMapper.selectOne(lqw);

        if(null == reportUserRechargeWithdrawPO){
            reportUserRechargeWithdrawPO = ConvertUtil.entityToModel(vo, ReportUserRechargeWithdrawPO.class);
            reportUserRechargeWithdrawPO.setNums(CommonConstant.business_one);
            if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                reportUserRechargeWithdrawPO.setLargeNums(CommonConstant.business_one);
            }
            if(null != vo.getDepositSubordinatesAmount()&& BigDecimal.ZERO.compareTo(vo.getDepositSubordinatesAmount()) < 0){
                reportUserRechargeWithdrawPO.setDepositSubordinatesNums(CommonConstant.business_one);
            }
            baseMapper.insert(reportUserRechargeWithdrawPO);
        }else{
            reportUserRechargeWithdrawPO.setAmount(reportUserRechargeWithdrawPO.getAmount().add(vo.getAmount()));
            reportUserRechargeWithdrawPO.setFeeAmount(reportUserRechargeWithdrawPO.getFeeAmount().add(vo.getFeeAmount()));
            reportUserRechargeWithdrawPO.setWayFeeAmount(reportUserRechargeWithdrawPO.getWayFeeAmount().add(vo.getWayFeeAmount()));
            reportUserRechargeWithdrawPO.setSettlementFeeAmount(reportUserRechargeWithdrawPO.getSettlementFeeAmount().add(vo.getSettlementFeeAmount()));
            reportUserRechargeWithdrawPO.setNums(reportUserRechargeWithdrawPO.getNums()+1);
            if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                reportUserRechargeWithdrawPO.setLargeAmount(reportUserRechargeWithdrawPO.getLargeAmount().add(vo.getLargeAmount()));
                reportUserRechargeWithdrawPO.setLargeNums(reportUserRechargeWithdrawPO.getLargeNums()+1);
            }
            if(null != vo.getDepositSubordinatesAmount() && BigDecimal.ZERO.compareTo(vo.getDepositSubordinatesAmount()) < 0){
                reportUserRechargeWithdrawPO.setDepositSubordinatesAmount(reportUserRechargeWithdrawPO.getDepositSubordinatesAmount().add(vo.getDepositSubordinatesAmount()));
                reportUserRechargeWithdrawPO.setDepositSubordinatesNums(reportUserRechargeWithdrawPO.getDepositSubordinatesNums()+1);
            }
            baseMapper.updateById(reportUserRechargeWithdrawPO);
        }
        log.info("会员累计存款-MQ队列-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);


    }

    public List<ReportRechargeAgentVO> queryByTimeAndAgent(ReportUserRechargeAgentReqVO vo) {
        return reportUserRechargeWithdrawRepository.queryByTimeAndAgent(vo);
    }

    public List<ReportUserRechargePayMethodAgentVO> queryPayMethodByTimeAndAgent(ReportUserRechargePayMethodAgentReqVO vo) {
        return reportUserRechargeWithdrawRepository.queryPayMethodByTimeAndAgent(vo);
    }

    /**
     * 会员存取款纤细
     */
    public List<DepositWithdrawalVO> getUserDepositWithdrawalPOList(UserInfoStatementVO vo) {
        return reportUserRechargeWithdrawRepository.getUserDepositWithdrawalPOList(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reportRealTimeUserRechargeWithdraw(ReportRealTimeUserDepositWithdrawReqParam param) {

        //需要重算的时间
        List<Long> needRunTimes = new ArrayList<>();
        computerTime(param.getStartTime(), param.getEndTime(), needRunTimes);
        log.info("重算时间：{}", JSONObject.toJSONString(needRunTimes));

        List<SiteVO> list = siteApi.siteInfoAllstauts().getData();
        List<String> siteCodes = Lists.newArrayList();
        if(ObjectUtil.isNotEmpty(param.getSiteCode())){
            siteCodes.add(param.getSiteCode());
        }else{
            siteCodes = list.stream().map(SiteVO::getSiteCode).toList();
        }
        String batchId = SnowFlakeUtils.getSnowId();
        if (CollectionUtil.isNotEmpty(needRunTimes)) {
            for (Long dataHourTime : needRunTimes) {
                long startTime = dataHourTime;
                long endTime = TimeZoneUtils.convertToUtcEndOfHour(dataHourTime);
                recalculation(startTime,endTime,siteCodes,list, batchId);

            }
        }
    }
    private void recalculation(Long startTime,Long endTime,List<String> siteCodes,
                               List<SiteVO> siteVOS,String batchId){

        Map<String,String> siteTimeZoneMap = siteVOS.stream()
                .collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getTimezone));
        //查询存取款数据
       List<DepositWithdrawAllRecordVO> depositWithdrawAllRecordVOS = userDepositWithdrawApi.getAllDepositWithdrawRecord(startTime,endTime,siteCodes);
       Map<String,ReportUserRechargeWithdrawPO> map = new HashMap<>();
       for (DepositWithdrawAllRecordVO vo:depositWithdrawAllRecordVOS) {
           String siteCode = vo.getSiteCode();
           String key = vo.getUserId()+startTime+vo.getType()+vo.getDepositWithdrawWayId()+vo.getAgentId();
           ReportUserRechargeWithdrawPO reportUserRechargeWithdrawPO = ConvertUtil.entityToModel(vo,ReportUserRechargeWithdrawPO.class);
           reportUserRechargeWithdrawPO.setDayHourMillis(startTime);
           Long dayMillis = TimeZoneUtils.getStartOfDayInTimeZone(startTime, siteTimeZoneMap.get(siteCode));
           reportUserRechargeWithdrawPO.setDayMillis(dayMillis);
           reportUserRechargeWithdrawPO.setDayStr(DateUtils.formatDateByZoneId(dayMillis,DateUtils.DATE_FORMAT_1,siteTimeZoneMap.get(siteCode)));
           reportUserRechargeWithdrawPO.setNums(CommonConstant.business_one);
           reportUserRechargeWithdrawPO.setRemark("重算,批次ID："+batchId);
           if(!map.containsKey(key)){
               reportUserRechargeWithdrawPO.setNums(CommonConstant.business_one);
               if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                   reportUserRechargeWithdrawPO.setLargeNums(CommonConstant.business_one);
               }
               if(null != vo.getDepositSubordinatesAmount()&& BigDecimal.ZERO.compareTo(vo.getDepositSubordinatesAmount()) < 0){
                   reportUserRechargeWithdrawPO.setDepositSubordinatesNums(CommonConstant.business_one);
               }
               map.put(key,reportUserRechargeWithdrawPO);
           }else{
               ReportUserRechargeWithdrawPO afterReportUserRechargeWithdrawPO = map.get(key);
               afterReportUserRechargeWithdrawPO.setAmount(afterReportUserRechargeWithdrawPO.getAmount().add(vo.getAmount()));
               afterReportUserRechargeWithdrawPO.setFeeAmount(afterReportUserRechargeWithdrawPO.getFeeAmount().add(vo.getFeeAmount()));
               afterReportUserRechargeWithdrawPO.setWayFeeAmount(afterReportUserRechargeWithdrawPO.getWayFeeAmount().add(vo.getWayFeeAmount()));
               afterReportUserRechargeWithdrawPO.setSettlementFeeAmount(afterReportUserRechargeWithdrawPO.getSettlementFeeAmount().add(vo.getSettlementFeeAmount()));
               afterReportUserRechargeWithdrawPO.setNums(afterReportUserRechargeWithdrawPO.getNums()+1);
               if(null != vo.getLargeAmount() && BigDecimal.ZERO.compareTo(vo.getLargeAmount()) < 0){
                   afterReportUserRechargeWithdrawPO.setLargeAmount(afterReportUserRechargeWithdrawPO.getLargeAmount().add(vo.getLargeAmount()));
                   afterReportUserRechargeWithdrawPO.setLargeNums(afterReportUserRechargeWithdrawPO.getLargeNums()+1);
               }
               if(null != vo.getDepositSubordinatesAmount() && BigDecimal.ZERO.compareTo(vo.getDepositSubordinatesAmount()) < 0){
                   afterReportUserRechargeWithdrawPO.setDepositSubordinatesAmount(afterReportUserRechargeWithdrawPO.getDepositSubordinatesAmount().add(vo.getDepositSubordinatesAmount()));
                   afterReportUserRechargeWithdrawPO.setDepositSubordinatesNums(afterReportUserRechargeWithdrawPO.getDepositSubordinatesNums()+1);
               }
               map.put(key,afterReportUserRechargeWithdrawPO);
           }
       }
       //删除之前数据
       new LambdaUpdateChainWrapper<>(baseMapper)
               .eq(ReportUserRechargeWithdrawPO::getDayHourMillis, startTime)
               .in(ReportUserRechargeWithdrawPO::getSiteCode,siteCodes)
               .remove();
       if(map.size()>0){

           List<ReportUserRechargeWithdrawPO> newList = new ArrayList<>(map.values());
           //批量保存数据
           this.saveBatch(newList);
       }

    }

    private void computerTime(Long startTime, Long endTime, List<Long> needRunTimes) {
        long startHourTime = TimeZoneUtils.convertToUtcStartOfHour(startTime);
        long endHourTime = TimeZoneUtils.convertToUtcStartOfHour(endTime);
        if (endHourTime >= TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis())) {
            // 如果结束时间是大于当前时间，则截止到统计到上个小时
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
        }
        while (startHourTime <= endHourTime) {
            needRunTimes.add(endHourTime);
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(endHourTime);
        }
    }

    public List<ReportUserAmountVO> getUserDepAmountByAgentIds(ReportAgentWinLossParamVO vo) {
        if(CollectionUtils.isEmpty(vo.getAgentIds())){
            return Lists.newArrayList();
        }
        return reportUserRechargeWithdrawRepository.getUserDepAmountByAgentIds(vo);
    }

    public List<ReportUserAmountVO> getUserDepAmountByUserId(ReportUserWinLossParamVO vo) {
        return reportUserRechargeWithdrawRepository.getUserDepAmountByUserId(vo);
    }

    public List<ReportUserAmountVO> getUserDepAmountByUserIds(ReportUserWinLossParamVO vo) {
        return reportUserRechargeWithdrawRepository.getUserDepAmountByUserIds(vo);
    }

    public List<ReportUserAmountVO> getUserFeeAmountByType(ReportAgentWinLossParamVO vo) {
        return reportUserRechargeWithdrawRepository.getUserFeeAmountByType(vo);
    }
}
