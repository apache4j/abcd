package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AddVisCountVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainBO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainShortVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remotePromotionDomainApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - PromotionDomainApi")
public interface PromotionDomainApi {

    String PREFIX = ApiConstants.PREFIX + "/agentPromotionDomain/api";

    @Operation(summary = "获取推广链接的列表")
    @PostMapping(PREFIX + "/promotion-domain/api/getPromotionDomainList")
    ResponseVO<Page<PromotionDomainRespVO>> getPromotionDomainList(@RequestBody AgentDomainPageQueryVO pageQueryVO);

    @Operation(summary = "获取推广链接的列表")
    @PostMapping(PREFIX + "/promotion-domain/api/getPromotionDomain")
    ResponseVO<AgentDomainShortVO> getPromotionDomain(@RequestBody AgentDomainShortVO agentDomainShortVO);

    @PostMapping(PREFIX + "addVisCount")
    @Operation(summary = "访问量+1")
    ResponseVO<Boolean> addVisCount(@RequestBody AddVisCountVO countVO);

}
