package com.cloud.baowang.user.api.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.notice.agent.*;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeHeadRspVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "remoteAgentNoticeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理-消息通知 服务")
public interface AgentNoticeApi {

    String PREFIX = ApiConstants.PREFIX + "/agentNoticeApi/api/";

//
//    @PostMapping(PREFIX + "getAngentNoticeTabList")
//    @Operation(summary = "获取代理的通知Tab字典")
//    ResponseVO<HashMap<String, Object>> getAngentNoticeTabList();
//

    @Operation(summary = "获取代理/商务消息的列表")
    @PostMapping(PREFIX + "getAgentNoticeList")
    ResponseVO<AgentNoticeRespBO> getAgentNoticeList(@RequestBody AgentNoticeVO agentNoticeVO ,@RequestParam(defaultValue = "true") boolean isAgent);


    @Operation(summary = "获取代理消息的未读第一条")
    @PostMapping(PREFIX + "getAgentNoticeNotReadFirst")
    ResponseVO<UserNoticeRespVO> getAgentNoticeNotReadFirst(@RequestBody AgentNoticeVO agentNoticeVO);


    /*@Operation(summary = "插入（批量）代理消息")
    @PostMapping(PREFIX+"saveAgentNotices")
    Boolean saveAgentNotices(@RequestBody SaveAgentNoticeReq saveAgentNoticeReq);*/


    @Operation(summary = "添加代理通知--系统添加代理消息")
    @PostMapping(PREFIX + "addAgentNotice")
    ResponseVO addAgentNotice(@RequestBody AgentNoticeReq agentNoticeReq);

//
//    @Operation(summary = "获取代理的未读通知总数量")
//    @PostMapping(PREFIX + "getAgentNoticeUnreadTotal")
//    ResponseVO<Long> getAgentNoticeUnreadTotal();

    @Operation(summary = "获取代理首页公告强制弹窗列表")
    @PostMapping(PREFIX + "getForceAgentNoticeHeadList")
    ResponseVO<List<UserNoticeRespVO>> getForceAgentNoticeHeadList(@RequestBody UserNoticeHeadReqVO userNoticeHeadReqVO);


    @Operation(summary = "标记已读通知")
    @PostMapping(PREFIX + "setReadState")
    ResponseVO setReadState(@RequestBody AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO);


    @Operation(summary = "批量标记已读通知")
    @PostMapping(PREFIX + "setReadStateMore")
    ResponseVO setReadStateMore(@RequestBody AgentNoticeSetReadStateMoreReqVO agentNoticeSetReadStateMoreReqVO,@RequestParam(defaultValue = "true") boolean isAgent);

    @Operation(summary = "获取跑马灯通知列表")
    @PostMapping(PREFIX + "/getAgentNoticeHeadList")

    ResponseVO<List<UserNoticeRespVO>> getAgentNoticeHeadList(UserNoticeHeadReqVO userNoticeReqVO);
}
