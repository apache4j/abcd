package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 13:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "站点币种信息")
@I18nClass
public class SiteCurrencyRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "修改人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;

    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 平台币代码
     */
    private String platCurrencyCode;

    /**
     * 平台币名称
     */
    @Schema(description = "平台币简称")
    private String platCurrencyName;

    @Schema(description = "平台币符号")
    private String platCurrencySymbol;

    @Schema(description = "平台币图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String platCurrencyIcon;

    @Schema(description = "平台币图标全路径")
    private String platCurrencyIconFileUrl;


    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 货币名称
     */
    @Schema(description = "货币名称 中文")
    private String currencyName;

    @Schema(description = "货币名称 多语言")
    // @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.CURRENCY_NAME)
    @I18nField
    private String currencyNameI18;

    /**
     * 货币符号
     */
    @Schema(description = "货币符号")
    private String currencySymbol;

    /**
     * 精度 TWO:2位小数 K:千位
     */
    @Schema(description = "精度 TWO:2位小数 K:千位")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CURRENCY_DECIMAL_TYPE)
    private String currencyDecimal;

    @Schema(description = "精度描述")
    private String currencyDecimalText;


    /**
     * 图标
     */
    @Schema(description = "图标")
    @I18nField(type =I18nFieldTypeConstants.FILE)
    private String currencyIcon;

    @Schema(description = "图标全路径")
    private String currencyIconFileUrl;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态描述多语言")
    private String statusText;

    @Schema(description = "转换汇率")
    private BigDecimal finalRate;

    @Schema(description = "选中状态")
    private Boolean isCheck;
}
