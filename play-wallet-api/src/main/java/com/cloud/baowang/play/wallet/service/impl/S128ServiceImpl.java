//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.OrderUtil;
//import com.cloud.baowang.common.core.utils.TimeZoneUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.kafka.constants.TopicsConstants;
//import com.cloud.baowang.common.kafka.utils.KafkaUtil;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.enums.ClassifyEnum;
//import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
//import com.cloud.baowang.play.api.enums.s128.S128BetErrorCodeEnum;
//import com.cloud.baowang.play.api.enums.s128.S128GetBalanceErrorCodeEnum;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.service.S128Service;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.s128.BetReq;
//import com.cloud.baowang.play.wallet.vo.req.s128.CancelBetReq;
//import com.cloud.baowang.play.wallet.vo.req.s128.GetBalanceReq;
//import com.cloud.baowang.play.wallet.vo.res.s128.BetRes;
//import com.cloud.baowang.play.wallet.vo.res.s128.CancelBetRes;
//import com.cloud.baowang.play.wallet.vo.res.s128.GetBalanceRes;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
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
//@Service
//@AllArgsConstructor
//@Slf4j
//public class S128ServiceImpl extends BaseService implements S128Service {
//    private final UserCoinApi userCoinApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//    private final UserInfoApi userInfoApi;
//    private final PlayVenueInfoApi playVenueInfoApi;
//    private final CasinoMemberApi casinoMemberApi;
//
//
//    @Override
//    public GetBalanceRes getBalance(GetBalanceReq req) {
//        String agentCode = req.getAgentCode();
//        if (StringUtils.isEmpty(agentCode)) {
//            log.error("s128 getBalance error: agent_code is empty, req: {}", req);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "agent_code is empty");
//        }
//        String apiKey = req.getApiKey();
//        if (StringUtils.isEmpty(apiKey)) {
//            log.error("s128 getBalance error: api_key is empty, req: {}", req);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "api_key is empty");
//        }
//        String loginId = req.getLoginId();
//        if (StringUtils.isEmpty(loginId)) {
//            log.error("s128 getBalance error: login_id is empty, req: {}", req);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "login_id is empty");
//        }
//        CasinoMemberReqVO memberReq = new CasinoMemberReqVO();
//        memberReq.setVenueUserAccount(loginId);
//        memberReq.setVenueCode(VenueCodeConstants.S128);
//        ResponseVO<CasinoMemberRespVO> casinoMemberRes = casinoMemberApi.getCasinoMember(memberReq);
//        CasinoMemberRespVO casinoMember = casinoMemberRes.getData();
//        if(casinoMember == null || casinoMember.getUserId()==null){
//            log.error("s128 getBalance error: casinoMember not exist req: {}", req);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.LOGIN_NOT_FOUND, null);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//        //NOTE 查询场馆状态
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueCodeConstants.S128)
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = responseVO.getData();
//        if (Objects.isNull(venueInfoVO)|| !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//            log.info("getBalance 斗鸡场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "system error");
//        }
//        if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//            log.error("s128 getBalance error: current merchant closed  req: {}", req);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "game lock");
//        }
//        if (userGameLock(userInfoVO)) {
//            log.error("s128 bet error  userInfoVO[{}] game lock.", userInfoVO);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "game lock");
//        }
//
//        return GetBalanceRes.success(userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder()
//                .userId(userInfoVO.getUserId()).userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build()).getTotalAmount());
//    }
//
//    @Override
//    public BetRes bet(BetReq req) {
//
//        if (req.valid()) {
//            log.error("s128 bet error: param is empty, req: {}", req);
//            return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "param error");
//        }
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.S128, req.getTicketId()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("s128 bet error get locker error, req:{}", req);
//                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "repeat request");
//            }
//            String loginId = req.getLoginId();
//            String agentCode = req.getAgentCode();
//            String apiKey = req.getApiKey();
//
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(loginId);
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.S128);
//            ResponseVO<CasinoMemberRespVO> casinoRespVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//
//            CasinoMemberRespVO casinoMember = casinoRespVO.getData();
//            if (Objects.isNull(casinoMember) || StrUtil.isEmpty(casinoMember.getUserId())) {
//                log.error("s128 bet error: casinoMember not exist req: {}", req);
//                return BetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
//            }
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//
//            String userId = casinoMember.getUserId();
//            if (Objects.isNull(userInfoVO)) {
//                log.error("s128 bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return BetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
//            }
//            if (userGameLock(userInfoVO)) {
//                log.error("s128 bet error  userName[{}] game lock.", userId);
//                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "game lock");
//            }
//            //NOTE 查询场馆状态
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.S128)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = responseVO.getData();
//            if (!responseVO.isOk()|| venueInfoVO==null
//                    || !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//                log.info("斗鸡场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
//            }
//
//            if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.S128.getVenueCode());
//                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "game lock");
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getStakeMoney()) < 0) {
//                log.error("s128 bet error wallet is not exist user account:{}.", userId);
//                return BetRes.fail(S128BetErrorCodeEnum.INSUFFICIENT_FUND, null);
//            }
//            BigDecimal transferAmount = req.getStakeMoney();
//            String txid = req.getTicketId();
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBet(userInfoVO, txid, transferAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            BetRes resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> BetRes.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL ->
//                        BetRes.fail(S128BetErrorCodeEnum.INSUFFICIENT_FUND, null);
//                case REPEAT_TRANSACTIONS -> BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "err: repeat_transaction");
//            };
//
//            UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            resp.setBalance(afterCoin.getTotalAmount());
//            resp.setRefId(txid);
//            if (!resp.isOk()) {
//                return resp;
//            }
//
//            // 注单发送
//
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setBetTime(TimeZoneUtils.parseDate4TimeZoneToTime(req.getCreatedDatetime(),TimeZoneUtils.patten_yyyyMMddHHmmss,TimeZoneUtils.ShangHaiTimeZone));
//
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setThirdOrderId(txid);
//            orderRecordMqVO.setGameName(req.getArenaCode());
//            //非结算增加gameNo的显示.
//            orderRecordMqVO.setGameNo(req.getArenaCode());
//            orderRecordMqVO.setGameCode(req.getArenaCode());
//            orderRecordMqVO.setBetContent(req.getBetOn());
//            orderRecordMqVO.setBetAmount(BigDecimal.valueOf(req.getStake()));
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
//            //屏蔽商户账号密钥
//            req.setAgentCode(null);
//            req.setApiKey(null);
//            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
//            orderRecordMqVO.setEventInfo(req.getMatchNo());
//            orderRecordMqVO.setOdds(req.getOddsGiven().toPlainString());
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("s128 bet error ", e);
//            return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet error");
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//
//    @Override
//    public CancelBetRes cancelBet(CancelBetReq req) {
//
//        if (req.valid()) {
//            log.error("s128 cancelBet error: param is empty, req: {}", req);
//            return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "param error");
//        }
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.S128, req.getTicketId()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("s128 cancelBet error get locker error, req:{}", req);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "repeat request");
//            }
//            String loginId = req.getLoginId();
//            String agentCode = req.getAgentCode();
//            String apiKey = req.getApiKey();
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(loginId);
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.S128);
//            ResponseVO<CasinoMemberRespVO> casinoMemberRespVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//
//            CasinoMemberRespVO casinoMember = casinoMemberRespVO.getData();
//            if (Objects.isNull(casinoMember)|| StrUtil.isEmpty(casinoMember.getUserId())) {
//                log.error("s128 cancelBet error: casinoMember not exist req: {}", req);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
//            }
//
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//            //NOTE 查询场馆状态
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.S128)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = responseVO.getData();
//            if (!responseVO.isOk()|| venueInfoVO==null
//                    || !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//                log.info("斗鸡取消投注, 场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
//            }
//
//            /*if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("斗鸡取消投注，该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.S128.getVenueCode());
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "game lock");
//            }*/
//
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(req.getTicketId());
//            coinRecordRequestVO.setUserAccount(casinoMember.getUserAccount());
//            ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinRecords.isOk() || CollectionUtil.isEmpty(userCoinRecords.getData())) {
//                log.error("s128 cancel bet error feign error or order not exist. feign resp{}.", userCoinRecords);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet record not exist");
//            }
//
//            coinRecordRequestVO.setOrderNo(req.getTicketId());
//            coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//            ResponseVO<List<UserCoinRecordVO>> userCoinRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinRecRespVO.isOk() || CollectionUtil.isNotEmpty(userCoinRecRespVO.getData())) {
//                log.error("s128 cancel bet error feign error or already settled.feign resp{}.", userCoinRecRespVO);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet record already settled");
//            }
//
//            List<UserCoinRecordVO> betCoinRecords = userCoinRecords.getData();
//            UserCoinRecordVO userCoinRecordVO = betCoinRecords.get(0);
//            BigDecimal transferAmount = userCoinRecordVO.getCoinValue();
//            String txid = req.getTicketId();
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, txid, transferAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            CancelBetRes resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> CancelBetRes.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    yield CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "canceled failed");
//                }
//                case REPEAT_TRANSACTIONS -> {
//                    yield CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "already canceled");
//                }
//            };
//
//            if (!resp.isOk()) {
//                return resp;
//            }
//            // 注单发送
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(req, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setWinLossAmount(BigDecimal.ZERO);
//            orderRecordMqVO.setOrderId(OrderUtil.getGameNo());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setThirdOrderId(req.getTicketId());
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("s128 cancel bet error ", e);
//            return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "cancel bet error");
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//
//}
