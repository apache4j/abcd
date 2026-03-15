package com.cloud.baowang.report.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResult;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteReportDailyWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "RPC 每日盈亏报表")
public interface ReportDailyWinLoseApi {

    String PREFIX = ApiConstants.PREFIX + "/reportDailyWinLoseApi/api";


    @Operation(summary = "每日盈亏报表列表")
    @PostMapping(value = PREFIX + "/dailyWinLosePage")
    ResponseVO<DailyWinLoseResult> dailyWinLosePage(@RequestBody DailyWinLosePageVO vo);


    @Operation(summary = "每日盈亏报表总数")
    @PostMapping(value = PREFIX + "/dailyWinLosePageCount")
    ResponseVO<Long> dailyWinLosePageCount(@RequestBody DailyWinLosePageVO vo);


}
