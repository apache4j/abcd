package com.cloud.baowang.websocket.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<PingWebSocketFrame> {
    private static final PongWebSocketFrame pongmessage = new PongWebSocketFrame();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingWebSocketFrame msg) throws Exception {
        ctx.channel().writeAndFlush(pongmessage);
    }
}
