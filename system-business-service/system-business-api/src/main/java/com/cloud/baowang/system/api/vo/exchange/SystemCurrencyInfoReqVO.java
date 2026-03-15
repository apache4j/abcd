package com.cloud.baowang.system.api.vo.exchange;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种配置查询条件")
public class SystemCurrencyInfoReqVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    private List<String> currencyCodeList;

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

    //语言代码
    private String languageCode;
}
