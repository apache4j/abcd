package com.cloud.baowang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 数据字典类型
 *
 * system_param中的所有type字段
 *
 */
@Getter
@AllArgsConstructor
public enum SystemParamTypeEnum {

    CHANNEL_TYPE("CHANNEL_TYPE","渠道类型"),
    CURRENCY_DECIMAL_TYPE("CURRENCY_DECIMAL_TYPE","币种精度"),
    ENABLE_DISABLE_TYPE("ENABLE_DISABLE_TYPE","启用禁用状态"),
    ABNORMAL_TYPE("abnormal_type","异常类型"),
    ACCOUNT_STATUS("account_status","会员账号状态"),
    ACCOUNT_TYPE("account_type","玩家账号类型"),
    ACTIVITY_DEADLINE("activity_deadLine","活动期限"),
    ACTIVITY_DISCOUNT_TYPE("activity_discount_type","活动优惠方式"),
    ACTIVITY_DISTRIBUTION_TYPE("activity_distribution_type","派发方式"),
    ACTIVITY_ELIGIBILITY("activity_eligibility","参与资格"),
    ACTIVITY_LIMIT_TYPE("activity_limit_type","转盘活动每位会员可领取次数上限"),
    ACTIVITY_PARTICIPATION_TYPE("activity_participation_type","参与方式"),
    ACTIVITY_PRESCRIPTION_TYPE("activity_prescription_type","站点-活动配置-活动是否有效期"),
    ACTIVITY_PRIZE_SOURCE("activity_prize_source","转盘奖励获取来源"),
    ACTIVITY_RECEIVE_STATUS("activity_receive_status","任务领取状态"),
    ACTIVITY_REWARD_RANK("activity_reward_rank","转盘奖品等级"),
    ACTIVITY_REWARD_TYPE("activity_reward_type","奖品类型"),
    ACTIVITY_TEMPLATE("activity_template","活动类型模板"),
    ACTIVITY_TEMPLATE_V2("activity_template_v2","活动类型模板v2"),
    ACTIVITY_USER_RANGE("activity_user_range","站点-活动配置-满足的某些段位"),
    ACTIVITY_USER_TYPE("activity_user_type","会员类型"),
    AGENT_ACTIVE_CONFIG("agent_active_config","有效投注有效活跃用户标准"),
    AGENT_ATTRIBUTION("agent_attribution","代理归属 1推广 2招商 3官资"),
    AGENT_BUSINESS_COIN_TYPE("agent_business_coin_type","代理账变记录-业务类型"),
    AGENT_CATEGORY("agent_category","代理类别"),
    AGENT_CHANGE_TYPE("agent_change_type","代理信息变更类型"),
    AGENT_CHANGE_TYPE_DELETE("agent_change_type_delete","代理信息变更类型"),
    AGENT_COIN_TYPE("agent_coin_type","代理账变记录-账变类型"),
    AGENT_COIN_TYPE_STATUS("agent_coin_type_status","代理PC,H5账变显示-账变状态"),
    AGENT_CUSTOMER_COIN_TYPE("agent_customer_coin_type","代理账变记录-客户端类型"),
    AGENT_CUSTOMER_SHOW_TYPE("agent_customer_show_type","代理PC,H5账变显示-账变类型"),
    AGENT_LABEL_OPERATION_TYPE("agent_label_operation_type","站点-代理标签配置-新增标签"),
    AGENT_MANUAL_ADJUST_DOWN_TYPE("agent_manual_adjust_down_type","代理资金调整类型减款-代理活动"),
    AGENT_MANUAL_ADJUST_TYPE("agent_manual_adjust_type","代理资金调整类型加款-其他调整"),
    AGENT_MANUAL_ORDER_STATUS("agent_manual_order_status","审核流程-二审状态"),
    AGENT_MANUAL_REVIEW_OPERATION("agent_manual_review_operation","代理人工金额审核操作"),
    AGENT_OVERFLOW_AUDIT_STATUS("agent_overflow_audit_status","代理调线申请-审核状态"),
    AGENT_REVIEW_STATUS("agent_review_status","代理信息修改审核状态"),
    AGENT_STATUS("agent_status","代理状态"),
    AGENT_TYPE("agent_type","代理类型"),
    AGENT_USER_BENEFIT("agent_user_benefit","会员福利"),
    AGENT_WALLET_TYPE("agent_wallet_type","代理账变记录-代理钱包"),
    AREA_LIMIT_Type("area_limit_type","区域限制类型-ip"),
    BANK_MANAGER_status("bank_manager_status","银行卡管理启用状态"),
    BINDING_STATus("binding_status","会员银行卡管理-绑定状态"),
    BLACK_STATUS("black_status","会员银行卡管理-黑名单状态"),
    BUSINESS_COIN_TYPE("business_coin_type","账变记录-业务类型"),
    CALCULATE_TYPE("calculate_type","结算周期"),
    CAPTCHA_SWITCH("captcha_switch","验证码开关 0 关闭  1打开"),
    CHANGE_STATUS("change_status","注单变更状态"),
    CHANGE_TYPE("change_type","变更类型"),
    CLASS_STATUS_TYPE("class_status_type","状态"),
    COIN_BALANCE_TYPE("coin_balance_type","账变记录-收支类型"),
    COIN_TYPE("coin_type","账变记录-账变类型"),
    COMMISSION_PLAN("commission_plan","抽成方案"),
    CORNER_LABELS("corner_labels","角标"),
    CUSTOMER_TYPE("customer_type","客服类型"),
    DATE_SELECT_SCOPE("date_select_scope","存取款时间范围"),
    DEPOSIT_WITHDRAW_CHANNEL("deposit_withdraw_channel","会员存取款-存取通道"),
    DEPOSIT_WITHDRAW_CUSTOMER_STATUS("deposit_withdraw_customer_status","会员存取款-客户端状态"),
    DEPOSIT_WITHDRAW_PAY_PROCESS_STATUS("deposit_withdraw_pay_process_status","会员存取款-三方消息状态"),
    DEPOSIT_WITHDRAW_STATUS("deposit_withdraw_status","会员存取款-订单状态"),
    DEVICE_TERMINAL("device_terminal","会员设备终端"),
    DISCOUNT_TYPE("discount_type","优惠方式"),
    DIVIDEND_RECEIVE_TYPE("dividend_receive_type","用户红利-红利状态"),
    DIVIDEND_TYPE("dividend_type","用户红利"),
    DOMAIN_STATE("domain_state","域名-状态"),
    DOMAIN_TYPE("domain_type","域名类型"),
    ENABLE_DISABLE_STATUS("enable_disable_status","启用禁用状态"),
    GAME_LABEL("game_label","标签名称"),
    GAME_SUPPORT_DEVICE("game_support_device","支持终端"),
    GENDER("gender","性别"),
    LABEL_CHANGE_TYPE("label_change_type","标签名称"),
    LOCK_STATUS("lock_status","会员审核-锁单状态"),
    LOGIN_TYPE("login_type","会员登录日志-登录状态"),
    MAINTENANCE_TERMINAL("maintenance_terminal","维护终端-用户"),
    MANUAL_ADJUST_DOWN_TYPE("manual_adjust_down_type","会员资金调整类型减款-其他调整"),
    MANUAL_ADJUST_TYPE("manual_adjust_type","会员资金调整类型加款-会员VIP优惠"),
    MANUAL_ADJUST_WAY("manual_adjust_way","会员/代理资金变更方式"),
    MEDAL_OPERATION("medal_operation","勋章变更"),
    MEDAL_OPERATOR("medal_operator","勋章变更操作"),
    ONE_MODEL("one_model","模板类型"),
    ONLINE_STATUS("online_status","会员状态"),
    ORDER_CLASSIFY("order_classify","注单状态"),
    ORDER_CLASSIFY_LOOKUP("order_classify_lookup","注单状态"),
    PAY_CHANNEL_NAME("pay_channel_name","支付渠道"),
    PLATFORM_CLASS_STATUS_TYPE("platform_class_status_type","状态"),
    PROCESS_STATUS("process_status","已处理"),
    RATE_ADJUST_WAY("rate_adjust_way","汇率调整方式"),
    RECHARGE_TYPE("recharge_type","充值-充值类型"),
    RECHARGE_WITHDRAW_STATUS("recharge_withdraw_status","存取款状态"),
    RECHARGE_WITHDRAW_TYPE("recharge_withdraw_type","存取款类型"),
    RECORD_DOMAIN("record_domain","域名-变更类型"),
    REGISTER_INFO("register_info","新增会员-注册信息"),
    REGISTER_WAY("register_way","注册方式"),
    REGISTRY("registry","会员注册终端"),
    RELEASE_TIME_TYPE("release_time_type","奖励活动类型"),
    RESETTLE_STATUS("resettle_status","不重算"),
    REVIEW_OPERATION("review_operation","审核操作"),
    REVIEW_STATUS("review_status","审核状态"),
    REVIEW_TYPE("review_type","审核申请类型"),
    RISK_CONTROL_TYPE("risk_control_type","风险类型"),
    SITE_DOMAIN_TYPE("site_domain_type","域名类型"),
    SITE_MODEL("site_model","站点模式"),
    SITE_TYPE("site_type","站点类型"),
    SPORT_ODDS_TYPE("sport_odds_type","赔率单选框"),
    SPORT_RECOMMEND_STATUS("sport_recommend_status","体育推荐状态"),
    SPORT_RECOMMEND_TYPE("sport_recommend_type","体育推荐类型"),
    STYLE_ONE_MODEL("style_one_model","黑夜模式"),
    SWITCH_STATUS("switch_status","通用状态-关闭"),
    TASK_TYPE("task_type","任务类型"),
    TWO_MODEL("two_model","模板类型"),
    USER_MANUAL_UP_ADJUST_TYPE("user_manual_up_adjust_type","会员人工添加额度-调整类型"),
    VENEU_CURRENCY_TYPE("veneu_currency_type","场馆币种类型"),
    VENUE_CODE("venue_code","场馆CODE"),
    VENUE_TYPE("venue_type","平台类型"),
    VIP_BENEFIT("vip_benefit","vip权益相关变动项"),
    VIP_CHANGE_TYPE("vip_change_type","VIP变更记录-变更类型"),
    VIP_GRADE("vip_grade","vip等级相关变动项"),
    VIP_LEVEL_CHANGE_TYPE("vip_level_change_type","vip等级变更类型-升级"),
    VIP_OPERATION_TYPE("vip_operation_type","vip权益配置操作类型"),
    VIP_RANK("vip_rank","VIP操作记录-操作类型"),
    VIP_RANK_CHANGE("vip_rank_change","VIP等级变更-变更类型"),
    WEEK_DAY("week_day","指定日期"),
    YES_NO("yes_no","是否"),
    WITHDRAW_TYPE("withdraw_type","提现类型"),
    WITHDRAW_COLLECT("withdraw_collect","提现信息收集类型"),
    DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS("deposit_withdrawal_order_customer_status","冲提账单客户端状态"),
    FRONT_END_SHOW_STATUS("front_end_show_status","前端展示状态"),

