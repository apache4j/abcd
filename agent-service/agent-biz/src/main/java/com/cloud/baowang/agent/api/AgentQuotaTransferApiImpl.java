package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentQuotaTransferApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferVO;
import com.cloud.baowang.agent.service.AgentQuotaTransferService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/30 14:11
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentQuotaTransferApiImpl implements AgentQuotaTransferApi {

    private AgentQuotaTransferService agentQuotaTransferService;

    @Override
    public ResponseVO<Page<AgentQuotaTransferRecordRespVO>> record(AgentQuotaTransferRecordReqVO reqVO) {
        return ResponseVO.success(agentQuotaTransferService.record(reqVO));
    }

    @Override
    public ResponseVO<Boolean> transfer(AgentQuotaTransferVO agentQuotaTransferVO) {
        return ResponseVO.success(agentQuotaTransferService.transfer(agentQuotaTransferVO));
    }

    @Override
    public ResponseVO<AgentQuotaTransferBalanceVO> balance(String agentAccount,String siteCode) {
        return ResponseVO.success(agentQuotaTransferService.balance(agentAccount, siteCode));

    }
}
