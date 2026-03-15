package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.level.AgentLevelConfigVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Desciption: 代理层级
 * @Author: Ford
 * @Date: 2024/6/3 10:03
 * @Version: V1.0
 **/
@FeignClient(contextId = "remoteAgentLevelApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理商层级 服务")
public interface AgentLevelApi {

    String PREFIX = ApiConstants.PREFIX + "/agentLevel/api/";

    @Operation(summary = "获取所有代理商层级信息")
    @PostMapping(value = PREFIX + "getAllLevelConfig")
    ResponseVO<List<AgentLevelConfigVO>> getAllLevelConfig();

    @Operation(summary = "保持代理商层级")
    @PostMapping(value = PREFIX + "saveConfig")
    ResponseVO saveConfig(@RequestBody @Validated AgentLevelConfigVO reqVO) ;

    @Operation(summary = "获取最新层级")
    @PostMapping(value = PREFIX + "getLatestLevel")
     ResponseVO<Integer> getLatestLevel();

    @Operation(summary = "修改层级")
    @PostMapping(value = PREFIX + "updateByLevel")
     ResponseVO<Boolean> updateByLevel(@RequestBody @Validated AgentLevelConfigVO agentLevelConfigVO);

}
