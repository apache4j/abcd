package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理PC和H5 本期统计")
public class MonthClientAgentResponseVO {

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "本期总流水")
    private BigDecimal monthValidBetAmount;

    @Schema(description = "本期佣金比例")
    private BigDecimal monthCommissionRatio;

    @Schema(description = "本期净输赢")
    private BigDecimal winOrLoss;


    @Schema(description = "今日直属会员有效投注")
    private BigDecimal monthDirectUserValidBetAmount = BigDecimal.ZERO;


    @Schema(description = "今日下级代理团队有效投注")
    private BigDecimal monthSugAgentValidBetAmount= BigDecimal.ZERO;



    //@Schema(description = "下级会员")
    //private Long lowerLevelUserNumber;

    @Schema(description = "本期新增下级")
    private Integer addLowerLevelAgentNumber;

    @Schema(description = "新注册会员上期")
    private Integer addLowerLevelUserNumberLast;

    @Schema(description = "本期新注册会员")
    private Integer addLowerLevelUserNumber;
    @Schema(description = "本期新注册-百分比")
    private String addLowerLevelUserPercentage;
    @Schema(description = "本期新注册-百分比 1:上升 2:下降 3:为0不显示")
    private Integer addLowerLevelUserFlag;

    @Schema(description = "首存人数上期")
    private Integer firstDepositNumberLast;

    @Schema(description = "首存人数")
    private Integer firstDepositNumber;
    @Schema(description = "首存人数-百分比")
    private String firstDepositPercentage;
    @Schema(description = "首存人数-百分比 1:上升 2:下降 3:为0不显示")
    private Integer firstDepositFlag;

    @Schema(description = "本期有效新增会员")
    private Integer newActiveUserNumber;

    @Schema(description = "本期有效活跃会员")
    private Integer validActiveUserNumber;

}
