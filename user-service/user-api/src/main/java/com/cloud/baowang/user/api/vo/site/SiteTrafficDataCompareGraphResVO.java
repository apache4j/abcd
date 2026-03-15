package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(description = "流量地图数据对比 曲线图 VO")
public class SiteTrafficDataCompareGraphResVO {



    @Schema(title = "1-PV，2-UV，3-IP")
    private Integer type = 1;

    @Schema(title = "横坐标 每时")
    private List<String> dayList;

    @Schema(title = "PV数据/UV数据/IP数据")
    private List<Long> currentData;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;


}
