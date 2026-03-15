package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: qiqi
 **/
@Schema(description = "提现方式")
@Data
@I18nClass
public class SystemWithdrawWayResponseVO {

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;



    @Schema(description = "提现类型Id")
    private String withdrawTypeId;

    @Schema(description = "提现类型编码")
    private String withdrawTypeCode;
    /**
     * 提现类型
     */
    @Schema(description = "提现类型多语言")
    @I18nField
    private String withdrawTypeI18;

    /**
     * 提现方式
     */
    @Schema(description = "提现方式")
    private String withdrawWay;


    @Schema(description = "提款方式 多语言代码")
    @I18nField
    private String withdrawWayI18;

    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典CODE: fee_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer feeType;

    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "百分比手续费 5 代表5%")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @DecimalMin(value = "0.01",message = ConstantsCode.PARAM_ERROR)
    private BigDecimal wayFee;



    /**
     * 固定金额手续费
     */
    @Schema(description = "固定金额手续费")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal wayFeeFixedAmount;

    /**
     * 快捷金额
     */
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
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态名称 0:禁用 1:启用")
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

    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    private Integer recommendFlag;

    /**
     * 信息收集 json格式
     */
    @Schema(description = "收集信息 数据从getWithdrawTypes()接口获取")
    @NotNull(message = "收集信息不能为空")
    private List<SystemWithDrawWayCollectFieldVO> collectFieldVOS;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;
}
