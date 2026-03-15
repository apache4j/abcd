package com.cloud.baowang.user.api.vo.user.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author: kimi
 */
@Data
@Schema(title = "备注编辑 Param")
public class EditRemarkParam {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(title = "会员账号")
    @NotEmpty(message = "会员账号不能为空")
    private String userAccount;

    @Schema(title = "会员备注")
    @NotEmpty(message = "会员备注不能为空")
    private String acountRemark;
}
