package com.cloud.baowang.wallet.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.activity.api.enums.DepositTypeEnum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.SystemRechargeChannelVO;
import com.cloud.baowang.pay.api.vo.SystemWithdrawChannelVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.SitSecurityBalanceMqVO;
import com.cloud.baowang.common.kafka.vo.UserRechargeWithdrawMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.ValidInviteUserRechargeMqVO;
import com.cloud.baowang.pay.api.api.PayRechargeWithdrawApi;
import com.cloud.baowang.system.api.api.bank.BankChannelManagerApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.bank.BankChannelManageRspVO;
import com.cloud.baowang.system.api.vo.bank.ChannelBankRelationReqVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.wallet.api.enums.SiteSecurityCoinTypeEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecuritySourceCoinTypeEnums;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserRechargeReqVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWtihdrawMqSendVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawTriggerVO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.po.UserWithdrawalManualRecordPO;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalAuditRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.repositories.UserWithdrawalManualRecordRepository;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserDepositWithdrawHandleService {


    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

//    private final UserCoinService userCoinService;

    private final UserDepositWithdrawalAuditRepository userDepositWithdrawalAuditRepository;


    private final UserInfoApi userInfoApi;

    private final SiteApi siteApi;

    private final PayRechargeWithdrawApi payRechargeWithdrawApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    private final RechargeWithdrawSocketService rechargeWithdrawSocketService;

    private final UserWithdrawalManualRecordRepository userWithdrawalManualRecordRepository;

    private final SystemWithdrawChannelRepository withdrawChannelRepository;



    private final UserTypingAmountService userTypingAmountService;

    private final SiteSecurityBalanceService siteSecurityBalanceService;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final BankChannelManagerApi bankChannelManagerApi;

    private final WalletUserCommonCoinService userCommonCoinService;

    /**
     * 提款终审通过处理
     *
     * @param userDepositWithdrawalPO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int withdrawReviewSuccess(final UserDepositWithdrawalPO userDepositWithdrawalPO, UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO, boolean isEnd) {

        int num = this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);

        this.userDepositWithdrawalAuditRepository.insert(userDepositWithdrawalAuditPO);
        if (isEnd) {
            if (ChannelTypeEnums.OFFLINE.getType().equals(userDepositWithdrawalPO.getPayoutType())) {
                //发起人工确认
                withdrawManualPay(userDepositWithdrawalPO);
            } else {
                //发起三方提款
                withdrawPay(userDepositWithdrawalPO);
            }

        }

        return num;
    }

    private void withdrawManualPay(UserDepositWithdrawalPO userDepositWithdrawalPO) {
        UserWithdrawalManualRecordPO userWithdrawalManualRecordPO = ConvertUtil.entityToModel(userDepositWithdrawalPO, UserWithdrawalManualRecordPO.class);
        userWithdrawalManualRecordPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());
        userWithdrawalManualRecordPO.setCreatedTime(userDepositWithdrawalPO.getUpdatedTime());
        userWithdrawalManualRecordPO.setCreator(CurrReqUtils.getAccount());
        userWithdrawalManualRecordPO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
        userWithdrawalManualRecordRepository.insert(userWithdrawalManualRecordPO);

    }

    private void withdrawPay(UserDepositWithdrawalPO userDepositWithdrawalPO) {
        BigDecimal amount = userDepositWithdrawalPO.getTradeCurrencyAmount();
        String currencyCode = userDepositWithdrawalPO.getCurrencyCode();
        if (CurrencyEnum.KVND.getCode().equals(userDepositWithdrawalPO.getCurrencyCode())
                && CurrencyEnum.KVND.getCode().equals(userDepositWithdrawalPO.getCoinCode())) {
            amount = amount.multiply(new BigDecimal(1000));
            currencyCode = CurrencyEnum.VND.getCode();
        }
        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setSiteCode(userDepositWithdrawalPO.getSiteCode());
        withdrawalVO.setUserId(userDepositWithdrawalPO.getUserId());
        withdrawalVO.setBankNo(userDepositWithdrawalPO.getDepositWithdrawAddress());
        withdrawalVO.setBankName(userDepositWithdrawalPO.getAccountType());
        withdrawalVO.setIfscCode(userDepositWithdrawalPO.getIfscCode());
        if(CurrencyEnum.INR.getCode().equals(userDepositWithdrawalPO.getCurrencyCode())){
            withdrawalVO.setBankCode(userDepositWithdrawalPO.getIfscCode());
        }else{
            withdrawalVO.setBankCode(userDepositWithdrawalPO.getAccountBranch());
        }
        withdrawalVO.setAmount(amount.setScale(2, RoundingMode.DOWN).toString());
        withdrawalVO.setChannelId(userDepositWithdrawalPO.getDepositWithdrawChannelId());
        withdrawalVO.setAccountType(CommonConstant.business_zero);
        withdrawalVO.setBankUserName(userDepositWithdrawalPO.getDepositWithdrawSurname());
        withdrawalVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        withdrawalVO.setDeviceType(userDepositWithdrawalPO.getDeviceType());
        withdrawalVO.setApplyIp(userDepositWithdrawalPO.getApplyIp());
        withdrawalVO.setCurrency(currencyCode);
        withdrawalVO.setCreateTime(userDepositWithdrawalPO.getCreatedTime());
        withdrawalVO.setChainType(userDepositWithdrawalPO.getAccountType());
        withdrawalVO.setOwnerUserType(OwnerUserTypeEnum.USER.getCode());
        withdrawalVO.setToAddress(userDepositWithdrawalPO.getDepositWithdrawAddress());
        withdrawalVO.setTelephone(userDepositWithdrawalPO.getTelephone());
        withdrawalVO.setEmail(userDepositWithdrawalPO.getEmail());
        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
            ChannelBankRelationReqVO channelBankRelationReqVO = new ChannelBankRelationReqVO();
            channelBankRelationReqVO.setBankCode(userDepositWithdrawalPO.getAccountBranch());
            channelBankRelationReqVO.setChannelCode(userDepositWithdrawalPO.getChannelCode());
            channelBankRelationReqVO.setChannelName(userDepositWithdrawalPO.getDepositWithdrawChannelName());
            channelBankRelationReqVO.setCurrencyCode(userDepositWithdrawalPO.getCurrencyCode());
            ResponseVO<BankChannelManageRspVO> responseVO = bankChannelManagerApi.getSystemChannelBankRelation(channelBankRelationReqVO);
            if(responseVO.isOk() && null != responseVO.getData()){
                BankChannelManageRspVO bankChannelManageRspVO = responseVO.getData();
                withdrawalVO.setBankCode(bankChannelManageRspVO.getBankChannelMapping());
            }
        }

        SystemWithdrawChannelPO withdrawChannelPO = withdrawChannelRepository.selectById(userDepositWithdrawalPO.getDepositWithdrawChannelId());
        SystemWithdrawChannelVO withdrawChannelVO = ConvertUtil.entityToModel(withdrawChannelPO, SystemWithdrawChannelVO.class);
        withdrawalVO.setWithdrawChannelVO(withdrawChannelVO);
        log.info("会员提款订单:{}, 发起三方支付参数:{}", userDepositWithdrawalPO.getOrderNo(), JSON.toJSONString(withdrawalVO));
        WithdrawalResponseVO responseVO = payRechargeWithdrawApi.withdrawal(withdrawalVO);
        log.info("会员提款订单:{}, 发起三方支付返回信息:{}", userDepositWithdrawalPO.getOrderNo(), responseVO);
        if (responseVO != null) {
            Integer withdrawOrderStatus = responseVO.getWithdrawOrderStatus();
            if (!PayoutStatusEnum.Pending.getCode().equals(withdrawOrderStatus)
                    && !PayoutStatusEnum.Success.getCode().equals(withdrawOrderStatus)) {
                if (StringUtils.isNotBlank(responseVO.getMessage())) {
                    ResultCode resultCode = ResultCode.getResultCodeByName(responseVO.getMessage());
                    if (resultCode != null) {
                        throw new BaowangDefaultException(resultCode);
                    } else {
                        throw new BaowangDefaultException(responseVO.getMessage());
                    }
                } else {
                    throw new BaowangDefaultException(ResultCode.THIRD_WITHDRAW_FAIL);
                }
            } else {
                userDepositWithdrawalPO.setPayTxId(responseVO.getWithdrawOrderId());
                userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.THIRD_WITHDRAW_FAIL);
        }
    }


    /**
     * 提款成功
     *
     * @param userDepositWithdrawalPO
     * @param userCoinAddVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void withdrawSuccess(UserInfoVO userInfoVO, UserDepositWithdrawalPO userDepositWithdrawalPO, UserCoinAddVO userCoinAddVO) {
        this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        if (!userCommonCoinService.userCommonCoinAdd(userCoinAddVO).getResult()) {
            log.error("该订单:{}, 提款成功添加支出账变失败", userDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }
        try {

            BigDecimal largeAmount = BigDecimal.ZERO;
            if (YesOrNoEnum.YES.getCode().equals(userDepositWithdrawalPO.getIsBigMoney())) {
                largeAmount = userDepositWithdrawalPO.getApplyAmount();
            }
            DepositWtihdrawMqSendVO depositWtihdrawMqSendVO = DepositWtihdrawMqSendVO.builder().feeAmount(userDepositWithdrawalPO.getFeeAmount())
                    .dateTime(userDepositWithdrawalPO.getUpdatedTime()).settlementFeeAmount(userDepositWithdrawalPO.getSettlementFeeAmount())
                    .amount(userDepositWithdrawalPO.getApplyAmount()).depositWithdrawWayId(userDepositWithdrawalPO.getDepositWithdrawWayId())
                    .wayFeeAmount(userDepositWithdrawalPO.getWayFeeAmount()).largeAmount(largeAmount).build();
            withdrawMq(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class), depositWtihdrawMqSendVO);
            if(ChannelTypeEnums.OFFLINE.getType().equals(userDepositWithdrawalPO.getPayoutType())){
                //提款成功扣除冻结保证金
                securityBalance(false, userDepositWithdrawalPO, SiteSecuritySourceCoinTypeEnums.USER_MANUAL_WITHDRAW.getCode(),
                        SiteSecurityCoinTypeEnums.WITHDRAW_SUCCESS.getCode(), userCoinAddVO.getCoinValue());
            }else {
                //提款成功扣除冻结保证金
                securityBalance(false, userDepositWithdrawalPO, SiteSecuritySourceCoinTypeEnums.USER_WITHDRAW.getCode(),
                        SiteSecurityCoinTypeEnums.WITHDRAW_SUCCESS.getCode(), userCoinAddVO.getCoinValue());
            }
            if(CommonConstant.business_one_str.equals(userDepositWithdrawalPO.getIsFirstOut())){
                sendFirstWithdrawTrigger(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class), userDepositWithdrawalPO.getUpdatedTime(), userDepositWithdrawalPO.getApplyAmount(), userDepositWithdrawalPO.getOrderNo());
            }
            rechargeWithdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS, userDepositWithdrawalPO.getSiteCode(),
                    userDepositWithdrawalPO.getUserId(), userDepositWithdrawalPO.getUserAccount(), userDepositWithdrawalPO.getArriveAmount()
                    , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), userDepositWithdrawalPO.getCurrencyCode());


        } catch (Exception e) {
            e.printStackTrace();
            log.error("会员提款订单成功，发送通知失败{}",e.getMessage());
        }

    }

    /**
     * 提款成功(人工确认出款)
     *
     * @param userDepositWithdrawalPO
     * @param userCoinAddVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualWithdrawSuccess(UserDepositWithdrawalPO userDepositWithdrawalPO, UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO, UserCoinAddVO userCoinAddVO) {
        this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        if (null != userDepositWithdrawalAuditPO) {
            this.userDepositWithdrawalAuditRepository.insert(userDepositWithdrawalAuditPO);
        }
        if (!userCommonCoinService.userCommonCoinAdd(userCoinAddVO).getResult()) {
            log.error("该订单:{}, 提款成功添加支出账变失败", userDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public void withdrawFail(UserDepositWithdrawalPO userDepositWithdrawalPO, UserDepositWithdrawalAuditPO userDepositWithdrawalAuditPO, UserCoinAddVO userCoinAddVO) {
        this.userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        if (null != userDepositWithdrawalAuditPO) {
            this.userDepositWithdrawalAuditRepository.insert(userDepositWithdrawalAuditPO);
        }

        CoinRecordResultVO coinRecordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
        if (!coinRecordResultVO.getResult()) {
            log.error("该订单:{}, 提款失败添加解冻账变失败", userDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }
        try {
            //提款失败增加保证金
            securityBalance(false,userDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.USER_WITHDRAW.getCode(),
                    SiteSecurityCoinTypeEnums.WITHDRAW_FAIL.getCode(),userCoinAddVO.getCoinValue());
            rechargeWithdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED, userDepositWithdrawalPO.getSiteCode(),
                    userDepositWithdrawalPO.getUserId(), userDepositWithdrawalPO.getUserAccount(), userDepositWithdrawalPO.getArriveAmount()
                    , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), userDepositWithdrawalPO.getCurrencyCode());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送提款失败消息 失败{}",e.getMessage());
        }


    }

    /**
     * 充值成功修改订单，插入账变等信息
     *
     * @param userCoinAddVO
     * @param userTypingAmountRequestVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void depositSuccess(UserInfoVO userInfoVO, UserDepositWithdrawalPO userDepositWithdrawalPO,
                               UserCoinAddVO userCoinAddVO,
                               UserTypingAmountRequestVO userTypingAmountRequestVO,boolean isNew) {
        int num = 0;
        if (isNew) {
            num = userDepositWithdrawalRepository.insert(userDepositWithdrawalPO);
        } else {
            //更新订单信息
            num = userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
        }


        if(num > 0 ){
            //校验是否清除打码量
            userTypingAmountService.userTypingAmountCleanZeroByUserId(userInfoVO.getUserId());
            //添加充值账变
            CoinRecordResultVO recordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
            if (!recordResultVO.getResult()) {
                log.info("充值账变错误:{}", recordResultVO);
                throw new BaowangDefaultException(ResultCode.CHANGE_RECORD_ADD_FAIL);
            }

            try {

                //发送打码量mq
                List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmountRequestVO);
                UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
                KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
                DepositWtihdrawMqSendVO vo = DepositWtihdrawMqSendVO.builder().userInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)).orderNo(userDepositWithdrawalPO.getOrderNo())
                        .dateTime(userDepositWithdrawalPO.getUpdatedTime()).amount(userDepositWithdrawalPO.getArriveAmount())
                        .feeAmount(userDepositWithdrawalPO.getFeeAmount()).settlementFeeAmount(userDepositWithdrawalPO.getSettlementFeeAmount())
                        .wayFeeAmount(userDepositWithdrawalPO.getWayFeeAmount()).depositWithdrawWayId(userDepositWithdrawalPO.getDepositWithdrawWayId()).build();
                rechargeMq(vo);
                if(!ChannelTypeEnum.SITE_CUSTOM.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawChannelType())) {
                    securityBalance(false, userDepositWithdrawalPO, SiteSecuritySourceCoinTypeEnums.USER_DEPOSIT.getCode(),
                            SiteSecurityCoinTypeEnums.USER_DEPOSIT.getCode(), userCoinAddVO.getCoinValue());
                }
                List<String> userIds = new ArrayList<>();
                userIds.add(userDepositWithdrawalPO.getUserId());
                RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
                rechargeSuccessVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
                rechargeSuccessVO.setCustomerStatus(userDepositWithdrawalPO.getCustomerStatus());
                rechargeSuccessVO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
                //发送实时状态
                sendWebSocketMessage(userDepositWithdrawalPO.getSiteCode(), userIds, rechargeSuccessVO);


                //首存通知
                if (null == userInfoVO.getFirstDepositTime()) {
                    rechargeWithdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_SECURITY, userDepositWithdrawalPO.getSiteCode(),
                            userDepositWithdrawalPO.getUserId(), userDepositWithdrawalPO.getUserAccount(), userDepositWithdrawalPO.getArriveAmount()
                            , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), userDepositWithdrawalPO.getCurrencyCode());
                }
                //存款完成通知
                rechargeWithdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS, userDepositWithdrawalPO.getSiteCode(),
                        userDepositWithdrawalPO.getUserId(), userDepositWithdrawalPO.getUserAccount(), userDepositWithdrawalPO.getArriveAmount()
                        , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), userDepositWithdrawalPO.getCurrencyCode());


            } catch (Exception e) {
                e.printStackTrace();
                log.error("充值发送打码量，消息通知失败{}",e.getMessage());
            }
        }

    }

    private void  securityBalance(boolean isWithdrawApply,UserDepositWithdrawalPO userDepositWithdrawalPO,String securitySourceCoinType,String securityCoinType,BigDecimal coinValue){

        log.info("调用保证金开始,账变金额{}",coinValue);
        SitSecurityBalanceMqVO sitSecurityBalanceMqVO = new SitSecurityBalanceMqVO();
        sitSecurityBalanceMqVO.setSourceOrderNo(userDepositWithdrawalPO.getOrderNo());
        sitSecurityBalanceMqVO.setUserId(userDepositWithdrawalPO.getUserId());
        sitSecurityBalanceMqVO.setUserName(userDepositWithdrawalPO.getUserAccount());
        sitSecurityBalanceMqVO.setSiteCode(userDepositWithdrawalPO.getSiteCode());
        sitSecurityBalanceMqVO.setSourceCoinType(securitySourceCoinType);
        sitSecurityBalanceMqVO.setCoinType(securityCoinType);
        BigDecimal currencyUsdExchangeRate = null == userDepositWithdrawalPO.getCurrencyUsdExchangeRate()?BigDecimal.ZERO:userDepositWithdrawalPO.getCurrencyUsdExchangeRate();
        log.info("调用保证金,USD汇率{}",currencyUsdExchangeRate);
        BigDecimal usdAmount = BigDecimal.ZERO;
        if(currencyUsdExchangeRate!=null && currencyUsdExchangeRate.compareTo(BigDecimal.ZERO)!=0){
            usdAmount =coinValue.divide(currencyUsdExchangeRate,4,RoundingMode.DOWN);
        }
        sitSecurityBalanceMqVO.setAdjustAmount(usdAmount);
        log.info("调用保证金,USD金额{}",usdAmount);
        sitSecurityBalanceMqVO.setUpdateUser(userDepositWithdrawalPO.getUpdater());//使用最后审核人员
        if(isWithdrawApply){
            SiteSecurityBalanceChangeRecordReqVO balanceChangeRecordReqVO = ConvertUtil.entityToModel(sitSecurityBalanceMqVO,SiteSecurityBalanceChangeRecordReqVO.class);
            siteSecurityBalanceService.recordBalanceChangeLog(balanceChangeRecordReqVO);
        }else{
            KafkaUtil.send(TopicsConstants.SITE_SECURITY_BALANCE, sitSecurityBalanceMqVO);
        }
        log.info("调用保证金结束USD金额{}",usdAmount);
    }

    public void sendWebSocketMessage(String siteCode, List<String> userIds, RechargeSuccessVO rechargeSuccessVO) {
        // ws信息推送
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(siteCode);
        messageMqVO.setUidList(userIds);
        messageMqVO.setClientTypeEnum(ClientTypeEnum.CLIENT);
        messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.RECHARGE_SUCCESS_FAIL.getTopic(), ResponseVO.success(rechargeSuccessVO)));
        log.info("会员充值发送实时状态消息开始,内容{}", com.alibaba.fastjson.JSON.toJSONString(messageMqVO));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        log.info("会员充值发送实时状态消息结束,内容{}", com.alibaba.fastjson.JSON.toJSONString(messageMqVO));

    }

    /**
     * 提款申请成功添加插入订单，异步修改账户地址信息，添加账变
     *
     * @param userDepositWithdrawalPO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int withdrawApplySuccess(UserDepositWithdrawalPO userDepositWithdrawalPO, UserCoinAddVO userCoinAddVO) {
        int num = this.userDepositWithdrawalRepository.insert(userDepositWithdrawalPO);

        String userAccount = userDepositWithdrawalPO.getUserAccount();

        //提款扣减保证金
        securityBalance(true,userDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.USER_WITHDRAW.getCode(),
                SiteSecurityCoinTypeEnums.USER_WITHDRAW.getCode(),userCoinAddVO.getCoinValue());

        CoinRecordResultVO coinRecordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);

        if (!coinRecordResultVO.getResult()) {
            log.info("会员{}提款申请成功，添加账变失败！", userAccount);
            throw new BaowangDefaultException(ResultCode.WITHDRAW_APPLY_FAIL);
        }

        try {
            //统计待审核记录条数,设置路由地址
            String siteCode = userDepositWithdrawalPO.getSiteCode();
            List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                    , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),
                    DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode());
            LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserDepositWithdrawalPO::getSiteCode, siteCode);
            lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            lqw.in(UserDepositWithdrawalPO::getStatus, statusList);
            Long l = userDepositWithdrawalRepository.selectCount(lqw);

            //发送消息通知站点后台小铃铛
            rechargeWithdrawSocketService.sendUserWithdrawApply(siteCode, l, "123");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息推送失败{}",e.getMessage());
        }
        return num;
    }


    @Transactional(rollbackFor = Exception.class)
    public String depositApplySuccess(UserRechargeReqVO userRechargeReqVo, UserDepositWithdrawalPO userDepositWithdrawalPO, SiteSystemRechargeChannelRespVO systemRechargeChannelPO) {

        userDepositWithdrawalRepository.insert(userDepositWithdrawalPO);
        String paymentUrl = "";
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawChannelType())){
            return  paymentUrl;
        }
        BigDecimal amount = userRechargeReqVo.getAmount();
        String currencyCode = userDepositWithdrawalPO.getCurrencyCode();
        if (CurrencyEnum.KVND.getCode().equals(userDepositWithdrawalPO.getCurrencyCode())) {
            amount = amount.multiply(new BigDecimal(1000));
            currencyCode = CurrencyEnum.VND.getCode();
        }
        SystemRechargeChannelVO rechargeChannelVO = ConvertUtil.entityToModel(systemRechargeChannelPO, SystemRechargeChannelVO.class);

        PaymentVO paymentVO = PaymentVO.builder()
                .amount(String.valueOf(amount))
                .currency(currencyCode)
                .channelId(userDepositWithdrawalPO.getDepositWithdrawChannelId())
                .accountType(CommonConstant.business_zero)
                .userId(userRechargeReqVo.getUserId())
                .orderId(userDepositWithdrawalPO.getOrderNo())
                .applyIp(userRechargeReqVo.getApplyIp())
                .depositName(userRechargeReqVo.getDepositName())
                .rechargeChannelVO(rechargeChannelVO)
                .build();
        log.info("三方支付发起参数{}" + JSON.toJSONString(paymentVO));
        ResponseVO<PaymentResponseVO> paymentResponseVOResponseVO = payRechargeWithdrawApi.payment(paymentVO);
        PaymentResponseVO paymentResponseVO = paymentResponseVOResponseVO.getData();
        log.info("三方支付发起结果{}" + JSON.toJSONString(paymentResponseVOResponseVO));
        if (paymentResponseVOResponseVO.isOk() && null != paymentResponseVO
                && CommonConstant.business_zero.equals(paymentResponseVO.getCode())) {
            userDepositWithdrawalPO.setPayThirdUrl(paymentResponseVO.getPaymentUrl());
            userDepositWithdrawalPO.setRemark(paymentResponseVO.getMessage());
            userDepositWithdrawalPO.setPayTxId(paymentResponseVO.getThirdOrderId());
            userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
            paymentUrl = paymentResponseVO.getPaymentUrl();
        } else {
            throw new BaowangDefaultException(ResultCode.NO_CHANNEL_AVAILABLE);
        }

        return paymentUrl;
    }

    public void withdrawMq(WalletUserInfoVO userInfo, DepositWtihdrawMqSendVO vo) {
        //发送存款累计
        UserRechargeWithdrawMqVO userRechargeWithdrawMqVO = new UserRechargeWithdrawMqVO();
        userRechargeWithdrawMqVO.setType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString());
        userRechargeWithdrawMqVO.setUserId(userInfo.getUserId());
        userRechargeWithdrawMqVO.setUserAccount(userInfo.getUserAccount());
        userRechargeWithdrawMqVO.setAccountType(userInfo.getAccountType());
        userRechargeWithdrawMqVO.setAgentId(userInfo.getSuperAgentId());
        userRechargeWithdrawMqVO.setCurrency(userInfo.getMainCurrency());
        userRechargeWithdrawMqVO.setDepositWithdrawWayId(vo.getDepositWithdrawWayId());
        userRechargeWithdrawMqVO.setFeeAmount(null == vo.getFeeAmount() ? BigDecimal.ZERO : vo.getFeeAmount());
        userRechargeWithdrawMqVO.setWayFeeAmount(null == vo.getWayFeeAmount() ? BigDecimal.ZERO : vo.getWayFeeAmount());
        userRechargeWithdrawMqVO.setSettlementFeeAmount(null == vo.getSettlementFeeAmount() ? BigDecimal.ZERO : vo.getSettlementFeeAmount());
        userRechargeWithdrawMqVO.setAgentAccount(userInfo.getSuperAgentAccount());
        userRechargeWithdrawMqVO.setSiteCode(userInfo.getSiteCode());
        userRechargeWithdrawMqVO.setLargeAmount(null == vo.getLargeAmount() ? BigDecimal.ZERO : vo.getLargeAmount());
        Long currentTime = vo.getDateTime();
        userRechargeWithdrawMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(currentTime));
        SiteVO siteInfo = siteApi.getSiteInfoByCode(userInfo.getSiteCode());
        String timeZone = siteInfo.getTimezone();
        Long dayMillis = TimeZoneUtils.getStartOfDayInTimeZone(currentTime, timeZone);
        userRechargeWithdrawMqVO.setDayMillis(dayMillis);
        userRechargeWithdrawMqVO.setDayStr(DateUtils.formatDateByZoneId(dayMillis, DateUtils.DATE_FORMAT_1, timeZone));
        userRechargeWithdrawMqVO.setAmount(null == vo.getAmount() ? BigDecimal.ZERO : vo.getAmount());
        log.info("开始发送提款累计:{}", userRechargeWithdrawMqVO);
        KafkaUtil.send(TopicsConstants.USER_RECHARGE_WITHDRAW, userRechargeWithdrawMqVO);

    }

    /**
     * @param
     */
    public void rechargeMq(DepositWtihdrawMqSendVO vo) {
        WalletUserInfoVO userInfo = vo.getUserInfoVO();
        //发送存款累计
        UserRechargeWithdrawMqVO userRechargeWithdrawMqVO = new UserRechargeWithdrawMqVO();
        userRechargeWithdrawMqVO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString());
        userRechargeWithdrawMqVO.setUserId(userInfo.getUserId());
        userRechargeWithdrawMqVO.setAccountType(userInfo.getAccountType());
        userRechargeWithdrawMqVO.setUserAccount(userInfo.getUserAccount());
        userRechargeWithdrawMqVO.setAgentId(userInfo.getSuperAgentId());
        userRechargeWithdrawMqVO.setCurrency(userInfo.getMainCurrency());
        userRechargeWithdrawMqVO.setDepositWithdrawWayId(vo.getDepositWithdrawWayId());
        userRechargeWithdrawMqVO.setAgentAccount(userInfo.getSuperAgentAccount());
        userRechargeWithdrawMqVO.setSiteCode(userInfo.getSiteCode());
        userRechargeWithdrawMqVO.setFeeAmount(null == vo.getFeeAmount() ? BigDecimal.ZERO : vo.getFeeAmount());
        userRechargeWithdrawMqVO.setWayFeeAmount(null == vo.getWayFeeAmount() ? BigDecimal.ZERO : vo.getWayFeeAmount());
        userRechargeWithdrawMqVO.setSettlementFeeAmount(null == vo.getSettlementFeeAmount() ? BigDecimal.ZERO : vo.getSettlementFeeAmount());
        userRechargeWithdrawMqVO.setDepositSubordinatesAmount(null == vo.getDepositSubordinatesAmount() ? BigDecimal.ZERO : vo.getDepositSubordinatesAmount());
        Long currentTime = vo.getDateTime();
        userRechargeWithdrawMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(currentTime));
        SiteVO siteInfo = siteApi.getSiteInfoByCode(userInfo.getSiteCode());
        String timeZone = siteInfo.getTimezone();
        Long dayMillis = TimeZoneUtils.getStartOfDayInTimeZone(currentTime, timeZone);
        userRechargeWithdrawMqVO.setDayMillis(dayMillis);
        userRechargeWithdrawMqVO.setDayStr(DateUtils.formatDateByZoneId(dayMillis, DateUtils.DATE_FORMAT_1, timeZone));
        userRechargeWithdrawMqVO.setAmount(vo.getAmount());
        log.info("开始发送存款累计:{}", userRechargeWithdrawMqVO);
        KafkaUtil.send(TopicsConstants.USER_RECHARGE_WITHDRAW, userRechargeWithdrawMqVO);

        //处理有效邀请
        handleValidInvite(userInfo,vo.getAmount());

        // 好友邀请充值触发
        RechargeTriggerVO triggerVO = new RechargeTriggerVO();
        triggerVO.setUserId(userInfo.getUserId());
        triggerVO.setUserAccount(userInfo.getUserAccount());
        triggerVO.setSiteCode(userInfo.getSiteCode());
        log.info("开始发送邀请好友充值信息:{}", triggerVO);
        KafkaUtil.send(TopicsConstants.CALL_FRIEND_MEMBER_RECHARGE, triggerVO);

        sendRechargeTrigger(userInfo, vo.getDateTime(), vo.getAmount(), vo.getOrderNo());

    }

    private void handleValidInvite(WalletUserInfoVO userInfo, BigDecimal amount) {
        if (StringUtils.isNotBlank(userInfo.getInviterId() )){
            ValidInviteUserRechargeMqVO validInviteVO = new ValidInviteUserRechargeMqVO();
            validInviteVO.setUserId(userInfo.getUserId());
            validInviteVO.setUserAccount(userInfo.getUserAccount());
            validInviteVO.setSiteCode(userInfo.getSiteCode());
            validInviteVO.setAmount(amount);
            validInviteVO.setSiteCode(userInfo.getSiteCode());
            if (null == userInfo.getFirstDepositTime()){
                validInviteVO.setIsFirstDeposit(1);
            }else {
                validInviteVO.setIsFirstDeposit(0);
            }
            KafkaUtil.send(TopicsConstants.VALID_INVITE_USER_RECHARGE, validInviteVO);
        }
    }

    private void sendRechargeTrigger(WalletUserInfoVO userInfo, Long dateTime, BigDecimal amount, String orderNo) {
        //开始处理 首存、次存活动
        RechargeTriggerVO rechargeTriggerVO = new RechargeTriggerVO();
        rechargeTriggerVO.setRechargeTime(dateTime);
        rechargeTriggerVO.setRechargeAmount(amount);
        rechargeTriggerVO.setUserAccount(userInfo.getUserAccount());
        rechargeTriggerVO.setSiteCode(userInfo.getSiteCode());
        rechargeTriggerVO.setCurrencyCode(userInfo.getMainCurrency());
        rechargeTriggerVO.setUserId(userInfo.getUserId());
        rechargeTriggerVO.setOrderNumber(orderNo);
        rechargeTriggerVO.setRegisterTime(userInfo.getRegisterTime());
        //更新用户首存信息
        if (null == userInfo.getFirstDepositTime()) {
            //是否首存
            log.info("更新会员次存信息:", userInfo.getUserAccount());
            userInfoApi.updateByUserId(userInfo.getUserId(), amount);
            rechargeTriggerVO.setDepositType(DepositTypeEnum.FIRST_DEPOSIT.getValue());
        } else if (null == userInfo.getSecondDepositTime()) {
            log.info("更新会员次存信息:", userInfo.getUserAccount());
            //是否次存
            userInfoApi.updateSecondDeposit(userInfo.getUserId(), amount);
            rechargeTriggerVO.setDepositType(DepositTypeEnum.SECOND_DEPOSIT.getValue());
        } else {
            rechargeTriggerVO.setDepositType(DepositTypeEnum.ONE_DEPOSIT.getValue());
        }
        if (StringUtils.isNotBlank(userInfo.getUserLabelId())) {
            List<String> labelIds = Arrays.asList(userInfo.getUserLabelId().split(","));
            //会员标签 不参与活动
            List<GetUserLabelByIdsVO> labelList = siteUserLabelConfigApi.getUserLabelByIds(labelIds);
            List<String> userLabelList = labelList.stream()
                    .map(GetUserLabelByIdsVO::getLabelId)
                    .collect(Collectors.toList());
            if (!userLabelList.contains(UserLabelEnum.NO_PARTICIPATION_ACTIVITY.getLabelId())) {
                log.info("开始发送存首存、次存消息:{}", rechargeTriggerVO);
                KafkaUtil.send(TopicsConstants.MEMBER_RECHARGE, rechargeTriggerVO);
            }
        } else {
            KafkaUtil.send(TopicsConstants.MEMBER_RECHARGE, rechargeTriggerVO);
        }


    }
    private void sendFirstWithdrawTrigger(WalletUserInfoVO userInfo, Long dateTime, BigDecimal amount, String orderNo) {
        //开始处理 首存、次存活动
        UserWithdrawTriggerVO withdrawTriggerVO = new UserWithdrawTriggerVO();
        withdrawTriggerVO.setWithdrawTime(dateTime);
        withdrawTriggerVO.setWithdrawAmount(amount);
        withdrawTriggerVO.setUserAccount(userInfo.getUserAccount());
        withdrawTriggerVO.setSiteCode(userInfo.getSiteCode());
        withdrawTriggerVO.setCurrencyCode(userInfo.getMainCurrency());
        withdrawTriggerVO.setUserId(userInfo.getUserId());
        withdrawTriggerVO.setOrderNumber(orderNo);
        withdrawTriggerVO.setRegisterTime(userInfo.getRegisterTime());
        if (StringUtils.isNotBlank(userInfo.getUserLabelId())) {
            List<String> labelIds = Arrays.asList(userInfo.getUserLabelId().split(","));
            //会员标签 不参与活动
            List<GetUserLabelByIdsVO> labelList = siteUserLabelConfigApi.getUserLabelByIds(labelIds);
            List<String> userLabelList = labelList.stream()
                    .map(GetUserLabelByIdsVO::getLabelId)
                    .collect(Collectors.toList());
            if (!userLabelList.contains(UserLabelEnum.NO_PARTICIPATION_ACTIVITY.getLabelId())) {
                log.info("开始发送首提消息:{}", withdrawTriggerVO);
                KafkaUtil.send(TopicsConstants.MEMBER_WITHDRAW, withdrawTriggerVO);
            }
        } else {
            log.info("开始发送首提消息:{}", withdrawTriggerVO);
            KafkaUtil.send(TopicsConstants.MEMBER_WITHDRAW, withdrawTriggerVO);
        }


    }


}
