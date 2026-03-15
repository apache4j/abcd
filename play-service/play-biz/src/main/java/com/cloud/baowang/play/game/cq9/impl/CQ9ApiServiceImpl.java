package com.cloud.baowang.play.game.cq9.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TransferEnums;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
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
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.DeviceEnums;
import com.cloud.baowang.play.api.enums.cq9.CQ9CallEnums;
import com.cloud.baowang.play.api.enums.cq9.CQ9CurrencyEnum;
import com.cloud.baowang.play.api.enums.cq9.CQ9ResultCodeEnums;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.vo.cq9.request.*;
import com.cloud.baowang.play.api.vo.cq9.response.*;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoRspVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.cq9.constant.CQ9Constant;
import com.cloud.baowang.play.game.cq9.enums.CQ9GameTypeEnums;
import com.cloud.baowang.play.game.cq9.enums.CQ9TransferTypeEnums;
import com.cloud.baowang.play.game.cq9.utils.CQ9Utils;
import com.cloud.baowang.play.po.CQ9TransactionRecordPO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.repositories.CQ9TransactionRecordRepository;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.cq9.params.CQ9PullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.GameNameVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserAccountListVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/**
 * CQ9Api服务实现类
 *
 * @author: lavine
 */
@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.CQ9)
@AllArgsConstructor
public class CQ9ApiServiceImpl extends GameBaseService implements GameService {

    public static final ZoneId gmt4ZoneId = ZoneId.of("Etc/GMT+4");

    public static final TimeZone gmt4TimeZone = TimeZone.getTimeZone("GMT-4");


