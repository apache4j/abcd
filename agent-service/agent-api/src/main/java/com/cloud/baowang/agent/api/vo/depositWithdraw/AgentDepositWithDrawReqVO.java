package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/11 14:25
 * @Version: V1.0
 **/
@Data
public class AgentDepositWithDrawReqVO extends SitePageVO {
    @Schema(description ="开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @Schema(description ="结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    private String status;

}
