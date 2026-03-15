package com.cloud.baowang.play.game.lgd.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.ldg.LgdDataResp;
import com.cloud.baowang.play.api.vo.ldg.LgdResp;
import com.cloud.baowang.play.api.vo.ldg.RequestVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.lgd.respon.DetsDetailsResp;
import com.cloud.baowang.play.game.lgd.respon.LGDContentResp;
import com.cloud.baowang.play.game.lgd.vo.FreeGameReqVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.GameNameVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
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
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.LGD)
public class LgdServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final VenueInfoService venueInfoService;
    private final static String SUCCESS_CODE = "00000";

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }


    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    private String getToken(CasinoMemberVO casinoMemberVO) {
        String token = casinoMemberVO.getCasinoPassword();

        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.LGD.getVenueCode(), token);

        RedisUtil.deleteKey(key);

        RedisUtil.setValue(key, casinoMemberVO, 60L, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        String url = venueDetailVO.getApiUrl();
        Map<String, String> prams = new HashMap<>();
        prams.put("METHOD", "LOGIN");
        prams.put("BUSINESS", venueDetailVO.getMerchantNo());
        prams.put("GAMECODE", loginVO.getGameCode());//指定子游戏进入
        prams.put("LANGUAGE", StringUtils.isNotEmpty(LANGUAGE_MAP.get(loginVO.getLanguageCode()))?LANGUAGE_MAP.get(loginVO.getLanguageCode()):"en_US");
        prams.put("TOKEN", getToken(casinoMemberVO));

        log.info("LGD-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("LGD-进入游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.getString("data") == null) {
                log.error("im登录返回异常，{}", response);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(jsonObject.getString("data"))
                    .type(GameLoginTypeEnums.URL.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.LGD.getVenueCode())
                    .build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("LGD-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    @Override
    public ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        if (CollectionUtil.isEmpty(orderRecordMqVOList)) {
            return ResponseVO.success();
        }
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenuePlatformConstants.LGD);

        // 场馆用户关联信息
        List<String> thirdUserName = orderRecordMqVOList.stream().map(OrderRecordMqVO::getCasinoUserName).distinct().toList();
        Map<String, CasinoMemberPO> casinoMemberMap = super.getCasinoMemberByUsers(thirdUserName, orderRecordMqVOList.get(0).getVenuePlatform());
        if (MapUtil.isEmpty(casinoMemberMap)) {
            log.info("{} 未找到用户信息", orderRecordMqVOList.get(0).getVenuePlatform());
            return null;
        }
        // 用户信息
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        if (CollUtil.isEmpty(userMap)) {
            log.info("LGD游戏用户账号不存在{}", userIds);
            return null;
        }

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
        // 场馆游戏配置
        Map<String, GameInfoPO> gameInfoMap = super.getGameInfoByVenueCode(VenuePlatformConstants.LGD);
        if (CollUtil.isEmpty(gameInfoMap)) {
            log.error("LDG游戏列表未配置");
            return null;
        }

        Map<String, String> siteNameMap = getSiteNameMap();
        List<OrderRecordVO> orderRecordVOS = Lists.newArrayList();
        for (OrderRecordMqVO orderRecordMqVO : orderRecordMqVOList) {
            OrderRecordVO orderRecord = BeanUtil.toBean(orderRecordMqVO, OrderRecordVO.class);

            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderRecordMqVO.getCasinoUserName());
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", orderRecordMqVO.getVenueCode(), orderRecordMqVO.getCasinoUserName());
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", orderRecordMqVO.getVenueCode(), casinoMemberVO.getUserAccount());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            orderRecord.setUserAccount(userInfoVO.getUserAccount());
            orderRecord.setUserId(userInfoVO.getUserId());
            orderRecord.setUserName(userInfoVO.getUserName());
            orderRecord.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecord.setAgentId(userInfoVO.getSuperAgentId());
            orderRecord.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecord.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecord.setVenueType(VenueEnum.LGD.getType().getCode());
            orderRecord.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecord.setVipRank(userInfoVO.getVipRank());
            orderRecord.setBetIp(userLoginInfoVO.getIp());
            orderRecord.setSiteCode(userInfoVO.getSiteCode());
            orderRecord.setSiteName(siteNameMap.get(orderRecord.getSiteCode()));
            if (userLoginInfoVO.getLoginTerminal() != null) {
                orderRecord.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
            if (orderRecordMqVO.getWinLossAmount().compareTo(BigDecimal.ZERO) == 0) {
                orderRecord.setWinLossAmount(orderRecord.getBetAmount().negate());
            }
            BigDecimal validBetAmount = computerValidBetAmount(orderRecord.getBetAmount(), orderRecord.getWinLossAmount(), VenueTypeEnum.ELECTRONICS);
            orderRecord.setValidAmount(validBetAmount);
            String gameCode = orderRecord.getGameCode();
            GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
            if (gameInfoPO != null) {
                orderRecord.setGameId(gameInfoPO.getGameId());
                orderRecord.setGameName(gameInfoPO.getGameI18nCode());
            }
            orderRecord.setThirdGameCode(gameCode);
            if (CurrencyEnum.VND.getCode().equalsIgnoreCase(userInfoVO.getMainCurrency())) {
                orderRecord.setBetAmount(BigDecimalUtils.toK(orderRecord.getBetAmount()));
                orderRecord.setValidAmount(BigDecimalUtils.toK(orderRecord.getValidAmount()));
                orderRecord.setWinLossAmount(BigDecimalUtils.toK(orderRecord.getWinLossAmount()));
            }
            orderRecord.setOrderId(OrderUtil.getGameNo());
            orderRecord.setTransactionId(orderRecord.getThirdOrderId());
            orderRecordVOS.add(orderRecord);
        }
        orderRecordProcessService.orderProcess(orderRecordVOS);

        return null;
    }

    @Override
    public ResponseVO<String> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        String venueCode = venueInfoVO.getVenueCode();
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueCode);
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueCode);
        int page = 1;
        int pageSize = 300;
        try {
            while (true) {
                Map<String, String> params = Maps.newHashMap();
                params.put("METHOD", "GETBETDETAIL");
                params.put("BUSINESS", venueInfoVO.getMerchantNo());
                params.put("PAGENUMBER", String.valueOf(page));
                params.put("PAGESIZE", String.valueOf(pageSize));
                String endTime = DateUtils.formatDateByZoneId(venuePullParamVO.getEndTime(), "GMT+8");
                String starTime = DateUtils.formatDateByZoneId(venuePullParamVO.getStartTime(), "GMT+8");
                params.put("START_TIME", starTime);
                params.put("END_TIME", endTime);
                String signature = MD5Util.encryptToMD5(venueInfoVO.getMerchantNo() + "GETBETDETAIL" + starTime + endTime + venueInfoVO.getMerchantKey());
                params.put("SIGNATURE", signature);
                log.info("signature={}", signature);
                log.info("LGD-获取下注记录request apiPath={}, request={}, key={}", venueInfoVO.getApiUrl(), JSON.toJSONString(params), "GETBETDETAIL" + starTime + endTime + venueInfoVO.getMerchantKey());
                String response = HttpClientHandler.post(venueInfoVO.getApiUrl(), params);
                log.info("LGD-获取下注记录response={}", response);
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject == null || !jsonObject.getString("RESPONSECODE").equals("00000")) {
                    log.error("{} 拉取注返回异常，返回：{}, {}", venueCode, response);
                    return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
                }
                LGDContentResp resp = JSONObject.parseObject(response, LGDContentResp.class);
                if (Objects.nonNull(resp) && CollectionUtil.isEmpty(resp.getBetsDetails())) {
                    log.info("Lgd拉取注单为空结束{}", venueCode);
                    break;
                }
                log.info("LGD拉取注单 返回:{},{}", venueCode, resp);
                List<DetsDetailsResp> betsDetails = resp.getBetsDetails();
                int size = betsDetails.size();
                log.info("LGD拉单条数={}", size);
                // 场馆用户关联信息
                List<String> usernames = betsDetails.stream()
                        .map(s -> formatString(s.getPlayerName())).distinct().toList();
                Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueInfoVO.getVenuePlatform());
                if (MapUtil.isEmpty(casinoMemberMap)) {
                    log.info("LGD未找到三方关联信息 玩家列表{},{}", venueCode, usernames);
                    break;
                }
                // 用户信息
                List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
                Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
                if (CollUtil.isEmpty(userMap)) {
                    log.info("{}游戏用户账号不存在{}", venueCode, userIds);
                    break;
                }
                // 用户登录信息
                Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

                List<OrderRecordVO> list = new ArrayList<>();

                Map<String, String> siteNameMap = getSiteNameMap();

                for (DetsDetailsResp orderResponseVO : betsDetails) {
                    CasinoMemberPO casinoMemberVO = casinoMemberMap.get(formatString(orderResponseVO.getPlayerName()));
                    if (casinoMemberVO == null) {
                        log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), formatString(orderResponseVO.getPlayerName()));
                        continue;
                    }
                    UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                    if (userInfoVO == null) {
                        log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
                        continue;
                    }

                    UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                    // 映射原始注单
                    OrderRecordVO recordVO = parseRecords(venueInfoVO, orderResponseVO, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
                    recordVO.setVenueType(VenueEnum.LGD.getType().getCode());
                    list.add(recordVO);
                }
                // 订单处理
                if (CollectionUtil.isNotEmpty(list)) {
                    orderRecordProcessService.orderProcess(list);
                }
                list.clear();
                if (size != pageSize) {
                    break;
                }
                page++;
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("lgd 执行拉单异常", e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, DetsDetailsResp orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(StringUtils.isNotEmpty(orderResponseVO.getBetPrice()) ? new BigDecimal(orderResponseVO.getBetPrice()) : BigDecimal.ZERO);

        try {
            Long betTime = TimeZoneUtils.convertToTimestamp(orderResponseVO.getCreateTime(), "GMT+8", DateUtils.FULL_FORMAT_1);
            //log.info("LGD时间转换betTime={}, afterBetTime={} ",orderResponseVO.getCreateTime(), betTime);
            recordVO.setBetTime(betTime);
            recordVO.setSettleTime(betTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(formatString(orderResponseVO.getPlayerName()));
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(orderResponseVO.getId());
        recordVO.setWinLossAmount(StringUtils.isNotEmpty(orderResponseVO.getPrizeWins()) ? new BigDecimal(orderResponseVO.getPrizeWins()) : BigDecimal.ZERO);
        recordVO.setPayoutAmount(StringUtils.isNotEmpty(orderResponseVO.getBetWins()) ? new BigDecimal(orderResponseVO.getBetWins()) : BigDecimal.ZERO);

        String key= CacheConstants.ERROR_ORDER_NO+ VenuePlatformConstants.LGD+":"+orderResponseVO.getId();
        if (RedisUtil.isKeyExist(key)){
            recordVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
            recordVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
        }else{
            recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
            recordVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
        }

        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());

        BigDecimal validBetAmount = recordVO.getBetAmount();
        recordVO.setValidAmount(validBetAmount);

        String gameCode = orderResponseVO.getGameCode();
        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
        recordVO.setThirdGameCode(gameCode);
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
        }
        return recordVO;
    }


    private Integer getOrderStatus(BigDecimal winLossAmount) {
        if (winLossAmount.compareTo(BigDecimal.ZERO) > 0) {
            return OrderStatusEnum.WIN.getCode();
        } else if (winLossAmount.compareTo(BigDecimal.ZERO) == 0) {
            return OrderStatusEnum.DRAW.getCode();
        } else {
            return OrderStatusEnum.LOSS.getCode();
        }
    }

    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.LGD.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO){
        String url = venueInfoVO.getApiUrl();
        Map<String, String> prams = new HashMap<>();
        prams.put("METHOD", "GETGAMESORTS");
        prams.put("BUSINESS", venueInfoVO.getMerchantNo());
        prams.put("SIGNATURE", MD5Util.MD5Encode(venueInfoVO.getMerchantNo() + prams.get("METHOD") + venueInfoVO.getMerchantKey()));
        List<JSONObject> resultList = Lists.newArrayList();
        log.info("LGD-获取游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("LGD-获取游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (Objects.isNull(jsonObject)) {
                return null;
            }
            if (!SUCCESS_CODE.equals(jsonObject.getString("RESPONSECODE"))) {
                return null;
            }
            JSONObject sortList = jsonObject.getJSONObject("sortList");
            JSONArray gameArr = sortList.getJSONArray("ALL");
            String langCode = CurrReqUtils.getLanguage();

            for (int i = 0; i < gameArr.size(); i++) {
                JSONObject game = gameArr.getJSONObject(i);
                String gameCode=game.getString("gameCode");
                JSONObject i18n = game.getJSONObject("i18n");
                String gameName = null;
                if (LanguageEnum.ZH_CN.getLang().equals(langCode)){
                    gameName = i18n.getString("zh_CN");
                }else{
                    gameName = i18n.getString("en_US");
                }
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", gameName);
                gameJson.put("deskNumber", gameCode);
                resultList.add(gameJson);
            }
        } catch (Exception e) {
            log.error("LGD-获取游戏异常", e);
        }
        return ResponseVO.success(resultList);
    }

    public List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueDetailVO) {
        List<ThirdGameInfoVO> ret = Lists.newArrayList();
        String url = venueDetailVO.getApiUrl();
        Map<String, String> prams = new HashMap<>();
        prams.put("METHOD", "GETGAMESORTS");
        prams.put("BUSINESS", venueDetailVO.getMerchantNo());
        prams.put("SIGNATURE", MD5Util.MD5Encode(venueDetailVO.getMerchantNo() + prams.get("METHOD") + venueDetailVO.getMerchantKey()));

        log.info("LGD-获取游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("LGD-获取游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (Objects.isNull(jsonObject)) {
                return ret;
            }
            if (!SUCCESS_CODE.equals(jsonObject.getString("RESPONSECODE"))) {
                return ret;
            }
            JSONObject sortList = jsonObject.getJSONObject("sortList");
            JSONArray gameArr = sortList.getJSONArray("ALL");
            for (int i = 0; i < gameArr.size(); i++) {
                ThirdGameInfoVO thirdGameInfoVO = new ThirdGameInfoVO();
                JSONObject game = gameArr.getJSONObject(i);
                thirdGameInfoVO.setGameCode(game.getString("gameCode"));
                JSONObject i18n = game.getJSONObject("i18n");
                List<GameNameVO> gameNameVOS = Lists.newArrayList();
                String enUsName = i18n.getString("en_US");
                if (StringUtil.isNotEmpty(enUsName)) {
                    GameNameVO gameNameVO = new GameNameVO();
                    gameNameVO.setGameName(enUsName);
                    gameNameVO.setLang(LanguageEnum.EN_US.getLang());
                    gameNameVOS.add(gameNameVO);
                }
                String zhCnName = i18n.getString("zh_CN");
                if (StringUtil.isNotEmpty(zhCnName)) {
                    GameNameVO gameNameVO = new GameNameVO();
                    gameNameVO.setGameName(zhCnName);
                    gameNameVO.setLang(LanguageEnum.ZH_CN.getLang());
                    gameNameVOS.add(gameNameVO);
                }
                String viVnCnName = i18n.getString("vi_VN");
                if (StringUtil.isNotEmpty(viVnCnName)) {
                    GameNameVO gameNameVO = new GameNameVO();
                    gameNameVO.setGameName(viVnCnName);
                    gameNameVO.setLang(LanguageEnum.VI_VN.getLang());
                    gameNameVOS.add(gameNameVO);
                }
                thirdGameInfoVO.setGameName(gameNameVOS);
                ret.add(thirdGameInfoVO);
            }

        } catch (Exception e) {
            log.error("LGD-获取游戏异常", e);
        }
        return ret;
    }

    @Override
    public ResponseVO<Boolean> freeGame(FreeGameVO freeGameVO, VenueInfoVO venueInfoVO, List<CasinoMemberVO> casinoMembers) {
        String url = venueInfoVO.getApiUrl();
        Map<String, String> prams = new HashMap<>();
        prams.put("METHOD", "FREEGAME");
        prams.put("BUSINESS", venueInfoVO.getMerchantNo());
        List<FreeGameReqVO> freeGameVOS = Lists.newArrayList();
        for (CasinoMemberVO casinoMember : casinoMembers) {
            FreeGameReqVO reqVO = new FreeGameReqVO();
            reqVO.setPlayerName(casinoMember.getVenueUserAccount().toUpperCase());
            reqVO.setGameCode("billiards5x243");
            reqVO.setFreeNums(freeGameVO.getCount());
            freeGameVOS.add(reqVO);
        }
        prams.put("FREEGAMEDATA", JSONObject.toJSONString(freeGameVOS));
        prams.put("SIGNATURE", MD5Util.MD5Encode(venueInfoVO.getMerchantNo() + prams.get("METHOD") + prams.get("FREEGAMEDATA") + venueInfoVO.getMerchantKey()));
        log.info("LGD-派发免费游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        String response = null;
        try {
            response = HttpClientHandler.post(url, prams);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (!SUCCESS_CODE.equals(jsonObject.getString("RESPONSECODE"))) {
                log.error("派发免费游戏失败,响应结果{}", response);
            }
        } catch (Exception e) {
            log.error("派发免费游戏失败,响应结果{}", response);
        }
        return ResponseVO.success(true);
    }

    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        CasinoMemberPO casinoMemberPO = casinoMemberService.getBaseMapper().selectOne(Wrappers.lambdaQuery(CasinoMemberPO.class)
                .eq(CasinoMemberPO::getVenueCode, VenueEnum.LGD.getVenueCode())
                .eq(CasinoMemberPO::getUserId, CurrReqUtils.getOneId())
                .orderByDesc(CasinoMemberPO::getCreatedTime)
                .last(" limit 1 "));
        if (ObjectUtil.isNotEmpty(casinoMemberPO)) {
            String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.LGD.getVenueCode(), casinoMemberPO.getCasinoPassword());
            RedisUtil.deleteKey(key);
        }
        return ResponseVO.success(true);
    }


    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("zh-CN", "zh_CN");
        LANGUAGE_MAP.put("en-US", "en_US");
        LANGUAGE_MAP.put("hi-IN", "en_US");
        LANGUAGE_MAP.put("id-ID", "en_US");
        LANGUAGE_MAP.put("pt-BR", "en_US");
        LANGUAGE_MAP.put("zh-TW", "en_US");
        LANGUAGE_MAP.put("vi-VN", "en_US");
    }


    public String formatString(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        // 分割字符串为数组，按 "_" 切分
        String[] parts = input.split("_");

        if (parts.length == 2) {
            return userAccountPrefix.concat(parts[1]);
        }
        return null;
    }




    private CasinoMemberVO getVerifierToken(String userToken) {
        if (StringUtils.isBlank(userToken)) {
            return null;
        }
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.LGD.getVenueCode(), userToken);
        CasinoMemberVO casinoMemberVO = RedisUtil.getValue(key);

        if (ObjectUtil.isNotEmpty(casinoMemberVO)) {
            return casinoMemberVO;
        }
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(userToken);
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.LGD);
        return casinoMemberService.getCasinoMember(casinoMemberReqVO);
