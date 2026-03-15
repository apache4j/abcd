package com.cloud.baowang.play.api.vo.pt2.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubJackpotContribution {

    private BigDecimal amount;
    private String jackpotId;
}
