package com.cloud.baowang.common.gateway.filter;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.SiteStatusEnums;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.SiteMaintenanceVO;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.gateway.constants.I18nMessageConstant;
import com.cloud.baowang.common.gateway.filter.abstractFilter.AgentAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.BusinessAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.SiteAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAbstractFilter;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAdminAbstractFilter;
import com.cloud.baowang.common.gateway.util.GatewayUtil;
import com.cloud.baowang.common.gateway.vo.AreaLimitVO;
import com.cloud.baowang.common.gateway.vo.ServerMaintenanceVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.cloud.baowang.common.core.constants.CommonConstant.LANGUAGE_HEAD;
import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;
import static com.cloud.baowang.common.core.constants.RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY;
import static com.cloud.baowang.common.core.utils.DateUtils.FULL_FORMAT_6;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.ADMIN_FOREIGN;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.AGENT_FOREIGN;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.APP_FOREIGN;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.BUSINESS_FOREIGN;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.PAY_BIZ;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.PLAY_WALLET_API;
import static com.cloud.baowang.common.gateway.constants.ServerNameConstant.SITE_FOREIGN;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 包网网关全局拦截器
 */
@Slf4j
@Order(1)
@Component
public class BaoWangGlobalFilter implements GlobalFilter , Ordered {

    private final List<UserAbstractFilter> userApiFilters;

    private final List<AgentAbstractFilter> agentApiFilters;

    private final List<UserAdminAbstractFilter> userAdminFilters;

    private final List<SiteAbstractFilter> siteAbstractFilters;

    private final List<BusinessAbstractFilter> businessAbstractFilters;

    private BaoWangGlobalFilter(List<UserAbstractFilter> userApiFilters,
                                List<AgentAbstractFilter> agentApiFilters,
                                List<UserAdminAbstractFilter> userAdminFilters,
                                List<SiteAbstractFilter> siteAbstractFilters,
                                List<BusinessAbstractFilter> businessAbstractFilters) {
        this.userApiFilters = userApiFilters;
        this.agentApiFilters = agentApiFilters;
        this.userAdminFilters = userAdminFilters;
        this.siteAbstractFilters = siteAbstractFilters;
        this.businessAbstractFilters = businessAbstractFilters;
    }

