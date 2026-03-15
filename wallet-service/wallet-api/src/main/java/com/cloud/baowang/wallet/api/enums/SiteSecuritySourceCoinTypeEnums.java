package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证金业务类型
 */
@AllArgsConstructor
@Getter
public enum SiteSecuritySourceCoinTypeEnums {

    USER_DEPOSIT("0", "会员存款",SiteSecurityUserTypeEnums.USER),
    USER_WITHDRAW("1", "会员提款",SiteSecurityUserTypeEnums.USER),
    AGENT_DEPOSIT("2", "代理存款",SiteSecurityUserTypeEnums.AGENT),
    AGENT_WITHDRAW("3", "代理提款",SiteSecurityUserTypeEnums.AGENT),
    ADD_SECURITY_BALANCE("4", "增加保证金",SiteSecurityUserTypeEnums.SITE),
    SUB_SECURITY_BALANCE("5", "减少保证金",SiteSecurityUserTypeEnums.SITE),
    ADD_SECURITY_OVERDRAW("6", "增加透支额度",SiteSecurityUserTypeEnums.SITE),
    SUB_SECURITY_OVERDRAW("7", "减少透支额度",SiteSecurityUserTypeEnums.SITE),
    USER_MANUAL_WITHDRAW("8", "会员人工提款",SiteSecurityUserTypeEnums.SITE),
    AGENT_MANUAL_WITHDRAW("9", "代理人工提款",SiteSecurityUserTypeEnums.SITE),

    ;
    private final String code;
    private final String value;
    private SiteSecurityUserTypeEnums userTypeEnums;

    public static SiteSecuritySourceCoinTypeEnums parseCode(String code) {
        for (SiteSecuritySourceCoinTypeEnums securityEnums:SiteSecuritySourceCoinTypeEnums.values()){
            if(code.equals(securityEnums.getCode())){
                return securityEnums;
            }
        }
        return null;
    }
}
