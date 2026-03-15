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
public class SportOrderClientResVO {
    @Schema(description = "注单号")
    private String orderId;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "场馆名称")
    private String venueName;
    @Schema(description = "有效投注金额")
    private BigDecimal validAmount;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态-文本")
    private String orderClassifyText;
    @Schema(description = "单关/串关信息")
    private String customs;

    @Schema(description = "串关注单信息")
    private List<OrderMultipleBetVO> orderMultipleBetList;

    public Integer getOrderClassify() {
        if (ObjUtil.isNotNull(orderClassify) && Objects.equals(orderClassify, ClassifyEnum.RESETTLED.getCode())) {
            return ClassifyEnum.SETTLED.getCode();
        }
        return orderClassify;
    }
}
