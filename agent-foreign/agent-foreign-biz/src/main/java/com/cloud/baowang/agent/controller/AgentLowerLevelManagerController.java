package com.cloud.baowang.agent.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentLowerLevelManagerApi;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogPageVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoGameDynamicReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoGameDynamicVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelManagerEditRemarkVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelManagerInfoReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelManagerInfoVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelManagerPageReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelManagerPageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name =  "代理-下级管理")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/agent-lower-level-manager")
public class AgentLowerLevelManagerController {

    private  final AgentLowerLevelManagerApi agentLowerLevelManagerApi;

     @Operation(summary ="下级管理分页列表")
    @PostMapping("/listPage")
     public ResponseVO<Page<AgentLowerLevelManagerPageVO>> listPage(@Valid @RequestBody AgentLowerLevelManagerPageReqVO pageVO) {
         pageVO.setAgentAccount(CurrReqUtils.getAccount());
         pageVO.setSiteCode(CurrReqUtils.getSiteCode());
         return agentLowerLevelManagerApi.agentLowerLevelManagerListPage(pageVO);
     }

     @Operation(summary ="编辑备注")
    @PostMapping("/editRemark")
    public ResponseVO<Void> editRemark(@Valid @RequestBody AgentLowerLevelManagerEditRemarkVO vo) {
         vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentLowerLevelManagerApi.agentLowerLevelManagerEditRemark(vo);
    }

     @Operation(summary ="下级详情")
    @PostMapping("/info")
    public ResponseVO<AgentLowerLevelManagerInfoVO> info(@Valid @RequestBody AgentLowerLevelManagerInfoReqVO vo) {
        return agentLowerLevelManagerApi.agentLowerLevelManagerInfo(vo);
    }

     @Operation(summary ="下级详情-游戏动态-H5")
    @PostMapping("/gameDynamic")
    public ResponseVO<Page<AgentLowerLevelInfoGameDynamicVO>> gameDynamic(@Valid @RequestBody AgentLowerLevelInfoGameDynamicReqVO vo) {
        return agentLowerLevelManagerApi.agentLowerLevelManagerGameDynamic(vo);
    }

     @Operation(summary ="下级详情-场馆统计-PC")
    @PostMapping("/venueStatistical")
    public ResponseVO<List<AgentLowerLevelInfoVenueStatisticalVO>> venueStatistical(@RequestBody AgentLowerLevelInfoVenueStatisticalReqVO vo) {
        return agentLowerLevelManagerApi.venueStatistical(vo);
    }

     @Operation(summary ="分配日志")
    @PostMapping("/distributeLog")
    public ResponseVO<Page<AgentDistributeLogPageVO>> distributeLog(@Valid @RequestBody AgentDistributeLogReqVO vo) {
        if (StrUtil.isBlank(vo.getAgentAccount())) {
            vo.setAgentAccount(CurrReqUtils.getAccount());
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentLowerLevelManagerApi.agentLowerLevelManagerDistributeLog(vo);
    }

     @Operation(summary ="分配日志下拉列表")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, Object>> getDistributeLogDownBox() {
        return agentLowerLevelManagerApi.getDistributeLogDownBox();
    }

}
