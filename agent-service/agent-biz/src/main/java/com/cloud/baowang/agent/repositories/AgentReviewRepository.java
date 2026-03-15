package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.AgentReviewResponseVO;
import com.cloud.baowang.agent.po.AgentReviewPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理审核表 Mapper 接口
 *
 * @author kimi
 * @since 2023-10-10
 */
@Mapper
public interface AgentReviewRepository extends BaseMapper<AgentReviewPO> {

    Page<AgentReviewResponseVO> getReviewPage(Page<AgentReviewResponseVO> page,
                                             @Param("vo") AgentReviewPageVO vo,
                                             @Param("adminName") String adminName);

    AgentReviewPO getByAgentAccount(@Param("siteCode") String siteCode,@Param("agentAccount") String agentAccount);
}
