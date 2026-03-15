package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelReqVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageResultVo;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoResponseVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.list.GetAllListVO;
import com.cloud.baowang.agent.api.vo.label.AgentInfoLabelCountVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantAgentCountVO;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawWayVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代理基本信息 Mapper 接口
 *
 * @author kimi
 * @since 2023-10-10
 */
@Mapper
public interface AgentInfoRepository extends BaseMapper<AgentInfoPO> {

    AgentInfoPO findAgentInfoNotCase(@Param("siteCode") String siteCode,@Param("agentAccount") String agentAccount);

    List<String> findAgentListByParam(@Param("siteCode") String siteCode,@Param("agentAccount") String agentAccount);

    List<GetAllListVO> findAllAgentInfoByIds(@Param("ids") List<String> ids);

    List<GetAllListVO> findAllAgentInfoById(@Param("id") String id);

    List<ShortUrlChangeRecordPageVO> shortRecordPageList(@Param("vo") ShortUrlChangeRecordQueryVO vo);

    List<GetAllListVO> getAllList(String siteCode);

    Page<AgentInfoResponseVO> getAgentPage(Page<AgentInfoResponseVO> page, @Param("vo") AgentInfoPageVO vo);


    List<AgentInfoResponseVO> getTotalPage(@Param("vo") AgentInfoPageVO vo);


    Long getTotalCount(@Param("vo") AgentInfoPageVO vo);

    List<GetAllListVO> findByParentId(@Param("parentIds") List<String> parentIds);

    List<AgentInfoLabelCountVO> agentLabelCount(@Param("ids")List<String> ids);

    List<AgentInfoVO> findAllDirectChildAgents(@Param("agentIds") List<String> agentIds);

    List<AgentInfoVO> findAgentInfoByAgentAccounts(@Param("agentAccounts") List<String> agentAccounts);

    AgentInfoVO findByIdSelf(@Param("agentId") String agentId);

    Page<AgentInfoVO> findAllChildAgentsByPage(Page<Object> page, @Param("vo")AgentLowerLevelReqVO vo, @Param("containsSelf") boolean containsSelf);

   // List<AgentSubLineUserResVO> findAgentSubLineUserNum(@Param("vo") AgentSubLineReqVO reqVO);

    List<AgentInfoPO> findAgentInfoTree(@Param("agentId") String agentId);

    Long getAgentLabelCount(@Param("labelId") String labelId);

    Page<AgentInfoPO> findInSetPageByLabelId(Page<AgentInfoPO> page, @Param("siteCode") String siteCode, @Param("labelId") String labelId);

    AgentInfoPO selectByAgentId(@Param("agentId")  String agentId);

    List<String> getSubAgentIdList(@Param("agentId")  String agentId);

    List<String> getSubAgentIdDirectReportList(@Param("agentId")  String agentId);

    AgentInfoVO getAgentBenefit(@Param("userId") String userId);

    Long selectLabelUseCount(@Param("labelId") String id);

    List<MerchantAgentCountVO> getCountGroupByMerchant(@Param("merchantAccountList") List<String> merchantAccountList,
                                                       @Param("siteCode")String siteCode);

    Long selectAgentCount(@Param("siteCode") String siteCode, @Param("merchantAccount") String merchantAccount);

    Page<AgentInfoPO> pageQuery(Page<AgentInfoPO> page, @Param("param") AgentShortUrlManagerPageQueryVO queryVO);


    Long selectAgentTeamNumMerchant(@Param("siteCode") String siteCode, @Param("merchantAccount") String merchantAccount);

    List<AgentWithdrawWayVO> queryWithdrawWay(@Param("siteCode") String siteCode);

    Page<AgentInfoPageResultVo> listPage(Page<AgentInfoPageResultVo> page,@Param("vo") AgentInfoPageVO vo);

    List<AgentInfoVO> getAgentBenefitList(@Param("userId") List<String> userId);


}
