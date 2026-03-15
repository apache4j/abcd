package com.cloud.baowang.site.service;
import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLogoutParamVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class SiteAuthService {
    private final SiteTokenService siteTokenService;

    public void adminUserLogout(@RequestBody AdminLogoutParamVO adminLogoutParamVO){
        String token = RedisUtil.getValue(UserAuthUtil.getJwtKey(adminLogoutParamVO.getSiteCode(),adminLogoutParamVO.getUserId()));
        if (!ObjectUtils.isEmpty(token)) {
            siteTokenService.delLoginUser(adminLogoutParamVO.getSiteCode(),token);
        }

    }
}
