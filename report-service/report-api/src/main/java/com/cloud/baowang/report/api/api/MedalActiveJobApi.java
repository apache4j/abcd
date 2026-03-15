package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteMedalActiveApi", value = ApiConstants.NAME)
@Tag(name = "RPC 勋章激活任务api")
public interface MedalActiveJobApi {

    String PREFIX = ApiConstants.PREFIX + "/medal-active/api";


    @Operation(summary = "勋章每月 激活任务")
    @PostMapping(value = PREFIX + "/siteMedalMonthJob")
    ResponseVO<Void> siteMedalActiveMonthJob(@RequestParam String siteCode);

    @Operation(summary = "勋章每周 激活任务")
    @PostMapping(value = PREFIX + "/siteMedalWeekJob")
    ResponseVO<Void> siteMedalActiveWeekJob(@RequestParam String siteCode);
}
