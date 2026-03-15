package com.cloud.baowang.system.api.vo.language;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "站点新增/编辑-查询语言列表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteLanguageVO {
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "code")
    private String code;
    @Schema(description = "是否选中0.否,1.是")
    private Integer isChecked;
}
