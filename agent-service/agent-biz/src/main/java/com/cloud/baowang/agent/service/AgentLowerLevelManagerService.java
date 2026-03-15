package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.AgentCoinChangeWalletTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTransferAccountTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTransferTypeEnum;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.*;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentSubLineReqVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentSubLineResVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.agent.UAgentSubLineUserResVO;
import com.cloud.baowang.user.api.vo.agent.UserAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AgentLowerLevelManagerService {

    private final AgentInfoService agentInfoService;
    private final ReportUserWinLoseApi reportUserWinLoseApi;
    private final UserWithdrawRecordApi userWithdrawRecordApi;
    private final PlayServiceApi playServiceApi;
    private final OrderRecordApi orderRecordApi;
    private final AgentTransferService agentTransferService;
    private final AgentDepositSiteRecordService agentDepositSiteRecordService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final UserInfoApi userInfoApi;
    private final AgentCommissionService agentCommissionService;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    public Page<AgentLowerLevelManagerPageVO> listPage(AgentLowerLevelManagerPageReqVO pageVO) {
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(pageVO.getCurrencyCode())) {
            pageVO.setCurrencyCode(null);
        }
        String siteCode = pageVO.getSiteCode();
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountSite(siteCode, pageVO.getAgentAccount());
        if (ObjectUtil.isNull(agentInfoVO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentLowerLevelReqVO build = AgentLowerLevelReqVO.builder().level(agentInfoVO.getLevel()).siteCode(siteCode).agentId(agentInfoVO.getAgentId()).lowerLevelAccount(pageVO.getLowerLevelAccount()).agentAccount(pageVO.getAgentAccount()).build();
        // 直属下级
        Page<AgentInfoVO> agentInfoVOPage = agentInfoService.findAllDirectChildAgentsByPage(new Page<>(pageVO.getPageNumber(), pageVO.getPageSize()), build);
        Page<AgentLowerLevelManagerPageVO> result = new Page<>(agentInfoVOPage.getCurrent(), agentInfoVOPage.getSize(), agentInfoVOPage.getTotal());
        List<AgentInfoVO> records = agentInfoVOPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        // 汇率
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        String platformCoin = CurrReqUtils.getPlatCurrencyName();

        // 参数构建
        List<String> agentIdList = records.stream().map(AgentInfoVO::getAgentId).toList();
        UserAgentSubLineReqVO reqVO = new UserAgentSubLineReqVO();
        reqVO.setAgentIds(agentIdList);
        reqVO.setStartTime(pageVO.getStartTime());
        reqVO.setEndTime(pageVO.getEndTime());

        Map<String, AgentSubLineUserResVO> userResVOMap = Maps.newHashMap();
        // 下线用户数
        List<UAgentSubLineUserResVO> agentSubLineUserNum = userInfoApi.findAgentSubLineUserNum(reqVO);
        if (CollectionUtil.isNotEmpty(agentSubLineUserNum)) {
            for (UAgentSubLineUserResVO agentSubLineUserResVO : agentSubLineUserNum) {
                String agentId = agentSubLineUserResVO.getAgentId();
                AgentSubLineUserResVO subLineUserResVO = userResVOMap.get(agentId);
                if (ObjectUtil.isEmpty(subLineUserResVO)) {
                    subLineUserResVO = new AgentSubLineUserResVO();
                    subLineUserResVO.setAgentId(agentId);
                    subLineUserResVO.setSubLineUserNum(0);
                    subLineUserResVO.setUserFirstDepositNum(0);
                    subLineUserResVO.setUserFirstDepositAmount(BigDecimal.ZERO);
                    userResVOMap.put(agentId, subLineUserResVO);
                }
                subLineUserResVO.setSubLineUserNum(subLineUserResVO.getSubLineUserNum() + agentSubLineUserResVO.getSubLineUserNum());
            }
        }

        reqVO.setCurrency(pageVO.getCurrencyCode());
        // 首存人数 & 首存额
        List<UAgentSubLineUserResVO> userResVOList = userInfoApi.findAgentSubLineUserFirstDeposit(reqVO);
        if (CollectionUtil.isNotEmpty(userResVOList) && currency2Rate != null) {
            for (UAgentSubLineUserResVO agentSubLineUserResVO : userResVOList) {
                String agentId = agentSubLineUserResVO.getAgentId();
                AgentSubLineUserResVO subLineUserResVO = userResVOMap.get(agentId);
                if (ObjectUtil.isEmpty(subLineUserResVO)) {
                    subLineUserResVO = new AgentSubLineUserResVO();
                    subLineUserResVO.setAgentId(agentId);
                    subLineUserResVO.setSubLineUserNum(0);
                    subLineUserResVO.setUserFirstDepositNum(0);
                    subLineUserResVO.setUserFirstDepositAmount(BigDecimal.ZERO);
                    userResVOMap.put(agentId, subLineUserResVO);
                }
                subLineUserResVO.setUserFirstDepositNum(agentSubLineUserResVO.getUserFirstDepositNum() + subLineUserResVO.getUserFirstDepositNum());
                if (StringUtils.isEmpty(pageVO.getCurrencyCode())) {
                    // 转为平台币
                    subLineUserResVO.setUserFirstDepositAmount(AmountUtils.divide(agentSubLineUserResVO.getUserFirstDepositAmount(), currency2Rate.get(agentSubLineUserResVO.getMainCurrency())).add(subLineUserResVO.getUserFirstDepositAmount()));
                } else {
                    subLineUserResVO.setUserFirstDepositAmount(agentSubLineUserResVO.getUserFirstDepositAmount().add(subLineUserResVO.getUserFirstDepositAmount()));
                }
            }
        }

        // 存款额
        reqVO.setFundsOperateType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        WalletAgentSubLineReqVO walletAgentSubLineReqVO=new WalletAgentSubLineReqVO();
        BeanUtils.copyProperties(reqVO,walletAgentSubLineReqVO);
        List<WalletAgentSubLineResVO> userDepositVO = userWithdrawRecordApi.getUserFundsListByAgent(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> userDepositResVOMap = getAgentSubLineResGroupByAgent(userDepositVO, pageVO, currency2Rate);
        // 代理代存 - 均为平台币
        List<WalletAgentSubLineResVO> agentDepositSubordinates = agentDepositSiteRecordService.depositSubordinatesByAgentList(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> agentDepositSubordinatesVOMap = getAgentSubLineResGroupByAgent(agentDepositSubordinates, pageVO, currency2Rate);

        // 人工存款额
        walletAgentSubLineReqVO.setManualAdjustTypes(List.of(String.valueOf(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode())));
        walletAgentSubLineReqVO.setManualOperateType(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        List<WalletAgentSubLineResVO> userManualDepositVO = userWithdrawRecordApi.getManualAmountGroupAgent(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> userManualDepositVOMap = getAgentSubLineResGroupByAgent(userManualDepositVO, pageVO, currency2Rate);

        // 输赢 添加流水
        walletAgentSubLineReqVO.setSiteCode(siteCode);
        ReportAgentSubLineReqVO reportAgentSubLineReqVO=new ReportAgentSubLineReqVO();
        BeanUtils.copyProperties(walletAgentSubLineReqVO,reportAgentSubLineReqVO);
        List<ReportAgentSubLineResVO> userWinLoseByAgent = reportUserWinLoseApi.getUserWinLoseByAgent(reportAgentSubLineReqVO);
        Map<String, ReportAgentSubLineResVO> userWinLoseByAgentMap = getReportAgentSubLineResGroupByAgent(userWinLoseByAgent, pageVO, currency2Rate);

        // 活跃用户数
        AgentActiveNumberReqVO activeNumberReqVO = new AgentActiveNumberReqVO();
        activeNumberReqVO.setStartTime(pageVO.getStartTime());
        activeNumberReqVO.setEndTime(pageVO.getEndTime());
        activeNumberReqVO.setAgentIdList(agentIdList);
        List<AgentActiveUserResponseVO> agentActiveUserResponseVOs = agentCommissionService.getAgentActiveUserInfoList(activeNumberReqVO);
        Map<String, AgentActiveUserResponseVO> activeUserMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(agentActiveUserResponseVOs)) {
            Map<String, AgentActiveUserResponseVO> activeUserResponseVOMap = agentActiveUserResponseVOs.stream().collect(Collectors.toMap(AgentActiveUserResponseVO::getAgentId, e -> e, (o, n) -> n));
            activeUserMap.putAll(activeUserResponseVOMap);
        }

        List<AgentLowerLevelManagerPageVO> pageVOList = new ArrayList<>();
        for (AgentInfoVO vo : records) {
            AgentLowerLevelManagerPageVO managerPageVO = new AgentLowerLevelManagerPageVO();
            managerPageVO.setAgentAccount(vo.getAgentAccount());
            managerPageVO.setRegisterTime(vo.getRegisterTime());
            // 备注
            managerPageVO.setRemark(vo.getSuperRemark());
            // 下线用户数 & 首存人数 & 首存额
            AgentSubLineUserResVO agentSubLineUserResVO = userResVOMap.get(vo.getAgentId());
            if (ObjectUtil.isNotEmpty(agentSubLineUserResVO)) {
                BeanUtils.copyProperties(agentSubLineUserResVO, managerPageVO, "agentAccount");
            }
            // 存款额 + 人工存款额 + 代理代存
            WalletAgentSubLineResVO depositResVO = userDepositResVOMap.get(vo.getAgentAccount());
            if (ObjectUtil.isNotEmpty(depositResVO)) {
                managerPageVO.setUserDepositAmount(managerPageVO.getUserDepositAmount().add(depositResVO.getAmount()));
            }
            WalletAgentSubLineResVO manualDepositResVO = userManualDepositVOMap.get(vo.getAgentAccount());
            if (ObjectUtil.isNotEmpty(manualDepositResVO)) {
                managerPageVO.setUserDepositAmount(managerPageVO.getUserDepositAmount().add(manualDepositResVO.getAmount()));
            }
            WalletAgentSubLineResVO agentDepositResVo = agentDepositSubordinatesVOMap.get(vo.getAgentAccount());
            if (ObjectUtil.isNotEmpty(agentDepositResVo)) {
                managerPageVO.setUserDepositAmount(managerPageVO.getUserDepositAmount().add(agentDepositResVo.getAmount()));
            }

            // 输赢
            ReportAgentSubLineResVO winLoseResVO = userWinLoseByAgentMap.get(vo.getAgentAccount());
            if (ObjectUtil.isNotEmpty(winLoseResVO)) {
                managerPageVO.setUserWinLoseAmount(winLoseResVO.getAmount().negate());
                managerPageVO.setValidBetAmount(winLoseResVO.getValidBetAmount());
            }
            AgentActiveUserResponseVO agentActiveUserResponseVO = activeUserMap.get(vo.getAgentId());
            if (agentActiveUserResponseVO != null) {
                // 有效活跃
                managerPageVO.setValidActiveUserNum(agentActiveUserResponseVO.getActiveNumber() == null ? 0 : agentActiveUserResponseVO.getActiveNumber());
                // 有效新增
                managerPageVO.setValidAddUserNum(agentActiveUserResponseVO.getNewValidNumber() == null ? 0 : agentActiveUserResponseVO.getNewValidNumber());
            }
            if (StringUtils.isEmpty(pageVO.getCurrencyCode())) {
                managerPageVO.setCurrency(platformCoin);
            } else {
                managerPageVO.setCurrency(pageVO.getCurrencyCode());
            }
            pageVOList.add(managerPageVO);
        }
        result.setRecords(pageVOList);
        return result;
    }

    private Map<String, WalletAgentSubLineResVO> getAgentSubLineResGroupByAgent(List<WalletAgentSubLineResVO> userDepositVO, AgentLowerLevelManagerPageReqVO pageVO, Map<String, BigDecimal> currency2Rate) {
        Map<String, WalletAgentSubLineResVO> userDepositResVOMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(userDepositVO)) {
            for (WalletAgentSubLineResVO agentSubLineResVO : userDepositVO) {
                String agentAccount = agentSubLineResVO.getAgentAccount();
                WalletAgentSubLineResVO subLineUserResVO = userDepositResVOMap.get(agentAccount);
                if (ObjectUtil.isEmpty(subLineUserResVO)) {
                    subLineUserResVO = new WalletAgentSubLineResVO();
                    subLineUserResVO.setAgentAccount(agentAccount);
                    subLineUserResVO.setAmount(BigDecimal.ZERO);
                    userDepositResVOMap.put(agentAccount, subLineUserResVO);
                }

                if ((pageVO == null || StringUtils.isEmpty(pageVO.getCurrencyCode())) && currency2Rate != null) {
                    subLineUserResVO.setAmount(AmountUtils.divide(agentSubLineResVO.getAmount(), currency2Rate.get(agentSubLineResVO.getCurrency())).add(subLineUserResVO.getAmount()));
                } else {
                    subLineUserResVO.setAmount(agentSubLineResVO.getAmount().add(subLineUserResVO.getAmount()));
                }

            }
        }
        return userDepositResVOMap;
    }

    private Map<String, ReportAgentSubLineResVO> getReportAgentSubLineResGroupByAgent(List<ReportAgentSubLineResVO> userDepositVO, AgentLowerLevelManagerPageReqVO pageVO, Map<String, BigDecimal> currency2Rate) {
        Map<String, ReportAgentSubLineResVO> userDepositResVOMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(userDepositVO)) {
            for (ReportAgentSubLineResVO agentSubLineResVO : userDepositVO) {
                String agentAccount = agentSubLineResVO.getAgentAccount();
                ReportAgentSubLineResVO subLineUserResVO = userDepositResVOMap.get(agentAccount);
                if (ObjectUtil.isEmpty(subLineUserResVO)) {
                    subLineUserResVO = new ReportAgentSubLineResVO();
                    subLineUserResVO.setAgentAccount(agentAccount);
                    subLineUserResVO.setAmount(BigDecimal.ZERO);
                    subLineUserResVO.setValidBetAmount(BigDecimal.ZERO);
                    userDepositResVOMap.put(agentAccount, subLineUserResVO);
                }

                if ((pageVO == null || StringUtils.isEmpty(pageVO.getCurrencyCode())) && currency2Rate != null) {
                    subLineUserResVO.setAmount(AmountUtils.divide(agentSubLineResVO.getAmount(), currency2Rate.get(agentSubLineResVO.getCurrency())).add(subLineUserResVO.getAmount()));
                    subLineUserResVO.setValidBetAmount(AmountUtils.divide(agentSubLineResVO.getValidBetAmount(), currency2Rate.get(agentSubLineResVO.getCurrency())).add(subLineUserResVO.getValidBetAmount()));

                } else {
                    subLineUserResVO.setAmount(agentSubLineResVO.getAmount().add(subLineUserResVO.getAmount()));
                    subLineUserResVO.setValidBetAmount(agentSubLineResVO.getValidBetAmount().add(subLineUserResVO.getValidBetAmount()));
                }

            }
        }
        return userDepositResVOMap;
    }

    /**
     * 下级查询结果转map
     *
     * @param resVO
     * @return
     */
    private Map<String, WalletAgentSubLineResVO> getAgentSubLineResGroupByAgent(List<WalletAgentSubLineResVO> resVO) {
        Map<String, WalletAgentSubLineResVO> map = Maps.newHashMap();
        if (CollUtil.isNotEmpty(resVO)) {
            Optional.ofNullable(resVO).ifPresent(list -> map.putAll(list.stream().collect(Collectors.toMap(WalletAgentSubLineResVO::getAgentAccount, p -> p, (k1, k2) -> k2))));
        }
        return map;
    }

    public void editRemark(AgentLowerLevelManagerEditRemarkVO vo) {
        AgentInfoVO infoVO = new AgentInfoVO();
        infoVO.setAgentAccount(vo.getAgentAccount());
        infoVO.setSuperRemark(Strings.isBlank(vo.getRemark()) ? Strings.EMPTY : vo.getRemark());
        infoVO.setSiteCode(vo.getSiteCode());
        agentInfoService.updateAgentByAccount(infoVO);
    }

    public AgentLowerLevelManagerInfoVO info(AgentLowerLevelManagerInfoReqVO vo) {
        AgentInfoVO agentInfoVO = agentInfoService.getByCurrAgentAccount(vo.getAgentAccount());
        if (ObjectUtil.isNull(agentInfoVO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String siteCode = CurrReqUtils.getSiteCode();
        // 汇率
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        AgentLowerLevelManagerInfoVO infoVO = new AgentLowerLevelManagerInfoVO();
        infoVO.setAgentAccount(vo.getAgentAccount());
        infoVO.setRemark(agentInfoVO.getSuperRemark());
        infoVO.setRegisterTime(agentInfoVO.getRegisterTime());
        infoVO.setLastLoginTime(agentInfoVO.getLastLoginTime());
        UserAgentSubLineReqVO reqVO = UserAgentSubLineReqVO.builder().agentAccountList(List.of(vo.getAgentAccount())).agentIds(List.of(agentInfoVO.getAgentId())).startTime(vo.getStartTime()).endTime(vo.getEndTime()).build();
        // 下线用户数 & 首存人数 & 首存额
        Map<String, AgentSubLineUserResVO> userResVOMap = Maps.newHashMap();
        AgentSubLineUserResVO initsubLineUserResVO = new AgentSubLineUserResVO();
        initsubLineUserResVO.setAgentId(agentInfoVO.getAgentId());
        initsubLineUserResVO.setSubLineUserNum(0);
        initsubLineUserResVO.setUserFirstDepositNum(0);
        initsubLineUserResVO.setUserFirstDepositAmount(BigDecimal.ZERO);
        userResVOMap.put(agentInfoVO.getAgentId(), initsubLineUserResVO);
        // 下线用户数
        infoVO.setSubLineUserNum(0);
        List<UAgentSubLineUserResVO> agentSubLineUserNum = userInfoApi.findAgentSubLineUserNum(reqVO);
        if (CollUtil.isNotEmpty(agentSubLineUserNum)) {
            infoVO.setSubLineUserNum(agentSubLineUserNum.get(0).getSubLineUserNum());
        }
        reqVO.setCurrency(vo.getCurrencyCode());
        if (vo.getCurrencyCode().equals(CurrReqUtils.getPlatCurrencyCode())) {
            reqVO.setCurrency(null);
        }
        // 首存人数 & 首存额
        List<UAgentSubLineUserResVO> userResVOList = userInfoApi.findAgentSubLineUserFirstDeposit(reqVO);
        if (CollectionUtil.isNotEmpty(userResVOList) && currency2Rate != null) {
            for (UAgentSubLineUserResVO agentSubLineUserResVO : userResVOList) {
                String agentId = agentSubLineUserResVO.getAgentId();
                AgentSubLineUserResVO subLineUserResVO = userResVOMap.get(agentId);
                subLineUserResVO.setUserFirstDepositNum(agentSubLineUserResVO.getUserFirstDepositNum() + subLineUserResVO.getUserFirstDepositNum());
                if (CurrReqUtils.getPlatCurrencyCode().equals(vo.getCurrencyCode())) {
                    subLineUserResVO.setUserFirstDepositAmount(AmountUtils.divide(agentSubLineUserResVO.getUserFirstDepositAmount(), currency2Rate.get(agentSubLineUserResVO.getMainCurrency())).add(subLineUserResVO.getUserFirstDepositAmount()));
                } else {
                    subLineUserResVO.setUserFirstDepositAmount(agentSubLineUserResVO.getUserFirstDepositAmount().add(subLineUserResVO.getUserFirstDepositAmount()));
                }
            }
        }
        infoVO.setUserFirstDepositNum(userResVOMap.get(agentInfoVO.getAgentId()).getUserFirstDepositNum());
        infoVO.setUserFirstDepositAmount(userResVOMap.get(agentInfoVO.getAgentId()).getUserFirstDepositAmount());
        // 会员活跃数
        AgentActiveNumberReqVO activeNumberReqVO = new AgentActiveNumberReqVO();
        activeNumberReqVO.setStartTime(vo.getStartTime());
        activeNumberReqVO.setEndTime(vo.getEndTime());
        activeNumberReqVO.setAgentId(agentInfoVO.getAgentId());
        activeNumberReqVO.setSiteCode(siteCode);
        AgentActiveUserResponseVO agentActiveUserResponseVO = agentCommissionService.getAgentActiveUserInfo(activeNumberReqVO);
        AgentLowerLevelManagerPageReqVO pageReqVO = new AgentLowerLevelManagerPageReqVO();
        pageReqVO.setCurrencyCode(vo.getCurrencyCode());
        if (vo.getCurrencyCode().equals(CurrReqUtils.getPlatCurrencyCode())) {
            pageReqVO.setCurrencyCode(null);
        }
        if (agentActiveUserResponseVO != null) {
            //有效活跃人数
            infoVO.setValidActiveUserNum(agentActiveUserResponseVO.getActiveNumber() == null ? 0 : agentActiveUserResponseVO.getActiveNumber());
            //效新增人数
            infoVO.setValidAddUserNum(agentActiveUserResponseVO.getNewValidNumber() == null ? 0 : agentActiveUserResponseVO.getNewValidNumber());
        }
        // 存款额 + 人工存款额 +代理代存
        reqVO.setFundsOperateType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        WalletAgentSubLineReqVO walletAgentSubLineReqVO=new WalletAgentSubLineReqVO();
        BeanUtils.copyProperties(reqVO,walletAgentSubLineReqVO);
        List<WalletAgentSubLineResVO> userDepositVO = userWithdrawRecordApi.getUserFundsListByAgent(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> userFundsListMap = getAgentSubLineResGroupByAgent(userDepositVO, pageReqVO, currency2Rate);
        WalletAgentSubLineResVO userFundsVO = userFundsListMap.get(vo.getAgentAccount());
        if (ObjectUtil.isNotEmpty(userFundsVO)) {
            infoVO.setUserDepositAmount(infoVO.getUserDepositAmount().add(Optional.ofNullable(userFundsVO).map(WalletAgentSubLineResVO::getAmount).orElse(BigDecimal.ZERO)));
        }
        reqVO.setManualAdjustTypes(List.of(String.valueOf(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode())));
        reqVO.setManualOperateType(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        List<WalletAgentSubLineResVO> userManualDepositVO = userWithdrawRecordApi.getManualAmountGroupAgent(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> userManualDepositVOMap = getAgentSubLineResGroupByAgent(userManualDepositVO, pageReqVO, currency2Rate);
        WalletAgentSubLineResVO manualDepositResVO = userManualDepositVOMap.get(vo.getAgentAccount());
        if (ObjectUtil.isNotEmpty(manualDepositResVO)) {
            infoVO.setUserDepositAmount(infoVO.getUserDepositAmount().add(Optional.ofNullable(manualDepositResVO).map(WalletAgentSubLineResVO::getAmount).orElse(BigDecimal.ZERO)));
        }
        // 代理代存 - 均为平台币
        List<WalletAgentSubLineResVO> agentDepositSubordinates = agentDepositSiteRecordService.depositSubordinatesByAgentList(walletAgentSubLineReqVO);
        Map<String, WalletAgentSubLineResVO> agentDepositSubordinatesVOMap = getAgentSubLineResGroupByAgent(agentDepositSubordinates, pageReqVO, currency2Rate);
        WalletAgentSubLineResVO agentDepositResVO = agentDepositSubordinatesVOMap.get(vo.getAgentAccount());
        if (ObjectUtil.isNotEmpty(agentDepositResVO)) {
            infoVO.setUserDepositAmount(infoVO.getUserDepositAmount().add(Optional.ofNullable(agentDepositResVO).map(WalletAgentSubLineResVO::getAmount).orElse(BigDecimal.ZERO)));
        }
        //  输赢
        reqVO.setSiteCode(siteCode);
        ReportAgentSubLineReqVO reportAgentSubLineReqVO=new ReportAgentSubLineReqVO();
        BeanUtils.copyProperties(reqVO,reportAgentSubLineReqVO);
        List<ReportAgentSubLineResVO> userWinLoseByAgent = reportUserWinLoseApi.getUserWinLoseByAgent(reportAgentSubLineReqVO);
        Map<String, ReportAgentSubLineResVO> userWinLoseByAgentMap = getReportAgentSubLineResGroupByAgent(userWinLoseByAgent, pageReqVO, currency2Rate);
        ReportAgentSubLineResVO winLoseVO = userWinLoseByAgentMap.get(vo.getAgentAccount());
        if (ObjectUtil.isNotEmpty(winLoseVO)) {
            infoVO.setUserWinLoseAmount(winLoseVO.getAmount().negate());
            infoVO.setValidBetAmount(winLoseVO.getValidBetAmount());
        }
        infoVO.setCurrency(vo.getCurrencyCode());
        return infoVO;
    }

    /**
     * 下级详情-游戏动态-H5
     *
     * @param vo
     * @return
     */
    public Page<AgentLowerLevelInfoGameDynamicVO> gameDynamic(AgentLowerLevelInfoGameDynamicReqVO vo) {
        AgentInfoVO agentInfoVO = agentInfoService.getByCurrAgentAccount(vo.getAgentAccount());
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        paramVO.setAgentIds(List.of(agentInfoVO.getAgentId()));
        paramVO.setSiteCode(CurrReqUtils.getSiteCode());
        paramVO.setStartTime(vo.getStartTime());
        paramVO.setEndTime(vo.getEndTime());
        List<ReportAgentVenueWinLossVO> records = reportUserVenueWinLoseApi.queryAgentVenueWinLoss(paramVO);
        Page<AgentLowerLevelInfoGameDynamicVO> pageResult = new Page<>();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode());
        //这里已经调用siteCode查询场馆了，所以不需要传
        ResponseVO<List<VenueInfoVO>> response = playVenueInfoApi.venueInfoByCodeIds(records.stream().map(ReportAgentVenueWinLossVO::getVenueCode).filter(Strings::isNotBlank).distinct().toList());
        List<VenueInfoVO> responseVO = response.getData();
        Map<String, VenueInfoVO> venueInfoMap = new HashMap<>();
        if (CollUtil.isNotEmpty(responseVO)) {
            venueInfoMap = responseVO.stream()
                    .collect(Collectors.toMap(venueInfo -> venueInfo.getVenueCode(), venueInfo -> venueInfo, (existing, replacement) -> existing));
        }

        List<AgentLowerLevelInfoGameDynamicVO> resultList = Lists.newArrayList();
        Map<String, VenueInfoVO> finalVenueInfoMap = venueInfoMap;
        records.forEach(record -> {
            if (vo.getVenueCode() != null && !vo.getVenueCode().equals(record.getVenueCode())) {
                return;
            }
            AgentLowerLevelInfoGameDynamicVO gameDynamicVO = new AgentLowerLevelInfoGameDynamicVO();
            gameDynamicVO.setVenueCode(record.getVenueCode());
            BigDecimal winLossAmount = record.getWinLossAmount().negate();
            if (currency2Rate != null) {
                gameDynamicVO.setBetAmount(AmountUtils.divide(record.getValidAmount(), currency2Rate.get(record.getCurrency())));
                winLossAmount = AmountUtils.divide(winLossAmount, currency2Rate.get(record.getCurrency()));
            }
            // 换名字
            VenueInfoVO venueInfoVO = finalVenueInfoMap.get(record.getVenueCode());
            if (ObjectUtil.isNotEmpty(venueInfoVO)) {
                gameDynamicVO.setVenuePlatformName(venueInfoVO.getVenuePlatformName());
            }

            gameDynamicVO.setWinLoseAmount(winLossAmount);
            resultList.add(gameDynamicVO);
        });
        pageResult.setRecords(resultList);
        return pageResult;
    }

    /**
     * 下级详情-场馆统计-PC
     *
     * @param vo
     * @return
     */
    public List<AgentLowerLevelInfoVenueStatisticalVO> venueStatistical(AgentLowerLevelInfoVenueStatisticalReqVO vo) {
        AgentInfoVO agentInfoVO = agentInfoService.getByCurrAgentAccount(vo.getAgentAccount());
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        if(agentInfoVO != null){
            paramVO.setAgentIds(List.of(agentInfoVO.getAgentId()));
        }
        paramVO.setSiteCode(CurrReqUtils.getSiteCode());
        paramVO.setStartTime(vo.getStartTime());
        paramVO.setEndTime(vo.getEndTime());
        String siteCode = CurrReqUtils.getSiteCode();
        List<ReportAgentVenueWinLossVO> records = reportUserVenueWinLoseApi.queryAgentVenueWinLoss(paramVO);
        if (CollUtil.isEmpty(records)) {
            return new ArrayList<>();
        }
//这里已经调用siteCode查询场馆了，所以不需要传
        ResponseVO<List<VenueInfoVO>> response = playVenueInfoApi.venueInfoByCodeIds(records.stream().map(ReportAgentVenueWinLossVO::getVenueCode).filter(Strings::isNotBlank).distinct().toList());
        List<VenueInfoVO> responseVO = response.getData();
        Map<String, VenueInfoVO> venueInfoMap = new HashMap<>();
        if (CollUtil.isNotEmpty(responseVO)) {
            venueInfoMap = responseVO.stream()
                    .collect(Collectors.toMap(venueInfo -> venueInfo.getVenueCode(), venueInfo -> venueInfo, (existing, replacement) -> existing));
        }

        // 汇率
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        String platformCoin = CurrReqUtils.getPlatCurrencyCode();

        Map<String, AgentLowerLevelInfoVenueStatisticalVO> map = Maps.newHashMap();
        Map<String, VenueInfoVO> finalVenueInfoMap = venueInfoMap;
        records.forEach(svo -> {
            AgentLowerLevelInfoVenueStatisticalVO statisticalVO = map.get(svo.getVenueCode());
            if (statisticalVO == null) {
                statisticalVO = BeanUtil.toBean(svo, AgentLowerLevelInfoVenueStatisticalVO.class);
                statisticalVO.setWinLossAmount(BigDecimal.ZERO);
                statisticalVO.setBetAmount(BigDecimal.ZERO);
                statisticalVO.setCurrency(platformCoin);
                map.put(svo.getVenueCode(), statisticalVO);
            }
            String currency = svo.getCurrency();
            BigDecimal winLossAmount = svo.getWinLossAmount().negate();
            BigDecimal betAmount = svo.getValidAmount();
            if (currency2Rate != null) {
                winLossAmount = AmountUtils.divide(winLossAmount, currency2Rate.get(currency));
                betAmount = AmountUtils.divide(betAmount, currency2Rate.get(currency));
            }
            statisticalVO.setWinLossAmount(statisticalVO.getWinLossAmount().add(winLossAmount));
            statisticalVO.setBetAmount(statisticalVO.getBetAmount().add(betAmount));
            // 换名字
            VenueInfoVO venueInfoVO = finalVenueInfoMap.get(svo.getVenueCode());
            if (ObjectUtil.isNotEmpty(venueInfoVO)) {
                statisticalVO.setVenuePlatformName(venueInfoVO.getVenuePlatformName());
            }

        });

        if (CollUtil.isNotEmpty(map)) {

            List<AgentLowerLevelInfoVenueStatisticalVO> statisticalVOS = new ArrayList<>(map.values().stream().toList());
            AgentLowerLevelInfoVenueStatisticalVO total = new AgentLowerLevelInfoVenueStatisticalVO();
            BigDecimal totalBetAmount = statisticalVOS.stream().map(AgentLowerLevelInfoVenueStatisticalVO::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalWinLossAmount = statisticalVOS.stream().map(AgentLowerLevelInfoVenueStatisticalVO::getWinLossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            total.setBetAmount(totalBetAmount);
            total.setWinLossAmount(totalWinLossAmount);
            total.setCurrency(platformCoin);
            total.setVenueCodeText("总计");
            String totalText= I18nMessageUtil.getI18NMessage(ConstantsCode.TOTAL_SHOW_CODE);
            total.setVenuePlatformName(totalText);
            statisticalVOS.add(total);
            return statisticalVOS;
        }
        return new ArrayList<>();
    }

    /**
     * 分配日志
     *
     * @param vo
     * @return
     */
    public Page<AgentDistributeLogPageVO> distributeLog(AgentDistributeLogReqVO vo) {
        Integer accountType = vo.getAccountType();
        Page<AgentDistributeLogPageVO> result = new Page<>(vo.getPageNumber(), vo.getPageSize());
        // 会员
        if (accountType.equals(AgentTransferAccountTypeEnum.MEMBER.getType())) {
            if (vo.getTransferType().equals(AgentTransferTypeEnum.IN.getType())) {
                return result;
            }
            result = agentDepositSiteRecordService.distributeLog(vo);
            List<AgentDistributeLogPageVO> records = result.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                for (AgentDistributeLogPageVO record : records) {
                    record.setAccountTypeText(AgentTransferAccountTypeEnum.MEMBER.getDesc());
                    record.setTransferOutCoin(AgentCoinChangeWalletTypeEnum.nameOfCode(record.getWalletType()));
                }
            }
        }
        // 代理
        if (accountType.equals(AgentTransferAccountTypeEnum.AGENT.getType())) {
            result = agentTransferService.distributeLog(vo);
            List<AgentDistributeLogPageVO> records = result.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                for (AgentDistributeLogPageVO record : records) {
                    record.setAccountTypeText(AgentTransferAccountTypeEnum.AGENT.getDesc());
                    record.setAccount(AgentTransferTypeEnum.IN.getType().equals(vo.getTransferType()) ? record.getTransferAccount() : record.getCollectionAccount());
                    String walletName = AgentCoinChangeWalletTypeEnum.nameOfCode(record.getWalletType());
                    record.setTransferOutCoin(walletName);
                    record.setTransferInCoin(walletName);
                }
            }
        }

        return result;
    }

    public Map<String, Object> getDistributeLogDownBox() {
        record CodeNameRecord(String code, String name) {
        }

        // 代理转账账号类型
        List<CodeNameRecord> transferAccountType = AgentTransferAccountTypeEnum.getList().stream().map(s -> new CodeNameRecord(String.valueOf(s.getType()), s.getDesc())).toList();
        // 代理转出转入类型类型
        List<CodeNameRecord> transferType = AgentTransferTypeEnum.getList().stream().map(s -> new CodeNameRecord(String.valueOf(s.getType()), s.getDesc())).toList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("transferAccountType", transferAccountType);
        map.put("transferType", transferType);
        return map;

    }
}
