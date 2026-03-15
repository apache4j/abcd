package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ford
 * @Date 2024-11-04
 */
@FeignClient(contextId = "remoteReportAgentDepositWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理充提报表")
public interface ReportAgentDepositWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/reportAgentDepositWithdraw/api";

    @Operation(summary = "代理充提报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportAgentDepositWithdrawResult> listPage(@RequestBody ReportAgentDepositWithdrawPageVO reportAgentDepositWithdrawPageVO);


    /**
     * 代理充提报表数据初始化
     * @return 成功失败
     */
    @Operation(summary = "代理充提报表数据初始化")
    @PostMapping(value = PREFIX + "/init")
    ResponseVO<Boolean> init(@RequestBody ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO);



}
