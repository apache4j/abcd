//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.cloud.baowang.common.core.constants.RedisConstants;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
//import com.cloud.baowang.play.wallet.enums.ACELTDefaultException;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.user.api.enums.UserStatusEnum;
//import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.MD5Util;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.common.redis.annotation.DistributedLock;
//import com.cloud.baowang.play.api.api.order.PlayServiceApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.base.ShBaseRes;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.SHDefaultException;
//import com.cloud.baowang.play.wallet.enums.ShTransferTypeEnums;
//import com.cloud.baowang.play.wallet.service.ShService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.sh.ShAdjustBalanceReq;
//import com.cloud.baowang.play.wallet.vo.req.sh.ShQueryBalanceReq;
//import com.cloud.baowang.play.wallet.vo.req.sh.ShTransferOrderReq;
//import com.cloud.baowang.play.wallet.vo.res.sh.ShAdjustBalanceRes;
//import com.cloud.baowang.play.wallet.vo.res.sh.ShBalanceRes;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@AllArgsConstructor
//@Service
//public class ShServiceImpl extends BaseService implements ShService {
//
//    private final UserCoinApi userCoinApi;
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//    private final UserInfoApi userInfoApi;
//
//    private final ApplicationContext applicationContext;
//
//    private final PlayServiceApi playServiceApi;
//
//
//    /**
//     * 根据商户查询场馆
//     * @param merchantNo 商户
//     * @param venuePlatform 平台
//     * @return 场馆
//     */
//    private VenueEnum getVenueByUser(String merchantNo, String venuePlatform) {
//        ResponseVO<String> respVO = playVenueInfoApi.venueInfoByPlatMerchant(venuePlatform, merchantNo);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            log.info("未查到用户登陆数据:{},{}", merchantNo, venuePlatform);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.DATA_NOT_EXIST);
//        }
//        String venueCode = respVO.getData();
//        return VenueEnum.nameOfCode(venueCode);
//    }
//
//
//    @Override
//    public ShBaseRes<ShBalanceRes> queryBalance(ShQueryBalanceReq request) {
//
//        if (!request.validate()) {
//            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
//        }
//
//        VenueEnum venueEnum = getVenueByUser(request.getMerchantNo(), VenueEnum.SH.getVenuePlatform());
//        if (ObjectUtil.isEmpty(venueEnum)) {
//            log.info("商户未查到:{}", request.getMerchantNo());
//            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//        }
//
//        if (this.validateMD5Sign(request.getMd5Sign(), request.getUserName(),venueEnum)) {
//            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//        }
//
//
//        try {
//            String userId = super.getVenueUserAccount(request.getUserName());
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (Objects.isNull(userInfoVO)) {
//                log.error("{} queryBalance userName[{}] not find.", venueEnum.getVenueName(), request.getUserName());
//                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//            if (venueMaintainClosed(venueEnum.getVenueCode(), userInfoVO.getSiteCode())) {
//                log.info("{}:场馆未开启", venueEnum.getVenueName());
//                return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
//            }
//            CheckActivityVO checkActivityVO = new CheckActivityVO();
//            checkActivityVO.setUserId(userInfoVO.getUserId());
//            checkActivityVO.setSiteCode(userInfoVO.getSiteCode());
//            checkActivityVO.setVenueCode(venueEnum.getVenueCode());
//            BigDecimal venueAmount = playServiceApi.checkJoinActivity(checkActivityVO);
//
//            //不允许下注
//            if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
//                return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//            }
//
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(venueEnum.getVenueCode())
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueEnum.getVenueCode());
//                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//
//            CasinoMemberReqVO reqVO = new CasinoMemberReqVO();
//            reqVO.setVenueCode(venueEnum.getVenueCode());
//            reqVO.setUserId(userId);
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.error("SH queryBalance userName[{}] not find.", request.getUserName());
//                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//            ShBalanceRes balanceRes = ShBalanceRes.builder().balanceAmount(userCenterCoin.getTotalAmount())
//                    .userName(request.getUserName())
//                    .currencyCode(userCenterCoin.getCurrency())
//                    .updatedTime(Instant.now().toEpochMilli()).build();
//            return ShBaseRes.success(balanceRes);
//        } catch (Exception e) {
//            log.error("SH queryBalance error", e);
//        }
//        return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//    }
//
//
//    @Override
//    public ShBaseRes<ShAdjustBalanceRes> adjustBalance(ShAdjustBalanceReq request) {
//
//        if (!request.validate()) {
//            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
//        }
//
//        VenueEnum venueEnum = getVenueByUser(request.getMerchantNo(), VenueEnum.SH.getVenuePlatform());
//        if (ObjectUtil.isEmpty(venueEnum)) {
//            log.info("商户未查到:{}", request.getMerchantNo());
//            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//        }
//
//        if (this.validateMD5Sign(request.getMd5Sign(), request.getUserName(),venueEnum)) {
//            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//        }
//
//        String userId = super.getVenueUserAccount(request.getUserName());
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (Objects.isNull(userInfoVO)) {
//            log.error("{} queryUserInfoByAccount userName[{}] not find.", venueEnum.getVenueName(), request.getUserName());
//            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//        }
//
//        if (venueMaintainClosed(venueEnum.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("{}:场馆未开启", venueEnum.getVenueName());
//            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
//        }
//        CheckActivityVO checkActivityVO = new CheckActivityVO();
//        checkActivityVO.setUserId(userInfoVO.getUserId());
//        checkActivityVO.setSiteCode(userInfoVO.getSiteCode());
//        checkActivityVO.setVenueCode(venueEnum.getVenueCode());
//        BigDecimal venueAmount = playServiceApi.checkJoinActivity(checkActivityVO);
//
//        //不允许下注
//        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
//            log.info("游戏校验失败,不允许下注:{},{}", checkActivityVO, venueAmount);
//            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
//        }
//
//
//        if (ObjectUtil.isNotEmpty(userInfoVO.getAccountStatus())) {
//            List<String> accountStatusList = Arrays.asList(userInfoVO.getAccountStatus().split(","));
//            if (CollectionUtil.isNotEmpty(accountStatusList) && accountStatusList.contains(UserStatusEnum.GAME_LOCK.getCode())) {
//                log.error("{} queryUserInfoByAccount userName[{}] account lock.", venueEnum.getVenueName(), request.getUserName());
//                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//        }
//
//
//        ShServiceImpl shService = applicationContext.getBean(ShServiceImpl.class);
//        return shService.toCoin(userInfoVO, request,venueEnum);
//    }
//
//    @DistributedLock(name = RedisConstants.SH_COIN_LOCK, unique = "#userInfoVO.userId", waitTime = 3, leaseTime = 180)
//    public ShBaseRes<ShAdjustBalanceRes> toCoin(UserInfoVO userInfoVO, ShAdjustBalanceReq request,VenueEnum venueEnum) {
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//        if (Objects.isNull(userCenterCoin)) {
//            log.info("{} queryBalance userName[{}] not find.", venueEnum.getVenueName(), request.getUserName());
//            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//        }
//
//        ShAdjustBalanceRes build = ShAdjustBalanceRes.builder()
//                .transferNo(request.getTransferNo())
//                .userName(request.getUserName())
//                .currentAmount(userCenterCoin.getTotalAmount())
//                .realAmount(request.getTotalAmount())
//                .build();
//
//
//        // 余额如果小于0，说明余额不足
//        BigDecimal balance = userCenterCoin.getTotalAmount().add(request.getTotalAmount());
//        if (balance.compareTo(BigDecimal.ZERO) < 0 && (ShTransferTypeEnums.CANCEL.getCode().intValue() != request.getTransferType() &&
//                ShTransferTypeEnums.RESETTLEMENT.getCode().intValue() != request.getTransferType())) {
//            log.info("{} Service updateBalance error, transferNo = {}, totalAmount = {}, amount = {}",
//                    venueEnum.getVenueName(), request.getTransferNo(), request.getTotalAmount(), userCenterCoin.getTotalAmount());
//            return ShBaseRes.failed(ShResultCodeEnums.INSUFFICIENT_BALANCE, build);
//        }
//
//        //视讯扣费传过来的是集合订单,所以把集合里面每个订单都检查一便看看是否有单出现重复账变,如果有则直接拒绝这一批次的所有订单
//        for (ShTransferOrderReq item : request.getTransferOrderVOList()) {
//            validCoin(request.getTransferType(), item.getOrderNo(), userInfoVO.getUserId(), item.getAmount());
//        }
//        try {
//
//            for (ShTransferOrderReq item : request.getTransferOrderVOList()) {
//
//                UserCoinAddVO userCoinAddVO = this.buildUserCoinAddVO(request.getTransferType(), item.getOrderNo(), item.getAmount(), userCenterCoin, userInfoVO);
//
//                CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
//
//                if (ObjectUtil.isEmpty(coinRecordResultVO)) {
//                    log.error("SH调用扣费失败:orderNo:{},amount:{},req:{}", item.getOrderNo(), item.getAmount(), request);
//                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR, build);
//                }
//
//                //重复扣费的单直接通过.例如多笔订单扣费的过程中某笔订单扣费失败 直接拒绝 ,等待三方重新回来调用
//                if (!coinRecordResultVO.getResult() && UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS.equals(coinRecordResultVO.getResultStatus())) {
//                    return ShBaseRes.failed(ShResultCodeEnums.REPEAT_TRANSACTIONS, build);
//                }
//
//                if (!coinRecordResultVO.getResult()) {
//                    log.error("SH调用扣费失败,扣费拒绝:orderNo:{},amount:{},req:{}", item.getOrderNo(), item.getAmount(), request);
//                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//                }
//
//                //修改余额 记录账变
//                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
//                if (resultStatus.getCode().equals(UpdateBalanceStatusEnums.SUCCESS.getCode())
//                        || resultStatus.getCode().equals(UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.getCode())) {
//                    continue;
//                }
//
//                if (resultStatus.getCode().equals(UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE.getCode()) ||
//                        resultStatus.getCode().equals(UpdateBalanceStatusEnums.WALLET_NOT_EXIST.getCode())) {//余额不足
//                    log.error("SH调用扣费失败:玩家余额不足,orderNo:{},amount:{},req:{},result:{}", item.getOrderNo(), item.getAmount(), request, coinRecordResultVO);
//                    return ShBaseRes.failed(ShResultCodeEnums.INSUFFICIENT_BALANCE);
//                } else {
//                    log.error("SH调用扣费失败:其他异常,orderNo:{},amount:{},req:{},result:{}", item.getOrderNo(), item.getAmount(), request, coinRecordResultVO);
//                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//                }
//            }
//
//
//        }catch (SHDefaultException sh){
//            return ShBaseRes.failed(sh.getResultCode());
//        }
//        catch (Exception e) {
//            log.info("视讯扣费异常",e);
//            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//        }
//        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//        return ShBaseRes.success(ShAdjustBalanceRes.builder()
//                .transferNo(request.getTransferNo())
//                .userName(request.getUserName())
//                .currentAmount(userCoinWalletVO.getTotalAmount())
//                .realAmount(request.getTotalAmount()).build());
//
//    }
//
//
//    private UserCoinAddVO buildUserCoinAddVO(String orderId, BigDecimal amount, UserCoinWalletVO userCenterCoin, UserInfoVO userInfoVO) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderId);
//        userCoinAddVO.setUserName(userInfoVO.getUserName());
//        userCoinAddVO.setCurrency(userCenterCoin.getCurrency());
//        if (amount.compareTo(BigDecimal.ZERO) > 0) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        } else {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        }
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        return userCoinAddVO;
//    }
//
//    private UserCoinAddVO buildUserCoinAddVO(Integer transferType, String orderId, BigDecimal amount, UserCoinWalletVO userCenterCoin, UserInfoVO userInfoVO) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderId);
//        userCoinAddVO.setUserName(userInfoVO.getUserName());
//        userCoinAddVO.setCurrency(userCenterCoin.getCurrency());
//
//        toSetUserCoinAddVO(userCoinAddVO, transferType, amount);
//
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        return userCoinAddVO;
//    }
//
//    /**
//     * 下注前校验
//     *
//     * @param transferType 视讯类型
//     * @param orderId      订单
//     * @param userId       用户
//     * @param amount       金额
//     */
//    private void validCoin(Integer transferType, String orderId, String userId, BigDecimal amount) {
//        UserCoinRecordRequestVO userCoinAddVO = new UserCoinRecordRequestVO();
//        userCoinAddVO.setUserId(userId);
//        userCoinAddVO.setOrderNo(orderId);
//        //下注类型
//        if (ShTransferTypeEnums.BET.getCode().equals(transferType) || ShTransferTypeEnums.TIPS.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//
//            //下注类型 只能是扣钱 下注类型加钱就是有异常
//            if (amount.compareTo(BigDecimal.ZERO) >= 0) {
//                log.info("下注类型异常,transferType:{},下注类型存在非负数:{}", transferType, amount);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            //正常结算
//        } else if (ShTransferTypeEnums.PAYOUT.getCode().equals(transferType)) {
//
//            //正常结算 只能是加钱 正常结算扣钱就是有异常
//            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                log.info("下注类型异常,transferType:{},正常结算类型存在负数:{}", transferType, amount);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            //跳局=退钱
//        } else if (ShTransferTypeEnums.JUMP.getCode().equals(transferType)) {
//
//            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                log.info("下注类型异常,transferType:{},跳局类型存在负数:{}", transferType, amount);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            //重算局 跟 取消都是重新结算
//        } else if (ShTransferTypeEnums.RESETTLEMENT.getCode().equals(transferType)) {
//            //重算局不做处理,重算局 会多次账变
//            return;
//        }else if (ShTransferTypeEnums.CANCEL.getCode().equals(transferType)) {
//            //取消局(有可能负数 扣款)
//            //取消局比较特殊,场景是 先下注扣钱 然后结算加钱 然后取消在扣钱,所以会存在一笔单出现多次重复 加扣的场景.所以这种类型的单
//            //在我们系统重复账变逻辑 只能出现一次
////            if (amount.compareTo(BigDecimal.ZERO) > 0) {
////                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
////                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
////            } else {
////                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
////                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
////            }
//
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setUserId(userId);
//            coinRecordRequestVO.setOrderNo(orderId);
//            coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
////            if (amount.compareTo(BigDecimal.ZERO) > 0) {
////                coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
////                coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
////            } else {
////                coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
////                coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
////            }
//
//            ResponseVO<List<UserCoinRecordVO>> betResponseVO = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if (!betResponseVO.isOk()) {
//                log.info("取消局 下注前校验,查询是否有扣费记录失败:{}", coinRecordRequestVO);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            List<UserCoinRecordVO> recordVOS = betResponseVO.getData();
//            if(CollectionUtil.isNotEmpty(recordVOS)){
//                log.info("取消局 下注前校验,该单已经进行过取消局,不允许第二次重新账变:{}", coinRecordRequestVO);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//            return;
//        }else if (ShTransferTypeEnums.TRANSACTION_ROLLBACK.getCode().equals(transferType)) {//下注 交易回滚
//            //视讯下注交易回滚类型 9 ,该类型的请求只有加款行为.
//            // 这笔单在我们系统订单是必须 已经存在扣款记录,并且这比单没有进行加款记录(防止重复加款),
//            //并且我们这边根据请求订单的金额的绝对值去对比我们我们数据库里的扣款金额的绝对值.一致的时候进行加款
//            //不符合这种条件的情况下.拒绝请求
//
//            //加款
//            if (amount.compareTo(BigDecimal.ZERO) == 0) {
//                log.info("下注前校验,交易回滚,transferType:{},不能等于0:{}", transferType, amount);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            String orderNo = userCoinAddVO.getOrderNo();//订单扣费记录
//
//            //先判断是否有扣费记录
//            UserCoinRecordRequestVO betRecordRequestVO = new UserCoinRecordRequestVO();
//            betRecordRequestVO.setUserId(userCoinAddVO.getUserId());
//            betRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//            betRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//            betRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            betRecordRequestVO.setOrderNo(orderNo);
//            ResponseVO<List<UserCoinRecordVO>> betResponseVO = userCoinRecordApi.getUserCoinRecords(betRecordRequestVO);
//            if (!betResponseVO.isOk()) {
//                log.info("下注前校验,交易回滚,查询是否有扣费记录失败:{}", betRecordRequestVO);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            List<UserCoinRecordVO> recordVOS = betResponseVO.getData();
//            if (CollectionUtil.isEmpty(recordVOS)) {//该单没有下注记录
//                log.info("下注前校验,交易回滚,该单没有下注记录,不进行回滚:{}", betRecordRequestVO);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//
//            UserCoinRecordVO coinRecordVO = recordVOS.get(0);
//            BigDecimal coinValue = coinRecordVO.getCoinValue().abs();//账变金额
//
//            amount = amount.abs();//绝对值
//
//            if (coinValue.compareTo(amount) != 0) {
//                log.info("下注前校验,交易回滚,回滚金额与扣费金额不一致:回滚金额:{},扣费金额:{}", amount, coinValue);
//                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//            }
//
//            //判断是否有加款记录
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setUserId(userCoinAddVO.getUserId());
//
//        }  else {
//            log.info("视讯扣费类型异常:{},", transferType);
//            throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//        }
//
//        ResponseVO<Long> addResponseVO = userCoinRecordApi.userCoinRecordPageCount(userCoinAddVO);
//        if (!addResponseVO.isOk()) {
//            log.info("下注前校验,交易回滚,查询是否有加扣款金额失败");
//            throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
//        }
//
//        Long addCount = addResponseVO.getData();
//        if (addCount > 0) {//该单已经加扣过款,不进行重复加款
//            log.info("下注前校验,交易回滚,该单已经加扣过款,不进行重复加款:{}", userCoinAddVO);
//            throw new SHDefaultException(ShResultCodeEnums.REPEAT_TRANSACTIONS);
//        }
//
//
//    }
//
//
//    /**
//     * 账变类型转换
//     *
//     * @param userCoinAddVO 对象
//     * @param transferType  视讯类型
//     * @param amount        账变金额
//     */
//    private void toSetUserCoinAddVO(UserCoinAddVO userCoinAddVO, Integer transferType, BigDecimal amount) {
//
//        //下注 只能是扣钱
//        if (ShTransferTypeEnums.BET.getCode().equals(transferType) || ShTransferTypeEnums.TIPS.getCode().equals(transferType)) {
//
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//
//            //正常结算=加钱
//        } else if (ShTransferTypeEnums.PAYOUT.getCode().equals(transferType)) {
//
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//
//            //跳局=退钱
//        } else if (ShTransferTypeEnums.JUMP.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//            //重算局 跟 取消都是重新结算
//        } else if (ShTransferTypeEnums.RESETTLEMENT.getCode().equals(transferType) || ShTransferTypeEnums.CANCEL.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
//            if (amount.compareTo(BigDecimal.ZERO) > 0) {
//                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//            } else {
//                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//            }
//        } else if (ShTransferTypeEnums.TRANSACTION_ROLLBACK.getCode().equals(transferType)) {//下注 交易回滚
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        }
//    }
//
//
//
//    private boolean validateMD5Sign(String reqMD5Sign, String userName,VenueEnum venueEnum) {
//        VenueInfoVO venueConfigInfo = playVenueInfoApi.venueInfoByVenueCode(venueEnum.getVenueCode()
//                , "").getData();
//        String localSign = MD5Util.md5(venueConfigInfo.getMerchantKey() + "|" + userName);
//        return !reqMD5Sign.equals(localSign.toUpperCase());
//    }
//
//    public static void main(String[] args) {
//        String localSign = MD5Util.md5("QN67EBMS4X3Q7VSMST952V2PPYU7KE8Q" + "|" + "Utest_36375024");
//        System.err.println(localSign.toUpperCase());
//    }
//}
