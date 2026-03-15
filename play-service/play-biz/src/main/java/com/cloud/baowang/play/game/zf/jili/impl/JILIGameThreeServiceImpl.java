package com.cloud.baowang.play.game.zf.jili.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.zf.ZfBetErrorCodeEnum;
import com.cloud.baowang.play.api.enums.zf.ZfCancelBetErrorCodeEnum;
import com.cloud.baowang.play.api.enums.zf.ZfErrorCodeEnum;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.zf.*;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.JILI.JILICurrencyEnum;
import com.cloud.baowang.play.api.enums.JILI.JILIWalletStatusEnum;
import com.cloud.baowang.play.api.enums.JILI.ResultTypeEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.jili.JILIBaseRes;
import com.cloud.baowang.play.api.vo.jili.req.*;
import com.cloud.baowang.play.api.vo.jili.res.JILIBalanceRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.zf.openApi.enums.JILIThreeOrderStatusEnums;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.play.vo.jl.GamePageVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.NEW_JILI)
public class JILIGameThreeServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;
    private final static String KVND = "kVND";

    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    // "de,en,es,fr,hi,hk,id,it,ja,ko,my,pt,ru,th,tl,tr,vi,zh",
    static {
        LANGUAGE_MAP.put("zh-CN", "zh"); //简体
        LANGUAGE_MAP.put("zh-TW", "hk"); //繁体
        LANGUAGE_MAP.put("en-US", "en"); //英语
        LANGUAGE_MAP.put("vi-VN", "vi"); // 越南文
        LANGUAGE_MAP.put("id-ID", "id"); //印尼语
        LANGUAGE_MAP.put("pt-BR", "pt"); // 葡萄牙语-巴西

        LANGUAGE_MAP.put("ko-KR", "ko"); // 韩语
        LANGUAGE_MAP.put("zh-SG", "sg");
        LANGUAGE_MAP.put("fr-FR", "fr");
        LANGUAGE_MAP.put("de-DE", "de");
        LANGUAGE_MAP.put("es-ES", "es");
        LANGUAGE_MAP.put("it-IT", "it");
        LANGUAGE_MAP.put("ru-RU", "ru");
        LANGUAGE_MAP.put("ar-SA", "sa");
        LANGUAGE_MAP.put("hi-IN", "in");
        LANGUAGE_MAP.put("th-TH", "th");
        LANGUAGE_MAP.put("ms-MY", "my");
        LANGUAGE_MAP.put("tr-TR", "tr");
        LANGUAGE_MAP.put("nl-NL", "nl");
        LANGUAGE_MAP.put("nl-BE", "be");
        LANGUAGE_MAP.put("sv-SE", "se");
        LANGUAGE_MAP.put("fi-FI", "fi");
        LANGUAGE_MAP.put("da-DK", "dk");
        LANGUAGE_MAP.put("no-NO", "no");
        LANGUAGE_MAP.put("pl-PL", "pl");
        LANGUAGE_MAP.put("cs-CZ", "cz");
        LANGUAGE_MAP.put("hu-HU", "hu");
        LANGUAGE_MAP.put("ro-RO", "ro");
        LANGUAGE_MAP.put("el-GR", "gr");
        LANGUAGE_MAP.put("he-IL", "il");
        LANGUAGE_MAP.put("uk-UA", "ua");
        LANGUAGE_MAP.put("ja-JP", "ja");
    }


    public JILIBaseRes balance(@NotNull JILIBalanceReq req) {
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(req.getSignature())) {
            log.error("{} 查余额但参数不全, JILIBalanceReq:{} ", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }

        String userId = getUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.JILI_03.getVenueName(), userId);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            log.error("{} 查余额但会员不存在, 参数:{} ", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("JILI_03 查余额 error user game lock userName[{}].", userId);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_DISABLED.name()).build();

        }
        //2. token and venue open status check
        if (venueMaintainClosed(VenueEnum.JILI_03.getVenueCode(),userInfoVO.getSiteCode())) {
            log.error("JILI 签名错误, 参数: {} , 签名, {}", req, req.getSignature());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), userInfoVO.getMainCurrency());
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!req.getSignature().equals(mySignature)) {
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 查余额解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, req.getSignature());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);

        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 币种不支持, 当前币种:{} ", VenueEnum.JILI_03.getVenueCode(), mainCurrency);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }
        JILIBalanceRes balanceRes = JILIBalanceRes.builder().balance(BigDecimal.ZERO).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
        //4. balance return
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNull(userCenterCoin)) {
            log.error("{} 币种不支持, 当前币种:{} ", VenueEnum.JILI_03.getVenueCode(), mainCurrency);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(balanceRes).build();
        }
        balanceRes.setBalance(userCenterCoin.getTotalAmount());
        log.info("{} 回调钱包成功, 请求参数: {}, 返回参数 : {}", VenueEnum.JILI_03.getVenueCode(), req, balanceRes);
        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(balanceRes).build();
    }


    public JILIBaseRes bet(@NotNull JILIBetReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            log.error("{} 游戏下注但参数不全, JILIBetReq:{} ", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("第三方回调bet接口 参数: {}", req);

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.error("{} 游戏下注但会员不存在, userId_1:{} ", VenueEnum.JILI_03.getVenueCode(), req.getUsername());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.error("{} 游戏下注但会员不存在, userId_2:{} ", VenueEnum.JILI_03.getVenueCode(), req.getUsername());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        //2. token and venue open status check
        if (venueMaintainClosed(VenueEnum.JILI_03.getVenueCode(),userInfoVO.getSiteCode())) {
            log.error("JILI 签名错误, 参数: {} , 签名, {}", req, req.getSignature());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), userInfoVO.getMainCurrency());
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏下注解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏下注解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }

        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("{} 游戏下注但会员被锁定, userInfoVO:{} ", VenueEnum.JILI_03.getVenueCode(), userInfoVO);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }
        //3. token and venue open status check
        if (!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.error("Venue is closed, Don`t allow to bet any more: {} ", VenueEnum.JILI_03.getVenueCode());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENUE_IS_CLOSED.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 币种不支持,  请求参数: {},  当前币种:{} ", VenueEnum.JILI_03.getVenueCode(), req, mainCurrency);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }
        //4. balance change not enough
        BigDecimal tradeAmount = req.getAmount();//账变金额
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(tradeAmount) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
        }
        //4. 去重操作
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinRecordVo.setOrderNo(req.getTransactionId());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            log.error("{} 下注失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            JILIBalanceRes endRes = JILIBalanceRes.builder().balance(userCoin.getTotalAmount()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(req.getTransactionId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(tradeAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId()+ "_"+req.getBetId());

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

        JILIBalanceRes balanceRes = JILIBalanceRes.builder()
                .balance(recordResultVO.getCoinAfterBalance())
                .currency(jiliCurrencyEnum.getCode())
                .username(req.getUsername())
                .timestamp(System.currentTimeMillis()).build();

        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(balanceRes).build();
    }

    /*
        resultType:
        resultType = WIN，
        - winAMount > 0， 存入会员额度
        resultType = BET_LOSE，
        - betAmount >= 0，从会员额度扣取
        - winAmount = 0，无需任何作业
        resultType = BET_WIN，
        - betAmount >= 0，从会员额度扣取
        - winAmount > 0， 存入会员额度
        resultType = LOSE，
        - betAmount = 0，无需任何作业
        - winAmount = 0， 无需任何作业
        resultType = END，
        - betAmount > 0，无需任何作业
        - winAmount >= 0，无需任何作业
        winLoss = 绝对的盈亏，当局的下注额 + 输赢金额形成的一个数额（不包括彩金 jackpotAmount）
        betAmount = 大过0 = 扣款
        winAmount = 大过0 = 加额度
        jackpotAmount = 大过0 = 加额度
        isEndRound = true 是指这笔注单记录（或这局下注单）已结束可以结算。
        另外 resultType=END 是指这笔注单结束，通知您不必再对会员的额度进行调整只需回传我方玩家最新余额即可
     */
    public JILIBaseRes betResult(@NotNull JILIBetResultReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            log.error("{} 游戏下注结果但参数不全, JILIBetReq:{} ", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("JILI第三方回调betResult接口 参数: {}", req);
        //2. token and venue open status check
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), null);
        if (venueInfoVO == null) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏下注结果解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏下注结果解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.error("{} 游戏下注结果但会员不存在, userId_1:{} ", VenueEnum.JILI_03.getVenueCode(), req.getUsername());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.error("{} 游戏下注结果但会员不存在, userId_2:{} ", VenueEnum.JILI_03.getVenueCode(), req.getUsername());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} 游戏下注结果但用户被锁定userInfoVO: {} ", VenueEnum.JILI_03.getVenueCode(), userInfoVO);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }
        //3. token and venue open status check
        if (!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.error("Venue is closed, Don`t allow to bet : {} ", VenueEnum.JILI_03.getVenueCode());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENUE_IS_CLOSED.name()).build();
        }


        if (venueMaintainClosed(VenueEnum.JILI_03.getVenueCode(),userInfoVO.getSiteCode())) {
            log.error("Site :{} has not venueCode:{} authority", userInfoVO.getSiteCode(), VenueEnum.JILI_03.getVenueCode());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_HAS_NO_VENUE_AUTHENTICATION.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 币种不支持, 请求参数: {},  当前币种:{} ", VenueEnum.JILI_03.getVenueCode(), req, mainCurrency);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }
        //4. balance change according to resultType
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        BigDecimal balanceFinal = userCoin.getTotalAmount();
        JILIBalanceRes endRes = JILIBalanceRes.builder().balance(balanceFinal).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();


        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setOrderNo(req.getTransactionId());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            log.error("{} 下注结果下注失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }

        //5. CoinWallet change
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTransactionId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId() + "_" + req.getBetId());
        String resultType = req.getResultType();
        ResultTypeEnum typeEnum = ResultTypeEnum.typeEnum(resultType);

        CoinRecordResultVO recordResultVO;
        switch (typeEnum) {
            case WIN:
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
                //派奖金额
                userCoinAddVO.setCoinValue(req.getWinAmount().add(req.getJackpotAmount()));
                recordResultVO = toUserCoinHandle(userCoinAddVO);

                break;
            case BET_WIN:

                if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0) {
                    log.error("{} 下注结果BET_WIN下注失败, 余额不足, 参数: {}, 当前余额: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin.getTotalAmount());
                    return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
                }
                if (req.getBetAmount().compareTo(BigDecimal.ZERO)>0){
                    userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
                    userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                    userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                    //投注金额
                    userCoinAddVO.setCoinValue(req.getBetAmount());
                    recordResultVO = toUserCoinHandle(userCoinAddVO);//
                    if (recordResultVO!=null && !UpdateBalanceStatusEnums.SUCCESS.getCode().equals(recordResultVO.getResultStatus().getCode())) {
                        log.error("{} 下注结果投注金额扣除异常, 请求参数: {}, 返回结果: {}", VenueEnum.JILI_03.getVenueCode(), req, recordResultVO);
                        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
                    }
                }

                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
                //派奖金额
                userCoinAddVO.setCoinValue(req.getWinAmount().add(req.getJackpotAmount()));
                recordResultVO = toUserCoinHandle(userCoinAddVO);

                break;
            case BET_LOSE:
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                //4. balance change not enough
                if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0) {
                    log.error("{} 下注结果下注失败, 余额不足, 参数: {}, 当前余额: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin.getTotalAmount());
                    return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
                }
                //投注金额
                userCoinAddVO.setCoinValue(req.getBetAmount());
                recordResultVO = toUserCoinHandle(userCoinAddVO);
                break;
            case END:
                UserCoinRecordRequestVO tempVo = new UserCoinRecordRequestVO();
                tempVo.setUserAccount(userInfoVO.getUserAccount());
                tempVo.setUserId(userInfoVO.getUserId());
                tempVo.setRoundIdBetId(req.getRoundId() + "_" + req.getBetId());
                List<UserCoinRecordVO> recordsVo = getUserCoinRecords(tempVo);
                if (CollUtil.isEmpty(recordsVo)) {
                    log.error("{} 下注结果结束, 找不到匹配的下注单, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
                    return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_TRANSACTION_NOT_EXISTS.name()).build();
                }
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
            default:
                log.info("{} 下注结果下注无操作, 参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }
        if (recordResultVO!=null && !UpdateBalanceStatusEnums.SUCCESS.getCode().equals(recordResultVO.getResultStatus().getCode())) {
            log.error("{} 下注结果下注异常, 下注结果产生的账变结果: {} 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), recordResultVO, req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        endRes.setBalance(recordResultVO!=null?recordResultVO.getCoinAfterBalance():balanceFinal);
        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
    }

    public JILIBaseRes rollback(@NotNull JILIRollbackReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("第三方回调rollback接口 参数: {}", req);
        //2. token and venue open status check
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), null);
        if (venueInfoVO == null) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }

        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏回滚解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏回滚解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }
        //3. username , currency and token check
        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.error("{} 游戏回滚userId不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.error("{} 游戏回滚userInfoApi.getByUserId(userId)不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} 游戏场馆会员游戏被锁定, 会员名称 : {}", VenueEnum.JILI_03.getVenueCode(), req.getUsername());
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 游戏回滚币种不匹配, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }

        //4. 找到之前的单, 根据betId...,
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setRoundIdBetId(req.getRoundId()+"_"+req.getBetId());

        List<UserCoinRecordVO> list = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isEmpty(list)) {
            log.error("{} 游戏回滚失败, 找不到匹配的回滚单, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_TRANSACTION_NOT_EXISTS.name()).build();
        }

        if (CollUtil.isEmpty(list) || list.stream().anyMatch(vo -> vo.getOrderNo().equals(req.getTransactionId()))) {
            log.error("{} 游戏回滚失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            JILIBalanceRes endRes = JILIBalanceRes.builder().balance(userCoin.getTotalAmount()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }



        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTransactionId());
        userCoinAddVO.setThirdOrderNo(req.getRoundId()+"_"+req.getBetId());
        userCoinAddVO.setCurrency(req.getCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId()+"_"+req.getBetId());

        UserCoinWalletVO userCoinTemp = getUserCenterCoin(userId);
        BigDecimal newUserCoin = userCoinTemp.getTotalAmount();
        List<UserCoinRecordVO> sortList = list.stream().sorted(Comparator.comparing(UserCoinRecordVO::getCreatedTime)).toList();
        for (UserCoinRecordVO userCoinRecordVO : sortList) {
            BigDecimal userCoin = userCoinRecordVO.getCoinValue();
            if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(userCoinRecordVO.getBalanceType())) {
                newUserCoin = newUserCoin.add(userCoin);
            } else {
                newUserCoin = newUserCoin.subtract(userCoin);
                if (newUserCoin.compareTo(BigDecimal.ZERO) < 0) {
                    log.error("{} 回合操作回滚模拟失败, 余额不足, 参数: {}, 当前余额: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoinTemp.getTotalAmount());
                    return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
                }
            }
        }
        //取消金额, 暂定它是唯一的
        CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
        for (UserCoinRecordVO oldCoinRecordVo : sortList) {
            userCoinAddVO.setCoinValue(oldCoinRecordVo.getCoinValue());
            if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(oldCoinRecordVo.getBalanceType())) {
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
            } else {
                UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
                if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(oldCoinRecordVo.getCoinValue()) < 0) {
                    log.error("{} 回合操作取消失败, 余额不足, 参数: {}, 当前余额: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin.getTotalAmount());
                    return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
                }
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
                userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
            }
            recordResultVO = toUserCoinHandle(userCoinAddVO);
        }

        if (recordResultVO!=null && !UpdateBalanceStatusEnums.SUCCESS.getCode().equals(recordResultVO.getResultStatus().getCode())) {
            log.error("{} 回合操作取消异常, 回合操作取消产生的账变结果: {} 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), recordResultVO, req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }

        JILIBalanceRes endRes = JILIBalanceRes.builder().balance(recordResultVO==null?BigDecimal.ZERO:recordResultVO.getCoinAfterBalance()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
    }

    public JILIBaseRes adjustment(@NotNull JILIAdjustmentReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("第三方回调adjustment接口 参数: {}", req);
        //2. token and venue open status check
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), null);
        if (venueInfoVO == null) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏调整解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏调整解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }

        //2. username , currency and token check
        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.error("{} 游戏调整userId不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.error("{} 游戏调整userInfoApi.getByUserId(userId)不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }
        // (req.getActionFlag() &&  )
        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} 游戏调整用户被锁定, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 游戏调整币种不匹配, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }

        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setRoundIdBetId(req.getRoundId());

        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isEmpty(userCoinRecords)){
            log.error("{} 调节失败, 找不到相应的回合数据, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_TRANSACTION_NOT_EXISTS.name()).build();
        }
        if (userCoinRecords.stream().anyMatch(vo -> vo.getOrderNo().equals(req.getTransactionId()))) {
            log.error("{} 调节失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            JILIBalanceRes endRes = JILIBalanceRes.builder().balance(userCoin.getTotalAmount()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTransactionId());
        //取正数
        userCoinAddVO.setCoinValue(req.getAmount().abs());
        if (req.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.OTHER_ADD.getCode());
        } else {
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(userCoinAddVO.getCoinValue()) < 0) {
                log.error("{} 游戏调整失败, 余额不足, 参数: {}, 当前余额: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin.getTotalAmount());
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
            }
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        }
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());

        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (recordResultVO!=null && !UpdateBalanceStatusEnums.SUCCESS.getCode().equals(recordResultVO.getResultStatus().getCode())) {
            log.error("{} 游戏调整异常, 游戏调整产生的账变结果: {} 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), recordResultVO, req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }

        JILIBalanceRes balanceRes = JILIBalanceRes.builder().balance(recordResultVO==null?BigDecimal.ZERO:recordResultVO.getCoinAfterBalance()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();

        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(balanceRes).build();
    }

    public JILIBaseRes betDebit(@NotNull JILIDebitReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("第三方回调betDebit接口 参数: {}", req);
        //2. token and venue open status check
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), null);
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏借贷解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏借贷解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }


        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.error("{} 游戏借贷userId不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.error("{} 游戏借贷userInfoApi.getByUserId(userId)不存在, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} 游戏借贷用户被锁定, 参数 : {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.error("{} 游戏借贷币种不匹配, 参数 : {}, 用户币种: {}", VenueEnum.JILI_03.getVenueCode(), req, mainCurrency);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal finalBalance = userCoin.getTotalAmount();
        JILIBalanceRes endRes = JILIBalanceRes.builder().balance(finalBalance).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();

        //4. 去重操作
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setOrderNo(req.getTransactionId());

        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (userCoinRecords!=null && userCoinRecords.stream().anyMatch(vo -> vo.getOrderNo().equals(req.getTransactionId()))) {
            log.error("{} 借贷失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        if (req.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCoinValue(req.getAmount().abs());
        } else {
            userCoinAddVO.setCoinValue(userCoin.getTotalAmount());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        }
        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().compareTo(BigDecimal.ZERO) == 0 || userCoin.getTotalAmount().compareTo(userCoinAddVO.getCoinValue()) < 0) {
            log.error("{} 借贷失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {}", VenueEnum.JILI_03.getVenueCode(), req, userCoin);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
        }

        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());

        userCoinAddVO.setOrderNo(req.getTransactionId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);


        if (recordResultVO!=null) {
           switch (Objects.requireNonNull(recordResultVO).getResultStatus()){
               case REPEAT_TRANSACTIONS : break;
               case SUCCESS : endRes.setBalance(recordResultVO.getCoinAfterBalance()); break;
               case INSUFFICIENT_BALANCE: return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INSUFFICIENT_FUNDS.name()).build();
               case AMOUNT_LESS_ZERO, FAIL, WALLET_NOT_EXIST: return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
           }
        }
        log.info("第三方回调betDebit成功, 参数: {}, 返回:{}", req, endRes);
        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
    }

    public JILIBaseRes betCredit(@NotNull JILICreditReq req) {
        String signature = req.getSignature();
        //1. check param (check empty and null )
        if (!req.isValid() && StrUtil.isEmpty(signature)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_REQUEST.name()).build();
        }
        log.info("第三方回调betCredit接口 参数: {}", req);
        //2. token and venue open status check
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(), null);
        if (venueInfoVO == null) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }
        try {
            String mySignature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(req.getMap()));
            if (!signature.equals(mySignature)) {
                log.error("{} 游戏信用解签名不匹配, 参数: {}, 原签名: {}, 我方签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature,mySignature);
                return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
            }
        } catch (Exception e) {
            log.error("{} 游戏信用解签名发生错误, 参数: {}, 原签名: {}", VenueEnum.JILI_03.getVenueName(), req, signature);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_INVALID_SIGNATURE.name()).build();
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_NOT_EXISTS.name()).build();
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_USER_IS_LOCKED.name()).build();
        }

        String mainCurrency = userInfoVO.getMainCurrency();
        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(mainCurrency);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(mainCurrency);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getCode().equals(req.getCurrency())) {
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_WRONG_CURRENCY.name()).build();
        }
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTransactionId());

        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setOrderNo(req.getTransactionId());
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setCoinValue(req.getAmount().abs());
        if (req.getIsRefund().equals(1)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        } else {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            if (req.getJackpotAmount() != null && req.getJackpotAmount().abs().compareTo(BigDecimal.ZERO) > 0) {
                userCoinAddVO.setCoinValue(userCoinAddVO.getCoinValue().add(req.getJackpotAmount()));
            }
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        }

        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (userCoinRecords.stream().anyMatch(vo -> req.getTransactionId().equals(vo.getOrderNo()))) {
            log.error("{} 信用失败, 当前transactionId已经被处理, 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), req);
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            JILIBalanceRes endRes = JILIBalanceRes.builder().balance(userCoin.getTotalAmount()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(endRes).build();
        }

        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId()+ "_"+ req.getBetId());


        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
        userCoinAddVO.setThirdOrderNo(req.getRoundId()+ "_"+ req.getBetId());
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

        if (recordResultVO!=null && !UpdateBalanceStatusEnums.SUCCESS.getCode().equals(recordResultVO.getResultStatus().getCode())) {
            log.error("{} 信用异常, 信用的账变结果: {} 请求参数: {}", VenueEnum.JILI_03.getVenueCode(), recordResultVO, req);
            return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_VENDOR_ERROR.name()).build();
        }

        JILIBalanceRes balanceRes = JILIBalanceRes.builder().balance(recordResultVO==null?BigDecimal.ZERO:recordResultVO.getCoinAfterBalance()).currency(jiliCurrencyEnum.getCode()).username(req.getUsername()).timestamp(System.currentTimeMillis()).build();
        log.info("第三方回调betCredit成功, 参数: {}, 返回:{}", req, balanceRes);
        return JILIBaseRes.builder().traceId(req.getTraceId()).status(JILIWalletStatusEnum.SC_OK.name()).data(balanceRes).build();
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(@NotNull LoginVO loginVO, @NotNull VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + "/game/url";
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<String, Object>();
        String currencyCode = loginVO.getCurrencyCode();

        JILICurrencyEnum jiliCurrencyEnum = JILICurrencyEnum.getFromPlat(currencyCode);
        CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(currencyCode);
        if (jiliCurrencyEnum == null || currencyEnum == null || !jiliCurrencyEnum.getPlat().equals(currencyEnum.getCode())) {
            log.error("{} 获取游戏URL时币种匹配发生错误, 参数: {}, 用户币种 {}", VenueEnum.JILI_03.getVenueName(), paramMap, currencyCode);
            return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
        }

        String lang = LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(), "en");

        paramMap.put("username", casinoMemberVO.getVenueUserAccount());
        paramMap.put("traceId", UUID.randomUUID());
        paramMap.put("gameCode", loginVO.getGameCode());
        paramMap.put("language", lang);
        paramMap.put("platform", "H5".equals(loginVO.getDevice()) ? "H5" : "web");
        paramMap.put("currency", jiliCurrencyEnum.getCode());
        paramMap.put("lobbyUrl", "https://www.google.com");
        paramMap.put("ipAddress", loginVO.getIp());
        String signature = "";
        log.info("{} 登录游戏获取URL,用户: {} ", VenueEnum.JILI_03.getVenueName(), loginVO.getUserAccount());
        try {
            signature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(paramMap));
        } catch (Exception e) {
            log.error("{} 获取游戏URL签名发生错误, 参数: {}, 签名: {}", VenueEnum.JILI_03.getVenueName(), paramMap, signature);
            return ResponseVO.fail(ResultCode.SIGN_ERROR);
        }

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-API-Key", venueInfoVO.getMerchantKey())
                .header("X-Signature", signature)
                .timeout(30000)
                .body(JSON.toJSONString(paramMap))
                .execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object status = jsonObject.get("status");
                if (status != null && JILIWalletStatusEnum.SC_OK.name().equals(status.toString())) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    // 获取用户token
                    String lobbyUrl = data.getString("gameUrl");
                    GameLoginVo gameLoginVo = GameLoginVo.builder()
                            .source(lobbyUrl).userAccount(loginVO.getUserAccount())
                            .venueCode(VenueEnum.JILI_03.getVenueCode())
                            .type(GameLoginTypeEnums.URL.getType()).build();
                    return ResponseVO.success(gameLoginVo);
                } else {
                    log.error("{} 获取游戏URL请求发生错误, 参数: {}, 签名: {} 信息: {} ", VenueEnum.JILI_03.getVenueName(), paramMap, signature, jsonObject);
                }
            } else {
                log.error("{} 获取游戏URL请求发生错误, 参数: {}, 返回响应: {} ", VenueEnum.JILI_03.getVenueName(), paramMap, response);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }

        return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
    }

    @Override
    public String genVenueUserPassword() {
        return StringUtil.createCharacter(8);
    }

    @Override
    public ResponseVO<?> getBetRecordList(@NotNull VenueInfoVO venueInfoVO, @NotNull VenuePullParamVO paramVO) {

        String url = venueInfoVO.getBetUrl() + "/transaction/list";

        GamePageVO gamePageVO = new GamePageVO();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        gamePageVO.setCurrentPage(atomicInteger);
        gamePageVO.setPageSize(2000);
        gamePageVO.setIsOK(true);
        Map<String, UserInfoVO> userInfoMap = new HashMap<>();
        Map<String, String> siteNameMap = getSiteNameMap();
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());
        int count = 0;
        do {
            gamePageVO = getBetRecordListByPage(url, venueInfoVO, paramVO, gamePageVO);
            JSONArray transactions = gamePageVO.getTransactions();
            List<OrderRecordVO> list = new ArrayList<>();
            if (CollUtil.isEmpty(transactions)) {
                break;
            }
            count += transactions.size();
            transactions.forEach(obj -> {
                JSONArray jObj = JSONArray.from(obj);
                String userVenueAccount = jObj.get(3).toString();
                UserInfoVO userInfoVO = userInfoMap.get(userVenueAccount);
                if (userInfoVO == null) {
                    CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(CasinoMemberReq.builder()
                            .venueUserAccount(userVenueAccount).venueCode(venueInfoVO.getVenueCode()).build());
                    if (casinoMember == null) {
                        log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), userVenueAccount);
                        return;
                    }
                    userInfoVO = getByUserId(casinoMember.getUserId());
                    userInfoMap.put(userVenueAccount, userInfoVO);
                }
                OrderRecordVO recordVO = new OrderRecordVO();
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                recordVO.setBetAmount(new BigDecimal(jObj.get(8).toString()));
                recordVO.setBetTime(Long.parseLong(jObj.get(14).toString()));
                recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                recordVO.setVenueCode(venueInfoVO.getVenueCode());
                recordVO.setVenueType(VenueEnum.JILI_03.getType().getCode());

                recordVO.setCasinoUserName(userVenueAccount);
                recordVO.setBetIp(userInfoVO.getLastLoginIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                Object betId = jObj.get(0);
                if (betId != null) {
                    recordVO.setThirdOrderId(betId.toString());
                } else {
                    recordVO.setThirdOrderId(jObj.get(2).toString());
                }
                recordVO.setWinLossAmount(new BigDecimal(jObj.get(10).toString()));
                recordVO.setPayoutAmount(new BigDecimal(jObj.get(9).toString()));
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                recordVO.setSettleTime(Long.parseLong(jObj.get(15).toString()));

                ClassifyEnum classifyEnum = JILIThreeOrderStatusEnums.getClassifyEnumByCode(jObj.get(13));
                if (classifyEnum==null){
                    log.error("{} 三方订单状态异常, 参数: {}", venueInfoVO.getVenueCode(), jObj);
                    classifyEnum = ClassifyEnum.NOT_SETTLE;
                }
                if (Objects.equals(classifyEnum.getCode(), ClassifyEnum.SETTLED.getCode())) {
                    recordVO.setOrderStatus(getOrderStatus(recordVO.getWinLossAmount()));
                }else {
                    recordVO.setOrderStatus(classifyEnum.getCode());
                }
                recordVO.setOrderClassify(classifyEnum.getCode());

                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setChangeStatus(0);
                recordVO.setReSettleTime(0L);
                recordVO.setParlayInfo(jObj.toString());
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setVipRank(userInfoVO.getVipRank());
                recordVO.setValidAmount(jObj.get(11) != null ? new BigDecimal(jObj.get(11).toString()) : BigDecimal.ZERO);
                recordVO.setThirdGameCode(jObj.get(5).toString());
                recordVO.setGameCode(String.valueOf(jObj.get(5)));
                //局号
                recordVO.setGameNo(jObj.get(1).toString());
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
            orderRecordProcessService.orderProcess(list);
            gamePageVO.setTransactions(new JSONArray());
            //接口请求成功, 返回的条数等于分页数, 继续
        } while (gamePageVO.getIsOK() && gamePageVO.getCurrentSize() > 0 && gamePageVO.getCurrentSize() == gamePageVO.getPageSize());

        if (!gamePageVO.getIsOK()){
            log.error("JILI getBetRecordList拉单异常, 共拉单: {}条", count);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }else {
            log.info("getBetRecordList拉单结束, 共拉单: {}条", count);
            return ResponseVO.success();
        }
    }



    public GamePageVO getBetRecordListByPage(String url, VenueInfoVO venueInfoVO, VenuePullParamVO paramVO, GamePageVO gamePageVO) {

        gamePageVO.setCurrentSize(0);
        gamePageVO.setIsOK(false);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("traceId", UUID.randomUUID());
        paramMap.put("fromTime", paramVO.getStartTime());
        paramMap.put("toTime", paramVO.getEndTime());
        paramMap.put("pageNo", gamePageVO.getCurrentPage().getAndIncrement());
        paramMap.put("pageSize", gamePageVO.getPageSize());
        log.info("{} JILI游戏拉注单, url: {}, 参数paramVO: {}, 参数paramVO: {}", VenueEnum.JILI_03.getVenueName(), url, JSON.toJSONString(paramVO), JSON.toJSONString(gamePageVO));
        String signature = "";
        try {
            signature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(paramMap));
        } catch (Exception e) {
            log.error("{} JILI游戏拉注单签名发生错误, 参数: {}, 签名: {}", VenueEnum.JILI_03.getVenueName(), paramMap, signature);
            gamePageVO.setIsOK(false);
            return gamePageVO;
        }
        try (HttpResponse response = HttpRequest.post(url).header(Header.CONTENT_TYPE, "application/json")
                .header("X-API-Key", venueInfoVO.getMerchantKey())
                .header("X-Signature", signature).timeout(30000)
                .body(JSON.toJSONString(paramMap)).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object status = jsonObject.get("status");
                if (status != null && JILIWalletStatusEnum.SC_OK.name().equals(status.toString())) {
                    //获取拉单
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray transactions = data.getJSONArray("transactions");
                    if (!CollUtil.isEmpty(transactions)) {
                        gamePageVO.setCurrentSize(transactions.size());
                        gamePageVO.setTransactions(transactions);
                    }
                    gamePageVO.setIsOK(true);
                } else {
                    log.error("{} JILI游戏拉注单返回码错误, 参数: {}, 签名: {} 信息: {} ", VenueEnum.JILI_03.getVenueName(), paramMap, signature, jsonObject);
                }
            } else {
                log.error("{} JILI游戏拉注单请求发生错误(不通或超时), 参数: {}, 签名: {} ", VenueEnum.JILI_03.getVenueName(), paramMap, signature);
            }
        } catch (Exception e) {
            log.error("{} 获取游戏注单发生Exception错误!!", VenueEnum.JILI_03.getVenueName(), e);
        }
        return gamePageVO;

    }

    @Override
    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.JILI_03.getVenueCode(),null);
        //平台语言.
        String langCode = CurrReqUtils.getLanguage();
        String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");

        JSONObject jsonObject = listAllJILI03Game(venueInfoVO, lang);
        JSONArray jsonArray = jsonObject.getJSONArray("games");

        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);

                ShDeskInfoVO infoVO = ShDeskInfoVO.builder().deskName(array.get(1).toString()).deskNumber(array.get(0).toString()).build();
                resultList.add(infoVO);
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", VenueEnum.JILI_03.getVenueName(), 0);
            return resultList;
        }
        return resultList;
    }

    @Override
    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {

        List<JSONObject> resultList = Lists.newArrayList();
        //平台语言.
        String langCode = CurrReqUtils.getLanguage();
        String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");

        JSONObject jsonObject = listAllJILI03Game(venueInfoVO, lang);
        JSONArray jsonArray = jsonObject.getJSONArray("games");

        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", array.get(1));
                gameJson.put("deskNumber", array.get(0));
                resultList.add(gameJson);
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", VenueEnum.JILI_03.getVenueName(), 0);
            return ResponseVO.success(resultList);
        }
        return ResponseVO.success(resultList);
    }

    private JSONObject listAllJILI03Game(VenueInfoVO venueInfoVO) {
        return listAllJILI03Game(venueInfoVO, LanguageEnum.EN_US.getLang());
    }

    private JSONObject listAllJILI03Game(@NotNull VenueInfoVO venueInfoVO, String lang) {
        String url = venueInfoVO.getApiUrl() + "/game/list";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("traceId", UUID.randomUUID());
        paramMap.put("vendorCode", "JL");
        paramMap.put("displayLanguage", lang);
        paramMap.put("pageNo", 1);
        paramMap.put("pageSize", 500); //一次全部查询完, 目前有186个游戏0
        //可以不传 paramMap.put("currency", "CNY");
        log.info("{} JILI游戏列表, url: {}, 参数: {}", VenueEnum.JILI_03.getVenueName(), url, JSON.toJSONString(paramMap));
        String signature = "";
        try {
            signature = Signature(venueInfoVO.getAesKey(), JSON.toJSONString(paramMap));
        } catch (Exception e) {
            log.error("{} JILI游戏获取游戏列表签名发生错误, 参数: {}, 签名: {}", VenueEnum.JILI_03.getVenueName(), paramMap, signature);
        }
        try (HttpResponse response = HttpRequest.post(url).header(Header.CONTENT_TYPE, "application/json")
                .header("X-API-Key", venueInfoVO.getMerchantKey())
                .header("X-Signature", signature).timeout(30000)
                .body(JSON.toJSONString(paramMap)).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object status = jsonObject.get("status");
                if (status != null && JILIWalletStatusEnum.SC_OK.name().equals(status.toString())) {
                    return jsonObject.getJSONObject("data");
                } else {
                    log.error("{} 获取游戏列表返回码, 参数: {}, 签名: {} 信息: {} ", VenueEnum.JILI_03.getVenueName(), paramMap, signature, jsonObject);
                }
            }
        } catch (Exception e) {
            log.error("{} 获取游戏列表发生错误!!", VenueEnum.JILI_03.getVenueName(), e);
        }
        return new JSONObject();
    }

    public String Signature(@NotNull String secretKey, String paramStr) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);
        byte[] hashBytes = sha256Hmac.doFinal(paramStr.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    @NotNull
    private static String bytesToHex(@NotNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // 将每个字节转换为两位的十六进制数，并用空格分隔（可选）
        }
        return sb.toString();
    }

    private String getUserAccount(String account) {
        String userAccount = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userAccount)) {
            return null;
        }
        return userAccount;
    }

    public ZfResp auth(ZfAuthReq req, String venueCode) {

        String token = req.getToken();
        if (StringUtils.isEmpty(token)) {
            return ZfResp.fail(ZfErrorCodeEnum.TOKEN_EXPIRED);
        }
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(req.getToken());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return ZfResp.fail(ZfErrorCodeEnum.TOKEN_EXPIRED);
        }


        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return ZfResp.fail(ZfErrorCodeEnum.OTHER_ERR);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        ZfResp resp = ZfResp.success();
        resp.setUsername(casinoMember.getVenueUserAccount());
        resp.setBalance(balance);
        resp.setCurrency(userCenterCoin.getCurrency());
        if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
            resp.setCurrency(KVND);
        }
        return resp;
    }

    public ZfBetResp bet(ZfBetReq req, String venueCode) {

        if (!req.valid()) {
            log.error("zf bet error invalid parameter req:{}", req);
            return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
        }


        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
        try {
            if (!rLock.tryLock()) {
                log.error("zf bet error get locker error, req:{}", req);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setCasinoPassword(req.getToken());
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("zf bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
            }

            if (venueMaintainClosed(venueCode,userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueCode);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }

            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("zf bet error user game lock userName[{}].", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
                log.error("zf bet error currency support usd only; req:{}", req);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0) {
                log.error("zf bet error wallet is not exist user account:{}.", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
            }
            BigDecimal betAmount = req.getBetAmount();
            BigDecimal winloseAmount = req.getWinloseAmount();


            // 账变
            // 交易订单No为  round
            String txid = req.getRound();
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, txid, betAmount,winloseAmount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ZfBetResp resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO -> ZfBetResp.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
                    zfBetResp.setTxId(txid);
                    yield zfBetResp;
                }
                case REPEAT_TRANSACTIONS -> {
                    ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
                    zfBetResp.setTxId(txid);
                    yield zfBetResp;
                }
            };
            UserCoinWalletVO afterCoin = getUserCenterCoin(userId);
            resp.setBalance(afterCoin.getTotalAmount());
            resp.setUsername(casinoMember.getVenueUserAccount());
            resp.setCurrency(userCenterCoin.getCurrency());
//          无需处理 update by xiaozhi 20250304
//            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
//                resp.setCurrency(KVND);
//            }

            /*if (!resp.isOk() || betAmount.compareTo(BigDecimal.ZERO) == 0 && (req.getIsFreeRound() != null || req.getFreeSpinData() != null)) {
                // 如果投注金额为0，但是不是免费旋转则是其他活动派彩，不需要发注单信息mq
                return resp;
            }*/
            if (!resp.isOk()){
                return resp;
            }
            // 注单发送
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setBetTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setThirdOrderId(txid);
            orderRecordMqVO.setSettleTime(req.getWagersTime() == null ? null : req.getWagersTime() * 1000);
            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
//            orderRecordMqVO.setGameType(req.getGameCategory().toString());
            orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
            orderRecordMqVO.setOrderStatus(getOrderStatus(orderRecordMqVO.getWinLossAmount()));
            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("zf bet error ", e);
            return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }



    public CoinRecordResultVO updateBalancePayout(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));


        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
        userCoinAddVO.setThirdOrderNo(transactionId);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }



    public ZfCancelBetResp cancelBet(ZfCancelBetReq req, String venueCode) {

        if (!req.valid()) {
            log.error("zf cancel bet error invalid parameter req:{}", req);
            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
        }
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
        try {
            if (!rLock.tryLock()) {
                log.error("zf bet error get locker error, req:{}", req);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setCasinoPassword(req.getToken());
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
            }

            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("zf cancel bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
                log.error("zf cancel bet error currency support usd only; req:{}", req);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
            }
            BigDecimal transferAmount = req.getBetAmount().subtract(req.getWinloseAmount());
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().add(transferAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.error("zf cancel bet error wallet is not exist user account:{}.", userId);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
            }

            // 账变
            // 交易订单No为  round
            String txid = req.getRound();
            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, txid,
                    transferAmount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ZfCancelBetResp resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO -> ZfCancelBetResp.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
                    zfBetResp.setTxId(txid);
                    yield zfBetResp;
                }
                case REPEAT_TRANSACTIONS -> {
                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.ALREADY_CANCELED);
                    zfBetResp.setTxId(req.getRound());
                    yield zfBetResp;
                }
            };
            UserCoinWalletVO afterCoin = getUserCenterCoin(userInfoVO.getUserId());
            resp.setBalance(afterCoin.getTotalAmount());
            resp.setUsername(casinoMember.getVenueUserAccount());
            resp.setCurrency(userCenterCoin.getCurrency());
            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
                resp.setCurrency(KVND);
            }
            if (!resp.isOk()) {
                return resp;
            }
            // 注单发送
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + req.getRound());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setThirdOrderId(req.getRound());
            // 重结算
            orderRecordMqVO.setOrderClassify(ClassifyEnum.RESETTLED.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());

            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));


            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("zf cancel bet error ", e);
            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    public ZfBetResp sessionBet(ZfSessionBetReq req, String venueCode) {

        if (!req.valid()) {
            log.error("zf session bet error invalid parameter req:{}", req);
            return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
        }

        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getSessionId()));
        try {
            boolean lockFlag = rLock.tryLock();
            if (!lockFlag) {
                log.error("zf session bet error get locker error,lockerId:{}", req.getSessionId());
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setCasinoPassword(req.getToken());
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())){
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("zf session bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            // 游戏锁定
            if (UserStatusEnum.GAME_LOCK.getCode().equals(userInfoVO.getAccountStatus())) {
                log.error("zf bet error user game lock userName[{}].", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
            }
            if (!req.getCurrency().equals("kVND") && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
                log.error("zf session bet error currency support usd only; req:{}", req);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.INVALID_PARAMETER);
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if ("1".equals(req.getType()) && (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getBetAmount()) < 0)) {
                log.error("zf session bet error wallet is not exist user account:{}.", userId);
                return ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
            }

            // 1=下注 2=结算
            String type = req.getType();
            Integer gameCategory = req.getGameCategory();
            String txid = req.getSessionId();
            if (gameCategory != null && (gameCategory == 8 || gameCategory == 2)){
                txid = req.getSessionId() + "_" + req.getRound();
            }
            ZfBetResp resp = null;
            if (CommonConstant.business_one.toString().equals(type)) {
                BigDecimal transferAmount = req.getBetAmount();
                // 下注
                if (req.getPreserve().compareTo(BigDecimal.ZERO) > 0) {
                    // 有preserve
                    transferAmount = req.getPreserve();
                }


                UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                userCoinAddVO.setOrderNo(txid);
                userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setUserId(userInfoVO.getUserId());
                userCoinAddVO.setCoinValue(transferAmount.abs());
                userCoinAddVO.setRemark(null);
                userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
                userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
                userCoinAddVO.setThirdOrderNo(txid);
                //修改余额 记录账变
                CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
                resp = switch (resultStatus) {
                    case SUCCESS -> ZfBetResp.success();
                    case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, AMOUNT_LESS_ZERO, FAIL -> {
                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.NOT_ENOUGH_BALANCE);
                        zfBetResp.setTxId(txid);
                        yield zfBetResp;
                    }
                    case REPEAT_TRANSACTIONS -> {
                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
                        zfBetResp.setTxId(txid);
                        yield zfBetResp;
                    }
                };

                UserCoinWalletVO afterCoin = getUserCenterCoin(casinoMember.getUserId());
                resp.setBalance(afterCoin.getTotalAmount());
                if (!resp.isOk()) {
                    return resp;
                }

            } else if (CommonConstant.business_two.toString().equals(type)) {
                // 结算
                BigDecimal transferAmount = req.getWinloseAmount();
                if (req.getPreserve().compareTo(BigDecimal.ZERO) > 0) {
                    transferAmount = req.getPreserve().subtract(req.getBetAmount()).add(req.getWinloseAmount());
                }
                CoinRecordResultVO coinRecordResultVO = this.updateBalancePayout(userInfoVO, txid, transferAmount);
                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
                resp = switch (resultStatus) {
                    case SUCCESS, AMOUNT_LESS_ZERO -> ZfBetResp.success();
                    case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
                        zfBetResp.setTxId(txid);
                        yield zfBetResp;
                    }
                    case REPEAT_TRANSACTIONS -> {
                        ZfBetResp zfBetResp = ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
                        zfBetResp.setTxId(txid);
                        yield zfBetResp;
                    }
                };
                UserCoinWalletVO afterCoin = getUserCenterCoin(casinoMember.getUserId());
                resp.setBalance(afterCoin.getTotalAmount());
                resp.setUsername(casinoMember.getVenueUserAccount());
                resp.setCurrency(userCenterCoin.getCurrency());
                if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
                    resp.setCurrency(KVND);
                }
                if (!resp.isOk()) {
                    return resp;
                }
            }
            // 注单发送
            Integer orderClassify = ClassifyEnum.NOT_SETTLE.getCode();
            Integer orderStatus = OrderStatusEnum.NOT_SETTLE.getCode();
            Long betTime = null;
            Long settleTime = null;
            BigDecimal betAmount = null;
            BigDecimal winloseAmount = BigDecimal.ZERO;
            if (CommonConstant.business_one.toString().equals(type)) {
                // 投注
                betTime = req.getWagersTime();
                betAmount = req.getBetAmount();
            } else {
                // 结算
                settleTime = req.getWagersTime();
                winloseAmount = req.getWinloseAmount();
                orderClassify = ClassifyEnum.SETTLED.getCode();
                betAmount = req.getSessionTotalBet();
            }
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");


            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setThirdOrderId(req.getSessionId());
            orderRecordMqVO.setSettleTime(settleTime == null ? null : settleTime * 1000);
            orderRecordMqVO.setBetTime(betTime == null ? null : betTime * 1000);
            if(orderClassify.equals(ClassifyEnum.SETTLED.getCode())){
                orderRecordMqVO.setPayoutAmount(winloseAmount);
                orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(betAmount));
                orderStatus = getOrderStatus(req.getWinloseAmount().subtract(betAmount));
            }
            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
