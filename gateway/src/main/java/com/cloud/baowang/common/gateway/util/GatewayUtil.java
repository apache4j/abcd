package com.cloud.baowang.common.gateway.util;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class GatewayUtil {

    /**
     * 获取请求token
     */
    public static String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.SIGN);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        }  else if (!ObjectUtils.isEmpty(token) && token.startsWith(ServletUtil.urlEncode(TokenConstants.PREFIX))) {
            token = ServletUtil.urlDecode(token);
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        } else if (ObjectUtils.isEmpty(token)) {
            if (ObjectUtils.isEmpty(token)) {
                return token;
            }
            token = request.getQueryParams().get(TokenConstants.SIGN).get(0);

        }
        return token;
    }

    public static void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtil.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    /**
     * 将 JWT 鉴权失败的消息响应给客户端
     */
    public static Mono<Void> unauthorizedResponse(ServerHttpResponse serverHttpResponse, ResponseVO<?> result) {
        serverHttpResponse.getHeaders().add(CommonConstant.CONTENT_TYPE_HEAD, CommonConstant.CONTENT_TYPE_HEAD_VALUE);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory()
                .wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }
}
