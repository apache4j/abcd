package com.cloud.baowang.system.api.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "手机区号编辑对象vo")
public class AreaCodeManageEditReqVO {
    @NotBlank
    @Schema(description = "id")
    private String id;
    @Schema(description = "code")
    private String areaCode;
    @Schema(description = "国家简写")
    private String countryCode;
    @Schema(description = "国家名称列表")
    private List<AreaNameVO> nameList;
    @Schema(description = "图标地址")
    private String icon;
    @Schema(title = "最大长度")
    private Integer maxLength;
    @Schema(title = "最小长度")
    private Integer minLength;

    @Schema(description = "操作人", hidden = true)
    private String updater;

}
