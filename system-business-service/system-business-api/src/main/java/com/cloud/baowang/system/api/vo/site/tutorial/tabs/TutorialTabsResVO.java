package com.cloud.baowang.system.api.vo.site.tutorial.tabs;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "教程页签列表查询")
public class TutorialTabsResVO extends PageVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "教程大类id")
    private String categoryId;
    @Schema(description = "教程分类id")
    private String classId;
    @Schema(description = "页签id")
    private String tabsId;
    @Schema(description = "状态 0-禁用,1-启用")
    private Integer status;
}
