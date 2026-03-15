package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeDetailReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordDetailVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentCoinChangeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理客户端账变明细查询 服务")
public interface AgentCoinChangeApi {

    String PREFIX = ApiConstants.PREFIX + "/agentCoinChangeApi/api";

    @Operation(description = "代理客户端账变记录列表")
    @PostMapping(value = PREFIX + "/listAgentCoinRecordPage")
    ResponseVO<Page<AgentCustomerCoinRecordVO>> listAgentCustomerCoinRecord(@RequestBody AgentCoinChangeReqVO vo);

    @Operation(description = "代理客户端账变记录详情")
    @PostMapping(value = PREFIX + "/getCoinRecordDetail")
    ResponseVO<AgentCustomerCoinRecordDetailVO> getCoinRecordDetail(@RequestBody AgentCoinChangeDetailReqVO vo);
}
