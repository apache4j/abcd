package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Desciption: 代理-下级管理
 * @Author: Ford
 * @Date: 2024/6/17 15:27
 * @Version: V1.0
 **/
@FeignClient(contextId = "remoteAgentLowerLevelManagerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理商层级 服务")
public interface AgentLowerLevelManagerApi {

    String PREFIX = ApiConstants.PREFIX + "/agentLowerLevelManager/api";

    @Operation(summary = "下级管理分页列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<Page<AgentLowerLevelManagerPageVO>> agentLowerLevelManagerListPage(@RequestBody AgentLowerLevelManagerPageReqVO pageVO);

    @Operation(summary = "编辑备注")
    @PostMapping(value = PREFIX + "/editRemark")
    ResponseVO<Void> agentLowerLevelManagerEditRemark(@RequestBody AgentLowerLevelManagerEditRemarkVO vo);

    @Operation(summary = "下级详情")
    @PostMapping(value = PREFIX + "/info")
    ResponseVO<AgentLowerLevelManagerInfoVO> agentLowerLevelManagerInfo(@RequestBody AgentLowerLevelManagerInfoReqVO vo);

    @Operation(summary = "下级详情-游戏动态-H5")
    @PostMapping(value = PREFIX + "/gameDynamic")
    ResponseVO<Page<AgentLowerLevelInfoGameDynamicVO>> agentLowerLevelManagerGameDynamic(@RequestBody AgentLowerLevelInfoGameDynamicReqVO vo);

    @Operation(summary = "下级详情-场馆统计-PC")
    @PostMapping(value = PREFIX + "/venueStatistical")
    ResponseVO<List<AgentLowerLevelInfoVenueStatisticalVO>> venueStatistical(@RequestBody AgentLowerLevelInfoVenueStatisticalReqVO vo);

    @Operation(summary = "分配日志")
    @PostMapping(value = PREFIX + "/distributeLog")
    ResponseVO<Page<AgentDistributeLogPageVO>> agentLowerLevelManagerDistributeLog(@RequestBody AgentDistributeLogReqVO vo);

    @Operation(summary = "分配日志下拉列表")
    @PostMapping(value = PREFIX + "/getDownBox")
    ResponseVO<Map<String, Object>> getDistributeLogDownBox();
}
