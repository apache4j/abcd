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
@Schema(title = "场馆盈亏报表-MQ参数")
public class VenueWinLossMqVO {
    @Schema(title = "项目类型  0 游戏 1 打赏")
    private Integer type;
    @Schema(title = "用户账号")
    private String userAccount;
    @Schema(title ="游戏平台code")
    private String venueCode;
    @Schema(title = "投注金额")
    private BigDecimal betAmount;
    @Schema(title = "有效投注")
    private BigDecimal validAmount;
    @Schema(title = "投注盈亏")
    private BigDecimal winLossAmount;
    @Schema(title = "日期")
    private Long day;
    /**
     * 订单状态-结算
     */
    @Schema(title = "订单状态")
    private Integer orderStatus;
    @Schema(title = "是否首次入库 0 否 1 是")
    private boolean saveFirst;

    private String orderId;

    /**
     * 投注人数
     */
    private Integer bettors;

    private Integer betCount;

    private String messageId;

    //消息类型  1 投注  2 结算
    private Integer messageType;
}
