package com.cloud.baowang.common.mybatis.base;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class   SiteBasePO extends BasePO{

    /**
     * 站点编码
     */
    private String siteCode;
}
