package com.cloud.baowang.job.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobUpsertVO {
    @Schema(title = "job名称")
    private String name;
    @Schema(title = "job cron 表达式")
    private String cron;
    @Schema(title = "job 执行参数")
    private String executorParam;
    @Schema(title = "job handler名称")
    private String handlerName;
    // 更新才需要带id
    @Schema(title = "id,更新才需要带")
    private String id;
}
