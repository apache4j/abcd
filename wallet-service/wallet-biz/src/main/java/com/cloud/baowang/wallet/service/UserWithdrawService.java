package com.cloud.baowang.wallet.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.system.api.api.site.SiteRiskCtrlBlackApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountIsBlackReqVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.UserWithDrawReviewOperationEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipFeeRateVO;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.bank.BankManageVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.*;
import com.cloud.baowang.wallet.repositories.*;
import com.cloud.baowang.wallet.service.bank.BankCardManagerService;
import com.cloud.baowang.common.core.utils.AddressUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserWithdrawService extends ServiceImpl<UserDepositWithdrawalRepository, UserDepositWithdrawalPO> {

    private final UserDepositWithdrawalRepository depositWithdrawalRepository;

    private final UserInfoApi userInfoApi;

    private final SystemWithdrawWayService systemWithdrawWayService;

    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;


    private final UserCoinService userCoinService;

    private final UserTypingAmountRepository userTypingAmountRepository;

    private final UserWithdrawConfigRepository userWithdrawConfigRepository;

    private final SiteWithdrawWayRepository siteWithdrawWayRepository;

    private final BankCardManagerService bankCardManagerService;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SiteWithdrawChannelRepository siteWithdrawChannelRepository;

    private final SystemWithdrawChannelRepository channelRepository;

    private final VipRankApi vipRankApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final SiteWithdrawWayService siteWithdrawWayService;

    private final UserWithdrawConfigDetailRepository userWithdrawConfigDetailRepository;

    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;

    private final SiteVipOptionApi siteVipOptionApi;

    private final UserReceiveAccountService userReceiveAccountService;

    private final SiteRiskCtrlBlackApi siteRiskCtrlBlackApi;


    @DistributedLock(name = RedisKeyTransUtil.USER_WITHDRAW_APPLY, unique = "#vo.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<Integer> userWithdrawApply(UserWithDrawApplyVO vo) {
        String userId = vo.getUserId();
        //校验是否有1笔处理中订单
        checkHandleOrder(userId);
        log.info("会员提现申请人:{},申请金额{}", vo.getUserAccount(), vo.getAmount());
        //校验金额是否为整数
        if (null == vo.getAmount()) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_IS_NULL);
        }
        //校验金额是否为整数
        if (!isWhole(vo.getAmount()) && BigDecimal.ZERO.compareTo(vo.getAmount()) < 0) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_AMOUNT_NEED_WHOLE);
        }
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserId(vo.getUserId());
        UserCoinWalletVO userCoinWalletVO = userCoinService.getUserCenterCoin(userCoinQueryVO);
        if (userCoinWalletVO.getCenterAmount().compareTo(vo.getAmount()) < 0) {
            throw new BaowangDefaultException(ResultCode.WALLET_INSUFFICIENT_BALANCE);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(vo.getUserId());
        if (!userInfoVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))) {
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验会员是否存取款限制
        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())) {
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }
        //获取会员标签集合
        List<String> userLabelList = getUserLabels(userInfoVO.getUserLabelId());
        //校验标签出款限制
        if (userLabelList.contains(UserLabelEnum.WITHDRAWAL_LIMIT.getLabelId())) {
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }


