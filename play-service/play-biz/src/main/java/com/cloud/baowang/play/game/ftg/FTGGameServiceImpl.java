package com.cloud.baowang.play.game.ftg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWTCreator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.base.FTGErrorRes;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.FTGDefaultException;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.ftg.FTGResultCodeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.ftg.FTGCurrencyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.betCoinJoin.BetCoinJoinVO;
import com.cloud.baowang.play.api.vo.ftg.FTGBetReq;
import com.cloud.baowang.play.api.vo.ftg.FTGCancelReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.FTGGameInfoVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.ftg.constant.FTGConstantApi;
import com.cloud.baowang.play.game.ftg.enums.FTGOrderStatusEnum;
import com.cloud.baowang.play.po.BetCoinJoinPO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.BetCoinJoinService;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.ftg.FTGOrderRecordVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.FTG)
@AllArgsConstructor
public class FTGGameServiceImpl extends GameBaseService implements GameService {

    private final SiteApi siteApi;

    private final BetCoinJoinService betCoinJoinService;

    private final OrderRecordProcessService orderRecordProcessService;

    private final GameInfoService gameInfoService;

//    private final PlayServiceApi playServiceApi;

    public void checkGameUser(UserInfoVO userInfoVO) {
        CheckActivityVO checkActivityVO = new CheckActivityVO();
        checkActivityVO.setUserId(userInfoVO.getUserId());
        checkActivityVO.setSiteCode(userInfoVO.getSiteCode());
        checkActivityVO.setVenueCode(VenueEnum.FTG.getVenueCode());
        BigDecimal venueAmount = gameInfoService.checkJoinActivity(checkActivityVO);
        //不允许下注
        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("不允许下注.调用游戏校验失败:{},result:{}", checkActivityVO, venueAmount);
            throw new SBDefaultException(SBResultCode.SYSTEM_ERROR);
        }
    }


    private String getUserAccount(String account) {
        String userAccount = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userAccount)) {
            return null;
        }
        return userAccount;
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + FTGConstantApi.CREATE_USER;

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.byPlatCurrencyCode(casinoMemberVO.getCurrencyCode());

        if (ftgCurrencyEnum == null) {
            log.info("参数异常FTG创建游戏失败");
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        Map<String, String> params = new HashMap<>();
        params.put("client_id", venueInfoVO.getMerchantNo());
        params.put("currency", ftgCurrencyEnum.getCode());
        params.put("username", casinoMemberVO.getVenueUserAccount());
        params.put("invite_code", venueInfoVO.getMerchantKey());

        JSONObject jsonObject = new JSONObject(params);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + createJWT(jsonObject, venueInfoVO.getAesKey()));
        header.put("Accept", "application/json");
        String rsp = HttpClientHandler.post(url, header, params);
        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        JSONObject resJson = JSONObject.parseObject(rsp);
        if (resJson == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        if (resJson.getJSONObject("user") != null) {
            return ResponseVO.success(Boolean.TRUE);
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    private String getTimezone(String timeZone) {
        String time = timeZone.substring(4);
        String type = timeZone.substring(3, 4);
        if (Integer.parseInt(time) < 10) {
            return type + "0" + time + ":00";
        }
        return type + time + ":00";
    }

    private String getToken() {
        String userId = CurrReqUtils.getOneId();

        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.FTG.getVenueCode(), userId);
        RedisUtil.deleteKey(key);

        String token = "ftg_" + userId + UUID.randomUUID();

        RedisUtil.setValue(key, token, 60L, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueInfoVO, String venueUserAccount) {
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.FTG.getVenueCode(), CurrReqUtils.getOneId());
        RedisUtil.deleteKey(key);

        String url = venueInfoVO.getApiUrl() + FTGConstantApi.LOGOUT_GAME;

        Map<String, String> params = new HashMap<>();
        params.put("client_id", venueInfoVO.getMerchantNo());
        params.put("username", venueUserAccount);
        JSONObject jsonObject = new JSONObject(params);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + createJWT(jsonObject, venueInfoVO.getAesKey()));
        header.put("Accept", "application/json");
        String rsp = HttpClientHandler.post(url, header, params);

        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.success(false);
        }

        JSONObject resultJson = JSONObject.parseObject(rsp);
        if (resultJson == null) {
            return ResponseVO.success(false);
        }
        String link = resultJson.getString("result");
        if (StringUtils.isBlank(link)) {
            return ResponseVO.success(false);
        }

        return ResponseVO.success(link.equals("success"));
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + FTGConstantApi.LOGIN_GAME;

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());

        if (ftgCurrencyEnum == null) {
            log.info("参数异常FTG登录游戏失败,币种异常");
            throw new BaowangDefaultException(ResultCode.VENUE_CURRENCY_NOT);
        }
        String ftgLang = FTGLangEnum.conversionLang(CurrReqUtils.getLanguage());

        String timeZone = CurrReqUtils.getTimezone();

        if (StringUtils.isBlank(timeZone)) {
            log.info("参数异常FTG登录游戏失败,未获取到时区");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        Map<String, String> params = new HashMap<>();
        params.put("client_id", venueInfoVO.getMerchantNo());
        params.put("game_id", loginVO.getGameCode());
        params.put("username", casinoMemberVO.getVenueUserAccount());
        params.put("lang", ftgLang);
        params.put("currency", ftgCurrencyEnum.getCode());
        params.put("invite_code", venueInfoVO.getMerchantKey());
        params.put("timezone", getTimezone(timeZone));
        params.put("token", getToken());

        JSONObject jsonObject = new JSONObject(params);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + createJWT(jsonObject, venueInfoVO.getAesKey()));
        header.put("Accept", "application/json");
        String rsp = HttpClientHandler.post(url, header, params);

        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        JSONObject resultJson = JSONObject.parseObject(rsp);
        if (resultJson == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        String link = resultJson.getString("link");
        if (StringUtils.isBlank(link)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        GameLoginVo loginVo = GameLoginVo.builder()
                .source(link)
                .type(GameLoginTypeEnums.URL.getType())
                .userAccount(casinoMemberVO.getVenueUserAccount())
                .venueCode(VenueEnum.FTG.getVenueCode())
                .merchantNo(venueInfoVO.getMerchantNo())
                .build();
        return ResponseVO.success(loginVo);
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        int page = 1;
        int pageSize = 1000;

        Integer gameTypeId = VenueTypeEnum.FISHING.getCode();
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));


        while (true) {
            Map<String, String> requestParameters = new HashMap<>();
            requestParameters.put("begin_at", TimeZoneUtils.convertTimestampToIso8601(venuePullParamVO.getStartTime()));
            requestParameters.put("end_at", TimeZoneUtils.convertTimestampToIso8601(venuePullParamVO.getEndTime()));
            requestParameters.put("wagers_type", "0");//注單狀態： 0：全部狀態 1：已結算狀態 (預設值) 2：已註銷
            requestParameters.put("page", String.valueOf(page));
            requestParameters.put("row_number", String.valueOf(pageSize));
            requestParameters.put("client_id", venueDetailVO.getMerchantNo());
            requestParameters.put("invite_code", venueDetailVO.getMerchantKey());

            String url = venueDetailVO.getApiUrl() + FTGConstantApi.ORDER_RECORD;

            JSONObject jsonObject = new JSONObject(requestParameters);

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + createJWT(jsonObject, venueDetailVO.getAesKey()));
            headers.put("Accept", "application/json");

            String response = HttpClientHandler.get(url, headers, requestParameters);

            if (ObjectUtil.isEmpty(response)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            JSONObject resultJson = JSON.parseObject(response);
            if (resultJson == null) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            List<OrderRecordVO> list = new ArrayList<>();

            String rows = resultJson.getString("rows");

            if (StringUtils.isBlank(rows)) {
                return ResponseVO.success();
            }

            List<FTGOrderRecordVO> rowsList = JSONArray.parseArray(rows, FTGOrderRecordVO.class);
            if (CollectionUtil.isEmpty(rowsList)) {
                return ResponseVO.success();
            }


            // 场馆用户关联信息
            List<String> thirdUserName = rowsList.stream().map(FTGOrderRecordVO::getUsername).distinct().toList();
            List<CasinoMemberPO> casinoMemberPOS = casinoMemberService.list(Wrappers.<CasinoMemberPO>lambdaQuery()
                    .eq(CasinoMemberPO::getVenueCode, venueDetailVO.getVenueCode())
                    .in(CasinoMemberPO::getVenueUserAccount, thirdUserName));
            if (CollectionUtils.isEmpty(casinoMemberPOS)) {
                log.info("{} 拉单异常,casinoMember 没有记录:{}", venueDetailVO.getVenueCode(), thirdUserName);
                break;
            }

            Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberPOS.stream()
                    .collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, e -> e));

            // 用户信息
            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();

            if (CollectionUtils.isEmpty(userIds)) {
                log.info("{} 拉单异常,accountList", venueDetailVO.getVenueCode());
                break;
            }

            userIds = userIds.stream().distinct().toList();

            Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
            // 用户登录信息
            Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);

            List<String> betOrderList = rowsList.stream().map(FTGOrderRecordVO::getId).distinct().toList();

            Map<String, List<BetCoinJoinPO>> betCoinJoinMap = betCoinJoinService.getBetCoinJoinMap(VenueEnum.FTG.getVenueCode(), betOrderList);


            for (FTGOrderRecordVO orderResponseVO : rowsList) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getUsername());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), orderResponseVO.getUsername());
                    continue;
                }
                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                if (userInfoVO == null) {
                    log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
                    continue;
                }

                //读取站点名称
                String siteName = null;
                if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
                    SiteVO siteVO = siteVOMap.get(userInfoVO.getSiteCode());
                    siteName = siteVO.getSiteName();
                }

                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
                Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenuePlatformConstants.FTG);
                // 映射原始注单
                OrderRecordVO recordVO = parseRecords(venueDetailVO, orderResponseVO, userInfoVO, userLoginInfoVO, paramToGameInfo, betCoinJoinMap);
                recordVO.setVenueType(gameTypeId);
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                list.add(recordVO);
            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            list.clear();
            page++;
        }
        return ResponseVO.success();

    }

    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, FTGOrderRecordVO orderResponseVO, UserInfoVO userInfoVO,
                                       UserLoginInfoVO userLoginInfoVO, Map<String, GameInfoPO> paramToGameInfo,
                                       Map<String, List<BetCoinJoinPO>> betCoinJoinMap) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBet_amount());
        recordVO.setBetContent(orderResponseVO.getCategory());
        recordVO.setPlayInfo(orderResponseVO.getCategory());
        recordVO.setBetTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getBet_at()));
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUsername());

        String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

        if (userLoginInfoVO != null) {
            recordVO.setBetIp(betIp);
            recordVO.setDeviceType(deviceType);
        }
        Integer gameId = orderResponseVO.getGame_id();

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(orderResponseVO.getCategory());
//        recordVO.setGameNo(orderResponseVO.getLobby_id());
        recordVO.setGameNo(orderResponseVO.getId());
        recordVO.setThirdGameCode(String.valueOf(gameId));


        List<BetCoinJoinPO> betCoinJoinPOS = betCoinJoinMap.get(orderResponseVO.getId());
        String orderId = null;
        if (CollectionUtil.isNotEmpty(betCoinJoinPOS)) {
            BetCoinJoinPO betCoinJoinPO = betCoinJoinPOS.get(0);
            orderId = betCoinJoinPO.getTransId();
        }
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(orderResponseVO.getId());
        recordVO.setTransactionId(orderId);
        recordVO.setValidAmount(orderResponseVO.getCommissionable());
        recordVO.setWinLossAmount(orderResponseVO.getProfit());
        recordVO.setPayoutAmount(orderResponseVO.getPayoff());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        Integer orderStatus = getLocalOrderStatus(orderResponseVO.getResult());
        recordVO.setOrderStatus(orderStatus);
        if (ObjectUtil.isNotEmpty(orderResponseVO.getPayoff_at()) && !orderResponseVO.getPayoff_at().equals("Invalid date")) {
            recordVO.setSettleTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getPayoff_at()));
            recordVO.setFirstSettleTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getPayoff_at()));
        }

        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
