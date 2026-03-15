package com.cloud.baowang.user.api.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.notice.agent.*;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeHeadRspVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.*;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.SiteHomeNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.UserNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetGetVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "remoteUserNoticeConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 通知配置 服务")
public interface UserNoticeConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/userNoticeConfigApi/api";


    @Operation(summary = "通知配置列表查询")
    @PostMapping(PREFIX + "/getUserNoticeConfigPage")
    ResponseVO<Page<UserNoticeConfigVO>> getUserNoticeConfigPage(@RequestBody UserNoticeConfigGetVO userNoticeConfigGetVO);

    @Operation(summary = "站点首页公告显示")
    @PostMapping(PREFIX + "/siteNoticeLists")
    ResponseVO<List<SiteHomeNoticeConfigVO>> siteNoticeLists(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "通知配置新增")
    @PostMapping(value = PREFIX + "/addUserNoticeConfig")
    ResponseVO<?> addUserNoticeConfig(@RequestBody UserNoticeConfigAddModifyVO userNoticeConfigAddVO);

    @Operation(summary = "通知系统消息配置新增")
    @PostMapping(PREFIX + "/addUserSysNoticeConfig")
    ResponseVO<?> addUserSysNoticeConfig(@RequestBody @Valid UserSysNoticeConfigAddVO sysNoticeConfigAddVO);

    @Operation(summary = "通知配置撤回通知")
    @PostMapping(PREFIX + "/updateUserNoticeConfig")
    ResponseVO<?> updateUserNoticeConfig(@RequestBody UserNoticeConfigGetVO userNoticeConfigGetVO);

    @Operation(summary = "通知配置编辑")
    @PostMapping(PREFIX + "/edit")
    ResponseVO<Boolean> edit(@Valid UserNoticeConfigEditVO userNoticeConfigEditVO);

    @Operation(summary = "通知配置刪除")
    @PostMapping("/del/{id}")
    ResponseVO<Boolean> del(@PathVariable("id") Long id);

    @Operation(summary = "点击查看更多会员")
    @PostMapping(PREFIX + "/pageAccount")
    ResponseVO<Page<UserNoticeTargetVO>> pageAccount(@RequestBody UserNoticeTargetGetVO userNoticeTargetGetVO);

    @Operation(summary = "查看排序")
    @PostMapping(PREFIX + "/sortNoticeSelect")
    ResponseVO<List<NoticeSortSelectResponseVO>> sortNoticeSelect(@RequestBody SortNoticeSelectVO vo);

    @Operation(summary = "排序")
    @PostMapping(PREFIX + "/sortNotice")
    ResponseVO sortNotice(@RequestBody NoticeConfigResortVO noticeConfigResortVO);


    /*ResponseVO<List<SiteHomeNoticeConfigVO>> getIPTop10(String siteCode);*/
}
