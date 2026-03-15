package com.cloud.baowang.system.api.constant;

public class AdminPermissionApiConstant {

    // 会员银行卡记录-银行卡号 显示齐全功能 permissionApi
    public static final String SHOW_BANK_CARD_NO = "/admin-foreign/userBankCardRecord/show/bankCardNo";
    // 会员虚拟币账号记录-虚拟币账户地址 显示齐全功能 permissionApi
    public static final String SHOW_VIRTUAL_CURRENCY_ADDRESS = "/admin-foreign/virtualCurrencyRecord/show/virtualCurrencyAddress";
    // 会员Ebpay账号记录-Ebpay账户地址 显示齐全功能 permissionApi
    public static final String SHOW_EBPAY_ADDRESS = "/admin-foreign/ebpayRecord/show/ebpayAddress";
    // 会员易币付账号记录-易币付账户地址 显示齐全功能 permissionApi
    public static final String SHOW_YBF_ADDRESS = "/admin-foreign/ybfRecord/show/ybfAddress";

    // 会员人工加额审核-待一审 permissionApi
    public static final String WAIT_ONE_REVIEW = "/admin-foreign/user-manual-up-review/api/oneTrial";
    // 会员人工加额审核-待二审 permissionApi
    public static final String WAIT_TWO_REVIEW = "/admin-foreign/user-manual-up-review/api/twoTrial";

    // 代理人工加额审核-待一审 permissionApi
    public static final String AGENT_MANUAL_WAIT_ONE_REVIEW = "/admin-foreign/agent-manual-up-review/api/oneTrial";
    // 代理人工加额审核-待二审 permissionApi
    public static final String AGENT_MANUAL_WAIT_TWO_REVIEW = "/admin-foreign/agent-manual-up-review/api/twoTrial";

    // 会员充值审核-待一审 permissionApi
    public static final String WAIT_ONE_REVIEW_ = "/Funds/FundReview/MemberRechargeReview/oneTrial";
    // 会员充值审核-待入款 permissionApi
    public static final String WAIT_BRING_MONEY = "/Funds/FundReview/MemberRechargeReview/recharge";

    // 会员充值审核 权限url
    public static final String USER_RECHARGE_REVIEW = "/Funds/FundReview/MemberRechargeReview";
    // 会员提款审核 权限url
    public static final String USER_WITHDRAW_REVIEW = "/Funds/FundReview/MemberWithdrawalReview";
    // 代理充值审核 权限url
    public static final String AGENT_RECHARGE_REVIEW = "/Funds/FundReview/AgentRechargeReview";
    // 代理提款审核 权限url
    public static final String AGENT_WITHDRAW_REVIEW = "/Funds/FundReview/AgentWithdrawalReview";


    /**
     * 取款待一审
     */
    public static final String WITHDRAW_WAIT_ONE_REVIEW = "/admin-foreign/user-withdraw-review/api/oneTrial";

    /**
     * 取款待二审
     */
    public static final String WITHDRAW_WAIT_TWO_REVIEW = "/admin-foreign/user-withdraw-review/api/twoTrial";

    /**
     * 取款待三审
     */
    public static final String WITHDRAW_WAIT_THIRD_REVIEW = "/admin-foreign/user-withdraw-review/api/threeTrial";

    /**
     * 取款待出款
     */
    public static final String WITHDRAW_WAIT_WITHDRAW = "/admin-foreign/user-withdraw-review/api/pendingPay";

    /** 代理充值审核-待一审 **/
    public static final String AGENT_WAIT_ONE_REVIEW = "/Funds/FundReview/AgentRechargeReview/oneTrial";

    /** 代理充值审核-待入款 **/
    public static final String AGENT_WAIT_BRING_MONEY = "/Funds/FundReview/AgentRechargeReview/recharge";



    /** 代理返点审核-待一审 **/
    public static final String AGENT_REBATE_WAIT_FIRST_REVIEW = "/Funds/FundReview/RebateReview/oneTrial";

    /** 代理返点审核-待二审 **/
    public static final String AGENT_REBATE_WAIT_SECOND_REVIEW = "/Funds/FundReview/RebateReview/pendingPay";

    /**
     * 代理取款待一审
     */
    public static final String AGENT_WITHDRAW_WAIT_ONE_REVIEW = "/admin-foreign/agent-withdraw-review/api/oneTrial";

    /**
     * 代理取款待二审
     */
    public static final String AGENT_WITHDRAW_WAIT_TWO_REVIEW = "/admin-foreign/agent-withdraw-review/api/twoTrial";

    /**
     * 代理取款待三审
     */
    public static final String AGENT_WITHDRAW_WAIT_THIRD_REVIEW = "/admin-foreign/agent-withdraw-review/api/threeTrial";

    /**
     * 代理取款待出款
     */
    public static final String AGENT_WITHDRAW_WAIT_WITHDRAW = "/admin-foreign/agent-withdraw-review/api/pendingPay";

    /** 代理佣金审核-待一审 **/
    public static final String AGENT_COMMISSION_WAIT_FIRST_REVIEW = "/Funds/FundReview/CommissionReview/oneTrial";

    /** 代理佣金审核-待二审 **/
    public static final String AGENT_COMMISSION_WAIT_SECOND_REVIEW = "/Funds/FundReview/CommissionReview/pendingPay";

    /**
     * 会员数据脱敏
     */
    public static final String USER_DATA_DESENSITIZATION = "common/Member/MemberManagement/MemberInfoShow";


}