//        获取提款方式对象
        SystemWithdrawWayPO withdrawWayPO = systemWithdrawWayService.getById(vo.getWithdrawWayId());
        if (null == withdrawWayPO) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_NOT_EXIST);
        }

        if (withdrawWayPO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_DISABLE);
        }
        SiteWithdrawWayVO siteWithdrawWayVO = siteWithdrawWayService.queryWithdrawWay(userInfoVO.getSiteCode(),vo.getWithdrawWayId());
        if (null == siteWithdrawWayVO) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_NOT_EXIST);
        }
        if (siteWithdrawWayVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_WAY_DISABLE);
        }
        //校验币种
        if (!userInfoVO.getMainCurrency().equals(withdrawWayPO.getCurrencyCode())) {
            throw new BaowangDefaultException(ResultCode.CURRENCY_NOT_MATCH);
        }

        //校验提款方式是否是正常开启状态，是否有提款通道,单次限额
        WithdrawConfigVO withdrawConfigVO = checkIsExistWithdrawChannel(vo.getWithdrawWayId(), userId, vo.getAmount());
        //校验单日提款限制
        if(withdrawConfigVO.getDayRemindWithdrawCount() <= 0){
            throw new BaowangDefaultException(ResultCode.EXCEED_DAY_MAX_NUM);
        }
        if(withdrawConfigVO.getDayRemindMaxWithdrawAmount().compareTo(vo.getAmount()) <0){
            throw new BaowangDefaultException(ResultCode.EXCEED_DAY_MAX_AMOUNT);
        }
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())
            && !WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(withdrawWayPO.getWithdrawTypeCode())){
            if(StringUtils.isBlank(vo.getUserReceiveAccountId())){
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

        }else{
            //校验参数
            checkWithdrawParam(vo, withdrawWayPO, withdrawConfigVO.getBankList());
        }

        if (ObjectUtil.isNotEmpty(userInfoVO.getWithdrawPwd())) {
            //校验取款密码
            checkWithdrawPassword(vo.getWithdrawPassWord(), userInfoVO);
        } else {
            //校验手机验证码
            if (ObjectUtil.isEmpty(vo.getSmsCode())) {
                throw new BaowangDefaultException(ResultCode.SMS_CODE_IS_NULL);
            } else {
                String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, userInfoVO.getSiteCode(), userInfoVO.getUserAccount()));
                if (!vo.getSmsCode().equals(code)) {
                    throw new BaowangDefaultException(ResultCode.SMS_CODE_NOT_MATCH);
                }
                //校验通过后删除原来的验证码
                RedisUtil.deleteKey(String.format(RedisConstants.VERIFY_CODE_CACHE, userInfoVO.getSiteCode(), userInfoVO.getUserAccount()));
            }
        }


        //会员标签是否配置提款免流水
        if (!userLabelList.contains(UserLabelEnum.WITHDRAWAL_NO_REQUIREMENTS.getLabelId())) {
            //校验流水
            if (null != withdrawConfigVO.getRemainingFlow() && withdrawConfigVO.getRemainingFlow().compareTo(BigDecimal.ZERO) > 0) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_LIMIT);
            }
        }
        //根据汇率计算金额， 计算费率
        //获取VIP段位手续费
        SiteVipFeeRateVO rankVo = getVipFeeRate(userInfoVO.getSiteCode(), userInfoVO.getVipRank(),userInfoVO.getVipGradeCode(), userInfoVO.getMainCurrency(),vo.getWithdrawWayId());
        //获取VIP段位配置信息
        BigDecimal feeRate;
        Integer feeType = 0 ;

        if (null != rankVo) {
            feeRate = rankVo.getWithdrawFee() == null ? BigDecimal.ZERO : rankVo.getWithdrawFee();
            feeType = rankVo.getWithdrawFeeType();
        } else {
            feeRate = BigDecimal.ZERO;
        }
        BigDecimal feeAmount = BigDecimal.ZERO;
        if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)){
            feeAmount = vo.getAmount().multiply(feeRate.divide(new BigDecimal("100"))).setScale(0, RoundingMode.DOWN);
        }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)){
            feeAmount = feeRate;
        }
        //如果有免费次数，并且免费额度满足
        if (vo.getAmount().compareTo(withdrawConfigVO.getSingleDayRemindMaxWithdrawAmount()) <= 0 &&
            withdrawConfigVO.getSingleDayRemindWithdrawCount() > 0) {
            feeRate = BigDecimal.ZERO;
            feeAmount = BigDecimal.ZERO;
        }
        BigDecimal arrAmount, tradeCurrencyAmount;
        //段位配置，加密货币是否有提款手续费
        BigDecimal exchangeRate = withdrawConfigVO.getExchangeRate();
        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawWayPO.getWithdrawTypeCode())) {
            //申请金额汇率转换U 2位小数
            tradeCurrencyAmount = vo.getAmount().divide(exchangeRate, 2, RoundingMode.DOWN);
            //配置不收手续费
            if (YesOrNoEnum.NO.getCode().equals(rankVo.getEncryCoinFee().toString())) {
                feeRate = BigDecimal.ZERO;
                feeAmount = BigDecimal.ZERO;
                arrAmount = vo.getAmount().subtract(feeAmount);
            } else {
                if (!CurrencyEnum.USDT.getCode().equals(userInfoVO.getMainCurrency()) ) {
                    BigDecimal freeCurrencyAmount = BigDecimal.ZERO;
                    if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)){
                        //主货币手续费
                        freeCurrencyAmount = vo.getAmount().multiply(feeRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.DOWN);
                    } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)) {
                        freeCurrencyAmount = feeRate;
                    }

                    // U手续费 取整
                    BigDecimal tradeUFeeAmount = freeCurrencyAmount.divide(exchangeRate, 0, RoundingMode.DOWN);

                    // U转换为主货币时间手续费 取整
                    feeAmount = tradeUFeeAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);

                    tradeCurrencyAmount = tradeCurrencyAmount.subtract(tradeUFeeAmount);
                    //手续费取整
                    // 实际交易金额 扣掉手续费
                    arrAmount = vo.getAmount().subtract(feeAmount);
                } else {
                    arrAmount = (vo.getAmount().subtract(feeAmount));
                    tradeCurrencyAmount = arrAmount;
                }
            }
        } else {
            arrAmount = (vo.getAmount().subtract(feeAmount));
            tradeCurrencyAmount = arrAmount;
        }
        if (BigDecimal.ZERO.compareTo(tradeCurrencyAmount) >= 0) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_ARRIVE_AMOUNT_NEED_WHOLE);
        }
        //获取站点方式手续费
        SiteWithdrawWayFeeVO siteWithdrawWayFeeVO = systemWithdrawWayService.calculateSiteWithdrawWayFeeRate(userInfoVO.getSiteCode(), vo.getWithdrawWayId(),vo.getAmount());
        //计算方式手续费
        BigDecimal wayFeeAmount = siteWithdrawWayFeeVO.getWayFeeAmount();
        //计算交收手续费   方式手续费-会员手续费
        BigDecimal settlementFeeRate = siteWithdrawWayFeeVO.getWayFee();
        BigDecimal settlementFeeAmount = wayFeeAmount.subtract(feeAmount);

        Integer wayFeeType = siteWithdrawWayFeeVO.getFeeType();
        BigDecimal wayFeeFixedAmount = siteWithdrawWayFeeVO.getWayFeeFixedAmount();
        BigDecimal wayFeePercentageAmount = siteWithdrawWayFeeVO.getWayFeePercentageAmount();
        //创建订单，冻结金额

        int num = createWithdrawOrder(vo, feeRate, feeAmount, wayFeeAmount, settlementFeeRate,
                settlementFeeAmount, arrAmount, tradeCurrencyAmount,
                withdrawConfigVO.getExchangeRate(),
                userInfoVO, withdrawWayPO, withdrawConfigVO.getLargeWithdrawMarkAmount(), userInfoVO.getLastDeviceNo(),
                feeType,wayFeeType,wayFeePercentageAmount,wayFeeFixedAmount);


        return ResponseVO.success(num);


    }

    private void checkHandleOrder(String userId) {
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode(), DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserId, userId);
        lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        lqw.in(UserDepositWithdrawalPO::getStatus, statusList);
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = depositWithdrawalRepository.selectList(lqw);
        if (CollectionUtil.isNotEmpty(userDepositWithdrawalPOS)) {
            throw new BaowangDefaultException(ResultCode.EXIST_WITHDRAW_HANDING_ORDER);
        }
    }


    private void checkWithdrawParam(UserWithDrawApplyVO vo, SystemWithdrawWayPO withdrawWayPO, List<BankManageVO> bankManageVOList) {

        String withdrawTypeCode = withdrawWayPO.getWithdrawTypeCode();
        if (!WithdrawTypeEnum.BANK_CARD.getCode().equals(withdrawTypeCode)
            && !WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)
            && !WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)
                && !WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(withdrawTypeCode)) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_ADDRESS_ERROR);
        }
        List<WithdrawCollectInfoVO> collectList = JSONArray.parseArray(withdrawWayPO.getCollectInfo(), WithdrawCollectInfoVO.class);
        for (WithdrawCollectInfoVO collectInfoVO : collectList) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getBankName())) {
                        throw new BaowangDefaultException(ResultCode.BANK_NAME_IS_EMPTY);
                    }
                }
                case BANK_CARD -> {
                    if (ObjectUtil.isEmpty(vo.getBankCard())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_EMPTY);
                    } else {
                        if (!vo.getBankCard().matches("^\\d{1,19}$")) {
                            throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_ERROR);
                        }
                    }
                }
                case BANK_CODE -> {
                    if (ObjectUtil.isEmpty(vo.getBankCode())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EMPTY);
                    } else {
                        if (CollectionUtil.isNotEmpty(bankManageVOList)) {
                            List<String> bankCodeList = bankManageVOList.stream().map(BankManageVO::getBankCode).collect(Collectors.toList());
                            if (!bankCodeList.contains(vo.getBankCode())) {
                                throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                        }
                    }
                }
                case SURNAME -> {
                    if (ObjectUtil.isEmpty(vo.getSurname())) {
                        throw new BaowangDefaultException(ResultCode.SURNAME_IS_EMPTY);
                    }
                }
                /*case USER_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getUserName())) {
                        throw new BaowangDefaultException(ResultCode.USER_NAME_IS_EMPTY);
                    }
                }*/
                case USER_EMAIL -> {
                    if (ObjectUtil.isEmpty(vo.getUserEmail())) {
                        throw new BaowangDefaultException(ResultCode.USER_EMAIL_IS_EMPTY);
                    }
                }
                case USER_PHONE -> {
                    if (ObjectUtil.isEmpty(vo.getUserPhone())) {
                        throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_EMPTY);
                    } else {
                        if (!vo.getUserPhone().matches("^\\d{3,15}$")) {
                            throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_ERROR);
                        }
                    }
                    if (ObjectUtil.isEmpty(vo.getAreaCode())) {
                        throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EMPTY);
                    } else {
                        String language = CurrReqUtils.getLanguage();
                        List<AreaSiteLangVO> areaList = areaSiteManageApi.getAreaList(CurrReqUtils.getSiteCode(), language).getData();
                        if (CollectionUtil.isNotEmpty(areaList)) {
                            List<String> areaCodeList = areaList.stream().map(AreaSiteLangVO::getAreaCode).collect(Collectors.toList());
                            if (!areaCodeList.contains(vo.getAreaCode())) {
                                throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                        }
                    }
                }
                case PROVINCE_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getProvinceName())) {
                        throw new BaowangDefaultException(ResultCode.PROVINCE_NAME_IS_EMPTY);
                    }
                }
                case CITY_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getCityName())) {
                        throw new BaowangDefaultException(ResultCode.CITY_NAME_IS_EMPTY);
                    }
                }
                case DETAIL_ADDRESS -> {
                    if (ObjectUtil.isEmpty(vo.getDetailAddress())) {
                        throw new BaowangDefaultException(ResultCode.DETAIL_ADDRESS_IS_EMPTY);
                    }
                }
                case USER_ACCOUNT -> {
                    if (ObjectUtil.isEmpty(vo.getUserAccount())) {
                        throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_IS_EMPTY);
                    }
                }
                case NETWORK_TYPE -> {
                    if (ObjectUtil.isEmpty(vo.getNetworkType())) {
                        throw new BaowangDefaultException(ResultCode.NETWORK_TYPE_IS_EMPTY);
                    }
                }
                case ADDRESS_NO -> {
                    if (ObjectUtil.isEmpty(vo.getAddressNo())) {
                        throw new BaowangDefaultException(ResultCode.ADDRESS_NO_IS_EMPTY);
                    }
                }
                case IFSC_CODE -> {
                    if (ObjectUtil.isEmpty(vo.getIfscCode())) {
                        throw new BaowangDefaultException(ResultCode.IFSC_CODE_IS_EMPTY);
                    }
                }
                case CPF -> {
                    if (ObjectUtil.isEmpty(vo.getCpf())) {
                        throw new BaowangDefaultException(ResultCode.IFSC_CODE_IS_EMPTY);
                    }
                }
            }
        }

        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)) {
            //校验取款地址
            if (!AddressUtils.isValidAddress(vo.getAddressNo(), NetWorkTypeEnum.nameOfCode(withdrawWayPO.getNetworkType()).getType())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_ADDRESS_ERROR);
            }
        }

    }

    private int createWithdrawOrder(UserWithDrawApplyVO vo, BigDecimal feeRate, BigDecimal feeAmount, BigDecimal wayFeeAmount,
                                    BigDecimal settlementFeeRate, BigDecimal settlementFeeAmount,
                                    BigDecimal arrAmount, BigDecimal tradeCurrencyAmount, BigDecimal exchangeRate,
                                    UserInfoVO userInfovo, SystemWithdrawWayPO withdrawWayPO, BigDecimal largeWithdrawMarkAmount,
                                    String deviceNo,Integer feeType,Integer wayFeeType,BigDecimal wayFeePercentageAmount,BigDecimal settlementFeeFixedAmount) {

        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setCurrencyCode(userInfovo.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        exchangeRateRequestVO.setSiteCode(CommonConstant.business_zero_str);
        BigDecimal currencyExchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);

        String orderNo = "TK" + userInfovo.getMainCurrency() + DateUtils.dateToyyyyMMddHHmmss(new Date()) + SnowFlakeUtils.getRandomZm();
        UserDepositWithdrawalPO userDepositWithdrawalPO = new UserDepositWithdrawalPO();
        BeanUtils.copyProperties(vo, userDepositWithdrawalPO);
        userDepositWithdrawalPO.setOrderNo(orderNo);
        //设置主货币
        userDepositWithdrawalPO.setCoinCode(withdrawWayPO.getCurrencyCode());
        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawWayPO.getWithdrawTypeCode())) {
            userDepositWithdrawalPO.setCoinCode(CurrencyEnum.USDT.getCode());
        }
        userDepositWithdrawalPO.setCurrencyUsdExchangeRate(currencyExchangeRate);
        userDepositWithdrawalPO.setCurrencyCode(userInfovo.getMainCurrency());
        userDepositWithdrawalPO.setType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        userDepositWithdrawalPO.setDepositWithdrawWayId(vo.getWithdrawWayId());
        userDepositWithdrawalPO.setDepositWithdrawWay(withdrawWayPO.getWithdrawWayI18());
        userDepositWithdrawalPO.setDepositWithdrawTypeId(withdrawWayPO.getWithdrawTypeId());
        userDepositWithdrawalPO.setDepositWithdrawTypeCode(withdrawWayPO.getWithdrawTypeCode());
        userDepositWithdrawalPO.setTradeCurrencyAmount(tradeCurrencyAmount);
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode()) && !WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(withdrawWayPO.getWithdrawTypeCode())) {
            setCnParam(withdrawWayPO.getCollectInfo(), withdrawWayPO.getNetworkType(), userDepositWithdrawalPO,vo.getUserReceiveAccountId());
        }else{
            setParam(withdrawWayPO.getCollectInfo(), withdrawWayPO.getNetworkType(), vo, userDepositWithdrawalPO);
        }
        RiskBlackAccountIsBlackReqVO blackAccountIsBlackReqVO = new RiskBlackAccountIsBlackReqVO();
        blackAccountIsBlackReqVO.setRiskControlTypeCode(userDepositWithdrawalPO.getDepositWithdrawTypeCode());
        blackAccountIsBlackReqVO.setRiskControlAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
        blackAccountIsBlackReqVO.setSiteCode(userInfovo.getSiteCode());
        if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
            blackAccountIsBlackReqVO.setRiskControlAccountName(userDepositWithdrawalPO.getAccountBranch());
        }
        ResponseVO<Boolean> responseVO = siteRiskCtrlBlackApi.isRiskBlack(blackAccountIsBlackReqVO);
        if(responseVO.isOk() && responseVO.getData()){
            throw new BaowangDefaultException(ResultCode.ACCOUNT_IS_BLACK);
        }
        userDepositWithdrawalPO.setCollectInfo(withdrawWayPO.getCollectInfo());
        userDepositWithdrawalPO.setReviewOperation(UserWithDrawReviewOperationEnum.PENDING_REVIEW.getCode());
        userDepositWithdrawalPO.setApplyAmount(vo.getAmount());
        userDepositWithdrawalPO.setFeeType(feeType);
        if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)) {
            userDepositWithdrawalPO.setFeeRate(feeRate);
        }else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)){
            userDepositWithdrawalPO.setFeeFixedAmount(feeRate);
        }
        if(StringUtils.isBlank(vo.getUserPhone())){
            userDepositWithdrawalPO.setAreaCode("");
        }
        userDepositWithdrawalPO.setFeeAmount(feeAmount);
        userDepositWithdrawalPO.setWayFeeType(wayFeeType);
        userDepositWithdrawalPO.setSettlementFeePercentageAmount(wayFeePercentageAmount);
        userDepositWithdrawalPO.setSettlementFeeFixedAmount(settlementFeeFixedAmount);
        userDepositWithdrawalPO.setWayFeeAmount(wayFeeAmount);
        userDepositWithdrawalPO.setSettlementFeeRate(settlementFeeRate);
        userDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);
        userDepositWithdrawalPO.setArriveAmount(arrAmount);
        userDepositWithdrawalPO.setDeviceNo(vo.getDeviceNo());
        userDepositWithdrawalPO.setUserId(userInfovo.getUserId());
        userDepositWithdrawalPO.setUserAccount(userInfovo.getUserAccount());
        userDepositWithdrawalPO.setSiteCode(userInfovo.getSiteCode());
        userDepositWithdrawalPO.setAgentId(userInfovo.getSuperAgentId());
        userDepositWithdrawalPO.setAgentAccount(userInfovo.getSuperAgentAccount());
        userDepositWithdrawalPO.setExchangeRate(exchangeRate);
        userDepositWithdrawalPO.setApplyIp(vo.getApplyIp());
        userDepositWithdrawalPO.setApplyDomain(vo.getApplyDomain());
        userDepositWithdrawalPO.setDeviceType(vo.getDeviceType());
        userDepositWithdrawalPO.setCreator(userInfovo.getUserAccount());
        userDepositWithdrawalPO.setCreatedTime(System.currentTimeMillis());
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

        userDepositWithdrawalPO.setUserLabelId(userInfovo.getUserLabelId());
        if (StringUtils.isNotBlank(userInfovo.getUserLabelId())) {
            List<String> labelIds = Arrays.asList(userInfovo.getUserLabelId().split(","));
            Collections.sort(labelIds);
            userDepositWithdrawalPO.setUserLabelId(StringUtils.join(labelIds.toArray(),","));
        }

        if (null != largeWithdrawMarkAmount) {
            userDepositWithdrawalPO.setIsBigMoney(isBigMoney(vo.getAmount(), largeWithdrawMarkAmount));
        } else {
            userDepositWithdrawalPO.setIsBigMoney(YesOrNoEnum.NO.getCode());
        }

        userDepositWithdrawalPO.setIsFirstOut(isFirstOut(userInfovo.getUserId()));
