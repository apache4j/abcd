package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawManualRecordApi;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualDetailReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPageReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPayReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordPageResVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordlDetailVO;
import com.cloud.baowang.agent.service.AgentWithdrawManualRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentWithdrawManualRecordApiImpl implements AgentWithdrawManualRecordApi {

    private final AgentWithdrawManualRecordService agentWithdrawManualRecordService;


    @Override
    public Page<AgentWithdrawManualRecordPageResVO> withdrawManualPage(AgentWithdrawManualPageReqVO vo) {
        return agentWithdrawManualRecordService.withdrawManualPage(vo);
    }

    @Override
    public AgentWithdrawManualRecordlDetailVO withdrawManualDetail(AgentWithdrawManualDetailReqVO vo) {
        return agentWithdrawManualRecordService.withdrawManualDetail(vo);
    }

    @Override
    public ResponseVO<Boolean> withdrawManualPay(AgentWithdrawManualPayReqVO vo) {
        return agentWithdrawManualRecordService.withdrawManualPay(vo);
    }

    @Override
    public ResponseVO<Long> withdrawalManualRecordPageCount(AgentWithdrawManualPageReqVO vo) {
        return ResponseVO.success(agentWithdrawManualRecordService.withdrawalManualRecordPageCount(vo));
    }
}
