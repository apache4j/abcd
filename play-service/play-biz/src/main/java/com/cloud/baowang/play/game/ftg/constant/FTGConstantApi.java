package com.cloud.baowang.play.game.ftg.constant;

public class FTGConstantApi {

    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 35 * 60 * 1000;

    /**
     * 获取游戏列表
     */
    public static final String GAME_LIST = "api/v4/game/outside/list";

    /**
     * 新增用戶
     */
    public static final String CREATE_USER = "api/user/outside";

    /**
     * 登录游戏
     */
    public static final String LOGIN_GAME = "api/v2/game/outside/link";

    /**
     * 登出游戏
     */
    public static final String LOGOUT_GAME = "api/user/outside/kick";


    /**
     * 下注记录
     */
    public static final String ORDER_RECORD = "api/v3/wagers/outside/list";







}