//        recordVO.setChangeStatus(getChangeStatus(orderResponseVO.getOrderStatus(), recordVO.getOrderId()));
//        recordVO.setDeskNo(orderResponseVO.getDeskNo());
//        recordVO.setBootNo(orderResponseVO.getBootNo());
        recordVO.setResultList(orderResponseVO.getResult());
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setOrderInfo(orderResponseVO.getResult());
        recordVO.setPlayType(orderResponseVO.getResult());
        GameInfoPO gameInfoPO = paramToGameInfo.get(String.valueOf(gameId));
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
        }

        return recordVO;
    }


    public Integer getLocalOrderStatus(String orderStatus) {
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put(FTGOrderStatusEnum.N.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
        statusMap.put(FTGOrderStatusEnum.W.getCode(), OrderStatusEnum.SETTLED.getCode());
        statusMap.put(FTGOrderStatusEnum.L.getCode(), OrderStatusEnum.SETTLED.getCode());
        statusMap.put(FTGOrderStatusEnum.C.getCode(), OrderStatusEnum.CANCEL.getCode());
        return statusMap.get(orderStatus);
    }






    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
//        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class).eq(VenueInfoPO::getVenueCode, VenueEnum.FTG.getVenueCode()));
//        VenueInfoVO venueInfoVO = new VenueInfoVO();
//        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);

        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.FTG,null);

        String url = venueInfoVO.getApiUrl() + FTGConstantApi.GAME_LIST;

        Map<String, String> params = new HashMap<>();
        params.put("client_id", venueInfoVO.getMerchantNo());
        params.put("sort_type", "ID");
        JSONObject jsonObject = new JSONObject(params);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + createJWT(jsonObject, venueInfoVO.getAesKey()));
        header.put("Accept", "application/json");
        String rsp = HttpClientHandler.get(url, header, params);

        if (ObjectUtil.isEmpty(rsp)) {
            return Lists.newArrayList();
        }

        JSONObject resJson = JSONObject.parseObject(rsp);
        if (resJson == null) {
            return resultList;
        }

        JSONArray gameArray = resJson.getJSONArray("games");
        if (gameArray == null) {
            return resultList;
        }
        List<FTGGameInfoVO> list = gameArray.toJavaList(FTGGameInfoVO.class);
        for (FTGGameInfoVO item : list) {
            resultList.add(ShDeskInfoVO.builder().deskName(getGameName(item)).deskNumber(item.getId().toString()).build());
        }
        return resultList;

    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {

        String url = venueInfoVO.getApiUrl() + FTGConstantApi.GAME_LIST;

        List<JSONObject> resultList = Lists.newArrayList();


        Map<String, String> params = new HashMap<>();
        params.put("client_id", venueInfoVO.getMerchantNo());
        params.put("sort_type", "ID");
        JSONObject jsonObject = new JSONObject(params);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + createJWT(jsonObject, venueInfoVO.getAesKey()));
        header.put("Accept", "application/json");
        String rsp = HttpClientHandler.get(url, header, params);

        if (ObjectUtil.isEmpty(rsp)) {
            return ResponseVO.success(Lists.newArrayList());
        }

        JSONObject resJson = JSONObject.parseObject(rsp);
        if (resJson == null) {
            return ResponseVO.success(Lists.newArrayList());
        }

        JSONArray gameArray = resJson.getJSONArray("games");
        if (gameArray == null) {
            return ResponseVO.success(resultList);
        }
        List<FTGGameInfoVO> list = gameArray.toJavaList(FTGGameInfoVO.class);
        for (FTGGameInfoVO item : list) {
            JSONObject gameJson = new JSONObject();
            gameJson.put("deskName", getGameName(item));
            gameJson.put("deskNumber", item.getId());
            resultList.add(gameJson);
        }
        return ResponseVO.success(resultList);
    }

    private String getGameName(FTGGameInfoVO item) {
        String langCode = CurrReqUtils.getLanguage();

        if (langCode == null || langCode.equals(LanguageEnum.EN_US.getLang())) {
            return item.getName_en();
        } else if (langCode.equals(LanguageEnum.ZH_CN.getLang())) {
            return item.getName_cn();
        } else if (langCode.equals(LanguageEnum.ZH_TW.getLang())) {
            return item.getName_zh();
        } else if (langCode.equals(LanguageEnum.PT_BR.getLang())) {
            return item.getName_pt();
        } else if (langCode.equals(LanguageEnum.VI_VN.getLang())) {
            return item.getName_vi();
        }
        return null;
    }

    private static String createJWT(JSONObject json, String aesKey) {
        Algorithm signatureAlgorithm = Algorithm.HMAC256(aesKey);
        JWTCreator.Builder builder = JWT.create().withIssuedAt(new Date());
        Set<String> keys = json.keySet();
        for (String key : keys) {
            Object obj = json.get(key);
            if (obj instanceof Integer) {
                builder.withClaim(key, Integer.valueOf(key));
            } else if (obj instanceof String) {
                builder.withClaim(key, json.getString(key));
            }
        }
        return builder.sign(signatureAlgorithm);
    }


    private boolean verifierToken(String userId, String userToken) {
        if (StringUtils.isBlank(userToken)) {
            return false;
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.FTG.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return token.equals(userToken);
    }


    public FTGGetBalanceRes queryBalance(FTGGetBalanceReq balanceReq) {
        //参数失败
        if (!balanceReq.valid()) {
            log.info("{}:参数校验失败", VenueEnum.FTG.getVenueName());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, balanceReq.getRequest_uuid());
        }

        String userId = getUserAccount(balanceReq.getUid());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, balanceReq.getRequest_uuid());
        }

        if (!verifierToken(userId, balanceReq.getToken())) {
            log.info("{}:token 不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.TOKEN_EXPIRED, balanceReq.getRequest_uuid());
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, balanceReq.getRequest_uuid());
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, balanceReq.getRequest_uuid());
        }

