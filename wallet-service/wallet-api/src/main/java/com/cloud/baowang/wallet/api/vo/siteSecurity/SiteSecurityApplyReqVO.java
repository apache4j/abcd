package com.cloud.baowang.wallet.api.vo.siteSecurity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "发起审核对象")
public class SiteSecurityApplyReqVO {
    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "币种",hidden = true)
    private String currency;

    /**
     * {@link com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums
     */
    @Schema(description = "调整类型")
    private Integer adjustType;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请原因")
    private String remark;

}
