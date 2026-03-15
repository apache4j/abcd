package com.cloud.baowang.system.po.member;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@TableName("business_admin_role")
public class BusinessAdminRolePO {

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 职员ID
     */
    private String adminId;

}
