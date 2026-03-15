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
@Schema(title = "域名查询请求对象")
public class DomainQueryVO {

    @Schema(description = "域名类型;1-代理端,2-H5端,3-app端,4-后端 字典CODE:site_domain_type")
    private Integer domainType;

    @Schema(description = "是否主域名;0-否,1-是")
    private Integer primaryDomain;
    /**
     * {@link com.cloud.baowang.system.api.enums.DomainBindStatusEnum}
     */
    @Schema(description = "绑定状态")
    private Integer bind;

    @Schema(description = "状态;1-启用,0-禁用 字典CODE:ENABLE_DISABLE_TYPE")
    private Integer status;

    @Schema(description = "站点编码")
    private String siteCode;

}
