package com.cloud.baowang.account.api;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.api.AccountPolymerizationApi;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountAgentCommissionDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaTransferVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToCommissionVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToQuotaVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserWtcToMainCurrencyVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class AccountPolymerizationApiImpl implements AccountPolymerizationApi {

    private final AccountPlatformCoinApiImpl accountPlatformCoinApi;

    private final AccountUserApiImpl accountUserApi;

    private final AccountAgentApiImpl accountAgentApi;


    /**
     * WTC钱包转主货币钱包
     * @param accountUserWtcToMainCurrencyVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountCoinResultVO WtcToMainCurrency(AccountUserWtcToMainCurrencyVO accountUserWtcToMainCurrencyVO) {
        AccountCoinResultVO accountCoinResultVO = new AccountCoinResultVO();
        //平台币扣减
        AccountCoinResultVO platformCoinResultVO = accountPlatformCoinApi.platformCoinAdd(accountUserWtcToMainCurrencyVO.getUserPlatformCoinAddReqVO());
        if(!platformCoinResultVO.getResult()){
            log.info("会员i{},调用平台币帐变失败:{}",accountUserWtcToMainCurrencyVO.getUserPlatformCoinAddReqVO().getUserAccount(),JSONObject.toJSONString(platformCoinResultVO));
            accountCoinResultVO.setResult(false);
        }
        //会员增加
        AccountCoinResultVO userCoinResultVO = accountUserApi.userBalanceCoin(accountUserWtcToMainCurrencyVO.getUserCoinAddReqVO());
        if(!userCoinResultVO.getResult()){
            log.info("会员{},调用法币帐变失败:{}",accountUserWtcToMainCurrencyVO.getUserCoinAddReqVO().getUserAccount(),JSONObject.toJSONString(userCoinResultVO));
            throw new BaowangDefaultException(ResultCode.TRANSFER_ERROR);
        }

        return  userCoinResultVO;
    }

    /**
     * 代理额度转账   佣金钱包 ->额度钱包
     * @param accountAgentQuotaTransferVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean agentQuotaTransfer(AccountAgentQuotaTransferVO accountAgentQuotaTransferVO) {
        AccountAgentCoinAddReqVO agentCommissionCoinReqVO = accountAgentQuotaTransferVO.getAgentCommissionCoinReqVO();
        AccountAgentCoinAddReqVO agentQuotaCoinReqVO = accountAgentQuotaTransferVO.getAgentQuotaCoinReqVO();
        //佣金钱包扣减
        Boolean commissionResult =  accountAgentApi.agentCommissionCoin(agentCommissionCoinReqVO);

        if(!commissionResult){
            log.info("代理{},调用佣金帐变失败:{}",agentCommissionCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentCommissionCoinReqVO));
            return false;
        }
        Boolean quotaResult = accountAgentApi.agentQuotaCoin(agentQuotaCoinReqVO);
        if(!quotaResult){
            log.info("代理{},调用额度帐变失败:{}",agentQuotaCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentQuotaCoinReqVO));
            throw new BaowangDefaultException(ResultCode.AGENT_QUOTA_INCREASE_ERROR);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean agentTransferToQuota(AccountAgentTransferToQuotaVO agentTransferToQuotaVO) {
        AccountAgentCoinAddReqVO agentQuotaCoinReqVO = agentTransferToQuotaVO.getAgentQuotaCoinReqVO();
        AccountAgentCoinAddReqVO agentSubordinatesQuotaCoinReqVO = agentTransferToQuotaVO.getAgentSubordinatesQuotaCoinReqVO();
        //额度钱包扣减
        Boolean quotaResult =  accountAgentApi.agentQuotaCoin(agentQuotaCoinReqVO);

        if(!quotaResult){
            log.info("下级转账代理{},调用额度帐变失败:{}",agentQuotaCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentQuotaCoinReqVO));
            return false;
        }
        //下级额度钱包增加
        Boolean subordinatesQuotaResult = accountAgentApi.agentQuotaCoin(agentSubordinatesQuotaCoinReqVO);
        if(!subordinatesQuotaResult){
            log.info("下级转账下级代理{},调用额度帐变失败:{}",agentSubordinatesQuotaCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentSubordinatesQuotaCoinReqVO));
            throw new BaowangDefaultException(ResultCode.TRANSFER_ERROR);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean agentTransferToCommission(AccountAgentTransferToCommissionVO agentTransferToCommissionVO) {
        AccountAgentCoinAddReqVO agentCommissionCoinReqVO = agentTransferToCommissionVO.getAgentCommissionCoinReqVO();
        AccountAgentCoinAddReqVO agentSubordinatesCommissionCoinReqVO = agentTransferToCommissionVO.getAgentSubordinatesCommissionCoinReqVO();
        //佣金钱包扣减
        Boolean commissionResult =  accountAgentApi.agentCommissionCoin(agentCommissionCoinReqVO);

        if(!commissionResult){
            log.info("下级转账{},调用佣金帐变失败:{}",agentCommissionCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentCommissionCoinReqVO));
            return false;
        }
        //下级佣金钱包增加
        Boolean subordinatesCommissionResult = accountAgentApi.agentCommissionCoin(agentSubordinatesCommissionCoinReqVO);
        if(!subordinatesCommissionResult){
            log.info("下级转账下级代理{},调用佣金帐变失败:{}",agentSubordinatesCommissionCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentSubordinatesCommissionCoinReqVO));
            throw new BaowangDefaultException(ResultCode.TRANSFER_ERROR);
        }
        return true;
    }

    @Override
    public Boolean agentQuotaDepositSubordinates(AccountAgentQuotaDepositSubordinatesVO agentQuotaDepositSubordinatesVO) {
        AccountAgentCoinAddReqVO agentQuotaCoinReqVO = agentQuotaDepositSubordinatesVO.getAgentQuotaCoinReqVO();
        AccountUserCoinAddReqVO  userCoinAddReqVO = agentQuotaDepositSubordinatesVO.getUserCoinAddReqVO();
        //额度钱包扣减
        Boolean commissionResult =  accountAgentApi.agentQuotaCoin(agentQuotaCoinReqVO);

        if(!commissionResult){
            log.info("代理代存佣金代理{},调用额度帐变失败:{}",agentQuotaCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentQuotaCoinReqVO));
            return false;
        }
        //会员钱包增加
        AccountCoinResultVO accountCoinResultVO = accountUserApi.userBalanceCoin(userCoinAddReqVO);
        if(!accountCoinResultVO.getResult()){
            log.info("代理代存佣金下级会员{},调用会员帐变失败:{}",userCoinAddReqVO.getUserAccount(),JSONObject.toJSONString(userCoinAddReqVO));
            throw new BaowangDefaultException(ResultCode.TRANSFER_ERROR);
        }
        return true;
    }
    @Override
    public Boolean agentCommissionDepositSubordinates(AccountAgentCommissionDepositSubordinatesVO agentCommissionDepositSubordinatesVO) {

        AccountAgentCoinAddReqVO agentCommissionCoinReqVO = agentCommissionDepositSubordinatesVO.getAgentCommissionCoinReqVO();
        AccountUserCoinAddReqVO  userCoinAddReqVO = agentCommissionDepositSubordinatesVO.getUserCoinAddReqVO();
        //佣金钱包扣减
        Boolean commissionResult =  accountAgentApi.agentCommissionCoin(agentCommissionCoinReqVO);

        if(!commissionResult){
            log.info("代理代存佣金代理{},调用佣金帐变失败:{}",agentCommissionCoinReqVO.getAgentAccount(),JSONObject.toJSONString(agentCommissionCoinReqVO));
            return false;
        }
        //会员钱包增加
        AccountCoinResultVO accountCoinResultVO = accountUserApi.userBalanceCoin(userCoinAddReqVO);
        if(!accountCoinResultVO.getResult()){
            log.info("代理代存佣金下级会员{},调用会员帐变失败:{}",userCoinAddReqVO.getUserAccount(),JSONObject.toJSONString(userCoinAddReqVO));
            throw new BaowangDefaultException(ResultCode.TRANSFER_ERROR);
        }
        return true;
    }
}
