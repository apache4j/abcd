package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordResponseVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.info.AgentPayPasswordParam;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentTransferRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理端下级管理转账记录 服务")
public interface AgentTransferRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentTransfer/api";

    @Operation(summary = "转账记录 -代理代存，代理转账")
    @PostMapping(value = PREFIX + "/all-security-transferRecord")
    ResponseVO<Page<AgentTransferRecordResponseVO>> transferRecord(@RequestBody AgentTransferRecordRequestVO vo);
}
