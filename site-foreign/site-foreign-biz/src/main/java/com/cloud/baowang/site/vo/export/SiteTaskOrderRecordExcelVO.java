package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.activity.api.enums.task.TaskDistributionTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "任务领取记录信息")
public class SiteTaskOrderRecordExcelVO {
    /**
     * 站点code
     */
    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @ExcelProperty(value = "订单号")
    private String orderNo;

    /**
     * 所属活动
     */
    @ExcelProperty(value = "任务ID")
    @Schema(description = "任务ID")
    private String taskId;

    /**
     * 活动类型
     * {@link com.cloud.baowang.activity.api.enums.task.TaskEnum}
     */
    @Schema(description = "任务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TASK_TYPE)
    private String taskType;

    @Schema(description = "任务类型")
    @ExcelProperty(value = "任务类型")
    private String taskTypeText;


    /**
     * 所属活动
     */
    @I18nField
    @Schema(description = "任务名称")
    @ExcelProperty(value = "任务名称")
    private String taskName;

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    private String userAccount;

    /**
     * 活动赠送金额
     */
    @Schema(description = "奖励")
    @ExcelProperty(value = "奖励")
    private BigDecimal taskAmount;


    /**
     * 币种
     */
    @Schema(description = "币种代码")
    @ExcelProperty(value = "币种")
    private String currencyCode;

    /**
     * 领取状态
     */
    @Schema(description = "领取状态 字典CODE:activity_receive_status")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TASK_RECEIVE_STATUS)
    private Integer receiveStatus;

    /**
     * 领取状态
     */
    @Schema(description = "领取状态 字典CODE:activity_receive_status")
    @ExcelProperty(value = "状态")
    private String receiveStatusText;

    /**
     * 可领取开始时间
     */
    @Schema(description = "可领取开始时间")
    private Long receiveStartTime;


    /**
     * 领取时间
     */
    @ExcelProperty(value = "发放时间")
    @Schema(description = "发放时间")
    private String receiveStartTimeStr;

    public String getReceiveStartTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(receiveStartTime, CurrReqUtils.getTimezone());
    }

    /**
     * 领取时间
     */
    @Schema(description = "领取时间")
    private Long receiveTime;

    /**
     * 领取时间
     */
    @Schema(description = "领取时间")
    @ExcelProperty(value = "领取时间")
    private String receiveTimeStr;


    public String getReceiveTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(receiveTime, CurrReqUtils.getTimezone());
    }

    /**
     * 会员ID
     */
    @Schema(description = "会员ID")
    private String userId;

    /**
     * 会员姓名
     */
    @Schema(description = "会员姓名")
    private String userName;


    /**
     * 代理账号
     */
    @Schema(description = "代理账号")
    private String agentUserId;

    /**
     * VIP等级
     */
    @Schema(description = "VIP等级")
    private Integer vipGradeCode;

    /**
     * VIP段位
     */
    @Schema(description = "VIP段位")
    private Integer vipRankCode;

    /**
     * 派发方式: 0:玩家自领-过期作废，1:玩家自领-过期不作废
     * {@link TaskDistributionTypeEnum}
     */
    @Schema(description = "派发方式 (0: 玩家自领-过期作废，1: 玩家自领-过期不作废)")
    private Integer distributionType;


    /**
     * 可领取结束时间
     */
    @Schema(description = "可领取结束时间")
    private Long receiveEndTime;


    /**
     * 发放礼金时的汇率
     */
    @Schema(description = "发放礼金时的汇率")
    private BigDecimal finalRate;


    /**
     * 流水倍数
     */
    @Schema(description = "流水倍数")
    private BigDecimal washRatio;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


    /**
     * 领取时用户设备号
     */
    @Schema(description = "领取时用户设备号")
    private String deviceNo;

    /**
     * 领取时用户IP
     */
    @Schema(description = "领取时用户IP")
    private String ip;
}



