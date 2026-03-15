package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.ReportUserTransferRespVO;
import com.cloud.baowang.agent.po.UserTransferAgentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 会员转代审核表 Mapper 接口
 * </p>
 */
@Mapper
public interface UserTransferAgentRepository extends BaseMapper<UserTransferAgentPO> {

    Page<UserTransferAgentPO> getReviewPage(Page<MemberTransferReviewPageReqVO> page,
                                            @Param("vo") MemberTransferReviewPageReqVO vo,
                                            @Param("adminName") String adminName);

    List<String> selectTransferAgent(@Param("agentAccounts") List<String> agentAccounts,
                                     @Param("startTime") long startTime, @Param("endTime") long endTime);


    List<ReportUserTransferRespVO> queryUserTransferCount(@Param("siteCode") String siteCode,
                                                          @Param("startTime") long startTime,
                                                          @Param("endTime") long endTime);

    //queryUserTransferCountAllPlatform
    List<ReportUserTransferRespVO> queryUserTransferCountAllPlatform(
            @Param("startTime") long startTime,
            @Param("endTime") long endTime);
}
