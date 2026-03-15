package com.cloud.baowang.report.api.vo.game;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表 场馆查询vo")
public class ReportGameQueryVenueReqVO extends PageVO {
    @Schema(title = "站点code,在总台进入需手动传入,站点查询无需传入")
    private String siteCode;

    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "币种code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "场馆大类", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer venueType;

    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "场馆code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venueCode;

    @Schema(title = "游戏名称")
    private String gameName;

    @Schema(title = "游戏名称", hidden = true)
    private List<String> thirdGameCodes;

    @Schema(description = "转化为平台币")
    private Boolean convertPlatCurrency = false;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "开始时间")
    private Long startTime;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "结束时间")
    private Long endTime;
}
