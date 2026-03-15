package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.api.UserInformationChangeApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.service.UserInformationChangeService;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserChangeTypesVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserInformationChangeApiImpl implements UserInformationChangeApi {

    private final UserInformationChangeService userInformationChangeService;

    @Override
    public ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(UserInformationChangeReqVO userInformationChangeReqVO) {
        return userInformationChangeService.getUserInformationChange(userInformationChangeReqVO);
    }

    @Override
    public Long getUserInformationChangeCount(UserInformationChangeReqVO userInformationChangeReqVO) {
        return userInformationChangeService.getUserInformationChangeCount(userInformationChangeReqVO);
    }

}
