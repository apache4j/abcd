package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.invite.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 15:45
 * @description:
 */
@FeignClient(contextId = "remoteSiteUserInviteRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 邀请好友记录 服务")
public interface SiteUserInviteRecordApi {
    String PREFIX = ApiConstants.PREFIX + "/userInviteRecord/api/";

    @Operation(summary = "分页查询邀请好友记录")
    @PostMapping(value = PREFIX + "getInviteRecordPage")
    Page<SiteUserInviteRecordResVO> getInviteRecordPage(@RequestBody SiteUserInviteRecordReqVO reqVO);

    @Operation(summary = "获取条数")
    @PostMapping(value = PREFIX + "getInviteRecordCount")
    Long getInviteRecordCount(@RequestBody SiteUserInviteRecordReqVO reqVO);

    @Operation(summary = "邀请好友")
    @PostMapping(value = PREFIX + "inviteFriend")
    UserInviteResVO inviteFriend(@RequestParam("userId") String userId,
                                 @RequestParam("siteCode") String siteCode);


    @Operation(summary = "任务查询邀请好友记录")
    @PostMapping(value = PREFIX + "getInviteRecord")
    List<SiteUserInviteRecordTaskResVO> getInviteRecord(@RequestBody SiteUserInviteRecordTaskReqVO reqVO);

    @Operation(summary = "有效邀请<老数据兼容>")
    @PostMapping(value = PREFIX + "validInviteRecoup")
    ResponseVO<Void> validInviteRecoup(@RequestParam("siteCode") String siteCode ,@RequestParam("isInit") boolean isInit);
}
