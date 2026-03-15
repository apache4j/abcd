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
public class SiteActivityTemplateSaveVO {
    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description ="选中的活动模版")
    private List<String> checkActivityTemplate;

    @Schema(description ="操作人")
    private String operator;

}
