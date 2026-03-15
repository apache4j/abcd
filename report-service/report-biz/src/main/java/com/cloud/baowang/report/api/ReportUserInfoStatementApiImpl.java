package com.cloud.baowang.report.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserInfoStatementApi;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementPageVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.api.vo.UserInfoStatementResponseVO;
import com.cloud.baowang.report.service.ReportUserInfoStatementService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class ReportUserInfoStatementApiImpl implements ReportUserInfoStatementApi {

    private final ReportUserInfoStatementService reportUserInfoStatementService;

    @Override
    public ResponseVO<UserInfoStatementResponseVO> pageList(ReportUserInfoStatementPageVO vo) {
        return reportUserInfoStatementService.pageList(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(ReportUserInfoStatementPageVO vo) {
        return reportUserInfoStatementService.reportUserCount(vo);
    }

    @Override
    public ResponseVO<Page<ReportUserInfoStatementResponseVO>> pageListUserAccount(ReportUserInfoStatementPageVO vo) {
        return reportUserInfoStatementService.pageListUserAccount(vo);
    }

    @Override
    public void saveReportUserInfoStatement(ReportUserInfoStatementSyncVO vo) {
        reportUserInfoStatementService.saveReportUserInfoStatement(vo);

    }
}
