package com.cloud.baowang.report.api.vo.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商务总代报表 结果
 */
@Data
@Schema(title = "商务总代报表 结果")
@I18nClass
public class ReportTopAgentStaticsResult {

   @Schema(title =   "本页合计",description = "本页合计")
    private ReportTopAgentStaticsResponseVO currentPage;

   @Schema(title =   "全部合计",description = "全部合计")
    private ReportTopAgentStaticsResponseVO totalPage;

   @Schema(title =   "分页列表",description = "分页列表")
    private Page<ReportTopAgentStaticsResponseVO> pageList;
}
