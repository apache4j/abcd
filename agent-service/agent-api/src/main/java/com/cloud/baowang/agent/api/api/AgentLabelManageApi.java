/**
 * @(#)AgentLabeService.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRequestVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentSaveLabelUserResVO;
import com.cloud.baowang.agent.api.vo.label.GetLabelsByAgentAccountVO;
import com.cloud.baowang.agent.api.vo.label.SaveUserAssociationLabelResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@FeignClient(contextId = "remoteAgentLabelManagerApi", value = ApiConstants.NAME)
@Tag(name = "代理标签")
public interface AgentLabelManageApi {
    String PREFIX = ApiConstants.PREFIX + "/agent-label-manager/api";

    @Operation(description = "代理标签配置增加")
    @PostMapping(PREFIX + "/add")
    ResponseVO<Void> add(@RequestBody @Valid AgentLabelManageAddVO agentLabelAddVO);

    @Operation(description = "代理标签配置修改")
    @PostMapping(PREFIX + "/edit")
    ResponseVO<Void> edit(@RequestBody AgentLabelManageEditVO agentLabelAddVO);

    @Operation(description = "代理标签配置删除")
    @PostMapping(PREFIX + "/delete")
    ResponseVO<Void> delete(@RequestBody AgentLabelManageDeleteVO vo);

    @Operation(description = "代理获取全部标签配置列表")
    @PostMapping(PREFIX + "/listAllLabel")
    ResponseVO<?> listAllLabel(@RequestBody AgentLabelRequestVO vo);

    @Operation(description = "代理对下级会员进行标签管理")
    @PostMapping(PREFIX + "/record/saveUserLabel")
    ResponseVO<Void> saveUserLabel(@RequestBody AgentSaveLabelUserResVO vo);

    @Operation(summary = "根据代理 查询标签集合")
    @PostMapping(PREFIX + "/getLabelsByAgentAccount")
    List<GetLabelsByAgentAccountVO> getLabelsByAgentAccount(@RequestParam("siteCode") String siteCode,
                                                            @RequestParam("agentAccount") String agentAccount,
                                                            @RequestParam(value = "userAccount", required = false) String userAccount);

    @Operation(summary = "根据代理 查询标签集合")
    @PostMapping(PREFIX + "/queryUserLabelRecord")
    ResponseVO<Map<String, AgentLabelUserResponseVO>> queryUserLabelRecord(@RequestBody AgentLabelRequestVO vo);

    @Operation(summary = "代理对下级会员进行标签管理")
    @PostMapping(PREFIX + "/record/saveUserAssociationLabel")
    ResponseVO<?> saveUserAssociationLabel(@RequestBody SaveUserAssociationLabelResVO vo);
}