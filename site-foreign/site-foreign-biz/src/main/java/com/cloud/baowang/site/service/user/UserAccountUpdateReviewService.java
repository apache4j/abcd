package com.cloud.baowang.site.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserAccountUpdateReviewApi;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewDetailsResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserAccountUpdateReviewService {

    private final UserAccountUpdateReviewApi userAccountUpdateReviewApi;

    public Page<UserAccountUpdateReviewResVO> getUserAccountUpdateReview(UserAccountUpdateReviewReqVO userAccountUpdateReviewReqVO) {
        return userAccountUpdateReviewApi.getUserAccountUpdateReview(userAccountUpdateReviewReqVO);
    }

    public ResponseVO getLock(IdVO vo, String adminId, String useName) {
        return userAccountUpdateReviewApi.getLock(vo, adminId, useName);
    }

    public ResponseVO fristReviewFail(ReviewVO vo, String adminId, String useName) {
        
        return userAccountUpdateReviewApi.fristReviewFail(vo, adminId, useName);
    }

    public ResponseVO fristReviewSuccess(ReviewVO vo, String registerIp, String adminId, String useName) {
        return userAccountUpdateReviewApi.fristReviewSuccess(vo, registerIp, adminId, useName);
    }

    public ResponseVO<UserAccountUpdateReviewDetailsResVO> getUpdateReviewDetails(IdVO vo) {
        return userAccountUpdateReviewApi.getUpdateReviewDetails(vo);
    }
}
