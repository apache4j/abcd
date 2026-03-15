package com.cloud.baowang.play.api.vo.order.client;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 基本类型注单信息 电子 棋牌
 */
@Data
@I18nClass
@Schema(description = "基本类型注单信息 电子 棋牌")
public class BasicOrderClientResVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "场馆名称")
    @I18nField
    private String venueName;
    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "三方游戏id")
    private String thirdGameCode;
    @Schema(description = "游戏名称")
    @I18nField
    private String gameName;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;
    @Schema(description = "投注时间")
    private Long betTime;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态-文本")
    private String orderClassifyText;
}
