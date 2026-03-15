package com.cloud.baowang.admin.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessLoginInfoApi;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiqi
 */
@Tag(name = "系统-系统登录记录")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/business_login/api")
public class BusinessLoginController {

    private final BusinessLoginInfoApi businessLoginInfoApi;


    @Operation(summary = "系统登录日志-分页")
    @PostMapping("/queryBusinessLoginInfoPage")
    public ResponseVO<Page<BusinessLoginInfoVO>> queryBusinessLoginInfoPage(@RequestBody UserLoginRequestVO businessRoleQueryVO) {
        businessRoleQueryVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        return businessLoginInfoApi.queryBusinessLoginInfoPage(businessRoleQueryVO);
    }

}
