package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author: qiqi
 **/
@Data
@Schema(description = "提现类型新增")
public class SystemWithdrawTypeAddVO {

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    //@NotNull(message = "货币代码不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    /**
     * 提款类型
     */
    @Schema(description = "提款类型CODE 数据字典值 withdraw_type")
    //@NotNull(message = "提款类型CODE不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String withdrawTypeCode;

    /**
     * 提现类型
     */
    @Schema(description = "提现类型 中文名称")
    //@NotNull(message = "提现类型 中文名称不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String withdrawType;

    /**
     * 排序
     */
    @Schema(description = "排序")
    //@NotNull(message = "排序不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 提现类型 多语言
     */
    @Schema(description = "提现类型 多语言代码")
    private String withdrawTypeI18;

    /**
     * 提现类型 多语言
     */
    @Schema(description = "提现类型 多语言List")
    //@NotNull(message = "提现类型多语言列表不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> withdrawTypeI18List;
}
