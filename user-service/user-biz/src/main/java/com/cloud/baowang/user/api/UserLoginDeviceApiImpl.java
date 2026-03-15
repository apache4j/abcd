package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserLoginDeviceApi;
import com.cloud.baowang.user.api.vo.user.UserLoginDeviceVO;
import com.cloud.baowang.user.api.vo.user.request.UserDeviceReqVO;
import com.cloud.baowang.user.service.UserLoginDeviceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserLoginDeviceApiImpl implements UserLoginDeviceApi {
    private final UserLoginDeviceService userLoginDeviceService;

    @Override
    public ResponseVO<Page<UserLoginDeviceVO>> queryUserLoginDevice(UserDeviceReqVO requestVO) {
        return ResponseVO.success(userLoginDeviceService.getPage(requestVO));
    }

    @Override
    public ResponseVO<Void> deleteUserLoginDevice(IdVO idVO) {
        userLoginDeviceService.deleteUserLoginDevice(idVO);
        return ResponseVO.success();
    }
}
