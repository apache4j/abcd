package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 17:22
 * @Version: V1.0
 **/
@Data
@Schema(title ="保证金分页查询参数")
public class SiteSecurityChangeLogPageReqVO extends PageVO {
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "来源订单号")
    private String sourceOrderNo;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "所属公司")
    private String company;

    @Schema(description = "站点类型")
    private Integer siteType;

    @Schema(description = "业务类型")
    private Integer sourceCoinType;

    @Schema(description = "帐变类型")
    private Integer coinType;

    @Schema(description = "保证金类型")
    private String balanceAccount;

    @Schema(description = "收入类型")
    private String amountDirect;

    @Schema(description = "帐号类型")
    private String userType;

    @Schema(description = "帐号名称")
    private String userName;

    @Schema(description = "最小帐变金额")
    private BigDecimal minAmount;
    @Schema(description = "最大帐变金额")
    private BigDecimal maxAmount;


}
