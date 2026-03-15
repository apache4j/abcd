package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/12/21 16:22
 * @Version: V1.0
 **/
@Data
public class ReportAgentUserWinLossVO {

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description ="开始时间")
    private Long startTime;

    @Schema(description ="结束时间")
    private Long endTime;

    private List<String> underAgentAccount;

    //时区
    private String timeZone;
}
