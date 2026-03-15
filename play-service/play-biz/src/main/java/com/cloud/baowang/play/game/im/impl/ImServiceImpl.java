package com.cloud.baowang.play.game.im.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.im.ImReq;
import com.cloud.baowang.play.api.vo.im.ImResp;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.GameNameVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;


@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.IM)
public class ImServiceImpl extends GameBaseService implements GameService {

    private OrderRecordProcessService orderRecordProcessService;
    private  final static String SUCCESS_STATUS = "success";


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    public static void main(String[] args) {
        String url = "https://tbs2api.aslot.net/"+"/API/exitUrl/";
        Map<String, String> prams = new HashMap<>();
        prams.put("cmd","openGame");
        prams.put("hall","3205941");
        prams.put("domain","HTTPS");
        prams.put("key","Cn8RuEBr4NMvPwyjLftk");
        prams.put("gameId","6909");//指定子游戏进入
        prams.put("language","en");
        prams.put("login","test001");
        prams.put("demo","0");
        prams.put("cdnUrl","0");

        log.info("IM-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, JSONObject.toJSONString(prams));
            log.info("IM-进入游戏response={}", response);
        }catch (Exception e){

        }
    }

    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        return null;
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        String url = venueDetailVO.getApiUrl()+"/API/openGame/";
        Map<String, String> prams = new HashMap<>();
        prams.put("cmd","openGame");
        prams.put("hall",venueDetailVO.getMerchantNo());
        prams.put("domain","HTTPS");
        prams.put("key",venueDetailVO.getMerchantKey());
        prams.put("gameId",loginVO.getGameCode());//指定子游戏进入
        prams.put("language",StringUtils.isNotEmpty(LANGUAGE_MAP.get(loginVO.getLanguageCode()))?LANGUAGE_MAP.get(loginVO.getLanguageCode()):"en");
        prams.put("login",casinoMemberVO.getVenueUserAccount());
        prams.put("demo","0");
        prams.put("cdnUrl","0");

