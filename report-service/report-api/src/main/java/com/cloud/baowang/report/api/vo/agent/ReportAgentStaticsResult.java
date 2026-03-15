package com.cloud.baowang.report.api.vo.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理报表 结果
 */
@Data
@Schema(title = "代理报表 结果")
@I18nClass
public class ReportAgentStaticsResult {

   @Schema(title =   "本页合计",description = "本页合计")
    private ReportAgentStaticsResponseVO currentPage;

   @Schema(title =   "全部合计",description = "全部合计")
    private ReportAgentStaticsResponseVO totalPage;

   @Schema(title =   "分页列表",description = "分页列表")
    private Page<ReportAgentStaticsResponseVO> pageList;
}
