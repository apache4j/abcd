package com.cloud.baowang.system.api.vo.dict;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.system.api.enums.dict.DictConfigCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@Schema(description = "系统字典配置vo")
@I18nClass
public class SystemDictConfigRespVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "siteCode")
    private String siteCode;
    /**
     * {@link com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums}
     */
    @Schema(description = "配置code")
    private Integer dictCode;
    /**
     * 配置名称
     */
    @Schema(description = "配置名称")
    private String configName;

    /**
     * 配置描述
     */
    @Schema(description = "配置描述")
    private String configDescription;

    /**
     * {@link DictConfigCategoryEnum}
     * 配置类目 0.固定值,1.百分比
     */
    @Schema(description = "配置类目")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DICT_CONFIG_CATEGORY)
    private Integer configCategory;

    @Schema(description = "配置类目")
    private String configCategoryText;

    /**
     * 配置参数
     */
    private String configParam;

    /**
     * 提示信息 system_param lookup配置
     */
    @I18nField
    private String hintInfo;

    @Schema(description = "小数位")
    private Integer decimalPlaces;

    @Schema(description = "字典值类型")
    private String type;
}
