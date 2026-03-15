package com.cloud.baowang.play.wallet.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.AceltErrorEnum;
import com.cloud.baowang.play.wallet.enums.DGErrorEnum;
import com.cloud.baowang.play.wallet.service.DBSHGameApi;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferData;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.wallet.vo.req.db.config.DBCryptoConfig;
import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.AceltTradeInfo;
import com.cloud.baowang.play.wallet.vo.req.db.sh.enums.SHErrorEnum;
import com.cloud.baowang.play.wallet.vo.req.db.sh.enums.SettleTypeEnum;
import com.cloud.baowang.play.wallet.vo.req.db.sh.enums.TransferTypeEnum;
import com.cloud.baowang.play.wallet.vo.req.db.sh.vo.*;
import com.cloud.baowang.play.wallet.vo.req.sexy.SexyBetRequest;
import com.cloud.baowang.play.wallet.vo.res.db.acelt.DBAceltBaseRsp;
import com.cloud.baowang.play.wallet.vo.res.db.sh.DBSHBaseRsp;
import com.cloud.baowang.play.wallet.vo.res.dg.DGBaseRsp;
import com.cloud.baowang.play.wallet.vo.res.sexy.SexyBaseRsp;
import com.cloud.baowang.play.wallet.vo.sexy.enums.SexyErrorEnum;
import com.cloud.baowang.play.wallet.vo.sexy.enums.SexyOrderType;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DBSHGameApiImpl extends BaseService implements DBSHGameApi {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private DBCryptoConfig cryptoConfig;


    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll(".*_(\\d+)", "$1");
    }

    public DBSHBaseRsp checkRequestValid(UserInfoVO userInfoVO) {
        if (userInfoVO == null) {
            return null;
        }
        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                .venueCode(VenueEnum.DBSH.getVenueCode())
                .currencyCode(userInfoVO.getMainCurrency()).build();
        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
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
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(
                UserCoinQueryVO.builder().userId(userId).build()
        );

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


    @Override
    public DBSHBaseRsp<SHOrderRspData> getBalance(SHBalanceQueryVO reqVO) {
        log.info("getBalance: DB真人 " + reqVO.toString());
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        SHUserInfo shUserInfo = JSON.parseObject(reqVO.getParams(), SHUserInfo.class);
        String thirdUserId = shUserInfo.getLoginName();
        String userId = adaptThirdAccount(thirdUserId);

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBSH.getVenueCode());
            return DBSHBaseRsp.failed(SHErrorEnum.MEMBER_NOT_EXIST);
        }

        UserCoinWalletVO userCenterCoin = userCoinApi.getUserActualBalance(UserCoinQueryVO.builder().userId(userId).build());
        if (userCenterCoin.getTotalAmount() == null) {
            return DBSHBaseRsp.failed(SHErrorEnum.SERVICE_UNAVAILABLE);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        SHOrderRspData rspData = SHOrderRspData.builder().loginName(thirdUserId).balance(totalAmount).build();
        String sign = buildSign(JSONObject.toJSONString(rspData), reqVO.getMerchantCode());
        return DBSHBaseRsp.success(Objects.requireNonNull(sign).toUpperCase(Locale.ROOT), rspData);
    }

    @Override
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

    @Override
    public DBSHBaseRsp<BetRspParams> betConfirm(BetRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        BetParams params = JSON.parseObject(reqVO.getParams(), BetParams.class);
        List<BetInfo> betInfos = params.getBetInfo();
        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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

    @Override
    public DBSHBaseRsp<BetRspParams> betCancel(DBSHRequestVO  reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        CancelBetParams cancelBetParams = JSON.parseObject(reqVO.getParams(), CancelBetParams.class);
        String loginName = cancelBetParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

        Map<String, BigDecimal> betPayoutMap = cancelBetParams.getBetPayoutMap();
        String transferNo = String.valueOf(reqVO.getTransferNo());
        List<BetInfo> pendingOrders = Lists.newArrayList();
        if (betPayoutMap == null) {
            //全部取消
            List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, transferNo);
            UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelBet != null) {
                log.info("DB真人 cancelBet 已经存在订单" + cancelBet);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());

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
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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

    @Override
    public DBSHBaseRsp<BetRspParams> gamePayout(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("betConfirm : DB真人 - {}", reqVO);
        SettleParams settleParams = JSON.parseObject(reqVO.getParams(), SettleParams.class);
        String loginName = settleParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

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

    @Override
    public DBSHBaseRsp<BetRspParams> activityPayout(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("activityPayout : DB真人 - {}", reqVO);
        ActivityPayoutVO activityParams = JSON.parseObject(reqVO.getParams(), ActivityPayoutVO.class);
        String loginName = activityParams.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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

    @Override
    public DBSHBaseRsp<BetRspParams> playerbetting(DBSHRequestVO reqVO) {

        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        PlayerBettingNotifyVO params = JSON.parseObject(reqVO.getParams(), PlayerBettingNotifyVO.class);

        log.info("activityPayout : DB真人 - {}", reqVO);

        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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

    @Override
    public DBSHBaseRsp<BetRspParams> activityRebate(DBSHRequestVO reqVO) {
        if (!checkMD5(reqVO.getParams(), reqVO.getMerchantCode(), reqVO.getSignature())) {
            return DBSHBaseRsp.failed(SHErrorEnum.SIGNATURE_ERROR);
        }
        log.info("activityPayout : DB真人 - {}", reqVO);
        ActivityRebateVO params = JSON.parseObject(reqVO.getParams(), ActivityRebateVO.class);

        String loginName = params.getLoginName();
        String userId = adaptThirdAccount(loginName);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

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
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }


    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBSH.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    protected CoinRecordResultVO handleBetCoin(UserInfoVO userInfoVO, String betId, BigDecimal tradeAmount, String tradeId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(betId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(tradeId);
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
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
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    public List<UserCoinRecordVO> checkExistCoinRecordForPush(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

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
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(isAdd ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBSH.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }





}
