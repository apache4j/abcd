package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.PlatformAdjustCollectVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserDepositWithdrawalReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserMemberAdjustmentsVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserMemberBettingVO;
import com.cloud.baowang.report.po.ReportMembershipStatsPO;
import com.cloud.baowang.report.repositories.MembershipStatsRepository;
import com.cloud.baowang.report.api.vo.user.complex.dto.AgentRegInfoVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserFirstDepositInfoVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserLoginReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserRegInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @auther amos
 * @create 2024-11-06
 */
@Slf4j
@Service
@AllArgsConstructor
public class MembershipStatsService extends ServiceImpl<MembershipStatsRepository, ReportMembershipStatsPO> {
    private MembershipStatsRepository repository;

    public Map<String, UserRegInfoVO> getRegSumInfo(ReportComprehensiveReportVO vo){
        List<UserRegInfoVO> list = repository.getRegSumInfo(vo);
        Map<String, UserRegInfoVO> map = list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> item));
        return map;
    }

    public  Map<String, AgentRegInfoVO> getAgentRegSumInfo(ReportComprehensiveReportVO vo){
        List<AgentRegInfoVO> list = repository.getAgentRegSumInfo(vo);
        Map<String, AgentRegInfoVO> map = list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> item));
        return map;
    }

    public Map<String, UserLoginReportVO> getUserLoginSumInfo(ReportComprehensiveReportVO vo){
        List<UserLoginReportVO> list = repository.getLoginSumInfo(vo);
        Map<String, UserLoginReportVO> map = list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> item));
        return map;
    }

    public Map<String, UserFirstDepositInfoVO> getUserFirstDepositSumInfo(ReportComprehensiveReportVO vo){
        List<UserFirstDepositInfoVO> list = repository.getUserFirstDepositInfo(vo);
        Map<String, UserFirstDepositInfoVO> map= list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> item));
        return map;
    }

    public Map<String, UserMemberBettingVO> getUserBetting(ReportComprehensiveReportVO vo){
        List<UserMemberBettingVO> list = repository.getUserBettingInfo(vo);
        Map<String, UserMemberBettingVO> map = list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> item));
        return map;
    }

    public Map<String, AgentStoredMemberVO> getAgentDepositSum(ReportComprehensiveReportVO vo){
        List<AgentStoredMemberVO> list =  repository.getAgentDepositSum(vo);
        return list.stream().collect(Collectors.toMap(obj->obj.getSiteCode()+obj.getDate()+obj.getCurrency(),item->item));
    }

    public Map<String, UserMemberAdjustmentsVO> getUserAdjustments(ReportComprehensiveReportVO vo){
        List<UserMemberAdjustmentsVO> list = repository.getUserAdjustments(vo);
        Map<String, UserMemberAdjustmentsVO> map =  list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> {
            item.setMemberAdjustmentsAmount(item.getMemberAdjustmentsAddAmount().subtract(item.getMemberAdjustmentsReduceAmount()));
            item.setRiskAmount(item.getRiskAddAmount().subtract(item.getRiskReduceAmount()));
            return item;
        }));
        return map;
    }

    public Map<String, UserDepositWithdrawalReportVO> getUserDepositWithdrawInfo(ReportComprehensiveReportVO vo){
        List<UserDepositWithdrawalReportVO> list = repository.getUserDepositWithdrawInfo(vo);
        Map<String, UserDepositWithdrawalReportVO> map = list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> {
            item.setMemberDepositWithdrawalDifference(item.getTotalMemberDeposit().subtract(item.getTotalMemberWithdrawal()));
            return item;
        }));
        return map;
    }

    public Map<String, PlatformAdjustCollectVO> getPlatformAdjustInfo(ReportComprehensiveReportVO vo){
        List<PlatformAdjustCollectVO> list = repository.getPlatformAdjustInfo(vo);
        Map<String, PlatformAdjustCollectVO> map =  list.stream().collect(Collectors.toMap(obj -> obj.getSiteCode() + obj.getDate() + obj.getCurrency(), item -> {
            item.setPlatformTotalAdjust(item.getPlatformAddAmount().subtract(item.getPlatformReduceAmount()));
            return item;
        }));
        return map;
    }
}
