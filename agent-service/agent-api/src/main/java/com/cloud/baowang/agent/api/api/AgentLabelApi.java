/**
 * @(#)AgentLabeService.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.label.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentLabelApi", value = ApiConstants.NAME)
@Tag(name = "代理标签")
public interface AgentLabelApi {
    String PREFIX = ApiConstants.PREFIX + "/agent-label/api";

    @Operation(description = "代理标签配置增加")
    @PostMapping(PREFIX + "/add")
    ResponseVO<Void> add(@RequestBody AgentLabelAddVO agentLabelAddVO);

    @Operation(description = "代理标签配置修改")
    @PostMapping(PREFIX + "/edit")
    ResponseVO<Void> edit(@RequestBody AgentLabelEditVO agentLabelAddVO);

    @Operation(description = "代理标签配置删除")
    @PostMapping(PREFIX + "/delete")
    ResponseVO<Void> delete(@RequestBody AgentLabelDeleteVO vo);

    @Operation(description = "代理标签配置列表")
    @PostMapping(PREFIX + "/list")
    ResponseVO<Page<AgentLabelListVO>> listPage(@RequestBody AgentLabelListPageVO vo);

    @Operation(description = "代理标签配置变更记录列表")
    @PostMapping(PREFIX + "/record/list")
    ResponseVO<Page<AgentLabelRecordListVO>> recordListPage(@RequestBody AgentLabelReordListPageVO vo);

    @Operation(description = "代理标签会员列表")
    @PostMapping(PREFIX + "/record/list/user")
    ResponseVO<Page<AgentLabelUserVO>> recordListUserPage(@RequestBody AgentLabelReordListUserPageVO vo);

    @Operation(description = "所有代理标签")
    @PostMapping(PREFIX + "/getAllAgentLabel")
    List<AgentLabelVO> getAllAgentLabel();

    @Operation(description = "按照代理列表获取代理标签")
    @PostMapping(PREFIX + "/geAgentLabelByAgentLabelIds")
    List<AgentLabelVO> getAgentLabelByAgentLabelIds(@RequestBody List<String> agentLabelIds);

    @Operation(description = "根据站点code获取所有标签信息")
    @PostMapping(PREFIX + "/getAllAgentLabelBySiteCode")
    List<AgentLabelVO> getAllAgentLabelBySiteCode(@RequestParam("siteCode") String siteCode);
}