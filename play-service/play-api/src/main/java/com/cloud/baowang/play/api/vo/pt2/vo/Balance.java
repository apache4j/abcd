package com.cloud.baowang.play.api.vo.pt2.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Balance {
    private BigDecimal real;
    private String timestamp;
}
