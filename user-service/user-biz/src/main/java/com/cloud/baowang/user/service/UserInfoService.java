package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.task.TaskOrderRecordApi;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.activity.api.vo.task.TaskAppReqVO;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentLabelManageApi;
import com.cloud.baowang.agent.api.enums.AgentLevelEnum;
import com.cloud.baowang.agent.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentAllDownUserVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.GetAgentDepositAmountByAgentVO;
import com.cloud.baowang.agent.api.vo.label.GetLabelsByAgentAccountVO;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.kafka.vo.UserLatestBetMqVO;
import com.cloud.baowang.common.kafka.vo.UserLatestBetVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.GetBetNumberByAgentIdVO;
import com.cloud.baowang.system.api.api.AgentParamConfigApi;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.api.site.SiteAdminApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.enums.RiskBlackTypeEnum;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateClientShowVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateConfigWebVO;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
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
import com.cloud.baowang.user.api.vo.user.request.*;
import com.cloud.baowang.user.api.vo.userTeam.UserTeamVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.*;
import com.cloud.baowang.user.service.vipV2.WelfareCenterV2Service;
import com.cloud.baowang.user.util.IpRangeUtil;
import com.cloud.baowang.user.util.MinioFileService;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardV2Enum;
import com.cloud.baowang.wallet.api.vo.activityV2.UserAwardRecordV2ReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListResponse;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.GetAllArriveAmountByAgentIdVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.GetAllArriveAmountByAgentUserResponseVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.GetAllWithdrawAmountByAgentIdVO;
import com.cloud.baowang.wallet.api.vo.vipV2.UserAwardRecordV2VO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 会员基本信息 服务类
 *
 * @author kimi
 * @since 2024-03-23 10:00:00
 */
@Slf4j
@Service
//@AllArgsConstructor
public class UserInfoService extends ServiceImpl<UserInfoRepository, UserInfoPO> {

    private final static int NONE = 0;
    private final static int LOCK = 1;
    private final static int UN_LOCK = 2;
    private final static String VIP_AWARD_TYPE_CN = "vip_award_type_cn";
    private final UserInfoRepository userInfoRepository;
    private final SiteUserLabelConfigService userLabelConfigService;
    private final SiteVIPRankService siteVIPRankService;
    private final SiteVIPGradeRepository siteVIPGradeRepository;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final HotWalletAddressApi hotWalletAddressApi;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final SystemParamApi systemParamApi;
    private final UserDetailsHistoryRemarkRepository userDetailsHistoryRemarkRepository;
    private final UserDetailsHistoryRemarkService userDetailsHistoryRemarkService;
    private final UserAccountUpdateReviewRepository userAccountUpdateReviewRepository;
    private final RiskApi riskApi;
    private final SiteVIPGradeService siteVIPGradeService;
    private final UserVIPFlowRecordRepository userVIPFlowRecordRepository;
    private final UserReviewService userReviewService;
    private final BusinessAdminApi businessAdminApi;
    private final UserManualUpRecordApi userManualUpRecordApi;
    private final ReportUserWinLoseApi reportUserWinLoseApi;
    private final OrderRecordApi orderRecordApi;
    private final UserCoinApi userCoinApi;
    private final AgentInfoApi agentInfoApi;
    private final UserDepositWithdrawApi userDepositWithdrawApi;
    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;
    private final AgentLabelManageApi agentLabelManageApi;
    private final AgentParamConfigApi agentParamConfigApi;
    private final UserRegistrationInfoRepository userRegistrationInfoRepository;
    private final SiteApi siteApi;
    private final SystemCurrencyInfoApi systemCurrencyInfoApi;
    private final SiteVIPVenueExeService siteVIPVenueExeService;
    private final UserCommonService userCommonService;
    private final UserNoticeService userNoticeService;
    private final SiteUserLabelConfigService siteUserLabelConfigService;
    private final UserPlatformCoinApi userPlatformCoinApi;
    private final AgentCommissionApi agentCommissionApi;
    private final SiteUserFeedbackService siteUserFeedbackService;
    private final WelfareCenterService welfareCenterService;
    private final WelfareCenterV2Service welfareCenterV2Service;
    private final SiteVIPRankRepository siteVIPRankRepository;
    private final MinioFileService fileService;
    private final SiteNewUserGuideStepRecordRepository newUserGuideStepRecordRepository;
    private final TaskOrderRecordApi taskOrderRecordApi;
    private final SiteRebateApi siteRebateApi;
    private final SiteAdminApi siteAdminApi;
    private final UserInformationChangePOService userInformationChangeService;
    private final UserPlatformTransferApi userPlatformTransferApi;
    private final AliAuthService aliAuthService;
    private final SiteVipOptionService siteVipOptionService;
    private final UserVipFlowRecordCnService userVipFlowRecordCnService;
    private final VIPAwardRecordV2Api vipAwardRecordV2Api;

    public UserInfoService(UserInfoRepository userInfoRepository,
                           SiteUserLabelConfigService userLabelConfigService,
                           SiteVIPRankService siteVIPRankService,
                           SiteVIPGradeRepository siteVIPGradeRepository,
                           SiteCurrencyInfoApi siteCurrencyInfoApi,
                           HotWalletAddressApi hotWalletAddressApi,
                           UserLoginInfoRepository userLoginInfoRepository,
                           SystemParamApi systemParamApi,
                           UserDetailsHistoryRemarkRepository userDetailsHistoryRemarkRepository,
                           UserDetailsHistoryRemarkService userDetailsHistoryRemarkService,
                           UserAccountUpdateReviewRepository userAccountUpdateReviewRepository,
                           RiskApi riskApi, SiteVIPGradeService siteVIPGradeService,
                           UserVIPFlowRecordRepository userVIPFlowRecordRepository,
                           UserReviewService userReviewService,
                           BusinessAdminApi businessAdminApi,
                           UserManualUpRecordApi userManualUpRecordApi,
                           ReportUserWinLoseApi reportUserWinLoseApi,
                           OrderRecordApi orderRecordApi,
                           UserCoinApi userCoinApi,
                           AgentInfoApi agentInfoApi,
                           UserDepositWithdrawApi userDepositWithdrawApi,
                           AgentDepositSubordinatesApi agentDepositSubordinatesApi,
                           AgentLabelManageApi agentLabelManageApi,
                           AgentParamConfigApi agentParamConfigApi,
                           UserRegistrationInfoRepository userRegistrationInfoRepository,
                           SiteApi siteApi, SystemCurrencyInfoApi systemCurrencyInfoApi,
                           SiteVIPVenueExeService siteVIPVenueExeService,
                           UserCommonService userCommonService,
                           UserNoticeService userNoticeService,
                           SiteUserLabelConfigService siteUserLabelConfigService,
                           UserPlatformCoinApi userPlatformCoinApi,
                           AgentCommissionApi agentCommissionApi,
                           SiteUserFeedbackService siteUserFeedbackService,
                           WelfareCenterService welfareCenterService,
                           WelfareCenterV2Service welfareCenterV2Service,
                           SiteVIPRankRepository siteVIPRankRepository,
                           MinioFileService fileService,
                           SiteNewUserGuideStepRecordRepository newUserGuideStepRecordRepository,
                           TaskOrderRecordApi taskOrderRecordApi,
                           SiteRebateApi siteRebateApi,
                           SiteAdminApi siteAdminApi,
                           UserInformationChangePOService userInformationChangeService,
                           UserPlatformTransferApi userPlatformTransferApi,
                           AliAuthService aliAuthService, SiteVipOptionService siteVipOptionService,
                           @Lazy UserVipFlowRecordCnService userVipFlowRecordCnService, VIPAwardRecordV2Api vipAwardRecordV2Api) {
        this.userInfoRepository = userInfoRepository;
        this.userLabelConfigService = userLabelConfigService;
        this.siteVIPRankService = siteVIPRankService;
        this.siteVIPGradeRepository = siteVIPGradeRepository;
        this.siteCurrencyInfoApi = siteCurrencyInfoApi;
        this.hotWalletAddressApi = hotWalletAddressApi;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.systemParamApi = systemParamApi;
        this.userDetailsHistoryRemarkRepository = userDetailsHistoryRemarkRepository;
        this.userDetailsHistoryRemarkService = userDetailsHistoryRemarkService;
        this.userAccountUpdateReviewRepository = userAccountUpdateReviewRepository;
        this.riskApi = riskApi;
        this.siteVIPGradeService = siteVIPGradeService;
        this.userVIPFlowRecordRepository = userVIPFlowRecordRepository;
        this.userReviewService = userReviewService;
        this.businessAdminApi = businessAdminApi;
        this.userManualUpRecordApi = userManualUpRecordApi;
        this.reportUserWinLoseApi = reportUserWinLoseApi;
        this.orderRecordApi = orderRecordApi;
        this.userCoinApi = userCoinApi;
        this.agentInfoApi = agentInfoApi;
        this.userDepositWithdrawApi = userDepositWithdrawApi;
        this.agentDepositSubordinatesApi = agentDepositSubordinatesApi;
        this.agentLabelManageApi = agentLabelManageApi;
        this.agentParamConfigApi = agentParamConfigApi;
        this.userRegistrationInfoRepository = userRegistrationInfoRepository;
        this.siteApi = siteApi;
        this.systemCurrencyInfoApi = systemCurrencyInfoApi;
        this.siteVIPVenueExeService = siteVIPVenueExeService;
        this.userCommonService = userCommonService;
        this.userNoticeService = userNoticeService;
        this.siteUserLabelConfigService = siteUserLabelConfigService;
        this.userPlatformCoinApi = userPlatformCoinApi;
        this.agentCommissionApi = agentCommissionApi;
        this.siteUserFeedbackService = siteUserFeedbackService;
        this.welfareCenterService = welfareCenterService;
        this.welfareCenterV2Service = welfareCenterV2Service;
        this.siteVIPRankRepository = siteVIPRankRepository;
        this.fileService = fileService;
        this.newUserGuideStepRecordRepository = newUserGuideStepRecordRepository;
        this.taskOrderRecordApi = taskOrderRecordApi;
        this.siteRebateApi = siteRebateApi;
        this.siteAdminApi = siteAdminApi;
        this.userInformationChangeService = userInformationChangeService;
        this.userPlatformTransferApi = userPlatformTransferApi;
        this.aliAuthService = aliAuthService;
        this.siteVipOptionService = siteVipOptionService;
        this.userVipFlowRecordCnService = userVipFlowRecordCnService;
        this.vipAwardRecordV2Api = vipAwardRecordV2Api;
    }

    public static void main(String[] args) {
        String relegationDaysTime = "2025-10-04";
        String timezone = "UTC+8";
        long startOfDayTimestamp = TimeZoneUtils.getStartOfDayTimestamp(relegationDaysTime, timezone);
        int daysBetweenInclusive = TimeZoneUtils.getDaysBetweenExclusive( System.currentTimeMillis(),startOfDayTimestamp, timezone);
        System.out.println(Math.abs(daysBetweenInclusive));
    }

