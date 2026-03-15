package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ford
 * @Date 2024-11-04
 */
@FeignClient(contextId = "remoteReportAgentStatics", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理报表")
public interface ReportAgentStaticsApi {

    String PREFIX = ApiConstants.PREFIX + "/reportAgentStatics/api";

    @Operation(summary = "代理报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportAgentStaticsResult> listPage(@RequestBody ReportAgentStaticsPageVO reportAgentStaticsPageVO);


    /**
     * 代理报表数据初始化
     * @return 成功失败
     */
    @Operation(summary = "代理报表数据初始化")
    @PostMapping(value = PREFIX + "/init")
    ResponseVO<Boolean> init(@RequestBody ReportAgentStaticsCondVO reportAgentStaticsCondVO);



}
