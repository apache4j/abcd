package com.cloud.baowang.play.api.vo.order.report;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "会员盈亏重算")
public class WinLoseRecalculateReqVO extends PageVO {
    @Schema(description = "场馆code")
    private String siteCode;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
}
