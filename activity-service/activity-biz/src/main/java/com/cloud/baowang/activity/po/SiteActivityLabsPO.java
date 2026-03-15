package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点-活动分类
 *
 * @author aomiao
 */
@Data
@TableName("site_activity_labs")
public class SiteActivityLabsPO extends BasePO implements Serializable {
    /**
     * 分类名称多语言code
     */
    private String labNameI18Code;
    /**
     * 所属站点
     */
    private String siteCode;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态，0.禁用，1.启用
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
