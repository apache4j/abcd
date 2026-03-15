package com.cloud.baowang.wallet.api.vo.userTypingAmount;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UserTypingAmountVO {
    private Long id;

    /**
     * 会员ID
     */
    private String userId;




    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 币种
     */
    private String currency;


    /**
     * 打码量
     */
    private BigDecimal typingAmount;

    /**
     * 流水开始统计时间
     */
    private Long startTime;

}
