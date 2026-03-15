/*
package com.cloud.baowang.admin.controller.agent.label;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.agent.api.enums.AgentLabelChangeEnum;
import com.cloud.baowang.agent.api.vo.label.AgentLabelAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRecordListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListUserPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserVO;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agent-label")
@Tag(name = "代理-代理标签配置")
@AllArgsConstructor
public class AgentLabelController {

    private final AgentLabelApi agentLabelApi;



    @Operation(summary = "代理标签配置增加")
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody AgentLabelAddVO agentLabelAddVO) {
        agentLabelAddVO.setOperator(CurrentRequestUtils.getCurrentOneId());
        return agentLabelApi.add(agentLabelAddVO);
    }

    @Operation(summary = "代理标签配置修改")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@RequestBody AgentLabelEditVO agentLabelAddVO) {
        agentLabelAddVO.setOperator(CurrentRequestUtils.getCurrentOneId());
        return agentLabelApi.edit(agentLabelAddVO);
    }

    @Operation(summary = "代理标签配置删除")
    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody AgentLabelDeleteVO agentLabelDeleteVO) {
        agentLabelDeleteVO.setOperator(CurrentRequestUtils.getCurrentOneId());
        return agentLabelApi.delete(agentLabelDeleteVO);
    }

    @Operation(summary = "代理标签配置列表")
    @PostMapping("/list")
    public ResponseVO<Page<AgentLabelListVO>> listPage(@RequestBody AgentLabelListPageVO vo) {
        return agentLabelApi.listPage(vo);
    }

    @Operation(summary = "代理标签配置变更记录列表")
    @PostMapping("/record/list")
    public ResponseVO<Page<AgentLabelRecordListVO>> recordListPage(@RequestBody AgentLabelReordListPageVO vo) {
        return agentLabelApi.recordListPage(vo);
    }

    @Operation(summary = "代理标签会员列表")
    @PostMapping("/record/list/user")
    public ResponseVO<Page<AgentLabelUserVO>> recordListUserPage(@RequestBody AgentLabelReordListUserPageVO vo) {
        return agentLabelApi.recordListUserPage(vo);
    }

    @Operation(summary = ("返回公共下拉框"))
    @GetMapping(value = "/record/list/querySelectCommon")
    public ResponseVO<Map<String, List<SystemParamVO>>> querySelectCommon() {
        Map<String, List<SystemParamVO>> map = Maps.newHashMap();
        List<SystemParamVO> agentStatus = AgentLabelChangeEnum.getList()
                .stream()
                .filter(item -> !item.getCode().equalsIgnoreCase(AgentLabelChangeEnum.ADD.getCode()))
                .map(item ->
                        SystemParamVO.builder().code(item.getCode()).value(item.getName()).type("").build())
                .toList();
        map.put("agentLabelChange", agentStatus);
        return ResponseVO.success(map);
    }
}

*/
