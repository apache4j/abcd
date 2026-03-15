package com.cloud.baowang.user.api.enums;

public enum SiteTodoEnum {
    USER_ACCOUNT_MODIFY("userAccountModify", "会员账户修改审核"),
    NEW_USER_AUDIT("newUserAudit", "新增会员审核"),
    NEW_AGENT_AUDIT("newAgentAudit", "新增代理审核"),
    AGENT_ACCOUNT_MODIFY("agentAccountModify", "代理账户修改审核"),
    USER_WITHDRAWAL_AUDIT("userWithdrawalAudit", "会员提款审核"),
    AGENT_WITHDRAWAL_AUDIT("agentWithdrawalAudit", "代理提款审核"),
    COMMISSION_AUDIT("commissionAudit", "佣金审核"),
    USER_MANUAL_INCREASE_AUDIT("userManualIncreaseAudit", "会员人工加额审核"),
    AGENT_MANUAL_INCREASE_AUDIT("agentManualIncreaseAudit", "代理人工加额审核"),

    PLATFORM_COIN_INCREASE_AUDIT("platformCoinIncreaseAudit", "会员平台币上分审核"),

    ;

    private final String code;
    private final String description;

    SiteTodoEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
