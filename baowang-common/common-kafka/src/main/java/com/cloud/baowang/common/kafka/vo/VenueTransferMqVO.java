package com.cloud.baowang.common.kafka.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "转账报表-MQ参数")
public class VenueTransferMqVO {
    @Schema(title = "钱包名称")
    private String walletName;
    @Schema(title = "游戏平台code")
    private String venueCode;
    @Schema(title = "收支类型 1收入 2支出")
    private String balanceType;
    @Schema(title = "金额")
    private BigDecimal amount;
    @Schema(title = "日期")
    private Long day;
}
