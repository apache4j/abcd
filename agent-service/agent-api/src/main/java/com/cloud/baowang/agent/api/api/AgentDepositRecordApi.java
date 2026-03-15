package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositAllRes;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordReq;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordRes;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalStatisticsVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(contextId = "remoteAgentDepositRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理存款记录 服务")
public interface AgentDepositRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentDepositRecord/api";

    @Operation(summary = "分页记录")
    @PostMapping(value = PREFIX + "/depositListPage")
    ResponseVO<AgentDepositAllRes> depositListPage(@RequestBody AgentDepositRecordReq requestVO);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "/depositExportCount")
    ResponseVO<Long> depositExportCount(@RequestBody AgentDepositRecordReq requestVO);

    @PostMapping(PREFIX+"getDepositTotal")
    @Operation(summary = "统计代理存款")
    AgentWithdrawalStatisticsVO getDepositTotal(@RequestBody AgentDepositRecordReq recordReq);

}
