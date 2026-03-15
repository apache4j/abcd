package com.cloud.baowang.play.api.vo.sba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransHistoryVO {

    /**
     * 交易类型，例如：PlaceBet, Settle
     */
    private String action;

    /**
     * 交易时间
     */
    private String actionDate;

    /**
     * 注单金额
     */
    private BigDecimal stake;

    /**
     * 赔率，例如：-0.95, 0.75
     */
    private BigDecimal odds;

    /**
     * 状态，0 =成功, 1=失败, 2 =搁置
     */
    private Integer status;

    /**
     * 注单结算的金额
     */
    private BigDecimal winlostAmount;

    /**
     * 决胜时间(仅显示日期) (yyyy-MM-dd 00:00:00.000) GMT-4
     */
    private String winlostDate;

    /**
     * 交易记录 id
     */
    private String operationId;

    /**
     * 需增加在玩家的金额
     */
    private BigDecimal creditAmount;

    /**
     * 需从玩家扣除的金额
     */
    private BigDecimal debitAmount;

    /**
     * 若此交易还在请求重试，返回 True; 否则返回 False
     */
    private boolean inRetry;



}
