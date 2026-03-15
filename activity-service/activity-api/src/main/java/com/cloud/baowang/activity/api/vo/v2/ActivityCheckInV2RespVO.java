package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.CheckInRewardConfigVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 签到
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityCheckInV2RespVO extends ActivityBaseV2RespVO implements Serializable {

    @Schema(description = "前端忽略该字段", hidden = true)
    private String baseId;

    /**
     * 存款金额
     */
    @Schema(description = "存款金额")
    private BigDecimal depositAmount;

    /**
     * 有效投注金额
     */
    @Schema(description = "有效投注金额")
    private BigDecimal betAmount;

    /**
     * 周奖励配置
     */
    @Schema(description = "周奖励配置")
    private List<CheckInRewardConfigVO> rewardWeek;

    /**
     * 月奖励配置
     */
    @Schema(description = "月奖励配置")
    private List<CheckInRewardConfigVO> rewardMonth;

    /**
     * 累计奖励配置
     *
     */
    @Schema(description = "累计奖励配置")
    private List<CheckInRewardConfigVO>  rewardTotal;

    /**
     * 存款金额
     */
    @Schema(description = "补签存款金额")
    private BigDecimal makeDepositAmount;

    /**
     * 有效投注金额
     */
    @Schema(description = "补签有效投注金额")
    private BigDecimal makeBetAmount;

    /**
     * 补签次数限制
     */
    @Schema(description = "补签次数限制")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private Integer makeupLimit;

    /**
     * 免费旋转
     */
    @Schema(description = "免费旋转图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String freeWheelPic;

    /**
     * 免费旋转
     */
    @Schema(description = "免费旋转图标-显示")
    private String freeWheelPicFileUrl;

    /**
     * 转盘
     */
    @Schema(description = "转盘图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String spinWheelPic;

    /**
     * 免费旋转
     */
    @Schema(description = "转盘图标-显示")
    private String spinWheelPicFileUrl;

    /**
     * 奖金
     */
    @Schema(description = "奖金图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String amountPic;

    /**
     * 免费旋转
     */
    @Schema(description = "奖金图标-显示")
    private String amountPicFileUrl;

    /**
     * 补签开关（0：关闭，1：开启）
     */
    @Schema(description = "补签开关（0：关闭，1：开启")
    private Integer checkInSwitch;

    /**
     * 当日存款金额
     */
    @Schema(description = "当日存款金额")
    private BigDecimal depositAmountToday;

    /**
     * 当日投注金额
     */
    @Schema(description = "当日投注金额")
    private BigDecimal betAmountToday;

    /**
     * 极光推送开关（0：关闭，1：开启）
     */
    @Schema(description = "极光推送开关（0：关闭，1：开启）")
    private Integer pushSwitch;

    /**
     * 极光推送终端（如：ANDROID、IOS）
     */
    @Schema(description = "ANDROID、IOS")
    private String pushTerminal;



}
