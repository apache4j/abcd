package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRespVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentCoinRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理账变记录 AgentCoinRecordApi")
public interface AgentCoinRecordApi {


    String PREFIX = ApiConstants.PREFIX + "/agentCoinRecord/api";

    @Operation(description = "代理账变记录列表")
    @PostMapping(value = PREFIX + "/listAgentCoinRecordPage")
    ResponseVO<AgentCoinRecordRespVO> listAgentCoinRecordPage(@RequestBody AgentCoinRecordRequestVO vo);

    /**
     * 代理账变记录数据统计
     *
     * @param vo
     * @return
     */
    @PostMapping(value = PREFIX + "/agentCoinRecordPageListCount")
    Long agentCoinRecordPageListCount(@RequestBody AgentCoinRecordRequestVO vo);
}
