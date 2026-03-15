package com.cloud.baowang.activity.vo.mq;

import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ActivitySendMqVO extends MessageBaseVO {

    /**
     * ID
     */
    public String orderNo;

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 活动模板
     */
    private String activityTemplate;

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 派发方式: 0:玩家自领-过期作废，1:玩家自领-过期自动派发，2:立即派发
     * {@link  ActivityDistributionTypeEnum}
     */
    private Integer distributionType;

    /**
     * 可领取开始时间
     */
    private Long receiveStartTime;

    /**
     * 可领取结束时间
     */
    private Long receiveEndTime;

    /**
     * 领取状态，无需传递
     */
    private Integer receiveStatus;

    /**
     * 活动赠送金额
     */
    private BigDecimal activityAmount;


    /**
     * 币种 CommonConstant.PLAT_CURRENCY_CODE
     */
    private String currencyCode;

    /**
     * 流水倍数
     */
    private BigDecimal runningWaterMultiple;

    /**
     * 备注
     */
    private String remark;

    /**
     *  （转盘活动）奖品类型 转盘奖励段位，青铜，白银，黄金
     */
    private Integer rewardRank;

    /**
     * 转盘活动）奖品类型
     */
    private Integer prizeType;

    /**
     * 转盘活动）奖品名称
     */
    private String prizeName;

    /**
     * 本金
     */
    private BigDecimal principalAmount;

    /**
     * 发放礼金时的汇率
     */
    private BigDecimal finalRate;

    /**
     * 红包雨专属字段.场次
     */
    private String redbagSessionId;




    /**
     * 流水要求
     */
    private BigDecimal runningWater;


    /**
     * 0 手动参与 1 自动参与
     * {@link com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum}
     */
    private Integer participationMode;

    /**
     *  只统计活动记录，但是不发奖励，用于活动结束后统计，发打码量，
     *  仅是免费旋转活动，免费旋转获取彩金，添加打码量
     */
    @Builder.Default
    private Boolean sendStatus = false;

   /* public Boolean getSendStatus() {
        return this.sendStatus==null?Boolean.FALSE:this.sendStatus;
    }*/

    @Schema(description = "盘口模式:0:国际盘 1:大陆盘")
    private int handicapMode;
}
