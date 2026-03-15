package com.cloud.baowang.wallet.api.vo.siteSecurity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 17:22
 * @Version: V1.0
 **/
@Data
@Schema(title ="保证金透支金额设置")
public class SiteSecurityBalanceOverdrawAmountReqVO {

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称",hidden = true)
    private String siteName;

    @Schema(description = "币种",hidden = true)
    private String currency;

    /**
     * {@link com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums}
     */
    @Schema(description = "调整类型")
    private Integer adjustType;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请原因")
    private String remark;

    @Schema(description = "最近修改人",hidden = true)
    private String updateUser;

}
