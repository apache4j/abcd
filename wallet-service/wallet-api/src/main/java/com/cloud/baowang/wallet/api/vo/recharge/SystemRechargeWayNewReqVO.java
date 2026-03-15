package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值方式创建")
public class SystemRechargeWayNewReqVO {

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;
    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
  //  @NotNull(message = "货币代码不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    @Schema(description = "充值类型Id")
    //@NotNull(message = "充值类型Id不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String rechargeTypeId;
    /**
     * 充值方式
     */
    @Schema(description = "充值方式中文名称")
    //@NotNull(message = "充值方式中文名称不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String rechargeWay;

    /**
     * 充值方式 多语言
     */
    @Schema(description = "充值方式 多语言List")
    //@NotNull(message = "充值方式多语言列表不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> rechargeWayI18List;


    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典code:fee_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer feeType;

    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "百分比手续费 5 代表5%")
    private BigDecimal wayFee;



    /**
     * 固定金额手续费
     */
    @Schema(description = "固定金额手续费")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "快捷金额 多个逗号分隔")
    private String quickAmount;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 图标
     */
    @Schema(description = "图标")
    //@NotNull(message = "图标不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String wayIcon;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    // @NotNull(message = "是否推荐不能为空")
//    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer recommendFlag;
    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;
}
