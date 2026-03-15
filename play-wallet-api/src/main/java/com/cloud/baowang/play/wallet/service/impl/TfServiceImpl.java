//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.ResultCode;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
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
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.TfTransferTypeEnums;
//import com.cloud.baowang.play.wallet.service.TfService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.tf.TfOrderInfoVO;
//import com.cloud.baowang.play.wallet.vo.req.tf.TfTransferReq;
//import com.cloud.baowang.play.wallet.vo.req.tf.TfValidReq;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfTransferResp;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfValidResp;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfWalletResp;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class TfServiceImpl extends BaseService implements TfService {
//
//    private final CasinoMemberApi casinoMemberApi;
//    private final UserCoinApi userCoinApi;
//    private final UserInfoApi userInfoApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//
//    @Override
//    public TfValidResp validate(TfValidReq req) {
//        String token = req.getToken();
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(token);
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//        ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        TfValidResp resp = new TfValidResp();
//        if (!casinoMember.isOk()) {
//            log.info("tf valid 用户信息不存在，token:{}", req.getToken());
//            return resp;
//        }
//        resp.setLoginName(casinoMember.getData().getVenueUserAccount());
//        return resp;
//    }
//
//    @Override
//    public TfWalletResp wallet(String loginName, HttpServletResponse response) {
//
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(loginName);
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        TfWalletResp resp = new TfWalletResp();
//        if (!casinoMemberResp.isOk()) {
//            log.error("tf 三方余额查询失败，查询用户{}", loginName);
//            response.setStatus(400);
//            return resp;
//        }
//        CasinoMemberRespVO casinoMember = casinoMemberResp.getData();
//
//        if (venueMaintainClosed(VenueCodeConstants.TF,casinoMember.getSiteCode())){
//            log.info("{}: wallet 场馆未开启", VenueEnum.TF.getVenueName());
//            response.setStatus(400);
//            return  new TfWalletResp();
//        }
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//        if (userInfoVO == null) {
//            log.error("{} wallet userInfoVO不存在, bet, 参数:{} ", VenueEnum.TF.getVenueCode(), loginName);
//            response.setStatus(400);
//            return  new TfWalletResp();
//        }
//
//        if (userGameLock(userInfoVO)) {
//            log.error("{} wallet user game lock, user 参数:{} ", VenueEnum.TF.getVenueCode(), userInfoVO);
//            response.setStatus(400);
//            return  new TfWalletResp();
//        }
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//        resp.setBalance(balance);
//        return resp;
//    }
//
//    @Override
//    public TfTransferResp transfer(TfTransferReq req, HttpServletResponse response) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//        casinoMemberReqVO.setVenueUserAccount(req.getLoginName());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        if (venueMaintainClosed(VenueCodeConstants.TF,casinoMember.getSiteCode())){
//            log.info("{}:transfer 场馆未开启", VenueEnum.TF.getVenueName());
//            response.setStatus(400);
//            return  new TfTransferResp();
//        }
//        TfTransferTypeEnums type = getTransferType(req);
//        if (type == null) {
//            response.setStatus(400);
//            return new TfTransferResp();
//        }
//        ResponseVO<Boolean> resp = switch (type) {
//            case BET -> bet(req);
//            case CANCELLED -> cancelled(req);
//            case PAYOUT -> payout(req);
//            case LOSS -> ResponseVO.success(true);
//            case RESETTLEMENT -> resettle(req);
//        };
//        if (!resp.isOk()) {
//            response.setStatus(400);
//        }
//        return new TfTransferResp();
//    }
//
//    private ResponseVO<Boolean> cancelled(TfTransferReq req) {
//        BigDecimal amount = req.getAmount();
//        if (amount == null) {
//            amount = BigDecimal.ZERO;
//        }
//        String loginName = req.getLoginName();
//        String orderNo = req.getTicketDetail().getId();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.TF, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("tf cancel error get locker error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//            casinoMemberReqVO.setVenueUserAccount(loginName);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                log.error("tf cancel error casinoMember error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("tf cancel error userInfoVO error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("tf cancel error wallet error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinBetRecRespVO.isOk() || CollectionUtil.isEmpty(userCoinBetRecRespVO.getData())) {
//                log.error("tf cancel error userRecord error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancel(userInfoVO, orderNo, amount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            return switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//            };
//        } catch (Exception e) {
//            log.error("tf cancel error system error, req:{}", req);
//            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    @Override
//    public TfTransferResp rollback(TfTransferReq req, HttpServletResponse response) {
//        BigDecimal amount = req.getAmount();
//        if (amount == null) {
//            amount = BigDecimal.ZERO;
//        }
//        String loginName = req.getLoginName();
//        String orderNo = req.getTicketNum();
//        TfTransferResp resp = new TfTransferResp();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.TF, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("tf rollback error get locker error, req:{}", req);
//                response.setStatus(400);
//                return resp;
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//            casinoMemberReqVO.setVenueUserAccount(loginName);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                response.setStatus(400);
//                return resp;
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("tf rollback error queryUserInfoByAccount userName[{}] not find.", userId);
//                response.setStatus(400);
//                return resp;
//            }
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("tf rollback error wallet is not exist user account:{}.", userId);
//                response.setStatus(400);
//                return resp;
//            }
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(orderNo);
//            coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!userCoinBetRecRespVO.isOk() || CollectionUtil.isEmpty(userCoinBetRecRespVO.getData())) {
//                log.error("tf rollback bet error feign error or order not exist feign resp{}.", userCoinBetRecRespVO);
//                response.setStatus(400);
//                return resp;
//            }
//
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, orderNo, amount, req.getTicketNum());
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ResponseVO<Boolean> balanceResp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//            };
//            if (!balanceResp.isOk()) {
//                response.setStatus(400);
//                return resp;
//            }
//
//            // 注单发送
////            TfOrderInfoVO ticketDetail = req.getTicketDetail();
////            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
////            BeanUtil.copyProperties(ticketDetail, orderRecordMqVO);
////            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
////            String eventDateTime = ticketDetail.getEventDateTime();
////            orderRecordMqVO.setBetTime(ticketDetail.() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount());
////            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
////            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
////            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
////            orderRecordMqVO.setThirdOrderId(txid);
////            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
////            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("tf rollback error ", e);
//            response.setStatus(400);
//            return resp;
//
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//
//    }
//
//    private ResponseVO<Boolean> resettle(TfTransferReq req) {
//        BigDecimal amount = req.getAmount();
//        if (amount == null) {
//            amount = BigDecimal.ZERO;
//        }
//        String loginName = req.getLoginName();
//        String orderNo = req.getTicketNum();
//        String ticketNum = req.getTicketNum();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.TF, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("tf resettle error get locker error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//            casinoMemberReqVO.setVenueUserAccount(loginName);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("tf resettle error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("tf resettle error wallet is not exist user account:{}.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            //NOTE 验证重复提交
//            TfOrderInfoVO ticketDetail = req.getTicketDetail();
//
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (CollectionUtil.isNotEmpty(userCoinBetRecRespVO.getData())) {
//                if (userCoinBetRecRespVO.getData().stream().anyMatch(vo -> ticketNum.equals(vo.getRemark()))){
//                    log.error("tf resettle but operation has been done, resp{}.", userCoinBetRecRespVO);
//                    return ResponseVO.success(true);
//                }
//                if (userCoinBetRecRespVO.getData().stream().noneMatch(vo -> CoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType()))){
//                    log.error("tf resettle but payout operation no exist, resp{}.", userCoinBetRecRespVO);
//                    return ResponseVO.success(true);
//                }
//            }
//
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateBalanceResettle(userInfoVO, orderNo, amount, ticketNum);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ResponseVO<Boolean> resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//            };
//            if (!resp.isOk()) {
//                return resp;
//            }
//
//            // 注单发送
////            TfOrderInfoVO ticketDetail = req.getTicketDetail();
////            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
////            BeanUtil.copyProperties(ticketDetail, orderRecordMqVO);
////            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
////            String eventDateTime = ticketDetail.getEventDateTime();
////            orderRecordMqVO.setBetTime(ticketDetail.() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount());
////            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
////            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
////            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
////            orderRecordMqVO.setThirdOrderId(txid);
////            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
////            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("tf resettle error ", e);
//            return ResponseVO.fail(ResultCode.PARAM_ERROR);
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
//    private ResponseVO<Boolean> payout(TfTransferReq req) {
//        BigDecimal amount = req.getAmount();
//        if (amount == null) {
//            amount = BigDecimal.ZERO;
//        }
//        String loginName = req.getLoginName();
//        String orderNo = req.getTicketDetail().getId();
//        String ticketNum = req.getTicketNum();
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.TF, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("tf paytout error get locker error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//            casinoMemberReqVO.setVenueUserAccount(loginName);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("tf paytout error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("tf paytout error wallet is not exist user account:{}.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//
//            UserCoinAddVO userCoinAddVO = updateBalancePayout(userInfoVO, orderNo, amount, ticketNum);
//
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> userCoinBetRecRespVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//
//            if (CollectionUtil.isEmpty(userCoinBetRecRespVO.getData())) {
//                log.error("tf payout but bet operation no found, resp{}.", userCoinBetRecRespVO);
//                return ResponseVO.success(true);
//            }else {
//                if (userCoinBetRecRespVO.getData().stream().anyMatch(vo -> ticketNum.equals(vo.getRemark()))){
//                    log.error("tf payout but operation has been done, resp{}.", userCoinBetRecRespVO);
//                    return ResponseVO.success(true);
//                }
//                if (userCoinBetRecRespVO.getData().size()>1){
//                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
//                }
//            }
//            // 账变
//            UpdateBalanceStatusEnums resultStatus = userCoinApi.addCoin(userCoinAddVO).getResultStatus();
//            ResponseVO<Boolean> resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//            };
//            if (!resp.isOk()) {
//                return resp;
//            }
//
//            // 注单发送
////            TfOrderInfoVO ticketDetail = req.getTicketDetail();
////            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
////            BeanUtil.copyProperties(ticketDetail, orderRecordMqVO);
////            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
////            String eventDateTime = ticketDetail.getEventDateTime();
////            orderRecordMqVO.setBetTime(ticketDetail.() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount());
////            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
////            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
////            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
////            orderRecordMqVO.setThirdOrderId(txid);
////            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
////            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("tf paytout error ", e);
//            return ResponseVO.fail(ResultCode.PARAM_ERROR);
//
//        } finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    public UserCoinAddVO updateBalancePayout(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        //修改余额 记录账变
//        return userCoinAddVO;
//    }
//
//    /**
//     * 重结算, 逻辑,
//     * NOTE 正数, 就给玩家减钱
//     * NOTE 负数, 就给玩家加钱
//     */
//    protected CoinRecordResultVO updateBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        if (amount.compareTo(BigDecimal.ZERO)>0){
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        }else {
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        }
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//
//        userCoinAddVO.setRemark(remark);
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//
//    }
//
//    private ResponseVO<Boolean> bet(TfTransferReq req) {
//        BigDecimal amount = req.getAmount();
//        String loginName = req.getLoginName();
//        String orderNo = req.getTicketNum();
//
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueCodeConstants.TF, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("tf bet error get locker error, req:{}", req);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.TF);
//            casinoMemberReqVO.setVenueUserAccount(loginName);
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();
//            if (!respVO.isOk() || casinoMember == null) {
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("tf bet error queryUserInfoByAccount userName[{}] not find.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            if (userGameLock(userInfoVO)) {
//                log.error("tf bet error userName[{}] game lock.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.TF)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.TF.getVenueCode());
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getSiteCode()).build());
//            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(amount.abs()) < 0) {
//                log.error("tf bet error wallet is not exist user account:{}.", userId);
//                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//            }
//
//            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBet(userInfoVO, orderNo,
//                    amount);
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            ResponseVO<Boolean> resp = switch (resultStatus) {
//                case SUCCESS, AMOUNT_LESS_ZERO,REPEAT_TRANSACTIONS -> ResponseVO.success(true);
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
//                    yield ResponseVO.fail(ResultCode.PARAM_ERROR);
//                }
//
//            };
//            if (!resp.isOk()) {
//                return resp;
//            }
//
//            // 注单发送
////            TfOrderInfoVO ticketDetail = req.getTicketDetail();
////            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
////            BeanUtil.copyProperties(ticketDetail, orderRecordMqVO);
////            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
////            String eventDateTime = ticketDetail.getEventDateTime();
////            orderRecordMqVO.setBetTime(ticketDetail.() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount());
////            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
////            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
////            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
////            orderRecordMqVO.setThirdOrderId(txid);
////            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
////            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
////            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
////            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
//            return resp;
//        } catch (Exception e) {
//            log.error("tf bet error ", e);
//            return ResponseVO.fail(ResultCode.PARAM_ERROR);
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
//    private TfTransferTypeEnums getTransferType(TfTransferReq req) {
//        Boolean betFlag = req.getPlaceBet();
//        if (betFlag != null && betFlag) {
//            return TfTransferTypeEnums.BET;
//        }
//
//        if (TfTransferTypeEnums.PAYOUT.getDescription().equals(req.getDescription())) {
//            String resultStatus = req.getTicketDetail().getResultStatus();
//            if (resultStatus != null && resultStatus.equals(TfTransferTypeEnums.CANCELLED.getDescription())){
//                return TfTransferTypeEnums.CANCELLED;
//            }
//            return TfTransferTypeEnums.PAYOUT;
//        } else if (TfTransferTypeEnums.LOSS.getDescription().equals(req.getDescription())) {
//            return TfTransferTypeEnums.LOSS;
//        } else if (TfTransferTypeEnums.RESETTLEMENT.getDescription().equals(req.getDescription())) {
//            return TfTransferTypeEnums.RESETTLEMENT;
//        }
//
//        return null;
//    }
//
//
//}
