package com.cloud.baowang.admin.interceptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.cloud.baowang.admin.utils.auth.AuthUtil;
import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.context.SecurityContextHolder;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author qiqi
 */
public class MyHeaderInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String reqUrl=request.getRequestURI();
        if(reqUrl.equals("/siteMessage/api/getSiteSystemInfo")){
            return true;
        }
        String userName = ServletUtil.getHeader(request, SecurityConstants.DETAILS_USERNAME);
        SecurityContextHolder.setUserId(ServletUtil.getHeader(request, SecurityConstants.DETAILS_ADMIN_ID));
        SecurityContextHolder.setUserName(userName);
        SecurityContextHolder.setUserKey(ServletUtil.getHeader(request, SecurityConstants.ADMIN_KEY));
        Boolean isSuperAdmin = Boolean.parseBoolean(ServletUtil.getHeader(request, SecurityConstants.SUPER_ADMIN));
        SecurityContextHolder.setSuperAdmin(isSuperAdmin);
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            LoginAdmin loginAdmin = AuthUtil.getLoginAdmin(token);
            if(null == loginAdmin){
                throw  new BaowangDefaultException(ResultCode.ACCOUNT_DISABLED);
            }else {
                if (CommonConstant.business_zero.equals(loginAdmin.getStatus())) {
                    throw new BaowangDefaultException(ResultCode.ACCOUNT_DISABLED);
                }
                loginAdmin.setAccessToken(token);
                AuthUtil.verifyLoginUserExpire(loginAdmin);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginAdmin);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
