package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data

@TableName("user_typing_amount")
public class UserTypingAmountPO {

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
     * 站点CODE
     */
    private String siteCode;

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
