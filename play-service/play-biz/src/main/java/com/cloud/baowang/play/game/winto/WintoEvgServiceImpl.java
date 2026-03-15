package com.cloud.baowang.play.game.winto;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBEVGErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.api.vo.winto.enums.WintoErrorEnum;
import com.cloud.baowang.play.api.vo.winto.req.WintoBaseVO;
import com.cloud.baowang.play.api.vo.winto.req.WintoOrderRecordVO;
import com.cloud.baowang.play.api.vo.winto.req.WintoTransferVO;
import com.cloud.baowang.play.api.vo.winto.rsp.WinToEvgRsp;
import com.cloud.baowang.play.api.vo.winto.rsp.WintoRspParams;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.winto.enums.WintoCurrencyEnum;
import com.cloud.baowang.play.game.winto.enums.WintoEVGLangEnum;
import com.cloud.baowang.play.game.winto.utils.AESCBCWINTOUtil;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainRequestVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
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
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.WINTO_EVG)
public class WintoEvgServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;
    private final static Integer SUCCESS_CODE = 200;

    private final DomainInfoApi domainInfoApi;
    private final UserInfoApi userInfoApi;
    private final VenueInfoService venueInfoService;
    private final GameInfoService gameInfoService;
    private final UserCoinRecordApi userCoinRecordApi;




    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    private String getToken(String prefix) {
        String userId = CurrReqUtils.getOneId();
        String token = prefix+userId + UUID.randomUUID();
        RedisUtil.deleteKey(token);

        RedisUtil.setValue(token, userId, 60L, TimeUnit.MINUTES);

        return token;
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        String apiUrl = venueDetailVO.getApiUrl();
        String agent = venueDetailVO.getMerchantNo();
        String aesKey = venueDetailVO.getAesKey();
        String externalUrl = "";
        ResponseVO<Page<DomainVO>> domainPage = domainInfoApi.queryDomainPage(DomainRequestVO.builder().siteCode(casinoMemberVO.getSiteCode()).build());
        if (domainPage.isOk()) {
            List<DomainVO> domainVOS = domainPage.getData().getRecords();
            DomainVO domainVO = domainVOS.stream().filter(domain -> domain.getDomainType() == 2).findAny().orElse(null);
            if (domainVO != null) {
                String URL_PREFIX = "https://";
                externalUrl = URL_PREFIX + domainVO.getDomainAddr();
            }
            Map<String, String> params = new HashMap<>();
            params.put("operatorCode", agent);
            params.put("gameId", loginVO.getGameCode());
            params.put("lang", WintoEVGLangEnum.conversionLang(CurrReqUtils.getLanguage()));
            params.put("userToken", getToken(VenueEnum.WINTO_EVG.getVenueCode()));
            params.put("externalUrl", externalUrl);
            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i);
                sb.append(k).append("=").append(params.get(k));
                if (i != keys.size() - 1) sb.append("&");
            }
            sb.append("&key=").append(aesKey);

            String assembled = sb.toString();
            log.info("WINTO_EVG 登录参数 : " + assembled);

            String url = apiUrl+"/api/v1/game/launchGame";
            String sign = MD5Util.md5(assembled);
            Map<String, String> head = Maps.newHashMap();
            head.put("Content-Type", "text/plain");
            params.put("sign", sign.toUpperCase());

            String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(params));

            if (rsp != null) {
                JSONObject jsonObject = JSONObject.parseObject(rsp);
                if (jsonObject.getInteger("code").equals(SUCCESS_CODE)) {
                    String encryptStr = jsonObject.getJSONObject("data").getString("url");
                    String decryptStr = null;
                    try {
                        decryptStr = AESCBCWINTOUtil.decrypt(encryptStr, aesKey);
                    } catch (Exception e) {
                        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    }
                    String userAccount = loginVO.getUserAccount();
                    String venueCode = venueDetailVO.getVenueCode();
                    GameLoginVo gameLoginVo = GameLoginVo.builder()
                            .source(decryptStr)
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
        return null;
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

        if (StringUtils.isEmpty(agent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String url = apiUrl + "/api/v1/betHistory";
        Long startTime = venuePullParamVO.getStartTime();
        Long endTime = venuePullParamVO.getEndTime();

        int pageNo = 1;
        int pageSize = 500;
        while (true) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("operatorCode", agent);
            dataMap.put("startTime", TimeZoneUtils.formatTimestampToDBDate(startTime));
            dataMap.put("endTime", TimeZoneUtils.formatTimestampToDBDate(endTime));
            dataMap.put("pageNo", pageNo);
            dataMap.put("pageSize", pageSize);
            List<String> keys = new ArrayList<>(dataMap.keySet());
            Collections.sort(keys);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i);
                sb.append(k).append("=").append(dataMap.get(k));
                if (i != keys.size() - 1) sb.append("&");
            }
            sb.append("&key=").append(key);

            String assembled = sb.toString();
            log.info("WINTO_EVG 拉单参数 : " + assembled);

            String sign = MD5Util.md5(assembled);
            Map<String, String> head = Maps.newHashMap();
            head.put("Content-Type", "text/plain");
            dataMap.put("sign", sign.toUpperCase());

            String rsp = HttpClientHandler.post(url, head, JSONObject.toJSONString(dataMap));
            JSONObject root = JSONObject.parseObject(rsp);
            if (root == null || !root.getInteger("code").equals(SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueDetailVO.getVenueCode(), rsp, JSONObject.toJSONString(dataMap), System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("400010-result:{}", rsp);
            JSONObject data = root.getJSONObject("data");
            List<WintoOrderRecordVO> records = data.getJSONArray("list").toJavaList(WintoOrderRecordVO.class);
            if (records.isEmpty()) {
                break;
            }
            handleRemoteOrder(records, venueDetailVO);
            if (records.size() < pageSize) {
                break;
            }
            pageNo++;
        }


        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<WintoOrderRecordVO> orderList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = orderList.stream().map(order -> adaptThirdAccount(order.getUserName())).distinct().toList();


        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.WINTO_EVG.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.WINTO_EVG.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (WintoOrderRecordVO order : orderList) {

            UserInfoVO userInfoVO = userMap.get(adaptThirdAccount(order.getUserName()));
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), order.getUserName());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setVenueType(VenueEnum.WINTO_EVG.getType().getCode());
            list.add(recordVO);
        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, WintoOrderRecordVO order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(order.getBetAmount());

        Long betTime = Long.valueOf(order.getBetTime())*1000L;
//        Long settleTime = Long.valueOf(order.getBetTime);
        ;
        recordVO.setBetTime(betTime);
        recordVO.setSettleTime(betTime);

        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(order.getUserName());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(order.getTransactionId());
//        recordVO.setTransactionId(String.valueOf(order.getHistoryId()));
        recordVO.setOrderInfo(order.getTransactionId());
        BigDecimal betAmount = order.getBetAmount();
        BigDecimal payoutAmount = order.getWinAmount();
        BigDecimal realWin = payoutAmount.subtract(betAmount);
        recordVO.setResultList(getWinLossResult(realWin));
        recordVO.setWinLossAmount(realWin);
        recordVO.setPayoutAmount(payoutAmount);

        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(order));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());
        recordVO.setValidAmount(order.getBetAmount());

        recordVO.setRoomType(String.valueOf(order.getMerchantId()));
        recordVO.setRoomTypeName(order.getMerchantName());

        String gameCode = String.valueOf(order.getGameId());
        recordVO.setThirdGameCode(gameCode);

        Integer orderStatus = getLocalOrderStatus(order.getStatus());
        recordVO.setOrderStatus(orderStatus);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));

        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
            recordVO.setPlayType(gameInfoPO.getGameI18nCode());
        }
        return recordVO;
    }

    public Integer getLocalOrderStatus(int betStatus) {
        //0-待商户确认 1-投注成功 2-投注取消 3-投注失败 4-投注作废
        switch (betStatus) {
            case 0 -> {
                return OrderStatusEnum.NOT_SETTLE.getCode();
            }
            case 1 -> {
                return OrderStatusEnum.SETTLED.getCode();
            }
            case 2 -> {
                return OrderStatusEnum.CANCEL.getCode();
            }
            case 3 -> {
                return OrderStatusEnum.LOTTERY_CANCEL.getCode();
            }
            case 4 -> {
                return OrderStatusEnum.ABERRANT.getCode();
            }
        }

        return -1;
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




    public static void main(String[] args) throws Exception {
        historyTest();

    }


    public static void historyTest() throws Exception {
        String agent = "04XS7M";
        String merchantKey = "no4U5YoMlBkEKYwKMX9tNNXVAIg3eZRn";

        String apiUrl = "https://test-api.okyslot.com/api/v1/betHistory";

        Map<String, Object> params = new HashMap<>();
        params.put("operatorCode", agent);
        params.put("startTime", TimeZoneUtils.formatTimestampToDBDate(1761128100573L));
        params.put("endTime", TimeZoneUtils.formatTimestampToDBDate(1761128280000L));
        params.put("pageNo",1);
        params.put("pageSize",500);
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        System.out.println("WintoEVGServiceImpl.historyTest keys = " + keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            sb.append(k).append("=").append(params.get(k));
            if (i != keys.size() - 1) sb.append("&");
        }
        sb.append("&key=").append(merchantKey);

        String assembled = sb.toString();

        String sign = MD5Util.md5(assembled);
        System.out.println("加密前数据 : "+assembled+ "加密数据: " + sign);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "text/plain");
        params.put("sign", sign.toUpperCase());

//        String rsp = HttpClientHandler.post(apiUrl, head, JSONObject.toJSONString(params));
        String body = "{\"pageNo\":1,\"sign\":\"8D2E6BAE7FA2105CDE4BC51196145FE4\",\"pageSize\":500,\"startTime\":\"2025-10-25 15:32:00\",\"endTime\":\"2025-10-25 15:52:00\",\"operatorCode\":\"04XS7M\"} ";
        String rsp = HttpClientHandler.post(apiUrl, head, body);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        Integer code = jsonObject.getInteger("code");
        if (code == SUCCESS_CODE) {
            String encryptStr = jsonObject.getJSONObject("data").getString("url");

            System.out.println(".loginTest ---- url : " + encryptStr);

        }

    }




    public static void loginTest() throws Exception {
        String agent = "04XS7M";
        String merchantKey = "no4U5YoMlBkEKYwKMX9tNNXVAIg3eZRn";

        String apiUrl = "https://test-api.okyslot.com/api/v1/game/launchGame";

        Map<String, String> params = new HashMap<>();
        params.put("externalUrl", "https://www.winto.com");
        params.put("gameId", "1007");
        params.put("lang", "en");
        params.put("operatorCode", agent);
        params.put("userToken", "ZestV");
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            sb.append(k).append("=").append(params.get(k));
            if (i != keys.size() - 1) sb.append("&");
        }
        sb.append("&key=").append(merchantKey);

        String assembled = sb.toString();

        String sign = MD5Util.md5(assembled);
        System.out.println("加密数据: " + sign);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "text/plain");
        params.put("sign", sign.toUpperCase());

        String rsp = HttpClientHandler.post(apiUrl, head, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        Integer code = jsonObject.getInteger("code");
        if (code == SUCCESS_CODE) {
            String encryptStr = jsonObject.getJSONObject("data").getString("url");
            String decryptStr = AESCBCWINTOUtil.decrypt(encryptStr, "no4U5YoMlBkEKYwKMX9tNNXVAIg3eZRn");
            System.out.println(".loginTest ---- url : " + decryptStr);

        }

    }


    public WinToEvgRsp verifySession(WintoBaseVO req) {
        log.info("winto verifySession "+req);
        UserInfoVO userInfoVO =userCheck(req.getUserToken());
        if (userInfoVO == null) {
            return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
        }
        CasinoMemberReq casinoMember = new CasinoMemberReq();
        casinoMember.setUserId(userInfoVO.getUserId());
        casinoMember.setVenueCode(VenueEnum.WINTO_EVG.getVenueCode());
        CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(casinoMember);
        if (casinoMemberVO == null) {
            return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
        }
        String mainCurrency = userInfoVO.getMainCurrency();
        String venueCurrency = WintoCurrencyEnum.getThirdCodeByCode(mainCurrency) ;
        WintoRspParams result = WintoRspParams.builder()
                .userName(casinoMemberVO.getVenueUserAccount())
                .nickName(casinoMemberVO.getVenueUserAccount())
                .currency(venueCurrency)
                .build();
        return WinToEvgRsp.success(result);

    }

    private UserInfoVO userCheck(String userToken){
        String userId = RedisUtil.getValue(userToken);
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return getByUserId(userId);
    }


    public WinToEvgRsp<WintoRspParams> getBalance(WintoBaseVO req) {
        log.info("winto getBalance "+req);
        UserInfoVO userInfoVO =userCheck(req.getUserToken());
        if (userInfoVO == null) {
            return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
        }
        WinToEvgRsp<WintoRspParams> baseRsp = checkRequestValid(userInfoVO, req.getGameId(), true);
        if (baseRsp != null) {
            return baseRsp;
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            return WinToEvgRsp.failed(WintoErrorEnum.INSUFFICIENT_BALANCE);
        }
        long seconds = Instant.now().getEpochSecond();
        String mainCurrency = userInfoVO.getMainCurrency();
        String venueCurrency = WintoCurrencyEnum.getThirdCodeByCode(mainCurrency) ;
        WintoRspParams rspParams = WintoRspParams.builder()
                .currency(venueCurrency)
                .balance(userCenterCoin.getTotalAmount())
                .updateTime(String.valueOf(seconds))
                .build();
        return WinToEvgRsp.success(rspParams);
    }



    public WinToEvgRsp<WintoRspParams> checkRequestValid(UserInfoVO userInfoVO,String gameId, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("checkRequestValid : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.WINTO_EVG.getVenueName());
            return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
        }

        if (isBetting) {
            String mainCurrency = userInfoVO.getMainCurrency();
            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.WINTO_EVG.getVenueCode());
                return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
            }
            if (venueMaintainClosed( VenueEnum.WINTO_EVG.getVenueCode(),userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.WINTO_EVG.getVenueCode());
                return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
            }

            if (!isGameAvailable(userInfoVO.getSiteCode(),gameId,VenueEnum.WINTO_EVG.getVenueCode(),mainCurrency)){
                log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), gameId);
                return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
            }



        }
        return null;
    }

    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode,String currencyCode) {
        // 判断游戏是否配置
        GameInfoPO gameInfoPO = gameInfoService.getGameInfoByCode(siteCode, gameCode, venueCode);
        if (gameInfoPO == null) {
            log.error("场馆:{} 没有配置游戏，游戏：{}", venueCode, gameCode);
            return false;
        }
        // 判断游戏是否开启
        if (!Objects.equals(gameInfoPO.getStatus(), StatusEnum.OPEN.getCode())) {
            log.error("场馆:{} 游戏关闭，游戏：{}", venueCode, gameCode);
            return false;
        }
        //币种
        List<String> currencyList = Arrays.asList(gameInfoPO.getCurrencyCode().split(CommonConstant.COMMA));
        if (!currencyList.contains(currencyCode)) {
            log.error("场馆:{} 游戏：{} 币种不支持 : {}", venueCode, gameCode, currencyCode);
            return false;
        }
        return true;
    }


    public WinToEvgRsp betTransfer(WintoTransferVO req) {
        log.info("betNSettle : " + req);
        UserInfoVO userInfoVO =userCheck(req.getUserToken());
        if (userInfoVO == null) {
            return WinToEvgRsp.failed(WintoErrorEnum.USER_NOT_EXIST);
        }
        WinToEvgRsp<WintoRspParams> baseRsp = checkRequestValid(userInfoVO, req.getGameId(), true);
        if (baseRsp != null) {
            return baseRsp;
        }
        String thirdCodeByCode = WintoCurrencyEnum.getThirdCodeByCode(req.getCurrency());
        if (! userInfoVO.getMainCurrency().equals(thirdCodeByCode)) {
            return WinToEvgRsp.failed(WintoErrorEnum.ILLEGAL_PARAMETERS);
        }
        //下注额
        BigDecimal betBalance = req.getBetAmount();
        //游戏赢分 <派彩>
        BigDecimal win = req.getWinAmount();

        BigDecimal netWin = req.getTransferAmount();
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (userCoin == null) {
            return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount();
        String orderId = String.valueOf(req.getBetId());
        //bet = 0,奖励
        if (betBalance.compareTo(BigDecimal.ZERO)==0){
            //"bet":0,"win":0,"netWin":0
            if (win.compareTo(BigDecimal.ZERO)==0 || netWin.compareTo(BigDecimal.ZERO)==0){
                String now = String.valueOf(Instant.now().getEpochSecond());
                WintoRspParams rspParams = WintoRspParams.builder().balance(totalAmount).currency(userInfoVO.getMainCurrency()).updateTime(now).build();
                return WinToEvgRsp.success(rspParams);
            }else {
                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransactionId());
                if (settleResult != null ) {
                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())
                            || UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())){
                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
                        String now = String.valueOf(Instant.now().getEpochSecond());
                        WintoRspParams rspParams = WintoRspParams.builder().balance(coinAfterBalance).currency(userInfoVO.getMainCurrency()).updateTime(now).build();
                        return WinToEvgRsp.success(rspParams);
                    }else {
                        log.error(" winto电子-betTransfer : " + req + " 结算数据 : " + settleResult);
                        return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
                    }
                }else {
                    log.error(" winto电子-道具奖励异常 : 请求数据 : " + req);
                    return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
                }
            }

        }
        if (totalAmount.subtract(betBalance).compareTo(BigDecimal.ZERO) < 0 ) {
            return WinToEvgRsp.failed(WintoErrorEnum.INSUFFICIENT_BALANCE);
        }

        //生成 下注/结算账变
        //查询账变
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(userCoinRecords)) {
            //重复投注
            String now = String.valueOf(Instant.now().getEpochSecond());
            WintoRspParams rspParams = WintoRspParams.builder().balance(totalAmount).currency(userInfoVO.getMainCurrency()).updateTime(now).build();
            return WinToEvgRsp.success(rspParams);
        }

        CoinRecordResultVO betResult = this.handleBet(userInfoVO, orderId, betBalance, req.getTransactionId());
        if (betResult != null) {
            UpdateBalanceStatusEnums betStatus = betResult.getResultStatus();
            if (UpdateBalanceStatusEnums.SUCCESS.equals(betStatus)) {
                //下注成功,处理结算
                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransactionId());
                if (settleResult != null ) {
                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())){
                        //都处理成功
                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
                        String now = String.valueOf(Instant.now().getEpochSecond());
                        WintoRspParams rspParams = WintoRspParams.builder().balance(coinAfterBalance).currency(userInfoVO.getMainCurrency()).updateTime(now).build();
                        return WinToEvgRsp.success(rspParams);

                    }else if (UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())) {
                        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
                        BigDecimal coinAfterBalance = userCenterCoin.getTotalAmount();
                        String now = String.valueOf(Instant.now().getEpochSecond());
                        WintoRspParams rspParams = WintoRspParams.builder().balance(coinAfterBalance).currency(userInfoVO.getMainCurrency()).updateTime(now).build();
                        return WinToEvgRsp.success(rspParams);
                    }else {
                        return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
                    }
                } else {
                    //取消下注订单
                    CoinRecordResultVO cancelBetResult = this.handleCancelBet(userInfoVO, orderId, betBalance, req.getTransactionId());
                    if (cancelBetResult == null || !cancelBetResult.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
                        log.error(" winto_evg:下注并结算异常 订单号 : " + orderId);
                    }
                    return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
                }
            } else {
                log.error(" winto_evg:下注并结算失败 订单号 : " + orderId);
                return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
            }
        } else {
            return WinToEvgRsp.failed(WintoErrorEnum.INTERNAL_ERROR);
        }

    }





    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
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
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.WINTO_EVG.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
    }


    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String orderNo, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenueEnum.WINTO_EVG.getVenueCode());
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    protected CoinRecordResultVO handleCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
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
        userCoinAddVO.setVenueCode(VenueEnum.WINTO_EVG.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

}
