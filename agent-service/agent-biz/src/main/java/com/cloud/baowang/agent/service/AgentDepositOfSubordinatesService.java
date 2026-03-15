package com.cloud.baowang.agent.service;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentProxyDepositVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesVO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentDepositSubordinatesRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawHandleApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import com.cloud.baowang.common.core.enums.ResultCode;

/**
 * @className: 代会员存款
 * @author: wade
 * @description: 代会员存款
 * @date: 2024/6/18 20:28
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentDepositOfSubordinatesService {

    private final UserInfoApi userInfoApi;

    private final AgentInfoRepository agentInfoRepository;

    private final AgentCommissionCoinService agentCommissionCoinService;

    private final AgentQuotaCoinService agentQuotaCoinService;

    private final AgentDepositWithdrawHandleService agentDepositWtihdrawHandleService;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final UserDepositWithdrawHandleApi userDepositWithdrawHandleApi;

    private final UserNoticeApi userNoticeApi;

    private final AgentDepositSubordinatesRepository agentDepositSubordinatesRepository;

    private final UserTypingAmountApi userTypingAmountApi;




    @DistributedLock(name = RedisConstants.ACTIVITY_GET_REWARD_LOCK, unique = "#vo.siteCode + ':' + #vo.agentAccount", waitTime = 3, leaseTime = 180)
    public Integer depositOfSubordinates(AgentDepositOfSubordinatesVO vo) {

        if (StringUtils.isNotBlank(vo.getRemark()) && vo.getRemark().length() > 50) {
            if (vo.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        // 设置为额度钱包 todo wade AgentDepositSubordinatesTypeEnum
        vo.setDepositSubordinatesType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        String agentAccount = vo.getAgentAccount();
        BigDecimal mainCurrencyAmount = vo.getDepositAmount();
        String userAccount = vo.getUserAccount();

        if (vo.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_CANNOT_BE_ZERO);
        }
        //校验代存用户信息
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccountAndSiteCode(userAccount, vo.getSiteCode());
        if (null == userInfoVO) {
            throw new BaowangDefaultException(ResultCode.AGENT_USER_ACCOUNT_NOT_EXIST);
        }
        if (StringUtils.isBlank(userInfoVO.getMainCurrency())) {
            throw new BaowangDefaultException(ResultCode.USER_MAIN_CURRENCY_NOT_NULL);
        }
        //校验代理信息
        String superAgentAccount = userInfoVO.getSuperAgentAccount();
        if (null == superAgentAccount || !superAgentAccount.equals(agentAccount)) {
            throw new BaowangDefaultException(ResultCode.AGENT_USER_NOT_SUBORDINATES);
        }
        AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(
                new LambdaQueryWrapper<AgentInfoPO>()
                        .eq(AgentInfoPO::getSiteCode, vo.getSiteCode())
                        .eq(AgentInfoPO::getAgentAccount, agentAccount));
        if (agentInfoPO.getStatus().equals(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_ABNORMAL);
        }
        // 校验流水倍数
        BigDecimal runningWaterMultiple = vo.getRunningWaterMultiple();
        // 校验流水倍数是否为整数
        if (runningWaterMultiple == null) {
            // 流水倍数不是1到100的整数
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_NULL_DESC);
        }
        if (runningWaterMultiple.compareTo(BigDecimal.ONE) < 0
                || runningWaterMultiple.compareTo(new BigDecimal(100)) >= 0 || runningWaterMultiple.scale() > 0) {
            // 流水倍数不是1到100的整数
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }

        //校验金额范围，和今日提款额度
                /*AgentWithdrawConfigVO agentWithdrawConfigVO = agentWithdrawConfigService.getWithdrawConfigByAgentAccount(agentAccount);
                BigDecimal bankSingleWithdrawMaxAmount = agentWithdrawConfigVO.getBankCardWithdrawMaxQuota();
                if (bankSingleWithdrawMaxAmount.compareTo(mainCurrencyAmount) < 0) {
                    throw new BaowangDefaultException(ResultCode.AGENT_GREATER_MAX_AMOUNT);
                }
                BigDecimal bankSingleWithdrawMinAmount = agentWithdrawConfigVO.getBankCardWithdrawMinQuota();
                if (mainCurrencyAmount.compareTo(bankSingleWithdrawMinAmount) < 0) {
                    throw new BaowangDefaultException(ResultCode.AGENT_LESS_MIN_AMOUNT);
                }
                BigDecimal todayAmount = getTodayDepositSubordinatesAmount(agentAccount);

                if((todayAmount.add(mainCurrencyAmount)).compareTo(agentWithdrawConfigVO.getDayWithdrawTotalAmount()) >= 0){
                    throw new BaowangDefaultException(ResultCode.AGENT_TODAY_DEPOSIT_SUBORDINATES_GT);
                }*/
        //校验支付密码
        if (StringUtils.isBlank(agentInfoPO.getPayPassword())) {
            throw new BaowangDefaultException(ResultCode.AGENT_PAY_PASSWORD_NOT_SET);
        } else {
            if (!checkPayPassword(vo.getPayPassword(), agentInfoPO)) {
                throw new BaowangDefaultException(ResultCode.AGENT_PAY_PASSWORD_ERROR);
            }
        }
        AgentCoinBalanceVO agentCoinBalanceVO = null;
        //校验是否有欠款
        if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.COMMISSION_DEPOSIT_SUBORDINATES.getCode().equals(vo.getDepositSubordinatesType())) {
            if (CommonConstant.business_one.equals(agentInfoPO.getIsAgentArrears())) {
                throw new BaowangDefaultException(ResultCode.AGENT_IS_ARREARS);
            }
            agentCoinBalanceVO = agentCommissionCoinService.getCommissionCoinBalanceSite(agentAccount, agentInfoPO.getSiteCode());
        } else if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.QUOTA_DEPOSIT_SUBORDINATES.getCode().equals(vo.getDepositSubordinatesType())) {
            agentCoinBalanceVO = agentQuotaCoinService.getQuotaCoinBalanceSite(agentAccount, agentInfoPO.getSiteCode());
        }
        // 计算转换 法币转换为平台币
        PlatCurrencyToTransferVO transferVO = PlatCurrencyToTransferVO.builder()
                .siteCode(vo.getSiteCode())
                .sourceAmt(mainCurrencyAmount)
                .sourceCurrencyCode(userInfoVO.getMainCurrency())
                .build();
        ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO = siteCurrencyInfoApi.transferToPlat(transferVO);
        if (!siteCurrencyConvertRespVOResponseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.SERVER_INTERNAL_ERROR);
        }
        // 查询汇率
        BigDecimal currencyFinalRate = siteCurrencyInfoApi.getCurrencyFinalRate(vo.getSiteCode(), userInfoVO.getMainCurrency());
        BigDecimal platFormCurrencyAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
        //
        if (null == agentCoinBalanceVO || null == agentCoinBalanceVO.getAvailableAmount() || agentCoinBalanceVO.getAvailableAmount().compareTo(platFormCurrencyAmount) < 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_COIN_AMOUNT_NOT_ENOUGH);
        }
        //插入代存记录
        String orderNo = "D" + SnowFlakeUtils.getSnowId();
        Long currentTime = System.currentTimeMillis();
        AgentDepositSubordinatesPO agentDepositSubordinatesPO = new AgentDepositSubordinatesPO();
        agentDepositSubordinatesPO.setAgentId(agentInfoPO.getAgentId());
        agentDepositSubordinatesPO.setAgentAccount(agentInfoPO.getAgentAccount());
        agentDepositSubordinatesPO.setAgentName(agentInfoPO.getName());
        agentDepositSubordinatesPO.setParentId(agentInfoPO.getParentId());
        agentDepositSubordinatesPO.setPath(agentInfoPO.getPath());
        agentDepositSubordinatesPO.setLevel(agentInfoPO.getLevel());
        agentDepositSubordinatesPO.setOrderNo(orderNo);
        agentDepositSubordinatesPO.setDepositTime(currentTime);
        //

        agentDepositSubordinatesPO.setAmount(mainCurrencyAmount);
        agentDepositSubordinatesPO.setCurrencyCode(userInfoVO.getMainCurrency());
        agentDepositSubordinatesPO.setRunningWaterMultiple(vo.getRunningWaterMultiple());
        agentDepositSubordinatesPO.setDepositSubordinatesType(vo.getDepositSubordinatesType());
        agentDepositSubordinatesPO.setUserId(userInfoVO.getUserId());
        agentDepositSubordinatesPO.setUserAccount(userInfoVO.getUserAccount());
        agentDepositSubordinatesPO.setAccountType(userInfoVO.getAccountType());
        agentDepositSubordinatesPO.setUserName(userInfoVO.getUserName());
        agentDepositSubordinatesPO.setRemark(vo.getRemark());
        agentDepositSubordinatesPO.setCreatedTime(currentTime);
        agentDepositSubordinatesPO.setSiteCode(vo.getSiteCode());
        agentDepositSubordinatesPO.setCurrencyCode(userInfoVO.getMainCurrency());
        // 平台币金额与汇率
        agentDepositSubordinatesPO.setPlatformAmount(platFormCurrencyAmount);
        agentDepositSubordinatesPO.setTransferRate(currencyFinalRate);
