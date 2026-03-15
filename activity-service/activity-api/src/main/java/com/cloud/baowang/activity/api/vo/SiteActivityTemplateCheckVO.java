package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteActivityTemplateCheckVO {
    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description ="活动模版")
    private String activityTemplate;

}
