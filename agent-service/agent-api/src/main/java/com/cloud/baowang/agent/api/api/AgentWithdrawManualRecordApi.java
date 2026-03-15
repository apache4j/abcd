package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualDetailReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPageReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPayReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordPageResVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordlDetailVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentWithdrawManualRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理人工出款记录 服务")
public interface AgentWithdrawManualRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentWithdrawManualRecord/api/";

    @Operation(summary = "代理人工出款分页列表")
    @PostMapping(value = PREFIX + "withdrawManualPage")
    Page<AgentWithdrawManualRecordPageResVO> withdrawManualPage(@RequestBody AgentWithdrawManualPageReqVO vo);


    @Operation(summary = "代理人工出款详情")
    @PostMapping(value = PREFIX + "withdrawManualDetail")
    AgentWithdrawManualRecordlDetailVO withdrawManualDetail(@RequestBody AgentWithdrawManualDetailReqVO vo);


    @Operation(summary = "代理人工出款详情")
    @PostMapping(value = PREFIX + "withdrawManualPay")
    ResponseVO<Boolean> withdrawManualPay(@RequestBody AgentWithdrawManualPayReqVO vo);
    @Operation(summary = "代理人工款记录条数统计")
    @PostMapping(value = PREFIX + "withdrawalManualRecordPageCount")
    ResponseVO<Long> withdrawalManualRecordPageCount(@RequestBody AgentWithdrawManualPageReqVO vo);
}
