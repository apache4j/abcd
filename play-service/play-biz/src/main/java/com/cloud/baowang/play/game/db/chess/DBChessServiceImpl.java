package com.cloud.baowang.play.game.db.chess;

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
import com.cloud.baowang.play.api.vo.db.evg.vo.CommonTradeInfo;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.fishing.FishingBalanceChangeBody;
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
import com.cloud.baowang.play.game.db.evg.enums.PlayTypeEnum;
import com.cloud.baowang.play.game.db.vo.AESDBUtil;
import com.cloud.baowang.play.game.db.vo.DBOrderCommonVO;
import com.cloud.baowang.play.game.db.vo.Md5StringUtils;
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
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_CHESS)
public class DBChessServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final static String SUCCESS_CODE = "1000";


    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private final DBCryptoConfig cryptoConfig;
    private final UserCoinRecordApi userCoinRecordApi;



    private FishingBalanceChangeBody decrypt(String body,String merchantNo) {
        String key = cryptoConfig.getKey(VenueEnum.DBCHESS.getVenueCode(),merchantNo);
        String iv = cryptoConfig.getIV(VenueEnum.DBCHESS.getVenueCode(),merchantNo);
        try {
            String decrypt = AESDBUtil.decrypt(body, key, iv);
            return JSON.parseObject(decrypt, FishingBalanceChangeBody.class);
        } catch (Exception e) {
            log.error(" DB棋牌 数据解密失败 - {}", e.getMessage());
            return null;
        }
    }




    public DBEVGBaseRsp checkRequestValid(UserInfoVO userInfoVO) {

        if (venueMaintainClosed( VenueEnum.DBCHESS.getVenueCode(),userInfoVO.getSiteCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DBCHESS.getVenueCode());
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_TIMEOUT);
        }


        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DBCHESS.getVenueCode());
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
        }

        return null;
    }

    public UserInfoVO userCheck(FishingBalanceChangeBody req) {
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
        FishingBalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryBalance : DB棋牌 - {}", req);

        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (ObjectUtil.isNull(userCenterCoin)) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder().balance(BigDecimal.ZERO.longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        totalAmount  = totalAmount.multiply(new BigDecimal(100));
        CommonTradeInfo rsp = CommonTradeInfo.builder().balance(totalAmount.longValue()).build();
        return DBEVGBaseRsp.success(rsp);
    }

    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        FishingBalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("balanceChange : DB棋牌 - {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }
        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getOrderId());
        if (coinRecordVOS != null && !coinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (userCoin == null) {
                return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
            }
            BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            totalAmount  = totalAmount.multiply(new BigDecimal(100));
            CommonTradeInfo rsp = CommonTradeInfo.builder().tradeType(req.getTradeType()).tradeAmount(req.getTradeAmount()).balance(totalAmount.longValue()).build();
            return DBEVGBaseRsp.success(DBFishingChessErrorEnum.ORDER_SUCCEED,rsp);
        }

        Integer tradeType = req.getTradeType();
        return switch (tradeType) {
            case 2 -> handleBet(req, userInfoVO);
            case 1 -> handleSettle(req, userInfoVO);
            default -> DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        };
    }


    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String reqStr) {
        FishingBalanceChangeBody req = decrypt(reqStr,evgBasicInfo.getAgent());
        if (req == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_PARSE_ERROR);
        }
        log.info("queryOrderStatus : DB棋牌 - {}", req);
        UserInfoVO userInfoVO = userCheck(req);
        if (userInfoVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.PARAM_ERROR);
        }

        List<UserCoinRecordVO> coinRecordVOS = checkExistCoinRecord(userInfoVO, req.getOrderId());
        if (coinRecordVOS == null || coinRecordVOS.isEmpty()) {
            //订单不存在
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.ORDER_NOT_FOUND);
        }

        UserCoinRecordVO betOrder = coinRecordVOS.stream().filter(order -> order.getRemark().equals(req.getOrderId())).findAny().orElse(null);
        if (betOrder == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.ORDER_NOT_FOUND);
        }
        CasinoMemberReq casinoMember = new CasinoMemberReq();
        casinoMember.setUserId(userInfoVO.getUserId());
        casinoMember.setVenueCode(VenueEnum.DBCHESS.getVenueCode());
        CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(casinoMember);
        if (casinoMemberVO == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_MAINTAINED_2);
        }
        CommonTradeInfo rsp = CommonTradeInfo.builder().status(CommonConstant.business_zero)
                .tradeAmount(req.getTradeAmount())
                .orderId(req.getOrderId())
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




    private DBEVGBaseRsp handleSettle(FishingBalanceChangeBody req, UserInfoVO userInfoVO) {
        log.info("DBChess - handleSettle : "+req);
        String tradeId = req.getOrderId();//流水号
        Integer tradeType = req.getTradeType();
        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount()).divide(new BigDecimal(100),2,RoundingMode.DOWN);

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
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

        String remark = getRemark(tradeId);
        CoinRecordResultVO coinRecordResultVO = this.handleSettleCoin(userInfoVO, tradeId, tradeAmount, remark);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            CommonTradeInfo rsp = CommonTradeInfo.builder()
                    .tradeType(tradeType)
                    .tradeAmount(req.getTradeAmount())
                    .balance(balance.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        } else {
            log.info("DB棋牌 settle : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_TIMEOUT);
        }
    }

    private DBEVGBaseRsp handleBet(FishingBalanceChangeBody req, UserInfoVO userInfoVO) {
        DBEVGBaseRsp baseRsp = checkRequestValid(userInfoVO);
        if (baseRsp != null) {
            return baseRsp;
        }
        log.info("DBChess - handleBet : "+req);
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());

        if (userCoin == null) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.USER_NOT_FOUND);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);

        BigDecimal tradeAmount = new BigDecimal(req.getTradeAmount());
        BigDecimal ackAmount=tradeAmount.divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.BALANCE_NOT_ENOUGH);
        }

        Integer tradeType = req.getTradeType();

        if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
            CommonTradeInfo rsp = CommonTradeInfo.builder().tradeType(tradeType).tradeAmount(req.getTradeAmount()).balance(totalAmount.multiply(new BigDecimal(100)).longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        }
        if (totalAmount.subtract(ackAmount).compareTo(BigDecimal.ZERO) < 0) {
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.BALANCE_NOT_ENOUGH);
        }
        String tradeId = req.getOrderId();//流水号
        String remark = getRemark(tradeId);
        CoinRecordResultVO coinRecordResultVO = handleBetCoin(userInfoVO, tradeId, ackAmount, remark);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            balance = balance.multiply(new BigDecimal(100));
            CommonTradeInfo rsp = CommonTradeInfo.builder().tradeType(tradeType).tradeAmount(req.getTradeAmount()).balance(balance.longValue()).build();
            return DBEVGBaseRsp.success(rsp);
        } else {
            log.info("DB棋牌 bet : 失败 : coinRecordResultVO -> " + coinRecordResultVO);
            return DBEVGBaseRsp.failed(DBFishingChessErrorEnum.SYSTEM_TIMEOUT);
        }
    }

    private String getRemark(String tradeId) {
        String remark = VenueEnum.DBCHESS.getVenueName();
        String lTime = tradeId.split(CommonConstant.COLON)[1];
        Long time = Long.valueOf(lTime);
        return remark+"\n"+ TimeZoneUtils.formatTimestampToDBDate(time);
    }

    protected CoinRecordResultVO handleSettleCoin(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
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
        userCoinAddVO.setDescInfo(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DBCHESS.getVenueCode());
        userCoinAddVO.setThirdOrderNo(orderNo);
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
        userCoinAddVO.setVenueCode(VenueEnum.DBCHESS.getVenueCode());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setDescInfo(tradeId);
        userCoinAddVO.setThirdOrderNo(betId);
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
        int randno = generate10DigitInt();
        String sign = buildSign(agent, timestamp, key);

        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String url = apiUrl + "/launchGame" + "?agent=" + agent + "&timestamp=" + timestamp + "&randno=" + randno + "&sign=" + sign;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", casinoMemberVO.getVenueUserAccount());
        dataMap.put("memberName", casinoMemberVO.getVenueUserAccount());
        dataMap.put("memberPwd", casinoMemberVO.getCasinoPassword());
        dataMap.put("deviceType", getDeviceType());
        dataMap.put("memberIp", loginVO.getIp());
//        dataMap.put("gameId", loginVO.getGameCode());
        String json = JSONObject.toJSONString(dataMap);
        log.info("DB_chess三方登录参数 : " + json);

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
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
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
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            String rsp = HttpClientHandler.post(url, null, body);
            JSONObject root = JSONObject.parseObject(rsp);
            if (root == null || !root.getString("code").equals(SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueDetailVO.getVenueCode(), rsp, json, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            log.info("400002-result:{}", rsp);
            JSONObject data = root.getJSONObject("data");

            List<DBOrderCommonVO> records = data.getJSONArray("list").toJavaList(DBOrderCommonVO.class);
            if (records.isEmpty()) {
                return ResponseVO.success();
            }
            try {
                handleRemoteOrder(records, venueDetailVO);
            } catch (Exception e) {
                log.info("400002-fail:",e);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
//            if (records.size() < pageSize) {
//                break;
//            }
            pageNum++;
        }


//        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<DBOrderCommonVO> orderList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = orderList.stream().map(order -> adaptThirdAccount(order.getMmi())).distinct().toList();


        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DBCHESS.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DBCHESS.getVenueCode());

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
            recordVO.setVenueType(VenueEnum.DBCHESS.getType().getCode());
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

        BigDecimal betAmount = BigDecimal.valueOf(order.getTb()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        BigDecimal netWin = BigDecimal.valueOf(order.getMw()).divide(new BigDecimal(100),2, RoundingMode.DOWN);
        recordVO.setBetAmount(betAmount);
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

        BigDecimal validBetAmount =  BigDecimal.valueOf(order.getBc()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        recordVO.setValidAmount(validBetAmount);

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


    private static String buildSign(String agentId, long timestamp, String apiKey) {
        String md5Key = agentId + timestamp + apiKey;
        String source = MD5Util.MD5Encode(md5Key);
        return Md5StringUtils.mix(source);
    }


    public static void main(String[] args) {
//        queryGameList();
        loginTest();


    }

    public static int generate10DigitInt() {
        int min = 1000000000;
        int max = Integer.MAX_VALUE;
        return ThreadLocalRandom.current().nextInt(min, max);
    }


    public static void loginTest() {
        String agent = "wintocnybyqp27f968d6";
        String key = "gm54V3HlKl4OAdrj";
        String iv = "0102030405060708";
        long timestamp = Instant.now().getEpochSecond();
        int randno = generate10DigitInt();
        String sign = buildSign(agent, timestamp, key);

        String url = "https://byuatopenapi.by100.site" + "/launchGameById" + "?agent=" + agent + "&timestamp=" + timestamp + "&randno=" + randno + "&sign=" + sign;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", "Utest_18642916");
        dataMap.put("memberName", "Utest_18642916");
        dataMap.put("memberPwd", "322fc6fae6033e81c7475bcac09cc314");
        dataMap.put("deviceType", 0);
        dataMap.put("memberIp", "103.20.81.116");
        dataMap.put("gameId", 106);
        String json = JSONObject.toJSONString(dataMap);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "text/plain");

        String body = AESDBUtil.encrypt(json, key, iv);

        String rsp = HttpClientHandler.post(url, head, body);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        String source = jsonObject.getString("data");

        System.out.println("DG2ServiceImpl.loginTest ---- url : " + source);
    }


}
