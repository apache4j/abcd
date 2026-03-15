package com.cloud.baowang.websocket.server;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

@Slf4j
public class HttpAttrHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest request) {
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 获取通行证
            String p = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("p")).map(CharSequence::toString).orElse(Strings.EMPTY);
            // ws type 默认为客户端
            String type = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("t")).map(CharSequence::toString).orElse(ClientTypeEnum.CLIENT.getCode());
            ClientTypeEnum clientTypeEnum = ClientTypeEnum.of(type);
            if (ObjUtil.isNull(clientTypeEnum)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, p);
            NettyUtil.setAttr(ctx.channel(), NettyUtil.CLIENT_TYPE, type);
            // 获取请求路径
            request.setUri(urlBuilder.getPath().toString());
            String ip = getClientIP(request);
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 获取真是ip地址
     *
     * @param request
     * @return
     */
    public static String getClientIP(FullHttpRequest request) {
        try {

            HttpHeaders headers = request.headers();
            String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                    "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
            String ip;
            for (String header : headerNames) {
                ip = headers.get(header);
                if (!NetUtil.isUnknown(ip)) {
                    return NetUtil.getMultistageReverseProxyIp(ip);
                }
            }
            ip = request.uri();
            return NetUtil.getMultistageReverseProxyIp(ip);
        } catch (Exception e) {
            log.error("获取ip异常", e);
        }
        return "127.0.0.1";
    }

}