package com.cloud.baowang.user.api;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
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
import com.cloud.baowang.user.service.UserCommonService;
import com.cloud.baowang.user.service.UserInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
//@AllArgsConstructor
public class UserInfoApiImpl implements UserInfoApi {

    private final UserInfoService userInfoService;
    private final UserCommonService userCommonService;

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;

    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;

    @Autowired
    public UserInfoApiImpl(UserInfoService userInfoService, UserCommonService userCommonService) {
        this.userInfoService = userInfoService;
        this.userCommonService = userCommonService;
    }

    @Override
    public ResponseVO<Page<UserInfoResponseVO>> getPage(UserInfoPageVO vo) {
        return userInfoService.getPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(UserInfoPageVO vo) {
        return userInfoService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<Page<UserRemarkVO>> queryUserRemark(UserBasicRequestVO requestVO) {
        return userInfoService.queryUserRemark(requestVO);
    }

    @Override
    public ResponseVO<Boolean> updateRemarkHistory(UserRemarkRequestVO requestVO) {
        return userInfoService.updateRemarkHistory(requestVO);
    }

    @Override
    public ResponseVO<UserBasicVO> queryBasicUser(UserBasicRequestVO requestVO) {
        return userInfoService.queryBasicUser(requestVO);
    }

    @Override
    public UserInfoVO getUserInfoVOByAccountOrRegister(UserBasicRequestVO requestVO) {
        return userInfoService.getUserInfoVOByAccountOrRegister(requestVO);
    }

    @Override
    public UserInfoVO getUserInfoVO(UserBasicRequestVO requestVO) {
        return userInfoService.getUserInfoVO(requestVO);
    }

    @Override
    public ResponseVO<List<UserInfoQueryVO>> getUserInfoList(UserBasicRequestVO requestVO) {
        return userInfoService.getUserInfoByUserAccount(requestVO);
    }

    @Override
    public ResponseVO<Page<UserLoginInfoVO>> queryUserLoginInfo(UserBasicRequestVO requestVO) {
        return userInfoService.queryUserLoginInfo(requestVO);
    }

    @Override
    public ResponseVO<Long> getCountByBlackAccount(RiskUserBlackAccountReqVO requestVO) {
        return userInfoService.getCountByBlackAccount(requestVO);
    }

    @Override
    public ResponseVO<List<String>> getAllAccountByRiskBlack(RiskUserBlackAccountReqVO requestVO) {
        return userInfoService.getAllAccountByRiskBlack(requestVO);
    }

    @Override
    public ResponseVO<List<UserInfoVO>> getAllUserIdByRiskBlack(RiskUserBlackAccountReqVO requestVO) {
        return userInfoService.getAllUserIdByRiskBlack(requestVO);
    }

    @Override
    public ResponseVO<Page<RiskUserBlackAccountVO>> getRiskUserBlackListPage(RiskUserBlackAccountReqVO reqVO) {
        return userInfoService.getRiskUserBlackListPage(reqVO);
    }

    @Override
    public UserInfoVO getUserInfoByAccount(String account) {
        return userInfoService.getUserInfoByAccount(account);
    }

    @Override
    public UserInfoVO getUserInfoByAccountAndSiteCode(String account, String siteCode) {
        return userInfoService.getUserInfoByAccountAndSiteCode(account, siteCode);
    }

    @Override
    public UserInfoVO getUserInfoByUserId(String account) {
        return userInfoService.getUserInfoByUserId(account);
    }

    @Override
    public ResponseVO<UserInfoVO> getUserInfoByAccountNoStatusName(String account) {
        return userInfoService.getUserInfoByAccountNoStatusName(account);
    }

    @Override
    public ResponseVO<Boolean> updateUserInfoById(UserInfoEditVO editVO) {
        return userInfoService.updateUserInfoById(editVO);
    }

    @Override
    public ResponseVO<Boolean> updateUserPersonInfoById(UserInfoPersonReqVO editVO) {
        return userInfoService.updateUserPersonInfoById(editVO);
    }

    @Override
    public List<String> getUserAccountByName(String userName, String siteCode) {
        return userInfoService.getUserAccountByName(userName, siteCode);
    }

    @Override
    public GetByUserAccountVO getByUserRegister(String userRegister) {
        return userInfoService.getByUserRegister(userRegister);
    }

    @Override
    public Long getCountByAgentIds(List<String> agentIds) {
        return userInfoService.getCountByAgentIds(agentIds);
    }

    @Override
    public List<GetUserInfoByAgentIdsVO> getUserInfoByAgentIds(List<String> agentIds) {
        return userInfoService.getUserInfoByAgentIds(agentIds);
    }

    @Override
    public List<UserInfoVO> getUserInfoByAgentId(String agentId) {
        return userInfoService.getUserInfoByAgentId(agentId);
    }


    @Override
    public GetByUserAccountVO getByUserInfoId(String userId) {
        return userInfoService.getByUserInfoId(userId);
    }


    @Override
    public UserInfoVO getByUserId(String userId) {
        return userInfoService.getByUserId(userId);
    }

    @Override
    public List<UserInfoVO> getByUserIds(List<String> userIds, String siteCode) {
        return userInfoService.getByUserIds(userIds, siteCode);
    }

    @Override
    public List<UserInfoVO> getByUserAccounts(List<String> userAccounts, String siteCode) {
        return userInfoService.getByUserAccounts(userAccounts, siteCode);
    }

    @Override
    public GetByUserAccountVO getByUserAccountAndSiteCode(String userAccount, String siteCode) {
        return userInfoService.getByUserAccountAndSiteCode(userAccount, siteCode);
    }

    @Override
    public UserInfoVO getUserByUserAccountAndSiteCode(String userAccount, String siteCode) {
        return userInfoService.getUserByUserAccountAndSiteCode(userAccount, siteCode);
    }

    @Override
    public Boolean updateByUserAccount(String userAccount, BigDecimal firstDepositAmount) {
        return userInfoService.updateByUserAccount(userAccount, firstDepositAmount);
    }

    /*@Override
    public List<UserInfoVO> getUserInfoByAccountList(UserAccountListVO vo) {
        return userInfoService.getUserInfoByAccountList(vo);
    }*/

    @Override
    public List<UserInfoVO> getUserInfoByUserIdsList(UserAccountListVO vo) {
        return userInfoService.getUserInfoByUserIdsList(vo);
    }


    /*@Override
    public List<UserLoginInfoVO> getLatestLoginInfoByAccountList(UserAccountListVO vo) {
        return userInfoService.getLatestLoginInfoByAccountList(vo);
    }*/


    @Override
    public List<UserLoginInfoVO> getLatestLoginInfoByUserIds(List<String> userIds) {
        return userInfoService.getLatestLoginInfoByUserIds(userIds);
    }

    @Override
    public UserLoginInfoVO getLatestLoginInfoByUserId(String userId) {
        return userInfoService.getLatestLoginInfoByUserIdForTask(userId);
    }

    @Override
    public ResponseVO updateAgentTransferInfo(String userAccount, String agentId, String agentAccount, Boolean isUpdateTransTime) {
        return userInfoService.updateAgentTransferInfo(userAccount, agentId, agentAccount, isUpdateTransTime);
    }

    @Override
    public ResponseVO updateAgentTransferInfoBySiteCode(String sitecode, String userAccount, String agentId, String agentAccount, Boolean isUpdateTransTime) {
        return userInfoService.updateAgentTransferInfoBySiteCode(sitecode, userAccount, agentId, agentAccount, isUpdateTransTime);
    }

    @Override
    public UserInfoVO queryUserInfoByAccount(String account) {
        return userInfoService.queryUserInfoByAccount(account);
    }

    @Override
    public Boolean updateByUserId(String userId, BigDecimal firstDepositAmount) {
        return userInfoService.updateByUserId(userId, firstDepositAmount);
    }

    @Override
    public ResponseVO<IndexVO> getUserBalance(String userId, String userAccount, String siteCode) {
        return userInfoService.getUserBalance(userId, userAccount, siteCode);
    }

    @Override
    public ResponseVO<UserVIPInfoVO> getUserVipInfo() {
        return userInfoService.getUserVipInfo();
    }

    @Override
    public ResponseVO<UserVIPInfoCNVO> getUserVipChianInfo(UserVIPInfoResVO userVIPInfoResVO) {
        return userInfoService.getUserVipChianInfo(userVIPInfoResVO);
    }

    @Override
    public ResponseVO<UserVIPDetailInfoVO> getUserVipDetailInfo() {
        return userInfoService.getUserVipDetailInfo();
    }

    @Override
    public List<UserInfoVO> getUserInfoListByAccounts(List<String> account) {
        return userInfoService.getUserInfoListByAccounts(account);
    }

    @Override
    public List<UserInfoVO> getUserInfoListByAccountsAndSiteCode(List<String> userAccounts, String siteCode) {
        return userInfoService.getUserInfoListByAccounts(siteCode, userAccounts);
    }

    @Override
    public Boolean getUserInfoIsExists(List<String> agentIds, String userAccount, String siteCode) {
        return userInfoService.getUserInfoIsExists(agentIds, userAccount, siteCode);
    }

    @Override
    public Long getByAgentId(String agentId) {
        return userInfoService.getByAgentId(agentId);
    }

    @Override
    public GetDirectUserListByAgentAndTimeResponse getDirectUserCountByAgentAndTime(GetDirectUserListByAgentAndTimeVO vo) {
        return userInfoService.getDirectUserCountByAgentAndTime(vo);
    }


    @Override
    public List<GetRegisterStatisticsByAgentIdVO> getRegisterStatisticsByAgentId(Long start, Long end, String agentId, String dbZone) {
        return userInfoService.getRegisterStatisticsByAgentId(start, end, agentId, dbZone);
    }

    @Override
    public List<GetFirstDepositStatisticsByAgentIdVO> getFirstDepositStatisticsByAgentId(Long start, Long end, String agentId, String dbZone) {
        return userInfoService.getFirstDepositStatisticsByAgentId(start, end, agentId, dbZone);
    }

    @Override
    public ResponseVO<SelectUserDetailResponseVO> selectUserDetail(SelectUserDetailParam vo) {
        return userInfoService.selectUserDetail(vo);
    }

    @Override
    public ResponseVO<Page<SubordinateUserListResponseVO>> subordinateUserList(SubordinateUserListParam vo) {
        return userInfoService.subordinateUserList(vo);
    }

    @Override
    public ResponseVO<UserVIPFlowRecordVO> getUserVipFlowLastOne(String userAccount, String siteCode) {
        return userInfoService.getUserVipFlowLastOne(userAccount, siteCode);
    }

    @Override
    public ResponseVO<?> agentEditRemark(EditRemarkParam vo) {
        return userInfoService.agentEditRemark(vo);
    }

    @Override
    public UserInfoVO getByMail(String email) {
        return userInfoService.getByMail(email);
    }

    @Override
    public UserInfoVO getByPhone(String phone) {
        return userInfoService.getByPhone(phone);
    }

    @Override
    public ResponseVO<UserIndexInfoVO> getIndexInfo(String userId, String userAccount, String siteCode, String timezone) {
        return userInfoService.getIndexInfo(userId, userAccount, siteCode, timezone);
    }

    @Override
    public UserInfoVO getUserInfoByQueryVO(UserQueryVO userQueryVO) {
        return userInfoService.getUserInfoByQueryVO(userQueryVO);
    }

    @Override
    public ResponseVO<SiteVIPSystemVO> getUserVipBenefitDetail() {
        return userInfoService.getUserVipBenefitDetail();
    }

    @Override
    public Boolean updateSecondDeposit(String userId, BigDecimal arriveAmount) {
        return userInfoService.updateSecondDeposit(userId, arriveAmount);
    }

    @Override
    public Long getUserRegisterDay(String userId) {
        UserInfoVO userInfoVO = userInfoService.getByUserId(userId);
        Long registerTime = userInfoVO.getRegisterTime();
        return ChronoUnit.DAYS.between(Instant.ofEpochSecond(registerTime), Instant.ofEpochSecond(System.currentTimeMillis()));
    }

    @Override
    public List<UserInfoVIPVO> getUserInfoVip(List<String> userIds, String siteCode) {
        return userInfoService.getUserInfoVip(userIds, siteCode);
    }

    @Override
    public List<UserInfoVO> getUserInfoByUserIds(List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        if (userIds.size() > 1000) {
            log.info("查询用户信息过多");
            return Lists.newArrayList();
        }
        return userInfoService.getUserInfoByUserIds(userIds);
    }

    @Override
    public List<UserInfoVO> getUserBalanceBySiteCodeUserAccount(String siteCode, List<String> userAccounts) {
        return userInfoService.getUserBalanceBySiteCodeUserAccount(siteCode, userAccounts);
    }

    @Override
    public Boolean incOnLineDay(String userId) {
        return userCommonService.incOnLineDay(userId);
    }

    @Override
    public List<String> getUsedAvatarList(List<String> avatarIds, String siteCode) {
        return userInfoService.getUsedAvatarList(avatarIds, siteCode);
    }

    @Override
    public UserSystemMessageConfigVO getUserLanguage(UserLanguageVO vo) {
        return userInfoService.getUserLanguage(vo);
    }


    @Override
    public UserLanguageVO updateUserLanguage(UserLanguageVO vo) {
        return userInfoService.updateUserLanguage(vo);
    }

    @Override
    public Map<String, Map<String, List<UserInfoVO>>> getUserListGroupSiteCode(Long startTime, Long endTime, String siteCode) {
        return userInfoService.getUserListGroupSiteCode(startTime, endTime, siteCode);
    }

    @Override
    public List<UserInfoVO> getUserListByParam(UserAgentQueryUserVO queryVO) {
        return userInfoService.getUserListByParam(queryVO);
    }

    @Override
    public List<String> getUnLabelByUserIds(String labelId, String siteCode) {
        return userInfoService.getUnLabelByUserIds(labelId, siteCode);
    }

    @Override
    public List<String> getUnBenefitByUserIds(String awardCode, String siteCode) {
        return userInfoService.getUnBenefitByUserIds(awardCode, siteCode);
    }

    @Override
    public Page<String> getUserIdListPage(UserIdPageVO userIdPageVO) {
        return userInfoService.getUserIdListPage(userIdPageVO);
    }

    @Override
    public Page<UserInfoResponseVO> listPage(UserInfoPageVO vo) {
        return userInfoService.listPage(vo);
    }

    @Override
    public List<UAgentSubLineUserResVO> findAgentSubLineUserNum(UserAgentSubLineReqVO reqVO) {
        return userInfoService.findAgentSubLineUserNum(reqVO);
    }

    @Override
    public List<UAgentSubLineUserResVO> findAgentSubLineUserFirstDeposit(UserAgentSubLineReqVO reqVO) {
        return userInfoService.findAgentSubLineUserFirstDeposit(reqVO);
    }

    @Override
    public Map<String, Long> getUserCountGroupByAgentId(List<String> downAgentIds) {
        return userInfoService.getUserCountGroupByAgentId(downAgentIds);
    }

    @Override
    public List<SiteUserDateQueryVO> getSiteCurrencyUserList(Map<String, List<String>> dateSiteStrMap, String currencyCode) {
        return userInfoService.getSiteCurrencyUserList(dateSiteStrMap, currencyCode);
    }

    @Override
    public ResponseVO<?> manuCollection(ManuBlockVO manuBlockVO) {
        final String apiUrl = jvPayDomainUrl + "/api/manual/manualCollection";
        String paramJson = JSON.toJSONString(manuBlockVO);
        Map<String, String> headerMap = buildHeadMap(paramJson);
        try {
            String result = HttpClientUtil.doPostJson(apiUrl, paramJson, headerMap);
            log.info("提交商户号结果：" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("code");
            if (success != null && success.equals("200")) {
                log.error("http request success:{1}", jsonObject.get("code"));
                return ResponseVO.success();
            }
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        } catch (Exception e) {
            log.error("http request error:{0}", e.getMessage());
        }
        return ResponseVO.fail(ResultCode.PARAM_ERROR);
    }

    @Override
    public Boolean checkUserIpMax(Integer maxCount, String ip, String siteCode) {
        return userInfoService.checkUserIpMax(maxCount, ip, siteCode);
    }

    @Override
    public ResponseVO<GetUserInfoCurrencyRespVO> checkUserCurrency(GetUserInfoCurrencyReqVO req) {

        return userInfoService.checkUserCurrency(req);
    }

    @Override
    public UserTeamVO getTeamUserInfo(GetTeamUserInfoParam param) {
        return userInfoService.getTeamUserInfo(param);
    }

    @Override
    public ResponseVO<ResultCode> authVerify(String userId, String userName, String areaCode, String phone, String birthday) {
        return userInfoService.authVerify(userId, userName, areaCode, phone, birthday);
    }



    /**
     * 报文加密
     *
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String timestamp = System.currentTimeMillis() + "";
        String random = RandomStringUtils.randomAlphabetic(6);
        String signVal = "";
        if (JSON.isValidArray(paramJson)) {
            JSONArray bodyJsonArray = JSONArray.parseArray(paramJson);
            signVal = ECDSAUtil.signParam(timestamp, random, bodyJsonArray, jvPayPrivateKey);
        } else {
            signVal = ECDSAUtil.signParam(timestamp, random, JSON.parseObject(paramJson), jvPayPrivateKey);
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP, timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM, random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME, signVal);
        return headerMap;
    }

    @Override
    public List<String> filterNoRebateUserIds(List<String> userIds, String siteCode) {
        return userInfoService.filterNoRebateUserIds(userIds, siteCode);
    }

    @Override
    public List<UserInfoVO> getUserInfoListByMinId(UserInfoReqVO userInfoReqVO) {
        return userInfoService.getUserInfoListByMinId(userInfoReqVO);
    }

}
