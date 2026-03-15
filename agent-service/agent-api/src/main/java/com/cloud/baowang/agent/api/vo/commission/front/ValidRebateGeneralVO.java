package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:55
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "有效流水返点", description = "有效流水返点")
public class ValidRebateGeneralVO {
    @Schema(description = "有效流水返点")
    private BigDecimal commissionAmount = new BigDecimal("0.0000");

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;

    @Schema(description = "计算开始时间")
    private Long startTime;

    @Schema(description = "计算结束时间")
    private Long endTime;
    @Schema(title = "有效新增人头费",description = "有效新增人头费")
    private BigDecimal newUserAmount = new BigDecimal("0.0000");
    @Schema(title = "电子有效流水",description = "电子有效流水")
    private BigDecimal slotAmount = new BigDecimal("0.0000");
    @Schema(title = "彩票有效流水",description = "彩票有效流水")
    private BigDecimal lotteryAmount = new BigDecimal("0.0000");
    @Schema(title = "真人有效流水",description = "真人有效流水")
    private BigDecimal liveAmount = new BigDecimal("0.0000");
    @Schema(title = " 体育有效流水",description = "体育有效流水")
    private BigDecimal sportsAmount = new BigDecimal("0.0000");
    @Schema(title = "棋牌有效流水",description = "棋牌有效流水")
    private BigDecimal chessAmount = new BigDecimal("0.0000");
    @Schema(title = "电竞有效流水",description = "电竞有效流水")
    private BigDecimal esportsAmount = new BigDecimal("0.0000");
    @Schema(title = "斗鸡有效流水",description = "斗鸡有效流水")
    private BigDecimal cockfightAmount = new BigDecimal("0.0000");
    @Schema(title = "捕鱼有效流水",description = "捕鱼有效流水")
    private BigDecimal fishAmount = new BigDecimal("0.0000");
    @Schema(title = "娱乐有效流水",description = "娱乐有效流水")
    private BigDecimal marblesAmount = new BigDecimal("0.0000");

    public BigDecimal getCommissionAmount() {
        return commissionAmount.setScale(4, RoundingMode.DOWN);
    }
}
