package com.cloud.baowang.activity.api.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务任务配置list请求入参")
public class TaskConfigReqVO implements Serializable {

    @Schema(description = "novice-新人任务,daily-每日任务,week-每周任务")
    private String taskType;
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
