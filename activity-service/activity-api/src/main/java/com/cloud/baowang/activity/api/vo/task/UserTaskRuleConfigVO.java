package com.cloud.baowang.activity.api.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务客户端 任务规则说明")
public class UserTaskRuleConfigVO  implements Serializable {
    /**
     * 每日任务规则
     */
    @Schema(title = "每日任务规则")
    private String dailyTaskRule;


    /**
     * 每日任务规则
     */
    @Schema(title = "每周任务规则")
    private String weekTaskRule;
}
