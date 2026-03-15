package com.cloud.baowang.report.api.vo.user.complex;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "总台-综合数据报表")
public class SiteIntegrateStaticRspVO {

    @Schema(description = "综合数据报表分页")
    private Page<AdminIntegrateDataRspVO> integrateDataReportRspVOPage;
    @Schema(description = "综合数据报表当前页汇总")
    private AdminIntegrateDataRspVO currentDataRespVO;
    @Schema(description = "综合数据报表所有数据汇总")
    private AdminIntegrateDataRspVO allDataRespVO;
}

