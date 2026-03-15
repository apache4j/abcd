package com.cloud.baowang.user.api.vo.vip;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description
 * @auther amos
 * @create 2024-10-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
@Schema(title = "VIP奖励发放记录返回对象")
@I18nClass
public class SiteVipAwardRecordVo {

    @Schema(description = "订单号")
    @ExcelProperty(value = "订单号")
    @ColumnWidth(32)
    private String orderId;

    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    @ColumnWidth(32)
    private String userAccount;

    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型")
    @ExcelProperty(value = "账号类型")
    @ColumnWidth(32)
    private String accountTypeText;

    @Schema(description = "VIP等级")
    @ExcelProperty(value = "VIP等级")
    @ColumnWidth(32)
    private String vipGrade;

    @Schema(description = "VIP段位")
    @ColumnWidth(32)
    private String vipRankCode;

    @Schema(description = "VIP段位")
    @ExcelProperty(value = "VIP段位")
    @ColumnWidth(32)
    @I18nField
    private String vipRankCodeText;

    /**
     * 奖励类型(0:升级礼金，1:周流水,2:月流水,3:周体育流水)
     */
    @Schema(description = "奖励类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_AWARD_TYPE)
    private String awardType;

    @Schema(description = "奖励类型")
    @ExcelProperty(value = "奖励类型")
    @ColumnWidth(32)
    private String awardTypeText;

    /**
     * 领取方式(0:手动领取,1:自动领取)
     */
    @Schema(description = "领取方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_RECEIVE_TYPE)
    private String receiveType;


    @Schema(description = "领取方式")
    @ExcelProperty(value = "领取方式")
    @ColumnWidth(32)
    private String receiveTypeText;


    /**
     * 领取状态(0:未领取,1:已领取,2:已过期)
     */
    @Schema(description = "领取状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_RECEIVE_STATUS)
    private String receiveStatus;

    @Schema(description = "领取状态")
    @ExcelProperty(value = "领取状态")
    @ColumnWidth(32)
    private String receiveStatusText;

    @Schema(description = "奖励金额")
    @ExcelProperty(value = "奖励金额")
    @ColumnWidth(32)
    private BigDecimal awardAmount;

    @Schema(description = "发放时间")
    private Long createdTime;

    @ExcelProperty(value = "发放时间")
    @ColumnWidth(32)
    private String createdTimeStr;

    @Schema(description = "领取时间")
    private Long receiveTime;

    @ExcelProperty(value = "领取时间")
    @ColumnWidth(32)
    private String receiveTimeStr;

    @Schema(description = "过期时间")
    private Long expiredTime;

    @ExcelProperty(value = "过期时间")
    @ColumnWidth(32)
    private String expiredTimeStr;

    public String getExpiredTimeStr() {
        return null == expiredTime ? null : TimeZoneUtils.formatTimestampToTimeZone(expiredTime, CurrReqUtils.getTimezone());
    }

    public String getReceiveTimeStr() {
        return null == receiveTime ? null : TimeZoneUtils.formatTimestampToTimeZone(receiveTime, CurrReqUtils.getTimezone());
    }

    public String getCreatedTimeStr() {
        return null == createdTime ? null : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }




}
