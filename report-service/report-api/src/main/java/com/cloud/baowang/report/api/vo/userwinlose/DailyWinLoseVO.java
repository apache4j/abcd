package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


/**
 * 每日盈亏 ReqVO
 */
@Data
@Schema(title = "每日盈亏 ReqVO")
@Builder
public class DailyWinLoseVO {


    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束日期")
    private Long endTime;

    @Schema(title = "站点")
    private String siteCode;

    @Schema(title = "币种")
    private String currencyCode;


    @Schema(title = "时区")
    private String timezone;

    @Schema(title = "userId")
    private String userId;
}
