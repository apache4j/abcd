package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务基础信息
 */
@TableName("agent_merchant")
@Data
public class AgentMerchantPO extends BasePO implements Serializable {


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
     * google验证密钥
     */
    private String googleAuthKey;

    /**
     * 账号状态 1正常 2登录锁定
     */
    private String status;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 风控id
     */
    private String riskId;
    /**
     * 注册时间
     */
    private Long registerTime;

    /**
     * 邮箱
     */
    private String email;

}
