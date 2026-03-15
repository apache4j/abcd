package com.cloud.baowang.play.api.vo.dg2.req;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Member {
    private String username;
    private BigDecimal balance;
    private BigDecimal amount;
}
