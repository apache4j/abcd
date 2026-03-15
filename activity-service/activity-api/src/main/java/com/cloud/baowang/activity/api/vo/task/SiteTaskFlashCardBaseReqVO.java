package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/22 13:37
 * @Version: V1.0
 **/
@Data
@Schema(description = "主键ID查询条件")
public class SiteTaskFlashCardBaseReqVO {
    @Schema(description = "主键ID")
    private String id;

    /**
     * 站点code
     */
    @Schema(title = "站点code",hidden = true)
    private String siteCode;


    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String taskType;

    @Schema(description = "操作人", hidden = true)
    private String operator;
}
