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
import java.math.BigDecimal;
import java.util.List;

/**
 * 转盘
 */
@Data
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "转盘活动返回对象")
public class ActivitySpinWheelRespVO extends ActivityBaseRespVO implements Serializable {


    /**
     * 存款是否勾选，0未勾选，1勾选
     */
    @Schema(description = "存款是否勾选，0未勾选，1勾选")
    private Integer depositSwitch;

    public Integer getDepositSwitch() {
        return depositAmount == null ? 0 : 1;
    }

    /**
     * 存款奖励次数
     */
    @Schema(description = "存款奖励次数")
    private Integer depositTimes;


    /**
     * 存款金额
     */
    @Schema(description = "存款金额")
    private BigDecimal depositAmount;

    /**
     * 投注是否勾选，0未勾选，1勾选
     */
    @Schema(description = "投注是否勾选，0未勾选，1勾选")
    private Integer betSwitch;

    public Integer getBetSwitch() {
        return betAmount == null ? 0 : 1;
    }

    /**
     * 投注流水
     */
    @Schema(description = "投注流水")
    private BigDecimal betAmount;
    /**
     * 投注奖励次数
     */
    @Schema(description = "投注奖励次数")
    private Integer betTimes;
    /**
     * 转盘初始获得金额
     */
    @Schema(description = "转盘初始获得金额")
    private BigDecimal initAmount;

    /**
     * 每位会员每日可领取次数上限，0-当选择全部会员的时候，1-是根据VIP等级限制会员领取次数
     */
    @Schema(description = "每位会员每日可领取次数上限类型 (0-全部会员, 1-根据VIP等级限制)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_LIMIT_TYPE)
    private Integer maxTimeType;

    @Schema(description = "每位会员每日可领取次数上限类型 (0-全部会员, 1-根据VIP等级限制)")
    private String maxTimeTypeText;

    /**
     * 每位会员每日可领取次数上限，当选择全部会员的时候
     */
    @Schema(description = "每位会员每日可领取次数上限 (适用于全部会员)")
    private Integer maxTimes;

    @Schema(description = "转盘奖励配置入参")
    private List<SiteActivityRewardSpinWheelResVO> rewardSpinWheel;

    @Schema(description = "转盘vip配置")
    private List<SiteActivityRewardVipGradeReqVO> rewardVipGrade;

    public boolean validateActivitySpinWheel() {
        return false;
    }

    @Schema(description = "操作人", hidden = true)
    private String operator;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
