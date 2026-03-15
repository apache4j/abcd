package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;



@FeignClient(contextId = "remoteAgentDomainRecordApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentDomainRecord")
public interface AgentDomainRecordApi {



    @Operation(summary = "获取域名的变更记录的列表")
    @PostMapping("/getAgentDomainRecordList")
    ResponseVO<Page<AgentDomainRecordResponseVO>> getAgentDomainRecordList(@RequestBody AgentDomainRecordVO agentDomainRecordVO);
}
