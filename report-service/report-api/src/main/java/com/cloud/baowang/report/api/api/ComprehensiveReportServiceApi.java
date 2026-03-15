package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteComprehensiveReportServiceApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员报表")
public interface ComprehensiveReportServiceApi {
    String PREFIX = ApiConstants.PREFIX + "/comprehensiveReportServiceApi/api";

    @Operation(summary = "生成报表数据")
    @PostMapping(value = PREFIX + "/execute")
    public ResponseVO execute(@RequestBody ReportComprehensiveReportVO vo);

}