//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FTG.getVenueCode(), null);
//        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
//            log.info("场馆未开启不允许下注:{} ", VenueEnum.FTG.getVenueCode());
//            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, balanceReq.getRequest_uuid());
//        }

        if (venueMaintainClosed(VenuePlatformConstants.FTG,userInfoVO.getSiteCode())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.FTG.getVenueCode());
            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, balanceReq.getRequest_uuid());
        }

        String userCurrencyCode = userInfoVO.getMainCurrency();

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.byPlatCurrencyCode(userCurrencyCode);

        if (ftgCurrencyEnum == null || ftgCurrencyEnum.getPlatCurrencyCode() == null) {
            log.info("币种:{} 没有映射成功", VenueEnum.FTG.getVenueCode());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_CURRENCY, balanceReq.getRequest_uuid());
        }


        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (Objects.isNull(userCenterCoin)) {
            return FTGGetBalanceRes.builder()
                    .uid(balanceReq.getUid())
                    .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                    .request_uuid(balanceReq.getRequest_uuid())
                    .balance(BigDecimal.ZERO)
                    .build();
        }

        if (userCenterCoin.getTotalAmount() == null) {
            return FTGGetBalanceRes.builder()
                    .uid(balanceReq.getUid())
                    .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                    .request_uuid(balanceReq.getRequest_uuid())
                    .balance(BigDecimal.ZERO)
                    .build();
        }

        return FTGGetBalanceRes.builder()
                .uid(balanceReq.getUid())
                .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                .request_uuid(balanceReq.getRequest_uuid())
                .balance(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN))
                .build();
    }



    /**
     * 账变
     *
     * @param req  参数
     * @param type true=扣款,false = 加款
     */
    private FTGGetBalanceRes balanceChange(FTGBetReq req, UserInfoVO userInfoVO, WalletEnum.CoinTypeEnum type) {

        String userId = getUserAccount(req.getUid());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }

        //FTG 上下分不娇艳TOKEN,因为如果退出游戏 重新进入游戏  已经产生了新的TOKEN   老的TOKEN进行派彩的时候会不通过
