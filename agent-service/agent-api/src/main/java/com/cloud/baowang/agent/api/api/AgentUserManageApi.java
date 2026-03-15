package com.cloud.baowang.agent.api.api;


import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.user.AgentOverviewResponseVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentUserManageApi", value = ApiConstants.NAME)
@Tag(name = "代理PC和H5 会员管理 服务")
public interface AgentUserManageApi {

    String PREFIX = ApiConstants.PREFIX + "/agentUserManageApi/api";


    @Operation(summary = "下级概览")
    @PostMapping(value = "/agentOverview")
    ResponseVO<AgentOverviewResponseVO> agentOverview(@RequestParam("currentId") String currentId,
                                                      @RequestParam("currentAgent") String currentAgent,
                                                      @RequestParam("siteCode")  String siteCode);


}
