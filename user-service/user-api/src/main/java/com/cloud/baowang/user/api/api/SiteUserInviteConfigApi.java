package com.cloud.baowang.user.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 15:45
 * @description:
 */
@FeignClient(contextId = "remoteSiteUserInviteConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 邀请好友配置 服务")
public interface SiteUserInviteConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/userInviteConfig/api/";

    @Operation(summary = "获取邀请好友配置")
    @PostMapping(value = PREFIX + "getInviteConfig")
    SiteUserInviteConfigResponseVO getInviteConfig(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "邀请好友配置")
    @PostMapping(value = PREFIX + "userInviteConfig")
    ResponseVO userInviteConfig(@RequestBody SiteUserInviteConfigReqVO reqVO);
}
