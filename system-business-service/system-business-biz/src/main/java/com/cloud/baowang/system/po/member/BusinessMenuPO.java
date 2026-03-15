package com.cloud.baowang.system.po.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@TableName("business_menu")
public class BusinessMenuPO extends BasePO implements Serializable {

    /**
     * 业务系统（ADMIN_CENTER总台 SITE站点）
     */
    private String businessSystem;


    /**
     * 菜单KEY唯一标识
     */
    private String menuKey;

    /**
     * 菜单名称
     */
    private String name;


    /**
     * 父菜单ID
     */
    private String parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 菜单上下级全路径
     */
    private String path;

    /**
     * 前端地址
     */
    private String url;

    /**
     * 类型（1-目录 2-菜单  3-tab页  9-按钮）
     */
    private Integer type;
    /**
     * 层级
     */
    private Integer level;

    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status;

    /**
     * API权限字符串
     */
    private String apiUrl;

    /**
     * 仅超级管理员显示
     */
    private String superAdminOnlyVisible;

}
