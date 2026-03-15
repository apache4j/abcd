package com.cloud.baowang.system.api.api.site;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteLoginInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteSiteAdminLoginInfoApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SiteAdminLoginInfoApi")
public interface SiteAdminLoginInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/siteAdminLoginInfoApi/api/";

    @PostMapping(PREFIX + "addLoginInfo")
    @Schema(description = "登录日志添加")
    String addLoginInfo(@RequestBody BusinessLoginInfoAddVO businessLoginInfoAddVO);

    @PostMapping(PREFIX + "querySiteAdminLoginInfoPage")
    @Schema(description = "登录日志列表")
    ResponseVO<Page<SiteLoginInfoVO>> querySiteAdminLoginInfoPage(@RequestBody UserLoginRequestVO businessRoleQueryVO);
}
