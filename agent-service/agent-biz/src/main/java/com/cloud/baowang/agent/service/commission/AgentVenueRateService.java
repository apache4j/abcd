package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentVenueRateVO;
import com.cloud.baowang.agent.po.commission.AgentVenueRatePO;
import com.cloud.baowang.agent.repositories.AgentVenueRateRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 23:36
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentVenueRateService extends ServiceImpl<AgentVenueRateRepository, AgentVenueRatePO> {

    private final AgentVenueRateRepository agentVenueRateRepository;

    public List<AgentVenueRateVO> getListByPlanId(String planId) {
        LambdaQueryWrapper<AgentVenueRatePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentVenueRatePO::getPlanId, planId);
        List<AgentVenueRatePO> list = agentVenueRateRepository.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            return  ConvertUtil.entityListToModelList(list, AgentVenueRateVO.class);
        }
        return new ArrayList<>();
    }
}
