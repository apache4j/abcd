package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值方式响应 SystemRechargeWayRespVO")
@I18nClass
public class SystemRechargeWayRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "充值类型Id")
    private String rechargeTypeId;

    @Schema(description = "充值类型编码")
    private String rechargeTypeCode;

    @Schema(description = "充值类型多语言")
    @I18nField
    private String rechargeTypeI18;

    /**
     * 充值方式
     */
    @Schema(description = "充值方式")
    private String rechargeWay;


    /**
     * 充值方式 多语言
     */
    @I18nField
    @Schema(description = "充值方式 多语言代码 ")
    private String rechargeWayI18;

    /**
     * 充值方式 多语言
     */
  //  @Schema(description = "充值方式 多语言List")
   // private List<I18nMsgFrontVO> rechargeWayI18List;

    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典code:fee_type")
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

    @Schema(description = "快捷金额")
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
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String wayIcon;

    @Schema(description = "图标Url")
    private String wayIconFileUrl;



    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态多语言")
    private String statusText;


    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    private Integer recommendFlag;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Long updatedTime;


    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;

}
