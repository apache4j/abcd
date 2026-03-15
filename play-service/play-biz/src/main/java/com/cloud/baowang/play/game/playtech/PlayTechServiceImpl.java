package com.cloud.baowang.play.game.playtech;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.pt2.PT2ActionVO;
import com.cloud.baowang.play.api.vo.pt2.PT2BaseVO;
import com.cloud.baowang.play.api.vo.pt2.enums.PT2ErrorEnums;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2BaseRsp;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2RspVO;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.Jackpot;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.Pay;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.TransferFundsVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.dg2.vo.DG2GameHistory;
import com.cloud.baowang.play.game.dg2.vo.DGConstant;
import com.cloud.baowang.play.game.playtech.enums.PT2CurrencyEnum;
import com.cloud.baowang.play.game.playtech.enums.PlayTechLangEnum;
import com.cloud.baowang.play.game.sh.enums.SHPlayTypeEnum;
import com.cloud.baowang.play.game.sh.enums.ShOrderStatusEnum;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.PLAYTECH_SH)
public class PlayTechServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;

    private final GameInfoService gameInfoService;


    private final static Integer SUCCESS_CODE = 200;

    private static final String URL = "https://api-uat.agmidway.net/";



    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private static final String apiKey = "e89450e3a3b5b0c7966c0ac2d38c2332419dd63f07df9c3b9cc49a59da2c982d";
    private static final String agentId = "FY521UATCNY";
    private static final String userId = "Utest_28272974";



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
        UserInfoVO userInfoVO = getByUserId(userId);

