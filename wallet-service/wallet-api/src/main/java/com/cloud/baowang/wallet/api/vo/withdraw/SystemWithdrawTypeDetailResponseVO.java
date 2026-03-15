package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: qiqi
 **/
@Schema(description = "提现类型详情")
@Data
@I18nClass
public class SystemWithdrawTypeDetailResponseVO {

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;


    @Schema(description = "提款类型编码")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_TYPE)
    private String withdrawTypeCode;

    @Schema(description = "提款类型编码名称")
    private String withdrawTypeCodeText;

    /**
     * 提现类型
     */
    @Schema(description = "提现类型")
    private String withdrawType;

    /**
     * 提现类型 多语言
     */
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "提现类型 多语言代码")
    private String withdrawTypeI18;

    /**
     * 提现类型 多语言
     */
    @Schema(description = "提现类型 多语言List")
    private List<I18nMsgFrontVO> withdrawTypeI18List;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

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

    @I18nField
    private String statusDesc;

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

}
