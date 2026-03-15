package com.cloud.baowang.wallet.api.vo.bank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "币种对应通道返回vo")
public class ChannelInfoRspVO {

    @Schema(description = "银行-code")
    private String id;

    @Schema(description = "银行名称")
    private String channelName;

    @Schema(description = "银行编码")
    private String channelCode;

    
}
