package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import lombok.Data;

import java.math.BigDecimal;


@Data
@TableName("user_activity_typing_amount")
public class UserActivityTypingAmountPO {

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
     * 存款活动结束时间
     */
    private Long startTime;


    /**
     * 存款活动结束时间
     */
    private Long endTime;


    /**
     * 限制游戏
     * 对应枚举 {@link VenueTypeEnum}
     */
    private String limitGameType;


}
