package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "通道")
public class SiteWithdrawChannelSingleNewRequestVO {

    @Schema(description = "通道编号")
    private String channelId;
    @Schema(description = "提款方式id")
    private String withdrawWayId;
}
