package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.api.UserAccountUpdateReviewApi;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.service.UserAccountUpdateReviewService;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewDetailsResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserAccountUpdateReviewApiImpl implements UserAccountUpdateReviewApi {

    private final UserAccountUpdateReviewService userInformationChangeService;

    @Override
    public Page<UserAccountUpdateReviewResVO> getUserAccountUpdateReview(UserAccountUpdateReviewReqVO userAccountUpdateReviewReqVO) {
        return userInformationChangeService.getUserAccountUpdateReview(userAccountUpdateReviewReqVO);
    }

    @Override
    public ResponseVO getLock(IdVO vo, String adminId, String adminName) {
        return userInformationChangeService.getLock(vo, adminId, adminName);
    }

    @Override
    public ResponseVO fristReviewFail(ReviewVO vo, String adminId, String adminName) {
        return userInformationChangeService.firstReviewFail(vo, adminId, adminName);
    }

    @Override
    public ResponseVO fristReviewSuccess(ReviewVO vo, String registerIp, String adminId, String adminName) {
        return userInformationChangeService.fristReviewSuccess(vo, registerIp, adminId, adminName);
    }

    @Override
    public ResponseVO<UserAccountUpdateReviewDetailsResVO> getUpdateReviewDetails(IdVO vo) {
        return userInformationChangeService.getUpdateReviewDetails(vo);
    }
}
