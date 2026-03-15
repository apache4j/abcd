package com.cloud.baowang.wallet.api.vo.withdraw;

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
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点提款方式响应 SiteRechargeWayResponseVO")
@I18nClass
public class SiteWithdrawWayResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "提款类型Id")
    private String withdrawTypeId;

    @Schema(description = "提款类型编码")
    private String withdrawTypeCode;

    @Schema(description = "提款类型多语言")
    @I18nField
    private String withdrawTypeI18;

    /**
     * 提款方式
     */
    @Schema(description = "提款方式id")
    private String withdrawWayId;

    /**
     * 提款方式
     */
    @Schema(description = "提款方式")
    private String withdrawWay;



    /**
     * 提款方式 多语言
     */
    @I18nField
    @Schema(description = "提款方式 多语言代码 ")
    private String withdrawWayI18;


    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典code:fee_type")
    @I18nField(type = DICT,value = CommonConstant.FEE_TYPE)
    private Integer feeType;

    @Schema(description = "手续费类型文本")
    private String feeTypeText;

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

    @Schema(description = "图标完整Url")
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
