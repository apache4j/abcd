package com.cloud.baowang.agent.service.commission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentValidUserRecordVO;
import com.cloud.baowang.agent.po.AgentValidUserRecordPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.repositories.AgentValidUserRecordRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/12/19 20:59
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentValidUserRecordService extends ServiceImpl<AgentValidUserRecordRepository, AgentValidUserRecordPO> {
    private final AgentValidUserRecordRepository agentValidUserRecordRepository;

    /**
     * 返回没有被算为有效新增的会员id集合
     */
    public List<String> getNotSettleList(List<String> userIdList, String agentId, String commissionType) {
        if (userIdList == null || userIdList.size() == 0) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<AgentValidUserRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AgentValidUserRecordPO::getUserId, userIdList);
        queryWrapper.eq(AgentValidUserRecordPO::getAgentId, agentId);
        queryWrapper.eq(AgentValidUserRecordPO::getCommissionType, commissionType);
        List<AgentValidUserRecordPO> list = agentValidUserRecordRepository.selectList(queryWrapper);
        if (list == null || list.size() == 0) {
            return userIdList;
        } else {
            List<String> settleList = list.stream().map(AgentValidUserRecordPO::getUserId).toList();
            List<String> delList = new ArrayList<>(settleList);
            List<String> notSettleList = new ArrayList<>(userIdList);
            notSettleList.removeAll(delList);
            return notSettleList;
        }
    }

    public void deleteHis(String agentId, Long startTime, Long endTime, String commissionType) {
        LambdaQueryWrapper<AgentValidUserRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentValidUserRecordPO::getAgentId, agentId);
        queryWrapper.eq(AgentValidUserRecordPO::getStartTime, startTime);
        queryWrapper.eq(AgentValidUserRecordPO::getEndTime, endTime);
        queryWrapper.eq(AgentValidUserRecordPO::getCommissionType, commissionType);
        agentValidUserRecordRepository.delete(queryWrapper);
    }

    public List<String> getNewValidList(String agentId, String commissionType, Long startTime, Long endTime) {
        LambdaQueryWrapper<AgentValidUserRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentValidUserRecordPO::getAgentId, agentId);
        queryWrapper.eq(AgentValidUserRecordPO::getStartTime, startTime);
        queryWrapper.eq(AgentValidUserRecordPO::getEndTime, endTime);
        queryWrapper.eq(AgentValidUserRecordPO::getCommissionType, commissionType);
        List<AgentValidUserRecordPO> list = agentValidUserRecordRepository.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.stream().map(AgentValidUserRecordPO::getUserId).toList();
        }
        return new ArrayList<>();
    }

    public void saveSettleUser(List<String> userIdList, AgentValidUserRecordVO agentValidUserRecordVO) {
        List<AgentValidUserRecordPO> recordPOList = new ArrayList<>();
        for (String userId : userIdList) {
            AgentValidUserRecordPO recordPO = new AgentValidUserRecordPO();
            BeanUtils.copyProperties(agentValidUserRecordVO, recordPO);
            recordPO.setIsSettle(CommonConstant.business_one);
            recordPO.setUserId(userId);
            recordPOList.add(recordPO);
        }
        if (recordPOList.size() > 0) {
            this.saveBatch(recordPOList);
        }
    }
}
