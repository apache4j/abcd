package com.cloud.baowang.wallet.api.vo.report;

import com.cloud.baowang.common.core.vo.base.PageVO;
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
public class WithdrawChannelStaticReportReqVO extends PageVO {

    @Schema(description = "开始时间")
    private Long beginTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "通道编码")
    private String channelCode;

    @Schema(description = "通道id")
    private String channelId;

    @Schema(description = "站点编码")
    private String siteCode;
    @Schema(description = "提现类型")
    private String rechargeType;
    @Schema(description = "提现方式")
    private String rechargeWay;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;
    //币种
    private String currencyCode;

}
