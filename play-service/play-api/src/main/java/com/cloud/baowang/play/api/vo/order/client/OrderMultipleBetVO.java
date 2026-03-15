package com.cloud.baowang.play.api.vo.order.client;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.constants.I18nInitConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@Schema(description = "赛事注单信息 串关单信息")
public class OrderMultipleBetVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "赛事信息")
    private String eventInfo;
    @Schema(description = "队伍信息")
    private String teamInfo;
    @Schema(description = "投注内容")
    private String betContent;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "赔率")
    private String odds;
    @Schema(description = "输赢状态 赢1 和0 输-1")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = I18nInitConstant.CLIENT_ORDER_WINLOSS_STATUS)
    private Integer winlossStatus;
    @Schema(description = "输赢状态-文本")
    private String winlossStatusText;
    @Schema(description = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态-文本")
    private String orderClassifyText;
}
