package com.cloud.baowang.play.game.dbDj.constant;

public class DbDJConstant {


    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 30 * 60;

    //玩家注册
    public static final String REGISTER = "/api/member/register";


    //游戏登陆
    public static final String LOGIN = "/api/v2/member/login";


    //会员踢下线
    public static final String LOGOUT_GAME = "/api/member/offline";

    //拉单 电子
    public static final String ORDER_RECORD = "/v2/pull/order/queryScroll";

    //拉单 英雄召唤
    public static final String HERO_ORDER_RECORD = "/pull/ticketOrder/query";






}
