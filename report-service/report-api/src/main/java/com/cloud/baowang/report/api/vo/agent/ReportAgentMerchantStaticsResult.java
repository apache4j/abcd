package com.cloud.baowang.report.api.vo.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商务报表 结果
 */
@Data
@Schema(title = "商务报表 结果")
@I18nClass
public class ReportAgentMerchantStaticsResult {

   @Schema(title =   "本页合计",description = "本页合计")
    private ReportAgentMerchantStaticsResponseVO currentPage;

   @Schema(title =   "全部合计",description = "全部合计")
    private ReportAgentMerchantStaticsResponseVO totalPage;

   @Schema(title =   "分页列表",description = "分页列表")
    private Page<ReportAgentMerchantStaticsResponseVO> pageList;
}
