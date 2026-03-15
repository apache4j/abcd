package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(title = "代理标签修改对象", description = "代理标签新增")
public class AgentLabelEditVO {
    @NotNull
    @Schema(title = "id")
    private Long id;
    @NotBlank
    @Schema(title = "标签名称")
    private String name;
    @Schema(title = "站点code", hidden = true)
    private String siteCode;
    @NotBlank
    @Schema(title = "标签描述")
    private String description;
    @Schema(title = "操作人", hidden = true)
    private String operator;


}
