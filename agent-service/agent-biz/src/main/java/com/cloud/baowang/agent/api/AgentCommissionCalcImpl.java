package com.cloud.baowang.agent.api;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.agent.api.api.AgentCommissionCalcApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionCalcVO;
import com.cloud.baowang.agent.service.commission.AgentCommissionCalcService;
import com.cloud.baowang.agent.service.commission.AgentCommissionExpectCalcService;
import com.cloud.baowang.agent.service.commission.AgentCommissionLadderService;
import com.cloud.baowang.agent.service.rebate.AgentRebateCalcService;
import com.cloud.baowang.agent.service.rebate.AgentRebateExpectCalcService;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/20 22:25
 * @description:
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentCommissionCalcImpl implements AgentCommissionCalcApi {

    private final AgentCommissionCalcService agentCommissionCalcService;

    private final AgentCommissionExpectCalcService agentCommissionExpectCalcService;

    private final AgentRebateCalcService agentRebateCalcService;

    private final AgentRebateExpectCalcService agentRebateExpectCalcService;

    private final AgentCommissionLadderService agentCommissionLadderService;

    private final SiteApi siteApi;

    @Override
    public void agentFinalCommissionGenerate(AgentCommissionCalcVO commissionCalcVO) {
        if (commissionCalcVO.getIsManual() != null && commissionCalcVO.getIsManual() == 1) {
            agentCommissionCalcService.agentFinalCommissionGenerate(commissionCalcVO);
            return;
        }

        //根据时区获取站点
        String timeZone = TimeZoneUtils.get4TimeZone();
        if (StringUtils.isBlank(commissionCalcVO.getTimeZone())) {
            commissionCalcVO.setTimeZone(timeZone);
        }
        List<SiteVO> siteList = siteApi.getSiteInfoByTimezone(timeZone);
        if (ObjectUtil.isEmpty(siteList)) {
            return;
        }

        for (SiteVO siteVO : siteList) {
//            自动跑这里不需要判断收否一致下面有判断,只有第一个站点会处理 update by xiaozhi 20250422
//            if (ObjectUtil.isNotEmpty(commissionCalcVO.getSiteCode())) {
//                if (!siteVO.getSiteCode().equals(commissionCalcVO.getSiteCode())) {
//                    continue;
//                }
//            }
            commissionCalcVO.setSiteCode(siteVO.getSiteCode());
            List<String> planCodeList = agentCommissionLadderService.getPlanCodeListByCycle(commissionCalcVO.getSettleCycle(), commissionCalcVO.getSiteCode());
            if(planCodeList == null || planCodeList.isEmpty()){//放在外面如果没有继续下一个，解决线上问题 update by xiaozhi 20250421
                log.error("负盈利佣金计算,站点{}, 没有对应计划id", commissionCalcVO.getSiteCode());
                continue;
            }
            agentCommissionCalcService.agentFinalCommissionGenerate(commissionCalcVO);
        }

    }

    @Override
    public void agentExpectCommissionGenerate(AgentCommissionCalcVO commissionCalcVO) {
        if (commissionCalcVO.getIsManual() != null && commissionCalcVO.getIsManual() == 1) {
            agentCommissionExpectCalcService.agentExpectCommissionGenerate(commissionCalcVO);
            return;
        }

        /*String timeZone = TimeZoneUtils.get4TimeZone();
        if (StringUtils.isBlank(commissionCalcVO.getTimeZone())) {
            commissionCalcVO.setTimeZone(timeZone);
        }*/
        //根据时区获取站点
        ResponseVO<List<SiteVO>> responseVO = siteApi.siteInfoAllstauts();
        List<SiteVO> siteList = responseVO.getData();
        if (ObjectUtil.isEmpty(siteList)) {
            return;
        }

        for (SiteVO siteVO : siteList) {
//            自动跑这里不需要判断收否一致下面有判断,只有第一个站点会处理 update by xiaozhi 20250422
//            if (ObjectUtil.isNotEmpty(commissionCalcVO.getSiteCode())) {
//                if (!siteVO.getSiteCode().equals(commissionCalcVO.getSiteCode())) {
//                    continue;
//                }
//            }
            commissionCalcVO.setTimeZone(siteVO.getTimezone());
            commissionCalcVO.setSiteCode(siteVO.getSiteCode());
            agentCommissionExpectCalcService.agentExpectCommissionGenerate(commissionCalcVO);
        }
    }

    @Override
    public void agentRebateGenerate(AgentCommissionCalcVO commissionCalcVO) {
        if (commissionCalcVO.getIsManual() != null && commissionCalcVO.getIsManual() == 1) {
            agentRebateCalcService.agentRebateGenerate(commissionCalcVO);
            return;
        }

        String timeZone = TimeZoneUtils.get2TimeZone();
        if (StringUtils.isBlank(commissionCalcVO.getTimeZone())) {
            commissionCalcVO.setTimeZone(timeZone);
        }
        //根据时区获取站点
        List<SiteVO> siteList = siteApi.getSiteInfoByTimezone(timeZone);
        if (ObjectUtil.isEmpty(siteList)) {
            return;
        }

        for (SiteVO siteVO : siteList) {
//            自动跑这里不需要判断收否一致下面有判断,只有第一个站点会处理 update by xiaozhi 20250422
//            if (ObjectUtil.isNotEmpty(commissionCalcVO.getSiteCode())) {
//                if (!siteVO.getSiteCode().equals(commissionCalcVO.getSiteCode())) {
//                    continue;
//                }
//            }
            commissionCalcVO.setSiteCode(siteVO.getSiteCode());
            agentRebateCalcService.agentRebateGenerate(commissionCalcVO);
        }
    }

    @Override
    public void agentRebateExpectGenerate(AgentCommissionCalcVO commissionCalcVO) {
        if (commissionCalcVO.getIsManual() != null && commissionCalcVO.getIsManual() == 1) {
            agentRebateExpectCalcService.agentRebateExpectGenerate(commissionCalcVO);
            return;
        }

        /*String timeZone = TimeZoneUtils.get2TimeZone();
        if (StringUtils.isBlank(commissionCalcVO.getTimeZone())) {
            commissionCalcVO.setTimeZone(timeZone);
        }*/
        //根据时区获取站点
        ResponseVO<List<SiteVO>> responseVO = siteApi.siteInfoAllstauts();
        List<SiteVO> siteList = responseVO.getData();
        if (ObjectUtil.isEmpty(siteList)) {
            return;
        }

        for (SiteVO siteVO : siteList) {
//            自动跑这里不需要判断收否一致下面有判断,只有第一个站点会处理 update by xiaozhi 20250422
//            if (ObjectUtil.isNotEmpty(commissionCalcVO.getSiteCode())) {
//                if (!siteVO.getSiteCode().equals(commissionCalcVO.getSiteCode())) {
//                    continue;
//                }
//            }
            commissionCalcVO.setTimeZone(siteVO.getTimezone());
            commissionCalcVO.setSiteCode(siteVO.getSiteCode());
            agentRebateExpectCalcService.agentRebateExpectGenerate(commissionCalcVO);
        }
    }
}
