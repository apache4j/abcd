package com.cloud.baowang.user.api.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadNumRspVO;
import com.cloud.baowang.user.api.vo.notice.user.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserNoticeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 用户通知的Controller 服务")
public interface UserNoticeApi {

    String PREFIX = ApiConstants.PREFIX + "/UserNoticeApi/api";


    @Operation(summary = "获取跑马灯通知列表")
    @PostMapping(PREFIX + "/getUserNoticeHeadList")
    ResponseVO<List<UserNoticeRespVO>> getUserNoticeHeadList(@RequestBody UserNoticeHeadReqVO userNoticeHeadReqVO);

    @Operation(summary = "获取强制弹窗通知列表")
    @PostMapping(PREFIX + "/getForceUserNoticeHeadList")
    ResponseVO<List<UserNoticeRespVO>> getForceUserNoticeHeadList(@RequestBody UserNoticeHeadReqVO userNoticeHeadReqVO);

    @Operation(summary = "获取用户通知列表")
    @PostMapping(PREFIX + "/getUserNoticeList")
    ResponseVO<Page<UserNoticeRespVO>> getUserNoticeList(@RequestBody UserNoticeReqVO userNoticeReqVO);


    @Operation(summary = "标记已读通知")
    @PostMapping(PREFIX + "/setReadState")
    ResponseVO setReadState(@RequestBody UserNoticeSetReadStateReqVO userNoticeSetReadStateReqVO);

    @Operation(summary = "标记已读通知,一键已读")
    @PostMapping(PREFIX + "/setReadStateAll")
    ResponseVO setReadStateAll(@RequestBody UserNoticeReqVO userNoticeReqVO);

    @Operation(summary = "一键删除")
    @PostMapping(PREFIX + "/setDelStateAll")
    ResponseVO<Boolean> setDelStateAll(@RequestBody UserNoticeReqVO userNoticeReqVO);


    @Operation(summary = "添加消息通知")
    @PostMapping(PREFIX + "/add")
    ResponseVO add(@RequestBody UserNoticeTargetAddVO userNoticeTargetAddVO);

    @Operation(summary ="批量删除 ")
    @PostMapping(PREFIX+"/deleteBatch")
    ResponseVO<Boolean> deleteBatch(@Valid @RequestBody NoticeUpdateVO reqVO);

    @Operation(summary ="获取未读消息数量 skin4")
    @GetMapping(PREFIX+"/getUserNoticeUnreadNum")
    ResponseVO<UserNoticeUnreadNumRspVO> getUserNoticeUnreadNums();
}
