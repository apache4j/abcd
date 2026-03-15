package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "后台VenueValueVO-code和value返回对象")
public class VenueValueVO implements Serializable {

    @Schema(title = "类型")
    private String type;


    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String code;


    private String codeText;





    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String value;

    public String getValue() {
        return codeText;
    }

    /**
     *
     */
    @Schema(title = "会员是否选择 0 -未选择，1-选择")
    private String selectFlag ;

    /**
     * 活动彩金，如果是选择游戏大类，默认游戏大类第一条
     */
    @Schema(description = "活动彩金总金额", required = true)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal activityAmount;

    /**
     * 活动彩金，如果是选择游戏大类，默认游戏大类第一条
     */
    @Schema(description = "活动彩金总金额-货币类型", required = true)
    private String activityAmountCurrencyCode;

    /**
     * 活动彩金，如果是选择游戏大类，默认游戏大类第一条
     */
    @Schema(description = "需打流水")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal runningWater;

    /**
     * 活动彩金，如果是选择游戏大类，默认游戏大类第一条
     */
    @Schema(description = "需打流水-货币类型")
    private String runningWaterCurrencyCode;

    @Schema(description = "活动规则-多语言")
    @I18nField
    private String activityRuleI18nCode;

 /*   @Schema(description = "活动规则-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;*/

    @Schema(description = "参与资格:true=可以参与,false=不可以参与")
    private Boolean activityCondition;



}
