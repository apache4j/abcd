package com.cloud.baowang.play.wallet.vo.req.pt2.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Balance {
    private BigDecimal real;
    private String timestamp;
}
