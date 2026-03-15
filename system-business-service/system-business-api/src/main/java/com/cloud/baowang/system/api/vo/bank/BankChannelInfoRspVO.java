package com.cloud.baowang.system.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行卡管理返回vo")
public class BankChannelInfoRspVO {
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "通道代码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "银行通道状态（1-全量 2-非全量 3-未配置）")
    private String bankChannelStatus;

    @Schema(description = "银行通道状态-中文显示")
    private String bankChannelStatusText;


    @Schema(description = "修改时间（时间戳）")
    private Long updatedTime;


    @Schema(description = "修改人")
    private String updater;

}
