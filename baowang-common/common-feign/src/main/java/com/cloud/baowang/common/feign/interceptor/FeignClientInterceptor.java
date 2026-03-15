package com.cloud.baowang.common.feign.interceptor;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.cloud.baowang.common.core.constants.CommonConstant.*;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 语言参数传递
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null != attributes){
            HttpServletRequest request = attributes.getRequest();
            String language = request.getHeader(LANGUAGE_HEAD);
            requestTemplate.header(LANGUAGE_HEAD, language);
            String referer = request.getHeader(X_CUSTOM);
            if (Strings.hasText(referer)) {
                requestTemplate.header(X_CUSTOM, referer);
            }

            String userAgent = request.getHeader(USER_AGENT);
            String userNo = request.getHeader(RequestBasicInfo.Fields.oneId);
            String userAccount = request.getHeader(RequestBasicInfo.Fields.userAccount);
            String requestIp = request.getHeader(RequestBasicInfo.Fields.requestIp);
            String siteCode = request.getHeader(RequestBasicInfo.Fields.siteCode);
            String reqClientSource = request.getHeader(RequestBasicInfo.Fields.reqClientSource);
            String roleIds = request.getHeader(RequestBasicInfo.Fields.roleIds);
            String timezone = request.getHeader(RequestBasicInfo.Fields.timezone);
            String platCurrencyName = request.getHeader(RequestBasicInfo.Fields.platCurrencyName);
            String platCurrencySymbol = request.getHeader(RequestBasicInfo.Fields.platCurrencySymbol);
            String platCurrencyIcon = request.getHeader(RequestBasicInfo.Fields.platCurrencyIcon);
            String deviceTypeVersion = request.getHeader(RequestBasicInfo.Fields.deviceTypeVersion);
            String handicapMode = request.getHeader(RequestBasicInfo.Fields.handicapMode);
            String traceId = request.getHeader(TRACE_ID);

            requestTemplate.header(RequestBasicInfo.Fields.oneId,userNo);
            requestTemplate.header(RequestBasicInfo.Fields.userAccount,userAccount);
            requestTemplate.header(RequestBasicInfo.Fields.requestIp,requestIp);
            requestTemplate.header(RequestBasicInfo.Fields.siteCode,siteCode);
            requestTemplate.header(RequestBasicInfo.Fields.reqClientSource, reqClientSource);
            requestTemplate.header(RequestBasicInfo.Fields.roleIds, roleIds);
            requestTemplate.header(RequestBasicInfo.Fields.timezone, timezone);
            requestTemplate.header(RequestBasicInfo.Fields.platCurrencyName, platCurrencyName);
            requestTemplate.header(RequestBasicInfo.Fields.platCurrencySymbol, platCurrencySymbol);
            requestTemplate.header(RequestBasicInfo.Fields.platCurrencyIcon, platCurrencyIcon);
            requestTemplate.header(RequestBasicInfo.Fields.userAgent, userAgent);
            requestTemplate.header(RequestBasicInfo.Fields.deviceTypeVersion, deviceTypeVersion);
            if(StringUtils.hasText(handicapMode)){
                requestTemplate.header(RequestBasicInfo.Fields.handicapMode, handicapMode);
            }
            requestTemplate.header(TRACE_ID, traceId);
        }
    }
}
