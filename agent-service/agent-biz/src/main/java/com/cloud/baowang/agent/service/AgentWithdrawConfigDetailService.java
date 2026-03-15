package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawConfigDetailVO;
import com.cloud.baowang.agent.po.AgentWithdrawConfigDetailPO;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigDetailRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/01 23:06
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentWithdrawConfigDetailService extends ServiceImpl<AgentWithdrawConfigDetailRepository, AgentWithdrawConfigDetailPO> {

    private final AgentWithdrawConfigDetailRepository detailRepository;

    public List<AgentWithdrawConfigDetailVO> getByConfigId(String configId) {
        LambdaQueryWrapper<AgentWithdrawConfigDetailPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentWithdrawConfigDetailPO::getConfigId, configId);
        List<AgentWithdrawConfigDetailPO> list = detailRepository.selectList(queryWrapper);
        return ConvertUtil.entityListToModelList(list, AgentWithdrawConfigDetailVO.class);
    }

    public Integer deleteByConfigId(String configId) {
        return detailRepository.deleteByConfigId(configId);
    }
}
