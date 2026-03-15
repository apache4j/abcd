package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点后台VIP权益配置返回对象")
public class SiteVipOptionVO {
    private String id;

    @Schema(description = "站点编码")
    @NotNull(message = "站点编码不能为空")
    private String siteCode;

    @Schema(description = "货币代码")
    @NotNull(message = "货币代码不能为空")
    private String currencyCode;

    @Schema(description = "VIP等级编码")
    @NotNull(message = "VIP等级编码为空")
    private Integer vipGradeCode;

    @Schema(description = "VIP等级名称")
    @NotNull(message = "VIP等级名称为空")
    private String vipGradeName;

    @Schema(description = "VIP图标地址")
    @NotNull(message = "VIP图标地址为空")
    private String vipIcon;

    @Schema(description = "VIP图标地址")
    @NotNull(message = "VIP图标地址为空")
    private String vipIconImage;

    @Schema(description = "升级流水经验")
    private BigDecimal vipUpgradeExp;

    @Schema(description = "保级流水金额")
    @NotNull(message = "保级流水金额为空")
    private BigDecimal relegationAmount;

    @Schema(description = "保级天数")
    @NotNull(message = "保级天数为空")
    private Integer relegationDays;

    @Schema(description = "晋级礼金金额")
    @NotNull(message = "晋级礼金金额为空")
    private BigDecimal promotionBonus;

    @Schema(description = "晋级礼金流水倍数")
    @NotNull(message = "晋级礼金流水倍数为空")
    private BigDecimal promotionBonusMultiple;

    @Schema(description = "周红包金额")
    @NotNull(message = "周红包金额为空")
    private BigDecimal weekBonus;

    @Schema(description = "周红包类型：0-先发后打, 1-先打后发")
    @NotNull(message = "周红包发放限定为空")
    private Integer weekBonusType;

    @Schema(description = "周红包流水倍数")
    private BigDecimal weekBonusAmountMultiple;

    @Schema(description = "周红包流水总金额")
    private BigDecimal weekBonusAmountTotal;

    @Schema(description = "每年生日红包金额")
    @NotNull(message = "每年生日红包金额为空")
    private BigDecimal ageAmount;

    @Schema(description = "每年生日红包流水倍数")
    @NotNull(message = "每年生日红包流水倍数为空")
    private BigDecimal ageAmountMultiple;

    @Schema(description = "单日免费提款次数")
    @NotNull(message = "单日免费提款次数为空")
    private Integer dailyWithdrawalFreeNum;

    @Schema(description = "单日提款次数上限")
    @NotNull(message = "单日提款次数上限为空")
    private Integer dailyWithdrawalNumLimit;

    @Schema(description = "单日免费提款金额")
    @NotNull(message = "单日免费提款金额为空")
    private BigDecimal dailyWithdrawalFreeAmountLimit;

    @Schema(description = "单日提款额度上限")
    @NotNull(message = "单日提款额度上限为空")
    @Max(value = 99999999, message = "单日提款金额上限不能大于99999999")
    private BigDecimal dailyWithdrawAmountLimit;

    @Schema(description = "是否有加密货币提款手续费：0-没有, 1-有")
    @NotNull(message = "是否有加密货币提款手续费为空")
    private Integer encryCoinFee;

    @Schema(description = "是否有日反水：0-没有, 1-有")
    @NotNull(message = "是否有日反水为空")
    private Integer rebateConfig;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "存款快捷金额，字符串逗号分隔")
    private String depositAmountLimit;

    @Schema(description = "充值渠道配置")
    private List<SiteVipOptionCurrencyConfigVO> currencyConfigVOs;
}
