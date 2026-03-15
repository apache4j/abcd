package com.cloud.baowang.agent.service;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewRecordVO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalAuditPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.api.enums.AgentWithdrawReviewNumberEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.system.api.api.RiskApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AgentWithdrawReviewRecordService {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;
    private final AgentInfoApi agentInfoApi;
    private final RiskApi riskApi;

    private final AgentDepositWithdrawalAuditService agentDepositWithdrawalAuditService;

    /**
     * 提款审核记录分页列表
     *
     * @param vo
     * @return
     */
    public Page<AgentWithdrawReviewRecordVO> withdrawalReviewRecordPageList(AgentWithdrawReviewRecordPageReqVO vo) {
        Page<AgentWithdrawReviewRecordVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentWithdrawReviewRecordVO> pageResult = agentDepositWithdrawalRepository.withdrawalReviewRecordPageList(page, vo);
        if (CollUtil.isEmpty(pageResult.getRecords())) {
            return new Page<>();
        }
        List<AgentWithdrawReviewRecordVO> records = pageResult.getRecords();
        List<String> agentAccount = records.stream().map(AgentWithdrawReviewRecordVO::getAgentAccount).toList();

        List<AgentInfoVO> agentInfoList = agentInfoApi.getByAgentAccountsAndSiteCode(vo.getSiteCode(), agentAccount);

        Map<String, String> agentAccountNameMap = agentInfoList.stream()
                .filter(agent -> StringUtils.isNotBlank(agent.getName()))
                .collect(Collectors.toMap(
                        AgentInfoVO::getAgentAccount,
                        AgentInfoVO::getName
                ));

        List<String> orderNoList = pageResult.getRecords().stream().map(AgentWithdrawReviewRecordVO::getOrderNo).toList();
        Map<String, List<AgentDepositWithdrawalAuditPO>> auditInfoMap = agentDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        for (AgentWithdrawReviewRecordVO record : pageResult.getRecords()) {
            String recordAgentAccount = record.getAgentAccount();
            if (agentAccountNameMap.containsKey(recordAgentAccount)) {
                record.setAgentName(agentAccountNameMap.get(recordAgentAccount));
            }

            List<AgentDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
            if (null != auditPOList) {
                long auditTime = 0L;
                long consuming = 0L;
                String auditUser = "";
                for (AgentDepositWithdrawalAuditPO audit : auditPOList) {
                    if (audit.getAuditTimeConsuming() != null) {
                        consuming += audit.getAuditTimeConsuming(); // 只累加非空的审核用时
                    }
                    if (AgentWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode().equals(audit.getNum())) {
                        record.setFirstAuditInfo(audit.getAuditInfo());
                        record.setFirstAuditUser(audit.getAuditUser());
                        record.setFirstAuditTime(audit.getAuditTime());
                        record.setFirstAuditUseTime(audit.getAuditTimeConsuming());
                        auditTime = audit.getAuditTime();
                        auditUser = audit.getAuditUser();
                    } else if (AgentWithdrawReviewNumberEnum.WAIT_PAY_OUT.getCode().equals(audit.getNum())) {
                        record.setPaymentAuditInfo(audit.getAuditInfo());
                        record.setPaymentAuditUser(audit.getAuditUser());
                        record.setPaymentAuditTime(audit.getAuditTime());
                        record.setPaymentAuditUseTime(audit.getAuditTimeConsuming());
                        auditTime = audit.getAuditTime();
                        auditUser = audit.getAuditUser();
                    }
                }
                record.setAuditUser(auditUser);
                record.setAuditTime(auditTime);
                record.setAuditDuration(DateUtils.formatTime(consuming));
            }
        }
        return pageResult;
    }

    public Long getTotal(AgentWithdrawReviewRecordPageReqVO vo) {
        return agentDepositWithdrawalRepository.withdrawalReviewRecordTotal(vo);
    }
}
