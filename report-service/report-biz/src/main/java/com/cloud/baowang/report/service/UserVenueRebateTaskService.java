package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollUtil;
import com.cloud.baowang.agent.api.api.AgentCommissionReviewApi;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountCommonVo;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountVo;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.report.api.vo.ReportUserAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserWinLossRebateParamVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.system.api.api.site.rebate.UserVenueRebateApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserVenueRebateTaskService {
    private final ReportUserVenueWinLoseService userVenueWinLoseService;
    private final UserVenueRebateApi userVenueRebateApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AgentCommissionReviewApi agentCommissionApi;

    public ResponseVO<Boolean> onAgentCommissionTaskBegin(ReportRecalculateVO reqVo) {
        log.info("onAgentCommissionTaskBegin reqVo:{}",reqVo);
        String timezone = reqVo.getTimeZone();
        Long startTime;
        Long endTime;
        Long flushTime = null;
        if (reqVo.getStartTime() != null){
             flushTime = reqVo.getStartTime();
             startTime = DateUtils.getPreviousDayStartTime(flushTime,timezone);
             endTime = DateUtils.getPreviousDayEndTime(flushTime, timezone);
        }else {
            startTime = DateUtils.getYesTodayStartTime(timezone);
            endTime = DateUtils.getYesTodayEndTime(timezone);
            flushTime = System.currentTimeMillis();
        }
        //查出当前站点会员场馆赢亏
        ReportUserWinLossRebateParamVO paramVO = new ReportUserWinLossRebateParamVO();
        List<SiteCurrencyInfoRespVO> currencyInfoList = siteCurrencyInfoApi.getValidBySiteCode(reqVo.getSiteCode());
        List<String> currencyCodeList = currencyInfoList.stream().map(SiteCurrencyInfoRespVO::getCurrencyCode).toList();

        VenueTypeEnum[] values = VenueTypeEnum.values();
            for (String currencyCode : currencyCodeList) {
                for (VenueTypeEnum venueTypeEnum : values) {
                    paramVO.setStartTime(startTime);
                    paramVO.setEndTime(endTime);
                    paramVO.setSiteCode(reqVo.getSiteCode());
                    paramVO.setCurrencyCode(currencyCode);
                    List<ReportUserAgentVenueStaticsVO> records = userVenueWinLoseService.getUserVenueBetInfo(paramVO);

                    log.info("userVenueWinLoseService - paramVO:{},size:{}",paramVO,records.size());
                    if (CollUtil.isNotEmpty(records)) {
                        AgentValidAmountVo agentTurnover = new AgentValidAmountVo();
                        List<AgentValidAmountCommonVo>  agentVenueTurnoverVOS= Lists.newArrayList();
                        records.forEach(o->{
                            AgentValidAmountCommonVo turnoverVo=new AgentValidAmountCommonVo();
                            BeanUtils.copyProperties(o,turnoverVo);
                            agentVenueTurnoverVOS.add(turnoverVo);
                        });
                        agentTurnover.setSiteCode(reqVo.getSiteCode());
                        agentTurnover.setRecords(agentVenueTurnoverVOS);
                        agentTurnover.setStartTime(startTime);
                        agentTurnover.setEndTime(endTime);
                        agentTurnover.setFlushTime(flushTime);
                        agentTurnover.setTimeZoneStr(timezone);
                        agentTurnover.setCurrencyCode(currencyCode);
                        agentTurnover.setVenueType(venueTypeEnum.getCode());
                        log.info("UserVenueRebateTaskService.onRebateTaskArrived----------参数:agentTurnover -> {}",agentTurnover);
                        agentCommissionApi.calcAgentCommission(agentTurnover);
//                        userVenueRebateApi.handleUserVenueBetInfo(agentTurnover)
                    }

            }
        }
        return ResponseVO.success(true);
    }


}
