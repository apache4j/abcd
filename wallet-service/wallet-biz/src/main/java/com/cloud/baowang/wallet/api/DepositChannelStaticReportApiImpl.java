package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.DepositChannelStaticReportApi;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportRespVO;
import com.cloud.baowang.wallet.service.DepositChannelReportService;
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
public class DepositChannelStaticReportApiImpl implements DepositChannelStaticReportApi {

    private final DepositChannelReportService depositChannelReportService;

    @Override
    public ResponseVO<DepositChannelStaticReportRespVO> getDataReportPage(DepositChannelStaticReportReqVO depositChannelStaticReportReqVO) {
        return ResponseVO.success(depositChannelReportService.staticDepositChannelReport(depositChannelStaticReportReqVO));
    }
}
