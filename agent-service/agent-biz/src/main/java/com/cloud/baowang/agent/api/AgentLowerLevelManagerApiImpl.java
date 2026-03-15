package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentLowerLevelManagerApi;
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
import com.cloud.baowang.agent.service.AgentLowerLevelManagerService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/17 15:32
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentLowerLevelManagerApiImpl implements AgentLowerLevelManagerApi {
    private final AgentLowerLevelManagerService agentLowerLevelManagerService;
    @Override
    public ResponseVO<Page<AgentLowerLevelManagerPageVO>> agentLowerLevelManagerListPage(AgentLowerLevelManagerPageReqVO pageVO) {
        return ResponseVO.success(agentLowerLevelManagerService.listPage(pageVO));
    }

    @Override
    public ResponseVO<Void> agentLowerLevelManagerEditRemark(AgentLowerLevelManagerEditRemarkVO vo) {
        agentLowerLevelManagerService.editRemark(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<AgentLowerLevelManagerInfoVO> agentLowerLevelManagerInfo(AgentLowerLevelManagerInfoReqVO vo) {
        return ResponseVO.success(agentLowerLevelManagerService.info(vo));
    }

    /**
     * 下级详情-游戏动态-H5
     * @param vo
     * @return
     */
    @Override
    public ResponseVO<Page<AgentLowerLevelInfoGameDynamicVO>> agentLowerLevelManagerGameDynamic(AgentLowerLevelInfoGameDynamicReqVO vo) {
        return ResponseVO.success(agentLowerLevelManagerService.gameDynamic(vo));
    }

    /**
     * 下级详情-场馆统计-PC
     * @param vo
     * @return
     */
    @Override
    public ResponseVO<List<AgentLowerLevelInfoVenueStatisticalVO>> venueStatistical(AgentLowerLevelInfoVenueStatisticalReqVO vo) {
        return ResponseVO.success(agentLowerLevelManagerService.venueStatistical(vo));
    }

    @Override
    public ResponseVO<Page<AgentDistributeLogPageVO>> agentLowerLevelManagerDistributeLog(AgentDistributeLogReqVO vo) {
        return ResponseVO.success(agentLowerLevelManagerService.distributeLog(vo));
    }

    @Override
    public ResponseVO<Map<String, Object>> getDistributeLogDownBox() {
        return ResponseVO.success(agentLowerLevelManagerService.getDistributeLogDownBox());
    }
}
