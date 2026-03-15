package com.cloud.baowang.wallet.api.vo.mq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户注单结算消息实体")
public class UserGamePayoutVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "游戏平台CODE")
    private String venueCode;

    @Schema(description = "投注额")
    private BigDecimal betAmount;

    @Schema(title = "派彩金额")
    private BigDecimal payoutAmount;

    @Schema(title = "三方注单ID")
    private String thirdOrderId;

    @Schema(description = "注单归类")
    private Integer orderClassify;


}