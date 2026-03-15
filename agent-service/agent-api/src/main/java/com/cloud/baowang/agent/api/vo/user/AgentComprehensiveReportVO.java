package com.cloud.baowang.agent.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @auther amos
 * @create 2024-11-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员报表请求对象")
public class AgentComprehensiveReportVO implements Serializable {
    private Long startTime;
    private Long endTime;
    private String siteCode;
//    private List<String> siteCodeList;
    private Integer type;
//    private String timeZone;
//    private List<SiteBasicReportVO> siteCodeAndNameList;

}
