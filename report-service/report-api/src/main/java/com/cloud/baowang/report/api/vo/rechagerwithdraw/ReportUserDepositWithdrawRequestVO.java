package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员存取报表请求对象")
public class ReportUserDepositWithdrawRequestVO extends PageVO {


    @Schema(description = "开始时间")
    private Long startDay;

    @Schema(description = "结束时间")
    private Long endDay;


    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "站点",hidden = true)
    private String siteCode;

    @Schema(title = "转换为平台币 true 是 false 否")
    private Boolean toPlatCurr;


    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

    @Schema(description="是否导出 true 是 false 否")
    private Boolean exportFlag = false;
}
