package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: qiqi
 **/
@Data
@Schema(description = "提款类型请求对象")
public class SystemWithdrawTypeRequestVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 提款类型
     */
    @Schema(description = "提款类型CODE 数据字典值 withdraw_type")
    private String withdrawTypeCode;

    /**
     * 提款类型
     */
    @Schema(description = "提款类型")
    private String withdrawType;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
