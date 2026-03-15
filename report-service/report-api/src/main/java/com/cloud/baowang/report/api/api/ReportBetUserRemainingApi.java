package com.cloud.baowang.report.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.report.ReportBetUserRemainingResVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(contextId = "reportBetUserRemainingApi", value = ApiConstants.NAME)
@Tag(name = "留存报表相关接口api")
public interface ReportBetUserRemainingApi {

    String PREFIX = ApiConstants.PREFIX + "/reportBetUserRemainingApi/api/";

    @Operation(summary = "留存报表汇总接口")
    @PostMapping(PREFIX + "calculate")
    void calculate(ReportRecalculateVO recalculateVO);

    @Operation(summary = "留存报表分页查询")
    @PostMapping(PREFIX + "pageList")
    ResponseVO<Page<ReportBetUserRemainingResVO>> pageList();

}
