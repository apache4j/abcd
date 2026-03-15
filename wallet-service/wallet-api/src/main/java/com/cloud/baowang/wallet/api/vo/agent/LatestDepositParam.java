package com.cloud.baowang.wallet.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "最新存款 Param")
public class LatestDepositParam {

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "1按照存款金额排序 2按照时间排序 (如果没有排序则不需要传递)")
    private Integer orderField;

    @Schema(description = "1升序 2降序 (如果没有排序则不需要传递)")
    private Integer orderType;

    @Schema(description = "当前用户",hidden = true)
    String currentId;
    @Schema(description = "当前代理账号",hidden = true)
    String currentAgent;
    @Schema(description = "当前站点",hidden = true)
    String siteCode;
}