//        if (!verifierToken(userId, req.getToken())) {
//            log.info("{}:token 不存在:{}", VenueEnum.FTG.getVenueName(), userId);
//            throw new FTGDefaultException(FTGResultCodeEnums.TOKEN_EXPIRED, req.getRequest_uuid());
//        }


//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
//            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, req.getRequest_uuid());
//        }


        if (venueMaintainClosed(VenuePlatformConstants.FTG,userInfoVO.getSiteCode())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.FTG.getVenueCode());
            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, req.getRequest_uuid());
        }


        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, req.getRequest_uuid());
        }

        Long count = betCoinJoinService.getBaseMapper().selectCount(Wrappers.lambdaQuery(BetCoinJoinPO.class)
                .eq(BetCoinJoinPO::getVenueCode, VenueEnum.FTG.getVenueCode())
                .eq(BetCoinJoinPO::getOrderId, req.getTransaction_uuid()));
        if (count > 0) {
            log.info("{}:订单已存在:{}", VenueEnum.FTG.getVenueName(), req.getTransaction_uuid());
            throw new FTGDefaultException(FTGResultCodeEnums.TRANSACTION_HAS_FINISHED, req.getRequest_uuid());
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            return FTGGetBalanceRes.builder()
                    .uid(req.getUid())
                    .currency(Integer.valueOf(FTGCurrencyEnum.getCurrencyEnum(req.getCurrency().toString()).getCode()))
                    .request_uuid(req.getRequest_uuid())
                    .balance(BigDecimal.ZERO)
                    .build();
        }
        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额

        String orderId = OrderUtil.getGameNo();
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        BigDecimal tradeAmount = req.getAmount();//账变金额
        userCoinAddVO.setOrderNo(req.getBet_id());
        userCoinAddVO.setThirdOrderNo(req.getTransaction_uuid());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setCoinType(type.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.FTG);
        String balanceType = null;
        String businessCoinType = null;
        String customerCoinType = null;

        BetCoinJoinVO betCoinJoinVO = BetCoinJoinVO.builder().venueCode(VenueEnum.FTG.getVenueCode()).betId(req.getBet_id()).build();
        List<BetCoinJoinPO> betCoinJoinList = betCoinJoinService.getBetCoinJoinList(betCoinJoinVO);
        if (CollectionUtil.isNotEmpty(betCoinJoinList)) {
            BetCoinJoinPO betCoinJoinPO = betCoinJoinList.get(0);
            if (type.getCode().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())) {
                orderId = betCoinJoinPO.getTransId() + CommonConstant.UNDERLINE + betCoinJoinList.size();
            } else {
                orderId = betCoinJoinPO.getTransId();
            }
        }


        //下注
        if (type.getCode().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())) {
            if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}:用户钱包,余额不足:{},用户金额:{},扣款金额:{}", VenueEnum.FTG.getVenueName(), userId, userTotalAmount, tradeAmount);
                throw new FTGDefaultException(FTGResultCodeEnums.NOT_ENOUGH_BALANCE, req.getRequest_uuid());
            }
            balanceType = CoinBalanceTypeEnum.EXPENSES.getCode();
            businessCoinType = WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode();
            customerCoinType = WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode();
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

            BetCoinJoinPO po = BetCoinJoinPO.builder()
                    .transId(orderId)
                    .orderId(req.getTransaction_uuid())
                    .betId(req.getBet_id())
                    .amount(req.getAmount())
                    .userAccount(req.getUid())
                    .venueCode(VenueEnum.FTG.getVenueCode())
                    .build();
            if (!betCoinJoinService.save(po)) {
                log.info("{}:账变异常,插入关联记录异常:{}", VenueEnum.FTG.getVenueName(), po);
                throw new FTGDefaultException(FTGResultCodeEnums.NOT_ENOUGH_BALANCE, req.getRequest_uuid());
            }
        }


        //派彩
        if (type.getCode().equals(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())) {
            balanceType = CoinBalanceTypeEnum.INCOME.getCode();
            businessCoinType = WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode();
            customerCoinType = WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode();
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        }
        userCoinAddVO.setBalanceType(balanceType);
        userCoinAddVO.setBusinessCoinType(businessCoinType);
        userCoinAddVO.setCustomerCoinType(customerCoinType);
        userCoinAddVO.setVenueCode(VenuePlatformConstants.FTG);
        userCoinAddVO.setThirdOrderNo(req.getTransaction_uuid());
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("{}:调用扣费失败,userCoinAddVO:{}", VenueEnum.FTG.getVenueName(), userCoinAddVO);
            throw new FTGDefaultException(FTGResultCodeEnums.UNKNOWN, req.getRequest_uuid());
        }

        return FTGGetBalanceRes.builder()
                .uid(req.getUid())
                .currency(Integer.valueOf(FTGCurrencyEnum.getCurrencyEnum(req.getCurrency().toString()).getCode()))
                .request_uuid(req.getRequest_uuid())
                .balance(recordResultVO.getCoinAfterBalance())
                .build();
    }

    @DistributedLock(name = RedisConstants.FTG_COIN_LOCK, unique = "#req.bet_id", waitTime = 3, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public FTGGetBalanceRes bet(FTGBetReq req) {
        if (!req.valid() || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.FTG.getVenueName(), req);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.getCurrencyEnum(String.valueOf(req.getCurrency()));
        if (ftgCurrencyEnum == null) {
            log.info("{}:币种异常", VenueEnum.FTG.getVenueName());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_CURRENCY, req.getRequest_uuid());
        }

        String platCurrencyCode = ftgCurrencyEnum.getPlatCurrencyCode();
        if (platCurrencyCode == null) {
            log.info("{}:我们平台未开启该币种,币种映射异常", VenueEnum.FTG.getVenueName());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_CURRENCY, req.getRequest_uuid());
        }

        String userId = getUserAccount(req.getUid());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }


        //没有传金额进来直接返回
        if(req.getAmount().compareTo(BigDecimal.ZERO) == 0){
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            return FTGGetBalanceRes.builder()
                    .uid(req.getUid())
                    .currency(Integer.valueOf(FTGCurrencyEnum.getCurrencyEnum(req.getCurrency().toString()).getCode()))
                    .request_uuid(req.getRequest_uuid())
                    .balance(userCenterCoin.getTotalAmount())
                    .build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, req.getRequest_uuid());
        }

        checkGameUser(userInfoVO);


        return balanceChange(req, userInfoVO, WalletEnum.CoinTypeEnum.GAME_BET);
    }

    @DistributedLock(name = RedisConstants.FTG_COIN_LOCK, unique = "#req.bet_id", waitTime = 3, leaseTime = 180)
    public FTGGetBalanceRes cancelBet(FTGCancelReq req) {
        String userId = getUserAccount(req.getUid());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }

