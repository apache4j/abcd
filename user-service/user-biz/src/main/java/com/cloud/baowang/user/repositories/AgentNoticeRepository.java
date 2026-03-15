package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.po.UserNoticeConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentNoticeRepository extends BaseMapper<UserNoticeConfigPO> {

    Page<UserNoticeRespVO> getAgentNoticeList(@Param("page") Page page,
                                              @Param("vo") AgentNoticeVO agentNoticeVO);



    Integer getUnreadCountForAgentNotice(@Param("noticeType") Integer noticeType,
                                         @Param("agentAccount") String agentAccount);





}
