package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证金会员类型
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityUserTypeEnums {

    USER("user", "会员"),
    AGENT("agent", "代理"),
    SITE("site", "站点"),
    ;

    private final String code;
    private final String value;


    public static SiteSecurityUserTypeEnums parseCode(String code) {
        for(SiteSecurityUserTypeEnums securityCoinTypeEnums: SiteSecurityUserTypeEnums.values()){
            if(code.equals(securityCoinTypeEnums.getCode())){
                return securityCoinTypeEnums;
            }
        }
        return null;
    }
}
