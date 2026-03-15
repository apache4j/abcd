package com.cloud.baowang.play.api.vo.acelt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ACELTGetBalanceRes extends ACELTBaseReq {

    //用户名
    private String username;

    //用户余额
    private BigDecimal balance;

}
