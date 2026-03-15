package com.cloud.baowang.play.constants;

public class ThirdConstants {
    public static final Integer CREATE_MEMBER_FAIL = 0;

    public static final Integer CREATE_MEMBER_SUCCESS = 1;

    /**
     * 沙巴-体育 登陆
     */
    public static final String SBA_LOGIN_API = "login";

    /**
     * 沙巴-体育 注册
     */
    public static final String SBA_REGISTER_API = "api/CreateMember";

    /**
     * 沙巴体育 拉单
     */
    public static final String SBA_BET_ORDER = "api/GetBetDetail";

    /**
     * 沙巴体育 查询状态
     */
    public static final String SBA_CHECK_STATUS = "api/checkticketstatus";


    /**
     * 沙巴体育 取得所有已达重试上限的注单。
     */
    public static final String SBA_GET_REACH_LIMIT_TRANS = "api/getreachlimittrans";

    /**
     * 沙巴体育 重试
     */
    public static final String SBA_RET_RY_OPERATION = "api/retryoperation";


    /**
     * 沙巴-体育 获取赛事信息
     */
    public static final String GET_EVENTS = "sports/v1/GetEvents";

    /**
     * 视讯-拉单
     */
    public static final String SH_BET_ORDER = "/game/api/getBetOrderList";

    /**
     * 沙巴体育客户端获取详情
     */
    public static final String SBA_CLIENT_ORDER_DETAIL_URL = "betting/v1/GetBetDetails?start=%s&end=%s&isSettled=%s&Language=%s";


    /**
     * 沙巴-体育 取得联赛/队伍/投注类型/体育 类型名称
     */
    public static final String SBA_SELECTION_INFO = "api/GetBetSelectionInfo";
}
