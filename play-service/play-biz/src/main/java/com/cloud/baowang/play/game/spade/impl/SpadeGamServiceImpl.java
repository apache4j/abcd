package com.cloud.baowang.play.game.spade.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.spade.enums.*;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;
import com.cloud.baowang.play.api.vo.spade.res.SpadeBalanceRes;
import com.cloud.baowang.play.api.vo.spade.res.SpadeBaseRes;
import com.cloud.baowang.play.api.vo.spade.res.SpadeTransferRes;
import com.cloud.baowang.play.api.vo.spade.utils.SpadeDigestUtils;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.spade.SpadeAcctInfo;
import com.cloud.baowang.play.api.vo.spade.req.AuthorizeReq;
import com.cloud.baowang.play.api.vo.spade.utils.SpadeUtil;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.fastSpin.utils.DigestUtils;
import com.cloud.baowang.play.game.spade.vo.SpadeBetInfo;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.spade.params.SpadePullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.SPADE)
@AllArgsConstructor
public class SpadeGamServiceImpl extends GameBaseService implements GameService {
    private final OrderRecordProcessService orderRecordProcessService;
    private final VenueInfoService venueInfoService;
    public static final String API = "API";
    public static final String DIGEST = "Digest";
    public static final String DATATYPE_JSON = "JSON";
    public static final String DATATYPE = "DataType";
    public static final String CODE = "code";
    public static final String TIME_FORM = "yyyyMMdd'T'HHmmss";


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        ResponseVO<String> responseVO = getGameURL(loginVO, venueInfoVO, casinoMemberVO);
        if (responseVO.isOk()) {
            return ResponseVO.success(GameLoginVo.builder().source(responseVO.getData()).userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.SPADE.getVenueCode()).type(GameLoginTypeEnums.URL.getType()).build());
        } else {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
    }

    //4.1 通过 API 启动游戏
    private ResponseVO<String> getGameURL(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        String optApi = "getAuthorize";
        SpadeCurrencyEnum spadeCurrencyEnum = SpadeCurrencyEnum.fromCode(loginVO.getCurrencyCode());
        SpadeLanguageEnum spadeLanguageEnum = SpadeLanguageEnum.fromCode(loginVO.getLanguageCode());
        SpadeAcctInfo acctInfo = SpadeAcctInfo.builder()
                .acctId(casinoMemberVO.getVenueUserAccount())
                .currency(spadeCurrencyEnum.name())
                .build();
        AuthorizeReq authorizeReq = AuthorizeReq.builder()
                .acctInfo(acctInfo)
                .merchantCode(venueInfoVO.getMerchantNo())
                .token(UUID.randomUUID().toString().replaceAll("-", ""))
                .acctIp(loginVO.getIp())
                .game(loginVO.getGameCode())
                .language(spadeLanguageEnum.name())
                .menuMode(false)
                .build();
        String jsonData = JSON.toJSONString(authorizeReq);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json;charset=utf-8");
        head.put(API, optApi);
        head.put(DATATYPE, DATATYPE_JSON);
        head.put(DIGEST, DigestUtils.digest(jsonData, venueInfoVO.getMerchantKey()));
        String response = HttpClientHandler.post(venueInfoVO.getApiUrl(),head,jsonData);
        log.info("Spade-进入游戏request, apiPath: {} , head:{}, request: {}, response: {}",venueInfoVO.getApiUrl(), JSON.toJSONString(head), jsonData,response);
        JSONObject jsonObject = JSON.parseObject(response);
        String code = jsonObject.getString(CODE);
        if (SpadeResCodeEnum.CODE_0.getCode().equals(code)) {
            return ResponseVO.success(jsonObject.getString("gameUrl"));
        } else {
            log.error("{} getGameURL 返回错误, 请求返回 {}", VenueEnum.SPADE.getVenueName(), response);
        }
        return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
    }

    @Override
    public ResponseVO<SpadePullBetParams> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        AtomicInteger pageInt = new AtomicInteger();
        Map<String, UserInfoVO> userInfoMap = new HashMap<>();
        Map<String, String> userVenueAccountMap = new HashMap<>();
        Map<String, String> siteNameMap = getSiteNameMap();
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());

        int size;
        List<OrderRecordVO> list = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORM);
        do {
            JSONArray betHistory = getBetHistory(venueInfoVO, venuePullParamVO, pageInt.incrementAndGet(), dateTimeFormatter);
            size = betHistory.size();
            if (size == 0) {
                break;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORM);
            betHistory.forEach(object -> {
                JSONObject obj = (JSONObject) object;
                SpadeBetInfo betInfo = obj.to(SpadeBetInfo.class);
                String acctId = betInfo.getAcctId();
                if (StrUtil.isEmpty(acctId) || acctId.split("_").length != 2) {
                    log.info("{} 拉单，三方关联账号不存在, 账号: {} ", venueInfoVO.getVenueCode(), acctId);
                    return;
                }
                OrderRecordVO recordVO = new OrderRecordVO();
                String userId = acctId.split("_")[1];
                UserInfoVO userInfoVO = userInfoMap.get(userId);
                String userVenueAccount = userVenueAccountMap.get(userId);
                if (userInfoVO == null) {
                    userInfoVO = getByUserId(userId);
                    if (userInfoVO == null) {
                        log.info("{} 拉单，三方关联账号不存在, userId: {} ", venueInfoVO.getVenueCode(), userId);
                        return;
                    }
                    userInfoMap.put(userId, userInfoVO);
                    userVenueAccount = venueUserAccountConfig.addVenueUserAccountPrefix(userId);
                    userVenueAccountMap.put(userId, userVenueAccount);
                }
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                recordVO.setBetAmount(betInfo.getBetAmount());
                //NOTE 投注时间按开始时间算. "2017-08-28 02:14:13"
                if (StrUtil.isNotEmpty(betInfo.getTicketTime())) {
                    recordVO.setBetTime(LocalDateTime.parse(betInfo.getTicketTime(), formatter)
                            .atZone(ZoneId.of("Asia/Shanghai"))
                            .toInstant()
                            .toEpochMilli());
                }
                recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                recordVO.setVenueCode(venueInfoVO.getVenueCode());
                recordVO.setVenueType(VenueEnum.SPADE.getType().getCode());
                recordVO.setCasinoUserName(userVenueAccount);
                recordVO.setBetIp(betInfo.getBetIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                recordVO.setThirdOrderId(betInfo.getTicketId());
                recordVO.setTransactionId(betInfo.getReferenceId());
                recordVO.setPayoutAmount(betInfo.getWinLoss().subtract(betInfo.getBetAmount()));
                //NOTE 中奖减投注
                recordVO.setWinLossAmount(betInfo.getWinLoss());
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                SpadeCategoryEnum spadeCategoryEnum = SpadeCategoryEnum.fromName(betInfo.getCategoryId());
                recordVO.setRoomType(spadeCategoryEnum.name());
                recordVO.setRoomTypeName(spadeCategoryEnum.getDesc());

                //NOTE 结算时间 暂时以下注时间一样
                if (recordVO.getBetTime() != null) {
                    recordVO.setSettleTime(recordVO.getBetTime());
                }
                //NOTE 状态
                if (betInfo.getWinLoss() == null ) {
                    recordVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
                    recordVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
                } else {
                    recordVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
                    recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
                }

                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setChangeStatus(0);
                recordVO.setReSettleTime(0L);
                recordVO.setParlayInfo(obj.toString());
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setVipRank(userInfoVO.getVipRank());
                //NOTE 以下注金额
                recordVO.setValidAmount(recordVO.getBetAmount());
                recordVO.setThirdGameCode(betInfo.getGameCode());
                recordVO.setOrderInfo(betInfo.getReferenceId());
                recordVO.setGameCode(betInfo.getGameCode());
                //NOTE  局号
                recordVO.setGameNo(betInfo.getRoundId().toString());
                GameInfoPO gameInfoPO = paramToGameInfo.get(recordVO.getGameCode());
                if (gameInfoPO != null) {
                    recordVO.setGameId(gameInfoPO.getGameId());
                    recordVO.setGameName(gameInfoPO.getGameI18nCode());
                }
                list.add(recordVO);
            });
            List<String> userIdList = list.stream().map(OrderRecordVO::getUserId).toList();
            Map<String, UserLoginInfoVO> loginInfoByUserIds = getLoginInfoByUserIds(userIdList);
            list.forEach(orderRecordVO -> {
                UserLoginInfoVO userLoginInfoVO = loginInfoByUserIds.get(orderRecordVO.getUserId());
                if (userLoginInfoVO != null) {
                    orderRecordVO.setBetIp(userLoginInfoVO.getIp());
                    if (userLoginInfoVO.getLoginTerminal() != null) {
                        orderRecordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
                    }
                }
            });

        } while (pageInt.get() < 5000);

        if (CollUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }
        log.info("Spade 电子, getBetRecordList 拉单结束, 共拉单: {}条", list.size());
        return ResponseVO.success();
    }

    //4.4.1 查询用户下注记录
    JSONArray getBetHistory(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO, int pageIndex, DateTimeFormatter dateTimeFormatter) {
            String optApi = "getBetHistory";
            String url = venueInfoVO.getGameUrl();
            Map<String, Object> paramMap = Maps.newHashMap();
            String beginDayStr = Instant.ofEpochMilli(venuePullParamVO.getStartTime())
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .format(dateTimeFormatter);
            String endDayStr = Instant.ofEpochMilli(venuePullParamVO.getEndTime())
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .format(dateTimeFormatter);
            paramMap.put("beginDate", beginDayStr);
            paramMap.put("endDate", endDayStr);
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("merchantCode", venueInfoVO.getMerchantNo());
            paramMap.put("serialNo", SpadeUtil.generalSerialNo());
            String jsonData = JSON.toJSONString(paramMap);
            Map<String, String> head = Maps.newHashMap();
            head.put("Content-Type", "application/json;charset=utf-8");
            head.put(API, optApi);
            head.put(DATATYPE, DATATYPE_JSON);
            head.put(DIGEST, DigestUtils.digest(jsonData, venueInfoVO.getMerchantKey()));
            String response = HttpClientHandler.post(url,head,jsonData);
            log.info("Spade-getBetHistory, apiPath: {} , head:{}, request: {}, response: {}",venueInfoVO.getApiUrl(), JSON.toJSONString(head), jsonData,response);
            JSONObject jsonObject = JSON.parseObject(response);
            String code = jsonObject.getString(CODE);
            if (SpadeResCodeEnum.CODE_0.getCode().equals(code)) {
                JSONArray list = jsonObject.getJSONArray("list");
                if (CollUtil.isNotEmpty(list)) {
                    return list;
                }
            } else {
                log.error("{} getBetHistory 返回错误, 请求返回 {}", VenueEnum.SPADE.getVenueName(), response);
            }
           return new JSONArray();
    }

    public  List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.SPADE.getVenueCode())
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

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        JSONArray jsonArray = getGameList(venueInfoVO);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", jsonObject.getString("gameName"));
                gameJson.put("deskNumber", jsonObject.get("gameCode"));
                resultList.add(gameJson);
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", VenueEnum.SPADE.getVenueName(), 0);
            return ResponseVO.success(resultList);
        }
        return ResponseVO.success(resultList);
    }

    //4.4.2 查询游戏列表信息
    public JSONArray getGameList(VenueInfoVO venueInfoVO) {
        String optApi = "getGames";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("serialNo", SpadeUtil.generalSerialNo());
        paramMap.put("merchantCode", venueInfoVO.getMerchantNo());
        String jsonData = JSON.toJSONString(paramMap);
        String param=JSON.toJSONString(paramMap);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json;charset=utf-8");
        head.put(API, optApi);
        head.put(DATATYPE, DATATYPE_JSON);
        head.put(DIGEST, DigestUtils.digest(jsonData, venueInfoVO.getMerchantKey()));
        String response = HttpClientHandler.post(venueInfoVO.getApiUrl(),head,jsonData);
        log.info("Spade-进入游戏request, apiPath: {} , head:{}, request: {}, response: {}",venueInfoVO.getApiUrl(), JSON.toJSONString(head), param,response);
        JSONObject jsonObject = JSON.parseObject(response);
        if (SpadeResCodeEnum.CODE_0.getCode().equals(jsonObject.getString(CODE))) {
            return jsonObject.getJSONArray("games");
        } else {
            log.error("{} getGameList 返回错误, 请求返回 {}", VenueEnum.SPADE.getVenueName(), response);
        }
        return new JSONArray();
    }



    /**
     * 4.3.1 查询用户余额 接口名：getBalance
     */
    public Object getBalance(SpadeBalanceReq vo) {
        try {
            String digest = vo.getDigest();
            String acctId = vo.getAcctId();
            if (StrUtil.isEmpty(acctId) || acctId.equals("null") || acctId.split("_").length != 2) {
                log.error("spade balance 但参数不全, acctId:{} ", acctId);
                return SpadeResCodeEnum.CODE_105.toResVO(new SpadeBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setUserId(split[1]);
            casinoMemberReqVO.setVenueCode(VenueEnum.SPADE.getVenueCode());

            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);

            if (casinoMember == null) {
                log.error("spade balance casinoMember不存在, balance, 参数:{} ", vo);
                return SpadeResCodeEnum.C_50100.toResVO(new SpadeBaseRes());
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} spade balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_50100.toResVO();
            }
            if (userGameLock(userInfoVO)) {
                log.error("{} getBalance game lock, user参数:{} ", VenueEnum.SPADE.getVenueCode(), userInfoVO);
                return SpadeResCodeEnum.C_50102.toResVO();
            }
            String venueCode = casinoMember.getVenueCode();
            //NOTE 查询场馆状态
            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} spade balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_5003.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.SPADE,null);


