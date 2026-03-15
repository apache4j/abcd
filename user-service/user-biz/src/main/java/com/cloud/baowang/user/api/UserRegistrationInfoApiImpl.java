package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import com.cloud.baowang.user.service.UserRegistrationInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserRegistrationInfoApiImpl implements UserRegistrationInfoApi {

    private UserRegistrationInfoService userRegistrationInfoService;

    @Override
    public ResponseVO<Page<UserRegistrationInfoResVO>> getRegistrationInfo(UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        return userRegistrationInfoService.getRegistrationInfo(userRegistrationInfoReqVO);
    }

    @Override
    public Page<UserRegistrationInfoResVO> listPage(UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        return userRegistrationInfoService.listPage(userRegistrationInfoReqVO);
    }

    @Override
    public Long getTotalCount(UserRegistrationInfoReqVO vo) {
        return userRegistrationInfoService.getTotalCount(vo);
    }


    @Override
    public GetRegisterInfoByAccountVO getRegisterInfoByAccount(String userAccount) {
        return userRegistrationInfoService.getRegisterInfoByAccount(userAccount);
    }

    @Override
    public GetRegisterInfoByAccountVO getRegisterInfoByAccountAndSiteCode(String userAccount, String siteCode) {
        return userRegistrationInfoService.getRegisterInfoByAccountAndSiteCode(userAccount,siteCode);
    }
}
