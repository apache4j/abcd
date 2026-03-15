package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: qiqi
 */
@Data
@Schema(description = "会员平台币上分记录 返回")
@I18nClass
public class UserPlatformCoinManualUpRecordResponseVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员注册信息")
    private String userAccount;

    @Schema(description = "VIP等级code")
    private Integer vipGradeCode;

    @Schema(description = "vip等级名称")
    private String vipGradeCodeName;

    @Schema(description = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(description = "调整方式 - Name")
    private String adjustWayText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "订单状态 - Name")
    private String auditStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型 - Name")
    private String adjustTypeText;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "平台币币种")
    private String platformCurrencyCode;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "备注")
    private String applyReason;

}
