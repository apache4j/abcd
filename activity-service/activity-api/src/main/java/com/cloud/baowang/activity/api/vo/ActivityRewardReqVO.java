package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRewardReqVO {

    @Schema(description = "活动ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;
}
