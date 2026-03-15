package com.cloud.baowang.wallet.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.OrderQueryVO;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.SystemRechargeChannelVO;
import com.cloud.baowang.pay.api.vo.SystemWithdrawChannelVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.VirtualCurrencyPayCallbackVO;
import com.cloud.baowang.pay.api.api.PayRechargeWithdrawApi;
import com.cloud.baowang.pay.api.api.VirtualCurrencyPayApi;
import com.cloud.baowang.pay.api.vo.OrderDateTimeQueryVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.VirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.SystemRechargeChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
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
public class UserRechargeWithdrawOrderStatusHandleService {

    private final UserDepositWithdrawCallbackService userDepositWithdrawCallbackService;

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final PayRechargeWithdrawApi payRechargeWithdrawApi;

    private final UserInfoApi userInfoApi;

    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;

    private final VirtualCurrencyPayApi virtualCurrencyPayApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final UserCoinRecordRepository userCoinRecordRepository;

    private final SystemWithdrawChannelRepository withdrawChannelRepository;

    private final SystemRechargeChannelRepository rechargeChannelRepository;


    public ResponseVO rechargeOrderHandle() {

        //获取待处理订单
        LambdaQueryWrapper<UserDepositWithdrawalPO> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        lambdaQueryWrapper.eq(UserDepositWithdrawalPO::getStatus,DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lambdaQueryWrapper);
        if (userDepositWithdrawalPOS.isEmpty()) {
            log.info("没有会员充值订单");
        } else {
            final List<List<UserDepositWithdrawalPO>> splitList = ConvertUtil.splitList(userDepositWithdrawalPOS, 3);
            List<CompletableFuture<Integer>> futures = splitList.stream().
                    map(item -> CompletableFuture.supplyAsync(() -> processUserDepositStatus(item))).collect(Collectors.toList());

            final List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            final Integer count = result.stream().mapToInt(Integer::intValue).sum();
            log.info("会员充值处理订单笔数 : {}", count);
        }

        return ResponseVO.success();

    }

