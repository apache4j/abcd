package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class ActivityOrderRecordRespExportVO {

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(description = "活动ID")
    @ExcelProperty("活动ID")
    @ColumnWidth(25)
    private String activityNo;

    @Schema(description = "活动名称")
    @I18nField
    @ExcelProperty("活动名称")
    @ColumnWidth(25)
    private String activityNameI18nCode;

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(25)
    private String userAccount;


    @Schema(description = "会员id")
    @ExcelProperty("会员id")
    @ColumnWidth(25)
    private String userId;


    @Schema(description = "活动奖励")
    //@ExcelProperty("活动奖励")
    @ColumnWidth(25)
    private BigDecimal activityAmount;

    @Schema(description = "活动奖励")
    @ExcelProperty("活动奖励")
    @ColumnWidth(25)
    private String activityAmountText;

    public String getActivityAmountText() {
        return activityAmount + currencyCode;
    }

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(25)
    private String currencyCode;

    @Schema(description = "领取状态 字典CODE：activity_receive_status")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_RECEIVE_STATUS)
    private Integer receiveStatus;

    @Schema(description = "领取状态 多语言")
    @ExcelProperty("状态")
    @ColumnWidth(25)
    private String receiveStatusText;


//    @Schema(description = "姓名")
//    @ExcelProperty("姓名")
//    @ColumnWidth(25)
//    private String userName;

    //
//    /**
//     * 代理账号
//     */
//    @Schema(description = "代理账号")
//    @ExcelProperty("订单号")
//    @ColumnWidth(25)
//    private String agentUserId;
//
//    @Schema(description = "vip段位")
//    @ExcelProperty("vip段位")
//    @ColumnWidth(25)
//    private Integer vipRank;
//    /**
//     * 派发方式: 0:玩家自领-过期作废，1:玩家自领-过期自动派发，2:立即派发
//     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
//     */
//    @Schema(description = "派发方式: 0:玩家自领-过期作废，1:玩家自领-过期自动派发，2:立即派发")
//    @ExcelProperty("订单号")
//    @ColumnWidth(25)
//    private Integer distributionType;
//
//    @Schema(description = "可领取开始时间")
//    @ExcelProperty("可领取开始时间")
//    @ColumnWidth(25)
//    private Long receiveStartTime;
//
//    @Schema(description = "可领取结束时间")
//    @ExcelProperty("可领取结束时间")
//    @ColumnWidth(25)
//    private Long receiveEndTime;
//
//
//
//
//
//    @Schema(description = "流水倍数")
//    @ExcelProperty("流水倍数")
//    @ColumnWidth(25)
//    private BigDecimal runningWaterMultiple;
//
//    @Schema(description = "备注")
//    @ExcelProperty("备注")
//    @ColumnWidth(25)
//    private String remark;
//
    @Schema(description = "领取时间")
    private Long receiveTime;

    @ExcelProperty("领取时间")
    @ColumnWidth(25)
    private String receiveTimeStr;

    public String getReceiveTimeStr() {
        return receiveTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(receiveTime, CurrReqUtils.getTimezone());
    }

    @Schema(description = "发放时间")
    private Long createdTime;

    @Schema(description = "发放时间")
    @ExcelProperty("发放时间")
    @ColumnWidth(25)
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(createdTime, CurrReqUtils.getTimezone());
    }

    //    /**
//     * 领取时用户-设备号
//     */
//    @Schema(description = "领取时用户-设备号")
//    @ExcelProperty("领取时用户-设备号")
//    @ColumnWidth(25)
//    private String deviceNo;
//    /**
//     * 领取时用户-ip
//     */
//    @Schema(description = "领取时用户-ip")
//    @ExcelProperty("领取时用户-ip")
//    @ColumnWidth(25)
//    private String ip;
//
//
//    /**
//     * 发放时间
//     */


}
