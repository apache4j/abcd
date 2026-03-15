package com.cloud.baowang.report.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawDayReqParam;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawRequestVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteReportUserDepositWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存取报表")
public interface ReportUserDepositWithdrawApi {
    String PREFIX = ApiConstants.PREFIX + "/reportUserDepositWithdrawApi/api";


    @PostMapping(PREFIX +"/listUserReportDepositWithdrawPage")
    @Operation(summary = "存取报表分页列表")
    ResponseVO<ReportUserDepositWithdrawResponseVO> listReportDepositWithdrawPage(@RequestBody ReportUserDepositWithdrawRequestVO reportDepositWithdrawRequestVO);


    @PostMapping(PREFIX +"/reportUserDepositWithdrawDay")
    @Operation(summary = "生成存取报表")
    void reportUserDepositWithdrawDay(@RequestBody ReportUserDepositWithdrawDayReqParam param);

    @PostMapping(PREFIX +"/userReportDepositWithdrawPageCount")
    @Operation(summary = "会员存取报表计数")
    ResponseVO<Long> userReportDepositWithdrawPageCount(@RequestBody ReportUserDepositWithdrawRequestVO vo);

}
