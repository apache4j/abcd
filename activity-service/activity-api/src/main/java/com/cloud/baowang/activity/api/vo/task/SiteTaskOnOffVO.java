package com.cloud.baowang.activity.api.vo.task;


import com.cloud.baowang.common.core.constants.ConstantsCode;
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
@Schema(title = "任务启用入参")
public class SiteTaskOnOffVO {
    @Schema(title = "id")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(title = "启用1，禁止0")
    private Integer status;

    @Schema(title = "站点siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "operator", hidden = true)
    private String operator;
}
