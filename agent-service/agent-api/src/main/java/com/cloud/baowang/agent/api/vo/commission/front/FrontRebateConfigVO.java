package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "流水返点配置以及人头费", description = "流水返点配置以及人头费")
public class FrontRebateConfigVO implements Serializable {
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;
    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;
    @Schema(description = "有效新增人头费")
    private BigDecimal newUserAmount;
    @Schema(description = "电子有效流水返点比例")
    private String slotRate;
    @Schema(description = "彩票有效流水返点比例")
    private String lotteryRate;
    @Schema(description = "真人有效流水返点比例")
    private String liveRate;
    @Schema(description = " 体育有效流水返点比例")
    private String sportsRate;
    @Schema(description = "棋牌有效流水返点比例")
    private String chessRate;
    @Schema(description = "电竞有效流水返点比例")
    private String esportsRate;
    @Schema(description = "斗鸡有效流水返点比例")
    private String cockfightRate;
    @Schema(description = "捕鱼有效流水返点比例")
    private String fishRate;
    @Schema(title = "娱乐返点比例")
    private String marblesRebate;
}