    @Value("${netty.application.name}")
    private String nettyRouteId;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant start = Instant.now();
        ServerHttpRequest request=exchange.getRequest();
        String path = request.getURI().getPath();
        // websocket过滤
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
        //全局日志追踪ID 解决skywalking gateway无法追踪问题
        String traceId=  SnowFlakeUtils.getSnowId();
        MDC.put(CommonConstant.TRACE_ID, traceId);
        addHeader(request.mutate(),CommonConstant.TRACE_ID,traceId);
        log.info("gateway entrance path:{},routeId:{}",path,routeId);
        if (routeId.equals(nettyRouteId)) {
            return chain.filter(exchange);
        }
        try {
            switch (routeId) {
                case PLAY_WALLET_API, PAY_BIZ -> {}
                case APP_FOREIGN -> {
                    // 服务维护校验
                    ResponseVO<?> responseVO = serverMaintenanceCheck(exchange);
                    if (!responseVO.isOk()) {
                        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), responseVO);
                    }
                    /*// 区域控制校验
                    ResponseVO<?> areaLimitResponseVO = areaLimitCheck(GatewayIpUtils.getRealIpAddress(exchange.getRequest()));
                    if (!areaLimitResponseVO.isOk()) {
                        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), areaLimitResponseVO);
                    }*/
                    userApiFilters.forEach(userFilter -> userFilter.filter(exchange, chain));
                }
                case SITE_FOREIGN -> {
                    // 服务维护校验
                    ResponseVO<?> responseVO = serverMaintenanceCheck(exchange);
                    if (!responseVO.isOk()) {
                        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), responseVO);
                    }
                    siteAbstractFilters.forEach(siteFilter -> siteFilter.filter(exchange, chain));
                }
                case AGENT_FOREIGN -> {
                    // 服务维护
                    ResponseVO<?> responseVO = serverMaintenanceCheck(exchange);
                    if (!responseVO.isOk()) {
                        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), responseVO);
                    }
                    agentApiFilters.forEach(agentFilter -> agentFilter.filter(exchange, chain));
                }
                case ADMIN_FOREIGN -> userAdminFilters.forEach(filter -> filter.filter(exchange, chain));
                case BUSINESS_FOREIGN -> {
                    // 服务维护校验
                    ResponseVO<?> responseVO = serverMaintenanceCheck(exchange);
                    if (!responseVO.isOk()) {
                        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), responseVO);
                    }
                    businessAbstractFilters.forEach(filter -> filter.filter(exchange, chain));
                }
                default -> throw new BaowangDefaultException(ResultCode.ROUTE_NOT_FOUND);
            }
            return chain.filter(exchange).doFinally(signalType -> {
                MDC.put(CommonConstant.TRACE_ID, traceId);
                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);
                log.info("[Gateway] 请求路径: " + path + ", 耗时: " + duration.toMillis() + "ms");
            });
        } catch (BaowangDefaultException e) {
           log.error("[Gateway] 处理发生异常:",e);
           return unauthorizedResponse(exchange, e.getResultCode());
        }finally {
            MDC.clear();
        }
    }

    private ResponseVO<?> areaLimitCheck(String ip) {
        // 查询ipinfo
//        IPResponse ipResponse = IpAddressUtils.queryIpRegion(ip);
        IPRespVO ipResponse = IpAPICoUtils.getIp(ip);
        String countryCode = null;
        if (ObjUtil.isNotEmpty(ipResponse)) {
            countryCode = ipResponse.getCountryCode();
        }

        // ip限制
        RSet<Object> ipSet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY);
        boolean ipContains = ipSet.contains(ip);
        if (ipContains) {
            AreaLimitVO build = AreaLimitVO.builder().ip(ip).countryCode(countryCode).build();
            ResponseVO<AreaLimitVO> result = ResponseVO.fail(ResultCode.AREA_LIMIT, build);
            result.setMessage(ResultCode.AREA_LIMIT.getDesc());
            return result;
        }
        // 国家限制
        if (StrUtil.isNotBlank(countryCode)) {
            RSet<Object> countrySet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY);
            boolean countryContains = countrySet.contains(countryCode);
            if (countryContains) {
                AreaLimitVO build = AreaLimitVO.builder().ip(ip).countryCode(countryCode).build();
                ResponseVO<AreaLimitVO> result = ResponseVO.fail(ResultCode.AREA_LIMIT, build);
                result.setMessage(ResultCode.AREA_LIMIT.getDesc());
                return result;
            }
        }

        return ResponseVO.success();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ResultCode resultCode) {
        log.warn("[鉴权异常处理]请求路径:{}, {}", exchange.getRequest().getPath(), resultCode.getMessageCode());
        ResponseVO<?> result = ResponseVO.fail(resultCode);
        String desc = resultCode.getDesc();
        if ("PROMPT_14011".equals(resultCode.getMessageCode()) || "PROMPT_10008".equals(resultCode.getMessageCode())) {
            String language = exchange.getRequest().getHeaders().getFirst(LANGUAGE_HEAD);
            if (language != null) {
                language = LanguageUtils.getLanguage(language);
                if ("PROMPT_14011".equals(resultCode.getMessageCode())) {
                    desc = I18nMessageConstant.LOGIN_ERROR_OTHER_AREA_MAP.get(language);
                } else {
                    desc = I18nMessageConstant.LOGIN_EXPIRE_MAP.get(language);
                }
            }
        }
        result.setMessage(desc);
        return GatewayUtil.unauthorizedResponse(exchange.getResponse(), result);
    }

    private ResponseVO<?> serverMaintenanceCheck(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String requestUrl=request.getURI().toString();
        if(requestUrl.endsWith("/v3/api-docs")){
            log.info("serverMain requestUrl:{} 无需拦截",requestUrl);
            return ResponseVO.success();
        }
        String xCustom = request.getHeaders().getFirst(X_CUSTOM);
        String siteCodeAndTimezone = (String) RedisUtil.getLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, xCustom);
        // todo wade
        log.info("xCustom : {},siteCodeAndTimezone: {}",xCustom,siteCodeAndTimezone);
        if (!StringUtils.hasText(siteCodeAndTimezone)) {
            log.info("serverMain xCustom: 接口获取到的siteCode与timezone:{}为空", siteCodeAndTimezone);
            throw new BaowangDefaultException(ResultCode.AGENT_DOMAIN_IS_NULL);
        }
        String[] siteCodeSplit = siteCodeAndTimezone.split(CommonConstant.COLON, 2);
        String siteCode = siteCodeSplit[0];
        String timezone = siteCodeSplit[1];
        // 服务维护校验
        ServerHttpRequest.Builder mutate = request.mutate();
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, siteCode);
       String serverStatusKey=KEY_SERVER_MAINTAIN_SITE_KEY.concat(siteCode);
        String serverStatusInfo = RedisUtil.getValue(serverStatusKey);
        if(StringUtils.hasText(serverStatusInfo)){
            SiteMaintenanceVO siteMaintenanceVO=JSON.parseObject(serverStatusInfo, SiteMaintenanceVO.class);
             if(Objects.equals(SiteStatusEnums.MAINTENANCE.getStatus(), siteMaintenanceVO.getSiteStatus())){
               ServerMaintenanceVO maintenanceVO = ServerMaintenanceVO.builder()
                       .beginTime(siteMaintenanceVO.getMaintenanceTimeStart())
                       .endTime(siteMaintenanceVO.getMaintenanceTimeEnd())
                       .beginTimeStr(DateUtils.formatDateByZoneId(siteMaintenanceVO.getMaintenanceTimeStart(),FULL_FORMAT_6,timezone))
                       .endTimeStr(DateUtils.formatDateByZoneId(siteMaintenanceVO.getMaintenanceTimeEnd(),FULL_FORMAT_6,timezone))
                       .build();
               //客服信息复制
               BeanUtils.copyProperties(siteMaintenanceVO,maintenanceVO);
               ResponseVO<?> result = ResponseVO.fail(ResultCode.SERVER_MAINTENANCE, maintenanceVO);
               result.setMessage(ResultCode.SERVER_MAINTENANCE.getDesc());
               return result;
           }else if(Objects.equals(SiteStatusEnums.DISABLE.getStatus(), siteMaintenanceVO.getSiteStatus())){
                 ResponseVO responseVO =  ResponseVO.fail(ResultCode.SERVER_DISABLE);
                 responseVO.setData("无数据");
                 return responseVO;
            }
        }
        return ResponseVO.success();
    }



    @Override
    public int getOrder() {
        return -1;
    }

    private  void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtil.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }
}
