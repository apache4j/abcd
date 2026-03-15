package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值类型新增")
public class SystemRechargeTypeNewReqVO {

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
   // @NotNull(message = "货币代码不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    /**
     * 充值类型
     */
    @Schema(description = "充值类型CODE 数据字典值 recharge_type")
    //@NotNull(message = "充值类型CODE不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String rechargeCode;

    /**
     * 充值类型
     */
    @Schema(description = "充值类型 中文名称")
    //@NotNull(message = "充值类型 中文名称 不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String rechargeType;

    /**
     * 排序
     */
    @Schema(description = "排序")
   // @NotNull(message = "排序不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;


    /**
     * 充值类型 多语言
     */
    @Schema(description = "充值类型 多语言List")
    //@NotNull(message = "充值类型多语言列表不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> rechargeTypeI18List;
}
