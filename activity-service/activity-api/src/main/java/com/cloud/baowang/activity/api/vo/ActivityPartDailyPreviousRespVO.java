package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.SensitiveDataJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(title = "每日竞赛-上届冠军")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartDailyPreviousRespVO {

    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "账号")
    @JsonSerialize(using = SensitiveDataJsonSerializer.class)
    private String userAccount;

    @Schema(description = "奖金")
    private BigDecimal awardAmount;

    @Schema(description = "头像")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "头像")
    private String iconFileUrl;

    @Schema(description = "彩金百分比")
    private BigDecimal activityAmountPer;

    @Schema(description = "币种符号")
    private String currencySymbol;

    @Schema(description = "优惠方式:0:百分比,1:固定金额")
    private Integer activityDiscountType;

}
