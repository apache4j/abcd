package com.cloud.baowang.report.api.vo.userwinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/5 14:15
 * @Version: V1.0
 **/
@Data
public class UserWinLoseListResponseVO {
    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;

    private String dayStr;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "上级代理Id")
    private String agentId;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;

    @Schema(title = "下级代理层级")
    private String path;

    @Schema(title = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(title = "注单量")
    private Integer betNum;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "流水纠正")
    private BigDecimal runWaterCorrect;

    @Schema(title = "投注盈亏")
    private BigDecimal betWinLose;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(title = "优惠金额")
    private BigDecimal activityAmount;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount;

    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount;

    @Schema(title = "补单其他调整")
    private BigDecimal repairOrderOtherAdjust;

    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;

    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount;

    @Schema(title = "站点Code")
    private String siteCode;

    @Schema(title = "主货币")
    private String mainCurrency;


    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount;

    @Schema(title = "打赏金额-主货币")
    private BigDecimal tipsAmount;

    @Schema(title = "风控金额-主货币")
    private BigDecimal riskAmount;
}
