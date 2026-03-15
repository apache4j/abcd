package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 流水排行榜详情配置实体
 */
@Data
public class ActivityDailyCompetitionDetailVO implements Serializable {

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityNameI18nCodeList;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityId;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "游戏类型：字典CODE：venue_type")
    private Integer venueType;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "场馆名称：接口：/venue_info/api/venueInfoList", required = true)
    private List<String> venueCodeList;


    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "初始展示金额")
    private BigDecimal initAmount;


    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "实际金额指定场馆总流水的")
    private BigDecimal venuePercentage;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "奖励方式:字典CODE：activity_discount_type")
    private Integer activityDiscountType;

    @Schema(description = "竞赛ID")
    private String comNo;

    /**
     * 活动详情配置, 存数组
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "活动详情配置")
    private List<SiteActivityDailyCompetitionDetail> activityDetail;


}
