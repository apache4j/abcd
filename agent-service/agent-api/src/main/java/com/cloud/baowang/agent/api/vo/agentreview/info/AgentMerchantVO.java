package com.cloud.baowang.agent.api.vo.agentreview.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务基础信息
 */
@Data
@Schema(description = "商务基本信息")
public class AgentMerchantVO implements Serializable {

    /**
     * 主键id
     */
    private String id;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 商务id-短
     */
    private String merchantId;

    /**
     * 商务账号
     */
    private String merchantAccount;

    /**
     * 商务名称
     */
    private String merchantName;

    /**
     * 当前语言
     */
    private String language;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * 登录密码
     */
    private String merchantPassword;


    /**
     * 账号状态 1正常 2登录锁定
     */
    private String status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 更新时间
     */
    private Long updatedTime;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    @Schema(name = "登录地址")
    private String loginAddress;

    @Schema(name = "登录终端")
    private String loginTerminal;

    @Schema(name = "登录设备号")
    private String terminalDeviceNo;

    private Integer loginType;

    private String ipAddress;

     /**
     * 风控id
     */
    private String riskId;

    private String remark;

    private String loginDeviceNo;

    private String googleAuthKey;

    /**
     * 注册时间
     */
    private Long registerTime;
    /**
     * 邮箱
     */
    private String email;

    private String loginIp;

}
