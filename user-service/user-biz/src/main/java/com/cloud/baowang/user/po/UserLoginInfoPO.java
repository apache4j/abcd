package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 15/5/23 10:24 AM
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@TableName("user_login_info")
@Schema(description = "会员登录日志")
public class UserLoginInfoPO extends BasePO implements Serializable {

    /* 会员账号 */
    private String userAccount;

    /* 会员userId */
    private String userId;

    /* 账号类型 */
    private Integer accountType;

    /* ip */
    private String ip;

    /* ip归属地 */
    private String ipAddress;

    /* 登录时间 */
    private Long loginTime;


    /**
     * 登录状态  1 是失败，0 是成功
     * system_param(login_type)
     */
    private Integer loginType;

    /* 登录网址 */
    private String loginAddress;

    /* 登录终端 */
    private String loginTerminal;

    /* 设备号 */
    private String deviceNo;

    /* 设备版本 */
    private String deviceVersion;

    /* 备注 */
    private String remark;

    /**
     * 站点code
     */
    private String siteCode;

    private String version;

    private String superAgentAccount;

}
