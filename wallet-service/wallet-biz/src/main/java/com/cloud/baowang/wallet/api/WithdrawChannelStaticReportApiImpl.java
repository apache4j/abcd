package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.WithdrawChannelStaticReportApi;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportRespVO;
import com.cloud.baowang.wallet.service.WithdrawChannelReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/10 20:12
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class WithdrawChannelStaticReportApiImpl implements WithdrawChannelStaticReportApi {

    private final WithdrawChannelReportService withdrawChannelReportService;

    @Override
    public ResponseVO<WithdrawChannelStaticReportRespVO> getDataReportPage(WithdrawChannelStaticReportReqVO withdrawChannelStaticReportReqVO) {
        return ResponseVO.success(withdrawChannelReportService.staticWithdrawChannelReport(withdrawChannelStaticReportReqVO));
    }
}
