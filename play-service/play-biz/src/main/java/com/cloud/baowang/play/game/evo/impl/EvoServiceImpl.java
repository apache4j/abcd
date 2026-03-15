package com.cloud.baowang.play.game.evo.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.evo.EvoGameErrorCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.evo.*;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.evo.EvoCurrencyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.evo.enums.EvoOrderStatusEnum;
import com.cloud.baowang.play.game.evo.response.EvoBetRecordRes;
import com.cloud.baowang.play.game.evo.response.EvoBetRecordResp;
import com.cloud.baowang.play.game.evo.utils.EVOUtils;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
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
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.EVO)
public class EvoServiceImpl extends GameBaseService implements GameService {

    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "al");//Evo不支持
        LANGUAGE_MAP.put(LanguageEnum.AR_SA.getLang(), "ar");
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "b5");//Evo不支持
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "bg");//Evo不支持
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "bp");
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "ca");
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "cn");


        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "cf");//Evo不支持
        LANGUAGE_MAP.put(LanguageEnum.CS_CZ.getLang(), "cs");//
        LANGUAGE_MAP.put(LanguageEnum.DA_DK.getLang(), "da");//
        LANGUAGE_MAP.put(LanguageEnum.DE_DE.getLang(), "de");//
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "dk");//Evo不支持
        LANGUAGE_MAP.put(LanguageEnum.EL_GR.getLang(), "el");//

        LANGUAGE_MAP.put(LanguageEnum.ES_ES.getLang(), "es");//
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "et");//
        LANGUAGE_MAP.put(LanguageEnum.FI_FI.getLang(), "fi");//
        LANGUAGE_MAP.put(LanguageEnum.FR_FR.getLang(), "fr");//
        LANGUAGE_MAP.put(LanguageEnum.HE_IL.getLang(), "he");//

        LANGUAGE_MAP.put(LanguageEnum.HI_IN.getLang(), "hi");//
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "hr");//
        LANGUAGE_MAP.put(LanguageEnum.HU_HU.getLang(), "hu");//
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "hy");//
        LANGUAGE_MAP.put(LanguageEnum.ID_ID.getLang(), "id");//

        LANGUAGE_MAP.put(LanguageEnum.IT_IT.getLang(), "it");//
        LANGUAGE_MAP.put(LanguageEnum.JA_JP.getLang(), "ja");//
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "ka");//
        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "lt");//
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "lv");//


        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "mn");//
        LANGUAGE_MAP.put(LanguageEnum.MS_MY.getLang(), "ms");//
        LANGUAGE_MAP.put(LanguageEnum.NL_NL.getLang(), "nl");//
        LANGUAGE_MAP.put(LanguageEnum.NO_NO.getLang(), "no");//
        LANGUAGE_MAP.put(LanguageEnum.PL_PL.getLang(), "pl");//


        LANGUAGE_MAP.put(LanguageEnum.RO_RO.getLang(), "ro");//


        LANGUAGE_MAP.put("zh-CN", "zh");
        LANGUAGE_MAP.put("en-US", "en");
        LANGUAGE_MAP.put("id-ID", "id");
        LANGUAGE_MAP.put("pt-BR", "pt");
        LANGUAGE_MAP.put(LanguageEnum.KO_KR.getLang(), "ko");


        LANGUAGE_MAP.put(LanguageEnum.RU_RU.getLang(), "ru");
        /*LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "sk");
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "sl");
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "sq");*/

        //LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "sr");//Evo不支持 北印度语-印度

        LANGUAGE_MAP.put(LanguageEnum.SV_SE.getLang(), "sv");
        LANGUAGE_MAP.put(LanguageEnum.TH_TH.getLang(), "th");
        LANGUAGE_MAP.put(LanguageEnum.TR_TR.getLang(), "tr");
        LANGUAGE_MAP.put(LanguageEnum.UK_UA.getLang(), "uk");
        LANGUAGE_MAP.put(LanguageEnum.VI_VN.getLang(), "vi");
        // 缅甸
        LANGUAGE_MAP.put("my-MM", "my");

    }

    @Autowired
    private OrderRecordProcessService orderRecordProcessService;
    @Autowired
    private VenueInfoService venueInfoService;
    @Autowired
    private GameInfoService gameInfoService;

    public static void main(String[] args) {
        String username = "subswin200000001";
        String token = "test123";
        String auth = username + ":" + token;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        System.out.println(encodedAuth);
        // 生成json窜，然后在解析
        EvoApiTokens evoApiTokens = new EvoApiTokens();
        evoApiTokens.setUa2Token("test123");
        evoApiTokens.setGameHistoryApiToken("test123");
        evoApiTokens.setExternalLobbyApiToken("test123");
        evoApiTokens.setRewardGameToken("test123");

        String json = JSON.toJSONString(evoApiTokens);
        System.out.println(json);
        // 解析
        EvoApiTokens evoApiTokens1 = JSON.parseObject(json, EvoApiTokens.class);
        json = "{\"id\":\"185d57b77328fb7a2e3930f8\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"startedAt\":\"2025-08-20T02:11:15.167Z\",\"settledAt\":\"2025-08-20T02:11:54.240Z\",\"status\":\"Resolved\",\"gameType\":\"lightningscalablebj\",\"table\":{\"id\":\"LightningSbj0001\",\"name\":\"Lightning Blackjack DNT\"},\"dealer\":{\"uid\":\"tts0r6m_________\",\"name\":\"ROB_238\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"subswin200000001\",\"playerId\":\"Utest_60058010\",\"screenName\":\"6666dfhjdf\",\"playerGameId\":\"185d57b77328fb7a2e3930f8-tc6nq3dlkiyaamds\",\"sessionId\":\"tc6nq3dlkiyaamdstdct5jdrcruqcvmx1ea2c593\",\"casinoSessionId\":\"674ab5616fd349e6a103f68a08bc2ad2\",\"currency\":\"MYR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":5,\"payout\":0,\"placedOn\":\"2025-08-20T02:11:30.211Z\",\"transactionId\":\"572b86e0-a6ec-4c38-8164-98cde9517621\"},{\"code\":\"SBJ_Main\",\"stake\":5,\"payout\":10,\"placedOn\":\"2025-08-20T02:11:30.211Z\",\"transactionId\":\"572b86e0-a6ec-4c38-8164-98cde9517621\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"macOS\",\"device\":\"Desktop\",\"currencyRateVersion\":\"tdcmo2takcgqaaac\",\"status\":\"Resolved\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Stand\",\"recordedAt\":\"2025-08-20T02:11:50.482Z\"}],\"scoreType\":\"Win\",\"score\":13,\"outcome\":\"Win\",\"cards\":[\"3D\",\"QH\"],\"position\":\"Main\"}},\"acquiredMultiplier\":2}],\"result\":{\"dealtToPlayer\":[\"3D\",\"QH\"],\"dealerHand\":{\"score\":23,\"cards\":[\"3H\",\"JD\",\"JC\"]},\"wonSideBets\":[],\"lightningPayTable\":{\"id\":4,\"value\":{\"19\":5,\"21\":10,\"17\":2,\"20\":6,\"18\":4,\"BJ\":15}}},\"wager\":2.033538,\"payout\":2.033538}\n";
        EvoBetRecordResp dto = JSON.parseObject(json, EvoBetRecordResp.class);
        System.out.println(dto);
    }


    /**
     * 将多个 JSON 对象字符串拆分为单独的 JSON 字符串列表
     * 适用于 response 中 JSON 对象直接拼接的情况，如：
     * {}{}{} → 拆分为 ["{}", "{}", "{}"]
     *
     * @param response 原始 JSON 字符串
     * @return 拆分后的 JSON 字符串列表，如果 response 为 null、空或没有 {}，返回空集合
     */
    public static List<String> splitJsonObjects(String response) {
        List<String> result = new ArrayList<>();

        // 1. 空值或无 JSON 对象处理
        if (response == null || response.trim().isEmpty() || !response.contains("{")) {
            return result; // 返回空集合
        }

        // 2. 使用正则按 "}{" 分割字符串
        // 注意 (?=\\s*\\{) 是正向前瞻，保证保留 { 的开头
        String[] parts = response.split("}(?=\\s*\\{)");

        // 3. 如果只有一个 JSON 对象，直接返回原字符串
        if (parts.length == 1) {
            result.add(response.trim());
            return result;
        }

        // 4. 遍历分割后的部分，补全缺失的 { 或 }
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            if (i == 0) {
                // 第一个元素：补齐尾部的 }
                if (!part.endsWith("}")) {
                    part = part + "}";
                }
            } else if (i == parts.length - 1) {
                // 最后一个元素：补齐开头的 {
                if (!part.startsWith("{")) {
                    part = "{" + part;
                }
            } else {
                // 中间元素：同时补齐开头的 { 和尾部的 }
                if (!part.startsWith("{")) {
                    part = "{" + part;
                }
                if (!part.endsWith("}")) {
                    part = part + "}";
                }
            }

            // 5. 添加到结果列表
            result.add(part);
        }

        return result;
    }

    /**
     * 生成 Basic Authentication 认证头字符串
     * Generates a Basic Authentication header value for HTTP requests.
     */
    private String generateAuth(String username, String token) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (token == null) {
            token = "";
        }

        // 拼接用户名和令牌，中间用冒号分隔
        String auth = username + ":" + token;

        // 将拼接字符串使用 UTF-8 编码后进行 Base64 编码
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // 返回标准的 Basic Auth 认证头格式，注意 Basic 后有空格
        return "Basic " + encodedAuth;
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    /**
     * 检查指定场馆的游戏是否可用。
     *
     * @param siteCode  站点代码
     * @param gameCode  游戏代码
     * @param venueCode 场馆代码
     * @return 如果游戏存在且状态为开启，返回 {@code true}，否则返回 {@code false}
     */
    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode) {
        // 判断游戏是否配置
        GameInfoPO gameInfo = gameInfoService.getGameInfoByCode(siteCode, gameCode, venueCode);
        if (gameInfo == null) {
            log.error("场馆:{} 没有配置游戏，游戏：{}", venueCode, gameCode);
            return false;
        }
        // 判断游戏是否开启
        if (!Objects.equals(gameInfo.getStatus(), StatusEnum.OPEN.getCode())) {
            log.error("场馆:{} 游戏关闭，游戏：{}", venueCode, gameCode);
            return false;
        }
        return true;
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        // 校验游戏
        if (StringUtils.isNotBlank(loginVO.getGameCode())) {
            if (getGameInfoByCode(loginVO.getSiteCode(), loginVO.getGameCode(), loginVO.getVenueCode()) == null) {
                throw new BaowangDefaultException(ResultCode.VENUE_CHOOSE_ERROR);
            }
        }

        EvoApiTokens evoApiTokens = JSON.parseObject(venueDetailVO.getMerchantKey(), EvoApiTokens.class);
        String url = venueDetailVO.getGameUrl() + "/ua/v1/" + venueDetailVO.getMerchantNo() + "/" + evoApiTokens.getExternalLobbyApiToken();
        Map<String, Object> params = new HashMap<>();
        //
        // 必填 uuid
        params.put("uuid", UUID.randomUUID().toString());

        // player 必填
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("id", casinoMemberVO.getVenueUserAccount());
        playerMap.put("update", true);
        playerMap.put("language", LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(), "en"));
        EvoCurrencyEnum evoCurrencyEnum = EvoCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());
        if (evoCurrencyEnum == null) {
            return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
        }
        playerMap.put("currency", evoCurrencyEnum.getCode());

        // player.session 必填
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("id", UUID.randomUUID().toString().replace("-", ""));
        sessionMap.put("ip", loginVO.getIp());
        playerMap.put("session", sessionMap);

        params.put("player", playerMap);

        // config 必填（最基础）
        Map<String, Object> configMap = new HashMap<>();
        Map<String, Object> game = new HashMap<>();
        Map<String, Object> table = new HashMap<>();
        table.put("id", loginVO.getGameCode());
        game.put("table", table);
        configMap.put("game", game); // 如果不指定表或类别，仍需传空 Map

        params.put("config", configMap);

        // Evo接口专用必填参数

        log.info("Evo-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(params));
        try {
            String response = HttpClientHandler.post(url, JSON.toJSONString(params));
            log.info("Evo-进入游戏response={}", response);
            // 解析 JSON 字符串为 JSONObject
            JSONObject obj = JSON.parseObject(response);
            // 获取 entryEmbedded 字段
            String entryEmbedded = obj.getString("entryEmbedded");
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(venueDetailVO.getGameUrl() + entryEmbedded).type(GameLoginTypeEnums.URL.getType()).userAccount(loginVO.getUserAccount()).venueCode(VenueEnum.EVO.getVenueCode()).build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("Evo-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @Override
    public String genVenueUserPassword() {
        return null;
    }

    @Override
    public ResponseVO<String> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        // 对应的时间
        String version = venuePullParamVO.getVersionKey();
        Map<String, String> params = Maps.newHashMap();
        //String url = venueInfoVO.getApiUrl().concat("/api/gamehistory/v1/casino/games/stream" + "?startDate=" + version);
        EvoApiTokens evoApiTokens = JSON.parseObject(venueInfoVO.getMerchantKey(), EvoApiTokens.class);
        String auth = generateAuth(venueInfoVO.getMerchantNo(), evoApiTokens.getGameHistoryApiToken());
        List<EvoBetRecordResp> data = Lists.newArrayList();
        try {
            do {
                String url = venueInfoVO.getApiUrl().concat("/api/gamehistory/v1/casino/games/stream" + "?startDate=" + version);
                log.info("Evo-拉单request apiPath={}, request={}", url, JSON.toJSONString(params));
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", auth);
                String response = HttpClientHandler.get(url, headers, params);
                log.debug("Evo-拉单response apiPath={}, response={}", url, response);
                // 解析成 List<EvoGameRecordDTO>
                // 先解析两个json字符串，然后在解析成对象
                // 1. 按 "}{" 分割


                List<String> rawJsonList = splitJsonObjects(response);
                if (rawJsonList.isEmpty()) {
                    // 如果没有数据， 默认是当天的结束时间。
                    // 且是当天，则设置为当天时间，如果不是当天，，且下一天时间小于或者等于今天，则设置下一天时间 UTC零时区零点时间。
                    version = EVOUtils.processUtcTimestamp(version);
                    break;
                }
                List<EvoBetRecordResp> dtoList = new ArrayList<>();
                for (String json : rawJsonList) {
                    log.info(json);
                    // 解析成对象
                    JSONObject jsonObj = JSON.parseObject(json);
                    EvoBetRecordResp dto = JSON.to(EvoBetRecordResp.class, jsonObj);
                    //EvoBetRecordResp dto = JSON.parseObject(json, EvoBetRecordResp.class);
                    // 如果是收工拉单，且时间大于结束时间，则不需要统计
                    if (!venuePullParamVO.getPullType()) {
                        String settleAt = dto.getSettledAt();
                        long settleAtTime = EVOUtils.utcToTimestamp(settleAt);
                        if (settleAtTime > venuePullParamVO.getEndTime()) {
                            continue;
                        }
                    }
                    String realJson = com.alibaba.fastjson.JSON.parseObject(json, String.class);
                    dto.setJson(realJson);
                    dtoList.add(dto);
                }

                version = dtoList.get(dtoList.size() - 1).getSettledAt();
                if (ObjectUtils.isEmpty(dtoList)) {
                    // 设置version为当前时间,还是取上次的值
                    //version = EVOUtils.processUtcTimestamp(version);
                    break; // 没有数据了
                }

                //解析注单数据入库
                List<OrderRecordVO> orderRecordVOList = this.parseOrder(venueInfoVO, dtoList);
                if (CollectionUtils.isNotEmpty(orderRecordVOList)) {
                    log.info("Evo-1111 orderProcess: {}", JSON.toJSONString(orderRecordVOList));
                    orderRecordProcessService.orderProcess(orderRecordVOList);
                    data = dtoList;
                }
                //获取最大的版本号
                version = dtoList.get(dtoList.size() - 1).getSettledAt();
                Long versionTime = EVOUtils.utcToTimestamp(version) + 1;
                version = EVOUtils.timestampToUtcZero(versionTime);
            } while (!data.isEmpty());
            log.info("Evo-拉单完成");
            return ResponseVO.success(version);
        } catch (Exception e) {
            log.error("Evo-拉单异常,version:{}", version, e);
        } finally {
            // 关闭连接

        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, List<EvoBetRecordResp> dataList) {

        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.EVO.getVenueCode());
        List<String> thirdUserName = dataList.stream().filter(Objects::nonNull).map(EvoBetRecordResp::getParticipants).filter(Objects::nonNull).flatMap(List::stream).map(EvoBetRecordResp.Participant::getPlayerId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        log.info("Evo-拉单用户信息{}", thirdUserName);
        // 场馆用户关联信息
        Map<String, CasinoMemberPO> casinoMemberMap = super.getCasinoMemberByUsers(thirdUserName, venueInfoVO.getVenuePlatform());
        if (MapUtil.isEmpty(casinoMemberMap)) {
            log.info("{} 未找到用户信息", venueInfoVO.getVenueCode());
            return null;
        }
        // 用户信息
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        if (CollUtil.isEmpty(userMap)) {
            log.info("Evo游戏用户账号不存在{}", userIds);
            return null;
        }
        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
        // 场馆游戏配置
        Map<String, GameInfoPO> gameInfoMap = super.getGameInfoByVenueCode(VenuePlatformConstants.EVO);
        if (CollUtil.isEmpty(gameInfoMap)) {
            log.error("Evo游戏列表未配置");
            return null;
        }

        Map<String, String> siteNameMap = getSiteNameMap();

        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        // 遍历
        for (EvoBetRecordResp evoBetRecordRes : dataList) {


            List<EvoBetRecordResp.Participant> participants = evoBetRecordRes.getParticipants();
            if (CollectionUtils.isEmpty(participants)) {
                continue;
            }
            // 桌台
            String gameId = "";
            EvoBetRecordResp.Table table = evoBetRecordRes.getTable();
            if (!ObjectUtils.isEmpty(table)) {
                gameId = table.getId();
            }

            for (EvoBetRecordResp.Participant participant : participants) {

                // EvoBetRecordResp.Participant participant = participants.get(i);

                List<EvoBetRecordResp.Participant.Bet> bets = participant.getBets();
                if (CollectionUtils.isEmpty(bets)) {
                    continue;
                }
                String account = participant.getPlayerId();
                String status = participant.getStatus();
                String currency = participant.getCurrency();
                String settledAt = evoBetRecordRes.getSettledAt();
                for (int i = 0; i < bets.size(); i++) {
                    EvoBetRecordResp.Participant.Bet entity = bets.get(i);
                    CasinoMemberPO casinoMemberPO = casinoMemberMap.get(account);
                    if (casinoMemberPO == null) {
                        log.error("Evo场馆账号玩家信息不存在，注单信息{}", entity);
                        continue;
                    }
                    UserInfoVO userInfoVO = userMap.get(casinoMemberPO.getUserId());
                    if (userInfoVO == null) {
                        log.error("Evo玩家信息不存在，注单信息{}", entity);
                        continue;
                    }
                    UserLoginInfoVO userLoginInfoVO = loginVOMap.get(casinoMemberPO.getUserId());
                    String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
                    Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

                    OrderRecordVO recordVO = new OrderRecordVO();
                    recordVO.setSiteCode(userInfoVO.getSiteCode());
                    recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                    // 会员账号
                    recordVO.setUserAccount(userInfoVO.getUserAccount());
                    recordVO.setUserId(userInfoVO.getUserId());
                    // 会员姓名
                    recordVO.setUserName(userInfoVO.getUserName());
                    // 账号类型 1测试 2正式 3商务 4置换
                    recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                    recordVO.setAgentId(userInfoVO.getSuperAgentId());
                    recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                    // 三方会员账号
                    recordVO.setCasinoUserName(account);
                    // 上级代理账号 agentAcct
                    recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                    // VIP等级
                    recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                    recordVO.setVipRank(userInfoVO.getVipRank());
                    // 三方平台
                    recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                    // 游戏平台CODE
                    recordVO.setVenueCode(venueInfoVO.getVenueCode());
                    // 游戏大类
                    recordVO.setVenueType(gameTypeId);
                    // 桌台
                    if (StringUtils.isNotBlank(gameId)) {
                        GameInfoPO gameInfoPO = gameInfoMap.get(gameId.toString());
                        if (!Objects.isNull(gameInfoPO)) {
                            recordVO.setGameId(gameInfoPO.getGameId());
                            //recordVO.setThirdGameCode(gameInfoPO.getAccessParameters());
                            recordVO.setGameName(gameInfoPO.getGameI18nCode());
                        }
                    }
                    recordVO.setThirdGameCode(gameId);
                    // 游戏编码
                    recordVO.setThirdGameCode(gameId);
                    // todo playerGameId：玩家在这局游戏的唯一记录 ID
                    recordVO.setGameNo(participant.getPlayerGameId());
                    recordVO.setRoomType(participant.getPlayerGameId());
                    // 玩法  gameType：游戏类型（这里是 blackjack，21 点）
                    recordVO.setPlayType(evoBetRecordRes.getGameType());
                    // 赔率 todo
                    //recordVO.setOdds(BigDecimal.ZERO.toString());
                    // 投注时间
                    recordVO.setBetTime(EVOUtils.utcToTimestamp(entity.getPlacedOn()));
                    // 结算时间
                    recordVO.setSettleTime(EVOUtils.utcToTimestamp(settledAt));
                    // 变更状态 todo
                    recordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());
                    // 投注额
                    recordVO.setBetAmount(entity.getStake());
                    // 输赢金额
                    recordVO.setWinLossAmount(entity.getPayout().subtract(entity.getStake()));
                    // 有效投注
                    recordVO.setValidAmount(entity.getStake());
                    // 派彩金额
                    recordVO.setPayoutAmount(entity.getPayout());
                    recordVO.setBetContent(entity.getCode());
                    // 赔率 todo
                    //recordVO.setOdds(orderResponseVO.getRate().toString());
                    // 注单状态
                    recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
                    // 重新结算 todo 重新计算
                    if (ObjectUtil.equal(status, EvoOrderStatusEnum.CANCELLED.getCode())) {
                        //recordVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
                        continue;
                    }
                    // 注单归类
                    recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
                    // 注单ID
                    recordVO.setOrderId(OrderUtil.getGameNo());
                    // 三方注单ID
                    recordVO.setThirdOrderId(entity.getTransactionId() + "_" + i);
                    recordVO.setTransactionId(entity.getTransactionId());
                    // 订单详情
                    //recordVO.setOrderInfo(entity.getDescription());
                    // 投注IP
                    recordVO.setBetIp(betIp);
                    // 币种 转换为我们币种
                    recordVO.setCurrency(userInfoVO.getMainCurrency());
                    // 设备类型
                    recordVO.setDeviceType(deviceType);
                    //记录原始注单,可能多多组信息
                    JSONObject obj = JSON.parseObject(evoBetRecordRes.getJson());
                    obj.put("thirdOrderId", recordVO.getThirdOrderId());
                    //System.out.println(obj.toJSONString());
                    recordVO.setParlayInfo(JSON.toJSONString(obj));
                    // 创建时间
                    recordVO.setCreatedTime(System.currentTimeMillis());
                    //  免费游戏
                    recordVO.setFreeGame(false);
                    recordVO.setTransactionId(recordVO.getThirdOrderId());


                    orderRecordList.add(recordVO);

                }


            }
            //for (EvoBetRecordResp.Participant participant : participants)
        }

        return orderRecordList;
    }

    /**
     * 解析注单详情
     *
     * @param entity
     * @return
     */
    private String getPlayType(EvoBetRecordRes.Participant.Bet entity) {
        //如果状态是 -2 代表打赏,
        /*if (ShOrderStatusEnum.ALREADY_SETTLED.getCode().equals(orderResponseVO.getOrderStatus())) {
            return SHPlayTypeEnum.TIPS.getCode();
        }

        if (ObjectUtil.isNotEmpty(orderResponseVO.getPlayType())) {
            return orderResponseVO.getPlayType();
        }*/
        return null;
    }


    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        /*String url = venueDetailVO.getApiUrl() + EvoServiceEnums.KICK_OUT.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, String> prams = new HashMap<>();
        prams.put("operator_token", venueDetailVO.getMerchantNo());
        prams.put("secret_key", venueDetailVO.getMerchantKey());
        prams.put("player_name", venueUserAccount);
        log.info("Evo-用户踢线request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("Evo-用户踢线response {}", response);
        } catch (Exception e) {
            log.error("Evo-进入游戏异常【{}】", venueUserAccount, e);
        }*/
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<Boolean> freeGame(FreeGameVO freeGameVO, VenueInfoVO venueInfoVO, List<CasinoMemberVO> casinoMembers) {
       /* String url = venueInfoVO.getApiUrl() + EvoServiceEnums.FREE_GAME.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, String> prams = new HashMap<>();
        prams.put("operator_token", venueInfoVO.getMerchantNo());
        prams.put("secret_key", venueInfoVO.getMerchantKey());
        prams.put("free_game_id", "252791");
        prams.put("transfer_reference", UUID.randomUUID().toString());
        prams.put("allow_multiple", "false");

        List<NameValuePair> params = convertMapToNameValuePairs(prams);
        for (CasinoMemberVO casinoMember : casinoMembers) {
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("player_name", casinoMember.getVenueUserAccount());
            playerData.put("free_game_count", freeGameVO.getCount());
            playerData.put("is_unlimited_bonus_maximum_conversion_amount", false);
            playerData.put("is_unlimited_free_game_maximum_conversion_amount", false);
            playerData.put("bonus_maximum_conversion_amount", 100.50);
            playerData.put("free_game_maximum_conversion_amount", 100.50);
            playerData.put("description", "activity bonus");
            String jsonString = JSONObject.toJSONString(playerData);
            params.add(new BasicNameValuePair("player_free_games", jsonString));
        }

        log.info("Evo-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.doPostPar(url, null, params);
            log.info("Evo-免费游戏response={}", response);
        } catch (Exception e) {
            log.error("Evo-免费游戏异常【{}】", freeGameVO, e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }*/
        return ResponseVO.success(true);
    }

    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class).eq(VenueInfoPO::getVenueCode, VenueEnum.EVO.getVenueCode()).last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);

        // 调用方法获取该场馆的所有桌台信息
        JSONObject tables = listAllEvoGame(venueInfoVO);

        if (tables != null && !tables.isEmpty()) {
            // 遍历所有桌台
            for (String tableKey : tables.keySet()) {
                JSONObject table = tables.getJSONObject(tableKey);

                if (table != null) {
                    // 获取桌台ID和桌台名称
                    String tableId = table.getString("tableId");
                    String deskName = table.getString("name");
                    // 封装桌台信息到 JSON 对象
                    JSONObject deskJson = new JSONObject();
                    deskJson.put("deskNumber", tableId); // 桌台编号
                    deskJson.put("deskName", deskName);  // 桌台名称
                    ShDeskInfoVO deskInfoVO = new ShDeskInfoVO();
                    deskInfoVO.setDeskNumber(tableId);
                    deskInfoVO.setDeskName(deskName);
                    resultList.add(deskInfoVO);
                }
            }
        }
        return resultList;

    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        // 用于存放结果的列表
        List<JSONObject> resultList = Lists.newArrayList();

        try {
            // 调用方法获取该场馆的所有桌台信息
            JSONObject tables = listAllEvoGame(venueInfoVO);

            if (tables != null && !tables.isEmpty()) {
                // 遍历所有桌台
                for (String tableKey : tables.keySet()) {
                    JSONObject table = tables.getJSONObject(tableKey);

                    if (table != null) {
                        // 获取桌台ID和桌台名称
                        String tableId = table.getString("tableId");
                        String deskName = table.getString("name");
                        // 封装桌台信息到 JSON 对象
                        JSONObject deskJson = new JSONObject();
                        deskJson.put("deskNumber", tableId); // 桌台编号
                        deskJson.put("deskName", deskName);  // 桌台名称
                        resultList.add(deskJson);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Evo-获取最新游戏列表失败", e);
        }
        log.info("{} 获取游戏列表成功, 游戏数量: {}", platformName(), resultList.size());
        return ResponseVO.success(resultList);

    }

    public VenueEnum platformEnum() {
        return VenueEnum.EVO;
    }

    public String platformName() {
        return platformEnum().getVenueName();
    }


    private JSONObject listAllEvoGame(VenueInfoVO venueInfoVO) {
        String url = venueInfoVO.getApiUrl() + "/api/lobby/v1/" + venueInfoVO.getMerchantNo() + "/state";
        Map<String, Object> prams = new HashMap<>();

        log.info("Evo-获取最新游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        EvoApiTokens evoApiTokens = JSON.parseObject(venueInfoVO.getMerchantKey(), EvoApiTokens.class);


        String auth = generateAuth(venueInfoVO.getMerchantNo(), evoApiTokens.getExternalLobbyApiToken());
        try (HttpResponse response = HttpRequest.get(url).header(Header.CONTENT_TYPE, "application/json;charset=utf-8")
                //.header(Header.CACHE_CONTROL, "no-cache")
                .header(Header.AUTHORIZATION, auth).timeout(30000).form(prams).execute()) {
            /*String response = HttpClientHandler.post(url, prams);
            log.info("Evo-获取最新游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);*/
            log.info("{} Evo获取游戏列表返回码: {}, 返回消息: {}", platformName(), response.getStatus(), response.body());


            JSONObject root = JSON.parseObject(response.body());

            JSONObject tablesNode = root.getJSONObject("tables");
            return tablesNode;
        } catch (Exception e) {
            log.error(String.format("%s 获取游戏列表发生错误!!", platformName()), e);
        }
        return null;
    }

    @Override
    public List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueDetailVO) {
        List<ThirdGameInfoVO> ret = Lists.newArrayList();
        try {
            JSONObject tables = listAllEvoGame(venueDetailVO);
            for (String tableKey : tables.keySet()) {
                JSONObject table = tables.getJSONObject(tableKey);
                String tableId = table.getString("tableId");
                String gameName = table.getString("name");
                ThirdGameInfoVO thirdGameInfoVO = new ThirdGameInfoVO();
                // 名称多语言
                List<GameNameVO> gameNameVOS = Lists.newArrayList();
                GameNameVO gameNameVO = new GameNameVO();

                gameNameVO.setGameName(gameName);
                gameNameVO.setLang(LanguageEnum.ZH_CN.getLang());
                gameNameVOS.add(gameNameVO);
                thirdGameInfoVO.setGameName(gameNameVOS);
                thirdGameInfoVO.setGameCode(tableId);
                ret.add(thirdGameInfoVO);
            }
        } catch (Exception e) {
            log.error("Evo-获取最新游戏error", e);
        }

        return ret;
    }



    public BalanceResponse balance(BalanceRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
                log.info("{}:用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
            if (!tokenFlag) {
                log.info("{}:用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.EVO);
            CasinoMemberVO casinoMemberData = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMemberData == null) {
                log.info("{}:游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            String userAccount = casinoMemberData.getUserAccount();
            String siteCode = casinoMemberData.getSiteCode();
            // 币种校验与切换

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            EvoCurrencyEnum currency = EvoCurrencyEnum.byPlatCurrencyCode(userCenterCoin.getCurrency());
            if (currency == null || currency.getCode() == null || !StringUtils.equals(currency.getCode(), request.getCurrency())) {
                log.info("{}:用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            BigDecimal totalAmount = userCenterCoin.getTotalAmount();

            return BalanceResponse.success(request.getUuid(), totalAmount);
        } catch (Exception e) {
            log.error("游戏登入后令牌验证异常, userId: {}", request.getUserId(), e);
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
        }

    }

    public CheckUserResponse check(CheckUserRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.info("{}:用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
            if (!tokenFlag) {
                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
            }

            return CheckUserResponse.success(request.getUuid(), request.getSid());
        } catch (Exception e) {
            log.error("游戏登入后令牌验证异常, userId: {}", request.getUserId(), e);
            return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
        }
    }

    /**
     * 生成用户的 sid
     *
     * @param request 请求参数
     */
    public CheckUserResponse sid(CheckUserRequest request) {
        String authToken = request.getAuthToken();
        VenueEnum venueEnum = VenueEnum.EVO;
        String userId = getVenueUserAccount(request.getUserId());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户sid账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
        }

        /*UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO == null || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户sid被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
        }*/
        /*boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
        if (!tokenFlag) {
            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
        }*/

        // 生成新的 SID
        String newSid = UUID.randomUUID().toString().replace("-", "");
        return CheckUserResponse.success(request.getUuid(), newSid);
    }

    public BalanceResponse debit(DebitRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:投注用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null
                    || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())
                    || userInfoVO.getAccountStatus().contains(UserStatusEnum.LOGIN_LOCK.getCode())) {
                log.info("{}:投注用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.ACCOUNT_LOCKED);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), true);
            if (!tokenFlag) {
                log.info("{}:投注用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.TEMPORARY_ERROR);
            }

            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.EVO);
            CasinoMemberVO casinoMemberData = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMemberData == null) {
                log.info("{}:游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }


            // 币种校验与切换
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.info("{}:投注用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            EvoCurrencyEnum currency = EvoCurrencyEnum.byPlatCurrencyCode(userCenterCoin.getCurrency());
            if (currency == null || currency.getCode() == null || !StringUtils.equals(currency.getCode(), request.getCurrency())) {
                log.info("{}:投注用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            // 游戏配置是否存在
            String tableId = request.getGame().getDetails().getTable().getId();
            boolean gameAvailable = isGameAvailable(userInfoVO.getSiteCode(), tableId, VenuePlatformConstants.EVO, userInfoVO.getMainCurrency());
            if (!gameAvailable) {
                log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), tableId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.ACCOUNT_LOCKED);
            }
            BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();
            // 投注金额
            BigDecimal betAmount = request.getTransaction().getAmount();//账变金额
            // 余额校验

            if (userTotalAmount.compareTo(betAmount) < 0) {
                log.info("{}:投注用户钱包,余额不足:{},用户金额:{},扣款金额:{}", VenueEnum.EVO.getVenueName(), userId, userTotalAmount, betAmount);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INSUFFICIENT_FUNDS);
            }
            // 每一笔注单都要发生帐变
            //List<DebitRequest.Transaction.Bets> bets = request.getTransaction().getBets();

            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
            // 取总的投注进行帐变
            // 校验
            UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, null, userInfoVO);
            // 或者金额是否为0
            if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
                // 帐变金额为o
                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
            }
            UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
            userCoinRecordRequestVO.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordRequestVO.setUserId(userCoinAddVO.getUserId());
            userCoinRecordRequestVO.setBalanceType(userCoinAddVO.getBalanceType());
            List<UserCoinRecordVO> userCoinRecord = getUserCoinRecords(userCoinRecordRequestVO);
            // 判断是否重复，
            if (ObjectUtil.isNotEmpty(userCoinRecord)) {
                // 有重复
                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
            }
            // 取消订单校验
            boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
            if (betCancel) {
                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
            }
            recordResultVO = toUserCoinHandle(userCoinAddVO);
            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
            }
            /*if (bets != null && bets.size() > 0) {
                for (DebitRequest.Transaction.Bets bet : bets) {
                    BigDecimal amount = bet.getAmount();
                    String code = bet.getCode();
                    // 校验投注金额
                    if (Objects.isNull(code) || Objects.isNull(amount)) {
                        log.info("{}:投注详值不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                    }
                    UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, bet, userInfoVO);
                    // 是否重复
                    List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
                    if (ObjectUtil.isNotEmpty(userCoinRecord)) {
                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
                    }
                    // 取消订单校验
                    boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
                    if (betCancel) {
                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
                    }

                }

                for (DebitRequest.Transaction.Bets bet : bets) {
                    UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, bet, userInfoVO);
                    // 金额是否为0
                    if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
                        // 帐变金额为o
                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                        continue;
                    }
                    // 是否重复
                    List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
                    if (ObjectUtil.isNotEmpty(userCoinRecord)) {
                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                        continue;
                    }

                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);
                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                        log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                    }
                }
            } else {
                // 校验
                UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, null, userInfoVO);
                // 或者金额是否为0
                if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
                    // 帐变金额为o
                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                    return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
                }
                // 判断是否重复，
                List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
                if (ObjectUtil.isNotEmpty(userCoinRecord)) {
                    // 有重复
                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
                }
                // 取消订单校验
                boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
                if (betCancel) {
                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
                }
                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                    log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
                }
            }*/
            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
        } catch (Exception e) {
            log.error("EVO游戏投注异常, userId: {}", request.getUserId(), e);
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
        }

    }

    /**
     * 投注校验是否该注单取消了
     *
     * @return 结果
     */
    private Boolean isBetCancel(UserInfoVO userInfoVO, String orderNo) {
        //订单状态
        String key = String.format(RedisConstants.EVO_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userInfoVO.getUserId());
        List<String> cancelBetList = RedisUtil.getValue(key);
        if (cancelBetList != null && !cancelBetList.isEmpty()) {
            return cancelBetList.contains(orderNo);
        }
        return false;

    }

    public BalanceResponse credit(CreditRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:派彩失败,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
                log.info("{}:派彩失败,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
            if (!tokenFlag) {
                log.info("{}:派彩失败,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.EVO);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                log.info("{}:派彩失败,游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            // 币种校验与切换
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.info("{}:派彩失败,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            EvoCurrencyEnum currency = EvoCurrencyEnum.byPlatCurrencyCode(userCenterCoin.getCurrency());
            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
                log.info("{}:派彩失败,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            // 每一笔注单都要发生帐变
            //List<CreditRequest.Transaction.Bets> bets = request.getTransaction().getBets();
            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
            UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, null, userInfoVO);
            BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
            if (ObjectUtil.isNotEmpty(response)) {
                return response;
            }
            recordResultVO = toUserCoinHandle(userCoinAddVO);
            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                if (!recordResultVO.getResult() && recordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS)) {
                    return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
                }
                log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
           /* if (bets != null && bets.size() > 0) {
                for (CreditRequest.Transaction.Bets bet : bets) {
                    BigDecimal amount = bet.getPayoff();
                    String code = bet.getCode();
                    // 校验投注金额
                    if (Objects.isNull(code) || Objects.isNull(amount)) {
                        log.info("{}:派彩失败值不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                    }
                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, bet, userInfoVO);
                    BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
                    if (ObjectUtil.isNotEmpty(response)) {
                        return response;
                    }

                }

                for (CreditRequest.Transaction.Bets bet : bets) {
                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, bet, userInfoVO);
                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);
                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                        log.error("派彩失败帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
                    }
                }
            } else {
                UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, null, userInfoVO);
                BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
                if (ObjectUtil.isNotEmpty(response)) {
                    return response;
                }
                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                    if (!recordResultVO.getResult() && recordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS)) {
                        return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
                    }
                    log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                }
            }*/

            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
        } catch (Exception e) {
            log.error("派彩失败, 参数: {}", JSON.toJSONString(request), e);
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * 派彩校验
     * 1.投注。 2.取消投注  3.派彩，那么这个时候，派彩会成功还是失败， 应该是失败，提示BET_ALREADY_SETTLED
     *
     * @param userCoinAddVO  用户投注请求对象
     * @param userCenterCoin 用户中心余额对象
     * @param recordResultVO 记录结果对象，用于设置余额
     * @return 如果存在问题返回对应的BalanceResponse，否则返回null
     */
    private BalanceResponse validateUserCredit(
            UserCoinAddVO userCoinAddVO,
            CreditRequest request,
            UserCoinWalletVO userCenterCoin,
            CoinRecordResultVO recordResultVO) {

        // 判断是否重复，或者金额是否为0
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
            // 帐变金额为o
            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
        }

        UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
        userCoinRecordRequestVO.setOrderNo(userCoinAddVO.getOrderNo());
        userCoinRecordRequestVO.setUserId(userCoinAddVO.getUserId());
        List<UserCoinRecordVO> userCoinRecord = getUserCoinRecords(userCoinRecordRequestVO);
        // 取消投注
        boolean isExist = false;
        if (ObjectUtil.isNotEmpty(userCoinRecord)) {
            // 判断是否重复或者已经取消
            for (UserCoinRecordVO userCoinRecordVO : userCoinRecord) {
                String coinType = userCoinRecordVO.getCoinType();
                //是否有这笔投注
                // 是否有这笔投注
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_BET.getCode()) && ObjectUtil.equals(userCoinRecordVO.getBalanceType(), CoinBalanceTypeEnum.EXPENSES.getCode())) {
                    isExist = true;
                }
                // 是否重复
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())) {
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
                }
                // 这笔投注已经取消
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())) {
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
                }

            }

        }
        // 是否有这笔投注
        //List<UserCoinRecordVO> userCoinRecordRePeat = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
        if (!isExist) {
            // 没有这笔投注
            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_DOES_NOT_EXIST);
        }
        // 4. 校验通过
        return null;
    }


    public BalanceResponse cancel(CancelRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:取消投注,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
                log.info("{}:取消投注,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
            if (!tokenFlag) {
                log.info("{}:取消投注,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.EVO);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                log.info("{}:取消投注,游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            // 币种校验与切换
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin)) {
                log.info("{}:取消投注,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            EvoCurrencyEnum currency = EvoCurrencyEnum.byPlatCurrencyCode(userCenterCoin.getCurrency());
            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
                log.info("{}:取消投注,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            // 每一笔注单都要发生帐变

            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
            UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, null, userInfoVO);
            setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
            BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
            if (response != null) {
                return response;
            }

            recordResultVO = toUserCoinHandle(userCoinAddVO);
            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            /*List<CancelRequest.Transaction.Bets> bets = request.getTransaction().getBets();
            if (bets != null && bets.size() > 0) {
                for (CancelRequest.Transaction.Bets bet : bets) {
                    BigDecimal amount = bet.getAmount();
                    String code = bet.getCode();
                    // 校验投注金额
                    if (Objects.isNull(code) || Objects.isNull(amount)) {
                        log.info("{}:取消投注不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                    }

                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, bet, userInfoVO);
                    setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
                    BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
                    if (response != null) {
                        return response;
                    }


                }
                for (CancelRequest.Transaction.Bets bet : bets) {

                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, bet, userInfoVO);

                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);

                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                        log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                    }

                }
            } else {
                UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, null, userInfoVO);
                setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
                BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
                if (response != null) {
                    return response;
                }

                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
                    log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
                }


            }*/

            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
        } catch (Exception e) {
            log.error("派彩失败, 参数: {}", JSON.toJSONString(request), e);
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
        }
    }

    private void setCancelBetList(UserInfoVO userInfoVO, String orderId) {
        log.info("evo cancelBet error 账单不存在: " + orderId);
        String key = String.format(RedisConstants.EVO_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userInfoVO.getUserId());
        List<String> cancelBetList = RedisUtil.getValue(key);
        if (cancelBetList == null || cancelBetList.isEmpty()) {
            cancelBetList = org.apache.commons.compress.utils.Lists.newArrayList();
        }
        cancelBetList.add(orderId);
        log.info(" evo cancel bet list : " + cancelBetList + " tid : " + orderId + " key : " + key);
        RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
    }

    private BalanceResponse ValidateCancel(UserCoinAddVO userCoinAddVO,
                                           CancelRequest request,
                                           UserCoinWalletVO userCenterCoin,
                                           CoinRecordResultVO recordResultVO) {

        // 判断是否重复，或者金额是否为0
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
            // 帐变金额为o
            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
        }
        UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
        userCoinRecordRequestVO.setOrderNo(userCoinAddVO.getOrderNo());
        userCoinRecordRequestVO.setUserId(userCoinAddVO.getUserId());
        List<UserCoinRecordVO> userCoinRecord = getUserCoinRecords(userCoinRecordRequestVO);
        // 取消投注是否重复
        boolean isExist = false;
        if (CollectionUtil.isNotEmpty(userCoinRecord)) {
            // 判断是否重复或者已经取消
            for (UserCoinRecordVO userCoinRecordVO : userCoinRecord) {
                String coinType = userCoinRecordVO.getCoinType();
                //是否有这笔投注
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_BET.getCode()) && ObjectUtil.equals(userCoinRecordVO.getBalanceType(), CoinBalanceTypeEnum.EXPENSES.getCode())) {
                    isExist = true;
                }
                // 是否已经派彩
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())) {
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
                }
                // 这笔投注已经取消
                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())) {
                    log.info("{}:取消投注,下注已取消,订单号:{}", VenueEnum.EVO.getVenueName(), userCoinAddVO.getOrderNo());
                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
                }
            }
        }
        // 是否有这笔投注
        //List<UserCoinRecordVO> userCoinRecordRePeat = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
        if (!isExist) {
            log.info("{}:取消投注,下注不存在,订单号:{}", VenueEnum.EVO.getVenueName(), userCoinAddVO.getOrderNo());
            // 没有这笔投注
            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_DOES_NOT_EXIST);
        }
        return null;
    }


    public BalanceResponse promoPayout(PromoPayoutRequest request) {
        String authToken = request.getAuthToken();
        try {
            VenueEnum venueEnum = VenueEnum.EVO;
            String userId = getVenueUserAccount(request.getUserId());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:奖励,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
                log.info("{}:奖励,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
            if (!tokenFlag) {
                log.info("{}:奖励,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }

            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.EVO);
            CasinoMemberVO casinoMemberData = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMemberData == null) {
                log.info("{}:游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }


            // 币种校验与切换
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.info("{}:奖励,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            EvoCurrencyEnum currency = EvoCurrencyEnum.byPlatCurrencyCode(userCenterCoin.getCurrency());
            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
                log.info("{}:奖励,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
            }
            // 每一笔注单都要发生帐变
            BigDecimal payoutAmount = request.getPromoTransaction().getAmount();
            String orderNo = request.getPromoTransaction().getId();
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(payoutAmount);
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            // 使用 一组投注或者一组派彩
            userCoinAddVO.setRemark("promo_payout_" + orderNo);
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            // 判断是否重复，或者金额是否为0
            if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
                // 帐变金额为o
                return BalanceResponse.success(request.getUuid(), userCenterCoin.getCenterAmount());
            }

            UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
            userCoinRecordRequestVO.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordRequestVO.setUserId(userCoinAddVO.getUserId());
            userCoinRecordRequestVO.setBalanceType(userCoinAddVO.getBalanceType());
            List<UserCoinRecordVO> userCoinRecordRepeats = getUserCoinRecords(userCoinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinRecordRepeats)) {
                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
            }
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
            if (recordResultVO != null && recordResultVO.getResult()) {
                return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
            }
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
        } catch (Exception e) {
            log.error("奖励失败, 参数: {}", JSON.toJSONString(request), e);
            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
        }

    }

    @NotNull
    private UserCoinAddVO getUserCoinAddVO(DebitRequest request, DebitRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {
        String refId = request.getTransaction().getRefId();
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();

        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        // 使用 一组投注或者一组派彩
        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.EVO);

        if (Objects.isNull(bet)) {
            userCoinAddVO.setOrderNo(refId);
            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
            userCoinAddVO.setThirdOrderNo(refId);
        } else {
            userCoinAddVO.setOrderNo(refId + "_" + bet.getCode());
            userCoinAddVO.setThirdOrderNo(refId + "_" + bet.getCode());
            userCoinAddVO.setCoinValue(bet.getAmount());
        }
        return userCoinAddVO;
    }


    @NotNull
    private UserCoinAddVO getUserCoinAddVOForPayOff(CreditRequest request, CreditRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {

        String refId = request.getTransaction().getRefId();
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();

        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        // 使用 一组投注或者一组派彩
        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.EVO);
        if (Objects.isNull(bet)) {
            userCoinAddVO.setThirdOrderNo(refId);
            userCoinAddVO.setOrderNo(refId);
            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
        } else {
            String orderNo = refId + "_" + bet.getCode();
            userCoinAddVO.setThirdOrderNo(orderNo);
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setCoinValue(bet.getPayoff());
        }

        return userCoinAddVO;
    }

    @NotNull
    private UserCoinAddVO getUserCoinAddVOForCancel(CancelRequest request, CancelRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();

        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        // 使用 一组投注或者一组派彩
        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.EVO);

        if (Objects.isNull(bet)) {
            String orderNo = request.getTransaction().getRefId();
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setThirdOrderNo(orderNo);
            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
        } else {
            String orderNo = request.getTransaction().getRefId() + "_" + bet.getCode();
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setCoinValue(bet.getAmount());
            userCoinAddVO.setThirdOrderNo(orderNo);

            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
            userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        }

        return userCoinAddVO;
    }


    /**
     * 检查指定场馆的游戏是否可用。
     *
     * @param siteCode  站点代码
     * @param gameCode  游戏代码
     * @param venueCode 场馆代码
     * @return 如果游戏存在且状态为开启，返回 {@code true}，否则返回 {@code false}
     */
    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode, String currencyCode) {
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

    private boolean validatorToken(String authToken, String siteCode, Boolean isCheck) {
        boolean venueMaintainClosed = venueMaintainClosed(VenueEnum.EVO.getVenueCode(), siteCode);
        if (venueMaintainClosed && isCheck) {
            log.error("场馆维护中, siteCode: {}", siteCode);
            return false;
        }

        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(siteCode, VenueEnum.EVO.getVenueCode(), null);
        if (venueInfoVO == null) {
            log.error("获取场馆信息失败, siteCode: {}", siteCode);
            return false;
        }

        EvoApiTokens evoApiTokens = JSON.parseObject(venueInfoVO.getMerchantKey(), EvoApiTokens.class);
        if (evoApiTokens == null) {
            log.error("获取场馆信息失败, siteCode: {}", siteCode);
            return false;
        }
        String token = evoApiTokens.getWalletToken();
        return StringUtils.equals(token, authToken);
    }



    private CheckUserResponse buildFailResponse(CheckUserRequest request, EvoGameErrorCode evoGameErrorCode) {
        return CheckUserResponse.fail(request.getUuid(), request.getSid(), evoGameErrorCode);
    }


}
