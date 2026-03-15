package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/27 23:33
 * @description: 佣金报表查询VO
 */
@Data
@Schema(description ="佣金报表查询VO")
public class CommissionReportReqVO extends PageVO implements Serializable {
    @Schema(description ="开始时间")
    private Long beginDay;
    @Schema(description ="结束时间")
    private Long endDay;
    @Schema(description ="是否导出 true 是 false 否 不需要传参")
    private Boolean exportFlag = false;
}
