package com.cloud.baowang.report.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员查询相关报表请求对象")
@Builder
public class ReportUserWinLossRebateParamVO extends PageVO {
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private String siteCode;

    private String currencyCode;

    @Schema( description = "时区")
    private String timezone;

    private Integer venueType;

}
