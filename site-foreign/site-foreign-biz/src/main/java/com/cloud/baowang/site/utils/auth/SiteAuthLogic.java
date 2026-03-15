package com.cloud.baowang.site.utils.auth;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.site.service.SiteTokenService;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Token 权限验证，逻辑实现类
 *
 * @author qiqi
 */
public class SiteAuthLogic {

    public SiteTokenService siteTokenService = SpringUtils.getBean(SiteTokenService.class);

    /**
     * 会话注销
     */
    public void logout(String siteCode) {
        String token = SiteSecurityUtils.getToken();
        if (token == null) {
            return;
        }
        logoutByToken(siteCode,token);
    }

    /**
     * 会话注销，根据指定Token
     */
    public void logoutByToken(String siteCode,String token) {
        siteTokenService.delLoginUser(siteCode,token);
    }

    /**
     * 检验用户是否已经登录，如未登录，则抛出异常
     */
    public void checkLogin() {
        getLoginAdmin();
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     *
     * @return 用户缓存信息
     */
    public LoginAdmin getLoginAdmin() {
        String token = SiteSecurityUtils.getToken();
        if (token == null) {
            throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
        }
        LoginAdmin loginAdmin = SiteSecurityUtils.getLoginAdmin();
        if (loginAdmin == null) {
            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
        }
        return loginAdmin;
    }

    /**
     * 验证API权限
     *
     * @param apiUrl
     * @return
     */
    public boolean checkApiUrl(String apiUrl) {
        LoginAdmin loginAdmin = getLoginAdmin();
        List<String> apiPermissions = loginAdmin.getApiPermissions();
        return apiPermissions.stream().filter(StringUtils::hasText)
                .anyMatch(x -> apiPermissions.contains(x));

    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     *
     * @param token 前端传递的认证信息
     * @return 用户缓存信息
     */
    public LoginAdmin getLoginAdmin(String siteCode,String token) {
        return siteTokenService.getLoginUser(siteCode,token);
    }

    /**
     * 验证当前用户有效期, 如果相差不足120分钟，自动刷新缓存
     *
     * @param loginAdmin 当前用户信息
     */
    public void verifyLoginUserExpire(LoginAdmin loginAdmin) {
        siteTokenService.verifyToken(loginAdmin);
    }

    /**
     * 校验API权限
     *
     * @param apiUrl
     * @return
     */
    public Boolean checkPermissions(String apiUrl) {
        LoginAdmin loginAdmin = getLoginAdmin();
        List<String> apiUrls = loginAdmin.getApiPermissions();
        if (null == apiUrls || apiUrls.isEmpty()) {
            return false;
        }
        return apiUrls.contains(apiUrl);
    }
}
