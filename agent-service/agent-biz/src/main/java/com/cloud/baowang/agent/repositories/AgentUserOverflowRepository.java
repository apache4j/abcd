package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageReqVO;
import com.cloud.baowang.agent.po.AgentUserOverflowPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 会员溢出审核表 Mapper 接口
 * </p>
 *
 */
@Mapper
public interface AgentUserOverflowRepository extends BaseMapper<AgentUserOverflowPO> {

    Page<AgentUserOverflowPO> getReviewPage(Page<MemberOverflowReviewPageReqVO> page,
                                            @Param("vo") MemberOverflowReviewPageReqVO vo,
                                            @Param("adminName") String adminName);

    List<String> selectOverFlowAgent(@Param("siteCode")String siteCode,@Param("agentAccounts") List<String> agentAccount,
                             @Param("startTime") long startTime, @Param("endTime")long endTime);
}
