package com.cloud.baowang.job.api.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/13 13:31
 * @Version: V1.0
 **/
@Data
public class JobUpdateAndStartVo {
    @NotNull(message = "执行器不能为空")
    private String executorHandler;		    // 执行器，任务Handler名称
    @NotNull(message = "调度配置不能为空")
    private String scheduleConf;			// 调度配置，值含义取决于调度类型

}
