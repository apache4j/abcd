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

import java.math.BigDecimal;

/**
 * 会员活动记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "转盘活动中奖记录")
@I18nClass
public class ActivityOrderRecordForSpinWheelRespVO {

    /**
     * 活动模板
     * {@link com.cloud.baowang.activity.api.enums.ActivityTemplateEnum}
     */
    @Schema(description = "活动模板")
    private String activityTemplate;

    /**
     * vip段位名称，对应的转盘奖品等级名称
     */
    /*@Schema(description = "vip段位")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_RANK_SPIN_WHEEL_PRIZE)
    private Integer vipRankCode;*/


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 领取时间
     */
    @Schema(description = "领取时间")
    private Long receiveTime;

    /**
     * v转盘奖品段位
     */
    @Schema(description = "转盘奖品段位")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_RANK_SPIN_WHEEL_PRIZE)
    private Integer rewardRank;


    @Schema(description = "转盘奖品段位")
    private String rewardRankText;
    /**
     * vip段位
     */
    @Schema(description = "奖品类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_REWARD_TYPE)
    private String prizeType;


    @Schema(description = "奖品类型")
    private String prizeTypeText;

    /**
     * vip段位
     */
    @Schema(description = "奖品名称")
    private String prizeName;

    /**
     * 活动赠送金额
     */
    @Schema(description = "活动赠送金额")
    private BigDecimal activityAmount;
    /**
     * 币种
     */
    @Schema(description = "币种")
    private String currencyCode;



}
