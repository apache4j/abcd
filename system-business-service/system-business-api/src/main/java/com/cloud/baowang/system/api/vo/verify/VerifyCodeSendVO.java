package com.cloud.baowang.system.api.vo.verify;

import lombok.Data;

@Data
public class VerifyCodeSendVO {
    private String account;
    /**
     * 验证码
     */
    private String verifyCode;

    private String siteCode;

    /**
     * 区号
     */
    private String areaCode;

    /** 会员或者代理账号 */
    private String userAccount;
}
