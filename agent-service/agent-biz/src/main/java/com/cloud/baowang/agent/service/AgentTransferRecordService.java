package com.cloud.baowang.agent.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTransferEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferInfoVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordResponseVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.po.AgentTransferRecordPO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.agent.repositories.AgentDepositSubordinatesRepository;
import com.cloud.baowang.agent.repositories.AgentTransferRecordRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AgentTransferRecordService {

    private final AgentDepositSubordinatesRepository depositSubordinatesRepository;

    private final AgentTransferRecordRepository transferRecordRepository;

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    public Page<AgentTransferRecordResponseVO> transferRecord(AgentTransferRecordRequestVO vo) {

        String agentId = CurrReqUtils.getOneId();
        Page<AgentTransferRecordResponseVO> transferRecordResponseVOPage = new Page<>();
        if(CommonConstant.business_one_str.equals(vo.getAccountType())){

            Page<AgentDepositSubordinatesPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            if(CommonConstant.business_one_str.equals(vo.getType())){
                return transferRecordResponseVOPage;
            }
            LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
            lqw.ge(null != vo.getStartTime(),AgentDepositSubordinatesPO::getDepositTime,vo.getStartTime());
            lqw.lt(null != vo.getEndTime(),AgentDepositSubordinatesPO::getDepositTime,vo.getEndTime());
            lqw.eq(StringUtils.isNotBlank(vo.getAccount()),AgentDepositSubordinatesPO::getUserAccount,vo.getAccount());
            lqw.eq(AgentDepositSubordinatesPO::getAgentId,agentId);
            lqw.orderByDesc(AgentDepositSubordinatesPO::getDepositTime);
            List<AgentTransferRecordResponseVO> list = new ArrayList<>();
            Page<AgentDepositSubordinatesPO>  depositSubordinatesPOPage = this.depositSubordinatesRepository.selectPage(page,lqw);

            BeanUtil.copyProperties(depositSubordinatesPOPage, transferRecordResponseVOPage);

            List<AgentDepositSubordinatesPO> depositSubordinatesPOS = depositSubordinatesPOPage.getRecords();
            if(CollectionUtil.isNotEmpty(depositSubordinatesPOS)){
                List<String> orderNos = depositSubordinatesPOS.stream().map(AgentDepositSubordinatesPO::getOrderNo).collect(Collectors.toList());
                List<String> agentIds = depositSubordinatesPOS.stream().map(AgentDepositSubordinatesPO::getAgentId).collect(Collectors.toList());
                Map<String,AgentCoinRecordPO> map = coinRecordList(orderNos,agentIds);
                for (AgentDepositSubordinatesPO po:depositSubordinatesPOS) {
                    AgentTransferRecordResponseVO agentTransferRecordResponseVO = new AgentTransferRecordResponseVO();
                    agentTransferRecordResponseVO.setAccount(po.getUserAccount());
                    agentTransferRecordResponseVO.setAccountType(CommonConstant.business_one_str);
                    agentTransferRecordResponseVO.setType(CommonConstant.business_two_str);
                    agentTransferRecordResponseVO.setDistributeTime(po.getDepositTime());
//                agentTransferRecordResponseVO.setDistributeAmount(po.getAmount());
                    agentTransferRecordResponseVO.setStatus(CommonConstant.business_one);
                    agentTransferRecordResponseVO.setTransferIn(CommonConstant.business_three.toString());
                    agentTransferRecordResponseVO.setTransferOut(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
                    agentTransferRecordResponseVO.setCrashFlow(po.getRunningWaterMultiple());
                    agentTransferRecordResponseVO.setRemark(po.getRemark());
                    AgentCoinRecordPO coinRecordPO = map.get(po.getOrderNo());
                    if(null != coinRecordPO){
                        agentTransferRecordResponseVO.setDistributeAmount(coinRecordPO.getCoinAmount());
                        agentTransferRecordResponseVO.setDistributeBeforeAmount(coinRecordPO.getCoinFrom());
                        agentTransferRecordResponseVO.setDistributeAfterAmount(coinRecordPO.getCoinTo());
                    }else {
                        agentTransferRecordResponseVO.setDistributeAmount(BigDecimal.ZERO);
                        agentTransferRecordResponseVO.setDistributeBeforeAmount(BigDecimal.ZERO);
                        agentTransferRecordResponseVO.setDistributeAfterAmount(BigDecimal.ZERO);
                    }
                    list.add(agentTransferRecordResponseVO);
                }
            }
            transferRecordResponseVOPage.setRecords(list);

        }else if(CommonConstant.business_two_str.equals(vo.getAccountType())){
            Page<AgentTransferRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            LambdaQueryWrapper<AgentTransferRecordPO> lqw = new LambdaQueryWrapper<>();
            lqw.ge(null != vo.getStartTime(),AgentTransferRecordPO::getTransferTime,vo.getStartTime());
            if(StringUtils.isNotBlank(vo.getType())){
                if(CommonConstant.business_one_str.equals(vo.getType())){
                    lqw.eq(AgentTransferRecordPO::getTransferAgentId, agentId);
                }else if(CommonConstant.business_two_str.equals(vo.getType())){
                    lqw.eq(AgentTransferRecordPO::getAgentId, agentId);
                }
            }else{
                lqw.and((wrapper)-> {
                    wrapper.eq(AgentTransferRecordPO::getAgentId, agentId)
                            .or().eq(AgentTransferRecordPO::getTransferAgentId, agentId);
                });
            }
            lqw.lt(null != vo.getEndTime(),AgentTransferRecordPO::getTransferTime,vo.getEndTime());
            lqw.eq(StringUtils.isNotBlank(vo.getAccount()),AgentTransferRecordPO::getTransferAccount,vo.getAccount());
            lqw.orderByDesc(AgentTransferRecordPO::getTransferTime);
            Page<AgentTransferRecordPO>  transferRecordPOPage = this.transferRecordRepository.selectPage(page,lqw);
            BeanUtil.copyProperties(transferRecordPOPage, transferRecordResponseVOPage);
            List<AgentTransferRecordResponseVO> list = new ArrayList<>();
            List<AgentTransferRecordPO> transferRecordPOS = transferRecordPOPage.getRecords();

            if(CollectionUtil.isNotEmpty(transferRecordPOS)){
                List<String> orderNos = transferRecordPOS.stream().map(AgentTransferRecordPO::getOrderNo).collect(Collectors.toList());
                List<String> agentIds = transferRecordPOS.stream().map(AgentTransferRecordPO::getTransferAgentId).collect(Collectors.toList());
                Map<String,AgentCoinRecordPO> map = coinRecordList(orderNos,agentIds);
                for (AgentTransferRecordPO po: transferRecordPOS) {
                    AgentTransferRecordResponseVO agentTransferRecordResponseVO = new AgentTransferRecordResponseVO();
                    agentTransferRecordResponseVO.setAccount(po.getTransferAccount());
                    agentTransferRecordResponseVO.setAccountType(CommonConstant.business_two_str);
                    if(agentId.equals(po.getTransferAgentId())){
                        agentTransferRecordResponseVO.setType(CommonConstant.business_one_str);
                    }else{
                        agentTransferRecordResponseVO.setType(CommonConstant.business_two_str);
                    }
                    if(CommonConstant.business_zero.equals(po.getTransferStatus())){
                        agentTransferRecordResponseVO.setStatus(CommonConstant.business_one);
                    }else{
                        agentTransferRecordResponseVO.setStatus(CommonConstant.business_two);
                    }
                    agentTransferRecordResponseVO.setDistributeTime(po.getTransferTime());

//                agentTransferRecordResponseVO.setDistributeAmount(po.getTransferAmount());
                    agentTransferRecordResponseVO.setTransferIn(po.getTransferType());
                    agentTransferRecordResponseVO.setTransferOut(po.getTransferType());
                    agentTransferRecordResponseVO.setCrashFlow(BigDecimal.ZERO);
                    agentTransferRecordResponseVO.setRemark(po.getRemark());
                    AgentCoinRecordPO coinRecordPO = map.get(po.getOrderNo());
                    if(null != coinRecordPO){
                        agentTransferRecordResponseVO.setDistributeAmount(coinRecordPO.getCoinAmount());
                        agentTransferRecordResponseVO.setDistributeBeforeAmount(coinRecordPO.getCoinFrom());
                        agentTransferRecordResponseVO.setDistributeAfterAmount(coinRecordPO.getCoinTo());
                    }else {
                        agentTransferRecordResponseVO.setDistributeAmount(BigDecimal.ZERO);
                        agentTransferRecordResponseVO.setDistributeBeforeAmount(BigDecimal.ZERO);
                        agentTransferRecordResponseVO.setDistributeAfterAmount(BigDecimal.ZERO);
                    }

                    list.add(agentTransferRecordResponseVO);
                }
            }


            transferRecordResponseVOPage.setRecords(list);
        }
        return transferRecordResponseVOPage;
    }

    private Map<String,AgentCoinRecordPO> coinRecordList (List<String> orderNos,List<String> agentIds){
        List<AgentCoinRecordPO>  coinRecordPOS = agentCoinRecordRepository.selectList(new LambdaQueryWrapper<AgentCoinRecordPO>()
                .in(AgentCoinRecordPO::getOrderNo,orderNos)
                .in(AgentCoinRecordPO::getAgentId,agentIds));
        Map<String,AgentCoinRecordPO> map = coinRecordPOS.stream().collect(Collectors.toMap(AgentCoinRecordPO::getOrderNo, coinRecordPO -> coinRecordPO));
        return map;
    }

    public AgentTransferInfoVO getAgentTransferInfo(String agentId) {
        LambdaQueryWrapper<AgentTransferRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentTransferRecordPO::getAgentId,agentId);
        lqw.eq(AgentTransferRecordPO::getTransferStatus, CommonConstant.business_zero);

        List<AgentTransferRecordPO> list = this.transferRecordRepository.selectList(lqw);
        AgentTransferInfoVO agentTransferInfoVO = new AgentTransferInfoVO();
        agentTransferInfoVO.setTransferAmount(BigDecimal.ZERO);
        agentTransferInfoVO.setTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setCommissionCoinTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setCommissionCoinTransferAmount(BigDecimal.ZERO);
        agentTransferInfoVO.setQuotaCoinTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setQuotaCoinTransferAmount(BigDecimal.ZERO);

        if(null != list && !list.isEmpty()) {
            BigDecimal totalAmount = list.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            agentTransferInfoVO.setTransferAmount(totalAmount);
            agentTransferInfoVO.setTransferNum(list.size());
            Map<String, List<AgentTransferRecordPO>> group = list.stream()
                    .collect(Collectors.groupingBy(AgentTransferRecordPO::getTransferType));
            //佣金钱包转账
            List<AgentTransferRecordPO> commissionTransferRecordList = group.get(AgentTransferEnum.COMMISSION.getCode());
            if (null != commissionTransferRecordList && !commissionTransferRecordList.isEmpty()) {
                BigDecimal amount = commissionTransferRecordList.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                agentTransferInfoVO.setCommissionCoinTransferAmount(amount);
                agentTransferInfoVO.setCommissionCoinTransferNum(commissionTransferRecordList.size());
            }
            //额度钱包转账
            List<AgentTransferRecordPO> quotaTransferRecordList = group.get(AgentTransferEnum.QUOTA.getCode());
            if (null != quotaTransferRecordList && !quotaTransferRecordList.isEmpty()) {
                BigDecimal amount = quotaTransferRecordList.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                agentTransferInfoVO.setQuotaCoinTransferAmount(amount);
                agentTransferInfoVO.setQuotaCoinTransferNum(quotaTransferRecordList.size());
            }
        }
        return agentTransferInfoVO;
    }
}
