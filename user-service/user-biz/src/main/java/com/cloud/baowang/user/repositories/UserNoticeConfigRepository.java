package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeReqVO;
import com.cloud.baowang.user.po.UserNoticeConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserNoticeConfigRepository extends BaseMapper<UserNoticeConfigPO> {
    List<UserNoticeRespVO> getUserHeadNoticeListNew(@Param("vo") UserNoticeHeadReqVO userNoticeHeadReqVO);

    List<UserNoticeRespVO> getForceUserNoticeHeadListNew(@Param("vo") UserNoticeHeadReqVO userNoticeHeadReqVO);

    Page<UserNoticeRespVO> getUserNoticeListNew(@Param("page") Page page,
                                                @Param("vo") UserNoticeReqVO userNoticeReqVO);

    int getUnreadNoticeNums(@Param("userId")String userId, @Param("terminal") Integer terminal);

    int getUnreadNoticeNumSplit(@Param("userId")String userId, @Param("terminal") Integer terminal);
    int getUnreadActivityNumSplit(@Param("userId")String userId, @Param("terminal") Integer terminal);

    void updateUserNoticeStatus(@Param("vo") UserNoticeReqVO userNoticeReqVO);

    Long getUserUnreadNoticeNums(@Param("vo") UserNoticeReqVO userNoticeReqVO);


    List<UserNoticeRespVO> getForceUserNoticeHeadList(@Param("vo") UserNoticeHeadReqVO userNoticeHeadReqVO);

    Page<UserNoticeRespVO> getUserNoticeListForNoUser(@Param("page") Page page,
                                                      @Param("vo") UserNoticeReqVO userNoticeReqVO);

    Page<UserNoticeRespVO> getUserNoticeList(@Param("page") Page page,
                                             @Param("vo") UserNoticeReqVO userNoticeReqVO);

    List<UserNoticeRespVO> getUserNoticeNoReadList(@Param("vo") UserNoticeReqVO userNoticeReqVO);


    Integer getUnreadCountForUserNotice1(@Param("noticeType") Integer noticeType,
                                         @Param("userId") String userId,
                                         @Param("currentTime") Long currentTime);


    Integer getUnreadCountForUserNotice3(@Param("noticeType") Integer noticeType,
                                         @Param("userId") String userId,
                                         @Param("deviceTerminal") String deviceTerminal,
                                         @Param("currentTime") Long currentTime);


    Integer getUnreadCountForUserNotice2(@Param("noticeType") Integer noticeType,
                                         @Param("userAccount") String userAccount,
                                         @Param("currentTime") Long currentTime);

    Integer getUnreadCountForUserNotice4(@Param("noticeType") Integer noticeType,
                                         @Param("userId") String userId);

    Integer getUnreadCountForUserNotice(@Param("noticeType") Integer noticeType,
                                        @Param("userAccount") String userAccount,
                                        @Param("deviceTerminal") String deviceTerminal,
                                        @Param("currentTime") Long currentTime);


    Integer getMaxSortBySiteCode(@Param("siteCode") String siteCode);


}
