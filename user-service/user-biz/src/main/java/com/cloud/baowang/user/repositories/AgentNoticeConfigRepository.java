package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.po.UserNoticeConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentNoticeConfigRepository extends BaseMapper<UserNoticeConfigPO> {

    /**
     * 未读的消息，是属于配置的公告
     */
    Page<UserNoticeRespVO> getUserNoticeList(@Param("page") Page page,
                                             @Param("vo") AgentNoticeVO agentNoticeVO);



    /**
     * getAgentNoticeFirst
     */
    UserNoticeRespVO getAgentNoticeFirst(@Param("vo") AgentNoticeVO agentNoticeVO);

    /**
     * 所有未读的消息，是属于配置的公告，配置对象是全部代理
     */
    List<UserNoticeRespVO> getUserAllNotReadNoticeList(@Param("vo") AgentNoticeVO agentNoticeVO);


    /**
     * 所有未读的消息，是属于配置的公告，配置对象是特定代理
     */
    //List<UserNoticeRespVO> getUserAllNotReadNoticeTargetId(@Param("vo") AgentNoticeVO agentNoticeVO);

    /**
     * 所有未读的消息，为删除查询数据，查询该账号不属于公告的
     */
    List<UserNoticeRespVO> getUserAllNotReadForDeleteNoticeList(@Param("vo") AgentNoticeVO agentNoticeVO);


    Page<UserNoticeRespVO> getAgentNoticeListNew(@Param("page") Page page,
                                                 @Param("vo") AgentNoticeVO agentNoticeVO);
    /**
     * 所有未读的消息，是属于配置的公告
     */
    List<UserNoticeRespVO> getAgentAllNotReadNoticeList(@Param("vo") AgentNoticeVO agentNoticeVO);


    /**
     * 所有未读的消息，查询公告，消息
     */
    Integer getUnreadCountForUserNotice1(@Param("noticeType") Integer noticeType,
                                         @Param("agentId") String agentId,
                                         @Param("currentTime") Long currentTime);


    Integer getUnreadCountForUserNotice3(@Param("noticeType") Integer noticeType,
                                         @Param("agentAccount") String agentAccount,
                                         @Param("deviceTerminal") String deviceTerminal);


    Integer getUnreadCountForUserNotice2(@Param("noticeType") Integer noticeType,
                                         @Param("agentAccount") String agentAccount,
                                         @Param("currentTime") Long currentTime);


    Integer getUnreadCountForAgentNotice(@Param("noticeType") Integer noticeType,
                                         @Param("agentAccount") String agentAccount);

    /**
     * 弹窗
     * @param vo
     * @return
     */
    List<UserNoticeRespVO> getForceAgentNoticeHeadList(@Param("vo") UserNoticeHeadReqVO vo);

    /**
     * 跑马灯
     * @param vo
     * @return
     */

    List<UserNoticeRespVO> getAgentHeadNoticeList(@Param("vo") UserNoticeHeadReqVO vo);
}
