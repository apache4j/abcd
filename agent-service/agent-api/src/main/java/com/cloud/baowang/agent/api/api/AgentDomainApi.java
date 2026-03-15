package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "remoteAgentDomainApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentDomain")
public interface AgentDomainApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-domain/api/";


    @PostMapping(PREFIX + "addAgentDomain")
    @Operation(summary = "添加域名管理")
    ResponseVO<Boolean> addAgentDomain(@RequestBody List<AgentDomainVO> agentDomainVO);


    @PostMapping(PREFIX + "updateAgentDomain")
    @Operation(summary = "修改域名管理")
    ResponseVO<Boolean> updateAgentDomain(@RequestBody AgentDomainVO agentDomainVO);


    @GetMapping(PREFIX + "deleteAgentDomain")
    @Operation(summary = "删除域名管理")
    ResponseVO<Boolean> deleteAgentDomain(@RequestParam("domainName") String domainName);


    @PostMapping(PREFIX + "getAgentDomainById")
    @Operation(summary = "获取域名管理")
    ResponseVO<AgentDomainResponseVO> getAgentDomainById(@RequestParam("id") String id);


    @Operation(summary = "获取域名管理的列表")
    @PostMapping(PREFIX + "getAgentDomainList")
    ResponseVO<Page<AgentDomainResponseVO>> getAgentDomainList(@RequestBody AgentDomainPageQueryVO agentDomainVO);


}
