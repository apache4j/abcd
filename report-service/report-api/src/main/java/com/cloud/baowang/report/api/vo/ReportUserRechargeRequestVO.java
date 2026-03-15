package com.cloud.baowang.report.api.vo;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员累计充值查询请求对象")
public class ReportUserRechargeRequestVO extends PageVO {
    /**
     * 站点编码
     */
    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(description = "日期-天")
    private String dateStr;

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


}
