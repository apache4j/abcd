package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownAddVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownAccountResultVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentManualDownApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理人工扣除额度 服务")
public interface AgentManualDownApi {

    String PREFIX = ApiConstants.PREFIX + "/agentManualDown/api";

    @PostMapping(value = PREFIX + "/saveManualDown")
    ResponseVO<Boolean> saveManualDown(@RequestBody AgentManualDownAddVO vo, @RequestParam("operator") String operator);

    @PostMapping(value = PREFIX + "/listAgentManualDownRecordPage")
    ResponseVO<AgentManualDownRecordResponseVO> listAgentManualDownRecordPage(@RequestBody AgentManualDownRecordRequestVO vo);

    @PostMapping(value = PREFIX + "/listAgentManualDownRecordPageExportCount")
    ResponseVO<Long> listAgentManualDownRecordPageExportCount(@RequestBody AgentManualDownRecordRequestVO vo);

    @GetMapping(PREFIX + "/getTotalPendingReviewBySiteCode")
    @Operation(summary = "统计当前站点所有代理人工加额待审核记录")
    long getTotalPendingReviewBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "/checkAgentInfo")
    @Operation(summary = "代理人工扣除额度校验代理信息")
    ResponseVO<List<AgentManualUpDownAccountResultVO>> checkAgentInfo(@RequestBody List<AgentManualUpDownAccountResultVO> list);
}
