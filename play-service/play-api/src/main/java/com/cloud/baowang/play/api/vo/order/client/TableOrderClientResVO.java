package com.cloud.baowang.play.api.vo.order.client;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 桌台注单信息  真人
 */
@Data
@I18nClass
@Schema(description = "桌台注单信息  真人")
public class TableOrderClientResVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "游戏id")
    private String gameId;
    @Schema(description = "场馆名称")
    @I18nField
    private String venueName;

    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "场馆")
    private String venueCode;
    @Schema(description = "游戏大类")
    private String playType;
    @Schema(description = "投注内容")
//    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SH_PLAY_TYPE)
    private String betContent;
    @Schema(description = "投注内容-文本")
    private String betContentText;
    @Schema(description = "局号")
    private String gameNo;
    @Schema(description = "桌号")
    private String deskNo;
    @Schema(description = "投注结果")
    private String resultList;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;
    @Schema(description = "赔率")
    private String odds;
    @Schema(description = "投注时间")
    private Long betTime;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态-文本")
    private String orderClassifyText;
    @Schema(description = "注单详情")
    private String orderInfo;

    @Schema(description = "注单详情")
    private String parlayInfo;

    @Schema(description = "税费")
    private BigDecimal lightningAmount;

    @Schema(description = "投注本金")
    private BigDecimal totalAmount;
}
