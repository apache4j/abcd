package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemActivityTemplateVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "活动模版")
    private String activityTemplate;

    @Schema(title = "活动名称")
    private String activityName;

    @Schema(title = "活动开启数量")
    private Integer activityNum;

    @Schema(title = "修改时间")
    private Long updatedTime;

    @Schema(title = "修改人")
    private String updater;

    @Schema(title = "活动绑定数量")
    private Integer activityBindNum;

}
