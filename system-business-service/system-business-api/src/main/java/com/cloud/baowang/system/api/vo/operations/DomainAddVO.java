package com.cloud.baowang.system.api.vo.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="新增域名请求对象")
public class DomainAddVO {

    @Schema(description ="域名地址")
    private String domainAddr;

    @Schema(description = "域名类型;1-代理端,2-H5端,3-app端,4-后端,5-下载页 字典CODE:site_domain_type")
    private Integer domainType;

    @Schema(description ="备注")
    private String remark;

    @Schema(description ="operator", hidden = true)
    private String operator;
}
