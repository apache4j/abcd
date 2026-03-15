package com.cloud.baowang.system.api.vo.site.rebate.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "审核列表vo")
@I18nClass
public class UserRebateAuditRspVO implements Serializable {

    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "审核员")
    private String auditAccount;


    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "会员段位code")
    private String vipRankCode;

    @Schema(description = "会员段位名称")
    @I18nField
    private String vipRankName;

    @Schema(description = "统计日期")
    private String statisticsDateStr;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "有效投注")
    private BigDecimal validAmount;

    @Schema(description = "返水金额")
    private BigDecimal rebateAmount;

    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(description = "审核状态名称")
    private String orderStatusText;

 /*   @Schema(description = "申请时间")
    private Long createdTime;*/

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "审核用时-秒")
    private Long auditTimeSec;

    @Schema(description = "审核时间")
    private Long auditTime;



}
