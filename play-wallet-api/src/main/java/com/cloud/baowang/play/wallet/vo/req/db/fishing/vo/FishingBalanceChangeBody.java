package com.cloud.baowang.play.wallet.vo.req.db.fishing.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FishingBalanceChangeBody {
    private String memberId;
    private Integer tradeType;
    //注单id
    private Long betId;
    private Long tradeAmount;

    //订单号
    private String orderId;


}
