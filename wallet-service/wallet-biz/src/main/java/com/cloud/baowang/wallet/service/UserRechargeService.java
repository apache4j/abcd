package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.DepositOrderFileVO;
import com.cloud.baowang.wallet.api.vo.recharge.GenHotWalletAddressReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.HandledDepositOrderVO;
import com.cloud.baowang.wallet.api.vo.recharge.OrderNoVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayFeeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserDepositOrderDetailVO;
import com.cloud.baowang.wallet.api.vo.recharge.UserRechargeReqVO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserRechargeService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final MinioFileService minioFileService;

    private final UserInfoApi userInfoApi;

    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;

    private final SystemRechargeWayService rechargeWayService;


    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final HotWalletAddressService hotWalletAddressService;

    private final SystemDictConfigApi systemDictConfigApi;

    private final SiteRechargeWayService siteRechargeWayService;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SiteVipOptionApi siteVipOptionApi;




    public Integer cancelDepositOrder(OrderNoVO orderNoVO) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo,orderNoVO.getOrderNo());
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);

        if(!DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode().equals(userDepositWithdrawalPO.getStatus())){
            throw new BaowangDefaultException(ResultCode.CURRENT_STATUS_NOT_CANCEL);
        }
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());

        return userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);




    }
    public HandledDepositOrderVO processingOrder(String userId) {

        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserId,userId);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        lqw.in(UserDepositWithdrawalPO::getStatus,statusList);
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);
        HandledDepositOrderVO handledDepositOrderVO = new HandledDepositOrderVO();
        if(null != userDepositWithdrawalPOS && !userDepositWithdrawalPOS.isEmpty()){
            handledDepositOrderVO.setHandleFlag(CommonConstant.business_one);
            UserDepositOrderDetailVO userDepositOrderDetailVO = new UserDepositOrderDetailVO();
            UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalPOS.get(0);
            handledDepositOrderVO.setUserDepositOrderDetailVO(userDepositOrderDetailVO);
            setDepositOrderDetail(userDepositWithdrawalPO,userDepositOrderDetailVO);
        }else{
            handledDepositOrderVO.setHandleFlag(CommonConstant.business_zero);
        }
        return handledDepositOrderVO;
    }

    private UserDepositOrderDetailVO setDepositOrderDetail(UserDepositWithdrawalPO userDepositWithdrawalPO,UserDepositOrderDetailVO userDepositOrderDetailVO){
        BeanUtils.copyProperties(userDepositWithdrawalPO,userDepositOrderDetailVO);

        userDepositOrderDetailVO.setAccountName(userDepositWithdrawalPO.getDepositWithdrawName());
        userDepositOrderDetailVO.setAccountAddress(userDepositWithdrawalPO.getDepositWithdrawAddress());
        userDepositOrderDetailVO.setExchangeRate(userDepositWithdrawalPO.getExchangeRate());
        userDepositOrderDetailVO.setTradeCurrencyAmount(userDepositWithdrawalPO.getTradeCurrencyAmount());
        userDepositOrderDetailVO.setArriveAmount(userDepositWithdrawalPO.getArriveAmount());
        userDepositOrderDetailVO.setCurrencyCode(userDepositWithdrawalPO.getCurrencyCode());
        userDepositOrderDetailVO.setCoinCode(userDepositWithdrawalPO.getCoinCode());
        userDepositOrderDetailVO.setFeeRate(BigDecimal.ZERO);
        userDepositOrderDetailVO.setFeeAmount(BigDecimal.ZERO);
        userDepositOrderDetailVO.setThirdPayUrl(userDepositWithdrawalPO.getPayThirdUrl());
        userDepositOrderDetailVO.setRecvUserName(userDepositWithdrawalPO.getRecvUserName());
        userDepositOrderDetailVO.setRecvBankBranch(userDepositWithdrawalPO.getRecvBankBranch());
        userDepositOrderDetailVO.setRecvBankCode(userDepositWithdrawalPO.getRecvBankCode());
        userDepositOrderDetailVO.setRecvBankName(userDepositWithdrawalPO.getRecvBankName());
        userDepositOrderDetailVO.setRecvBankAccount(userDepositWithdrawalPO.getRecvBankAccount());
        userDepositOrderDetailVO.setRecvQrCode(userDepositWithdrawalPO.getRecvQrCode());
        userDepositOrderDetailVO.setNetworkType(userDepositWithdrawalPO.getAccountBranch());
        if(StringUtils.isNotBlank(userDepositWithdrawalPO.getCashFlowFile())){
            String  domain = minioFileService.getMinioDomain();
            userDepositOrderDetailVO.setVoucherFlag(CommonConstant.business_one);
//            userDepositOrderDetailVO.setCashFlowFile(userDepositWithdrawalPO.getCashFlowFile());
            String[] cashFlowFileArr = userDepositWithdrawalPO.getCashFlowFile().split(",");
            List<String> cashFlowFileList = Arrays.asList(userDepositWithdrawalPO.getCashFlowFile().split(",")).stream()
                    .map(s -> domain+"/"+s )
                    .toList();
            userDepositOrderDetailVO.setCashFlowFileList(cashFlowFileList);
            userDepositOrderDetailVO.setCashFlowFile(String.join(",",cashFlowFileList));

        }else{
            userDepositOrderDetailVO.setVoucherFlag(CommonConstant.business_zero);
        }
        SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.CLIENT_ORDER_TIMEOUT.getCode(),"").getData();
        Long time = Long.parseLong(systemDictConfigRespVO.getConfigParam())*60*1000;
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = userDepositWithdrawalPO.getCreatedTime()+ time;
        Long  remindTime = (expiredTime-currentTime)/1000;
        userDepositOrderDetailVO.setRemindTime(remindTime < 0?0:remindTime);
        userDepositOrderDetailVO.setType(userDepositWithdrawalPO.getType());
        if(RechargeTypeEnum.CRYPTO_CURRENCY.equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
            userDepositOrderDetailVO.setApplyAmountUSD(convertApplyAmountToUSD(userDepositWithdrawalPO.getTradeCurrencyAmount(),userDepositWithdrawalPO.getCurrencyCode()));
        }
        return userDepositOrderDetailVO;
    }

    public BigDecimal convertApplyAmountToUSD(BigDecimal source,String currencyCode){
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode());
        BigDecimal rate = currencyRateMap.get(currencyCode);
        BigDecimal wtc = AmountUtils.divide(source, rate);
        BigDecimal usdRate = currencyRateMap.get(CurrencyEnum.USD.getCode());
        BigDecimal usdAmount = BigDecimal.ZERO;
        if(null != usdRate){
            usdAmount = AmountUtils.multiply(wtc, usdRate,2);
        }
        return usdAmount;
    }


    @Transactional(rollbackFor = Exception.class)
    public UserDepositOrderDetailVO depositOrderDetail(OrderNoVO orderNoVO) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo,orderNoVO.getOrderNo());
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);
        UserDepositOrderDetailVO userDepositOrderDetailVO = new UserDepositOrderDetailVO();
        setDepositOrderDetail(userDepositWithdrawalPO,userDepositOrderDetailVO);
        return userDepositOrderDetailVO;
    }

    public int  uploadVoucher(DepositOrderFileVO depositOrderFileVO) {
        LambdaUpdateWrapper<UserDepositWithdrawalPO> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(UserDepositWithdrawalPO::getOrderNo,depositOrderFileVO.getOrderNo());
//        updateWrapper.set(UserDepositWithdrawalPO::getFileKey,depositOrderFileVO.getFileKey());
        updateWrapper.set(UserDepositWithdrawalPO::getCashFlowFile,depositOrderFileVO.getCashFlowFile());
        updateWrapper.set(UserDepositWithdrawalPO::getCashFlowRemark,depositOrderFileVO.getCashFlowRemark());
        updateWrapper.set(UserDepositWithdrawalPO::getPayTxId,depositOrderFileVO.getOrderHash());
        return  userDepositWithdrawalRepository.update(null,updateWrapper);
    }


    @DistributedLock(name = RedisKeyTransUtil.USER_DEPOSIT, unique = "#userRechargeReqVo.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<OrderNoVO> userRecharge(UserRechargeReqVO userRechargeReqVo) {
        String userId = userRechargeReqVo.getUserId();
        BigDecimal depositAmount = userRechargeReqVo.getAmount();
        if(null == depositAmount){
            throw new BaowangDefaultException(ResultCode.AMOUNT_IS_NULL);
        }
        if(depositAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BaowangDefaultException(ResultCode.DEPOSIT_AMOUNT_NOT_LE_ZERO);
        }

        SystemRechargeWayPO systemRechargeWayPO = rechargeWayService.getById(userRechargeReqVo.getDepositWayId());
        if (null == systemRechargeWayPO ) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_NOT_EXIST);
        }
        if (systemRechargeWayPO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_DISABLE);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userRechargeReqVo.getUserId());
        SiteRechargeWayVO siteRechargeWayVO = siteRechargeWayService.queryRechargeWay(userInfoVO.getSiteCode(),userRechargeReqVo.getDepositWayId());
        if (null == siteRechargeWayVO ) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_NOT_EXIST);
        }
        if (siteRechargeWayVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_DISABLE);
        }

        if(RechargeTypeEnum.BANK_CARD.getCode().equals(systemRechargeWayPO.getRechargeTypeCode())
                && StringUtils.isBlank(userRechargeReqVo.getDepositName())){
            throw new BaowangDefaultException(ResultCode.DEPOSIT_USER_NAME_IS_NULL);
        }

        //二小时内存在5笔失败订单，一小时内不能再次充值
        checkContinueFiveFailOrder(userId);


        //检查会员类型
        if(!userInfoVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))){
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验会员账号状态
        if(userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }
        //校验币种
        if(!userInfoVO.getMainCurrency().equals(systemRechargeWayPO.getCurrencyCode())){
            throw new BaowangDefaultException(ResultCode.CURRENCY_NOT_MATCH);
        }
        //校验是否有三笔充值中订单
        checkThreeHandleOrder(userId,userInfoVO.getSiteCode());

        OrderNoVO orderNoVO = new OrderNoVO();
        pay(orderNoVO,userRechargeReqVo,userInfoVO,systemRechargeWayPO);
        return ResponseVO.success(orderNoVO);
    }

    private void checkContinueFiveFailOrder(String userId){
        //如果存在暂停充值KEY 返回充值限制
        String rechargeLimitKey = "recharge::limit::" + userId;
        if(RedisUtil.isKeyExist(rechargeLimitKey)){
            throw new BaowangDefaultException(ResultCode.RECHARGE_LIMIT);
        }
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        Long startTime = DateUtils.addHour(System.currentTimeMillis(),-2);
        lqw.ge(UserDepositWithdrawalPO::getCreatedTime,startTime);
        lqw.eq(UserDepositWithdrawalPO::getUserId,userId);
        lqw.orderByDesc(UserDepositWithdrawalPO::getUpdatedTime);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);
        if(CollectionUtil.isNotEmpty(userDepositWithdrawalPOS) && userDepositWithdrawalPOS.size() >= CommonConstant.business_five){
            boolean flag = true;
            for (int i = 0;i<CommonConstant.business_five;i++ ){
                UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalPOS.get(i);
                if(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(userDepositWithdrawalPO.getStatus())
                    || DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode().equals(userDepositWithdrawalPO.getCustomerStatus())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                RedisUtil.setValue(rechargeLimitKey, rechargeLimitKey, 3600L,TimeUnit.SECONDS);
                throw new BaowangDefaultException(ResultCode.RECHARGE_LIMIT);
            }
        }
    }
    private void checkThreeHandleOrder(String userId,String siteCode){
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserId,userId);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        lqw.in(UserDepositWithdrawalPO::getStatus,statusList);
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);

        SystemDictConfigRespVO  systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.MAX_ORDER_COUNT_IN_PROCESS.getCode(),siteCode).getData();
        int num = Integer.parseInt(systemDictConfigRespVO.getConfigParam());
        if(null != userDepositWithdrawalPOS && userDepositWithdrawalPOS.size() >= num){
            throw new BaowangDefaultException(systemDictConfigRespVO.getHintInfo());
        }
    }



    private void pay(OrderNoVO orderNoVO, UserRechargeReqVO userRechargeReqVo, UserInfoVO userInfoVO,SystemRechargeWayPO systemRechargeWayPO){
        //获取充值方式，通道等信息
        String depositWayId = userRechargeReqVo.getDepositWayId();
        BigDecimal amount = userRechargeReqVo.getAmount();


        List<SiteSystemRechargeChannelRespVO>  filterChannelList =  checkRechargeChannel(depositWayId,userInfoVO.getSiteCode(),userInfoVO.getVipRank(),userInfoVO.getVipGradeCode(),amount);
        SiteSystemRechargeChannelRespVO systemRechargeChannelPO = new SiteSystemRechargeChannelRespVO();
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayPO.getRechargeTypeCode())){
            List<SiteSystemRechargeChannelRespVO> channelRespVOS = filterChannelList.stream()
                    .filter(p -> ChannelTypeEnum.SITE_CUSTOM.getCode().equals(p.getChannelType()))
                    .collect(Collectors.toList());
            if(null == channelRespVOS || channelRespVOS.isEmpty()){
                throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
            }
            systemRechargeChannelPO = channelRespVOS.get(0);
        }else{
            //获取上一次充值的订单
            UserDepositWithdrawalPO lastUserDepositWithdrawalPO = userDepositWithdrawalRepository.selectLastRechargeOrder(userRechargeReqVo.getUserId());

            if(null == lastUserDepositWithdrawalPO){
                //获取顺位第一的通道
                systemRechargeChannelPO = filterChannelList.get(0);
            }else{
                //如果上一笔订单为成功 ， 则取上次成功的通道 ,失败 顺延往下取，下面没有了 取第一顺位
                String lastChannelId = lastUserDepositWithdrawalPO.getDepositWithdrawChannelId();
                SiteSystemRechargeChannelRespVO lastChannelPo = filterChannelList.stream()
                        .filter(p -> p.getId().equals(lastChannelId))
                        .findFirst()
                        .orElse(null);
                if(null == lastChannelPo){
                    systemRechargeChannelPO = filterChannelList.get(0);
                }else{
                    if(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(lastUserDepositWithdrawalPO.getStatus())){
                        systemRechargeChannelPO = lastChannelPo;
                    }else if(DepositWithdrawalOrderStatusEnum.FAIL.getCode().equals(lastUserDepositWithdrawalPO.getStatus())){
                        int num = filterChannelList.indexOf(lastChannelPo);
                        if(filterChannelList.size() > num+1 ){
                            systemRechargeChannelPO = filterChannelList.get(num+1);
                        }else{
                            systemRechargeChannelPO = filterChannelList.get(0);
                        }
                    }else{
                        systemRechargeChannelPO = filterChannelList.get(0);
                    }
                }
            }
        }

        UserDepositWithdrawalPO userDepositWithdrawalPO = new UserDepositWithdrawalPO();
        //获取站点充值方式费率配置
        SiteRechargeWayFeeVO siteRechargeWayFee = rechargeWayService.calculateSiteRechargeWayFeeRate(userInfoVO.getSiteCode(),depositWayId,amount,systemRechargeChannelPO.getChannelType());
        BigDecimal settlementFeeAmount = siteRechargeWayFee.getWayFeeAmount();
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(systemRechargeChannelPO.getChannelType())){
            userDepositWithdrawalPO.setWayFeeAmount(BigDecimal.ZERO);
        }
        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        exchangeRateRequestVO.setSiteCode(CommonConstant.business_zero_str);
        BigDecimal currencyExchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);

        //获取站点汇率
        RateCalculateRequestVO siteExchangeRateRequestVO = new RateCalculateRequestVO();
        siteExchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        siteExchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        siteExchangeRateRequestVO.setSiteCode(userInfoVO.getSiteCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(siteExchangeRateRequestVO);

        BigDecimal tradeCurrencyAmount = amount;
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayPO.getRechargeTypeCode())){
            tradeCurrencyAmount = amount.divide(exchangeRate,2,RoundingMode.DOWN);
            userDepositWithdrawalPO.setExchangeRate(exchangeRate);
            userDepositWithdrawalPO.setCoinCode(CurrencyEnum.USDT.getCode());
        }else{
            userDepositWithdrawalPO.setExchangeRate(BigDecimal.ONE);
            userDepositWithdrawalPO.setCoinCode(systemRechargeWayPO.getCurrencyCode());
        }
        String orderNo = "CK"+userInfoVO.getMainCurrency()+ DateUtils.dateToyyyyMMddHHmmss(new Date())+SnowFlakeUtils.getRandomZm();

        userDepositWithdrawalPO.setCurrencyUsdExchangeRate(currencyExchangeRate);
        userDepositWithdrawalPO.setSiteCode(userInfoVO.getSiteCode());
        userDepositWithdrawalPO.setUserId(userInfoVO.getUserId());

        userDepositWithdrawalPO.setUserAccount(userRechargeReqVo.getUserAccount());
        userDepositWithdrawalPO.setApplyIp(userRechargeReqVo.getApplyIp());
        userDepositWithdrawalPO.setDeviceType(userRechargeReqVo.getDeviceType());
        userDepositWithdrawalPO.setApplyAmount(userRechargeReqVo.getAmount());
        userDepositWithdrawalPO.setTradeCurrencyAmount(tradeCurrencyAmount);

        userDepositWithdrawalPO.setArriveAmount(amount);
        userDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
        userDepositWithdrawalPO.setWayFeeType(siteRechargeWayFee.getFeeType());
        userDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
        userDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
        userDepositWithdrawalPO.setWayFeeAmount(siteRechargeWayFee.getWayFeeAmount());
        userDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);
        userDepositWithdrawalPO.setCurrencyCode(systemRechargeWayPO.getCurrencyCode());
        userDepositWithdrawalPO.setDepositWithdrawTypeCode(systemRechargeWayPO.getRechargeTypeCode());
        userDepositWithdrawalPO.setDepositWithdrawTypeId(systemRechargeWayPO.getRechargeTypeId());
        userDepositWithdrawalPO.setDepositWithdrawWayId(userRechargeReqVo.getDepositWayId());
        userDepositWithdrawalPO.setDepositWithdrawWay(systemRechargeWayPO.getRechargeWayI18());
        userDepositWithdrawalPO.setAccountBranch(systemRechargeWayPO.getNetworkType());
        userDepositWithdrawalPO.setDepositWithdrawChannelId(systemRechargeChannelPO.getId());
        userDepositWithdrawalPO.setDepositWithdrawChannelCode(systemRechargeChannelPO.getChannelCode());
        userDepositWithdrawalPO.setDepositWithdrawChannelName(systemRechargeChannelPO.getChannelName());
        userDepositWithdrawalPO.setDepositWithdrawChannelType(systemRechargeChannelPO.getChannelType());
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(systemRechargeChannelPO.getChannelType())){
            userDepositWithdrawalPO.setRecvUserName(systemRechargeChannelPO.getRecvUserName());
            userDepositWithdrawalPO.setRecvBankBranch(systemRechargeChannelPO.getRecvBankBranch());
            userDepositWithdrawalPO.setRecvBankCode(systemRechargeChannelPO.getRecvBankCode());
            userDepositWithdrawalPO.setRecvBankName(systemRechargeChannelPO.getRecvBankName());
            userDepositWithdrawalPO.setRecvBankAccount(systemRechargeChannelPO.getRecvBankAccount());
            userDepositWithdrawalPO.setRecvQrCode(systemRechargeChannelPO.getRecvQrCode());
            userDepositWithdrawalPO.setDepositWithdrawAddress(systemRechargeChannelPO.getRecvBankCard());
        }
        userDepositWithdrawalPO.setOrderNo(orderNo);
        userDepositWithdrawalPO.setDepositWithdrawName(userRechargeReqVo.getDepositName());
        userDepositWithdrawalPO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());
        userDepositWithdrawalPO.setCreatedTime(System.currentTimeMillis());
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        userDepositWithdrawalPO.setAgentId(userInfoVO.getSuperAgentId());
        userDepositWithdrawalPO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        userDepositWithdrawalPO.setDeviceNo(userRechargeReqVo.getDeviceNo());
        userDepositWithdrawalPO.setUserLabelId(userInfoVO.getUserLabelId());

        String paymentUrl = userDepositWithdrawHandleService.depositApplySuccess(userRechargeReqVo,userDepositWithdrawalPO,systemRechargeChannelPO);
        orderNoVO.setThirdIsUrl(isURL(paymentUrl)?CommonConstant.business_one:CommonConstant.business_zero);
        orderNoVO.setThirdPayUrl(paymentUrl);
        orderNoVO.setChannelType(systemRechargeChannelPO.getChannelType());
        orderNoVO.setOrderNo(orderNo);

    }
    public boolean isURL(String str) {
        try {
            new URL(str);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }

    private  List<SiteSystemRechargeChannelRespVO> checkRechargeChannel(String depositWayId,String siteCode,Integer vipRank,Integer vipGradeCode,BigDecimal amount){
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup =  rechargeWayService.getUserChannelGroup(siteCode,vipRank,vipGradeCode);
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(depositWayId);
        if(null == channelPOS || channelPOS.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        BigDecimal rechargeMin = channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMin).min(BigDecimal::compareTo).get();
        BigDecimal rechargeMax = channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMax).max(BigDecimal::compareTo).get();

        if(amount.compareTo(rechargeMin) < 0){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        if(amount.compareTo(rechargeMax) > 0){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        List<SiteSystemRechargeChannelRespVO> filterChannelList = channelPOS.stream()
                .filter(p -> amount.compareTo(p.getRechargeMin())>= 0  && amount.compareTo(p.getRechargeMax()) <= 0)
                .collect(Collectors.toList());
        if(null == filterChannelList || filterChannelList.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        return filterChannelList;
    }

    public void urgeOrder(OrderNoVO vo) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo,vo.getOrderNo());
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);
        userDepositWithdrawalPO.setUrgeOrder(CommonConstant.business_one);
        userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
    }

    public ResponseVO<RechargeConfigVO> getRechargeConfig(RechargeConfigRequestVO vo) {
        UserInfoVO userInfoVO = userInfoApi.getByUserId(vo.getUserId());

        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup =  rechargeWayService.getUserChannelGroup(vo.getSiteCode(),userInfoVO.getVipRank(),userInfoVO.getVipGradeCode());
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(vo.getRechargeWayId());
        if(null == channelPOS || channelPOS.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        RechargeConfigVO rechargeConfigVO = rechargeWayService.getRechargeConfig(vo,userInfoVO);
        //充值不收客户手续费
        rechargeConfigVO.setFeeRate(BigDecimal.ZERO);
        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        exchangeRateRequestVO.setSiteCode(userInfoVO.getSiteCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        rechargeConfigVO.setExchangeRate(exchangeRate.toString());

        SystemRechargeWayPO systemRechargeWayPO = rechargeWayService.getById(vo.getRechargeWayId());
        rechargeConfigVO.setHaveThreeHandingOrder(CommonConstant.business_zero);
//        checkThreeHandleOrder(userInfoVO.getUserId(), vo.getSiteCode());
        rechargeConfigVO.setCurrencyCode(userInfoVO.getMainCurrency());
        rechargeConfigVO.setRechargeWayId(vo.getRechargeWayId());
        rechargeConfigVO.setRechargeWay(systemRechargeWayPO.getRechargeWayI18());
        rechargeConfigVO.setQuickAmount(systemRechargeWayPO.getQuickAmount());
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            SiteVipOptionVO siteVipOptionVO = siteVipOptionApi.getVipGradeInfoByCode(vo.getSiteCode(),userInfoVO.getVipGradeCode(),userInfoVO.getMainCurrency());
            if(null != siteVipOptionVO && StringUtils.isNotBlank(siteVipOptionVO.getDepositAmountLimit())){
                List<String> vipQuickAmounts = new ArrayList<>(Arrays.asList(siteVipOptionVO.getDepositAmountLimit().split(CommonConstant.COMMA)));
                vipQuickAmounts = vipQuickAmounts.stream().filter(o -> o != null && !o.isEmpty()).collect(Collectors.toList());

                List<String> wayQuickAmounts = new ArrayList<>(Arrays.asList(systemRechargeWayPO.getQuickAmount().split(CommonConstant.COMMA)));
                wayQuickAmounts = wayQuickAmounts.stream().filter(o -> o != null && !o.isEmpty()).collect(Collectors.toList());
                for (String quickAmount: vipQuickAmounts) {
                    if(!wayQuickAmounts.contains(quickAmount)){
                        wayQuickAmounts.add(quickAmount);
                    }
                }
                Collections.sort(wayQuickAmounts, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                    }
                });
                rechargeConfigVO.setQuickAmount(String.join(CommonConstant.COMMA,wayQuickAmounts));
            }
        }

        //获取加密货币热钱包地址
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayPO.getRechargeTypeCode())){
            List<SiteSystemRechargeChannelRespVO> channelRespVOS = channelPOS.stream()
                    .filter(p -> ChannelTypeEnum.SITE_CUSTOM.getCode().equals(p.getChannelType()))
                    .collect(Collectors.toList());
            if(!channelRespVOS.isEmpty()){
                rechargeConfigVO.setChannelType(ChannelTypeEnum.SITE_CUSTOM.getCode());
            }else{
                GenHotWalletAddressReqVO genHotWalletAddressReqVO = new GenHotWalletAddressReqVO();
                genHotWalletAddressReqVO.setSiteCode(vo.getSiteCode());
                genHotWalletAddressReqVO.setOneId(userInfoVO.getUserId());
                genHotWalletAddressReqVO.setOneAccount(userInfoVO.getUserAccount());
                genHotWalletAddressReqVO.setNetworkType(systemRechargeWayPO.getNetworkType());
                genHotWalletAddressReqVO.setOwnerUserType(OwnerUserTypeEnum.USER.getCode());
                genHotWalletAddressReqVO.setCurrencyCode(userInfoVO.getMainCurrency());
                rechargeConfigVO.setAddress(hotWalletAddressService.getHotWalletAddress(genHotWalletAddressReqVO).getData());
                rechargeConfigVO.setChannelType(ChannelTypeEnum.THIRD.getCode());
            }
        }
        String remindKey = "recharge::noRemind::" + vo.getUserId()+"::"+systemRechargeWayPO.getNetworkType();
        if (RedisUtil.isKeyExist(remindKey)) {
            rechargeConfigVO.setIsRemind(CommonConstant.business_zero);
        }else {
            rechargeConfigVO.setIsRemind(CommonConstant.business_one);
        }
        return ResponseVO.success(rechargeConfigVO);
    }

    private String  getUnionStr(String  wayQuickAmount ,String vipQuickAmount){
        if(StringUtils.isNotBlank(vipQuickAmount)){
            return wayQuickAmount;
        }
        Set<String> vipQuickAmounts = new HashSet<>(Arrays.asList(vipQuickAmount.split(CommonConstant.COMMA)));
        if(vipQuickAmounts.size() == 0){
            return wayQuickAmount;
        }
        Set<String> wayQuickAmounts = new HashSet<>(Arrays.asList(wayQuickAmount.split(CommonConstant.COMMA)));
        wayQuickAmounts.addAll(vipQuickAmounts);
        return String.join(CommonConstant.COMMA,wayQuickAmounts);
    }
}

