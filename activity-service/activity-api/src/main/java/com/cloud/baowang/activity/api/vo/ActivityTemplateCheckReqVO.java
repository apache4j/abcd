package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(title = "活动模板校验对象")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityTemplateCheckReqVO {

    @Schema(description = "活动模板")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityTemplate;

}
