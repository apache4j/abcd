package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 14:23
 * @description: 佣金发放记录佣金详情
 */
@Data
@I18nClass
@Schema(title = "佣金发放记录佣金详情 --负盈利佣金", description = "佣金发放记录佣金详情----负盈利佣金")
public class CommissionVenueFeeDetailVO {
    @Schema(description = "本期负盈利佣金合计")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal commissionAmount;

    @Schema(description = "平台总输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossTotal;

    @Schema(description = "平台币钱包转化金额 - 已使用优惠取这个")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal transferAmount;

    @Schema(description = "本期场馆费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal venueFee;

    @Schema(description = "总存取手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal accessFee;

    @Schema(description = "待冲正金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal lastMonthRemain;

    @Schema(description = "会员总输赢 减了打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal userWinLoss;

    @Schema(description = "会员输赢 注单输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betWinLoss;


    @Schema(description = "提前结算")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal earlySettle;

    @Schema(description = "会员存款手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal userDepFee;

    @Schema(description = "会员提款手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal userWithFee;

    @Schema(description = "代理额度钱包存款手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal agentDepFee;

    @Schema(description = "代理佣金钱包提款手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal agentWithFee;

    @Schema(description = "佣金比例")
    private String rate;

    @Schema(description = "货币")
    private String currency;

    @Schema(description = "其他调整金额-会员调整其他调整")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal adjustAmount;

    @Schema(description = "场馆费详情")
    private List<ReportCommissionVenueFeeVO> dataList;


    @Schema(description = "佣金调整金额-审核界面调整")
    private BigDecimal reviewAdjustAmount;

    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;

    @Schema(description = "已使用优惠")
    private BigDecimal discountAmount;



}
