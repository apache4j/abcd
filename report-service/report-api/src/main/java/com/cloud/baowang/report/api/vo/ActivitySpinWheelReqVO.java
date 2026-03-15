package com.cloud.baowang.report.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/10 9:56
 * @description: 排行榜请求VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "排行榜请求VO")
public class ActivitySpinWheelReqVO extends PageVO {
    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "活动配置金额")
    private BigDecimal limitAmount;

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


}
