package com.cloud.baowang.play.api.vo.pt2.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class JackpotContribution {

    private BigDecimal amount;

    private String jackpotId;

    private List<SubJackpotContribution> subJackpotInfo;
}
