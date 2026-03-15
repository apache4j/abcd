package com.cloud.baowang.common.gateway.filter.encrypt;


import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AESCBCUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.gateway.constants.ServerNameConstant;
import com.cloud.baowang.common.gateway.properties.EncryptProperties;
import com.cloud.baowang.common.gateway.properties.IgnoreEncryptProperties;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * response返回加密，通过请求头判断，order必须要比-1级别拦截器优先级低，后置处理
 */
@Slf4j
@Component
public class ResponseEncryptFilter implements GlobalFilter, Ordered {

    /**
     * 响应体体加密标识header
     */
    private static final String ENCRYPT_HEADER = "X-Encrypt";
    private static final String ENCRYPT_HEADER_VALUE = "encrypted";
    @Autowired
    private EncryptProperties encryptProperties;
    @Autowired
    private IgnoreEncryptProperties ignoreEncryptProperties;

    /**
     * 不加密route
     */
    private static final Map<String, Integer> ignoreServerMap = Maps.newHashMap();

    static {
        ignoreServerMap.put(ServerNameConstant.ADMIN_FOREIGN, CommonConstant.business_one);
        ignoreServerMap.put(ServerNameConstant.PLAY_WALLET_API, CommonConstant.business_one);
        ignoreServerMap.put(ServerNameConstant.PAY_BIZ, CommonConstant.business_one);
    }

    @Override

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = route.getUri().getAuthority();
        // 不加密route
        if (ObjUtil.isNotEmpty(ignoreServerMap.get(routeId))) {
            return chain.filter(exchange);
        }

        // 跳过不需要验证的路径
        if (!encryptProperties.isEnable() || JwtUtils.matches(exchange.getRequest().getURI().getPath(), ignoreEncryptProperties.getWhites())) {
            return chain.filter(exchange);
        }

        return chain.filter(exchange.mutate().response(getServerHttpResponseDecorator(exchange)).build());
    }

    private ServerHttpResponseDecorator getServerHttpResponseDecorator(ServerWebExchange exchange) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpHeaders headers = getHeaders();
                if (headers.getContentType() != null &&
                        headers.getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
                    // json格式响应体做加密
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                        // 获取响应体 组装拆分数据块
                        DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffer);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);

                        // 加密响应体
                        String encryptedBase64 = encrypt(content);

                        // 重新组装响应体为{"data":密文}
                        String jsonResponse = "{\"data\":\"" + encryptedBase64 + "\"}";
                        // 将调整后密文重新写入响应体
                        byte[] responseBytes = jsonResponse.getBytes();

                        // 请求头调整
                        headers.setContentLength(responseBytes.length);
                        // 已加密标识
                        headers.add(ENCRYPT_HEADER, ENCRYPT_HEADER_VALUE);

                        return exchange.getResponse().bufferFactory().wrap(responseBytes);
                    }));
                } else {
                    return super.writeWith(body);
                }
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
    }

    private String encrypt(byte[] content) {
        try {
            String contentStr = new String(content, StandardCharsets.UTF_8);
            // 加密
            return AESCBCUtil.encrypt(encryptProperties.getAes().getSecretKey(), encryptProperties.getAes().getIv(), contentStr);
        } catch (Exception e) {
            log.error("response返回加密失败，加密内容：{}", content, e);
            throw new RuntimeException("response返回加密失败");
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
