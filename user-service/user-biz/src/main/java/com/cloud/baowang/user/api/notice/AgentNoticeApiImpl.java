package com.cloud.baowang.user.api.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.notice.AgentNoticeApi;
import com.cloud.baowang.user.api.vo.notice.agent.*;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeHeadRspVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.service.AgentNoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentNoticeApiImpl implements AgentNoticeApi {

    private final AgentNoticeService agentNoticeService;


//    @Override
//    public ResponseVO<HashMap<String, Object>> getAngentNoticeTabList() {
//        return ResponseVO.success(agentNoticeService.getAngentNoticeTabList());
//    }

    @Override
    public ResponseVO<AgentNoticeRespBO> getAgentNoticeList(AgentNoticeVO agentNoticeVO,boolean isAgent) {
        return ResponseVO.success(agentNoticeService.getAgentNoticeList(agentNoticeVO,isAgent));
    }

    @Override
    public ResponseVO<UserNoticeRespVO> getAgentNoticeNotReadFirst(AgentNoticeVO agentNoticeVO) {
        return ResponseVO.success(agentNoticeService.getAgentNoticeNotReadFirst(agentNoticeVO));
    }



    @Override
    public ResponseVO addAgentNotice(AgentNoticeReq agentNoticeReq) {
        agentNoticeService.addAgentNotice(agentNoticeReq);
        return ResponseVO.success();
    }
//
//    @Override
//    public ResponseVO<Long> getAgentNoticeUnreadTotal() {
//        return ResponseVO.success(agentNoticeService.getAgentNoticeUnreadTotal());
//    }

    @Override
    public ResponseVO<List<UserNoticeRespVO>> getForceAgentNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        return ResponseVO.success(agentNoticeService.getForceAgentNoticeHeadList(userNoticeHeadReqVO));
    }

    @Override
    public ResponseVO setReadState(AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        try {
            agentNoticeService.setReadState(agentNoticeSetReadStateReqVO);
            return ResponseVO.success();
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseVO setReadStateMore(AgentNoticeSetReadStateMoreReqVO agentNoticeSetReadStateMoreReqVO,boolean isAgent) {
        agentNoticeService.setReadStateMore(agentNoticeSetReadStateMoreReqVO,isAgent);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<UserNoticeRespVO>> getAgentNoticeHeadList(UserNoticeHeadReqVO userNoticeReqVO) {
        return agentNoticeService.getAgentNoticeHeadList(userNoticeReqVO);
    }
}
