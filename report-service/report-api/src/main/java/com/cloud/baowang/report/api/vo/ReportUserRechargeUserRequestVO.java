package com.cloud.baowang.report.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "会员累计充值查询单个请求对象")
public class ReportUserRechargeUserRequestVO {


    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


}
