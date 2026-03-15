package com.cloud.baowang.system.po.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@TableName("business_role")
public class BusinessRolePO extends BasePO {

    /**
     * 业务系统（ADMIN_CENTER总台 SITE站点）
     */
    private String businessSystem;

    /**
     * 站点code
     */
    private String siteCode;


    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String remark;

    /**
     * 使用数量
     */
    private Integer useNums;

    /**
     * 状态 0 正常 1禁用
     */
    private Integer status;


}