//            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!SpadeDigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return SpadeResCodeEnum.C_50104.toResVO();
            }

            SpadeCurrencyEnum spadeCurrencyEnum = SpadeCurrencyEnum.fromCode(userInfoVO.getMainCurrency());
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            SpadeAcctInfo acctInfo = SpadeAcctInfo.builder().acctId(casinoMember.getVenueUserAccount()).userName(casinoMember.getUserAccount())
                    .balance(userCenterCoin.getTotalAmount()).currency(spadeCurrencyEnum.name()).build();

            return SpadeResCodeEnum.CODE_0.toResVO(SpadeBalanceRes.builder().acctInfo(acctInfo)
                    .merchantCode(venueInfoVO.getMerchantNo()).serialNo(SpadeUtil.generalSerialNo()).build());
        } catch (Exception e) {
            log.error("{} spade balance Exception, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
        }
        return SpadeResCodeEnum.CODE_1.toResVO();
    }
    public Object transfer(SpadeTransferReq vo) {
        try {
            String acctId = vo.getAcctId();
            String digest = vo.getDigest();
            String merchantCode= vo.getMerchantCode();
            if (StrUtil.isEmpty(acctId) || acctId.equals("null") || acctId.split("_").length != 2) {
                log.error("spade balance 但参数不全, acctId:{} ", acctId);
                return SpadeResCodeEnum.CODE_105.toResVO(new SpadeBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setUserId(split[1]);
            casinoMemberReqVO.setVenueCode(VenueEnum.SPADE.getVenueCode());
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                log.error("spade balance casinoMember不存在, balance, 参数:{} ", vo);
                return SpadeResCodeEnum.C_50100.toResVO(new SpadeBaseRes());
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} spade balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_50100.toResVO();
            }
            String venueCode = casinoMember.getVenueCode();
            List<String> venueCodes = Lists.newArrayList(VenueEnum.SPADE.getVenueCode());
            //NOTE 查询场馆状态
            if (!venueCodes.contains(venueCode)){
                log.error("{} spade balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_5003.toResVO();
            }

            //NOTE 查询场馆状态
            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} spade balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_5003.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.SPADE,null);

            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!SpadeDigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return SpadeResCodeEnum.C_50104.toResVO();
            }

            SpadeTransferTypeEnum typeEnum = SpadeTransferTypeEnum.fromCode(vo.getType());
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            SpadeTransferRes transferRes = SpadeTransferRes.builder()
                    .transferId(vo.getTransferId())
                    .acctId(acctId)
                    .balance(userCenterCoin.getTotalAmount())
                    .serialNo(SpadeUtil.generalSerialNo())
                    .build();
            //账变金额为 < 0 不处理
            if (vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("{} transfer, 无账变金额, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
            }
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(vo.getAmount());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVO.setOrderNo(vo.getTicketId());
            userCoinAddVO.setRemark(vo.getReferenceId());
            switch (Objects.requireNonNull(typeEnum)) {
                case PLACE_BET:
                    userCoinAddVO.setOrderNo(vo.getTransferId());
                    userCoinAddVO.setRemark(vo.getSerialNo());
                    userCoinAddVO.setThirdOrderNo(vo.getTransferId());
                    getBetCoinAddVO(userCoinAddVO);
                    if (!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                        log.error("{} SPADE transfer venueInfoVO不存在或不开放, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.C_5003.toResVO();
                    }
                    if (venueGameMaintainClosed(venueInfoVO.getVenueCode(),casinoMember.getSiteCode(),vo.getGameCode())){
                        log.info("{}:游戏未开启", VenueEnum.SPADE.getVenueName());
                        return SpadeResCodeEnum.C_5003.toResVO(transferRes);
                    }
                    if (userGameLock(userInfoVO)) {
                        log.error("{} transfer bet game lock, user参数:{} ", VenueEnum.SPADE.getVenueCode(), userInfoVO);
                        return SpadeResCodeEnum.C_50102.toResVO();
                    }
                    //账变金额为 < 0 不处理
                    if (vo.getAmount().compareTo(userCenterCoin.getTotalAmount()) > 0) {
                        log.error("{} transfer PLACE_BET, 余额不足, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.C_50110.toResVO(transferRes);
                    }
                    break;
                case PAYOUT:
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case BONUS:
                    log.info("SPADE transfer bonus operation: {}", vo);
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case CANCEL_BET:
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    log.info("SPADE transfer cancel bet operation: {}", vo);
                    userCoinAddVO.setOrderNo(vo.getReferenceId());
                    userCoinAddVO.setRemark(vo.getSerialNo());
                    getCancelBetCoinAddVO(userCoinAddVO);
                    //NOTE 确认成功下注
                    UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
                    userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
                    userCoinRecordVo.setUserId(userInfoVO.getUserId());
                    userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
                    userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                    List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
                    if (CollUtil.isEmpty(userCoinRecords)) {
                        log.error("{} transfer 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                    }
                    break;
            }
            if (StrUtil.isEmpty(userCoinAddVO.getOrderNo())) {
                return SpadeResCodeEnum.CODE_105.toResVO(transferRes);
            }
            //NOTE 去重操作
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordVo.setRemark(userCoinAddVO.getRemark());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isNotEmpty(userCoinRecords)) {
                log.error("{} transfer失败, 当前reference已经被处理, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
            }
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
            switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> SpadeResCodeEnum.C_50110.toResVO(transferRes);
                //NOTE 走默认： case FAIL, WALLET_NOT_EXIST -> spadeResCodeEnum.CODE_1.toResVO(transferRes);
                //NOTE 重复操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS -> SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                case SUCCESS -> {
                    transferRes.setBalance(recordResultVO.getCoinAfterBalance());
                    transferRes.setMerchantTxId(recordResultVO.getId());
                    SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                }
                default -> SpadeResCodeEnum.CODE_1.toResVO(transferRes);
            }
            return transferRes;
        } catch (Exception e) {
            log.error("{} transfer Exception, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
        }
        return SpadeResCodeEnum.CODE_1.toResVO();
    }




    /**
     * 投注账变
     */
    private static void getBetCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SPADE);
    }

    /**
     * 派彩账变
     */
    private static void getPayoutCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SPADE);
    }

    /**
     * 取消下注
     */
    private static void getCancelBetCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SPADE);
    }
}
