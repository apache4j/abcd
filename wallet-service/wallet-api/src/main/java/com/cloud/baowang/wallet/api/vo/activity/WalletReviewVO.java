package com.cloud.baowang.wallet.api.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title= "审核结果 Request")
public class WalletReviewVO {

    @Schema(title = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "提交审核信息")
    private String reviewRemark;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;
    @Schema(title = "操作人",hidden = true)
    private String operator;

    @Schema(title = "时区",hidden = true)
    private String timeZone;
}
