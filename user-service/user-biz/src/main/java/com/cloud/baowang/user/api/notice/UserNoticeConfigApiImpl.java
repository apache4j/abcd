package com.cloud.baowang.user.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.notice.UserNoticeConfigApi;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.*;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.SiteHomeNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.UserNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetGetVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetVO;
import com.cloud.baowang.user.service.UserNoticeConfigService;
import com.cloud.baowang.user.service.UserNoticeTargetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserNoticeConfigApiImpl implements UserNoticeConfigApi {

    private final UserNoticeConfigService userNoticeConfigService;

    private final UserNoticeTargetService userNoticeTargetService;


    @Override
    public ResponseVO<Page<UserNoticeConfigVO>> getUserNoticeConfigPage(UserNoticeConfigGetVO userNoticeConfigGetVO) {
        return userNoticeConfigService.getUserNoticeConfigPage(userNoticeConfigGetVO);
    }

    @Override
    public ResponseVO<List<SiteHomeNoticeConfigVO>> siteNoticeLists(String siteCode) {
        return ResponseVO.success(userNoticeConfigService.siteNoticeLists(siteCode));
    }

    @Override
    public ResponseVO<?> addUserNoticeConfig(UserNoticeConfigAddModifyVO userNoticeConfigAddVO) {
        return userNoticeConfigService.addUserNoticeConfig(userNoticeConfigAddVO);
    }

    @Override
    public ResponseVO<?> addUserSysNoticeConfig(UserSysNoticeConfigAddVO sysNoticeConfigAddVO) {
        return userNoticeConfigService.addUserSysNoticeConfig(sysNoticeConfigAddVO);
    }

    @Override
    public ResponseVO<?> updateUserNoticeConfig(UserNoticeConfigGetVO userNoticeConfigGetVO) {
        return userNoticeConfigService.updateUserNoticeConfig(userNoticeConfigGetVO);
    }

    @Override
    public ResponseVO<Boolean> edit(UserNoticeConfigEditVO userNoticeConfigEditVO) {
        return ResponseVO.success(userNoticeConfigService.edit(userNoticeConfigEditVO));
    }

    @Override
    public ResponseVO<Boolean> del(Long id) {
        return ResponseVO.success(userNoticeConfigService.del(id));

    }

    @Override
    public ResponseVO<Page<UserNoticeTargetVO>> pageAccount(UserNoticeTargetGetVO userNoticeTargetGetVO) {
        return userNoticeTargetService.getList(userNoticeTargetGetVO);
    }

    @Override
    public ResponseVO<List<NoticeSortSelectResponseVO>> sortNoticeSelect(SortNoticeSelectVO vo) {
        try {
            return userNoticeConfigService.sortNoticeSelect(vo);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseVO sortNotice(NoticeConfigResortVO noticeConfigResortVO) {
        try {
            return userNoticeConfigService.sortNotice(noticeConfigResortVO);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    /*@Override
    public ResponseVO<List<SiteHomeNoticeConfigVO>> getIPTop10(String siteCode) {
        return null;
    }
*/

}

