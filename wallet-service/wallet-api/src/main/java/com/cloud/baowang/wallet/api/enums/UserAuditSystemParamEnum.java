package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAuditSystemParamEnum {
    /**
     * 一审审核
     */
    FIRST_AUDIT("LOOKUP_11800"),
    /**
     * 挂单审核
     */
    PENDING_ORDER_AUDIT("LOOKUP_11801"),
    /**
     * 待出款审核
     */
    WITHDRAWAL_REVIEW("LOOKUP_11802"),
    ;
    private final String systemParamValue;
}
