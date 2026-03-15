package com.cloud.baowang.play.game.acelt.constant;

public final class AceLtConstant {

    public static final Integer SUCC_CODE = 200;

    public static final String H5 = "0";

    public static final String PC = "1";

    public static final Integer FAIL_TRANS = 0;

    public static final Integer SUCCESS_TRANS = 1;

    public static final Integer PEND_TRANS = 2;


    public static final String ACELT_PLATFORM_TOKEN = "acelt_platformToken:";

    /**
     * 登陆
     */
    public static final String LOGIN = "/third/rest/third/u/openApi/v2/Player/GetBaseInfo";

    /**
     * 查询彩种信息
     */
    public static final String QUERY_GAME_LIST = "/third/rest/third/u/openApi/v2/game/queryGameList";

    /**
     * 拉单
     */
    public static final String BET_ORDER = "/third/rest/third/u/openApi/v2/Query/GameRecord";

}
