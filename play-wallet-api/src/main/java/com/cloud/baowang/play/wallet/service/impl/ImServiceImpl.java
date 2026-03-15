//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.CurrencyEnum;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.DateUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.kafka.constants.TopicsConstants;
//import com.cloud.baowang.common.kafka.utils.KafkaUtil;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.enums.ClassifyEnum;
//import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.service.ImService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.im.ImReq;
//import com.cloud.baowang.play.wallet.vo.res.im.ImResp;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Objects;
//import java.util.concurrent.*;
//
//@Slf4j
//@Service
//public class ImServiceImpl extends BaseService implements ImService {
//
//    private final static String  KVND = "VNDK";
//
//
//    @Resource
//    private PlayVenueInfoApi playVenueInfoApi;
//
//    @Override
//    public ImResp getBalance(ImReq request) {
//
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(request.getLogin());
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.IM);
//        ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        CasinoMemberRespVO casinoMemberData = casinoMember.getData();
//        if (Objects.isNull(casinoMemberData)){
//            return ImResp.err("user not exist");
//        }
//        if (venueMaintainClosed(VenueCodeConstants.IM,casinoMemberData.getSiteCode())){
//            log.info("{}:场馆未开启", VenueEnum.IM.getVenueName());
//            return ImResp.err("venue not open");
//        }
//        String userAccount = casinoMemberData.getUserAccount();
//        String siteCode = casinoMemberData.getSiteCode();
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build());
//        if (Objects.isNull(userCenterCoin)) {
//            return ImResp.err("wallet not exist");
//        }
//        ImResp ret = ImResp.success();
//        ret.setBalance(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN));
//        ret.setLogin(request.getLogin());
//        ret.setCurrency(userCenterCoin.getCurrency());
//        if(CurrencyEnum.KVND.getCode().equals(userCenterCoin.getCurrency())){
//            ret.setCurrency(KVND);
//        }
//
//        return ret;
//    }
//    ExecutorService executor = Executors.newCachedThreadPool();
//
//    @Override
//    public ImResp writeBet(ImReq request) {
//
//        Future<ImResp> future = executor.submit(() -> doWriteBet(request));
//        try {
//            // 设置超时时间为 6 秒
//            return future.get(6, TimeUnit.SECONDS);
//        } catch (TimeoutException e) {
//            log.error("writeBet 超时，触发 fallback, {}", e.getMessage());
//            future.cancel(true);
//            return ImResp.err("system err");
//        } catch (Exception e) {
//            log.error("writeBet 异常，触发 fallback {}", e.getMessage());
//            return ImResp.err("system err");
//        }
//    }
//
//    public ImResp doWriteBet(ImReq request) {
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.IM, request.getSessionId()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("im bet error get locker error, req:{}", request);
//                return ImResp.err("system err");
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getLogin());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.IM);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ImResp.err("system err");
//            }
//            if (venueMaintainClosed(VenueCodeConstants.IM,casinoMember.getSiteCode())){
//                log.info("{}:场馆未开启", VenueEnum.IM.getVenueName());
//                return ImResp.err("venue not open");
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("im bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ImResp.err("user_not_found");
//            }
//            // 游戏锁定
//            if (userGameLock(userInfoVO)) {
//                log.error("im bet error user game lock userName[{}].", userId);
//                return ImResp.err("game lock");
//            }
//
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.IM)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO2 = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO2.getData();
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueCodeConstants.IM);
//                return ImResp.err("game lock");
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(request.getBet()) < 0) {
//                log.error("im bet error wallet is not exist user account:{}.", userId);
//                return ImResp.err("fail_balance");
//            }
//            BigDecimal winloseAmount = request.getWin();
//            BigDecimal betAmount = request.getBet();
//
//            // 账变
//            // 交易订单No为  sessionId
//            String txid = request.getTradeId();
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, txid, betAmount, winloseAmount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ImResp resp = switch (resultStatus) {
//                case SUCCESS ,AMOUNT_LESS_ZERO -> ImResp.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    yield ImResp.err("fail_balance");
//                }
//                case REPEAT_TRANSACTIONS -> {
//                    yield ImResp.err("already accepted");
//                }
//            };
//
//
//            UserCoinWalletVO afterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            resp.setBalance(afterCoin.getTotalAmount());
//            resp.setLogin(casinoMember.getVenueUserAccount());
//            resp.setCurrency(afterCoin.getCurrency());
//            if(CurrencyEnum.KVND.getCode().equals(afterCoin.getCurrency())){
//                resp.setCurrency(KVND);
//            }
//
//            if(!resp.isOk()){
//                return resp;
//            }
//
//            // 注单
//            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(request, orderRecordMqVO);
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
//            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
//            orderRecordMqVO.setThirdOrderId(txid);
//            String dateStr = request.getDate();
//            if (StringUtils.isNotEmpty(dateStr)){
//                orderRecordMqVO.setBetTime(System.currentTimeMillis());
//                try {
//                    Long date = DateUtils.parseDate(dateStr, "UTC-0").getTime();
//                    orderRecordMqVO.setSettleTime(date);
//                }catch (Exception e){
//                    log.error("im 注单时间解析错误, ",e);
//                }
//            }
//            orderRecordMqVO.setBetAmount(request.getBet());
//
//            winloseAmount = Objects.isNull(winloseAmount)?BigDecimal.ZERO:winloseAmount;
//            orderRecordMqVO.setPayoutAmount(winloseAmount);
//            orderRecordMqVO.setWinLossAmount(winloseAmount.subtract(request.getBet()));
//            orderRecordMqVO.setValidAmount(betAmount);
//
//            orderRecordMqVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
//            orderRecordMqVO.setAgentId(userInfoVO.getSuperAgentId());
//            orderRecordMqVO.setThirdGameCode(request.getGameId());
//            orderRecordMqVO.setGameCode(request.getGameId());
//            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
//            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
//            orderRecordMqVO.setBetContent(request.getBetInfo());
//            orderRecordMqVO.setSiteCode(casinoMember.getSiteCode());
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("im bet error ", e);
//            return ImResp.err("system err");
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//}
