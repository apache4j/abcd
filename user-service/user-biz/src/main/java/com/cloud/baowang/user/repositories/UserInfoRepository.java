package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentAllDownUserVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.system.api.enums.RiskBlackTypeEnum;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountReqVO;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import  com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserCheckExistReqVO;
import com.cloud.baowang.user.api.vo.agent.*;
import com.cloud.baowang.user.api.vo.site.GetFirstDepositStatisticsBySiteCodeVO;
import com.cloud.baowang.user.api.vo.site.GetRegisterStatisticsBySiteCodeVO;
import com.cloud.baowang.user.api.vo.site.UserDataOverviewResVo;
import com.cloud.baowang.user.api.vo.site.UserDataOverviewRespVo;
import com.cloud.baowang.user.api.vo.user.SiteUserDateQueryVO;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
import com.cloud.baowang.user.api.vo.user.UserInfoQueryVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigVO;
import com.cloud.baowang.user.po.UserInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 会员基本信息 Mapper 接口
 *
 * @author kimi
 * @since 2024-03-20 10:00:00
 */
@Mapper
public interface UserInfoRepository extends BaseMapper<UserInfoPO> {

    Page<UserInfoResponseVO> getPage(Page<UserInfoResponseVO> page, @Param("vo") UserInfoPageVO vo);

    Long getTotalCount(@Param("vo") UserInfoPageVO vo);

    UserInfoVO findUserInfoNotCase(@Param("userAccount") String userAccount, @Param("siteCode") String siteCode);

    UserInfoVO findUserReviewExist(@Param("vo") UserCheckExistReqVO vo);

    List<UserInfoQueryVO> getUserInfoList(@Param("userRegister") String userRegister);


    List<UserLabelConfigVO> getUserLabelConfigListList(@Param("labelIds") List<String> labelIds, @Param("siteCode") String siteCode);

    Page<UserLabelConfigVO> getUserLabelConfig(Page<UserLabelConfigVO> page, @Param("labelId") String labelId,@Param("siteCode")String siteCode);
    List<String> getAllUserIdByLabelConfig(@Param("siteCode") String siteCode, @Param("labelId") String labelId);

    /**
     * 返回首存人数与首存金额
     */
    List<AgentAllDownUserVO> queryTeamUserInfo(@Param("vo") AgentUserTeamParam param);

    List<String> selectAddUser(@Param("vo") AgentUserTeamParam param);

    List<String> queryUserDepositVIPUpgrade(
            @Param("vipUpgradeStartTime") long vipUpgradeStartTime,
            @Param("vipUpgradeEndTime") long vipUpgradeEndTime,
            @Param("nowStartTime") long nowStartTime,
            @Param("nowEndTime") long nowEndTime);

    List<GetRegisterStatisticsByAgentIdVO> getRegisterStatisticsByAgentId(@Param("start") Long start,
                                                                          @Param("end") Long end,
                                                                          @Param("agentId") String agentId,
                                                                          @Param("dbZone")String dbZone
                                                                          );

    List<GetRegisterStatisticsBySiteCodeVO> getRegisterStatisticsBySiteCode(@Param("start") Long start,
                                                                            @Param("end") Long end,
                                                                            @Param("siteCode") String siteCode,
                                                                            @Param("dbZone")String dbZone
    );



    List<GetFirstDepositStatisticsByAgentIdVO> getFirstDepositStatisticsByAgentId(@Param("start") Long start,
                                                                                  @Param("end") Long end,
                                                                                  @Param("agentId") String agentId,
                                                                                  @Param("dbZone")String dbZone
                                                                                  );

    List<GetFirstDepositStatisticsBySiteCodeVO> getFirstDepositStatisticsBySiteCode(@Param("start") Long start,
                                                                                    @Param("end") Long end,
                                                                                    @Param("siteCode") String siteCode,
                                                                                    @Param("dbZone")String dbZone
    );


    void updateOfflineDaysTask(@Param("currentTime") Long currentTime,@Param("siteCode")String siteCode);

    String isExistsUserId(@Param("userId") String userId);



    Set<String> isExistsUserIdList(@Param("userIdList") Set<String> userIdList);



    /**
     * 新增会员：时间区间内注册的会员数量
     * 新增充值会员：时间区间内注册且有任意充值成功行为的用户数量
     */
    UserDataOverviewRespVo getSiteStatistics(@Param("siteCode") String siteCode, @Param("startTime") Long startTime,@Param("endTime") Long endTime);


    /**
     * 登录会员
     */
    Integer getSiteUserLastLongin(@Param("siteCode") String siteCode, @Param("startTime") Long startTime,@Param("endTime") Long endTime);
    /**
     * 首存人数
     */
    Integer getFirstDepositNumber(@Param("vo") UserDataOverviewResVo vo);

    Integer getCountByFriendInviteCodeAndSiteCode(@Param("siteCode") String siteCode,
                                                  @Param("friendInviteCode") String friendInviteCode);



    List<String> getAllUserIds(@Param("siteCode") String siteCode);


    Long countInviteeChargeNum(@Param("userId")  String userId,@Param("siteCode")  String siteCode);



    AgentInfoVO selectAgentBenefit(@Param("userId") String userId);

    List<String> getUnLabelByUserIds(@Param("labelId") String labelId, @Param("siteCode") String siteCode);

    List<String> getUnBenefitByUserIds(@Param("awardCode") String awardCode, @Param("siteCode") String siteCode);

    List<UAgentSubLineUserResVO> findAgentSubLineUserFirstDeposit(@Param("param") UserAgentSubLineReqVO reqVO);

    List<UAgentSubLineUserResVO> findAgentSubLineUserNum(@Param("param") UserAgentSubLineReqVO reqVO);

    List<UAgentSubLineUserResVO> getUserCountGroupByAgentId(@Param("agentIds")List<String> downAgentIds);

    List<SiteUserDateQueryVO> getSiteCurrencyUserList(@Param("siteCodes") List<String> siteCodes,@Param("currencyCode")String currencyCode,@Param("dateStr")String dateStr);

    List<UserInfoPO> selectListByRegIpSegment(@Param("vo")RiskUserBlackAccountReqVO reqVO, boolean needKickOut);

    List<UserInfoPO> selectListByLoginIpSegment(@Param("vo")RiskUserBlackAccountReqVO reqVO, boolean needKickOut);
}
