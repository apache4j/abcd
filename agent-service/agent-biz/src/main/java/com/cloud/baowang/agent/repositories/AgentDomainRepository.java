package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainShortVO;
import com.cloud.baowang.agent.po.AgentDomainPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 域名管理
 */
@Mapper
public interface AgentDomainRepository extends BaseMapper<AgentDomainPO> {

    AgentDomainShortVO getPromotionDomain(@Param("vo") AgentDomainShortVO agentDomainShortVO);

    Page<PromotionDomainRespVO> getPromotionDomainAndSiteDomainPage(Page<PromotionDomainRespVO> page, @Param("param") AgentDomainPageQueryVO queryVO);
}
