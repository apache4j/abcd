package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
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
public class ActivityIdReqVO {
    @Schema(description = "主键ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    /**
     * 站点code
     */
    @Schema(title = "站点code",hidden = true)
    private String siteCode;


    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplate;
}
