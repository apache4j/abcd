package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author aomiao
 */
@Data
@Schema(title = "代理标签新增对象", description = "代理标签新增")
public class AgentLabelAddVO {

    @NotBlank
    @Schema(title = "标签名称")
    private String name;
    @NotBlank
    @Schema(title = "标签描述")
    private String description;
    @Schema(title = "站点code", hidden = true)
    private String siteCode;
    @Schema(title = "操作人", hidden = true)
    private String operator;
}
