package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 打码量记录表
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_typing_amount_record")
public class UserTypingAmountRecordPO {

    private Long id;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 会员ID
     */
    private String userAccount;

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号类型
     */
    private String accountType;
    /**
     * 币种
     */
    private String currency;
    /**
     * vip等级CODE
     */
    private Integer vipRankCode;
    /**
     * vip等级CODE名称
     */
    private String vipRankCodeName;
    /**
     * 调整方式 1增加 2扣除
     */
    private String adjustWay;
    /**
     * 调整类型 1人工增加流水 2人工清除流水 3系统自动清除 4投注扣减流水 5活动增加流水 6充值添加流水 7返水添加流水 8VIP奖励添加流水
     */
    private String adjustType;
    /**
     * 调整流水
     */
    private BigDecimal coinValue;
    /**
     * 调整前流水
     */
    private BigDecimal coinFrom;
    /**
     * 调整后流水
     */
    private BigDecimal coinTo;
    /**
     * 创建时间
     */
    private Long createdTime;
    /**
     * 备注
     */
    private String remark;

    /**
     * mq消息id
     */
    private String msgId;
}

