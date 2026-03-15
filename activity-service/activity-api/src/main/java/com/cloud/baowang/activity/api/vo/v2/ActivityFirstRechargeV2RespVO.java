package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首存活动-详情实体
 */
@Data
@Schema(description = "首存活动详细信息")
@I18nClass
public class ActivityFirstRechargeV2RespVO extends ActivityBaseV2RespVO implements Serializable {

    /**
     * 所属活动id
     */
    @Schema(description = "所属活动id", hidden = true)
    private String activityId;

    /**
     * 优惠方式类型，0.百分比，1.固定
     * {@link com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum}
     */
    @Schema(description = "优惠方式类型，0.百分比，1.固定")
    @NotNull(message = "优惠方式类型不能为空")
    @Min(value = 0, message = "优惠方式类型不能小于0")
    @Max(value = 1, message = "优惠方式类型不能大于1")
    private Integer discountType;


    @Schema(description = "百分比类型对应条件值--优惠方式==0")
    private List<RechargePercentageV2VO> percentageVO;

    @Schema(description = "固定金额对应条件值--优惠方式==1")
    private List<FixedAmountV2VO> fixedAmountVOS;


    @Schema(description = "对应选择了游戏大类-首存/次存")
    private List<DepositConfigV2DTO> depositConfigDTOS;


    @Schema(description = "对应选择了游戏大类-指定日存款")
    private List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS;

    /**
     * 对应的活动条件值
     */
    private String conditionalValue;

    /**
     * 领取方式0.次日领取，1.每日领取
     * {@link ReceiveTypeEnum}
     */
    @Schema(description = "领取方式0.次日领取，1.每日领取")
    @NotNull(message = "领取方式不能为空")
    @Min(value = 0, message = "领取方式不能小于0")
    @Max(value = 1, message = "领取方式不能大于1")
    private Integer claimPeriod;

    /**
     * 领取时间-开始时间
     */
    @Schema(description = "领取时间-开始时间")
    private Long claimStartTime;

    /**
     * 领取时间-结束时间
     */
    @Schema(description = "领取时间-结束时间")
    private Long claimEndTime;

    /**
     * 奖励领取过期天数
     */
    @Schema(description = "奖励领取过期天数")
    private Integer claimExpiryDays;

    @Schema(description = "参与方式,0.手动参与，1.自动参与")
    private Integer participationMode;

    /**
     * 结算周期: 0 - 日结, 1 - 周结, 2 - 月结
     */
    private Integer calculateType;

    @Schema(description = "活动对象 字典CODE：activity_user_type", hidden = true)
    private Integer userType;


    @Schema(description = "注册天数", hidden = true)
    private Integer registerDay;


    /**
     * 派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发")
    @NotNull(message = "派发方式不能为空")
    @Min(value = 0, message = "派发方式不能小于0")
    @Max(value = 1, message = "派发方式不能大于1")
    private Integer distributionType;


    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String venueTypeText;

    @Schema(title = "平台币还是法币")
    private String platformOrFiatCurrency;


}
