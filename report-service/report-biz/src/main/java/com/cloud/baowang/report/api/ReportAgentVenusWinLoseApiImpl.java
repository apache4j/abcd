package com.cloud.baowang.report.api;

import com.cloud.baowang.report.api.api.ReportAgentVenusWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserVenueLisParam;
import com.cloud.baowang.report.api.vo.user.ReportUserFinanceVO;
import com.cloud.baowang.report.service.AgentStatisticsOrderService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 代理场馆净输赢
 * @Author: Ford
 * @Date: 2024/12/23 14:19
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class ReportAgentVenusWinLoseApiImpl implements ReportAgentVenusWinLoseApi {
    private AgentStatisticsOrderService agentStatisticsOrderService;

    /**
     * 代理下场馆投注前三
     * @param vo
     * @return
     */
    @Override
    public ReportUserFinanceVO agentTopThreeVenue(ReportAgentUserVenueLisParam vo) {
        return agentStatisticsOrderService.agentTopThreeVenue(vo);
    }
}
