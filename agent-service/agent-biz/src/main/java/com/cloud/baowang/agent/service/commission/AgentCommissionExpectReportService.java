package com.cloud.baowang.agent.service.commission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionExpectVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportReqVO;
import com.cloud.baowang.agent.po.commission.AgentCommissionExpectReportPO;
import com.cloud.baowang.agent.repositories.AgentCommissionExpectReportRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2023/10/29 1:18
 * @description: 代理佣金预期表记录
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionExpectReportService extends ServiceImpl<AgentCommissionExpectReportRepository, AgentCommissionExpectReportPO> {
    private final AgentCommissionExpectReportRepository agentCommissionExpectReportRepository;

    public AgentCommissionExpectVO getLatestCommissionExpectReport(String agentId) {
        AgentCommissionExpectReportPO po = agentCommissionExpectReportRepository.getLatestCommissionExpectReport(agentId);
        if (po == null) {
            AgentCommissionExpectVO vo = new AgentCommissionExpectVO();
            vo.setAgentRate(BigDecimal.ZERO);
            return vo;
        }
        return ConvertUtil.entityToModel(po, AgentCommissionExpectVO.class);
    }

    public List<AgentCommissionExpectReportPO> getCommissionExpectList(AgentCommissionReportReqVO reqVO) {
        LambdaQueryWrapper<AgentCommissionExpectReportPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionExpectReportPO::getSiteCode, reqVO.getSiteCode());
        query.eq(AgentCommissionExpectReportPO::getAgentId, reqVO.getAgentId());
        query.ge(AgentCommissionExpectReportPO::getStartTime, reqVO.getStartTime());
        query.le(AgentCommissionExpectReportPO::getEndTime, reqVO.getEndTime());
        return this.baseMapper.selectList(query);
    }
}
