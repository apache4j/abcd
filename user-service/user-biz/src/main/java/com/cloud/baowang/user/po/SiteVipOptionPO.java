package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author mufan
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_option")
public class SiteVipOptionPO extends BasePO implements Serializable {

    private String id;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "VIP等级编码")
    private Integer vipGradeCode;

    @Schema(description = "VIP等级名称")
    private String vipGradeName;

    @Schema(description = "VIP图标地址")
    private String vipIcon;

    @Schema(description = "升级流水经验")
    private BigDecimal vipUpgradeExp;

    @Schema(description = "保级流水金额")
    private BigDecimal relegationAmount;

    @Schema(description = "保级天数")
    private Integer relegationDays;

    @Schema(description = "晋级礼金金额")
    private BigDecimal promotionBonus;

    @Schema(description = "晋级礼金流水倍数")
    private BigDecimal promotionBonusMultiple;

    @Schema(description = "周红包金额")
    private BigDecimal weekBonus;

    @Schema(description = "周红包类型：0-先发后打, 1-先打后发")
    private Integer weekBonusType;

    @Schema(description = "周红包流水倍数")
    private BigDecimal weekBonusAmountMultiple;

    @Schema(description = "周红包流水总金额")
    private BigDecimal weekBonusAmountTotal;

    @Schema(description = "每年生日红包金额")
    private BigDecimal ageAmount;

    @Schema(description = "每年生日红包流水倍数")
    private BigDecimal ageAmountMultiple;

    @Schema(description = "单日免费提款次数")
    private Integer dailyWithdrawalFreeNum;

    @Schema(description = "单日提款次数上限")
    private Integer dailyWithdrawalNumLimit;

    @Schema(description = "单日免费提款金额")
    private BigDecimal dailyWithdrawalFreeAmountLimit;

    @Schema(description = "单日提款额度上限")
    private BigDecimal dailyWithdrawAmountLimit;

    @Schema(description = "是否有加密货币提款手续费：0-没有, 1-有")
    private Integer encryCoinFee;

    @Schema(description = "是否日反水：0-没有, 1-有")
    private Integer rebateConfig;

    @Schema(description = "存款快捷金额，字符串逗号分隔")
    private String depositAmountLimit;

    @Schema(description = "备注")
    private String remark;

}
