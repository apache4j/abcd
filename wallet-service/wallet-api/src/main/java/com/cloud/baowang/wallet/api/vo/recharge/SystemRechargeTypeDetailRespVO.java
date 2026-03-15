package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "充值类型响应")
@I18nClass
public class SystemRechargeTypeDetailRespVO {

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 充值类型Code
     */
    @Schema(description = "充值类型Code")
    private String rechargeCode;
    /**
     * 充值类型
     */
    @Schema(description = "充值类型 列表展示中文")
    private String rechargeType;

    /**
     * 充值类型 多语言
     */
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "充值类型 多语言代码")
    private String rechargeTypeI18;

    /**
     * 充值类型 多语言
     */
   @Schema(description = "充值类型 多语言List 前端展示名称")
   private List<I18nMsgFrontVO> rechargeTypeI18List;

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
    @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态 0:禁用 1:启用")
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

}
