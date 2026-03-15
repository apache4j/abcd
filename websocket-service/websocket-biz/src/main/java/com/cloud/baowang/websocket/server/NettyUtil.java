package com.cloud.baowang.websocket.server;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.config.ThreadPoolConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
public class NettyUtil {
    static {
        threadPoolTaskExecutor = SpringUtils.getBean(ThreadPoolConfig.WS_EXECUTOR);
    }

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> CLIENT_TYPE = AttributeKey.valueOf("client-type");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    public static AttributeKey<String> UID = AttributeKey.valueOf("uid");
    public static AttributeKey<String> ACCOUNT = AttributeKey.valueOf("account");
    public static AttributeKey<String> SITE_CODE = AttributeKey.valueOf("siteCode");
    private static final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey).get();
    }

    public static <T> void topicPublish(Channel channel, WSSubscribeEnum wsSubscribeEnum, T data) {
        log.info("ws发送主题信息,topic:{},uid:{},data:{}", wsSubscribeEnum.getTopic(), getAttr(channel, UID), data);
        sendMsg(channel, new WSBaseResp<>(wsSubscribeEnum.getTopic(), data));
    }

    public static <T> void sendMsg(Channel channel, T data) {
        threadPoolTaskExecutor.execute(() -> {
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(data)));
            log.info("ws 发送消息,uid:{},data:{}", getAttr(channel, UID), data);
        });
    }

    public static <T> void topicPublish(ChannelGroup group, T data) {
        log.info("ws channel group发送主题信息,groupName:{},data:{}", group.name(), data);
        sendMsg(group, data);
    }

    public static <T> void sendMsg(ChannelGroup group, T data) {
        threadPoolTaskExecutor.execute(() -> group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(data))));
    }
}
