package com.cloud.baowang.agent.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentVirtualCurrencyPayCallbackVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentVirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.OrderQueryVO;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.SystemRechargeChannelVO;
import com.cloud.baowang.pay.api.vo.SystemWithdrawChannelVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.api.PayRechargeWithdrawApi;
import com.cloud.baowang.pay.api.api.VirtualCurrencyPayApi;
import com.cloud.baowang.pay.api.vo.OrderDateTimeQueryVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeChannelApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AgentRechargeWithdrawOrderStatusHandleService {

    private final AgentDepositWithdrawCallbackService agentDepositWithdrawCallbackService;

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;

    private final PayRechargeWithdrawApi payRechargeWithdrawApi;

    private final AgentInfoApi agentInfoApi;

    private final AgentDepositWithdrawHandleService agentDepositWithdrawHandleService;

    private final VirtualCurrencyPayApi virtualCurrencyPayApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    private final SystemWithdrawChannelApi systemWithdrawChannelApi;

    private final SystemRechargeChannelApi rechargeChannelApi;


    public ResponseVO rechargeOrderHandle() {

        //获取待处理订单
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        lambdaQueryWrapper.eq(AgentDepositWithdrawalPO::getStatus,DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        if (agentDepositWithdrawalPOS.isEmpty()) {
            log.info("没有代理充值订单");
        } else {
            final List<List<AgentDepositWithdrawalPO>> splitList = ConvertUtil.splitList(agentDepositWithdrawalPOS, 3);
            List<CompletableFuture<Integer>> futures = splitList.stream().
                    map(item -> CompletableFuture.supplyAsync(() -> processAgentDepositStatus(item))).collect(Collectors.toList());

            final List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            final Integer count = result.stream().mapToInt(Integer::intValue).sum();
            log.info("代理充值处理订单笔数 : {}", count);
        }

        return ResponseVO.success();

    }

    private Integer processAgentDepositStatus(List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS) {
        log.info("进入充值订单处理");
        int count = 0;
        SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.THIRD_PARTY_RECHARGE_ORDER_TIMEOUT.getCode(),"").getData();
        Long time = Long.parseLong(systemDictConfigRespVO.getConfigParam())*60*1000;
        for (AgentDepositWithdrawalPO agentDepositWithdrawalPO : agentDepositWithdrawalPOS) {
            final Long diffTime = System.currentTimeMillis() - agentDepositWithdrawalPO.getCreatedTime();
            log.info("充值订单状态任务开始，当前时间{},订单时间{},相差时间{}",System.currentTimeMillis(),agentDepositWithdrawalPO.getCreatedTime(),diffTime);
            if (diffTime > time) {
                log.info("代理{}充值三方{}充值过期处理 -开始 ", agentDepositWithdrawalPO.getAgentId(),agentDepositWithdrawalPO.getDepositWithdrawChannelName());
                this.changeOrderStatusToFail(agentDepositWithdrawalPO);
                log.info("代理{}充值三方{}充值过期处理 -结束", agentDepositWithdrawalPO.getAgentId(),agentDepositWithdrawalPO.getDepositWithdrawChannelName());
                //发送 失败mq

                List<String> userIds = new ArrayList<>();
                userIds.add(agentDepositWithdrawalPO.getAgentId());
                RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
                rechargeSuccessVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
                rechargeSuccessVO.setCustomerStatus(agentDepositWithdrawalPO.getCustomerStatus());
                rechargeSuccessVO.setUpdatedTime(agentDepositWithdrawalPO.getUpdatedTime());

                agentDepositWithdrawHandleService.sendWebSocketMessage(agentDepositWithdrawalPO.getSiteCode(),userIds,rechargeSuccessVO);
            }else{
                OrderQueryVO orderQueryVO = new OrderQueryVO();
                orderQueryVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
                orderQueryVO.setChannelId(agentDepositWithdrawalPO.getDepositWithdrawChannelId());
                IdVO idVO = IdVO.builder().id(agentDepositWithdrawalPO.getDepositWithdrawChannelId()).build();
                SystemRechargeChannelBaseVO rechargeChannelBaseVO = rechargeChannelApi.getChannelById(idVO);
                SystemRechargeChannelVO rechargeChannelVO = ConvertUtil.entityToModel(rechargeChannelBaseVO, SystemRechargeChannelVO.class);
                orderQueryVO.setRechargeChannelVO(rechargeChannelVO);
                orderQueryVO.setThirdOrderNo(agentDepositWithdrawalPO.getPayTxId());
                PayOrderResponseVO payOrderResponseVO = payRechargeWithdrawApi.queryPayOrder(orderQueryVO);
                log.info("充值三方支付查询 订单号:{},查询结果:{}", agentDepositWithdrawalPO.getOrderNo(), payOrderResponseVO);
                if (payOrderResponseVO != null && null != payOrderResponseVO.getPayOrderStatus()) {
                    log.info("充值三方支付查询 订单号:{},查询结果状态:{}", agentDepositWithdrawalPO.getOrderNo(), payOrderResponseVO.getPayOrderStatus());
                    //组装返回参数 与回调共用一个处理方法，
                    AgentCallbackDepositParamVO callbackDepositParamVO = new AgentCallbackDepositParamVO();
                    callbackDepositParamVO.setStatus(payOrderResponseVO.getPayOrderStatus());
                    if(StringUtils.isBlank(payOrderResponseVO.getAmount())){
                        callbackDepositParamVO.setAmount(new BigDecimal(CommonConstant.business_zero));
                    }else{
                        callbackDepositParamVO.setAmount(new BigDecimal(payOrderResponseVO.getAmount()));
                    }
                    callbackDepositParamVO.setOrderNo(payOrderResponseVO.getOrderNo());
                    callbackDepositParamVO.setPayId(payOrderResponseVO.getThirdOrderNo());
                    agentDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
                }
            }

            count++;
        }
        return count;
    }

    private void changeOrderStatusToFail(final AgentDepositWithdrawalPO agentDepositWithdrawalPO) {
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
    }

    public ResponseVO withdrawOrderHandle() {

        try {
            log.info("代理提款USDT查询待处理订单定时任务start....");
            LambdaQueryWrapper<AgentDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentDepositWithdrawalPO::getType,DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            queryWrapper.eq(AgentDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
            List<AgentDepositWithdrawalPO> list = agentDepositWithdrawalRepository.selectList(queryWrapper);
            List<List<AgentDepositWithdrawalPO>> result = ConvertUtil.splitList(list, 10);
            List<CompletableFuture<List<AgentDepositWithdrawalPO>>> futures = result.stream()
                    .map(item -> CompletableFuture.supplyAsync(() ->
                            doAgentWithdraw(item))).toList();
            result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            log.info("此次代理提款查询待处理订单数量 :{}", result.size());

        } catch (Exception e) {
            e.printStackTrace();
            log.warn("代理提款轮询处理待处理订单发生异常{}", e.getMessage());
        }
        return ResponseVO.success();
    }

    private List<AgentDepositWithdrawalPO> doAgentWithdraw(List<AgentDepositWithdrawalPO> item) {
        List<AgentDepositWithdrawalPO> list = Lists.newArrayList();
        for(AgentDepositWithdrawalPO agentDepositWithdrawalPO : item){
            try {
                withdrawOrderQuery(list,agentDepositWithdrawalPO);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("该提现订单{},提现方式{},轮询处理发生异常,{}",agentDepositWithdrawalPO.getOrderNo(),agentDepositWithdrawalPO.getDepositWithdrawWay(), e.getMessage());
            }
        }
        return list;
    }

    private List<AgentDepositWithdrawalPO> withdrawOrderQuery(final List<AgentDepositWithdrawalPO> list, final AgentDepositWithdrawalPO dto) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        orderQueryVO.setOrderNo(dto.getOrderNo());
        orderQueryVO.setChannelId(dto.getDepositWithdrawChannelId());
        orderQueryVO.setThirdOrderNo(dto.getPayTxId());
        IdVO idVO = IdVO.builder().id(dto.getDepositWithdrawChannelId()).build();
        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = systemWithdrawChannelApi.getChannelById(idVO);
        SystemWithdrawChannelVO withdrawChannelVO = ConvertUtil.entityToModel(withdrawChannelResponseVO, SystemWithdrawChannelVO.class);
        orderQueryVO.setWithdrawChannelVO(withdrawChannelVO);
        WithdrawalResponseVO response = payRechargeWithdrawApi.queryWithdrawalOrder(orderQueryVO);
        log.info("提款该订单:{}三方查询交易结果:{}", dto.getOrderNo(), response);
        if (response != null) {
            log.info("提款三方支付查询 订单号:{},查询结果:{}", dto.getOrderNo(), response);
            if(null != response.getWithdrawOrderStatus()){
                AgentCallbackWithdrawParamVO callbackWithdrawParamVO = new AgentCallbackWithdrawParamVO();
                callbackWithdrawParamVO.setOrderNo(response.getOrderNo());
                callbackWithdrawParamVO.setStatus(response.getWithdrawOrderStatus());
                callbackWithdrawParamVO.setPayId(response.getWithdrawOrderId());
                callbackWithdrawParamVO.setRemark(response.getMessage());
                if(StringUtils.isBlank(response.getAmount())){
                    callbackWithdrawParamVO.setAmount(new BigDecimal(CommonConstant.business_zero));
                }else{
                    callbackWithdrawParamVO.setAmount(new BigDecimal(response.getAmount()));
                }
                agentDepositWithdrawCallbackService.agentWithdrawCallback(callbackWithdrawParamVO);
                list.add(dto);


            }else{
                log.info("三方支付查询失败 订单号:{}", dto.getOrderNo());
            }

        }
        return list;
    }


    public ResponseVO virtualCurrencyRechargeOmissionsHandle(AgentVirtualCurrencyRechargeOmissionsReqVO reqVo) {
        if(ObjectUtil.isEmpty(reqVo.getStartTime()) ){
            reqVo.setStartTime(DateUtils.getAddMinute(new Date(System.currentTimeMillis()),-30).getTime());
        }
        if(ObjectUtil.isEmpty(reqVo.getEndTime())){
            reqVo.setEndTime(System.currentTimeMillis()) ;
        }
        OrderDateTimeQueryVO vo = new OrderDateTimeQueryVO();
        vo.setStartTime(reqVo.getStartTime());
        vo.setEndTime(reqVo.getEndTime());
        log.info("查询时间段内的虚拟币订单数 开始时间:{}，结束时间{}",reqVo.getStartTime(),reqVo.getEndTime());
        List<TradeNotifyVo> list = virtualCurrencyPayApi.queryByTime(vo).getData();
        log.info("查询时间段内的虚拟币订单数:{}",list.size());
        if(CollectionUtil.isNotEmpty(list)){
            for (TradeNotifyVo tradeNotifyVo:list) {
                if(OwnerUserTypeEnum.AGENT.getCode().equals(tradeNotifyVo.getOwnerUserType())){
                    LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
                    lqw.eq(AgentDepositWithdrawalPO::getAgentId, tradeNotifyVo.getOwnerUserId());
                    lqw.eq(AgentDepositWithdrawalPO::getType,DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                    lqw.eq(AgentDepositWithdrawalPO::getPayTxId,tradeNotifyVo.getTradeHash());
                    AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);

                    LambdaQueryWrapper<AgentCoinRecordPO> agentCoinRecordLqw = new LambdaQueryWrapper<>();
                    agentCoinRecordLqw.eq(AgentCoinRecordPO::getRemark,tradeNotifyVo.getTradeHash());
                    AgentCoinRecordPO agentCoinRecordPO = agentCoinRecordRepository.selectOne(agentCoinRecordLqw);

                    if(tradeNotifyVo.getTradeStatus() == CommonConstant.business_one && ObjectUtil.isEmpty(agentDepositWithdrawalPO) && ObjectUtil.isEmpty(agentCoinRecordPO)){
                        AgentVirtualCurrencyPayCallbackVO virtualCurrencyPayCallbackVO = ConvertUtil.entityToModel(tradeNotifyVo, AgentVirtualCurrencyPayCallbackVO.class);
                        agentDepositWithdrawCallbackService.virtualCurrencyDepositCallback(virtualCurrencyPayCallbackVO);
                    }
                }
            }
        }

        return ResponseVO.success();

    }
}
