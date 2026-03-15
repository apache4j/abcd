package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentWithdrawRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理提款记录 服务")
public interface AgentWithdrawRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentWithdrawRecord/api";

    @PostMapping(value = PREFIX + "/getAgentWithdrawalRecordPageList")
    ResponseVO<AgentWithdrawalRecordPageResVO> getAgentWithdrawalRecordPageList(@RequestBody AgentWithdrawalRecordReqVO requestVO);

    @PostMapping(value = PREFIX + "/agentWithdrawRecordRecordPageCount")
    ResponseVO<Long> agentWithdrawRecordRecordPageCount(@RequestBody AgentWithdrawalRecordReqVO vo);

    @Operation(summary ="根据订单id查询记录")
    @PostMapping(PREFIX + "getRecordByOrderId")
    AgentWithdrawalRecordResVO getRecordByOrderId(@RequestParam("orderId") String orderId);
}
