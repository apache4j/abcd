package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoRespVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "agentMerchantLoginInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC 商务基础信息 服务")
public interface AgentMerchantLoginInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/agentMerchantLoginInfoApi/api";

    @Operation(summary = "添加商务后台日志")
    @PostMapping(value = PREFIX + "addLoginInfo")
    boolean addLoginInfo(@RequestBody AgentMerchantVO agentMerchantVO);

    @PostMapping(PREFIX + "pageQuery")
    ResponseVO<AgentMerchantLoginInfoRespVO> pageQuery(@RequestBody AgentMerchantLoginInfoPageQueryVO queryVO);
}
