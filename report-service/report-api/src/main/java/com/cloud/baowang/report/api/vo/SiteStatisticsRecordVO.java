package com.cloud.baowang.report.api.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.report.api.vo.site.SiteStatisticsVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "平台报表视图")
@Data
@I18nClass
public class SiteStatisticsRecordVO {
    @Schema(description = "分页")
    private Page<SiteStatisticsVO> pages;
    @Schema(description = "总计")
    private SiteStatisticsVO totalRecord;
    @Schema(description = "小计")
    private SiteStatisticsVO smallRecord;

}
