package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: wade
 */
@Data
@Schema(description = "流量访问数据概览 VO")
public class SiteTrafficDataResVO {

    @Schema(title = "PV访问量")
    private Long pageViews;

    @Schema(title = "PV访问量访问量与昨天想比")
    private Long PVCompare;

    @Schema(title = "UV访问量")
    private Long uniqueVisitors;

    @Schema(title = "UV访问量访问量与昨天想比")
    private Long UVCompare;

    @Schema(title = "IP访问量")
    private Long uniqueIPs;

    @Schema(title = "IP访问量与昨天想比")
    private Long IPCompare;



}
