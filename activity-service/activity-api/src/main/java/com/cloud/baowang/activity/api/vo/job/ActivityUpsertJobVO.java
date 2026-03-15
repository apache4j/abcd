package com.cloud.baowang.activity.api.vo.job;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ActivityUpsertJobVO {
    @Schema(title = "任务id-更新需要")
    private String id;
    @Schema(title = "cron表达式")
    private String cron;
    @Schema(title = "job名称")
    private String name;
    @Schema(title = "执行参数-tempalte")
    private String param;
    @Schema(title = "时区")
    private String timeZone;
}
