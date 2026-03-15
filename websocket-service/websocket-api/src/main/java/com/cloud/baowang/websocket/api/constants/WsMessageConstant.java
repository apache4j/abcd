package com.cloud.baowang.websocket.api.constants;

public class WsMessageConstant {
    /*websocket广播消息*/
    public final static String WS_MESSAGE_BROADCAST_TOPIC = "ws_message_broadcast";
    public final static String WS_MESSAGE_BROADCAST_GROUP_PREFIX = "ws_message_group";

    //Metrics指标监控
    /**
     * WebSocket服务总链接数
     */
    public final static String WS_CONNECTIONS_TOTAL = "WS_CONNECTIONS_TOTAL";
    /**
     * 各个APP站点总在线已连接数
     */
    public final static String WS_CONNECTIONS_CLIENT_TOTAL = "WS_CONNECTIONS_CLIENT_TOTAL";
    /**
     * 各个APP站点总在线已登录用户数
     */
    public final static String WS_CONNECTIONS_CLIENT_USER = "WS_CONNECTIONS_CLIENT_USER";

    /**
     * 各个APP站点总在线游客数
     */
    public final static String WS_CONNECTIONS_CLIENT_GUEST = "WS_CONNECTIONS_CLIENT_GUEST";
}
