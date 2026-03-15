package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;


@FeignClient(contextId = "remotePromotionImageApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - PromotionImageApi")
public interface PromotionImageApi {

    String PREFIX = ApiConstants.PREFIX + "/agentPromotionImage/api";


    @PostMapping(PREFIX + "/getAgentImageById")
    @Operation(summary = "获取图片素材")
    ResponseVO<AgentImageVO> getAgentImageById(@RequestBody AgentImageVO agentDomainVO);


    @Operation(summary = "获取图片素材的列表")
    @PostMapping(PREFIX +"/getAgentImageList")
    ResponseVO<Page<AgentImageVO>> getAgentImageList(@RequestBody AgentImageVO agentDomainVO);
}
