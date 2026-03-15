package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.po.AgentCommissionCoinPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentQuotaCoinPO;
import com.cloud.baowang.agent.po.AgentQuotaTransferRecordPO;
import com.cloud.baowang.agent.po.AgentTransferRecordPO;
import com.cloud.baowang.agent.repositories.AgentCommissionCoinRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentQuotaCoinRepository;
import com.cloud.baowang.agent.repositories.AgentQuotaTransferRecordRepository;
import com.cloud.baowang.agent.repositories.AgentTransferRecordRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>
 * 代理额度钱包 服务类
 * </p>
 *
 * @author qiqi
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentQuotaTransferService  {

    private final AgentQuotaCoinRepository agentQuotaCoinRepository;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentQuotaCoinService agentQuotaCoinService;
    private final AgentCommissionCoinService agentCommissionCoinService;
    private final AgentCommissionCoinRepository agentCommissionCoinRepository;
    private final AgentQuotaTransferRecordRepository agentQuotaTransferRecordRepository;
    private final AgentTransferRecordRepository agentTransferRecordRepository;

    private final AgentCommonCoinService agentCommonCoinService;

    public Page<AgentQuotaTransferRecordRespVO> record(AgentQuotaTransferRecordReqVO reqVO) {
        Page<AgentQuotaTransferRecordPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<AgentQuotaTransferRecordPO> agentQuotaTransferRecordPOPage = agentQuotaTransferRecordRepository.selectPage(page, Wrappers.<AgentQuotaTransferRecordPO>lambdaQuery()
                .eq(AgentQuotaTransferRecordPO::getAgentId, reqVO.getAgentInfoId())
                .ge(reqVO.getStartTime() != null, AgentQuotaTransferRecordPO::getCreatedTime, reqVO.getStartTime())
                .le(reqVO.getEndTime() != null, AgentQuotaTransferRecordPO::getCreatedTime, reqVO.getEndTime())
                .orderByDesc(AgentQuotaTransferRecordPO::getUpdatedTime)
        );
        Page<AgentQuotaTransferRecordRespVO> ret = new Page<>();
        BeanUtil.copyProperties(agentQuotaTransferRecordPOPage,ret);
        List<AgentQuotaTransferRecordPO> records = agentQuotaTransferRecordPOPage.getRecords();
        List<AgentQuotaTransferRecordRespVO> agentQuotaTransferRecordRespVOS = BeanUtil.copyToList(records, AgentQuotaTransferRecordRespVO.class);
        for (AgentQuotaTransferRecordRespVO agentQuotaTransferRecordRespVO : agentQuotaTransferRecordRespVOS) {
            agentQuotaTransferRecordRespVO.setTransferFrom("佣金钱包");
            agentQuotaTransferRecordRespVO.setTransferTo("代存钱包");
            agentQuotaTransferRecordRespVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        }
        ret.setRecords(agentQuotaTransferRecordRespVOS);
        return ret;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean transfer(AgentQuotaTransferVO agentQuotaTransferVO) {

        //校验金额是否为整数
        if (null == agentQuotaTransferVO.getTransferAmount()) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_IS_NULL);
        }
        //校验金额是否为整数
        if (!isWhole(agentQuotaTransferVO.getTransferAmount()) || BigDecimal.ZERO.compareTo(agentQuotaTransferVO.getTransferAmount()) > 0) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_CAN_ONLY_BE_INTEGER);
        }

        String agentInfoId = agentQuotaTransferVO.getAgentInfoId();
        AgentInfoPO agentInfoPO = agentInfoRepository.selectByAgentId(agentInfoId);
        String payPassword = agentInfoPO.getPayPassword();
        if (StringUtils.isEmpty(payPassword)) {
            throw new BaowangDefaultException(ResultCode.AGENT_PAY_PASSWORD_NOT_SET_ERROR);
        }
        //校验支付密码
        String payPasswordVoEncrypt = AgentServerUtil.getEncryptPassword(agentQuotaTransferVO.getPayPassword(),
                agentInfoPO.getSalt());
        if(!payPasswordVoEncrypt.equals(agentInfoPO.getPayPassword())){
            throw new BaowangDefaultException(ResultCode.PAYPASSWORD_ERROR);
        }
        // 余额校验
        AgentCommissionCoinPO agentCommissionCoinPO = agentCommissionCoinService.getOne(Wrappers.<AgentCommissionCoinPO>lambdaQuery()
                .eq(AgentCommissionCoinPO::getAgentId, agentInfoPO.getAgentId()));
        if (agentCommissionCoinPO == null || agentCommissionCoinPO.getAvailableAmount().compareTo(agentQuotaTransferVO.getTransferAmount()) < 0){
            throw new BaowangDefaultException(ResultCode.AGENT_COMMISSION_COIN_INSUFFICIENT_BALANCE);
        }
        String agentAccount = agentInfoPO.getAgentAccount();
        String orderNo = "BG" + SnowFlakeUtils.getSnowId();
        // 扣佣金账户
        AgentCoinAddVO commissionAddVO  = decreaseCommission(agentQuotaTransferVO, orderNo, agentInfoPO);
        // 增加额度账户
        AgentCoinAddVO quotaAddVO = increaseQuota(agentQuotaTransferVO, agentAccount, orderNo,agentInfoPO);

        agentCommonCoinService.agentTransferCoin(commissionAddVO,quotaAddVO);

        // 记录日志
        quotaTransferRecord(agentInfoId, agentAccount, orderNo, agentQuotaTransferVO.getTransferAmount(),agentInfoPO.getSiteCode());
        return true;

    }
    public static boolean isWhole(BigDecimal bigDecimal) {
        return bigDecimal.setScale(0, RoundingMode.HALF_UP).compareTo(bigDecimal) == 0;
    }

    private void quotaTransferRecord(String agentInfoId, String agentAccount, String orderNo, BigDecimal amount,String siteCode) {
        AgentQuotaTransferRecordPO agentQuotaTransferRecordPO = new AgentQuotaTransferRecordPO();
        agentQuotaTransferRecordPO.setAgentId(agentInfoId);
        agentQuotaTransferRecordPO.setSiteCode(siteCode);
        agentQuotaTransferRecordPO.setAgentAccount(agentAccount);
        agentQuotaTransferRecordPO.setStatus(CommonConstant.business_one);
        agentQuotaTransferRecordPO.setOrderNo(orderNo);
        agentQuotaTransferRecordPO.setAmount(amount);
        long currentTimeMillis = System.currentTimeMillis();
        agentQuotaTransferRecordPO.setCreatedTime(currentTimeMillis);
        agentQuotaTransferRecordPO.setCreator(agentInfoId);
        agentQuotaTransferRecordPO.setUpdatedTime(currentTimeMillis);
        agentQuotaTransferRecordPO.setUpdater(agentInfoId);
        agentQuotaTransferRecordRepository.insert(agentQuotaTransferRecordPO);

        /*AgentTransferRecordPO po = new AgentTransferRecordPO();
        po.setAgentId(agentInfoId);
        po.setAgentAccount(agentAccount);
        po.setTransferAmount(amount);
        po.setTransferType(CommonConstant.business_one.toString());
        po.setRemark("额度转账");
        po.setTransferTime(currentTimeMillis);
        po.setTransferAgentId(agentInfoId);
        po.setTransferAccount(agentAccount);
        po.setOrderNo(orderNo);
        po.setTransferStatus(TransferStatusEnum.SUCCESS.getCode());
        agentTransferRecordRepository.insert(po);*/

    }


    private AgentCoinAddVO increaseQuota(AgentQuotaTransferVO agentQuotaTransferVO, String agentAccount, String orderNo, AgentInfoPO agentInfoPO) {
        AgentCoinAddVO quotaAddVO = new AgentCoinAddVO();
        quotaAddVO.setAgentAccount(agentAccount);
        quotaAddVO.setSiteCode(agentInfoPO.getSiteCode());
        quotaAddVO.setOrderNo(orderNo);
        quotaAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        quotaAddVO.setCoinValue(agentQuotaTransferVO.getTransferAmount());
        quotaAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        quotaAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        quotaAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA_TRANSFER.getCode());
        quotaAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.TO_QUOTA_TRANSFER.getCode());
        quotaAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.QUOTA_TRANSFER.getCode());
        AgentInfoVO agentInfoVO = BeanUtil.toBean(agentInfoPO, AgentInfoVO.class);
        quotaAddVO.setAgentInfo(agentInfoVO);
        quotaAddVO.setRemark("额度转账");
        /*if (!agentQuotaCoinService.addQuotaCoin(quotaAddVO)) {
            throw new BaowangDefaultException(ResultCode.AGENT_QUOTA_INCREASE_ERROR);
        }*/

        return quotaAddVO;
    }

    private AgentCoinAddVO decreaseCommission(AgentQuotaTransferVO agentQuotaTransferVO, String orderNo, AgentInfoPO agentInfoPO) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setRemark("额度转账");
        agentCoinAddVO.setSiteCode(agentInfoPO.getSiteCode());
        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        agentCoinAddVO.setAgentAccount(agentInfoPO.getAgentAccount());
        agentCoinAddVO.setCoinValue(agentQuotaTransferVO.getTransferAmount());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_TRANSFER.getCode());
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA_TRANSFER.getCode());
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.QUOTA_TRANSFER.getCode());
        agentCoinAddVO.setWithdrawFlag(0);
        AgentInfoVO agentInfoVO = BeanUtil.toBean(agentInfoPO, AgentInfoVO.class);
        agentCoinAddVO.setAgentInfo(agentInfoVO);
