package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportRespVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.WithdrawChannelStaticReportRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 提现渠道报表
 */
@FeignClient(contextId = "remoteWithdrawChannelStaticReportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 提现渠道报表 服务")
public interface WithdrawChannelStaticReportApi {

    String PREFIX = ApiConstants.PREFIX + "/withdrawChannelStaticReportApi/api/";

    @Operation(summary = "提现渠道报表查询")
    @PostMapping(value = PREFIX + "getDataReportPage")
    ResponseVO<WithdrawChannelStaticReportRespVO> getDataReportPage(@RequestBody WithdrawChannelStaticReportReqVO withdrawChannelStaticReportReqVO);


}
