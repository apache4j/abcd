package com.cloud.baowang.play.wallet.vo.jdb.constant;

public class JDBConstantApi {

    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 35 * 60 * 1000;

    /**
     * 获取游戏列表
     */
    public static final String GAME_LIST = "api/v4/game/outside/list";


    /**
     * 登录游戏
     */
    public static final String LOGIN_GAME = "apiRequest.do";

    /**
     * gType
     */
    public static final int[] gTypes = new int[]{0, 7, 9, 12, 18};
    /**
     * 下注记录
     */
    public static final String ORDER_RECORD = "api/v3/wagers/outside/list";







}
