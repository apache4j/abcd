package com.cloud.baowang.activity.api.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(title = "报表 任务任务配置list请求入参")
public class ReportTaskConfigReqVO implements Serializable {

    @Schema(description = "任务codeList")
    private List<String> taskNameI18nCodes;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

}