//        userDepositWithdrawalPO.setIsContinue(isContinue(userWithdrawDTOList,userAccount));
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(vo.getUserId());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.FREEZE.getCode());
        userCoinAddVO.setCoinValue(vo.getAmount());
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setRemark("会员提款");
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfovo, WalletUserInfoVO.class));
        int num = userDepositWithdrawHandleService.withdrawApplySuccess(userDepositWithdrawalPO, userCoinAddVO);
        return num;
    }

    private void setCnParam(String collectInfo, String networkType, UserDepositWithdrawalPO userDepositWithdrawalPO,String userReceiveAccountId) {
        UserReceiveAccountPO userReceiveAccountPO = userReceiveAccountService.getById(userReceiveAccountId);

        List<WithdrawCollectInfoVO> collectList = JSONArray.parseArray(collectInfo, WithdrawCollectInfoVO.class);
        List<WithdrawCollectInfoVO> withdrawCollectInfoVOS = collectList.stream().filter(o -> o.getFiledCode().equals(WithDrawCollectEnum.USER_PHONE.getType())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(withdrawCollectInfoVOS)){
            userDepositWithdrawalPO.setAreaCode("");
            userDepositWithdrawalPO.setTelephone("");
        }
        userDepositWithdrawalPO.setAccountBranch(userReceiveAccountPO.getBankCode());
        for (WithdrawCollectInfoVO collectInfoVO : collectList) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    userDepositWithdrawalPO.setAccountType(userReceiveAccountPO.getBankName());
                }
                case BANK_CARD -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(userReceiveAccountPO.getBankCard());
                }
                case BANK_CODE -> {
                    userDepositWithdrawalPO.setAccountBranch(userReceiveAccountPO.getBankCode());
                }
                case SURNAME -> {
                    userDepositWithdrawalPO.setDepositWithdrawSurname(userReceiveAccountPO.getSurname());
                }
               /* case USER_NAME -> {
                    userDepositWithdrawalPO.setDepositWithdrawName(vo.getUserName());
                }*/
                case USER_EMAIL -> {
                    userDepositWithdrawalPO.setEmail(userReceiveAccountPO.getUserEmail());
                }
                case USER_PHONE -> {
                    userDepositWithdrawalPO.setAreaCode(userReceiveAccountPO.getAreaCode());
                    userDepositWithdrawalPO.setTelephone(userReceiveAccountPO.getUserPhone());
                }
                case PROVINCE_NAME -> {
                    userDepositWithdrawalPO.setProvince(userReceiveAccountPO.getProvinceName());
                }
                case CITY_NAME -> {
                    userDepositWithdrawalPO.setCity(userReceiveAccountPO.getCityName());
                }
                case DETAIL_ADDRESS -> {
                    userDepositWithdrawalPO.setAddress(userReceiveAccountPO.getDetailAddress());
                }
                case USER_ACCOUNT -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(userReceiveAccountPO.getElectronicWalletAccount());
                    userDepositWithdrawalPO.setAccountBranch(userReceiveAccountPO.getElectronicWalletName());
                }
                case NETWORK_TYPE -> {
                    userDepositWithdrawalPO.setAccountType(NetWorkTypeEnum.nameOfCode(networkType).getType());
                    userDepositWithdrawalPO.setAccountBranch(networkType);
                }
                case ADDRESS_NO -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(userReceiveAccountPO.getAddressNo());
                    if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())){
                        userDepositWithdrawalPO.setAccountBranch(userReceiveAccountPO.getElectronicWalletName());
                    }

                }
                case IFSC_CODE -> {
                    userDepositWithdrawalPO.setIfscCode(userReceiveAccountPO.getIfscCode());
                }
            }
        }
    }

    private void setParam(String collectInfo, String networkType, UserWithDrawApplyVO vo, UserDepositWithdrawalPO userDepositWithdrawalPO) {

        List<WithdrawCollectInfoVO> collectList = JSONArray.parseArray(collectInfo, WithdrawCollectInfoVO.class);
        List<WithdrawCollectInfoVO> withdrawCollectInfoVOS = collectList.stream().filter(o -> o.getFiledCode().equals(WithDrawCollectEnum.USER_PHONE.getType())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(withdrawCollectInfoVOS)){
            userDepositWithdrawalPO.setAreaCode("");
            userDepositWithdrawalPO.setTelephone("");
        }
        userDepositWithdrawalPO.setAccountBranch(vo.getBankCode());
        for (WithdrawCollectInfoVO collectInfoVO : collectList) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    userDepositWithdrawalPO.setAccountType(vo.getBankName());
                }
                case BANK_CARD -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(vo.getBankCard());
                }
                case BANK_CODE -> {
                    userDepositWithdrawalPO.setAccountBranch(vo.getBankCode());
                }
                case SURNAME -> {
                    userDepositWithdrawalPO.setDepositWithdrawSurname(vo.getSurname());
                }
               /* case USER_NAME -> {
                    userDepositWithdrawalPO.setDepositWithdrawName(vo.getUserName());
                }*/
                case USER_EMAIL -> {
                    userDepositWithdrawalPO.setEmail(vo.getUserEmail());
                }
                case USER_PHONE -> {
                    userDepositWithdrawalPO.setAreaCode(vo.getAreaCode());
                    userDepositWithdrawalPO.setTelephone(vo.getUserPhone());
                }
                case PROVINCE_NAME -> {
                    userDepositWithdrawalPO.setProvince(vo.getProvinceName());
                }
                case CITY_NAME -> {
                    userDepositWithdrawalPO.setCity(vo.getCityName());
                }
                case DETAIL_ADDRESS -> {
                    userDepositWithdrawalPO.setAddress(vo.getDetailAddress());
                }
                case USER_ACCOUNT -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(vo.getUserAccount());
                }
                case NETWORK_TYPE -> {
                    userDepositWithdrawalPO.setAccountType(NetWorkTypeEnum.nameOfCode(networkType).getType());
                    userDepositWithdrawalPO.setAccountBranch(networkType);
                }
                case ADDRESS_NO -> {
                    userDepositWithdrawalPO.setDepositWithdrawAddress(vo.getAddressNo());
                }
                case IFSC_CODE -> {
                    userDepositWithdrawalPO.setIfscCode(vo.getIfscCode());
                }
            }
        }
    }

    private WithdrawConfigVO checkIsExistWithdrawChannel(String withdrawWayId, String userId, BigDecimal withdrawAmount) {

        WithdrawConfigRequestVO vo = new WithdrawConfigRequestVO();
        vo.setWithdrawWayId(withdrawWayId);
        vo.setUserId(userId);
        WithdrawConfigVO withdrawConfigVO = getWithdrawConfig(vo);
        /*if(!withdrawConfigVO.getIsExistChannel()){
            throw new BaowangDefaultException(ResultCode.NOT_FOUND_WITHDRAW_CHANNEL);
        }*/
        //校验通道金额
        if (withdrawAmount.compareTo(withdrawConfigVO.getWithdrawMinAmount()) < 0) {
            throw new BaowangDefaultException(ResultCode.LESS_MIN_AMOUNT);
        }
        if (withdrawAmount.compareTo(withdrawConfigVO.getWithdrawMaxAmount()) > 0) {
            throw new BaowangDefaultException(ResultCode.GREATER_MAX_AMOUNT);
        }
        return withdrawConfigVO;
    }

    private void checkIsExistWithdrawOrder(String userId) {
        //校验会员是否存在处理中的取款订单
        LambdaQueryWrapper<UserDepositWithdrawalPO> applyIngQueryWrapper = new LambdaQueryWrapper<>();
        applyIngQueryWrapper.eq(UserDepositWithdrawalPO::getUserId, userId);
        applyIngQueryWrapper.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        applyIngQueryWrapper.in(UserDepositWithdrawalPO::getStatus, Arrays.asList(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode(), DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode()));
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = this.baseMapper.selectList(applyIngQueryWrapper);
        if (!userDepositWithdrawalPOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.USER_FUND_ORDER_EXIST);
        }
    }

    private void checkWithdrawPassword(String withdrawPassword, UserInfoVO userInfoVO) {
        if (StringUtils.isBlank(withdrawPassword)) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
        String regex = RegexEnum.WITHDRAW_PWD.getRegex();
        boolean withdrawPwdRegex = withdrawPassword.matches(regex);
        if (!withdrawPwdRegex) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
        String originPassword = getEncryptPassword(withdrawPassword, userInfoVO.getSalt());
        if (!originPassword.equals(userInfoVO.getWithdrawPwd())) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
    }

    public String getEncryptPassword(String password, String salt) {
        String origin = password + salt;
        return MD5Util.MD5Encode(MD5Util.MD5Encode(origin));
    }

    public static boolean isWhole(BigDecimal bigDecimal) {
        return bigDecimal.setScale(0, RoundingMode.HALF_UP).compareTo(bigDecimal) == 0;
    }

    private String isBigMoney(BigDecimal amount, BigDecimal bigMoneyFlag) {
        if (amount.compareTo(bigMoneyFlag) >= 0) {
            return YesOrNoEnum.YES.getCode();
        }
        return YesOrNoEnum.NO.getCode();
    }

    private String isFirstOut(String userId) {
        LambdaQueryWrapper<UserDepositWithdrawalPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDepositWithdrawalPO::getUserId, userId);
        queryWrapper.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        queryWrapper.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
        List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = this.baseMapper.selectList(queryWrapper);
        if (userDepositWithdrawalPOS.isEmpty()) {
            return YesOrNoEnum.YES.getCode();
        }
        return YesOrNoEnum.NO.getCode();
    }

    public WithdrawConfigVO getWithdrawConfig(WithdrawConfigRequestVO vo) {

        UserInfoVO userInfoVO = userInfoApi.getByUserId(vo.getUserId());
        //检查会员类型
       /* if(!userInfoVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))){
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验会员账号状态
        if(userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }*/

        //获取余额，提款方式最大最小值，剩余流水，提款费率
        WithdrawConfigVO withdrawConfigVO = new WithdrawConfigVO();
        withdrawConfigVO.setWithdrawWayId(vo.getWithdrawWayId());

        //设置余额
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserId(userInfoVO.getUserId());
        UserCoinWalletVO userCoinWalletVO = userCoinService.getUserCenterCoin(userCoinQueryVO);
        withdrawConfigVO.setBalance(userCoinWalletVO.getCenterAmount());
        List<String> userLabelList = getUserLabels(userInfoVO.getUserLabelId());
        //设置流水
        if (!userLabelList.contains(UserLabelEnum.WITHDRAWAL_NO_REQUIREMENTS.getLabelId())) {
            UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(new LambdaQueryWrapper<UserTypingAmountPO>()
                    .eq(UserTypingAmountPO::getUserId, vo.getUserId()));
            if (null != userTypingAmountPO) {
                withdrawConfigVO.setRemainingFlow(userTypingAmountPO.getTypingAmount());
            } else {
                withdrawConfigVO.setRemainingFlow(BigDecimal.ZERO);
            }
        } else {
            withdrawConfigVO.setRemainingFlow(BigDecimal.ZERO);
        }
        SystemWithdrawWayPO systemWithdrawWayPO = systemWithdrawWayService.getById(vo.getWithdrawWayId());
        withdrawConfigVO.setWayIcon(systemWithdrawWayPO.getWayIcon());
        withdrawConfigVO.setNetworkType(systemWithdrawWayPO.getNetworkType());
        LambdaQueryWrapper<UserWithdrawConfigPO> withdrawConfigLqw = new LambdaQueryWrapper<>();
        withdrawConfigLqw.eq(UserWithdrawConfigPO::getSiteCode, userInfoVO.getSiteCode());
        withdrawConfigLqw.eq(UserWithdrawConfigPO::getCurrencyCode, userInfoVO.getMainCurrency());
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            withdrawConfigLqw.eq(UserWithdrawConfigPO::getVipGradeCode,userInfoVO.getVipGradeCode());
        }else{
            withdrawConfigLqw.eq(UserWithdrawConfigPO::getVipRankCode, userInfoVO.getVipRank());
        }
        UserWithdrawConfigPO userWithdrawConfigPO = userWithdrawConfigRepository.selectOne(withdrawConfigLqw);
        if (null != userWithdrawConfigPO) {
            WithdrawConfigVO remainConfig = getTodayRemain(vo.getUserId(),vo.getWithdrawWayId());
            setTodayRemainNumsAndAmount(withdrawConfigVO,userWithdrawConfigPO.getSingleDayWithdrawCount(),userWithdrawConfigPO.getSingleMaxWithdrawAmount(),
                    userWithdrawConfigPO.getDailyWithdrawalNumsLimit(),userWithdrawConfigPO.getDailyWithdrawAmountLimit(),
                    remainConfig.getSingleDayRemindWithdrawCount(),remainConfig.getSingleDayRemindMaxWithdrawAmount(),
                    remainConfig.getDayRemindWithdrawCount(),remainConfig.getDayRemindMaxWithdrawAmount());
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(systemWithdrawWayPO.getWithdrawTypeCode())) {
                withdrawConfigVO.setWithdrawMinAmount(userWithdrawConfigPO.getBankCardSingleWithdrawMinAmount());
                withdrawConfigVO.setWithdrawMaxAmount(userWithdrawConfigPO.getBankCardSingleWithdrawMaxAmount());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(systemWithdrawWayPO.getWithdrawTypeCode())) {
                withdrawConfigVO.setWithdrawMinAmount(userWithdrawConfigPO.getElectronicWalletWithdrawMinAmount());
                withdrawConfigVO.setWithdrawMaxAmount(userWithdrawConfigPO.getElectronicWalletWithdrawMaxAmount());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemWithdrawWayPO.getWithdrawTypeCode())) {
                withdrawConfigVO.setWithdrawMinAmount(userWithdrawConfigPO.getCryptoCurrencySingleWithdrawMinAmount());
                withdrawConfigVO.setWithdrawMaxAmount(userWithdrawConfigPO.getCryptoCurrencySingleWithdrawMaxAmount());
            }else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(systemWithdrawWayPO.getWithdrawTypeCode())) {
                withdrawConfigVO.setWithdrawMinAmount(userWithdrawConfigPO.getElectronicWalletWithdrawMinAmount());
                withdrawConfigVO.setWithdrawMaxAmount(userWithdrawConfigPO.getElectronicWalletWithdrawMaxAmount());
            }else{
                withdrawConfigVO.setWithdrawMinAmount(BigDecimal.ZERO);
                withdrawConfigVO.setWithdrawMaxAmount(BigDecimal.ZERO);
            }
            withdrawConfigVO.setLargeWithdrawMarkAmount(userWithdrawConfigPO.getLargeWithdrawMarkAmount());
            LambdaQueryWrapper<UserWithdrawConfigDetailPO> withdrawConfigDetailLqw = new LambdaQueryWrapper<>();
            withdrawConfigDetailLqw.eq(UserWithdrawConfigDetailPO::getUserId,userInfoVO.getUserId());
            UserWithdrawConfigDetailPO userWithdrawConfigDetailPO = userWithdrawConfigDetailRepository.selectOne(withdrawConfigDetailLqw);
            if(null != userWithdrawConfigDetailPO){
                setTodayRemainNumsAndAmount(withdrawConfigVO,userWithdrawConfigDetailPO.getSingleDayWithdrawCount(),userWithdrawConfigDetailPO.getSingleMaxWithdrawAmount(),
                        userWithdrawConfigDetailPO.getDayWithdrawCount(),userWithdrawConfigDetailPO.getMaxWithdrawAmount(),
                        remainConfig.getSingleDayRemindWithdrawCount(),remainConfig.getSingleDayRemindMaxWithdrawAmount(),
                        remainConfig.getDayRemindWithdrawCount(),remainConfig.getDayRemindMaxWithdrawAmount());
            }
        }
        withdrawConfigVO.setCollectInfoVOS(JSONArray.parseArray(systemWithdrawWayPO.getCollectInfo(), WithdrawCollectInfoVO.class));
        //设置费率
        SiteVipFeeRateVO siteVipRankCurrencyConfigVO = getVipFeeRate(userInfoVO.getSiteCode(), userInfoVO.getVipRank(),userInfoVO.getVipGradeCode(),
                userInfoVO.getMainCurrency(),vo.getWithdrawWayId());
        if (null != siteVipRankCurrencyConfigVO) {
            withdrawConfigVO.setFeeType(siteVipRankCurrencyConfigVO.getWithdrawFeeType());
            withdrawConfigVO.setFeeRate(siteVipRankCurrencyConfigVO.getWithdrawFee());
            if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemWithdrawWayPO.getWithdrawTypeCode())
                && YesOrNoEnum.NO.getCode().equals(siteVipRankCurrencyConfigVO.getEncryCoinFee().toString())) {
                withdrawConfigVO.setFeeRate(BigDecimal.ZERO);
            }
        } else {
            withdrawConfigVO.setFeeRate(BigDecimal.ZERO);
        }

        //设置最大最小范围
        Map<String, List<SystemWithdrawChannelResponseVO>> channelGroup = systemWithdrawWayService.getChannelGroup(userInfoVO.getSiteCode(), String.valueOf(userInfoVO.getVipRank()));
        List<SystemWithdrawChannelResponseVO> channelPOS = channelGroup.get(vo.getWithdrawWayId());
        if (null != channelPOS && !channelPOS.isEmpty()) {
            withdrawConfigVO.setIsExistChannel(true);
            BigDecimal minAmount = channelPOS.stream().map(SystemWithdrawChannelResponseVO::getWithdrawMin).min(BigDecimal::compareTo).get();
            BigDecimal maxAmount = channelPOS.stream().map(SystemWithdrawChannelResponseVO::getWithdrawMax).max(BigDecimal::compareTo).get();
            //通道最小值金额比会元提款配置的大，去通道最小
            if(null == withdrawConfigVO.getWithdrawMinAmount() || minAmount.compareTo(withdrawConfigVO.getWithdrawMinAmount()) > 0){
                withdrawConfigVO.setWithdrawMinAmount(minAmount);

            }
            if(null ==withdrawConfigVO.getWithdrawMaxAmount()  || maxAmount.compareTo(withdrawConfigVO.getWithdrawMaxAmount()) <0){
                withdrawConfigVO.setWithdrawMaxAmount(maxAmount);
            }
        } else {
            withdrawConfigVO.setIsExistChannel(false);
        }
        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        exchangeRateRequestVO.setSiteCode(userInfoVO.getSiteCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        withdrawConfigVO.setExchangeRate(exchangeRate);
        //获取银行列表
        List<BankManageVO> list = bankCardManagerService.bankList(userInfoVO.getMainCurrency());
        withdrawConfigVO.setBankList(list);


        //获取上一次提款成功的信息
        UserDepositWithdrawalPO userDepositWithdrawalPO = depositWithdrawalRepository.selectLastSuccessOrder(vo.getUserId(), vo.getWithdrawWayId());
        LastWithdrawInfoVO lastWithdrawInfoVO = new LastWithdrawInfoVO();
        if (null != userDepositWithdrawalPO) {
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setBankName(userDepositWithdrawalPO.getAccountType());
                lastWithdrawInfoVO.setBankCode(userDepositWithdrawalPO.getAccountBranch());
                lastWithdrawInfoVO.setBankCard(userDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
                lastWithdrawInfoVO.setUserEmail(userDepositWithdrawalPO.getEmail());
                lastWithdrawInfoVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                lastWithdrawInfoVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                lastWithdrawInfoVO.setProvinceName(userDepositWithdrawalPO.getProvince());
                lastWithdrawInfoVO.setCityName(userDepositWithdrawalPO.getCity());
                lastWithdrawInfoVO.setDetailAddress(userDepositWithdrawalPO.getAddress());
                lastWithdrawInfoVO.setIfscCode(userDepositWithdrawalPO.getIfscCode());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
                lastWithdrawInfoVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                lastWithdrawInfoVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                lastWithdrawInfoVO.setAddressNo(userDepositWithdrawalPO.getDepositWithdrawAddress());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setNetworkType(userDepositWithdrawalPO.getAccountBranch());
                lastWithdrawInfoVO.setAddressNo(userDepositWithdrawalPO.getDepositWithdrawAddress());
            }else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                lastWithdrawInfoVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                lastWithdrawInfoVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                lastWithdrawInfoVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
            }
        }
        withdrawConfigVO.setLastWithdrawInfoVO(lastWithdrawInfoVO);
        return withdrawConfigVO;
    }



    private List<String> getUserLabels(String userLabelId) {
        List<String> userLabelList = new ArrayList<>();
        if (StringUtils.isNotBlank(userLabelId)) {
            List<String> labelIds = Arrays.asList(userLabelId.split(","));
            //会员标签
            List<GetUserLabelByIdsVO> labelList = siteUserLabelConfigApi.getUserLabelByIds(labelIds);
            userLabelList = labelList.stream()
                    .map(GetUserLabelByIdsVO::getLabelId)
                    .collect(Collectors.toList());
        }
        return userLabelList;
    }

    /**
     * 获取段位手续费率
     *
     * @return
     */
    private SiteVipFeeRateVO getVipFeeRate(String siteCode, Integer vipRank,Integer vipGradeCode, String currencyCode, String withdrawWayId) {
        SiteVipFeeRateVO siteVIPRankVO = null;
        //如果是国际盘获取VIP等级配置
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            siteVIPRankVO = siteVipOptionApi.getVipGradeSiteCodeAndCurrency(siteCode, vipGradeCode, currencyCode, withdrawWayId);
        }else{
            siteVIPRankVO = vipRankApi.getVipRankSiteCodeAndCurrency(siteCode, vipRank, currencyCode, withdrawWayId);
        }

        return siteVIPRankVO;
    }

    /**
     * 设置剩余次数和剩余金额
     * @return
     */
    private void setTodayRemainNumsAndAmount(WithdrawConfigVO withdrawConfigVO,Integer singleDayWithdrawCount,BigDecimal singleMaxWithdrawAmount,
                                                         Integer dayWithdrawCount,BigDecimal maxWithdrawAmount,
                                             Integer usedFreeNums,BigDecimal userFreeAmount,
                                             Integer usedNums,BigDecimal usedAmount) {

        Integer freeRemainNums = null == singleDayWithdrawCount?0: singleDayWithdrawCount- usedFreeNums;
        BigDecimal freeRemainAmount = null==singleMaxWithdrawAmount?BigDecimal.ZERO:singleMaxWithdrawAmount.subtract(userFreeAmount);
        Integer totalRemainNums = null ==dayWithdrawCount?0: dayWithdrawCount- usedNums;
        BigDecimal totalRemainAmount = null==maxWithdrawAmount?BigDecimal.ZERO:maxWithdrawAmount.subtract(usedAmount);
        withdrawConfigVO.setSingleDayRemindWithdrawCount(freeRemainNums < 0 ? 0 : freeRemainNums);
        withdrawConfigVO.setSingleDayRemindMaxWithdrawAmount(freeRemainAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : freeRemainAmount);
        withdrawConfigVO.setDayRemindWithdrawCount(totalRemainNums < 0 ? 0 : totalRemainNums);
        withdrawConfigVO.setDayRemindMaxWithdrawAmount(totalRemainAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalRemainAmount);
    }

    /**
     * 获取今日已提款次数和已提款金额
     */
    private WithdrawConfigVO getTodayRemain(String userId,String withdrawWayId) {
        //获取今日处理中和提款成功的订单
        Long todayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone());
        LambdaQueryWrapper<UserDepositWithdrawalPO> withdrawLqw = new LambdaQueryWrapper<>();
        withdrawLqw.ge(UserDepositWithdrawalPO::getUpdatedTime, todayStartTime);
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode()
                , DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode(), DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode(),
                DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode(), DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        withdrawLqw.in(UserDepositWithdrawalPO::getStatus, statusList);
        withdrawLqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
