package com.cloud.baowang.wallet.api.enums.usercoin;

import lombok.Getter;

import java.util.*;

/**
 * 存款取款 订单类型
 *system_param deposit_withdraw_status
 * @author qiqi
 */
@Getter
public enum DepositWithdrawalOrderStatusEnum {

    FIRST_WAIT("1", "待一审"),
    FIRST_AUDIT("2", "一审审核"),
    FIRST_AUDIT_REJECT("3", "一审拒绝"),

    ORDER_WAIT("4", "待挂单审核"),
    ORDER_AUDIT("5", "挂单审核"),
    ORDER_AUDIT_REJECT("6", "挂单审核拒绝"),

    //THIRD_WAIT("7", "待三审"),
    //THIRD_AUDIT("8", "三审审核"),
    //THIRD_AUDIT_REJECT("9", "三审拒绝"),

    WITHDRAW_WAIT("7", "待出款审核"),
    WITHDRAW_AUDIT("8", "出款审核"),
    WITHDRAW_AUDIT_REJECT("9", "出款审核驳回"),
    WITHDRAW_AUDIT_SUCCESS("10", "出款审核通过"),

    //WITHDRAW_AUDIT("10", "待出款"),

    MANUAL_FIRST_WAIT("11", "待一审(人工)"),
    MANUAL_FIRST_AUDIT("12", "一审审核(人工)"),
    MANUAL_FIRST_AUDIT_REJECT("13", "一审拒绝(人工)"),

    /*******************处理流程********************/
    HANDLE_ING("21", "处理中"),

    /******************失败*********************/
    WITHDRAW_FAIL("96", "出款失败"),
    BACKSTAGE_CANCEL("97", "出款取消"),
    APPLICANT_CANCEL("98", "取消订单(申请人)"),


    FAIL("100", "失败"),
    SUCCEED("101", "成功"),

    ;
    private final String code;
    private final String name;

    DepositWithdrawalOrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DepositWithdrawalOrderStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DepositWithdrawalOrderStatusEnum[] types = DepositWithdrawalOrderStatusEnum.values();
        for (DepositWithdrawalOrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


    public static List<DepositWithdrawalOrderStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
