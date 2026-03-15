package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 每日盈亏 ReqVO
 */
@Data
@Schema(title = "每日盈亏 ReqVO")
public class DailyWinLosePageVO extends SitePageVO {

    @Schema(title = "开始日期")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long startDay;

    @Schema(title = "结束日期")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long endDay;

    @Schema(title = "主币种")
    private String mainCurrency;

    @Schema(title = "转换为平台币")
    private boolean toPlatCurr;

}
