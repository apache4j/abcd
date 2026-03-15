package com.cloud.baowang.play.wallet.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.DBEVGErrorEnum;
import com.cloud.baowang.play.wallet.enums.DBFishingChessErrorEnum;
import com.cloud.baowang.play.wallet.service.DBScratchLotteryGameApi;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.vo.db.AESDBUtil;
import com.cloud.baowang.play.wallet.vo.req.db.config.DBCryptoConfig;
import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.BalanceChangeBody;
import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.CommonTradeInfo;
import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.wallet.vo.res.db.evg.DBEVGBaseRsp;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DBScratchLotteryGameApiImpl extends BaseService implements DBScratchLotteryGameApi {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountApi venueUserAccountApi;

    private DBCryptoConfig cryptoConfig;

    private final GameInfoApi gameInfoApi;


    private BalanceChangeBody decrypt(String body,String merchantNo) {
        String key = cryptoConfig.getKey(VenueEnum.DBSCRATCH.getVenueCode(),merchantNo);
        String iv = cryptoConfig.getIV(VenueEnum.DBSCRATCH.getVenueCode(),merchantNo);
        try {
            String decrypt = AESDBUtil.decrypt(body, key, iv);
            return JSON.parseObject(decrypt, BalanceChangeBody.class);
        } catch (Exception e) {
            log.error(" DBSCRATCH 数据解密失败 - {}", e.getMessage());
            return null;
        }
    }

    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll("\\D+", "");
    }

    public DBEVGBaseRsp checkRequestValid(UserInfoVO userInfoVO) {

        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                .venueCode(VenueEnum.DBSCRATCH.getVenueCode())
                .currencyCode(userInfoVO.getMainCurrency()).build();
        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBSCRATCH.getVenueCode());
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.UNKNOWN_ERROR);
        }

        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBSCRATCH.getVenueCode());
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
        }

        return null;
    }

    public UserInfoVO userCheck(BalanceChangeBody req) {
        if (req == null) {
            return null;
        }
        String userId = adaptThirdAccount(req.getMemberId());
        if (userId == null) {
            return null;
        }
        return userInfoApi.getByUserId(userId);
    }

    @Override
    public DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String reqStr) {

        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryBalance : {}", req);

        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
        if (ObjectUtil.isNull(userCenterCoin)) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.ACCOUNT_FROZEN);
        }
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder().balance(BigDecimal.ZERO.longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        CommonTradeInfo rsp = CommonTradeInfo.builder().balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
        return DBEVGBaseRsp.success(rsp);
    }

    @Override
    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("db_scratch balanceChange : {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }
        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getOrderId());
        if (coinRecordVOS != null && !coinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
            if (userCoin == null) {
                return DBEVGBaseRsp.failed(DBEVGErrorEnum.USER_NOT_FOUND);
            }
            BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(req.getTradeType())
                    .tradeAmount(req.getTradeAmount())
                    .balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }

        Integer tradeType = req.getTradeType();
        return switch (tradeType) {
            case 2 -> handleBet(req, userInfoVO);
            case 1 -> handleSettle(req, userInfoVO);
            default -> DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_ERROR);
        };
    }


    @Override
    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryOrderStatus : {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getOrderId());
        if (coinRecordVOS == null || coinRecordVOS.isEmpty()) {
            //订单不存在
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.ORDER_NOT_FOUND);
        }

        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
        casinoMember.setUserId(userInfoVO.getUserId());
        casinoMember.setVenueCode(VenueEnum.DBSCRATCH.getVenueCode());
        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
        if (!respVO.isOk() || respVO.getData() == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_MAINTAINED_2);
        }
        CommonTradeInfo rsp = CommonTradeInfo.builder().status(CommonConstant.business_one)
                .memberId(respVO.getData().getVenueUserAccount())
                .tradeAmount(req.getTradeAmount())
                .tradeId(req.getTradeId())
                .tradeType(req.getTradeType()).build();
        return DBEVGBaseRsp.success(rsp);
    }

    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String orderNo) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderNo);
        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (!userCoinRecords.isOk()) {
            return null;
        }
        List<UserCoinRecordVO> coinRecordVOS = userCoinRecords.getData();
        return coinRecordVOS;
    }


    private DBEVGBaseRsp handleSettle(BalanceChangeBody req, UserInfoVO userInfoVO) {
        String tradeId = req.getOrderId();//流水号
        String betId = req.getBetId();//注单id
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
            if (userCoin == null) {
                return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
            }
            BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }


        CoinRecordResultVO coinRecordResultVO = this.handleSettleCoin(userInfoVO, betId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(balance.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        } else {
            log.info("DBSCRATCH settle : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.UNKNOWN_ERROR);
        }
    }

    private DBEVGBaseRsp handleBet(BalanceChangeBody req, UserInfoVO userInfoVO) {
        log.info("handleBet : "+req);
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
        if (userCoin == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.BALANCE_NOT_ENOUGH);
        }

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }
        if (totalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.BALANCE_NOT_ENOUGH);
        }

        String tradeId = req.getOrderId();//流水号
        String betId = req.getBetId();//注单id
        CoinRecordResultVO coinRecordResultVO = handleBetCoin(userInfoVO, betId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(balance.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        } else {
            log.info("DBSCRATCH bet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.UNKNOWN_ERROR);
        }
    }


    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
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
        userCoinAddVOPayout.setVenueCode(VenueEnum.DBSCRATCH.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    protected CoinRecordResultVO handleBetCoin(UserInfoVO userInfoVO, String betId, BigDecimal tradeAmount, String tradeId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(betId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setVenueCode(VenueEnum.DBSCRATCH.getVenueCode());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(tradeId);
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

}
