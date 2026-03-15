package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.*;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailParam;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailResponseVO;
import com.cloud.baowang.user.api.vo.agent.*;
import com.cloud.baowang.user.api.vo.freegame.GetUserInfoCurrencyReqVO;
import com.cloud.baowang.user.api.vo.freegame.GetUserInfoCurrencyRespVO;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.reponse.GetDirectUserListByAgentAndTimeResponse;
import com.cloud.baowang.user.api.vo.user.reponse.GetDirectUserListByAgentAndTimeVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserVIPFlowRecordVO;
import com.cloud.baowang.user.api.vo.user.request.EditRemarkParam;
import com.cloud.baowang.user.api.vo.user.request.ManuBlockVO;
import com.cloud.baowang.user.api.vo.user.request.UserIdPageVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.user.api.vo.userTeam.UserTeamVO;
import com.cloud.baowang.user.api.vo.vip.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteUserInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC会员信息服务")
public interface UserInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/userInfo/api/";

    @Operation(summary = "根据会员ID查询会员信息")
    @PostMapping(value = PREFIX + "getByUserId")
    UserInfoVO getByUserId(@RequestParam("userid") String userId);

    @Operation(summary = "根据会员IDs 批量查询会员信息")
    @PostMapping(value = PREFIX + "getByUserIds")
    List<UserInfoVO> getByUserIds(@RequestBody List<String> userIds, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据会员账号批量查询会员信息")
    @PostMapping(value = PREFIX + "getByUserAccounts")
    List<UserInfoVO> getByUserAccounts(@RequestBody List<String> userAccounts, @RequestParam("siteCode") String siteCode);


    @Operation(summary = "会员列表-分页查询")
    @PostMapping(value = PREFIX + "getPage")
    ResponseVO<Page<UserInfoResponseVO>> getPage(@RequestBody UserInfoPageVO vo);

    @Operation(summary = "会员列表-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody UserInfoPageVO vo);

    @Operation(summary = "根据会员账号查询备注信息")
    @PostMapping(value = PREFIX + "queryUserRemark")
    ResponseVO<Page<UserRemarkVO>> queryUserRemark(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "修改备注信息")
    @PostMapping(value = PREFIX + "updateRemarkHistory")
    ResponseVO<Boolean> updateRemarkHistory(@RequestBody UserRemarkRequestVO requestVO);

    @Operation(summary = "根据会员账号查询基本信息")
    @PostMapping(value = PREFIX + "queryBasicUser")
    ResponseVO<UserBasicVO> queryBasicUser(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "根据会员账号或者注册查询会员信息")
    @PostMapping(value = PREFIX + "getUserInfoVOByAccountOrRegister")
    UserInfoVO getUserInfoVOByAccountOrRegister(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "根据条件查询会员信息")
    @PostMapping(value = PREFIX + "getUserInfoVO")
    UserInfoVO getUserInfoVO(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "根据注册信息查询会员")
    @PostMapping(value = PREFIX + "getUserInfoList")
    ResponseVO<List<UserInfoQueryVO>> getUserInfoList(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "根据会员账号查询登录日志")
    @PostMapping(value = PREFIX + "queryUserLoginInfo")
    ResponseVO<Page<UserLoginInfoVO>> queryUserLoginInfo(@RequestBody UserBasicRequestVO requestVO);

    @Operation(summary = "根据风控黑名单获取会员数量")
    @PostMapping(value = PREFIX + "getCountByBlackAccount")
    ResponseVO<Long> getCountByBlackAccount(@RequestBody RiskUserBlackAccountReqVO requestVO);

    @Operation(summary = "根据风控黑名单获取会员账号")
    @PostMapping(value = PREFIX + "getAllAccountByRiskBlack")
    ResponseVO<List<String>> getAllAccountByRiskBlack(@RequestBody RiskUserBlackAccountReqVO requestVO);

    @Operation(summary = "根据风控黑名单获取会员ID")
    @PostMapping(value = PREFIX + "getAllUserIdByRiskBlack")
    ResponseVO<List<UserInfoVO>> getAllUserIdByRiskBlack(@RequestBody RiskUserBlackAccountReqVO requestVO);


    @Operation(summary = "根据风控黑名单获取会员信息")
    @PostMapping(PREFIX + "getRiskUserBlackListPage")
    ResponseVO<Page<RiskUserBlackAccountVO>> getRiskUserBlackListPage(@RequestBody RiskUserBlackAccountReqVO reqVO);

    // 账变记录可以调用此方法，来获取会员信息
    @Operation(summary = "根据会员账号查询基本信息")
    @PostMapping(value = PREFIX + "getUserInfoByAccount")
    UserInfoVO getUserInfoByAccount(@RequestParam("account") String account);

    @Operation(summary = "根据会员账号查询基本信息")
    @PostMapping(value = PREFIX + "getUserInfoByAccountAndSiteCode")
    UserInfoVO getUserInfoByAccountAndSiteCode(@RequestParam("account") String account, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据会员账号查询基本信息")
    @PostMapping(value = PREFIX + "getUserInfoByUserId")
    UserInfoVO getUserInfoByUserId(@RequestParam("userId") String userId);

    @Operation(summary = "根据会员账号查询基本信息")
    @PostMapping(value = PREFIX + "getUserInfoByAccountNoStatusName")
    ResponseVO<UserInfoVO> getUserInfoByAccountNoStatusName(@RequestParam("account") String account);

    @Operation(summary = "根据id更新本信息")
    @PostMapping(value = PREFIX + "updateUserInfoById")
    ResponseVO<Boolean> updateUserInfoById(@RequestBody UserInfoEditVO editVO);

    @Operation(summary = "根据id更新个人信息")
    @PostMapping(value = PREFIX + "updateUserPersonInfoById")
    ResponseVO<Boolean> updateUserPersonInfoById(@RequestBody UserInfoPersonReqVO editVO);

    @Operation(summary = "根据会员姓名 查询会员账号")
    @PostMapping(value = PREFIX + "getUserAccountByName")
    List<String> getUserAccountByName(@RequestParam("userName") String userName, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据会员注册信息 查询基本信息")
    @PostMapping(value = PREFIX + "getByUserRegister")
    GetByUserAccountVO getByUserRegister(@RequestParam("userRegister") String userRegister);

    @Operation(summary = "根据上级代理ids 查询会员数量")
    @PostMapping(value = PREFIX + "getCountByAgentIds")
    Long getCountByAgentIds(@RequestBody List<String> agentIds);

    @Operation(summary = "根据上级代理ids 查询会员")
    @PostMapping(value = PREFIX + "getUserInfoByAgentIds")
    List<GetUserInfoByAgentIdsVO> getUserInfoByAgentIds(@RequestBody List<String> agentIds);

    @Operation(summary = "根据上级代理id 查询会员")
    @GetMapping(value = PREFIX + "getUserInfoByAgentId")
    List<UserInfoVO> getUserInfoByAgentId(@RequestParam("agentId") String agentId);


    @Operation(summary = "根据userId 查询基本信息")
    @PostMapping(value = PREFIX + "getByUserInfoId")
    GetByUserAccountVO getByUserInfoId(@RequestParam("userId") String userId);

    @Operation(summary = "根据userAccount与siteCode 查询基本信息")
    @PostMapping(value = PREFIX + "getByUserAccountAndSiteCode")
    GetByUserAccountVO getByUserAccountAndSiteCode(@RequestParam("userAccount") String userAccount, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据userAccount与siteCode查询vo信息，包含主键，风控等")
    @GetMapping(value = PREFIX + "getUserByUserAccountAndSiteCode")
    UserInfoVO getUserByUserAccountAndSiteCode(@RequestParam("userAccount") String userAccount, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据userAccount 更新首存时间、首存金额")
    @PostMapping(value = PREFIX + "updateByUserAccount")
    Boolean updateByUserAccount(@RequestParam("userAccount") String userAccount, @RequestParam("firstDepositAmount") BigDecimal firstDepositAmount);


    //@Operation(summary = "根据会员账号集合查询基本信息")
    //@PostMapping(value = PREFIX + "getUserInfoByAccountList")
    //List<UserInfoVO> getUserInfoByAccountList(@RequestBody UserAccountListVO vo);

    @Operation(summary = "根据会员账号集合查询基本信息")
    @PostMapping(value = PREFIX + "getUserInfoByUserIdsList")
    List<UserInfoVO> getUserInfoByUserIdsList(@RequestBody UserAccountListVO vo);


    /*@Operation(summary = "查询会员最后一次登录信息--批量")
    @PostMapping(value = PREFIX + "getLatestLoginInfoByAccountList")
    List<UserLoginInfoVO> getLatestLoginInfoByAccountList(@RequestBody UserAccountListVO vo);*/


    @Operation(summary = "查询会员最后一次登录信息用户id--批量")
    @PostMapping(value = PREFIX + "getLatestLoginInfoByUserIds")
    List<UserLoginInfoVO> getLatestLoginInfoByUserIds(@RequestBody List<String> userIds);

    @Operation(summary = "查询会员最后一次登录信息")
    @PostMapping(value = PREFIX + "getLatestLoginInfoByUserId")
    UserLoginInfoVO getLatestLoginInfoByUserId(@RequestParam("userId") String userId);


    @Operation(summary = "更新会员上级代理信息")
    @PostMapping(value = "/updateAgentTransferInfo")
    ResponseVO updateAgentTransferInfo(@RequestParam("userAccount") String userAccount, @RequestParam("agentId") String agentId, @RequestParam("agentAccount") String agentAccount, @RequestParam("isUpdateTransTime") Boolean isUpdateTransTime);

    @Operation(summary = "更新会员上级代理信息")
    @PostMapping(value = "/updateAgentTransferInfoBySiteCode")
    ResponseVO updateAgentTransferInfoBySiteCode(@RequestParam("siteCode") String siteCode, @RequestParam("userAccount") String userAccount, @RequestParam("agentId") String agentId, @RequestParam("agentAccount") String agentAccount, @RequestParam("isUpdateTransTime") Boolean isUpdateTransTime);

    // todo
    /*@Operation(summary = "根据会员账号数组查询基本信息数组")
    @PostMapping(value = "/getTeamUserInfo")
    AgentTeamVO getTeamUserInfo(final String agentId, final List<String> allDownAgentNum);*/

    @Operation(summary = "根据会员账号查询基本信息-单一钱包")
    @PostMapping(value = PREFIX + "queryUserInfoByAccount")
    UserInfoVO queryUserInfoByAccount(@RequestParam("account") String account);

    @Operation(summary = "根据会员id 更新首存时间、首存金额")
    @PostMapping(value = PREFIX + "updateByUserId")
    Boolean updateByUserId(@RequestParam("userId") String userId, @RequestParam("firstDepositAmount") BigDecimal firstDepositAmount);

    @Operation(summary = "获取首页信息")
    @PostMapping(value = PREFIX + "getUserBalance")
    ResponseVO<IndexVO> getUserBalance(@RequestParam("userId") String userId, @RequestParam("userAccount") String userAccount, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取会员VIP信息")
    @GetMapping(value = PREFIX + "getUserVipInfo")
    ResponseVO<UserVIPInfoVO> getUserVipInfo();

    @Operation(summary = "获取会员VIP信息")
    @PostMapping (value = PREFIX + "getUserVipChianInfo")
    ResponseVO<UserVIPInfoCNVO> getUserVipChianInfo(@RequestBody UserVIPInfoResVO userVIPInfoResVO);

    @Operation(summary = "获取VIP详细信息")
    @GetMapping(value = PREFIX + "getUserVipDetailInfo")
    ResponseVO<UserVIPDetailInfoVO> getUserVipDetailInfo();

    @Operation(summary = "根据会员账号数组查询基本信息数组")
    @PostMapping(value = PREFIX + "getUserInfoListByAccounts")
    List<UserInfoVO> getUserInfoListByAccounts(@RequestBody List<String> account);

    @Operation(summary = "根据会员账号数组查询基本信息数组")
    @PostMapping(value = PREFIX + "getUserInfoListByAccountsAndSiteCode")
    List<UserInfoVO> getUserInfoListByAccountsAndSiteCode(@RequestParam("userAccounts") List<String> userAccounts,
                                                          @RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取用户信息是否存在")
    @PostMapping(value = PREFIX + "getUserInfoIsExists")
    Boolean getUserInfoIsExists(@RequestParam("agentIds") List<String> agentIds,
                                @RequestParam("userAccount") String userAccount,
                                @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据代理id查询会员数量")
    @PostMapping(value = PREFIX + "getByAgentId")
    Long getByAgentId(@RequestParam("agentId") String agentId);

    @Operation(summary = "指定时间内 代理新增的直属会员数、直属会员首存人数")
    @PostMapping(value = PREFIX + "getDirectUserCountByAgentAndTime")
    GetDirectUserListByAgentAndTimeResponse getDirectUserCountByAgentAndTime(@RequestBody GetDirectUserListByAgentAndTimeVO vo);


    @Operation(summary = "代理客户端-新注册人数 按天统计")
    @PostMapping(value = PREFIX + "getRegisterStatisticsByAgentId")
    List<GetRegisterStatisticsByAgentIdVO> getRegisterStatisticsByAgentId(@RequestParam("start") Long start,
                                                                          @RequestParam("end") Long end,
                                                                          @RequestParam("agentId") String agentId,
                                                                          @RequestParam("dbZone") String dbZone
    );

    @Operation(summary = "代理客户端-首存人数 按天统计")
    @PostMapping(value = PREFIX + "getFirstDepositStatisticsByAgentId")
    List<GetFirstDepositStatisticsByAgentIdVO> getFirstDepositStatisticsByAgentId(@RequestParam("start") Long start,
                                                                                  @RequestParam("end") Long end,
                                                                                  @RequestParam("agentId") String agentId,
                                                                                  @RequestParam("dbZone") String dbZone
    );

    @Operation(summary = "代理客户端-会员详情")
    @PostMapping(value = PREFIX + "selectUserDetail")
    ResponseVO<SelectUserDetailResponseVO> selectUserDetail(@RequestBody SelectUserDetailParam vo);

    @Operation(summary = "代理客户端-会员详情")
    @PostMapping(value = PREFIX + "subordinateUserList")
    ResponseVO<Page<SubordinateUserListResponseVO>> subordinateUserList(@RequestBody SubordinateUserListParam vo);


    @Operation(summary = "查询用户当前经验")
    @PostMapping(value = PREFIX + "getUserVipFlowLastOne")
    ResponseVO<UserVIPFlowRecordVO> getUserVipFlowLastOne(@RequestParam("userAccount") String userAccount,
                                                          @RequestParam("siteCode") String siteCode);

    @Operation(summary = "代理备注编辑")
    @PostMapping(value = PREFIX + "agentEditRemark")
    ResponseVO<?> agentEditRemark(@RequestBody EditRemarkParam vo);

    @PostMapping(value = PREFIX + "getByMail")
    UserInfoVO getByMail(@RequestParam("email") String email);

    @PostMapping(value = PREFIX + "getByPhone")
    UserInfoVO getByPhone(@RequestParam("phone") String phone);

    @PostMapping(value = PREFIX + "getIndexInfo")
    ResponseVO<UserIndexInfoVO> getIndexInfo(@RequestParam("userId") String userId,
                                             @RequestParam("userAccount") String userAccount,
                                             @RequestParam("siteCode") String siteCode,
                                             @RequestParam("timezone") String timezone);

    @PostMapping(value = PREFIX + "getUserInfoByQueryVO")
    UserInfoVO getUserInfoByQueryVO(@RequestBody UserQueryVO userQueryVO);

    @GetMapping(PREFIX + "getUserVipBenefitDetail")
    @Operation(summary = "获取VIP等级制度")
    ResponseVO<SiteVIPSystemVO> getUserVipBenefitDetail();

    @GetMapping(PREFIX + "更新会员次存")
    @Operation(summary = "updateSecondDeposit")
    Boolean updateSecondDeposit(@RequestParam("userId") String userId, @RequestParam("arriveAmount") BigDecimal arriveAmount);

    /**
     * 查询用户注册到现在的天数
     *
     * @param userId userId
     * @return 天数
     */
    @GetMapping(PREFIX + "查询用户注册到现在的天数")
    @Operation(summary = "getUserRegisterDay")
    Long getUserRegisterDay(@RequestParam("userId") String userId);

    /**
     * 查询用户vip信息
     *
     * @param userIds userIds
     * @return 天数
     */
    @Operation(summary = "查询用户vip信息")
    @PostMapping(PREFIX + "getUserInfoVip")
    List<UserInfoVIPVO> getUserInfoVip(@RequestBody List<String> userIds, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "批量查询信息")
    @PostMapping(PREFIX + "getUserInfoByUserIds")
    List<UserInfoVO> getUserInfoByUserIds(@RequestBody List<String> userIds);

    @Operation(summary = "批量获取当前站点下会员信息")
    @PostMapping(PREFIX + "getUserBalanceBySiteCodeUserAccount")
    List<UserInfoVO> getUserBalanceBySiteCodeUserAccount(@RequestParam("siteCode") String siteCode, @RequestBody List<String> userAccounts);

    @Operation(summary = "增加累计登录天数")
    @PostMapping(PREFIX + "incOnLineDay")
    Boolean incOnLineDay(@RequestParam("userId") String userId);

    @PostMapping(PREFIX + "getUsedAvatarList")
    @Operation(summary = "查看当前站点有哪些头像被会员使用过,返回头像id")
    List<String> getUsedAvatarList(@RequestBody List<String> avatarIds, @RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX + "getUserLanguage")
    @Operation(summary = "获取用户语言")
    UserSystemMessageConfigVO getUserLanguage(@Validated @RequestBody UserLanguageVO vo);

    @PostMapping(PREFIX + "updateUserLanguage")
    @Operation(summary = "设置用户语言")
    UserLanguageVO updateUserLanguage(@RequestBody UserLanguageVO vo);

    @GetMapping(PREFIX + "getUserListGroupSiteCode")
    @Operation(summary = "站点,币种,会员分组")
    Map<String, Map<String, List<UserInfoVO>>> getUserListGroupSiteCode(@RequestParam("startTime") Long startTime,
                                                                        @RequestParam("endTime") Long endTime,
                                                                        @RequestParam(value = "siteCode", required = false) String siteCode);

    @PostMapping(PREFIX + "getUserListByParam")
    @Operation(summary = "代理端-根据条件查询userList")
    List<UserInfoVO> getUserListByParam(@RequestBody UserAgentQueryUserVO queryVO);

    @PostMapping(PREFIX + "getUnLabelByUserIds")
    @Operation(summary = "不满足标签的会员ID")
    List<String> getUnLabelByUserIds(@RequestParam("labelId") String labelId,
                                     @RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX + "getUnBenefitByUserIds")
    @Operation(summary = "不满足标签的会员ID")
    List<String> getUnBenefitByUserIds(@RequestParam("awardCode") String awardCode,
                                       @RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX + "getUserIdListPage")
    @Operation(summary = "获取userAccount分页数据")
    Page<String> getUserIdListPage(@RequestBody UserIdPageVO userIdPageVO);


    @Operation(summary = "会员列表-根据时间范围查询注册会员或者首存会员")
    @PostMapping(value = PREFIX + "listPage")
    Page<UserInfoResponseVO> listPage(@RequestBody UserInfoPageVO vo);


    @Operation(summary = "所有代理下级用户数量")
    @PostMapping(value = PREFIX + "findAgentSubLineUserNum")
    List<UAgentSubLineUserResVO> findAgentSubLineUserNum(@RequestBody UserAgentSubLineReqVO reqVO);

    @Operation(summary = "所有代理下级用户首充信息")
    @PostMapping(value = PREFIX + "findAgentSubLineUserFirstDeposit")
    List<UAgentSubLineUserResVO> findAgentSubLineUserFirstDeposit(@RequestBody UserAgentSubLineReqVO reqVO);


    @Operation(summary = "代理所属下级会员数")
    @PostMapping(value = PREFIX + "getUserCountGroupByAgentId")
    Map<String, Long> getUserCountGroupByAgentId(@RequestBody List<String> downAgentIds);

    @Operation(summary = "分组查询截止到某个时间的站点对应的会员,根据币种分组")
    @PostMapping(value = PREFIX + "getSiteCurrencyUserList")
    List<SiteUserDateQueryVO> getSiteCurrencyUserList(@RequestBody Map<String, List<String>> dateSiteStrMap, @RequestParam(value = "currencyCode", required = false) String currencyCode);

    @Operation(summary = "手动归集(ETH,TRC)")
    @PostMapping(value = PREFIX + "manuCollection")
    ResponseVO<?> manuCollection(@RequestBody ManuBlockVO manuBlockVO);

    @Operation(summary = "用户ip限定")
    @PostMapping(value = PREFIX + "checkUserIpMax")
    Boolean checkUserIpMax(@RequestParam("maxCount") Integer maxCount, @RequestParam("ip") String ip,
                           @RequestParam("siteCode") String siteCode);

    @Operation(summary = "checkUserCurrency")
    @PostMapping(value = PREFIX + "checkUserCurrency")
    ResponseVO<GetUserInfoCurrencyRespVO> checkUserCurrency(@RequestBody GetUserInfoCurrencyReqVO req);

    @Operation(summary = "根据会员账号数组查询基本信息数组")
    @PostMapping(value = "/getTeamUserInfo")
    UserTeamVO getTeamUserInfo(@RequestBody GetTeamUserInfoParam param);

    @PostMapping(PREFIX + "authVerify")
    @Operation(summary = "存款实名认证")
    ResponseVO<ResultCode> authVerify(@RequestParam("userId") String userId,
                                      @RequestParam("userName") String userName,
                                      @RequestParam("areaCode") String areaCode,
                                      @RequestParam("phone") String phone,
                                      @RequestParam("birthday") String birthday);


    @Operation(summary = "过滤#不返水标签")
    @PostMapping(PREFIX + "filterNoRebateUserIds")
    List<String> filterNoRebateUserIds(@RequestBody List<String> userIds, @RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取会员列表，排序大于小最值分页")
    @PostMapping(PREFIX + "getUserInfoListByMinId")
    public List<UserInfoVO> getUserInfoListByMinId(UserInfoReqVO userInfoReqVO);

}


