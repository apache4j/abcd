package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Schema(description = "签到活动历史")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class CheckInRecordRespVO implements Serializable {

    @Schema(description = "这个月年月 2025/04")
    private String currentDate;

    @Schema(description = "上个月的年月 2025/04")
    private String lastDate;

    @Schema(description = "今天是否签到")
    private Boolean isSignedToday = false;

    @Schema(description = "剩余补签次数")
    private Integer makeupCount = 0;

    @Schema(description = "补签功能是否开启，true-开启，false-关闭")
    private Boolean makeupFlagStatus = false;



    /*@Schema(description = "实际获取补签次数，如果没有配置补签条件，这个就是实际已经使用的补签次数")
    private Integer makeupAlreadyReceiveCount = 0;

    @Schema(description = "实际使用获取补签次数")
    private Integer makeupAlreadyUseCount = 0;

    @Schema(description = "配置的补签次数-实际使用的补签次数差值（如果没有配置补签条件，则是配置最大补签次数-实际使用补签次数），-是否获取补签值达到上限")
    private Boolean isMakeupLimitReached = false;
*/
    @Schema(description = "今天是否补签过-")
    private Boolean isMakeupFlag = false;

    @Schema(description = "当月月数据")
    private List<CheckInRecordVO> currentMonth;

    @Schema(description = "上月数据")
    private List<CheckInRecordVO> lastMonth;

    /**
     * 免费旋转
     */
    @Schema(description = "免费旋转图标")
    private String freeWheelPic;

    /**
     * 转盘
     */
    @Schema(description = "转盘图标")
    private String spinWheelPic;

    /**
     * 奖金
     */
    @Schema(description = "奖金图标")
    private String amountPic;

    @Schema(description = "累计奖励配置")
    private List<CheckInRewardConfigRespVO> TotalRewardConfigs;

    /*@Schema(description = "额外-当月奖励配置")
    private CheckInRewardConfigVO currentMonthConfig;
    @Schema(description = "额外-上月奖励配置")
    private CheckInRewardConfigVO lastMonthConfig;*/
    @Schema(description = "倒计时-秒")
    private Long remainingTime;



    @Schema(description = "活动对象-0:全体会员,1:新注册会员", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_USER_TYPE)
    private Integer userType;

    @Schema(description = "活动对象-多语言名称", required = true)
    private String userTypeText;




    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    @Schema(title = "活动时效 0-限时,1-长期")
    private Integer activityDeadline;


    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    private Long activityEndTime;

    /**
     * 活动规则,多语言
     */
    @Schema(description = "活动规则,多语言")
    @I18nField
    private String activityRuleI18nCode;

    /**
     * 活动描述,多语言
     */
    @Schema(description = "活动描述,多语言")
    @I18nField
    private String activityDescI18nCode;

    @Schema(description = "活动简介-多语言")
    @I18nField
    private String activityIntroduceI18nCode;






}
