package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "平台报表统计查询对象")
@Builder
@AllArgsConstructor
public class SiteBasicReportVO implements Serializable {

    @Schema(description = "平台编号")
    private String siteCode;

    @Schema(description = "平台名称")
    private String siteName;


}
