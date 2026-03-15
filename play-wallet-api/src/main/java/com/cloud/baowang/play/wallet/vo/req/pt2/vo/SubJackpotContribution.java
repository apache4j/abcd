package com.cloud.baowang.play.wallet.vo.req.pt2.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubJackpotContribution {

    private BigDecimal amount;
    private String jackpotId;
}
