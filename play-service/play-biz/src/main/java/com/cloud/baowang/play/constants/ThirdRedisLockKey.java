package com.cloud.baowang.play.constants;

public class ThirdRedisLockKey {
    /**
     * 场馆拉单lock
     */
    public static final String CASINO_THIRD_PULL_BET_LOCK_KEY = "third::casino::bet::%s::%s";

    /**
     * 游戏名称与code映射 缓存key
     */
    public final static String THIRD_GAME_INFO_MAP_KEY = "third:game:info:map";

    /**
     * 登录三方场馆用户锁 %s 用户账号
     */
    public static final String THIRD_LOGIN_GAME_KEY = "play:login:game:";




}
