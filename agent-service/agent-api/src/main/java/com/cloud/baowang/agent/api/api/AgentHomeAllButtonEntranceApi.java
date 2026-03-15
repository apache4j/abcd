package com.cloud.baowang.agent.api.api;


import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.BaseReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentHomeAllButtonApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentHomeAllButton")
public interface AgentHomeAllButtonEntranceApi {


    String PREFIX = ApiConstants.PREFIX + "/agent-home-button/api";


    @PostMapping(PREFIX + "/init")
    @Operation(summary = "代理端菜单按钮初始化")
    ResponseVO<Boolean> init(@RequestBody BaseReqVO baseReqVO);


}
