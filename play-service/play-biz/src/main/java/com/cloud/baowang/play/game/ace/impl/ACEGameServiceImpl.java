package com.cloud.baowang.play.game.ace.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.ace.req.*;
import com.cloud.baowang.play.api.vo.ace.res.ACEAuthenticateRes;
import com.cloud.baowang.play.api.vo.ace.res.ACEBaseRes;
import com.cloud.baowang.play.api.vo.ace.res.ACEBetOrResultRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.ace.enums.ACECallErrorEnum;
import com.cloud.baowang.play.game.ace.enums.ACECurrencyEnum;
import com.cloud.baowang.play.game.ace.enums.ACEErrorEnum;
import com.cloud.baowang.play.game.ace.enums.ACEGameLangEnum;
import com.cloud.baowang.play.game.ace.utils.ACESUtil;
import com.cloud.baowang.play.game.ace.vo.ACERecordVO;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.fc.enums.FCErrorCodeEnum;
import com.cloud.baowang.play.game.pp.utils.CsvParserUtil;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.*;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.ACE)
@AllArgsConstructor
public class ACEGameServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;
    private final CasinoMemberService casinoMemberService;
    private final GameInfoService gameInfoService;
    static String CONTENT_TYPE = "application/json;charset=utf-8";

    static String TOKEN_NAME = "token";
    static String DELIMITER = "♂♫‼◄¶";


    private String venueCode() {
        return VenuePlatformConstants.ACE;
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        ResponseVO<Object> responseVO = createplayer(venueInfoVO, casinoMemberVO);
        if (!responseVO.isOk()) {
            return ResponseVO.fail(responseVO.getCode());
        }
        casinoMemberVO.setVenueUserId(responseVO.getData().toString());

        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        ResponseVO<JSONObject> login = Login(venueInfoVO, casinoMemberVO,loginVO);
        //拼接多URL
        //通过游戏编码获取游戏名称
        String[] split = loginVO.getGameCode().split("_");

        ACEGameLangEnum langEnum = ACEGameLangEnum.fromCode(loginVO.getLanguageCode());
        String url = "";
        String[] urlSplit = venueInfoVO.getGameUrl().split("\\|");

        if (login.isOk()){
            url+=urlSplit[urlSplit.length-1];
            url+=":8443/v3/h5games/";
            url+=split[split.length-1];
            url+="/?lang=";
            url+=langEnum.getCode();
            url+="&userName=";
            url+=casinoMemberVO.getVenueUserAccount() + venueInfoVO.getMerchantNo();
            url+="&actk=";
            url+=login.getData().getString("actk");
//            url+="&returnUrl=https://google.com/";
        }
        return ResponseVO.success(GameLoginVo.builder()
                .source(url).userAccount(casinoMemberVO.getVenueUserAccount() + venueInfoVO.getMerchantNo())
                .venueCode(venueCode())
                .type(GameLoginTypeEnums.URL.getType()).build());
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {

        JSONArray array = AccountTransactions(venueInfoVO, venuePullParamVO);

        List<OrderRecordVO> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(array)) {
            Map<String, UserInfoVO> userInfoMap = new HashMap<>();
            Map<String, String> siteNameMap = getSiteNameMap();
            List<GameInfoPO> gameInfoList = gameInfoService.queryGameByVenueCode(venueCode());

            Map<String, GameInfoPO> gameInfoPOS = gameInfoList.stream().collect(Collectors.toMap(po -> po.getAccessParameters().split("_")[0], po -> po));

            // 创建格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            ZoneOffset offset = ZoneOffset.ofHours(0);
            array.forEach(obj -> {

                JSONObject jsonObject = (JSONObject) obj;

                ACERecordVO aceRecordVO = jsonObject.to(ACERecordVO.class);

                if (!"BR".equals(aceRecordVO.getMethodType())){
                    return;
                }

                String venueUserId = aceRecordVO.getPlayerID().toString();
                UserInfoVO userInfoVO = userInfoMap.get(venueUserId);
                if (userInfoVO == null) {
                    CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(CasinoMemberReq.builder().venueUserId(venueUserId).venueCode(venueInfoVO.getVenueCode()).build());
                    if (casinoMember == null) {
                        log.info("{} 三方关联账号不存在, 账号: {} ", venueInfoVO.getVenueCode(), venueUserId);
                        return;
                    }
                    userInfoVO = getByUserId(casinoMember.getUserId());
                    userInfoMap.put(venueUserId, userInfoVO);
                }
                OrderRecordVO recordVO = new OrderRecordVO();
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                recordVO.setBetAmount(aceRecordVO.getBetAmount());
                //NOTE 投注时间为GMT-4时区.
                LocalDateTime localDateBetTime = LocalDateTime.parse(jsonObject.getString("created"), formatter);

                recordVO.setBetTime(localDateBetTime.toInstant(offset).toEpochMilli());
                recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                recordVO.setVenueCode(venueInfoVO.getVenueCode());
                recordVO.setVenueType(VenueEnum.ACE.getType().getCode());
                recordVO.setCasinoUserName(venueUserAccountConfig.addVenueUserAccountPrefix(userInfoVO.getUserId()));
                recordVO.setBetIp(userInfoVO.getLastLoginIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                recordVO.setThirdOrderId(aceRecordVO.getPlaySessionID());
                recordVO.setTransactionId(aceRecordVO.getPlaySessionID());
                recordVO.setPayoutAmount(aceRecordVO.getWinAmount());
                //NOTE 中奖减投注
                recordVO.setWinLossAmount(recordVO.getPayoutAmount().subtract(recordVO.getBetAmount()));
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                //NOTE 结算时间按下注时间算
                LocalDateTime localDateSettleTime = LocalDateTime.parse(jsonObject.getString("updated"), formatter);
                recordVO.setSettleTime(localDateSettleTime.toInstant(offset).toEpochMilli());
                //NOTE 状态
                recordVO.setOrderStatus(getOrderStatus(aceRecordVO.getStatus()));
                recordVO.setOrderClassify(getOrderStatus(aceRecordVO.getStatus()));
                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setChangeStatus(0);
                recordVO.setReSettleTime(0L);
                recordVO.setParlayInfo(obj.toString());
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setVipRank(userInfoVO.getVipRank());
                recordVO.setValidAmount(aceRecordVO.getBetAmount());
                recordVO.setThirdGameCode(aceRecordVO.getGameID());
                recordVO.setOrderInfo(recordVO.getThirdOrderId());
                recordVO.setGameCode(aceRecordVO.getGameID());

                //FCGameTypeEnum typeEnum = FCGameTypeEnum.fromCode(fcRecordVO.getGametype().toString());
                //recordVO.setRoomType(typeEnum.getCode());
                //recordVO.setRoomTypeName(typeEnum.getDesc());

                //NOTE  TODO 局号
                recordVO.setGameNo(aceRecordVO.getReferenceID());
                GameInfoPO gameInfoPO = gameInfoPOS.get(Integer.valueOf(recordVO.getGameCode()).toString());
                if (gameInfoPO != null) {
                    recordVO.setGameCode(gameInfoPO.getAccessParameters());
                    recordVO.setThirdGameCode(gameInfoPO.getAccessParameters());
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
            if (CollUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            log.info("ACE电子 getBetRecordList 拉单结束, 共拉单: {}条", list.size());
        }
        return ResponseVO.success();
    }


    private Integer getOrderStatus(String dataStatus){

        if ("C".equals(dataStatus)){
            return OrderStatusEnum.SETTLED.getCode();
        }
        return OrderStatusEnum.NOT_SETTLE.getCode();
    }


    //NOTE 1. 获取游戏列表
    public List<ShDeskInfoVO> queryGameList() {
        try {

            String venueCode = venueCode();
//            VenueInfoPO po = venueInfoService.getBaseMapper().selectOne(new LambdaQueryWrapper<>(VenueInfoPO.class)
//                    .eq(VenueInfoPO::getVenueCode, venueCode()).orderByAsc(VenueInfoPO::getCurrencyCode)
//                    .last("limit 1"));

            VenueInfoVO po = getVenueInfo(venueCode,null);
            String[] urlSplit = po.getApiUrl().split("\\|");


            String url = urlSplit[urlSplit.length-1] + "/api/Game/GameList";

            String[] split = po.getBetKey().split("\\|");
            String secretKey = split[0];
            String md5Key = split[1];
            String encryptKey = split[2];

            String currTime = DateTimeFormatter.ofPattern(DateUtils.yyyyMMddHHmmss)
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());
            // 1. 构建 QS（原始字符串）
            String QS = "key=" + secretKey + DELIMITER +
                    "time=" + currTime + DELIMITER +
                    "gameType=1,2,3";
            // 2. DES 加密（ECB/PKCS5Padding）+ URL 编码
            String q = URLEncoder.encode(ACESUtil.encrypt(QS, encryptKey), StandardCharsets.UTF_8);
            // 3. 生成签名 s（MD5）
            String s = ACESUtil.md5(QS + md5Key + currTime + secretKey);

            url+="?q=";
            url+=q;
            url+="&s=";
            url+=s;
            url+="&accessToken=";
            url+=po.getAesKey();
            HttpResponse response = HttpRequest.get(url)
                    .header(Header.CONTENT_TYPE, CONTENT_TYPE)
                    .header(TOKEN_NAME, po.getAesKey()).execute();
            if (response.isOk()) {
                if (StrUtil.isNotEmpty(response.body())){
                    JSONArray gamelist = JSON.parseObject(response.body()).getJSONArray("gamelist");
                    if (CollUtil.isNotEmpty(gamelist)){
                        return gamelist.stream().map(o -> ShDeskInfoVO.builder()
                                .deskNumber(((JSONObject) o).getString("gId") + "_" + ((JSONObject) o).getString("gname"))
                                .deskName(((JSONObject) o).getString("gname")).build()).toList();
                    }
                }

            } else {
                log.error("{} gameList 请求错误, url:{} 返回 : {}", venueCode(),url, response);
            }
        } catch (Exception e) {
            log.error("{} gameList 获取游戏列表发生错误.", venueCode(), e);
        }
        return new ArrayList<>();
    }

    //NOTE 2. 创建三方游戏账号
    public ResponseVO<Object> createplayer(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        try {
            ACECurrencyEnum aceCurrencyEnum = ACECurrencyEnum.fromCode(casinoMemberVO.getCurrencyCode());
            if (ACECurrencyEnum.UNKNOWN == aceCurrencyEnum) {
                log.error("{} createplayer 未知币种, 请求对象 CasinoMemberVO: {}", venueCode(), JSON.toJSONString(casinoMemberVO));
                return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("accountID", casinoMemberVO.getVenueUserAccount());
            map.put("nickname", casinoMemberVO.getVenueUserAccount());
            map.put("currency", aceCurrencyEnum.name());
            String[] urlSplit = venueInfoVO.getApiUrl().split("\\|");

            HttpResponse response = HttpRequest.post(urlSplit[0] + "/api/createplayer")
                    .header(Header.CONTENT_TYPE, CONTENT_TYPE)
                    .header(TOKEN_NAME, venueInfoVO.getAesKey())
                    .body(JSON.toJSONString(map)).execute();
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                ACEErrorEnum errorEnum = ACEErrorEnum.fromCode(jsonBody.getString("error"));
                if (ACEErrorEnum.CODE_0 != errorEnum) {
                    log.error("{} createplayer 请求异常, 错误码: {}, 返回内容: {}", venueCode(), errorEnum, jsonBody);
                    return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
                }
                return ResponseVO.success(jsonBody.get("playerID"));
            } else {
                log.error("{} createplayer 请求失败, HTTP状态: {}, 响应内容: {}", venueCode(), response.getStatus(), response.body());
            }
        } catch (Exception e) {
            log.error("{} createplayer 创建三方账号表发生错误!", venueCode(), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }

    //NOTE 3. 获取游戏TOKEN Login
    public ResponseVO<JSONObject> Login(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO,LoginVO loginVO) {
        try {
            ACECurrencyEnum currencyEnum = ACECurrencyEnum.fromCode(loginVO.getCurrencyCode());
            if (ACECurrencyEnum.UNKNOWN == currencyEnum) {
                log.error("{} Login 未知币种, 请求对象 CasinoMemberVO {}", venueCode(), casinoMemberVO);
                return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
            }
            if (StrUtil.isEmpty(venueInfoVO.getBetKey()) || venueInfoVO.getBetKey().split("\\|").length != 3) {
                log.error("{} Login 场馆参数错误, venueInfoVO 参数 {}", venueCode(), venueInfoVO);
                return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
            }
            String[] split = venueInfoVO.getBetKey().split("\\|");
            String secretKey = split[0];
            String md5Key = split[1];
            String encryptKey = split[2];

            String currTime = DateTimeFormatter.ofPattern(DateUtils.yyyyMMddHHmmss)
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());
            // 1. 构建 QS（原始字符串）
            String QS = "key=" + secretKey + DELIMITER +
                    "time=" + currTime + DELIMITER +
                    "userName=" + casinoMemberVO.getVenueUserAccount() + venueInfoVO.getMerchantNo() + DELIMITER +
                    "password=" + casinoMemberVO.getCasinoPassword() + DELIMITER +
                    "currency=" + currencyEnum.name() + DELIMITER +
                    "nickName=" + casinoMemberVO.getVenueUserAccount();
            // 2. DES 加密（ECB/PKCS5Padding）+ URL 编码
            String q = URLEncoder.encode(ACESUtil.encrypt(QS, encryptKey), StandardCharsets.UTF_8);
            // 3. 生成签名 s（MD5）
            String s = ACESUtil.md5(QS + md5Key + currTime + secretKey);
            System.out.println("s: " + s);
            val map = new LinkedHashMap<>();
            map.put("q", q);
            map.put("s", s);
            map.put("accessToken", venueInfoVO.getAesKey());

            String[] urlSplit = venueInfoVO.getGameUrl().split("\\|");
            HttpRequest request = HttpRequest.post( urlSplit[0]+ "/api/Acc/Login")
                    .header(Header.CONTENT_TYPE, CONTENT_TYPE)
                    .header(TOKEN_NAME, venueInfoVO.getAesKey())
                    .body(JSON.toJSONString(map));
            HttpResponse response = request.execute();
            if (response.isOk()) {
                return ResponseVO.success(JSON.parseObject(response.body()));
            } else {
                log.error("{} Login 请求HTTP错误, 返回 {}", venueCode(), response);
            }
        } catch (Exception e) {
            log.error("{} Login 获取游戏列表发生错误!", venueCode(), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }

    //NOTE 4. 获取游戏记录 Data Feeds AccountTransactions
    public JSONArray AccountTransactions(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        try {

            HttpRequest request = HttpRequest.get(venueInfoVO.getBetUrl() + "/api/playsessions")
                    .header(Header.CONTENT_TYPE, "text/plain")
                    .header(TOKEN_NAME, venueInfoVO.getAesKey())
                    .form("timepoint", venuePullParamVO.getStartTime());
            HttpResponse response = request.execute();
            if (response.isOk()) {
                return CsvParserUtil.parseCsv(response.body());
            } else {
                log.error("{} AccountTransactions 请求错误, 返回 {}", venueCode(), response);
            }
        } catch (Exception e) {
            log.error("{} AccountTransactions 获取游戏列表发生错误!", venueCode(), e);
        }
        return new JSONArray();
    }

    //NOTE 5. 三方回调接口
    public Object authenticate(ACEAuthenticateReq vo) {
        String venueCode = venueCode();

        if (!vo.isValid()) {
            log.error("{} authenticate 但参数不全, 参数:{} ", venueCode, vo);
            return ACECallErrorEnum.CODE_100.toResVO();
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode).venueUserAccount(vo.getUserName()).casinoPassword(vo.getPassword()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} authenticate 会员不存在, 参数:{} ", venueCode, vo);
            return ACECallErrorEnum.CODE_2.toResVO();
        }
        if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
            log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_8.toResVO();
        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
        if (venueInfoVO == null) {
            return ACECallErrorEnum.CODE_8.toResVO();
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} authenticate userInfoVO 不存在, 参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_2.toResVO();
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        return ACECallErrorEnum.CODE_0.toResVO(ACEAuthenticateRes.builder()
                .playerID(Long.parseLong(casinoMember.getVenueUserId())).balance(userCenterCoin.getTotalAmount()).build());
    }

    public Object getbalance(ACEBaseReq vo) {

        if (!vo.isValid()) {
            log.error("{} getbalance 但参数不全, 参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_100.toResVO();
        }

        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode()).venueUserId(vo.getPlayerID()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} getbalance 会员不存在, authenticate, 参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_2.toResVO();
        }

        String venueCode = venueCode();
//        //NOTE 查询场馆状态
//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(casinoMember.getSiteCode(), venueCode(), null);
//        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
//            log.error("{} getbalance venueInfoVO 不存在或不开放, 参数:{} ", venueCode(), vo);
//            return ACECallErrorEnum.CODE_8.toResVO();
//        }
//
//        if (!vo.getToken().equals(venueInfoVO.getMerchantKey())) {
//            return ACECallErrorEnum.CODE_8.toResVO();
//        }

        if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
            log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_8.toResVO();
        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
        if (venueInfoVO == null) {
            return ACECallErrorEnum.CODE_8.toResVO();
        }



        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} getbalance userInfoVO 不存在,  参数:{} ", venueCode(), vo);
            return ACECallErrorEnum.CODE_2.toResVO();
        }

        if (userGameLock(userInfoVO)) {
            log.error("{} getbalance user or game lock, user 参数:{} ", venueCode(), userInfoVO);
            return ACECallErrorEnum.CODE_6.toResVO();
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());

        return ACECallErrorEnum.CODE_0.toResVO(ACEBaseRes.builder().balance(userCenterCoin.getTotalAmount()).build());

    }

    public Object bet(ACEBetReq vo) {

        try {
            if (!vo.isValid()) {
                log.error("{} bet 但参数不全, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_100.toResVO();
            }

            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode()).venueUserId(vo.getPlayerID()).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("{} bet 会员不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            String venueCode = venueCode();

            if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
            if (venueInfoVO == null) {
                return ACECallErrorEnum.CODE_8.toResVO();
            }


            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} bet userInfoVO 不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} bet user or game lock, user 参数:{} ", venueCode(), userInfoVO);
                return ACECallErrorEnum.CODE_6.toResVO();
            }

            int gameID = Integer.parseInt(vo.getGameID());

            List<GameInfoPO> siteGameInfoList = gameInfoService.getSiteGameInfoList(userInfoVO.getSiteCode());
            List<GameInfoPO> venueGameList = siteGameInfoList.stream().filter(gameInfoPO -> gameInfoPO.getVenueCode().equals(venueCode())).toList();
            List<GameInfoPO> list = venueGameList.stream().filter(gameInfoPO -> gameInfoPO.getAccessParameters().startsWith(gameID+"_")).toList();

            if (CollUtil.isEmpty(list)|| list.size()!=1 || !CommonConstant.business_one.equals(list.stream().findFirst().orElse(new GameInfoPO()).getStatus())){
                log.info("{} Bet 游戏不存在或未开启, game code: {}", venueCode(), gameID);
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (vo.getBetAmount().abs().compareTo(userCenterCoin.getTotalAmount())>0){
                log.error("{} bet 余额不足, 参数:{} ", venueCode(), vo);
                return FCErrorCodeEnum.CODE_203.toResVO();
            }

            UserCoinAddVO userCoinAddVO = getBetCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> ACECallErrorEnum.CODE_1.toResVO();
                case FAIL, WALLET_NOT_EXIST -> ACECallErrorEnum.CODE_100.toResVO();
                //NOTE 去重操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder()
                        .balance(userCenterCoin.getTotalAmount()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
                default -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().transactionID(UUID.randomUUID().toString().replaceAll("-", "")).balance(recordResultVO.getCoinAfterBalance()).build());
            };
        } catch (Exception e) {
            log.error("{} bet 参数:{} Exception: {}", venueCode(), vo, e.getMessage());
        }
        return ACECallErrorEnum.CODE_100.toResVO();
    }

    public Object betresult(ACEBetresultReq vo) {

        try {
            if (!vo.isValid()) {
                log.error("{} betresult 但参数不全, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_100.toResVO();
            }

            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode()).venueUserId(vo.getPlayerID()).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("{} betresult 会员不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            String venueCode = venueCode();

            if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
            if (venueInfoVO == null) {
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} betresult userInfoVO 不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} betresult user or game lock, user 参数:{} ", venueCode(), userInfoVO);
                return ACECallErrorEnum.CODE_6.toResVO();
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            UserCoinAddVO userCoinAddVO = getBetresultCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> ACECallErrorEnum.CODE_1.toResVO();
                case FAIL, WALLET_NOT_EXIST -> ACECallErrorEnum.CODE_100.toResVO();
                //NOTE 去重操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(userCenterCoin.getTotalAmount()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
                default -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(recordResultVO.getCoinAfterBalance()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
            };
        } catch (Exception e) {
            log.error("{} betresult 参数:{} Exception: {}", venueCode(), vo, e.getMessage());
        }
        return ACECallErrorEnum.CODE_100.toResVO();
    }

    public Object refund(ACERefundReq vo) {
        try {
            if (!vo.isValid()) {
                log.error("{} refund 但参数不全, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_100.toResVO();
            }

            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode()).venueUserId(vo.getPlayerID()).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("{} refund 会员不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            String venueCode = venueCode();

            if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
            if (venueInfoVO == null) {
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} refund userInfoVO 不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} refund user or game lock, user 参数:{} ", venueCode(), userInfoVO);
                return ACECallErrorEnum.CODE_6.toResVO();
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            //NOTE 确认成功下注
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(vo.getPlaySessionID());
            userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isEmpty(userCoinRecords)) {
                log.error("{} refund 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", venueCode(), vo);
                return ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(userCenterCoin.getTotalAmount()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
            }
            UserCoinAddVO userCoinAddVO = getRefundCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> ACECallErrorEnum.CODE_1.toResVO();
                case FAIL, WALLET_NOT_EXIST -> ACECallErrorEnum.CODE_100.toResVO();
                //NOTE 去重操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(userCenterCoin.getTotalAmount()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
                default -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(recordResultVO.getCoinAfterBalance()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
            };
        } catch (Exception e) {
            log.error("{} refund 参数:{} Exception: {}", venueCode(), vo, e.getMessage());
        }
        return ACECallErrorEnum.CODE_100.toResVO();
    }

    public Object jackpotwin(ACEJackpotwinReq vo) {
        try {
            if (!vo.isValid()) {
                log.error("{} jackpotwin 但参数不全, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_100.toResVO();
            }

            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(venueCode()).venueUserId(vo.getPlayerID()).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("{} jackpotwin 会员不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            String venueCode = venueCode();

            if(venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                log.error("{} authenticate venueInfoVO 不存在或不开放, authenticate, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(vo.getToken(),venueCode);
            if (venueInfoVO == null) {
                return ACECallErrorEnum.CODE_8.toResVO();
            }

            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} jackpotwin userInfoVO 不存在, 参数:{} ", venueCode(), vo);
                return ACECallErrorEnum.CODE_2.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} jackpotwin user or game lock, user 参数:{} ", venueCode(), userInfoVO);
                return ACECallErrorEnum.CODE_6.toResVO();
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            //NOTE 确认成功下注
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(vo.getPlaySessionID());
            userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isEmpty(userCoinRecords)) {
                log.error("{} jackpotwin 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", venueCode(), vo);
                return ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(userCenterCoin.getTotalAmount()).build());
            }

            UserCoinAddVO userCoinAddVO = getJackpotwinCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> ACECallErrorEnum.CODE_1.toResVO();
                case FAIL, WALLET_NOT_EXIST -> ACECallErrorEnum.CODE_100.toResVO();
                //NOTE 去重操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(userCenterCoin.getTotalAmount()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
                default  -> ACECallErrorEnum.CODE_0.toResVO(ACEBetOrResultRes.builder().balance(recordResultVO.getCoinAfterBalance()).transactionID(UUID.randomUUID().toString().replaceAll("-", "")).build());
            };
        } catch (Exception e) {
            log.error("{} jackpotwin 参数:{} Exception: {}", venueCode(), vo, e.getMessage());
        }
        return ACECallErrorEnum.CODE_100.toResVO();
    }


    /**
     * 投注账变
     */
    @NotNull
    private static UserCoinAddVO getBetCoinAddVO(ACEBetReq vo, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(vo.getPlaySessionID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getBetAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getReferenceID());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        userCoinAddVO.setThirdOrderNo(vo.getReferenceID());
        return userCoinAddVO;
    }

    /**
     * 派奖账变
     */
    @NotNull
    private static UserCoinAddVO getBetresultCoinAddVO(ACEBetresultReq vo, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(vo.getPlaySessionID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getWinAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getReferenceID());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        userCoinAddVO.setThirdOrderNo(vo.getReferenceID());
        return userCoinAddVO;
    }

    /**
     * Jackpot账变
     */
    @NotNull
    private static UserCoinAddVO getJackpotwinCoinAddVO(ACEJackpotwinReq vo, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(vo.getPlaySessionID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getWinAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getReferenceID());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        userCoinAddVO.setThirdOrderNo(vo.getReferenceID());
        return userCoinAddVO;
    }

    /**
     * 回退账变
     */
    @NotNull
    private static UserCoinAddVO getRefundCoinAddVO(ACERefundReq vo, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(vo.getPlaySessionID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getBetAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getReferenceID());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        userCoinAddVO.setThirdOrderNo(vo.getReferenceID());
        return userCoinAddVO;
    }

}
