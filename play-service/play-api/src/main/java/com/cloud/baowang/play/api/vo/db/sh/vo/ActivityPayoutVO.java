package com.cloud.baowang.play.api.vo.db.sh.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityPayoutVO {

    private BigDecimal payoutAmount;   // 派彩金额，可能为负数
    private Long payoutTime;           // 派彩时间（时间戳）
    private String payoutType;         // 派彩类型，如：DEDUCTION
    private Long transferNo;           // 转账编号
    private String loginName;          // 登录名
    private Long playerId;             // 玩家ID
    private String transferType;       // 转账类型，如：DEDUCTION_ANCHOR
    private Integer hasTransferOut;    // 是否转出，0=否 1=是
    private String currency;           // 币种
}
