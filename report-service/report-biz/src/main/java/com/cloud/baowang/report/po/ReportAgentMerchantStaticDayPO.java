package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;


/**
 * 商务报表每日
 *
 * @author ford
 * @since 2025-02-10
 */

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_agent_merchant_static_day")
@Schema(title = "商务报表")
public class ReportAgentMerchantStaticDayPO extends BasePO {

    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(title = "统计日期")
    private Long dayMillis;

    @Schema(title = "报表统计类型 0:日报 1:月报")
    private String reportType;

    @Schema(title = "报表统计日期 天或者月 yyyy-MM-dd")
    private String reportDate;

    @Schema(title = "站点Code")
    private String siteCode;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(title = "注册时间")
    private Long registerTime;


    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(title = "直属总代人数")
    private Long directAgentNum;
    @Schema(title = "团队代理人数")
    private Long teamAgentNum;

    @Schema(title = "注册人数")
    private Long registerUserNum;
    @Schema(title = "首存人数")
    private Long firstDepositNum;
    @Schema(title = "首存转换率=首存人数 / 注册人数")
    private BigDecimal firstDepositRate;
    @Schema(title = "投注单数")
    private Long betUserNum;

    @Schema(title = "币种")
    private String currencyCode;

    @Schema(title = "存款金额")
    private BigDecimal depositAmount;

    @Schema(title = "取款金额")
    private BigDecimal withdrawAmount;

    @Schema(title = "存提手续费 主货币")
    private BigDecimal depositWithdrawFee;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "有效投注")
    private BigDecimal validAmount;

    @Schema(title = "会员输赢")
    private BigDecimal winLossAmountUser;
    @Schema(title = "平台总输赢")
    private BigDecimal winLossAmountPlat;

    @Schema(title = "已经使用优惠 主货币")
    private BigDecimal alreadyUseAmount;

    @Schema(title = "打赏金额-主货币")
    private BigDecimal tipsAmount;
    @Schema(title = "调整金额(其他调整)-主货币")
    private BigDecimal adjustAmount;


    public Long getTeamAgentNum() {
        return this.teamAgentNum==null?0L:this.teamAgentNum;
    }

    public Long getDirectAgentNum() {
        return this.directAgentNum==null?0L:this.directAgentNum;
    }

    public Long getRegisterUserNum() {
        return this.registerUserNum==null?0L:this.registerUserNum;
    }

    public Long getFirstDepositNum() {
        return this.firstDepositNum==null?0L:this.firstDepositNum;
    }

    public BigDecimal getFirstDepositRate() {
        return this.firstDepositRate==null?BigDecimal.ZERO:this.firstDepositRate;
    }

    public Long getBetUserNum() {
        return this.betUserNum==null?0L:this.betUserNum;
    }

    public BigDecimal getDepositAmount() {
        return this.depositAmount==null?BigDecimal.ZERO:this.depositAmount;
    }

    public BigDecimal getWithdrawAmount() {
        return this.withdrawAmount==null?BigDecimal.ZERO:this.withdrawAmount;
    }

    public BigDecimal getDepositWithdrawFee() {
        return this.depositWithdrawFee==null?BigDecimal.ZERO:this.depositWithdrawFee;
    }

    public BigDecimal getBetAmount() {
        return this.betAmount==null?BigDecimal.ZERO:this.betAmount;
    }

    public BigDecimal getValidAmount() {
        return this.validAmount==null?BigDecimal.ZERO:this.validAmount;
    }

    public BigDecimal getWinLossAmountUser() {
        return this.winLossAmountUser==null?BigDecimal.ZERO:this.winLossAmountUser;
    }

    public BigDecimal getWinLossAmountPlat() {
        return this.winLossAmountPlat==null?BigDecimal.ZERO:this.winLossAmountPlat;
    }

    public BigDecimal getAlreadyUseAmount() {
        return this.alreadyUseAmount==null?BigDecimal.ZERO:this.alreadyUseAmount;
    }

    public BigDecimal getTipsAmount() {
        return this.tipsAmount==null?BigDecimal.ZERO:this.tipsAmount;
    }

    public BigDecimal getAdjustAmount() {
        return this.adjustAmount==null?BigDecimal.ZERO:this.adjustAmount;
    }
}
