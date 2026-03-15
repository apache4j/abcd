package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "银行卡管理/虚拟币账号管理-启用/禁用 Request")
public class EnableOrDisableVO {

    @Schema(description =  "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description =  "备注信息")
    @NotEmpty(message = "备注信息不能为空")
    @Size(min = 2, max = 50, message = "备注信息在2-50个字符之间")
    private String remark;
}
