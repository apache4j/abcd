package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSiteRecordPageVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesListPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesPageResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentDepositSiteRecordApi", value = ApiConstants.NAME)
@Tag(name = "站点-资金-代理代存记录列表")
public interface AgentDepositSiteRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-deposit-list/api/";

    @Operation(summary = "代理代存记录列表")
    @PostMapping(value = PREFIX + "listPage")
    ResponseVO<AgentDepositSubordinatesPageResVO> listPage(@RequestBody AgentDepositSiteRecordPageVO agentDepositQueryPageVo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "depositExportCount")
    ResponseVO<Long> depositExportCount(@RequestBody AgentDepositSiteRecordPageVO requestVO);

    @Operation(summary = "代理代存记录列表-导出")
    @PostMapping(value = PREFIX + "doExport")
    Page<AgentDepositSubordinatesListPageResVO> doExport(@RequestBody AgentDepositSiteRecordPageVO agentDepositQueryPageVo);


    @Operation(summary = "代理代存记录按时间范围搜索")
    @PostMapping(value = PREFIX + "depositSubordinatesByTime")
    List<AgentDepositOfSubordinatesResVO> depositSubordinatesByTime(@RequestParam("startTime") Long startTime,
                                                                    @RequestParam("endTime") Long endTime);

}
