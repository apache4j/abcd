package com.cloud.baowang.site.controller.agent;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.agent.api.vo.label.*;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author aomiao
 */
@RestController
@RequestMapping("/agent-label")
@Tag(name = "代理-代理标签配置")
@AllArgsConstructor
public class AgentLabelController {

    private final AgentLabelApi agentLabelApi;


    @Operation(summary = "代理标签配置增加")
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody AgentLabelAddVO agentLabelAddVO) {
        agentLabelAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentLabelAddVO.setOperator(CurrReqUtils.getAccount());
        return agentLabelApi.add(agentLabelAddVO);
    }

    @Operation(summary = "代理标签配置修改")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@RequestBody AgentLabelEditVO editVO) {
        editVO.setSiteCode(CurrReqUtils.getSiteCode());
        editVO.setOperator(CurrReqUtils.getAccount());
        return agentLabelApi.edit(editVO);
    }

    @Operation(summary = "代理标签配置删除")
    @PostMapping("/delete")
    public ResponseVO<Void> delete(@RequestBody AgentLabelDeleteVO deleteVO) {
        deleteVO.setSiteCode(CurrReqUtils.getSiteCode());
        deleteVO.setOperator(CurrReqUtils.getAccount());
        return agentLabelApi.delete(deleteVO);
    }

    @Operation(summary = "代理标签配置列表")
    @PostMapping("/listPage")
    public ResponseVO<Page<AgentLabelListVO>> listPage(@RequestBody AgentLabelListPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentLabelApi.listPage(vo);
    }

    @Operation(summary = "代理标签配置变更记录列表")
    @PostMapping("/record/list")
    public ResponseVO<Page<AgentLabelRecordListVO>> recordListPage(@RequestBody AgentLabelReordListPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentLabelApi.recordListPage(vo);
    }

    @Operation(summary = "代理标签已被代理使用分页列表（查询某个代理标签下所有关联的代理信息分页）")
    @PostMapping("/record/list/user")
    public ResponseVO<Page<AgentLabelUserVO>> recordListUserPage(@RequestBody AgentLabelReordListUserPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentLabelApi.recordListUserPage(vo);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "获取代理标签下拉")
    public ResponseVO<List<CodeValueNoI18VO>> getDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<CodeValueNoI18VO> result = new ArrayList<>();
        List<AgentLabelVO> allAgentLabelBySiteCode = agentLabelApi.getAllAgentLabelBySiteCode(siteCode);
        if (CollectionUtil.isNotEmpty(allAgentLabelBySiteCode)) {
            result = allAgentLabelBySiteCode.stream()
                    .map(agentLabel -> new CodeValueNoI18VO(agentLabel.getId(), agentLabel.getName()))
                    .toList();
        }
        return ResponseVO.success(result);
    }
}

