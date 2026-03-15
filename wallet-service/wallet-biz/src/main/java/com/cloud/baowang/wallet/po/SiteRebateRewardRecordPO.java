package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 福利中心-游戏返奖记录表
 *
 * @author mufan
 */
@Data
@TableName("site_rebate_reward_record")
public class SiteRebateRewardRecordPO extends BasePO {
    /**
     * 返奖名称
     */
    private String rebateNameI18nCode;
    /**
     * 返利订单号
     */
    private String orderNo;
    /**
     * 会员Id
     */
    private String userId;
    /**
     * 上级代理id
     */
    private String superAgentId;
    /**
     * 上级代理账号
     */
    private String superAgentAccount;
    /**
     * 失效时间
     */
    private Long invalidTime;
    /**
     * 领取时间
     */
    private Long rewardTime;
    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;
    /**
     * 状态 0:未领取 1:已领取,2已过期
     * {  ActivityReceiveStatusEnum}
     */
    private Integer openStatus;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 币种
     */
    private String siteCode;
}

