package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.withdraw.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理提款 服务")
public interface AgentWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteUserWithdrawApi/api/";

    @Operation(summary = "代理提现申请")
    @PostMapping(value = PREFIX + "agentWithdrawApply")
    ResponseVO<Integer> agentWithdrawApply(@RequestBody AgentWithDrawApplyVO vo);


    @Operation(summary = "代理提款配置")
    @PostMapping(value = PREFIX + "getWithdrawConfig")
    ResponseVO<AgentWithdrawConfigResponseVO> getAgentWithdrawConfig(@RequestBody AgentWithdrawConfigRequestVO withdrawConfigRequestVO);


    @PostMapping(value = PREFIX + "clientAgentWithdrawRecorder")
    Page<ClientAgentWithdrawRecordResponseVO> clientAgentWithdrawRecorder(@RequestBody ClientAgentWithdrawRecordRequestVO vo);


    @Operation(summary = "代理端提款订单详情")
    @PostMapping(value = PREFIX + "clientAgentWithdrawRecordDetail")
    AgentWithdrawRecordDetailResponseVO clientAgentWithdrawRecordDetail(@RequestBody AgentTradeRecordDetailRequestVO vo);



}
