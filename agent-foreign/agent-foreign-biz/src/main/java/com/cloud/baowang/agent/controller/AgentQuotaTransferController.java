package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentQuotaTransferApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "代理-额度转账")
@AllArgsConstructor
@RestController
@RequestMapping(value = "agent-quota-transfer/api/")
public class AgentQuotaTransferController {

    private final AgentQuotaTransferApi agentQuotaTransferApi;

    @Operation(summary ="额度转账-可用余额")
    @PostMapping("balance")
    public ResponseVO<AgentQuotaTransferBalanceVO> balance() {
        String agentAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        return agentQuotaTransferApi.balance(agentAccount,siteCode);
    }

    @Operation(summary = "额度转账-转账")
    @PostMapping("transfer")
    public ResponseVO<Boolean> transfer(@RequestBody AgentQuotaTransferVO agentQuotaTransferVO) {
        agentQuotaTransferVO.setAgentInfoId(CurrReqUtils.getOneId());
        return agentQuotaTransferApi.transfer(agentQuotaTransferVO);
    }

    @Operation(summary = "额度转账记录")
    @PostMapping("record")
    public ResponseVO<Page<AgentQuotaTransferRecordRespVO>> record(@RequestBody AgentQuotaTransferRecordReqVO reqVO) {
        reqVO.setAgentInfoId(CurrReqUtils.getOneId());
        return agentQuotaTransferApi.record(reqVO);
    }

}
