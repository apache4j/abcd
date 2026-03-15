package com.cloud.baowang.system.api.vo.site.tutorial.category;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "教程大类列表查询")
public class TutorialCategoryQueryVO extends PageVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "教程大类id")
    private String categoryId;
    @Schema(description = "状态 0-禁用,1-启用")
    private Integer status;
}
