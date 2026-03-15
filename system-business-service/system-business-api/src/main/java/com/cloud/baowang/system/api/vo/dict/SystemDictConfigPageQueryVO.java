package com.cloud.baowang.system.api.vo.dict;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@Schema(description = "分页查询对象")
@I18nClass
public class SystemDictConfigPageQueryVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "总台是否展示同步至站点的默认初始化数据",hidden = true)
    private Integer isSyncSite;

    @Schema(description = "配置code")
    private Integer dictCode;

    @Schema(description = "字典值类型")
    private String type;
}
