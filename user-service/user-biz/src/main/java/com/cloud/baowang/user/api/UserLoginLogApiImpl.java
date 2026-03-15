package com.cloud.baowang.user.api;

import com.cloud.baowang.user.api.api.UserLoginLogApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.user.service.UserLoginLogService;
import com.cloud.baowang.user.api.vo.user.UserLoginLogVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserLoginLogApiImpl implements UserLoginLogApi {

    private final UserLoginLogService userLoginLogService;

    @Override
    public ResponseVO<UserLoginLogVO> queryUserLogin(UserLoginRequestVO requestVO) {
        return userLoginLogService.queryUserLogin(requestVO);
    }

    @Override
    public Long getTotalCount(UserLoginRequestVO vo) {
        return userLoginLogService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<Void> insertUserLogin(UserLoginInfoVO userLoginInfoVO) {
        userLoginLogService.insertUserLogin(userLoginInfoVO);
        return ResponseVO.success();
    }
}
