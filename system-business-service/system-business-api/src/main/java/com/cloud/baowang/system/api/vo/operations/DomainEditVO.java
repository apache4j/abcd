package com.cloud.baowang.system.api.vo.operations;

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
@Schema(title = "修改域名状态对象")
public class DomainEditVO {

    @Schema(description = "id")
    @NotNull
    private String id;

    @Schema(description = "域名地址")
    private String domainAddr;
    @Schema(description = "域名类型")
    private Integer domainType;
    @Schema(description = "remark")
    private String remark;

    @Schema(description = "operator", hidden = true)
    private String operator;
}
