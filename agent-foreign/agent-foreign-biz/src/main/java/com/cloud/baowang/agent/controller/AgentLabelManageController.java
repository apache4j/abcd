package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.vo.label.AgentLabelManageAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRequestVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentSaveLabelUserResVO;
import com.cloud.baowang.agent.api.vo.label.SaveUserAssociationLabelResVO;
import com.cloud.baowang.agent.service.AgentLabelManageService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name = "代理下级会员标签")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agentLabelManage/api")
public class AgentLabelManageController {


    private final AgentLabelManageService agentLabelManageService;

    @Operation(summary = "代理标签配置增加")
    @PostMapping("/add")
    public ResponseVO<?> add(@RequestBody @Valid AgentLabelManageAddVO agentLabelAddVO) {
        agentLabelAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentLabelAddVO.setAgentAccount(CurrReqUtils.getAccount());
        return agentLabelManageService.add(agentLabelAddVO);
    }

    @Operation(summary = "代理标签配置修改")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@RequestBody AgentLabelManageEditVO agentLabelAddVO) {
        agentLabelAddVO.setAgentAccount(CurrReqUtils.getAccount());
        agentLabelAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentLabelManageService.edit(agentLabelAddVO);
        return ResponseVO.success();
    }

    @Operation(summary = "代理标签配置删除")
    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody AgentLabelManageDeleteVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        agentLabelManageService.delete(vo);
        return ResponseVO.success();
    }

    @Operation(summary = "查询该代理下该会员所有的标签记录")
    @PostMapping("/queryUserLabelRecord")
    public ResponseVO<Map<String, AgentLabelUserResponseVO>> queryUserLabelRecord(@RequestBody AgentLabelRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return agentLabelManageService.queryUserLabelRecord(vo);
    }

    @Operation(summary = "代理获取全部标签配置列表")
    @PostMapping("/listAllLabel")
    public ResponseVO<?> listAllLabel(@RequestBody AgentLabelRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return agentLabelManageService.listAllLabel(vo);
    }

    @Operation(summary = "代理对下级会员进行标签管理")
    @PostMapping("/record/saveUserLabel")
    ResponseVO<Void> saveUserLabel(@RequestBody AgentSaveLabelUserResVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return agentLabelManageService.saveUserLabel(vo);
    }

    @Operation(summary = "代理对下级多个会员进行关联标签管理")
    @PostMapping("/record/saveUserAssociationLabel")
    public ResponseVO<?> saveUserAssociationLabel(@RequestBody @Valid SaveUserAssociationLabelResVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setAgentAccountId(CurrReqUtils.getOneId());
        return agentLabelManageService.saveUserAssociationLabel(vo);
    }


}
