package com.cloud.baowang.play.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.PT2ErrorEnums;
import com.cloud.baowang.play.wallet.service.PT2GameApi;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.vo.pt2.PT2CurrencyEnum;
import com.cloud.baowang.play.wallet.vo.req.pt2.PT2ActionVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.PT2BaseVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.Jackpot;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.Pay;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.TransferFundsVO;
import com.cloud.baowang.play.wallet.vo.res.pt2.PT2BaseRsp;
import com.cloud.baowang.play.wallet.vo.res.pt2.PT2RspVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PT2GameApiImpl extends BaseService implements PT2GameApi {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountApi venueUserAccountApi;

    private final GameInfoApi gameInfoApi;


    @Override
    public PT2BaseRsp authenticate(PT2ActionVO actionVo) {

        log.info("pt2 authenticate "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        String externalToken = actionVo.getExternalToken();
        if (StringUtils.isBlank(token) || !externalToken.equals(token)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.PLAYTECH_SH.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        String kiosk = venueInfoVO.getBetKey();

        String mainCurrency = userInfoVO.getMainCurrency();
        String venueCurrency = PT2CurrencyEnum.enumOfCode(mainCurrency).getVenueCurrency();
        String countryCodeKey = String.format(RedisConstants.VENUE_LANGUAGE, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String countryCode = RedisUtil.getValue(countryCodeKey);
        PT2RspVO result = PT2RspVO.builder().requestId(actionVo.getRequestId())
                .username(actionVo.getUsername())
                .currencyCode(venueCurrency)
                .countryCode(countryCode)
                .build();
        return PT2BaseRsp.success(result);
    }

    @Override
    public PT2BaseRsp bet(PT2ActionVO actionVo) {
        log.info("pt2 bet "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        String externalToken = actionVo.getExternalToken();
        if (StringUtils.isBlank(token) || !externalToken.equals(token)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        String orderId = actionVo.getTransactionCode();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, orderId, true);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return  PT2BaseRsp.success(rspVO,userCoin.getTotalAmount());
        }
        //检查余额
        BigDecimal amount = new BigDecimal(actionVo.getAmount());
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());

        if (ObjectUtil.isNull(userCoin)|| userCoin.getTotalAmount().compareTo(BigDecimal.ZERO)<=0 || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.PLAYTECH_SH.getVenueCode(), actionVo, userCoin, amount);
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            BigDecimal balance = userCoin.getTotalAmount().compareTo(BigDecimal.ZERO)>=0?userCoin.getTotalAmount():BigDecimal.ZERO;
            return  PT2BaseRsp.success(rspVO,balance);
        }

        CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, amount, orderId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return  PT2BaseRsp.success(rspVO,coinRecordResultVO.getCoinAfterBalance());
        } else {
            log.info("dg2 bet : 失败 : actionVo -> "+ actionVo.getRequestId());
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.ERR_TRANSACTION_DECLINED);
        }
    }

    @Override
    public PT2BaseRsp gameroundresult(GameRoundResultVO actionVo) {
        log.info("pt2 getbalance "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        String externalToken = actionVo.getExternalToken();
        if (StringUtils.isBlank(token) || !externalToken.equals(token)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;

        Jackpot jackpot = actionVo.getJackpot();
        Pay pay = actionVo.getPay();
        totalAmount = jackpot.getWinAmount().add(new BigDecimal(pay.getAmount()));
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return  PT2BaseRsp.success(rspVO,userCoin.getTotalAmount());
        }
        String orderId = pay.getTransactionCode();
        String remark = actionVo.getGameHistoryUrl();
        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, totalAmount, remark);
        log.info("PT2奖金发放 : 用户 : {} transferId : {} activityNo : {} activityDate :  {} amount : {} ",
                userId, orderId, orderId, actionVo.getTransactionDate(),  totalAmount);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            PT2RspVO rspVO =PT2RspVO.builder().requestId(actionVo.getRequestId())
                    .externalTransactionCode(actionVo.getTransactionCode())
                    .externalTransactionDate(actionVo.getTransactionDate())
                    .build();
            return PT2BaseRsp.success(rspVO,coinRecordResultVO.getCoinAfterBalance());
        } else {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.INTERNAL_ERROR);
        }
    }

    @Override
    public PT2BaseRsp getbalance(PT2BaseVO actionVo) {
        log.info("pt2 getbalance "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        String externalToken = actionVo.getExternalToken();
        if (StringUtils.isBlank(token) || !externalToken.equals(token)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return PT2BaseRsp.success(rspVO,BigDecimal.ZERO);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        PT2RspVO rspVO = buildBaseRsp(actionVo);
        return  PT2BaseRsp.success(rspVO,totalAmount);
    }



    @Override
    public PT2BaseRsp transferFunds(TransferFundsVO actionVo) {
        log.info("pt2 transfer funds "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.INTERNAL_ERROR);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        String orderId = actionVo.getTransactionCode();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (userCoinRecords.isOk() && !userCoinRecords.getData().isEmpty()) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.ERR_TRANSACTION_DECLINED);
        }
        BigDecimal amount = new BigDecimal(actionVo.getAmount()).abs();
        String remark = actionVo.getType();
        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, amount, remark);
        log.info("PT2奖金发放 : 用户 : {} transferId : {} activityNo : {} activityDate :  {} amount : {} ",
                userId, orderId, orderId, actionVo.getTransactionDate(),  amount);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            PT2RspVO rspVO =PT2RspVO.builder().requestId(actionVo.getRequestId())
                    .externalTransactionCode(actionVo.getTransactionCode())
                    .externalTransactionDate(actionVo.getTransactionDate())
                    .build();
            return PT2BaseRsp.success(rspVO,coinRecordResultVO.getCoinAfterBalance());
        } else {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.INTERNAL_ERROR);
        }
    }




    @Override
    public PT2BaseRsp logout(PT2BaseVO actionVo) {
        log.info("pt2 logout "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        log.info("PT2 logout : "+actionVo.getUsername() +" key - "+key);
        RedisUtil.deleteKey(key);
        PT2RspVO rspVO = PT2RspVO.builder().requestId(actionVo.getRequestId()).build();
        return PT2BaseRsp.success(rspVO);
    }

    public PT2RspVO buildBaseRsp(PT2ActionVO actionVo) {
        return PT2RspVO.builder().requestId(actionVo.getRequestId())
                .externalTransactionCode(actionVo.getTransactionCode())
                .externalTransactionDate(actionVo.getTransactionDate())
                .build();
    }


    public PT2RspVO buildBaseRsp(PT2BaseVO actionVo) {
        return PT2RspVO.builder().requestId(actionVo.getRequestId())
                .username(actionVo.getUsername())
                .externalTransactionDate(actionVo.getExternalToken())
                .build();
    }


    /**
     * 派彩
     */
    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
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
        userCoinAddVOPayout.setVenueCode(VenueEnum.JDB.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }


    /**
     * 下注
     */
    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        userCoinAddVO.setRemark(remark);
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }




    /**
     * 订单校验 支出? 收入?
     */
    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String orderId, boolean expenses) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }

    public PT2BaseRsp checkRequestValid(UserInfoVO userInfoVO, PT2BaseVO req, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("checkRequestValid : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.SEXY.getVenueName());
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(req.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }

        if (isBetting) {
            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                    .venueCode(VenueEnum.SEXY.getVenueCode())
                    .currencyCode(userInfoVO.getMainCurrency()).build();
            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.SEXY.getVenueCode());
                return PT2BaseRsp.failed(PT2RspVO.builder().requestId(req.getRequestId()).build(), PT2ErrorEnums.ERR_TRANSACTION_DECLINED);
            }


            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.SEXY.getVenueCode());
                return PT2BaseRsp.failed(PT2RspVO.builder().requestId(req.getRequestId()).build(), PT2ErrorEnums.ERR_TRANSACTION_DECLINED);
            }

        }
        return null;
    }



    public  String adaptUserAccount(String input) {
        if (input == null) return null;
        String sub = input.replaceFirst("^[^_]+_", "");
        return sub.substring(0, 1).toUpperCase() + sub.substring(1).toLowerCase();
    }

    public String userCheck(String username) {
        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
        String userId = adaptUserAccount(username);
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        casinoMember.setVenueUserAccount(userId);
        casinoMember.setVenueCode(VenueEnum.PLAYTECH_SH.getVenueCode());
        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
        if (!respVO.isOk() || respVO.getData() == null) {
            return null;
        }
        return respVO.getData().getUserId();
    }

}
