package com.cloud.baowang.system.api.vo.dict;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典配置变更记录表 PO 类
 */
@Data
@Schema(description = "变更记录vo")
@I18nClass
public class SystemDictConfigChangeLogRespVO {
    @Schema(description = "主键")
    private String id;

    /**
     * 配置名称
     */
    @Schema(description = "配置名称")
    private String configName;

    /**
     * 配置注释
     */
    @Schema(description = "配置注释")
    private String configDescription;

    /**
     * 配置类目 (0: 固定值, 1: 百分比)
     */
    @Schema(description = "0: 固定值, 1: 百分比")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DICT_CONFIG_CATEGORY)
    private Integer configCategory;

    @Schema(description = "0: 固定值, 1: 百分比")
    private String configCategoryText;

    /**
     * 修改前
     */
    @Schema(description = "修改前")
    private String beforeChange;

    /**
     * 修改后
     */
    @Schema(description = "修改后")
    private String afterChange;

    @Schema(description = "修改时间")
    private Long updatedTime;

    @Schema(description = "修改人")
    private String updater;

}
