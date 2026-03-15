package com.cloud.baowang.admin.service;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.UserLoginLogApi;
import com.cloud.baowang.user.api.vo.user.UserLoginLogVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class UserLoginLogService {

    private final UserLoginLogApi userLoginLogApi;

    public ResponseVO<UserLoginLogVO> queryUserLogin(final UserLoginRequestVO requestVO) {
        return userLoginLogApi.queryUserLogin(requestVO);
    }

}
