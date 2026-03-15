package com.cloud.baowang.system.po.dict;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.system.api.enums.dict.DictConfigCategoryEnum;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@TableName("system_dict_config")
public class SystemDictConfigPO extends BasePO {

    private String siteCode;

    /**
     * 配置名称
     */
    private String configName;
    /**
     * 字典code
     * {@link com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums}
     */
    private Integer dictCode;

    /**
     * 配置描述
     */
    private String configDescription;

    /**
     * {@link DictConfigCategoryEnum}
     * 配置类目
     */
    private Integer configCategory;

    /**
     * 配置参数
     */
    private String configParam;

    /**
     * 是否同步至站点0.否,1.是
     */
    private Integer isSyncSite;

    /**
     * 错误提示
     */
    private String hintInfo;
    /**
     * 小数位
     */
    private Integer decimalPlaces;
    /**
     * {@link com.cloud.baowang.system.api.enums.DictTypeEnums}
     * 字典值类型1.数字,2.字符串
     */
    private Integer type;

}
