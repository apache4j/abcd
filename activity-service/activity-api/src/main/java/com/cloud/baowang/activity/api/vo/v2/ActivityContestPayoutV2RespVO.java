package com.cloud.baowang.activity.api.vo.v2;


import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.io.Serializable;

import java.util.List;

/**
 * @author brence
 * @date 2025-10-20
 * @desc 赛事包赔--体育类
 */
@Schema(description = "赛事包赔活动响应")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityContestPayoutV2RespVO extends ActivityBaseV2RespVO implements Serializable {


    @Schema(description = "活动Id")
    private String activityId;

    /**
     * 活动适用范围 0:全体会员 1:新注册会员
     * {@link com.cloud.baowang.activity.api.enums.ActivityScopeEnum}
     */
    @Schema(description = "活动适用范围 0:全体会员 1:新注册会员")
    @NotNull(message = "活动适用范围不能为空")
    private String activityScope;

    /**
     * 通用配置 设置活动大类

    @Schema(description = "活动大类.默认只有体育类")
    @I18nField
    private List<ActivityContestPayoutVenueV2VO> activityContestPayoutVenueVOS;
     */
    /**
     * 场馆编码
     * system_param "venue_type"
     */
    @Schema(description = "场馆编码")
    @NotNull(message = "场馆不能为空")
    private String venueCode;

    @Schema(description = "活动访问参数,游戏id")
    @NotNull(message = "活动访问参数,游戏id")
    private String accessParameters;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     *
     */
    @Schema(title = "活动类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;


    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdADayAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端白天图")
    private List<I18nMsgFrontVO> thirdADayAppI18nCodeList;
    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdANightAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    private List<I18nMsgFrontVO> thirdANightAppI18nCodeList;
    /**
     * 三方A赛事推荐图- PC端白天图
     */
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdADayPcI18nCode;
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    private List<I18nMsgFrontVO> thirdADayPcI18nCodeList;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdBDayAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端白天图")
    private List<I18nMsgFrontVO> thirdBDayAppI18nCodeList;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdBNightAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    private List<I18nMsgFrontVO> thirdBNightAppI18nCodeList;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    @Schema(title = "三方B赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String thirdBDayPcI18nCode;

    @Schema(title = "三方B赛事推荐图- PC端白天图")
    private List<I18nMsgFrontVO> thirdBDayPcI18nCodeList;


    @Schema(description = "0:平台币, 1: 法币")
    private String platformCurrency;
}
