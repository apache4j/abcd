package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 1:10
 * @description: 代理佣金信息表
 */
@Data
@Schema(title = "AgentCommissionTotalReportVO对象", description = "佣金报表")
public class AgentCommissionTotalReportVO  implements Serializable {
    private Long id;

    private Long creator;

    private Long createdTime;

    private Long updater;

    private Long updatedTime;

    /** 月份 */
    @Schema(description ="月份")
    private String month;

    /** 日期 */
    @Schema(description ="日期")
    private Long reportDay;

    @Schema(description ="返佣人数")
    private Integer agentCount;

    /** 场馆费 */
    @Schema(description ="场馆费")
    private BigDecimal venueFee;

    /** 优惠 */
    @Schema(description ="总红利")
    private BigDecimal discountAmount;

    /** 返水金额 */
    @Schema(description ="总返水")
    private BigDecimal rebateAmount;

    /** 账号调整 */
    @Schema(description ="账户调整")
    private BigDecimal adjustAmount;

    @Schema(description ="总返点")
    private BigDecimal pointAmount;


    /** 代理净输赢 */
    @Schema(description ="净输赢")
    private BigDecimal agentWinLoss;

    /** 上月结余 */
    @Schema(description ="上月结余")
    private BigDecimal lastMonthRemain;

    /** 冲正后净输赢 */
    @Schema(description ="冲正后净输赢")
    private BigDecimal totalWinLoss;

    /** 存提款手续费 */
    @Schema(description ="存提手续费")
    private BigDecimal feeAmount;

    /** 活跃人数 */
    @Schema(description ="当月活跃人数")
    private Integer activeNumber;

    /** 有效活跃人数 */
    @Schema(description ="当月有效活跃人数")
    private Integer activeValidNumber;

    @Schema(description ="加赠佣金")
    private BigDecimal extraAmount;

    /** 返佣金额 */
    @Schema(description ="实际返佣")
    private BigDecimal commissionAmount;

    /** 团队总佣金 */
    @Schema(description ="团队总佣金")
    private BigDecimal teamCommission;

    /** 佣金调整 */
    @Schema(description ="佣金调整")
    private BigDecimal adjustCommission;

    /** 总投注人数 */
    @Schema(description ="总投注人数")
    private Integer bettors;

    /** 总注单量 */
    @Schema(description ="总注单量")
    private Integer betNumber;

    /** 总投注金额 */
    @Schema(description ="总投注金额")
    private BigDecimal betAmount;

    /** 总有效投注 */
    @Schema(description ="总有效投注")
    private BigDecimal validAmount;

    @Schema(description ="总输赢")
    private BigDecimal winLossAmount;
}
