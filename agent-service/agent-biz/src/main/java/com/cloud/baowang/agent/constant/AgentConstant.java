package com.cloud.baowang.agent.constant;

public class AgentConstant {
    /**
     * 代理提款设置通用账号名
     */
    public static final String AGENT_WITHDRAW_CONFIG_COMMON = "通用";

    /**
     * 代理新增会员；消息标题 无模板 暂时固定
     */
    public static final String ADD_USER_MESSAGE_TITLE = "新增下级会员通知";
    /**
     * 代理新增会员；消息内容 无模板 暂时固定
     */
    public static final String ADD_USER_MESSAGE_INFO = "新会员 %s 已转入到您的名下。祝您财源滚滚，大吉大利！";

    /**
     * 代理接受契约通知
     */
    public static final String CONTRACT_TITLE = "成功签署契约";
    /**
     * 代理接受契约通知消息
     */
    public static final String CONFIRM_AGENT_MESSAGE_INFO = "您已成功签署%s契约，在致富的道路上一路有你，愿我们携手共进再创辉煌";

    /**
     * 总代收到佣金
     */
    public static final String COMMISSION_RECEIVE_TITLE = "佣金奖励通知";
    /**
     * 总代收到佣金通知消息
     */
    public static final String COMMISSION_RECEIVE_TITLE_MESSAGE_INFO = "第%s期佣金奖励%s元已到账，可在\"历史佣金\"内查看，如有疑问请联系客服或您的代理专员。";

    /**
     * 平台/上级给下级发送契约
     */
    public static final String CONTRACT_RECEIVE_TITLE = "新的契约通知";
    /**
     * 上级给下级发送契约通知消息
     */
    public static final String CONTRACT_RECEIVE_TITLE_MESSAGE_INFO = "收到上级给您发来新的%s契约，可在【我的契约】页面可查看详情！契约是代理享受收益的前提，每一份契约都要细心检查哟。";
    public static final String CONTRACT_SYSTEM_RECEIVE_TITLE_MESSAGE_INFO = "收到一份新的%s契约，可在【我的契约】页面可查看详情！契约是代理享受收益的前提，每一份契约都要细心检查哟。";

    /**
     * 下级签约通知
     */
    public static final String CONTRACT_SUB_TITLE = "下级签约通知";
    /**
     * 下级签约通知消息
     */
    public static final String  CONTRACT_SUB_MESSAGE_INFO = "下级%s代理已接受您的%s契约，预祝您们在新的环境新的机遇中，合作愉快，万事如意。";

    /**
     * 平台代签通知
     */
    public static final String CONTRACT_SYSTEM_TITLE = "平台代签通知";
    /**
     * 平台代签通知消息
     */
    public static final String  CONTRACT_SYSTEM_MESSAGE_INFO = "平台已代下级%s代理接受您的%s契约，预祝您们在新的环境新的机遇中，合作愉快，万事如意。";
}
