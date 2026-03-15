package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询站点配置信息vo")
public class SiteMessageQueryVO {
    @Schema(description = "域名地址")
    private String domainAddr;
}
