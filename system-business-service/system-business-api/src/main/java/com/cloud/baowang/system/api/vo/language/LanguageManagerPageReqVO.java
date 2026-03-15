package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "语言管理分页查询vo")
public class LanguageManagerPageReqVO extends PageVO {
    @Schema(description = "状态 0启用 1禁用")
    private Integer status;
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "站点编码", hidden = true)
    private String siteCode;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "语言代码")
    private String code;
}
