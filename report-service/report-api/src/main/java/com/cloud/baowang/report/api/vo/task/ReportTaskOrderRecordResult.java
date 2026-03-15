package com.cloud.baowang.report.api.vo.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 代理报表 结果
 */
@Data
@I18nClass
@Schema(title = "任务发放报表 后端管理页面 结果")
public class ReportTaskOrderRecordResult implements Serializable {

    @Schema(title = "本页合计")
    private ReportTaskTotalResponseVO currentPage;

    @Schema(title = "全部合计")
    private ReportTaskTotalResponseVO totalPage;

    @Schema(title = "分页列表")
    @I18nField
    private Page<ReportTaskResponseVO> pageList;
}
