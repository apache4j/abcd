package com.cloud.baowang.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 *  该表如果需要做国际化，请参考 这三个配置，配置好后，运行AutoGenI18ConfigDemo 这个了类 ，即可插入i18n_message。
 *  label_change_type	0	LOOKUP_10000	标签名称
 * label_change_type	1	LOOKUP_10001	标签描述
 * label_change_type	2	LOOKUP_10002	删除标签
 */
@Data
@TableName(value = "system_param")
public class SystemParamPO extends BasePO {
    private String type;
    private String code;
    private String value;

    /**
     * 配置值中文描述-为翻译
     */
    private String valueDesc;
    private String description;
}
