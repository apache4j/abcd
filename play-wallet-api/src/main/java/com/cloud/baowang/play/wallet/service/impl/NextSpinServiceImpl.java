//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
//import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
//import com.cloud.baowang.play.api.api.third.NextSpinGameApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.nextSpin.NextSpinCurrencyEnums;
//import com.cloud.baowang.play.api.vo.nextSpin.NextSpinTransactionRecordVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.NextSpinRespErrEnums;
//import com.cloud.baowang.play.wallet.service.NextSpinService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.nextSpin.NextSpinReq;
//import com.cloud.baowang.play.wallet.vo.res.nextSpin.AcctInfo;
//import com.cloud.baowang.play.wallet.vo.res.nextSpin.NextSpinBetResp;
//import com.cloud.baowang.play.wallet.vo.res.nextSpin.NextSpinResp;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
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
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class NextSpinServiceImpl extends BaseService implements NextSpinService {
//    private final CasinoMemberApi casinoMemberApi;
//    private final UserCoinApi userCoinApi;
//    private final UserInfoApi userInfoApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//    private final PlayVenueInfoApi playVenueInfoApi;
//    private final NextSpinGameApi nextSpinGameApi;
//
//    @Override
//    public Object oauth(NextSpinReq request) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(request.getAcctId());
//        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
//        //只有authorize 才有token，所以才对token获取
//        if (StringUtils.isNotEmpty(request.getToken())){
//            casinoMemberReqVO.setCasinoPassword(request.getToken());
//        }
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return NextSpinResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST,request.getMerchantCode(), request.getSerialNo());
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return NextSpinResp.err(NextSpinRespErrEnums.SYSTEM_ERROR,request.getMerchantCode(), request.getSerialNo());
//        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return NextSpinResp.err(NextSpinRespErrEnums.SYSTEM_ERROR,request.getMerchantCode(), request.getSerialNo());
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        String mainCurrency =null;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//            mainCurrency = userCenterCoin.getCurrency();
//        }else{
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//            mainCurrency = userInfoVO.getMainCurrency();
//        }
//        mainCurrency=NextSpinCurrencyEnums.getByCode(mainCurrency).name();
//        AcctInfo acctInfo=new AcctInfo();
//        acctInfo.setAcctId(request.getAcctId());
//        acctInfo.setUserName(casinoMember.getUserAccount());
//        acctInfo.setCurrency(mainCurrency);
//        acctInfo.setBalance(balance);
//        return NextSpinResp.success(request.getMerchantCode(), request.getSerialNo(),acctInfo);
//
//    }
//
//    @Override
//    public Object checkBalance(NextSpinReq request) {
//        return oauth(request);
//    }
//
//    @Override
//    public Object bet(NextSpinReq request) {
////        1下注 2取消下注 4派彩 6jackpot的派彩
//        Integer type = request.getType();
//        return switch (type) {
//            case 1:
//                yield placeBet(request);
//            case 2:
//                yield refund(request);
//            case 4,6:
//                yield settleBet(request);
//            default:
//                yield NextSpinResp.err(NextSpinRespErrEnums.INVALID_REQUEST,request.getMerchantCode(), request.getSerialNo());
//        };
//    }
//
//    /**
//     * 下注
//     * @param req
//     * @return
//     */
//    private Object placeBet(NextSpinReq req) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
//        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
//        }
//        String userId = respVO.getData().getUserId();
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if(Objects.isNull(userInfoVO)){
//            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] not find.",req.getAcctId());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
//        }
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.NEXTSPIN.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        //用户中心钱包余额
//        BigDecimal centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount();
//        if(venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())){
//            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.NEXTSPIN.getVenueCode());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//
//        // 场馆关闭
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            log.info("{}:场馆未开启", VenueEnum.NEXTSPIN.getVenueName());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//
//        if (venueGameMaintainClosed(venueCode,casinoMember.getSiteCode(),req.getGameCode())){
//            log.info("{}:游戏未开启", VenueEnum.NEXTSPIN.getVenueName());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//
//        // 游戏锁定
//        if (userGameLock(userInfoVO)) {
//            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//        }
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, req.getTransferId()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
//                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//            }
//            if(BigDecimal.ZERO.compareTo(req.getAmount()) == 0){
//                String tid=String.valueOf(UUID.randomUUID());
//                NextSpinTransactionRecordVO vo=new NextSpinTransactionRecordVO();
//                vo.setAccount(req.getAcctId());
//                vo.setReturnNumber(tid);
//                vo.setTransferId(req.getTransferId());
//                vo.setRequestJson(JSONObject.toJSONString(req));
//                nextSpinGameApi.save(vo);
//                return NextSpinBetResp.success( req, tid,centerAmount);
//            }
//            // 检查余额
//            if(!compareAmount(userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getAmount())){
//                log.info("NEXTSPIN 用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getAmount());
//                return NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
//            }
//            //修改余额 记录账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, req.getTransferId(),
//                    req.getAmount(),req.getTransferId());
//
//            return switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS -> {
//                    UserCoinRecordVO vo=userCoinRecordApi.getUserCoinRecord(req.getTransferId(),userInfoVO.getUserId(),CoinBalanceTypeEnum.EXPENSES.getCode());
//                    yield NextSpinBetResp.success(req,vo.getId(),coinRecordResultVO.getCoinAfterBalance());
//                }
//                case AMOUNT_LESS_ZERO ->{
//                    yield NextSpinBetResp.success(req,UUID.randomUUID().toString(),coinRecordResultVO.getCoinAfterBalance());
//                }
//                case REPEAT_TRANSACTIONS ->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,centerAmount);
//                }
//                case  INSUFFICIENT_BALANCE->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
//                }
//                case  WALLET_NOT_EXIST->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,centerAmount);
//                }
//                default ->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
//                }
//            };
//        }catch (Exception e){
//            log.error("NEXTSPIN failed processAdjustBet error {}", e.getMessage());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
//        }finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//    /**
//     * 取消下注
//     * @param req
//     * @return
//     */
//    public Object refund(NextSpinReq req) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
//        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        String userId = respVO.getData().getUserId();
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        //用户中心钱包余额
//        BigDecimal centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount();
//
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        String orderNo = req.getTransferId();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
//        try{
//            if (!rLock.tryLock()) {
//                log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
//                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//            }
//            // 游戏锁定
//            if (userGameLock(userInfoVO)) {
//                log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
//                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//            }
//            // 场馆关闭
//            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//                log.info("{}:场馆未开启", VenueEnum.NEXTSPIN.getVenueName());
//                return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//            }
//
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//            coinRecordRequestVO.setUserId(userId);
//            coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//            coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            coinRecordRequestVO.setRemark(req.getReferenceId());
//            ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if(!userCoinRecords.isOk() || CollectionUtil.isEmpty(userCoinRecords.getData()) || userCoinRecords.getData().size() == 0) {
//                log.error("NEXTSPIN 查询交易订单为空 或不存在 req:{}", req);
//                return NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO,req,centerAmount);
//            }
//            UserCoinRecordVO userCoinRecordVO = userCoinRecords.getData().get(0);
//            CoinRecordResultVO coinRecordResultVO=updateBalanceBetCancel(userInfoVO, req.getReferenceId(), userCoinRecordVO.getCoinValue(),req.getTransferId());
//            return switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS,AMOUNT_LESS_ZERO ->{
//                    UserCoinRecordVO uvo=userCoinRecordApi.getUserCoinRecord(req.getTransferId(),userInfoVO.getUserId(),CoinBalanceTypeEnum.INCOME.getCode());
//                    String tid=null;
//                    if (Objects.isNull(uvo)){
//                        tid=UUID.randomUUID().toString();
//                    }else{
//                        tid= uvo.getId();
//                    }
//                    yield NextSpinBetResp.success(req,tid,coinRecordResultVO.getCoinAfterBalance());
//                }
//                case REPEAT_TRANSACTIONS ->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,centerAmount);
//                }
//                case  INSUFFICIENT_BALANCE->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
//                }
//                case  WALLET_NOT_EXIST->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,centerAmount);
//                }
//                default ->{
//                    yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
//                }
//            };
//        }catch (Exception e){
//            log.error("NEXTSPIN游戏退款取消失败，失败原因为{}",e.getMessage());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO,req,centerAmount);
//        }finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    /**
//     * 投注取消
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
////    /**
////     * 重结算
////     * @param userInfoVO
////     * @param orderNo
////     * @param amount
////     * @return
////     */
////    protected CoinRecordResultVO updateBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount,String remark) {
////        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
////        userCoinAddVO.setOrderNo(orderNo);
////        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
////        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
////        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
////        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
////        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
////        userCoinAddVO.setUserId(userInfoVO.getUserId());
////        userCoinAddVO.setCoinValue(amount.abs());
////        userCoinAddVO.setRemark(remark);
////        userCoinAddVO.setUserInfoVO(userInfoVO);
////        //修改余额 记录账变
////        return userCoinApi.addCoin(userCoinAddVO);
////
////    }
//
//    /**
//     * 派彩
//     * @param req
//     * @return
//     */
//    public Object settleBet(NextSpinReq req) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
//        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
//        }
//        String userId = respVO.getData().getUserId();
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if(Objects.isNull(userInfoVO)){
//            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] not find.",req.getAcctId());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
//        }
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.NEXTSPIN.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        //用户中心钱包余额
//        BigDecimal centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount();
//        if(venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())){
//            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.NEXTSPIN.getVenueCode());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        // 场馆关闭
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            log.info("{}:场馆未开启", VenueEnum.NEXTSPIN.getVenueName());
//            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
//        }
//        // 三方的参数没有唯一性，故加上时间戳
//        String orderNo = req.getTransferId();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
//        try{
//                    if (!rLock.tryLock()) {
//                        log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
//                        return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//                    }
//                    // 游戏锁定
//                    if (userGameLock(userInfoVO)) {
//                        log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
//                        return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
//                    }
//
//                    if(BigDecimal.ZERO.compareTo(req.getAmount()) == 0){
//                        return NextSpinBetResp.success( req, String.valueOf(UUID.randomUUID()),centerAmount);
//                    }
//
//                    if(ObjectUtils.isEmpty(req.getReferenceId())){
//                        return NextSpinBetResp.err(NextSpinRespErrEnums.MISSING_REQUIRED_PARAM, req,centerAmount);
//                    }
//                   // 根据关联的transactionId和preTransactionId 查询出对应的orderNo
//                   CoinRecordResultVO coinRecordResultVO = null;
//                   String refTransaction = req.getReferenceId();
//                   UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//                   coinRecordRequestVO.setRemark(refTransaction);
//                   coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//                   coinRecordRequestVO.setUserId(userId);
//                   coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                   ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                   List<UserCoinRecordVO> data = userCoinRecords.getData();
//                   if (CollectionUtil.isNotEmpty(data)) {
//                       UserCoinRecordVO userCoinRecordVO = data.get(0);
//                       // 查询出是否有已经派彩的
//                       coinRecordRequestVO = new UserCoinRecordRequestVO();
//                       coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
//                       coinRecordRequestVO.setRemark(req.getTransferId());
//                       coinRecordRequestVO.setUserId(userId);
//                       coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//                       coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//                       ResponseVO<List<UserCoinRecordVO>> recordsList = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//                       if (recordsList.isOk() && CollectionUtil.isNotEmpty(recordsList.getData()) && recordsList.getData().size() > 0) {
//                           return  NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req, centerAmount);
//                       }
//                   }else{
//                       NextSpinTransactionRecordVO nextSpinTransactionRecordVO= nextSpinGameApi.getByTransferId(req.getReferenceId());
//                       if (Objects.isNull(nextSpinTransactionRecordVO)){
//                           return NextSpinBetResp.err(NextSpinRespErrEnums.BATCH_NO_NOT_EXIST, req, centerAmount);
//                       }
//                   }
//                   // 正常派彩
//                    coinRecordResultVO = updateBalancePayoutLocal(userInfoVO, req.getReferenceId(), req.getAmount(),req.getTransferId());
//
//                   if(coinRecordResultVO == null){
//                        log.error("NEXTSPIN error settleBet error updateBalancePayout error, req:{}", req);
//                        return  NextSpinBetResp.err(NextSpinRespErrEnums.BATCH_NO_NOT_EXIST, req, centerAmount);
//                   }
//                    return switch (coinRecordResultVO.getResultStatus()) {
//                        case SUCCESS ->{
//                            UserCoinRecordVO uvo=userCoinRecordApi.getUserCoinRecord(req.getTransferId(),userInfoVO.getUserId(),CoinBalanceTypeEnum.INCOME.getCode());
//                            yield NextSpinBetResp.success(req,uvo.getId(),coinRecordResultVO.getCoinAfterBalance());
//                        }
//                        case REPEAT_TRANSACTIONS ->{
//                            yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,centerAmount);
//                        }
//                        case  INSUFFICIENT_BALANCE->{
//                            yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
//                        }
//                        case  WALLET_NOT_EXIST->{
//                            yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,centerAmount);
//                        }
//                        default ->{
//                            yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
//                        }
//                    };
//                }catch (Exception e){
//                    log.error("NEXTSPIN 退款取消失败， 失败原因为{}",e.getMessage());
//                    return NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
//                }finally
//                    {
//                        if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                            rLock.unlock();
//                        }
//                    }
//         }
//}
