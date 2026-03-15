package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.po.SiteUserInviteIconPO;
import com.cloud.baowang.user.repositories.SiteUserInviteIconRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author: fangfei
 * @createTime: 2024/11/23 23:46
 * @description:
 */
@Service
@AllArgsConstructor
@Slf4j
public class SiteUserInviteIconService extends ServiceImpl<SiteUserInviteIconRepository, SiteUserInviteIconPO> {

    private final SiteUserInviteIconRepository siteUserInviteIconRepository;

    public String getIconUrl(String configId, String language, String deviceType) {
        LambdaQueryWrapper<SiteUserInviteIconPO> iconQuery = new LambdaQueryWrapper<>();
        iconQuery.eq(SiteUserInviteIconPO::getConfigId, configId);
        iconQuery.eq(SiteUserInviteIconPO::getLanguage, language);
        iconQuery.eq(SiteUserInviteIconPO::getDeviceType, deviceType);
        SiteUserInviteIconPO po = siteUserInviteIconRepository.selectOne(iconQuery);
        if (po == null) {
            return "";
        }
        return po.getIconUrl();
     }
}
