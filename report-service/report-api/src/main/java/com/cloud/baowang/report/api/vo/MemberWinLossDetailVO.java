/**
 * @(#)AgentBelongUserCount.java, 10月 25, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/25
 */
@Data
@Schema(title = "代理会员盈亏明细返回")
public class MemberWinLossDetailVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "主货币")
    private String currency;

    @Schema(title = "投注额")
    private BigDecimal betAmount;

    @Schema(title = "输赢金额")
    private BigDecimal winLossAmount;

    @Schema(title = "有效投注")
    private BigDecimal validAmount;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(title = "注单量")
    private Integer betNumber;

    @Schema(title = "优惠金额")
    private BigDecimal activityAmount;
    @Schema(title = "vip福利")
    private BigDecimal vipAmount;
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount;


    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount;

    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理账号Id")
    private String agentId;

    /**
     * bet 时间就是dayHourTime
     */
    @Schema(title = "投注时间")
    private Long betTime;
    @Schema(title = "投注时间")
    private String siteCode;
    @Schema(title = "主币种")
    private String mainCurrency;

    private long dayMillis;

    /**
     * {@link UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount = BigDecimal.ZERO;

}
