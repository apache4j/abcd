package com.cloud.baowang.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInformationChangeApi;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserChangeTypesVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserInformationChangeService {

    private final UserInformationChangeApi userInformationChangeApi;

    public ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(UserInformationChangeReqVO userInformationChangeReqVO) {
        return userInformationChangeApi.getUserInformationChange(userInformationChangeReqVO);
    }
}
