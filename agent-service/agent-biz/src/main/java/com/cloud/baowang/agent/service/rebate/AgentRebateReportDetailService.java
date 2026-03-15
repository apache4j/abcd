package com.cloud.baowang.agent.service.rebate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.po.commission.AgentRebateReportDetailPO;
import com.cloud.baowang.agent.repositories.AgentRebateReportDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 14:15
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentRebateReportDetailService extends ServiceImpl<AgentRebateReportDetailRepository, AgentRebateReportDetailPO> {
    private final AgentRebateReportDetailRepository detailRepository;

    public void deleteByReportId(String reportId) {
        LambdaQueryWrapper<AgentRebateReportDetailPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentRebateReportDetailPO::getRebateReportId, reportId);
        detailRepository.delete(queryWrapper);
    }

    public List<AgentRebateReportDetailPO> getRebateDetailByReportId(String reportId) {
        LambdaQueryWrapper<AgentRebateReportDetailPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentRebateReportDetailPO::getRebateReportId, reportId);
        return detailRepository.selectList(queryWrapper);
    }
}
