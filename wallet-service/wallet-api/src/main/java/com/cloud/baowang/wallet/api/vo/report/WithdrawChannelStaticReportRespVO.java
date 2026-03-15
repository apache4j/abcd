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
@Schema(description = "提现渠道报表查询条件")
@I18nClass
public class WithdrawChannelStaticReportRespVO {

    @Schema(description = "提现渠道分页报表")
    private Page<WithdrawChannelDataReportRespVO> withdrawChannelDataReportRespVOPage;
    @Schema(description = "提现渠道当前页汇总")
    private WithdrawChannelDataReportRespVO currentDataRespVO;
    @Schema(description = "提现渠道所有数据汇总")
    private WithdrawChannelDataReportRespVO allDataRespVO;

}
