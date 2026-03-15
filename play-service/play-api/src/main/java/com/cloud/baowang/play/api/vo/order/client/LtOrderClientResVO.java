package com.cloud.baowang.play.api.vo.order.client;

import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@I18nClass
@Schema(description = "彩票注单信息")
public class LtOrderClientResVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "三方游戏id")
    private String thirdGameCode;
    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "期号")
    private String gameNo;
    @Schema(description = "场馆名称")
    @I18nField
    private String venueName;
    @Schema(description = "场馆CODE")
    private String venueCode;
    @Schema(description = "投注内容")
    private String betContent;
    @Schema(description = "玩法类型")
    private String playType;
    @Schema(description = "赔率")
    private String odds;
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

    public Integer getOrderClassify() {
        if (ObjUtil.isNotNull(orderClassify) && Objects.equals(orderClassify, ClassifyEnum.RESETTLED.getCode())) {
            return ClassifyEnum.SETTLED.getCode();
        }
        return orderClassify;
    }
}