    private Integer processUserDepositStatus(List<UserDepositWithdrawalPO> userDepositWithdrawalPOS) {
        log.info("进入充值订单处理");
        int count = 0;
        SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.THIRD_PARTY_RECHARGE_ORDER_TIMEOUT.getCode(),"").getData();
        Long time = Long.parseLong(systemDictConfigRespVO.getConfigParam())*60*1000;
        for (UserDepositWithdrawalPO userDepositWithdrawalPO : userDepositWithdrawalPOS) {
            final Long diffTime = System.currentTimeMillis() - userDepositWithdrawalPO.getCreatedTime();
            log.info("充值订单状态任务开始，当前时间{},订单时间{},相差时间{}",System.currentTimeMillis(),userDepositWithdrawalPO.getCreatedTime(),diffTime);
            if (diffTime > time) {
                log.info("会员{}充值三方{}充值过期处理 -开始 ", userDepositWithdrawalPO.getUserId(),userDepositWithdrawalPO.getDepositWithdrawChannelName());
                this.changeOrderStatusToFail(userDepositWithdrawalPO);
                log.info("会员{}充值三方{}充值过期处理 -结束", userDepositWithdrawalPO.getUserId(),userDepositWithdrawalPO.getDepositWithdrawChannelName());
                //发送 失败mq
                List<String> userIds = new ArrayList<>();
                userIds.add(userDepositWithdrawalPO.getUserId());
                RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
                rechargeSuccessVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
                rechargeSuccessVO.setCustomerStatus(userDepositWithdrawalPO.getCustomerStatus());
                rechargeSuccessVO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
                userDepositWithdrawHandleService.sendWebSocketMessage(userDepositWithdrawalPO.getSiteCode(),userIds,rechargeSuccessVO);
            }else{
                if(!ChannelTypeEnum.SITE_CUSTOM.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawChannelType())){
                    OrderQueryVO orderQueryVO = new OrderQueryVO();
                    orderQueryVO.setChannelId(userDepositWithdrawalPO.getDepositWithdrawChannelId());
                    orderQueryVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());

                    orderQueryVO.setThirdOrderNo(userDepositWithdrawalPO.getPayTxId());
                    SystemRechargeChannelPO rechargeChannelPO = rechargeChannelRepository.selectById(userDepositWithdrawalPO.getDepositWithdrawChannelId());
                    SystemRechargeChannelVO rechargeChannelVO = ConvertUtil.entityToModel(rechargeChannelPO, SystemRechargeChannelVO.class);
                    orderQueryVO.setRechargeChannelVO(rechargeChannelVO);
                    PayOrderResponseVO payOrderResponseVO = payRechargeWithdrawApi.queryPayOrder(orderQueryVO);
                    log.info("充值三方支付查询 订单号:{},查询结果:{}", userDepositWithdrawalPO.getOrderNo(), payOrderResponseVO);
                    if (payOrderResponseVO != null && null !=payOrderResponseVO.getPayOrderStatus()) {
                        log.info("充值三方支付查询 订单号:{},查询结果状态:{}", userDepositWithdrawalPO.getOrderNo(), payOrderResponseVO.getPayOrderStatus());
                        //组装返回参数 与回调共用一个处理方法，
                        CallbackDepositParamVO callbackDepositParamVO = new CallbackDepositParamVO();
                        callbackDepositParamVO.setStatus(payOrderResponseVO.getPayOrderStatus());
                        if(StringUtils.isBlank(payOrderResponseVO.getAmount())){
                            callbackDepositParamVO.setAmount(new BigDecimal(CommonConstant.business_zero));
                        }else{
                            callbackDepositParamVO.setAmount(new BigDecimal(payOrderResponseVO.getAmount()));
                        }
                        callbackDepositParamVO.setOrderNo(payOrderResponseVO.getOrderNo());
                        callbackDepositParamVO.setPayId(payOrderResponseVO.getThirdOrderNo());
                        userDepositWithdrawCallbackService.depositCallback(callbackDepositParamVO);
                    }
                }
            }

            count++;
        }
        return count;
    }

    private void changeOrderStatusToFail(final UserDepositWithdrawalPO userDepositWithdrawalPO) {
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
    }

    public ResponseVO withdrawOrderHandle() {

        try {
            log.info("会员提款查询待处理订单定时任务start....");
            LambdaQueryWrapper<UserDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserDepositWithdrawalPO::getType,DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
            queryWrapper.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
            List<UserDepositWithdrawalPO> list = userDepositWithdrawalRepository.selectList(queryWrapper);
            List<List<UserDepositWithdrawalPO>> result = ConvertUtil.splitList(list, 10);
            List<CompletableFuture<List<UserDepositWithdrawalPO>>> futures = result.stream()
                    .map(item -> CompletableFuture.supplyAsync(() ->
                            doUserWithdraw(item))).toList();
            result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            log.info("此次会员提款查询待处理订单数量 :{}", result.size());

        } catch (Exception e) {
            e.printStackTrace();
            log.warn("会员提款轮询处理待处理订单发生异常{}", e.getMessage());
        }
        return ResponseVO.success();
    }

    private List<UserDepositWithdrawalPO> doUserWithdraw(List<UserDepositWithdrawalPO> item) {
        List<UserDepositWithdrawalPO> list = Lists.newArrayList();
        for(UserDepositWithdrawalPO userDepositWithdrawalPO : item){
            try {
                withdrawOrderQuery(list,userDepositWithdrawalPO);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("该提现订单{},提现方式{},轮询处理发生异常,{}",userDepositWithdrawalPO.getOrderNo(),userDepositWithdrawalPO.getDepositWithdrawWay(), e.getMessage());
            }
        }
        return list;
    }

    private List<UserDepositWithdrawalPO> withdrawOrderQuery(final List<UserDepositWithdrawalPO> list, final UserDepositWithdrawalPO dto) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        orderQueryVO.setOrderNo(dto.getOrderNo());
        orderQueryVO.setChannelId(dto.getDepositWithdrawChannelId());
        SystemWithdrawChannelPO systemWithdrawChannelPO = withdrawChannelRepository.selectById(dto.getDepositWithdrawChannelId());
        SystemWithdrawChannelVO withdrawChannelVO = ConvertUtil.entityToModel(systemWithdrawChannelPO, SystemWithdrawChannelVO.class);
        orderQueryVO.setWithdrawChannelVO(withdrawChannelVO);
        orderQueryVO.setThirdOrderNo(dto.getPayTxId());
        WithdrawalResponseVO response = payRechargeWithdrawApi.queryWithdrawalOrder(orderQueryVO);
        log.info("提款该订单:{}三方查询交易结果:{}", dto.getOrderNo(), response);
        if (response != null) {
            log.info("提款三方支付查询 订单号:{},查询结果:{}", dto.getOrderNo(), response);
            if(null != response.getWithdrawOrderStatus()){
                CallbackWithdrawParamVO callbackWithdrawParamVO = new CallbackWithdrawParamVO();
                callbackWithdrawParamVO.setOrderNo(response.getOrderNo());
                callbackWithdrawParamVO.setStatus(response.getWithdrawOrderStatus());
                callbackWithdrawParamVO.setPayId(response.getWithdrawOrderId());
                callbackWithdrawParamVO.setRemark(response.getMessage());
                if(StringUtils.isBlank(response.getAmount())){
                    callbackWithdrawParamVO.setAmount(new BigDecimal(CommonConstant.business_zero));
                }else{
                    callbackWithdrawParamVO.setAmount(new BigDecimal(response.getAmount()));
                }
                userDepositWithdrawCallbackService.userWithdrawCallback(callbackWithdrawParamVO);
                list.add(dto);

            }else{
                log.info("三方支付查询失败 订单号:{}", dto.getOrderNo());
            }

        }
        return list;
    }

    public ResponseVO virtualCurrencyRechargeOmissionsHandle(VirtualCurrencyRechargeOmissionsReqVO reqVo) {
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
                if(OwnerUserTypeEnum.USER.getCode().equals(tradeNotifyVo.getOwnerUserType())){
                    LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
                    lqw.eq(UserDepositWithdrawalPO::getUserId, tradeNotifyVo.getOwnerUserId());
                    lqw.eq(UserDepositWithdrawalPO::getType,DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                    lqw.eq(UserDepositWithdrawalPO::getPayTxId,tradeNotifyVo.getTradeHash());
                    UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);

                    LambdaQueryWrapper<UserCoinRecordPO> userCoinRecordLqw = new LambdaQueryWrapper<>();
                    userCoinRecordLqw.eq(UserCoinRecordPO::getRemark,tradeNotifyVo.getTradeHash());
                    UserCoinRecordPO userCoinRecordPO = userCoinRecordRepository.selectOne(userCoinRecordLqw);
                    if(tradeNotifyVo.getTradeStatus() == CommonConstant.business_one
                            && ObjectUtil.isEmpty(userDepositWithdrawalPO)
                            && ObjectUtil.isEmpty(userCoinRecordPO)){
                        VirtualCurrencyPayCallbackVO virtualCurrencyPayCallbackVO = ConvertUtil.entityToModel(tradeNotifyVo, VirtualCurrencyPayCallbackVO.class);
                        userDepositWithdrawCallbackService.virtualCurrencyDepositCallback(virtualCurrencyPayCallbackVO);
                    }
                }
            }
        }

        return ResponseVO.success();

    }
}
