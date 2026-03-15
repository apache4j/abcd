package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionImitateCalcVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentCommissionExplainVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/06/21 14:13
 * @description:
 */
@Tag(name = "代理-佣金说明")
@AllArgsConstructor
@RestController
@RequestMapping("/agentCommission/api")
public class AgentCommissionController {

    private final AgentCommissionApi agentCommissionApi;

    @Operation(summary = "佣金模拟器")
    @PostMapping(value = "/commissionImitate")
    public ResponseVO<BigDecimal> commissionImitate(@Valid @RequestBody CommissionImitateCalcVO commissionImitateCalcVO) {
        String agentAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        commissionImitateCalcVO.setAgentAccount(agentAccount);
        commissionImitateCalcVO.setSiteCode(siteCode);
        return ResponseVO.success(agentCommissionApi.commissionImitate(commissionImitateCalcVO));
    }

    @Operation(summary = "佣金说明")
    @PostMapping(value = "/getCommissionExplain")
    public ResponseVO<AgentCommissionExplainVO> getCommissionExplain() {
        String agentId = CurrReqUtils.getOneId();
        return agentCommissionApi.getCommissionExplain(agentId);
    }

    @Operation(summary = "当前代理佣金方案")
    @PostMapping(value = "/getCurrentCommissionPlain")
    public ResponseVO<AgentCommissionPlanVO> getCurrentCommissionPlain() {
        return agentCommissionApi.getCurrentCommissionPlain(CurrReqUtils.getOneId());
    }

}
