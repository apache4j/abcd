package com.cloud.baowang.play.api.vo.db.evg.vo;

import lombok.Data;

@Data
public class BalanceChangeBody {
    private Integer gameId;

    private Long tradeAmount;

    /** 注单id */
    private String betId;

    /** 关联交易 ID(取消关联的原交易订单 ID) */
    private String refTradeId;

    /** 交易订单 ID(唯一交易编号) */
    private String tradeId;

    /** 交易类型(1 下注, 2 派彩, 3 取消, 4 活动) 5.4 交易类型说明 */
    private Integer tradeType;

    private String memberId;

    private String currency;

    private String orderId;
}
