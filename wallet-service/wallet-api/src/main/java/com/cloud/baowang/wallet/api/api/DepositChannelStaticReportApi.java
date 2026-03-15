package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositChannelStaticReportRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 充值渠道报表
 */
@FeignClient(contextId = "remoteDepositChannelStaticReportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 充值渠道报表 服务")
public interface DepositChannelStaticReportApi {

    String PREFIX = ApiConstants.PREFIX + "/depositChannelStaticReportApi/api/";

    @Operation(summary = "充值渠道报表查询")
    @PostMapping(value = PREFIX + "getDataReportPage")
    ResponseVO<DepositChannelStaticReportRespVO> getDataReportPage(@RequestBody DepositChannelStaticReportReqVO depositChannelStaticReportReqVO);


}
