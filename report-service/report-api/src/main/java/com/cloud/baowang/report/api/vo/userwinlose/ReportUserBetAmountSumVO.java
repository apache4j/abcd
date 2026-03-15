package com.cloud.baowang.report.api.vo.userwinlose;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 23:14
 * @description:
 */
@Data
public class ReportUserBetAmountSumVO {
    private String userId;
    private String currency;
    private BigDecimal validAmount;
    private BigDecimal winLossAmount;
}
