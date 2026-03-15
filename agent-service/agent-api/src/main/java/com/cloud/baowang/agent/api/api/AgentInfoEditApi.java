package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agent.AgentTeamVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentDetailIBasicVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoEditVO;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentInfoEditApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理信息编辑-基本信息 服务")
public interface AgentInfoEditApi {

    String PREFIX = ApiConstants.PREFIX + "/agentInfoEdit/api";

    @Operation(summary = "发起代理信息变更")
    @PostMapping(value = PREFIX + "/initiateAgentInfoChange")
    ResponseVO<Void> initiateAgentInfoChange(@RequestBody AgentInfoEditVO vo);

    @Operation(summary = "基本信息查询")
    @PostMapping(value = PREFIX + "/getBasicAgentInfo")
    ResponseVO<AgentDetailIBasicVO> getBasicAgentInfo(@RequestBody AgentDetailParam param);

    @Operation(summary = "备注信息查询")
    @PostMapping(value = PREFIX + "/getAgentRemark")
    ResponseVO<Page<AgentRemarkRecordVO>> getAgentRemark(@RequestBody AgentDetailParam param);

    @Operation(summary = "团对信息")
    @PostMapping(value = PREFIX + "/getAgentTeam")
    ResponseVO<AgentTeamVO> getAgentTeam(@RequestBody AgentDetailParam param);
}
