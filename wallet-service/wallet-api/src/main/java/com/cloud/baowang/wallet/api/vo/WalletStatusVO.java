package com.cloud.baowang.wallet.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 启用禁用
 *
 * @author kimi
 */
@Data
@Schema(title = "启用禁用")
public class WalletStatusVO {

    @Schema(title = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "状态,0.解锁，1.锁单")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(title = "提交审核信息")
    private String reviewRemark;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;
}
