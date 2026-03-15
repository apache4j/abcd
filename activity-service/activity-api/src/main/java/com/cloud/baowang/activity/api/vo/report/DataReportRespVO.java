package com.cloud.baowang.activity.api.vo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/3 11:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "活动数据报表响应")
@I18nClass
public class DataReportRespVO {
    @Schema(description = "活动数据响应结果 分页")
    private Page<ActivityDataReportRespVO> activityDataReportRespVOPage;
    @Schema(description = "当前页面展示结果")
    private ActivityDataReportRespVO currentDataReportRespVO;
    @Schema(description = "总计展示结果")
    private ActivityDataReportRespVO allDataReportRespVO;
}
