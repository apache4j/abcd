package com.cloud.baowang.play.game.db.sh;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.db.config.DBCryptoConfig;
import com.cloud.baowang.play.api.vo.db.rsp.sh.DBSHBaseRsp;
import com.cloud.baowang.play.api.vo.db.sh.enums.SHErrorEnum;
import com.cloud.baowang.play.api.vo.db.sh.enums.SettleTypeEnum;
import com.cloud.baowang.play.api.vo.db.sh.enums.TransferTypeEnum;
import com.cloud.baowang.play.api.vo.db.sh.vo.*;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.db.sh.enums.DBSHGameTypeEnum;
import com.cloud.baowang.play.game.db.sh.enums.DBSHLangEnum;
import com.cloud.baowang.play.game.db.sh.utils.AESUtil;
import com.cloud.baowang.play.game.db.vo.Md5StringUtils;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainRequestVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_SH)
public class DBSHServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final DomainInfoApi domainInfoApi;
    private final UserInfoApi userInfoApi;
    private final DBCryptoConfig cryptoConfig;




    private final static String SUCCESS_CODE = "200";
    private final static String REGISTER_SUCCESS_CODE = "20000";

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;




    public DBSHBaseRsp checkRequestValid(UserInfoVO userInfoVO) {
        if (userInfoVO == null) {
            return null;
        }
        if (venueMaintainClosed( VenueEnum.DBSH.getVenueCode(),userInfoVO.getSiteCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBSH.getVenueCode());
            return DBSHBaseRsp.failed(SHErrorEnum.SERVICE_UNAVAILABLE);
        }


        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBSH.getVenueCode());
            return DBSHBaseRsp.failed(SHErrorEnum.MEMBER_STATUS_ERROR);
        }

        return null;
    }

    public Map<String, String> userCheck(SHBalanceQueryBatchVO req) {
        SHUserInfos shUserInfo = JSON.parseObject(req.getParams(), SHUserInfos.class);
        List<String> members = shUserInfo.getLoginNames();
        if (members == null || members.isEmpty()) {
            return null;
        }

        Map<String, String> userIdsMap = members.stream()
                .collect(Collectors.toMap(this::adaptThirdAccount, Function.identity()));

        List<UserInfoVO> userInfos = userInfoApi.getUserInfoByUserIds(new ArrayList<>(userIdsMap.keySet()));
        DBSHBaseRsp rsp = checkRequestValid(userInfos.get(0));
        if (rsp != null) {
            return null;
        }

        return (userInfos.size() == members.size()) ? userIdsMap : null;
    }


    private  boolean checkMD5(String params, String merchant, String sign) {
        String key = cryptoConfig.getIV(VenueEnum.DBSH.getVenueCode(), merchant);
        String signStr = params + key;
        log.info("reqVO.toString(): " + params + " key : " + key);
        return sign.equals(Objects.requireNonNull(MD5Util.MD5Encode(signStr)).toUpperCase(Locale.ROOT));
    }

    private DBSHBaseRsp<BetRspParams> buildRsp(String merchantCode, String loginName, String userId,
                                               BigDecimal rollbackAmount,
                                               BigDecimal realBetAmount,
                                               BigDecimal badAmount,
                                               BigDecimal realAmount,
                                               List<BetInfo> realBetOrders) {
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);

        BetRspParams rspParams = new BetRspParams();
        rspParams.setLoginName(loginName);
        rspParams.setBalance(userCoin.getTotalAmount());

        if (rollbackAmount != null) {
            rspParams.setRollbackAmount(rollbackAmount);
        }
        if (realBetAmount != null) {
            rspParams.setRealBetAmount(realBetAmount);
        }
        if (realBetOrders != null && !realBetOrders.isEmpty()) {
            rspParams.setRealBetInfo(realBetOrders);
        }
        if (badAmount != null) {
            rspParams.setBadAmount(badAmount);
        }
        if (realAmount != null) {
            rspParams.setRealAmount(realAmount);
        }

        String sign = buildSign(JSONObject.toJSONString(rspParams), merchantCode);
        return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
    }


    public String buildSign(String source,String merchant){
        String md5Key = cryptoConfig.getMerchantkey(VenueEnum.DBSH.getVenueCode(), merchant);
        log.info("md5Key: " + md5Key);
        return MD5Util.MD5Encode(source+md5Key);
    }


    public DBSHBaseRsp<SHOrderRspData> getBalance(SHBalanceQueryVO reqVO) {
        log.info("getBalance: DB真人 " + reqVO.toString());
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        SHUserInfo shUserInfo = JSON.parseObject(reqVO.getParams(), SHUserInfo.class);
        String thirdUserId = shUserInfo.getLoginName();
        String userId = adaptThirdAccount(thirdUserId);

        UserInfoVO userInfoVO = getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBSH.getVenueCode());
            return DBSHBaseRsp.failed(SHErrorEnum.MEMBER_NOT_EXIST);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (userCenterCoin.getTotalAmount() == null) {
            return DBSHBaseRsp.failed(SHErrorEnum.SERVICE_UNAVAILABLE);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        SHOrderRspData rspData = SHOrderRspData.builder().loginName(thirdUserId).balance(totalAmount).build();
        String sign = buildSign(JSONObject.toJSONString(rspData), reqVO.getMerchantCode());
        return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspData);
    }

    public DBSHBaseRsp<List<SHOrderRspData>> getBatchBalance(SHBalanceQueryBatchVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("getBatchBalance : DB真人 - {}", reqVO);

        Map<String, String> userIdMap = userCheck(reqVO);

        if (userIdMap == null) {
            return DBSHBaseRsp.failed(SHErrorEnum.MEMBER_ACCOUNT_ERROR);
        }
        List<UserCoinWalletVO> userCenterCoinList = userCoinApi.getUserCenterCoinList(new ArrayList<>(userIdMap.keySet()));
        if (userCenterCoinList.isEmpty()
                || userCenterCoinList.stream().anyMatch(vo -> vo.getUserId() == null)) {
            return DBSHBaseRsp.failed(SHErrorEnum.OTHER_EXCEPTION);
        }

        List<SHOrderRspData> result = Lists.newArrayList();
        for (UserCoinWalletVO userCoinWalletVO : userCenterCoinList) {
            String thirdUserId = userIdMap.get(userCoinWalletVO.getUserId());
            BigDecimal totalAmount = userCoinWalletVO.getTotalAmount().setScale(2, RoundingMode.DOWN);
            SHOrderRspData rspData = SHOrderRspData.builder().loginName(thirdUserId).balance(totalAmount).build();
            result.add(rspData);
        }
        String sign = buildSign(JSONObject.toJSONString(result), reqVO.getMerchantCode());
        return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), result);
    }

    public DBSHBaseRsp<BetRspParams> betConfirm(BetRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        BetParams params = JSON.parseObject(reqVO.getParams(), BetParams.class);
        List<BetInfo> betInfos = params.getBetInfo();
        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO =getByUserId(userId);
        DBSHBaseRsp isValid = checkRequestValid(userInfoVO);
        if (isValid != null) {
            return isValid;
        }

        List<BetInfo> pendingOrders = Lists.newArrayList();
        List<BetInfo> processedOrders = Lists.newArrayList();
        for (BetInfo betInfo : betInfos) {
            String orderId = String.valueOf(betInfo.getBetId());
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                processedOrders.add(betInfo);
                log.info("DB真人 betConfirm error 账单已经存在: " + betInfo);
            } else {
                pendingOrders.add(betInfo);
            }
        }

        if (pendingOrders.isEmpty()) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BetRspParams rspParams = new BetRspParams();
            rspParams.setLoginName(loginName);
            rspParams.setBalance(userCoin.getTotalAmount());
            rspParams.setRealBetAmount(params.getBetTotalAmount());
            rspParams.setRealBetInfo(processedOrders);
            String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
            return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
        }
        log.info("待处理订单 : - " + pendingOrders);
        pendingOrders.sort(new BetInfo.BetAmountComparator());

        BigDecimal betTotalAmount = params.getBetTotalAmount();
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(betTotalAmount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.DBSH.getVenueCode(), pendingOrders, userCoin, betTotalAmount);
            return DBSHBaseRsp.failed(SHErrorEnum.INSUFFICIENT_BALANCE);
        }

        List<BetInfo> realBetOrders = Lists.newArrayList();
        for (BetInfo betInfo : pendingOrders) {
            String orderId = String.valueOf(betInfo.getBetId());
            String roundId = String.valueOf(params.getTransferNo());
            BigDecimal singleAmount = betInfo.getBetAmount();
            //账变
            CoinRecordResultVO coinRecordResultVO = this.handleBetCoin(userInfoVO, orderId, singleAmount, roundId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("DB真人 bet error 账变失败:  {} ", betInfo);
                break;
            } else {
                realBetOrders.add(betInfo);
            }

        }
        if (realBetOrders.isEmpty()) {
            return DBSHBaseRsp.failed(SHErrorEnum.INSUFFICIENT_BALANCE);
        }
        BigDecimal realBetAmount = realBetOrders.stream()
                .map(BetInfo::getBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, realBetAmount, null, null, realBetOrders);
    }

    public DBSHBaseRsp<BetRspParams> betCancel(DBSHRequestVO  reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        CancelBetParams cancelBetParams = JSON.parseObject(reqVO.getParams(), CancelBetParams.class);
        String loginName = cancelBetParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = getByUserId(userId);

        Map<String, BigDecimal> betPayoutMap = cancelBetParams.getBetPayoutMap();
        String transferNo = String.valueOf(reqVO.getTransferNo());
        List<BetInfo> pendingOrders = Lists.newArrayList();
        if (betPayoutMap == null) {
            //全部取消
            List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, transferNo);
            UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelBet != null) {
                log.info("DB真人 cancelBet 已经存在订单" + cancelBet);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);

                BetRspParams rspParams = new BetRspParams();
                rspParams.setLoginName(loginName);
                rspParams.setBalance(userCoin.getTotalAmount());
                BigDecimal rollBackAmount = existOrders.stream()
                        .map(UserCoinRecordVO::getCoinValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                rspParams.setRollbackAmount(rollBackAmount);
                String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
                return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
            }

            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder == null) {
                log.info("DB真人 cancelBet 不存在下注订单" + betOrder);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                BetRspParams rspParams = new BetRspParams();
                rspParams.setLoginName(loginName);
                rspParams.setBalance(userCoin.getTotalAmount());
                BigDecimal rollBackAmount = BigDecimal.ZERO;
                rspParams.setRollbackAmount(rollBackAmount);
                String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
                return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
            }
            List<BetInfo> betInfos = existOrders.stream()
                    .map(order -> {
                        BetInfo betInfo = new BetInfo();
                        betInfo.setBetId(Long.valueOf(order.getOrderNo()));
                        betInfo.setBetAmount(order.getCoinValue());
                        return betInfo;
                    })
                    .toList();
            pendingOrders.addAll(betInfos);
        } else {
            for (Map.Entry<String, BigDecimal> entry : betPayoutMap.entrySet()) {
                String key = entry.getKey();
                BigDecimal value = entry.getValue();
                List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, key);
                UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
                if (cancelBet != null) {
                    log.info("DB真人 cancelBet 已经存在订单" + cancelBet);
                    continue;
                }
                UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
                if (betOrder == null) {
                    log.info("DB真人 cancelBet 不存在下注订单" + betOrder);
                    continue;
                }
                BetInfo betInfo = new BetInfo();
                betInfo.setBetId(Long.valueOf(key));
                betInfo.setBetAmount(value);
                pendingOrders.add(betInfo);
            }
        }

        if (pendingOrders.isEmpty()) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BetRspParams rspParams = new BetRspParams();
            rspParams.setLoginName(loginName);
            rspParams.setBalance(userCoin.getTotalAmount());
            BigDecimal rollbackAmount = BigDecimal.ZERO;
            rspParams.setRollbackAmount(rollbackAmount);

            String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
            return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
        }

        for (BetInfo betInfo : pendingOrders) {
            BigDecimal amt = betInfo.getBetAmount();
            String orderId = String.valueOf(betInfo.getBetId());
            CoinRecordResultVO result = handleCancelBetCoin(userInfoVO, orderId, amt, String.valueOf(reqVO.getTransferNo()), true);
            if (result == null || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                log.info("DB真人 bet error 账变失败:  {} ", betInfo);
            }
        }
        BigDecimal rollbackAmount = pendingOrders.stream()
                .map(BetInfo::getBetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return buildRsp(reqVO.getMerchantCode(), loginName, userId, rollbackAmount, null, null, null, null);

    }

    public DBSHBaseRsp<BetRspParams> gamePayout(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        SettleParams settleParams = JSON.parseObject(reqVO.getParams(), SettleParams.class);
        String loginName = settleParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = getByUserId(userId);

        Map<String, BigDecimal> betPayoutMap = settleParams.getBetPayoutMap();
        String transferNo = String.valueOf(reqVO.getTransferNo());
        List<BetInfo> pendingOrders = Lists.newArrayList();
        if (betPayoutMap == null) {
            //全部取消
            return DBSHBaseRsp.failed(SHErrorEnum.PARAMETER_ERROR);
        } else {
            for (Map.Entry<String, BigDecimal> entry : betPayoutMap.entrySet()) {
                String key = entry.getKey();
                BigDecimal value = entry.getValue();
                List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, transferNo);
                if (existOrders != null && !existOrders.isEmpty()) {
                    return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, settleParams.getPayoutAmount(), null);
                }
                BetInfo betInfo = new BetInfo();
                betInfo.setBetId(Long.valueOf(key));
                betInfo.setBetAmount(value);
                pendingOrders.add(betInfo);
            }
        }

        String transferType = settleParams.getTransferType();
        SettleTypeEnum settleTypeEnum = SettleTypeEnum.fromCode(transferType);
        switch (settleTypeEnum) {
            case PAYOUT, DISCARD -> {
                for (BetInfo betInfo : pendingOrders) {
                    BigDecimal amt = betInfo.getBetAmount();
                    String orderId = String.valueOf(betInfo.getBetId());
                    CoinRecordResultVO result = handleSettleCoin(userInfoVO, orderId, amt, String.valueOf(reqVO.getTransferNo()));
                    if (result == null || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                        log.info("DB真人 bet error 账变失败:  {} ", betInfo);
                    }
                }
            }
            case CANCEL -> {
                for (BetInfo betInfo : pendingOrders) {
                    BigDecimal amt = betInfo.getBetAmount();
                    String orderId = String.valueOf(betInfo.getBetId());
                    boolean isAdd = amt.compareTo(BigDecimal.ZERO) > 0;
                    CoinRecordResultVO result = handleCancelBetCoin(userInfoVO, orderId, amt, String.valueOf(reqVO.getTransferNo()), isAdd);
                    if (result == null || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                        log.info("DB真人 bet error 账变失败:  {} ", betInfo);
                    }
                }
            }

            case REPAYOUT -> {
                for (BetInfo betInfo : pendingOrders) {
                    BigDecimal amt = betInfo.getBetAmount();
                    String orderId = String.valueOf(betInfo.getBetId());
                    boolean isAdd = amt.compareTo(BigDecimal.ZERO) > 0;
                    CoinRecordResultVO result = reSettle(userInfoVO, orderId, amt, String.valueOf(reqVO.getTransferNo()), isAdd);
                    if (result == null || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                        log.info("DB真人 bet error 账变失败:  {} ", betInfo);
                    }
                }
            }
            default -> {}
        }
        //TODO 记坏账?

        return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, settleParams.getPayoutAmount(), null);
    }

    public DBSHBaseRsp<BetRspParams> activityPayout(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("activityPayout : DB真人 - {}", reqVO);
        ActivityPayoutVO activityParams = JSON.parseObject(reqVO.getParams(), ActivityPayoutVO.class);
        String loginName = activityParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = getByUserId(userId);
        String transferNo = String.valueOf(reqVO.getTransferNo());

        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, transferNo, true);
        if (!userCoinRecordVOS.isEmpty()) {
            return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, activityParams.getPayoutAmount(), null);
        }
        BigDecimal amount = activityParams.getPayoutAmount();
        String remark = TransferTypeEnum.fromCode(activityParams.getTransferType()).getDesc();
        if (BigDecimal.ZERO.compareTo(amount) == 0) {
            return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, activityParams.getPayoutAmount(), null);
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            //扣钱
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                log.error("{} 打赏失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 打赏金额 : {} ", VenueEnum.DG2.getVenueCode(), activityParams, userCoin, amount);
                return DBSHBaseRsp.failed(SHErrorEnum.INSUFFICIENT_BALANCE);
            }

            CoinRecordResultVO coinRecordResultVO = this.handleBetCoin(userInfoVO, transferNo, amount, remark);
            if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, activityParams.getPayoutAmount(), null);
            } else {
                log.info("DB真人 tip : 投注 : reqData -> " + activityParams);
                return DBSHBaseRsp.failed(SHErrorEnum.SYSTEM_ERROR);
            }

        } else {
            CoinRecordResultVO coinRecordResultVO = this.handleSettleCoin(userInfoVO, transferNo, amount, remark);
            if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                return buildRsp(reqVO.getMerchantCode(), loginName, userId, null, null, BigDecimal.ZERO, activityParams.getPayoutAmount(), null);
            } else {
                log.info("DB真人 tip : 结算 : reqData -> " + activityParams);
                return DBSHBaseRsp.failed(SHErrorEnum.SYSTEM_ERROR);
            }
        }

    }

    public DBSHBaseRsp<BetRspParams> playerbetting(DBSHRequestVO reqVO) {

        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        PlayerBettingNotifyVO params = JSON.parseObject(reqVO.getParams(), PlayerBettingNotifyVO.class);

        log.info("activityPayout : DB真人 - {}", reqVO);

        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = getByUserId(userId);
        String transferNo = String.valueOf(reqVO.getTransferNo());

        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, transferNo, true);
        if (!userCoinRecordVOS.isEmpty()) {
            BetRspParams rspParams = new BetRspParams();
            rspParams.setMerchantCode(reqVO.getMerchantCode());
            String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
            return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
        } else {
            return DBSHBaseRsp.failed(SHErrorEnum.BUSINESS_LOGIC_ERROR);
        }

    }

    public DBSHBaseRsp<BetRspParams> activityRebate(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("activityPayout : DB真人 - {}", reqVO);
        ActivityRebateVO params = JSON.parseObject(reqVO.getParams(), ActivityRebateVO.class);

        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = getByUserId(userId);
        String transferNo = String.valueOf(reqVO.getTransferNo());

        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecordForPush(userInfoVO, transferNo);
        if (!userCoinRecordVOS.isEmpty()) {
            BetRspParams rspParams = new BetRspParams();
            rspParams.setMerchantCode(reqVO.getMerchantCode());
            String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
            return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
        }
        BigDecimal amount = params.getRewardAmount();
        String remark = params.getActivityName();
        CoinRecordResultVO coinRecordResultVO = this.handleSettleCoin(userInfoVO, transferNo, amount, remark);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BetRspParams rspParams = new BetRspParams();
            rspParams.setMerchantCode(reqVO.getMerchantCode());
            String sign = buildSign(JSONObject.toJSONString(rspParams), reqVO.getMerchantCode());
            return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspParams);
        } else {
            log.info("DB真人 tip : 结算 : reqData -> " + params);
            return DBSHBaseRsp.failed(SHErrorEnum.SYSTEM_ERROR);
        }

    }

    /**
     * 订单校验 支出? 收入?
     */
    public List<UserCoinRecordVO> checkCoinRecord(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord!= null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return Lists.newArrayList();
    }

    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String orderId, boolean expenses) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord  != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return Lists.newArrayList();
    }


    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DBSH.getVenueCode());
        userCoinAddVO.setThirdOrderNo(orderNo);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleBetCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal tradeAmount, String tradeId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(tradeId);
        userCoinAddVO.setThirdOrderNo(tradeId);
        userCoinAddVO.setVenueCode(VenuePlatformConstants.DBSH);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleCancelBetCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark, boolean isAdd) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(isAdd ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DBSH.getVenueCode());
        userCoinAddVO.setThirdOrderNo(orderNo);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    public List<UserCoinRecordVO> checkExistCoinRecordForPush(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return Lists.newArrayList();
    }



    /**
     * 重派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO reSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark, boolean isAdd) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(isAdd ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DBSH.getVenueCode());
        userCoinAddVO.setThirdOrderNo(orderNo);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
    }



    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String apiUrl = venueDetailVO.getApiUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getBetKey();
        String agent = venueDetailVO.getMerchantNo();

        String url = apiUrl + "/api/merchant/create/v2";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loginName", agent + casinoMemberVO.getVenueUserAccount());
        dataMap.put("loginPassword", casinoMemberVO.getCasinoPassword());
        dataMap.put("lang", 1);
        dataMap.put("timestamp", System.currentTimeMillis());
        String json = JSONObject.toJSONString(dataMap);
        log.info("DBSHServiceImpl.注册加密前参数 : " + json);

        String signature = MD5Util.MD5Encode(json + iv);
        String body = AESUtil.encrypt(json, key);

        Map<String, String> head = Maps.newHashMap();

        Map<String, String> map = new HashMap<>();
        map.put("merchantCode", agent);
        map.put("signature", signature);
        map.put("params", body);

        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(map));
        log.info("createMember." +rsp);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            String code = jsonObject.getString("code");
            if (code.equals(SUCCESS_CODE) || code.equals(REGISTER_SUCCESS_CODE) ) {
                return ResponseVO.success(true);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String domainAddr = "";
        ResponseVO<Page<DomainVO>> domainPage = domainInfoApi.queryDomainPage(DomainRequestVO.builder().siteCode(casinoMemberVO.getSiteCode()).build());
        if (domainPage.isOk()) {
            List<DomainVO> domainVOS = domainPage.getData().getRecords();
            DomainVO domainVO = domainVOS.stream().filter(domain -> domain.getDomainType() == 2).findAny().orElse(null);
            if (domainVO != null) {
                String URL_PREFIX = "https://";
                domainAddr = URL_PREFIX + domainVO.getDomainAddr();
            }
            if (StringUtils.isEmpty(domainAddr)) {
                throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
            }
        }else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }
        String apiUrl = venueDetailVO.getApiUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getBetKey();
        String agent = venueDetailVO.getMerchantNo();


        String url = apiUrl + "/api/merchant/forwardGame/v2";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loginName", agent + casinoMemberVO.getVenueUserAccount());
        dataMap.put("loginPassword", casinoMemberVO.getCasinoPassword());
        dataMap.put("deviceType", getDeviceType());
        dataMap.put("lang", 1);
        dataMap.put("backurl", domainAddr);
        dataMap.put("showExit", 0);
        dataMap.put("timestamp", System.currentTimeMillis());
        dataMap.put("playerLanguageV2", DBSHLangEnum.conversionLang(CurrReqUtils.getLanguage()));

        dataMap.put("ip", loginVO.getIp());
        String json = JSONObject.toJSONString(dataMap);
        log.info("DBSHServiceImpl.加密前参数 : " + json);

        String signature = MD5Util.MD5Encode(json + iv);
        String body = AESUtil.encrypt(json, key);

        Map<String, String> head = Maps.newHashMap();

        Map<String, String> map = new HashMap<>();
        map.put("merchantCode", agent);
        map.put("signature", signature);
        map.put("params", body);
        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(map));
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getString("code").equals(SUCCESS_CODE) ) {
                String source = jsonObject.getJSONObject("data").getString("url");
                String userAccount = loginVO.getUserAccount();
                String venueCode = venueDetailVO.getVenueCode();
                GameLoginVo gameLoginVo = GameLoginVo.builder()
                        .source(source)
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(userAccount)
                        .venueCode(venueCode).build();
                return ResponseVO.success(gameLoginVo);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

    }

    public int getDeviceType() {
        Integer reqDeviceType = CurrReqUtils.getReqDeviceType();
        if (reqDeviceType == null) {
            return 0;
        }
        if (reqDeviceType == 1) {
            return 1;
        } else {
            return 3;
        }
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        log.info("400006-DB真人拉单入参: {}", venueDetailVO.getVenueCode());
        String apiUrl = venueDetailVO.getGameUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getBetKey();

        String agent = venueDetailVO.getMerchantNo();
        long timestamp = System.currentTimeMillis();



        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String url = apiUrl + "/data/merchant/betHistoryRecord/v1";
        Long startTime = venuePullParamVO.getStartTime();
        Long endTime = venuePullParamVO.getEndTime();
        Map<String, Object> dataMap = new HashMap<>();

        int pageNum = 1;
        int pageSize = 1000;
        while (true) {
            dataMap.put("startTime", fromTimestamp(startTime));
            dataMap.put("endTime", fromTimestamp(endTime));
            dataMap.put("pageIndex", pageNum);
            dataMap.put("timestamp", timestamp);
            String json = JSONObject.toJSONString(dataMap);
            log.info("400006-DB真人拉单参数: {}", json);

            Map<String, String> head = Maps.newHashMap();
            head.put("merchantCode", agent);
            head.put("pageIndex", String.valueOf(pageNum));

            String signature = MD5Util.MD5Encode(json + iv);
            String body = AESUtil.encrypt(json, key);

            Map<String, String> map = new HashMap<>();
            map.put("merchantCode", agent);
            map.put("signature", signature);
            map.put("params", body);

            String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(map));
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject == null || !jsonObject.getString("code").equals(SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueDetailVO.getVenueCode(), rsp, json, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("400006-result: {} 请求参数 : {}", rsp,json);
            JSONObject data = jsonObject.getJSONObject("data");
            if(data.getJSONArray("record").isEmpty()){
                break;
            }
            List<SHOrderRecordVO> records = data.getJSONArray("record").toJavaList(SHOrderRecordVO.class);
            try {
                handleRemoteOrder(records, venueDetailVO);
            } catch (Exception e) {
                log.info("400006-fail : ",e);
            }
            if(records.size()<pageSize){
                break;
            }
            pageNum++;
        }


        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<SHOrderRecordVO> orderList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = orderList.stream().map(order -> adaptThirdAccount(order.getPlayerName())).distinct().toList();


        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DBSH.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DBSH.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (SHOrderRecordVO order : orderList) {

            UserInfoVO userInfoVO = userMap.get(adaptThirdAccount(order.getPlayerName()));
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), order.getPlayerName());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setVenueType(VenueEnum.DBSH.getType().getCode());
            list.add(recordVO);
        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, SHOrderRecordVO order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(order.getBetAmount());
        recordVO.setBetIp(order.getLoginIp());
        recordVO.setBetTime(order.getCreatedAt());
        recordVO.setVenuePlatform(order.getPlatformName());
        recordVO.setVenueCode(VenueEnum.DBSH.getVenueCode());
        recordVO.setCasinoUserName(order.getPlayerName());
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        if (userLoginInfoVO != null) {
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(order.getTableCode());
        recordVO.setRoomTypeName(order.getTableName());

        recordVO.setGameNo(order.getRoundNo());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(String.valueOf(order.getId()));

        recordVO.setBetAmount(order.getBetAmount());
        recordVO.setValidAmount(order.getValidBetAmount());
        recordVO.setWinLossAmount(order.getNetAmount());
        recordVO.setPayoutAmount(order.getPayAmount());
        recordVO.setOdds(order.getOdds());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        Integer orderStatus = getLocalOrderStatus(order.getBetStatus(),order.getBetFlag());
        recordVO.setOrderStatus(orderStatus);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));

        recordVO.setSettleTime(order.getNetAt());

        recordVO.setReSettleTime(order.getRecalcuAt()==0L?null:order.getRecalcuAt());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setDeskNo(order.getTableCode());
        recordVO.setBootNo(order.getBootNo());
        recordVO.setResultList(order.getJudgeResult());
        recordVO.setPlayType(String.valueOf(order.getBetPointId()));
        recordVO.setBetContent(recordVO.getOrderInfo());
        recordVO.setGameName(order.getGameTypeName());
        recordVO.setThirdGameCode(String.valueOf(order.getGameTypeId()));
        recordVO.setParlayInfo(JSONObject.toJSONString(order));

        if (Objects.equals(String.valueOf(order.getGameTypeId()), DBSHGameTypeEnum.GAME_2009.getCode())){
            BigDecimal betAmount = order.getBetAmount();
            BigDecimal preDebitAmount = order.getPreDebitAmount();
            recordVO.setBetAmount(betAmount.add(preDebitAmount));
        }

        if (Objects.equals(String.valueOf(order.getGameTypeId()), DBSHGameTypeEnum.GAME_2034.getCode())){
            BigDecimal betAmount = order.getBetAmount();
            BigDecimal actualHandingFee = order.getActualHandingFee();
            BigDecimal netAmount = order.getNetAmount();
            recordVO.setWinLossAmount(netAmount.subtract(actualHandingFee));
            recordVO.setBetAmount(betAmount.add(actualHandingFee));
        }
        return recordVO;
    }


    public Integer getLocalOrderStatus(int betStatus,int betFlag) {
        //0=未结算 1=已结算2=取消投注
       if (betStatus == 0) {
           return OrderStatusEnum.NOT_SETTLE.getCode();
       } else if (betStatus==1) {
           if (betFlag == 4) {
               return OrderStatusEnum.RESETTLED.getCode();
           }
           if (betFlag == 1) {
               return OrderStatusEnum.SKIP_ISSUE.getCode();
           }else {
               return OrderStatusEnum.SETTLED.getCode();
           }
       }else if (betStatus==2){
           return OrderStatusEnum.CANCEL.getCode();
       }
       return -1;
    }



    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll(".*_(\\d+)", "$1");
    }




    private static String buildSign(String agentId, long timestamp, String apiKey) {
        String md5Key = agentId + timestamp + apiKey;
        String source = MD5Util.MD5Encode(md5Key);
        return Md5StringUtils.mix(source);
    }

    public static String fromTimestamp(long timestamp) {
        ZoneId ZONE_UTC8 = ZoneId.of("Asia/Shanghai");
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZONE_UTC8)
                .toLocalDateTime();
        return localDateTime.format(FORMATTER);
    }

    public static void main(String[] args) {
//        queryGameList();
        createTest();
    }



    public static void loginTest() {
        String agent = "YACYMA";
        String key = "ZyGavLeooHgsrCYl";
        String iv = "1L3rSu061tQH8Gi4";
        long timestamp = Instant.now().getEpochSecond();


        /**
         *
         api基础接口: https://api.obothapi.com
         api数据接口: https://api-data.obothapi.com
         */

        String url = "https://api.obothapi.com" + "/api/merchant/fastGame/v2";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loginName", agent + "Utest_18642916");
        dataMap.put("loginPassword", "322fc6fae6033e81c7475bcac09cc314");
        dataMap.put("deviceType", 3);
        dataMap.put("lang", 1);
        dataMap.put("backurl", "https://www.baidu.com");
        dataMap.put("showExit", 0);
        dataMap.put("timestamp", System.currentTimeMillis());

        dataMap.put("ip", "103.20.81.116");
        String json = JSONObject.toJSONString(dataMap);
        System.out.println("DBSHServiceImpl.加密前参数 : " + json);

        String signature = MD5Util.MD5Encode(json + iv);
        String body = AESUtil.encrypt(json, key);

        Map<String, String> head = Maps.newHashMap();

        Map<String, String> map = new HashMap<>();
        map.put("merchantCode", agent);
        map.put("signature", signature);
        map.put("params", body);

        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(map));
        JSONObject jsonObject = JSONObject.parseObject(rsp);


        System.out.println("DG2ServiceImpl.loginTest ---- url : " + jsonObject);
    }


    public static void createTest(){
        String agent = "YACYMA";
        String key = "ZyGavLeooHgsrCYl";
        String iv = "1L3rSu061tQH8Gi4";
        long timestamp = System.currentTimeMillis();


        /**
         *
         api基础接口: https://api.obothapi.com
         api数据接口: https://api-data.obothapi.com
         */

        String url = "https://api.obothapi.com" + "/api/merchant/create/v2";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("loginName", agent + "Utest_18642916");
        dataMap.put("loginPassword", "322fc6fae6033e81c7475bcac09cc314");
//        dataMap.put("deviceType", 3);
        dataMap.put("lang", 1);
//        dataMap.put("backurl", "https://www.baidu.com");
//        dataMap.put("showExit", 0);
        dataMap.put("timestamp", System.currentTimeMillis());

//        dataMap.put("ip", "103.20.81.116");
        String json = JSONObject.toJSONString(dataMap);
        System.out.println("DBSHServiceImpl.加密前参数 : " + json);

        String signature = MD5Util.MD5Encode(json + iv);
        String body = AESUtil.encrypt(json, key);

        Map<String, String> head = Maps.newHashMap();

        Map<String, String> map = new HashMap<>();
        map.put("merchantCode", agent);
        map.put("signature", signature);
        map.put("params", body);

        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(map));
        JSONObject jsonObject = JSONObject.parseObject(rsp);


        System.out.println("DG2ServiceImpl.loginTest ---- url : " + jsonObject);

    }

}
