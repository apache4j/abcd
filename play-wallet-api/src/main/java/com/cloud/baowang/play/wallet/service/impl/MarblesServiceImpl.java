//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.constants.RedisConstants;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.CurrencyEnum;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
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
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.MarblesRespErrEnums;
//import com.cloud.baowang.play.wallet.service.MarblesService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.marbles.*;
//import com.cloud.baowang.play.wallet.vo.res.marbles.MarblesBalanceResp;
//import com.cloud.baowang.play.wallet.vo.res.marbles.MarblesPlaceBetResp;
//import com.cloud.baowang.play.wallet.vo.res.marbles.MarblesRefundResp;
//import com.cloud.baowang.play.wallet.vo.res.marbles.MarblesResp;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import com.google.common.collect.Lists;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//
///**
// *方呼叫您的 PlaceBet API 的请求参数里是会传入 BetID 的。但是当结算时，RefTransactionId 是对应下注时的 TransactionId。
// * 因此如贵司需要对账，贵司可以通过 TransactionId 查看结算或是退款的是哪一个 PlaceBet 的注单。而 PlaceBet API 里的 BetID 和 GetBetLog 里的 BetId 是一样的
// *
// * 意思： GetBetLog 的betID 和 PlaceBet 的betID 是一样的
// * refund的preTransactionId 和 PlaceBet 的TransactionId 是一样的
// *
// */
//@Slf4j
//@Service
//@AllArgsConstructor
//public class MarblesServiceImpl extends BaseService implements MarblesService {
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//
//    private CasinoMemberVO checkCasinoMemberVO(String playerId) {
//        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.MARBLES.getVenueCode(), playerId);
//        CasinoMemberVO casinoMemberVO = RedisUtil.getValue(key);
//        if (ObjectUtil.isNotEmpty(casinoMemberVO)) {
//            return casinoMemberVO;
//        }
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(playerId);
//        casinoMemberReqVO.setVenueCode(VenueCodeConstants.MARBLES);
//        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!casinoMemberResp.isOk()) {
//            log.info("MARBLES oauth 用户信息不存在，venueCode:{},token:{}", VenueCodeConstants.MARBLES, casinoMemberVO);
//            return null;
//        }
//        CasinoMemberVO memberVO = new CasinoMemberVO();
//        BeanUtil.copyProperties(casinoMemberResp.getData(), memberVO);
//        return memberVO;
//    }
//
//
//    /**
//     * 查询余额
//     */
//    @Override
//    public MarblesBalanceResp getBalance(MarblesReq req) {
//        CasinoMemberVO casinoMember = this.checkCasinoMemberVO(req.getPlayerId());
//        if (casinoMember == null) {
//            return  MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_NOT_EXIST);
//        }
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_INACTIVE);
//        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_INACTIVE);
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//        String mainCurrency = userInfoVO.getMainCurrency();
//
//        switch (mainCurrency) {
//            case "KVND":
//                mainCurrency = CurrencyEnum.VND.getCode();
//                break;
//            case "USDT", "USD":
//                mainCurrency = "UST";
//                break;
//            default:
//                break;
//        }
//
//        return MarblesBalanceResp.success(casinoMember.getVenueUserAccount(),mainCurrency,balance.toString());
//    }
//
//    @Override
//    public MarblesResp getApproval(MarblesApprovalReq req) {
//        return null;
//    }
//
//
//    /**
//     * 下注
//     * 不支持一个玩家多笔交易
//     *
//     * RefTransactionId 是会返回下注时的 TransactionId 哦。贵司需要把 RefTransactionId 和下注时的 TransactionId 进行对比
//     */
//    @Override
//    public MarblesPlaceBetResp placeBet(MarblesPlaceBetReq req) {
//        log.info("im marbles placeBet下注入参{}",JSONObject.toJSONString(req));
//        // 经与第三方确认，数组里面只有1条内容
//        PlaceBet placeBet = CollectionUtil.isNotEmpty(req.getTransactions()) && req.getTransactions().size() > 0 ?req.getTransactions().get(0):new PlaceBet();
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(placeBet.getPlayerId());
//        casinoMemberReqVO.setVenueCode(VenueEnum.MARBLES.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null, placeBet.getTransactionId(),null);
//        }
//        String userId = respVO.getData().getUserId();
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if(Objects.isNull(userInfoVO)){
//            log.error("im marbles queryUserInfoByAccount userName[{}] not find.",req.getSessionToken());
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null, placeBet.getTransactionId(),null);
//        }
//
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.MARBLES.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        String centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount().toString();
//        if(venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())){
//            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.MARBLES.getVenueCode());
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
//        }
//        // 场馆关闭
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            log.info("{}:场馆未开启", VenueEnum.MARBLES.getVenueName());
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
//        }
//        // 游戏锁定
//        if (userGameLock(userInfoVO)) {
//            log.error("marbles userName[{}] game lock.", userInfoVO.getUserName());
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
//        }
//
//        if (venueGameMaintainClosed(venueCode,casinoMember.getSiteCode(),placeBet.getGameId())){
//            log.info("{}:游戏未开启", VenueEnum.NEXTSPIN.getVenueName());
//            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
//        }
//        try {
//            RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, placeBet.getBetId()));
//            if (!rLock.tryLock()) {
//                log.error("marbles errorBet error get locker error, req:{}", req);
//                return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, req.getSessionToken(),null);
//            }
//
//            // 检查余额
//            if(!compareAmount(userInfoVO.getUserId(),userInfoVO.getSiteCode(),placeBet.getAmount())){
//                log.info("im marbles 用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),placeBet.getAmount());
//                return setPlaceBetBuilder(MarblesRespErrEnums.INSUFFICIENT_AMOUNT,centerAmount, placeBet.getTransactionId(),null);
//            }
//            //用户中心钱包余额
//            if(BigDecimal.ZERO.compareTo(placeBet.getAmount()) == 0){
//                return setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, placeBet.getTransactionId(),null);
//            }
//
//            //修改余额 记录账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, placeBet.getBetId(),
//                    placeBet.getAmount(),placeBet.getTransactionId());
//
//            return switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS, AMOUNT_LESS_ZERO -> {
//                    yield setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,coinRecordResultVO.getCoinAfterBalance().toString(), placeBet.getTransactionId(),null);
//                }
//                case REPEAT_TRANSACTIONS->
//                        setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, placeBet.getTransactionId(),null);
//                case  INSUFFICIENT_BALANCE, WALLET_NOT_EXIST -> setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,null, placeBet.getTransactionId(),null);
//                default -> setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,centerAmount, placeBet.getTransactionId(),null);
//            };
//        }catch (Exception e){
//            log.error("im marbles failed processAdjustBet error {}", e);
//            e.printStackTrace();
//            return setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,centerAmount, placeBet.getTransactionId(),null);
//        }
//    }
//
//
//    /**
//     * 设置下注反参
//     */
//    public MarblesPlaceBetResp setPlaceBetBuilder(MarblesRespErrEnums errEnums , String balance, String transactionId, String OperatorTransactionId) {
//        MarblesPlaceBetResp placeBetResp = new MarblesPlaceBetResp();
//        List<MarblesPlaceBetResp.PlaceBetBuilder> placeBetRespList = new ArrayList<>();
//        MarblesPlaceBetResp.PlaceBetBuilder placeBetBuilder = new MarblesPlaceBetResp.PlaceBetBuilder();
//        placeBetBuilder.setCode(errEnums.getCode());
//        placeBetBuilder.setMessage(errEnums.getDescription());
//        placeBetBuilder.setOperatorTransactionId(OperatorTransactionId);
//        placeBetBuilder.setBalance(balance);
//        placeBetBuilder.setTransactionId(transactionId);
//        placeBetRespList.add(placeBetBuilder);
//        placeBetResp.setResult(placeBetRespList);
//        placeBetResp.setCode(errEnums.getCode());
//        placeBetResp.setMessage(errEnums.getDescription());
//        return placeBetResp;
//    }
//
//    /**
//     * 结算(派彩)
//     * 结算和取消的接口是会有多笔交易的。会包括不一样的玩家注单
//     *
//     * 我方查看后是先 Settle 注单后，会再呼叫多一次 Commission 的 SettleBet
//     */
//    @Override
//    public MarblesRefundResp settleBet(MarblesSettleBetReq req) {
//        log.info("弹珠settleBet req:{}", JSONObject.toJSONString(req));
//        List<SettleBetReq> settleBetList = req.getTransactions();
//        MarblesRefundResp refundResp = new MarblesRefundResp();
//        if(CollectionUtil.isNotEmpty(settleBetList)){
//            List<MarblesRefundResp.RefundBuilder> result = new ArrayList<>();
//            for (SettleBetReq settleBetReq : settleBetList) {
//                CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//                casinoMemberReqVO.setVenueUserAccount(settleBetReq.getPlayerId());
//                casinoMemberReqVO.setVenueCode(VenueEnum.MARBLES.getVenueCode());
//                ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//                if (!respVO.isOk() || respVO.getData() == null) {
//                    log.error("im marbles settleBet error, req:{}", req);
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null,settleBetReq.getTransactionId(),null));
//                    continue;
//                }
//                CasinoMemberRespVO casinoMember = respVO.getData();
//                String venueCode = casinoMember.getVenueCode();
////                List<String> venueCodes = Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
////                if (!venueCodes.contains(venueCode)){
////                    log.info("venueCode not match,venueCode:{}",venueCode);
////                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,settleBetReq.getTransactionId(),null));
////                    continue;
////                }
////                if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
////                    log.info("{}:场管未开启", VenueEnum.MARBLES.getVenueName());
////                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,settleBetReq.getTransactionId(),null));
////                    continue;
////                }
//                // 三方的参数没有唯一性，故加上时间戳
//                String orderNo = settleBetReq.getBetId()+System.currentTimeMillis();
//                RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
//                String userId = casinoMember.getUserId();
//                UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//                String centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount().toString();
//                try{
//                    if (!rLock.tryLock()) {
//                        log.error("im marbles error settleBet error get locker error, req:{}", req);
//                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,settleBetReq.getAmount().toString(),settleBetReq.getTransactionId(),null));
//                        continue;
//                    }
//
//                    // 游戏锁定
////                    if (userGameLock(userInfoVO)) {
////                        log.error("im marbles locked userName[{}] game lock.", userInfoVO.getUserName());
////                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,settleBetReq.getTransactionId(),null));
////                        continue;
////                    }
//
//                    if(BigDecimal.ZERO.compareTo(settleBetReq.getAmount()) == 0){
//                        result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, settleBetReq.getTransactionId(),System.currentTimeMillis()+""));
//                        continue;
//                    }
//
//                    if(CollectionUtil.isEmpty(settleBetReq.getRefTransactionId())){
//                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),System.currentTimeMillis()+""));
//                        continue;
//                    }
//                    // 根据关联的transactionId和preTransactionId 查询出对应的orderNo
//                    CoinRecordResultVO coinRecordResultVO = null;
//                    String refTransaction = settleBetReq.getRefTransactionId().get(0);
//                    UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//                    coinRecordRequestVO.setRemark(refTransaction);
//                    coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//                    coinRecordRequestVO.setUserId(userId);
//                    coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                    ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                    List<UserCoinRecordVO> data = userCoinRecords.getData();
//                    if (CollectionUtil.isNotEmpty(data)) {
//                        UserCoinRecordVO userCoinRecordVO = data.get(0);
//                        // 查询出是否有已经派彩的
//                        coinRecordRequestVO = new UserCoinRecordRequestVO();
//                        coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
//                        coinRecordRequestVO.setUserId(userId);
//                        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//                        ResponseVO<List<UserCoinRecordVO>> recordsList = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                        if (recordsList.isOk() && CollectionUtil.isNotEmpty(recordsList.getData()) && recordsList.getData().size() > 0){
//                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,settleBetReq.getTransactionId(),null));
//                            continue;
//                        }
//                    }else{
//                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
//                        continue;
//                    }
//
//                    // 正常派彩
//                    if("Settle".equals(settleBetReq.getType())){
//                        coinRecordResultVO = updateBalancePayoutLocal(userInfoVO, settleBetReq.getBetId(), settleBetReq.getAmount(),settleBetReq.getTransactionId());
//                    }else if("Commission".equals(settleBetReq.getType())){
//                        // 重新派彩
//                        coinRecordResultVO = updateBalanceResettle(userInfoVO, settleBetReq.getBetId(), settleBetReq.getAmount());
//                    }
//
//                    if(coinRecordResultVO == null){
//                        log.error("im marbles error settleBet error updateBalancePayout error, req:{}", req);
//                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
//                        continue;
//                    }
//
//                    switch (coinRecordResultVO.getResultStatus()) {
//                        case SUCCESS -> {
//                            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//                            BigDecimal balance = BigDecimal.ZERO;
//                            if (!Objects.isNull(userCenterCoin)) {
//                                balance = userCenterCoin.getTotalAmount();
//                            }
//                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS, balance.toString(), settleBetReq.getTransactionId(), System.currentTimeMillis() + ""));
//                        }
//                        case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO ->{
//                                result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
//                        }
//                    }
//                }catch (Exception e){
//                    log.error("IM 弹珠游戏 退款取消失败， 失败原因为{}",e.getMessage());
//                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
//                    e.printStackTrace();
//                }finally {
//                    try {
//                        rLock.lockInterruptibly(10, TimeUnit.SECONDS);
//                    }catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            refundResp.setCode(result.get(0).getCode());
//            refundResp.setMessage(result.get(0).getMessage());
//            refundResp.setResult(result);
//        }
//        return refundResp;
//    }
//
//
//    /**
//     * 退款/取消
//     * 结算和取消的接口是会有多笔交易的。会包括不一样的玩家注单
//     *
//     * 第三方回复
//     *
//     * Refund API 里如 TransactionType 是 CancelSettlement, RefTransactionId 的值会是SettleBet 的 TransactionId，如是 CancelWager，
//     * RefTransactionId 的值则会是 Placebet 的 TransactionId。谢谢 - sunny
//     */
//    @Override
//    public MarblesRefundResp refund(MarblesRefundReq req) {
//        log.info("弹珠refund req:{}", JSONObject.toJSONString(req));
//        List<RefundReq> refundReqList = req.getTransactions();
//        MarblesRefundResp refundResp = new MarblesRefundResp();
//        if(CollectionUtil.isNotEmpty(refundReqList)){
//            List<MarblesRefundResp.RefundBuilder> result = new ArrayList<>();
//            for (RefundReq refundReq : refundReqList) {
//                CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//                casinoMemberReqVO.setVenueUserAccount(refundReq.getPlayerId());
//                casinoMemberReqVO.setVenueCode(VenueEnum.MARBLES.getVenueCode());
//                ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//                if (!respVO.isOk() || respVO.getData() == null) {
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
//                    continue;
//                }
//                CasinoMemberRespVO casinoMember = respVO.getData();
//                String venueCode = casinoMember.getVenueCode();
//                List<String> venueCodes = Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
//                if (!venueCodes.contains(venueCode)){
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
//                    continue;
//                }
//                if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
//                    continue;
//                }
//                String orderNo = refundReq.getTransactionId()+System.currentTimeMillis();
//                RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
//                String userId = casinoMember.getUserId();
//                UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//                String centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount().toString();
//                try{
//                    if (!rLock.tryLock()) {
//                        log.error("im marbles error refund error get locker error, req:{}", req);
//                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,refundReq.getTransactionId(),null));
//                        continue;
//                    }
//
//                    // 游戏锁定
//                    if (userGameLock(userInfoVO)) {
//                        log.error("im marbles locked userName[{}] game lock.", userInfoVO.getUserName());
//                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,refundReq.getTransactionId(),null));
//                        continue;
//                    }
//                    TransactionTypeEnum typeEnum = TransactionTypeEnum.getCode(refundReq.getTransactionType());
//                    if(Objects.isNull(typeEnum)){
//                        log.error("im marbles error refund error transactionType error, req:{}", req);
//                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
//                        continue;
//                    }
//                    UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//                    coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//                    coinRecordRequestVO.setUserId(userId);
//                    coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                    coinRecordRequestVO.setRemark(refundReq.getRefTransactionId());
//                    ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                    if(!userCoinRecords.isOk() || CollectionUtil.isEmpty(userCoinRecords.getData()) || userCoinRecords.getData().size() == 0) {
//                        log.error("im marbles 查询交易订单为空 或不存在 req:{}", req);
//                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
//                        continue;
//                    }else{
//                        UserCoinRecordVO userCoinRecordVO = userCoinRecords.getData().get(0);
//                        coinRecordRequestVO = new UserCoinRecordRequestVO();
//                        coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
//                        coinRecordRequestVO.setRemark(refundReq.getTransactionId());
//                        coinRecordRequestVO.setUserId(userId);
//                        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                        ResponseVO<List<UserCoinRecordVO>> recordsList = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                        if (recordsList.isOk() && CollectionUtil.isNotEmpty(recordsList.getData()) && recordsList.getData().size() > 0) {
//                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,refundReq.getTransactionId(),null));
//                            continue;
//                        }
//                        CoinRecordResultVO coinRecordResultVO;
//                        switch (typeEnum){
//                            case CANCELWAGER -> // 取消投注
//                                    coinRecordResultVO = updateBalanceBetCancel(userInfoVO, userCoinRecordVO.getOrderNo(), userCoinRecordVO.getCoinValue(),refundReq.getTransactionId());
//                            case CANCELSETTLEMENT,CANCELCOMMISSION -> // 取消结算
//                                    coinRecordResultVO = updateBalanceCancelBet(userInfoVO, userCoinRecordVO.getOrderNo(), userCoinRecordVO.getCoinValue(),refundReq.getTransactionId());
//                            default -> {
//                                log.error("im marbles error refund error transactionType error, req:{}", req);
//                                result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
//                                continue;
//                            }
//                        }
//                        switch (coinRecordResultVO.getResultStatus()) {
//                            case SUCCESS ->
//                                    result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,coinRecordResultVO.getCoinAfterBalance().toString(),refundReq.getTransactionId(),null));
//                            case REPEAT_TRANSACTIONS ->
//                                    result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,refundReq.getTransactionId(),null));
//                            case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL , AMOUNT_LESS_ZERO ->
//                                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
//                        }
//
//                    }
//
//
//                }catch (Exception e){
//                    log.error("IM弹珠游戏退款取消失败，失败原因为{}",e.getMessage());
//                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
//                    e.printStackTrace();
//                }
//            }
//            refundResp.setCode(result.get(0).getCode());
//            refundResp.setMessage(result.get(0).getMessage());
//            refundResp.setResult(result);
//        }
//        return refundResp;
//    }
//
//
//    /**
//     * 投注取消
//     *
//     * @param userInfoVO
//     * @param orderNo
//     * @param amount
//     * @return
//     */
//    protected CoinRecordResultVO updateBalanceBetCancel(UserInfoVO userInfoVO, String orderNo, BigDecimal amount,String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        // 账变类型
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setRemark(remark);
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//    /**
//     * 设置退款取消响应
//     */
//    public MarblesRefundResp.RefundBuilder setRefundBuilder(MarblesRespErrEnums errEnums , String balance, String transactionId, String OperatorTransactionId) {
//        MarblesRefundResp.RefundBuilder refundBuilder = new MarblesRefundResp.RefundBuilder();
//        refundBuilder.setCode(errEnums.getCode());
//        refundBuilder.setMessage(errEnums.getDescription());
//        refundBuilder.setOperatorTransactionId(OperatorTransactionId);
//        refundBuilder.setBalance(balance);
//        refundBuilder.setTransactionId(transactionId);
//        return refundBuilder;
//    }
//
//
//    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount,String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//
//
//    public enum TransactionTypeEnum {
//
//        CANCELWAGER("CancelWager", "取消投注"),
//        CANCELSETTLEMENT("CancelSettlement","取消结算"),
//        CANCELCOMMISSION("CancelCommission","取消重复结算");
//
//
//        private final String name;
//
//        private final String msg;
//
//
//        public String getName() {
//            return name;
//        }
//
//        TransactionTypeEnum(String name, String msg) {
//            this.name = name;
//            this.msg = msg;
//        }
//
//        public static TransactionTypeEnum getCode(final String code){
//            for(TransactionTypeEnum transactionTypeEnum : TransactionTypeEnum.values()){
//                if(transactionTypeEnum.getName().equals(code)){
//                    return transactionTypeEnum;
//                }
//            }
//            return null;
//        }
//    }
//
//
//    protected CoinRecordResultVO updateBalancePayoutLocal(UserInfoVO userInfoVO, String transactionId, BigDecimal payoutAmount,String remark) {
//        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
//        userCoinAddVOPayout.setOrderNo(transactionId);
//        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVOPayout.setCoinType( WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
//        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
//        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
//        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVOPayout.setRemark(remark);
//        return userCoinApi.addCoin(userCoinAddVOPayout);
//    }
//
//
//}
