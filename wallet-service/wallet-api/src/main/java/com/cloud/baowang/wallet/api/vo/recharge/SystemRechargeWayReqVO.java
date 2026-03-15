package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 13:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值方式分页查询操作")
public class SystemRechargeWayReqVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 充值类型
     */
    @Schema(description = "充值类型Id")
    private String rechargeTypeId;


    @Schema(description = "充值方式中文名称")
    private String rechargeWay;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典code:fee_type")
    private Integer feeType;

}
