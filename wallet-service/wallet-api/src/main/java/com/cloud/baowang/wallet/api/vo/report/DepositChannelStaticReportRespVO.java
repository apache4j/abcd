package com.cloud.baowang.wallet.api.vo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/10 20:06
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值渠道报表查询条件")
@I18nClass
public class DepositChannelStaticReportRespVO {

    @Schema(description = "充值渠道分页报表")
    private Page<DepositChannelDataReportRespVO> depositChannelDataReportRespVOPage;
    @Schema(description = "充值渠道当前页汇总")
    private DepositChannelDataReportRespVO currentDataRespVO;
    @Schema(description = "充值渠道所有数据汇总")
    private DepositChannelDataReportRespVO allDataRespVO;

}
