package com.cloud.baowang.report.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportDailyWinLoseApi;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResult;
import com.cloud.baowang.report.service.ReportUserWinLoseService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class ReportDailyWinLoseApiImpl implements ReportDailyWinLoseApi {

    private final ReportUserWinLoseService reportUserWinLoseService;


    @Override
    public ResponseVO<DailyWinLoseResult> dailyWinLosePage(DailyWinLosePageVO vo) {
        return ResponseVO.success(reportUserWinLoseService.dailyWinLosePage(vo));
    }

    @Override
    public ResponseVO<Long> dailyWinLosePageCount(DailyWinLosePageVO vo) {
        return ResponseVO.success(reportUserWinLoseService.dailyWinLosePageCount(vo));
    }
}
