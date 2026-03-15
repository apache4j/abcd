package com.cloud.baowang.report.api.vo.venuewinlose;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "场馆盈亏报表分页查询入参")
public class ReportVenueWinLossPageReqVO extends PageVO {
    @Schema(description = "站点code", hidden = true)
    private String siteCode;
    @Schema(description = "场馆code,按天查询详情才需要传")
    private String venueCode;
    @Schema(description = "统计日期 开始时间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long startTime;
    @Schema(description = "统计日期 结束时间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long endTime;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "场馆code")
    private List<String> venueCodeList;
    @Schema(description = "转化为平台币")
    private Boolean convertPlatCurrency = false;
    @Schema(description = "是否导出")
    private Boolean exportFlag = false;
}
