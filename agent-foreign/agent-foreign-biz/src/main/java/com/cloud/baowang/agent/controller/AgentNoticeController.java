package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.notice.AgentNoticeApi;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeRespBO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateMoreReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: wade
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name = "代理-消息通知-agent")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agent-notice/api")
public class AgentNoticeController {

    private final AgentNoticeApi agentNoticeApi;

//
//    @PostMapping("/getAngentNoticeTabList")
//    @Operation(summary = "获取代理的通知Tab字典")
//    public ResponseVO<HashMap<String, Object>> getAngentNoticeTabList() {
//        return agentNoticeApi.getAngentNoticeTabList();
//    }


    @Operation(summary = "获取代理消息的列表")
    @PostMapping("/getAgentNoticeList")
    public ResponseVO<AgentNoticeRespBO> getAgentNoticeList(@RequestBody AgentNoticeVO agentNoticeVO) {
        //setLoginState(agentNoticeVO);
        return agentNoticeApi.getAgentNoticeList(agentNoticeVO,Boolean.TRUE);
    }


//    @Operation(summary = "获取代理的未读通知总数量")
//    @PostMapping("/getAgentNoticeUnreadTotal")
//    public ResponseVO<Long> getAgentNoticeUnreadTotal() {
//        return agentNoticeApi.getAgentNoticeUnreadTotal();
//    }


    @Operation(summary = "单个标记已读/删除通知")
    @PostMapping("/setReadState")
    public ResponseVO setReadState(@RequestBody AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        agentNoticeSetReadStateReqVO.setAgentAccount(CurrReqUtils.getAccount());
        agentNoticeSetReadStateReqVO.setAgentId(CurrReqUtils.getOneId());
        return agentNoticeApi.setReadState(agentNoticeSetReadStateReqVO);
    }

    @Operation(summary = "获取代理用户强制弹窗通知列表")
    @PostMapping("/getForceAgentNoticeHeadList")
    public ResponseVO<List<UserNoticeRespVO>> getForceAgentNoticeHeadList(@Valid @RequestBody UserNoticeHeadReqVO userNoticeReqVO) {
        userNoticeReqVO.setAgentAccount(CurrReqUtils.getAccount());
        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        return agentNoticeApi.getForceAgentNoticeHeadList(userNoticeReqVO);
    }


    @Operation(summary = "批量标记已读/删除通知")
    @PostMapping("/setReadStateMore")
    public ResponseVO setReadStateMore(@RequestBody AgentNoticeSetReadStateMoreReqVO agentNoticeSetReadStateReqVO) {
        agentNoticeSetReadStateReqVO.setAgentAccount(CurrReqUtils.getAccount());
        agentNoticeSetReadStateReqVO.setAgentId(CurrReqUtils.getOneId());
        return agentNoticeApi.setReadStateMore(agentNoticeSetReadStateReqVO,true);
    }


    @Operation(summary ="获取跑马灯通知列表-公告")
    @PostMapping("/getAgentNoticeHeadList")
    public ResponseVO<List<UserNoticeRespVO>> getAgentNoticeHeadList() {
        UserNoticeHeadReqVO userNoticeReqVO = new UserNoticeHeadReqVO();

        userNoticeReqVO.setDeviceTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        return agentNoticeApi.getAgentNoticeHeadList(userNoticeReqVO);
    }
    /*@Deprecated
    @Operation(summary = "删除消息通知--该方法废弃")
    @PostMapping("/deleteAgentNotice")
    public ResponseVO deleteAgentNotice(@RequestBody AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        UserAccountVO currentUser = JwtUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        agentNoticeSetReadStateReqVO.setAgentAccount(currentUser.getUserAccount());
        return agentNoticeApi.deleteAgentNotice(agentNoticeSetReadStateReqVO);
    }*/

    /*@Deprecated
    @Operation(summary = "批量删除消息通知--该方法废弃")
    @PostMapping("/deleteAgentNoticeMore")
    public ResponseVO deleteAgentNoticeMore(@RequestBody AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        UserAccountVO currentUser = JwtUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        agentNoticeSetReadStateReqVO.setAgentAccount(currentUser.getUserAccount());
        return agentNoticeApi.deleteAgentNoticeMore(agentNoticeSetReadStateReqVO);
    }*/


    public void setLoginState(AgentNoticeVO agentNoticeVO) {
        agentNoticeVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentNoticeVO.setUserId(CurrReqUtils.getOneId());
        agentNoticeVO.setUserAccount(CurrReqUtils.getAccount());

    }

}
