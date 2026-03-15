package com.cloud.baowang.agent.api.vo.agentreview.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务基础信息
 */
@Data
@Schema(description = "商务基本信息")
public class AgentMerchantParamVO implements Serializable {

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
    @Schema(description = "商务账号")
    private String userName;


    @Schema(description = "密码")
    private String password  ;

    @Schema(description = "验证码")
    private String verifyCode;


    @Schema(title = "验证码KEY")
    private String codeKey;

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


    /**
     * 登录设备号
     */
    @Schema(description = "登录设备号")
    private String loginDeviceNo;

}
