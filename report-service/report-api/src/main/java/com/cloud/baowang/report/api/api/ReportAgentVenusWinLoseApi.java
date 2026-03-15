package com.cloud.baowang.report.api.api;

import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserVenueLisParam;
import com.cloud.baowang.report.api.vo.user.ReportUserFinanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteReportAgentVenusWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "注单记录相关api")
public interface ReportAgentVenusWinLoseApi {

    String PREFIX = ApiConstants.PREFIX + "/reportAgentVenusWinLose/api/";

    @Operation(summary = "注单列表查询-站点列表")
    @PostMapping(PREFIX + "getAgentUserVenueLis")
    ReportUserFinanceVO agentTopThreeVenue(@RequestBody ReportAgentUserVenueLisParam vo);


}
