package com.cloud.baowang.system.api.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "语言和国家名称VO")
public class AreaNameVO {
    @Schema(description = "国家名称列表")
    private String countryName;
    @Schema(description = "语言")
    private String language;

    //语言信息
    @Schema(description = "语言名称")
    private String languageName;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "语言代码")
    private String code;
}
