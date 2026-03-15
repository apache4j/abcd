package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**

 * @Author: Ford

 **/
@Data
@Schema(description = "提现方式分页请求对象")
public class SystemWithdrawWayRequestVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;


    /**
     * 提款类型ID
     */
    @Schema(description = "提现类型Id")
    private String withdrawTypeId;

    /**
     * 提现类型
     */
    @Schema(description = "提现类型")
    private String withdrawType;

    @Schema(description = "提现方式")
    private String withdrawWay;

    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典CODE: fee_type")
    private Integer feeType;


    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
