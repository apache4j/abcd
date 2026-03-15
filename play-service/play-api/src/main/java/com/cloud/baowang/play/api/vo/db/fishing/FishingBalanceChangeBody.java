package com.cloud.baowang.play.api.vo.db.fishing;

import lombok.Builder;
import lombok.Data;

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
