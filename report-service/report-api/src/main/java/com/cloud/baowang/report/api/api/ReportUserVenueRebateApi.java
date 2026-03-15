package com.cloud.baowang.report.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "reportUserVenueRebateApi", value = ApiConstants.NAME)
@Tag(name = "RPC 佣金计算")
public interface ReportUserVenueRebateApi {

    String PREFIX = ApiConstants.PREFIX + "/ReportUserVenueRebateApi/api/";

    @Operation(summary = "佣金计算 ")
    @PostMapping(value = PREFIX+"onRebateTaskArrived")
    ResponseVO<?> onAgentCommissionTaskBegin(@RequestBody ReportRecalculateVO reqVo);
}
