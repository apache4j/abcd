package com.cloud.baowang.common.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.auth.vo.UserTokenVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAbstractFilter;
import com.cloud.baowang.common.gateway.properties.UserIgnoreAuthProperties;
import com.cloud.baowang.common.gateway.util.GatewayUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.cloud.baowang.common.core.constants.CommonConstant.*;

/**
 * 用户api拦截器
 */
@Order(1)
@Component
@Slf4j
public class UserApiFilter implements UserAbstractFilter {

    private final UserIgnoreAuthProperties ignoreAuth;


    public UserApiFilter(UserIgnoreAuthProperties ignoreAuth) {
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
        String device_type_version = request.getHeaders().getFirst(DEVICE_TYPE_VERSION);
        // bizCustom="https://indla.bwsh.store";
        //referer="https://indla.bwsh.store";
        log.info("会员接口获取到的xCustom:{}", xCustom);

        if (StrUtil.isBlank(xCustom)) {
            throw new BaowangDefaultException(ResultCode.REFERER_EMPTY);
        }

        String siteCodeAndTimezone = (String) RedisUtil.getLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, xCustom);
        //siteCodeAndTimezone="10002:UTC+8";
        if (!StringUtils.hasText(siteCodeAndTimezone)) {
            log.info("xCustom: 会员接口获取到的siteCode与timezone:{}为空", siteCodeAndTimezone);
            throw new BaowangDefaultException(ResultCode.AGENT_DOMAIN_IS_NULL);
        }
        String[] split = siteCodeAndTimezone.split(CommonConstant.COLON, 2);
        String siteCode = split[0];
        String timezone = split[1];
        addHeader(mutate, RequestBasicInfo.Fields.timezone, timezone);
        log.info("会员接口获取到的siteCode:{},时区:{}", siteCode, timezone);

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
        addHeader(mutate, RequestBasicInfo.Fields.version, request.getHeaders().getFirst(Version));
        log.info("获取到当前站点:{}，时区:{}，平台币:{},符号:{},盘口模式:{}", siteCode, timezone, platCurrencyName, platCurrencySymbol,handingCapMode);
        addHeader(mutate, RequestBasicInfo.Fields.deviceId, deviceId);
        addHeader(mutate, RequestBasicInfo.Fields.deviceTypeVersion, device_type_version);

        if (JwtUtils.matches(url, ignoreAuth.getWhites())) {
            addHeader(mutate, RequestBasicInfo.Fields.siteCode, siteCode);
            //判断是否有token，有的话也解析, 如果token过期，当作没有处理
            String token = GatewayUtil.getToken(request);
            if (!ObjectUtils.isEmpty(token)) {
                try {
                    UserTokenVO userTokenVO = UserAuthUtil.userAuth(token);
                    //如果token中的siteCode与域名解析的不一致，则抛出异常
                    if (!siteCode.equals(userTokenVO.getSiteCode())) {
                        throw new BaowangDefaultException(ResultCode.SITECODE_IS_ERROR);
                    }
                    addHeader(mutate, RequestBasicInfo.Fields.userAccount, userTokenVO.getUserAccount());
                    addHeader(mutate, RequestBasicInfo.Fields.oneId, userTokenVO.getUserId());
                } catch (Exception e) {
                    log.info(e.getMessage());
                    e.printStackTrace();
                    if(e instanceof BaowangDefaultException ){
                        BaowangDefaultException exception = (BaowangDefaultException) e;
                        if(exception.getResultCode().getCode() == ResultCode.SITECODE_IS_ERROR.getCode()){
                            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
                        }

                    }
                }
            }

            return chain.filter(exchange.mutate().request(mutate.build()).build());
        }
        String token = GatewayUtil.getToken(request);

        UserTokenVO userTokenVO = UserAuthUtil.userAuth(token);

        //如果token中的siteCode与域名解析的不一致，则抛出异常 //fixme
        /*if (!siteCode.equals(userTokenVO.getSiteCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_USER_ACCOUNT_NOT_EXIST);
        }*/

        // 设置用户信息到请求
        addHeader(mutate, RequestBasicInfo.Fields.userAccount, userTokenVO.getUserAccount());
        addHeader(mutate, RequestBasicInfo.Fields.oneId, userTokenVO.getUserId());
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, userTokenVO.getSiteCode());

        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }
}
