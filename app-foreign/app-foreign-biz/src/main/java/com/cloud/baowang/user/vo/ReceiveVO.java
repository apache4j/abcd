package com.cloud.baowang.user.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Schema
@Data
public class ReceiveVO implements Serializable {
    @Schema(description = "主键")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;
    //BenefitTypeEnum
    /**
     * {@link com.cloud.baowang.user.vo.enums.BenefitTypeEnum}
     */
    @Schema(description = "福利类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer welfareCenterRewardType;
}
