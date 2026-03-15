package com.cloud.baowang.agent.api.vo.agent.commission;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "代理下总流水")
public class AgentValidAmountVo {
    private String siteCode;
    private Long startTime;
    private Long endTime;
    //重跑时间
    private Long flushTime;
    //时区
    private String timeZoneStr;
    private String currencyCode;
    private Integer venueType;
    private List<AgentValidAmountCommonVo>  records;
}