//        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!casinoMemberResp.isOk()) {
//            log.info("lgd oauth 用户信息不存在，venueCode:{},token:{}", VenueCodeConstants.LGD, casinoMemberVO);
//            return null;
//        }
//        CasinoMemberVO memberVO = new CasinoMemberVO();
//        BeanUtil.copyProperties(casinoMemberResp.getData(), memberVO);
//        return memberVO;
    }


    public LgdResp oauth(RequestVO request) {
        String token = request.getToken();
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setCasinoPassword(token);
//        casinoMemberReqVO.setVenueCode(VenueCode.LGD);
//        ResponseVO<CasinoMemberRespVO> casinoMemberResp = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
        LgdResp resp = LgdResp.success();
//        if (!casinoMemberResp.isOk()) {
//            log.info("lgd oauth 用户信息不存在，token:{}", token);
//            return resp.setCODE("10000").setMSG("user not exist");
//        }

        CasinoMemberVO casinoMember = getVerifierToken(token);
        if (casinoMember == null) {
            log.info("lgd oauth 用户信息不存在，token:{}", token);
            return resp.setCODE("0004").setMSG("user not exist");
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        String mainCurrency = userInfoVO.getMainCurrency();
        LgdDataResp lgdDataResp = new LgdDataResp();
        lgdDataResp.setCurrency(mainCurrency.equals(CurrencyEnum.KVND.getCode())
                ? "VN2" : mainCurrency);
        lgdDataResp.setPlayerName(casinoMember.getVenueUserAccount());
        lgdDataResp.setPlayerPrice(balance.toString());
        return resp.setData(lgdDataResp);
    }

    public LgdResp checkBalance(RequestVO request) {
        return oauth(request);
    }

    public LgdResp bet(RequestVO request) {

        String orderNo = request.getBetId();
        BigDecimal betAmount = request.getBetPrice();
        BigDecimal winloseAmount = request.getBetWins();
        //当fcid不为null的时候,则为免费优惠活动
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(request.getFcid())) {
            orderNo = request.getFcid();
            betAmount = BigDecimal.ZERO;
        }
        LgdResp resp = LgdResp.success();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.LGD, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("lgd rollback error get locker error, req:{}", request);
                resp.setCODE("4003").setMSG("try lock error");
                return resp;
            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.LGD);
