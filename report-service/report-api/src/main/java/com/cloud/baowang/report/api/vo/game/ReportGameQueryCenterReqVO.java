package com.cloud.baowang.report.api.vo.game;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表 总台查询vo")
public class ReportGameQueryCenterReqVO extends PageVO {
    @Schema(title = "币种code")
    private String currency;

    @Schema(title = "站点名称")
    private String siteName;

    @Schema(title = "站点code", hidden = true)
    private List<String> siteCodeList;

    @Schema(description = "转化为平台币")
    private Boolean convertPlatCurrency = false;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "开始时间")
    private Long startTime;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "结束时间")
    private Long endTime;
}
