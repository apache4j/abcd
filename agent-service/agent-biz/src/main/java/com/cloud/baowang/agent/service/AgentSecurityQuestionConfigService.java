package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentSecurityQuestionConfigEnum;
import com.cloud.baowang.agent.api.vo.security.AgentSecurityListVO;
import com.cloud.baowang.agent.po.AgentSecurityPO;
import com.cloud.baowang.agent.po.AgentSecurityQuestionConfigPO;
import com.cloud.baowang.agent.repositories.AgentSecurityQuestionConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 15:55
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentSecurityQuestionConfigService extends ServiceImpl<AgentSecurityQuestionConfigRepository, AgentSecurityQuestionConfigPO> {
    private final AgentSecurityQuestionConfigRepository agentSecurityQuestionConfigRepository;

    public List<AgentSecurityListVO> allEnableSecurityQuestions() {
        List<AgentSecurityQuestionConfigPO> agentSecurityQuestionConfigPOS = agentSecurityQuestionConfigRepository.selectList(Wrappers.<AgentSecurityQuestionConfigPO>lambdaQuery().eq(AgentSecurityQuestionConfigPO::getStatus, AgentSecurityQuestionConfigEnum.ENABLE.getCode()));
        if (CollectionUtils.isEmpty(agentSecurityQuestionConfigPOS)){
            return Lists.newArrayList();
        }
        List<AgentSecurityListVO> agentSecurityListVOS = Lists.newArrayList();
        for (AgentSecurityQuestionConfigPO agentSecurityQuestionConfigPO : agentSecurityQuestionConfigPOS) {
            AgentSecurityListVO agentSecurityListVO = new AgentSecurityListVO();
            agentSecurityListVO.setSecurityQuestion(agentSecurityQuestionConfigPO.getSecurityQuestion());
            agentSecurityListVO.setSecurityQuestionId(agentSecurityQuestionConfigPO.getId());
            agentSecurityListVOS.add(agentSecurityListVO);
        }
        return agentSecurityListVOS;
    }

}