//                agentDepositSubordinatesRepository.insert(agentDepositSubordinatesPO);

        //添加代理账变
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(agentAccount);
        agentCoinAddVO.setOrderNo(orderNo);
        // 平台币
        agentCoinAddVO.setCoinValue(platFormCurrencyAmount);
        // 支出
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        String userCoinRemark = "";
        String type = "";
        if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.COMMISSION_DEPOSIT_SUBORDINATES.getCode().equals(vo.getDepositSubordinatesType())) {
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES_MEMBER.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
            agentCoinAddVO.setRemark("转给" + userAccount);
//                    agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
            userCoinRemark = vo.getRemark();
        } else if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.QUOTA_DEPOSIT_SUBORDINATES.getCode().equals(vo.getDepositSubordinatesType())) {

            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES_MEMBER.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
            agentCoinAddVO.setRemark("转给" + userAccount);
            agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
//                    agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);
            userCoinRemark = vo.getRemark();
        }
        //添加会员账变
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        // 会员是代理代存
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.TRANSFER_FROM_SUPERIOR.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        userCoinAddVO.setCoinValue(mainCurrencyAmount);
        userCoinAddVO.setRemark(userCoinRemark);

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        AgentInfoVO agentInfoVO = ConvertUtil.entityToModel(agentInfoPO, AgentInfoVO.class);
        agentCoinAddVO.setAgentInfo(agentInfoVO);
