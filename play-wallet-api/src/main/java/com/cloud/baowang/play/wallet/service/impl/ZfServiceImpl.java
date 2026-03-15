//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.constants.CommonConstant;
//import com.cloud.baowang.common.core.enums.CurrencyEnum;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.user.api.enums.UserStatusEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.common.kafka.constants.TopicsConstants;
//import com.cloud.baowang.common.kafka.utils.KafkaUtil;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.enums.ClassifyEnum;
//import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
//import com.cloud.baowang.play.api.enums.zf.ZfBetErrorCodeEnum;
//import com.cloud.baowang.play.api.enums.zf.ZfCancelBetErrorCodeEnum;
//import com.cloud.baowang.play.api.enums.zf.ZfErrorCodeEnum;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.service.ZfService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.zf.ZfAuthReq;
//import com.cloud.baowang.play.wallet.vo.req.zf.ZfBetReq;
//import com.cloud.baowang.play.wallet.vo.req.zf.ZfCancelBetReq;
//import com.cloud.baowang.play.wallet.vo.req.zf.ZfCancelSessionBetReq;
//import com.cloud.baowang.play.wallet.vo.req.zf.ZfSessionBetReq;
//import com.cloud.baowang.play.wallet.vo.res.zf.ZfBetResp;
//import com.cloud.baowang.play.wallet.vo.res.zf.ZfCancelBetResp;
//import com.cloud.baowang.play.wallet.vo.res.zf.ZfResp;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//
//
//@Service
//@Slf4j
//@AllArgsConstructor
//public class ZfServiceImpl extends BaseService implements ZfService {
//
//    private final static String KVND = "kVND";
//    private final CasinoMemberApi casinoMemberApi;
//    private final UserCoinApi userCoinApi;
//    private final UserInfoApi userInfoApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//    @Override
//    public ZfResp auth(ZfAuthReq req, String venueCode) {
//
//        String token = req.getToken();
//        if (StringUtils.isEmpty(token)) {
//            return ZfResp.fail(ZfErrorCodeEnum.TOKEN_EXPIRED);
//        }
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(req.getToken());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return ZfResp.fail(ZfErrorCodeEnum.TOKEN_EXPIRED);
//        }
//
//        CasinoMemberRespVO casinoMember = respVO.getData();
//
//
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return ZfResp.fail(ZfErrorCodeEnum.OTHER_ERR);
//        }
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//
//        ZfResp resp = ZfResp.success();
//        resp.setUsername(casinoMember.getVenueUserAccount());
//        resp.setBalance(balance);
//        resp.setCurrency(userCenterCoin.getCurrency());
//        if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
//            resp.setCurrency(KVND);
//        }
//        return resp;
//    }
//
//    @Override
//    public ZfBetResp bet(ZfBetReq req, String venueCode) {
//
//        if (!req.valid()) {
//            log.error("zf bet error invalid parameter req:{}", req);
//            return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
//        }
//
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("zf bet error get locker error, req:{}", req);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setCasinoPassword(req.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("zf bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(venueCode)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueCode);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            // 游戏锁定
//            if (userGameLock(userInfoVO)) {
//                log.error("zf bet error user game lock userName[{}].", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
//                log.error("zf bet error currency support usd only; req:{}", req);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0) {
//                log.error("zf bet error wallet is not exist user account:{}.", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
//            }
//            BigDecimal betAmount = req.getBetAmount();
//            BigDecimal winloseAmount = req.getWinloseAmount();
//
//
//            // 账变
//            // 交易订单No为  round
//            String txid = req.getRound();
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, txid, betAmount,winloseAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ZfBetResp resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> ZfBetResp.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
//                    zfBetResp.setTxId(txid);
//                    yield zfBetResp;
//                }
//                case REPEAT_TRANSACTIONS -> {
//                    ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//                    zfBetResp.setTxId(txid);
//                    yield zfBetResp;
//                }
//            };
//            UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            resp.setBalance(afterCoin.getTotalAmount());
//            resp.setUsername(casinoMember.getVenueUserAccount());
//            resp.setCurrency(userCenterCoin.getCurrency());
////          无需处理 update by xiaozhi 20250304
////            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
////                resp.setCurrency(KVND);
////            }
//
//            /*if (!resp.isOk() || betAmount.compareTo(BigDecimal.ZERO) == 0 && (req.getIsFreeRound() != null || req.getFreeSpinData() != null)) {
//                // 如果投注金额为0，但是不是免费旋转则是其他活动派彩，不需要发注单信息mq
//                return resp;
//            }*/
//            if (!resp.isOk()){
//                return resp;
//            }
//            // 注单发送
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setBetTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
//            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
//            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setThirdOrderId(txid);
//            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
//            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
////            orderRecordMqVO.setGameType(req.getGameCategory().toString());
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
//            orderRecordMqVO.setOrderStatus(getOrderStatus(orderRecordMqVO.getWinLossAmount()));
//            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("zf bet error ", e);
//            return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//
//
//    public CoinRecordResultVO updateBalancePayout(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//
//
//    @Override
//    public ZfCancelBetResp cancelBet(ZfCancelBetReq req, String venueCode) {
//
//        if (!req.valid()) {
//            log.error("zf cancel bet error invalid parameter req:{}", req);
//            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
//        }
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("zf bet error get locker error, req:{}", req);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setCasinoPassword(req.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//
//            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("zf cancel bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
//                log.error("zf cancel bet error currency support usd only; req:{}", req);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
//            }
//            BigDecimal transferAmount = req.getBetAmount().subtract(req.getWinloseAmount());
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().add(transferAmount).compareTo(BigDecimal.ZERO) < 0) {
//                log.error("zf cancel bet error wallet is not exist user account:{}.", userId);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
//            }
//
//            // 账变
//            // 交易订单No为  round
//            String txid = req.getRound();
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, txid,
//                    transferAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ZfCancelBetResp resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> ZfCancelBetResp.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
//                    zfBetResp.setTxId(txid);
//                    yield zfBetResp;
//                }
//                case REPEAT_TRANSACTIONS -> {
//                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.ALREADY_CANCELED);
//                    zfBetResp.setTxId(req.getRound());
//                    yield zfBetResp;
//                }
//            };
//            UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            resp.setBalance(afterCoin.getTotalAmount());
//            resp.setUsername(casinoMember.getVenueUserAccount());
//            resp.setCurrency(userCenterCoin.getCurrency());
//            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
//                resp.setCurrency(KVND);
//            }
//            if (!resp.isOk()) {
//                return resp;
//            }
//            // 注单发送
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
//            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + req.getRound());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setThirdOrderId(req.getRound());
//            // 重结算
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.RESETTLED.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
//
//            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
//
//
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("zf cancel bet error ", e);
//            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    @Override
//    public ZfBetResp sessionBet(ZfSessionBetReq req, String venueCode) {
//
//        if (!req.valid()) {
//            log.error("zf session bet error invalid parameter req:{}", req);
//            return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
//        }
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getSessionId()));
//        try {
//            boolean lockFlag = rLock.tryLock();
//            if (!lockFlag) {
//                log.error("zf session bet error get locker error,lockerId:{}", req.getSessionId());
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setCasinoPassword(req.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("zf session bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            // 游戏锁定
//            if (UserStatusEnum.GAME_LOCK.getCode().equals(userInfoVO.getAccountStatus())) {
//                log.error("zf bet error user game lock userName[{}].", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//            }
//            if (!req.getCurrency().equals("kVND") && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
//                log.error("zf session bet error currency support usd only; req:{}", req);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if ("1".equals(req.getType()) && (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0)) {
//                log.error("zf session bet error wallet is not exist user account:{}.", userId);
//                return ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
//            }
//
//            // 1=下注 2=结算
//            String type = req.getType();
//            Integer gameCategory = req.getGameCategory();
//            String txid = req.getSessionId();
//            if (gameCategory != null && (gameCategory == 8 || gameCategory == 2)){
//                txid = req.getSessionId() + "_" + req.getRound();
//            }
//            ZfBetResp resp = null;
//            if (CommonConstant.business_one.toString().equals(type)) {
//                BigDecimal transferAmount = req.getBetAmount();
//                // 下注
//                if (req.getPreserve().compareTo(BigDecimal.ZERO) > 0) {
//                    // 有preserve
//                    transferAmount = req.getPreserve();
//                }
//
//                CoinRecordResultVO coinRecordResultVO = updateBalanceBet(userInfoVO, txid, transferAmount);
//                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//                resp = switch (resultStatus) {
//                    case SUCCESS -> ZfBetResp.success();
//                    case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, AMOUNT_LESS_ZERO, FAIL -> {
//                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
//                        zfBetResp.setTxId(txid);
//                        yield zfBetResp;
//                    }
//                    case REPEAT_TRANSACTIONS -> {
//                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//                        zfBetResp.setTxId(txid);
//                        yield zfBetResp;
//                    }
//                };
//                UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//                resp.setBalance(afterCoin.getTotalAmount());
//                if (!resp.isOk()) {
//                    return resp;
//                }
//
//            } else if (CommonConstant.business_two.toString().equals(type)) {
//                // 结算
//                BigDecimal transferAmount = req.getWinloseAmount();
//                if (req.getPreserve().compareTo(BigDecimal.ZERO) > 0) {
//                    transferAmount = req.getPreserve().subtract(req.getBetAmount()).add(req.getWinloseAmount());
//                }
//                CoinRecordResultVO coinRecordResultVO = this.updateBalancePayout(userInfoVO, txid, transferAmount);
//                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//                resp = switch (resultStatus) {
//                    case SUCCESS, AMOUNT_LESS_ZERO -> ZfBetResp.success();
//                    case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//                        zfBetResp.setTxId(txid);
//                        yield zfBetResp;
//                    }
//                    case REPEAT_TRANSACTIONS -> {
//                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//                        zfBetResp.setTxId(txid);
//                        yield zfBetResp;
//                    }
//                };
//                UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//                resp.setBalance(afterCoin.getTotalAmount());
//                resp.setUsername(casinoMember.getVenueUserAccount());
//                resp.setCurrency(userCenterCoin.getCurrency());
//                if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
//                    resp.setCurrency(KVND);
//                }
//                if (!resp.isOk()) {
//                    return resp;
//                }
//            }
//            // 注单发送
//            Integer orderClassify = ClassifyEnum.NOT_SETTLE.getCode();
//            Integer orderStatus = OrderStatusEnum.NOT_SETTLE.getCode();
//            Long betTime = null;
//            Long settleTime = null;
//            BigDecimal betAmount = null;
//            BigDecimal winloseAmount = BigDecimal.ZERO;
//            if (CommonConstant.business_one.toString().equals(type)) {
//                // 投注
//                betTime = req.getWagersTime();
//                betAmount = req.getBetAmount();
//            } else {
//                // 结算
//                settleTime = req.getWagersTime();
//                winloseAmount = req.getWinloseAmount();
//                orderClassify = ClassifyEnum.SETTLED.getCode();
//                betAmount = req.getSessionTotalBet();
//            }
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//
//
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setThirdOrderId(req.getSessionId());
//            orderRecordMqVO.setSettleTime(settleTime == null ? null : settleTime * 1000);
//            orderRecordMqVO.setBetTime(betTime == null ? null : betTime * 1000);
//            if(orderClassify.equals(ClassifyEnum.SETTLED.getCode())){
//                orderRecordMqVO.setPayoutAmount(winloseAmount);
//                orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(betAmount));
//                orderStatus = getOrderStatus(req.getWinloseAmount().subtract(betAmount));
//            }
//            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
////            orderRecordMqVO.setGameType(req.getGameCategory().toString());
//            orderRecordMqVO.setOrderClassify(orderClassify);
//            orderRecordMqVO.setOrderStatus(orderStatus);
//            orderRecordMqVO.setBetAmount(betAmount);
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("zf session bet error ", e);
//            return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//    private Integer getOrderStatus(BigDecimal winloseAmount){
//        if (winloseAmount.compareTo(BigDecimal.ZERO) == 0){
//            return OrderStatusEnum.DRAW.getCode();
//        } else if (winloseAmount.compareTo(BigDecimal.ZERO) > 0) {
//            return OrderStatusEnum.WIN.getCode();
//        } else if (winloseAmount.compareTo(BigDecimal.ZERO) < 0) {
//            return OrderStatusEnum.LOSS.getCode();
//        }
//        return OrderStatusEnum.SETTLED.getCode();
//    }
//    @Override
//    public ZfCancelBetResp cancelSessionBet(ZfCancelSessionBetReq req, String venueCode) {
//
//
//        if (!req.valid()) {
//            log.error("zf cancel bet error invalid parameter req:{}", req);
//            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
//        }
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("zf bet error get locker error, req:{}", req);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setCasinoPassword(req.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("zf cancel bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
//            }
//            if (venueMaintainClosed(venueCode,userInfoVO.getSiteCode())) {
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//            }
//            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
//                log.error("zf cancel bet error currency support usd only; req:{}", req);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
//            }
//
//            BigDecimal transferAmount = req.getBetAmount().subtract(req.getWinloseAmount());
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().add(transferAmount).compareTo(BigDecimal.ZERO) < 0) {
//                log.error("zf cancel bet error wallet is not exist user account:{}.", userId);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
//            }
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(req.getSessionId());
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinBetRecRespVO.isOk() || CollectionUtil.isEmpty(userCoinBetRecRespVO.getData())) {
//                log.error("zf cancel bet error feign error or order not exsit.feign resp{}.", userCoinBetRecRespVO);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.ROUND_NOT_FOUND);
//            }
//            coinRecordRequestVO.setOrderNo("2" + req.getSessionId());
//            ResponseVO<List<UserCoinRecordVO>> userCoinRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinRecRespVO.isOk() || CollectionUtil.isNotEmpty(userCoinRecRespVO.getData())) {
//                log.error("zf cancel bet error feign error or already settled.feign resp{}.", userCoinRecRespVO);
//                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
//            }
//            // 账变
//            // 交易订单No为  sesssionId
//            String txid = req.getSessionId() + "_" + req.getRound();
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, txid, transferAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ZfCancelBetResp resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> ZfCancelBetResp.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
//                    zfBetResp.setTxId(txid);
//                    yield zfBetResp;
//                }
//                case REPEAT_TRANSACTIONS -> {
//                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//                    zfBetResp.setTxId(req.getRound());
//                    yield zfBetResp;
//                }
//            };
//            UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            resp.setBalance(afterCoin.getTotalAmount());
//            resp.setUsername(casinoMember.getVenueUserAccount());
//            resp.setCurrency(userCenterCoin.getCurrency());
//            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
//                resp.setCurrency(KVND);
//            }
//            if (!resp.isOk()) {
//                return resp;
//            }
//
//             // 注单发送
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
//            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + req.getRound());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setThirdOrderId(req.getSessionId());
//            // 重结算
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
//            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
//
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("zf cancel bet error ", e);
//            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
//
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {
//
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//}
