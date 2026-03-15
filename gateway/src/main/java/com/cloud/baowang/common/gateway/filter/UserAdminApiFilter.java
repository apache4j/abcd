package com.cloud.baowang.common.gateway.filter;


import cn.hutool.core.collection.CollUtil;
import com.cloud.baowang.common.auth.util.AdminAuthUtil;
import com.cloud.baowang.common.auth.vo.AdminTokenVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import com.cloud.baowang.common.gateway.filter.abstractFilter.UserAdminAbstractFilter;
import com.cloud.baowang.common.gateway.properties.AuthenticationProperties;
import com.cloud.baowang.common.gateway.properties.IgnoreAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.cloud.baowang.common.core.constants.CommonConstant.Deviceid;

/**
 * admin用户拦截器
 */
@Order(1)
@Component
@Slf4j
public class UserAdminApiFilter implements UserAdminAbstractFilter {
    private final IgnoreAuthProperties ignoreAuth;
    private final AuthenticationProperties authenticationProperties;


    public UserAdminApiFilter(IgnoreAuthProperties ignoreAuth,
                              AuthenticationProperties authenticationProperties) {
        this.ignoreAuth = ignoreAuth;
        this.authenticationProperties = authenticationProperties;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        String url = request.getURI().getPath();
        String deviceId = request.getHeaders().getFirst(Deviceid);

        // 总控siteCode 时区默认值填充
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        addHeader(mutate, RequestBasicInfo.Fields.timezone, CommonConstant.ADMIN_CENTER_TIMEZONE);
        addHeader(mutate, RequestBasicInfo.Fields.deviceId, deviceId);
        // 跳过不需要验证的路径
        if (JwtUtils.matches(url, ignoreAuth.getWhites())) {
            return chain.filter(exchange);
        }
        String token = getToken(request);
        if (ObjectUtils.isEmpty(token)) {
            throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
        }
        AdminTokenVO adminTokenVO = AdminAuthUtil.adminAuth(token, url, authenticationProperties.getEnable());
        // 设置用户信息到请求
        addHeader(mutate, TokenConstants.SUPER_ADMIN, Boolean.parseBoolean(JwtUtils.getSuperAdmin(token)));
        addHeader(mutate, RequestBasicInfo.Fields.oneId, adminTokenVO.getAdminId());
        addHeader(mutate, RequestBasicInfo.Fields.userAccount, adminTokenVO.getUserName());
        addHeader(mutate, RequestBasicInfo.Fields.roleIds, CollUtil.isNotEmpty(adminTokenVO.getUserRoleIds()) ? String.join(CommonConstant.COMMA, adminTokenVO.getUserRoleIds()) : Strings.EMPTY);
        addHeader(mutate, RequestBasicInfo.Fields.siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        addHeader(mutate, RequestBasicInfo.Fields.dataDesensitization, adminTokenVO.getDataDesensitization());
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }
}
