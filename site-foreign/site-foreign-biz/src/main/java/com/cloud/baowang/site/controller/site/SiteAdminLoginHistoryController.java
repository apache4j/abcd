package com.cloud.baowang.site.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
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
@RequestMapping(value = "/site_login/api")
public class SiteAdminLoginHistoryController {

   // private final SiteAdminLoginInfoApi siteAdminLoginInfoApi;
    private final BusinessLoginInfoApi businessLoginInfoApi;

    @Operation(summary = "系统登录日志-分页")
    @PostMapping("/querySiteAdminLoginInfoPage")
    public ResponseVO<Page<BusinessLoginInfoVO>> querySiteAdminLoginInfoPage(@RequestBody UserLoginRequestVO businessRoleQueryVO) {
        businessRoleQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return businessLoginInfoApi.queryBusinessLoginInfoPage(businessRoleQueryVO);
    }

}
