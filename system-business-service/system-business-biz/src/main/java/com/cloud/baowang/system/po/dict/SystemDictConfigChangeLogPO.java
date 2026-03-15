package com.cloud.baowang.system.po.dict;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 * 字典配置变更记录表 PO 类
 */
@Data
@TableName("system_dict_config_change_log")
public class SystemDictConfigChangeLogPO extends BasePO {

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置注释
     */
    private String configDescription;

    /**
     * 配置类目 (0: 固定值, 1: 百分比)
     */
    private Integer configCategory;

    /**
     * 修改前
     */
    private String beforeChange;

    /**
     * 修改后
     */
    private String afterChange;

    private String siteCode;

}
