package com.cloud.baowang.play.wallet.vo.req.dg;

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
