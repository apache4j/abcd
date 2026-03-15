package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 体育负盈利详情实体
 */
@Data
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLossInSportsVO extends ActivityBaseVO implements Serializable {

    @Schema(description = "前端忽略该字段",hidden = true)
    private String activityId;

    @Schema(description = "注册天数")
    private Integer registerDay;

    @Schema(description = "活动对象。字典CODE：activity_user_type", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer userType;

    @Schema(description = "场馆类型：字典CODE：venue_type", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer venueType;

    @Schema(description = "场馆名称：接口：/venue_info/api/venueInfoList", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<String> venueCodeList;

    @Schema(description = "优惠方式:字典CODE：activity_discount_type ", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer activityDiscountType;

    @Schema(description = "活动详情配置", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<SiteActivityProfitRebateDetail> activityDetail;

    @Schema(description = "百分比类型对应条件值--优惠方式==0")
    private RechargePercentageVO percentageVO;

    @Schema(description = "结算周期: 字典CODE:calculate_type", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer calculateType;

    @Schema(description = "奖励上限", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal upperLimit;

    @Schema(description = "派发方式: 字典CODE:activity_distribution_type ", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer distributionType;

    @Schema(description = "领取方式: 字典CODE:activity_receive_type", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer receiveType;

    @Schema(description = "领取时间。0表示周期结束才过期", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer receiveDate;


    @Schema(description = "字典CODE：activity_participation_type")
    @NotNull(message = "参与方式不能为空")
    @Min(value = 0, message = "参与方式不能小于0")
    @Max(value = 1, message = "参与方式不能大于1")
    private Integer participationMode;






}
