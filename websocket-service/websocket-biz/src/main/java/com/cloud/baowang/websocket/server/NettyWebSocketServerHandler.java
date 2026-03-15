package com.cloud.baowang.websocket.server;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSAuthorize;
import com.cloud.baowang.websocket.service.WebSocketService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import static com.cloud.baowang.websocket.server.NettyUtil.UID;


@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static WebSocketService webSocketService;

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        webSocketService = getService();
        NettyWebSocketServer.CHANNEL_GROUP.add(ctx.channel());
        log.info("新的客户端连接: {}，当前在线数：{}", ctx.channel().remoteAddress(), NettyWebSocketServer.CHANNEL_GROUP.size());
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
        NettyWebSocketServer.CHANNEL_GROUP.remove(ctx.channel());
        log.info("客户端断开连接: {}，当前在线数：{}", ctx.channel().remoteAddress(), NettyWebSocketServer.CHANNEL_GROUP.size());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
        super.channelUnregistered(ctx);
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", NettyUtil.getAttr(ctx.channel(), UID));
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            String clientType = NettyUtil.getAttr(ctx.channel(), NettyUtil.CLIENT_TYPE);
            // 认证
            boolean authorize = webSocketService.authorize(clientType, ctx.channel(), new WSAuthorize(token));
            if (!authorize) {
                userOffLine(ctx);
            } else {
                webSocketService.connect(clientType, ctx.channel(), NettyUtil.getAttr(ctx.channel(), UID));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息:", cause);
        ctx.channel().close();
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // log.info("收到信息：{}", msg.text());
        String text = msg.text();
        // 心跳
        if (text.equals(CommonConstant.business_one_str)) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(CommonConstant.business_two_str));
            return;
        }
        // 订阅标识
        if (text.startsWith(CommonConstant.ws_topic_prefix)) {
            String[] textSplit = text.split(CommonConstant.COLON, 2);
            String data = null;
            if (textSplit.length > 1) {
                data = textSplit[1];
            }
            String subscribeType=textSplit[0];
            WSSubscribeEnum wsSubscribeEnum = WSSubscribeEnum.of(subscribeType);
            log.info("text:{},subscribeType:{},",text,subscribeType);
            if (ObjUtil.isNotNull(wsSubscribeEnum)) {
                webSocketService.subscribe(ctx.channel(), wsSubscribeEnum, data);
            } else {
                log.error("未知订阅标识,uid:{},topic:{}", NettyUtil.getAttr(ctx.channel(), UID), text);
            }
        }
    }
}
