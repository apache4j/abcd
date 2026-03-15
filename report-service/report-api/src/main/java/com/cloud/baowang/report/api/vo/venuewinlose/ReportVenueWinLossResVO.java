package com.cloud.baowang.report.api.vo.venuewinlose;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "场馆盈亏报表返回参数")
@I18nClass
public class ReportVenueWinLossResVO {
    @Schema(description = "分页信息")
    private Page<VenueWinLossDetailResVO> pageList;
    @Schema(description = "总计信息")
    private VenueWinLossDetailResVO totalInfo;
}
