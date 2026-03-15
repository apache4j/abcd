package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.withdraw.*;
import com.cloud.baowang.agent.service.AgentDepositWithdrawService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentWithdrawApiImpl implements AgentWithdrawApi {

    private final AgentDepositWithdrawService agentDepositWithdrawService;



    @Override
    public ResponseVO<AgentWithdrawConfigResponseVO> getAgentWithdrawConfig(AgentWithdrawConfigRequestVO withdrawConfigRequestVO){
        AgentWithdrawConfigResponseVO agentWithdrawConfigResponseVO = agentDepositWithdrawService.getAgentWithdrawConfig(withdrawConfigRequestVO);
       return ResponseVO.success(agentWithdrawConfigResponseVO);
    }

    @Override
    public Page<ClientAgentWithdrawRecordResponseVO> clientAgentWithdrawRecorder(ClientAgentWithdrawRecordRequestVO vo) {
        return agentDepositWithdrawService.clientAgentWithdrawRecorder(vo);
    }

    @Override
    public AgentWithdrawRecordDetailResponseVO clientAgentWithdrawRecordDetail(AgentTradeRecordDetailRequestVO vo) {
        return agentDepositWithdrawService.clientAgentWithdrawRecordDetail(vo);
    }

    @Override
    public ResponseVO<Integer> agentWithdrawApply(AgentWithDrawApplyVO agentWithDrawApplyVO) {
        return agentDepositWithdrawService.withdrawApply(agentWithDrawApplyVO);
    }

}
