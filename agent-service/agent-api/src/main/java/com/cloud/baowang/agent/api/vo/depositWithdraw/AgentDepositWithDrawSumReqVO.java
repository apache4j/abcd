package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/11 14:25
 * @Version: V1.0
 **/
@Data
@Builder
@AllArgsConstructor
public class AgentDepositWithDrawSumReqVO {

    @Schema(description ="开始时间")
    private Long startTime;

    @Schema(description ="结束时间")
    private Long endTime;

    private String siteCode;

    private String timezone;

    private String currencyCode;

    private int depositOrWithDraw;

}
