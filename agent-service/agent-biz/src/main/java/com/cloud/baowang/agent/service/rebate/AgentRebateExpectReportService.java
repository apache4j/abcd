package com.cloud.baowang.agent.service.rebate;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanTurnoverConfigVo;
import com.cloud.baowang.agent.api.vo.commission.AgentRebateRateVO;
import com.cloud.baowang.agent.api.vo.commission.RebateDetailVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentRebateExpectReportPO;
import com.cloud.baowang.agent.repositories.AgentRebateExpectReportRepository;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 14:15
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentRebateExpectReportService extends ServiceImpl<AgentRebateExpectReportRepository, AgentRebateExpectReportPO> {
    private final AgentRebateExpectReportRepository rebateExpectReportRepository;
    private final AgentInfoService agentInfoService;
    private final AgentRebateConfigService agentRebateConfigService;
    private final AgentCommissionPlanService agentCommissionPlanService;

    public AgentRebateRateVO getLatestRebateDetail(String agentId) {
        AgentRebateRateVO agentRebateRateVO = new AgentRebateRateVO();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        if(planVO==null){
           log.info("当前代理:{}佣金方案不存在",agentId);
            return null;
        }
        AgentCommissionPlanTurnoverConfigVo agentCommissionPlanTurnoverConfigVo = agentRebateConfigService.getConfigByPlanId(planVO.getId());
        agentRebateRateVO.setNewUserAmount(agentCommissionPlanTurnoverConfigVo.getNewUserAmount());

        List<RebateDetailVO> detailList = rebateExpectReportRepository.getLatestRebateDetail(agentId);
        if (detailList == null || detailList.size() == 0) {
            VenueTypeEnum[] types = VenueTypeEnum.values();
            List<RebateDetailVO> result = new ArrayList<>();
            for (VenueTypeEnum type : types) {
                RebateDetailVO vo = new RebateDetailVO();
                vo.setVenueType(type.getCode().toString());
                vo.setRebateRate(BigDecimal.ZERO);
                result.add(vo);
            }

            agentRebateRateVO.setDetailList(result);
        } else {
            agentRebateRateVO.setDetailList(detailList);
        }

        return agentRebateRateVO;
    }
}
