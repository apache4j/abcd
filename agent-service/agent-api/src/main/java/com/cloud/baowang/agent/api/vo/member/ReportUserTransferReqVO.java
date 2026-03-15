package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(title = "会员转代次数请求入参")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportUserTransferReqVO implements Serializable {

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;

    @Schema(title = "站点")
    private String siteCode;


}
