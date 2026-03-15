package com.cloud.baowang.system.api.vo.site.tutorial.operation;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "教程变更记录查询vo")
public class TutorialOperationRecordResVO extends PageVO {
    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description ="开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @Schema(description ="结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @Schema(description = "变更目录")
    private String changeCatalog;
    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "操作人")
    private String operator;
}
