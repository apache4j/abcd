package com.cloud.baowang.system.po.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@TableName("business_admin")
public class BusinessAdminPO extends BasePO {

    /**
     * 业务系统（ADMIN_CENTER总台 SITE站点）
     */
    private String businessSystem;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 管理员ID
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 姓名
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 谷歌验证key
     */
    private String googleAuthKey;

    /**
     * 状态 0 正常 1禁用
     */
    private Integer status;

    /**
     * 验证码类型 1 谷歌
     */
    private Integer verifyCodeType;

    /**
     * 是否首次登录
     */
    private Integer isFirstTime;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 电话
     */
    private String phone;

    /**
     * 锁定状态 0 未锁定 1 已锁定
     */
    private Integer lockStatus;

    /**
     * 职员编码
     */
    private String staffNo;

    /**
     * 接受IPS
     */
    private String allowIps;

    /**
     * 备注
     */
    private String remark;

    private String isSuperAdmin;

    @Schema(description = "是否已重置google 1 是 0 否")
    private Integer isSetGoogle;

    /**
     * 站点首页快捷方式
     */
    private String homeQuickButton;
}
