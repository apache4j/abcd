package com.cloud.baowang.play.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.SexyActionEnum;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoValidRequestVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.service.sexy.SexyOrderService;
import com.cloud.baowang.play.wallet.vo.req.sexy.SexyBetRequest;
import com.cloud.baowang.play.wallet.vo.res.sexy.SexyBaseRsp;
import com.cloud.baowang.play.wallet.vo.sexy.SexyActionVo;
import com.cloud.baowang.play.wallet.vo.sexy.enums.SEXYCurrencyEnum;
import com.cloud.baowang.play.wallet.vo.sexy.enums.SexyErrorEnum;
import com.cloud.baowang.play.wallet.vo.sexy.enums.SexyOrderType;
import com.cloud.baowang.play.wallet.vo.sexy.enums.VoidTypeEnum;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class SexyGameApiExtendImpl extends BaseService {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountApi venueUserAccountApi;

    private final GameInfoApi gameInfoApi;

    private final SexyOrderService sexyOrderService;

    public String adaptUserAccount(String source) {
        return source.replaceAll("\\D+", "");
    }


    public SexyBaseRsp getBalance(JSONObject reqData) {
        String thirdUserId = reqData.getString("userId");
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//
//        casinoMember.setVenueUserAccount(adaptUserAccount(thirdUserId));
//        casinoMember.setVenueCode(VenueEnum.SEXY.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
//        }
        String userId = adaptUserAccount(thirdUserId);
//        if (userId == null) {
//            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
//        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.SEXY.getVenueCode());
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_LOCK);
        }

        UserCoinWalletVO userCenterCoin = userCoinApi.getUserActualBalance(UserCoinQueryVO.builder().userId(userId).build());
        if (userCenterCoin.getTotalAmount() == null ) {
            return SexyBaseRsp.failed(SexyErrorEnum.NOT_ENOUGH_BALANCE,BigDecimal.ZERO);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        return SexyBaseRsp.success(totalAmount, balanceTs);
    }

    public SexyBaseRsp checkRequestValid(UserInfoVO userInfoVO, List<SexyBetRequest> req, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("SEXYGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.SEXY.getVenueName());
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
        }

        if (isBetting) {
            String userCurrencyCode = userInfoVO.getMainCurrency();
            String SEXYCode = SEXYCurrencyEnum.enumOfCode(userCurrencyCode).getSexyCode();
            String currency = req.get(0).getCurrency();
            String gameCode = req.get(0).getGameCode();

            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                    .venueCode(VenueEnum.SEXY.getVenueCode())
                    .currencyCode(userInfoVO.getMainCurrency()).build();
            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.SEXY.getVenueCode());
                return SexyBaseRsp.failed(SexyErrorEnum.INVALID_GAME);
            }

            if (!isGameAvailable(userInfoVO.getSiteCode(), gameCode, VenueEnum.SEXY.getVenueCode(), userCurrencyCode)) {
                log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), gameCode);
                return SexyBaseRsp.failed(SexyErrorEnum.INVALID_GAME);
            }

            if (!SEXYCode.equals(currency)) {
                log.info("币种不支持 : {} : 用户 : {}", userCurrencyCode, userInfoVO.getUserId());
                return SexyBaseRsp.failed(SexyErrorEnum.INVALID_GAME);
            }
            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.SEXY.getVenueCode());
                return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_LOCK);
            }

        }
        return null;
    }

    /**
     * 游戏开启/关闭校验
     */
    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode, String currencyCode) {
        // 判断游戏是否配置
        GameInfoValidRequestVO requestVO = GameInfoValidRequestVO.builder().siteCode(siteCode).gameId(gameCode).venueCode(venueCode).build();
        ResponseVO<GameInfoVO> responseVO = gameInfoApi.getGameInfoByCode(requestVO);
        if (!responseVO.isOk()) {
            return false;
        }
        GameInfoVO gameInfo = responseVO.getData();
        if (gameInfo == null) {
            log.error("场馆:{} 没有配置游戏，游戏：{}", venueCode, gameCode);
            return false;
        }
        // 判断游戏是否开启
        if (!Objects.equals(gameInfo.getStatus(), StatusEnum.OPEN.getCode())) {
            log.error("场馆:{} 游戏关闭，游戏：{}", venueCode, gameCode);
            return false;
        }
        //币种
        List<String> currencyList = Arrays.asList(gameInfo.getCurrencyCode().split(CommonConstant.COMMA));
        if (!currencyList.contains(currencyCode)) {
            log.error("场馆:{} 游戏：{} 币种不支持 : {}", venueCode, gameCode, currencyCode);
            return false;
        }
        return true;
    }

    public String userCheck(List<SexyBetRequest> reqData) {
        String userAccount = reqData.get(0).getUserId();
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//        casinoMember.setVenueUserAccount(adaptUserAccount(userAccount));
//        casinoMember.setVenueCode(VenueEnum.SEXY.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return null;
//        }
        return adaptUserAccount(userAccount);
    }

    public String userCheckSettle(SexyBetRequest reqData) {
        String userAccount = reqData.getUserId();
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//        casinoMember.setVenueUserAccount(adaptUserAccount(userAccount));
//        casinoMember.setVenueCode(VenueEnum.SEXY.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return null;
//        }
        return adaptUserAccount(userAccount);
    }


    /**
     * 下注
     */
    public SexyBaseRsp bet(JSONObject reqData) {
        log.info("sexy bet : " + reqData.toString());
        String txnsJson = reqData.getString("txns");

        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, true);
        if (isValid != null) {
            return isValid;
        }

        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
            List<String> cancelBetList = RedisUtil.getValue(key);
            if (cancelBetList != null && !cancelBetList.isEmpty()) {
                if (cancelBetList.contains(orderId)) {
                    UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                    String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                    return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
                }
            }
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("sexy voidBet error 账单已经存在: " + betInfo);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.DUPLICATE_TRANSACTION);
        }

        for (SexyBetRequest betInfo : pendingOrders) {
            String orderId = betInfo.getPlatformTxId();
            String roundId = betInfo.getRoundId();
            //检查余额
            BigDecimal betAmount = betInfo.getBetAmount();
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());

            if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(betAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.SEXY.getVenueCode(), betInfo, userCoin, betAmount);
                return SexyBaseRsp.failed(SexyErrorEnum.NOT_ENOUGH_BALANCE);
            }

            //账变
            CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, betAmount, roundId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("sexy bet error 账变失败:  {} ", betInfo);
            }

        }
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.BET);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);

    }


    /**
     * 取消下注
     */
    public SexyBaseRsp cancelBet(JSONObject reqData) {
//        log.info("sexy cancelBet : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        if (userId == null) {
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            if (existOrders.isEmpty()) {
                log.info("sexy cancelBet error 账单不存在: " + betInfo);
                String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
                List<String> cancelBetList = RedisUtil.getValue(key);
                if (cancelBetList == null || cancelBetList.isEmpty()) {
                    cancelBetList = Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                log.info("sexy cancelBet error 账单已经取消: " + betInfo);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO unCancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (unCancelOrder != null) {
                //未取消
                betInfo.setBetAmount(unCancelOrder.getCoinValue());
                pendingOrders.add(betInfo);
            }
        }

        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }

        for (SexyBetRequest betInfo : pendingOrders) {
            String orderId = betInfo.getPlatformTxId();
            String roundId = betInfo.getRoundId();
            BigDecimal betAmount = betInfo.getBetAmount();
            CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, betAmount, roundId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("sexy cancelBet error 取消账变失败:  {} ", betInfo);
            }
        }
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.CANCEL_BET);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
    }


    /**
     * 交易作废(已下注 -> 无效注单)
     */
    private SexyBaseRsp voidBet(JSONObject reqData) {
//        log.info("sexy voidBet : " + reqData.toString());
        String txnsJson = reqData.getString("txns");

        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, false);
            if (isValid != null) {
                return isValid;
            }
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder == null) {
                log.info("sexy voidBet error 已经下注账单不存在: " + betInfo);
                String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
                List<String> cancelBetList = RedisUtil.getValue(key);
                if (cancelBetList == null || cancelBetList.isEmpty()) {
                    cancelBetList = Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                log.info("sexy voidBet error 账单已取消-无效已经存在: " + betInfo);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            betInfo.setUserInfo(userInfoVO);
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }

        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserInfo().getUserId()));
        settleMap.forEach((userId, orders) -> {
            orders.stream()
                    .filter(order -> order.getBetAmount().compareTo(BigDecimal.ZERO) != 0)
                    .forEach(order -> {
                        log.info("sexy settle : userId - " + userId + " order : " + order);
                        String orderId = order.getPlatformTxId();
                        String roundId = order.getRoundId();
                        UserInfoVO userInfoVO = order.getUserInfo();
                        BigDecimal betAmount = order.getBetAmount();

                        CoinRecordResultVO coinRecordResultVO = this.handleVoidBet(userInfoVO, orderId, betAmount, roundId);
                        if (coinRecordResultVO == null
                                || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                            log.info("sexy settle error 投注调整账变失败:  {}", order);
                        }
                    });

            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });
        return SexyBaseRsp.success();
    }


    /**
     * 派彩
     */
    public SexyBaseRsp settle(JSONObject reqData) {
        log.info("sexy settle : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, false);
            if (isValid != null) {
                return isValid;
            }
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode()) ).findAny().orElse(null);
            if (betOrder == null) {
                log.info("sexy settle error 账单不存在: " + betInfo);
                continue;
//                return SexyBaseRsp.failed(SexyErrorEnum.TRANSACTION_NOT_FOUND);
            }
            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                //已经处理过,还会出现? TODO
                continue;
            }
            betInfo.setUserInfo(userInfoVO);
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }
        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserInfo().getUserId()));
        settleMap.forEach((userId, orders) -> {
            orders.stream()
                    .filter(order -> order.getWinAmount().compareTo(BigDecimal.ZERO) != 0)
                    .forEach(order -> {
                        log.info("sexy settle : userId - " + userId + " order : " + order);
                        String orderId = order.getPlatformTxId();
                        String roundId = order.getRoundId();
                        UserInfoVO userInfoVO = order.getUserInfo();
                        BigDecimal winAmount = order.getWinAmount();

                        CoinRecordResultVO coinRecordResultVO = this.handleSettle(userInfoVO, orderId, winAmount, roundId);
                        if (coinRecordResultVO == null
                                || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                            log.info("sexy settle error 投注调整账变失败:  {}", order);
                        }
                    });

            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });
        return SexyBaseRsp.success();

    }


    /**
     * 结账单转为无效(派彩取消/下注取消)
     */
    private SexyBaseRsp voidSettle(JSONObject reqData) {
//        log.info("sexy voidSettle : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });


        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        List<UserCoinRecordVO> pendingCoinOrder = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheck(txns);
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, false);
            if (isValid != null) {
                return isValid;
            }
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecordForVoidSettle(userInfoVO, orderId);

            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                //已经处理过,还会出现? TODO
                log.info(" voidSettle : 已存在过voidSettle订单 : " + adjustOrder);
                continue;
            }
            UserCoinRecordVO payoutOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            betInfo.setWinAmount(payoutOrder != null ? payoutOrder.getCoinValue() : BigDecimal.ZERO);
            betInfo.setUserInfo(userInfoVO);
            pendingOrders.add(betInfo);
            pendingCoinOrder.addAll(existOrders);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.success();
        }

        Map<String, List<SexyBetRequest>> voidSettleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserInfo().getUserId()));

        voidSettleMap.forEach((userId, orders) -> {
            orders.forEach(order -> {
                String orderId = order.getPlatformTxId();
                UserInfoVO userInfoVO = order.getUserInfo();
                String remark = VoidTypeEnum.fromCode(order.getVoidType()).getDescription();
                UserCoinRecordVO PayoutOrder = pendingCoinOrder.stream().filter(coin -> coin.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()) && orderId.equals(coin.getOrderNo()) ).findAny().orElse(null);
                log.info(" voidSettle 取消派彩 : " + PayoutOrder);
                if (PayoutOrder != null) {
                    BigDecimal winAmount = PayoutOrder.getCoinValue();
                    CoinRecordResultVO voidSettle = this.handleVoidSettle(userInfoVO, orderId, winAmount, remark);
                    if (voidSettle == null || !UpdateBalanceStatusEnums.SUCCESS.equals(voidSettle.getResultStatus())) {
                        log.info("sexy voidSettle error 取消下注账变失败:  {} ", order);
                    }
                }
                UserCoinRecordVO betOrder = pendingCoinOrder.stream().filter(coin -> coin.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode()) && orderId.equals(coin.getOrderNo())).findAny().orElse(null);
                log.info(" voidSettle 取消下注 : " + betOrder);
                if (betOrder != null) {
                    BigDecimal betAmount = order.getBetAmount();
                    CoinRecordResultVO cancelBet = this.handleCancelBet(userInfoVO, orderId, betAmount, remark);
                    if (cancelBet == null || !UpdateBalanceStatusEnums.SUCCESS.equals(cancelBet.getResultStatus())) {
                        log.info("sexy voidSettle error 取消派彩账变失败:  {} ", order);
                    }
                }
            });

            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });
        return SexyBaseRsp.success();
    }


    /**
     * 重新结账派彩
     */
    private SexyBaseRsp reSettle(JSONObject reqData) {
//        log.info("sexy voidSettle : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        List<UserCoinRecordVO> coinOrder = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, false);
            if (isValid != null) {
                return isValid;
            }
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, false);
            UserCoinRecordVO payoutOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (payoutOrder == null) {
                log.info("sexy reSettle 无派彩账变 : " + betInfo);
                //continue;
            } else {
                coinOrder.add(payoutOrder);
            }
            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                //已经处理过,还会出现? TODO
                continue;
            }
            betInfo.setUserInfo(userInfoVO);
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.success();
        }

        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserInfo().getUserId()));

        settleMap.forEach((userId, orders) -> {
            orders.forEach(order -> {
                log.info("sexy reSettle : userId - " + userId + " order : " + order);
                String orderId = order.getPlatformTxId();
                String roundId = order.getRoundId();
                UserInfoVO userInfoVO = order.getUserInfo();
                BigDecimal winAmount = order.getWinAmount();

                UserCoinRecordVO PayoutOrder = coinOrder.stream().filter(coin -> coin.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()) && orderId.equals(coin.getOrderNo())).findAny().orElse(null);
                if (PayoutOrder != null) {
                    BigDecimal payoutAmount = PayoutOrder.getCoinValue();
                    CoinRecordResultVO voidSettle = this.handleVoidSettle(userInfoVO, orderId, payoutAmount, roundId);
                    if (voidSettle == null || !UpdateBalanceStatusEnums.SUCCESS.equals(voidSettle.getResultStatus())) {
                        log.info("sexy reSettle error 重派彩-取消派彩失败:  {} ", order);
                    }
                }
                //TODO

                CoinRecordResultVO coinRecordResultVO = this.reSettle(userInfoVO, orderId, winAmount, roundId);
                if (coinRecordResultVO == null
                        || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                    log.info("sexy reSettle error 重派彩账变失败:  {}", order);
                }
            });

            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });

        return SexyBaseRsp.success();
    }


    /**
     * 打赏
     */
    private SexyBaseRsp tip(JSONObject reqData) {
//        log.info("sexy tip : " + reqData.toString());
        String txnsJson = reqData.getString("txns");

        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, true);
        if (isValid != null) {
            return isValid;
        }

        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
            List<String> cancelBetList = RedisUtil.getValue(key);
            if (cancelBetList != null && !cancelBetList.isEmpty()) {
                if (cancelBetList.contains(orderId)) {
                    UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                    String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                    return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
                }
            }
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("sexy voidBet error 账单已经存在: " + betInfo);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);

            }
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }

        for (SexyBetRequest betInfo : pendingOrders) {
            String orderId = betInfo.getPlatformTxId();
            String roundId = betInfo.getRoundId();
            //检查余额
            BigDecimal betAmount = betInfo.getTip();
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());

            if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(betAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.error("{} 打赏失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.SEXY.getVenueCode(), betInfo, userCoin, betAmount);
                return SexyBaseRsp.failed(SexyErrorEnum.NOT_ENOUGH_BALANCE);
            }

            //账变
            CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, betAmount, roundId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("sexy bet error 账变失败:  {} ", betInfo);
            }

        }
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.TIP);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
    }


    /**
     * 取消打赏
     */
    private SexyBaseRsp cancelTip(JSONObject reqData) {
//        log.info("sexy cancelTip : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        if (userId == null) {
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        List<SexyBetRequest> pendingOrders = Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            if (existOrders.isEmpty()) {
                String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
                List<String> cancelBetList = RedisUtil.getValue(key);
                if (cancelBetList == null || cancelBetList.isEmpty()) {
                    cancelBetList = Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO unCancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (unCancelOrder != null) {
                //未取消
                betInfo.setTip(unCancelOrder.getCoinValue());
                pendingOrders.add(betInfo);
            }
        }

        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }

        for (SexyBetRequest betInfo : pendingOrders) {
            String orderId = betInfo.getPlatformTxId();
            String roundId = betInfo.getRoundId();
            BigDecimal betAmount = betInfo.getTip();
            CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, betAmount, roundId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("sexy cancelBet error 取消打赏账变失败:  {} ", betInfo);
            }
        }
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.CANCEL_TIP);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
    }

    /**
     * 订单校验 支出? 收入?
     */
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

    /**
     * 订单校验 支出? 收入?
     */
    public List<UserCoinRecordVO> checkExistCoinRecordForVoidSettle(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }


    /**
     * 三方行动
     */
    public SexyBaseRsp doAction(SexyActionVo actionVo) {
        String message = actionVo.getMessage();
        JSONObject reqData = JSON.parseObject(message);
//        log.info("Sexy doAction : " + reqData);
        String actionType = reqData.getString("action");
        SexyActionEnum actionEnum = SexyActionEnum.parseActionType(actionType);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SexyBaseRsp response = switch (actionEnum) {
            case GET_BALANCE -> getBalance(reqData);
            case PLACE_BET -> bet(reqData);
            case CANCEL_BET -> cancelBet(reqData);
            case VOID_BET -> voidBet(reqData);
            case SETTLE -> settle(reqData);
            case VOID_SETTLE -> voidSettle(reqData);
            case RE_SETTLE -> reSettle(reqData);
            case TIP -> tip(reqData);
            case CANCEL_TIP -> cancelTip(reqData);
            default -> SexyBaseRsp.builder().status(SexyErrorEnum.FAIL.getCode()).build();
        };
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        if (totalTimeMillis >= 3000) {
            log.info("SEXY-Action 请求: {} 返回: {} 耗时: {}ms", reqData, response, totalTimeMillis);
        }
        return response;

    }


    /**
     * 处理取消下注
     */
    protected CoinRecordResultVO handleCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    /**
     * 派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
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
        userCoinAddVOPayout.setVenueCode(VenueEnum.SEXY.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    /**
     * 下注
     */
    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }


    /**
     * - 下注取消
     */
    protected CoinRecordResultVO handleVoidBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }


    /**
     * 取消派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleVoidSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.SEXY.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }


    /**
     * 重派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO reSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.SEXY.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }


}