    public ResponseVO<Page<UserInfoResponseVO>> getPage(UserInfoPageVO vo) {
        Page<UserInfoResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        if (ObjectUtils.isNotEmpty(vo.getUserAccount())) {
            boolean isValid = vo.getUserAccount().matches("^[a-zA-Z0-9,]*$");
            if (!isValid) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }

        /*if (StringUtils.isNotBlank(vo.getSiteCode())) {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.allSiteInfo();
            if (CollectionUtil.isNotEmpty(listResponseVO.getData())) {
                listResponseVO.getData().forEach(siteVO -> {
                    handicapModeMap.put(siteVO.getSiteCode(), siteVO.getHandicapMode());
                });
            }
        } else {
            handicapModeMap.put(vo.getSiteCode(), CurrReqUtils.getHandicapMode());
        }*/


        Page<UserInfoResponseVO> pageResult = userInfoRepository.getPage(page, vo);
        // 查询vip等级
        Map<String, Map<Integer, String>> vipGradeMap = new HashMap<>();
        if (StringUtils.isBlank(vo.getSiteCode())) {

            Map<String, List<SiteVIPGradeVO>> allSiteVipGrade = siteVIPGradeService.getAllSiteVipGrade();
            if (CollectionUtil.isNotEmpty(allSiteVipGrade)) {
                for (Map.Entry<String, List<SiteVIPGradeVO>> entry : allSiteVipGrade.entrySet()) {
                    String key = entry.getKey();
                    List<SiteVIPGradeVO> value = entry.getValue();
                    if (CollectionUtil.isNotEmpty(value)) {
                        Map<Integer, String> vipGradeMapSiteT = value.stream().filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName())).collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
                        vipGradeMap.put(key, vipGradeMapSiteT);
                    }
                }
            }

        } else {
            List<SiteVIPGradeVO> siteVIPGradeVOS = siteVIPGradeService.queryAllVIPGrade(vo.getSiteCode());

            Map<Integer, String> vipGradeMapSite;
            if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
                vipGradeMapSite = siteVIPGradeVOS.stream().filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName())).collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
                vipGradeMap.put(vo.getSiteCode(), vipGradeMapSite);
            }

        }
        // 查询vip Rank
        Map<String, Map<Integer, String>> vipRankMap = new HashMap<>();
        if (StringUtils.isBlank(vo.getSiteCode())) {
            Map<String, List<SiteVIPRankVO>> allSiteVipRank = siteVIPRankService.getAllSiteVipRank();
            for (Map.Entry<String, List<SiteVIPRankVO>> entry : allSiteVipRank.entrySet()) {
                String key = entry.getKey();
                List<SiteVIPRankVO> value = entry.getValue();
                if (CollectionUtil.isNotEmpty(value)) {
                    Map<Integer, String> vipRankMapS = value.stream().filter(vip -> vip.getVipRankCode() != null && StringUtils.isNotBlank(vip.getVipRankNameI18nCode()) && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                            .collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
                    vipRankMap.put(key, vipRankMapS);
                }
            }

        } else {
            List<SiteVIPRankVO> siteVIPRankVOSResponse = siteVIPRankService.getVipRankListBySiteCode(vo.getSiteCode());
            if (CollectionUtil.isNotEmpty(siteVIPRankVOSResponse)) {
                Map<Integer, String> vipRankMapS = siteVIPRankVOSResponse.stream().filter(vip -> vip.getVipRankCode() != null && StringUtils.isNotBlank(vip.getVipRankNameI18nCode()) && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                        .collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
                vipRankMap.put(vo.getSiteCode(), vipRankMapS);

            }
        }
        // 查询vip Rank
        SiteRequestVO siteRequestVO = new SiteRequestVO();
        // 盘口模式
        Map<String, Integer> handicapModeMap = Maps.newHashMap();
        // 查询siteName
        Map<String, String> siteCodeMap = new HashMap<>();
        // 需要查询所有的站点或者指定的站点
        siteRequestVO.setSiteCode(vo.getSiteCode());
        // 查询系统用户获取用户是否需要托敏
        SiteAdminVO siteAdminVO = siteAdminApi.getAdminByUserNameAndSite(CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());
        siteRequestVO.setPageSize(10000);
        if (vo.getIsAll()) {
            ResponseVO<Page<SiteVO>> responseVO = siteApi.querySiteInfo(siteRequestVO);
            List<SiteVO> siteVOS = responseVO.getData().getRecords();
            if (CollUtil.isNotEmpty(siteVOS)) {
                siteCodeMap = siteVOS.stream().filter(siteVO -> StringUtils.isNotBlank(siteVO.getSiteCode())).collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getSiteName));
                handicapModeMap = siteVOS.stream().filter(siteVO -> StringUtils.isNotBlank(siteVO.getSiteCode())).collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getHandicapMode));
            }
        }
        // 查询是否在线
        Set<String> onlineUSerIds = RedisUtil.getSet(RedisKeyTransUtil.wsOnlineUserList(vo.getSiteCode()));
        //封装代理信息
        List<UserInfoResponseVO> records = pageResult.getRecords();
        List<String> superAgentIds = records.stream()
                .map(UserInfoResponseVO::getSuperAgentId)
                .filter(StringUtils::isNotBlank)
                .toList();
        Map<String, AgentInfoVO> agentIdVOMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(superAgentIds)) {
            List<AgentInfoVO> infoVOS = agentInfoApi.getByAgentIds(superAgentIds);
            agentIdVOMap = infoVOS.stream()
                    .collect(Collectors.toMap(AgentInfoVO::getAgentId, Function.identity()));
        }
        List<String> userIds = records.stream()
                .map(UserInfoResponseVO::getUserId)
                .toList();

        // 注册ip信息 Map<userId, String>
       /* Map<String, String> regMsgMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userIds)) {
            LambdaQueryWrapper<UserRegistrationInfoPO> regQuery = Wrappers.lambdaQuery();
            regQuery.eq(UserRegistrationInfoPO::getSiteCode, vo.getSiteCode()).in(UserRegistrationInfoPO::getMemberId, userIds);
            List<UserRegistrationInfoPO> userRegistrationInfoPOS = userRegistrationInfoRepository.selectList(regQuery);
            if (CollectionUtil.isNotEmpty(userRegistrationInfoPOS)) {
                regMsgMap = userRegistrationInfoPOS.stream().collect(Collectors.toMap(UserRegistrationInfoPO::getMemberId, UserRegistrationInfoPO::getIpAttribution));
            }
        }*/
        // 会员标签
        Set<String> labelIds = records.stream()
                .filter(record -> StrUtil.isNotEmpty(record.getUserLabelId()))
                .flatMap(record -> Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA)))
                .collect(Collectors.toSet());

        List<GetUserLabelByIdsVO> userLabels = userLabelConfigService.getUserLabelByIds(new ArrayList<>(labelIds));
        Map<String, GetUserLabelByIdsResponseVO> userLabelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userLabels)) {
            userLabelMap = userLabels.stream()
                    .collect(Collectors.toMap(GetUserLabelByIdsVO::getId, tempVo -> ConvertUtil.entityToModel(tempVo, GetUserLabelByIdsResponseVO.class), (K1, K2) -> K2));
        }

        // 风控层级
        Set<String> riskIds = records.stream()
                .map(UserInfoResponseVO::getRiskLevelId)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskIds));
        for (UserInfoResponseVO record : records) {
            // 总代ID
            if (StrUtil.isNotEmpty(record.getSuperAgentId())) {
                if (agentIdVOMap.containsKey(record.getSuperAgentId())) {
                    AgentInfoVO agentInfoVO = agentIdVOMap.get(record.getSuperAgentId());
                    if (agentInfoVO.getLevel() != null && AgentLevelEnum.PARENT_AGENT.getCode().equals(agentInfoVO.getLevel().toString())) {
                        record.setGeneralAgentId(record.getSuperAgentId());
                    } else {
                        /*if (StrUtil.isNotEmpty(agentInfoVO.getPath())) {
                            String firstAgentId = agentInfoVO.getPath().split(",")[0];
                            AgentInfoVO firstAgent = agentInfoApi.getByAgentId(firstAgentId);
                            if (null != firstAgent) {
                                record.setGeneralAgentId(firstAgent.getId());
                            }
                        }*/
                    }
                    record.setSuperAgentAccount(agentInfoVO.getAgentAccount());
                }
            }
            /*if (regMsgMap.containsKey(record.getUserId())) {
                record.setRegisterIpAttribution(regMsgMap.get(record.getUserId()));
            }*/
            record.setRegisterIpAttribution(record.getIpaddress());

            // 会员标签
            if (StrUtil.isNotEmpty(record.getUserLabelId())) {

                /*List<String> split = Arrays.stream(record.getUserLabelId().split(",")).toList();
                if (CollUtil.isNotEmpty(split)) {
                    Map<String, String> finalUserLabelMap = userLabelMap;
                    List<String> userLabelList = split.stream().map(e -> finalUserLabelMap.get(e)).toList();
                    record.setUserLabel(userLabelList.stream().map(label -> "#" + label).collect(Collectors.joining()));
                }*/
                // 直接通过流进行 split 和非空检查
                List<GetUserLabelByIdsResponseVO> userLabelList = Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA))
                        .filter(StrUtil::isNotEmpty)// 过滤掉空的标签ID
                        .map(userLabelMap::get)// 映射到标签名称
                        .filter(Objects::nonNull)// 过滤掉映射为空的情况
                        .map(label -> {
                            GetUserLabelByIdsResponseVO temp = new GetUserLabelByIdsResponseVO();
                            temp.setLabelName("#" + label.getLabelName()); // 修改标签名称，添加 #
                            temp.setColor(label.getColor());
                            return temp;
                        })
                        .toList(); // 收集结果到列表中

                /*List<GetUserLabelByIdsResponseVO> userLabelList  = new ArrayList<>();
                String[] arr  = record.getUserLabelId().split(CommonConstant.COMMA);
                for(String str :arr){
                    if(StringUtils.isNotBlank(str)){
                        GetUserLabelByIdsResponseVO getUserLabelByIdsResponseVO = userLabelMap.get(str);
                        if(getUserLabelByIdsResponseVO != null){
                            GetUserLabelByIdsResponseVO temp = new GetUserLabelByIdsResponseVO();
                            temp.setLabelName("#" +getUserLabelByIdsResponseVO.getLabelName());
                            temp.setColor(getUserLabelByIdsResponseVO.getColor());
                            userLabelList.add(temp);
                        }
                    }
                }*/
                if (CollUtil.isNotEmpty(userLabelList)) {
                    //record.setUserLabel(String.join("", userLabelList));
                    record.setUserLabelVO(userLabelList);
                    String str = userLabelList.stream()
                            .map(GetUserLabelByIdsResponseVO::getLabelName)
                            .collect(Collectors.joining(","));
                    record.setUserLabel(str);
                }
            }

            // 风控层级
            if (null != record.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO = riskLevelDetailsVOMap.get(record.getRiskLevelId());
                record.setRiskLevel(riskLevelDetailsVO == null ? "" : riskLevelDetailsVO.getRiskControlLevel());
            }
            // 站点名称
            if (vo.getIsAll()) {
                record.setSiteName(siteCodeMap.get(record.getSiteCode()));
            }
            //VIP段位
            // vip等级
            if (ObjectUtils.isNotEmpty(record.getVipGrade())) {
                Map<Integer, String> integerStringMap = vipGradeMap.get(record.getSiteCode());
                if (CollectionUtil.isNotEmpty(integerStringMap)) {
                    record.setVipGradeName(integerStringMap.get(record.getVipGrade()));
                }
                //record.setVipGradeName(vipGradeMap.get(record.getVipGrade()));
            }
            if (ObjectUtils.isNotEmpty(record.getVipGradeUp())) {
                Map<Integer, String> integerStringMap = vipGradeMap.get(record.getSiteCode());
                if (CollectionUtil.isNotEmpty(integerStringMap)) {
                    record.setVipGradeUpName(integerStringMap.get(record.getVipGradeUp()));
                }
                // record.setVipGradeUpName(vipGradeMap.get(record.getVipGradeUp()));
            }
            if (ObjectUtils.isNotEmpty(record.getVipRank())) {
                Map<Integer, String> integerStringMap = vipRankMap.get(record.getSiteCode());
                if (CollectionUtil.isNotEmpty(integerStringMap)) {
                    record.setVipRankName(integerStringMap.get(record.getVipRank()));
                }
                //record.setVipRankName(vipRankMap.get(record.getVipRank()));
            }
            //在线状态
            if (onlineUSerIds.contains(record.getUserId())) {
                record.setOnlineStatus(CommonConstant.business_one);
            }
            if (siteAdminVO.getDataDesensitization()) {
                // 注册-脱敏
                record.setTronAddress(Optional.ofNullable(record.getTronAddress())
                        .filter(s -> !s.trim().isEmpty()) // 过滤掉空字符串
                        .map(s -> Arrays.stream(s.split(",")) // 按逗号分割
                                .map(SymbolUtil::showBankOrVirtualNo) // 脱敏处理
                                .collect(Collectors.joining(","))) // 重新用逗号拼接
                        .orElse(""));// 如果输入为null或空，返回空字符串
                record.setEthAddress(Optional.ofNullable(record.getEthAddress())
                        .filter(s -> !s.trim().isEmpty()) // 过滤掉空字符串
                        .map(s -> Arrays.stream(s.split(",")) // 按逗号分割
                                .map(SymbolUtil::showBankOrVirtualNo) // 脱敏处理
                                .collect(Collectors.joining(","))) // 重新用逗号拼接
                        .orElse("")); // 如果输入为null或空，返回空字符串
                if (StringUtils.isNotBlank(record.getEmail()) && record.getEmail().contains("@")) {
                    // 邮箱账号
                    record.setEmail(SymbolUtil.showEmail(record.getEmail()));
                }
                if (StringUtils.isNotBlank(record.getPhone())) {
                    // 手机号码
                    record.setPhone(SymbolUtil.showPhone(record.getPhone()));
                }

            }
            //
            String siteCode = record.getSiteCode();
            // 没有值就默认是海外盘
            if (StringUtils.isNotBlank(siteCode)) {
                record.setHandicapMode(handicapModeMap.get(siteCode) == null ? 0 : handicapModeMap.get(siteCode));
            }
        }
        return ResponseVO.success(pageResult);
    }

    public ResponseVO<Long> getTotalCount(UserInfoPageVO vo) {
        Long count = userInfoRepository.getTotalCount(vo);
        return ResponseVO.success(count);
    }

    public ResponseVO<Page<UserRemarkVO>> queryUserRemark(UserBasicRequestVO requestVO) {
        try {
            UserInfoPO po = getUserInfoPOByAccountOrRegister(requestVO);
            if (po == null) {
                return ResponseVO.success();
            }
            requestVO.setUserAccount(po.getUserAccount());
            Page<UserChangeTypeHistoryRecordPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            LambdaQueryWrapper<UserChangeTypeHistoryRecordPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserChangeTypeHistoryRecordPO::getMemberAccount, requestVO.getUserAccount());
            queryWrapper.eq(UserChangeTypeHistoryRecordPO::getSiteCode, requestVO.getSiteCode());
            queryWrapper.orderByDesc(UserChangeTypeHistoryRecordPO::getUpdatedTime);
            Page<UserChangeTypeHistoryRecordPO> resultPage = userDetailsHistoryRemarkRepository.selectPage(page, queryWrapper);
            // 查询所有的id
           /* Map<String, String> userInfoMap = new HashMap<>();
            List<String> ids = resultPage.getRecords().stream().map(UserChangeTypeHistoryRecordPO::getCreator).map(e -> String.valueOf(e)).toList();
            if (CollUtil.isNotEmpty(ids)) {
                List<BusinessAdminVO> userInfoPOS = businessAdminApi.getUserByIds(ids);
                if (CollUtil.isNotEmpty(userInfoPOS)) {
                    userInfoMap = userInfoPOS.stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName));
                }
            }

            Map<String, String> finalUserInfoMap = userInfoMap;*/
            List<UserRemarkVO> result = resultPage.getRecords().stream().map(obj -> {
                UserRemarkVO vo = new UserRemarkVO();
                vo.setId(obj.getId());
                vo.setMemberAccount(obj.getMemberAccount());
                vo.setRemark(obj.getRemark());
                vo.setUpdateTime(obj.getUpdatedTime());
                vo.setOperator(obj.getUpdater());
                return vo;
            }).toList();
            return ResponseVO.success(new Page<UserRemarkVO>(requestVO.getPageNumber(), requestVO.getPageSize(), resultPage.getTotal())
                    .setRecords(result));
        } catch (Exception e) {
            log.error("查询该用户:{}备注信息异常", e);
            return ResponseVO.fail(ResultCode.USER_REMARK_QUERY_ERROR);
        }
    }

    public ResponseVO<Boolean> updateRemarkHistory(UserRemarkRequestVO remarkRequestVO) {
        LambdaUpdateWrapper<UserChangeTypeHistoryRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserChangeTypeHistoryRecordPO::getRemark, remarkRequestVO.getRemark());
        updateWrapper.set(UserChangeTypeHistoryRecordPO::getUpdater, remarkRequestVO.getOperator());
        updateWrapper.set(UserChangeTypeHistoryRecordPO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.eq(UserChangeTypeHistoryRecordPO::getId, remarkRequestVO.getId());
        userDetailsHistoryRemarkRepository.update(null, updateWrapper);

        return ResponseVO.success(true);
    }

    public ResponseVO<List<UserInfoQueryVO>> getUserInfoByUserAccount(UserBasicRequestVO requestVO) {
        return ResponseVO.success(userInfoRepository.getUserInfoList(requestVO.getUserAccount()));
    }

    public List<UserInfoVO> getUserInfoListByMinId(UserInfoReqVO userInfoReqVO) {

        LambdaQueryWrapper<UserInfoPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotEmpty(userInfoReqVO.getSiteCode()), UserInfoPO::getSiteCode, userInfoReqVO.getSiteCode());
        wrapper.gt(userInfoReqVO.isVipNotGrade1Flag(), UserInfoPO::getVipGradeCode, CommonConstant.business_one);
        wrapper.likeLeft(StrUtil.isNotEmpty(userInfoReqVO.getBirthdayLeft()), UserInfoPO::getBirthday, userInfoReqVO.getBirthdayLeft());
        wrapper.between((userInfoReqVO.getRegisterTimeStart()!=null && userInfoReqVO.getRegisterTimeEnd()!=null) , UserInfoPO::getRegisterTime, userInfoReqVO.getRegisterTimeStart(),userInfoReqVO.getRegisterTimeEnd());
        wrapper.gt(StrUtil.isNotEmpty(userInfoReqVO.getMinId()), UserInfoPO::getId, userInfoReqVO.getMinId());

        //NOTE 后面可以继续加条件。。。
        wrapper.orderBy(true, true, UserInfoPO::getId);
        wrapper.last(" limit " + userInfoReqVO.getLimitCount());
        return  BeanUtil.copyToList(userInfoRepository.selectList(wrapper), UserInfoVO.class);
    }

    public ResponseVO<UserBasicVO> queryBasicUser(UserBasicRequestVO requestVO) {
        try {
            if (ObjectUtil.isEmpty(requestVO.getUserAccount())) {
                throw new BaowangDefaultException(ResultCode.PLEASE_RE_ENTER_USER_ACCOUNT);
            }
            UserBasicVO vo = new UserBasicVO();
            requestVO.setUserAccount(requestVO.getUserAccount().trim());
            UserInfoVO userInfoVO = getUserInfoByAccount(requestVO);

            if (ObjectUtil.isEmpty(userInfoVO)) {
                // 校验用户不存在
                return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
            }
            if (requestVO.getDataDesensitization()) {
                // 邮箱
                userInfoVO.setEmail(SymbolUtil.showEmail(userInfoVO.getEmail()));
                // 手机号
                userInfoVO.setPhone(SymbolUtil.showPhone(userInfoVO.getPhone()));
                // 姓名
                userInfoVO.setUserName(SymbolUtil.showUserName(userInfoVO.getUserName()));
            }
            String siteCode = userInfoVO.getSiteCode();
            ResponseVO<SiteVO> siteInfo1 = siteApi.getSiteInfo(siteCode);
            String timezone = "";
            if (siteInfo1.isOk()) {
                timezone = siteInfo1.getData().getTimezone();
            } else {
                //
                log.error("获取站点信息失败！");
                return ResponseVO.fail(ResultCode.SITE_STATUS_NOT_EXIST);
            }
            // 组装概要信息
            UserSummaryVO summaryVO = assembleSummaryVO(userInfoVO);
            if (StringUtils.isNotBlank(summaryVO.getRegistryName())) {
                String nameStr = I18nMessageUtil.getI18NMessageInAdvice((summaryVO.getRegistryName()));
                summaryVO.setRegistryName(nameStr);
            }
            // 组装个人资料
            UserPersonalVO personalVO = ConvertUtil.entityToModel(userInfoVO, UserPersonalVO.class);
            if (null != personalVO) {
                personalVO.setGenderName(userInfoVO.getGenderName());
                if (StringUtils.isBlank(personalVO.getPhone())) {
                    personalVO.setAreaCode("");
                }
            }
            Integer handicapMode = SiteHandicapModeEnum.Internacional.getCode();
            ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(userInfoVO.getSiteCode());
            if (ObjectUtil.isNotEmpty(siteInfo.getData())) {
                if (siteInfo.isOk()) {
                    handicapMode = siteInfo.getData().getHandicapMode();
                }
            }
            // 组装VIP信息
            UserVIPVO vipVO;
            if (ObjectUtil.equals(handicapMode, SiteHandicapModeEnum.China.getCode())) {
                vipVO = assembleVIPVOcn(userInfoVO, timezone);
            } else {
                vipVO = assembleVIPVO(userInfoVO);
            }
            vo.setUserSummaryVO(summaryVO);
            vo.setUserPersonalVO(personalVO);
            vo.setUserVIPVO(vipVO);

            vo.setId(userInfoVO.getId());
            vo.setDataDesensitization(requestVO.getDataDesensitization());
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("查询会员详情基本信息异常", e);
            return ResponseVO.fail(ResultCode.USER_BASIC_QUERY_ERROR);
        }
    }

    private UserSummaryVO assembleSummaryVO(final UserInfoVO userInfoVO) {
        UserSummaryVO summaryVO = ConvertUtil.entityToModel(userInfoVO, UserSummaryVO.class);
        summaryVO.setUserLabelId(stringToList(userInfoVO.getUserLabelId()));
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_REGISTRY)).getData();
        Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                .stream().filter(obj -> obj.getCode().equals(String.valueOf(summaryVO.getRegistry()))).findFirst();
        registryOptional.ifPresent(systemParamVO -> summaryVO.setRegistryName(systemParamVO.getValue()));
        LambdaQueryWrapper<UserAccountUpdateReviewPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAccountUpdateReviewPO::getMemberAccount, userInfoVO.getUserAccount());
        queryWrapper.eq(UserAccountUpdateReviewPO::getSiteCode, userInfoVO.getSiteCode());
        queryWrapper.in(UserAccountUpdateReviewPO::getReviewStatus, List
                .of(BigDecimalConstants.ONE.intValue(), BigDecimalConstants.TWO.intValue()));
        // 后台修改审核标签如果审核中(状态为 1 需要置灰，0 可以编辑)
        int reviewStatus = userAccountUpdateReviewRepository.selectCount(queryWrapper).intValue();
        summaryVO.setUserReviewStatus(reviewStatus > 0 ? 1 : 0);
        // 查询某个用户热钱包地址
        List<HotWalletAddressVO> addressVOList = hotWalletAddressApi
                .queryHotWalletAddressByUserId(userInfoVO.getUserId());
        String trcAdd = addressVOList.stream().filter(obj -> obj.getChainType()
                        .equals(NetWorkTypeEnum.TRC20.getType())).findFirst().orElse(new HotWalletAddressVO())
                .getAddress();
        String ethAdd = addressVOList.stream().filter(obj -> obj.getChainType()
                        .equals(NetWorkTypeEnum.ERC20.getType())).findFirst().orElse(new HotWalletAddressVO())
                .getAddress();
        summaryVO.setTrcAddress(trcAdd);
        summaryVO.setEthAddress(ethAdd);
        return summaryVO;
    }

    public UserInfoVO getUserInfoByAccount(final String account) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_ACCOUNT_TYPE, CommonConstant.USER_ACCOUNT_STATUS,
                        CommonConstant.USER_GENDER, CommonConstant.USER_REGISTRY)).getData();
        UserInfoPO po = userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(UserInfoPO::getUserAccount, account)
                .eq(UserInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
        );
        UserInfoVO vo = ConvertUtil.entityToModel(po, UserInfoVO.class);
//        AgentInfoVO agentInfoVO = agentFeignResource.getByAgentAccount(vo.getSuperAgentAccount());
        if (null != vo) {
            vo.setSuperAgentName(vo.getSuperAgentAccount());
            vo.setAccountType(po.getAccountType());
            Optional<CodeValueVO> typeOptional = map.get(CommonConstant.USER_ACCOUNT_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getAccountType()))).findFirst();
            Optional<CodeValueVO> genderOptional = map.get(CommonConstant.USER_GENDER)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getGender()))).findFirst();
            Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getRegistry()))).findFirst();
            Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                    .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
            List<CodeValueVO> list = Lists.newArrayList();
            for (String str : po.getAccountStatus().split(",")) {
                list.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).type(CommonConstant.USER_ACCOUNT_STATUS).build());
            }
            vo.setAccountStatusName(list);
            typeOptional.ifPresent(systemParamVO -> vo.setAccountTypeName(systemParamVO.getValue()));
            genderOptional.ifPresent(systemParamVO -> vo.setGenderName(systemParamVO.getValue()));
            registryOptional.ifPresent(systemParamVO -> vo.setRegistryName(systemParamVO.getValue()));
            // 会员标签
            if (null != vo.getUserLabelId()) {
                //
                List<String> ids = stringToList(vo.getUserLabelId());
                List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                String userLabel = "";
                if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                    for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                        userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
                    }
                    // 拿出来是string,转化为list，然后在组装
                }
                if (userLabel.endsWith(CommonConstant.COMMA)) {
                    userLabel = userLabel.substring(0, userLabel.length() - 1);
                }
                vo.setUserLabelName(userLabel);
            }
            // 风控层级
            if (null != vo.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                String riskLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                vo.setRiskLevel(riskLevel);
            }
        }
        return vo;
    }

    public UserInfoVO getUserInfoByAccountAndSiteCode(final String account, String siteCode) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_ACCOUNT_TYPE, CommonConstant.USER_ACCOUNT_STATUS,
                        CommonConstant.USER_GENDER, CommonConstant.USER_REGISTRY)).getData();
        UserInfoPO po = userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(UserInfoPO::getUserAccount, account)
                .eq(UserInfoPO::getSiteCode, siteCode)
        );
        UserInfoVO vo = ConvertUtil.entityToModel(po, UserInfoVO.class);
//        AgentInfoVO agentInfoVO = agentFeignResource.getByAgentAccount(vo.getSuperAgentAccount());
        if (null != vo) {
            vo.setSuperAgentName(vo.getSuperAgentAccount());
            vo.setAccountType(po.getAccountType());
            Optional<CodeValueVO> typeOptional = map.get(CommonConstant.USER_ACCOUNT_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getAccountType()))).findFirst();
            Optional<CodeValueVO> genderOptional = map.get(CommonConstant.USER_GENDER)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getGender()))).findFirst();
            Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getRegistry()))).findFirst();
            Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                    .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
            List<CodeValueVO> list = Lists.newArrayList();
            for (String str : po.getAccountStatus().split(",")) {
                list.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).build());
            }
            vo.setAccountStatusName(list);
            typeOptional.ifPresent(systemParamVO -> vo.setAccountTypeName(systemParamVO.getValue()));
            genderOptional.ifPresent(systemParamVO -> vo.setGenderName(systemParamVO.getValue()));
            registryOptional.ifPresent(systemParamVO -> vo.setRegistryName(systemParamVO.getValue()));
            // 会员标签
            if (null != vo.getUserLabelId()) {
                //
                List<String> ids = stringToList(vo.getUserLabelId());
                List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                String userLabel = "";
                if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                    for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                        userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
                    }
                    // 拿出来是string,转化为list，然后在组装
                }
                if (userLabel.endsWith(CommonConstant.COMMA)) {
                    userLabel = userLabel.substring(0, userLabel.length() - 1);
                }
                vo.setUserLabelName(userLabel);
            }
            // 风控层级
            if (null != vo.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                String riskLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                vo.setRiskLevel(riskLevel);
            }
        }
        return vo;
    }

    public UserInfoVO getUserInfoByUserId(final String userId) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_ACCOUNT_TYPE, CommonConstant.USER_ACCOUNT_STATUS,
                        CommonConstant.USER_GENDER, CommonConstant.USER_REGISTRY)).getData();
        UserInfoPO po = userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(UserInfoPO::getUserId, userId)
        );
        UserInfoVO vo = ConvertUtil.entityToModel(po, UserInfoVO.class);
//        AgentInfoVO agentInfoVO = agentFeignResource.getByAgentAccount(vo.getSuperAgentAccount());
        if (null != vo) {
            vo.setSuperAgentName(vo.getSuperAgentAccount());
            vo.setAccountType(po.getAccountType());
            Optional<CodeValueVO> typeOptional = map.get(CommonConstant.USER_ACCOUNT_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getAccountType()))).findFirst();
            Optional<CodeValueVO> genderOptional = map.get(CommonConstant.USER_GENDER)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getGender()))).findFirst();
            Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getRegistry()))).findFirst();
            Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                    .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
            List<CodeValueVO> list = Lists.newArrayList();
            for (String str : po.getAccountStatus().split(",")) {
                list.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).build());
            }
            vo.setAccountStatusName(list);
            typeOptional.ifPresent(systemParamVO -> vo.setAccountTypeName(systemParamVO.getValue()));
            genderOptional.ifPresent(systemParamVO -> vo.setGenderName(systemParamVO.getValue()));
            registryOptional.ifPresent(systemParamVO -> vo.setRegistryName(systemParamVO.getValue()));
            // 会员标签
            if (null != vo.getUserLabelId()) {
                //
                List<String> ids = stringToList(vo.getUserLabelId());
                List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                String userLabel = "";
                if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                    for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                        userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
                    }
                    // 拿出来是string,转化为list，然后在组装
                }
                if (userLabel.endsWith(CommonConstant.COMMA)) {
                    userLabel = userLabel.substring(0, userLabel.length() - 1);
                }
                vo.setUserLabelName(userLabel);
            }
            // 风控层级
            if (null != vo.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                String riskLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                vo.setRiskLevel(riskLevel);
            }
        }
        return vo;
    }

    public UserInfoVO getUserInfoVOByAccountOrRegister(UserBasicRequestVO requestVO) {
        UserInfoPO po = getUserInfoPOByAccountOrRegister(requestVO);
        return ConvertUtil.entityToModel(po, UserInfoVO.class);


    }

    public UserInfoVO getUserInfoVO(UserBasicRequestVO requestVO) {
        UserInfoPO po = getUserInfoPOByAccountOrRegister(requestVO);
        if (po == null) {
            return new UserInfoVO();
        }
        return BeanUtil.toBean(po, UserInfoVO.class);
    }

    public UserInfoPO getUserInfoPOByAccountOrRegister(UserBasicRequestVO requestVO) {
        return userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(StringUtils.isNotBlank(requestVO.getUserAccount()), UserInfoPO::getUserAccount, requestVO.getUserAccount())
                .eq(StringUtils.isNotBlank(requestVO.getSiteCode()), UserInfoPO::getSiteCode, requestVO.getSiteCode()));
    }

    public UserInfoPO getUserInfoPOByAccountOrRegister(String userAccount, String siteCode) {
        return userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(StringUtils.isNotBlank(userAccount), UserInfoPO::getUserAccount, userAccount)
                .eq(StringUtils.isNotBlank(siteCode), UserInfoPO::getSiteCode, siteCode));
    }

    public UserInfoVO getUserInfoByAccount(UserBasicRequestVO requestVO) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_ACCOUNT_TYPE, CommonConstant.USER_ACCOUNT_STATUS,
                        CommonConstant.USER_GENDER, CommonConstant.USER_REGISTRY)).getData();
        UserInfoPO po = getUserInfoPOByAccountOrRegister(requestVO);
        UserInfoVO vo = ConvertUtil.entityToModel(po, UserInfoVO.class);
