package com.cloud.baowang.system.api.vo.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="修改皮肤状态对象")
public class SkinEditVO {

    @Schema(description ="id")
    @NotNull
    private String id;

    @Schema(description ="状态;1-启用,0-禁用 字典CODE:ENABLE_DISABLE_TYPE")
    @NotNull
    private Integer status;

    @Schema(description ="updaterName", hidden = true)
    private String updaterName;

}
