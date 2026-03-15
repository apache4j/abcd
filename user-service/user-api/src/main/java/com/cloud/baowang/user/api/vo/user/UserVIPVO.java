package com.cloud.baowang.user.api.vo.user;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @Author 小智
 * @Date 11/5/23 6:28 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户vip信息")
@I18nClass
public class UserVIPVO implements Serializable {

    @Schema(description = "VIP等级")
    private Integer vipGrade;

    @Schema(description = "VIP升级后等级")
    private Integer vipGradeUp;

    @Schema(description = "VIP等级名称")
    private String vipGradeName;

    @Schema(description = "VIP升级后等级名称")
    private String vipGradeUpName;

    @Schema(description = "VIP段位")
    private Integer vipRank;
    @Schema(description = "vip段位名称")
    @I18nField
    private String vipRankNameI18nCode;

//    @Schema(description ="剩余VIP升级需要存款金额")
//    private BigDecimal leftDepositAmount;
//
//    @Schema(description ="已完成存款金额")
//    private BigDecimal finishDepositAmount;
//
//    @Schema(description ="已完成存款金额VIP升级需要金额")
//    private BigDecimal upgradeDepositAmount;

    @Schema(description = "剩余VIP升级需要有效投注")
    private BigDecimal leftBetAmount;

    @Schema(description = "已完成有效投注金额")
    private BigDecimal finishBetAmount;

    @Schema(description = "已完成有效投注VIP升级需要金额")
    private BigDecimal upgradeBetAmount;

    @Schema(description = "剩余保级有效投注金额")
    private BigDecimal leftRelegationAmount;


    @Schema(description = "已完成保级有效投注金额-当前等级经验")
    private BigDecimal finishRelegationAmount;
    //
    @Schema(description = "VIP升级需要保级有效投注金额-当前等级总经验")
    private BigDecimal upgradeRelegationAmount;

    @Schema(description = "剩余保级天数")
    private Integer relegationLessDate;




    @Schema(description = "保级日期-配置保级天数")
    private Integer relegationDate;




//    public BigDecimal getLeftDepositAmount() {
//        return Optional.ofNullable(leftDepositAmount).orElse(BigDecimal.ZERO);
//    }
//
//    public BigDecimal getFinishDepositAmount() {
//        return Optional.ofNullable(finishDepositAmount).orElse(BigDecimal.ZERO);
//    }
//
//    public BigDecimal getUpgradeDepositAmount() {
//        return Optional.ofNullable(upgradeDepositAmount).orElse(BigDecimal.ZERO);
//    }

    public BigDecimal getLeftBetAmount() {
        return Optional.ofNullable(leftBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getFinishBetAmount() {
        return Optional.ofNullable(finishBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getUpgradeBetAmount() {
        return Optional.ofNullable(upgradeBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getLeftRelegationAmount() {
        return Optional.ofNullable(leftRelegationAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getFinishRelegationAmount() {
        return Optional.ofNullable(finishRelegationAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getUpgradeRelegationAmount() {
        return Optional.ofNullable(upgradeRelegationAmount).orElse(BigDecimal.ZERO);
    }

    @Schema(description = "备注信息")
    private String remark;
}
