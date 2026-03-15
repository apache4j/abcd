package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ford
 * @Date 2024-11-04
 */
@FeignClient(contextId = "remoteTopAgentReportStatics", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 商务总代报表")
public interface ReportTopAgentStaticsApi {

    String PREFIX = ApiConstants.PREFIX + "/reportTopAgentStatics/api";

    @Operation(summary = "商务总代报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportTopAgentStaticsResult> listPage(@RequestBody ReportTopAgentStaticsPageVO reportTopAgentStaticsPageVO);


    /**
     * 商务总代报表数据初始化
     * @return 成功失败
     */
    @Operation(summary = "商务总代报表数据初始化")
    @PostMapping(value = PREFIX + "/init")
    ResponseVO<Boolean> init(@RequestBody ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO);



}
