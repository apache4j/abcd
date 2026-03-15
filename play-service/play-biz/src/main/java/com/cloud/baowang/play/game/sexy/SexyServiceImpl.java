package com.cloud.baowang.play.game.sexy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.SexyActionEnum;
import com.cloud.baowang.play.api.enums.SexyGameTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.sexy.enums.SexyErrorEnum;
import com.cloud.baowang.play.api.vo.sexy.enums.SexyOrderType;
import com.cloud.baowang.play.api.vo.sexy.enums.VoidTypeEnum;
import com.cloud.baowang.play.api.vo.sexy.req.SexyActionVo;
import com.cloud.baowang.play.api.vo.sexy.req.SexyBetRequest;
import com.cloud.baowang.play.api.vo.sexy.rsp.SexyBaseRsp;
import com.cloud.baowang.play.game.sexy.service.SexyOrderService;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.card.Card;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.sexy.enums.SEXYCurrencyEnum;
import com.cloud.baowang.play.game.sexy.enums.SEXYLangEnum;
import com.cloud.baowang.play.game.sexy.enums.SexyPlayTypeEnum;
import com.cloud.baowang.play.game.sexy.po.SexyOrderRecordPO;
import com.cloud.baowang.play.game.sexy.po.SexyOrderRecordRepository;
import com.cloud.baowang.play.game.sexy.vo.BetSetting;
import com.cloud.baowang.play.game.sexy.vo.SEXYConstant;
import com.cloud.baowang.play.game.sexy.vo.SexyOrderInfoUtil;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cloud.baowang.play.game.sexy.vo.SEXYConstant.BET_SETTING_MAP;
import static com.cloud.baowang.play.game.sexy.vo.SEXYConstant.BET_SETTING_MAP_PROD;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.SEXY)
public class SexyServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;

    private final GameInfoService gameInfoService;

    private final SexyOrderRecordRepository sexyOrderRepository;

    private final static String SUCCESS_CODE = "0000";

    private final SexyOrderService sexyOrderService;


    private static final String URL = "https://tttint.apihub55.com/";

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private static final String cert = "8FYR9Dip9otbZxlBQRk";
    private static final String agentID = "fy521test";
    private static final String userId = "Utest_57772627";



    public String adaptUserAccount(String source) {
        return source.replaceAll("\\D+", "");
    }


    public SexyBaseRsp getBalance(JSONObject reqData) {
        String thirdUserId = reqData.getString("userId");

        String userId = adaptUserAccount(thirdUserId);
//        if (userId == null) {
//            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
//        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.SEXY.getVenueCode());
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_LOCK);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (userCenterCoin.getTotalAmount() == null ) {
            return SexyBaseRsp.failed(SexyErrorEnum.NOT_ENOUGH_BALANCE, BigDecimal.ZERO);
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


            if (venueMaintainClosed(VenuePlatformConstants.DBCHESS, userInfoVO.getSiteCode())) {
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
//        GameInfoValidRequestVO requestVO = GameInfoValidRequestVO.builder().siteCode(siteCode).gameId(gameCode).venueCode(venueCode).build();
        GameInfoPO gameInfo = getGameInfoByCode(siteCode,gameCode,venueCode);
//        if (!responseVO.isOk()) {
//            return false;
//        }
//        GameInfoVO gameInfo = responseVO.getData();
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
        UserInfoVO userInfoVO = getByUserId(userId);
        SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, true);
        if (isValid != null) {
            return isValid;
        }

        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
            List<String> cancelBetList = RedisUtil.getValue(key);
            if (cancelBetList != null && !cancelBetList.isEmpty()) {
                if (cancelBetList.contains(orderId)) {
                    UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                    String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                    return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
                }
            }
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("sexy voidBet error 账单已经存在: " + betInfo);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);

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
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.BET);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);

    }


    /**
     * 取消下注
     */
    public SexyBaseRsp cancelBet(JSONObject reqData) {
        log.info("sexy cancelBet : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        if (userId == null) {
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            if (existOrders.isEmpty()) {
                log.info("sexy cancelBet error 账单不存在: " + betInfo);
                String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
                List<String> cancelBetList = RedisUtil.getValue(key);
                if (cancelBetList == null || cancelBetList.isEmpty()) {
                    cancelBetList = org.apache.commons.compress.utils.Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                log.info("sexy cancelBet error 账单已经取消: " + betInfo);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.CANCEL_BET);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
    }


    /**
     * 交易作废(已下注 -> 无效注单)
     */
    private SexyBaseRsp voidBet(JSONObject reqData) {
        log.info("sexy voidBet : " + reqData.toString());
        String txnsJson = reqData.getString("txns");

        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = getByUserId(userId);
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
                    cancelBetList = org.apache.commons.compress.utils.Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                log.info("sexy voidBet error 账单已取消-无效已经存在: " + betInfo);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }

        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(SexyBetRequest::getUserId));
        settleMap.forEach((userId, orders) -> {
            orders.stream()
                    .filter(order -> order.getBetAmount().compareTo(BigDecimal.ZERO) != 0)
                    .forEach(order -> {
                        log.info("sexy settle : userId - " + userId + " order : " + order);
                        String orderId = order.getPlatformTxId();
                        String roundId = order.getRoundId();
                        UserInfoVO userInfoVO = getByUserId(userId);
                        BigDecimal betAmount = order.getBetAmount();

                        CoinRecordResultVO coinRecordResultVO = this.handleVoidBet(userInfoVO, orderId, betAmount, roundId);
                        if (coinRecordResultVO == null
                                || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                            log.info("sexy settle error 投注调整账变失败:  {}", order);
                        }
                    });

            UserInfoVO userInfoVO = getByUserId(userId);
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

        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = getByUserId(userId);
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
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.failed(SexyErrorEnum.FAIL);
        }
        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserId()));
        settleMap.forEach((userId, orders) -> {
            orders.stream()
                    .filter(order -> order.getWinAmount().compareTo(BigDecimal.ZERO) != 0)
                    .forEach(order -> {
                        log.info("sexy settle : userId - " + userId + " order : " + order);
                        String orderId = order.getPlatformTxId();
                        String roundId = order.getRoundId();
                        UserInfoVO userInfoVO = getByUserId(userId);
                        BigDecimal winAmount = order.getWinAmount();

                        CoinRecordResultVO coinRecordResultVO = this.handleSettle(userInfoVO, orderId, winAmount, roundId);
                        if (coinRecordResultVO == null
                                || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                            log.info("sexy settle error 投注调整账变失败:  {}", order);
                        }
                    });

            UserInfoVO userInfoVO = getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });
        return SexyBaseRsp.success();

    }


    /**
     * 结账单转为无效(派彩取消/下注取消)
     */
    private SexyBaseRsp voidSettle(JSONObject reqData) {
        log.info("sexy voidSettle : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });


        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        List<UserCoinRecordVO> pendingCoinOrder = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheck(txns);
            UserInfoVO userInfoVO = getByUserId(userId);
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
            pendingOrders.add(betInfo);
            pendingCoinOrder.addAll(existOrders);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.success();
        }

        Map<String, List<SexyBetRequest>> voidSettleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserId()));

        voidSettleMap.forEach((userId, orders) -> {
            orders.forEach(order -> {
                String orderId = order.getPlatformTxId();
                UserInfoVO userInfoVO = getByUserId(userId);
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

            UserInfoVO userInfoVO = getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });
        return SexyBaseRsp.success();
    }


    /**
     * 重新结账派彩
     */
    private SexyBaseRsp reSettle(JSONObject reqData) {
        log.info("sexy voidSettle : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        List<UserCoinRecordVO> coinOrder = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String userId = userCheckSettle(betInfo);
            UserInfoVO userInfoVO = getByUserId(userId);
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
            pendingOrders.add(betInfo);
        }
        if (pendingOrders.isEmpty()) {
            return SexyBaseRsp.success();
        }

        Map<String, List<SexyBetRequest>> settleMap = pendingOrders.stream().collect(Collectors.groupingBy(item -> item.getUserId()));

        settleMap.forEach((userId, orders) -> {
            orders.forEach(order -> {
                log.info("sexy reSettle : userId - " + userId + " order : " + order);
                String orderId = order.getPlatformTxId();
                String roundId = order.getRoundId();
                UserInfoVO userInfoVO = getByUserId(userId);
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

            UserInfoVO userInfoVO = getByUserId(userId);
            sexyOrderService.handleSexyBetOrder(userInfoVO, orders, SexyOrderType.SETTLE);
        });

        return SexyBaseRsp.success();
    }


    /**
     * 打赏
     */
    private SexyBaseRsp tip(JSONObject reqData) {
        log.info("sexy tip : " + reqData.toString());
        String txnsJson = reqData.getString("txns");

        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        UserInfoVO userInfoVO = getByUserId(userId);
        SexyBaseRsp isValid = checkRequestValid(userInfoVO, txns, true);
        if (isValid != null) {
            return isValid;
        }

        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            //订单状态
            String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
            List<String> cancelBetList = RedisUtil.getValue(key);
            if (cancelBetList != null && !cancelBetList.isEmpty()) {
                if (cancelBetList.contains(orderId)) {
                    UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                    String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                    return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
                }
            }
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("sexy voidBet error 账单已经存在: " + betInfo);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);

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
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
        sexyOrderService.handleSexyBetOrder(userInfoVO, pendingOrders, SexyOrderType.TIP);
        return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
    }


    /**
     * 取消打赏
     */
    private SexyBaseRsp cancelTip(JSONObject reqData) {
        log.info("sexy cancelTip : " + reqData.toString());
        String txnsJson = reqData.getString("txns");
        List<SexyBetRequest> txns = JSON.parseObject(txnsJson, new TypeReference<List<SexyBetRequest>>() {
        });

        String userId = userCheck(txns);
        if (userId == null) {
            return SexyBaseRsp.failed(SexyErrorEnum.ACCOUNT_NOT_EXISTS);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        List<SexyBetRequest> pendingOrders = org.apache.commons.compress.utils.Lists.newArrayList();
        for (SexyBetRequest betInfo : txns) {
            String orderId = betInfo.getPlatformTxId();
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            if (existOrders.isEmpty()) {
                String key = String.format(RedisConstants.SEXY_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userId);
                List<String> cancelBetList = RedisUtil.getValue(key);
                if (cancelBetList == null || cancelBetList.isEmpty()) {
                    cancelBetList = org.apache.commons.compress.utils.Lists.newArrayList();
                }
                cancelBetList.add(orderId);
                RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
                String balanceTs = TimeZoneUtils.formatTimestampToSexyDate(System.currentTimeMillis());
                return SexyBaseRsp.success(userCoin.getTotalAmount(), balanceTs);
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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

        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(cancelBetRecord)) {
            //订单已处理
            return cancelBetRecord;

        }
        return org.apache.commons.compress.utils.Lists.newArrayList();
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
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(cancelBetRecord)) {
            //订单已处理
            return cancelBetRecord;

        }
        return org.apache.commons.compress.utils.Lists.newArrayList();
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
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());

        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());

        return toUserCoinHandle(userCoinAddVO);
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
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
    }




    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
//        log.info(" sexy createMember : " + casinoMemberVO);
        String currencyCode = casinoMemberVO.getCurrencyCode();
        String sexyCurrencyCode = SEXYCurrencyEnum.getSexyCodeByCode(currencyCode);
        if (sexyCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String agentId = venueDetailVO.getMerchantNo();
        String cert = venueDetailVO.getAesKey();

        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(cert)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        Map<String, String> data = new HashMap<>();
        data.put("cert", cert);
        data.put("agentId", agentId);
        data.put("userId", casinoMemberVO.getVenueUserAccount().replace(CommonConstant.UNDERSCORE, ""));
        data.put("currency", sexyCurrencyCode);
        String betSetting = toBetLimitJson(agentId,sexyCurrencyCode);
        data.put("betLimit", betSetting);
        String url = venueDetailVO.getApiUrl() + SEXYConstant.URI + SEXYConstant.CREATE_MEMBER;
        log.info("sexy createMember : 请求参数 : " + data);
        String rsp = HttpClientHandler.post(url, null, data);

        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getString("status").equals(SUCCESS_CODE)) {
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
//        log.info(" sexy login : " + loginVO + " casinoMemberVO : " + casinoMemberVO);
        String currencyCode = loginVO.getCurrencyCode();
        String sexyCurrencyCode = SEXYCurrencyEnum.getSexyCodeByCode(currencyCode);
        if (sexyCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String agentId = venueDetailVO.getMerchantNo();
        String cert = venueDetailVO.getAesKey();


        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(cert)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        String url = venueDetailVO.getApiUrl() + SEXYConstant.URI + SEXYConstant.LOGIN_AND_LAUNCH_GAME;

        Map<String, String> data = new HashMap<>();
        data.put("cert", cert);
        data.put("agentId", agentId);
        data.put("userId", casinoMemberVO.getVenueUserAccount().replace(CommonConstant.UNDERSCORE, ""));
        data.put("currency", sexyCurrencyCode);
        data.put("platform", SEXYConstant.PLATFORM);
        data.put("gameType", SEXYConstant.GAME_TYPE);
        data.put("gameCode", loginVO.getGameCode());
        data.put("language", SEXYLangEnum.conversionLang(loginVO.getLanguageCode()));

        String rsp = HttpClientHandler.post(url, null, data);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getString("status").equals(SUCCESS_CODE)) {
                String source = jsonObject.getString("url");
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

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        List<GameInfoPO> gameInfoList = gameInfoService.queryGameByVenueCode(venueInfoVO.getVenueCode());
        gameInfoList.forEach(gameInfo -> {
            JSONObject gameJson = new JSONObject();
            gameJson.put("deskName", gameInfo.getGameName());
            gameJson.put("deskNumber", gameInfo.getAccessParameters());
            resultList.add(gameJson);
        });
        return ResponseVO.success(resultList);
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        int pageSize = 500;
        int pageNumber = 1;
        while (true) {
            Page<SexyOrderRecordPO> page = new Page<>(pageNumber, pageSize);
            LambdaQueryWrapper<SexyOrderRecordPO> query = Wrappers.lambdaQuery(SexyOrderRecordPO.class);
            query.ge(SexyOrderRecordPO::getBetTime, venuePullParamVO.getStartTime());
            query.le(SexyOrderRecordPO::getBetTime, venuePullParamVO.getEndTime());
            query.orderByAsc(SexyOrderRecordPO::getBetTime);
            page = sexyOrderRepository.selectPage(page, query);
            List<SexyOrderRecordPO> records = page.getRecords();
            log.info("sexy 拉单结果 : " + records);
            if (records.isEmpty()) {
                log.info("sexy 没有更多数据，循环结束。拉单参数 : " + venuePullParamVO);
                break;
            }
            handleRemoteOrder(records, venueInfoVO);
            pageNumber++;
        }


        return ResponseVO.success();

    }

    private void handleRemoteOrder(List<SexyOrderRecordPO> gameHistoryList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = gameHistoryList.stream().map(SexyOrderRecordPO::getUserId).distinct().toList();
//
//        Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(thirdAccounts, venueInfoVO.getVenuePlatform());

        //userInfo
//        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.SEXY.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.SEXY.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (SexyOrderRecordPO order : gameHistoryList) {

            UserInfoVO userInfoVO = userMap.get(order.getUserId());
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setSiteName(siteNameMap.get(userInfoVO.getSiteCode()));
            recordVO.setSiteCode(userInfoVO.getSiteCode());
            recordVO.setUserName(userInfoVO.getUserName());
            recordVO.setVenueType(VenueEnum.SEXY.getType().getCode());
            list.add(recordVO);
            if (list.size() == 300) {
                orderRecordProcessService.orderProcess(list);
                list.clear();
            }

        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, SexyOrderRecordPO order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(order.getBetAmount());
        recordVO.setBetIp(order.getIp() == null ? null : order.getIp());
        recordVO.setBetTime(order.getBetTime());
        recordVO.setVenuePlatform(order.getPlatform());
        recordVO.setVenueCode(VenueEnum.SEXY.getVenueCode());
        recordVO.setCasinoUserName(order.getVenueUserId());
        if (userLoginInfoVO != null) {
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(order.getGameCode());
        recordVO.setGameNo(order.getRoundId());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(order.getPlatformTxId());
        recordVO.setTransactionId(order.getPlatformTxId());
        recordVO.setValidAmount(order.getTurnover());
        recordVO.setWinLossAmount(order.getWinLoss());
        recordVO.setPayoutAmount(order.getWinAmount());
        recordVO.setOdds(order.getOdds().replaceAll("^-?(\\d+)", "$1"));
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        Integer orderStatus = getLocalOrderStatus(order.getOrderType());
        recordVO.setOrderStatus(orderStatus);
        recordVO.setSettleTime(order.getUpdateTime());
        if (Objects.equals(orderStatus, OrderStatusEnum.CANCEL.getCode())) {
            recordVO.setFirstSettleTime(order.getTxTime());
        }
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setDeskNo(order.getDeskNo());
        String bootNo = order.getRoundId().replaceAll(".*GA(\\d{5}).*", "$1");
        recordVO.setBootNo(bootNo);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setResultList(getPlayType(order));
        recordVO.setPlayType(getPlayType(order));
        recordVO.setBetContent(recordVO.getOrderInfo());
        recordVO.setGameName(SexyGameTypeEnum.getEnumByCode(order.getGameCode()).getGameType());
        recordVO.setThirdGameCode(SexyGameTypeEnum.getEnumByCode(order.getGameCode()).getGameType());

        recordVO.setRoomTypeName(SexyGameTypeEnum.getEnumByCode(order.getGameCode()).getName());

        buildBetDetailAndResult(recordVO,order);
        String gameInfo = order.getGameInfo();
        JSONObject gameInfoJson = JSONObject.parseObject(gameInfo);
        gameInfoJson.put("playType",getPlayType(order));
        gameInfoJson.put("betResult",SexyOrderInfoUtil.getSexyResultList(recordVO.getOrderInfo(),order.getGameCode()));
        recordVO.setParlayInfo(JSONObject.toJSONString(gameInfoJson));

        return recordVO;
    }


    public void buildBetDetailAndResult(OrderRecordVO recordVO, SexyOrderRecordPO order) {
        if (order.getResultList()==null){
            return;
        }
        String gameCode = order.getGameCode();
        SexyGameTypeEnum gameTypeEnum = SexyGameTypeEnum.getEnumByCode(gameCode);
        switch (gameTypeEnum) {
            case BAC,BAC_2,TPD:
                recordVO.setOrderInfo(buildBJLResultList(order));
                break;
            case LH:
                recordVO.setOrderInfo(buildLhResultList(order));
                break;
            case LP,SBR,E_SBR,YXX,YSD:
                recordVO.setOrderInfo(order.getResultList());
                break;
            case UNKNOWN:
                break;
        }
    }

    public String buildBJLResultList(SexyOrderRecordPO orderResponseVO) {

        String resultList = orderResponseVO.getResultList();
        // TODO
        List<String> list = JSON.parseArray(resultList, String.class);
        if (list==null || list.isEmpty()){
            return "";
        }
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> playerCardList = Lists.newArrayList();

        if (list.size() > 2) {
            List<String> bankerPart;
            List<String> playerPart;

            if (list.contains(CommonConstant.EMPTY_STRING)) {
                if (list.size()==CommonConstant.business_six){
                    playerPart = list.subList(0, 3);
                    bankerPart = list.subList(3, list.size());
                }else {
                    int index = list.indexOf(CommonConstant.EMPTY_STRING);
                    playerPart = list.subList(0, index);
                    bankerPart = list.subList(index + 1, list.size());
                }

            } else {
                playerPart = list.subList(0, 3);
                bankerPart = list.subList(3, list.size());
            }

            bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> bankerCardList.add(new Card().formatSexyCard(cardStr)));
            playerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> playerCardList.add(new Card().formatSexyCard(cardStr)));

        } else {
            Card playerCard = new Card().formatSexyCard(list.get(0));
            Card bankerCard = new Card().formatSexyCard(list.get(1));
            bankerCardList.add(bankerCard);
            playerCardList.add(playerCard);
        }
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("banker", bankerCardList);
        cardMap.put("player", playerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);
        return bjlResult;
    }

    public String buildLhResultList(SexyOrderRecordPO orderResponseVO) {
        log.info(" buildBJLResultList - " + orderResponseVO.getResultList());

        String resultList = orderResponseVO.getResultList();
        // TODO
        List<String> list = JSON.parseArray(resultList, String.class);
        if (list==null || list.isEmpty()){
            return "";
        }
        List<Card> dragonCardList = Lists.newArrayList();
        List<Card> tigerCardList = Lists.newArrayList();

        Card dragonCard = new Card().formatSexyCard(list.get(0));
        Card tigerCard = new Card().formatSexyCard(list.get(1));
        dragonCardList.add(dragonCard);
        tigerCardList.add(tigerCard);

        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("dragon", dragonCardList);
        cardMap.put("tiger", tigerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);
        return bjlResult;
    }


    public String buildYXXResultList(SexyOrderRecordPO orderResponseVO) {
        log.info(" buildYXXResultList - " + orderResponseVO.getResultList());

        String resultList = orderResponseVO.getResultList();
        // TODO
        List<String> list = JSON.parseArray(resultList, String.class);
        List<Card> dragonCardList = Lists.newArrayList();
        List<Card> tigerCardList = Lists.newArrayList();

        Card dragonCard = new Card().formatSexyCard(list.get(0));
        Card tigerCard = new Card().formatSexyCard(list.get(1));
        dragonCardList.add(dragonCard);
        tigerCardList.add(tigerCard);

        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("dragon", dragonCardList);
        cardMap.put("tiger", tigerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);
        return bjlResult;
    }


    /**
     * 下注项 - 映射
     */
    private String getPlayType(SexyOrderRecordPO orderResponseVO) {
        if (orderResponseVO.getGameCode().equals(SexyGameTypeEnum.TPD.getCode())){
            return orderResponseVO.getBetType();
        }
        StringBuilder playType = new StringBuilder();
        String betType = orderResponseVO.getBetType();
        String gameCode = orderResponseVO.getGameCode();
        if  (gameCode.equals(SexyGameTypeEnum.BAC.getCode())
                || gameCode.equals(SexyGameTypeEnum.LH.getCode())
                || gameCode.equals(SexyGameTypeEnum.LP.getCode())){
            SexyPlayTypeEnum.fromKey(betType).ifPresent(SexyPlayTypeEnum -> playType.append(SexyPlayTypeEnum.getShCode()).append(","));

        }else {
            playType.append(betType).append(",");
        }
        return playType.toString();
    }


    public Integer getLocalOrderStatus(String orderStatus) {

        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("1", OrderStatusEnum.NOT_SETTLE.getCode());
        statusMap.put("2", OrderStatusEnum.CANCEL.getCode());
        statusMap.put("3", OrderStatusEnum.CANCEL.getCode());
        statusMap.put("4", OrderStatusEnum.SETTLED.getCode());
        statusMap.put("5", OrderStatusEnum.CANCEL.getCode());
        statusMap.put("6", OrderStatusEnum.SETTLED.getCode());
        statusMap.put("7", OrderStatusEnum.SETTLED.getCode());
        statusMap.put("8", OrderStatusEnum.CANCEL.getCode());
        return statusMap.getOrDefault(orderStatus, 0);
    }


    public static void main(String[] args) {
        test();
//        loginTest();
//        queryGameList();
//        createMemberTest();
        String source = "prod93744660";
        String output = source.substring(0, 1).toUpperCase() + source.substring(1);
        String replaceAll = output.replaceAll("(?<=\\D)(?=\\d)", "_");
        System.out.println("SexyServiceImpl.main " + replaceAll);
    }
    public static void test(){
        String resultList = "[\"H12\",\"H12\",\"D05\",\"D02\",\"H04\",\"\"]";
        // TODO
        List<String> list = JSON.parseArray(resultList, String.class);
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> playerCardList = Lists.newArrayList();

        if (list.size() > 2) {
            List<String> bankerPart;
            List<String> playerPart;

            if (list.contains(CommonConstant.EMPTY_STRING)) {
                if (list.size()==CommonConstant.business_six){
                    playerPart = list.subList(0, 3);
                    bankerPart = list.subList(3, list.size());
                }else {
                    int index = list.indexOf(CommonConstant.EMPTY_STRING);
                    playerPart = list.subList(0, index);
                    bankerPart = list.subList(index + 1, list.size());
                }

            } else {
                playerPart = list.subList(0, 3);
                bankerPart = list.subList(3, list.size());
            }

            bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> bankerCardList.add(new Card().formatSexyCard(cardStr)));
            playerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> playerCardList.add(new Card().formatSexyCard(cardStr)));

        } else {
            Card playerCard = new Card().formatSexyCard(list.get(0));
            Card bankerCard = new Card().formatSexyCard(list.get(1));
            bankerCardList.add(bankerCard);
            playerCardList.add(playerCard);
        }
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("banker", bankerCardList);
        cardMap.put("player", playerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);
        System.out.println("SexyServiceImpl.test - " + bjlResult);

        Map<String, List<Card>> cardMap2 = JSON.parseObject(bjlResult, new TypeReference<Map<String, List<Card>>>(){});
        List<Card> list1 = cardMap2.get("banker");
        List<Card> list2 = cardMap2.get("player");
       list1.forEach(System.out::println);
        System.out.println("SexyServiceImpl.test list1 - list2"+list2.get(0).getPokerPattern().toString() );
        list2.forEach(System.out::println);

    }


    public static void loginTest() {
        String sexyCurrencyCode = SEXYCurrencyEnum.CNY.getSexyCode();
        if (sexyCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }

        String url = URL + SEXYConstant.URI + "/login";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("cert", cert);
        dataMap.put("agentId", agentID);
        dataMap.put("userId", userId);
        dataMap.put("currency", sexyCurrencyCode);

        String rsp = HttpClientHandler.post(url, null, dataMap);
        System.out.println("JdbServiceImpl.loginTest ---- " + rsp);
    }

    public static void createMemberTest() {

        String sexyCurrencyCode = SEXYCurrencyEnum.CNY.getSexyCode();
        if (sexyCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String url = URL + SEXYConstant.URI + "/createMember";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("cert", cert);
        dataMap.put("agentId", agentID);
        dataMap.put("userId", userId);
//        dataMap2.put("mobileLogin", loginVO.isMobileLogin());
        dataMap.put("currency", sexyCurrencyCode);

        String betSetting = toBetLimitJson(agentID,sexyCurrencyCode);
        System.out.println("SexyServiceImpl.createMemberTest.  json.   " + betSetting);
        dataMap.put("betLimit", betSetting);

        String rsp = HttpClientHandler.post(url, null, dataMap);
        System.out.println("JdbServiceImpl.loginTest ---- " + rsp);
    }


    public static String toBetLimitJson(String agentId,String currency) {
        BetSetting betSetting;
        if (agentId .endsWith(SEXYConstant.ENV_PROD)) {
             betSetting = BET_SETTING_MAP_PROD.get(currency);
        }else {
             betSetting = BET_SETTING_MAP.get(currency);
        }

        if (betSetting == null) {
            return null;
        }

        Map<String, Map<String, Object>> serializeMap = Map.of(
                betSetting.getVenueCode(),
                Map.of(betSetting.getGameType(), Map.of("limitId", betSetting.getLimitId()))
        );

        return JSON.toJSONString(serializeMap, JSONWriter.Feature.PrettyFormat);
    }

    public String formatString(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output.replaceAll("(?<=\\D)(?=\\d)", "_");
    }


}
