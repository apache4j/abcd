package com.cloud.baowang.agent.api.vo.agent.winLoss;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员查询相关报表请求对象")
@Builder
public class UserWinLossRebateParamVO extends PageVO {
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private String siteCode;

    private String currencyCode;

    @Schema( description = "时区")
    private String timezone;


}