//        AgentInfoVO agentInfoVO = agentFeignResource.getByAgentAccount(vo.getSuperAgentAccount());
        if (null != vo) {
            vo.setSuperAgentName(vo.getSuperAgentAccount());
            vo.setAccountType(po.getAccountType());
            Optional<CodeValueVO> typeOptional = map.get(CommonConstant.USER_ACCOUNT_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getAccountType()))).findFirst();
            Optional<CodeValueVO> genderOptional = map.get(CommonConstant.USER_GENDER)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getGender()))).findFirst();
            Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getRegistry()))).findFirst();
            Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                    .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
            List<CodeValueVO> list = Lists.newArrayList();
            for (String str : po.getAccountStatus().split(",")) {
                list.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).build());
            }
            vo.setAccountStatusName(list);
            typeOptional.ifPresent(systemParamVO -> vo.setAccountTypeName(I18nMessageUtil.getI18NMessageInAdvice(systemParamVO.getValue())));
            genderOptional.ifPresent(systemParamVO -> vo.setGenderName(I18nMessageUtil.getI18NMessageInAdvice(systemParamVO.getValue())));
            registryOptional.ifPresent(systemParamVO -> vo.setRegistryName(I18nMessageUtil.getI18NMessageInAdvice(systemParamVO.getValue())));
            vo.setRegistryName(I18nMessageUtil.getI18NMessageInAdvice(vo.getRegistryName()));
            // 会员标签
            if (null != vo.getUserLabelId()) {
                //
                List<String> ids = stringToList(vo.getUserLabelId());
                List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                String userLabel = "";
                List<GetUserLabelByIdsResponseVO> userLabelNames = new ArrayList<>();
                if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                    for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                        userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
                        GetUserLabelByIdsResponseVO getUserLabelByIdsResponse = new GetUserLabelByIdsResponseVO();
                        getUserLabelByIdsResponse.setLabelName(userLabelConfigPO.getLabelName());
                        getUserLabelByIdsResponse.setColor(userLabelConfigPO.getColor());
                        userLabelNames.add(getUserLabelByIdsResponse);

                    }
                    // 拿出来是string,转化为list，然后在组装
                }
                if (userLabel.endsWith(CommonConstant.COMMA)) {
                    userLabel = userLabel.substring(0, userLabel.length() - 1);
                }
                vo.setUserLabelName(userLabel);
                vo.setUserLabelNames(userLabelNames);
            }
            // 风控层级
            if (StringUtils.isNotBlank(vo.getRiskLevelId())) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                String riskLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                vo.setRiskLevel(riskLevel);
            }
            // 注册ip风控层级
            if (StringUtils.isNotBlank(vo.getRegisterIp())) {
                RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                riskAccountQueryVO.setSiteCode(requestVO.getSiteCode());
                riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                riskAccountQueryVO.setRiskControlAccount(vo.getRegisterIp());
                RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
                if (null != riskAccountByAccount) {
                    vo.setRegisterIpRiskLevel(riskAccountByAccount.getRiskControlLevel());
                }

            }
        }

        return vo;
    }

    private List<String> stringToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        // 使用 split 方法将字符串按逗号分隔
        String[] items = str.split(",");

        // 将数组转换为列表
        return Arrays.asList(items);
    }

    public ResponseVO<UserInfoVO> getUserInfoByAccountNoStatusName(String account) {
        UserInfoPO po = userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>()
                .eq(UserInfoPO::getUserAccount, account));
        if (po == null) {
            return ResponseVO.success(null);
        }
        return ResponseVO.success(ConvertUtil.entityToModel(po, UserInfoVO.class));
    }

    public ResponseVO<Boolean> updateUserInfoById(UserInfoEditVO userInfoEditVO) {
        if (userInfoEditVO.getId() == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        int update = userInfoRepository.update(null, Wrappers.<UserInfoPO>lambdaUpdate()
                .eq(UserInfoPO::getId, userInfoEditVO.getId())
                .set(userInfoEditVO.getRiskLevelId() != null, UserInfoPO::getRiskLevelId, userInfoEditVO.getRiskLevelId())
                .set(userInfoEditVO.getLastLoginTime() != null, UserInfoPO::getLastLoginTime, userInfoEditVO.getLastLoginTime())
                .set(userInfoEditVO.getUpdatedTime() != null, UserInfoPO::getUpdatedTime, userInfoEditVO.getUpdatedTime())
                .set(userInfoEditVO.getPassword() != null, UserInfoPO::getPassword, userInfoEditVO.getPassword())
                .set(userInfoEditVO.getPhone() != null, UserInfoPO::getPhone, userInfoEditVO.getPhone())
                .set(userInfoEditVO.getEmail() != null, UserInfoPO::getEmail, userInfoEditVO.getEmail())
                .set(userInfoEditVO.getOfflineDays() != null, UserInfoPO::getOfflineDays, userInfoEditVO.getOfflineDays())
                .set(userInfoEditVO.getLastDeviceNo() != null, UserInfoPO::getLastDeviceNo, userInfoEditVO.getLastDeviceNo())
                .set(userInfoEditVO.getLastLoginIp() != null, UserInfoPO::getLastLoginIp, userInfoEditVO.getLastLoginIp())
        );

        return update > 0 ? ResponseVO.success() : ResponseVO.fail(ResultCode.UPDATE_FAIL);
    }

    /**
     * @param userName
     * @return
     */

    public List<String> getUserAccountByName(String userName, String siteCode) {
        List<UserInfoPO> list = this.lambdaQuery()
                .eq(UserInfoPO::getUserName, userName)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .list();
        return list.stream().map(UserInfoPO::getUserAccount).toList();
    }

    private UserVIPVO assembleVIPVO(final UserInfoVO userInfoVO) {
        UserVIPVO vipVO = new UserVIPVO();
        String siteCode = userInfoVO.getSiteCode();
        vipVO.setVipGrade(userInfoVO.getVipGradeCode());
        vipVO.setVipGradeUp(userInfoVO.getVipGradeUp());
        // 升级后的VIP等级配置

        SiteVIPGradeVO vipGradeVo = siteVIPGradeService.queryVIPGradeByGrade(String.valueOf(userInfoVO.getVipGradeUp()), siteCode);


        // 当前VIP等级配置
        SiteVIPGradeVO currentVipGrade = siteVIPGradeService.queryVIPGradeByGrade(String.valueOf(userInfoVO.getVipGradeCode()), siteCode);
        if (currentVipGrade == null) {
            currentVipGrade = new SiteVIPGradeVO();
        }
        /* TODO 需要调取存款表先临时写死 */
        UserVIPFlowRecordPO lastPo = userVIPFlowRecordRepository.selectLastOne(userInfoVO.getUserId(),
                userInfoVO.getVipGradeCode());
        LambdaQueryWrapper<UserVIPFlowRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVIPFlowRecordPO::getUserAccount, userInfoVO.getUserAccount());
        queryWrapper.eq(UserVIPFlowRecordPO::getSiteCode, siteCode);
        queryWrapper.eq(UserVIPFlowRecordPO::getVipGradeCode, userInfoVO.getVipGradeCode());
        queryWrapper.orderByDesc(UserVIPFlowRecordPO::getUpdatedTime);
        queryWrapper.last("limit 1");
        UserVIPFlowRecordPO po = userVIPFlowRecordRepository.selectOne(queryWrapper);
        po = null != lastPo ? lastPo : po;
        if (null != po) {
            // 有投注记录
            //vipVO.setFinishDepositAmount(po.getDepositSumAmount());
            vipVO.setFinishBetAmount(po.getValidSumExe());
//            vipVO.setFinishRelegationAmount(po.getRelegationSumAmount());
            /*vipVO.setRelegationDate(DateUtil.offsetDay(DateUtil.parse(po.getLastVipTime(), DatePattern.NORM_DATE_PATTERN),
                    vipRank.getRelegationDay()).getTime());*/
        }
        if (ObjectUtil.isNotEmpty(vipGradeVo)) {
//            vipVO.setUpgradeDepositAmount(vipRankUp.getDepositUpgrade());
//            vipVO.setLeftDepositAmount(vipVO.getUpgradeDepositAmount()
//                    .subtract(vipVO.getFinishDepositAmount()));
            vipVO.setUpgradeBetAmount(vipGradeVo.getUpgradeXp());
            // 升级条件所需XP - 已完成的 todo 等慕凡完善这个接口
            //vipVO.setLeftBetAmount(vipGradeVo.getUpgradeXp().subtract(vipVO.getFinishBetAmount()));
        }
        if (vipGradeVo != null) {
            vipVO.setVipGradeName(currentVipGrade.getVipGradeName());
        }
        Integer vipRank = userInfoVO.getVipRank();
        vipVO.setVipRank(vipRank);

        if (vipRank != null) {
            LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
            query.eq(SiteVIPRankPO::getSiteCode, userInfoVO.getSiteCode()).eq(SiteVIPRankPO::getVipRankCode, vipRank);
            SiteVIPRankPO rankPO = siteVIPRankService.getOne(query);
            if (rankPO != null) {
                vipVO.setVipRankNameI18nCode(rankPO.getVipRankNameI18nCode());
            }

        }


        return vipVO;
    }

    private UserVIPVO assembleVIPVOcn(final UserInfoVO userInfoVO, String timezone) {
        UserVIPVO vipVO = new UserVIPVO();
        String siteCode = userInfoVO.getSiteCode();
        vipVO.setVipGrade(userInfoVO.getVipGradeCode());
        vipVO.setVipGradeUp(userInfoVO.getVipGradeUp());
        // 升级后的VIP等级配置

        //com.cloud.baowang.user.service.SiteVipOptionService#getList
        List<SiteVipOptionVO> list = siteVipOptionService.getList(siteCode, userInfoVO.getMainCurrency());
        SiteVipOptionVO vipGradeUpVo = list.stream().filter(item -> item.getVipGradeCode().equals(userInfoVO.getVipGradeUp())).findFirst().orElse(null);
        //SiteVIPGradeVO vipGradeVo = siteVIPGradeService.queryVIPGradeByGrade(String.valueOf(userInfoVO.getVipGradeUp()), siteCode);
        // 当前VIP等级配置
        //SiteVIPGradeVO currentVipGrade = siteVIPGradeService.queryVIPGradeByGrade(String.valueOf(userInfoVO.getVipGradeCode()), siteCode);
        SiteVipOptionVO currentVipGrade = list.stream().filter(item -> item.getVipGradeCode().equals(userInfoVO.getVipGradeCode())).findFirst().orElse(null);
        /* TODO 需要调取存款表先临时写死 */
        // com.cloud.baowang.user.api.vip.VipGradeApiImpl#getUserVipFlow
        UserVipFlowRecordCnVO userVipFlow = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);
       /* UserVIPFlowRecordPO lastPo = userVIPFlowRecordRepository.selectLastOne(userInfoVO.getUserId(),
                userInfoVO.getVipGradeCode());
        LambdaQueryWrapper<UserVIPFlowRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVIPFlowRecordPO::getUserAccount, userInfoVO.getUserAccount());
        queryWrapper.eq(UserVIPFlowRecordPO::getSiteCode, siteCode);
        queryWrapper.eq(UserVIPFlowRecordPO::getVipGradeCode, userInfoVO.getVipGradeCode());
        queryWrapper.orderByDesc(UserVIPFlowRecordPO::getUpdatedTime);
        queryWrapper.last("limit 1");
        UserVIPFlowRecordPO po = userVIPFlowRecordRepository.selectOne(queryWrapper);
        po = null != lastPo ? lastPo : po;*/
        if (null != userVipFlow) {
            // 有投注记录
            //vipVO.setFinishDepositAmount(po.getDepositSumAmount());
            // 当前等级的升级完成的流水
            vipVO.setFinishBetAmount(userVipFlow.getFinishBetAmount());
            // 升级完成所需要的流水
            vipVO.setUpgradeBetAmount(userVipFlow.getUpgradeBetAmount());
            // 剩余VIP升级需要有效投注 = 升级完成所需要的流水 - 当前等级的升级完成的流水
            vipVO.setLeftBetAmount(userVipFlow.getUpgradeBetAmount().subtract(userVipFlow.getFinishBetAmount()));
            // 保级当前流水
            vipVO.setFinishRelegationAmount(userVipFlow.getFinishRelegationAmount());
            // 保级总流水金额
            vipVO.setUpgradeRelegationAmount(userVipFlow.getGradeRelegationAmount());
            //
            vipVO.setLeftRelegationAmount(userVipFlow.getGradeRelegationAmount().subtract(vipVO.getFinishRelegationAmount()));
            // 保级天数
            vipVO.setRelegationDate(userVipFlow.getRelegationDays());
            // 保级剩余多少天
            String relegationDaysTime = userVipFlow.getRelegationDaysTime();
            if (StringUtils.isNotBlank(relegationDaysTime)) {
                // yyyy-mm-dd ,指定时间格式，获取
                long startOfDayTimestamp = TimeZoneUtils.getStartOfDayTimestamp(relegationDaysTime, timezone);
                int daysBetweenInclusive = TimeZoneUtils.getDaysBetweenExclusive(System.currentTimeMillis(), startOfDayTimestamp, timezone);
                vipVO.setRelegationLessDate(Math.abs(daysBetweenInclusive));

            }
        }
        if (ObjectUtil.isNotEmpty(vipGradeUpVo)) {
//            vipVO.setUpgradeDepositAmount(vipRankUp.getDepositUpgrade());
//            vipVO.setLeftDepositAmount(vipVO.getUpgradeDepositAmount()
//                    .subtract(vipVO.getFinishDepositAmount()));
            // vipVO.setUpgradeBetAmount(vipGradeVo.getVi());
            // 升级条件所需XP - 已完成的 todo 等慕凡完善这个接口
            //vipVO.setLeftBetAmount(vipGradeVo.getUpgradeXp().subtract(vipVO.getFinishBetAmount()));
        }
        if (vipGradeUpVo != null) {
            vipVO.setVipGradeUpName(vipGradeUpVo.getVipGradeName());
        }
        vipVO.setVipGradeName(currentVipGrade.getVipGradeName());
        UserAccountUpdateReviewPO po = userAccountUpdateReviewRepository.selectOne(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                .eq(UserAccountUpdateReviewPO::getMemberAccount, userInfoVO.getUserAccount())
                .eq(UserAccountUpdateReviewPO::getSiteCode, siteCode)
                .eq(UserAccountUpdateReviewPO::getReviewApplicationType,UserChangeTypeEnum.VIP_RANK_STATUS.getCode())
                .eq(UserAccountUpdateReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                .orderByDesc(UserAccountUpdateReviewPO::getCreatedTime)
                .last("limit 1"));
        String passStatus = String.valueOf(ReviewStatusEnum.REVIEW_PASS.getCode());
        if (null != po && ObjectUtil.equals(passStatus, po.getReviewStatus())) {
            vipVO.setRemark(po.getApplicationInformation());
        }
        return vipVO;
    }

    /**
     * @param requestVO
     * @return
     */

    public ResponseVO<Page<UserLoginInfoVO>> queryUserLoginInfo(UserBasicRequestVO requestVO) {
        try {
            UserInfoPO po = getUserInfoPOByAccountOrRegister(requestVO);
            if (po == null) {
                return ResponseVO.success();
            }
            requestVO.setUserAccount(po.getUserAccount());
            Page<UserLoginInfoPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            LambdaQueryWrapper<UserLoginInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserLoginInfoPO::getUserAccount, po.getUserAccount());
            queryWrapper.eq(UserLoginInfoPO::getSiteCode, po.getSiteCode());
            queryWrapper.orderByDesc(UserLoginInfoPO::getLoginTime);
            Page<UserLoginInfoPO> resultPage = userLoginInfoRepository.selectPage(page, queryWrapper);
            List<UserLoginInfoVO> list = ConvertUtil.convertListToList(resultPage.getRecords(), new UserLoginInfoVO());
            // 获取IP风控层级list ip
            RiskListAccountQueryVO vo = new RiskListAccountQueryVO();
            vo.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
            vo.setRiskControlAccounts(list.stream().map(UserLoginInfoVO::getIp).toList());
            vo.setSiteCode(requestVO.getSiteCode());
            // 封控等级待补充
            List<RiskAccountVO> ipRisk = riskApi.getRiskListAccount(vo);
            // 获取设备风控层级list device todo
            vo = new RiskListAccountQueryVO();
            vo.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());

            vo.setRiskControlAccounts(list.stream().map(UserLoginInfoVO::getDeviceNo).toList());
            vo.setSiteCode(requestVO.getSiteCode());
            //  封控等级待补充
            List<RiskAccountVO> deviceRisk = riskApi.getRiskListAccount(vo);
            list.forEach(obj -> {
                // IP地址风控层级
                if (null != obj.getIp()) {
                    Optional<RiskAccountVO> ipOptional = ipRisk.stream().filter(item -> item.getRiskControlAccount()
                            .equals(obj.getIp())).findFirst();
                    ipOptional.ifPresent(riskAccountVO -> obj.setIpControl(riskAccountVO.getRiskControlLevel()));
                }
                // 设备号风控层级
                if (null != obj.getDeviceNo()) {
                    Optional<RiskAccountVO> deviceOptional = deviceRisk.stream().filter(item -> item.getRiskControlAccount()
                            .equals(obj.getDeviceNo())).findFirst();
                    deviceOptional.ifPresent(riskAccountVO -> obj.setDeviceControl(riskAccountVO.getRiskControlLevel()));
                }
            });
            return ResponseVO.success(new Page<UserLoginInfoVO>(requestVO.getPageNumber(), requestVO.getPageSize(),
                    resultPage.getTotal()).setRecords(list));
        } catch (Exception e) {
            log.error("会员详情登录信息查询异常", e);
            return ResponseVO.fail(ResultCode.USER_INFO_LOGIN_QUERY_ERROR);
        }
    }

    public ResponseVO<Long> getCountByBlackAccount(RiskUserBlackAccountReqVO requestVO) {
        RiskBlackTypeEnum riskBlackType = RiskBlackTypeEnum.nameOfCode(requestVO.getRiskControlTypeCode());
        if (riskBlackType == null) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_TYPE_NOT_EXIST);
        }
        LambdaQueryWrapper<UserInfoPO> queryWrapper = getRiskUserLambdaQueryWrapper(requestVO, riskBlackType);

        return ResponseVO.success(userInfoRepository.selectCount(queryWrapper));
    }

    public ResponseVO<List<String>> getAllAccountByRiskBlack(RiskUserBlackAccountReqVO requestVO) {
        RiskBlackTypeEnum riskBlackType = RiskBlackTypeEnum.nameOfCode(requestVO.getRiskControlTypeCode());
        if (riskBlackType == null) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_TYPE_NOT_EXIST);
        }
        LambdaQueryWrapper<UserInfoPO> queryWrapper = getRiskUserLambdaQueryWrapper(requestVO, riskBlackType);
        queryWrapper.select(UserInfoPO::getUserAccount);
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(userInfoPOS)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        List<String> accounts = userInfoPOS.stream().map(UserInfoPO::getUserAccount).toList();
        return ResponseVO.success(accounts);
    }

    public ResponseVO<List<UserInfoVO>> getAllUserIdByRiskBlack(RiskUserBlackAccountReqVO requestVO) {
        RiskBlackTypeEnum riskBlackType = RiskBlackTypeEnum.nameOfCode(requestVO.getRiskControlTypeCode());
        if (riskBlackType == null) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_TYPE_NOT_EXIST);
        }
        List<UserInfoPO> userInfoPOS = null;
        if (!requestVO.isIpSegmentFlag()) {
            LambdaQueryWrapper<UserInfoPO> queryWrapper = getRiskUserLambdaQueryWrapper(requestVO, riskBlackType);
            // queryWrapper.select(UserInfoPO::getUserId);
            userInfoPOS = userInfoRepository.selectList(queryWrapper);
        } else {
            // 将ip转为long的string形式
            requestVO.setIpStart(String.valueOf(IpRangeUtil.ipToLong(requestVO.getIpStart())));
            requestVO.setIpEnd(String.valueOf(IpRangeUtil.ipToLong(requestVO.getIpEnd())));
            switch (riskBlackType) {
                case RISK_REG_IP -> userInfoPOS = userInfoRepository.selectListByRegIpSegment(requestVO, riskBlackType.isNeedKickOut());
                case RISK_LOGIN_IP -> userInfoPOS = userInfoRepository.selectListByLoginIpSegment(requestVO, riskBlackType.isNeedKickOut());
            }
        }
        if (CollectionUtil.isEmpty(userInfoPOS)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        return ResponseVO.success(ConvertUtil.entityListToModelList(userInfoPOS, UserInfoVO.class));
    }

    public ResponseVO<Page<RiskUserBlackAccountVO>> getRiskUserBlackListPage(RiskUserBlackAccountReqVO reqVO) {
        RiskBlackTypeEnum riskBlackType = RiskBlackTypeEnum.nameOfCode(reqVO.getRiskControlTypeCode());
        if (riskBlackType == null) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_TYPE_NOT_EXIST);
        }
        LambdaQueryWrapper<UserInfoPO> queryWrapper = getRiskUserLambdaQueryWrapper(reqVO, riskBlackType);
        Page<UserInfoPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<UserInfoPO> poPage = userInfoRepository.selectPage(page, queryWrapper);
        Page<RiskUserBlackAccountVO> voPage = new Page<>();
        BeanUtil.copyProperties(poPage, voPage);
        if (voPage.getTotal() > 0) {
            List<RiskUserBlackAccountVO> records = voPage.getRecords();
            List<RiskUserBlackAccountVO> riskUserBlackAccountVOS = BeanUtil.copyToList(records, RiskUserBlackAccountVO.class);
            voPage.setRecords(riskUserBlackAccountVOS);
        }

        return ResponseVO.success(voPage);
    }

    private LambdaQueryWrapper<UserInfoPO> getRiskUserLambdaQueryWrapper(RiskUserBlackAccountReqVO reqVO, RiskBlackTypeEnum riskBlackType) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        //如果不需要踢出 则返回结果为空
        if(!riskBlackType.isNeedKickOut()){
            queryWrapper.eq(UserInfoPO::getId, -1);
        }
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getSiteCode()), UserInfoPO::getSiteCode, reqVO.getSiteCode());
        switch (riskBlackType) {
            case RISK_REG_IP -> queryWrapper.eq(UserInfoPO::getRegisterIp, reqVO.getRiskControlAccount());
            case RISK_LOGIN_IP -> queryWrapper.eq(UserInfoPO::getLastLoginIp, reqVO.getRiskControlAccount());
            case RISK_REG_DEVICE -> queryWrapper.eq(UserInfoPO::getDeviceNo, reqVO.getRiskControlAccount());
            case RISK_LOGIN_DEVICE -> queryWrapper.eq(UserInfoPO::getLastDeviceNo, reqVO.getRiskControlAccount());
        }
        return queryWrapper;
    }

    public GetByUserAccountVO getByUserRegister(String userAccount) {
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getUserAccount, userAccount).one();
        return ConvertUtil.entityToModel(one, GetByUserAccountVO.class);
    }

    public GetByUserAccountVO getByUserInviteCode(String inviteCode) {
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getFriendInviteCode, inviteCode).one();
        return ConvertUtil.entityToModel(one, GetByUserAccountVO.class);
    }

    public Long getCountByAgentIds(List<String> agentIds) {
        if (CollUtil.isEmpty(agentIds)) {
            return null;
        }
        return this.lambdaQuery().in(UserInfoPO::getSuperAgentId, agentIds).count();
    }

    public List<GetUserInfoByAgentIdsVO> getUserInfoByAgentIds(List<String> agentIds) {
        if (CollUtil.isEmpty(agentIds)) {
            return Lists.newArrayList();
        }
        List<UserInfoPO> list =
                this.lambdaQuery()
                        .in(UserInfoPO::getSuperAgentId, agentIds)
                        .select(UserInfoPO::getSuperAgentId)
                        .list();
        return ConvertUtil.entityListToModelList(list, GetUserInfoByAgentIdsVO.class);
    }

    /**
     * 根据代理id,获取对应会员列表
     *
     * @param agentId 代理id
     * @return 会员信息
     */
    public List<UserInfoVO> getUserInfoByAgentId(String agentId) {
        List<UserInfoPO> list = this.lambdaQuery().eq(UserInfoPO::getSuperAgentId, agentId).list();
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, UserInfoVO.class);
        }
        return new ArrayList<>();
    }

    public GetByUserAccountVO getByUserAccount(String userAccount, String siteCode) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userAccount)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .one();
        return ConvertUtil.entityToModel(one, GetByUserAccountVO.class);
    }

    public GetByUserAccountVO getByUserInfoId(String userId) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserId, userId)
                .one();
        GetByUserAccountVO getByUserAccountVO = ConvertUtil.entityToModel(one, GetByUserAccountVO.class);
        if (Objects.nonNull(getByUserAccountVO)) {
            if (ObjectUtil.isNotEmpty(getByUserAccountVO.getUserName())) {
                // 托名
                getByUserAccountVO.setUserName(SymbolUtil.showUserName(getByUserAccountVO.getUserName()));
            }
        }
        return getByUserAccountVO;
    }

    public UserInfoVO getByUserId(String userId) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserId, userId)
                .one();
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public GetByUserAccountVO getByUserAccountAndSiteCode(String userAccount, String siteCode) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userAccount)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .one();
        return ConvertUtil.entityToModel(one, GetByUserAccountVO.class);
    }

    public UserInfoVO getUserByUserAccountAndSiteCode(String userAccount, String siteCode) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userAccount)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .one();
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public Boolean updateByUserAccount(String userAccount, BigDecimal firstDepositAmount) {
        LambdaUpdateWrapper<UserInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserInfoPO::getUserAccount, userAccount)
                .set(UserInfoPO::getFirstDepositTime, System.currentTimeMillis())
                .set(UserInfoPO::getFirstDepositAmount, firstDepositAmount);
        return this.update(null, lambdaUpdate);
    }

    public List<UserInfoVO> getUserInfoByAccountList(UserAccountListVO vo) {
        if (CollectionUtil.isEmpty(vo.getAccountList())) {
            return Lists.newArrayList();
        }
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>()
                .in(UserInfoPO::getUserAccount, vo.getAccountList()));
        return ConvertUtil.entityListToModelList(userInfoPOS, UserInfoVO.class);
    }


    /*public List<UserLoginInfoVO> getLatestLoginInfoByAccountList(UserAccountListVO vo) {
        List<UserLoginInfoPO> userLoginInfoPOS = userLoginInfoRepository.getLatestLoginInfoByAccountList(vo);
        if (CollectionUtil.isEmpty(userLoginInfoPOS)) {
            return null;
        }
        userLoginInfoPOS = userLoginInfoRepository.selectBatchIds(userLoginInfoPOS.stream().map(UserLoginInfoPO::getId).toList());
        return ConvertUtil.entityListToModelList(userLoginInfoPOS, UserLoginInfoVO.class);
    }*/

    public List<UserInfoVO> getUserInfoByUserIdsList(UserAccountListVO vo) {
        if (CollectionUtil.isEmpty(vo.getAccountList())) {
            return Lists.newArrayList();
        }
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>()
                .in(UserInfoPO::getUserId, vo.getAccountList()));
        return ConvertUtil.entityListToModelList(userInfoPOS, UserInfoVO.class);
    }

    public ResponseVO<UserVIPInfoVO> getUserVipInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        Integer handicapMode = CurrReqUtils.getHandicapMode();
        UserInfoPO userInfoPO = this.lambdaQuery().eq(UserInfoPO::getUserId, userId).one();
        if (ObjectUtils.isEmpty(userInfoPO)) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserVIPInfoVO responseVO = new UserVIPInfoVO();
        BeanUtils.copyProperties(userInfoPO, responseVO);

        Integer vipGradeCode = responseVO.getVipGradeCode();
        List<SiteVIPGradePO> vipGradePOS =
                siteVIPGradeService.list(Wrappers.<SiteVIPGradePO>lambdaQuery()
                        .eq(SiteVIPGradePO::getSiteCode, siteCode)
                        .in(SiteVIPGradePO::getVipGradeCode, Lists.newArrayList(responseVO.getVipGradeCode(), responseVO.getVipGradeUp())));
