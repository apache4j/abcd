package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 19:47
 * @description:
 */
@Data
@Schema(description = "通道查询条件")
public class ChannelQueryReqVO {
    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 通道代码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;
}
