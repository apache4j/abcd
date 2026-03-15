package com.cloud.baowang.report.api.vo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员报表返回Response VO")
public class ReportUserInfoStatementResponseVO {
    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(title = "站点日期 当天起始时间戳")
    private Long dayMillis;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "上级代理id")
    private String superAgentId;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;


    @Schema(title = "总存款")
    private BigDecimal totalDeposit = BigDecimal.ZERO;

    @Schema(title = "存款次数")
    private Integer numberDeposit = 0;

    @Schema(title = "上级转入")
    private BigDecimal advancedTransfer;

    public BigDecimal getAdvancedTransfer() {
        if (ObjectUtil.isEmpty(advancedTransfer)) {
            advancedTransfer = BigDecimal.valueOf(0);
        }
        return advancedTransfer;
    }

    @Schema(title = "上级转入次数")
    private Integer numberTransfer = 0;

    @Schema(title = "转中心钱包次数")
    private Integer centralWallet = 0;

    @Schema(title = "转回次数")
    private Integer numberReversals = 0;

    @Schema(title = "总取款")
    private BigDecimal totalWithdrawal = BigDecimal.ZERO;

    @Schema(title = "取款次数")
    private Integer numberWithdrawal = 0;

    @Schema(title = "大额取款次数")
    private Integer numberLargeDeposits = 0;

    @Schema(title = "存取差")
    private BigDecimal poorAccess = BigDecimal.ZERO;

    @Schema(title = "总优惠")
    private BigDecimal totalPreference = BigDecimal.ZERO;

    @Schema(title = "总返水")
    private BigDecimal grossRecoil = BigDecimal.ZERO;

    @Schema(title = "其他调整")
    private BigDecimal otherAdjustments = BigDecimal.ZERO;

    @Schema(title = "注单量")
    private Integer placeOrderQuantity = 0;

    @Schema(title = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "有效投注金额")
    private BigDecimal activeBet = BigDecimal.ZERO;

    @Schema(title = "投注盈亏")
    private BigDecimal bettingProfitLoss = BigDecimal.ZERO;

    @Schema(title = "转代次数")
    private Integer transAgentTime = 0;
    // 添加的字段
    @Schema(title = "会员活动人工加减额")
    private BigDecimal memberLabourAmount = BigDecimal.ZERO;

    // 添加的字段
    @Schema(title = "会员VIP人工加减额")
    private BigDecimal memberVipLabourAmount = BigDecimal.ZERO;

    // 添加的字段
    @Schema(title = "活动金额")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    // 添加的字段
    @Schema(title = "VIP福利金额")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    // 添加的字段
    @Schema(title = "已使用优惠金额")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @TableField(value = "统计日期")
    private Long createdTime;

    /**
     * 统计日期就dayMillis。
     */
    public Long getCreatedTime() {
        return dayMillis;
    }
}
