/**
 * @(#)SgTranferRsp.java, 10月 20, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <h2></h2>
 *
 */
@Data
public class AceLtTransfer {

    /**
     * 转账的订单号
     */
    private String transactionId;
    /**
     * 转换类型，0表示转入游戏，1表示转出游戏
     */
    private Integer type;
    /**
     * 转换金额
     */
    private BigDecimal change;
    /**
     * 发生时间
     */
    private String time;
}
