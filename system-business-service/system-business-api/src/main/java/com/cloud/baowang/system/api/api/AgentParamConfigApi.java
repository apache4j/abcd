package com.cloud.baowang.system.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigBO;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "remoteAgentParamConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统-参数字典配置服务 - AgentParamConfigApi")
public interface AgentParamConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/AgentParamConfig/api";


    @Operation(summary = "获取代理参数配置的常量字典")
    @PostMapping(PREFIX + "/getEnumList")
    ResponseVO<HashMap<String, Object>> getEnumList();


    @Operation(summary = "修改代理参数配置")
    @PostMapping(PREFIX + "/updateAgentParamConfig")
    ResponseVO updateAgentParamConfig(@RequestBody AgentParamConfigVO agentParamConfigVO);


    @Operation(summary = "获取代理参数配置")
    @PostMapping(PREFIX + "/getAgentParamConfigById")
    ResponseVO<AgentParamConfigBO> getAgentParamConfigById(@RequestBody AgentParamConfigVO agentParamConfigVO);


    @Operation(summary = "获取代理参数配置的列表")
    @PostMapping(PREFIX + "/getAgentParamConfigList")
    ResponseVO<Page<AgentParamConfigBO>> getAgentParamConfigList(@RequestBody AgentParamConfigVO agentParamConfigVO);

    @Operation(summary = "获取所有代理参数配置")
    @PostMapping(PREFIX + "/getAgentParamConfigAll")
    List<AgentParamConfigBO> getAgentParamConfigAll();

    @Operation(summary = "获取字典配置列表")
    @PostMapping(PREFIX + "/queryAgentParamConfig")
    List<AgentParamConfigBO> queryAgentParamConfig(@RequestBody List<String> paramCode);


    @Operation(summary = "获取字典配置列表-根据code")
    @PostMapping(PREFIX + "/queryAgentParamConfigByCode")
    AgentParamConfigBO queryAgentParamConfigByCode(@RequestParam("paramCode") String paramCode);


}