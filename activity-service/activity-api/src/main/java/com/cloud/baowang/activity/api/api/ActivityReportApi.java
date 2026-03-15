package com.cloud.baowang.activity.api.api;

import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.report.DataReportReqVO;
import com.cloud.baowang.activity.api.vo.report.DataReportRespVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 活动报表相关
 */
@FeignClient(contextId = "activityReportApi", value = ApiConstants.NAME)
@Tag(name = "活动报表-接口")
public interface ActivityReportApi {

    String PREFIX = ApiConstants.PREFIX +"/"+ApiConstants.PATH+ "/activityReportApi/api/";

    @Operation(summary = "数据报表")
    @PostMapping(PREFIX + "getDataReportPage")
    ResponseVO<DataReportRespVO> getDataReportPage(@RequestBody DataReportReqVO dataReportReqVO);
}
