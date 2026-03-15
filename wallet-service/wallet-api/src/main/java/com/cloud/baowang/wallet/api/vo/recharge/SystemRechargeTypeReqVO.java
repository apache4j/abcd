package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值类型查询条件")
public class SystemRechargeTypeReqVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 充值类型
     */
    @Schema(description = "充值类型CODE 数据字典值 recharge_type")
    private String rechargeCode;

    /**
     * 充值类型
     */
    @Schema(description = "充值类型")
    private String rechargeType;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
