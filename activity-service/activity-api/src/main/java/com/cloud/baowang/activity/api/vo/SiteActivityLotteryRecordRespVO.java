package com.cloud.baowang.activity.api.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
@Schema(description = "转盘活动抽奖记录响应")
public class SiteActivityLotteryRecordRespVO implements Serializable {
    /**
     * 站点code
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * VIP段位code
     */
    @Schema(description = "VIP段位编码")
    private Integer vipRankCode;

    /**
     * VIP段位code
     */
    @Schema(description = "VIP段位名称")
    @I18nField
    private String vipRankCodeName;

    /**
     * VIP等级code
     */
    @Schema(description = "VIP等级编码")
    private Integer vipGradeCode;
    /**
     * VIP等级code
     */
    @Schema(description = "vip等级名称")
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
    private String prizeSourceText;

    /**
     * 操作前抽奖次数
     */
    @Schema(description = "操作前抽奖次数")
    private Integer startCount;

    /**
     * 获取的抽奖次数
     */
    @Schema(description = "获取的抽奖次数")
    private Integer rewardCount;

    /**
     * 操作后抽奖次数
     */
    @Schema(description = "操作后抽奖次数")
    private Integer endCount;

    /**
     * 会员ID
     */
    @Schema(description = "会员ID")
    private String userId;

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "创建时间")
    private String createdTimeStr;

    public String getCreatedTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
}
