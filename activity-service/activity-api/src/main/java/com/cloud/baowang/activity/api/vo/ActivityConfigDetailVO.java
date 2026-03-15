package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.vo.v2.contestPayOut.ContestPayoutV2VO;
import com.cloud.baowang.activity.api.vo.v2.newHand.*;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "活动配置详情")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class ActivityConfigDetailVO {

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    @Schema(description = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureI18nCode;

    @Schema(description = "入口图-PC端，app获取绝对路径，展示", required = true)
    private String entrancePictureI18nCodeFileUrl;

    @Schema(description = "入口图-移动端-黑夜-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureBlackI18nCode;

    @Schema(description = "入口图-移动端-黑夜-codeFileUrl", required = true)
    private String entrancePictureBlackI18nCodeFileUrl;

    @Schema(description = "入口图-PC端", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcI18nCode;
    @Schema(description = "入口图-PC端，app获取绝对路径，展示", required = true)
    private String entrancePicturePcI18nCodeFileUrl;

    @Schema(description = "入口图-PC端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcBlackI18nCode;

    @Schema(description = "入口图-PC端-黑夜-codeFileUrl", required = true)
    private String entrancePicturePcBlackI18nCodeFileUrl;

    @Schema(description = "活动头图-移动端", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureI18nCode;

    @Schema(description = "活动头图-移动端，app获取绝对路径，展示", required = true)
    private String headPictureI18nCodeFileUrl;

    @Schema(description = "活动头图-移动端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureBlackI18nCode;

    @Schema(description = "活动头图-移动端-黑夜-codeFileUrl", required = true)
    private String headPictureBlackI18nCodeFileUrl;

    @Schema(description = "活动头图-PC端", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcI18nCode;

    @Schema(description = "活动头图-PC端app获取绝对路径，展示", required = true)
    private String headPicturePcI18nCodeFileUrl;

    @Schema(description = "活动头图-PC端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcBlackI18nCode;

    @Schema(description = "活动头图-PC端-黑夜-codeFileUrl", required = true)
    private String headPicturePcBlackI18nCodeFileUrl;

    @Schema(description = "未登录首页浮动图标(移动端)-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconAppI18nCode;

    @Schema(description = "未登录首页浮动图标(移动端)-codeFileUrl", required = true)
    private String floatIconAppI18nCodeFileUrl;

    @Schema(description = "未登录首页浮动图标(PC端)-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconPcI18nCode;

    @Schema(description = "未登录首页浮动图标(PC端)-codeFileUrl", required = true)
    private String floatIconPcI18nCodeFileUrl;

    @Schema(description = "活动对象-0:全体会员,1:新注册会员", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_USER_TYPE)
    private Integer userType;

    @Schema(description = "活动对象-多语言名称", required = true)
    private String userTypeText;

    @Schema(title = "活动时效 0: 限时,1:永久")
    private Integer activityDeadline;

    @Schema(title = "活动开始时间", required = true)
    private Long activityStartTime;

    @Schema(title = "活动结束时间", required = true)
    private Long activityEndTime;

    @Schema(description = "活动描述-多语言", required = true)
    @I18nField
    private String activityDescI18nCode;

    @Schema(description = "活动规则-多语言", required = true)
    @I18nField
    private String activityRuleI18nCode;

    @Schema(description = "存款总金额", required = true)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositAmount;

    @Schema(description = "存款总金额-货币类型", required = true)
    private String depositCurrencyCode;

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

    @Schema(description = "参与资格:true=可以参与,false=不可以参与")
    private Boolean activityCondition;

    @Schema(description = "参与方式,0.手动参与，1.自动参与")
    private Integer participationMode;


    @Schema(description = "状态CODE,10000=立即参与,30047=已参与过该活动")
    private Integer status;

    //@I18nField
    @Schema(description = "活动状态 0 未开启， 1 ，开启, 2，已结束")
    private int openStatus;


    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "用户号", hidden = true)
    private String userId;

    @Schema(title = "用户账号", hidden = true)
    private String userAccount;

    @Schema(title = "设备类型", hidden = true)
    private Integer reqDeviceType;

    @Schema(title = "时区", hidden = true)
    private String timeZone;

    @Schema(description = "活动描述-多语言")
    @I18nField
    private String activityIntroduceI18nCode;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private String venueType;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String venueTypeText;


    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    /**
     *
     */
    @Schema(title = "会员是否选择 0 -未选择，1-选择")
    private String selectFlag;

    @Schema(title = "会员选择的游戏大类")
    private String selectVenueType;


    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private List<VenueValueVO> venueTypeList;

    @Schema(description = "对应选择了游戏大类-首存/次存")
    private List<DepositConfigDTO> depositConfigDTOS;


    @Schema(description = "对应选择了游戏大类-指定日存款")
    private List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS;

    //NOTE 新手活动的相关内容
    @Schema(description = "新手活动首次充值")
    private ConditionFirstDepositRespVO conditionFirstDepositRespVO;

    @Schema(description = "新手活动首次提现")
    private ConditionFirstWithdrawalRespVO conditionFirstWithdrawalRespVO;

    @Schema(description = "新手活动签到")
    private ConditionSignInRespVO conditionSignInRespVO;

    @Schema(description = "新手活动负盈利")
    private ConditionNegativeProfitRespVO conditionNegativeProfitRespVO;


    @Schema(description = "赛事包赔")
    private ContestPayoutV2VO contestPayoutVO;

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;

    @Schema(description = "0 未登陆， 1 新用户， 2 老用户")
    private int newbieStatus;

}
