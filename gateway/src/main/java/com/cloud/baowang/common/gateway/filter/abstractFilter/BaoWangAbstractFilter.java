package com.cloud.baowang.common.gateway.filter.abstractFilter;

import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.gateway.util.GatewayIpUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;

public interface BaoWangAbstractFilter extends GatewayFilter {

    default void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtil.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    default void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }


    /**
     * 获取请求token
     */
    default String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.SIGN);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        } else if (!ObjectUtils.isEmpty(token) && token.startsWith(ServletUtil.urlEncode(TokenConstants.PREFIX))) {
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

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    default String getIp(ServerHttpRequest request) {
        return GatewayIpUtils.getRealIpAddress(request);
    }

    /**
     * 判断是否是swagger path
     *
     * @param path uri
     * @return boolean
     */
    default boolean isSwagger(String path) {
        return path.contains("/v3/api-docs")
                || path.contains("/swagger")
                || path.contains("/webjars");
    }
}