//                walletFeignResource.addCoin(userCoinAddVO);

        //校验是否清除打码量
        userTypingAmountApi.userTypingAmountCleanZeroByUserId(userInfoVO.getUserId());
        agentDepositWtihdrawHandleService.handleDepositOfSubordinates(agentDepositSubordinatesPO, agentCoinAddVO, userCoinAddVO, vo.getDepositSubordinatesType());


        //增加会员打码量
        UserTypingAmountRequestVO userTypingAmountRequestVO = new UserTypingAmountRequestVO();
        userTypingAmountRequestVO.setUserAccount(userAccount);
        userTypingAmountRequestVO.setUserId(userInfoVO.getUserId());
        userTypingAmountRequestVO.setSiteCode(userInfoVO.getSiteCode());
        // 校验流水倍数
        BigDecimal typingAmount = NumberUtil.round(NumberUtil.mul(mainCurrencyAmount, vo.getRunningWaterMultiple()), 2);
        userTypingAmountRequestVO.setTypingAmount(typingAmount);
        userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVO.setOrderNo(orderNo);
        userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.DEPOSIT.getCode());
        userTypingAmountRequestVO.setRemark("代理代存添加流水");
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmountRequestVO);
        // 发送 发送消息，添加打码量
        //rabbitTemplate.convertAndSend(MqConstants.USER_TYPING_AMOUNT_BATCH_QUEUE, JSON.toJSONString(userTypingAmountRequestVOS));
        UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
        // 清除打码量
        //userTypingAmountApi.userTypingAmountCleanZeroByUserId(userInfoVO.getUserId());
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
        DepositWtihdrawMqSendVO sendVO = DepositWtihdrawMqSendVO.builder().orderNo(orderNo)
                .dateTime(currentTime).amount(mainCurrencyAmount).depositSubordinatesAmount(mainCurrencyAmount).userInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)).build();
        userDepositWithdrawHandleApi.rechargeMq(sendVO);

        userNoticeApi.add(buildSystemMessageVO(vo, userInfoVO));
        return 0;
    }

    public UserNoticeTargetAddVO buildSystemMessageVO(AgentDepositOfSubordinatesVO vo, UserInfoVO userInfoVO) {

        UserLanguageVO languageVO = new UserLanguageVO();
        languageVO.setUserType(CommonConstant.business_zero_str);
        languageVO.setMessageType(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS);
        languageVO.setUserId(userInfoVO.getUserId());
        UserSystemMessageConfigVO messageConfigVO = userInfoApi.getUserLanguage(languageVO);

        UserNoticeTargetAddVO result = new UserNoticeTargetAddVO();
        result.setUserId(userInfoVO.getUserId());
        result.setNoticeType(CommonConstant.business_four);
        result.setReadState(CommonConstant.business_zero);
        result.setPlatform(CommonConstant.business_one);
        result.setDeleteState(CommonConstant.business_one);
        result.setRevokeState(CommonConstant.business_one);
        result.setMessageContentI18nCode(messageConfigVO.getContentI18nCode());
        result.setNoticeTitleI18nCode(messageConfigVO.getTitleI18nCode());
        String contentConvertValue = userInfoVO.getUserAccount() + "," + vo.getDepositAmount() + "," + userInfoVO.getMainCurrency();
        result.setContentConvertValue(contentConvertValue);
        result.setSystemMessageCode(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode());
        return result;
    }

    /**
     * 校验支付密码
     *
     * @return
     */
    public boolean checkPayPassword(String payPassword, AgentInfoPO agentInfoPO) {
        String salt = agentInfoPO.getSalt();
        String payPasswordEncrypt = agentInfoPO.getPayPassword();
        String payPasswordOldEncrypt = AgentServerUtil.getEncryptPassword(payPassword, salt);
        // 去掉校验 todo wade
        return StringUtils.equals(payPasswordEncrypt, payPasswordOldEncrypt);
    }

    public AgentProxyDepositVO getAgentProxyDepositVO(String agentId) {

        LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositSubordinatesPO::getAgentId, agentId);

        List<AgentDepositSubordinatesPO> agentDepositSubordinatesPOS = this.agentDepositSubordinatesRepository.selectList(lqw);
        AgentProxyDepositVO agentProxyDepositVO = new AgentProxyDepositVO();
        agentProxyDepositVO.setQuotaProxyDepositAmount(BigDecimal.ZERO);
        agentProxyDepositVO.setQuotaProxyDepositNum(CommonConstant.business_zero);
        if (null != agentDepositSubordinatesPOS && !agentDepositSubordinatesPOS.isEmpty()) {

            BigDecimal totalAmount = agentDepositSubordinatesPOS.stream().map(AgentDepositSubordinatesPO::getPlatformAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            agentProxyDepositVO.setQuotaProxyDepositAmount(totalAmount);
            agentProxyDepositVO.setQuotaProxyDepositNum(agentDepositSubordinatesPOS.size());
        }
        return agentProxyDepositVO;
    }

}
