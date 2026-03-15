package com.cloud.baowang.system.api.vo.site.rebate.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;



@Data
@Schema(description = "启用禁用")
public class RebateListVO {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private List<String> id;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "操作人账号",hidden = true)
    private String operatorName;
}
