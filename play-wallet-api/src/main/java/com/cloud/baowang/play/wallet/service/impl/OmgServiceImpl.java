//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.OmgRespErrEnums;
//import com.cloud.baowang.play.wallet.enums.OmgTransferTypeEnums;
//import com.cloud.baowang.play.wallet.service.OmgService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.omg.OmgReq;
//import com.cloud.baowang.play.wallet.vo.res.omg.OmgResp;
//import com.cloud.baowang.play.wallet.vo.res.omg.OmgRespData;
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
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class OmgServiceImpl extends BaseService implements OmgService {
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//    @Override
//    public OmgResp verify(OmgReq req) {
//        log.info("OMG verify request{}", req);
//        String token = req.getOperatorPlayerSession();
//        if (StringUtils.isEmpty(token)) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(token);
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        ResponseVO<List<VenueInfoVO>> venueInfoVO=playVenueInfoApi.getVenueInfoList(venueCode);
//        if (!venueInfoVO.isOk() || venueInfoVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        List<String> AppIds = venueInfoVO.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
////        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
////                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
////                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
////        if (!venueCodes.contains(venueCode)){
////            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
////        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//
//        OmgRespData data = new OmgRespData();
//        data.setNickname(casinoMember.getVenueUserAccount());
//        data.setBalance(balance);
//        data.setUname(casinoMember.getVenueUserAccount());
//        return OmgResp.success(data);
//    }
//
//    @Override
//    public OmgResp getBalance(OmgReq req) {
//        log.info("OMG getBalance request{}", req);
//        String uname = req.getUname();
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(req.getPlayerLoginToken());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
////        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
////                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
////                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
////        if (!venueCodes.contains(venueCode)){
////            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
////        }
//        ResponseVO<List<VenueInfoVO>> venueInfoVO=playVenueInfoApi.getVenueInfoList(venueCode);
//        if (!venueInfoVO.isOk() || venueInfoVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        List<String> AppIds = venueInfoVO.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//
//        OmgRespData data = new OmgRespData();
//        data.setNickname(casinoMember.getVenueUserAccount());
//        data.setBalance(balance);
//        data.setUname(casinoMember.getVenueUserAccount());
//        return OmgResp.success(data);
//    }
//
//    @Override
//    public OmgResp changeBalance(OmgReq req) {
//        String uname = req.getUname();
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(req.getPlayerLoginToken());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        ResponseVO<List<VenueInfoVO>> venueInfoVOs=playVenueInfoApi.getVenueInfoList(venueCode);
//        if (!venueInfoVOs.isOk() || venueInfoVOs.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        List<String> AppIds = venueInfoVOs.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
//        }
////        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
////                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
////                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
////        if (!venueCodes.contains(venueCode)){
////            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
////        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
//        }
//
//        String orderNo = req.getSessionId();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("omg errorBet error get locker error, req:{}", req);
//                return OmgResp.fail(OmgRespErrEnums.FAILURE);
//            }
//            String userId = casinoMember.getUserId();
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            // 游戏锁定
//            if (userGameLock(userInfoVO)) {
//                log.error("PG queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
//                return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//            }
//            Integer type = req.getType();
//            OmgTransferTypeEnums omgTransferTypeEnums = OmgTransferTypeEnums.fromCode(type);
//            if (omgTransferTypeEnums == null){
//                log.error("omg transfer type not support,{}",req);
//                return OmgResp.fail(OmgRespErrEnums.FAILURE);
//            }
//
//            if(type.equals(OmgTransferTypeEnums.BET.getCode()) && !compareAmount(userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getMoney())){
//                log.info("用户余额不足，userId:{},siteCode:{},money:{},errorCore={}",userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getMoney(),OmgRespErrEnums.INSUFFICIENT_BALANCE);
//                return OmgResp.fail(OmgRespErrEnums.INSUFFICIENT_BALANCE);
//            }
//
//            if(type.equals(OmgTransferTypeEnums.BET.getCode())) {
//                SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                        .siteCode(userInfoVO.getSiteCode())
//                        .venueCode(venueCode)
//                        .currencyCode(userInfoVO.getMainCurrency()).build();
//                ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//                VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//                if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                    log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueCode);
//                    return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//                }
//            }
//
//            CoinRecordResultVO coinRecordResultVO =  switch (omgTransferTypeEnums){
//                case BET->{
//                    yield  bet(req,userInfoVO);
//                }
//                case PAYOUT->{
//                    yield payout(req,userInfoVO);
//                }
//                case CANCEL->{
//                    yield cancelGame(req,userInfoVO);
//                }
//                case END->{
//                    yield endGame(req, userInfoVO);
//                }
//                case LUCKWIN->{
//                    yield luckwin(req, userInfoVO);
//                }
//                case Future->{
//                    yield future(req,userInfoVO);
//                }
//            };
//            if (coinRecordResultVO == null){
//                log.info("omg coinRecordResultVO ={}",omgTransferTypeEnums.getCode());
//                return  OmgResp.fail(OmgRespErrEnums.FAILURE);
//            }
//            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//            OmgResp resp = switch (resultStatus) {
//                case SUCCESS-> OmgResp.success();
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO -> {
//                    log.info("omg amount resp={}",resultStatus);
//                    yield OmgResp.fail(OmgRespErrEnums.FAILURE);
//                }
//            };
//            if (resp.getCode() == OmgRespErrEnums.SUCCESS.getCode()){
//                UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//                BigDecimal balance = BigDecimal.ZERO;
//                if (!Objects.isNull(userCenterCoin)) {
//                    balance = userCenterCoin.getTotalAmount();
//                }
//                OmgRespData data = new OmgRespData();
//                data.setBalance(balance);
//                resp.setData(data);
//            }
//            return resp;
//        }catch (Exception e){
//            log.error("omg amount change error ", e);
//            return OmgResp.fail(OmgRespErrEnums.FAILURE);
//        }finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    private CoinRecordResultVO cancelGame(OmgReq req, UserInfoVO userInfoVO) {
//        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setOrderNo(req.getOrderId());
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        List<UserCoinRecordVO> data = userCoinRecords.getData();
//        if (CollectionUtil.isEmpty(data)){
//            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
//            return coinRecordResultVO;
//        }
//        List<String> coinTypes = data.stream().map(UserCoinRecordVO::getCoinType).toList();
//        if (coinTypes.contains(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())){
//            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
//            return coinRecordResultVO;
//        }
//        return updateBalanceBetCancel(userInfoVO, req.getSessionId(), req.getMoney().abs());
//    }
//
//    private CoinRecordResultVO future(OmgReq req, UserInfoVO userInfoVO) {
//        return null;
//    }
//
//    private CoinRecordResultVO luckwin(OmgReq req, UserInfoVO userInfoVO) {
//        return null;
//    }
//
//    private CoinRecordResultVO endGame(OmgReq req, UserInfoVO userInfoVO) {
//        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
//        coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.SUCCESS);
//        return coinRecordResultVO;
//    }
//
//    private CoinRecordResultVO payout(OmgReq req, UserInfoVO userInfoVO) {
//        String order = req.getRoundId();
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setOrderNo(order);
//        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        List<UserCoinRecordVO> data = userCoinRecords.getData();
//        if (CollectionUtil.isNotEmpty(data)) {
//            UserCoinRecordVO userCoinRecordVO = data.get(0);
//            if (req.getOrderId().equals(userCoinRecordVO.getRemark())){
//                CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
//                coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
//                return coinRecordResultVO;
//            }
//           /* // 注单号已经派彩，后续拼接
//            String orderId = req.getOrderId();
//            // 截取流水单号作为附加单号
//            int from = orderId.length() > 12 ? orderId.length() - 12 : 0;
//            order = order + "_" + orderId.substring(from);*/
//        }
//        return updateBalancePayout(userInfoVO, req.getRoundId(), req.getMoney().abs());
//    }
//
//    private CoinRecordResultVO bet(OmgReq req, UserInfoVO userInfoVO) {
//        String orderNo = req.getSessionId();
//        return updateBalanceBet(userInfoVO, orderNo,req.getMoney().abs(),req.getOrderId());
//    }
//
//
//}