//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.PLAYTECH_SH.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        String kiosk = venueInfoVO.getBetKey();

        String mainCurrency = userInfoVO.getMainCurrency();
        String venueCurrency = PT2CurrencyEnum.enumOfCode(mainCurrency).getPt2Code();
        String countryCodeKey = String.format(RedisConstants.VENUE_LANGUAGE, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        String countryCode = RedisUtil.getValue(countryCodeKey);
        PT2RspVO result = PT2RspVO.builder().requestId(actionVo.getRequestId())
                .username(actionVo.getUsername())
                .currencyCode(venueCurrency)
                .countryCode(countryCode)
                .build();
        return PT2BaseRsp.success(result);
    }

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
        UserInfoVO userInfoVO = getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        String orderId = actionVo.getTransactionCode();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, orderId, true);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return  PT2BaseRsp.success(rspVO,userCoin.getTotalAmount());
        }
        //检查余额
        BigDecimal amount = new BigDecimal(actionVo.getAmount());
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
        UserInfoVO userInfoVO = getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;

        Jackpot jackpot = actionVo.getJackpot();
        Pay pay = actionVo.getPay();
        totalAmount = jackpot.getWinAmount().add(new BigDecimal(pay.getAmount()));
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
        UserInfoVO userInfoVO = getByUserId(userId);
        PT2BaseRsp baseRsp = checkRequestValid(userInfoVO, actionVo, true);
        if (baseRsp != null) {
            return baseRsp;
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            PT2RspVO rspVO = buildBaseRsp(actionVo);
            return PT2BaseRsp.success(rspVO,BigDecimal.ZERO);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        PT2RspVO rspVO = buildBaseRsp(actionVo);
        return  PT2BaseRsp.success(rspVO,totalAmount);
    }



    public PT2BaseRsp transferFunds(TransferFundsVO actionVo) {
        log.info("pt2 transfer funds "+actionVo);
        String userId = userCheck(actionVo.getUsername());
        if (StringUtils.isBlank(userId)) {
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(actionVo.getRequestId()).build(), PT2ErrorEnums.INTERNAL_ERROR);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        String orderId = actionVo.getTransactionCode();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(userCoinRecords)) {
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
        userCoinAddVOPayout.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setVenueCode(VenuePlatformConstants.PLAYTECH_SH);
        userCoinAddVOPayout.setThirdOrderNo(remark);
        return toUserCoinHandle(userCoinAddVOPayout);
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
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PLAYTECH_SH);
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setThirdOrderNo(remark);

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(cancelBetRecord)) {
            //订单已处理
            return cancelBetRecord;

        }
        return org.apache.commons.compress.utils.Lists.newArrayList();
    }

    public PT2BaseRsp checkRequestValid(UserInfoVO userInfoVO, PT2BaseVO req, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("checkRequestValid : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.PLAYTECH_SH.getVenueName());
            return PT2BaseRsp.failed(PT2RspVO.builder().requestId(req.getRequestId()).build(), PT2ErrorEnums.AUTHENTICATION);
        }

        if (isBetting) {
//            String mainCurrency = userInfoVO.getMainCurrency();
//            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(),
//                    VenueEnum.PLAYTECH_SH.getVenueCode(),
//                    mainCurrency);
            if (venueMaintainClosed(VenuePlatformConstants.PLAYTECH_SH,userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.PLAYTECH_SH.getVenueCode());
                return PT2BaseRsp.failed(PT2RspVO.builder().requestId(req.getRequestId()).build(), PT2ErrorEnums.ERR_TRANSACTION_DECLINED);
            }


            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.PLAYTECH_SH.getVenueCode());
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
        CasinoMemberReq casinoMember = new CasinoMemberReq();
        String userId = adaptUserAccount(username);
        if (StringUtils.isBlank(userId)) {
            return null;
        }

        casinoMember.setUserAccount(userId);
        casinoMember.setVenueCode(VenueEnum.PLAYTECH_SH.getVenueCode());
        CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(casinoMember);
        return casinoMemberVO.getUserId();
    }



    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
       return ResponseVO.success();
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        log.info("playtech login : "+loginVO+"casinoMemberVO:"+casinoMemberVO);
        String currencyCode = loginVO.getCurrencyCode();
        String venueCurrencyCode = PT2CurrencyEnum.getPt2CodeByCode(currencyCode);
        if (venueCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String agentId = venueDetailVO.getMerchantNo();
        String apiKey = venueDetailVO.getAesKey();
        String serverName = venueDetailVO.getMerchantKey();
        String kiosk = venueDetailVO.getBetKey();
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(apiKey)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        String apiUrl = venueDetailVO.getApiUrl();
        String gameCode = loginVO.getGameCode();
        String venueUserAccount = casinoMemberVO.getVenueUserAccount();
        String venueLanguage = PlayTechLangEnum.conversionLang(loginVO.getLanguageCode());
        String url = apiUrl + "from-operator/getGameLaunchUrl";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("requestId", UUID.randomUUID().toString());
        dataMap.put("serverName", serverName);
        String userName = formatAccount(kiosk,venueUserAccount);
        dataMap.put("username", userName);
        dataMap.put("gameCodeName", gameCode);
        String clientPlatform = DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType()) ? "web" : "mobile";
        dataMap.put("clientPlatform", clientPlatform);
        dataMap.put("language", venueLanguage);
        dataMap.put("externalToken",getToken(kiosk));
        String json = JSONObject.toJSONString(dataMap);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        head.put("x-auth-kiosk-key", apiKey);

        String rsp = HttpClientHandler.post(url, head, json);
        if (rsp != null) {
            JSONObject obj = JSON.parseObject(rsp);
            if (obj.getInteger("code").equals(SUCCESS_CODE)) {
                String source =obj.getJSONObject("data").getString("url");
                String userAccount = loginVO.getUserAccount();
                String venueCode = venueDetailVO.getVenueCode();
                GameLoginVo gameLoginVo = GameLoginVo.builder()
                        .source(source)
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(userAccount)
                        .venueCode(venueCode).build();
                return ResponseVO.success(gameLoginVo);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        List<GameInfoPO> gameInfoList = gameInfoService.queryGameByVenueCode(venueInfoVO.getVenueCode());
        gameInfoList.forEach(gameInfo -> {
            JSONObject gameJson = new JSONObject();
            gameJson.put("deskName", gameInfo.getGameName());
            gameJson.put("deskNumber", gameInfo.getAccessParameters());
            resultList.add(gameJson);
        });
        return ResponseVO.success(resultList);
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        log.info("dg2 getBetRecordList : "+venuePullParamVO);
        String apiUrl = venueInfoVO.getApiUrl();
        String agentId = venueInfoVO.getMerchantNo();
        String apiKey = venueInfoVO.getAesKey();
        String url = apiUrl + DGConstant.ORDER_RECORD;
        Map<String, String> head = Maps.newHashMap();
        buildSign(agentId, apiKey, head);
        try {
            String rsp = HttpClientHandler.doPostPar(url, head, null);
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject == null || !Objects.equals(jsonObject.getInteger("codeId"), SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} : 当前时间 : {} ", venueInfoVO.getVenueCode(), rsp, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("DG2三方拉单返回结果 : {}  代理ID : {} ", rsp, agentId);
            JSONArray dataArray = jsonObject.getJSONArray("list");
            if (dataArray.isEmpty()) {
                return ResponseVO.success();
            }
            List<DG2GameHistory> gameHistoryList = dataArray.toJavaList(DG2GameHistory.class);
            //标记下
            handleRemoteOrder(gameHistoryList, venueInfoVO);
        } catch (Exception e) {
            log.info("DG2 拉取注单失败 " + e.getMessage());
        }

        return ResponseVO.success();

    }

    private static void buildSign(String agentId, String apiKey, Map<String, String> head) {
        String time = String.valueOf(System.currentTimeMillis());
        String md5Key = agentId + apiKey + time;
        String sign = MD5Util.MD5Encode(md5Key);
        head.put("agent", agentId);
        head.put("sign", sign);
        head.put("time", time);
    }

    private void handleRemoteOrder(List<DG2GameHistory> gameHistoryList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> thirdAccounts = gameHistoryList.stream().map(order -> adaptUsername(order.getUserName())).distinct().toList();
        log.info("getCasinoMemberByUsers : thirdAccounts " +thirdAccounts);
        Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(thirdAccounts, venueInfoVO.getVenuePlatform());

        //userInfo
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DG2.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DG2.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (DG2GameHistory order : gameHistoryList) {
            order.setUserName(adaptUsername(order.getUserName()));
            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(order.getUserName());
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), order.getUserName());
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
                continue;
            }
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            Integer gameTypeId = VenueTypeEnum.SH.getCode();
            recordVO.setVenueType(gameTypeId);
            recordVO.setSiteName(siteNameMap.get(userInfoVO.getSiteCode()));
            recordVO.setSiteCode(userInfoVO.getSiteCode());
            recordVO.setUserName(userInfoVO.getUserName());
            recordVO.setVenueType(VenueEnum.DG2.getType().getCode());
            list.add(recordVO);
            if (list.size() == 300) {
                orderRecordProcessService.orderProcess(list);
                list.clear();
            }

        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }

    public String adaptUsername(String source) {
        return source.substring(0, 1).toUpperCase() + source.substring(1).toLowerCase();
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, DG2GameHistory orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBetPoints());
        recordVO.setBetIp(orderResponseVO.getIp() == null ? null : orderResponseVO.getIp());
        recordVO.setBetContent(orderResponseVO.getBetDetail());
        Long betTime = TimeZoneUtils.formatDateStrToTimestamp(orderResponseVO.getBetTime());
        recordVO.setBetTime(betTime);
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUserName());
        if (userLoginInfoVO != null) {
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }

        String gameId = orderResponseVO.getGameId().toString();
        Integer tableId = orderResponseVO.getTableId();

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(gameId);
        recordVO.setRoomTypeName(DG2GameTypeEnum.enumOfCode(gameId).getDescription());
        recordVO.setGameNo(String.valueOf(orderResponseVO.getPlayId()));
        recordVO.setThirdGameCode(gameId);
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getId()));
        recordVO.setValidAmount(orderResponseVO.getBetPoints());
        recordVO.setWinLossAmount(orderResponseVO.getWinOrLoss());
        BigDecimal winLose = orderResponseVO.getWinOrLoss().subtract(orderResponseVO.getBetPoints());
        recordVO.setPayoutAmount(winLose);
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());

        Integer orderStatus = getLocalOrderStatus(orderResponseVO.getIsRevocation());
        recordVO.setOrderStatus(orderStatus);
        //结算时间
        String calTimeStr = orderResponseVO.getCalTime();
        Long calTime = TimeZoneUtils.formatDateStrToTimestamp(calTimeStr);
        recordVO.setSettleTime(calTime);
        if (Objects.equals(orderStatus, OrderStatusEnum.CANCEL.getCode())) {
            recordVO.setFirstSettleTime(calTime);
        }
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setDeskNo(String.valueOf(orderResponseVO.getTableId()));
        recordVO.setBootNo(String.valueOf(orderResponseVO.getShoeId()));
        recordVO.setResultList(orderResponseVO.getResult());
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setOrderInfo(orderResponseVO.getBetDetail());
        recordVO.setPlayType(getPlayType(orderResponseVO));
        GameInfoPO gameInfoPO = paramToGameInfo.get(tableId);
        recordVO.setGameName(gameInfoPO.getGameName());
        return recordVO;
    }

    private String getPlayType(DG2GameHistory orderResponseVO) {
        //注单类型(1:注单，2: 红包小费)
        if (CommonConstant.business_two == orderResponseVO.getGameType()) {
            return SHPlayTypeEnum.TIPS.getCode();
        } else {
            return SHPlayTypeEnum.ANTE.getCode();
        }
    }

    public Integer getLocalOrderStatus(Integer orderStatus) {
        //结算状态(0:未结算，1:已结算，2:已撤销，3: 冻结)
        Map<Integer, Integer> statusMap = new HashMap<>();
        statusMap.put(ShOrderStatusEnum.NOT_SETTLEMENT.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
        statusMap.put(ShOrderStatusEnum.SETTLEMENT.getCode(), OrderStatusEnum.SETTLED.getCode());
        statusMap.put(ShOrderStatusEnum.CANCEL.getCode(), OrderStatusEnum.CANCEL.getCode());
        statusMap.put(CommonConstant.business_three, OrderStatusEnum.ABERRANT.getCode());
        Integer localStatus = statusMap.get(orderStatus);

        if (localStatus != null) {
            return localStatus;
        }

        log.info("视讯返回未知订单状态:{},", orderStatus);
        return null;
    }


    public static void main(String[] args) {
        loginTest();
//        queryGameList();
//        createMemberTest();
//        String input = "UTEST_37846152";
//        String output =input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
//        System.out.println(output);



    }



    public static void loginTest() {

        String url = URL + "from-operator/getGameLaunchUrl";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("requestId", UUID.randomUUID().toString());
        dataMap.put("serverName", "AGCASTG");
        String userName = PlayTechConstant.PREFIX+"_"+"UTEST_37846152";
        dataMap.put("username", userName);
        dataMap.put("gameCodeName", "ubal;bal_emperorbaccarat");
        dataMap.put("clientPlatform", "web");
        dataMap.put("language", "en");
        String token = PlayTechConstant.PREFIX+"_"+UUID.randomUUID();
        dataMap.put("externalToken",token);
//        dataMap.put("playMode", "1");
        String json = JSONObject.toJSONString(dataMap);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
//        buildSign(agentId, apiKey, head);
        head.put("x-auth-kiosk-key", apiKey);


        String rsp = HttpClientHandler.post(url, head, json);
//        JSONObject jsonObject = JSONObject.parseObject(rsp);
//        JSONArray list = jsonObject.getJSONArray("list");
//        List<String> urlList = list.toJavaList(String.class);
        JSONObject obj = JSON.parseObject(rsp);

        // 取 data 里的 url
        String data = obj.getJSONObject("data").getString("url");


        System.out.println("DG2ServiceImpl.loginTest ---- " + data);
    }



    public String formatAccount(String prefix, String input) {
        return prefix+"_"+input.toUpperCase();
    }


    private String getWinLossResult(BigDecimal winLossAmount) {
        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
                : WinLossEnum.TIE.getName();

    }

    private String getToken(String prefix) {
        String userId = CurrReqUtils.getOneId();

        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.PLAYTECH_SH.getVenueCode(), userId);
        RedisUtil.deleteKey(key);

        String token = prefix+userId + UUID.randomUUID();

        RedisUtil.setValue(key, token, 60L, TimeUnit.MINUTES);

        return token;
    }

}
