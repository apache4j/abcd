package com.cloud.baowang.user.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadNumRspVO;
import com.cloud.baowang.user.api.vo.notice.user.request.*;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.service.UserNoticeService;
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
public class UserNoticeApiImpl implements UserNoticeApi {
    private final UserNoticeService userNoticeService;
    private final UserNoticeTargetService userNoticeTargetService;

    @Override
    public ResponseVO<List<UserNoticeRespVO>> getUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        return ResponseVO.success(userNoticeService.getUserNoticeHeadList(userNoticeHeadReqVO));
    }

    @Override
    public ResponseVO<List<UserNoticeRespVO>> getForceUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        return ResponseVO.success(userNoticeService.getForceUserNoticeHeadList(userNoticeHeadReqVO));
    }

    @Override
    public ResponseVO<Page<UserNoticeRespVO>> getUserNoticeList(UserNoticeReqVO userNoticeReqVO) {
        return userNoticeService.getUserNoticeList(userNoticeReqVO);

    }

    @Override
    public ResponseVO setReadState(UserNoticeSetReadStateReqVO userNoticeSetReadStateReqVO) {
        try {
            userNoticeService.setReadState(userNoticeSetReadStateReqVO);
            return ResponseVO.success();
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseVO<Boolean> setReadStateAll(UserNoticeReqVO userNoticeReqVO) {
        try {
            userNoticeService.setReadStateAll(userNoticeReqVO);
            return ResponseVO.success(true);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseVO<Boolean> setDelStateAll(UserNoticeReqVO userNoticeReqVO) {
        try {
            userNoticeService.setDelStateAll(userNoticeReqVO);
            return ResponseVO.success(true);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseVO add(UserNoticeTargetAddVO userNoticeTargetAddVO) {

        userNoticeService.userNoticeTargetAdd(userNoticeTargetAddVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Boolean> deleteBatch(NoticeUpdateVO reqVO) {
        userNoticeTargetService.deleteBatch(reqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<UserNoticeUnreadNumRspVO> getUserNoticeUnreadNums() {
        return ResponseVO.success(userNoticeService.getUserNoticeUnreadNum());
    }
}
