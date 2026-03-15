package com.cloud.baowang.play.api.vo.third;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferReqVO {
    /**
     * 账户名
     */
    private String userAccount;
    /**
     * 场馆平台
     */
    private String venuePlatform;
    /**
     * 场馆code
     */
    private String venueCode;
    /**
     * 转账金额
     */
    private BigDecimal transferAmount;
}
