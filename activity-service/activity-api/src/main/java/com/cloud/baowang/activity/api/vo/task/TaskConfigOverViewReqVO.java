package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务是否展示请求入参")
public class TaskConfigOverViewReqVO implements Serializable {

    @Schema(description = "任务配置1展开，2-隐藏，默认是1")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer expandStatus;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;
    /**
     * 站点code
     */
    @Schema(title = "操作人", hidden = true)
    private String operator;
}
