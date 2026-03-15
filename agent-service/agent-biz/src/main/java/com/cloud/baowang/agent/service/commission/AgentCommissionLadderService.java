package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionLadderVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanPageVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionPlanReqVO;
import com.cloud.baowang.agent.po.AgentCommissionLadderPO;
import com.cloud.baowang.agent.po.AgentCommissionPlanPO;
import com.cloud.baowang.agent.repositories.AgentCommissionLadderRepository;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 15:01
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionLadderService extends ServiceImpl<AgentCommissionLadderRepository, AgentCommissionLadderPO> {

    private final AgentCommissionLadderRepository agentCommissionLadderRepository;


    public List<AgentCommissionLadderVO> getListByPlanIds(List<String> ids) {
        LambdaQueryWrapper<AgentCommissionLadderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AgentCommissionLadderPO::getPlanId, ids);
        List<AgentCommissionLadderPO> list = agentCommissionLadderRepository.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            return  ConvertUtil.entityListToModelList(list, AgentCommissionLadderVO.class);
        }
        return null;
    }

    public List<AgentCommissionLadderVO> getListByPlanId(String planId) {
        LambdaQueryWrapper<AgentCommissionLadderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionLadderPO::getPlanId, planId);
        List<AgentCommissionLadderPO> list = agentCommissionLadderRepository.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            return  ConvertUtil.entityListToModelList(list, AgentCommissionLadderVO.class);
        }
        return null;
    }

    public Integer deleteByPlanId(String planId) {
        return agentCommissionLadderRepository.deleteByPlanId(planId);
    }

    public List<String> getPlanCodeListByCycle(Integer settleCycle, String siteCode) {
        return agentCommissionLadderRepository.getPlanCodeListByCycle(settleCycle, siteCode);
    }
}
