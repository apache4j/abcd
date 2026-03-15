package com.cloud.baowang.activity.api.vo.v2.contestPayOut;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @description 赛事包赔活动-v2
 * @author BEJSON.com
 * @date 2025-10-18
 */
@Slf4j
@Data
@I18nClass
public class ContestPayoutV2VO implements Serializable {


    /**
     * 活动适用范围，0:全体会员，1:新注册会员
     * {@link com.cloud.baowang.activity.api.enums.ActivityScopeEnum }
     */
    private String activityScope;

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

    @Schema(description = "场馆编码，场馆")
    private String venueCode;

    @Schema(description = "场馆名称")
    private String venueName;

    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdADayAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端白天图")
    private String thirdADayAppI18nCodeFileUrl;
    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdANightAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    private String thirdANightAppI18nCodeFileUrl;
    /**
     * 三方A赛事推荐图- PC端白天图
     */
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdADayPcI18nCode;
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    private String thirdADayPcI18nCodeFileUrl;

    /**
     * 三方A赛事推荐图- PC端白天图
     */
    @Schema(title = "三方A赛事推荐图- PC端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdANightPcI18nCode;

    @Schema(title = "三方A赛事推荐图- PC端夜间图")
    private String thirdANightPcI18nCodeFileUrl;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdBDayAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端白天图")
    private String thirdBDayAppI18nCodeFileUrl;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdBNightAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    private String thirdBNightAppI18nCodeFileUrl;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    @Schema(title = "三方B赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdBDayPcI18nCode;

    @Schema(title = "三方B赛事推荐图- PC端白天图")
    private String thirdBDayPcI18nCodeFileUrl;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    @Schema(title = "三方B赛事推荐图- PC端夜间图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String thirdBNightPcI18nCode;

    @Schema(title = "三方B赛事推荐图- PC端夜间图")
    private String thirdBNightPcI18nCodeFileUrl;


}