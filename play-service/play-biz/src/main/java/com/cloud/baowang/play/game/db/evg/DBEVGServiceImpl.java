package com.cloud.baowang.play.game.db.evg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.db.config.DBCryptoConfig;
import com.cloud.baowang.play.api.vo.db.evg.vo.BalanceChangeBody;
import com.cloud.baowang.play.api.vo.db.evg.vo.CommonTradeInfo;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBEVGErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBFishingChessErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.db.vo.AESDBUtil;
import com.cloud.baowang.play.game.db.vo.Md5StringUtils;
import com.cloud.baowang.play.game.db.evg.enums.PlayTypeEnum;
import com.cloud.baowang.play.game.db.vo.DBOrderCommonVO;
import com.cloud.baowang.play.game.jdb.utils.AESEncrypt;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
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
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_EVG)
public class DBEVGServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final static String SUCCESS_CODE = "1000";


    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private final DBCryptoConfig cryptoConfig;



    private BalanceChangeBody decrypt(String body, String merchantNo) {
        String key = cryptoConfig.getKey(VenueEnum.DBEVG.getVenueCode(),merchantNo);
        String iv = cryptoConfig.getIV(VenueEnum.DBEVG.getVenueCode(),merchantNo);
        try {
            String decrypt = AESDBUtil.decrypt(body, key, iv);
            return JSON.parseObject(decrypt, BalanceChangeBody.class);
        } catch (Exception e) {
            log.error(" db_evg 数据解密失败 - {}", e.getMessage());
            return null;
        }
    }


    public DBEVGBaseRsp checkRequestValid(UserInfoVO userInfoVO) {

        if (venueMaintainClosed( VenueEnum.DBEVG.getVenueCode(),userInfoVO.getSiteCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBEVG.getVenueCode());
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.SYSTEM_MAINTAINED);
        }


        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBEVG.getVenueCode());
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.USER_NOT_FOUND);
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
        return getByUserId(userId);
    }

    public DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String reqStr) {

        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryBalance : {}", req);

        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_ERROR);
        }
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
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

    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("balanceChange : {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_ERROR);
        }
        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTradeId());
        if (coinRecordVOS != null && !coinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
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
            case 1 -> handleBet(req, userInfoVO);
            case 2, 4 -> handleSettle(req, userInfoVO);
            case 3 -> handleCancel(req, userInfoVO);
            default -> DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_ERROR);
        };
    }


    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        BalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryOrderStatus : {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.PARAM_ERROR);
        }

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getTradeId());
        if (coinRecordVOS == null || coinRecordVOS.isEmpty()) {
            //订单不存在
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.ORDER_NOT_FOUND);
        }
        CasinoMemberReq casinoMember = new CasinoMemberReq();
        casinoMember.setUserId(userInfoVO.getUserId());
        casinoMember.setVenueCode(VenueEnum.DBCHESS.getVenueCode());
        CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(casinoMember);
        if (casinoMemberVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_MAINTAINED_2);
        }

        CommonTradeInfo rsp = CommonTradeInfo.builder().status(CommonConstant.business_one)
                .memberId(casinoMemberVO.getVenueUserAccount())
                .tradeAmount(req.getTradeAmount())
                .tradeId(req.getTradeId())
                .tradeType(req.getTradeType()).build();
        return DBEVGBaseRsp.success(rsp);
    }

    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String tradeId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(tradeId);
        return getUserCoinRecords(coinRecordRequestVO);
    }


    public List<UserCoinRecordVO> checkCoinRecord(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;
        }
        return Lists.newArrayList();
    }


    private DBEVGBaseRsp handleCancel(BalanceChangeBody req, UserInfoVO userInfoVO) {
        String tradeId = req.getTradeId();
        String betId = req.getBetId();
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount());

        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (userCoin == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.USER_NOT_FOUND);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        long rspBalance = totalAmount.multiply(new BigDecimal(100)).longValue();
        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(rspBalance).build();
            return DBEVGBaseRsp.success(rsp);
        }

        List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, betId);
        UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
        if (betOrder == null) {
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(rspBalance).build();
            return DBEVGBaseRsp.success(rsp);
        }

        UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
        if (cancelBet != null) {
            CommonTradeInfo rsp = CommonTradeInfo.builder().tradeType(tradeType).tradeAmount(req.getTradeAmount()).balance(rspBalance).build();
            return DBEVGBaseRsp.success(rsp);
        }

        CoinRecordResultVO coinRecordResultVO = this.handleCancelBetCoin(userInfoVO, betId, tradeAmount, tradeId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(balance.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        } else {
            log.info("db_evg cancelBet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.SYSTEM_MAINTAINED);
        }
    }

    private DBEVGBaseRsp handleSettle(BalanceChangeBody req, UserInfoVO userInfoVO) {
        String tradeId = req.getTradeId();//流水号
        String betId = req.getBetId();//注单id
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (userCoin == null) {
                return DBEVGBaseRsp.failed(DBEVGErrorEnum.USER_NOT_FOUND);
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
            log.info("db_evg settle : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.SYSTEM_MAINTAINED);
        }
    }

    private DBEVGBaseRsp handleBet(BalanceChangeBody req, UserInfoVO userInfoVO) {
        log.info("handleBet : "+req);
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (userCoin == null) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.USER_NOT_FOUND);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.BALANCE_NOT_ENOUGH);
        }

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }
        if (totalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.BALANCE_NOT_ENOUGH);
        }

        String tradeId = req.getTradeId();//流水号
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
            log.info("db_evg bet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBEVGErrorEnum.SYSTEM_MAINTAINED);
        }
    }

    protected CoinRecordResultVO handleCancelBetCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DBEVG.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String tradeId) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(tradeId);
        userCoinAddVO.setVenueCode(VenueEnum.DBEVG.getVenueCode());
        userCoinAddVO.setThirdOrderNo(tradeId);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setVenueCode(VenueEnum.DBEVG.getVenueCode());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(tradeId);
        userCoinAddVO.setThirdOrderNo(tradeId);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String apiUrl = venueDetailVO.getApiUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getMerchantKey();


        String agent = venueDetailVO.getMerchantNo();
        long timestamp = Instant.now().getEpochSecond();
        System.out.println("DBEVGServiceImpl.timestamp. - "+timestamp);
        int randno = generate10DigitInt();
        String sign = buildSign(agent, timestamp, key);
        log.info("apiUrl : " + apiUrl + " key : " + key + " iv : " + iv + " agent : " + agent);
        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String url = apiUrl + "/launchGameById" + "?agent=" + agent + "&timestamp=" + timestamp + "&randno=" + randno + "&sign=" + sign;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", casinoMemberVO.getVenueUserAccount());
        dataMap.put("memberName", casinoMemberVO.getVenueUserAccount());
        dataMap.put("memberPwd", casinoMemberVO.getCasinoPassword());
        dataMap.put("deviceType", getDeviceType());
        dataMap.put("memberIp", loginVO.getIp());
        dataMap.put("gameId", loginVO.getGameCode());
        String json = JSONObject.toJSONString(dataMap);
        log.info("DB_EVG三方登录参数 : " + json);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "text/plain");

        String body = AESDBUtil.encrypt(json, key, iv);
        String rsp = HttpClientHandler.post(url, head, body);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getString("code").equals(SUCCESS_CODE)) {
                String source = jsonObject.getString("data");
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

    //(0:windows, 1:mac, 2:ios, 3:android, 4:未知)
    public int getDeviceType() {
        Integer reqDeviceType = CurrReqUtils.getReqDeviceType();
        if (reqDeviceType == null) {
            return 4;
        }
        return switch (reqDeviceType) {
            case 1 -> 0;
            case 2, 3 -> 2;
            case 4, 5 -> 3;
            default -> 4;
        };
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        String apiUrl = venueDetailVO.getGameUrl();
        String key = venueDetailVO.getAesKey();
        String iv = venueDetailVO.getMerchantKey();

        String agent = venueDetailVO.getMerchantNo();
        long timestamp = Instant.now().getEpochSecond();
        int randno = generate10DigitInt();
        String sign = buildSign(agent, timestamp, key);

        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String url = apiUrl + "/queryGameOrders" + "?agent=" + agent + "&timestamp=" + timestamp + "&randno=" + randno + "&sign=" + sign;
        Long startTime = venuePullParamVO.getStartTime()/1000;
        Long endTime = venuePullParamVO.getEndTime()/1000;

        int pageNum = 1;
        int pageSize = 1000;
        while (true) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("beginTime", startTime);
            dataMap.put("endTime", endTime);
            dataMap.put("pageNum", pageNum);
            dataMap.put("pageSize", pageSize);
            String json = JSONObject.toJSONString(dataMap);

            String body = AESDBUtil.encrypt(json, key, iv);
            if (body == null) {
                throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
            }

            String rsp = HttpClientHandler.post(url, null, body);
            JSONObject root = JSONObject.parseObject(rsp);
            if (root == null || !root.getString("code").equals(SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueDetailVO.getVenueCode(), rsp, json, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("400001-result:{}", rsp);
            JSONObject data = root.getJSONObject("data");

            List<DBOrderCommonVO> records = data.getJSONArray("list").toJavaList(DBOrderCommonVO.class);
            if (records.isEmpty()) {
                break;
            }
            handleRemoteOrder(records, venueDetailVO);
            if (records.size() < pageSize) {
                break;
            }
            pageNum++;
        }


        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<DBOrderCommonVO> orderList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = orderList.stream().map(order -> adaptThirdAccount(order.getMmi())).distinct().toList();


        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DBEVG.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DBEVG.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (DBOrderCommonVO order : orderList) {

            UserInfoVO userInfoVO = userMap.get(adaptThirdAccount(order.getMmi()));
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), order.getMmi());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setVenueType(VenueEnum.DBEVG.getType().getCode());
            list.add(recordVO);
        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, DBOrderCommonVO order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(BigDecimal.valueOf(order.getTb()).divide(new BigDecimal(100), 2, RoundingMode.DOWN).abs());

        Long betTime = order.getSt() * 1000L;
        Long settleTime = order.getEt() * 1000L;
        ;
        recordVO.setBetTime(betTime);
        recordVO.setSettleTime(settleTime);

        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(order.getMmi());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(order.getBi());
//        recordVO.setTransactionId(String.valueOf(order.getHistoryId()));
        recordVO.setOrderInfo(PlayTypeEnum.fromCode(order.getBrt()).getDescription());
        BigDecimal betAmount = BigDecimal.valueOf(order.getBc()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        BigDecimal netWin = BigDecimal.valueOf(order.getMw()).divide(new BigDecimal(100),2, RoundingMode.DOWN);
        recordVO.setResultList(getWinLossResult(netWin));
        recordVO.setWinLossAmount(netWin);
        recordVO.setPayoutAmount(betAmount.add(netWin));

        recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
        recordVO.setOrderClassify(OrderStatusEnum.SETTLED.getCode());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(order));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());


        recordVO.setValidAmount(new BigDecimal(order.getBc()).divide(new BigDecimal(100), 2, RoundingMode.DOWN));

        Integer gameType = order.getGf();
        if (gameType == 0) {
            recordVO.setRoomType("游戏类");
        } else if (gameType == 100) {
            recordVO.setRoomType("活动类");
        }
        recordVO.setRoomTypeName(order.getGn());

        String gameCode = String.valueOf(order.getGi());
        recordVO.setThirdGameCode(gameCode);
        recordVO.setGameNo(order.getCn());
        recordVO.setGameName(order.getGn());

        return recordVO;
    }

    public String adaptThirdAccount(String thirdAccount) {
        if (StringUtils.isEmpty(thirdAccount)) {
            return null;
        }
        return thirdAccount.replaceAll("\\D+", "");
    }


    private String getWinLossResult(BigDecimal winLossAmount) {
        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
                : WinLossEnum.TIE.getName();

    }


    private static String buildSign(String agentId, Long timestamp, String apiKey) {
        String md5Key = agentId + timestamp + apiKey;
        String source = MD5Util.MD5Encode(md5Key);
        return Md5StringUtils.mix(source);
    }


    public static void main(String[] args) {
        String agent = "wintousddz89f8c755";
        long timestamp = 1758968968366L;
        String key = "MF72v8599RpN1sPl";
        System.out.println("DBEVGServiceImpl.main sign - "+buildSign(agent, timestamp, key));
        //loginTest();
        //eeb579fb0e5106ee6ab22a010335d82f
        //JMcc8f0044dXia6ae1207Km0be18f18e63c650dQ

    }

    public static int generate10DigitInt() {
        int min = 1000000000;
        int max = Integer.MAX_VALUE;
        return ThreadLocalRandom.current().nextInt(min, max);
    }


    public static void loginTest() {
        String agent = "wintousddz89f8c755";
        String apiKey = "MF72v8599RpN1sPl";
        long timestamp = Instant.now().getEpochSecond();
        int randno = generate10DigitInt();
        String sign = buildSign(agent, 1758968968366L, apiKey);
        System.out.println("DBEVGServiceImpl.loginTest sign : " + sign);
        String key = "MF72v8599RpN1sPl";
        String iv = "0102030405060708";

        String url = "https://dyuat-eg-openapi.666789.site" + "/launchGameById" + "?agent=" + agent + "&timestamp=" + 1758968968366L + "&randno=" + randno + "&sign=" + sign;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", "Utest_45469970");
        dataMap.put("memberName", "Utest_45469970");
        dataMap.put("memberPwd", "kmwV2gjC");
        dataMap.put("deviceType", 0);
        dataMap.put("memberIp", "103.20.81.116");
        dataMap.put("gameId", 5552);
        String json = JSONObject.toJSONString(dataMap);

        System.out.println("DBEVGServiceImpl.loginTest. json - " + json);
        String body = AESDBUtil.encrypt(json, key, iv);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "text/plain");

        String rsp = HttpClientHandler.post(url, head, body);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        String source = jsonObject.getString("data");

        System.out.println("DG2ServiceImpl.loginTest ---- url : " + source);
    }


}
