package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;


/**
 * 商务总代报表
 *
 * @author ford
 * @since 2024-11-05
 */

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_top_agent_static_day")
@Schema(title = "商务总代报表")
public class ReportTopAgentStaticDayPO extends BasePO {

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

    @Schema(title = "上级总代Id")
    private String agentId;

    @Schema(title = "上级总代账号")
    private String agentAccount;

    @Schema(title = "直属上级总代ID")
    private String parentId;

    @Schema(title = "直属上级总代账号")
    private String parentAccount;

    @Schema(title = "层次")
    private String path;

    @Schema(title = "总代层级")
    private Integer level;

    @Schema(title = "总代类型 1正式 2商务 3置换")
    private Integer agentType;

    @Schema(title = "总代归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "商务账号")
    private String merchantAccount;

    @Schema(title = "商务账号")
    private String merchantName;

    @Schema(description = "总代标签id")
    private String agentLabelId;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(title = "团队总代人数")
    private Long teamAgentNum;

    @Schema(title = "直属下级 人数")
    private Long directReportNum;

    @Schema(title = "会员人数")
    private Long userNum;

    @Schema(title = "注册人数")
    private Long registerUserNum;
    @Schema(title = "首存人数")
    private Long firstDepositNum;
    @Schema(title = "首存转换率=首存人数 / 注册人数")
    private BigDecimal firstDepositRate;
    @Schema(title = "投注人次")
    private Long betUserCount;

    @Schema(title = "币种")
    private String currencyCode;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "有效投注")
    private BigDecimal validAmount;

    @Schema(title = "会员输赢")
    private BigDecimal winLossAmountUser;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(title = "平台总输赢 等于 -（用户投注输赢 - 打赏金额）")
    private BigDecimal winLossAmountPlat;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(title = "盈亏比例 等于 平台总输赢 / 投注额")
    private BigDecimal winLossRate;

    @Schema(title = "活动优惠 平台币")
    private BigDecimal activityAmount;

    @Schema(title = "vip福利 平台币")
    private BigDecimal vipAmount;

    @Schema(title = "已经使用优惠 主货币")
    private BigDecimal alreadyUseAmount;

    @Schema(title = "存款金额")
    private BigDecimal depositAmount;

    @Schema(title = "取款金额")
    private BigDecimal withdrawAmount;

    @Schema(title = "存提手续费 主货币")
    private BigDecimal depositWithdrawFeeAmount;

    @Schema(title = "打赏金额-主货币")
    private BigDecimal tipsAmount;


    public Long getTeamAgentNum() {
        return teamAgentNum==null?0L:this.teamAgentNum;
    }

    public Long getDirectReportNum() {
        return directReportNum==null?0L:this.directReportNum;
    }

    public Long getRegisterUserNum() {
        return registerUserNum==null?0L:this.registerUserNum;
    }

    public Long getFirstDepositNum() {
        return firstDepositNum==null?0L:this.firstDepositNum;
    }

    public BigDecimal getFirstDepositRate() {
        return firstDepositRate==null?BigDecimal.ZERO:this.firstDepositRate;
    }

    public Long getBetUserCount() {
        return betUserCount==null?0L:this.betUserCount;
    }


    public BigDecimal getBetAmount() {
        return betAmount==null?BigDecimal.ZERO:this.betAmount;
    }
    public BigDecimal getRebateAmount() {
        return rebateAmount==null?BigDecimal.ZERO:this.rebateAmount;
    }

    public BigDecimal getValidAmount() {
        return validAmount==null?BigDecimal.ZERO:this.validAmount;
    }

    public BigDecimal getWinLossAmountUser() {
        return winLossAmountUser==null?BigDecimal.ZERO:this.winLossAmountUser;
    }

    public BigDecimal getWinLossAmountPlat() {
        return winLossAmountPlat==null?BigDecimal.ZERO:this.winLossAmountPlat;
    }

    public BigDecimal getAdjustAmount() {
        return adjustAmount==null?BigDecimal.ZERO:this.adjustAmount;
    }

    public BigDecimal getWinLossRate() {
        return winLossRate==null?BigDecimal.ZERO:this.winLossRate;
    }

    public BigDecimal getActivityAmount() {
        return activityAmount==null?BigDecimal.ZERO:this.activityAmount;
    }

    public BigDecimal getVipAmount() {
        return vipAmount==null?BigDecimal.ZERO:this.vipAmount;
    }

    public BigDecimal getAlreadyUseAmount() {
        return alreadyUseAmount==null?BigDecimal.ZERO:this.alreadyUseAmount;
    }

    public BigDecimal getDepositWithdrawFeeAmount() {
        return depositWithdrawFeeAmount==null?BigDecimal.ZERO:this.depositWithdrawFeeAmount;
    }
}
