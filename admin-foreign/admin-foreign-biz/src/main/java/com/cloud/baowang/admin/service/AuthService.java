package com.cloud.baowang.admin.service;
import com.cloud.baowang.common.auth.util.AdminAuthUtil;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLogoutParamVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class AuthService {
    private final TokenService tokenService;

    public void adminUserLogout(@RequestBody AdminLogoutParamVO adminLogoutParamVO){
        String token = RedisUtil.getValue(AdminAuthUtil.getJwtKey(adminLogoutParamVO.getUserId()));
        if (!ObjectUtils.isEmpty(token)) {
            tokenService.delLoginUser(token);
        }

    }
}