//            casinoMemberReqVO.setCasinoPassword(request.getToken());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();

            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setCasinoPassword(request.getToken());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.LGD);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                resp.setCODE("0004").setMSG("member not exist");
                return resp;
            }
            if (venueMaintainClosed(VenuePlatformConstants.LGD,casinoMember.getSiteCode())) {
                return LgdResp.err("4004", "venue close");
            }

            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("lgd bet error queryUserInfoByAccount userName[{}] not find.", userId);
                resp.setCODE("0004").setMSG("member not exist");
                return resp;
            }
            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("lgd bet error game lock userName[{}] not find.", userId);
                resp.setCODE("4004").setMSG("game lock");
                return resp;
            }

//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueEnum.LGD.getVenueCode())
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO2 = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO2.getData();
            if (venueMaintainClosed(VenueEnum.LGD.getVenueCode(),userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.LGD.getVenueCode());
                resp.setCODE("4004").setMSG("game lock");
                return resp;
            }


            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin)) {
                log.error("lgd bet error wallet is not exist user account:{}.", userId);
                resp.setCODE("0001").setMSG("member wallet not exist");
                return resp;
            }
            // LGD 新增余额判断 update by xiaozhi 2025-06-15
            if(betAmount.compareTo(userCenterCoin.getCenterTotalAmount()) > 0){
                log.error("lgd bet error wallet is not enough:{}.", userId);
                resp.setCODE("0001").setMSG("member wallet is not enough");
                return resp;
            }
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(request.getBetId());
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinBetRecRespVO)) {
                log.error("lgd  bet error feign error or order not exist feign resp{}.", userCoinBetRecRespVO);
                resp.setCODE("4004").setMSG("betId already exist");
                return resp;
            }
            // 账变
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, orderNo,
                    betAmount, winloseAmount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ResponseVO<Boolean> res = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    yield ResponseVO.fail(ResultCode.PARAM_ERROR);
                }
            };
            if (!res.isOk()) {
                return resp.setCODE("4004").setMSG("wallet change error");
            }

            // 发送注单
            /***OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
             BeanUtil.copyProperties(request, orderRecordMqVO,"id");
             BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
             orderRecordMqVO.setBetAmount(request.getBetPrice());
             orderRecordMqVO.setValidAmount(request.getBetPrice());
             orderRecordMqVO.setBetTime(request.getTimestamp());

             BigDecimal betWin = Objects.nonNull(request.getBetWins())?request.getBetWins():BigDecimal.ZERO;
             BigDecimal betPrice = Objects.nonNull(request.getBetPrice())?request.getBetPrice():BigDecimal.ZERO;
             orderRecordMqVO.setWinLossAmount(betWin.subtract(betPrice));

             orderRecordMqVO.setPayoutAmount(request.getBetWins());
             orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + orderNo);
             orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
             orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
             orderRecordMqVO.setThirdOrderId(orderNo);
             orderRecordMqVO.setSettleTime(request.getTimestamp());
             orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
             orderRecordMqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
             orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
             orderRecordMqVO.setFreeGame(request.getFcid() != null);
             orderRecordMqVO.setVenueCode(VenueCode.LGD);
             KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);***/

            // 获取平台信息
            VenueInfoVO venueInfoVOResponseVO = getVenueInfo(VenuePlatformConstants.LGD, userInfoVO.getMainCurrency());
            String apiKey = venueInfoVOResponseVO.getMerchantKey();
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            LgdDataResp dataResp = new LgdDataResp();
            dataResp.setId(orderNo);
            dataResp.setMethod("bet");
            dataResp.setLb(null);
            dataResp.setPlayerPrice(userCoin.getTotalAmount().toString());
            dataResp.setPlayerName(request.getPlayerName());
            dataResp.setSign(MD5Util.md5(dataResp.getId() + dataResp.getMethod() + dataResp.getPlayerName() + dataResp.getPlayerPrice() + apiKey));

            return resp.setData(dataResp);
        } catch (Exception e) {
            log.error("lgd bet error ", e);
            resp.setCODE("4004");
            return resp;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

    }

    public LgdResp errorBet(RequestVO request) {

        String orderNo = request.getId();
        BigDecimal amount = request.getCreditRemit();
        LgdResp resp = LgdResp.success();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.LGD, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("lgd errorBet error get locker error, req:{}", request);
                return resp.setCODE("4003").setMSG("get locker error");
            }
            LgdDataResp lgdDataResp = new LgdDataResp();
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);

            if (CollectionUtil.isEmpty(userCoinBetRecRespVO)) {
                // 订单不存在 则废弃订单
                lgdDataResp.setProcessStatus("2");
                resp.setData(lgdDataResp);
                return resp;
            }
            orderNo = "1" + orderNo;
            coinRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> userCoinErrBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinErrBetRecRespVO)) {
                // 订单已经处理过
                lgdDataResp.setProcessStatus("2");
                resp.setData(lgdDataResp);
                return resp;
            }
            String userId = userCoinErrBetRecRespVO.get(0).getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);

            if (venueMaintainClosed(VenuePlatformConstants.LGD,userInfoVO.getSiteCode())) {
                return LgdResp.err("4004", "venue close");
            }

            if (Objects.isNull(userInfoVO)) {
                log.error("lgd errorBet error queryUserInfoByAccount userName[{}] not find.", userId);
                lgdDataResp.setProcessStatus("2");
                resp.setCODE("4004").setMSG("member not exist");
                return resp;
            }
            // 账变
            CoinRecordResultVO coinRecordResultVO = updateLgdBalanceResettle(userInfoVO, orderNo, amount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ResponseVO<Boolean> res = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);

            };

            if (!res.isOk()) {
                lgdDataResp.setProcessStatus("2");
                resp.setCODE("4004").setMSG("member not exist").setData(lgdDataResp);
                return resp;
            }

