package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentUserManageApi;
import com.cloud.baowang.agent.api.vo.user.AgentOverviewResponseVO;
import com.cloud.baowang.agent.service.AgentUserManageService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.agent.AgentBetOrderResVO;
import com.cloud.baowang.play.api.vo.agent.AgentUserDetailOrderRecordReqVO;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailParam;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailResponseVO;
import com.cloud.baowang.user.api.vo.user.SubordinateUserListParam;
import com.cloud.baowang.user.api.vo.user.SubordinateUserListResponseVO;
import com.cloud.baowang.user.api.vo.user.request.EditRemarkParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name = "代理PC和H5 会员管理")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/user-manage/api")
public class AgentUserManageController {


    private final AgentUserManageService agentUserManageService;
    private final AgentUserManageApi agentUserManageApi;

    @Operation(summary = "会员详情")
    @PostMapping(value = "/selectUserDetail")
    public ResponseVO<SelectUserDetailResponseVO> selectUserDetail(@Valid @RequestBody SelectUserDetailParam vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(siteCode);
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return agentUserManageService.selectUserDetail(vo);
    }

    @Operation(summary = "下级会员列表")
    @PostMapping(value = "/subordinateUserList")
    public ResponseVO<Page<SubordinateUserListResponseVO>> subordinateUserList(@Valid @RequestBody SubordinateUserListParam vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String currentUserAccount = CurrReqUtils.getAccount();
        vo.setAgentAccount(currentUserAccount);
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setSiteCode(siteCode);
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return agentUserManageService.subordinateUserList(vo);
    }

    @Operation(summary = "代理查询下级会员-游戏注单明细")
    @PostMapping("/getAgentClientOrder")
    public ResponseVO<AgentBetOrderResVO> getAgentClientOrder(@RequestBody AgentUserDetailOrderRecordReqVO requestVO) {
        requestVO.setAgentAccount(CurrReqUtils.getAccount());
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setAgentId(CurrReqUtils.getOneId());
        requestVO.setTimeZone(CurrReqUtils.getTimezone());
        return agentUserManageService.getAgentClientOrder(requestVO);

    }

    @Operation(summary = "代理备注编辑")
    @PostMapping(value = "/editRemark")
    public ResponseVO<?> editRemark(@Valid @RequestBody EditRemarkParam vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentUserManageService.agentEditRemark(vo);
    }

    @Operation(summary = "下级概览")
    @PostMapping(value = "/agentOverview")
    public ResponseVO<AgentOverviewResponseVO> agentOverview() {
        return agentUserManageApi.agentOverview(CurrReqUtils.getOneId(),
                CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());
    }


}
