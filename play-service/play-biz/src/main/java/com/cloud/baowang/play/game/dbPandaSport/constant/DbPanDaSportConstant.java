package com.cloud.baowang.play.game.dbPandaSport.constant;

public class DbPanDaSportConstant {


    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 30 * 60 * 1000;

    //玩家注册
    public static final String REGISTER = "/api/user/create";


    //游戏登陆
    public static final String LOGIN = "/api/user/login";


    //会员踢下线
    public static final String LOGOUT_GAME = "/api/user/kickOutUser";

    //拉单
    public static final String ORDER_RECORD = "/api/bet/queryBetList";


    //查询交易记录列表
    public static final String QUERY_TRANSFER_LIST = "/api/fund/queryTransferList";


    /**
     * 获取单条交易记录
     */
    public static final String GET_TRANSFER_RECORD = "/api/fund/getTransferRecord";








}
