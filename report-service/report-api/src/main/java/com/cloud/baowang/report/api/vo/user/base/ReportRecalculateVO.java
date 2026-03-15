package com.cloud.baowang.report.api.vo.user.base;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "报表重算入参")
public class ReportRecalculateVO {
    /**
     * 站点，如果没有站点，则更新所有指定时间的数据
     */
    @Schema(description = "站点编码")
    private String siteCode;
    /**
     * 开始时间，转换为utc整点开始时间
     */
    @Schema(description = "开始时间 时间戳")
    private Long startTime;

    /**
     * 结束时间，转换为utc整点结束时间¬
     */
    @Schema(description = "结束时间 时间戳")
    private Long endTime;

    @Schema(description = "站点时区")
    private String timeZone;

   /* *//**
     * 如果有会员，则更新改会员
     *//*
    @Schema(description = "会员账号id")
    private String userId;
    *//**
     * 若有代理，则更新这个代理下所有会员
     *//*
    @Schema(description = "代理账号id")
    private String agentId;*/
}
