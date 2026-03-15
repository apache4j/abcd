package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainRecordVO;
import com.cloud.baowang.agent.po.AgentDomainRecordPO;
import com.cloud.baowang.agent.repositories.AgentDomainRecordRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 域名的变更记录
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentDomainRecordService {


    private final AgentDomainRecordRepository agentDomainRecordRepository;

    /**
     * 获取域名的变更记录的列表
     */
    public Page<AgentDomainRecordResponseVO> getAgentDomainRecordList(AgentDomainRecordVO agentDomainRecordVO) {
        LambdaQueryWrapper<AgentDomainRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentDomainRecordPO::getSiteCode, agentDomainRecordVO.getSiteCode());
        lambdaQueryWrapper.orderByDesc(AgentDomainRecordPO::getUpdatedTime);
        lambdaQueryWrapper.ge(agentDomainRecordVO.getUpdateTimeBegin() != null, AgentDomainRecordPO::getUpdatedTime, agentDomainRecordVO.getUpdateTimeBegin());
        lambdaQueryWrapper.le(agentDomainRecordVO.getUpdateTimeEnd() != null, AgentDomainRecordPO::getUpdatedTime, agentDomainRecordVO.getUpdateTimeEnd());
        Integer recordType = agentDomainRecordVO.getRecordType();
        if (recordType != null) {
            lambdaQueryWrapper.like(AgentDomainRecordPO::getRecordType, recordType);
        }
        String updater = agentDomainRecordVO.getUpdater();
        if (StringUtils.isNotBlank(updater)) {
            lambdaQueryWrapper.eq(AgentDomainRecordPO::getUpdater, updater);
        }
        String orderField = agentDomainRecordVO.getOrderField();
        if (StringUtils.isNotBlank(orderField) && "updatedTime".equals(orderField)) {
            String orderType = agentDomainRecordVO.getOrderType();
            if ("asc".equals(orderType)) {
                lambdaQueryWrapper.orderByAsc(AgentDomainRecordPO::getUpdatedTime);
            } else {
                lambdaQueryWrapper.orderByDesc(AgentDomainRecordPO::getUpdatedTime);
            }
        } else {
            lambdaQueryWrapper.orderByDesc(AgentDomainRecordPO::getUpdatedTime);
        }
        Page<AgentDomainRecordPO> page = new Page<>(agentDomainRecordVO.getPageNumber(), agentDomainRecordVO.getPageSize());
        Page<AgentDomainRecordPO> pageList = agentDomainRecordRepository.selectPage(page, lambdaQueryWrapper);
        return ConvertUtil.toConverPage(pageList.convert(item -> BeanUtil.copyProperties(item, AgentDomainRecordResponseVO.class)));
    }


}
