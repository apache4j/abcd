package com.cloud.baowang.user.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserInviteConfigApi;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigResponseVO;
import com.cloud.baowang.user.service.SiteUserInviteConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 15:47
 * @description:
 */
@Slf4j
@AllArgsConstructor
@RestController
public class SiteUserInviteConfigApiImpl implements SiteUserInviteConfigApi {
    private final SiteUserInviteConfigService siteUserInviteConfigService;

    @Override
    public SiteUserInviteConfigResponseVO getInviteConfig(String siteCode) {
        return siteUserInviteConfigService.getInviteConfig(siteCode);
    }

    @Override
    public ResponseVO userInviteConfig(SiteUserInviteConfigReqVO reqVO) {
        return siteUserInviteConfigService.userInviteConfig(reqVO);
    }
}
