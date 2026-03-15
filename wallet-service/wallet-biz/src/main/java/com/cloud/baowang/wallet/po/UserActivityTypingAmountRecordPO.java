package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import lombok.Data;

import java.math.BigDecimal;


@Data
@TableName("user_activity_typing_amount_record")
public class UserActivityTypingAmountRecordPO {

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
     * 账号类型(1-测试,2-正式)
     */
    private String accountType;


    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 币种
     */
    private String currency;

    /**
     * 流水类型对应枚举 {@link TypingAmountAdjustTypeEnum}
     */
    private String adjustType;
    /**
     * 打码量
     */
    private BigDecimal coinValue;

    /**
     * 打码量(变动前)
     */
    private BigDecimal coinFrom;


    /**
     * 打码量(变动后)
     */
    private BigDecimal coinTo;


    /**
     * 插入时间
     */
    private Long createdTime;


    /**
     * 增减类型(1-增加,2-减少)
     */
    private String adjustWay;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 创建人
     */
    private String creator;

    /**
     * mq消息id
     */
    private String msgId;


    /**
     * 备用字段
     */
    private String exId1;

    /**
     * 备用字段
     */
    private String exId2;

    /**
     * 备用字段
     */
    private String exId3;
}