        log.info("IM-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, JSONObject.toJSONString(prams));
            log.info("IM-进入游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            String linkUrl = jsonObject.getJSONObject("content").getJSONObject("game").getString("url");
            if (StringUtils.isEmpty(linkUrl)) {
                log.error("im 登录返回信息错误 {}", response);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(linkUrl)
                    .type(GameLoginTypeEnums.URL.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.IM.getVenueCode())
                    .build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("IM-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    @Override
    public ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        if (CollectionUtil.isEmpty(orderRecordMqVOList)) {
            return ResponseVO.success();
        }
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenuePlatformConstants.IM);

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
            log.info("IM游戏用户账号不存在{}", userIds);
            return null;
        }

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
        // 场馆游戏配置
        Map<String, GameInfoPO> gameInfoMap = super.getGameInfoByVenueCode(VenuePlatformConstants.IM);
        if (CollUtil.isEmpty(gameInfoMap)) {
            log.error("IM游戏列表未配置");
            return null;
        }
        Integer venueType = VenueEnum.IM.getType().getCode();
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
            orderRecord.setCurrency(userInfoVO.getMainCurrency());
            orderRecord.setUserAccount(userInfoVO.getUserAccount());
            orderRecord.setUserId(userInfoVO.getUserId());
            orderRecord.setUserName(userInfoVO.getUserName());
            orderRecord.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecord.setAgentId(userInfoVO.getSuperAgentId());
            orderRecord.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecord.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecord.setVenueType(venueType);
            orderRecord.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecord.setVipRank(userInfoVO.getVipRank());
            orderRecord.setBetIp(userLoginInfoVO.getIp());
            orderRecord.setSiteCode(userInfoVO.getSiteCode());
            orderRecord.setSiteName(siteNameMap.get(orderRecord.getSiteCode()));
            if (userLoginInfoVO.getLoginTerminal() != null) {
                orderRecord.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
            if (orderRecordMqVO.getWinLossAmount().compareTo(BigDecimal.ZERO) == 0) {
                // orderRecord.setWinLossAmount(orderRecord.getBetAmount().negate());
            }
            BigDecimal validBetAmount = computerValidBetAmount(orderRecord.getBetAmount(), orderRecord.getWinLossAmount(), VenueTypeEnum.ELECTRONICS);
            orderRecord.setValidAmount(validBetAmount);
            String gameCode = orderRecord.getGameCode();
            GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
            if (gameInfoPO != null) {
                orderRecord.setGameId(gameInfoPO.getGameId());
                orderRecord.setGameName(gameInfoPO.getGameI18nCode());
            }
            orderRecord.setOrderId(OrderUtil.getGameNo());
            orderRecord.setTransactionId(orderRecord.getThirdOrderId());
            orderRecordVOS.add(orderRecord);
        }
        orderRecordProcessService.orderProcess(orderRecordVOS);

        return null;
    }

    @Override
    public List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueDetailVO) {
        List<ThirdGameInfoVO> ret = Lists.newArrayList();
        String url = venueDetailVO.getApiUrl()+"/API/";
        Map<String, String> prams = new HashMap<>();
        prams.put("cmd","getGamesList");
        prams.put("hall",venueDetailVO.getMerchantNo());
        prams.put("key",venueDetailVO.getMerchantKey());

        log.info("IM-获取游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, JSONObject.toJSONString(prams));
            log.info("IM-获取游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (Objects.isNull(jsonObject)){
                return ret;
            }
            if (!SUCCESS_STATUS.equals(jsonObject.getString("status"))){
                return ret;
            }
            JSONObject sortList = jsonObject.getJSONObject("content");
            JSONArray gameArr = sortList.getJSONArray("ainsworth");
            for (int i = 0; i < gameArr.size(); i++) {
                ThirdGameInfoVO thirdGameInfoVO = new ThirdGameInfoVO();
                JSONObject game = gameArr.getJSONObject(i);
                thirdGameInfoVO.setGameCode(game.getString("id"));
                List<GameNameVO> gameNameVOS = Lists.newArrayList();
                String enUsName = game.getString("name");
                if (StringUtil.isNotEmpty(enUsName)){
                    GameNameVO gameNameVO = new GameNameVO();
                    gameNameVO.setGameName(enUsName);
                    gameNameVO.setLang(LanguageEnum.ZH_CN.getLang());
                    gameNameVOS.add(gameNameVO);

                    GameNameVO gameNameVOEn = new GameNameVO();
                    gameNameVOEn.setGameName(enUsName);
                    gameNameVOEn.setLang(LanguageEnum.EN_US.getLang());
                    gameNameVOS.add(gameNameVOEn);
                }
                thirdGameInfoVO.setGameName(gameNameVOS);
                ret.add(thirdGameInfoVO);
            }

        } catch (Exception e) {
            log.error("IM-获取游戏异常",  e);
        }
        return ret;
    }

    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount){


        return ResponseVO.success(true);
    }


    private static final Map<String,String> LANGUAGE_MAP = new HashMap<>();
    static {
        LANGUAGE_MAP.put("zh-CN", "zh");
        LANGUAGE_MAP.put("en-US", "en");
        LANGUAGE_MAP.put("hi-IN", "hi");
        LANGUAGE_MAP.put("id-ID", "id");
        LANGUAGE_MAP.put("pt-BR", "pt");
        LANGUAGE_MAP.put("zh-TW", "en");
        LANGUAGE_MAP.put(LanguageEnum.KO_KR.getLang(), "ko");
        LANGUAGE_MAP.put(LanguageEnum.JA_JP.getLang(), "ja");
    }


    private final static String  KVND = "VNDK";


    public ImResp getBalance(ImReq request) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getLogin());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.IM);
        CasinoMemberVO casinoMemberData = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (Objects.isNull(casinoMemberData)){
            return ImResp.err("user not exist");
        }
        if (venueMaintainClosed(VenuePlatformConstants.IM,casinoMemberData.getSiteCode())){
            log.info("{}:场馆未开启", VenueEnum.IM.getVenueName());
            return ImResp.err("venue not open");
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMemberData.getUserId());
        if (Objects.isNull(userCenterCoin)) {
            return ImResp.err("wallet not exist");
        }
        ImResp ret = ImResp.success();
        ret.setBalance(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN));
        ret.setLogin(request.getLogin());
        ret.setCurrency(userCenterCoin.getCurrency());
        if(CurrencyEnum.KVND.getCode().equals(userCenterCoin.getCurrency())){
            ret.setCurrency(KVND);
        }

        return ret;
    }

    public ImResp writeBet(ImReq request) {
    ExecutorService executor = Executors.newCachedThreadPool();

        Future<ImResp> future = executor.submit(() -> doWriteBet(request));
        try {
            // 设置超时时间为 6 秒
            return future.get(6, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("writeBet 超时，触发 fallback, {}", e.getMessage());
            future.cancel(true);
            return ImResp.err("system err");
        } catch (Exception e) {
            log.error("writeBet 异常，触发 fallback {}", e.getMessage());
            return ImResp.err("system err");
        }
    }

    public ImResp doWriteBet(ImReq request) {

        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.IM, request.getSessionId()));
        try {
            if (!rLock.tryLock()) {
                log.error("im bet error get locker error, req:{}", request);
                return ImResp.err("system err");
            }

            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(request.getLogin());
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.IM);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);

            if (venueMaintainClosed(VenuePlatformConstants.IM,casinoMember.getSiteCode())){
                log.info("{}:场馆未开启", VenueEnum.IM.getVenueName());
                return ImResp.err("venue not open");
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("im bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ImResp.err("user_not_found");
            }
            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("im bet error user game lock userName[{}].", userId);
                return ImResp.err("game lock");
            }

            if (venueMaintainClosed(VenuePlatformConstants.IM,userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenuePlatformConstants.IM);
                return ImResp.err("game lock");
            }


            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(request.getBet()) < 0) {
                log.error("im bet error wallet is not exist user account:{}.", userId);
                return ImResp.err("fail_balance");
            }
            BigDecimal winloseAmount = request.getWin();
            BigDecimal betAmount = request.getBet();

            // 账变
            // 交易订单No为  sessionId
            String txid = request.getTradeId();
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOut(userInfoVO, txid, betAmount, winloseAmount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ImResp resp = switch (resultStatus) {
                case SUCCESS ,AMOUNT_LESS_ZERO -> ImResp.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    yield ImResp.err("fail_balance");
                }
                case REPEAT_TRANSACTIONS -> {
                    yield ImResp.err("already accepted");
                }
            };


            UserCoinWalletVO afterCoin = getUserCenterCoin(userId);
            resp.setBalance(afterCoin.getTotalAmount());
            resp.setLogin(casinoMember.getVenueUserAccount());
            resp.setCurrency(afterCoin.getCurrency());
            if(CurrencyEnum.KVND.getCode().equals(afterCoin.getCurrency())){
                resp.setCurrency(KVND);
            }

            if(!resp.isOk()){
                return resp;
            }

            // 注单
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(request, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setThirdOrderId(txid);
            String dateStr = request.getDate();
            if (StringUtils.isNotEmpty(dateStr)){
                orderRecordMqVO.setBetTime(System.currentTimeMillis());
                try {
                    Long date = DateUtils.parseDate(dateStr, "UTC-0").getTime();
                    orderRecordMqVO.setSettleTime(date);
                }catch (Exception e){
                    log.error("im 注单时间解析错误, ",e);
                }
            }
            orderRecordMqVO.setBetAmount(request.getBet());

            winloseAmount = Objects.isNull(winloseAmount)?BigDecimal.ZERO:winloseAmount;
            orderRecordMqVO.setPayoutAmount(winloseAmount);
            orderRecordMqVO.setWinLossAmount(winloseAmount.subtract(request.getBet()));
            orderRecordMqVO.setValidAmount(betAmount);

            orderRecordMqVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecordMqVO.setAgentId(userInfoVO.getSuperAgentId());
            orderRecordMqVO.setThirdGameCode(request.getGameId());
            orderRecordMqVO.setGameCode(request.getGameId());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(request));
            orderRecordMqVO.setBetContent(request.getBetInfo());
            orderRecordMqVO.setSiteCode(casinoMember.getSiteCode());
            orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("im bet error ", e);
            return ImResp.err("system err");
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

}
