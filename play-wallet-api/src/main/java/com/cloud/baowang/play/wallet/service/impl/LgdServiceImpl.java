//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.cloud.baowang.common.core.constants.CacheConstants;
//import com.cloud.baowang.common.core.constants.RedisConstants;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.CurrencyEnum;
//import com.cloud.baowang.common.core.enums.ResultCode;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.MD5Util;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.service.LgdService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.ldg.RequestVO;
//import com.cloud.baowang.play.wallet.vo.res.ldg.LgdDataResp;
//import com.cloud.baowang.play.wallet.vo.res.ldg.LgdResp;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class LgdServiceImpl extends BaseService implements LgdService {
//    private final CasinoMemberApi casinoMemberApi;
//    private final UserCoinApi userCoinApi;
//    private final UserInfoApi userInfoApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//
//    private CasinoMemberVO getVerifierToken(String userToken) {
//        if (StringUtils.isBlank(userToken)) {
//            return null;
//        }
//        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.LGD.getVenueCode(), userToken);
//        CasinoMemberVO casinoMemberVO = RedisUtil.getValue(key);
//
//        if (ObjectUtil.isNotEmpty(casinoMemberVO)) {
//            return casinoMemberVO;
//        }
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(userToken);
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.LGD);
//        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!casinoMemberResp.isOk()) {
//            log.info("lgd oauth 用户信息不存在，venueCode:{},token:{}", VenueCodeConstants.LGD, casinoMemberVO);
//            return null;
//        }
//        CasinoMemberVO memberVO = new CasinoMemberVO();
//        BeanUtil.copyProperties(casinoMemberResp.getData(), memberVO);
//        return memberVO;
//    }
//
//
//    @Override
//    public LgdResp oauth(RequestVO request) {
//        String token = request.getToken();
////        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
////        casinoMemberReqVO.setCasinoPassword(token);
////        casinoMemberReqVO.setVenueCode(VenueCode.LGD);
////        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        LgdResp resp = LgdResp.success();
////        if (!casinoMemberResp.isOk()) {
////            log.info("lgd oauth 用户信息不存在，token:{}", token);
////            return resp.setCODE("10000").setMSG("user not exist");
////        }
//
//        CasinoMemberVO casinoMember = getVerifierToken(token);
//        if (casinoMember == null) {
//            log.info("lgd oauth 用户信息不存在，token:{}", token);
//            return resp.setCODE("0004").setMSG("user not exist");
//        }
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder()
//                .userAccount(casinoMember.getUserAccount())
//                .siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//        String mainCurrency = userInfoVO.getMainCurrency();
//        LgdDataResp lgdDataResp = new LgdDataResp();
//        lgdDataResp.setCurrency(mainCurrency.equals(CurrencyEnum.KVND.getCode())
//                ? "VN2" : mainCurrency);
//        lgdDataResp.setPlayerName(casinoMember.getVenueUserAccount());
//        lgdDataResp.setPlayerPrice(balance.toString());
//        return resp.setData(lgdDataResp);
//    }
//
//    @Override
//    public LgdResp checkBalance(RequestVO request) {
//        return oauth(request);
//    }
//
//    @Override
//    public LgdResp bet(RequestVO request) {
//
//        String orderNo = request.getBetId();
//        BigDecimal betAmount = request.getBetPrice();
//        BigDecimal winloseAmount = request.getBetWins();
//        //当fcid不为null的时候,则为免费优惠活动
//        if (StringUtils.isNotEmpty(request.getFcid())) {
//            orderNo = request.getFcid();
//            betAmount = BigDecimal.ZERO;
//        }
//        LgdResp resp = LgdResp.success();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.LGD, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("lgd rollback error get locker error, req:{}", request);
//                resp.setCODE("4003").setMSG("try lock error");
//                return resp;
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.LGD);
//            casinoMemberReqVO.setCasinoPassword(request.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                resp.setCODE("0004").setMSG("member not exist");
//                return resp;
//            }
//            if (venueMaintainClosed(VenueCodeConstants.LGD,casinoMember.getSiteCode())) {
//                return LgdResp.err("4004", "venue close");
//            }
//
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("lgd bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                resp.setCODE("0004").setMSG("member not exist");
//                return resp;
//            }
//            // 游戏锁定
//            if (userGameLock(userInfoVO)) {
//                log.error("lgd bet error game lock userName[{}] not find.", userId);
//                resp.setCODE("4004").setMSG("game lock");
//                return resp;
//            }
//
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueEnum.LGD.getVenueCode())
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO2 = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO2.getData();
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.LGD.getVenueCode());
//                resp.setCODE("4004").setMSG("game lock");
//                return resp;
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("lgd bet error wallet is not exist user account:{}.", userId);
//                resp.setCODE("0001").setMSG("member wallet not exist");
//                return resp;
//            }
//            // LGD 新增余额判断 update by xiaozhi 2025-06-15
//            if(betAmount.compareTo(userCenterCoin.getCenterTotalAmount()) > 0){
//                log.error("lgd bet error wallet is not enough:{}.", userId);
//                resp.setCODE("0001").setMSG("member wallet is not enough");
//                return resp;
//            }
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(request.getBetId());
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinBetRecRespVO.isOk() || CollectionUtil.isNotEmpty(userCoinBetRecRespVO.getData())) {
//                log.error("lgd  bet error feign error or order not exist feign resp{}.", userCoinBetRecRespVO);
//                resp.setCODE("4004").setMSG("betId already exist");
//                return resp;
//            }
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, orderNo,
//                    betAmount, winloseAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ResponseVO<Boolean> res = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    yield ResponseVO.fail(ResultCode.PARAM_ERROR);
//                }
//            };
//            if (!res.isOk()) {
//                return resp.setCODE("4004").setMSG("wallet change error");
//            }
//
//            // 发送注单
//            /***OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//             BeanUtil.copyProperties(request, orderRecordMqVO,"id");
//             BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//             orderRecordMqVO.setBetAmount(request.getBetPrice());
//             orderRecordMqVO.setValidAmount(request.getBetPrice());
//             orderRecordMqVO.setBetTime(request.getTimestamp());
//
//             BigDecimal betWin = Objects.nonNull(request.getBetWins())?request.getBetWins():BigDecimal.ZERO;
//             BigDecimal betPrice = Objects.nonNull(request.getBetPrice())?request.getBetPrice():BigDecimal.ZERO;
//             orderRecordMqVO.setWinLossAmount(betWin.subtract(betPrice));
//
//             orderRecordMqVO.setPayoutAmount(request.getBetWins());
//             orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + orderNo);
//             orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//             orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//             orderRecordMqVO.setThirdOrderId(orderNo);
//             orderRecordMqVO.setSettleTime(request.getTimestamp());
//             orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
//             orderRecordMqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
//             orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
//             orderRecordMqVO.setFreeGame(request.getFcid() != null);
//             orderRecordMqVO.setVenueCode(VenueCode.LGD);
//             KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);***/
//
//            // 获取平台信息
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.venueInfoByVenueCode(VenueCodeConstants.LGD, userInfoVO.getMainCurrency());
//            String apiKey = venueInfoVOResponseVO.getData().getMerchantKey();
//            UserCoinWalletVO userCoin = getUserCoin(userId, userInfoVO.getSiteCode());
//            LgdDataResp dataResp = new LgdDataResp();
//            dataResp.setId(orderNo);
//            dataResp.setMethod("bet");
//            dataResp.setLb(null);
//            dataResp.setPlayerPrice(userCoin.getTotalAmount().toString());
//            dataResp.setPlayerName(request.getPlayerName());
//            dataResp.setSign(MD5Util.md5(dataResp.getId() + dataResp.getMethod() + dataResp.getPlayerName() + dataResp.getPlayerPrice() + apiKey));
//
//            return resp.setData(dataResp);
//        } catch (Exception e) {
//            log.error("lgd bet error ", e);
//            resp.setCODE("4004");
//            return resp;
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//
//    }
//
//    @Override
//    public LgdResp errorBet(RequestVO request) {
//
//        String orderNo = request.getId();
//        BigDecimal amount = request.getCreditRemit();
//        LgdResp resp = LgdResp.success();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.LGD, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("lgd errorBet error get locker error, req:{}", request);
//                return resp.setCODE("4003").setMSG("get locker error");
//            }
//            LgdDataResp lgdDataResp = new LgdDataResp();
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinBetRecRespVO.isOk()) {
//                log.error("lgd errorBet error feign error {}.", userCoinBetRecRespVO);
//                lgdDataResp.setProcessStatus("2");
//                resp.setCODE("4004").setMSG("bet not exist");
//                return resp;
//            }
//
//            if (CollectionUtil.isEmpty(userCoinBetRecRespVO.getData())) {
//                // 订单不存在 则废弃订单
//                lgdDataResp.setProcessStatus("2");
//                resp.setData(lgdDataResp);
//                return resp;
//            }
//            orderNo = "1" + orderNo;
//            coinRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> userCoinErrBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinErrBetRecRespVO.isOk()) {
//                log.error("lgd errorBet error feign error {}.", userCoinErrBetRecRespVO);
//                lgdDataResp.setProcessStatus("2");
//                resp.setCODE("4004").setMSG("bet not exist");
//                return resp;
//            }
//            if (CollectionUtil.isNotEmpty(userCoinErrBetRecRespVO.getData())) {
//                // 订单已经处理过
//                lgdDataResp.setProcessStatus("2");
//                resp.setData(lgdDataResp);
//                return resp;
//            }
//            String userId = userCoinBetRecRespVO.getData().get(0).getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//
//            if (venueMaintainClosed(VenueCodeConstants.LGD,userInfoVO.getSiteCode())) {
//                return LgdResp.err("4004", "venue close");
//            }
//
//            if (Objects.isNull(userInfoVO)) {
//                log.error("lgd errorBet error queryUserInfoByAccount userName[{}] not find.", userId);
//                lgdDataResp.setProcessStatus("2");
//                resp.setCODE("4004").setMSG("member not exist");
//                return resp;
//            }
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateLgdBalanceResettle(userInfoVO, orderNo, amount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ResponseVO<Boolean> res = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//            };
//
//            if (!res.isOk()) {
//                lgdDataResp.setProcessStatus("2");
//                resp.setCODE("4004").setMSG("member not exist").setData(lgdDataResp);
//                return resp;
//            }
//
////            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
////            casinoMemberReqVO.setVenueCode(VenueCode.LGD);
////            casinoMemberReqVO.setUserId(userInfoVO.getUserId());
////            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
////            CasinoMemberRespVO casinoMember = respVO.getData();
//
//            // 发送注单
//            /****OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//             BeanUtil.copyProperties(request, orderRecordMqVO,"id");
//             BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//             orderRecordMqVO.setBetAmount(request.getBetPrice());
//             orderRecordMqVO.setValidAmount(request.getBetPrice());
//             orderRecordMqVO.setBetTime(request.getTimestamp());
//
//             BigDecimal betWin = Objects.nonNull(request.getBetWins())?request.getBetWins():BigDecimal.ZERO;
//             BigDecimal betPrice = Objects.nonNull(request.getBetPrice())?request.getBetPrice():BigDecimal.ZERO;
//             orderRecordMqVO.setWinLossAmount(betWin.subtract(betPrice));
//
//             // orderRecordMqVO.setWinLossAmount(request.getBetWins());
//             orderRecordMqVO.setPayoutAmount(request.getBetWins().add(request.getBetPrice()));
//             orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + orderNo);
//             orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//             orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//             orderRecordMqVO.setThirdOrderId(orderNo);
//             orderRecordMqVO.setSettleTime(request.getTimestamp());
//             orderRecordMqVO.setOrderClassify(ClassifyEnum.RESETTLED.getCode());
//             orderRecordMqVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
//             orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
//             orderRecordMqVO.setFreeGame(request.getFcid() != null);
//             orderRecordMqVO.setVenueCode(VenueCode.LGD);
//             KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);***/
//
//            lgdDataResp.setProcessStatus("2");
//            resp.setData(lgdDataResp);
//            String key= CacheConstants.ERROR_ORDER_NO+ VenueCodeConstants.LGD+":"+request.getId();
//            RedisUtil.setValue(key,request.getId(), 31L, TimeUnit.DAYS);
//            return resp;
//        } catch (Exception e) {
//            log.error("lgd cancel bet error ", e);
//            resp.setCODE("4004").setMSG("error bet err");
//            return resp;
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//
//    }
//
//    protected CoinRecordResultVO updateLgdBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//
//    }
//}
