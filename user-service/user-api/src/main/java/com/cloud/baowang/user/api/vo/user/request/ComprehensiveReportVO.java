package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @auther amos
 * @create 2024-11-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员报表请求对象")
public class ComprehensiveReportVO implements Serializable {
    private Long startTime;
    private Long endTime;
    private String siteCode;
//    private List<String> siteCodeList;
    private Integer type;
//    private String timeZone;
//    private List<SiteBasicReportVO> siteCodeAndNameList;

}
