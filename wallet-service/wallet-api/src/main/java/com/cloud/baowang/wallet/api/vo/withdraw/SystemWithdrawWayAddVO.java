package com.cloud.baowang.wallet.api.vo.withdraw;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.ConstantsCode;
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
@Data
@Schema(description = "提现方式")
public class SystemWithdrawWayAddVO {

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 提现类型
     */
    @Schema(description = "提现类型Id")
    //@NotNull(message = "提款类型Id不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String withdrawTypeId;



    /**
     * 提现方式
     */
    @Schema(description = "提现方式")
    //@NotNull(message = "提现方式不能为空")
//    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String withdrawWay;

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
    private BigDecimal wayFee;



    /**
     * 固定金额手续费
     */
    @Schema(description = "固定金额手续费")
    private BigDecimal wayFeeFixedAmount;

    @Schema(description = "快捷金额 多个逗号分隔 废弃")
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
    private String wayIcon;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    /**
     * 充值方式 多语言
     */
    @Schema(description = "提款方式 多语言List")
    //@NotNull(message = "提现方式多语言列表不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> withdrawWayI18List;

    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    private Integer recommendFlag;

    @Schema(description = "链网络类型")
    private String networkType;

    @Schema(description = "收集信息 数据从getWithdrawTypes()接口获取")
    //@NotNull(message = "收集信息不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<SystemWithDrawWayCollectFieldVO> collectFieldVOS;

}