    /**
     * 时区：GMT+8 (东8区)
     */
    public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("zh-CN", "zh-cn");
        LANGUAGE_MAP.put("zh-TW", "zh-tw");
        LANGUAGE_MAP.put("en-US", "en");
        LANGUAGE_MAP.put("vi-VN", "vn");// 越南文
        LANGUAGE_MAP.put("id-ID", "id");
        LANGUAGE_MAP.put("pt-BR", "pt");// 葡萄牙语-巴西
        LANGUAGE_MAP.put("ko-KR", "ko");      // 韩语 - 韩国
        LANGUAGE_MAP.put("th-TH", "th");      // 泰语 - 泰国
        LANGUAGE_MAP.put("id-ID", "id");      // 印尼语 - 印度尼西亚
        LANGUAGE_MAP.put("fil-PH", "ph");     // 菲律宾语（塔加洛语） - 菲律宾
        LANGUAGE_MAP.put("de-DE", "de");      // 德语 - 德国
        LANGUAGE_MAP.put("da-DK", "da");      // 丹麦语 - 丹麦
        LANGUAGE_MAP.put("es-ES", "es");      // 西班牙语 - 西班牙
        LANGUAGE_MAP.put("fi-FI", "fi");      // 芬兰语 - 芬兰
        LANGUAGE_MAP.put("fr-FR", "fr");      // 法语 - 法国
        LANGUAGE_MAP.put("it-IT", "it");      // 意大利语 - 意大利
        LANGUAGE_MAP.put("no-NO", "no");      // 挪威语 - 挪威
        LANGUAGE_MAP.put("pt-BR", "pt-br");   // 葡萄牙语 - 巴西
        LANGUAGE_MAP.put("ru-RU", "ru");      // 俄语 - 俄罗斯
        LANGUAGE_MAP.put("sv-SE", "se");      // 瑞典语 - 瑞典（注意标准缩写应为sv）
        LANGUAGE_MAP.put("tr-TR", "tr");      // 土耳其语 - 土耳其
        LANGUAGE_MAP.put("ja-JP", "ja");      // 日语 - 日本
        LANGUAGE_MAP.put("hi-IN", "hi");      // 印地语 - 印度
        LANGUAGE_MAP.put("km-KH", "km");      // 高棉语 - 柬埔寨
        LANGUAGE_MAP.put("ms-MY", "ms");      // 马来语 - 马来西亚


    }

    private final OrderRecordProcessService orderRecordProcessService;
    private final UserInfoApi userInfoApi;
    private final CasinoMemberService casinoMemberService;
    private final VenueInfoService venueInfoService;
    private final PlayVenueInfoApi playVenueInfoApi;
    private final UserCoinApi userCoinApi;
    private final UserCoinRecordApi userCoinRecordApi;
    private final CQ9TransactionRecordRepository transactionRecordRepository;
    private final SiteApi siteApi;
    private final GameInfoService gameInfoService;

    /**
     * 创建玩家
     *
     * @param venueInfoVO    场馆信息
     * @param casinoMemberVO 玩家信息
     * @return 返回结果
     */
    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        //查看贵司是单一钱包，目前贵司是使用转帐钱包API，单一钱包不需要创建玩家，只需要直接获取游戏链结即可，
        if (true) {
            return ResponseVO.success(true);
        }
        // 不用创建用户
        String casinoUserName = casinoMemberVO.getVenueUserAccount();
        String casinoPassword = casinoMemberVO.getCasinoPassword();

        String url = venueInfoVO.getApiUrl() + "/gameboy/player";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("account", casinoUserName);
        paramMap.put("nickname", casinoUserName);
        paramMap.put("password", casinoPassword);

        HttpResponse response = null;
        try {
            log.info("{} 创建帐号, 请求url: {}, 参数: {}", platformName(), url, JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 创建帐号, 返回码: {}, 用户名: {}, 返回消息: {}", platformName(), response.getStatus(), casinoUserName, response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null
                        && (CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())
                        || CQ9Constant.ALREADY_HAS_SAME_ACCOUNT.equals(baseRsp.getStatus().getCode()) //会员已存在 直接成功
                )) {
                    log.info("{} 创建帐号成功, 用户名: {}", platformName(), casinoUserName);
                    return ResponseVO.success(true);
                }
                log.info("{} 创建帐号失败, 用户名: {}", platformName(), casinoUserName);
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 创建会员发生错误!!", platformName(), casinoUserName), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    log.error("Failed to close response", e);
                }
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 登入,获取链接，单一钱包
     *
     * @param loginVO        登录用户
     * @param venueInfoVO    场馆信息
     * @param casinoMemberVO 玩家信息
     * @return 返回结果
     */
    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        // CQ9如果玩家账户没有钱，导致获取游戏链接失败，此处需要提前给提示
        /*UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(loginVO.getUserId()).build());
        if (ObjectUtil.isNull(userCenterCoin) ||
                userCenterCoin.getTotalAmount() == null ||
                BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            throw new BaowangDefaultException(ResultCode.WALLET_INSUFFICIENT_BALANCE);
        }*/
        // 判断游戏是否配置
        // 判断游戏是否配置
        if (StringUtils.isNotBlank(loginVO.getGameCode())) {
            if (!isGameAvailable(loginVO.getSiteCode(), loginVO.getGameCode(), loginVO.getVenueCode())) {
                throw new BaowangDefaultException(ResultCode.VENUE_CHOOSE_ERROR);
            }
        }
        String venueUserAccount = casinoMemberVO.getVenueUserAccount();

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("account", venueUserAccount);
        paramMap.put("password", casinoMemberVO.getCasinoPassword());


        try {
            ResponseVO responseVO = null;
            // 如果不带gameCode,则跳转大厅，如果带有gameCode，则返回指定游戏的链接
            if (StringUtils.isEmpty(loginVO.getGameCode())) {
                //responseVO = getGameLobbylink(venueInfoVO, usertoken, venueUserAccount, loginVO);
                log.error("{} 用户名: {}, 获取大厅链接结果: {}, 返回消息: {}", platformName(), venueUserAccount,
                        responseVO.isOk(), JSON.toJSONString(responseVO));
            } else {
                responseVO = getGamelinkSingle(venueInfoVO, loginVO, venueUserAccount, casinoMemberVO.getCasinoPassword());
                log.info("{} 用户名: {}, 获取游戏链接结果: {}, 返回消息: {}", platformName(), venueUserAccount,
                        responseVO.isOk(), JSON.toJSONString(responseVO));
            }
            return responseVO;
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 登录发生错误!!", platformName(), venueUserAccount), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 登入
     *
     * @param loginVO        登录用户
     * @param venueInfoVO    场馆信息
     * @param casinoMemberVO 玩家信息
     * @return 返回结果
     */
    public ResponseVO<GameLoginVo> loginCopy(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        String url = venueInfoVO.getApiUrl() + "/gameboy/player/login";

        String venueUserAccount = casinoMemberVO.getVenueUserAccount();

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("account", venueUserAccount);
        paramMap.put("password", casinoMemberVO.getCasinoPassword());

        HttpResponse response = null;
        try {
            log.info("{} 用户名: {}, 登录 URL: {}, 参数: {}", platformName(), venueUserAccount, url,
                    JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 登录返回码: {}, 返回消息: {}", platformName(), venueUserAccount,
                    response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    // 获取用户token
                    String usertoken = jsonObject.getJSONObject("data").getString("usertoken");

                    ResponseVO responseVO = null;
                    // 如果不带gameCode,则跳转大厅，如果带有gameCode，则返回指定游戏的链接
                    if (StringUtils.isEmpty(loginVO.getGameCode())) {
                        responseVO = getGameLobbylink(venueInfoVO, usertoken, venueUserAccount, loginVO);
                        log.info("{} 用户名: {}, 获取大厅链接结果: {}, 返回消息: {}", platformName(), venueUserAccount,
                                responseVO.isOk(), JSON.toJSONString(responseVO));
                    } else {
                        responseVO = getGamelink(venueInfoVO, usertoken, loginVO, venueUserAccount);
                        log.info("{} 用户名: {}, 获取游戏链接结果: {}, 返回消息: {}", platformName(), venueUserAccount,
                                responseVO.isOk(), JSON.toJSONString(responseVO));
                    }
                    return responseVO;
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 登录发生错误!!", platformName(), venueUserAccount), e);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 获取游戏大厅的跳转链接
     *
     * @param venueInfoRspVO 场馆信息
     * @param usertoken      用户token
     * @param userAccount    用户
     * @return 返回
     */
    private ResponseVO getGameLobbylink(VenueInfoVO venueInfoRspVO, String usertoken, String userAccount, LoginVO loginVO) {
        String url = venueInfoRspVO.getApiUrl() + "/gameboy/player/lobbylink";

        Map<String, Object> paramMap = Maps.newHashMap();
        // 使用者Token
        paramMap.put("usertoken", usertoken);
        // 語言代碼，目前全額提供 zh-cn ,en 多數已支援 th ,vn，確切支援程度請調用游戏列表 API 回傳資訊 ※若未帶參數則預設為en
        paramMap.put("lang", "zh-cn");
        // 是否是透過app 執行游戏，Y=是，N=否，預設為N
        String app = DeviceEnums.PC.getCode().equals(loginVO.getDevice()) ? "N" : "Y";
        paramMap.put("app", app);
        // 是否開啟阻擋不合游戏規格瀏覽器提示， Y=是，N=否，預設為N
        paramMap.put("detect", "N");

        HttpResponse response = null;
        try {
            log.info("{} 用户名: {}, 获取大厅链接 URL: {}, 参数: {}", platformName(), userAccount, url,
                    JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoRspVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 获取大厅链接返回码: {}, 返回消息: {}", platformName(), userAccount,
                    response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    // 获取用户token
                    String lobbyUrl = jsonObject.getJSONObject("data").getString("url");

                    HashMap<String, String> loginHashMap = new HashMap<>();
                    loginHashMap.put("url", lobbyUrl);
                    loginHashMap.put("userAccount", userAccount);
                    return ResponseVO.success(loginHashMap);
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 获取大厅链接发生错误!!", platformName(), userAccount), e);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 获取指定游戏的跳转链接-单一钱包
     * 您好，查看贵司是单一钱包，目前贵司是使用转帐钱包API，单一钱包不需要创建玩家，只需要直接获取游戏链结即可，请改用单一钱包API并使用其他玩家帐号再次测试，谢谢。
     *
     * @param venueInfoRspVO 场馆信息
     * @param loginVO        登录信息
     * @return 返回结果
     */
    private ResponseVO getGamelinkSingle(VenueInfoVO venueInfoRspVO, LoginVO loginVO, String venueUserAccount, String secretKey) {
        String url = venueInfoRspVO.getApiUrl() + "/gameboy/player/sw/gamelink";
        Map<String, Object> paramMap = Maps.newHashMap();
        // 使用者Token
        paramMap.put("account", venueUserAccount);
        // 游戏廠商
        paramMap.put("gamehall", "cq9");
        // 游戏代码 对应id
        paramMap.put("gamecode", loginVO.getGameCode());
        // 游戏平台，請填入 web 或 mobile ※若gameplat 不為該游戏平台時，將帶入預設值，預設值為web

        paramMap.put("gameplat", "mobile");
        // 語言代碼，目前全額提供 zh-cn ,en 多數已支援 th ,vn，確切支援程度請調用游戏列表 API 回傳資訊 ※若未帶參數則預設為en
        // 设置语言
        String lang = LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(), "en");
        paramMap.put("lang", lang);
        // 是否是透過app 執行游戏，Y=是，N=否，預設為N

        paramMap.put("session", getToken(venueUserAccount));
        paramMap.put("app", "N");
        // 是否開啟阻擋不合游戏規格瀏覽器提示， Y=是，N=否，預設為N
        paramMap.put("detect", "N");

        HttpResponse response = null;
        try {
            log.info("{} 用户名: {}, 获取游戏链接 URL: {}, 参数: {}", platformName(), venueUserAccount, url,
                    JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoRspVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 获取游戏链接返回码: {}, 返回消息: {}", platformName(), venueUserAccount,
                    response.getStatus(), response.body());

            // 如果是 301，手动重定向
            if (response.getStatus() == 301 || response.getStatus() == 302) {
                String newUrl = response.header("Location");
                log.info("检测到 301 重定向，新 URL: {}", newUrl);

                response = HttpRequest.post(newUrl)
                        .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                        .header(Header.CACHE_CONTROL, "no-cache")
                        .header(Header.AUTHORIZATION, venueInfoRspVO.getMerchantKey())
                        .timeout(30000)
                        .form(paramMap)
                        .execute();
            }

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    // 获取用户token
                    String lobbyUrl = jsonObject.getJSONObject("data").getString("url");

                    GameLoginVo gameLoginVo = GameLoginVo.builder()
                            .source(lobbyUrl).userAccount(loginVO.getUserAccount())
                            .venueCode(VenueEnum.CQ9.getVenueCode())
                            .type(GameLoginTypeEnums.URL.getType()).build();
                    return ResponseVO.success(gameLoginVo);
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 获取游戏链接发生错误!!", platformName(), venueUserAccount), e);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 获取指定游戏的跳转链接-转账钱包
     * 您好，查看贵司是单一钱包，目前贵司是使用转帐钱包API，单一钱包不需要创建玩家，只需要直接获取游戏链结即可，请改用单一钱包API并使用其他玩家帐号再次测试，谢谢。
     *
     * @param venueInfoRspVO 返回结果
     * @param usertoken      用户token
     * @param loginVO        结果
     * @return 结果
     */
    private ResponseVO getGamelink(VenueInfoVO venueInfoRspVO, String usertoken, LoginVO loginVO, String userAccount) {
        String url = venueInfoRspVO.getApiUrl() + "/gameboy/player/gamelink";

        Map<String, Object> paramMap = Maps.newHashMap();
        // 使用者Token
        paramMap.put("usertoken", usertoken);
        // 游戏廠商
        paramMap.put("gamehall", "cq9");
        // 游戏代码 对应id
        paramMap.put("gamecode", loginVO.getGameCode());
        // 游戏平台，請填入 web 或 mobile ※若gameplat 不為該游戏平台時，將帶入預設值，預設值為web
        paramMap.put("gameplat", "mobile");
        // 語言代碼，目前全額提供 zh-cn ,en 多數已支援 th ,vn，確切支援程度請調用游戏列表 API 回傳資訊 ※若未帶參數則預設為en
        // 设置语言
        String lang = LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(), "en");
        paramMap.put("lang", lang);
        // 是否是透過app 執行游戏，Y=是，N=否，預設為N
        paramMap.put("app", "N");
        // 是否開啟阻擋不合游戏規格瀏覽器提示， Y=是，N=否，預設為N
        paramMap.put("detect", "N");

        HttpResponse response = null;
        try {
            log.info("{} 用户名: {}, 获取游戏链接 URL: {}, 参数: {}", platformName(), userAccount, url,
                    JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoRspVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 获取游戏链接返回码: {}, 返回消息: {}", platformName(), userAccount,
                    response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    // 获取用户token
                    String lobbyUrl = jsonObject.getJSONObject("data").getString("url");

                    HashMap<String, String> loginHashMap = new HashMap<>();
                    loginHashMap.put("url", lobbyUrl);
                    loginHashMap.put("userAccount", loginVO.getUserAccount());
                    return ResponseVO.success(loginHashMap);
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 获取游戏链接发生错误!!", platformName(), userAccount), e);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    public ResponseVO logout(String userAccount, VenueInfoRspVO venueInfoRspVO, CasinoMemberVO casinoMemberVO) {
        String url = venueInfoRspVO.getApiUrl() + "/gameboy/player/logout";
        userAccount = casinoMemberVO.getVenueUserAccount();

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("account", userAccount);

        HttpResponse response = null;
        try {
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoRspVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名【{}】 会员下线返回码【{}】,返回消息: {}", platformName(), userAccount,
                    response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    log.info("{} 会员下线: {} 成功!!", platformName(), userAccount);
                    return ResponseVO.success();
                } else {
                    log.warn("{} 会员下线: {} 失败!!", platformName(), userAccount);
                }
            } else {
                log.warn("{} 会员下线: {} 失败!!", platformName(), userAccount);
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 会员下线发生错误!!", platformName(), userAccount), e);
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 注单拉取
     *
     * @param venueInfoVO      场馆信息
     * @param venuePullParamVO 拉单参数
     */
    @Override
    public ResponseVO getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        CQ9PullBetParams params = new CQ9PullBetParams();
        params.setPage(1);
        params.setPagesize(2000);
        long startTime = venuePullParamVO.getStartTime();
        long endTime = venuePullParamVO.getEndTime();

        params.setStarttime(TimeZoneUtils.formatDate4TimeZone(startTime,
                shanghaiTimeZone, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));
        params.setEndtime(TimeZoneUtils.formatDate4TimeZone(endTime,
                shanghaiTimeZone, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));
        return getBetRecordListByParams(venueInfoVO, params);
    }


    public ResponseVO<BigDecimal> getBalance(String userAccount, VenueInfoRspVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        String url = venueInfoVO.getApiUrl() + "/gameboy/player/balance/" + casinoMemberVO.getVenueUserAccount();

        Map<String, Object> paramMap = Maps.newHashMap();

        HttpResponse response = null;
        try {
            log.info("{} 查询余额, url: {}, 参数: {}", platformName(), url, JSON.toJSONString(paramMap));
            response = HttpRequest.get(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 获取余额返回码: {}, 返回消息: {}", platformName(), casinoMemberVO.getVenueUserAccount(),
                    response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null && CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body());
                    JSONObject dataJson = jsonObject.getJSONObject("data");

                    BigDecimal balance = dataJson.getBigDecimal("balance");
                    log.info("{} 用户名: {}, 获取余额成功, 余额: {}", platformName(), casinoMemberVO.getVenueUserAccount(), balance);
                    return ResponseVO.success(balance);
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名【%s】 获取余额发生错误!!", platformName(), casinoMemberVO.getVenueUserAccount()), e);
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    public ResponseVO transferIn(String orderId, BigDecimal turnAmount, VenueInfoRspVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        return fundTransfer(orderId, turnAmount, venueInfoVO, casinoMemberVO, CQ9TransferTypeEnums.IN);
    }


    public ResponseVO transferOut(String orderId, BigDecimal turnAmount, VenueInfoRspVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        return fundTransfer(orderId, turnAmount, venueInfoVO, casinoMemberVO, CQ9TransferTypeEnums.OUT);
    }

    private ResponseVO<Object> fundTransfer(String orderId, BigDecimal turnAmount, VenueInfoRspVO venueInfoVO, CasinoMemberVO casinoMemberVO,
                                            CQ9TransferTypeEnums transferTypeEnums) {
        Integer amount = turnAmount.intValue();

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("account", casinoMemberVO.getVenueUserAccount()); // 要转账的用户名 示例：ceshi01
        paramMap.put("amount", amount); // 金額 ※ 數值長度限制小數點前12位、小數點後4位
        paramMap.put("mtcode", orderId); // 交易代碼，格式請參考API注意事項 ※ mtcode為唯一值

        String url = venueInfoVO.getApiUrl();
        String directionStr = "";
        if (CQ9TransferTypeEnums.IN.getCode().equals(transferTypeEnums.getCode())) {
            url = url + "/gameboy/player/deposit";
            directionStr = "资金转入";
        } else {
            url = url + "/gameboy/player/withdraw";
            directionStr = "资金转出";
        }

        HttpResponse response = null;
        try {
            log.info("{} " + directionStr + " url: {}, 参数: {}", platformName(), url, JSON.toJSONString(paramMap));
            response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 用户名: {}, 金额: {}, " + directionStr + "返回码: {}, 返回消息: {}", platformName(), casinoMemberVO.getVenueUserAccount(),
                    amount, response.getStatus(), response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (baseRsp != null && baseRsp.getStatus() != null) {
                    String code = baseRsp.getStatus().getCode();
                    if (CQ9Constant.SUCC_CODE.equals(code)) {
                        log.info("{} 用户名: {}, 金额: {}, " + directionStr + " 成功!!", platformName(), casinoMemberVO.getVenueUserAccount(),
                                amount);
                        return ResponseVO.success(TransferEnums.SUCC.getCode());
                    } else if (CQ9Constant.PROGRESS_CODE.equals(code)) {
                        log.info("{} 用户名: {}, 金额: {}, " + directionStr + " 处理中!!", platformName(), casinoMemberVO.getVenueUserAccount(),
                                amount);
                        return ResponseVO.success(TransferEnums.PENDING.getCode());
                    } else {
                        log.info("{} 用户名: {}, 金额: {}, " + directionStr + " 失败!!", platformName(), casinoMemberVO.getVenueUserAccount(),
                                amount);
                        return ResponseVO.success(TransferEnums.FAIL.getCode());
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 用户名: %s " + directionStr + "发生错误!!", platformName(), casinoMemberVO.getVenueUserAccount()), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
            }
        }
        return ResponseVO.fail(ResultCode.TRANSFER_ERROR);
    }


    public JSONObject getBetRecordList(VenueInfoVO venueInfoVO, CQ9PullBetParams pullBetParams) {
        String url = venueInfoVO.getApiUrl() + "/gameboy/order/view";

        Map<String, Object> paramMap = Maps.newHashMap();
        // 查詢日期開始時間，格式為RFC3339 如 2017-06-01T00:00:00-04:00 ※時區請用UTC-4
        paramMap.put("starttime", pullBetParams.getStarttime());
        // 查詢日期結束時間，格式為RFC3339 如 2017-06-02T00:00:00-04:00 ※時區請用UTC-4
        paramMap.put("endtime", pullBetParams.getEndtime());
        paramMap.put("page", pullBetParams.getPage()); // 查询页码（默认1）
        paramMap.put("pagesize", pullBetParams.getPagesize());

        HttpResponse response = null;
        try {
            log.info("{} 获取注单列表 url: {}, 参数: {}，场馆：{}", platformName(), url, JSON.toJSONString(paramMap), venueInfoVO.getVenueCode());

            response = HttpRequest.get(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                    .timeout(30000).form(paramMap).execute();
            log.info("{} 获取注单列表返回码【{}】,返回消息: {}", platformName(), response.getStatus(),
                    response.body());

            if (response.isOk()) {
                CQ9BaseRsp baseRsp = JSON.parseObject(response.body(), CQ9BaseRsp.class);
                if (CQ9Constant.SUCC_CODE.equals(baseRsp.getStatus().getCode())) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body());

                    JSONObject infoJson = jsonObject.getJSONObject("data");

                    Long recordCount = infoJson.getLong("TotalSize");
                    JSONArray betDetails = infoJson.getJSONArray("Data");

                    if (betDetails != null && !betDetails.isEmpty()) {
                        for (int i = 0; i < betDetails.size(); i++) {
                            JSONObject betDetail = betDetails.getJSONObject(i);

                            CQ9BetDetailEntity detailEntity = JSON.parseObject(betDetail.toJSONString(), CQ9BetDetailEntity.class);
                            log.info("betDetail: {}", JSON.toJSONString(detailEntity));
                        }
                    }
                    return infoJson;
                }
                // 数据未找到
                if (CQ9Constant.ERROR_CODE_DATA_NOT_FOUND.equals(baseRsp.getStatus().getCode())) {
                    return new JSONObject();
                }
            }
        } catch (Exception e) {
            log.error(String.format("%s 获取注单列表发生错误!!", platformName()), e);
            throw new BaowangDefaultException(String.format("%s 获取注单异常", venueInfoVO.getVenueCode()));
        }
        return null;
    }


    public ResponseVO<CQ9PullBetParams> getBetRecordListByParams(VenueInfoVO venueInfoVO, CQ9PullBetParams params) {
        // 拉取指定时间的注单，死循环是查看拉单参数拉完数据
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        for (; ; ) {
            List<OrderRecordVO> orderList;
            try {
                JSONObject infoJson = getBetRecordList(venueInfoVO, params);
                if (Objects.isNull(infoJson)) {
                    //return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                    break;
                }

                // 总记录数量
                Long recordCount = infoJson.getLong("TotalSize");
                if (Objects.isNull(recordCount)) {
                    break;
                }
                // 投注明细列表
                JSONArray betDetails = infoJson.getJSONArray("Data");

                // 解析注单数据
                orderList = parseOrder(venueInfoVO, betDetails, siteVOMap);
                log.info("{} 拉取注单参数: {} | {}, page: {}, size: {}, 本次拉取数量: {}, 总记录数: {}", platformName(), params.getStarttime(), params.getEndtime(),
                        params.getPage(), params.getPagesize(), betDetails.size(), recordCount);

                if (CollectionUtils.isNotEmpty(orderList)) {
                    orderRecordProcessService.orderProcess(orderList);
                }
            } catch (Exception e) {
                log.error(String.format("%s 获取注单列表发生错误!! %s", platformName(), JSON.toJSONString(params)), e);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            params.setPage(params.getPage() + 1);

            // 拉取的数据为空时, 终止本次拉取
            if (CollectionUtils.isEmpty(orderList)) {
                break;
            }
        }

        // 生成下一次拉单参数
        CQ9PullBetParams pullBetParams = nextPullBetParams(params);
        // 判断时间是否是现在，如果是现在，则不执行本身，如果不是现在，则执行本身

        return ResponseVO.success(pullBetParams);
    }

    /**
     * 解析注单数据
     *
     * @param venueInfoVO 商户信息
     * @param betDetails  注单详情
     * @return 解析的注单
     */
    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, JSONArray betDetails, Map<String, SiteVO> siteVOMap) {
        // 获取VIP返水配置
        //Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueInfoVO.getVenueCode());

        // 三方游戏编码与名称的映射关系
        //  game 查询;
        // Map<gameCode, GameInfoPO>
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());

        List<CQ9BetDetailEntity> betDetailEntityList = Lists.newArrayList();
        if (betDetails != null && !betDetails.isEmpty()) {
            for (int i = 0; i < betDetails.size(); i++) {
                JSONObject betDetail = betDetails.getJSONObject(i);
                try {

                    CQ9BetDetailEntity betDetailEntity = JSONObject.parseObject(betDetail.toJSONString(), CQ9BetDetailEntity.class);

                    // 游戏結束時間，格式為 RFC3339
                    betDetailEntity.setEndroundtime(TimeZoneUtils.convertTimeZone2Str(betDetailEntity.getEndroundtime(),
                            checkTimePatten(betDetailEntity.getEndroundtime()) ? DateUtils.PATTEN_EASTERN_TIME : DateUtils.PATTEN_EASTERN_TIME2,
                            gmt4TimeZone, shanghaiTimeZone));
                    // 系統結算時間, 注單結算時間及報表結算時間都是createtime | 當筆資料建立時間，格式為 RFC3339
                    betDetailEntity.setCreatetime(TimeZoneUtils.convertTimeZone2Str(betDetailEntity.getCreatetime(),
                            checkTimePatten(betDetailEntity.getCreatetime()) ? DateUtils.PATTEN_EASTERN_TIME : DateUtils.PATTEN_EASTERN_TIME2,
                            gmt4TimeZone, shanghaiTimeZone));
                    // 下注時間，格式為 RFC3339
                    betDetailEntity.setBettime(TimeZoneUtils.convertTimeZone2Str(betDetailEntity.getBettime(),
                            checkTimePatten(betDetailEntity.getBettime()) ? DateUtils.PATTEN_EASTERN_TIME : DateUtils.PATTEN_EASTERN_TIME2,
                            gmt4TimeZone, shanghaiTimeZone));

                    betDetailEntity.setOriginalBetDetail(JSON.toJSONString(betDetail));
                    betDetailEntityList.add(betDetailEntity);
                } catch (Exception e) {
                    log.error("{} 解析注单时间适配异常:{}", platformName(), betDetail);
                }
            }
        }

        // 场馆用户关联信息
        List<String> venueUserList = betDetailEntityList.stream().map(CQ9BetDetailEntity::getAccount).distinct().toList();
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder()
                .venuePlatform(venueInfoVO.getVenuePlatform())
                .venueUserAccountList(venueUserList).build();
        // key:游戏账号 Utest_44905163
        Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberService.getCasinoMemberMap(casinoMemberReq);
        if (MapUtil.isEmpty(casinoMemberMap)) {
            log.info("{} 未找到三方关联信息", venueInfoVO.getVenueCode());
            return null;
        }
        // 用户信息 userIds
        List<String> userList = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();

        List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoByUserIdsList(UserAccountListVO.builder().accountList(userList).build());
        if (CollUtil.isEmpty(userInfoVOS)) {
            log.info("{} 未找到用户信息", venueInfoVO.getVenueCode());
            return null;
        }
        // key: userId
        Map<String, UserInfoVO> userMap = userInfoVOS.stream().collect(Collectors.toMap(UserInfoVO::getUserId, p -> p, (k1, k2) -> k2));

        // 用户登录信息
        List<UserLoginInfoVO> loginInfoList = userInfoApi.getLatestLoginInfoByUserIds(userList);
        // key:userId
        Map<String, UserLoginInfoVO> loginVOMap = Maps.newHashMap();
        if (CollUtil.isNotEmpty(loginInfoList)) {
            loginVOMap = loginInfoList.stream().collect(Collectors.toMap(UserLoginInfoVO::getUserId, p -> p, (k1, k2) -> k2));
        }
        // 游戏大类
        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.CQ9.getVenueCode());
        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        for (CQ9BetDetailEntity detailEntity : betDetailEntityList) {
            String account = detailEntity.getAccount();

            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(account);
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), account);
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号不存在", venueInfoVO.getVenueCode());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            String betIp = userLoginInfoVO.getIp();
            Integer deviceType = Strings.isNotBlank(userLoginInfoVO.getLoginTerminal()) ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

            OrderRecordVO orderRecordVO = new OrderRecordVO();
            // 会员账号
            orderRecordVO.setUserAccount(userInfoVO.getUserAccount());
            // 会员姓名
            orderRecordVO.setUserName(userInfoVO.getUserName());
            orderRecordVO.setUserId(userInfoVO.getUserId());
            orderRecordVO.setSiteCode(userInfoVO.getSiteCode());
            //读取站点名称
            String siteName = null;
            if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
                SiteVO siteVO = siteVOMap.get(userInfoVO.getSiteCode());
                siteName = siteVO.getSiteName();
            }
            orderRecordVO.setSiteName(siteName);

            // 账号类型 1测试 2正式 3商务 4置换
            orderRecordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecordVO.setAgentId(userInfoVO.getSuperAgentId());
            orderRecordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            // 三方会员账号
            orderRecordVO.setCasinoUserName(account);
            // 上级代理账号
            orderRecordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecordVO.setAgentId(userInfoVO.getSuperAgentId());
            // VIP等级
            orderRecordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecordVO.setVipRank(userInfoVO.getVipRank());
            // 三方平台
            orderRecordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
            // 游戏平台名称
            //orderRecordVO.setVenueName(venueInfoVO.getVenueName());
            // 游戏平台CODE
            orderRecordVO.setVenueCode(venueInfoVO.getVenueCode());

            // 游戏大类
            orderRecordVO.setVenueType(gameTypeId);

            String gameType = detailEntity.getGametype();
            // 游戏大类
            orderRecordVO.setRoomType(detailEntity.getGametype());
            orderRecordVO.setRoomTypeName(CQ9GameTypeEnums.nameOfCode(gameType));
            // 游戏小类名称
            GameInfoPO gameInfoPO = paramToGameInfo.get(detailEntity.getGamecode());
            if (gameInfoPO != null) {
                orderRecordVO.setGameId(gameInfoPO.getGameId());
                orderRecordVO.setGameName(gameInfoPO.getGameI18nCode());
            }
            // 游戏名称-添加
            orderRecordVO.setThirdGameCode(detailEntity.getGamecode());
            // 游戏Code
            orderRecordVO.setGameCode(detailEntity.getGamecode());
            // 房间类型 roomType
            // 房间类型名称 roomTypeName
            // 玩法类型(电子类玩法就是结果)
            orderRecordVO.setPlayType(detailEntity.getGameresult());
            // 下注内容
            //orderRecordVO.setBetContent(detailEntity.getContent());

            // 投注时间
            orderRecordVO.setBetTime(TimeZoneUtils.parseDate4TimeZoneToTime(detailEntity.getBettime(), checkTimePatten(detailEntity.getBettime()) ? DateUtils.PATTEN_EASTERN_TIME : DateUtils.PATTEN_EASTERN_TIME2, shanghaiTimeZone));
            // 结算时间
            orderRecordVO.setSettleTime(TimeZoneUtils.parseDate4TimeZoneToTime(detailEntity.getCreatetime(), checkTimePatten(detailEntity.getCreatetime()) ? DateUtils.PATTEN_EASTERN_TIME : DateUtils.PATTEN_EASTERN_TIME2, shanghaiTimeZone));

            // 变更状态
            orderRecordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());

            // 投注额
            orderRecordVO.setBetAmount(detailEntity.getBet());
            // 已结算注单
            BigDecimal validBetAmount = BigDecimal.ZERO;
            if (orderRecordVO.getSettleTime() != null) {
                // 输赢金额
                orderRecordVO.setWinLossAmount(detailEntity.getWin() == null ? null : detailEntity.getWin());
                // 老虎機/街機/魚機 win包含本金，需要扣除bet | 真人視訊/牌桌類   win不包含本金
                List<String> gameTypeList = Lists.newArrayList(CQ9GameTypeEnums.SLOT.getCode(), CQ9GameTypeEnums.ARCADE.getCode(),
                        CQ9GameTypeEnums.FISH.getCode());
                if (orderRecordVO.getWinLossAmount() != null) {
                    if (gameTypeList.contains(detailEntity.getGametype())) {
                        orderRecordVO.setWinLossAmount(orderRecordVO.getWinLossAmount().subtract(orderRecordVO.getBetAmount()));
                    } else {
                        orderRecordVO.setWinLossAmount(orderRecordVO.getWinLossAmount()
                                .subtract(detailEntity.getRake()) // 抽水金額
                                .subtract(detailEntity.getRoomfee()) // 開房費用
                        );
                    }
                }

                // 有效投注
                validBetAmount = detailEntity.getValidbet() == null ? null : detailEntity.getValidbet();
                orderRecordVO.setValidAmount(validBetAmount);
                // Slot /Fish /Arcade 請直接使用bet的值 | Live /Table 請使用validbet
                if (gameTypeList.contains(detailEntity.getGametype())) {
                    validBetAmount = detailEntity.getBet();
                    orderRecordVO.setValidAmount(validBetAmount);
                }

                // 派彩金额=输赢金额+投注金额
                orderRecordVO.setPayoutAmount(orderRecordVO.getWinLossAmount().add(orderRecordVO.getBetAmount()));
            }

            // 注单ID
            orderRecordVO.setOrderId(OrderUtil.getGameNo());
            // 三方注单ID
            orderRecordVO.setThirdOrderId(String.valueOf(detailEntity.getRound()));
            orderRecordVO.setTransactionId(String.valueOf(detailEntity.getRound()));
            // 注单状态 CQ9回复，只会返回已经结算的注单
            orderRecordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());

            // 注单归类
            orderRecordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(orderRecordVO.getOrderStatus()));
            // 赔率
//            orderRecordVO.setOdds(detailEntity.getOdds());
            // 局号/期号
            orderRecordVO.setGameNo(String.valueOf(detailEntity.getRoundnumber()));
            // 桌号 deskNo
            orderRecordVO.setDeskNo(detailEntity.getTableid());
            // 靴号 bootNo
            // 结果牌 / 结果 resultList
            if (ObjectUtil.isNotEmpty(orderRecordVO.getWinLossAmount())) {
                orderRecordVO.setResultList(getWinLossResult(orderRecordVO.getWinLossAmount()));
            }

            // 投注IP
            orderRecordVO.setBetIp(betIp);
            // 币种
            orderRecordVO.setCurrency(userInfoVO.getMainCurrency());
            // 设备类型
            orderRecordVO.setDeviceType(deviceType);
            // 体育、电竞、彩票, 需要保存原始注单到串关字段中
            orderRecordVO.setParlayInfo(detailEntity.getOriginalBetDetail());
            // wade 确认没有返水


            /*if (!rebateMap.isEmpty() && ClassifyEnum.SETTLED.getCode().equals(orderRecordVO.getOrderClassify())) {
                Map<String, BigDecimal> resultRate = ThirdUtil.getRebate(userInfoVO, orderRecordVO, rebateMap, validBetAmount);
                // 返回比例
                orderRecordVO.setRebateRate(resultRate.get("rebate"));
                // 返回金额
                orderRecordVO.setRebateAmount(resultRate.get("scale"));
            }*/
            orderRecordList.add(orderRecordVO);
        }
        return orderRecordList;
    }

    /**
     * 根据盈亏金额返回对应的输赢结果。
     *
     * @param winLossAmount 盈亏金额，负数表示亏损，正数表示盈利，零表示平局
     * @return 对应的输赢结果字符串：
     * - 若金额小于 0，则返回 {@code WinLossEnum.LOSS.getName()}
     * - 若金额大于 0，则返回 {@code WinLossEnum.WIN.getName()}
     * - 若金额等于 0，则返回 {@code WinLossEnum.TIE.getName()}
     */
    private String getWinLossResult(BigDecimal winLossAmount) {
        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
                : WinLossEnum.TIE.getName();

    }

    /**
     * 是否包含毫秒值格式,需要用不同的patten解析
     *
     * @param time 参数
     * @return 检查结果
     */
    private boolean checkTimePatten(String time) {
        if (StringUtils.isBlank(time)) {
            return false;
        }
        return time.contains(".");
    }

    /**
     * 生成下一次拉单参数
     *
     * @param params 拉单参数对象
     * @return 参数
     */
    public CQ9PullBetParams nextPullBetParams(CQ9PullBetParams params) {
        CQ9PullBetParams newPullBetParams = new CQ9PullBetParams();
        newPullBetParams.setStarttime(params.getStarttime());
        newPullBetParams.setEndtime(params.getEndtime());
        newPullBetParams.setPagesize(params.getPagesize());
        newPullBetParams.setPage(1); // 每次从第1页开始

        Integer timeInterval = params.getTimeInterval() == null ? CQ9Constant.DEFAULT_TIME_INTERVAL : params.getTimeInterval();
        newPullBetParams.setTimeInterval(timeInterval);

        // 美东当前时间
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt4ZoneId);

        // 当前查询区间的起始时间
        Date startAtTime = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getStarttime(), gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);
        // 当前时间减xx分钟的时间
        DateTime beforeTenMinutes = new DateTime(canadaCurrentDate).plusMillis(timeInterval * -1);

        // 如果起始时间 > (当前时间 - 10分钟), 则重新设置起始时间
        assert startAtTime != null;
        if (beforeTenMinutes.isBefore(startAtTime.getTime())) {
            // 开始时间设置（当前时间-10）
            newPullBetParams.setStarttime(TimeZoneUtils.formatDate4TimeZone(beforeTenMinutes.toDate(), gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));
            // 结束时间设置当前时间
            newPullBetParams.setEndtime(TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));
        } else {
            Date endAt = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getEndtime(), gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);
            // 起始时间 = 结束时间 (endAt)
            String starAtStr = TimeZoneUtils.formatDate4TimeZone(endAt, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);
            // 结束时间 = 结束时间 (endAt) + timeInterval（例如 10 分钟）
            String endAtStr = TimeZoneUtils.formatDate4TimeZone(new DateTime(endAt).plusMillis(timeInterval).toDate(), gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);

            newPullBetParams.setStarttime(starAtStr);
            newPullBetParams.setEndtime(endAtStr);
        }
        log.info("{} 下一次拉单参数: {} | {}, page: {}, size: {}", platformName(), newPullBetParams.getStarttime(), newPullBetParams.getEndtime(),
                newPullBetParams.getPage(), newPullBetParams.getPagesize());
        return newPullBetParams;
    }

    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.CQ9.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);

        JSONObject jsonObject = listAllCQ9Game(venueInfoVO);
        JSONObject status = jsonObject.getJSONObject("status");
        String langCode = CurrReqUtils.getLanguage();
        if (status != null && CQ9Constant.SUCC_CODE.equals(status.getString("code"))) {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray != null && !jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String gameCode = json.getString("gamecode"); //游戏代碼
                    // 设置语言
                    String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");
                    String gameName = getLangName(json, "nameset", lang);

                    ShDeskInfoVO infoVO = ShDeskInfoVO.builder().deskName(gameName).deskNumber(gameCode).build();
                    resultList.add(infoVO);
                }
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), resultList.size());
            return resultList;
        }
        return resultList;
    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        JSONObject jsonObject = listAllCQ9Game(venueInfoVO);
        JSONObject status = jsonObject.getJSONObject("status");
        String langCode = CurrReqUtils.getLanguage();
        if (status != null && CQ9Constant.SUCC_CODE.equals(status.getString("code"))) {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray != null && !jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String gameCode = json.getString("gamecode"); //游戏代碼
                    // 设置语言
                    String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");
                    String gameName = getLangName(json, "nameset", lang);
                    JSONObject gameJson = new JSONObject();
                    gameJson.put("deskName", gameName);
                    gameJson.put("deskNumber", gameCode);
                    resultList.add(gameJson);
                }
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), resultList.size());
            return ResponseVO.success(resultList);
        }
        return ResponseVO.success(resultList);

    }

    private JSONObject listAllCQ9Game(VenueInfoVO venueInfoVO) {
        String url = venueInfoVO.getApiUrl() + "/gameboy/game/list/cq9";
        Map<String, Object> paramMap = Maps.newHashMap();
        log.info("{} CQ9游戏列表, url: {}, 参数: {}", platformName(), url, JSON.toJSONString(paramMap));
        try (HttpResponse response = HttpRequest.get(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                .timeout(30000).form(paramMap).execute()) {
            log.info("{} CQ9获取游戏列表返回码: {}, 返回消息: {}", platformName(), response.getStatus(), response.body());

            if (response.isOk()) {
                return JSON.parseObject(response.body());
            }
        } catch (Exception e) {
            log.error(String.format("%s 获取游戏列表发生错误!!", platformName()), e);
        }
        return new JSONObject();
    }

    @Override
    public List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueInfoVO) {
        List<ThirdGameInfoVO> ret = Lists.newArrayList();
        JSONObject jsonObject = listAllCQ9Game(venueInfoVO);
        JSONObject status = jsonObject.getJSONObject("status");
        if (status != null && CQ9Constant.SUCC_CODE.equals(status.getIntValue("code"))) {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //List<ThirdGameInfoPO> thirdGameInfoList = Lists.newArrayList();
            if (jsonArray != null && !jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    ThirdGameInfoVO thirdGameInfoVO = new ThirdGameInfoVO();
                    JSONObject json = jsonArray.getJSONObject(i);
                    String gameCode = json.getString("gamecode"); //游戏代碼
                    // 设置语言
                    //String lang = LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(), "en");
                    String gameNameCN = getLangName(json, "nameset", "zh-cn");
                    thirdGameInfoVO.setGameCode(gameCode);
                    // 名称多语言
                    List<GameNameVO> gameNameVOS = Lists.newArrayList();
                    GameNameVO gameNameVO = new GameNameVO();
                    gameNameVO.setGameName(gameNameCN);
                    gameNameVO.setLang(LanguageEnum.ZH_CN.getLang());
                    gameNameVOS.add(gameNameVO);
                    thirdGameInfoVO.setGameName(gameNameVOS);
                    ret.add(thirdGameInfoVO);
                }
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), ret.size());
            return ret;
        }
        return ret;
    }

    /**
     * 获取指定语言的属性名称，如果找不到对应语言的名称，则返回英语（"en"）的名称
     *
     * @param betDetail 包含多语言名称的 JSON 对象
     * @param attr      需要查找的属性名，该属性对应的值是一个 JSON 数组
     * @param defLang   目标语言代码，例如 "zh"（中文）、"en"（英语）等
     * @return 返回指定语言（defLang）的名称，如果找不到，则返回英语（"en"）的名称，如果都找不到，则返回空字符串
     */
    private String getLangName(JSONObject betDetail, String attr, String defLang) {
        String name = "";
        JSONArray jsonArray = betDetail.getJSONArray(attr);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject langJson = jsonArray.getJSONObject(j);
                String tmpName = langJson.getString("name");
                String lang = langJson.getString("lang");
                if ("en".equals(lang)) {
                    name = tmpName;
                }
                if (defLang.equals(lang)) {
                    name = tmpName;
                    break;
                }
            }
        }
        return name;
    }

    public VenueEnum platformEnum() {
        return VenueEnum.CQ9;
    }

    public String platformName() {
        return platformEnum().getVenueName();
    }


    public CQ9BaseRsp<Boolean> checkPlayer(String account, String token) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(token)) {
            log.error("{}:校验玩家账号参数缺少,account:{},token:{}", VenueEnum.CQ9.getVenueName(), account, token);
            return CQ9Utils.checkFailStatusCheck(CQ9ResultCodeEnums.SUCCESS, false);
        }

        String userId = getUserAccount(account);

        // 查询游戏用户
        if (StringUtils.isBlank(userId)) {
            log.error("{}:校验玩家账号参数失败,account:{},token:{}", VenueEnum.CQ9.getVenueName(), account, token);
            return CQ9Utils.checkFailStatusCheck(CQ9ResultCodeEnums.SUCCESS, false);
        }
        UserInfoVO byUserId = getByUserId(userId);
        if (byUserId == null) {
            log.error("{}:校验玩家账号参数失败,account:{},token:{}", VenueEnum.CQ9.getVenueName(), account, token);
            return CQ9Utils.checkFailStatusCheck(CQ9ResultCodeEnums.SUCCESS, false);
        }
        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(byUserId.getSiteCode(), VenueEnum.CQ9.getVenueCode(), byUserId.getMainCurrency());
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.CQ9.getVenueCode());
            return CQ9Utils.checkFailStatus(CQ9ResultCodeEnums.CQ9_GAMING_UNDER_MAINTENANCE, false);
        }
        // 校验入参
        if (StringUtils.equals(token, venueInfoVO.getAesKey())) {
            return CQ9Utils.getUserSuccessStatus();
        }
        return CQ9Utils.checkFailStatus(CQ9ResultCodeEnums.BAD_PARAMETERS, false);
    }

    /**
     * @param account 游戏账号
     * @return 返回改场馆账号
     */
    private CasinoMemberPO getCasinoMemberPO(String account) {
        LambdaQueryWrapper<CasinoMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CasinoMemberPO::getVenueUserAccount, account)
                .eq(CasinoMemberPO::getVenueCode, VenueEnum.CQ9.getVenueCode())
                .last(" LIMIT 1 ");
        return casinoMemberService.getBaseMapper().selectOne(queryWrapper);
    }

    public CQ9BaseRsp<CQ9BalanceRsp> balance(String account, String gamecode, String token) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(token)) {
            log.error("{}:校验玩家账号参数缺少,account:{},token:{}", VenueEnum.CQ9.getVenueName(), account, token);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.BAD_PARAMETERS, CQ9BalanceRsp.builder().build());
        }
        // 查询游戏用户
        // 校验入参
        String userId = getUserAccount(account);
        // 查询游戏用户
        if (StringUtils.isBlank(userId)) {
            log.error("{}:校验玩家账号参数失败,account:{},token:{}", VenueEnum.CQ9.getVenueName(), account, token);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_DISABLED, CQ9BalanceRsp.builder().build());
        }

        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode(), userInfoVO.getMainCurrency());
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.CQ9.getVenueCode());
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.GAME_UNDER_MAINTENANCE, CQ9BalanceRsp.builder().build());
        }


        String userCurrencyCode = userInfoVO.getMainCurrency();
        CQ9CurrencyEnum cq9CurrencyEnum = CQ9CurrencyEnum.byPlatCurrencyCode(userCurrencyCode);

        if (cq9CurrencyEnum == null || cq9CurrencyEnum.getPlatCurrencyCode() == null) {
            log.error("币种:{} 没有映射成功", VenueEnum.CQ9.getVenueCode());
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.CURRENCY_NOT_SUPPORTED, CQ9BalanceRsp.builder().build());
        }
        //
        // 判断游戏是否配置
        // 判断游戏是否配置
        if (StringUtils.isNotBlank(gamecode)) {
            if (!isGameAvailable(userInfoVO.getSiteCode(), gamecode, venueInfoVO.getVenueCode())) {
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.GAME_UNDER_MAINTENANCE, CQ9BalanceRsp.builder().build());
            }
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNull(userCenterCoin)) {
            return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(BigDecimal.ZERO).build());
        }
        if (userCenterCoin.getTotalAmount() == null) {
            return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(BigDecimal.ZERO).build());
        }

        return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                .currency(cq9CurrencyEnum.getCode())
                .balance(userCenterCoin.getTotalAmount()).build());


    }

    @DistributedLock(name = RedisConstants.CQ9_COIN_LOCK, unique = "#req.account", waitTime = 3, leaseTime = 180)
    public CQ9BaseRsp bet(CQ9BetReq req) {

        // 记录
        return handleBalanceChange(req);
    }

    private CQ9BaseRsp<CQ9BalanceRsp> handleBalanceChange(CQ9BetReq req) {
        // 参数校验
        if (StringUtils.isBlank(req.getAccount())
                || StringUtils.isBlank(req.getEventTime())
                || StringUtils.isBlank(req.getGamehall())
                || StringUtils.isBlank(req.getRoundid())
                // 如果是takeAll 不校验金额
                || (ObjectUtil.isEmpty(req.getAmount()) && !ObjectUtil.equal(req.getCallType(), CQ9CallEnums.TAKEALL.getCode()))
                || ObjectUtil.isEmpty(req.getGamecode())
                || ObjectUtil.isEmpty(req.getMtcode())) {
            log.info("参数校验失败");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());

        }

        // he evenTime parameter has wrong time foramat, should return error code 1004
        if (!req.isRFC3339TimeFormat(req.getEventTime())) {
            log.info("下注校验:时间格式错误");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1004, CQ9BalanceRsp.builder().build());
        }
        // bet 下注校验
        if (StringUtils.equals(req.getCallType(), CQ9CallEnums.BET.getCode())
                || StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.ENDROUND.getCode())
                || StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.ROLLOUT.getCode())
                || StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.ROLLIN.getCode())
                || StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.CREDIT.getCode())
                || StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.DEBIT.getCode())) {
            BigDecimal amount = req.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                log.info("下注校验:金额必须大于等于0");
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            }
            //Bet - Insufficient Balance, should return error code 1005
        }

        String userId = getUserAccount(req.getAccount());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());

        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
        }

        List<CQ9TransactionRecordPO> cq9TransactionRecordPOS = getCq9Records(req.getRoundid(), req.getBalanceType());

        CQ9TransactionRecordPO recordPO = new CQ9TransactionRecordPO();
        recordPO.setAccount(req.getAccount());
        String timeNow = CQ9Utils.getNowTimeRF33();
        recordPO.setCreateTime(timeNow);
        recordPO.setMtcode(req.getMtcode());
        recordPO.setAmount(req.getAmount());
        recordPO.setEventTime(req.getEventTime());
        recordPO.setAction(req.getCallType());
        recordPO.setRequestJson(JSON.toJSONString(req));
        recordPO.setRoundId(req.getRoundid());
        recordPO.setBalanceType(req.getBalanceType());
        LambdaQueryWrapper<CQ9TransactionRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CQ9TransactionRecordPO::getMtcode, req.getMtcode());
        queryWrapper.eq(CQ9TransactionRecordPO::getStatusCode, "0");
        queryWrapper.eq(CQ9TransactionRecordPO::getAction, req.getCallType());
        List<CQ9TransactionRecordPO> cqRecordPOS = transactionRecordRepository.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(cqRecordPOS)) {
            log.info("{}:交易订单号重复,req:{}", VenueEnum.CQ9.getVenueName(), req);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.MTCODE_DUPLICATE_2009, CQ9BalanceRsp.builder().build());
        }
        // 校验
        transactionRecordRepository.insert(recordPO);
        if (CollUtil.isNotEmpty(cq9TransactionRecordPOS)) {
            int num = cq9TransactionRecordPOS.size();
            req.setRoundid(req.getRoundid() + "_" + num);
        }
        CQ9BaseRsp<CQ9BalanceRsp> cq9BaseRsp = balanceChange(req, userInfoVO);
        if (StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.TAKEALL.getCode())) {
            updateRecord(cq9BaseRsp, recordPO.getId(), true);
        } else {
            updateRecord(cq9BaseRsp, recordPO.getId(), false);
        }

        return cq9BaseRsp;
    }

    /**
     * 获取指定 roundId 的 CQ9 交易记录列表。
     *
     * @param roundId     交易代码，用于查询对应的交易记录
     * @param balanceType 收支类型1收入,2支出
     * @return 符合条件的 CQ9 交易记录列表
     */
    private List<CQ9TransactionRecordPO> getCq9Records(String roundId, String balanceType) {
        LambdaQueryWrapper<CQ9TransactionRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(ObjectUtil.isNotEmpty(roundId), CQ9TransactionRecordPO::getRoundId, roundId);
        queryWrapper.eq(CQ9TransactionRecordPO::getBalanceType, balanceType);
        queryWrapper.eq(CQ9TransactionRecordPO::getStatusCode, "0");
        return transactionRecordRepository.selectList(queryWrapper);
    }

    /**
     * @param cq9BaseRsp f返回结果更新
     * @param id         逐渐
     * @param flag       只有refund 才是true
     */
    private void updateRecord(CQ9BaseRsp<CQ9BalanceRsp> cq9BaseRsp, String id, boolean flag) {
        // update
        LambdaUpdateWrapper<CQ9TransactionRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        CQ9StatusRsp status = cq9BaseRsp.getStatus();
        String endNow = CQ9Utils.getNowTimeRF33();
        updateWrapper.set(CQ9TransactionRecordPO::getEndTime, endNow);
        updateWrapper
                .set(CQ9TransactionRecordPO::getEndTime, endNow) //
                .set(CQ9TransactionRecordPO::getResponseTime, endNow)
                .set(CQ9TransactionRecordPO::getStatusCode, status.getCode())
                .set(CQ9TransactionRecordPO::getStatusMessage, status.getMessage())
                .eq(CQ9TransactionRecordPO::getId, id);
        if (flag) {
            updateWrapper.set(CQ9TransactionRecordPO::getAmount, cq9BaseRsp.getData().getAmount().subtract(cq9BaseRsp.getData().getBalance()).abs());
        }
        if (StringUtils.endsWithIgnoreCase(status.getMessage(), "Success")) {
            updateWrapper.set(CQ9TransactionRecordPO::getStatus, "success");
        } else {
            updateWrapper.set(CQ9TransactionRecordPO::getStatus, "1014");
        }


        CQ9BalanceRsp data = cq9BaseRsp.getData();
        if (data != null) {
            updateWrapper
                    .set(CQ9TransactionRecordPO::getBalance, data.getBalance())
                    .set(CQ9TransactionRecordPO::getBefore, data.getAmount())
                    .set(CQ9TransactionRecordPO::getCurrency, data.getCurrency());
        }

        transactionRecordRepository.update(null, updateWrapper);
    }

    /**
     * @param cq9BaseRsp 返回结果更新
     */
    private void updateRecordForPayoff(CQ9BaseRsp<CQ9BalanceRsp> cq9BaseRsp, List<String> mtcodeList, BigDecimal beforeAmount) {
        // update
        LambdaUpdateWrapper<CQ9TransactionRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        String endNow = CQ9Utils.getNowTimeRF33();
        String codes = String.join(",", mtcodeList);
        updateWrapper
                .set(CQ9TransactionRecordPO::getEndTime, endNow) //
                .set(CQ9TransactionRecordPO::getResponseTime, endNow)
                .set(CQ9TransactionRecordPO::getRoundArray, codes)
                .in(CQ9TransactionRecordPO::getMtcode, mtcodeList);


        CQ9BalanceRsp data = cq9BaseRsp.getData();
        if (data != null) {
            updateWrapper
                    .set(CQ9TransactionRecordPO::getBalance, data.getBalance())
                    .set(CQ9TransactionRecordPO::getBefore, beforeAmount);
        }

        transactionRecordRepository.update(null, updateWrapper);
    }


    /**
     * 返回当前美东时间
     *
     * @return time
     */
    private String getNowTime() {
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt4ZoneId);
        return TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);
    }

    /*public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(getNowTimeRF33());
    }*/


    private String getUserAccount(String account) {
        String userAccount = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userAccount)) {
            return null;
        }
        return userAccount;
    }

    /**
     * 组装备注信息
     *
     * @param gamehall 游戏大厅名称，可为空
     * @param gamecode 游戏代码，可为空
     * @param remark   备注信息，可为空
     * @return 组装后的备注字符串，多个参数之间以空格分隔
     */
    private String assembleRemark(String gamehall, String gamecode, String remark) {
        StringBuilder sb = new StringBuilder();

        // 如果游戏大厅不为空，则追加
        if (StringUtils.isNotBlank(gamehall)) {
            sb.append(gamehall).append(" ");
        }

        // 如果游戏代码不为空，则追加
        if (StringUtils.isNotBlank(gamecode)) {
            sb.append(gamecode).append(" ");
        }

        // 如果备注信息不为空，则追加
        if (StringUtils.isNotBlank(remark)) {
            sb.append(remark);
        }

        // 去除末尾可能多余的空格并返回
        return sb.toString().trim();
    }


    /**
     * 账变
     *
     * @param req 参数
     *            type true=扣款,false = 加款
     */
    private CQ9BaseRsp<CQ9BalanceRsp> balanceChange(CQ9BetReq req, UserInfoVO userInfoVO) {
//        WalletEnum.CoinTypeEnum type = req.getType();


        if (!req.valid()) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.BAD_PARAMETERS, CQ9BalanceRsp.builder().build());
        }


        String userId = userInfoVO.getUserId();
       /* if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());

        }*/

        // UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        /*if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
        }*/

        if (req.getActionFlag() &&
                !playVenueInfoApi.getSiteVenueIdsBySiteCodeAndByVenueCode(userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode()).getData()) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode());
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_DISABLED, CQ9BalanceRsp.builder().build());
        }
        //
        if (req.getActionFlag() && (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())
                || userInfoVO.getAccountStatus().contains(UserStatusEnum.LOGIN_LOCK.getCode()))) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_DISABLED, CQ9BalanceRsp.builder().build());
        }
        // 校验钱包token ,aeskey,是钱包的密钥
        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode(), userInfoVO.getMainCurrency());
        if (venueInfoVO == null || !StringUtils.equals(venueInfoVO.getAesKey(), req.getWtoken())) {
            log.info("{}:参数mtoken校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TOKEN_INVALID, CQ9BalanceRsp.builder().build());

        }
        CQ9CurrencyEnum cq9CurrencyEnum = CQ9CurrencyEnum.byPlatCurrencyCode(userInfoVO.getMainCurrency());
        if (cq9CurrencyEnum == null) {
            log.info("{}:币种异常", VenueEnum.CQ9.getVenueName());
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.CURRENCY_NOT_SUPPORTED, CQ9BalanceRsp.builder().build());
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(BigDecimal.ZERO).build());
        }
        if (req.getActionFlag() && StringUtils.isNotBlank(req.getGamecode())) {
            // 判断游戏是否配置
            if (!isGameAvailable(userInfoVO.getSiteCode(), req.getGamecode(), venueInfoVO.getVenueCode())) {
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.GAME_UNDER_MAINTENANCE, CQ9BalanceRsp.builder().build());
            }

        }
        // 扣除之前的金额 可用余额
        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        BigDecimal tradeAmount = req.getAmount();//账变金额
        userCoinAddVO.setOrderNo(req.getRoundid());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        // CQ9帐变都使用 mtcode
        userCoinAddVO.setRemark(req.getMtcode());
        userCoinAddVO.setThirdOrderNo(req.getMtcode());
        /*if (StringUtils.isNotBlank(req.getRemark())) {
            userCoinAddVO.setRemark(assembleRemark(req.getGamehall(), req.getGamecode(), req.getRemark()));
        }*/
        userCoinAddVO.setVenueCode(VenuePlatformConstants.CQ9);
        //下注 bet/rollout
        if (req.getCallType().equals(CQ9CallEnums.ROLLOUT.getCode())
                || req.getCallType().equals(CQ9CallEnums.BET.getCode())) {
            if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}:用户钱包,余额不足:{},用户金额:{},扣款金额:{}", VenueEnum.CQ9.getVenueName(), userId, userTotalAmount, tradeAmount);
                //throw new CQ9DefaultException(CQ9ResultCodeEnums.INSUFFICIENT_BALANCE, req.getAccount());
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1005, CQ9BalanceRsp.builder().build());
            }
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
            //payoff/rollin/endround
        } else if (req.getCallType().equals(CQ9CallEnums.PAYOFF.getCode())
                || req.getCallType().equals(CQ9CallEnums.ROLLIN.getCode())
                || req.getCallType().equals(CQ9CallEnums.ENDROUND.getCode())) {
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
            // 其他减少对应takeall
        } else if (req.getCallType().equals(CQ9CallEnums.TAKEALL.getCode())) {
            // 全部转账 对应takeall
            if (req.getTakeAllFlag()) {
                if (userTotalAmount.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
                    log.info("{}:用户钱包,余额为0:{},用户金额:{},扣款金额:{}", VenueEnum.CQ9.getVenueName(), userId, userTotalAmount, tradeAmount);
                    //throw new CQ9DefaultException(CQ9ResultCodeEnums.INSUFFICIENT_BALANCE, req.getAccount());
                    return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1005, CQ9BalanceRsp.builder().build());
                }

                //扣除全部
                userCoinAddVO.setCoinValue(userTotalAmount);
            } else { // 对应rollout
                //  部分转账
                if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
                    log.info("{}:用户钱包,余额不足:{},用户金额:{},扣款金额:{}", VenueEnum.CQ9.getVenueName(), userId, userTotalAmount, tradeAmount);
                    //throw new CQ9DefaultException(CQ9ResultCodeEnums.INSUFFICIENT_BALANCE, req.getAccount());
                    return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1005, CQ9BalanceRsp.builder().build());
                }

            }
            // takeAll rollout
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
            // debit 对已经完成的订单进行扣款
        } else if (req.getCallType().equals(CQ9CallEnums.DEBIT.getCode())) {

            //  部分转账
            if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}:用户钱包,帐变金额不能为负数:{},用户金额:{},扣款金额:{}", VenueEnum.CQ9.getVenueName(), userId, userTotalAmount, tradeAmount);
                //throw new CQ9DefaultException(CQ9ResultCodeEnums.INSUFFICIENT_BALANCE, req.getAccount());
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1005, CQ9BalanceRsp.builder().build());
            }
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            // 重算逻辑不会重复校验
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
            // credit
        } else if (req.getCallType().equals(CQ9CallEnums.CREDIT.getCode())) {
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
        }
        //

        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
        // 帐变为0
            return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(userTotalAmount)
                    .amount(userTotalAmount).build());
        }

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("{}:调用扣费失败,userCoinAddVO:{}", VenueEnum.CQ9.getVenueName(), userCoinAddVO);
            //throw new CQ9DefaultException(CQ9ResultCodeEnums.TRANSACTION_SERVICE_ERROR, req.getAccount());
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TRANSACTION_SERVICE_ERROR, CQ9BalanceRsp.builder().build());
        }

        return CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                .currency(cq9CurrencyEnum.getCode())
                .balance(recordResultVO.getCoinAfterBalance())
                .amount(userTotalAmount).build());
    }

    private String getToken(String userId) {
        String key = String.format(RedisConstants.CQ9_TOKEN, userId);
        RedisUtil.deleteKey(key);
        String token = "ftg_" + userId + UUID.randomUUID();

        RedisUtil.setValue(key, token);

        return token;
    }


    @DistributedLock(name = RedisConstants.CQ9_COIN_LOCK, unique = "#req.account", waitTime = 3, leaseTime = 180)
    public CQ9BaseRsp<CQ9BalanceRsp> payOut(CQ9EndRoundReq req) {
        if (StringUtils.equalsAnyIgnoreCase(req.getCallType(), CQ9CallEnums.ENDROUND.getCode())) {
            if (ObjectUtil.isEmpty(req.getData())
                    || ObjectUtil.isEmpty(req.getCreateTime())) {
                log.info("参数校验失败");
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            }

        }
        CQ9BaseRsp<CQ9BalanceRsp> cq9BaseRsp = new CQ9BaseRsp();
        List<String> mtcodeList = new ArrayList<>();
        BigDecimal beforeAmount = BigDecimal.ZERO;
        for (EventData eventData : req.getData()) {
            // char
            CQ9BetReq cq9BetReq = CQ9BetReq.builder()
                    .wtoken(req.getWtoken())
                    .account(req.getAccount())
                    .eventTime(eventData.getEventtime())
                    .gamehall(req.getGamehall())
                    .gamecode(req.getGamecode())
                    .roundid(req.getRoundid())
                    .amount(eventData.getAmount())
                    .mtcode(eventData.getMtcode())
                    .type(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())
                    .callType(CQ9CallEnums.ENDROUND.getCode())
                    .balanceType(CommonConstant.business_one_str)
                    .takeAllFlag(false)
                    .actionFlag(false)
                    .build();
            mtcodeList.add(cq9BetReq.getMtcode());
            // 第一次的befor
            if (beforeAmount.compareTo(cq9BetReq.getAmount()) != 0) {

                CQ9BalanceRsp data = cq9BaseRsp.getData();
                if (data != null) {
                    beforeAmount = data.getAmount();
                }
            }
            cq9BaseRsp = handleBalanceChange(cq9BetReq);

        }
        // 如果多笔派彩，需要更新一样的结果
        if (!mtcodeList.isEmpty() && mtcodeList.size() > 1) {
            updateRecordForPayoff(cq9BaseRsp, mtcodeList, beforeAmount);
        }
        return cq9BaseRsp;
    }

    @DistributedLock(name = RedisConstants.CQ9_COIN_LOCK, unique = "#req.account", waitTime = 3, leaseTime = 180)
    public CQ9BaseRsp<CQ9BalanceRsp> rollin(CQ9RollinReq req) {
        // 参数校验
        if (ObjectUtil.isEmpty(req.getBet())
                || ObjectUtil.isEmpty(req.getWin())
                || ObjectUtil.isEmpty(req.getAmount())
                || ObjectUtil.isEmpty(req.getCreateTime())
                || ObjectUtil.isEmpty(req.getGametype())
                || ObjectUtil.isEmpty(req.getRake())
                || ObjectUtil.isEmpty(req.getMtcode())
                || ObjectUtil.isEmpty(req.getEventTime())) {
            log.info("参数校验失败");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());

        }
        // he evenTime parameter has wrong time foramat, should return error code 1004
        if (!CQ9Utils.isRFC3339TimeFormat(req.getEventTime())
                || !CQ9Utils.isRFC3339TimeFormat(req.getCreateTime())) {
            log.info("下注校验:时间格式错误");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1004, CQ9BalanceRsp.builder().build());
        }
        CQ9BetReq cq9BetReq = CQ9BetReq.builder()
                .wtoken(req.getWtoken())
                .account(req.getAccount())
                .eventTime(req.getEventTime())
                .gamehall(req.getGamehall())
                .gamecode(req.getGamecode())
                .roundid(req.getRoundid())
                .amount(req.getAmount())
                .mtcode(req.getMtcode())
                .type(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())
                .takeAllFlag(false)
                .balanceType(req.getBalanceType())
                .callType(req.getCallType())
                .actionFlag(false)
                .remark(req.getMtcode())
                .build();
        return handleBalanceChange(cq9BetReq);
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

    @DistributedLock(name = RedisConstants.CQ9_COIN_LOCK, unique = "#req.account", waitTime = 3, leaseTime = 180)
    public CQ9BaseRsp<CQ9BalanceRsp> refund(CQ9BetReq req) {
        if (ObjectUtil.isEmpty(req.getMtcode())) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            //throw new CQ9DefaultException(CQ9ResultCodeEnums.BAD_PARAMETERS, req.getAccount());
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            return failStatus;

        }
        LambdaQueryWrapper<CQ9TransactionRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getStatusCode, "0");
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getAction, req.getCallType());
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getMtcode, req.getMtcode());
        List<CQ9TransactionRecordPO> cqRecordPOS = transactionRecordRepository.selectList(lambdaQueryWrapper);
        if (CollUtil.isNotEmpty(cqRecordPOS)) {
            log.info("{}:交易订单号重复,req:{}", VenueEnum.CQ9.getVenueName(), req);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.MTCODE_DUPLICATE_1015, CQ9BalanceRsp.builder().build());
        }
        // 插入记录
        CQ9TransactionRecordPO transactionRecordPO = CQ9TransactionRecordPO.builder()
                .account(req.getAccount())
                .createTime(CQ9Utils.getNowTimeRF33())
                .mtcode(req.getMtcode())
                .amount(req.getAmount())
                .eventTime(req.getEventTime() == null ? CQ9Utils.getNowTimeRF33() : req.getEventTime())
                .action(req.getCallType())
                .balanceType(req.getBalanceType())
                .requestJson(JSON.toJSONString(req))
                .build();
        List<CQ9TransactionRecordPO> cq9RecordPOS = getCq9Records(req.getMtcode(), req.getBalanceType());
        if (CollUtil.isNotEmpty(cq9RecordPOS)) {
            req.setRoundid(req.getRoundid() + "_" + cq9RecordPOS.size());
        }
        transactionRecordRepository.insert(transactionRecordPO);
        // update
        LambdaUpdateWrapper<CQ9TransactionRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        String endNow = CQ9Utils.getNowTimeRF33();
        updateWrapper.set(CQ9TransactionRecordPO::getEndTime, endNow);
        WalletEnum.CoinTypeEnum type = WalletEnum.CoinTypeEnum.CANCEL_BET;
        if (!ObjectUtil.isAllNotEmpty(req.getAccount(), req.getMtcode())) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            //throw new CQ9DefaultException(CQ9ResultCodeEnums.BAD_PARAMETERS, req.getAccount());
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.BAD_PARAMETERS, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;

        }


        String userId = getUserAccount(req.getAccount());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }
        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode(), userInfoVO.getMainCurrency());
        /*if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode());
            var failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_DISABLED, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId());
            return failStatus;
        }*/

        // 校验钱包token ,aeskey,是钱包的密钥
        if (!StringUtils.equals(venueInfoVO.getAesKey(), req.getWtoken())) {
            log.info("{}:参数mtoken校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TOKEN_INVALID, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        /*if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.CQ9.getVenueName(), userId);
            var failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_DISABLED, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId());
            return failStatus;
        }*/
        CQ9CurrencyEnum cq9CurrencyEnum = CQ9CurrencyEnum.byPlatCurrencyCode(userInfoVO.getMainCurrency());
        if (cq9CurrencyEnum == null) {
            log.info("{}:币种异常", VenueEnum.CQ9.getVenueName());
            var failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.CURRENCY_NOT_SUPPORTED, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            var failStatus = CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(BigDecimal.ZERO).build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }
        // 根据req.getMtcode() 查询转出的金额
        UserCoinRecordVO userCoinRecord = userCoinRecordApi.getUserCoinRecord(req.getMtcode(), userId, CoinBalanceTypeEnum.EXPENSES.getCode());
        if (userCoinRecord == null) {
            // 查询CQ9转账记录表
            LambdaQueryWrapper<CQ9TransactionRecordPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CQ9TransactionRecordPO::getStatusCode, "0");
            queryWrapper.eq(CQ9TransactionRecordPO::getMtcode, req.getMtcode());
            queryWrapper.eq(CQ9TransactionRecordPO::getBalanceType, 2);// 支出
            List<CQ9TransactionRecordPO> cq9RecordRefundS = transactionRecordRepository.selectList(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(cq9RecordRefundS)) {
                CQ9TransactionRecordPO cq9RecordRefund = cq9RecordRefundS.get(0);
                if (cq9RecordRefund.getBefore().compareTo(cq9RecordRefund.getBalance()) == 0) {
                    log.info("{}:用户转出的金额记录与转入金额相同，直接返回结果,用户:{}", VenueEnum.CQ9.getVenueName(), userId);
                    var successStatus = CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                            .currency(cq9CurrencyEnum.getCode())
                            .balance(BigDecimal.ZERO)
                            .amount(BigDecimal.ZERO).build());
                    updateRecord(successStatus, transactionRecordPO.getId(), false);
                    return successStatus;
                }
            }
            log.info("{}:没有查到用户转出的金额记录,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            var failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.SERVER_ERR, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }
        // 判断游戏是否配置
        /*if (StringUtils.isNotBlank(req.getGamecode())) {
            if (!isGameAvailable(userInfoVO.getSiteCode(), req.getGamecode(), venueInfoVO.getVenueCode())) {
                return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.GAME_UNDER_MAINTENANCE, CQ9BalanceRsp.builder().build());
            }
        }*/
        BigDecimal tradeAmount = userCoinRecord.getCoinValue();//账变金额
        // 扣除之前的金额
        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getRoundid());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        // 转入游戏所有取消
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        if (StringUtils.isNotBlank(req.getRemark())) {
            userCoinAddVO.setRemark(assembleRemark(req.getGamehall(), req.getGamecode(), req.getRemark()));
        }

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.CQ9);
        userCoinAddVO.setThirdOrderNo(req.getRoundid());

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("{}:调用扣费失败,userCoinAddVO:{}", VenueEnum.CQ9.getVenueName(), userCoinAddVO);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TRANSACTION_SERVICE_ERROR, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        var successStatus = CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                .currency(cq9CurrencyEnum.getCode())
                .balance(recordResultVO.getCoinAfterBalance())
                .amount(userTotalAmount).build());
        updateRecord(successStatus, transactionRecordPO.getId(), true);
        return successStatus;
    }

    public CQ9BaseRsp<TransactionRecordRsp> record(String mtcode, String wtoken) {

        // 校验钱包token ,aeskey,是钱包的密钥
        /*VenueInfoVO venueInfoVO = venueInfoService.venueInfoByVenueCode(VenueEnum.CQ9.getVenueCode(), null);
        if (!StringUtils.equals(venueInfoVO.getAesKey(), wtoken)) {
            log.info("{}:参数mtoken校验失败,req:{}", VenueEnum.CQ9.getVenueName(), wtoken);
            return CQ9Utils.getRecordFailStatus(CQ9ResultCodeEnums.TOKEN_INVALID, TransactionRecordRsp.builder().build());
        }*/
        // 根据req.getMtcode() 查询转出的金额
        LambdaQueryWrapper<CQ9TransactionRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CQ9TransactionRecordPO::getMtcode, mtcode)
                // refund 是对bet/rollout/takeal 进行回退，所以查询不包括refund
                //.ne(CQ9TransactionRecordPO::getAction, CQ9CallEnums.REFUND.getCode())
                .eq(CQ9TransactionRecordPO::getStatusCode, "0")//只取成功的
                .orderByDesc(CQ9TransactionRecordPO::getId)
                .last(" limit 2 ");

        List<CQ9TransactionRecordPO> recordPOs = transactionRecordRepository.selectList(queryWrapper);
        // mtcode 是唯一， 即使是退款，也只有两个，那么不管多少，只取两条
        // 如果一条，就返回，如果有两条数据，说明一个是扣款，一个退回
        // 两条action 取扣款的那条，其他取补款的
        if (ObjectUtil.isEmpty(recordPOs)) {
            log.error("{}:记录不存在,不存在:{}", VenueEnum.CQ9.getVenueName(), mtcode);
            return CQ9Utils.getRecordFailStatus(CQ9ResultCodeEnums.SERVER_ERR, "record not found");
        }
        CQ9TransactionRecordPO recordPOReturn = recordPOs.get(0);

        if (recordPOReturn == null) {
            log.error("{}:记录不存在,不存在:{}", VenueEnum.CQ9.getVenueName(), mtcode);
            return CQ9Utils.getRecordFailStatus(CQ9ResultCodeEnums.SERVER_ERR, "record not found");
        }
        if (ObjectUtil.isEmpty(recordPOReturn.getStatus())
                || ObjectUtil.equal(CQ9ResultCodeEnums.SERVER_ERR.getCode(), recordPOReturn.getStatus())
                || !ObjectUtil.equal(CQ9ResultCodeEnums.SUCCESS.getCode(), recordPOReturn.getStatusCode())) {
            log.error("{}:查询不到指定记录结果更新状态，设置为1014，:{}", VenueEnum.CQ9.getVenueName(), mtcode);
            recordPOReturn.setStatus("1014");
            return CQ9Utils.getRecordFailStatus(CQ9ResultCodeEnums.SERVER_ERR, "record not found");
        }
        String action;
        String statusAction;
        if (recordPOs.size() == 1) {
            action = recordPOReturn.getAction();
            statusAction = "success";

        } else {
            action = recordPOs.get(1).getAction();
            statusAction = recordPOReturn.getAction();
        }

        // 组装event
        String roundArray = recordPOReturn.getRoundArray();
        List<TransactionRecordRsp.Event> events = new ArrayList<>();
        if (ObjectUtil.isEmpty(roundArray)) {
            BigDecimal amount = recordPOReturn.getAmount() == null
                    ? recordPOReturn.getBalance().subtract(recordPOReturn.getBefore()).abs()
                    : recordPOReturn.getAmount();
            events.add(TransactionRecordRsp.Event.builder()
                    .mtcode(recordPOReturn.getMtcode())
                    .amount(amount)
                    .eventtime(CQ9Utils.toRfc3339(recordPOReturn.getEventTime())).build());
        } else {
            // 根据roundId 查询记录
            String[] roundArray1 = roundArray.split(",");
            LambdaQueryWrapper<CQ9TransactionRecordPO> queryWrapperEvent = new LambdaQueryWrapper<>();
            queryWrapperEvent
                    .eq(CQ9TransactionRecordPO::getStatusCode, "0")
                    .in(CQ9TransactionRecordPO::getMtcode, roundArray1)//只取成功的
                    .eq(CQ9TransactionRecordPO::getAction, action)
                    .orderByAsc(CQ9TransactionRecordPO::getId);

            List<CQ9TransactionRecordPO> recordPOEvents = transactionRecordRepository.selectList(queryWrapperEvent);

            if (ObjectUtil.isNotEmpty(recordPOEvents)) {

                for (CQ9TransactionRecordPO recordPO : recordPOEvents) {
                    BigDecimal amount = recordPO.getAmount() == null
                            ? recordPO.getBalance().subtract(recordPO.getBefore()).abs()
                            : recordPO.getAmount();
                    events.add(TransactionRecordRsp.Event.builder()
                            .mtcode(recordPO.getMtcode())
                            .amount(amount)
                            .eventtime(CQ9Utils.toRfc3339(recordPO.getEventTime())).build());
                }

            }
        }


        TransactionRecordRsp.TransactionRecordRspBuilder builder = TransactionRecordRsp.builder();
        builder._id(recordPOReturn.getId());
        builder.action(action);
        builder.target(TransactionRecordRsp.Target.builder().account(recordPOReturn.getAccount()).build());
        builder.status(TransactionRecordRsp.Status.builder().createtime(CQ9Utils.toRfc3339(recordPOReturn.getCreateTime()))
                .endtime(CQ9Utils.toRfc3339(recordPOReturn.getEndTime()))
                .status(statusAction)
                .datetime(CQ9Utils.toRfc3339(recordPOReturn.getResponseTime()))
                .message(recordPOReturn.getStatusMessage() == null ? null : recordPOReturn.getStatusMessage().toLowerCase())
                .build());
        builder.before(recordPOReturn.getBefore());
        builder.balance(recordPOReturn.getBalance());
        builder.currency(recordPOReturn.getCurrency());
        builder.event(events);
        TransactionRecordRsp recordRsp = builder
                .build();
        return CQ9Utils.getSuccessRecordStatus(recordRsp);
    }

    public CQ9BaseRsp<CQ9BalanceRsp> payoff(CQ9PayoffReq req) {
        if (ObjectUtil.isEmpty(req.getMtcode())
                || ObjectUtil.isEmpty(req.getAccount())
                || ObjectUtil.isEmpty(req.getEventTime())
                || ObjectUtil.isEmpty(req.getAmount())
                || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
            return failStatus;

        }
        if (!req.isRFC3339TimeFormat(req.getEventTime())) {
            log.info("下注校验:时间格式错误");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1004, CQ9BalanceRsp.builder().build());
        }
        // 派彩
        BigDecimal amount = req.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("下注校验:金额必须大于0");
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PARAM_NOT_FOUND_1003, CQ9BalanceRsp.builder().build());
        }
        String userId = getUserAccount(req.getAccount());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
            //updateRecord(failStatus, transactionRecordPO.getId());
            return failStatus;
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.PLAYER_NOT_FOUND_1016, CQ9BalanceRsp.builder().build());
            //updateRecord(failStatus, transactionRecordPO.getId());
            return failStatus;
        }

        LambdaQueryWrapper<CQ9TransactionRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getStatusCode, "0");
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getAction, req.getCallType());
        lambdaQueryWrapper.eq(CQ9TransactionRecordPO::getMtcode, req.getMtcode());
        List<CQ9TransactionRecordPO> cqRecordPOS = transactionRecordRepository.selectList(lambdaQueryWrapper);
        if (CollUtil.isNotEmpty(cqRecordPOS)) {
            log.info("{}:交易订单号重复,req:{}", VenueEnum.CQ9.getVenueName(), req);
            return CQ9Utils.getFailStatus(CQ9ResultCodeEnums.MTCODE_DUPLICATE_2009, CQ9BalanceRsp.builder().build());
        }
        // 插入记录
        CQ9TransactionRecordPO transactionRecordPO = CQ9TransactionRecordPO.builder()
                .account(req.getAccount())
                .createTime(CQ9Utils.getNowTimeRF33())
                .mtcode(req.getMtcode())
                .amount(req.getAmount())
                .eventTime(req.getEventTime())
                .action(req.getCallType())
                .balanceType(req.getBalanceType())
                .requestJson(JSON.toJSONString(req))
                .build();

        transactionRecordRepository.insert(transactionRecordPO);
        // update
        LambdaUpdateWrapper<CQ9TransactionRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        String endNow = CQ9Utils.getNowTimeRF33();
        updateWrapper.set(CQ9TransactionRecordPO::getEndTime, endNow);
        if (!ObjectUtil.isAllNotEmpty(req.getAccount(), req.getMtcode())) {
            log.info("{}:参数校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            //throw new CQ9DefaultException(CQ9ResultCodeEnums.BAD_PARAMETERS, req.getAccount());
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.BAD_PARAMETERS, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }


        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.CQ9.getVenueCode(), userInfoVO.getMainCurrency());

        // 校验钱包token ,aeskey,是钱包的密钥
        if (!StringUtils.equals(venueInfoVO.getAesKey(), req.getWtoken())) {
            log.info("{}:参数mtoken校验失败,req:{}", VenueEnum.CQ9.getVenueName(), req);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TOKEN_INVALID, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        CQ9CurrencyEnum cq9CurrencyEnum = CQ9CurrencyEnum.byPlatCurrencyCode(userInfoVO.getMainCurrency());
        if (cq9CurrencyEnum == null) {
            log.info("{}:币种异常", VenueEnum.CQ9.getVenueName());
            var failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.CURRENCY_NOT_SUPPORTED, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.CQ9.getVenueName(), userId);
            var failStatus = CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                    .currency(cq9CurrencyEnum.getCode())
                    .balance(BigDecimal.ZERO).build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }
        // 根据req.getMtcode() 查询转出的金额


        BigDecimal tradeAmount = req.getAmount();//奖励金额
        // 扣除之前的金额
        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getMtcode());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        // 转入游戏所有取消
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        if (StringUtils.isNotBlank(req.getRemark())) {
            userCoinAddVO.setRemark(assembleRemark(req.getRemark(), req.getPromoid(), null));
        }

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.CQ9);
        userCoinAddVO.setThirdOrderNo(req.getMtcode());

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("{}:活動獎勵透過此支API派發給玩家,userCoinAddVO:{}", VenueEnum.CQ9.getVenueName(), userCoinAddVO);
            CQ9BaseRsp<CQ9BalanceRsp> failStatus = CQ9Utils.getFailStatus(CQ9ResultCodeEnums.TRANSACTION_SERVICE_ERROR, CQ9BalanceRsp.builder().build());
            updateRecord(failStatus, transactionRecordPO.getId(), false);
            return failStatus;
        }

        var successStatus = CQ9Utils.getSuccessStatus(CQ9BalanceRsp.builder()
                .currency(cq9CurrencyEnum.getCode())
                .balance(recordResultVO.getCoinAfterBalance())
                .amount(userTotalAmount).build());
        updateRecord(successStatus, transactionRecordPO.getId(), false);
        return successStatus;
    }
}
