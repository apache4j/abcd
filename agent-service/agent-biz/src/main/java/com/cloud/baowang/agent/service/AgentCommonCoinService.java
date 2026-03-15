package com.cloud.baowang.agent.service;


import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.account.api.api.AccountAgentApi;
import com.cloud.baowang.account.api.api.AccountPolymerizationApi;
import com.cloud.baowang.account.api.api.AccountUserApi;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountAgentCommissionDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaTransferVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToCommissionVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToQuotaVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AccountAgentCoinRequestMqVO;
import com.cloud.baowang.common.kafka.vo.AccountPlatfromCoinRequestMqVO;
import com.cloud.baowang.common.kafka.vo.AccountUserCoinRequestMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class AgentCommonCoinService {

    private final AccountAgentApi accountAgentApi;

    private final AccountPolymerizationApi accountPolymerizationApi;

    private final UserCoinApi userCoinApi;

    private final AgentCommissionCoinService agentCommissionCoinService;

    private final AgentQuotaCoinService agentQuotaCoinService;


    /**
     * 代理额度普通账变
     * @param agentCoinAddVO
     * @return
     */
    public Boolean agentCommonQuotaCoinAdd(AgentCoinAddVO agentCoinAddVO){
        log.info("代理额度{}账变开始",agentCoinAddVO.getAgentId());

        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("代理{}额度账变调用开关值{}",agentCoinAddVO.getAgentId(),accountOpenFlag);
        Boolean result = null;
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            result = agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);

        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            result = agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            AccountAgentCoinRequestMqVO accountAgentCoinRequestMqVO = ConvertUtil.entityToModel(accountAgentCoinAddReqVO,AccountAgentCoinRequestMqVO.class);
            KafkaUtil.send(TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC,accountAgentCoinRequestMqVO);

        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            result = accountAgentApi.agentQuotaCoin(accountAgentCoinAddReqVO);
        }
        return result;
    }

    /**
     * 代理佣金普通账变
     * @param agentCoinAddVO
     * @return
     */
    public Boolean agentCommonCommissionCoinAdd(AgentCoinAddVO agentCoinAddVO){
        log.info("代理佣金{}账变开始",agentCoinAddVO.getAgentId());
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("代理{}佣金账变调用开关值{}",agentCoinAddVO.getAgentAccount(),accountOpenFlag);
        Boolean result = null;
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            result = agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);

        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            result = agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            AccountAgentCoinRequestMqVO accountAgentCoinRequestMqVO = ConvertUtil.entityToModel(accountAgentCoinAddReqVO,AccountAgentCoinRequestMqVO.class);
            KafkaUtil.send(TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC,accountAgentCoinRequestMqVO);


        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            result = accountAgentApi.agentCommissionCoin(accountAgentCoinAddReqVO);
        }
        return result;
    }

    /**
     * 1 额度转账   佣金->额度
     * 2 转账下级   额度->下级额度
     * 3 转账下级   佣金->佣金
     * @param agentCoinAddVO
     * @param agentCoinAddVOParam
     * @return
     */
    public Boolean agentTransferCoin(AgentCoinAddVO agentCoinAddVO,AgentCoinAddVO agentCoinAddVOParam){
        log.info("代理代存{}账变开始",agentCoinAddVO.getAgentId());
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("代理代理代存{}账变调用开关值{}",agentCoinAddVO.getAgentAccount(),accountOpenFlag);
        Boolean result = null;
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            result = agentCoin(agentCoinAddVO);
            if(result){
                result = agentCoin(agentCoinAddVOParam);
            }
        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            result = agentCoin(agentCoinAddVO);
            if(result){
                result = agentCoin(agentCoinAddVOParam);
                AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
                setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);

                AccountAgentCoinRequestMqVO accountAgentCoinRequestMqVO = ConvertUtil.entityToModel(accountAgentCoinAddReqVO,AccountAgentCoinRequestMqVO.class);
                KafkaUtil.send(TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC,accountAgentCoinRequestMqVO);

                AccountAgentCoinAddReqVO accountAgentCoinAddReqVOParam = ConvertUtil.entityToModel(agentCoinAddVOParam,AccountAgentCoinAddReqVO.class);
                setAgentInfo(accountAgentCoinAddReqVOParam,agentCoinAddVO);
                AccountAgentCoinRequestMqVO accountAgentCoinRequestMqVO1 = ConvertUtil.entityToModel(accountAgentCoinAddReqVOParam,AccountAgentCoinRequestMqVO.class);
                KafkaUtil.send(TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC,accountAgentCoinRequestMqVO1);
            }
        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVOParam = ConvertUtil.entityToModel(agentCoinAddVOParam,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVOParam,agentCoinAddVO);
            if(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode().equals(agentCoinAddVO.getBusinessCoinType())){
                if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
                   // 转账下级   额度->下级额度
                    AccountAgentTransferToQuotaVO agentTransferToQuotaVO = new AccountAgentTransferToQuotaVO();
                    agentTransferToQuotaVO.setAgentQuotaCoinReqVO(accountAgentCoinAddReqVO);
                    agentTransferToQuotaVO.setAgentSubordinatesQuotaCoinReqVO(accountAgentCoinAddReqVOParam);
                    result = accountPolymerizationApi.agentTransferToQuota(agentTransferToQuotaVO);
                }else if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
                    //转账下级   佣金->佣金
                    AccountAgentTransferToCommissionVO agentTransferToCommissionVO = new AccountAgentTransferToCommissionVO();
                    agentTransferToCommissionVO.setAgentCommissionCoinReqVO(accountAgentCoinAddReqVO);
                    agentTransferToCommissionVO.setAgentSubordinatesCommissionCoinReqVO(accountAgentCoinAddReqVOParam);
                    result = accountPolymerizationApi.agentTransferToCommission(agentTransferToCommissionVO);
                }
            }else if(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA_TRANSFER.getCode().equals(agentCoinAddVO.getBusinessCoinType())) {
                // 额度转账   佣金->额度
                AccountAgentQuotaTransferVO accountAgentQuotaTransferVO = new AccountAgentQuotaTransferVO();
                accountAgentQuotaTransferVO.setAgentQuotaCoinReqVO(accountAgentCoinAddReqVOParam);
                accountAgentQuotaTransferVO.setAgentCommissionCoinReqVO(accountAgentCoinAddReqVO);
                result = accountPolymerizationApi.agentQuotaTransfer(accountAgentQuotaTransferVO);
            }
        }
        return result;
    }
    public Boolean agentDepositSubordinates(AgentCoinAddVO agentCoinAddVO,UserCoinAddVO userCoinAddVO){
        log.info("代理代存{}账变开始",agentCoinAddVO.getAgentId());
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("代理代理代存{}账变调用开关值{}",agentCoinAddVO.getAgentAccount(),accountOpenFlag);
        Boolean result = null;
        //公共调用
        if(ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)){
            //全部老账变
            result = agentCoin(agentCoinAddVO);
            if(result){
                result = userCoinApi.addCoin(userCoinAddVO).getResult();
            }
        }else if(CommonConstant.business_one.equals(accountOpenFlag)){
            //财务流量推送
            result = agentCoin(agentCoinAddVO);
            if(result){
                result = userCoinApi.addCoin(userCoinAddVO).getResult();
                AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
                setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
                AccountAgentCoinRequestMqVO accountAgentCoinRequestMqVO = ConvertUtil.entityToModel(accountAgentCoinAddReqVO,AccountAgentCoinRequestMqVO.class);
                KafkaUtil.send(TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC,accountAgentCoinRequestMqVO);

                AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
                setUserInfo(accountUserCoinAddReqVO, userCoinAddVO);
                AccountUserCoinRequestMqVO accountUserCoinRequestMqVO = ConvertUtil.entityToModel(accountUserCoinAddReqVO,AccountUserCoinRequestMqVO.class);
                KafkaUtil.send(TopicsConstants.ACCOUNT_USER_COIN_TOPIC,accountUserCoinRequestMqVO);
            }

        }else if(CommonConstant.business_two.equals(accountOpenFlag)){
            //全部走新的账务接口
            AccountAgentCoinAddReqVO accountAgentCoinAddReqVO = ConvertUtil.entityToModel(agentCoinAddVO,AccountAgentCoinAddReqVO.class);
            setAgentInfo(accountAgentCoinAddReqVO,agentCoinAddVO);
            AccountUserCoinAddReqVO accountUserCoinAddReqVO = ConvertUtil.entityToModel(userCoinAddVO,AccountUserCoinAddReqVO.class);
            setUserInfo(accountUserCoinAddReqVO, userCoinAddVO);
            if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
                AccountAgentQuotaDepositSubordinatesVO agentQuotaDepositSubordinatesVO = new AccountAgentQuotaDepositSubordinatesVO();
                agentQuotaDepositSubordinatesVO.setAgentQuotaCoinReqVO(accountAgentCoinAddReqVO);
                agentQuotaDepositSubordinatesVO.setUserCoinAddReqVO(accountUserCoinAddReqVO);
                result = accountPolymerizationApi.agentQuotaDepositSubordinates(agentQuotaDepositSubordinatesVO);
            }else if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
                AccountAgentCommissionDepositSubordinatesVO agentCommissionDepositSubordinatesVO = new AccountAgentCommissionDepositSubordinatesVO();
                agentCommissionDepositSubordinatesVO.setAgentCommissionCoinReqVO(accountAgentCoinAddReqVO);
                agentCommissionDepositSubordinatesVO.setUserCoinAddReqVO(accountUserCoinAddReqVO);
                result = accountPolymerizationApi.agentCommissionDepositSubordinates(agentCommissionDepositSubordinatesVO);
            }

        }
        return result;
    }

    private Boolean agentCoin(AgentCoinAddVO agentCoinAddVO){
        Boolean result = false;
        if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
            result = agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);
        }else if(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(agentCoinAddVO.getAgentWalletType())){
            result = agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
        }
        return result;
    }



    private void setAgentInfo(AccountAgentCoinAddReqVO reqVO,AgentCoinAddVO agentCoinAddVO){
        AgentInfoVO agentInfoVO = agentCoinAddVO.getAgentInfo();
        reqVO.setSiteCode(agentInfoVO.getSiteCode());
        reqVO.setAgentId(agentInfoVO.getAgentId());
        reqVO.setAgentAccount(agentInfoVO.getAgentAccount());
        reqVO.setAgentName(agentInfoVO.getName());
        reqVO.setParentId(agentInfoVO.getParentId());
        reqVO.setPath(agentInfoVO.getPath());
        reqVO.setCurrency(agentCoinAddVO.getCurrency());
        reqVO.setRiskLevelId(agentInfoVO.getRiskLevelId());
        reqVO.setLevel(agentInfoVO.getLevel());
        reqVO.setStatus(agentInfoVO.getStatus());
        reqVO.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reqVO.setInnerOrderNo(agentCoinAddVO.getOrderNo());
        if (Objects.isNull(agentCoinAddVO.getThirdOrderNo())){
            reqVO.setThirdOrderNo(agentCoinAddVO.getOrderNo());
        }else{
            reqVO.setThirdOrderNo(agentCoinAddVO.getThirdOrderNo());
        }
        if (Objects.nonNull(agentCoinAddVO.getToThridCode()) ){
            reqVO.setToThirdCode(agentCoinAddVO.getToThridCode());
        }else{
            reqVO.setToThirdCode(agentInfoVO.getSiteCode());
        }
        reqVO.setCoinTime(agentCoinAddVO.getCoinTime());
    }
    private void setUserInfo(AccountUserCoinAddReqVO reqVO,UserCoinAddVO userCoinAddVO){
        WalletUserInfoVO userInfoVO = userCoinAddVO.getUserInfoVO();
        reqVO.setSiteCode(userInfoVO.getSiteCode());
        reqVO.setUserId(userInfoVO.getUserId());
        reqVO.setUserAccount(userInfoVO.getUserAccount());
        reqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        reqVO.setCurrencyCode(userInfoVO.getMainCurrency());
        reqVO.setUserName(userInfoVO.getUserName());
        reqVO.setAccountStatus(userInfoVO.getAccountStatus());
        reqVO.setAccountType(userInfoVO.getAccountType());
        reqVO.setVipRank(userInfoVO.getVipRank());
        reqVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        reqVO.setInnerOrderNo(userCoinAddVO.getOrderNo());
        reqVO.setThirdOrderNo(userCoinAddVO.getOrderNo());
        reqVO.setToThirdCode(userInfoVO.getSiteCode());
        reqVO.setCoinTime(userCoinAddVO.getCoinTime());
    }


}
