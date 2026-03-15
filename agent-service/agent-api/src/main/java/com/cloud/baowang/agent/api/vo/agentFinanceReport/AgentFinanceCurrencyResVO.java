package com.cloud.baowang.agent.api.vo.agentFinanceReport;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "代理财务报表")
public class AgentFinanceCurrencyResVO {
    
    @Schema(description = "币种", hidden = true)
    private String currency;

    @Schema(description = "平台币种")
    private String platCurrency = CommonConstant.PLAT_CURRENCY_CODE;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理id")
    private String agentId;

    @Schema(description = "总输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalWinLoss = BigDecimal.ZERO;

    @Schema(description = "净输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal netWinLoss = BigDecimal.ZERO;


    @Schema(description = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetAmount = BigDecimal.ZERO;



/*    @Schema(description = "团队投注-不包括团队有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal subAgentValidBetAmount = BigDecimal.ZERO;*/


    @Schema(description = "存款额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Schema(description = "取款额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal withdrawAmount = BigDecimal.ZERO;

    @Schema(description = "vip 福利")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal vipBenefits = BigDecimal.ZERO;

    @Schema(description = "活动优惠")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal activityDiscounts = BigDecimal.ZERO;

    @Schema(description = "已使用优惠")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal usedDiscounts = BigDecimal.ZERO;


  /*  @Schema(description = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetAmount = BigDecimal.ZERO;*/

    @Schema(description = "调整金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal adjustAmount = BigDecimal.ZERO;

    @Schema(description = "存提手续费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Schema(description = "场馆费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal venueFee = BigDecimal.ZERO;

    @Schema(description = "有效活跃")
    private Integer activeNum = CommonConstant.business_zero;

    @Schema(description = "有效新增")
    private Integer validNewNum = CommonConstant.business_zero;

    @Schema(description = "返水金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAmount = BigDecimal.ZERO;

    @Schema(description = "打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount= BigDecimal.ZERO;


}
