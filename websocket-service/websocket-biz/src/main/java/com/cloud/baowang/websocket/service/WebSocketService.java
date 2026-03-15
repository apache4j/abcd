package com.cloud.baowang.websocket.service;

import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSAuthorize;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import io.netty.channel.Channel;

import java.util.List;

public interface WebSocketService {

    /**
     * 处理所有ws连接的事件
     *
     * @param clientType
     * @param channel
     */
    void connect(String clientType, Channel channel, String uid);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void removed(Channel channel);


    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToAllOnline(ClientTypeEnum clientTypeEnum, WSBaseResp<?> wsBaseResp);

    /**
     * 推动消息给站点下所有在线已订阅该topic的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToSiteByTopic(ClientTypeEnum clientTypeEnum, String siteCode, WSBaseResp<?> wsBaseResp);

    /**
     * 推动消息给站点下的uidList
     *
     * @param uidList    接受消息的人
     * @param wsBaseResp 发送的消息体
     */
    void sendToSiteUidList(ClientTypeEnum clientTypeEnum, String siteCode, List<String> uidList, WSBaseResp<?> wsBaseResp);

    boolean authorize(String clientType, Channel channel, WSAuthorize wsAuthorize);

    /**
     * 订阅ws消息
     *
     * @param channel
     * @param wsSubscribeEnum
     * @param data
     */
    void subscribe(Channel channel, WSSubscribeEnum wsSubscribeEnum, String data);
}
