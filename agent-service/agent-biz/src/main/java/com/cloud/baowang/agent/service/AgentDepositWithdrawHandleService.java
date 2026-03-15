package com.cloud.baowang.agent.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWtihdrawMqSendVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeReqVO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalAuditPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.po.AgentWithdrawalManualRecordPO;
import com.cloud.baowang.agent.repositories.AgentDepositSubordinatesRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalAuditRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawalManualRecordRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.SystemRechargeChannelVO;
import com.cloud.baowang.pay.api.vo.SystemWithdrawChannelVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AgentRechargeWithdrawMqVO;
import com.cloud.baowang.common.kafka.vo.SitSecurityBalanceMqVO;
import com.cloud.baowang.pay.api.api.PayRechargeWithdrawApi;
import com.cloud.baowang.system.api.api.bank.BankChannelManagerApi;
import com.cloud.baowang.system.api.vo.bank.BankChannelManageRspVO;
import com.cloud.baowang.system.api.vo.bank.ChannelBankRelationReqVO;
import com.cloud.baowang.wallet.api.api.SiteSecurityBalanceApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.SiteSecurityCoinTypeEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecuritySourceCoinTypeEnums;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
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
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AgentDepositWithdrawHandleService {


    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;

//    private final AgentCommissionCoinService agentCommissionCoinService;

    private final AgentDepositWithdrawalAuditRepository agentDepositWithdrawalAuditRepository;

//    private final AgentQuotaCoinService agentQuotaCoinService;

    private final AgentDepositSubordinatesRepository agentDepositSubordinatesRepository;

    private final UserCoinApi userCoinApi;

    private final PayRechargeWithdrawApi payRechargeWithdrawApi;

    private final AgentRechargeWithdrawSocketService agentRechargeWithdrawSocketService;

    private final AgentWithdrawalManualRecordRepository agentWithdrawalManualRecordRepository;

    private final SystemWithdrawChannelApi systemWithdrawChannelApi;

    private final SiteSecurityBalanceApi siteSecurityBalanceApi;

    private final BankChannelManagerApi bankChannelManagerApi;

    private final AgentCommonCoinService agentCommonCoinService;


    /**
     * 提款终审通过处理
     *
     * @param agentDepositWithdrawalPO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int withdrawReviewSuccess(final AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentDepositWithdrawalAuditPO agentDepositWithdrawalAuditPO, boolean isEnd) {

        int num = this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);

        this.agentDepositWithdrawalAuditRepository.insert(agentDepositWithdrawalAuditPO);
        if (isEnd) {
            if (ChannelTypeEnums.OFFLINE.getType().equals(agentDepositWithdrawalPO.getPayoutType())) {
                //发起人工确认
                withdrawManualPay(agentDepositWithdrawalPO);
            } else {
                //发起三方提款
                withdrawPay(agentDepositWithdrawalPO);
            }
        }

        return num;
    }

    private void withdrawManualPay(AgentDepositWithdrawalPO agentDepositWithdrawalPO) {
        AgentWithdrawalManualRecordPO agentWithdrawalManualRecordPO = ConvertUtil.entityToModel(agentDepositWithdrawalPO, AgentWithdrawalManualRecordPO.class);
        agentWithdrawalManualRecordPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());
        agentWithdrawalManualRecordPO.setCreatedTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentWithdrawalManualRecordPO.setCreator(CurrReqUtils.getAccount());
        agentWithdrawalManualRecordPO.setUpdatedTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentWithdrawalManualRecordRepository.insert(agentWithdrawalManualRecordPO);

    }

    private void withdrawPay(AgentDepositWithdrawalPO agentDepositWithdrawalPO) {

        BigDecimal amount = agentDepositWithdrawalPO.getTradeCurrencyAmount();
        String currencyCode = agentDepositWithdrawalPO.getCurrencyCode();
        if (CurrencyEnum.KVND.getCode().equals(agentDepositWithdrawalPO.getCurrencyCode())
                && CurrencyEnum.KVND.getCode().equals(agentDepositWithdrawalPO.getCoinCode())) {
            amount = amount.multiply(new BigDecimal(1000));
            currencyCode = CurrencyEnum.VND.getCode();
        }

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setSiteCode(agentDepositWithdrawalPO.getSiteCode());
        withdrawalVO.setUserId(agentDepositWithdrawalPO.getAgentId());
        withdrawalVO.setBankNo(agentDepositWithdrawalPO.getDepositWithdrawAddress());
        withdrawalVO.setBankName(agentDepositWithdrawalPO.getAccountType());
        withdrawalVO.setIfscCode(agentDepositWithdrawalPO.getIfscCode());
        if(CurrencyEnum.INR.getCode().equals(agentDepositWithdrawalPO.getCurrencyCode())){
            withdrawalVO.setBankCode(agentDepositWithdrawalPO.getIfscCode());
        }else{
            withdrawalVO.setBankCode(agentDepositWithdrawalPO.getAccountBranch());
        }

        withdrawalVO.setAmount(amount.setScale(2, RoundingMode.DOWN).toString());
        withdrawalVO.setChannelId(agentDepositWithdrawalPO.getDepositWithdrawChannelId());
        withdrawalVO.setAccountType(CommonConstant.business_one);
        withdrawalVO.setBankUserName(agentDepositWithdrawalPO.getDepositWithdrawSurname());
        withdrawalVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        withdrawalVO.setDeviceType(agentDepositWithdrawalPO.getDeviceType());
        withdrawalVO.setApplyIp(agentDepositWithdrawalPO.getApplyIp());
        withdrawalVO.setCurrency(currencyCode);
        withdrawalVO.setCreateTime(agentDepositWithdrawalPO.getCreatedTime());
        withdrawalVO.setChainType(agentDepositWithdrawalPO.getAccountType());
        withdrawalVO.setOwnerUserType(OwnerUserTypeEnum.AGENT.getCode());
        withdrawalVO.setToAddress(agentDepositWithdrawalPO.getDepositWithdrawAddress());
        withdrawalVO.setTelephone(agentDepositWithdrawalPO.getTelephone());
        withdrawalVO.setEmail(agentDepositWithdrawalPO.getEmail());
        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())){
            ChannelBankRelationReqVO channelBankRelationReqVO = new ChannelBankRelationReqVO();
            channelBankRelationReqVO.setBankCode(agentDepositWithdrawalPO.getAccountBranch());
            channelBankRelationReqVO.setChannelCode(agentDepositWithdrawalPO.getChannelCode());
            channelBankRelationReqVO.setChannelName(agentDepositWithdrawalPO.getDepositWithdrawChannelName());
            channelBankRelationReqVO.setCurrencyCode(agentDepositWithdrawalPO.getCurrencyCode());
            ResponseVO<BankChannelManageRspVO> responseVO = bankChannelManagerApi.getSystemChannelBankRelation(channelBankRelationReqVO);
            if(responseVO.isOk() && null != responseVO.getData()){
                BankChannelManageRspVO bankChannelManageRspVO = responseVO.getData();
                withdrawalVO.setBankCode(bankChannelManageRspVO.getBankChannelMapping());
            }
        }
        IdVO idVO = IdVO.builder().id(agentDepositWithdrawalPO.getDepositWithdrawChannelId()).build();
        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = systemWithdrawChannelApi.getChannelById(idVO);
        SystemWithdrawChannelVO withdrawChannelVO = ConvertUtil.entityToModel(withdrawChannelResponseVO, SystemWithdrawChannelVO.class);
        withdrawalVO.setWithdrawChannelVO(withdrawChannelVO);
        WithdrawalResponseVO responseVO = payRechargeWithdrawApi.withdrawal(withdrawalVO);
        log.info("该订单:{}, 发起三方代付返回信息:{}", agentDepositWithdrawalPO.getOrderNo(), responseVO);
        if (responseVO != null) {
            Integer withdrawOrderStatus = responseVO.getWithdrawOrderStatus();
            if (!PayoutStatusEnum.Pending.getCode().equals(withdrawOrderStatus) && !PayoutStatusEnum.Success.getCode().equals(withdrawOrderStatus)) {
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
                agentDepositWithdrawalPO.setPayTxId(responseVO.getWithdrawOrderId());
                agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.THIRD_WITHDRAW_FAIL);
        }
    }


    /**
     * 提款成功
     *
     * @param agentDepositWithdrawalPO
     * @param agentCoinAddVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void withdrawSuccess(AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentCoinAddVO agentCoinAddVO) {
        this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        if (!agentCommonCoinService.agentCommonCommissionCoinAdd(agentCoinAddVO)) {
            log.error("该订单:{}, 提款成功添加支出账变失败", agentDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }
        try {
            BigDecimal largeAmount = BigDecimal.ZERO;
            if (YesOrNoEnum.YES.getCode().equals(agentDepositWithdrawalPO.getIsBigMoney())) {
                largeAmount = agentDepositWithdrawalPO.getApplyAmount();
            }
            AgentDepositWtihdrawMqSendVO vo = AgentDepositWtihdrawMqSendVO.builder()
                    .feeAmount(agentDepositWithdrawalPO.getFeeAmount()).settlementFeeAmount(agentDepositWithdrawalPO.getSettlementFeeAmount())
                    .amount(agentDepositWithdrawalPO.getApplyAmount()).depositWithdrawWayId(agentDepositWithdrawalPO.getDepositWithdrawWayId())
                    .largeAmount(largeAmount).wayFeeAmount(agentDepositWithdrawalPO.getWayFeeAmount()).build();
            withdrawMq(agentCoinAddVO.getAgentInfo(), vo);
            if(ChannelTypeEnums.OFFLINE.getType().equals(agentDepositWithdrawalPO.getPayoutType())){
                //线下提款成功解冻保证金
                securityBalance(false,agentDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.AGENT_MANUAL_WITHDRAW.getCode(),
                        SiteSecurityCoinTypeEnums.WITHDRAW_SUCCESS.getCode(),agentCoinAddVO.getCoinValue());
            }else{
                //三防提款成功扣除冻结保证金
                securityBalance(false,agentDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.AGENT_WITHDRAW.getCode(),
                        SiteSecurityCoinTypeEnums.WITHDRAW_SUCCESS.getCode(),agentCoinAddVO.getCoinValue());
            }

            agentRechargeWithdrawSocketService.sendAgentDepositWithdrawSocket(SystemMessageEnum.AGENT_WITHDRAWAL_SUCCESS, agentDepositWithdrawalPO.getSiteCode(),
                    agentDepositWithdrawalPO.getAgentId(), agentDepositWithdrawalPO.getArriveAmount()
                    , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), CommonConstant.PLAT_CURRENCY_CODE);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("会员提款订单成功，发送通知失败{}",e.getMessage());
        }

    }

    /**
     * 提款成功(人工确认出款)
     *
     * @param agentDepositWithdrawalPO
     * @param agentCoinAddVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualWithdrawSuccess(AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentDepositWithdrawalAuditPO agentDepositWithdrawalAuditPO, AgentCoinAddVO agentCoinAddVO) {
        this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        if (null != agentDepositWithdrawalAuditPO) {
            this.agentDepositWithdrawalAuditRepository.insert(agentDepositWithdrawalAuditPO);
        }
        if (!agentCommonCoinService.agentCommonCommissionCoinAdd((agentCoinAddVO))) {
            log.error("该订单:{}, 提款成功添加支出账变失败", agentDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public void withdrawFail(AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentDepositWithdrawalAuditPO agentDepositWithdrawalAuditPO, AgentCoinAddVO agentCoinAddVO) {
        this.agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        if (null != agentDepositWithdrawalAuditPO) {
            this.agentDepositWithdrawalAuditRepository.insert(agentDepositWithdrawalAuditPO);
        }

        if (!agentCommonCoinService.agentCommonCommissionCoinAdd((agentCoinAddVO))) {
            log.error("该订单:{}, 提款失败添加解冻账变失败", agentDepositWithdrawalPO.getOrderNo());
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }

        try {

            agentRechargeWithdrawSocketService.sendAgentDepositWithdrawSocket(SystemMessageEnum.AGENT_WITHDRAWAL_FAILED, agentDepositWithdrawalPO.getSiteCode(),
                    agentDepositWithdrawalPO.getAgentId(), agentDepositWithdrawalPO.getArriveAmount()
                    , WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(), CommonConstant.PLAT_CURRENCY_CODE);
            //提款失败增加保证金
            securityBalance(false,agentDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.AGENT_WITHDRAW.getCode(),
                    SiteSecurityCoinTypeEnums.WITHDRAW_FAIL.getCode(),agentCoinAddVO.getCoinValue());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送提款失败消息 失败{}",e.getMessage());
        }

    }

    /**
     * 充值成功修改订单，插入账变等信息
     *
     * @param agentCoinAddVO
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    public void depositSuccess(AgentDepositWithdrawalPO agentDepositWithdrawalPO,
                               AgentCoinAddVO agentCoinAddVO,boolean isNew){
        int num = 0;
        if(isNew){
            num = agentDepositWithdrawalRepository.insert(agentDepositWithdrawalPO);
        }else {
            //更新订单信息
            num = agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
        }

        if(num > 0){
            if(!agentCommonCoinService.agentCommonQuotaCoinAdd((agentCoinAddVO))){
                throw new BaowangDefaultException(ResultCode.DEPOSIT_FAIL);
            }

            try{

                AgentDepositWtihdrawMqSendVO vo = AgentDepositWtihdrawMqSendVO.builder().agentInfo(agentCoinAddVO.getAgentInfo()).orderNo(agentDepositWithdrawalPO.getOrderNo())
                        .dateTime(agentDepositWithdrawalPO.getUpdatedTime()).amount(agentDepositWithdrawalPO.getArriveAmount())
                        .feeAmount(agentDepositWithdrawalPO.getFeeAmount()).settlementFeeAmount(agentDepositWithdrawalPO.getSettlementFeeAmount())
                        .wayFeeAmount(agentDepositWithdrawalPO.getWayFeeAmount()).depositWithdrawWayId(agentDepositWithdrawalPO.getDepositWithdrawWayId()).build();
                rechargeMq(vo);
                if(!ChannelTypeEnum.SITE_CUSTOM.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawChannelType())) {
                    //充值增加保证金
                    securityBalance(false, agentDepositWithdrawalPO, SiteSecuritySourceCoinTypeEnums.AGENT_DEPOSIT.getCode(),
                            SiteSecurityCoinTypeEnums.AGENT_DEPOSIT.getCode(), agentCoinAddVO.getCoinValue());
                }
                List<String> userIds = new ArrayList<>();
                userIds.add(agentDepositWithdrawalPO.getAgentId());
                RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
                rechargeSuccessVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
                rechargeSuccessVO.setCustomerStatus(agentDepositWithdrawalPO.getCustomerStatus());
                rechargeSuccessVO.setUpdatedTime(agentDepositWithdrawalPO.getUpdatedTime());
                //发送实时状态
                sendWebSocketMessage(agentDepositWithdrawalPO.getSiteCode(),userIds,rechargeSuccessVO);
                //存款完成通知
                agentRechargeWithdrawSocketService.sendAgentDepositWithdrawSocket(SystemMessageEnum.AGENT_DEPOSIT_SUCCESS,agentDepositWithdrawalPO.getSiteCode(),
                        agentDepositWithdrawalPO.getAgentId(),agentDepositWithdrawalPO.getArriveAmount()
                        ,WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(),CommonConstant.PLAT_CURRENCY_CODE);



            }catch (Exception e){
                e.printStackTrace();
                log.error("充值发送消息通知失败{}",e.getMessage());
            }
        }
    }

    private void  securityBalance(boolean isWithdrawApply,AgentDepositWithdrawalPO agentDepositWithdrawalPO,String securitySourceCoinType,String securityCoinType,BigDecimal coinValue){

        log.info("调用保证金开始,账变金额{}",coinValue);
        SitSecurityBalanceMqVO sitSecurityBalanceMqVO = new SitSecurityBalanceMqVO();
        sitSecurityBalanceMqVO.setSourceOrderNo(agentDepositWithdrawalPO.getOrderNo());
        sitSecurityBalanceMqVO.setUserId(agentDepositWithdrawalPO.getAgentId());
        sitSecurityBalanceMqVO.setUserName(agentDepositWithdrawalPO.getAgentAccount());
        sitSecurityBalanceMqVO.setSiteCode(agentDepositWithdrawalPO.getSiteCode());
        sitSecurityBalanceMqVO.setSourceCoinType(securitySourceCoinType);
        sitSecurityBalanceMqVO.setCoinType(securityCoinType);
        BigDecimal wtcUsdExchangeRate = null == agentDepositWithdrawalPO.getWtcUsdExchangeRate()?BigDecimal.ZERO:agentDepositWithdrawalPO.getWtcUsdExchangeRate();
        log.info("调用保证金,USD汇率{}",wtcUsdExchangeRate);
        BigDecimal usdAmount = AmountUtils.multiply(coinValue,wtcUsdExchangeRate);
        sitSecurityBalanceMqVO.setAdjustAmount(usdAmount);
        log.info("调用保证金,USD金额{}",usdAmount);
        sitSecurityBalanceMqVO.setUpdateUser(agentDepositWithdrawalPO.getUpdater());//使用最后审核人员
        if(isWithdrawApply){
            SiteSecurityBalanceChangeRecordReqVO balanceChangeRecordReqVO = ConvertUtil.entityToModel(sitSecurityBalanceMqVO,SiteSecurityBalanceChangeRecordReqVO.class);
            ResponseVO<Void> responseVO = siteSecurityBalanceApi.afterDepositOrWithdraw(balanceChangeRecordReqVO);
            if(!responseVO.isOk()){
                throw new BaowangDefaultException(ResultCode.CONTACT_CUSTOMER_SERVICE);
            }
        }else{
            KafkaUtil.send(TopicsConstants.SITE_SECURITY_BALANCE, sitSecurityBalanceMqVO);
        }
        log.info("调用保证金结束USD金额{}",usdAmount);
    }
    public void rechargeMq(AgentDepositWtihdrawMqSendVO vo) {
        AgentInfoVO agentInfoVO = vo.getAgentInfo();
        //发送存款累计
        AgentRechargeWithdrawMqVO agentRechargeWithdrawMqVO = new AgentRechargeWithdrawMqVO();
        agentRechargeWithdrawMqVO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString());
        agentRechargeWithdrawMqVO.setAgentId(agentInfoVO.getAgentId());
        agentRechargeWithdrawMqVO.setAgentAccount(agentInfoVO.getAgentAccount());
        agentRechargeWithdrawMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentRechargeWithdrawMqVO.setDepositWithdrawWayId(vo.getDepositWithdrawWayId());
        agentRechargeWithdrawMqVO.setSiteCode(agentInfoVO.getSiteCode());
        agentRechargeWithdrawMqVO.setFeeAmount(null == vo.getFeeAmount() ? BigDecimal.ZERO : vo.getFeeAmount());
        agentRechargeWithdrawMqVO.setWayFeeAmount(null == vo.getWayFeeAmount() ? BigDecimal.ZERO : vo.getWayFeeAmount());
        agentRechargeWithdrawMqVO.setSettlementFeeAmount(null == vo.getSettlementFeeAmount() ? BigDecimal.ZERO : vo.getSettlementFeeAmount());
        Long currentTime = System.currentTimeMillis();
//        agentRechargeWithdrawMqVO.setDayMillis(DateUtils.getTodayStartTime());
        agentRechargeWithdrawMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(currentTime));
        agentRechargeWithdrawMqVO.setAmount(vo.getAmount());
        log.info("开始代理发送存款累计:{}", agentRechargeWithdrawMqVO);
        KafkaUtil.send(TopicsConstants.AGENT_RECHARGE_WITHDRAW, agentRechargeWithdrawMqVO);


    }



    public void sendWebSocketMessage(String siteCode, List<String> userIds, RechargeSuccessVO rechargeSuccessVO) {
        // ws信息推送
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(siteCode);
        messageMqVO.setUidList(userIds);
        messageMqVO.setClientTypeEnum(ClientTypeEnum.AGENT);
        messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.AGENT_RECHARGE_SUCCESS_FAIL.getTopic(), ResponseVO.success(rechargeSuccessVO)));
        log.info("代理充值发送实时状态消息开始,内容{}", JSON.toJSONString(messageMqVO));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        log.info("代理充值发送实时状态消息结束,内容{}", JSON.toJSONString(messageMqVO));

    }

    @Transactional(rollbackFor = Exception.class)
    public String depositApplySuccess(AgentRechargeReqVO agentRechargeReqVO, AgentDepositWithdrawalPO agentDepositWithdrawalPO, SiteSystemRechargeChannelRespVO systemRechargeChannelPO) {

        agentDepositWithdrawalRepository.insert(agentDepositWithdrawalPO);
        String paymentUrl = "";
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawChannelType())){
            return  paymentUrl;
        }
        BigDecimal amount = agentRechargeReqVO.getAmount();
        String currencyCode = agentDepositWithdrawalPO.getCurrencyCode();
        if (CurrencyEnum.KVND.getCode().equals(agentDepositWithdrawalPO.getCurrencyCode())) {
            amount = amount.multiply(new BigDecimal(1000));
            currencyCode = CurrencyEnum.VND.getCode();
        }

        SystemRechargeChannelVO rechargeChannelVO = ConvertUtil.entityToModel(systemRechargeChannelPO, SystemRechargeChannelVO.class);
        PaymentVO paymentVO = PaymentVO.builder()
                .amount(String.valueOf(amount))
                .currency(currencyCode)
                .channelId(agentDepositWithdrawalPO.getDepositWithdrawChannelId())
                .accountType(CommonConstant.business_one)
                .userId(agentRechargeReqVO.getAgentId())
                .orderId(agentDepositWithdrawalPO.getOrderNo())
                .depositName(agentRechargeReqVO.getDepositName())
                .applyIp(agentRechargeReqVO.getApplyIp())
                .rechargeChannelVO(rechargeChannelVO)
                .build();
        log.info("代理三方支付发起参数{}" + JSON.toJSONString(paymentVO));
        ResponseVO<PaymentResponseVO> paymentResponseVOResponseVO = payRechargeWithdrawApi.payment(paymentVO);
        PaymentResponseVO paymentResponseVO = paymentResponseVOResponseVO.getData();
        log.info("代理三方支付发起结果{}" + JSON.toJSONString(paymentResponseVOResponseVO));
        if (paymentResponseVOResponseVO.isOk() && null != paymentResponseVO
                && CommonConstant.business_zero.equals(paymentResponseVO.getCode())) {
            agentDepositWithdrawalPO.setPayThirdUrl(paymentResponseVO.getPaymentUrl());
            agentDepositWithdrawalPO.setRemark(paymentResponseVO.getMessage());
            agentDepositWithdrawalPO.setPayTxId(paymentResponseVO.getThirdOrderId());
            agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
            paymentUrl = paymentResponseVO.getPaymentUrl();
        } else {
            throw new BaowangDefaultException(ResultCode.NO_CHANNEL_AVAILABLE);
//            paymentUrl = "https://www.google.com";
        }

        return paymentUrl;
    }

    public void withdrawMq(AgentInfoVO agentInfoPO, AgentDepositWtihdrawMqSendVO vo) {
        //发送存款累计
        AgentRechargeWithdrawMqVO agentRechargeWithdrawMqVO = new AgentRechargeWithdrawMqVO();
        agentRechargeWithdrawMqVO.setType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString());
        agentRechargeWithdrawMqVO.setAgentId(agentInfoPO.getAgentId());
        agentRechargeWithdrawMqVO.setAgentAccount(agentInfoPO.getAgentAccount());
        agentRechargeWithdrawMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentRechargeWithdrawMqVO.setDepositWithdrawWayId(vo.getDepositWithdrawWayId());
        agentRechargeWithdrawMqVO.setFeeAmount(null == vo.getFeeAmount() ? BigDecimal.ZERO : vo.getFeeAmount());
        agentRechargeWithdrawMqVO.setWayFeeAmount(null == vo.getWayFeeAmount() ? BigDecimal.ZERO : vo.getWayFeeAmount());
        agentRechargeWithdrawMqVO.setSettlementFeeAmount(null == vo.getSettlementFeeAmount() ? BigDecimal.ZERO : vo.getSettlementFeeAmount());
        agentRechargeWithdrawMqVO.setLargeAmount(null == vo.getLargeAmount() ? BigDecimal.ZERO : vo.getLargeAmount());
        agentRechargeWithdrawMqVO.setSiteCode(agentInfoPO.getSiteCode());
        Long currentTime = System.currentTimeMillis();
//        agentRechargeWithdrawMqVO.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(currentTime, siteInfo.getData().getTimezone()));
        agentRechargeWithdrawMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(currentTime));
        agentRechargeWithdrawMqVO.setAmount(vo.getAmount());
        log.info("开始发送代理提款累计:{}", agentRechargeWithdrawMqVO);
        KafkaUtil.send(TopicsConstants.AGENT_RECHARGE_WITHDRAW, agentRechargeWithdrawMqVO);

    }

    @Transactional(rollbackFor = Exception.class)
    public void handleDepositOfSubordinates(AgentDepositSubordinatesPO agentDepositSubordinatesPO, AgentCoinAddVO agentCoinAddVO,
                                            UserCoinAddVO userCoinAddVO, String depositSubordinatesType) {

        agentDepositSubordinatesRepository.insert(agentDepositSubordinatesPO);


        /*if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.COMMISSION_DEPOSIT_SUBORDINATES.getCode().equals(depositSubordinatesType)) {
            agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
        } else if (AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.QUOTA_DEPOSIT_SUBORDINATES.getCode().equals(depositSubordinatesType)) {
            agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);
        }
        CoinRecordResultVO responseVO = userCoinApi.addCoin(userCoinAddVO);*/
        Boolean result = agentCommonCoinService.agentDepositSubordinates(agentCoinAddVO,userCoinAddVO);
        if (!result) {
            throw new BaowangDefaultException("代理代存账变失败！");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void handleWithdrawApply(AgentDepositWithdrawalPO agentDepositWithdrawalPO, AgentCoinAddVO agentCoinAddVO) {
        agentDepositWithdrawalRepository.insert(agentDepositWithdrawalPO);

        //提款申请成功扣除保证金
        securityBalance(true,agentDepositWithdrawalPO,SiteSecuritySourceCoinTypeEnums.AGENT_WITHDRAW.getCode(),
                SiteSecurityCoinTypeEnums.AGENT_WITHDRAW.getCode(),agentCoinAddVO.getCoinValue());

        if (!agentCommonCoinService.agentCommonCommissionCoinAdd(agentCoinAddVO)) {
            throw new BaowangDefaultException("代理提款申请账变失败！");
        }

        try {
            String siteCode = agentDepositWithdrawalPO.getSiteCode();
            LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(AgentDepositWithdrawalPO::getSiteCode, siteCode);
            List<Integer> reviewOperationArr = new ArrayList<>();
            reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_REVIEW.getCode());
            reviewOperationArr.add(UserWithDrawReviewOperationEnum.PENDING_PAYMENT.getCode());
            lqw.in(AgentDepositWithdrawalPO::getReviewOperation, reviewOperationArr);
            lqw.eq(AgentDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            Long count = agentDepositWithdrawalRepository.selectCount(lqw);

            agentRechargeWithdrawSocketService.sendUserWithdrawApply(siteCode, count, "123");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送提款推送失败,原因:{}", e.getMessage());
        }
    }


}
