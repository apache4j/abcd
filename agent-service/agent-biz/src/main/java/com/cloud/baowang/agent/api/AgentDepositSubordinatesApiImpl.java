package com.cloud.baowang.agent.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.GetAgentDepositAmountByAgentVO;
import com.cloud.baowang.agent.api.vo.user.AgentComprehensiveReportVO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.service.AgentDepositSiteRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentDepositSubordinatesApiImpl implements AgentDepositSubordinatesApi {

    private final AgentDepositSiteRecordService agentDepositSubordinatesService;


    @Override
    public GetAgentDepositAmountByAgentVO getAgentDepositAmountByAgent(String siteCode, String agentAccount, String userAccount, Long startTime, Long endTime) {

        LambdaQueryChainWrapper<AgentDepositSubordinatesPO> chainWrapper = agentDepositSubordinatesService.lambdaQuery();
        chainWrapper.eq(AgentDepositSubordinatesPO::getSiteCode, siteCode).eq(AgentDepositSubordinatesPO::getAgentAccount, agentAccount);
        chainWrapper.eq(StrUtil.isNotEmpty(userAccount), AgentDepositSubordinatesPO::getUserAccount, userAccount);
        if (null != startTime) {
            chainWrapper.ge(AgentDepositSubordinatesPO::getDepositTime, startTime);
        }
        if (null != endTime) {
            chainWrapper.le(AgentDepositSubordinatesPO::getDepositTime, endTime);
        }
        List<AgentDepositSubordinatesPO> list = chainWrapper.list();

        Map<String, BigDecimal> agentDepositAmountMap = list.stream()
                .collect(Collectors.groupingBy(AgentDepositSubordinatesPO::getUserAccount, Collectors.reducing(BigDecimal.ZERO, AgentDepositSubordinatesPO::getAmount, BigDecimal::add)));
        Map<String, Long> transAgentTime = list.stream()
                .collect(Collectors.groupingBy(AgentDepositSubordinatesPO::getUserAccount, Collectors.counting()));

        return GetAgentDepositAmountByAgentVO.builder().agentDepositAmountMap(agentDepositAmountMap).transAgentTime(transAgentTime).build();
    }

    @Override
    public List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserAccount(String siteCode, String userAccount) {
        return agentDepositSubordinatesService.getAgentDepositAmountByUserAccount(siteCode,userAccount);
    }

    @Override
    public List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserId(String userId){
        return agentDepositSubordinatesService.getAgentDepositAmountByUserId(userId);
    }

    @Override
    public AgentDepositOfSubordinatesResVO getAgentDepositAmountByOderNo(String orderNo) {

        return agentDepositSubordinatesService.getAgentDepositAmountByOderNo(orderNo);
    }

    @Override
    public ResponseVO<Map<String, AgentStoredMemberVO>> getAgentDepositSum(AgentComprehensiveReportVO vo) {
        List<AgentStoredMemberVO> list =  agentDepositSubordinatesService.getAgentDepositSum(vo);
        return ResponseVO.success(list.stream().collect(Collectors.toMap(obj->obj.getSiteCode()+obj.getDate()+obj.getCurrency(),item->item)));
    }

    @Override
    public Page<AgentDepositOfSubordinatesResVO> listPage(AgentDepositSubordinatesPageReqVo vo) {
        return agentDepositSubordinatesService.listPage(vo);
    }
}

