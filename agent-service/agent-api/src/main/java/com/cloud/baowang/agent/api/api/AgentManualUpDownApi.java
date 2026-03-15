package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentManualUpDownApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理人工加减额 服务")
public interface AgentManualUpDownApi {


    @Operation(summary = "代理人工加减额 分页查询")
    @PostMapping(value = "/agent-manual-up-down/api/listPage")
    Page<AgentManualUpRecordResponseVO> listPage(@RequestBody AgentManualDownRequestVO agentManualDownRequestVO);

    @Operation(summary = "代理人工加减额 统计数据查询")
    @PostMapping(value = "/agent-manual-up-down/api/listStaticData")
    Map<String,AgentManualUpRecordResponseVO> listStaticData(@RequestBody List<String> agentIds);


}
