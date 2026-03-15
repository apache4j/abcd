package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证金帐变类型
 *
 *  会员代理充值成功 可用增加
 *  会员代理提现申请 可用减少、冻结增加
 *  会员代理提现失败 可用增加、冻结减少
 *  会员代理提现成功 冻结减少
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityCoinTypeEnums {

    USER_DEPOSIT("0", "会员存款"),
    USER_WITHDRAW("1", "会员提款"),//申请
    AGENT_DEPOSIT("2", "代理存款"),
    AGENT_WITHDRAW("3", "代理提款"),//申请
    WITHDRAW_FAIL("4", "提款失败"),//审核拒绝
    WITHDRAW_SUCCESS("5", "提款成功"),//提款到账

    ADD_SECURITY_BALANCE("6", "增加保证金"),//审核成功
    SUB_SECURITY_BALANCE("7", "减少保证金"),//申请
    SUB_SECURITY_BALANCE_SUCCESS("8", "减少保证金成功"),//审核成功
    SUB_SECURITY_BALANCE_FAIL("9", "减少保证金失败"),//审核拒绝

    ADD_SECURITY_OVERDRAW("10", "增加保证金透支额度"),//申请
    SUB_SECURITY_OVERDRAW("11", "减少保证金透支额度"),//申请
    SUB_SECURITY_OVERDRAW_SUCCESS("12", "减少保证金透支额度成功"),//审核成功
    SUB_SECURITY_OVERDRAW_FAIL("13", "减少保证金透支额度失败"),
    DEDUCT_SECURITY_OVERDRAW("14", "透支额度抵扣"),
    ;

    private final String code;
    private final String value;



    public static SiteSecurityCoinTypeEnums parseCode(String code) {
        for(SiteSecurityCoinTypeEnums securityCoinTypeEnums:SiteSecurityCoinTypeEnums.values()){
            if(code.equals(securityCoinTypeEnums.getCode())){
                return securityCoinTypeEnums;
            }
        }
        return null;
    }
}
