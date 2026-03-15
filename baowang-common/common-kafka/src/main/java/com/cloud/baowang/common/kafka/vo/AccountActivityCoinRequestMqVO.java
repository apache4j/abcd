package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author: mufan
 * @createTime: 2025/10/25 18:11
 * @description:
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单第一次结算发送的消息实体")
public class AccountActivityCoinRequestMqVO extends MessageBaseVO {

    @Schema(title = "会员账户")
    private String userAccount;

    @Schema(title = "会员id")
    private String userId;

    @Schema(title = "场馆code")
    private String venueCode;

    @Schema(title = "有效投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "我方注单号，且是必填写")
    private String orderId;

    @Schema(title = "siteCode")
    private String siteCode;

    @Schema( description ="币种")
    private String currencyCode;

    @Schema(description = "汇率")
    private BigDecimal finalRate;

}
