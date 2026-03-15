package com.cloud.baowang.common.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.auth.util.AgentAuthUtil;
import com.cloud.baowang.common.auth.vo.AgentTokenVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.gateway.filter.abstractFilter.AgentAbstractFilter;
import com.cloud.baowang.common.gateway.properties.AgentIgnoreAuthProperties;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.cloud.baowang.common.core.constants.CommonConstant.Deviceid;
import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;

/**
 * 代理api拦截器
 */
@Order(1)
@Component
@Slf4j
public class AgentApiFilter implements AgentAbstractFilter {
    private final AgentIgnoreAuthProperties ignoreAuth;


    public AgentApiFilter(AgentIgnoreAuthProperties ignoreAuth) {
        this.ignoreAuth = ignoreAuth;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        String url = request.getURI().getPath();

        // 跳过不需要验证的路径
        if (JwtUtils.matches(url, ignoreAuth.getWhites())) {
            return chain.filter(exchange);
        }
        String xCustom = request.getHeaders().getFirst(X_CUSTOM);
        String deviceId = request.getHeaders().getFirst(Deviceid);
        log.info("代理接口获取到的xCustom:{}", xCustom);

        if (StrUtil.isBlank(xCustom)) {
            throw new BaowangDefaultException(ResultCode.REFERER_EMPTY);
        }

        String siteCodeAndTimezone = (String) RedisUtil.getLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, xCustom);
        if (!StringUtils.hasText(siteCodeAndTimezone)) {
            log.info("xCustom: 代理接口获取到的siteCode与timezone:{}为空", siteCodeAndTimezone);
          throw new BaowangDefaultException(ResultCode.AGENT_DOMAIN_IS_NULL);
        }
        String[] split = siteCodeAndTimezone.split(CommonConstant.COLON,2);
        String siteCode = split[0];
        String timezone = split[1];
        addHeader(mutate, RequestBasicInfo.Fields.timezone, timezone);

        Integer handingCapMode = 0;//默认是海外盘模式
        String handingCapRedisKey=CacheConstants.SITE_HANDICAPMODE.concat(siteCode);
        if(RedisUtil.isKeyExist(handingCapRedisKey)){
            handingCapMode = (Integer) RedisUtil.getValue(handingCapRedisKey);
        }
        addHeader(mutate, RequestBasicInfo.Fields.handicapMode, handingCapMode);

        String platCurrencyName = (String) RedisUtil.getMapValue(CacheConstants.KEY_SITE_PLAT_CURRENCY, siteCode);
        String platCurrencySymbol = Character.toString(CommonConstant.PLAT_FORM_SYMBOL);
        String platCurrencyIcon = (String) RedisUtil.getMapValue(CacheConstants.KEY_SITE_PLAT_CURRENCY_ICON, siteCode);
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencyName,platCurrencyName);
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencySymbol, StringUtil.currencyToHex(platCurrencySymbol));
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencyIcon,platCurrencyIcon);
        addHeader(mutate, RequestBasicInfo.Fields.deviceId, deviceId);
        log.info("获取到当前站点:{}，时区:{}，平台币:{},符号:{},盘口模式:{}", siteCode, timezone, platCurrencyName, platCurrencySymbol,handingCapMode);
        // 跳过不需要验证的路径
        if (JwtUtils.matches(url, ignoreAuth.getWhites())) {
            addHeader(mutate, RequestBasicInfo.Fields.siteCode, siteCode);
            return chain.filter(exchange);
        }
        String token = getToken(request);

        AgentTokenVO agentTokenVO = AgentAuthUtil.agentAuth(token);

        // 设置用户信息到请求
        addHeader(mutate, RequestBasicInfo.Fields.userAccount, agentTokenVO.getAgentAccount());
        addHeader(mutate, RequestBasicInfo.Fields.oneId, agentTokenVO.getAgentId());
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, agentTokenVO.getSiteCode());

        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

}
