package com.cloud.baowang.user.api;

import com.cloud.baowang.user.api.api.UserAddApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.service.UserReviewService;
import com.cloud.baowang.user.api.vo.userreview.UserAddVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserAddApiImpl implements UserAddApi {

    private UserReviewService userReviewService;

    @Override
    public ResponseVO<?> addUser(UserAddVO vo, String adminId, String adminName) {
        return userReviewService.addUser(vo, adminId, adminName);
    }
}
