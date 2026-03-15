package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务客户端 批量 领取任务结果-请求入参")
@I18nClass
public class TaskReceiveBatchAppReqVO implements Serializable {


    /**
     * 任务配置id
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "任务配置id, 如果不传id，则批量领取")
    private String id;


    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "会员id", hidden = true)
    private String userId;


}
