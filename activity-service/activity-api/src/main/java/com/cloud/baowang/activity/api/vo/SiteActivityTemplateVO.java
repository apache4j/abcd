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
public class SiteActivityTemplateVO {

    @Schema(title = "活动模版编号")
    private String activityTemplate;

    @Schema(title = "活动名称")
    private String activityName;

    @Schema(title = "绑定状态 1绑定,0解绑")
    private Integer bindStatus;

    @Schema(title = "活动数量")
    private Integer activityNum;

}
