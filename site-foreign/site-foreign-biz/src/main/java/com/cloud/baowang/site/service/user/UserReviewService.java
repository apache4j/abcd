package com.cloud.baowang.site.service.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.UserReviewApi;
import com.cloud.baowang.user.api.vo.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserReviewService {

    private final SystemParamApi systemParamApi;
    private final UserReviewApi userReviewApi;

    public ResponseVO<Boolean> lock(StatusVO vo, String adminId, String adminName) {
        return userReviewApi.lock(vo,adminId, adminName);
    }



    public ResponseVO<Page<UserReviewResponseVO>> getReviewPage(UserReviewPageVO vo, String useName) {
        return userReviewApi.getReviewPage(vo, useName);
    }
    public Long getTotalCount(UserReviewPageVO vo,String useName) {
        return userReviewApi.getTotalCount(vo, useName);
    }

    public ResponseVO<UserReviewDetailsVO> getReviewDetails(IdVO vo, Boolean dataDesensitization) {
        return userReviewApi.getReviewDetails(vo,dataDesensitization);

    }

    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(String siteCode) {
        return userReviewApi.getNotReviewNum(siteCode);
    }

    public ResponseVO<Boolean> reviewSuccess(ReviewVO vo, String registerIp, String registerHost, String adminId, String useName) {
        return userReviewApi.reviewSuccess(vo,registerIp,registerHost,adminId,useName);
    }

    public ResponseVO<Boolean> reviewFail(ReviewVO vo, String adminId, String useName) {
        return userReviewApi.reviewFail(vo,adminId,useName);
    }
}
