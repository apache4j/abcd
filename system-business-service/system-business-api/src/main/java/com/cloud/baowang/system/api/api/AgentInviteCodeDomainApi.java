package com.cloud.baowang.system.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainQueryVO;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "agentInviteCodeDomainApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - systemConfig")
public interface AgentInviteCodeDomainApi {

    String _PREFIX = ApiConstants.PREFIX + "/agentInviteCodeDomainApi/api/";

    @PostMapping(_PREFIX + "getDomainAndInCode")
    @Operation(summary = "获取代理邀请码-域名地址")
    ResponseVO<AgentInviteCodeDomainVO> getDomainAndInCode(@RequestBody AgentInviteCodeDomainQueryVO queryVO);

}