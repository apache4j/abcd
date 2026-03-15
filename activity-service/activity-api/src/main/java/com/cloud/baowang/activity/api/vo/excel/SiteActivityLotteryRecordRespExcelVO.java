package com.cloud.baowang.activity.api.vo.excel;

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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SiteActivityLotteryRecordRespVO
 * @author: wade
 * @description: 转盘活动抽奖记录响应
 * @date: 10/9/24 21:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(description = "转盘活动抽奖记录响应-下载")
@ExcelIgnoreUnannotated
public class SiteActivityLotteryRecordRespExcelVO implements Serializable {

    /**
     * 会员账号
     */
    @ExcelProperty("会员账号")
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "vip段位名称")
    @ExcelProperty(value = "VIP段位")
    @ColumnWidth
    private String vipRankCodeName;






    /**
     * 站点code
     */
    @Schema(description = "站点编码")
    private String siteCode;



    /**
     * VIP等级code
     */
    @Schema(description = "VIP等级编码")
    private Integer vipGradeCode;
    /**
     * VIP等级code
     */
    @Schema(description = "vip等级名称")
    @ExcelProperty("VIP等级")
    private String vipGradeCodeName;

    /**
     * 获取来源 system-param(activity_prize_source)
     */
    @Schema(description = "获取来源 1-存款赠送 2-流水赠送 system-param(activity_prize_source_show) ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_PRIZE_SOURCE_SHOW)
    private String prizeSource;

    /**
     * 获取来源 system-param(activity_prize_source)
     */
    @Schema(description = "获取来源 1-存款赠送 2-流水赠送 system-param(activity_prize_source) ")
    @ExcelProperty("获取来源")
    private String prizeSourceText;

    /**
     * 操作前抽奖次数
     */
    @Schema(description = "操作前抽奖次数")
    @ExcelProperty("原次数")
    private Integer startCount;

    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "创建时间")
    @ExcelProperty("获取时间")
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }



    /**
     * 获取的抽奖次数
     */
    @Schema(description = "获取的抽奖次数")
    @ExcelProperty("获取次数")
    private Integer rewardCount;

    /**
     * 操作后抽奖次数
     */
    @ExcelProperty("获取后次数")
    @Schema(description = "操作后抽奖次数")
    private Integer endCount;

    /**
     * 会员ID
     */
    @Schema(description = "会员ID")
    private String userId;



}