//        if (!agentCommissionCoinService.addCommissionCoin(agentCoinAddVO)) {
//            throw new BaowangDefaultException(ResultCode.AGENT_COMISSION_DECREASE_ERROR);
//        }
        return agentCoinAddVO;
    }

    public AgentQuotaTransferBalanceVO balance(String agentAccount,String siteCode) {
        AgentQuotaTransferBalanceVO vo = new AgentQuotaTransferBalanceVO();
        AgentCommissionCoinPO agentCommissionCoinPO = agentCommissionCoinRepository.selectOne(Wrappers.<AgentCommissionCoinPO>lambdaQuery()
                .eq(AgentCommissionCoinPO::getAgentAccount, agentAccount)
                .eq(AgentCommissionCoinPO::getSiteCode,siteCode)
                .last("limit 1")
        );
        vo.setCommissionBalance(agentCommissionCoinPO.getAvailableAmount());

        AgentQuotaCoinPO agentQuotaCoinPO = agentQuotaCoinRepository.selectOne(Wrappers.<AgentQuotaCoinPO>lambdaQuery()
                .eq(AgentQuotaCoinPO::getAgentAccount, agentAccount)
                .eq(AgentQuotaCoinPO::getSiteCode,siteCode)
                .last("limit 1")
        );
        vo.setQuotaBalance(agentQuotaCoinPO.getTotalAmount());

        return vo;

    }
}
