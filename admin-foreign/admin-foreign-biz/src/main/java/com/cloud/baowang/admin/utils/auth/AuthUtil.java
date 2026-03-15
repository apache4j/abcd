package com.cloud.baowang.admin.utils.auth;


import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;

/**
 * Token 权限验证工具类
 *
 * @author qiqi
 */
public class AuthUtil {
    /**
     * 底层的 AuthLogic 对象
     */
    public static AuthLogic authLogic = new AuthLogic();

    /**
     * 会话注销
     */
    public static void logout() {
        authLogic.logout();
    }

    /**
     * 会话注销，根据指定Token
     *
     * @param token 指定token
     */
    public static void logoutByToken(String token) {
        authLogic.logoutByToken(token);
    }

    /**
     * 检验当前会话是否已经登录，如未登录，则抛出异常
     */
    public static void checkLogin() {
        authLogic.checkLogin();
    }

    /**
     * 获取当前登录用户信息
     *
     * @param token 指定token
     * @return 用户信息
     */
    public static LoginAdmin getLoginAdmin(String token) {
        return authLogic.getLoginAdmin(token);
    }

    /**
     * 验证当前用户有效期
     *
     * @param loginAdmin 用户信息
     */
    public static void verifyLoginUserExpire(LoginAdmin loginAdmin) {
        authLogic.verifyLoginUserExpire(loginAdmin);
    }

    /**
     * 校验api权限
     *
     * @param apiUrl
     * @return
     */
    public static Boolean checkApiUrl(String apiUrl) {
        return authLogic.checkPermissions(apiUrl);
    }


}
