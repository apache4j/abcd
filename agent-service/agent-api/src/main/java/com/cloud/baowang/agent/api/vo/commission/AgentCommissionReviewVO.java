package com.cloud.baowang.agent.api.vo.commission;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 11:49
 * @description: 代理佣金审核记录对象
 */
@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "代理佣金审核记录对象")
public class AgentCommissionReviewVO extends BaseVO {
    @Schema(title = "操作 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_OPERATION)
    private Integer reviewOperation;
    @Schema(description = "操作")
    private String reviewOperationText;

    @Schema(description = "站点code")
    private String siteCode;

    /** 代理ID */
    @Schema(description = "代理ID")
    private String agentId;

    /** 代理账号 */
    @Schema(description = "代理账号")
    @ExcelProperty(value = "代理账号", order = 2)
    @ColumnWidth(25)
    private String agentAccount;

    /** 代理姓名 */
    @Schema(description = "代理姓名")
    private String agentName;

    /** 订单号 */
    @Schema(description = "订单号")
    @ExcelProperty(value = "订单号", order = 1)
    @ColumnWidth(25)
    private String orderNo;

    /** 账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开) */
    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private String agentStatus;

    @Schema(description = "账号状态名称")
    private String agentStatusText;

    /** {@link CommissionTypeEnum}*/
    /** 佣金类型 */
    @Schema(description = "佣金类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_TYPE)
    private String commissionType;

    @Schema(description = "佣金类型名称")
    @ExcelProperty(value = "佣金类型", order = 4)
    @ColumnWidth(25)
    private String commissionTypeText;

    /** 佣金金额 */
    @Schema(description = "申请佣金金额")
    @ExcelProperty(value = "申请佣金金额", order = 8)
    @ColumnWidth(25)
    private BigDecimal commissionAmount;

    /** 结算开始时间 */
    @Schema(description = "结算开始时间")
    private Long startTime;

    /** 结算结束时间 */
    @Schema(description = "结算结束时间")
    private Long endTime;

    /** 申请时间 */
    @Schema(description = "申请时间")
    private Long applyTime;

    /** 重算时间 */
    @Schema(description = "重算时间")
    private Long settleTime;

    /** 是否重算  0 不是  1是 */
    @Schema(description = "是否重算 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private Integer settleStatus;

    @Schema(description = "是否重算名称")
    private String settleStatusText;

    @Schema(description = "币种")
    @ExcelProperty(value = "币种", order = 7)
    @ColumnWidth(25)
    private String currency;

    /**结算周期  1 自然日 2 自然周  3 自然月*/
    @Schema(description = "结算周期")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期名称")
    @ExcelProperty(value = "结算周期", order = 5)
    @ColumnWidth(25)
    private String settleCycleText;

    /** 一审开始时间 */
    @Schema(description = "一审开始时间")
    private Long oneReviewStartTime;

    /** 一审完成时间 */
    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    /** 订单状态*/
    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_OPERATION)
    private Integer orderStatus;

    @Schema(description = "审核状态名称")
    @ExcelProperty(value = "订单状态", order = 3)
    @ColumnWidth(25)
    private String orderStatusText;

    @Schema(description = "锁单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    @Schema(description = "锁单状态")
    private String lockStatusText;

    /** 锁单人 */
    @Schema(description = "锁单人")
    private String locker;

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(description = "结算开始时间+结算结束时间")
    private String startTime_$_endTime;

    @Schema(description = "结算时间范围")
    private String settleTimeScope;

    @Schema(description = "审核时间")
    private String reviewTime;

    @Schema(description = "审核用时")
    private String reviewTimeTotal;

    @Schema(description = "二审备注")
    private String secondReviewRemark;



    public String getStartTime_$_endTime() {
        return String.valueOf(startTime);
    }

    public String getReviewTime() {
        if (oneReviewFinishTime == null) return null;
        return TimeZoneUtils.formatTimestampToTimeZone(oneReviewFinishTime, CurrReqUtils.getTimezone());
    }

    public String getReviewTimeTotal() {
        if (oneReviewFinishTime == null || oneReviewStartTime == null) return null;
        Long userTime = (oneReviewFinishTime - oneReviewStartTime);
        return DateUtils.formatTime(userTime);
    }

}
