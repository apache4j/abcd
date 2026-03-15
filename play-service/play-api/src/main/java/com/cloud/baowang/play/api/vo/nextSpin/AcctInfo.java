package com.cloud.baowang.play.api.vo.nextSpin;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class AcctInfo {

    /**
     * 用户标识 ID
     */
    private String acctId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 货币的 ISO 代码
     */
    private String currency;
    /**
     * 用户当前余额
     */
    private BigDecimal balance;

    public BigDecimal getBalance() {
        if (balance == null) {
            return null;
        }
        // 保留两位小数，使用四舍五入
        return balance.setScale(2, RoundingMode.DOWN);
    }



}
