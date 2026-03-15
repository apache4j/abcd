package com.cloud.baowang.play.api.vo.cq9.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @className: EventData
 * @author: wade
 * @description: 事件数据对象，包含每个事件的详细信息
 * @date: 21/2/25 18:56
 */
@Data
public class EventData {
    /**
     * 交易码，唯一值
     */
    private String mtcode;

    /**
     * 金额，实际给玩家钱包的金额，不能为负值
     */
    private BigDecimal amount;

    /**
     * 事件时间，格式为 RFC3339
     */
    private String eventtime;

    /**
     * 有效投注金额，老虎机/街机/鱼机有效投注为0
     */
    private Integer validbet;
}
