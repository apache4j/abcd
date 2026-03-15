package com.cloud.baowang.system.api.vo.rebate;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "生成返水vo")
public class ReportUserRebateInitVO {
    private String siteCode;
    private Long startTime;
    private Long endTime;
    //重跑时间
    private Long flushTime;
    //时区
    private String timeZoneStr;
    private String currencyCode;
    private Integer venueType;
    private List<SystemUserVenueStaticsVO>  records;
}
