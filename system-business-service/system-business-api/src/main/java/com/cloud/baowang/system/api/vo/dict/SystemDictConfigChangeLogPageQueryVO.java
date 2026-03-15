package com.cloud.baowang.system.api.vo.dict;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典配置变更记录表 PO 类
 */
@Data
@Schema(description = "分页查询对象")
public class SystemDictConfigChangeLogPageQueryVO extends PageVO {

    /**
     * 配置类目 (0: 固定值, 1: 百分比)
     */
    @Schema(description = "配置类目")
    private Integer configCategory;

    /**
     * 修改前
     */
    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
}
