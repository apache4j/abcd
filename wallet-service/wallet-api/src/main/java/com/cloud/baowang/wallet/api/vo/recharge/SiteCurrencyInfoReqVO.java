package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种配置查询条件")
public class SiteCurrencyInfoReqVO extends SitePageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 货币名称
     */
    @Schema(description = "货币名称")
    private String currencyName;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    private String languageCode;
}
