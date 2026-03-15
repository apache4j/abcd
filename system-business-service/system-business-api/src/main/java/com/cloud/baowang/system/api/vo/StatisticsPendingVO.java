package com.cloud.baowang.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "统计待审核vo")
public class StatisticsPendingVO {
    @Schema(description = "当前待审核类型")
    private String code;
    @Schema(description = "总记录数")
    private Long total = 0L;
}
