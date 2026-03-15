package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 备用金类型枚举类
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityReviewEnums {

    INCREASE_RESERVE_FUND(0, "增加保证金",SiteSecuritySourceCoinTypeEnums.ADD_SECURITY_BALANCE,SiteSecurityCoinTypeEnums.ADD_SECURITY_BALANCE),
    REDUCE_RESERVES(1, "减少保证金",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_BALANCE,SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE),
    INCREASE_OVERDRAW(2, "增加透支额度",SiteSecuritySourceCoinTypeEnums.ADD_SECURITY_OVERDRAW,SiteSecurityCoinTypeEnums.ADD_SECURITY_OVERDRAW),
    REDUCE_OVERDRAW(3, "减少透支额度",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_OVERDRAW,SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW),
    REDUCE_RESERVES_SUCCESS(4, "减少保证金审核成功",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_BALANCE,SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE_SUCCESS),
    REDUCE_OVERDRAW_SUCCESS(5, "减少透支额度审核成功",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_OVERDRAW,SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW_SUCCESS),
    REDUCE_RESERVES_FAIL(6, "减少保证金审核失败",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_BALANCE,SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE_FAIL),
    REDUCE_OVERDRAW_FAIL(7, "减少透支额度审核失败",SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_OVERDRAW,SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW_FAIL),
    ;


    private final Integer code;
    private final String value;
    private final SiteSecuritySourceCoinTypeEnums sourceCoinTypeEnums;
    private final SiteSecurityCoinTypeEnums coinTypeEnums;

    public static SiteSecurityReviewEnums parseCode(Integer code) {
        for(SiteSecurityReviewEnums siteSecurityReviewEnums:SiteSecurityReviewEnums.values()){
            if(siteSecurityReviewEnums.getCode()==code){
                return siteSecurityReviewEnums;
            }
        }
        return null;
    }

}
