package com.cloud.baowang.system.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteAdminLoginInfoApi;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteLoginInfoVO;
import com.cloud.baowang.system.service.site.SiteAdminLoginInfoService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiqi
 */
@Validated
@AllArgsConstructor
@RestController
public class SiteAdminLoginInfoApiImpl implements SiteAdminLoginInfoApi {

    private final SiteAdminLoginInfoService siteAdminLoginInfoService;


    @Override
    public String addLoginInfo(BusinessLoginInfoAddVO businessLoginInfoAddVO){
       return siteAdminLoginInfoService.addLoginInfo(businessLoginInfoAddVO);
    }

    @Override
    public ResponseVO<Page<SiteLoginInfoVO>> querySiteAdminLoginInfoPage(UserLoginRequestVO businessRoleQueryVO) {
        return ResponseVO.success(siteAdminLoginInfoService.querySiteAdminLoginInfoPage(businessRoleQueryVO));
    }

}
