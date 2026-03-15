package com.cloud.baowang.play.api.vo.order.client;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 赛事注单信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(description = "赛事注单信息")
public class EventOrderClientResVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "场馆名称")
    private String venueName;
    @Schema(description = "场馆CODE")
    private String venueCode;
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
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;

    @Schema(description = "赔率")
    private String odds;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态-文本")
    private String orderClassifyText;
    @Schema(description = "是否串关 true:是 false:否")
    private Boolean multipleBet;
    @Schema(description = "串关注单信息")
    private List<OrderMultipleBetVO> orderMultipleBetList;

    @Schema(description = "三方信息")
    private JSONObject data;
    @Schema(description = "玩法信息")
    private String playInfo;
    public Integer getOrderClassify() {
        if (ObjUtil.isNotNull(orderClassify) && Objects.equals(orderClassify, ClassifyEnum.RESETTLED.getCode())) {
            return ClassifyEnum.SETTLED.getCode();
        }
        return orderClassify;
    }
}
