package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 17:05
 * @description: 流水返点配置
 */
@Data
@I18nClass
@Schema(title = "流水返点配置VO", description = "流水返点配置VO")
public class RebateConfigVO implements Serializable {
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    @NotNull
    private Integer settleCycle;
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;
    @Schema(description = "有效新增人头费/人")
    @NotNull
    private BigDecimal newUserAmount;
    @Schema(description = "电子有效流水返点比例")
    @NotNull
    private String slotRate;
    @Schema(description = "彩票有效流水返点比例")
    private String lotteryRate;
    @Schema(description = "彩票赔率方案id")
    @NotNull
    private Long lotteryPlanId;
    @Schema(description = "真人有效流水返点比例")
    @NotNull
    private String liveRate;
    @Schema(description = " 体育有效流水返点比例")
    private String sportsRate;
    @Schema(description = "体育赔率方案id")
    @NotNull
    private Long sportsPlanId;
    @Schema(description = "棋牌有效流水返点比例")
    @NotNull
    private String chessRate;
    @Schema(description = "电竞有效流水返点比例")
    private String esportsRate;
    @Schema(description = "电竞赔率方案id")
    @NotNull
    private String esportsPlanId;
    @Schema(description = "斗鸡有效流水返点比例")
    @NotNull
    private String cockfightRate;
    @Schema(description = "捕鱼有效流水返点比例")
    @NotNull
    private String fishRate;

    @Schema(description = "娱乐有效流水返点比例")
    @NotNull
    private String marblesRebate;
}