//        if (!verifierToken(userId, req.getToken())) {
//            log.info("{}:token 不存在:{}", VenueEnum.FTG.getVenueName(), userId);
//            throw new FTGDefaultException(FTGResultCodeEnums.TOKEN_EXPIRED, req.getRequest_uuid());
//        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, req.getRequest_uuid());
        }

//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FTG.getVenueCode(), null);
        if (venueMaintainClosed(VenuePlatformConstants.FTG,userInfoVO.getSiteCode())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.FTG.getVenueCode());
            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, req.getRequest_uuid());
        }


        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.INVALID_GAME, req.getRequest_uuid());
        }


        BigDecimal balance = BigDecimal.ZERO;
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNotEmpty(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.byPlatCurrencyCode(userInfoVO.getMainCurrency());
        if (ftgCurrencyEnum == null) {
            log.info("{}:用户账号查询失败,用户币种缺失:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }
        BetCoinJoinPO betCoinJoinPO = betCoinJoinService.getBaseMapper().selectOne(Wrappers.lambdaQuery(BetCoinJoinPO.class)
                .eq(BetCoinJoinPO::getOrderId, req.getReference_transaction_uuid())
                .eq(BetCoinJoinPO::getVenueCode, VenueEnum.FTG.getVenueCode())
                .eq(BetCoinJoinPO::getUserAccount, req.getUid()));
        if (ObjectUtil.isEmpty(betCoinJoinPO)) {
            log.info("{}:回滚失败,未找到这笔扣费记录:{}", VenueEnum.FTG.getVenueName(), req.getReference_transaction_uuid());
            throw new FTGDefaultException(FTGResultCodeEnums.TRANSACTION_DOES_NOT_EXIST, req.getRequest_uuid());
        }


        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(betCoinJoinPO.getBetId());
        coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        List<UserCoinRecordVO> list = getUserCoinRecords(coinRecordRequestVO);

        //如果没有查询到扣费记录说明可能没有扣费成功,所以也不需要加款,直接返回
        if (CollectionUtil.isEmpty(list)) {
            return FTGGetBalanceRes.builder()
                    .uid(req.getUid())
                    .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                    .request_uuid(req.getRequest_uuid())
                    .balance(balance)
                    .build();
        }



        //查询是否已经加过款.防止重复加款
        UserCoinRecordRequestVO addCoinRecordRequestVO = new UserCoinRecordRequestVO();
        addCoinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        addCoinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        addCoinRecordRequestVO.setUserId(userInfoVO.getUserId());
        addCoinRecordRequestVO.setOrderNo(betCoinJoinPO.getBetId());
        addCoinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        addCoinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        List<UserCoinRecordVO> addUserCoinRecordList = getUserCoinRecords(addCoinRecordRequestVO);

        //如果已经有加款记录说明这比单已经给他加过款了,所以也不需要再次加款,直接返回三方成功,避免他们重复请求
        if(CollectionUtil.isNotEmpty(addUserCoinRecordList)){
            return FTGGetBalanceRes.builder()
                    .uid(req.getUid())
                    .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                    .request_uuid(req.getRequest_uuid())
                    .balance(balance)
                    .build();
        }



        UserCoinRecordVO coinRecordVO = list.get(0);
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(betCoinJoinPO.getBetId());
        userCoinAddVO.setThirdOrderNo(betCoinJoinPO.getOrderId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(coinRecordVO.getCoinValue());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.FTG);
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("{}:调用扣费失败,userCoinAddVO:{}", VenueEnum.FTG.getVenueName(), userCoinAddVO);
            throw new FTGDefaultException(FTGResultCodeEnums.UNKNOWN, req.getRequest_uuid());
        }

        return FTGGetBalanceRes.builder()
                .uid(req.getUid())
                .currency(Integer.valueOf(ftgCurrencyEnum.getCode()))
                .request_uuid(req.getRequest_uuid())
                .balance(recordResultVO.getCoinAfterBalance())
                .build();
    }


    @DistributedLock(name = RedisConstants.FTG_COIN_LOCK, unique = "#req.bet_id", waitTime = 3, leaseTime = 180)
    public FTGGetBalanceRes payOut(FTGBetReq req) {
        if (!req.valid() || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.FTG.getVenueName(), req);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }

        FTGCurrencyEnum ftgCurrencyEnum = FTGCurrencyEnum.getCurrencyEnum(String.valueOf(req.getCurrency()));
        if (ftgCurrencyEnum == null) {
            log.info("{}:币种异常", VenueEnum.FTG.getVenueName());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_CURRENCY, req.getRequest_uuid());
        }

        String platCurrencyCode = ftgCurrencyEnum.getPlatCurrencyCode();
        if (platCurrencyCode == null) {
            log.info("{}:我们平台未开启该币种,币种映射异常", VenueEnum.FTG.getVenueName());
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_CURRENCY, req.getRequest_uuid());
        }

        String userId = getUserAccount(req.getUid());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, req.getRequest_uuid());
        }



        //没有传金额进来直接返回
        if(req.getAmount().compareTo(BigDecimal.ZERO) == 0){
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            return FTGGetBalanceRes.builder()
                    .uid(req.getUid())
                    .currency(Integer.valueOf(FTGCurrencyEnum.getCurrencyEnum(req.getCurrency().toString()).getCode()))
                    .request_uuid(req.getRequest_uuid())
                    .balance(userCenterCoin.getTotalAmount())
                    .build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.FTG.getVenueName(), userId);
            throw new FTGDefaultException(FTGResultCodeEnums.USER_DISABLE, req.getRequest_uuid());
        }

        return balanceChange(req, userInfoVO, WalletEnum.CoinTypeEnum.GAME_PAYOUT);
    }


}