//        );

        // 用户总下注金额
        LambdaQueryWrapper<UserVIPFlowRecordPO> query = new LambdaQueryWrapper<>();
        query.eq(UserVIPFlowRecordPO::getUserId, userId);
        query.eq(UserVIPFlowRecordPO::getVipGradeCode, userInfoPO.getVipGradeCode());
        query.orderByDesc(UserVIPFlowRecordPO::getUpdatedTime);
        query.last("limit 1");
        UserVIPFlowRecordPO userVIPFlowRecordPO = userVIPFlowRecordRepository.selectOne(query);
        UserVIPFlowRecordPO lastPo = userVIPFlowRecordRepository.selectLastOne(userInfoPO.getUserId(),
                userInfoPO.getVipGradeCode());
        userVIPFlowRecordPO = null != lastPo ? lastPo : userVIPFlowRecordPO;
        if (null != userVIPFlowRecordPO) {
            responseVO.setCurrentExp(userVIPFlowRecordPO.getValidSumExe());
        }
        if (responseVO.getCurrentExp() == null) {
            responseVO.setCurrentExp(BigDecimal.ZERO);
        }
        if (CollectionUtil.isNotEmpty(vipGradePOS)) {
            Map<Integer, SiteVIPGradePO> code2po = vipGradePOS.stream().collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode, e -> e));
            SiteVIPGradePO currentVIP = code2po.get(vipGradeCode);
            SiteVIPGradePO upVIP = code2po.get(responseVO.getVipGradeUp());
            if (currentVIP != null && upVIP != null) {
                BigDecimal betAmountUpgrade = upVIP.getUpgradeXp();
                responseVO.setCurrentVipExp(betAmountUpgrade);
            }
        }

        // 根据站点查询所有VIP段位
        List<SiteVIPRankVO> siteVIPRankVOS = siteVIPRankService.getVipRankListBySiteCode(siteCode);
        // 根据站点查询所有VIP等级
        List<SiteVIPGradeVO> siteVIPGradeVOList = siteVIPGradeService.queryAllVIPGrade(siteCode);
        Map<Integer, String> siteVIPGradeNameMap = siteVIPGradeVOList.stream().collect(Collectors
                .toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName));
        // VIP等级名称赋值
        responseVO.setVipGradeName(siteVIPGradeNameMap.get(userInfoPO.getVipGradeCode()));
        responseVO.setVipGradeUpName(siteVIPGradeNameMap.get(userInfoPO.getVipGradeUp()));
        // 下一个等级对应的段位
        responseVO.setNextVipRank(siteVIPGradeVOList.stream().filter(obj -> obj.getVipGradeCode()
                .equals(userInfoPO.getVipGradeUp())).findFirst().get().getVipRankCode());
        Map<Integer, BigDecimal> siteVIPGradeMap = siteVIPGradeVOList.stream()
                .collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getUpgradeBonus));
        // 体育经验
        SiteVIPVenueExePO po = siteVIPVenueExeService.getOne(new LambdaQueryWrapper<SiteVIPVenueExePO>()
                .eq(SiteVIPVenueExePO::getSiteCode, siteCode).eq(SiteVIPVenueExePO::getVenueType,
                        VenueTypeEnum.SPORTS.getCode()));
        responseVO.setSportExe(po.getExperience());
        List<SiteVIPBenefitVO> siteVIPBenefitVOList = Lists.newArrayList();
        String minioDomain = fileService.getMinioDomain();
        siteVIPRankVOS.forEach(obj -> {
            SiteVIPBenefitVO benefitVO = new SiteVIPBenefitVO();
            List<SiteVIPWeekSportVO> sportVOS = Lists.newArrayList();
            BeanUtils.copyProperties(obj, benefitVO);
            benefitVO.setVipIconImage(minioDomain + "/" + obj.getVipIcon());
            //benefitVO.setVipIconImage(obj.getVipIconImage());
            benefitVO.setUpgradeFlag(userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            List<BigDecimal> totalUpgrade = IntStream.rangeClosed(obj.getMinVipGrade(), obj.getMaxVipGrade())
                    .mapToObj(siteVIPGradeMap::get).toList();
            benefitVO.setUpgrade(totalUpgrade.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            benefitVO.setLuckFlag(obj.getLuckFlag() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setLuckMinVipGradeName(siteVIPRankVOS.stream().filter(item -> item.getLuckFlag().toString()
                    .equals(YesOrNoEnum.YES.getCode())).min(Comparator
                    .comparing(SiteVIPRankVO::getVipRankCode)).orElse(new SiteVIPRankVO()).getMinVipGradeName());
            benefitVO.setEncryMinVipGradeName(siteVIPRankVOS.stream().filter(item -> item.getEncryCoinFee().toString()
                    .equals(YesOrNoEnum.YES.getCode())).min(Comparator
                    .comparing(SiteVIPRankVO::getVipRankCode)).orElse(new SiteVIPRankVO()).getMinVipGradeName());
            benefitVO.setWeekAmountFlag(obj.getWeekAmountFlag() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setWeekSportFlag(obj.getWeekSportFlag() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setMonthAmountFlag(obj.getMonthAmountFlag() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setSvipWelfareFlag(obj.getSvipWelfare() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setLuxuriousGiftsFlag(obj.getLuxuriousGifts() == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setEncryCoinFee(obj.getEncryCoinFee() == BigDecimal.ONE.intValue() ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            benefitVO.setRebateFlag(Optional.ofNullable(obj.getRebateConfig()).orElse(NONE) == NONE ? NONE : userInfoPO.getVipRank()
                    >= obj.getVipRankCode() ? UN_LOCK : LOCK);
            if (CollectionUtil.isNotEmpty(obj.getSportVos())) {
                sportVOS = IntStream.range(0, obj.getSportVos().size()).mapToObj(item -> {
                            var sportVo = obj.getSportVos().get(item);
                            if (sportVo != null) {
                                if (obj.getSportVos().size() > 1 && item == obj.getSportVos().size() - 1
                                        || obj.getSportVos().size() == 1) {
                                    return SiteVIPWeekSportVO.builder()
                                            .weekSportMin(sportVo.getWeekSportBetAmount())
                                            .weekSportMax(BigDecimal.ZERO)
                                            .weekSportBonus(sportVo.getWeekSportBonus())
                                            .build();
                                } else if (obj.getSportVos().size() > 1 && item != obj.getSportVos().size() - 1) {
                                    //获取右范围
                                    var nextSportVo = obj.getSportVos().get(item + 1);
                                    return SiteVIPWeekSportVO.builder()
                                            .weekSportMin(sportVo.getWeekSportBetAmount())
                                            .weekSportMax(nextSportVo != null
                                                    ? nextSportVo.getWeekSportBetAmount().subtract(new BigDecimal("0.01"))
                                                    : BigDecimal.ZERO)
                                            .weekSportBonus(sportVo.getWeekSportBonus())
                                            .build();
                                }
                            }
                            return null; // 或者可以选择其他处理方式
                        }).filter(Objects::nonNull) // 过滤掉可能返回的 null
                        .toList();
            }

            benefitVO.setVipWeekSportVOS(sportVOS);
            siteVIPBenefitVOList.add(benefitVO);
        });
        responseVO.setVipBenefit(siteVIPBenefitVOList);
        //组装当前段位和下一段位的图片颜色
        Integer vipRank = responseVO.getVipRank();
        Integer nextVipRank = responseVO.getNextVipRank();
        SiteVIPRankPO vipRankPO = siteVIPRankRepository.getVipRankByCodeAndSiteCode(siteCode, vipRank);
        // 根据
        if (vipRankPO != null) {
            responseVO.setVipIcon(fileService.getMinioDomain() + "/" + vipRankPO.getVipIcon());
            responseVO.setRankColor(vipRankPO.getRankColor());
        }

        SiteVIPRankPO nextVipRankPO = siteVIPRankRepository.getVipRankByCodeAndSiteCode(siteCode, nextVipRank);
        if (nextVipRankPO != null) {
            responseVO.setNextVipIcon(fileService.getMinioDomain() + "/" + nextVipRankPO.getVipIcon());
            responseVO.setNextRankColor(nextVipRankPO.getRankColor());
        }
        //添加反水配置 by mufan
        if (ObjectUtils.isNotEmpty(vipRankPO.getRebateConfig())) {
            responseVO.setRebateConfig(vipRankPO.getRebateConfig());
            if (vipRankPO.getRebateConfig().equals(Integer.parseInt(YesOrNoEnum.YES.getCode()))) {
                SiteRebateClientShowVO rebateReq = SiteRebateClientShowVO.builder().siteCode(siteCode).currencyCode(userInfoPO.getMainCurrency()).vipCode(vipRankPO.getVipRankCode()).build();
                responseVO.setRebates(BeanUtil.copyToList(siteRebateApi.webListPage(rebateReq).getData(), SiteRebateConfigWebCopyVO.class));
            }
        }
        //end by
        // 添加保级天数

       /* UserInfoVO userInfoVO = ConvertUtil.entityToModel(userInfoPO, UserInfoVO.class);

        UserVipFlowRecordCnVO userVipFlowRecordCnVO = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);
        if (ObjectUtils.isNotEmpty(userVipFlowRecordCnVO)) {
            responseVO.setRelegationDays(userVipFlowRecordCnVO.getRelegationDays());
        }*/
        return ResponseVO.success(responseVO);
    }

    public List<UserInfoVO> getUserInfoListByAccounts(List<String> account) {
        return null;
//        return vipRankService.getUserInfoListByAccounts(account);
    }

    /**
     * 替代 getUserInfoListByAccounts
     *
     * @param siteCode 站点code
     * @param accounts 用户账号
     * @return 返回
     */
    public List<UserInfoVO> getUserInfoListByAccounts(String siteCode, List<String> accounts) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getSiteCode, siteCode);
        queryWrapper.in(UserInfoPO::getUserAccount, accounts);
        return ConvertUtil.entityListToModelList(userInfoRepository.selectList(queryWrapper), UserInfoVO.class);
    }

    public Boolean getUserInfoIsExists(List<String> agentIds, String userAccount, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getSiteCode, siteCode);
        queryWrapper.eq(UserInfoPO::getUserAccount, userAccount);
        queryWrapper.in(UserInfoPO::getSuperAgentId, agentIds);
        return null == userInfoRepository.selectOne(queryWrapper);
    }

    public UserLoginInfoVO getLatestLoginInfoByUserIdForTask(String userId) {
        UserLoginInfoPO userLoginInfoPO = userLoginInfoRepository.getLatestLoginInfoByUserIdForTask(userId);
        if (userLoginInfoPO == null) {
            return null;
        }
        return ConvertUtil.entityToModel(userLoginInfoPO, UserLoginInfoVO.class);
    }

    public Long getByAgentId(String agentId) {
        return this.lambdaQuery().eq(UserInfoPO::getSuperAgentId, agentId).count();
    }

    public GetDirectUserListByAgentAndTimeResponse getDirectUserCountByAgentAndTime(GetDirectUserListByAgentAndTimeVO vo) {
        log.info("按照参数:{}获取新注册会员数及首存人数", vo);
        GetDirectUserListByAgentAndTimeResponse result = new GetDirectUserListByAgentAndTimeResponse();
        if (CollectionUtils.isEmpty(vo.getSuperAgentId())) {
            result.setNewUserNumber(0);
            result.setDirectReportNum(0);
            result.setFirstDepositNumber(0);
            result.setFirstDepositAmount(BigDecimal.ZERO);
            return result;
        }
        List<UserInfoPO> list = this
                .lambdaQuery()
                .in(UserInfoPO::getSuperAgentId, vo.getSuperAgentId())
                .list();

        // LambdaQueryWrapper<UserRegistrationInfoPO> regQuery = Wrappers.lambdaQuery();
        // regQuery.in(UserRegistrationInfoPO::getAgentId, vo.getSuperAgentId());
        // List<UserRegistrationInfoPO> userRegistrationInfoPOS = userRegistrationInfoRepository.selectList(regQuery);
        //注册时属于哪个代理,就算哪个代理的新注册会员
       /* if (!CollectionUtils.isEmpty(userRegistrationInfoPOS)) {
            for (UserRegistrationInfoPO userRegistrationInfoPO : userRegistrationInfoPOS) {
                //注册时间在档期范围内就算
                if (userRegistrationInfoPO.getCreatedTime() >= vo.getRegisterTimeStart()
                        && userRegistrationInfoPO.getCreatedTime() <= vo.getRegisterTimeEnd()) {
                    newUserNumber += 1;
                }
            }
        }*/


        int newUserNumber = 0;
        int firstDepositNumber = 0;
        BigDecimal firstDepositAmount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(list)) {
            for (UserInfoPO userInfoPO : list) {
                //注册时间在当前计算周期范围内 并且当前用户属于当前代理即可
                if (userInfoPO.getRegisterTime() >= vo.getRegisterTimeStart()
                        && userInfoPO.getRegisterTime() <= vo.getRegisterTimeEnd()) {
                    newUserNumber += 1;
                }

                //首存时间在档期范围内就算
                if (null != userInfoPO.getFirstDepositTime()
                        && userInfoPO.getFirstDepositTime() >= vo.getRegisterTimeStart()
                        && userInfoPO.getFirstDepositTime() <= vo.getRegisterTimeEnd()
                ) {
                    firstDepositNumber += 1;
                    firstDepositAmount = firstDepositAmount.add(userInfoPO.getFirstDepositAmount());
                }
            }
        }
        result.setDirectReportNum(list.size());
        //本期新注册会员数
        result.setNewUserNumber(newUserNumber);
        //本期首存人数
        result.setFirstDepositNumber(firstDepositNumber);
        //本期首存金额
        result.setFirstDepositAmount(firstDepositAmount);
        return result;
    }

    public ResponseVO updateAgentTransferInfo(String userAccount, String agentId, String agentAccount, Boolean isUpdateTransTime) {
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfoPO::getUserAccount, userAccount);
        updateWrapper.set(UserInfoPO::getSuperAgentId, agentId);
        updateWrapper.set(UserInfoPO::getSuperAgentAccount, agentAccount);
        updateWrapper.set(UserInfoPO::getBindingAgentTime, System.currentTimeMillis());
        updateWrapper.setSql("change_agent_count = change_agent_count + 1");
        if (isUpdateTransTime) {
            updateWrapper.setSql("trans_agent_time = IFNULL(trans_agent_time,0) + 1");
        }
        userInfoRepository.update(null, updateWrapper);
        return ResponseVO.success();
    }

    /**
     * 根据站点code,账号更新代理信息
     *
     * @param siteCode          站点code
     * @param userAccount       会员账号
     * @param agentId           代理id
     * @param agentAccount      代理账号
     * @param isUpdateTransTime 变更次数
     * @return void
     */
    public ResponseVO updateAgentTransferInfoBySiteCode(String siteCode, String userAccount, String agentId, String agentAccount, Boolean isUpdateTransTime) {
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfoPO::getUserAccount, userAccount).eq(UserInfoPO::getSiteCode, siteCode);
        updateWrapper.set(UserInfoPO::getSuperAgentId, agentId);
        updateWrapper.set(UserInfoPO::getSuperAgentAccount, agentAccount);
        updateWrapper.set(UserInfoPO::getBindingAgentTime, System.currentTimeMillis());
        updateWrapper.setSql("change_agent_count = change_agent_count + 1");
        if (isUpdateTransTime) {
            updateWrapper.setSql("trans_agent_time = IFNULL(trans_agent_time,0) + 1");
        }
        userInfoRepository.update(null, updateWrapper);
        return ResponseVO.success();
    }

    public UserInfoVO findUserReviewExist(UserCheckExistReqVO reqVO) {
        return userInfoRepository.findUserReviewExist(reqVO);
    }

    public boolean checkUserExist(UserCheckExistReqVO reqVO) {
        // 根据邮箱或手机号判断是否已经存在
        if (null != userReviewService.findUserReviewExist(reqVO) || null != this.findUserReviewExist(reqVO)) {
            return true;
        }
        return false;
    }

    public UserInfoVO getByMail(String email) {
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getEmail, email).one();
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public UserInfoVO getByPhone(String phone) {
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getPhone, phone).one();
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public UserInfoVO getUserInfoByQueryVO(UserQueryVO userQueryVO) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(userQueryVO.getUserAccount()), UserInfoPO::getUserAccount, userQueryVO.getUserAccount());
        queryWrapper.eq(StringUtils.isNotEmpty(userQueryVO.getEmail()), UserInfoPO::getEmail, userQueryVO.getEmail());
        queryWrapper.eq(StringUtils.isNotEmpty(userQueryVO.getPhone()), UserInfoPO::getPhone, userQueryVO.getPhone());
        queryWrapper.eq(UserInfoPO::getSiteCode, userQueryVO.getSiteCode());

        UserInfoPO userInfoPO = userInfoRepository.selectOne(queryWrapper);
        return ConvertUtil.entityToModel(userInfoPO, UserInfoVO.class);
    }

    public void resetPassword(ResetPasswordVO resetPasswordVO) {
        UserQueryVO queryVO = UserQueryVO.builder().userAccount(resetPasswordVO.getUserAccount())
                .siteCode(resetPasswordVO.getSiteCode()).build();
        UserInfoVO userInfoVO = this.getUserInfoByQueryVO(queryVO);


        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(resetPasswordVO.getNewPassword(), salt);

        userInfoRepository.update(null, Wrappers.<UserInfoPO>lambdaUpdate()
                .eq(UserInfoPO::getId, userInfoVO.getId())
                .set(UserInfoPO::getUpdatedTime, System.currentTimeMillis())
                .set(UserInfoPO::getPassword, encryptPassword));
    }

    public List<UserInfoVO> getUserInfoByPhoneList(String phone) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getPhone, phone);
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(queryWrapper);
        List<UserInfoVO> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(userInfoPOS)) {
            for (UserInfoPO userInfoPO : userInfoPOS) {
                UserInfoVO vo = new UserInfoVO();
                BeanUtils.copyProperties(userInfoPO, vo);
                list.add(vo);
            }
        }
        return list;
    }

    public UserTeamVO getTeamUserInfo(GetTeamUserInfoParam requestVo) {
        String agentId = requestVo.getAgentId();
        String siteCode = requestVo.getSiteCode();
        Map<String, BigDecimal> allRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        // 所有
        List<String> allDownAgentNum = requestVo.getAllDownAgentNum();
        UserTeamVO vo = new UserTeamVO();
        // 当天开始时间
        long startTime = DateUtils.getTodayStartTime(requestVo.getTimeZone());
        // 当天结束时间
        long endTime = DateUtils.getTodayEndTime(requestVo.getTimeZone());


        // 下级会员人数
        final List<String> allUserNum = Lists.newArrayList();
        allUserNum.add(agentId);
        allUserNum.addAll(allDownAgentNum);
        LambdaQueryWrapper<UserInfoPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfoPO::getSuperAgentId, allUserNum);
        //下级会员
        vo.setUnderUserCount(userInfoRepository.selectCount(wrapper));

        // mysql 查询=
        AgentUserTeamParam param = new AgentUserTeamParam();
        // 除了下级和直属，其他的字段查询包含下级和直属
        allDownAgentNum.add(agentId);
        param.setAllDownAgentNum(allDownAgentNum);
        if (allDownAgentNum.size() > 0) {
            // 返回首存人数与首存金额
            List<AgentAllDownUserVO> agentAllDownUserVOs = userInfoRepository.queryTeamUserInfo(param);
            if (!CollectionUtils.isEmpty(agentAllDownUserVOs)) {
                Long firstDepositUserCount = 0L;
                BigDecimal firstDepositAmount = BigDecimal.ZERO;
                for (AgentAllDownUserVO agentAllDownUserVO : agentAllDownUserVOs) {
                    if (agentAllDownUserVO.getFirstDepositTime() != null) {
                        firstDepositUserCount++;
                        BigDecimal currencyRate = allRateMap.get(agentAllDownUserVO.getMainCurrency());
                        firstDepositAmount = firstDepositAmount.add(AmountUtils.divide(agentAllDownUserVO.getFirstDepositAmount(), currencyRate));
                    }
                }
                // 首存人数
                vo.setFirstDepositCount(firstDepositUserCount);
                // 首存金额
                vo.setFirstDepositAmount(firstDepositAmount);
            }
        }
        // 直属会员人数
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getSuperAgentId, agentId);
        vo.setDirectlyUserCount(userInfoRepository.selectCount(queryWrapper));

        //今日新注册会员人数
        param = new AgentUserTeamParam();
        param.setStartTime(startTime);
        param.setEndTime(endTime);
        param.setAllDownAgentNum(allDownAgentNum);
        if (allDownAgentNum.size() > 0) {
            List<String> todayUserAccounts = userInfoRepository.selectAddUser(param);
            vo.setTodayAddCount((long) todayUserAccounts.size());
        }


        //本期新注册会员人数
        param = new AgentUserTeamParam();
        param.setStartTime(requestVo.getStartTime());
        param.setEndTime(requestVo.getEndTime());
        param.setAllDownAgentNum(allDownAgentNum);
        if (allDownAgentNum.size() > 0) {
            List<String> monthUserAccounts = userInfoRepository.selectAddUser(param);
            vo.setMonthAddCount((long) monthUserAccounts.size());
        }

        return vo;
    }

    private List<String> intersection(final List<String> nowList, final List<String> beforeList) {
        List<String> retainList = Lists.newArrayList();
        retainList.addAll(nowList);
        // 取出nowList和beforeList交集
        retainList.retainAll(beforeList);
        List<String> newNow = new ArrayList<>(nowList);
        List<String> intersection = new ArrayList<>(retainList);
        // 用合集去掉交集
        if (newNow.size() > 0 && intersection.size() > 0) {
            newNow.removeAll(intersection);
        }
        return newNow;
    }

    /**
     * 指定代理的所有会员
     */
    private List<String> getUserAccountByAgentId(final List<String> allDownAgentNum) {
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfoPO::getSuperAgentId, allDownAgentNum);
        return userInfoRepository.selectList(queryWrapper).stream()
                .map(UserInfoPO::getUserAccount).toList();
    }

    public UserInfoVO queryUserInfoByAccount(String account) {
        UserInfoPO po = userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getUserAccount, account));
        UserInfoVO vo = ConvertUtil.entityToModel(po, UserInfoVO.class);
        if (Objects.isNull(vo)) {
            return null;
        }
        // 风控层级
        if (StringUtils.isNotBlank(vo.getRiskLevelId())) {
            RiskLevelDetailsVO riskLevelDetailsVO = riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
            vo.setRiskLevel(Objects.isNull(riskLevelDetailsVO) ? null : riskLevelDetailsVO.getRiskControlLevel());
        }
        return vo;
    }

    public Boolean updateByUserId(String userId, BigDecimal firstDepositAmount) {
        LambdaUpdateWrapper<UserInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserInfoPO::getUserId, userId)
                .set(UserInfoPO::getFirstDepositTime, System.currentTimeMillis())
                .set(UserInfoPO::getFirstDepositAmount, firstDepositAmount);
        return this.update(null, lambdaUpdate);
    }

    public ResponseVO<IndexVO> getUserBalance(String userId, String userAccount, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInfoPO::getUserId, userId);
        UserInfoPO userInfoPO = userInfoRepository.selectOne(lqw);
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserId(userId);
        userCoinQueryVO.setCurrencyCode(userInfoPO.getMainCurrency());
        userCoinQueryVO.setSiteCode(siteCode);
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoin(userCoinQueryVO);
        IndexVO indexVO = new IndexVO();
        indexVO.setBalance(userCoinWalletVO.getCenterAmount());
        indexVO.setFreezeAmount(userCoinWalletVO.getCenterFreezeAmount());
        UserPlatformCoinWalletVO userPlatformCoinWalletVO = userPlatformCoinApi.getUserPlatformCoin(userCoinQueryVO);
        indexVO.setPlatAvailableAmount(userPlatformCoinWalletVO.getCenterAmount());
        return ResponseVO.success(indexVO);
    }

    public UserInfoVO getInfoByUserAccount(String userAccount) {
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getUserAccount, userAccount).one();
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public UserInfoVO getInfoByUserAccountAndSite(String userAccount, String siteCode) {
        UserInfoPO one = this.lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userAccount)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .one();
        log.info("userInfo:{}", one);
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    public boolean getUserInfoByNickNameExist(String name) {
        return this.lambdaQuery().eq(UserInfoPO::getNickName, name).count() > 0;
    }

    public Boolean updateInfo(UserEditVO editVO) {
        /*ResponseVO<SiteUserAvatarConfigRespVO> resp = avatarConfigService.getAvatarConfigByTXIdSiteCode(editVO.getSiteCode(), editVO.getAvatarCode());
        if (resp.isOk()) {
            SiteUserAvatarConfigRespVO data = resp.getData();
            if (data == null) {
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
        }*/
        LambdaUpdateWrapper<UserInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserInfoPO::getUserAccount, editVO.getUserAccount())
                .eq(UserInfoPO::getSiteCode, editVO.getSiteCode())
                .set(ObjectUtil.isNotEmpty(editVO.getEmail()), UserInfoPO::getEmail, editVO.getEmail())
                .set(ObjectUtil.isNotEmpty(editVO.getAreaCode()), UserInfoPO::getAreaCode, editVO.getAreaCode())
                .set(ObjectUtil.isNotEmpty(editVO.getPhone()), UserInfoPO::getPhone, editVO.getPhone())
                .set(ObjectUtil.isNotEmpty(editVO.getMailStatus()), UserInfoPO::getMailStatus, editVO.getMailStatus())
                .set(ObjectUtil.isNotEmpty(editVO.getPhoneStatus()), UserInfoPO::getPhoneStatus, editVO.getPhoneStatus())
                .set(ObjectUtil.isNotEmpty(editVO.getPassword()), UserInfoPO::getPassword, editVO.getPassword())
                .set(ObjectUtil.isNotEmpty(editVO.getNickName()), UserInfoPO::getNickName, editVO.getNickName())
                .set(editVO.getAvatarCode() != null, UserInfoPO::getAvatarCode, editVO.getAvatarCode())
                .set(ObjectUtil.isNotEmpty(editVO.getAvatar()), UserInfoPO::getAvatar, editVO.getAvatar())
                .set(ObjectUtil.isNotEmpty(editVO.getWithdrawPwd()), UserInfoPO::getWithdrawPwd, editVO.getWithdrawPwd());
        return this.update(null, lambdaUpdate);
    }

    /**
     * 代理客户端-新注册人数 按天统计
     */

    public List<GetRegisterStatisticsByAgentIdVO> getRegisterStatisticsByAgentId(Long start,
                                                                                 Long end,
                                                                                 String agentId,
                                                                                 String dbZone
    ) {
        return userInfoRepository.getRegisterStatisticsByAgentId(start, end, agentId, dbZone);
    }

    /**
     * 代理客户端-首存人数 按天统计
     */

    public List<GetFirstDepositStatisticsByAgentIdVO> getFirstDepositStatisticsByAgentId(Long start,
                                                                                         Long end,
                                                                                         String agentId,
                                                                                         String dbZone
    ) {
        return userInfoRepository.getFirstDepositStatisticsByAgentId(start, end, agentId, dbZone);
    }

    public ResponseVO<SelectUserDetailResponseVO> selectUserDetail(SelectUserDetailParam vo) {
        // 跨度不超过三个月的统计
        //long time = DateUtil.beginOfMonth(DateUtil.lastMonth()).getTime();
        Integer time = 0;
        List<String> betweenDates = TimeZoneUtils.getBetweenDates(vo.getRegisterStartTime(), vo.getRegisterEndTime(), vo.getTimeZone());
        if (CollectionUtil.isNotEmpty(betweenDates)) {
            time = betweenDates.size();
        }
        if (time > 60) {
            return ResponseVO.fail(ResultCode.SUBORDINATE_USER_LIST_2MONTH);
        }

        GetByUserAccountVO userInfo = this.getByUserAccount(vo.getUserAccount(), vo.getSiteCode());
        if (null == userInfo) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        SelectUserDetailResponseVO result = BeanUtil.copyProperties(userInfo, SelectUserDetailResponseVO.class);
        result.setAcountRemark(userInfo.getAgentRemark());
        log.info("代理端会员详情默认脱敏===>>");
        if (StringUtils.isNotBlank(result.getPhone())) {
            List<String> tempList = new ArrayList<>();
            if (StringUtils.isNotBlank(result.getAreaCode())) {
                tempList.add(result.getAreaCode());
            }
            if (StringUtils.isNotBlank(result.getPhone())) {
                tempList.add(SymbolUtil.showPhone(result.getPhone()));
            }
            String phone = StringUtils.join(tempList, " ");
            result.setPhone(phone);
        }
        if (StringUtils.isNotBlank(result.getEmail())) {
            result.setEmail(SymbolUtil.showEmail(result.getEmail()));
        }
//        result.setAccountRemark(userInfo.getAgentRemark());
        // vip当前等级 - Name
        List<SiteVIPGradeVO> siteVIPGradeVOS = siteVIPGradeService.queryAllVIPGrade(vo.getSiteCode());
        String vipGradeName = siteVIPGradeVOS.stream()
                .filter(e -> e.getVipGradeCode().equals(result.getVipGradeCode()))
                .findFirst()
                .map(SiteVIPGradeVO::getVipGradeName)
                .orElse("");
        result.setVipGradeCodeName(vipGradeName);
        result.setPlatCurrencyCode(CurrReqUtils.getPlatCurrencyName());
        result.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());

        // 是否活跃 0非活跃 1活跃
        AgentUserTeamParam param = new AgentUserTeamParam();
        param.setStartTime(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()));
        param.setEndTime(TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()));
        param.setAllDownAgentNum(Lists.newArrayList(vo.getAgentId()));
        //  wade 等方飞
        Boolean activeFlag = agentCommissionApi.userActiveValidate(userInfo.getUserId());
        if (activeFlag) {
            result.setIsActive(CommonConstant.business_one);
            result.setIsActiveName("活跃");
        } else {
            result.setIsActive(CommonConstant.business_zero);
            result.setIsActiveName("非活跃");
        }

        // 存款
        // 查询客户端存款
        GetAllArriveAmountByAgentUserResponseVO depositAmountClient = userDepositWithdrawApi.getAllArriveAmountByAgentUser(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                vo.getUserAccount(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime(),
                CommonConstant.business_one);
        // 查询客户端取款
        GetAllArriveAmountByAgentUserResponseVO withdrawAmountClient = userDepositWithdrawApi.getAllArriveAmountByAgentUser(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                vo.getUserAccount(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime(),
                CommonConstant.business_two);
        // 代理代存
        GetAgentDepositAmountByAgentVO agentDepositAmount = agentDepositSubordinatesApi.getAgentDepositAmountByAgent(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                vo.getUserAccount(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime());

        // 人工加额 - 会员存款(后台) & 会员提款(后台)
        GetDepositWithdrawManualRecordListResponse userDepositsManual = userManualUpRecordApi.getDepositWithdrawManualRecordList(
                GetDepositWithdrawManualRecordListVO.builder()
                        .siteCode(vo.getSiteCode())
                        .start(vo.getRegisterStartTime())
                        .end(vo.getRegisterEndTime())
                        .userAccount(vo.getUserAccount())
                        .agentAccount(vo.getAgentAccount())
                        .build());


        //人工加额
        Map<String, BigDecimal> depositAmountMap = userDepositsManual.getDepositAmount();
        //人工减额
        Map<String, BigDecimal> withdrawAmountMap = userDepositsManual.getWithdrawAmount();

        BigDecimal agentDeposit = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(agentDepositAmount.getAgentDepositAmountMap())) {
            //代理代存金额
            agentDeposit = agentDepositAmount.getAgentDepositAmountMap().get(vo.getUserAccount());
        }
        BigDecimal allDepositAmount = depositAmountClient.getArriveAmount();
        allDepositAmount = allDepositAmount.add(agentDeposit);
        if (CollectionUtil.isNotEmpty(depositAmountMap)) {
            //代理人工加额金额
            allDepositAmount = allDepositAmount.add(depositAmountMap.get(vo.getUserAccount()));
        }
        //会员总存款金额=代存金额+人工加额金额
        result.setAllDepositAmount(allDepositAmount);


        //人工减额
        BigDecimal allWithdrawAmount = BigDecimal.ZERO;
        //会员提款金额
        BigDecimal userWithdrawApplyAmount = withdrawAmountClient.getApplyAmount();
        allWithdrawAmount = allWithdrawAmount.add(userWithdrawApplyAmount);
        if (CollectionUtil.isNotEmpty(withdrawAmountMap)) {
            allWithdrawAmount = allWithdrawAmount.add(withdrawAmountMap.get(vo.getUserAccount()));
        }
        // 取款总额= 会员提款+人工减额
        result.setAllWithdrawAmount(allWithdrawAmount);

        List<GetBetNumberByAgentIdVO> reportUserWinLose = reportUserWinLoseApi.getBetNumberByAgentId(
                vo.getSiteCode(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime(),
                vo.getAgentId(),
                userInfo.getUserId());
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal validBetAmount = BigDecimal.ZERO;
        BigDecimal betAmount = BigDecimal.ZERO;
        BigDecimal betWinLose = BigDecimal.ZERO;
        BigDecimal profitAndLoss = BigDecimal.ZERO;
        BigDecimal activityAmount = BigDecimal.ZERO;
        BigDecimal vipAmount = BigDecimal.ZERO;
        BigDecimal alreadyUseAmount = BigDecimal.ZERO;
        BigDecimal rebateAmount = BigDecimal.ZERO;
        BigDecimal tipsAmount = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(reportUserWinLose)) {
            discountAmount = reportUserWinLose.get(0).getDiscountAmount();
            validBetAmount = reportUserWinLose.get(0).getValidBetAmount();
            betAmount = reportUserWinLose.get(0).getBetAmount();
            betWinLose = reportUserWinLose.get(0).getBetWinLose();
            profitAndLoss = reportUserWinLose.get(0).getProfitAndLoss();
            activityAmount = reportUserWinLose.get(0).getActivityAmount();
            vipAmount = reportUserWinLose.get(0).getVipAmount();
            alreadyUseAmount = reportUserWinLose.get(0).getAlreadyUseAmount();
            rebateAmount = reportUserWinLose.get(0).getRebateAmount();
            tipsAmount = reportUserWinLose.get(0).getTipsAmount();
        }
        result.setDiscountAmount(discountAmount);
        result.setValidBetAmount(validBetAmount);
        result.setBetAmount(betAmount);
        // 总输赢=总输赢-打赏 33885 bug 取反
        result.setBetWinLose(betWinLose.subtract(tipsAmount).negate());
        result.setProfitAndLoss(profitAndLoss.negate());
        result.setActivityAmount(activityAmount);
        result.setVipAmount(vipAmount);
        result.setAlreadyUseAmount(alreadyUseAmount);
        result.setRebateAmount(rebateAmount);

        // 会员标签
        List<GetLabelsByAgentAccountVO> userLabelsList =
                agentLabelManageApi.getLabelsByAgentAccount(vo.getSiteCode(), vo.getAgentAccount(), vo.getUserAccount());
        result.setUserLabels(userLabelsList.stream().map(GetLabelsByAgentAccountVO::getLabel).toList());

        return ResponseVO.success(result);
    }

    public ResponseVO<Page<SubordinateUserListResponseVO>> subordinateUserList(SubordinateUserListParam vo) {
        // 仅支持最近两个月的统计
        Integer time = 0;
        List<String> betweenDates = TimeZoneUtils.getBetweenDates(vo.getRegisterStartTime(), vo.getRegisterEndTime(), vo.getTimeZone());
        if (CollectionUtil.isNotEmpty(betweenDates)) {
            time = betweenDates.size();
        }
        if (time > 90) {
            return ResponseVO.fail(ResultCode.FORTY_DAY_OVER);
        }

        // 无该用户信息
        if (StrUtil.isNotEmpty(vo.getUserAccount())) {
            GetByUserAccountVO userInfo = this.getByUserAccount(vo.getUserAccount(), vo.getSiteCode());
            if (null == userInfo) {
                return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST_);
            } else {
                //获取到了用户信息,判断一下是否是其他代理的用户,以及这个用户是否是下级
                if (!vo.getAgentAccount().equals(userInfo.getSuperAgentAccount())) {
                    return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST_);
                }
            }
        }
        // 查询的下级直属会员信息
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getSiteCode, vo.getSiteCode());
        queryWrapper.eq(UserInfoPO::getSuperAgentId, vo.getAgentId());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getUserAccount()), UserInfoPO::getUserAccount, vo.getUserAccount());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getCurrencyCode()), UserInfoPO::getMainCurrency, vo.getCurrencyCode());
        Long registerStartTime = vo.getRegisterStartTime();
        Long registerEndTime = vo.getRegisterEndTime();
        /*if (registerStartTime != null) {
            queryWrapper.ge(UserInfoPO::getRegisterTime, registerStartTime);
        }
        if (registerEndTime != null) {
            queryWrapper.le(UserInfoPO::getRegisterTime, registerEndTime);
        }*/
        // 排序
        queryWrapper.orderByDesc(UserInfoPO::getRegisterTime);

        Page<UserInfoPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserInfoPO> pageList = this.page(page, queryWrapper);
        log.info("代理客户端-下级会员列表-pageList:{}", pageList.getRecords());

        // 查询会员盈亏
        List<GetBetNumberByAgentIdVO> allUserAccounts =
                reportUserWinLoseApi.getBetNumberByAgentId(vo.getSiteCode(), vo.getRegisterStartTime(), vo.getRegisterEndTime(), vo.getAgentId(), null);

        // 查询客户端充值
        List<GetAllArriveAmountByAgentIdVO> userDeposits = userDepositWithdrawApi.getAllArriveAmountByAgentId(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime());
        // 查询客户端提款
        List<GetAllWithdrawAmountByAgentIdVO> userWithdraws = userDepositWithdrawApi.getAllWithdrawAmountByAgentId(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime());
        // 人工加额 - 会员存款(后台) & 会员提款(后台)
        GetDepositWithdrawManualRecordListResponse userDepositsManual = userManualUpRecordApi.getDepositWithdrawManualRecordList(
                GetDepositWithdrawManualRecordListVO.builder()
                        .siteCode(vo.getSiteCode())
                        .start(vo.getRegisterStartTime())

                        .end(vo.getRegisterEndTime())
                        .agentAccount(vo.getAgentAccount())
                        .build());
        // 代理代存
        GetAgentDepositAmountByAgentVO agentDepositVO = agentDepositSubordinatesApi.getAgentDepositAmountByAgent(
                vo.getSiteCode(),
                vo.getAgentAccount(),
                null,
                vo.getRegisterStartTime(),
                vo.getRegisterEndTime());

        List<GetLabelsByAgentAccountVO> agentUserLabels = agentLabelManageApi.getLabelsByAgentAccount(vo.getSiteCode(), vo.getAgentAccount(), null);
        List<SiteVIPGradeVO> siteVIPGradeVOS = siteVIPGradeService.queryAllVIPGrade(vo.getSiteCode());
        Map<Integer, String> vipCode2Name = siteVIPGradeVOS.stream().collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (o, n) -> o));
        List<SubordinateUserListResponseVO> list = pageList.getRecords().stream().map(item -> {
            SubordinateUserListResponseVO record =
                    ConvertUtil.entityToModel(item, SubordinateUserListResponseVO.class);

            record.setAcountRemark(item.getAgentRemark());
            if (item.getVipGradeCode() != null) {
                record.setVipGradeCode(item.getVipGradeCode());
                record.setVipGradeCodeName(vipCode2Name.get(item.getVipGradeCode()));
            }

            if (StrUtil.isNotEmpty(record.getUserName())) {
                record.setIsRealName("已实名");
            } else {
                record.setIsRealName("未实名");
            }
            if (record.getPhone() != null) {
                record.setPhone(SymbolUtil.showPhone(record.getPhone()));
            }
            if (record.getEmail() != null) {
                record.setEmail(SymbolUtil.showEmail(record.getEmail()));
            }
            // 总输赢
            BigDecimal betWinLose = BigDecimal.ZERO;

            // 添加会员有效投注
            BigDecimal validBetAmount = BigDecimal.ZERO;
            // 打赏
            BigDecimal tipsAmount = BigDecimal.ZERO;
            for (GetBetNumberByAgentIdVO userAccount : allUserAccounts) {
                if (record.getUserAccount().equals(userAccount.getUserAccount())) {
                    betWinLose = userAccount.getBetWinLose();
                    tipsAmount = userAccount.getTipsAmount();
                    // 添加会员有效投注
                    validBetAmount = userAccount.getValidBetAmount();

                    break;
                }
            }
            // 33885 bug单。取反
            record.setBetWinLose(betWinLose.subtract(tipsAmount).negate());
            record.setValidBetAmount(validBetAmount);


            // 存款
            BigDecimal allDepositAmount = BigDecimal.ZERO;
            for (GetAllArriveAmountByAgentIdVO userDeposit : userDeposits) {
                if (record.getUserAccount().equals(userDeposit.getUserAccount())) {
                    allDepositAmount = allDepositAmount.add(userDeposit.getArriveAmount());
                    break;
                }
            }
            if (CollectionUtil.isNotEmpty(userDepositsManual.getDepositAmount())) {
                for (Map.Entry<String, BigDecimal> userDeposit : userDepositsManual.getDepositAmount().entrySet()) {
                    String userAccount = userDeposit.getKey();
                    BigDecimal depositAmount = userDeposit.getValue();
                    if (record.getUserAccount().equals(userAccount)) {
                        allDepositAmount = allDepositAmount.add(depositAmount);
                        break;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(agentDepositVO.getAgentDepositAmountMap())) {
                for (Map.Entry<String, BigDecimal> agentDeposit : agentDepositVO.getAgentDepositAmountMap().entrySet()) {
                    String userAccount = agentDeposit.getKey();
                    BigDecimal agentDepositAmount = agentDeposit.getValue();
                    if (record.getUserAccount().equals(userAccount)) {
                        allDepositAmount = allDepositAmount.add(agentDepositAmount);
                        break;
                    }
                }
            }
            record.setAllDepositAmount(allDepositAmount);

            // 取款
            BigDecimal allWithdrawAmount = BigDecimal.ZERO;
            for (GetAllWithdrawAmountByAgentIdVO userWithdraw : userWithdraws) {
                if (record.getUserAccount().equals(userWithdraw.getUserAccount())) {
                    allWithdrawAmount = allWithdrawAmount.add(userWithdraw.getApplyAmount());
                    break;
                }
            }
            for (Map.Entry<String, BigDecimal> userWithdraw : userDepositsManual.getWithdrawAmount().entrySet()) {
                String userAccount = userWithdraw.getKey();
                BigDecimal withdrawAmount = userWithdraw.getValue();
                if (record.getUserAccount().equals(userAccount)) {
                    allWithdrawAmount = allWithdrawAmount.add(withdrawAmount);
                    break;
                }
            }
            record.setAllWithdrawAmount(allWithdrawAmount);

            // 分配次数
            Long transAgentTime = agentDepositVO.getTransAgentTime().get(record.getUserAccount());
            record.setTransAgentTime(null == transAgentTime ? 0 : transAgentTime);

            // 会员标签
            Set<String> userLabels = new HashSet<>();
            for (GetLabelsByAgentAccountVO agentUserLabel : agentUserLabels) {
                if (record.getUserAccount().equals(agentUserLabel.getUserAccount())) {
                    userLabels.add(agentUserLabel.getLabel());
                }
            }
            record.setUserLabels(userLabels.stream().toList());

            return record;
        }).toList();

        log.info("代理客户端-下级会员列表Result-list:{}", list);
        return ResponseVO.success(
                new Page<SubordinateUserListResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list));
    }


    /*public void updateOfflineDaysTask() {
        userInfoRepository.updateOfflineDaysTask(System.currentTimeMillis());
    }*/

    public ResponseVO<UserVIPFlowRecordVO> getUserVipFlowLastOne(String userAccount, String siteCode) {
        UserVIPFlowRecordVO result = UserVIPFlowRecordVO.builder().build();
        GetByUserAccountVO userAccountVO = getByUserAccount(userAccount, siteCode);
        UserVIPFlowRecordPO userVIPFlowRecordPO = userVIPFlowRecordRepository.selectLastOne(userAccountVO.getUserAccount(), userAccountVO.getVipGradeCode());
        if (ObjectUtil.isEmpty(userVIPFlowRecordPO)) {
            return ResponseVO.success();
        }
        BeanUtils.copyProperties(userVIPFlowRecordPO, result);
        return ResponseVO.success(result);
    }

    public ResponseVO<?> agentEditRemark(EditRemarkParam vo) {
        log.info("agentEditRemark:{}", JSON.toJSONString(vo));
        UserInfoPO userInfo = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getSiteCode, vo.getSiteCode()).eq(UserInfoPO::getUserAccount, vo.getUserAccount()));
        if (null == userInfo) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        userInfo.setAgentRemark(vo.getAcountRemark());
        userInfoRepository.updateById(userInfo);

        return ResponseVO.success();
    }

    public List<UserInfoPO> selectByUserIds(List<String> userIds) {
        LambdaQueryWrapper<UserInfoPO> lqw = new LambdaQueryWrapper<UserInfoPO>();
        lqw.in(UserInfoPO::getUserId, userIds);
        return userInfoRepository.selectList(lqw);
    }

    public ResponseVO<UserIndexInfoVO> getIndexInfo(String userId, String userAccount, String siteCode, String timezone) {

        UserInfoPO userInfoPO = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userAccount)
                .eq(UserInfoPO::getSiteCode, siteCode));
        if (userInfoPO == null) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        Integer handicapMode = CurrReqUtils.getHandicapMode();

        userCommonService.sendLoginCount(userInfoPO.getUserId(), userInfoPO.getSiteCode(), userInfoPO.getUserAccount(), CurrReqUtils.getTimezone());

        UserIndexInfoVO userIndexInfoVO = BeanUtil.copyProperties(userInfoPO, UserIndexInfoVO.class);
        userIndexInfoVO.setPlatCurrencyCode(CurrReqUtils.getPlatCurrencyCode());
        userIndexInfoVO.setPlatCurrencyName(CurrReqUtils.getPlatCurrencyName());
        userIndexInfoVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
        userIndexInfoVO.setUserId(userInfoPO.getUserId());
        TimeZone timeZone = TimeZone.getTimeZone(CurrReqUtils.getTimezone());
        Date registerDate = new Date(userInfoPO.getRegisterTime());
        Date currentDate = new Date();
        // 使用时区调整
        DateTime registerDateTime = DateUtil.date(registerDate).setTimeZone(timeZone);
        DateTime currentDateTime = DateUtil.date(currentDate).setTimeZone(timeZone);
        userIndexInfoVO.setJoinDays(DateUtil.betweenDay(DateUtil.beginOfDay(registerDateTime), DateUtil.beginOfDay(currentDateTime), true) + 1);
        userIndexInfoVO.setAreaCode(userInfoPO.getAreaCode());
        userIndexInfoVO.setPhone(userInfoPO.getPhone());
        // vip
        List<SiteVIPGradeVO> siteVIPGradeVOS = siteVIPGradeService.queryAllVIPGrade(siteCode);
        SiteVIPGradeVO siteVIPGradeVO = null;
        if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
            Map<Integer, SiteVIPGradeVO> code2Grade = siteVIPGradeVOS.stream().distinct().collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, e -> e));

            Integer vipGradeUp = userInfoPO.getVipGradeUp();
            siteVIPGradeVO = code2Grade.get(vipGradeUp);
            // 当前VIP段位和下一个等级的VIP段位
            userIndexInfoVO.setVipRank(code2Grade.get(userInfoPO.getVipGradeCode()).getVipRankCode());
            userIndexInfoVO.setNextVipRank(code2Grade.get(userInfoPO.getVipGradeUp()).getVipRankCode());
            userIndexInfoVO.setVipGradeName(code2Grade.get(userInfoPO.getVipGradeCode()).getVipGradeName());
            userIndexInfoVO.setVipGradeUpName(code2Grade.get(userInfoPO.getVipGradeUp()).getVipGradeName());
            userIndexInfoVO.setCurrentVipExp(code2Grade.get(userInfoPO.getVipGradeCode()).getUpgradeXp());
        }
        // 区分大陆盘与国际盘
        if (SiteHandicapModeEnum.Internacional.getCode().equals(handicapMode)) {
            LambdaQueryWrapper<UserVIPFlowRecordPO> query = new LambdaQueryWrapper<>();
            query.eq(UserVIPFlowRecordPO::getUserId, userInfoPO.getUserId());
            query.eq(UserVIPFlowRecordPO::getVipGradeCode, userInfoPO.getVipGradeCode());
            query.orderByDesc(UserVIPFlowRecordPO::getUpdatedTime);
            query.last("limit 1");
            UserVIPFlowRecordPO userVIPFlowRecordPO = userVIPFlowRecordRepository.selectOne(query);
            UserVIPFlowRecordPO lastPo = userVIPFlowRecordRepository.selectLastOne(userInfoPO.getUserId(),
                    userInfoPO.getVipGradeCode());
            userVIPFlowRecordPO = null != lastPo ? lastPo : userVIPFlowRecordPO;
            userIndexInfoVO.setCurrentExperience(BigDecimal.ZERO);
            if (null != userVIPFlowRecordPO) {
                userIndexInfoVO.setCurrentExperience(userVIPFlowRecordPO.getValidSumExe());
            }
            if (siteVIPGradeVO != null) {
                userIndexInfoVO.setVipGradeUp(siteVIPGradeVO.getVipGradeCode());
                userIndexInfoVO.setNextExperience(siteVIPGradeVO.getUpgradeXp());
            }
            userIndexInfoVO.setWelfareNotClaimNums(welfareCenterService.getWaitReceiveByUserId(userId));

        } else {
            // 保级/降级 天数
            UserInfoVO userInfoVO = ConvertUtil.entityToModel(userInfoPO, UserInfoVO.class);
            UserVipFlowRecordCnVO userVipFlow = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);
            if (null != userVipFlow) {
                // 当前流水
                userIndexInfoVO.setCurrentExperience(userVipFlow.getFinishBetAmount());
                // 下一等级
                userIndexInfoVO.setVipGradeUp(siteVIPGradeVO.getVipGradeCode());
                // 下一等级需要的金额
                userIndexInfoVO.setNextExperience(userVipFlow.getUpgradeBetAmount());
                //
                userIndexInfoVO.setRelegationDays(userVipFlow.getRelegationDays());
                // 剩余等级
                userIndexInfoVO.setLeftExperience(userIndexInfoVO.getNextExperience().subtract(userVipFlow.getFinishBetAmount()).max(BigDecimal.ZERO));

                String relegationDaysTime = userVipFlow.getRelegationDaysTime();
                if (StringUtils.isNotBlank(relegationDaysTime)) {
                    // yyyy-mm-dd ,指定时间格式，获取
                    long startOfDayTimestamp = TimeZoneUtils.getStartOfDayTimestamp(relegationDaysTime, timezone);
                    int daysBetweenInclusive = TimeZoneUtils.getDaysBetweenExclusive(System.currentTimeMillis(), startOfDayTimestamp, timezone);
                    userIndexInfoVO.setLeftRelegationDays(Math.abs(daysBetweenInclusive));

                }
                // 保级流水
                userIndexInfoVO.setFinishRelegationAmount(userVipFlow.getFinishRelegationAmount());
                // 保级总流水金额
                userIndexInfoVO.setGradeRelegationAmount(userVipFlow.getGradeRelegationAmount());
                // 剩余保级流水金额
                userIndexInfoVO.setLeftRelegationAmount(userVipFlow.getGradeRelegationAmount().subtract(userVipFlow.getFinishRelegationAmount()).max(BigDecimal.ZERO));
                // 根据站点查询所有VIP段位
            }

            userIndexInfoVO.setWelfareNotClaimNums(welfareCenterV2Service.getWaitReceiveByUserId(userId));

        }


        // balance
        ResponseVO<IndexVO> userBalance = getUserBalance(userId, userAccount, siteCode);
        BigDecimal balance = userBalance.getData().getBalance();
        userIndexInfoVO.setTotalBalance(balance);

        //平台币钱包余额
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserId(userId);
        userCoinQueryVO.setUserAccount(userInfoPO.getUserAccount());
        userCoinQueryVO.setSiteCode(userInfoPO.getSiteCode());
        UserPlatformBalanceRespVO respVO = userPlatformCoinApi.getUserPlatformBalance(userCoinQueryVO);
        userIndexInfoVO.setPlatAvailableAmount(respVO == null ? BigDecimal.ZERO : respVO.getPlatAvailableAmount());

        String mainCurrency = userInfoPO.getMainCurrency();
        userIndexInfoVO.setMainCurrency(mainCurrency);
        ResponseVO<List<SystemCurrencyInfoRespVO>> listResponseVO = systemCurrencyInfoApi.selectAll();
        List<SystemCurrencyInfoRespVO> data = listResponseVO.getData();
        Optional<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOOptional = data.stream().filter(o -> o.getCurrencyCode().equals(mainCurrency)).findFirst();
        if (systemCurrencyInfoRespVOOptional.isPresent()) {
            SystemCurrencyInfoRespVO systemCurrencyInfoRespVO = systemCurrencyInfoRespVOOptional.get();
            userIndexInfoVO.setCurrencySymbol(systemCurrencyInfoRespVO.getCurrencySymbol());
            userIndexInfoVO.setCurrencyIcon(systemCurrencyInfoRespVO.getCurrencyIcon());
            userIndexInfoVO.setMainCurrencyName(systemCurrencyInfoRespVO.getCurrencyNameI18());
        }
        int userUnReadNoticeNums = userNoticeService.getUserUnReadNoticeNums();
        userIndexInfoVO.setUnReadNoticeNums(userUnReadNoticeNums);
        // 当平台币小于0，查看是否兑换过，如果大于0，则不查询
        if (userIndexInfoVO.getPlatAvailableAmount().compareTo(BigDecimal.ZERO) <= 0) {
            ResponseVO<Boolean> booleanResponseVO = userPlatformTransferApi.hasPlatformTransferRecord(userId);
            if (booleanResponseVO.isOk() && ObjectUtils.isNotEmpty(booleanResponseVO.getData()) && booleanResponseVO.getData()) {
                userIndexInfoVO.setPlatAmountFlag(1);
            }
        }
        //充提限制
        //校验会员是否存取款限制
        if (userInfoPO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())) {
            userIndexInfoVO.setRechargeWithdrawLimit(CommonConstant.business_one);
        } else {
            userIndexInfoVO.setRechargeWithdrawLimit(CommonConstant.business_zero);
        }
        List<String> userLabelList = new ArrayList<>();
        //校验标签出款限制
        if (StringUtils.isNotBlank(userInfoPO.getUserLabelId())) {
            List<String> labelIds = Arrays.asList(userInfoPO.getUserLabelId().split(","));
            //会员标签 不参与活动
            List<GetUserLabelByIdsVO> labelList = siteUserLabelConfigService.getUserLabelByIds(labelIds);
            userLabelList = labelList.stream()
                    .map(GetUserLabelByIdsVO::getLabelId)
                    .collect(Collectors.toList());
        }
        if (userLabelList.contains(UserLabelEnum.WITHDRAWAL_LIMIT.getLabelId())) {
            userIndexInfoVO.setWithdrawLimit(CommonConstant.business_one);
        } else {
            userIndexInfoVO.setWithdrawLimit(CommonConstant.business_zero);
        }
        // 意见反馈未读标识
        userIndexInfoVO.setUnReadFeedbackNums(siteUserFeedbackService.getUnreadNums(userId));
        // 福利中心未领取标识

        Integer vipRank = userIndexInfoVO.getVipRank();
        Integer nextVipRank = userIndexInfoVO.getNextVipRank();
        String minioDomain = fileService.getMinioDomain();
        SiteVIPRankPO rankPO = siteVIPRankService.getVipRankBySiteCodeAndCode(siteCode, vipRank);

        if (rankPO != null) {
            userIndexInfoVO.setVipIconImage(minioDomain + "/" + rankPO.getVipIcon());
            userIndexInfoVO.setRankColor(rankPO.getRankColor());
        }

        if (SiteHandicapModeEnum.Internacional.getCode().equals(handicapMode)) {
            SiteVIPRankPO nextRankPO = siteVIPRankService.getVipRankBySiteCodeAndCode(siteCode, nextVipRank);
            if (nextRankPO != null) {
                userIndexInfoVO.setNextVipIconImage(minioDomain + "/" + nextRankPO.getVipIcon());
                userIndexInfoVO.setNextRankColor(nextRankPO.getRankColor());
            }
        } else {
            //中国盘
            List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoPO.getSiteCode(), userInfoPO.getMainCurrency());
            Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
            userIndexInfoVO.setVipIconImage(siteVipOptionMap.get(userInfoPO.getVipGradeCode()).getVipIconImage());
            userIndexInfoVO.setNextVipIconImage(siteVipOptionMap.get(userInfoPO.getVipGradeUp()).getVipIconImage());
        }
        // 会员步骤
        userIndexInfoVO.setStep(getNewUserGuide(userId));
        // 新人任务领取状态
        TaskAppReqVO taskAppReqVO = new TaskAppReqVO();
        taskAppReqVO.setUserId(userId);
        taskAppReqVO.setSiteCode(siteCode);
        taskAppReqVO.setUserAccount(userAccount);
        taskAppReqVO.setTaskType(TaskEnum.NOVICE_WELCOME.getTaskType());
        taskAppReqVO.setSubTaskType(TaskEnum.NOVICE_WELCOME.getSubTaskType());
        Integer noviceStatus = taskOrderRecordApi.noviceStatus(taskAppReqVO);
        userIndexInfoVO.setReceiveStatus(noviceStatus);
        //log.info("getIndexInfo会员步骤:{}", JSON.toJSONString(userIndexInfoVO));
        return ResponseVO.success(userIndexInfoVO);
    }

    public ResponseVO<SiteVIPSystemVO> getUserVipBenefitDetail() {
        SiteVIPSystemVO result = new SiteVIPSystemVO();
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        UserInfoPO userInfoPO = this.lambdaQuery().eq(UserInfoPO::getUserId, userId).one();
        List<SiteVIPRankVO> vipRankVOList = siteVIPRankService.getVipRankListBySiteCode(siteCode);
        List<SiteVIPSystemRankVO> systemVOS = Lists.newArrayList();
        String minioDomain = fileService.getMinioDomain();

        vipRankVOList.forEach(obj -> {
            SiteVIPSystemRankVO vo = new SiteVIPSystemRankVO();
            BeanUtils.copyProperties(obj, vo);
            List<SiteVIPGradeVO> gradeVOList = siteVIPGradeService.queryVIPGradeByVIPS(siteCode, obj.getVipGradeList());
            vo.setSiteVIPGradeVOList(gradeVOList);
            String vipIcon = vo.getVipIcon();
            if (StringUtils.isNotBlank(vipIcon)) {
                vo.setVipIconImage(minioDomain + "/" + vipIcon);
            }
            systemVOS.add(vo);
        });
        result.setCurrentVIPRankCode(userInfoPO.getVipRank());
        result.setCurrentVIPGradeCode(userInfoPO.getVipGradeCode());
        result.setSiteVIPSystemRankVOList(systemVOS);
        return ResponseVO.success(result);
    }

    /**
     * 根据userId更新vip等级与vip段位
     *
     * @param userEditVipVO 更新vip等级与vip段位
     */
    public void updateVipStatus(UserEditVipVO userEditVipVO) {
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfoPO::getUserId, userEditVipVO.getUserId());
        updateWrapper.set(UserInfoPO::getVipGradeCode, userEditVipVO.getVipGradeCode());
        updateWrapper.set(UserInfoPO::getVipGradeUp, userEditVipVO.getVipGradeUp());
        updateWrapper.set(UserInfoPO::getVipRank, userEditVipVO.getVipRank());
        userInfoRepository.update(null, updateWrapper);
    }

    public Boolean updateSecondDeposit(String userId, BigDecimal arriveAmount) {
        LambdaUpdateWrapper<UserInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserInfoPO::getUserId, userId)
                .set(UserInfoPO::getSecondDepositTime, System.currentTimeMillis())
                .set(UserInfoPO::getSecondDepositAmount, arriveAmount);
        return this.update(null, lambdaUpdate);
    }

    @Async
    public void deleteUserLabel(String siteCode, List<String> userIds, String labelId) {
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(Wrappers.<UserInfoPO>lambdaQuery().select(UserInfoPO::getId, UserInfoPO::getUserLabelId).in(UserInfoPO::getId, userIds));
        if (CollectionUtil.isEmpty(userInfoPOS)) {
            return;
        }
        for (UserInfoPO user : userInfoPOS) {
            String userLabelId = user.getUserLabelId();
            if (Objects.isNull(userLabelId)) {
                continue;
            }
            String[] split = userLabelId.split(CommonConstant.COMMA);
            String afterLabelId = Arrays.stream(split).filter(e -> !e.equals(labelId)).collect(Collectors.joining(CommonConstant.COMMA));
            LambdaUpdateWrapper<UserInfoPO> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UserInfoPO::getId, user.getId())
                    .set(UserInfoPO::getUserLabelId, afterLabelId);
            this.update(wrapper);
        }
        log.info("删出标签，清除会员标签");
    }

    public List<UserInfoVIPVO> getUserInfoVip(List<String> userIds, String siteCode) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        if (!org.springframework.util.StringUtils.hasText(siteCode)) {
            return Lists.newArrayList();
        }
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(Wrappers.<UserInfoPO>lambdaQuery()
                .select(UserInfoPO::getVipGradeCode, UserInfoPO::getUserAccount, UserInfoPO::getUserId, UserInfoPO::getVipRank)
                .in(UserInfoPO::getUserId, userIds)
                .eq(UserInfoPO::getSiteCode, siteCode));
        if (CollectionUtil.isEmpty(userInfoPOS)) {
            return Lists.newArrayList();
        }
        Map<Integer, SiteVIPGradePO> code2Grade = Maps.newHashMap();
        List<SiteVIPGradePO> siteVIPGradePOS = siteVIPGradeRepository.selectList(Wrappers.<SiteVIPGradePO>lambdaQuery().eq(SiteVIPGradePO::getSiteCode, siteCode));
        if (CollectionUtil.isNotEmpty(siteVIPGradePOS)) {
            code2Grade = siteVIPGradePOS.stream().collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode, e -> e, (o, n) -> n));
        }
        Map<Integer, SiteVIPRankPO> code2rank = Maps.newHashMap();
        List<SiteVIPRankPO> siteVIPRankPOS = siteVIPRankService.list(Wrappers.<SiteVIPRankPO>lambdaQuery().eq(SiteVIPRankPO::getSiteCode, siteCode));
        if (CollectionUtil.isNotEmpty(siteVIPGradePOS)) {
            code2rank = siteVIPRankPOS.stream().collect(Collectors.toMap(SiteVIPRankPO::getVipRankCode, e -> e, (o, n) -> n));
        }
        List<UserInfoVIPVO> userInfoVIPVOS = Lists.newArrayList();
        for (UserInfoPO userInfoPO : userInfoPOS) {
            UserInfoVIPVO userInfoVIPVO = BeanUtil.toBean(userInfoPO, UserInfoVIPVO.class);
            userInfoVIPVO.setVipRankCode(userInfoPO.getVipRank());
            SiteVIPGradePO siteVIPGradePO = code2Grade.get(userInfoVIPVO.getVipGradeCode());
            if (Objects.nonNull(siteVIPGradePO)) {
                userInfoVIPVO.setVipGradeName(siteVIPGradePO.getVipGradeName());
            }
            SiteVIPRankPO siteVIPRankPO = code2rank.get(userInfoVIPVO.getVipRankCode());
            if (Objects.nonNull(siteVIPRankPO)) {
                userInfoVIPVO.setVipRankName(siteVIPRankPO.getVipRankName());
            }
            userInfoVIPVOS.add(userInfoVIPVO);
        }
        return userInfoVIPVOS;
    }

    public UserInfoVO getUserByInviteCode(String inviteCode, String siteCode) {
        UserInfoPO one = getOne(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getFriendInviteCode, inviteCode)
                .eq(UserInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(one, UserInfoVO.class);
    }

    /**
     * 生产一个邀请码
     *
     * @return
     */
    public String generateInviteCode(String siteName) {
        String friendInviteCode = MD5Util.inviteCode(siteName);
        UserInfoPO one = this.lambdaQuery().eq(UserInfoPO::getFriendInviteCode, friendInviteCode).one();
        if (one == null) {
            return friendInviteCode;
        } else {
            return generateInviteCode(siteName);

        }
    }

    public List<UserInfoVO> getUserInfoByUserIds(List<String> userIds) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.USER_ACCOUNT_TYPE, CommonConstant.USER_ACCOUNT_STATUS,
                        CommonConstant.USER_GENDER, CommonConstant.USER_REGISTRY)).getData();
        List<UserInfoPO> list = baseMapper.selectList(Wrappers.lambdaQuery(UserInfoPO.class)
                .in(UserInfoPO::getUserId, userIds));
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(x -> {
            UserInfoVO vo = new UserInfoVO();
            BeanUtils.copyProperties(x, vo);
            if (null != vo) {
                vo.setSuperAgentName(vo.getSuperAgentAccount());
                vo.setAccountType(x.getAccountType());
                Optional<CodeValueVO> typeOptional = map.get(CommonConstant.USER_ACCOUNT_TYPE)
                        .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getAccountType()))).findFirst();
                Optional<CodeValueVO> genderOptional = map.get(CommonConstant.USER_GENDER)
                        .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getGender()))).findFirst();
                Optional<CodeValueVO> registryOptional = map.get(CommonConstant.USER_REGISTRY)
                        .stream().filter(obj -> obj.getCode().equals(String.valueOf(vo.getRegistry()))).findFirst();
                Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                        .collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
                List<CodeValueVO> data = Lists.newArrayList();
                for (String str : x.getAccountStatus().split(",")) {
                    data.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).build());
                }
                vo.setAccountStatusName(data);
                typeOptional.ifPresent(systemParamVO -> vo.setAccountTypeName(systemParamVO.getValue()));
                genderOptional.ifPresent(systemParamVO -> vo.setGenderName(systemParamVO.getValue()));
                registryOptional.ifPresent(systemParamVO -> vo.setRegistryName(systemParamVO.getValue()));
                // 会员标签
                if (null != vo.getUserLabelId()) {
                    //
                    List<String> ids = stringToList(vo.getUserLabelId());
                    List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                    String userLabel = "";
                    if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                        for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                            userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
                        }
                        // 拿出来是string,转化为list，然后在组装
                    }
                    if (userLabel.endsWith(CommonConstant.COMMA)) {
                        userLabel = userLabel.substring(0, userLabel.length() - 1);
                    }
                    vo.setUserLabelName(userLabel);
                }
                // 风控层级
                if (null != vo.getRiskLevelId()) {
                    RiskLevelDetailsVO riskLevelDetailsVO =
                            riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                    String riskLevel = null;
                    if (null != riskLevelDetailsVO) {
                        riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                    }
                    vo.setRiskLevel(riskLevel);
                }
            }
            return vo;
        }).toList();
    }

    public List<UserInfoVO> getUserBalanceBySiteCodeUserAccount(String siteCode, List<String> userAccounts) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.eq(UserInfoPO::getSiteCode, siteCode).in(UserInfoPO::getUserAccount, userAccounts);
        List<UserInfoPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, UserInfoVO.class);
        }
        return new ArrayList<>();
    }

    public List<UserInfoVO> getByUserIds(List<String> userIds, String siteCode) {
        List<UserInfoPO> list = baseMapper.selectList(Wrappers.lambdaQuery(UserInfoPO.class).eq(UserInfoPO::getSiteCode, siteCode)
                .in(UserInfoPO::getUserId, userIds));
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, UserInfoVO.class);
        }
        return new ArrayList<>();
    }

    public List<String> getAllUserIds(String siteCode) {
        return userInfoRepository.getAllUserIds(siteCode);
    }

    public Long countInviteeChargeNum(String userId, String siteCode) {
        return userInfoRepository.countInviteeChargeNum(userId, siteCode);
    }

    /**
     * 定时任务处理离线天数
     */
    public void updateOfflineDaysTask(String siteCode) {
        userInfoRepository.updateOfflineDaysTask(System.currentTimeMillis(), siteCode);
    }

    public List<UserInfoVO> getUsersBySiteCode(String siteCode) {
        List<UserInfoPO> list = baseMapper.selectList(Wrappers.lambdaQuery(UserInfoPO.class).eq(UserInfoPO::getSiteCode, siteCode));
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, UserInfoVO.class);
        }
        return new ArrayList<>();
    }

    public List<String> filterNoRebateUserIds(List<String> userIds, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.select(UserInfoPO::getUserId, UserInfoPO::getUserLabelId)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .in(UserInfoPO::getUserId, userIds);
        List<UserInfoPO> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream()
                .filter(item -> {
                    String labelIdStr = item.getUserLabelId();
                    return StringUtils.isNotEmpty(labelIdStr)
                            && Arrays.asList(labelIdStr.split(CommonConstant.COMMA))
                            .contains("10005");
                })
                .map(UserInfoPO::getUserId)
                .toList();
    }

    public List<String> getUsedAvatarList(List<String> avatarIds, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.eq(UserInfoPO::getSiteCode, siteCode).in(UserInfoPO::getAvatarCode, avatarIds);
        List<String> result = new ArrayList<>();
        List<UserInfoPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            result = list.stream().map(UserInfoPO::getAvatarCode).filter(StringUtils::isNotBlank).toList();
        }
        return result;
    }

    public List<UserInfoVO> getByUserAccounts(List<String> userAccounts, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.eq(UserInfoPO::getSiteCode, siteCode).in(UserInfoPO::getUserAccount, userAccounts);
        List<UserInfoPO> list = this.list(query);
        return BeanUtil.copyToList(list, UserInfoVO.class);
    }

    public UserSystemMessageConfigVO getUserLanguage(UserLanguageVO vo) {
        String userId = vo.getUserId();
        //默认为英文
        String language = CurrReqUtils.getLanguage();
        if (StringUtils.isEmpty(language)) {
            language = LanguageEnum.EN_US.getLang();
        }
        String noticeContentI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.NOTICE_CONTENT.getCode());
        log.info("=======" + noticeContentI18Code);
        //先从缓存中取用户的语言
        String key = "account::language::" + userId;
        String cacheVal = RedisUtil.getValue(key);
        if (StringUtils.isNotEmpty(cacheVal)) {
            log.info("get cache value:{}-{}", userId, language);
            language = cacheVal;
        } else {
            LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(UserInfoPO::getUserId, userId);
            UserInfoPO userInfoPo = userInfoRepository.selectOne(queryWrapper);
            if (StringUtils.isEmpty(userInfoPo.getLanguage())) {
                log.info("User has not set language");
                userInfoPo.setLanguage(language);
                userInfoRepository.updateById(userInfoPo);
            } else {
                log.info("Use user's language setting:{}", userInfoPo.getLanguage());
                language = userInfoPo.getLanguage();
            }
            RedisUtil.setValue(key, language);
        }
        String code = vo.getMessageType().getCode();
        String title = "BIZ_" + code + "_TITLE_8100";
        String content = "BIZ_" + code + "_CONTENT_8100";
        String titleString = I18nMessageUtil.getI18NMessage(title);
        String contentString = I18nMessageUtil.getI18NMessage(content);

        UserSystemMessageConfigVO systemMessageConfigVO = new UserSystemMessageConfigVO();
        systemMessageConfigVO.setTitleI18nCode(title);
        systemMessageConfigVO.setTitle(titleString);
        systemMessageConfigVO.setContentI18nCode(content);
        systemMessageConfigVO.setContent(contentString);
        systemMessageConfigVO.setLanguage(language);
        return systemMessageConfigVO;
    }

    public UserLanguageVO updateUserLanguage(UserLanguageVO vo) {
        UserLanguageVO res = new UserLanguageVO();
        String userId = vo.getUserId();
        if (StringUtils.isEmpty(userId)) {
            log.info("UserId is null");
            return res;
        }


        String language = CurrReqUtils.getLanguage();
        if (StringUtils.isEmpty(language)) {
            log.info("The language obtained is empty, use the default language");
            language = LanguageEnum.EN_US.getLang();
        }
        String key = "account::language::" + userId;
        RedisUtil.setValue(key, language);
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(UserInfoPO::getUserId, userId);
        UserInfoPO userInfoPo = userInfoRepository.selectOne(queryWrapper);
        if (userInfoPo != null) {
            userInfoPo.setLanguage(language);
            userInfoRepository.updateById(userInfoPo);
        }
        log.info("UserId:{}-{}", userId, language);


        return res;
    }

    public Map<String, Map<String, List<UserInfoVO>>> getUserListGroupSiteCode(Long startTime, Long endTime, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserInfoPO::getSiteCode, siteCode);
        }
        query.eq(UserInfoPO::getAccountType, UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        query.and(wq ->wq.between(UserInfoPO::getRegisterTime, startTime, endTime).or().
                between(UserInfoPO::getFirstDepositTime, startTime, endTime));
        List<UserInfoPO> list = this.list(query);
        List<UserInfoVO> vos = BeanUtil.copyToList(list, UserInfoVO.class);
        if (CollectionUtil.isNotEmpty(vos)) {
            return vos.stream().collect(Collectors.groupingBy(UserInfoVO::getSiteCode, Collectors.groupingBy(UserInfoVO::getMainCurrency)));
        }
        return new HashMap<>();
    }

    public List<UserInfoVO> getUserListByParam(UserAgentQueryUserVO queryVO) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        String siteCode = queryVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(UserInfoPO::getSiteCode, siteCode);
        }
        String agentId = queryVO.getAgentId();
        if (StringUtils.isNotBlank(agentId)) {
            query.eq(UserInfoPO::getSuperAgentId, agentId);
        }
        String agentAccount = queryVO.getAgentAccount();
        if (StringUtils.isNotBlank(agentAccount)) {
            query.eq(UserInfoPO::getSuperAgentAccount, agentAccount);
        }
        Long regStartTime = queryVO.getRegStartTime();
        if (regStartTime != null) {
            query.ge(UserInfoPO::getRegisterTime, regStartTime);
        }
        Long regEndTime = queryVO.getRegEndTime();
        if (regEndTime != null) {
            query.le(UserInfoPO::getRegisterTime, regEndTime);
        }
        List<UserInfoPO> list = this.list(query);
        return BeanUtil.copyToList(list, UserInfoVO.class);
    }

    public List<String> getUnLabelByUserIds(String labelId, String siteCode) {
        return userInfoRepository.getUnLabelByUserIds(labelId, siteCode);
    }

    public List<String> getUnBenefitByUserIds(String awardCode, String siteCode) {
        return userInfoRepository.getUnBenefitByUserIds(awardCode, siteCode);
    }

    /**
     * 处理会员最近下注时间
     *
     * @param userLatestBetMqVO
     */
    public void processUserBetTime(UserLatestBetMqVO userLatestBetMqVO) {
        List<UserLatestBetVO> userLatestBetVOS = userLatestBetMqVO.getUserLatestBetVOS();
        if (CollectionUtil.isNotEmpty(userLatestBetVOS)) {
            for (UserLatestBetVO userBetVO : userLatestBetVOS) {
                log.info("当前会员:{},最新下注时间:{}", userBetVO.getUserId(), userBetVO.getBetTime());
                RedisUtil.setValue(RedisConstants.USER_BET_TIME_FLUSH + userBetVO.getUserId(), userBetVO);
            }
        }

    }

    public List<UserLoginInfoVO> getLatestLoginInfoByUserIds(List<String> userIds) {
        List<UserLoginInfoPO> userLoginInfoPOS = userLoginInfoRepository.getLatestLoginInfoByUserIds(userIds);
        if (CollectionUtil.isEmpty(userLoginInfoPOS)) {
            return null;
        }
//        userLoginInfoPOS = userLoginInfoRepository.selectBatchIds(userLoginInfoPOS.stream().map(UserLoginInfoPO::getId).toList());
        return ConvertUtil.entityListToModelList(userLoginInfoPOS, UserLoginInfoVO.class);
    }

    public Page<String> getUserIdListPage(UserIdPageVO userIdPageVO) {
        Page<UserInfoPO> page = new Page<>(userIdPageVO.getPageNumber(), userIdPageVO.getPageSize());
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<UserInfoPO>();
        queryWrapper.in(UserInfoPO::getUserId, userIdPageVO.getUserIdList());
        queryWrapper.orderByDesc(UserInfoPO::getRegisterTime);

        Page<UserInfoPO> poPage = userInfoRepository.selectPage(page, queryWrapper);
        Page<String> userIdPage = new Page<>();
        BeanUtils.copyProperties(poPage, userIdPage);
        List<String> userAccountList = poPage.getRecords().stream().map(UserInfoPO::getUserAccount).toList();
        userIdPage.setRecords(userAccountList);
        return userIdPage;
    }

    public Page<UserInfoResponseVO> listPage(UserInfoPageVO vo) {
        Page<UserInfoPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<UserInfoPO>();
        if (org.springframework.util.StringUtils.hasText(vo.getSiteCode())) {
            queryWrapper.eq(UserInfoPO::getSiteCode, vo.getSiteCode());
        }
        if (!CollectionUtils.isEmpty(vo.getAccountType())) {
            queryWrapper.in(UserInfoPO::getAccountType, vo.getAccountType());
        }
        if (vo.getRegisterTimeStart() != null) {
            queryWrapper.ge(UserInfoPO::getRegisterTime, vo.getRegisterTimeStart());
        }
        if (vo.getRegisterTimeEnd() != null) {
            queryWrapper.le(UserInfoPO::getRegisterTime, vo.getRegisterTimeEnd());
        }
        if (vo.getFirstDepositTimeStart() != null) {
            queryWrapper.ge(UserInfoPO::getFirstDepositTime, vo.getFirstDepositTimeStart());
        }
        if (vo.getFirstDepositTimeEnd() != null) {
            queryWrapper.le(UserInfoPO::getFirstDepositTime, vo.getFirstDepositTimeEnd());
        }
        if (vo.getAgentFlag() != null && 1 == vo.getAgentFlag()) {
            queryWrapper.isNotNull(UserInfoPO::getSuperAgentId);
        }
        if (vo.getAgentFlag() != null && 0 == vo.getAgentFlag()) {
            queryWrapper.isNull(UserInfoPO::getSuperAgentId);
        }

        Page<UserInfoPO> poPage = userInfoRepository.selectPage(page, queryWrapper);
        Page<UserInfoResponseVO> resultPage = new Page<>();
        BeanUtils.copyProperties(poPage, resultPage);
        List<UserInfoResponseVO> userInfoResponseVOS = ConvertUtil.entityListToModelList(poPage.getRecords(), UserInfoResponseVO.class);
        resultPage.setRecords(userInfoResponseVOS);
        return resultPage;
    }

    public List<UAgentSubLineUserResVO> findAgentSubLineUserNum(UserAgentSubLineReqVO reqVO) {
        if (CollectionUtil.isEmpty(reqVO.getAgentIds())) {
            return Lists.newArrayList();
        }
        return userInfoRepository.findAgentSubLineUserNum(reqVO);
    }

    public List<UAgentSubLineUserResVO> findAgentSubLineUserFirstDeposit(UserAgentSubLineReqVO reqVO) {
        if (CollectionUtil.isEmpty(reqVO.getAgentIds())) {
            return Lists.newArrayList();
        }
        return userInfoRepository.findAgentSubLineUserFirstDeposit(reqVO);
    }

    /**
     * 按照代理分组 获取会员数
     *
     * @param downAgentIds 下级代理
     * @return
     */
    public Map<String, Long> getUserCountGroupByAgentId(List<String> downAgentIds) {
        if (CollectionUtil.isEmpty(downAgentIds)) {
            return Maps.newHashMap();
        }
        List<UAgentSubLineUserResVO> agentSubLineUserResVOS = userInfoRepository.getUserCountGroupByAgentId(downAgentIds);
        Map<String, Long> resultMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(agentSubLineUserResVOS)) {
            for (UAgentSubLineUserResVO agentSubLineUserResVO : agentSubLineUserResVOS) {
                resultMap.put(agentSubLineUserResVO.getAgentId(), Long.valueOf(agentSubLineUserResVO.getSubLineUserNum()));
            }
        }
        return resultMap;
    }

    public List<SiteUserDateQueryVO> getSiteCurrencyUserList(Map<String, List<String>> dateSiteStrMap, String currencyCode) {
        List<SiteUserDateQueryVO> result = new ArrayList<>();
        Set<String> dateStrSet = dateSiteStrMap.keySet();
        //分别统计某一天的某些站点的某些币种(如果有),对应的现有会员人数
        for (String dateStr : dateStrSet) {
            List<String> siteCodes = dateSiteStrMap.get(dateStr);
            List<SiteUserDateQueryVO> siteCurrencyUserList = userInfoRepository.getSiteCurrencyUserList(siteCodes, currencyCode, dateStr);
            if (CollectionUtil.isNotEmpty(siteCurrencyUserList)) {
                result.addAll(siteCurrencyUserList);
            }
        }

        return result;
    }

    public Boolean checkUserIpMax(Integer maxCount, String ip, String siteCode) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.eq(UserInfoPO::getRegisterIp, ip);
        query.eq(UserInfoPO::getSiteCode, siteCode);
        return userInfoRepository.selectCount(query) < maxCount;
    }

    public Integer getNewUserGuide(String userId) {
        LambdaQueryWrapper<SiteNewUserGuideStepRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteNewUserGuideStepRecordPO::getUserId, userId);
        queryWrapper.last(" limit 1 ");
        SiteNewUserGuideStepRecordPO siteNewUserGuideStepRecordPO = newUserGuideStepRecordRepository.selectOne(queryWrapper);
        if (siteNewUserGuideStepRecordPO == null) {
            return 0;
        } else {
            return siteNewUserGuideStepRecordPO.getStep();
        }

    }

    /**
     * 校验指定用户是否为同一种货币
     *
     * @param req 请求体，包含 siteCode 和 userAccounts（以 ; 分隔）
     * @return 响应体，包含是否是同一种货币、币种类型、币种不一致的用户账号
     */
    public ResponseVO<GetUserInfoCurrencyRespVO> checkUserCurrency(GetUserInfoCurrencyReqVO req) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        String[] userAccountArr = Optional.ofNullable(req.getUserAccounts())
                .map(s -> s.split("[;,]"))
                .orElse(new String[0]);

        // 排除空账号并去除前后空格
        List<String> userAccountList = Arrays.stream(userAccountArr)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (!userAccountList.isEmpty()) {
            query.in(UserInfoPO::getUserAccount, userAccountList);
        } else {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        query.eq(UserInfoPO::getSiteCode, req.getSiteCode());
        // 3. 查询数据库中存在的用户
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(query);
        if (CollectionUtils.isEmpty(userInfoPOS)) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        // 4. 找出数据库中没有的账号
        Set<String> existsAccountSet = userInfoPOS.stream()
                .map(UserInfoPO::getUserAccount)
                .collect(Collectors.toSet());
        List<String> notExistsAccounts = userAccountList.stream()
                .filter(account -> !existsAccountSet.contains(account))
                .collect(Collectors.toList());
        GetUserInfoCurrencyRespVO res = new GetUserInfoCurrencyRespVO();
        // 判断是否都是一种货币
        String mainCurrency = userInfoPOS.get(0).getMainCurrency();
        List<String> userAccounts = new ArrayList<>();
        for (UserInfoPO userInfoPO : userInfoPOS) {
            if (!mainCurrency.equals(userInfoPO.getMainCurrency())) {
                userAccounts.add(userInfoPO.getUserAccount());
                res.setIsSingleCurrency(false);
            }
        }
        // 判断是否都有不存在的用户
        if (!CollectionUtils.isEmpty(notExistsAccounts)) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        res.setUserAccounts(userAccounts);
        res.setCurrency(mainCurrency);
        if (!res.getIsSingleCurrency()) {
            return ResponseVO.fail(ResultCode.CURRENCY_HAVE_NO_LIMIT);
        }
        return ResponseVO.success(res);

    }

    public ResponseVO<Boolean> updateUserPersonInfoById(UserInfoPersonReqVO userInfoEditVO) {

        int update = userInfoRepository.update(null, Wrappers.<UserInfoPO>lambdaUpdate()
                .eq(UserInfoPO::getUserId, userInfoEditVO.getUserId())
                .set(userInfoEditVO.getUserName() != null, UserInfoPO::getUserName, userInfoEditVO.getUserName())
                .set(userInfoEditVO.getGender() != null, UserInfoPO::getGender, userInfoEditVO.getGender())
                .set(userInfoEditVO.getBirthday() != null, UserInfoPO::getBirthday, userInfoEditVO.getBirthday())
        );

        return update > 0 ? ResponseVO.success() : ResponseVO.fail(ResultCode.UPDATE_FAIL);
    }

    public ResponseVO<ResultCode> authVerify(String userId, String userName, String areaCode, String phone, String birthday) {
        // 查询userinfo ，如果有userName ，phone 则使用 表存在的，如果没有，则使用传入的。
        // 如果校验成功，则更新userInfo
        UserInfoPO userInfoPO = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery()
                .eq(UserInfoPO::getUserId, userId));
        if (userInfoPO == null) {
            return ResponseVO.fail(ResultCode.AGENT_USER_ACCOUNT_NOT_EXIST);
        }
        // 校验手机号码，区号，站点是否唯一
        // 校验手机号码是否唯一
        List<UserInfoPO> existUsers = userInfoRepository.selectList(Wrappers.<UserInfoPO>lambdaQuery()
                .eq(UserInfoPO::getSiteCode, userInfoPO.getSiteCode())
                .eq(UserInfoPO::getPhone, phone)
                .eq(UserInfoPO::getAreaCode, areaCode));
        if (ObjectUtil.isNotEmpty(existUsers)) {
            // 去掉本身
            existUsers.removeIf(user -> user.getUserId().equals(userId));
            if (!existUsers.isEmpty()) {
                log.warn("认证失败：手机号码已被注册, userId={}, phone={}", userId, phone);
                return ResponseVO.fail(ResultCode.PHONE_BOUND);
            }
        }

        userName = Optional.ofNullable(userInfoPO.getUserName()).orElse(userName);
        phone = Optional.ofNullable(userInfoPO.getPhone()).orElse(phone);
        birthday = Optional.ofNullable(userInfoPO.getBirthday()).orElse(birthday);
        areaCode = Optional.ofNullable(userInfoPO.getAreaCode()).orElse(areaCode);
        boolean verify = aliAuthService.phoneVerify(userName, phone);
        if (!verify) {
            log.warn("认证失败：phoneVerify校验不通过, userId={}, userName={}, phone={}", userId, userName, phone);
            return ResponseVO.fail(ResultCode.AUTH_WRONG_OPEN);
        }
        int update = userInfoRepository.update(null, Wrappers.<UserInfoPO>lambdaUpdate()
                .eq(UserInfoPO::getUserId, userId)
                .set(UserInfoPO::getAuthStatus, 1)
                .set(ObjectUtil.isEmpty(userInfoPO.getUserName()), UserInfoPO::getUserName, userName)
                .set(ObjectUtil.isEmpty(userInfoPO.getPhone()), UserInfoPO::getPhone, phone)
                .set(ObjectUtil.isEmpty(userInfoPO.getBirthday()), UserInfoPO::getBirthday, birthday)
                .set(ObjectUtil.isEmpty(userInfoPO.getAreaCode()), UserInfoPO::getAreaCode, areaCode)
        );
        return update > 0 ? ResponseVO.success(ResultCode.SUCCESS) : ResponseVO.fail(ResultCode.AUTH_WRONG_OPEN);

    }

    public ResponseVO<UserVIPDetailInfoVO> getUserVipDetailInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        UserInfoPO userInfoPO = this.lambdaQuery().eq(UserInfoPO::getUserId, userId).one();
        if (ObjectUtils.isEmpty(userInfoPO)) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserVIPDetailInfoVO resp = new UserVIPDetailInfoVO();
        // 查询返水
        SiteRebateClientShowVO rebateReq = SiteRebateClientShowVO.builder().siteCode(siteCode).currencyCode(userInfoPO.getMainCurrency()).build();

        resp.setRebates(BeanUtil.copyToList(siteRebateApi.webQueryListByStatusPage(rebateReq).getData(), SiteRebateConfigWebCopyVO.class));
        //添加反水配置 by mufan

        // 添加保级天数
        UserInfoVO userInfoVO = ConvertUtil.entityToModel(userInfoPO, UserInfoVO.class);

        UserVipFlowRecordCnVO userVipFlowRecordCnVO = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);
        if (ObjectUtils.isNotEmpty(userVipFlowRecordCnVO)) {
            resp.setRelegationDays(userVipFlowRecordCnVO.getRelegationDays());
        }
        // 查询当前等级vip图标
        // 根据站点查询所有VIP等级
        //List<SiteVIPGradeVO> siteVIPGradeVOList = siteVIPGradeService.queryAllVIPGrade(siteCode);
        List<SiteVipOptionVO> list = siteVipOptionService.getList(userInfoPO.getSiteCode(), userInfoPO.getMainCurrency());
        if (ObjectUtils.isNotEmpty(list)) {
            list.stream().filter(item -> item.getVipGradeCode().equals(userInfoPO.getVipGradeCode()))
                    .findFirst()
                    .ifPresent(item -> resp.setVipGradeIcon(item.getVipIconImage()));
        }
        return ResponseVO.success(resp);
    }

    public ResponseVO<UserVIPInfoCNVO> getUserVipChianInfo(UserVIPInfoResVO userVIPInfoResVO) {
        String siteCode = userVIPInfoResVO.getSiteCode();
        String userId = userVIPInfoResVO.getUserId();
        UserInfoPO userInfoPO = this.lambdaQuery().eq(UserInfoPO::getUserId, userId).one();
        if (ObjectUtils.isEmpty(userInfoPO)) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserVIPInfoCNVO responseVO = new UserVIPInfoCNVO();
        BeanUtils.copyProperties(userInfoPO, responseVO);

        UserInfoVO userInfoVO = ConvertUtil.entityToModel(userInfoPO, UserInfoVO.class);
        UserVipFlowRecordCnVO userVipFlow = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);

        if (null != userVipFlow) {
            // 当前vip流水
            responseVO.setCurrentExp(userVipFlow.getFinishBetAmount());
        }
        if (responseVO.getCurrentExp() == null) {
            responseVO.setCurrentExp(BigDecimal.ZERO);
        }
        // 升级所需经验值
        responseVO.setUpgradeVipExp(userVipFlow.getUpgradeBetAmount());

        responseVO.setUpgradeVipNeedExp(userVipFlow.getUpgradeBetAmount().subtract(userVipFlow.getFinishBetAmount()).max(BigDecimal.ZERO));
        // 保级天数
        String relegationDaysTime = userVipFlow.getRelegationDaysTime();
        if (StringUtils.isNotBlank(relegationDaysTime)) {
            // yyyy-mm-dd ,指定时间格式，获取
            long startOfDayTimestamp = TimeZoneUtils.getStartOfDayTimestamp(relegationDaysTime, userVIPInfoResVO.getTimezone());
            int daysBetweenInclusive = TimeZoneUtils.getDaysBetweenExclusive(System.currentTimeMillis(), startOfDayTimestamp, userVIPInfoResVO.getTimezone());
            responseVO.setLeftRelegationDays(Math.abs(daysBetweenInclusive));

        }
        responseVO.setRelegationDays(userVipFlow.getRelegationDays());
        // 保级流水
        responseVO.setFinishRelegationAmount(userVipFlow.getFinishRelegationAmount());
        // 保级总流水金额
        responseVO.setGradeRelegationAmount(userVipFlow.getGradeRelegationAmount());
        // 剩余保级流水金额
        responseVO.setLeftRelegationAmount(userVipFlow.getGradeRelegationAmount().subtract(userVipFlow.getFinishRelegationAmount()).max(BigDecimal.ZERO));
        // 根据站点查询所有VIP段位
        //List<SiteVIPRankVO> siteVIPRankVOS = siteVIPRankService.getVipRankListBySiteCode(siteCode);
        // 根据站点查询所有VIP等级
        List<SiteVIPGradeVO> siteVIPGradeVOList = siteVIPGradeService.queryAllVIPGrade(siteCode);
        Map<Integer, String> siteVIPGradeNameMap = siteVIPGradeVOList.stream().collect(Collectors
                .toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName));
        // VIP等级名称赋值
        responseVO.setVipGradeName(siteVIPGradeNameMap.get(userInfoPO.getVipGradeCode()));
        responseVO.setVipGradeUpName(siteVIPGradeNameMap.get(userInfoPO.getVipGradeUp()));

        VIPGradeBenefitsDetailAPP vipGradeBenefitsDetailAPP = getVIPAward(userInfoVO);
        // vip 权益详情
        responseVO.setVipBenefitAPPs(vipGradeBenefitsDetailAPP.getVipBenefitAPPs());
        // 提款
        responseVO.setUserVipWithdrawalAPP(vipGradeBenefitsDetailAPP.getUserVipWithdrawalAPP());
        // 是否展示
        responseVO.setIsShowRebate(vipGradeBenefitsDetailAPP.getIsShowRebate());
        // 返水
        responseVO.setSiteRebate(vipGradeBenefitsDetailAPP.getSiteRebate());

        return ResponseVO.success(responseVO);
    }

    public VIPGradeBenefitsDetailAPP getVIPAward(UserInfoVO userInfo) {
        Integer vipUserGradeCode = userInfo.getVipGradeCode();
        VIPGradeBenefitsDetailAPP vipGradeBenefitsDetailAPP = new VIPGradeBenefitsDetailAPP();
        if (null == userInfo) {
            return vipGradeBenefitsDetailAPP;
        }
        List<VIPGradeBenefitsAPP> vipBenefitAPPs = new ArrayList<>();
        // 查询vip等级权益配置
        List<SiteVipOptionVO> vipBenefits = siteVipOptionService.getList(userInfo.getSiteCode(), userInfo.getMainCurrency());
        // 找到对应用户所在的
        Optional<SiteVipOptionVO> first = vipBenefits.stream().filter(item -> Objects.equals(item.getVipGradeCode(), vipUserGradeCode)).findFirst();
        Boolean isShowRebate = false;
        if (first.isPresent()) {
            Integer rebateConfig = first.get().getRebateConfig();
            if (!Objects.isNull(rebateConfig) && ObjectUtil.equals(1, rebateConfig)) {
                isShowRebate = true;
            }

        }
        vipGradeBenefitsDetailAPP.setIsShowRebate(isShowRebate);
        // 查询VIP权益领取记录
        UserAwardRecordV2ReqVO userAwardRecordV2ReqVO = new UserAwardRecordV2ReqVO();
        userAwardRecordV2ReqVO.setUserId(userInfo.getUserId());
        userAwardRecordV2ReqVO.setSiteCode(userInfo.getSiteCode());
        List<UserAwardRecordV2VO> userAwardRecordV2VOS = vipAwardRecordV2Api.awardRecordByVipGrade(userAwardRecordV2ReqVO);
        // 当前会员vip 详情配置
        SiteVipOptionVO vipBenefitVO = vipBenefits.stream().filter(item ->
                userInfo.getVipGradeCode().equals(item.getVipGradeCode())).findFirst().get();
        // 获取当前vip等级 vip详情与 vip反水
        UserVipWithdrawConfigCopyAPPVO userVipWithdrawalAPP = new UserVipWithdrawConfigCopyAPPVO();
        BeanUtils.copyProperties(vipBenefitVO, userVipWithdrawalAPP);
        userVipWithdrawalAPP.setSingleDayWithdrawCount(vipBenefitVO.getDailyWithdrawalFreeNum());
        userVipWithdrawalAPP.setSingleMaxWithdrawAmount(vipBenefitVO.getDailyWithdrawalFreeAmountLimit());
        userVipWithdrawalAPP.setDailyWithdrawalNumsLimit(vipBenefitVO.getDailyWithdrawalNumLimit());
        userVipWithdrawalAPP.setDailyWithdrawAmountLimit(vipBenefitVO.getDailyWithdrawAmountLimit());
        userVipWithdrawalAPP.setCurrencyCode(userInfo.getMainCurrency());
        userVipWithdrawalAPP.setVipGradeCode(userInfo.getVipGradeCode());
        vipGradeBenefitsDetailAPP.setUserVipWithdrawalAPP(userVipWithdrawalAPP);
        // 返水详情
        UserInfoVO userInfoVO = ConvertUtil.entityToModel(userInfo, UserInfoVO.class);
        // 查询返水
        if (isShowRebate) {
            SiteRebateClientShowVO rebateReq = SiteRebateClientShowVO.builder().siteCode(userInfoVO.getSiteCode())
                    .currencyCode(userInfoVO.getMainCurrency()).vipCode(userInfoVO.getVipGradeCode()).build();
            ResponseVO<List<SiteRebateConfigWebVO>> listResponseVO = siteRebateApi.webListPage(rebateReq);
            if (listResponseVO.isOk() && CollectionUtil.isNotEmpty(listResponseVO.getData())) {
                List<SiteRebateConfigWebCopyVO> siteRebateConfigWebCopyVOS = BeanUtil.copyToList(listResponseVO.getData(), SiteRebateConfigWebCopyVO.class);
                // 当前等级只有一个返水配置
                vipGradeBenefitsDetailAPP.setSiteRebate(siteRebateConfigWebCopyVOS.get(0));
                vipGradeBenefitsDetailAPP.setIsShowRebate(true);
            }
        }
        //UserVipFlowRecordCnVO userVipFlowRecordCnVO = userVipFlowRecordCnService.getUserVipFlow(userInfoVO);
        // 每个vip等级的特权领取情况
        // 各个Vip等级的权益
        for (SiteVipOptionVO siteVipOptionVO : vipBenefits) {
            // 各个等级卡片
            VIPGradeBenefitsAPP getVIPAwardVO = new VIPGradeBenefitsAPP();
            BeanUtils.copyProperties(siteVipOptionVO, getVIPAwardVO);
            Integer vipGradeCode = siteVipOptionVO.getVipGradeCode();
            getVIPAwardVO.setVipGradeLock(vipUserGradeCode >= vipGradeCode);
            List<UserAwardRecordV2VO> userAwardRecordV2VOList = userAwardRecordV2VOS.stream()
                    .filter(item -> item.getVipGradeCode().equals(vipGradeCode)).toList();
            // 领取状态 奖励类型(0升级礼金 1生日礼金 2周红包)
            // 0-升级礼金
            GetVIPAwardVO upgradeGift = new GetVIPAwardVO();
            upgradeGift.setAwardType(Integer.valueOf(VIPAwardV2Enum.UPGRADE_BONUS.getCode()));
            String upgradeGiftName = I18nMessageUtil.getSystemParamAndTrans(VIP_AWARD_TYPE_CN, VIPAwardV2Enum.UPGRADE_BONUS.getCode());
            upgradeGift.setAwardTypeName(upgradeGiftName);
            upgradeGift.setAwardAmount(siteVipOptionVO.getPromotionBonus());
            upgradeGift.setSort(CommonConstant.business_two);
            // vip 领取情况
            List<GetVIPAwardVO> vipAwardList = new ArrayList<>();
            // 领取状态 0-未领取 1-已领取 2-已过期 3-没资格转换为2
            if (CollectionUtil.isNotEmpty(userAwardRecordV2VOList)) {
                // 选择对应的奖项
                UserAwardRecordV2VO userAwardRecordV2VO = userAwardRecordV2VOList.stream()
                        .filter(item -> item.getAwardType().equals(VIPAwardV2Enum.UPGRADE_BONUS.getCode()))
                        .findFirst()
                        .orElse(null);
                if (userAwardRecordV2VO != null) {
                    upgradeGift.setReceiveStatus(userAwardRecordV2VO.getReceiveStatus());
                    upgradeGift.setOrderId(userAwardRecordV2VO.getId());
                } else {
                    upgradeGift.setReceiveStatus(CommonConstant.business_three);
                    upgradeGift.setOrderId(null);
                }

            } else {
                // 没有资格
                upgradeGift.setReceiveStatus(CommonConstant.business_three);
                upgradeGift.setOrderId(null);
            }
            vipAwardList.add(upgradeGift);
            // 3-生日礼金
            boolean isAgeAward = false;
            if (CollectionUtil.isNotEmpty(userAwardRecordV2VOList)) {
                List<UserAwardRecordV2VO> birthdayGiftList = userAwardRecordV2VOList.stream()
                        .filter(item -> item.getAwardType().equals(VIPAwardV2Enum.BIRTH_BONUS.getCode()))
                        .toList();
                if (CollectionUtil.isNotEmpty(birthdayGiftList)) {
                    // 不为空
                    for (UserAwardRecordV2VO item : birthdayGiftList) {
                        GetVIPAwardVO birthdayGift = new GetVIPAwardVO();
                        birthdayGift.setAwardType(Integer.valueOf(VIPAwardV2Enum.BIRTH_BONUS.getCode()));
                        String birthdayGiftName = I18nMessageUtil.getSystemParamAndTrans(VIP_AWARD_TYPE_CN, VIPAwardV2Enum.BIRTH_BONUS.getCode());
                        birthdayGift.setAwardTypeName(birthdayGiftName);
                        birthdayGift.setAwardAmount(item.getAwardAmount());
                        birthdayGift.setReceiveStatus(item.getReceiveStatus());
                        birthdayGift.setOrderId(item.getId());
                        birthdayGift.setSort(CommonConstant.business_one);
                        isAgeAward = true;
                        vipAwardList.add(birthdayGift);
                    }
                }
            }
            if (!isAgeAward) {
                vipAwardList.add(createAgeAward(siteVipOptionVO));
            }
            // 1-周红包
            boolean isWeekAward = false;
            if (CollectionUtil.isNotEmpty(userAwardRecordV2VOList)) {
                List<UserAwardRecordV2VO> weekGiftList = userAwardRecordV2VOList.stream()
                        .filter(item -> item.getAwardType().equals(VIPAwardV2Enum.WEEK_BONUS.getCode()))
                        .toList();
                if (CollectionUtil.isNotEmpty(weekGiftList)) {
                    // 不为空
                    for (UserAwardRecordV2VO item : weekGiftList) {
                        GetVIPAwardVO weekGift = new GetVIPAwardVO();
                        weekGift.setAwardType(Integer.valueOf(VIPAwardV2Enum.WEEK_BONUS.getCode()));
                        String birthdayGiftName = I18nMessageUtil.getSystemParamAndTrans(VIP_AWARD_TYPE_CN, VIPAwardV2Enum.WEEK_BONUS.getCode());
                        weekGift.setAwardTypeName(birthdayGiftName);
                        weekGift.setAwardAmount(item.getAwardAmount());
                        weekGift.setReceiveStatus(item.getReceiveStatus());
                        weekGift.setOrderId(item.getId());
                        weekGift.setSort(CommonConstant.business_three);
                        isWeekAward = true;
                        vipAwardList.add(weekGift);
                    }
                }
            }
            if (!isWeekAward) {
                vipAwardList.add(createWeekAward(siteVipOptionVO));
            }
            getVIPAwardVO.setVipAwardVOS(vipAwardList);
            vipBenefitAPPs.add(getVIPAwardVO);
        }
        vipGradeBenefitsDetailAPP.setVipBenefitAPPs(vipBenefitAPPs);


        return vipGradeBenefitsDetailAPP;
    }

    private GetVIPAwardVO createAgeAward(SiteVipOptionVO siteVipOptionVO) {
        GetVIPAwardVO birthdayGift = new GetVIPAwardVO();
        birthdayGift.setAwardType(Integer.valueOf(VIPAwardV2Enum.BIRTH_BONUS.getCode()));
        String birthdayGiftName = I18nMessageUtil.getSystemParamAndTrans(VIP_AWARD_TYPE_CN, VIPAwardV2Enum.BIRTH_BONUS.getCode());
        birthdayGift.setAwardTypeName(birthdayGiftName);
        birthdayGift.setAwardAmount(siteVipOptionVO.getAgeAmount());
        birthdayGift.setReceiveStatus(CommonConstant.business_three);
        birthdayGift.setSort(CommonConstant.business_one);
        birthdayGift.setOrderId(null);
        return birthdayGift;
    }

    private GetVIPAwardVO createWeekAward(SiteVipOptionVO siteVipOptionVO) {
        GetVIPAwardVO weekGift = new GetVIPAwardVO();
        weekGift.setAwardType(Integer.valueOf(VIPAwardV2Enum.WEEK_BONUS.getCode()));
        String birthdayGiftName = I18nMessageUtil.getSystemParamAndTrans(VIP_AWARD_TYPE_CN, VIPAwardV2Enum.WEEK_BONUS.getCode());
        weekGift.setAwardTypeName(birthdayGiftName);
        weekGift.setAwardAmount(siteVipOptionVO.getWeekBonus());
        weekGift.setReceiveStatus(CommonConstant.business_three);
        weekGift.setOrderId(null);
        weekGift.setSort(CommonConstant.business_three);
        return weekGift;
    }
}
