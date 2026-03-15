package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.po.AgentClosurePO;
import com.cloud.baowang.agent.repositories.AgentClosureRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class AgentClosureService extends ServiceImpl<AgentClosureRepository, AgentClosurePO> {
    private final AgentClosureRepository agentClosureRepository;

    public void onNewAgentAdd(String parentUserId, String newUserId) {
        // 自己到自己
        agentClosureRepository.insertSelfIfAbsent(newUserId);

        // 没有上级：总代/根节点，结束
        if (StringUtils.isBlank(parentUserId)) {
            return;
        }

        //自己到自己
        agentClosureRepository.insertSelfIfAbsent(newUserId);

        //所有父节点（distance + 1）
        agentClosureRepository.insertByParent(parentUserId, newUserId);
    }
}
