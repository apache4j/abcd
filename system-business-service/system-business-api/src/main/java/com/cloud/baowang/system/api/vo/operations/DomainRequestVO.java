package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="查询域名列表对象")
public class DomainRequestVO extends PageVO {

    @Schema(description ="域名地址")
    private String domainAddr;

    @Schema(description ="站点Code")
    private String siteCode;

    @Schema(description = "域名类型;1-代理端,2-H5端,3-app端,4-后端 字典CODE:site_domain_type")
    private Integer domainType;

    @Schema(description = "绑定状态;0-未绑定,1-已绑定",hidden = true)
    private Integer bind;

}
