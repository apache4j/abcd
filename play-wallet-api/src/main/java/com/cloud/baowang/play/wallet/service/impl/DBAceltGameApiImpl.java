package com.cloud.baowang.play.wallet.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.AceltErrorEnum;
import com.cloud.baowang.play.wallet.service.DBAceltGameApi;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.enums.AceltActionEnum;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.*;
import com.cloud.baowang.play.wallet.vo.req.db.config.DBCryptoConfig;
import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.AceltTradeInfo;
import com.cloud.baowang.play.wallet.vo.res.db.acelt.DBAceltBaseRsp;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DBAceltGameApiImpl extends BaseService implements DBAceltGameApi {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private DBCryptoConfig cryptoConfig;

    private final Integer API_SUCCESS = 200;


    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll("\\D+", "");
    }

    public DBAceltBaseRsp checkRequestValid(UserInfoVO userInfoVO) {
        if (userInfoVO == null) {
            return null;
        }
        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                .venueCode(VenueEnum.DBACELT.getVenueCode())
                .currencyCode(userInfoVO.getMainCurrency()).build();
        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBACELT.getVenueCode());
            return DBAceltBaseRsp.failed(AceltErrorEnum.FAIL);
        }


        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBACELT.getVenueCode());
            return DBAceltBaseRsp.failed(AceltErrorEnum.MEMBER_LOGIN_RESTRICTED);
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


    @Override
    public DBAceltBaseRsp<List<AceltTradeInfo>> getBalance(BalanceQueryVO reqVO) {
        if (!checkMD5(reqVO, reqVO.getSign())) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.SIGN_INVALID);
        }
        log.info("getBalance : DB彩票 - {}", reqVO);

        Map<String, String> userIdMap = userCheck(reqVO);

        if (userIdMap == null) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.PARAM_ERROR);
        }
        List<UserCoinWalletVO> userCenterCoinList = userCoinApi.getUserCenterCoinList(new ArrayList<>(userIdMap.keySet()));
        if (userCenterCoinList.isEmpty()
                || userCenterCoinList.stream().anyMatch(vo -> vo.getUserId() == null)) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.PARAM_ERROR);
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


    @Override
    public DBAceltBaseRsp<List<OrderRspData>> upateBalance(TransferRequestVO reqVO) {
        if (!checkMD5(reqVO, reqVO.getSign())) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.SIGN_INVALID);
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
            default -> DBAceltBaseRsp.failed(AceltErrorEnum.FAIL);
        };
    }


    private DBAceltBaseRsp<List<OrderRspData>> handleCancelBetRebate(TransferRequestVO req) {
        log.info("DB彩票-取消投注返点 : " + req.toString());
        List<TransferData> transferDatas = req.getTransferDatas();
        String userId = formatThirdAccount(transferDatas);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(
                UserCoinQueryVO.builder().userId(userId).build()
        );
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


    @Override
    public DBAceltBaseRsp<String> transfer(TransferCheckVO req) {
        if (!checkMD5(req)) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.SIGN_INVALID);
        }
        String userId = adaptThirdAccount(req.getUserName());
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO == null) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.PARAM_ERROR);
        }
        boolean isBet = req.getSafetyType().equals(CommonConstant.business_one_str);

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTransferId(), isBet);
        if (coinRecordVOS != null && !coinRecordVOS.isEmpty()) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.TRANSFER_REPEAT);
        }

        String transferType = req.getTransferType();
        return switch (transferType) {
            case "1" -> handleTransferBet(req, userInfoVO);
            case "2" -> handleTransferSettle(req, userInfoVO);
            default -> DBAceltBaseRsp.failed(AceltErrorEnum.FAIL);
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
            return DBAceltBaseRsp.failed(AceltErrorEnum.FAIL);
        }
    }

    private DBAceltBaseRsp<String> handleTransferBet(TransferCheckVO req, UserInfoVO userInfoVO) {
        DBAceltBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }
        log.info("DB彩票 - handleTransferBet : " + req);
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
        if (userCoin == null) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.MEMBER_FUNDS_FROZEN);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount();
        BigDecimal tradeAmount = new BigDecimal(req.getAmount());

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }
        if (totalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.MERCHANT_BALANCE_NOT_ENOUGH);
        }
        String tradeId = req.getTransferId();//流水号
        CoinRecordResultVO coinRecordResultVO = handleBetCoin(userInfoVO, tradeId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return DBAceltBaseRsp.failed(AceltErrorEnum.SUCCESS);
        } else {
            log.info("DB彩票 bet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBAceltBaseRsp.failed(AceltErrorEnum.FAIL);
        }
    }

    @Override
    public TransferRspData safetyTransfer(TransferCheckVO req) {
        if (!checkMD5(req)) {
            return TransferRspData.builder()
                    .code(String.valueOf(AceltErrorEnum.FAIL.getCode()))
                    .msg(AceltErrorEnum.FAIL.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }
        String userId = adaptThirdAccount(req.getUserName());
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO == null) {
            return TransferRspData.builder()
                    .code(String.valueOf(AceltErrorEnum.FAIL.getCode()))
                    .msg(AceltErrorEnum.FAIL.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }

        boolean isBet = req.getSafetyType().equals(CommonConstant.business_one_str);

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTransferId(), isBet);
        if (coinRecordVOS == null || coinRecordVOS.isEmpty()) {
            //订单不存在
            return TransferRspData.builder()
                    .code(String.valueOf(AceltErrorEnum.PARAM_ERROR.getCode()))
                    .msg(AceltErrorEnum.PARAM_ERROR.getMsg())
                    .serverTime(String.valueOf(System.currentTimeMillis())).build();
        }
        return TransferRspData.builder()
                .code("0000")
                .msg(AceltErrorEnum.SUCCESS.getMsg())
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
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }


    public List<OrderRspData> buildOrderRspData(List<TransferData> transferDatas, String userId, int code) {
        List<OrderRspData> orderRspData = Lists.newArrayList();
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
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
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());

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
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBACELT.getVenueCode());
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

    protected CoinRecordResultVO handleCancelBetCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
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
        userCoinAddVO.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

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
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBACELT.getVenueCode());
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
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }


    protected CoinRecordResultVO handleBetRebateCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    protected CoinRecordResultVO handleCancelBetRebateCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBACELT.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }


}
