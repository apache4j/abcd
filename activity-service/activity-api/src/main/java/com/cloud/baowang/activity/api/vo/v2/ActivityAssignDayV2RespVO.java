package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityAssignDayCondVO;
import com.cloud.baowang.activity.api.vo.ActivityAssignDayVenueVO;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Schema(description = "指定存款日期活动响应")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityAssignDayV2RespVO extends ActivityBaseV2RespVO implements Serializable {
    @Schema(description = "活动Id")
    private String activityId;

    @Schema(description = "指定日期存款")
    @NotNull(message = "指定存款日期不能为空")
    private String weekDays;

    /**
     * {@link DisCountTypeEnum}
     */
    @Schema(description = "优惠方式 0:阶梯次数 1:固定次数")
    @NotNull(message = "优惠方式不能为空")
    private Integer discountType;

    /**
     * 参与方式,0.手动参与，1.自动参与
     * {@link ActivityParticipationModeEnum}
     */

    @Schema(description = "参与方式 0:手动参与 1:自动参与")
    @NotNull(message = "参与方式不能为空")
    private Integer participationMode;

    /**
     * 派发方式
     * {@link ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式 0:玩家自领-过期作废 1:玩家自领-过期自动派发 2:立即派发")
    @NotNull(message = "派发方式不能为空")
    private Integer distributionType;

    /**
     * 通用配置 当没有设置游戏大类
     */
    @Schema(description = "当配置游戏大类，每一个游戏的奖励配置")
    @I18nField
    private List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS;

    /**
     * 对应的活动条件值
     */
    private String conditionalValue;

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


    private BigDecimal washRatio;
    private String platformOrFiatCurrency;

    @Schema(description = "匹配条件 优惠方式=百分比时 ")
    private List<AssignDayCondV2VO> percentCondVO;

    @Schema(description = "匹配条件 优惠方式=固定金额时")
    private List<ActivityAssignDayCondV2VO> fixCondVOList;
}
