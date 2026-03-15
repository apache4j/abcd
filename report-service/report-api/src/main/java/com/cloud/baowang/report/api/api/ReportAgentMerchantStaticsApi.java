package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author ford
 * @Date 2024-11-04
 */
@FeignClient(contextId = "remoteReportAgentMerchantStatics", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 商务报表")
public interface ReportAgentMerchantStaticsApi {

    String PREFIX = ApiConstants.PREFIX + "/merchantReport/api";

    @Operation(summary = "商务报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportAgentMerchantStaticsResult> listPage(@RequestBody ReportAgentMerchantStaticsPageVO reportAgentMerchantStaticsPageVO);


    /**
     * 商务报表数据初始化
     * @return 成功失败
     */
    @Operation(summary = "商务报表数据初始化")
    @PostMapping(value = PREFIX + "/init")
    ResponseVO<Boolean> init(@RequestBody ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO);



}
