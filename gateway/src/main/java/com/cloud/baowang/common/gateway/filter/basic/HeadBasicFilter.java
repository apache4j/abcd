package com.cloud.baowang.common.gateway.filter.basic;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.HttpHeaderUtil;
import com.cloud.baowang.common.core.utils.LanguageUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.gateway.filter.abstractFilter.AgentAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.BusinessAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.SiteAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAdminAbstractFilter;
import com.cloud.baowang.common.gateway.util.GatewayUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.cloud.baowang.common.core.constants.CommonConstant.*;

/**
 * 请求基本过滤器 后置处理
 */
@Order(100)
@Component
@Slf4j
public class HeadBasicFilter implements UserAbstractFilter, UserAdminAbstractFilter, AgentAbstractFilter, SiteAbstractFilter, BusinessAbstractFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("url:{}",request.getPath());
        String realClientIp=getIp(request);
        if(!StringUtils.hasText(realClientIp)){
            log.info("GateWay 获取客户端IP不合法,不能继续操作");
            throw new BaowangDefaultException(ResultCode.SYSTEM_PROTECTION);
        }
        ServerHttpRequest.Builder mutate = request.mutate();
        String language = request.getHeaders().getFirst(LANGUAGE_HEAD);
        String userAgent = request.getHeaders().getFirst(USER_AGENT);
        String device_type_version = request.getHeaders().getFirst(DEVICE_TYPE_VERSION);
        String referer = request.getHeaders().getFirst(X_CUSTOM);
        language = LanguageUtils.getLanguage(language);
        mutate.header(LANGUAGE_HEAD, language);
        mutate.header(X_CUSTOM, referer);
        mutate.header(USER_AGENT, userAgent);
        mutate.header(DEVICE_TYPE_VERSION, device_type_version);
        mutate.header(RequestBasicInfo.Fields.requestIp, realClientIp);
        mutate.header(RequestBasicInfo.Fields.reqClientSource, String.valueOf(HttpHeaderUtil.getDeviceType(userAgent)));
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }
}
