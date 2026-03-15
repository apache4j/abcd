package com.cloud.baowang.play.game.db.acelt;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.db.acelt.enums.AceltActionEnum;
import com.cloud.baowang.play.api.vo.db.acelt.vo.*;
import com.cloud.baowang.play.api.vo.db.config.DBCryptoConfig;
import com.cloud.baowang.play.api.vo.db.evg.vo.AceltTradeInfo;
import com.cloud.baowang.play.api.vo.db.rsp.acelt.DBAceltBaseRsp;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBAceltErrorEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.db.acelt.enums.ACELTCurrencyEnum;
import com.cloud.baowang.play.game.db.acelt.enums.ACELTLangEnum;
import com.cloud.baowang.play.game.db.acelt.vo.AceltOrderVO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_ACELT)
public class DBAceltServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final static int SUCCESS_CODE = 0;

    /**
     * 已经注册过
     */
    private final static int REGISTER_SUCCEED = 701;

    private final Integer API_SUCCESS = 200;


    private final UserInfoApi userInfoApi;

    private final DBCryptoConfig cryptoConfig;




    public DBAceltBaseRsp checkRequestValid(UserInfoVO userInfoVO) {
        if (userInfoVO == null) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.QUERY_MEMBER_FAIL);
        }
        if (venueMaintainClosed( VenueEnum.DBACELT.getVenueCode(),userInfoVO.getSiteCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBACELT.getVenueCode());
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.SYSTEM_BUSY);
        }

        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBACELT.getVenueCode());
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.MEMBER_LOGIN_RESTRICTED);
        }

        return null;
    }

    public Map<String, String> userCheck(BalanceQueryVO req) {
        List<String> members = req.getMembers();
        if (members == null || members.isEmpty()) {
            return null;
        }

        Map<String, String> userIdsMap = members.stream()
                .collect(Collectors.toMap(this::adaptThirdAccount, Function.identity()));

        List<UserInfoVO> userInfos = userInfoApi.getUserInfoByUserIds(new ArrayList<>(userIdsMap.keySet()));
        DBAceltBaseRsp rsp = checkRequestValid(userInfos.get(0));
        if (rsp != null) {
            return null;
        }

        return (userInfos.size() == members.size()) ? userIdsMap : null;
    }


    private <T> boolean checkMD5(T reqVO, String sign) {
        String key = cryptoConfig.getAesKey(VenueEnum.DBACELT.getVenueCode());
        String signStr = reqVO.toString() + key;
        log.info("reqVO.toString(): " + reqVO.toString() + " key : " + key);
        return sign.equals(MD5Util.MD5Encode(signStr));
    }


    public DBAceltBaseRsp<List<AceltTradeInfo>> getBalance(BalanceQueryVO reqVO) {
        if (!checkMD5(reqVO, reqVO.getSign())) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.SIGN_INVALID);
        }
        log.info("getBalance : DB彩票 - {}", reqVO);

        Map<String, String> userIdMap = userCheck(reqVO);

        if (userIdMap == null) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.PARAM_ERROR);
        }
        List<UserCoinWalletVO> userCenterCoinList = userCoinApi.getUserCenterCoinList(new ArrayList<>(userIdMap.keySet()));
        if (userCenterCoinList.isEmpty()
                || userCenterCoinList.stream().anyMatch(vo -> vo.getUserId() == null)) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.PARAM_ERROR);
        }

        List<AceltTradeInfo> aceltRspInfos = Lists.newArrayList();
        for (UserCoinWalletVO userCoinWalletVO : userCenterCoinList) {
            if (userCoinWalletVO.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCoinWalletVO.getTotalAmount()) >= 0) {
                AceltTradeInfo rsp = AceltTradeInfo.builder().member(userIdMap.get(userCoinWalletVO.getUserId())).balance(String.valueOf(BigDecimal.ZERO)).build();
                aceltRspInfos.add(rsp);
            } else {
                BigDecimal totalAmount = userCoinWalletVO.getTotalAmount().setScale(2, RoundingMode.DOWN);
                AceltTradeInfo rsp = AceltTradeInfo.builder().member(userIdMap.get(userCoinWalletVO.getUserId())).balance(String.valueOf(totalAmount)).build();
                aceltRspInfos.add(rsp);
            }

        }

        return DBAceltBaseRsp.success(API_SUCCESS, aceltRspInfos);
    }


    public DBAceltBaseRsp<List<OrderRspData>> upateBalance(TransferRequestVO reqVO) {
        if (!checkMD5(reqVO, reqVO.getSign())) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.SIGN_INVALID);
        }

        String transferType = reqVO.getTransferType();
        AceltActionEnum actionEnum = AceltActionEnum.fromCode(transferType);
        return switch (actionEnum) {
            case BETTING -> handleBet(reqVO);//投注
            case CANCEL_ORDER -> handleCancelBet(reqVO);//取消投注
            case PAYOUT -> handleSettle(reqVO);//结算
            case CANCEL_PAYOUT -> handleCancelSettle(reqVO);//取消结算
            case SECOND_PAYOUT -> handleReSettle(reqVO);//重结算
            case BETTING_REBATE -> handleBetRebate(reqVO);//投注返点
            case CANCEL_BETTING_REBATE -> handleCancelBetRebate(reqVO);//取消投注返点
            default -> DBAceltBaseRsp.failed(DBAceltErrorEnum.FAIL);
        };
    }


    private DBAceltBaseRsp<List<OrderRspData>> handleCancelBetRebate(TransferRequestVO req) {
        log.info("DB彩票-取消投注返点 : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {

            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecordForVoidSettle(userInfoVO, orderId);
            UserCoinRecordVO existOrder = existOrders.stream()
                    .filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.RETURN_BET.getCode())
                            && CoinBalanceTypeEnum.EXPENSES.getCode().equals(order.getCoinType()))
                    .findAny().orElse(null);
            if (existOrder != null) {
                log.info("DB彩票取消投注返点账变已存在: " + betInfo);
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(3);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }

            UserCoinRecordVO betOrder = existOrders.stream()
                    .filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.RETURN_BET.getCode())
                            && CoinBalanceTypeEnum.INCOME.getCode().equals(order.getCoinType()))
                    .findAny().orElse(null);

            if (betOrder == null) {
                //无此投注返点的账变
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(2);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }

        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = handleCancelBetRebateCoin(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }
        return buildResponse(userId, orderRspData);
    }

    private DBAceltBaseRsp<List<OrderRspData>> handleBetRebate(TransferRequestVO req) {
        log.info("DB彩票-投注返点 : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {

            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, false);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.RETURN_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("DB彩票投注返点 error 账单不存在: " + betInfo);
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(3);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }

        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = handleBetRebateCoin(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }
        return buildResponse(userId, orderRspData);
    }

    private DBAceltBaseRsp<List<OrderRspData>> handleReSettle(TransferRequestVO req) {
        log.info("DB彩票重结算 : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {
            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecordForVoidSettle(userInfoVO, orderId);

            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(3);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                log.info("handleReSettle : 已存在重结算订单 : " + adjustOrder);
                continue;
            }
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (betOrder == null) {
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(2);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                log.info("handleReSettle : 不存在结算订单 : " + adjustOrder);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }

        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = reSettle(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }
        return buildResponse(userId, orderRspData);

    }


    private DBAceltBaseRsp<List<OrderRspData>> buildResponse(String userId, List<OrderRspData> rspList) {
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        rspList.forEach(rsp -> {
            rsp.setBeforeBalance(String.valueOf(userCoin.getTotalAmount()));
            rsp.setBalance(String.valueOf(userCoin.getTotalAmount()));
        });
        return DBAceltBaseRsp.success(API_SUCCESS, rspList);
    }

    private OrderRspData buildRsp(int code, TransferData betInfo) {
        OrderRspData rsp = new OrderRspData();
        rsp.setCode(code);
        rsp.setOrderId(betInfo.getOrderId());
        rsp.setMember(betInfo.getMember());
        rsp.setAmount(betInfo.getAmount());
        return rsp;
    }


    public DBAceltBaseRsp<String> transfer(TransferCheckVO req) {
        if (!checkMD5(req)) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.SIGN_INVALID);
        }
        String userId = adaptThirdAccount(req.getUserName());
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.PARAM_ERROR);
        }
        boolean isBet = req.getSafetyType().equals(CommonConstant.business_one_str);

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTransferId(), isBet);
        if (coinRecordVOS != null && !coinRecordVOS.isEmpty()) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.TRANSFER_REPEAT);
        }

        String transferType = req.getTransferType();
        return switch (transferType) {
            case "1" -> handleTransferBet(req, userInfoVO);
            case "2" -> handleTransferSettle(req, userInfoVO);
            default -> DBAceltBaseRsp.failed(DBAceltErrorEnum.FAIL);
        };
    }

    private DBAceltBaseRsp<String> handleTransferSettle(TransferCheckVO req, UserInfoVO userInfoVO) {
        log.info("DB彩票 - handleTransferSettle : " + req);
        String tradeId = req.getTransferId();//流水号

        BigDecimal tradeAmount = new BigDecimal(req.getAmount());

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            return DBAceltBaseRsp.success(API_SUCCESS);
        }

        CoinRecordResultVO coinRecordResultVO = this.handleSettleCoin(userInfoVO, tradeId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return DBAceltBaseRsp.success(API_SUCCESS);
        } else {
            log.info("DB彩票 handleTransferSettle : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.FAIL);
        }
    }

    private DBAceltBaseRsp<String> handleTransferBet(TransferCheckVO req, UserInfoVO userInfoVO) {
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }
        log.info("DB彩票 - handleTransferBet : " + req);
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (userCoin == null) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.MEMBER_FUNDS_FROZEN);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount();
        BigDecimal tradeAmount = new BigDecimal(req.getAmount());

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }
        if (totalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }
        String tradeId = req.getTransferId();//流水号
        CoinRecordResultVO coinRecordResultVO = handleBetCoin(userInfoVO, tradeId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.SUCCESS);
        } else {
            log.info("DB彩票 bet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBAceltBaseRsp.failed(DBAceltErrorEnum.FAIL);
        }
    }


    public TransferRspData safetyTransfer(TransferCheckVO req) {
        if (!checkMD5(req)) {
            return TransferRspData.builder()
                    .code(String.valueOf(DBAceltErrorEnum.FAIL.getCode()))
                    .msg(DBAceltErrorEnum.FAIL.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }
        String userId = adaptThirdAccount(req.getUserName());
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            return TransferRspData.builder()
                    .code(String.valueOf(DBAceltErrorEnum.FAIL.getCode()))
                    .msg(DBAceltErrorEnum.FAIL.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }

        boolean isBet = req.getSafetyType().equals(CommonConstant.business_one_str);

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTransferId(), isBet);
        if (coinRecordVOS == null || coinRecordVOS.isEmpty()) {
            //订单不存在
            return TransferRspData.builder()
                    .code(String.valueOf(DBAceltErrorEnum.PARAM_ERROR.getCode()))
                    .msg(DBAceltErrorEnum.PARAM_ERROR.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }
        return TransferRspData.builder()
                .code("0000")
                .msg(DBAceltErrorEnum.SUCCESS.getMsg())
                .serverTime(String.valueOf(System.currentTimeMillis())).build();
    }

    private boolean checkMD5(TransferCheckVO reqVO) {
        String key = cryptoConfig.getAesKey(VenueEnum.DBACELT.getVenueCode());
        String firstMD5 = MD5Util.MD5Encode(reqVO.toString());
        String secondMD5 = firstMD5 + "&" + key;
        String signStr = MD5Util.MD5Encode(secondMD5);
        return reqVO.getSignature().equals(signStr);
    }


    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String orderId, boolean expenses) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());

        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return Lists.newArrayList();
    }


    public List<OrderRspData> buildOrderRspData(List<TransferData> transferDatas, String userId, int code) {
        List<OrderRspData> orderRspData = Lists.newArrayList();
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        for (TransferData transferData : transferDatas) {
            OrderRspData orderRsp = new OrderRspData();
            orderRsp.setCode(code);
            orderRsp.setOrderId(transferData.getOrderId());
            orderRsp.setMember(transferData.getMember());
            orderRsp.setAmount(transferData.getAmount());
            orderRsp.setBeforeBalance(String.valueOf(userCoin.getTotalAmount()));
            orderRsp.setBalance(String.valueOf(userCoin.getTotalAmount()));
            orderRspData.add(orderRsp);
        }
        return orderRspData;
    }

    public DBAceltBaseRsp<List<OrderRspData>> handleCancelBet(TransferRequestVO req) {
        log.info("DB彩票-handleCancelBet : " + req);
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {
            String orderId = betInfo.getOrderId();
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            if (existOrders.isEmpty()) {
                log.info("DB彩票-handleCancelBet error 账单不存在: " + betInfo);
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(2);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            UserCoinRecordVO cancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
            if (cancelOrder != null) {
                log.info("DB彩票-handleCancelBet error 账单已经取消: " + betInfo);
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(0);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            UserCoinRecordVO unCancelOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (unCancelOrder != null) {
                //未取消
                pendingOrders.add(betInfo);
            }
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }

        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = handleCancelBetCoin(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }

        return buildResponse(userId, orderRspData);
    }

    private DBAceltBaseRsp<List<OrderRspData>> handleCancelSettle(TransferRequestVO req) {
        log.info("DB彩票 handleCancelSettle : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {
            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecordForVoidSettle(userInfoVO, orderId);

            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(3);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                log.info("handleCancelSettle : 已存在过取消结算订单 : " + adjustOrder);
                continue;
            }
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (betOrder == null) {
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(2);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                log.info("handleCancelSettle : 不存在结算订单 : " + adjustOrder);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }
        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = handleVoidSettle(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }

        return buildResponse(userId, orderRspData);

    }


    private DBAceltBaseRsp<List<OrderRspData>> handleSettle(TransferRequestVO req) {
        log.info("DB彩票-handleSettle : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        List<OrderRspData> orderRspData = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {

            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder == null) {
                log.info("DB彩票 settle error 账单不存在: " + betInfo);
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(2);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            UserCoinRecordVO adjustOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())).findAny().orElse(null);
            if (adjustOrder != null) {
                //已经处理过
                OrderRspData orderRsp = new OrderRspData();
                orderRsp.setCode(3);
                orderRsp.setOrderId(orderId);
                orderRsp.setMember(betInfo.getMember());
                orderRsp.setAmount(betInfo.getAmount());
                orderRspData.add(orderRsp);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            return buildResponse(userId, orderRspData);
        }
        for (TransferData betInfo : pendingOrders) {
            BigDecimal amt = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO result = handleSettleCoin(userInfoVO, betInfo.getOrderId(), amt, req.getNotifyId());
            orderRspData.add(buildRsp(
                    result != null && UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus()) ? 0 : 1,
                    betInfo
            ));
        }

        return buildResponse(userId, orderRspData);
    }


    private DBAceltBaseRsp<List<OrderRspData>> handleBet(TransferRequestVO req) {
        log.info("DB彩票 - handleBet : " + req);
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = getByUserId(userId);
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        List<TransferData> pendingOrders = Lists.newArrayList();
        for (TransferData betInfo : transferDatas) {
            String orderId = betInfo.getOrderId();
            //订单状态
            List<UserCoinRecordVO> existOrders = checkExistCoinRecord(userInfoVO, orderId, true);
            UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
            if (betOrder != null) {
                log.info("DB彩票 handleBet error 账单已经存在: " + betInfo);
                continue;
            }
            pendingOrders.add(betInfo);
        }
        log.info("pendingOrders : " + pendingOrders.size());
        if (pendingOrders.isEmpty()) {
            //都已处理
            List<OrderRspData> orderRspData = buildOrderRspData(transferDatas, userId, 0);
            return buildResponse(userId, orderRspData);
        }
        //检查余额
        BigDecimal betAmountTotal = transferDatas.stream().map(t -> new BigDecimal(t.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);

        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(betAmountTotal).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.DBACELT.getVenueCode(), transferDatas, userCoin, betAmountTotal);
            List<OrderRspData> orderRspData = buildOrderRspData(transferDatas, userId, 4);
            return DBAceltBaseRsp.success(API_SUCCESS, orderRspData);
        }
        String notifyId = req.getNotifyId();
        List<TransferData> orderSucceed = Lists.newArrayList();
        List<TransferData> orderFailed = Lists.newArrayList();
        for (TransferData betInfo : pendingOrders) {
            String orderId = betInfo.getOrderId();
            //账变
            BigDecimal betAmount = new BigDecimal(betInfo.getAmount());
            CoinRecordResultVO coinRecordResultVO = this.handleBetCoin(userInfoVO, orderId, betAmount, notifyId);
            if (coinRecordResultVO == null || !UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                log.info("DB彩票 bet error 账变失败:  {} ", betInfo);
                orderFailed.add(betInfo);
            }
            orderSucceed.add(betInfo);

        }
        if (!orderFailed.isEmpty()) {
            //回滚
            for (TransferData betInfo : orderSucceed) {
                String orderId = betInfo.getOrderId();
                BigDecimal betAmount = new BigDecimal(betInfo.getAmount());
                this.handleCancelBetCoin(userInfoVO, orderId, betAmount, notifyId);
            }
            List<OrderRspData> orderRspData = buildOrderRspData(transferDatas, userId, 1);
            return DBAceltBaseRsp.success(API_SUCCESS, orderRspData);
        }

        List<OrderRspData> orderRspData = buildOrderRspData(transferDatas, userId, 0);
        return DBAceltBaseRsp.success(API_SUCCESS, orderRspData);

    }

    public String formatThirdAccount(List<TransferData> reqData) {
        String userAccount = reqData.get(0).getMember();

        return adaptThirdAccount(userAccount);
    }


    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String transactionId) {
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
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setThirdOrderNo(tradeId);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleCancelBetCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String transactionId) {
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
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    public List<UserCoinRecordVO> checkExistCoinRecordForVoidSettle(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return Lists.newArrayList();
    }


    /**
     * 取消派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleVoidSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String transactionId) {
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
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
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
    protected CoinRecordResultVO reSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String transactionId) {
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
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());

        return toUserCoinHandle(userCoinAddVO);
    }


    protected CoinRecordResultVO handleBetRebateCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String transactionId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RETURN_BET.getCode());

        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleCancelBetRebateCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String transactionId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(transactionId);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RETURN_BET.getCode());
        return toUserCoinHandle(userCoinAddVO);
    }





    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        log.info("DB彩票-createMember");
        String currencyCode = casinoMemberVO.getCurrencyCode();

        String agentId = venueDetailVO.getMerchantNo();
        String key = venueDetailVO.getAesKey();
        String url = venueDetailVO.getApiUrl()+"/boracay/api/member/create";
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(key)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("doubleList", "");
        dataMap.put("member", adaptUserAccount(casinoMemberVO.getVenueUserAccount()));
        dataMap.put("memberType", 1);
        dataMap.put("merchant", agentId);
        dataMap.put("normalList", "");
        dataMap.put("password", casinoMemberVO.getCasinoPassword());
        dataMap.put("timestamp", System.currentTimeMillis());
        String sign = buildSign(dataMap, key);


        Integer currencyType = ACELTCurrencyEnum.fromCode(currencyCode).getCode();
        dataMap.put("currencyType", currencyType);
        dataMap.put("sign", sign);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(dataMap));
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            Integer code = jsonObject.getInteger("code");
            if (code == SUCCESS_CODE || code == REGISTER_SUCCEED) {
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
        log.info("DB彩票: "+venueDetailVO);
        String agent = venueDetailVO.getMerchantNo();
        String apiUrl = venueDetailVO.getApiUrl();
        String key = venueDetailVO.getAesKey();

        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        String url = apiUrl + "/boracay/api/member/login";
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("member", adaptUserAccount(casinoMemberVO.getVenueUserAccount()));
        dataMap.put("merchant", agent);
        dataMap.put("password", casinoMemberVO.getCasinoPassword());
        dataMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        String sign = buildSignStr(dataMap, key);
        dataMap.put("sign", sign);
        dataMap.put("loginIp", loginVO.getIp());
//        dataMap.put("returnUrl","www.baidu.com");
        dataMap.put("currentHallType", String.valueOf(1));
        dataMap.put("lang", ACELTLangEnum.conversionLang(CurrReqUtils.getLanguage()));
        dataMap.put("currencyType", String.valueOf(ACELTCurrencyEnum.fromCode("CNY").getCode()));

        log.info("DB彩票: "+url+"DB彩票登录参数 : " + JSONObject.toJSONString(dataMap));

        String rsp = HttpClientHandler.post(url, null, dataMap);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            Integer code = jsonObject.getInteger("code");
            if (code == SUCCESS_CODE) {
                JSONObject data = jsonObject.getJSONObject("data");
                String pc = data.getString("pc");
                String h5 = data.getString("h5");
                String source;
                if (CommonConstant.business_one.equals(CurrReqUtils.getReqDeviceType())) {
                    source = pc;
                } else {
                    source = h5;
                }
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


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        String apiUrl = venueDetailVO.getApiUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getMerchantKey();
        String agent = venueDetailVO.getMerchantNo();
        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        boolean agency = true;
        String lastOrderId = "0";
        int pageSize = 100;
        String startTime = fromTimestamp(venuePullParamVO.getStartTime());
        String endTime = fromTimestamp(venuePullParamVO.getEndTime());

        String url = apiUrl + "/merchantdata/pull/order/all";
        Map<String, String> header = new HashMap<>();
        while (true) {
            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("agency", String.valueOf(agency));
            dataMap.put("endTime", endTime);
            dataMap.put("lastOrderId", lastOrderId);
            dataMap.put("merchantAccount", agent);
            dataMap.put("pageSize", String.valueOf(pageSize));
            dataMap.put("startTime", startTime);
            String sign = buildSignStr(dataMap, key);
            dataMap.put("sign", sign);

            String rsp = HttpClientHandler.get(url, header, dataMap);
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject == null || !jsonObject.getInteger("code").equals(SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueDetailVO.getVenueCode(), rsp, dataMap, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("4000044: {}", rsp);

            List<AceltOrderVO> records = jsonObject.getJSONArray("data").toJavaList(AceltOrderVO.class);
            if (records.isEmpty()) {
                break;
            }
            try {
                handleRemoteOrder(records, venueDetailVO);
            } catch (Exception e) {
                log.info("4000045",e);
            }
            if (records.size() < pageSize) {
                break;
            }
            lastOrderId = String.valueOf(records.get(records.size() - 1).getOrderId());

        }


        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<AceltOrderVO> orderList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = orderList.stream().map(order -> adaptThirdAccount(order.getMemberAccount())).distinct().toList();


        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DBACELT.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DBACELT.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (AceltOrderVO order : orderList) {

            UserInfoVO userInfoVO = userMap.get(adaptThirdAccount(order.getMemberAccount()));
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), order.getMemberAccount());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setVenueType(VenueEnum.DBACELT.getType().getCode());
            list.add(recordVO);
        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, AceltOrderVO order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        BigDecimal betMoney = order.getBetMoney().abs();
        recordVO.setBetAmount(betMoney);

        Long betTime = fromDateToTimestamp(order.getBetTime());
        Long settleTime = fromDateToTimestamp(order.getUpdateAt());
        ;
        recordVO.setBetTime(betTime);
        recordVO.setSettleTime(settleTime);

        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(order.getMemberAccount());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(String.valueOf(order.getOrderId()));
//        recordVO.setTransactionId(String.valueOf(order.getHistoryId()));
//        recordVO.setOrderInfo(PlayTypeEnum.fromCode(order.getBrt()).getDescription());


        recordVO.setResultList(order.getTicketResult());
        //派彩金额
        recordVO.setPayoutAmount(order.getWinAmount());
        //盈利金额
        BigDecimal profitAmount = order.getProfitAmount();
        recordVO.setWinLossAmount(profitAmount);


        recordVO.setOrderStatus(getLocalOrderStatus(order));
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(order));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());

        BigDecimal validBetAmount = recordVO.getBetAmount();
        recordVO.setValidAmount(validBetAmount.abs());

        //彩系id
        recordVO.setRoomType(String.valueOf(order.getSeriesId()));
        //彩系名称
        recordVO.setRoomTypeName(order.getSeriesName());


        recordVO.setGameName(order.getTicketName());
        recordVO.setPlayType(order.getPlayName());
        recordVO.setPlayInfo(order.getBetContent());
        recordVO.setBetContent(order.getBetContent());

        recordVO.setOdds(order.getOdd());

        recordVO.setGameNo(order.getTicketPlanNo());


        return recordVO;
    }

    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll("\\D+", "");
    }

    public Integer getLocalOrderStatus(AceltOrderVO order) {
        /**
         1：待开奖；2：未中奖；3：已中奖；4：挂起；5：已结算。
         */
        if (order .getCancelStatus()) {
            return OrderStatusEnum.CANCEL.getCode();
        }

        Integer betStatus = order.getBetStatus();
        return switch (betStatus) {
            case 1 -> OrderStatusEnum.PRE_LOTTERY.getCode();
            case 2 -> OrderStatusEnum.LOSS.getCode();
            case 3 -> OrderStatusEnum.WIN.getCode();
            case 4 -> OrderStatusEnum.HANG_UP.getCode();
            case 5 -> OrderStatusEnum.SETTLED.getCode();
            default -> 0;
        };

    }


    private String adaptUserAccount(String userAccount) {
        return userAccount.replaceAll("[^a-zA-Z0-9]", "");
    }


    private static String buildSign(Map<String, Object> dataMap, String key) {
        String signStr = dataMap.entrySet().stream()
                .map(e -> e.getKey() + (e.getValue() == null ? "" : e.getValue()))
                .collect(Collectors.joining());
        signStr += key;
        System.out.println("DBAceltServiceImpl.buildSign. 加密前 : " + signStr);
        return MD5Util.md5(signStr);
    }

    private static String buildSignStr(Map<String, String> dataMap, String key) {
        String signStr = dataMap.entrySet().stream()
                .map(e -> e.getKey() + (e.getValue() == null ? "" : e.getValue()))
                .collect(Collectors.joining());
        signStr += key;
        log.info("DBAceltServiceImpl.buildSign. 加密前 : " + signStr);
        return MD5Util.md5(signStr);
    }

    public static String fromTimestamp(long timestamp) {
        ZoneId ZONE_UTC8 = ZoneId.of("Asia/Shanghai");
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZONE_UTC8)
                .toLocalDateTime();
        return localDateTime.format(FORMATTER);
    }

    public static Long fromDateToTimestamp(String dateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));

        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static void main(String[] args) {
//        queryGameList();
        //createMemberTest();
        String dateStr = fromTimestamp(-62135625943000L);
        System.out.println("DBAceltServiceImpl.main - " + dateStr);
        Long l = fromDateToTimestamp("0001-01-01T00:00:00");

        System.out.println("DBAceltServiceImpl.main 时间戳 : " + l);
//        loginTest();

    }


    public static void createMemberTest() {
        String agent = "Winto01";
        String key = "50CA348C38A4B217";
        String url = "https://cpapi.yabocp.xyz" + "/boracay/api/member/create";

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("doubleList", "");
        dataMap.put("member", "Utest94083147");
        dataMap.put("memberType", 3);
        dataMap.put("merchant", agent);
        dataMap.put("normalList", "");
        dataMap.put("password", "322fc6fae6033e81c7475bcac09cc314");
        dataMap.put("timestamp", System.currentTimeMillis());

        String sign = buildSign(dataMap, key);

        dataMap.put("currencyType", ACELTCurrencyEnum.fromCode("CNY").getCode());
        dataMap.put("sign", sign);


        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");


        String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(dataMap));
        JSONObject jsonObject = JSONObject.parseObject(rsp);

        System.out.println("DG2ServiceImpl.loginTest ---- url : " + jsonObject);
    }


    public static void loginTest() {
        String apiUrl = "https://cpapi.yabocp.xyz";
        String key = "50CA348C38A4B217";

        String agent = "Winto01";

        boolean agency = true;
        String lastOrderId = "0";
        int pageSize = 100;
        String startTime = fromTimestamp(1759377600000L);
        String endTime = fromTimestamp(1759377900000L);

        String url = apiUrl + "/merchantdata/pull/order/all";
        Map<String, String> header = new HashMap<>();

        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("agency", String.valueOf(agency));
        dataMap.put("endTime", endTime);
        dataMap.put("lastOrderId", lastOrderId);
        dataMap.put("merchantAccount", agent);
        dataMap.put("pageSize", String.valueOf(pageSize));
        dataMap.put("startTime", startTime);
        String sign = buildSignStr(dataMap, key);
        dataMap.put("sign", sign);

        String rsp = HttpClientHandler.get(url, header, dataMap);
        JSONObject jsonObject = JSONObject.parseObject(rsp);

        log.info("DB彩票 三方拉单返回结果 : {}", rsp);


    }
}



