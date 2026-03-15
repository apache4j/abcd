package com.cloud.baowang.common.gateway.filter;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.auth.util.SiteAuthUtil;
import com.cloud.baowang.common.auth.vo.SiteTokenVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.gateway.filter.abstractFilter.SiteAbstractFilter;
import com.cloud.baowang.common.gateway.properties.AuthenticationProperties;
import com.cloud.baowang.common.gateway.properties.SiteIgnoreAuthProperties;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;

import static com.cloud.baowang.common.core.constants.CommonConstant.Deviceid;
import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;

/**
 * 代理api拦截器
 */
@Order(1)
@Component
@Slf4j
public class SiteApiFilter implements SiteAbstractFilter {
    private final SiteIgnoreAuthProperties ignoreAuth;
    private final AuthenticationProperties authenticationProperties;

    public SiteApiFilter(SiteIgnoreAuthProperties ignoreAuth, AuthenticationProperties authenticationProperties) {
        this.ignoreAuth = ignoreAuth;
        this.authenticationProperties = authenticationProperties;
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
        // bizCustom="https://indla.bwsh.store";
        //xCustom="https://indla.bwsh.store";
        log.info("站点后台接口获取到的xCustom:{}", xCustom);

        if (StrUtil.isBlank(xCustom)) {
            throw new BaowangDefaultException(ResultCode.REFERER_EMPTY);
        }

        String siteCodeAndTimezone = (String) RedisUtil.getLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, xCustom);
        if (!StringUtils.hasText(siteCodeAndTimezone)) {
            log.info("xCustom: 站点后台接口获取到的siteCode与timezone:{}为空", siteCodeAndTimezone);
            throw new BaowangDefaultException(ResultCode.AGENT_DOMAIN_IS_NULL);
        }
        String[] split = siteCodeAndTimezone.split(CommonConstant.COLON, 2);
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
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencyName, platCurrencyName);
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencySymbol, StringUtil.currencyToHex(platCurrencySymbol));
        addHeader(mutate, RequestBasicInfo.Fields.platCurrencyIcon, platCurrencyIcon);
        addHeader(mutate, RequestBasicInfo.Fields.deviceId, deviceId);
        log.info("获取到当前站点:{}，时区:{}，平台币:{},符号:{},url:{},盘口模式:{}", siteCode, timezone, platCurrencyName, platCurrencySymbol, url,handingCapMode);
        // 跳过不需要验证的路径
        if (JwtUtils.matches(url, ignoreAuth.getWhites())) {
            addHeader(mutate, RequestBasicInfo.Fields.siteCode, siteCode);
            return chain.filter(exchange);
        }

        String token = getToken(request);
        if (ObjectUtils.isEmpty(token)) {
            throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
        }
        SiteTokenVO siteTokenVO = SiteAuthUtil.siteAuth(token, url, authenticationProperties.getEnable());

        //如果token中的siteCode与域名解析的不一致，则抛出异常  //fixme
       /* if (!siteCode.equals(siteTokenVO.getSiteCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_USER_ACCOUNT_NOT_EXIST);
        }*/

        // 设置用户信息到请求
        addHeader(mutate, TokenConstants.SUPER_ADMIN, Boolean.parseBoolean(JwtUtils.getSuperAdmin(token)));
        addHeader(mutate, RequestBasicInfo.Fields.oneId, siteTokenVO.getAdminId());
        addHeader(mutate, RequestBasicInfo.Fields.userAccount, siteTokenVO.getUserName());
        addHeader(mutate, RequestBasicInfo.Fields.roleIds, CollUtil.isNotEmpty(siteTokenVO.getUserRoleIds()) ? String.join(CommonConstant.COMMA, siteTokenVO.getUserRoleIds()) : Strings.EMPTY);
        addHeader(mutate, RequestBasicInfo.Fields.dataDesensitization, siteTokenVO.getDataDesensitization());
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, siteTokenVO.getSiteCode());
        HttpHeaders headers = request.getHeaders();
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

}
