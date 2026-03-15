package com.cloud.baowang.play.api.vo.pt2.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NotifyBonusEventVO {
    private String requestId;

    private String username;

    //运营商端的奖金实例标识，用于触发事件通知
    private String remoteBonusCode;

    //Playtech端的奖金实例标识，用于触发事件通知
    private String bonusInstanceCode;

    //ACCEPTED，REMOVED
    private String resultingStatus;

    private String date;

    //已经发放或者移除的金额
    private BigDecimal bonusBalanceChange;

    //通知已发放或者已移除的次数
    private Long freeSpinsChange;

    //已发放或已移除的金色筹码。
    private GoldenChipsChange goldenChipsChange;

    private String bonusTemplateId;

    //单个免费旋转的金额
    private BigDecimal freeSpinValue;
}
