package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.PlatformAdjustCollectVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserDepositWithdrawalReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserMemberAdjustmentsVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserMemberBettingVO;
import com.cloud.baowang.report.po.ReportMembershipStatsPO;
import com.cloud.baowang.report.api.vo.user.complex.dto.AgentRegInfoVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserFirstDepositInfoVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserLoginReportVO;
import com.cloud.baowang.report.api.vo.user.complex.dto.UserRegInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
@Mapper
public interface MembershipStatsRepository extends BaseMapper<ReportMembershipStatsPO> {



    //会员注册
    List<UserRegInfoVO> getRegSumInfo(@Param("vo") ReportComprehensiveReportVO vo);
    //代理注册
    List<AgentRegInfoVO> getAgentRegSumInfo(@Param("vo") ReportComprehensiveReportVO vo);
    //会员登陆
    List<UserLoginReportVO> getLoginSumInfo(@Param("vo") ReportComprehensiveReportVO vo);

    //会员首存
    List<UserFirstDepositInfoVO> getUserFirstDepositInfo(@Param("vo") ReportComprehensiveReportVO vo);

    List<UserMemberBettingVO> getUserBettingInfo(@Param("vo") ReportComprehensiveReportVO vo);
    List<AgentStoredMemberVO> getAgentDepositSum(@Param("vo") ReportComprehensiveReportVO vo);

    List<UserMemberAdjustmentsVO> getUserAdjustments(@Param("vo") ReportComprehensiveReportVO vo);
    //会员存取款
    List<UserDepositWithdrawalReportVO> getUserDepositWithdrawInfo(@Param("vo") ReportComprehensiveReportVO vo);

    //上下分
    List<PlatformAdjustCollectVO> getPlatformAdjustInfo(@Param("vo") ReportComprehensiveReportVO vo);
}
