package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 流水排行榜详情配置实体
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@I18nClass
public class ActivityDailyCompetitionDetailRespVO implements Serializable {

    private String id;

    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "活动名称")
    private String activityNameI18nCode;

    private List<I18nMsgFrontVO> activityNameI18nCodeList;

    @Schema(description = "活动ID")
    private String activityId;

    @Schema(description = "每日竞赛才有的编号")
    private String comNo;

    @Schema(description = "游戏类型。字典CODE：venue_type")
    private Integer venueType;

    @Schema(description = "场馆CODE")
    private List<String> venueCodeList;

    @Schema(description = "初始展示金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal initAmount;

    @Schema(description = "实际金额指定场馆总流水的")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal venuePercentage;

    @Schema(description = "活动规则")
    private String activityRuleI18nCode;

    @Schema(description = "优惠类型:字典CODE：activity_discount_type")
    private Integer activityDiscountType;

    @Schema(description = "奖励设置排名配置")
    private List<SiteActivityDailyCompetitionDetail> activityDetail;

    @Schema(description = "key=竞赛Id,value = 机器人配置")
    private List<ActivityDailyRobotRespVO> robotList;
}
