package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserReviewApi;
import com.cloud.baowang.user.api.enums.UserConorRouterConstants;
import com.cloud.baowang.user.api.vo.*;
import com.cloud.baowang.user.po.UserReviewPO;
import com.cloud.baowang.user.service.UserAccountUpdateReviewService;
import com.cloud.baowang.user.service.UserReviewService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserReviewApiImpl implements UserReviewApi {

    private final UserReviewService userReviewService;
    private final UserAccountUpdateReviewService userAccountUpdateReviewService;

    @Override
    public ResponseVO<Boolean> lock(StatusVO vo, String adminId, String adminName) {
        return userReviewService.lock(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<Page<UserReviewResponseVO>> getReviewPage(UserReviewPageVO vo, String adminName) {
        return userReviewService.getReviewPage(vo,adminName);
    }
    @Override
    public Long getTotalCount(UserReviewPageVO vo, String adminName) {
        return userReviewService.getTotalCount(vo,adminName);
    }

    @Override
    public ResponseVO<UserReviewDetailsVO> getReviewDetails(IdVO vo, Boolean dataDesensitization) {
        return userReviewService.getReviewDetails(vo,dataDesensitization);
    }

    @Override
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(String siteCode) {
        List<UserAccountUpdateVO> list = Lists.newArrayList();
        list.add(userAccountUpdateReviewService.getNumber(siteCode));
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        long count = userReviewService.count(Wrappers.<UserReviewPO>lambdaQuery().eq(UserReviewPO::getReviewOperation, CommonConstant.business_one)
                .eq(UserReviewPO::getSiteCode,siteCode));
        vo.setNum((int)count);
        vo.setRouter(UserConorRouterConstants.ADD_MEMBER_REVIEW);

        list.add(vo);
        return ResponseVO.success(list);
    }

    @Override
    public ResponseVO<Boolean> reviewSuccess(ReviewVO vo, String registerIp, String registerHost, String adminId, String adminName) {
        return userReviewService.reviewSuccess(vo, registerIp, registerHost, adminId, adminName);
    }

    @Override
    public ResponseVO<Boolean> reviewFail(ReviewVO vo, String adminId, String adminName) {
        return userReviewService.reviewFail(vo,adminId,adminName);
    }


}
