package com.cloud.baowang.report.api.vo.game;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表站点 请求vo")
public class ReportGameQuerySiteReqVO extends PageVO {

    @Schema(title = "站点code,在总台进入需手动传入,站点查询无需传入")
    private String siteCode;

    @Schema(title = "币种code")
    private String currency;

    @Schema(description = "转化为平台币")
    private Boolean convertPlatCurrency = false;

    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    private Integer venueType;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "开始时间")
    private Long startTime;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "结束时间")
    private Long endTime;
}
