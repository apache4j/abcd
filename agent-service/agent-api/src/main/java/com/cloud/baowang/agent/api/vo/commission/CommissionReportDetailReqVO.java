package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/27 23:33
 * @description: 佣金报表详情查询VO
 */
@Data
@Schema(description = "佣金报表详情查询VO")
public class CommissionReportDetailReqVO extends PageVO implements Serializable {
    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private Long id;
    @Schema(description =  "月份/报表日期", hidden = true)
    private Long reportDay;

}
