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
public class SiteSecurityBalancePageReqVO extends PageVO {

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "所属公司")
    private String company;
    @Schema(description = "保证金账户状态 1:正常 2:预警 3:透支")
    private Integer accountStatus;
    @Schema(description = "保证金最小值")
    private BigDecimal minAmount;
    @Schema(description = "保证金最大值")
    private BigDecimal maxAmount;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "最近修改人")
    private String lastModifyUser;

}
