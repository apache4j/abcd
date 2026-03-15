package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserInviteConfigApi;
import com.cloud.baowang.user.api.api.SiteUserInviteRecordApi;
import com.cloud.baowang.user.api.vo.user.invite.*;
import com.cloud.baowang.user.service.SiteUserInviteConfigService;
import com.cloud.baowang.user.service.SiteUserInviteRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 15:47
 * @description:
 */
@Slf4j
@AllArgsConstructor
@RestController
public class SiteUserInviteRecordApiImpl implements SiteUserInviteRecordApi {

    private final SiteUserInviteRecordService siteUserInviteRecordService;

    @Override
    public Page<SiteUserInviteRecordResVO> getInviteRecordPage(SiteUserInviteRecordReqVO reqVO) {
        return siteUserInviteRecordService.getInviteRecordPage(reqVO);
    }

    @Override
    public Long getInviteRecordCount(SiteUserInviteRecordReqVO reqVO) {
        return siteUserInviteRecordService.getInviteRecordCount(reqVO);
    }

    @Override
    public UserInviteResVO inviteFriend(String userId, String siteCode) {
        return siteUserInviteRecordService.inviteFriend(userId, siteCode);
    }

    @Override
    public List<SiteUserInviteRecordTaskResVO> getInviteRecord(SiteUserInviteRecordTaskReqVO reqVO) {
        List<SiteUserInviteRecordTaskResVO> result = siteUserInviteRecordService.getInviteRecord(reqVO);
        return result;
    }

    @Override
    public ResponseVO<Void> validInviteRecoup(String siteCode,boolean isInit) {
        return siteUserInviteRecordService.validInviteRecoup(siteCode,isInit);
    }
}
