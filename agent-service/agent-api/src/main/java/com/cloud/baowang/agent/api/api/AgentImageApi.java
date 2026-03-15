package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImagePageQueryVO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * 代理推广-推广素材
 */
@FeignClient(contextId = "remoteAgentImageApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentImage")
public interface AgentImageApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-image/api";

    @GetMapping(PREFIX + "/getEnumList")
    @Operation(summary = "获取图片管理的常量字典")
    ResponseVO<HashMap<String, Object>> getEnumList();

    @PostMapping(PREFIX + "/addAgentImage")
    @Operation(summary = "添加图片")
    ResponseVO<Boolean> addAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO);

    @PostMapping(PREFIX + "/updateAgentImage")
    @Operation(summary = "修改图片")
    ResponseVO<Boolean> updateAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO);

    @GetMapping(PREFIX + "/deleteAgentImage")
    @Operation(summary = "删除图片")
    ResponseVO<Boolean> deleteAgentImage(@RequestParam("id")String id);

    @GetMapping(PREFIX + "/getAgentImageById")
    @Operation(summary = "获取图片")
    ResponseVO<AgentImageVO> getAgentImageById(@RequestParam("id")String id);

    @Operation(summary = "获取图片的列表")
    @PostMapping(PREFIX + "/getAgentImageList")
    ResponseVO<Page<AgentImageVO>> getAgentImageList(@RequestBody AgentImagePageQueryVO agentDomainVO);

}
