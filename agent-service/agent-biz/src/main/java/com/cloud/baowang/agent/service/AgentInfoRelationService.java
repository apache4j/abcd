package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.po.AgentInfoRelationPO;
import com.cloud.baowang.agent.repositories.AgentInfoRelationRepository;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理上下级关系表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentInfoRelationService extends ServiceImpl< AgentInfoRelationRepository,AgentInfoRelationPO> {

    private final AgentInfoRelationRepository mapper;
    /**
     * 存储代理上下级关系
     * @param childId 当前代理编号
     * @param parentId 上级代理编号
     */
    public void insertRelation(String siteCode,String childId, String parentId) {
        long now = System.currentTimeMillis();
        String account = CurrReqUtils.getAccount();
        // 自己到自己
        mapper.insertSelfIfAbsent(SnowFlakeUtils.getSnowId(), siteCode, childId, account, now);

        // 没有上级：总代/根节点，结束
        if (StringUtils.isBlank(parentId)) {
            return;
        }

        //自己到自己
        mapper.insertSelfIfAbsent(SnowFlakeUtils.getSnowId(), siteCode, parentId, account, now);

        //所有父节点（distance + 1）
        insertByParent(siteCode, parentId, childId, account, now);

    }

    public void insertByParent(String siteCode, String parentId,String childId, String account,long now) {
        List<AgentInfoRelationPO> parentNodes = mapper.selectList(Wrappers.<AgentInfoRelationPO>lambdaQuery()
                        .eq(AgentInfoRelationPO::getSiteCode, siteCode)
                        .eq(AgentInfoRelationPO::getDescendantAgentId, parentId)
                        .select(AgentInfoRelationPO::getAncestorAgentId, AgentInfoRelationPO::getAgentDepth)
        );

        if (parentNodes == null || parentNodes.isEmpty()) {
            return;
        }
        List<AgentInfoRelationPO> list = new ArrayList<>(parentNodes.size());
        for (AgentInfoRelationPO parent : parentNodes) {
            AgentInfoRelationPO insert = new AgentInfoRelationPO();
            insert.setId(SnowFlakeUtils.getSnowId());
            insert.setSiteCode(siteCode);
            insert.setAncestorAgentId(parent.getAncestorAgentId());
            insert.setDescendantAgentId(childId);
            insert.setAgentDepth(parent.getAgentDepth() + 1);
            insert.setCreator(account);
            insert.setUpdater(account);
            insert.setCreatedTime(now);
            insert.setUpdatedTime(now);
            list.add(insert);
        }

        this.saveBatch(list);
    }

    public List<AgentInfoRelationPO> selectByParentId(String parentId) {
        LambdaQueryWrapper<AgentInfoRelationPO> queryWrapper = Wrappers.<AgentInfoRelationPO>lambdaQuery();
        queryWrapper.eq(AgentInfoRelationPO::getAncestorAgentId, parentId);
        queryWrapper.eq(AgentInfoRelationPO::getAgentDepth, 1);
        queryWrapper.orderByDesc(AgentInfoRelationPO::getAgentDepth);
        return mapper.selectList(queryWrapper);
    }
}