//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueCode(VenueCode.LGD);
//            casinoMemberReqVO.setUserId(userInfoVO.getUserId());
//            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            CasinoMemberRespVO casinoMember = respVO.getData();

            // 发送注单
            /****OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
             BeanUtil.copyProperties(request, orderRecordMqVO,"id");
             BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
             orderRecordMqVO.setBetAmount(request.getBetPrice());
             orderRecordMqVO.setValidAmount(request.getBetPrice());
             orderRecordMqVO.setBetTime(request.getTimestamp());

             BigDecimal betWin = Objects.nonNull(request.getBetWins())?request.getBetWins():BigDecimal.ZERO;
             BigDecimal betPrice = Objects.nonNull(request.getBetPrice())?request.getBetPrice():BigDecimal.ZERO;
             orderRecordMqVO.setWinLossAmount(betWin.subtract(betPrice));

             // orderRecordMqVO.setWinLossAmount(request.getBetWins());
             orderRecordMqVO.setPayoutAmount(request.getBetWins().add(request.getBetPrice()));
             orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + orderNo);
             orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
             orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
             orderRecordMqVO.setThirdOrderId(orderNo);
             orderRecordMqVO.setSettleTime(request.getTimestamp());
             orderRecordMqVO.setOrderClassify(ClassifyEnum.RESETTLED.getCode());
             orderRecordMqVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
             orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
             orderRecordMqVO.setFreeGame(request.getFcid() != null);
             orderRecordMqVO.setVenueCode(VenueCode.LGD);
             KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);***/

            lgdDataResp.setProcessStatus("2");
            resp.setData(lgdDataResp);
            String key= CacheConstants.ERROR_ORDER_NO+ VenuePlatformConstants.LGD+":"+request.getId();
            RedisUtil.setValue(key,request.getId(), 31L, TimeUnit.DAYS);
            return resp;
        } catch (Exception e) {
            log.error("lgd cancel bet error ", e);
            resp.setCODE("4004").setMSG("error bet err");
            return resp;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

    }

    protected CoinRecordResultVO updateLgdBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.LGD);
        userCoinAddVO.setThirdOrderNo(orderNo);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);

    }
}
