package com.cloud.baowang.play.wallet.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeBalanceChangeVO implements Serializable {
    private String siteCode;
    private String userId;
    private BigDecimal amount;
}
