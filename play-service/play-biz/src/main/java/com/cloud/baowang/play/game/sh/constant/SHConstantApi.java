/**
 * @(#)KYConstant.java, 9月 15, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.game.sh.constant;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/9/15
 */
public class SHConstantApi {

    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 35 * 60 * 1000;

    /**
     * 获取桌台集合
     */
    public static final String DESK_STATUS_LIST = "/game/api/deskStatusList";


    /**
     * 登陆接口
     */
    public static final String LOGIN = "/game/api/loginMZ";


    /**
     * 退出接口
     */
    public static final String LOGOUT = "/game/api/logout";


    /**
     * 视讯拉单
     */
    public static final String GET_BET_ORDER_LIST = "/game/api/getBetOrderList";

}
