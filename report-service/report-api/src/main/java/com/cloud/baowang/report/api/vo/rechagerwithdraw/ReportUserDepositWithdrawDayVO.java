package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title = "存取报表日返回对象")
public class ReportUserDepositWithdrawDayVO {

    @Schema(description = "日期")
    private Long day;

    @Schema(description = "存取报表对象")
    private List<ReportUserDepositWithdrawVO> dayReportList;

}