    NOTIFICATION_TYPE("notification_type","通知类型"),
    TARGET_TYPE("target_type","发送对象类型"),
    MEMBERSHIP_TYPE("membership_type","会员对象"),
    POP_UP_TYPEPOP_UP_TYPE("pop_up_type","弹窗类型"),
    /***
     * 特定会员类型 1:vip等级 2:主货币 3:特定会员
     */
    SPECIFY_MEMBERSHIP_TYPE("specify_membership_type","特定会员类型"),

    ;

    private final String type;
    private final String name;



    public static SystemParamTypeEnum getOne(String type) {
        if (null == type) {
            return null;
        }
        SystemParamTypeEnum[] arr = SystemParamTypeEnum.values();
        for (SystemParamTypeEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        SystemParamTypeEnum[] arr = SystemParamTypeEnum.values();
        for (SystemParamTypeEnum itemObj : arr) {
            LinkedHashMap<String, Object> itemMap = new LinkedHashMap<>();
            String type = itemObj.getType();
            String name = itemObj.getName();
            itemMap.put("type", type);
            itemMap.put("name", name);
            dataList.add(itemMap);
        }
        return dataList;
    }



    public static Map<String, String> toMap() {
        LinkedHashMap<String, String> itemMap = new LinkedHashMap<>();
        SystemParamTypeEnum[] arr = SystemParamTypeEnum.values();
        for (SystemParamTypeEnum itemObj : arr) {
            String type = itemObj.getType();
            String name = itemObj.getName();
            itemMap.put(type, name);
        }
        return itemMap;
    }



    public static boolean isExist(String type) {
        SystemParamTypeEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(String type) {
        return !isExist(type);
    }



}
