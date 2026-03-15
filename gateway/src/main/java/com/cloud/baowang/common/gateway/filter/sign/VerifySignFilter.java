package com.cloud.baowang.common.gateway.filter.sign;

import cn.hutool.core.lang.Pair;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AESCBCUtil;
import com.cloud.baowang.common.core.utils.CharArrayUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.gateway.filter.abstractFilter.AgentAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.BusinessAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.SiteAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAbstractFilter;
import com.cloud.baowang.common.gateway.properties.EncryptProperties;
import com.cloud.baowang.common.gateway.properties.IgnoreSignProperties;
import com.cloud.baowang.common.gateway.util.GatewayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 签名拦截器 默认最高优先级 需要验签就实现抽象过滤器
 */
@Slf4j
@Order(-1)
@Component
@ConditionalOnProperty(value = "sign.enable", havingValue = "true")
public class VerifySignFilter implements UserAbstractFilter, AgentAbstractFilter, SiteAbstractFilter, BusinessAbstractFilter {

    private static final String SIGN_HEAD = "Sign";
    @Value("${sign.salt}")
    private String salt;
    @Value("${sign.timeliness}")
    private Long timeliness;
    @Autowired
    private EncryptProperties encryptProperties;

    @Autowired
    private IgnoreSignProperties ignoreSign;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        if (JwtUtils.matches(url, ignoreSign.getSignUrl())) {
            return chain.filter(exchange);
        }
        String sign = request.getHeaders().getFirst(SIGN_HEAD);
        if (Strings.isBlank(sign)) {
            throw new BaowangDefaultException(ResultCode.SIGN_EMPTY);
        }
        // 验签
        String token = verifySign(sign);
//         放入token
        GatewayUtil.addHeader(mutate, TokenConstants.SIGN, token);

        return chain.filter(exchange);
    }

    private String verifySign(String sign) {
        try {
            String signDecrypt = AESCBCUtil.decrypt(encryptProperties.getAes().getSecretKey(), encryptProperties.getAes().getIv(), sign);
            if (Strings.isBlank(signDecrypt)) {
                log.warn("签名异常,signDecrypt:{}", signDecrypt);
                throw new BaowangDefaultException(ResultCode.SIGN_ERROR);
            }
            String[] split = signDecrypt.split(CommonConstant.ASTERISK);
            if (split.length < 2) {
                log.warn("签名异常,signDecrypt:{}", signDecrypt);
                throw new BaowangDefaultException(ResultCode.SIGN_ERROR);
            }
            // 接口请求时间间隔判断
            if (timeliness > 0) {
                long timestamp = Long.parseLong(split[1]);
                long nowMillis = System.currentTimeMillis();
                long step = nowMillis - timestamp;
                if (Math.abs(step) > timeliness) {
                    throw new BaowangDefaultException(ResultCode.SIGN_EXPIRED);
                }
            }
            // 解析token与盐值
            Pair<String, String> pair = parseTokenAndSalt(split[0]);
            if (!pair.getKey().equals(salt)) {
                throw new BaowangDefaultException(ResultCode.SIGN_ERROR);
            }
            // 放入token
            return pair.getValue();
        } catch (BaowangDefaultException c) {
            log.warn("签名异常,sign:{}", sign);
            throw c;
        } catch (Exception e) {
            log.error("签名异常,sign:{},error:", sign, e);
            throw new BaowangDefaultException(ResultCode.SIGN_ERROR);
        }
    }

    /**
     * 解析token与盐值
     *
     * @param tokenAndSalt 加盐token
     * @return pair<盐值 ， token>
     */
    private Pair<String, String> parseTokenAndSalt(String tokenAndSalt) {
        // 与盐值一样即返回
        if (tokenAndSalt.equals(salt)) {
            return new Pair<>(tokenAndSalt, null);
        } else {
            return CharArrayUtil.extractAndMerge(tokenAndSalt, 4, 5, 10, 11, 18, 19, tokenAndSalt.length() - 4, tokenAndSalt.length() - 3);
        }
    }
}