//            orderRecordMqVO.setGameType(req.getGameCategory().toString());
            orderRecordMqVO.setOrderClassify(orderClassify);
            orderRecordMqVO.setOrderStatus(orderStatus);
            orderRecordMqVO.setBetAmount(betAmount);
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("zf session bet error ", e);
            return ZfBetResp.fail(ZfBetErrorCodeEnum.OTHER_ERR);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
    private Integer getOrderStatus(BigDecimal winloseAmount){
        if (winloseAmount.compareTo(BigDecimal.ZERO) == 0){
            return OrderStatusEnum.DRAW.getCode();
        } else if (winloseAmount.compareTo(BigDecimal.ZERO) > 0) {
            return OrderStatusEnum.WIN.getCode();
        } else if (winloseAmount.compareTo(BigDecimal.ZERO) < 0) {
            return OrderStatusEnum.LOSS.getCode();
        }
        return OrderStatusEnum.SETTLED.getCode();
    }

    public ZfCancelBetResp cancelSessionBet(ZfCancelSessionBetReq req, String venueCode) {


        if (!req.valid()) {
            log.error("zf cancel bet error invalid parameter req:{}", req);
            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
        }

        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameZfBetLockKey(req.getRound()));
        try {
            if (!rLock.tryLock()) {
                log.error("zf bet error get locker error, req:{}", req);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setCasinoPassword(req.getToken());
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("zf cancel bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.TOKEN_EXPIRED);
            }
            if (venueMaintainClosed(venueCode,userInfoVO.getSiteCode())) {
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
            }
            if (!"kVND".equals(req.getCurrency()) && !userInfoVO.getMainCurrency().equals(req.getCurrency())) {
                log.error("zf cancel bet error currency support usd only; req:{}", req);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.INVALID_PARAMETER);
            }

            BigDecimal transferAmount = req.getBetAmount().subtract(req.getWinloseAmount());
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().add(transferAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.error("zf cancel bet error wallet is not exist user account:{}.", userId);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
            }
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(req.getSessionId());
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isEmpty(userCoinBetRecRespVO)) {
                log.error("zf cancel bet error feign error or order not exsit.feign resp{}.", userCoinBetRecRespVO);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.ROUND_NOT_FOUND);
            }
            coinRecordRequestVO.setOrderNo("2" + req.getSessionId());
            List<UserCoinRecordVO> userCoinRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinRecRespVO)) {
                log.error("zf cancel bet error feign error or already settled.feign resp{}.", userCoinRecRespVO);
                return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
            }
            // 账变
            // 交易订单No为  sesssionId
            String txid = req.getSessionId() + "_" + req.getRound();
            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, txid, transferAmount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ZfCancelBetResp resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO -> ZfCancelBetResp.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.CANNOT_CANCELED);
                    zfBetResp.setTxId(txid);
                    yield zfBetResp;
                }
                case REPEAT_TRANSACTIONS -> {
                    ZfCancelBetResp zfBetResp = ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);
                    zfBetResp.setTxId(req.getRound());
                    yield zfBetResp;
                }
            };
            UserCoinWalletVO afterCoin = getUserCenterCoin(userId);
            resp.setBalance(afterCoin.getTotalAmount());
            resp.setUsername(casinoMember.getVenueUserAccount());
            resp.setCurrency(userCenterCoin.getCurrency());
            if (userCenterCoin.getCurrency().equals(CurrencyEnum.KVND.getCode())){
                resp.setCurrency(KVND);
            }
            if (!resp.isOk()) {
                return resp;
            }

            // 注单发送
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setPayoutAmount(req.getWinloseAmount());
            orderRecordMqVO.setWinLossAmount(req.getWinloseAmount().subtract(req.getBetAmount()));
            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + req.getRound());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setThirdOrderId(req.getSessionId());
            // 重结算
            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
            orderRecordMqVO.setGameCode(String.valueOf(req.getGame()));

            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("zf cancel bet error ", e);
            return ZfCancelBetResp.fail(ZfCancelBetErrorCodeEnum.OTHER_ERR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.NEW_JILI);
        userCoinAddVO.setThirdOrderNo(transactionId);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

}
