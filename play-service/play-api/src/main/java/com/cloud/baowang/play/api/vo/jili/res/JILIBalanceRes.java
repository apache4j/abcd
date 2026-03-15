package com.cloud.baowang.play.api.vo.jili.res;

import com.cloud.baowang.play.api.vo.jili.JILIBaseRes;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class JILIBalanceRes {
    String username;
    String currency;
    BigDecimal balance;
    Long timestamp;
}
