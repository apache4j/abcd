package com.cloud.baowang.report.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementPageVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.api.vo.UserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.task.ReportTaskReportPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteReportUserInfoStatementApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员报表")
public interface ReportUserInfoStatementApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteReportUserInfoStatementApi/api";


    @Operation(summary = "会员报表列表")
    @PostMapping(value = PREFIX + "/pageList")
    ResponseVO<UserInfoStatementResponseVO> pageList(@RequestBody ReportUserInfoStatementPageVO vo);

    @Operation(summary = "会员报表列表-总记录数")
    @PostMapping(value = PREFIX + "/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody ReportUserInfoStatementPageVO vo);

    @Operation(summary = "根据会员账号查询")
    @PostMapping(value = PREFIX + "/pageListUserAccount")
    ResponseVO<Page<ReportUserInfoStatementResponseVO>> pageListUserAccount(@RequestBody ReportUserInfoStatementPageVO vo);


    @Operation(summary = "新增会员报表")
    @PostMapping(value = PREFIX + "/saveReportUserInfoStatement")
    void saveReportUserInfoStatement(@RequestBody ReportUserInfoStatementSyncVO vo);


}
