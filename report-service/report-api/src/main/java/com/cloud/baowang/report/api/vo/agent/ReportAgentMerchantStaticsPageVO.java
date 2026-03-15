package com.cloud.baowang.report.api.vo.agent;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 商务报表 ReqVO
 */
@Data
@Schema(title = "商务报表 ReqVO")
public class ReportAgentMerchantStaticsPageVO extends SitePageVO {

    @Schema(description = "是否转换成平台币 默认false")
    private boolean transferPlatformFlag;

    @Schema(description = "报表类型 字典类型:report_type  0:日报 1:月报")
    @NotNull(message = "报表类型不能为空")
    private Integer reportType;
    @Schema(title = "统计日期-开始时间")
    private Long startStaticDay;
    @Schema(title = "统计日期-结束时间")
    private Long endStaticDay;
    @Schema(title = "注册日期-开始时间")
    private Long startRegisterDay;
    @Schema(title = "注册日期-结束时间")
    private Long endRegisterDay;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

    private List<String> agentIds;

}
