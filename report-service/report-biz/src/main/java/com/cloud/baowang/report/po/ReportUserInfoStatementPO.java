package com.cloud.baowang.report.po;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableName;

import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员报表
 */
@Data
@Accessors(chain = true)
@TableName("report_user_info_statement")
public class ReportUserInfoStatementPO extends SiteBasePO implements Serializable {
    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;

    /**
     * 会员id
     */
    @Schema(title = "会员id")
    private String userId;
    /**
     * 会员账号
     */
    @Schema(title = "会员账号")
    private String userAccount;

    /**
     * 上级代理id
     */
    @Schema(title = "上级代理id")
    private String superAgentId;

    /**
     * 上级代理账号
     */
    @Schema(title = "上级代理账号")
    private String superAgentAccount;
    /**
     * 代理归属 1推广 2招商 3官资"
     */
    @Schema(title = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    /**
     * 总存款
     */
    @Schema(title = "总存款")
    private BigDecimal totalDeposit;
    /**
     * 存款次数
     */
    @Schema(title = "存款次数")
    private Integer numberDeposit;
    /**
     * 大额存款次数
     */
    @Schema(title = "大额存款次数")
    private Integer numberLargeDeposits;

    /**
     * 大额存款总额 amount_large_deposits
     */
    @Schema(title = "大额存款总额")
    private BigDecimal amountLargeDeposits;


    /**
     * 上级转入
     */
    @Schema(title = "上级转入")
    private BigDecimal advancedTransfer;

    public BigDecimal getAdvancedTransfer() {
        if (ObjectUtil.isEmpty(advancedTransfer)) {
            advancedTransfer = BigDecimal.valueOf(0);
        }
        return advancedTransfer;
    }

    /**
     * 上级转入次数
     */
    @Schema(title = "上级转入次数")
    private Integer numberTransfer;
    /**
     * 转中心钱包次数
     */
    /*@Schema(title = "转中心钱包次数")
    private Integer centralWallet;*/
    /**
     * 转回次数
     */
    /*@Schema(title = "转回次数")
    private Integer numberReversals;*/
    /**
     * 总取款
     */
    @Schema(title = "总取款")
    private BigDecimal totalWithdrawal;
    /**
     * 取款次数
     */
    @Schema(title = "取款次数")
    private Integer numberWithdrawal;
    /**
     * 大额取款次数 number_large_withdrawal
     */
    @Schema(title = "大额取款次数")
    private Integer numberLargeWithdrawal;

    /**
     * 大额取款总额 amount_large_withdrawal
     */
    @Schema(title = "大额取款总额")
    private BigDecimal amountLargeWithdrawal;
    /**
     * 存取差
     */
    @Schema(title = "存取差")
    private BigDecimal poorAccess;
    /**
     * 会员活动人工加减额 - 没有
     */
    /*@Schema(title = "会员活动人工加减额")
    private BigDecimal memberLabourAmount;*/
    /**
     * 会员VIP人工加减额 - 没有
     */
    /*@Schema(title = "会员VIP人工加减额")
    private BigDecimal memberVipLabourAmount;*/
    /**
     * 总返水
     */
    @Schema(title = "总返水")
    private BigDecimal rebateAmount;
    /**
     * 其他调整
     */
    @Schema(title = "其他调整")
    private BigDecimal otherAdjustments;
    /**
     * 注单量
     */
    @Schema(title = "注单量")
    private Integer placeOrderQuantity;
    /**
     * 投注金额
     */
    @Schema(title = "投注金额")
    private BigDecimal betAmount;
    /**
     * 有效投注金额
     */
    @Schema(title = "有效投注金额")
    private BigDecimal activeBet;
    /**
     * 投注盈亏
     */
    @Schema(title = "会员输赢")
    private BigDecimal bettingProfitLoss;
    /**
     * 投注盈亏 - new
     */
    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;
    /**
     * 转代次数
     */
    @Schema(title = "转代次数")
    private Integer transAgentTime;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    private BigDecimal activityAmount;

    /**
     * 优惠金额
     */
    @Schema(title = "vip福利")
    private BigDecimal vipAmount;


    /**
     * 优惠金额
     */
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount;

    /**
     * 主货币
     */
    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount;

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount;

}
