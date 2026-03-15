package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 证金调整审核表
 * </p>
 *
 * @author ford
 * @since 2025-06-27
 */
@Data
@Schema(description = "保证金审核记录返回对象")
@I18nClass
public class SiteSecurityAdjustReviewLogVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "站点name")
    private String siteName;

    @Schema(description = "申请时间")
    private Long applyTime;
    @Schema(description = "申请备注")
    private String remark;


    @Schema(description = "审核完成时间")
    private Long firstReviewTime;


    @Schema(description = "审核用时")
    private BigDecimal reviewTotalTime;

    @Schema(description = "审核用时字符串")
    private String reviewTotalTimeStr;

    public BigDecimal getReviewTotalTime() {
        if (reviewTotalTime == null) {
            return BigDecimal.ZERO;
        }
        // 四舍五入，保留0位小数
        return reviewTotalTime.setScale(0, RoundingMode.DOWN);
    }

    @Schema(description = "审核单号")
    private String reviewOrderNumber;

    @Schema(description = "申请人")
    private String applyUser;

    @Schema(description = "审核人")
    private String firstReviewer;


    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_SECURITY_REVIEW)
    private Integer adjustType;

    @Schema(description = "调整类型")
    private String adjustTypeText;

    @Schema(description = " 调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "审核备注")
    private String reviewRemark;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private String reviewStatusText;

    @Schema(description = "保证金账户状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SECURITY_ACCOUNT_STATUS)
    private Integer accountStatus;

    @Schema(description = "保证金账户状态")
    private String accountStatusText;
}
