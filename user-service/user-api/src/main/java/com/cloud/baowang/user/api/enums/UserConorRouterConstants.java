package com.cloud.baowang.user.api.enums;


/**
 * 角标路由
 * 中控后台提示角标路由 （由前端提供）
 */
public class UserConorRouterConstants {


    /**
     * 新增会员审核
     */
    public static final String ADD_MEMBER_REVIEW = "/Member/MemberAudit/AddMember";
    /**
     * 会员账户修改审核
     */
    public static final String UPDATE_MEMBER_REVIEW = "/Member/MemberAudit/MemberAccountEdit";
    /**
     * 会员充值人工确认
     */
    public static final String MEMBER_DEPOSIT_MANUAL_CONFIRM = "/Funds/FundsConfirm/MemberDepositManualConfirm";
    /**
     * 会员提款人工确认
     */
    public static final String MEMBER_WITHDRAWAL_MANUAL_CONFIRM = "/Funds/FundsConfirm/MemberWithdrawalManualConfirm";


}