//        withdrawLqw.eq(UserDepositWithdrawalPO::getDepositWithdrawWayId,withdrawWayId);
        withdrawLqw.eq(UserDepositWithdrawalPO::getUserId, userId);

        List<UserDepositWithdrawalPO> withdrawalPOS = depositWithdrawalRepository.selectList(withdrawLqw);
        BigDecimal withdrawAmount = withdrawalPOS.stream()
                .map(UserDepositWithdrawalPO::getApplyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //获取今日人工减额处理中和成功的订单
        LambdaQueryWrapper<UserManualUpDownRecordPO> manualUpDownLqw = new LambdaQueryWrapper<>();
        manualUpDownLqw.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());
        manualUpDownLqw.eq(UserManualUpDownRecordPO::getAdjustType, ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode());
        manualUpDownLqw.eq(UserManualUpDownRecordPO::getBalanceChangeStatus,CommonConstant.business_one);
        manualUpDownLqw.eq(UserManualUpDownRecordPO::getAuditStatus,ReviewStatusEnum.REVIEW_PASS.getCode());
        manualUpDownLqw.ge(UserManualUpDownRecordPO::getUpdatedTime, todayStartTime);
        manualUpDownLqw.eq(UserManualUpDownRecordPO::getUserId, userId);
        List<UserManualUpDownRecordPO> manualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(manualUpDownLqw);

        BigDecimal manualAmount = manualUpDownRecordPOS.stream()
                .map(UserManualUpDownRecordPO::getAdjustAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        WithdrawConfigVO withdrawConfigVO = new WithdrawConfigVO();
        withdrawConfigVO.setSingleDayRemindWithdrawCount(withdrawalPOS.size());
        withdrawConfigVO.setSingleDayRemindMaxWithdrawAmount(withdrawAmount);
        withdrawConfigVO.setDayRemindWithdrawCount(withdrawalPOS.size()+manualUpDownRecordPOS.size());
        withdrawConfigVO.setDayRemindMaxWithdrawAmount(withdrawAmount.add(manualAmount));
        return withdrawConfigVO;
    }



    public CheckRemainingFlowVO checkRemainingFlow(String userId) {
        CheckRemainingFlowVO checkRemainingFlowVO = new CheckRemainingFlowVO();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        List<String> userLabelList = getUserLabels(userInfoVO.getUserLabelId());
        //设置流水
        if (!userLabelList.contains(UserLabelEnum.WITHDRAWAL_NO_REQUIREMENTS.getLabelId())) {
            UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(new LambdaQueryWrapper<UserTypingAmountPO>()
                    .eq(UserTypingAmountPO::getUserId, userId));
            if (null != userTypingAmountPO && BigDecimal.ZERO.compareTo(userTypingAmountPO.getTypingAmount()) < 0) {
                checkRemainingFlowVO.setIsWithdraw(CommonConstant.business_zero_str);
                checkRemainingFlowVO.setRemainingFlow(userTypingAmountPO.getTypingAmount());
            } else {
                checkRemainingFlowVO.setIsWithdraw(CommonConstant.business_one_str);
                checkRemainingFlowVO.setRemainingFlow(new BigDecimal(CommonConstant.business_zero));
            }
        } else {
            checkRemainingFlowVO.setIsWithdraw(CommonConstant.business_one_str);
            checkRemainingFlowVO.setRemainingFlow(new BigDecimal(CommonConstant.business_zero));
        }
        return checkRemainingFlowVO;
    }
}

