package com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundChange {

    //REAL: 真钱余额。 //BONUS: 奖池余额。
    private String type;

    //余额变动金额
    private BigDecimal amount;
}
