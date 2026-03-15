package com.cloud.baowang.agent.service.rebate;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanTurnoverConfigVo;
import com.cloud.baowang.agent.po.AgentRebateConfigPO;
import com.cloud.baowang.agent.repositories.AgentRebateConfigRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 15:01
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentRebateConfigService extends ServiceImpl<AgentRebateConfigRepository, AgentRebateConfigPO> {

    private final AgentRebateConfigRepository agentRebateConfigRepository;

    public List<AgentCommissionPlanTurnoverConfigVo> getListByPlanIds(List<String> ids) {
        LambdaQueryWrapper<AgentRebateConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AgentRebateConfigPO::getPlanId, ids);
        List<AgentRebateConfigPO> list = agentRebateConfigRepository.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            return  ConvertUtil.entityListToModelList(list, AgentCommissionPlanTurnoverConfigVo.class);
        }
        return null;
    }

    public AgentCommissionPlanTurnoverConfigVo getConfigByPlanId(String id) {
        LambdaQueryWrapper<AgentRebateConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentRebateConfigPO::getPlanId, id);
        AgentRebateConfigPO po = agentRebateConfigRepository.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(po)) {
            return  ConvertUtil.entityToModel(po, AgentCommissionPlanTurnoverConfigVo.class);
        }
        return null;
    }

    public Integer deleteByPlanId(String planId) {
        return agentRebateConfigRepository.deleteByPlanId(planId);
    }

    public List<String> getPlanCodeListByCycle(Integer settleCycle, String siteCode) {
        return agentRebateConfigRepository.getPlanCodeListByCycle(settleCycle, siteCode);
    }


    public AgentCommissionPlanTurnoverConfigVo getCommissionPlan(String siteCode, String currencyCode, BigDecimal validAmount) {
        //todo

        return null;
    }
}
