package com.cloud.baowang.site.controller.notice;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.notice.AgentNoticeApi;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeRespBO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateMoreReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:商务消息
 */
@Tag(name = "商务通知/公告-business")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/business-notice/api")
public class BusinessNoticeController {

    private final AgentNoticeApi agentNoticeApi;

    @Operation(summary = "获取商务消息的列表")
    @PostMapping("/getBusinessNoticeList")
    public ResponseVO<AgentNoticeRespBO> getBusinessNoticeList(@RequestBody AgentNoticeVO agentNoticeVO) {
        return agentNoticeApi.getAgentNoticeList(agentNoticeVO,Boolean.FALSE);
    }

    @Operation(summary = "单个标记已读/删除通知")
    @PostMapping("/setReadState")
    public ResponseVO setReadState(@RequestBody AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        agentNoticeSetReadStateReqVO.setAgentAccount(CurrReqUtils.getAccount());
        agentNoticeSetReadStateReqVO.setAgentId(CurrReqUtils.getOneId());
        return agentNoticeApi.setReadState(agentNoticeSetReadStateReqVO);
    }


    @Operation(summary = "批量标记已读/删除通知")
    @PostMapping("/setReadStateMore")
    public ResponseVO setReadStateMore(@RequestBody AgentNoticeSetReadStateMoreReqVO agentNoticeSetReadStateReqVO) {
        agentNoticeSetReadStateReqVO.setAgentAccount(CurrReqUtils.getAccount());
        agentNoticeSetReadStateReqVO.setAgentId(CurrReqUtils.getOneId());
        return agentNoticeApi.setReadStateMore(agentNoticeSetReadStateReqVO,false);
    }
}
