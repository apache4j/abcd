package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "推广链接返回vo")
@Data
public class PromotionDomainRespVO {
    @Schema(description = "域名")
    private String domainName;

    @Schema(description = "域名描述")
    private String domainDescription;

    @Schema(description = "访问量")
    private Long longUrlVisitCount;

    @Schema(description = "域名类型")
    private Integer domainType;
    @Schema(description = "链接类型")
    private Integer shortType;
}
