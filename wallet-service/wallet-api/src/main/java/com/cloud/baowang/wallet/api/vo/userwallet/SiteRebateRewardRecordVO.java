package com.cloud.baowang.wallet.api.vo.userwallet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 福利中心-游戏返奖记录表
 *
 * @author mufan
 */
@Data
@Schema(title = "平台币兑换记录返回")
public class SiteRebateRewardRecordVO {
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
     * 奖励金额
     */
    private BigDecimal rewardAmount;
    /**
     * 币种
     */
    private String currencyCode;
}

