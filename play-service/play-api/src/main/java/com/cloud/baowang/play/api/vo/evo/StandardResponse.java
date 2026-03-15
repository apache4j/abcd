package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * StandardResponse
 * 标准响应结构
 */
@Data
public class StandardResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求状态
     * Describes status of request
     */
    private String status;

    /**
     * 玩家真实余额（不包含奖金）
     * Player's balance value (real money, excluding bonus)
     */
    private BigDecimal balance;

    /**
     * 玩家奖金余额（可选）
     * Player's bonus balance
     */
    private BigDecimal bonus;

    /**
     * 是否为重传（可选）
     * true if response is a retransmission of original response
     */
    private Boolean retransmission;

    /**
     * 唯一响应 ID（可选）
     * Unique response id
     */
    private String uuid;

}


