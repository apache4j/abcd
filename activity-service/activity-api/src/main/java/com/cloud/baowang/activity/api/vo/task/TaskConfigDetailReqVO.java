package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务任务查看配置list请求入参")
public class TaskConfigDetailReqVO implements Serializable {

    @Schema(description = "主键id")
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    private String id;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

}
