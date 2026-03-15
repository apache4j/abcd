package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 证金调整审核表
 * </p>
 *
 * @author ford
 * @since 2025-06-27
 */
@Data
@Schema(description = "保证金审核详情返回对象")
@I18nClass
public class SiteSecurityAdjustReviewDetailVO {
    @Schema(description = "id")
    private String id;

    @Schema(description = "审核单号")
    private String reviewOrderNumber;

    @Schema(description = "申请人")
    private String applyUser;

    @Schema(description = "申请时间")
    private Long applyTime;


    @Schema(description = "审核人")
    private String firstReviewer;

    @Schema(description = "审核时间")
    private Long firstReviewTime;

    @Schema(description = "审核备注")
    private String reviewRemark;


    @Schema(description = " 调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_SECURITY_REVIEW)
    private Integer adjustType;

    @Schema(description = "调整类型")
    private String adjustTypeText;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = " 调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请原因")
    private String remark;

    @Schema(description = "站点备注")
    private String siteRemark;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "所属公司")
    private String company;

    @Schema(description = "站点管理员账号")
    private String siteAdminAccount;

    /* 站点类型 */
    @Schema(description = "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型文本")
    private String siteTypeText;

    @Schema(description = "站点状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_STATUS)
    private Integer status;
    @Schema(description = "站点状态")
    private String statusText;
}
