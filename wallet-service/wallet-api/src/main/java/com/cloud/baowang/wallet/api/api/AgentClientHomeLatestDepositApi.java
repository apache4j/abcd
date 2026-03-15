package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.agent.LatestDepositParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteAgentClientHomeLatestDepositApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理PC和H5 首页统计 最新存款 服务")
public interface AgentClientHomeLatestDepositApi {

    String PREFIX = ApiConstants.PREFIX + "/agentClientHomeLatestDeposit/api/";

    @Operation(summary = "最新存款")
    @PostMapping(value = PREFIX + "latestDeposit")
    ResponseVO<List<DepositRecordResponseVO>> latestDeposit(@RequestBody LatestDepositParam vo);
}