package com.cloud.baowang.user.intreceptors;


import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserTokenVO;
import com.cloud.baowang.user.service.UserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * @author: fangfei
 * @createTime: 2024/10/14 15:25
 * @description: 用户拦截器 检测用户是否有操作
 */
public class LoginInfoInterceptor implements AsyncHandlerInterceptor {

    private static final UserInfoApi userInfoApi;
    private static final UserTokenService userTokenService;
    static {
        userInfoApi = SpringUtils.getBean(UserInfoApi.class);
        userTokenService = SpringUtils.getBean(UserTokenService.class);
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        try {
            String userId = request.getHeader("oneId");
            String siteCode = request.getHeader("siteCode");

            String token = userTokenService.getTokenByUserId(siteCode,userId);
            UserTokenVO userTokenVO = userTokenService.getLoginUser(token);
            //有操作则刷新用户token
            if (userTokenVO != null) {
                userTokenService.refreshToken(userTokenVO);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.SIGN);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        }  else if (!ObjectUtils.isEmpty(token) && token.startsWith(ServletUtil.urlEncode(TokenConstants.PREFIX))) {
            token = ServletUtil.urlDecode(token);
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        } else if (ObjectUtils.isEmpty(token)) {

        }
        return token;
    }

}
