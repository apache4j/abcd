package com.cloud.baowang.admin.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kimi
 */
@Tag(name = "会员注册信息")
@RestController
@RequestMapping(value = "/user-registration-info/api")
@AllArgsConstructor
public class UserRegistrationInfoController {

    private final UserRegistrationInfoApi userRegistrationInfoApi;



    @Operation(summary = "会员注册信息")
    @PostMapping("/getRegistrationInfo")
    public ResponseVO<Page<UserRegistrationInfoResVO>> getRegistrationInfo(@RequestBody UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        userRegistrationInfoReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userRegistrationInfoApi.getRegistrationInfo(userRegistrationInfoReqVO);
    }
}
