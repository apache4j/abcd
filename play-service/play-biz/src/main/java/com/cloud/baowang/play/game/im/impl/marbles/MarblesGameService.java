package com.cloud.baowang.play.game.im.impl.marbles;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.marbles.MarblesRespErrEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.marbles.*;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.im.impl.resp.marbles.MarblesOrderDataRespVO;
import com.cloud.baowang.play.game.im.impl.resp.marbles.MarblesOrderRespVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
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
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.cloud.baowang.common.core.utils.DateUtils.FULL_FORMAT_4;


/**
 * IM 弹珠游戏
 */
@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.MARBLES)
public class MarblesGameService extends GameBaseService implements GameService {
    @Autowired
    private OrderRecordProcessService orderRecordProcessService;

    // 时区：GMT+8 (东8区)
    public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main2(String[] args) {
        String traceId = "trace_id=" + UUID.randomUUID();
        String api = "Game/NewLaunchGame?" + traceId;
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        params.put("MerchantCode","");
        params.put("PlayerId","");
        params.put("Currency","CNY");
        params.put("GameCode","");
        params.put("Language","zh_CN");
        params.put("IpAddress","127.0.0.1");
//        params.put("ProductWallet",501);
        params.put("IsDownload",0);

        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(respones);
        System.out.println(jsonObject);
    }

    public boolean checkExists(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO){
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        String api = venueDetailVO.getApiUrl() + "/Player/CheckExists";
        // 运营商唯一代码
        params.put("MerchantCode",venueDetailVO.getMerchantNo());
        params.put("PlayerId",casinoMemberVO.getVenueUserAccount());
        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        log.info("IM弹珠检查用户是否存在 入参params={},url={};respones={}",params,api,respones);
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (Objects.nonNull(jsonObject) && (jsonObject.getIntValue("Code") == 503 || jsonObject.getIntValue("Code") == 0)){
            log.error("IM弹珠检查用户是存在,参数：venueCode={}, 响应参数={}",venueDetailVO.getVenueCode(),respones);
            return true;
        }else{
            return false;
        }
    }


    public void register(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO){
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        String api = venueDetailVO.getApiUrl() + "/Player/Register";
        // 运营商唯一代码
        params.put("MerchantCode",venueDetailVO.getMerchantNo());
        params.put("PlayerId",casinoMemberVO.getVenueUserAccount());
        changeLoginCurrency(loginVO);
        params.put("Currency",loginVO.getCurrencyCode());
        params.put("Password",casinoMemberVO.getCasinoPassword());
        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        log.info("IM弹珠注册用户入参params={},url={},{}",params,api,respones);
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (Objects.nonNull(jsonObject) && jsonObject.getIntValue("Code") != 0){
            log.error("IM弹珠注册用参数：venueCode={}, 响应参数={}",venueDetailVO.getVenueCode(),respones);
        }
    }

    private Boolean checkCasinoMemberVO(CasinoMemberVO casinoMemberVO) {
        CasinoMemberReq casinoMemberReq = new CasinoMemberReq();
        casinoMemberReq.setVenueUserAccount(casinoMemberVO.getVenueUserAccount());
        casinoMemberReq.setVenueCode(VenueEnum.MARBLES.getVenueCode());
        CasinoMemberVO respVO = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (ObjectUtil.isNotEmpty(respVO)) {
            return false;
        }else{
            String token = casinoMemberVO.getVenueUserAccount();
            String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.MARBLES.getVenueCode(), token);
            RedisUtil.deleteKey(key);
            RedisUtil.setValue(key, casinoMemberVO, 60L, TimeUnit.MINUTES);
            return true;
        }
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        if(checkCasinoMemberVO(casinoMemberVO)){
            register(loginVO, venueDetailVO, casinoMemberVO);
        }
        Integer reqDeviceType = CurrReqUtils.getReqDeviceType();
        log.info("im登录设备类型reqDeviceType={}",reqDeviceType);
        String api = null;
        if(Objects.isNull(reqDeviceType) || reqDeviceType == DeviceType.PC.getCode()){
            api = venueDetailVO.getApiUrl() + "/Game/NewLaunchGame";
        }else{
            api = venueDetailVO.getApiUrl() + "/Game/NewLaunchMobileGame";
        }
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        // 运营商唯一代码
        params.put("MerchantCode",venueDetailVO.getMerchantNo());
        params.put("PlayerId",casinoMemberVO.getVenueUserAccount());

        switch (loginVO.getCurrencyCode()){
            case "KVND":loginVO.setCurrencyCode(CurrencyEnum.VND.getCode());break;
            case "USDT":case "USD":loginVO.setCurrencyCode("UST");break;
            default:  break;
        }
        changeLoginCurrency(loginVO);
        params.put("Currency",loginVO.getCurrencyCode());
        params.put("GameCode",loginVO.getGameCode());
        params.put("Language",StringUtils.isNotEmpty(LANGUAGE_MAP.get(loginVO.getLanguageCode()))?LANGUAGE_MAP.get(loginVO.getLanguageCode()):"EN");
        params.put("IpAddress",loginVO.getIp());
        params.put("ProductWallet",venueDetailVO.getBetKey());
        params.put("IsDownload",0);

        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        log.info("IM登录入参params={},url={}",params,api);
        log.info("IM弹珠返回的内容{}",respones);
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (jsonObject == null || jsonObject.getIntValue("Code") != CommonConstant.business_zero){
            log.error("IM弹珠游戏登录失败,参数：venueCode={}, 响应参数={}",venueDetailVO.getVenueCode(),respones);
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        GameLoginVo gameLoginVO = GameLoginVo.builder().source(jsonObject.getString("GameUrl"))
                .type(GameLoginTypeEnums.URL.getType())
                .userAccount(loginVO.getUserAccount())
                .venueCode(venueDetailVO.getVenueCode())
                .build();
        return ResponseVO.success(gameLoginVO);

    }

    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        params.put("MerchantCode",venueDetailVO.getMerchantNo());
        params.put("PlayerId",venueUserAccount);
        params.put("ProductWallet",venueDetailVO.getBetKey());

        String api = venueDetailVO.getApiUrl() + "/Player/TerminateSession";
        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(respones);
        log.info("marbles登出{}, params={}",jsonObject,params);
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        log.info("弹珠获取投注记录参数：{}",venueInfoVO);
        String venueCode = venueInfoVO.getVenueCode();
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueCode);
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueCode);
        int page = 1;
        int pageSize = 300;
        try {
            while (true) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("MerchantCode", venueInfoVO.getMerchantNo());
                params.put("StartDate", DateUtils.formatDateByZoneId(venuePullParamVO.getStartTime(),FULL_FORMAT_4,"GMT+8"));
                params.put("EndDate", DateUtils.formatDateByZoneId(venuePullParamVO.getEndTime(),FULL_FORMAT_4,"GMT+8"));
                params.put("Page", page);
                params.put("PageSize", pageSize);
                String currency = null ;
                switch (venueInfoVO.getPullCurrencyCodeList().get(0)){
                    case "KVND": currency = CurrencyEnum.VND.getCode();break;
                    case "USDT","USD": currency = "UST";break;
                    default: currency = venueInfoVO.getPullCurrencyCodeList().get(0); break;
                }
                params.put("Currency", currency);
                params.put("ProductWallet", venueInfoVO.getBetKey());
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Content-Type","application/json; charset=utf-8");
                String traceId = "trace_id=" + UUID.randomUUID();
                String url = venueInfoVO.getBetUrl() + "/Report/GetBetLog";
                headers.put("sign",getSign(traceId,JSONObject.toJSONString(params), venueInfoVO.getMerchantKey()));
                log.info("{} 拉单参数, url:{},参数：{}", venueCode, url, params);

                String response = HttpClientHandler.post(url,headers, JSONObject.toJSONString(params));
                JSONObject jsonObject = JSONObject.parseObject(response);
                log.info("marbles json={}",jsonObject);
                if (jsonObject == null && (jsonObject.getIntValue("Code") != CommonConstant.business_zero
                || jsonObject.getIntValue("Code") != 558)) {
                    log.error("{} 拉取注返回异常，返回：{}", venueCode, response);
                    break;
                }
                MarblesOrderRespVO marblesOrderRespVO = JSONObject.parseObject(response, MarblesOrderRespVO.class);
                if (CollectionUtil.isEmpty(marblesOrderRespVO.getResult())) {
                    log.info("{} 拉取注单返回为空", venueCode);
                    break;
                }
                log.info("{} 拉取注单 返回:{}", venueCode,marblesOrderRespVO);
                List<MarblesOrderDataRespVO> betData = marblesOrderRespVO.getResult();
                int size = betData.size();

                // 场馆用户关联信息
                List<String> usernames = betData.stream().map(MarblesOrderDataRespVO::getPlayerId).distinct().toList();
                Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueInfoVO.getVenuePlatform());
                if (MapUtil.isEmpty(casinoMemberMap)) {
                    log.info("{}未找到三方关联信息 玩家列表{}",venueCode, usernames);
                    break;
                }
                // 用户信息
                List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
                Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
                if (CollUtil.isEmpty(userMap)) {
                    log.info("{}游戏用户账号不存在{}",venueCode, userIds);
                    break;
                }
                // 用户登录信息
                Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

                List<OrderRecordVO> list = Lists.newArrayList();
                Map<String, String> siteNameMap = getSiteNameMap();
                for (MarblesOrderDataRespVO orderResponseVO : betData) {
                    CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getPlayerId());
                    if (casinoMemberVO == null) {
                        log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), orderResponseVO.getPlayerId());
                        continue;
                    }
                    UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                    if (userInfoVO == null) {
                        log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
                        continue;
                    }

                    UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                    // 映射原始注单
                    OrderRecordVO recordVO = parseRecords(venueInfoVO, orderResponseVO, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap,paramToGameInfo);
                    //recordVO.setVenueType(gameTypeId);
                    recordVO.setVenueType(VenueEnum.MARBLES.getType().getCode());
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
            log.error("im 执行拉单异常", e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }

    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, MarblesOrderDataRespVO orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBetAmount());
        recordVO.setOrderInfo(orderResponseVO.getBetDetails());
        recordVO.setGameNo(orderResponseVO.getGameNo());
        recordVO.setOdds(orderResponseVO.getOdds());
        recordVO.setPlayInfo(orderResponseVO.getBetOn());
        Long betTime = TimeZoneUtils.parseDate4TimeZoneToTime(orderResponseVO.getBetDate(), DateUtils.FULL_FORMAT_1, shanghaiTimeZone);
        recordVO.setBetTime(betTime);
        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getPlayerId());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(orderResponseVO.getBetId());
        recordVO.setWinLossAmount(orderResponseVO.getWinLoss());
        recordVO.setPayoutAmount(orderResponseVO.getBetAmount().add(orderResponseVO.getWinLoss()));
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(orderResponseVO.getSettlementDate(), DateUtils.FULL_FORMAT_1, shanghaiTimeZone);
        recordVO.setSettleTime(settleTime);
        recordVO.setOrderStatus(getOrderStatus(orderResponseVO.getStatus()));
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());

        BigDecimal validBetAmount = super.computerValidBetAmount(recordVO.getBetAmount(), recordVO.getWinLossAmount(), VenueTypeEnum.MARBLES);
        recordVO.setValidAmount(validBetAmount);

        String gameCode = orderResponseVO.getGameId();
        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
        recordVO.setThirdGameCode(gameCode);
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
        }
        recordVO.setTransactionId(recordVO.getThirdOrderId());
        return recordVO;
    }

    private Integer getOrderStatus(final String status) {
        if (StringUtils.isNotEmpty(status)) {
            if ("Open".equals(status)) {
                return OrderStatusEnum.NOT_SETTLE.getCode();
            } else if ("Settled".equals(status)) {
                return OrderStatusEnum.SETTLED.getCode();
            } else if ("Cancelled".equals(status)) {
                return OrderStatusEnum.CANCEL.getCode();
            } else if ("Adjusted".equals(status)) {
                return OrderStatusEnum.RESETTLED.getCode();
            }
        }
        return null;
    }


    private static String getSign(String urlParam, String jsonParam, String key){
        return MD5Util.md5(urlParam + jsonParam + key ).toLowerCase();
    }


    private static final Map<String,String> LANGUAGE_MAP = new HashMap<>();
    static {
        LANGUAGE_MAP.put(LanguageEnum.ZH_CN.getLang(), LanguageEnum.ZH_CN.getLang().toUpperCase());
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "EN");
        LANGUAGE_MAP.put(LanguageEnum.VI_VN.getLang(), "VI");
        LANGUAGE_MAP.put(LanguageEnum.ZH_TW.getLang(), LanguageEnum.ZH_CN.getLang().toUpperCase());
        LANGUAGE_MAP.put(LanguageEnum.TH_TH.getLang(), LanguageEnum.ZH_CN.getCountryCode().toUpperCase());
        LANGUAGE_MAP.put(LanguageEnum.KO_KR.getLang(), "KO");
        LANGUAGE_MAP.put(LanguageEnum.JA_JP.getLang(), "JP");
        LANGUAGE_MAP.put(LanguageEnum.MS_MY.getLang(), LanguageEnum.ZH_CN.getCountryCode().toUpperCase());
    }




    private void changeLoginCurrency(LoginVO loginVO){
        switch (loginVO.getCurrencyCode()){
            case "KVND":loginVO.setCurrencyCode(CurrencyEnum.VND.getCode());break;
            case "USDT","USD":loginVO.setCurrencyCode("UST");break;
            default:  break;
        }
    }



    private CasinoMemberVO checkCasinoMemberVO(String playerId) {
        String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.MARBLES.getVenueCode(), playerId);
        CasinoMemberVO casinoMemberVO = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(casinoMemberVO)) {
            return casinoMemberVO;
        }
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(playerId);
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.MARBLES);
        return casinoMemberService.getCasinoMember(casinoMemberReqVO);
    }


    /**
     * 查询余额
     */
    public MarblesBalanceResp getBalance(MarblesReq req) {
        CasinoMemberVO casinoMember = this.checkCasinoMemberVO(req.getPlayerId());
        if (casinoMember == null) {
            return  MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_NOT_EXIST);
        }
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = com.google.common.collect.Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            return MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_INACTIVE);
        }
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return MarblesBalanceResp.fail(MarblesRespErrEnums.PLAYER_INACTIVE);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }
        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        String mainCurrency = userInfoVO.getMainCurrency();

        switch (mainCurrency) {
            case "KVND":
                mainCurrency = CurrencyEnum.VND.getCode();
                break;
            case "USDT", "USD":
                mainCurrency = "UST";
                break;
            default:
                break;
        }

        return MarblesBalanceResp.success(casinoMember.getVenueUserAccount(),mainCurrency,balance.toString());
    }

    public MarblesResp getApproval(MarblesApprovalReq req) {
        return null;
    }


    /**
     * 余额小于投注金额
     *
     * @param amount 投注金额
     */
    protected boolean compareAmount(String userId, BigDecimal amount) {
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        if (userCoin == null) {
            return false;
        }
        log.info("用户可用余额{}, 扣减的余额{}", userCoin.getCenterAmount(), amount);
        log.info("可用和扣减比对{}", userCoin.getCenterAmount().compareTo(BigDecimal.ZERO) > 0 && userCoin.getCenterAmount().compareTo(amount.abs()) >= 0);
        return userCoin.getCenterAmount().compareTo(BigDecimal.ZERO) > 0 && userCoin.getCenterAmount().compareTo(amount.abs()) >= 0;
    }



    /**
     * 下注
     * 不支持一个玩家多笔交易
     *
     * RefTransactionId 是会返回下注时的 TransactionId 哦。贵司需要把 RefTransactionId 和下注时的 TransactionId 进行对比
     */
    public MarblesPlaceBetResp placeBet(MarblesPlaceBetReq req) {
        log.info("im marbles placeBet下注入参{}",JSONObject.toJSONString(req));
        // 经与第三方确认，数组里面只有1条内容
        PlaceBet placeBet = CollectionUtil.isNotEmpty(req.getTransactions()) && req.getTransactions().size() > 0 ?req.getTransactions().get(0):new PlaceBet();
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(placeBet.getPlayerId());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.MARBLES);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null, placeBet.getTransactionId(),null);
        }

        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);
        if(Objects.isNull(userInfoVO)){
            log.error("im marbles queryUserInfoByAccount userName[{}] not find.",req.getSessionToken());
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null, placeBet.getTransactionId(),null);
        }

        String centerAmount=getUserCenterCoin(userInfoVO.getUserId()).getCenterAmount().toString();
        if(venueMaintainClosed(VenueEnum.MARBLES.getVenueCode(),userInfoVO.getSiteCode())){
            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.MARBLES.getVenueCode());
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
        }
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = com.google.common.collect.Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
        }
        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("marbles userName[{}] game lock.", userInfoVO.getUserName());
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
        }

        if (venueGameMaintainClosed(venueCode,casinoMember.getSiteCode(),placeBet.getGameId())){
            log.info("{}:游戏未开启", VenueEnum.NEXTSPIN.getVenueName());
            return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, placeBet.getTransactionId(),null);
        }
        try {
            RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, placeBet.getBetId()));
            if (!rLock.tryLock()) {
                log.error("marbles errorBet error get locker error, req:{}", req);
                return setPlaceBetBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount, req.getSessionToken(),null);
            }

            // 检查余额
            if(!compareAmount(userInfoVO.getUserId(),placeBet.getAmount())){
                log.info("im marbles 用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),placeBet.getAmount());
                return setPlaceBetBuilder(MarblesRespErrEnums.INSUFFICIENT_AMOUNT,centerAmount, placeBet.getTransactionId(),null);
            }
            //用户中心钱包余额
            if(BigDecimal.ZERO.compareTo(placeBet.getAmount()) == 0){
                return setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, placeBet.getTransactionId(),null);
            }



            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(placeBet.getBetId());
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(placeBet.getAmount());
            userCoinAddVO.setRemark(placeBet.getTransactionId());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
            userCoinAddVO.setVenueCode(VenuePlatformConstants.MARBLES);
            userCoinAddVO.setThirdOrderNo(placeBet.getTransactionId());

            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS, AMOUNT_LESS_ZERO -> {
                    yield setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,coinRecordResultVO.getCoinAfterBalance().toString(), placeBet.getTransactionId(),null);
                }
                case REPEAT_TRANSACTIONS->
                        setPlaceBetBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, placeBet.getTransactionId(),null);
                case  INSUFFICIENT_BALANCE, WALLET_NOT_EXIST -> setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,null, placeBet.getTransactionId(),null);
                default -> setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,centerAmount, placeBet.getTransactionId(),null);
            };
        }catch (Exception e){
            log.error("im marbles failed processAdjustBet error {}", e);
            e.printStackTrace();
            return setPlaceBetBuilder(MarblesRespErrEnums.INVALID_TOKEN,centerAmount, placeBet.getTransactionId(),null);
        }
    }


    /**
     * 设置下注反参
     */
    public MarblesPlaceBetResp setPlaceBetBuilder(MarblesRespErrEnums errEnums , String balance, String transactionId, String OperatorTransactionId) {
        MarblesPlaceBetResp placeBetResp = new MarblesPlaceBetResp();
        List<MarblesPlaceBetResp.PlaceBetBuilder> placeBetRespList = new ArrayList<>();
        MarblesPlaceBetResp.PlaceBetBuilder placeBetBuilder = new MarblesPlaceBetResp.PlaceBetBuilder();
        placeBetBuilder.setCode(errEnums.getCode());
        placeBetBuilder.setMessage(errEnums.getDescription());
        placeBetBuilder.setOperatorTransactionId(OperatorTransactionId);
        placeBetBuilder.setBalance(balance);
        placeBetBuilder.setTransactionId(transactionId);
        placeBetRespList.add(placeBetBuilder);
        placeBetResp.setResult(placeBetRespList);
        placeBetResp.setCode(errEnums.getCode());
        placeBetResp.setMessage(errEnums.getDescription());
        return placeBetResp;
    }

    /**
     * 结算(派彩)
     * 结算和取消的接口是会有多笔交易的。会包括不一样的玩家注单
     *
     * 我方查看后是先 Settle 注单后，会再呼叫多一次 Commission 的 SettleBet
     */
    public MarblesRefundResp settleBet(MarblesSettleBetReq req) {
        log.info("弹珠settleBet req:{}", JSONObject.toJSONString(req));
        List<SettleBetReq> settleBetList = req.getTransactions();
        MarblesRefundResp refundResp = new MarblesRefundResp();
        if(CollectionUtil.isNotEmpty(settleBetList)){
            List<MarblesRefundResp.RefundBuilder> result = new ArrayList<>();
            for (SettleBetReq settleBetReq : settleBetList) {
                CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
                casinoMemberReqVO.setVenueUserAccount(settleBetReq.getPlayerId());
                casinoMemberReqVO.setVenueCode(VenueEnum.MARBLES.getVenueCode());
                CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
                if (casinoMember == null) {
                    log.error("im marbles settleBet error, req:{}", req);
                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_NOT_EXIST,null,settleBetReq.getTransactionId(),null));
                    continue;
                }
                String venueCode = casinoMember.getVenueCode();
//                List<String> venueCodes = Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
//                if (!venueCodes.contains(venueCode)){
//                    log.info("venueCode not match,venueCode:{}",venueCode);
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,settleBetReq.getTransactionId(),null));
//                    continue;
//                }
//                if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//                    log.info("{}:场管未开启", VenueEnum.MARBLES.getVenueName());
//                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,settleBetReq.getTransactionId(),null));
//                    continue;
//                }
                // 三方的参数没有唯一性，故加上时间戳
                String orderNo = settleBetReq.getBetId()+System.currentTimeMillis();
                RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
                String userId = casinoMember.getUserId();
                UserInfoVO userInfoVO = getByUserId(userId);
                String centerAmount=getUserCenterCoin(userInfoVO.getUserId()).getCenterAmount().toString();
                try{
                    if (!rLock.tryLock()) {
                        log.error("im marbles error settleBet error get locker error, req:{}", req);
                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,settleBetReq.getAmount().toString(),settleBetReq.getTransactionId(),null));
                        continue;
                    }

                    // 游戏锁定
//                    if (userGameLock(userInfoVO)) {
//                        log.error("im marbles locked userName[{}] game lock.", userInfoVO.getUserName());
//                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,settleBetReq.getTransactionId(),null));
//                        continue;
//                    }

                    if(BigDecimal.ZERO.compareTo(settleBetReq.getAmount()) == 0){
                        result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount, settleBetReq.getTransactionId(),System.currentTimeMillis()+""));
                        continue;
                    }

                    if(CollectionUtil.isEmpty(settleBetReq.getRefTransactionId())){
                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),System.currentTimeMillis()+""));
                        continue;
                    }
                    // 根据关联的transactionId和preTransactionId 查询出对应的orderNo
                    CoinRecordResultVO coinRecordResultVO = null;
                    String refTransaction = settleBetReq.getRefTransactionId().get(0);
                    UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
                    coinRecordRequestVO.setRemark(refTransaction);
                    coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
                    coinRecordRequestVO.setUserId(userId);
                    coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
                    List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
                    if (CollectionUtil.isNotEmpty(userCoinRecords)) {
                        UserCoinRecordVO userCoinRecordVO = userCoinRecords.get(0);
                        // 查询出是否有已经派彩的
                        coinRecordRequestVO = new UserCoinRecordRequestVO();
                        coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
                        coinRecordRequestVO.setUserId(userId);
                        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
                        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                        List<UserCoinRecordVO> recordsList = getUserCoinRecords(coinRecordRequestVO);
                        if (CollectionUtil.isNotEmpty(recordsList) && recordsList.size() > 0){
                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,settleBetReq.getTransactionId(),null));
                            continue;
                        }
                    }else{
                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
                        continue;
                    }

                    // 正常派彩
                    if("Settle".equals(settleBetReq.getType())){
                        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
                        userCoinAddVOPayout.setOrderNo(settleBetReq.getBetId());
                        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
                        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
                        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
                        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
                        userCoinAddVOPayout.setCoinValue(settleBetReq.getAmount().abs());
                        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                        userCoinAddVOPayout.setRemark(settleBetReq.getTransactionId());
                        userCoinAddVOPayout.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
                        userCoinAddVOPayout.setVenueCode(VenuePlatformConstants.MARBLES);
                        userCoinAddVOPayout.setThirdOrderNo(settleBetReq.getTransactionId());
                        coinRecordResultVO =  toUserCoinHandle(userCoinAddVOPayout);
                    }else if("Commission".equals(settleBetReq.getType())){
                        // 重新派彩
                        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                        userCoinAddVO.setOrderNo(settleBetReq.getBetId());
                        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
                        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                        userCoinAddVO.setUserId(userInfoVO.getUserId());
                        userCoinAddVO.setCoinValue(settleBetReq.getAmount().abs());
                        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

                        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
                        userCoinAddVO.setVenueCode(VenuePlatformConstants.MARBLES);
                        userCoinAddVO.setThirdOrderNo(settleBetReq.getTransactionId());


                        //修改余额 记录账变
                        coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
                    }

                    if(coinRecordResultVO == null){
                        log.error("im marbles error settleBet error updateBalancePayout error, req:{}", req);
                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
                        continue;
                    }

                    switch (coinRecordResultVO.getResultStatus()) {
                        case SUCCESS -> {
                            UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
                            BigDecimal balance = BigDecimal.ZERO;
                            if (!Objects.isNull(userCenterCoin)) {
                                balance = userCenterCoin.getTotalAmount();
                            }
                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS, balance.toString(), settleBetReq.getTransactionId(), System.currentTimeMillis() + ""));
                        }
                        case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO ->{
                            result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
                        }
                    }
                }catch (Exception e){
                    log.error("IM 弹珠游戏 退款取消失败， 失败原因为{}",e.getMessage());
                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,settleBetReq.getTransactionId(),null));
                    e.printStackTrace();
                }finally {
                    try {
                        rLock.lockInterruptibly(10, TimeUnit.SECONDS);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            refundResp.setCode(result.get(0).getCode());
            refundResp.setMessage(result.get(0).getMessage());
            refundResp.setResult(result);
        }
        return refundResp;
    }


    /**
     * 退款/取消
     * 结算和取消的接口是会有多笔交易的。会包括不一样的玩家注单
     *
     * 第三方回复
     *
     * Refund API 里如 TransactionType 是 CancelSettlement, RefTransactionId 的值会是SettleBet 的 TransactionId，如是 CancelWager，
     * RefTransactionId 的值则会是 Placebet 的 TransactionId。谢谢 - sunny
     */
    public MarblesRefundResp refund(MarblesRefundReq req) {
        log.info("弹珠refund req:{}", JSONObject.toJSONString(req));
        List<RefundReq> refundReqList = req.getTransactions();
        MarblesRefundResp refundResp = new MarblesRefundResp();
        if(CollectionUtil.isNotEmpty(refundReqList)){
            List<MarblesRefundResp.RefundBuilder> result = new ArrayList<>();
            for (RefundReq refundReq : refundReqList) {
                CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
                casinoMemberReqVO.setVenueUserAccount(refundReq.getPlayerId());
                casinoMemberReqVO.setVenueCode(VenueEnum.MARBLES.getVenueCode());
                CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
                if (casinoMember == null) {
                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
                    continue;
                }
                String venueCode = casinoMember.getVenueCode();
                List<String> venueCodes = com.google.common.collect.Lists.newArrayList(VenueEnum.MARBLES.getVenueCode());
                if (!venueCodes.contains(venueCode)){
                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
                    continue;
                }
                if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
                    result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,null,refundReq.getTransactionId(),null));
                    continue;
                }
                String orderNo = refundReq.getTransactionId()+System.currentTimeMillis();
                RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
                String userId = casinoMember.getUserId();
                UserInfoVO userInfoVO = getByUserId(userId);
                String centerAmount=getUserCenterCoin(userInfoVO.getUserId()).getCenterAmount().toString();
                try{
                    if (!rLock.tryLock()) {
                        log.error("im marbles error refund error get locker error, req:{}", req);
                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,refundReq.getTransactionId(),null));
                        continue;
                    }

                    // 游戏锁定
                    if (userGameLock(userInfoVO)) {
                        log.error("im marbles locked userName[{}] game lock.", userInfoVO.getUserName());
                        result.add(setRefundBuilder(MarblesRespErrEnums.PLAYER_INACTIVE,centerAmount,refundReq.getTransactionId(),null));
                        continue;
                    }
                    TransactionTypeEnum typeEnum = TransactionTypeEnum.getCode(refundReq.getTransactionType());
                    if(Objects.isNull(typeEnum)){
                        log.error("im marbles error refund error transactionType error, req:{}", req);
                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
                        continue;
                    }
                    UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
                    coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
                    coinRecordRequestVO.setUserId(userId);
                    coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
                    coinRecordRequestVO.setRemark(refundReq.getRefTransactionId());
                    List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
                    if(CollectionUtil.isEmpty(userCoinRecords) || userCoinRecords.size() == 0) {
                        log.error("im marbles 查询交易订单为空 或不存在 req:{}", req);
                        result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
                        continue;
                    }else{
                        UserCoinRecordVO userCoinRecordVO = userCoinRecords.get(0);
                        coinRecordRequestVO = new UserCoinRecordRequestVO();
                        coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
                        coinRecordRequestVO.setRemark(refundReq.getTransactionId());
                        coinRecordRequestVO.setUserId(userId);
                        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
                        List<UserCoinRecordVO> recordsList = getUserCoinRecords(coinRecordRequestVO);
                        if (CollectionUtil.isNotEmpty(recordsList)) {
                            result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,refundReq.getTransactionId(),null));
                            continue;
                        }
                        CoinRecordResultVO coinRecordResultVO;
                        switch (typeEnum){
                            case CANCELWAGER -> // 取消投注
                                    coinRecordResultVO = updateBalanceBetCancel(userInfoVO, userCoinRecordVO.getOrderNo(),
                                            userCoinRecordVO.getCoinValue(),refundReq.getTransactionId(),VenuePlatformConstants.MARBLES);
                            case CANCELSETTLEMENT,CANCELCOMMISSION -> // 取消结算
                                    coinRecordResultVO = updateBalanceCancelBet(userInfoVO, userCoinRecordVO.getOrderNo(), userCoinRecordVO.getCoinValue(),refundReq.getTransactionId());
                            default -> {
                                log.error("im marbles error refund error transactionType error, req:{}", req);
                                result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
                                continue;
                            }
                        }
                        switch (coinRecordResultVO.getResultStatus()) {
                            case SUCCESS ->
                                    result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,coinRecordResultVO.getCoinAfterBalance().toString(),refundReq.getTransactionId(),null));
                            case REPEAT_TRANSACTIONS ->
                                    result.add(setRefundBuilder(MarblesRespErrEnums.SUCCESS,centerAmount,refundReq.getTransactionId(),null));
                            case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL , AMOUNT_LESS_ZERO ->
                                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
                        }

                    }


                }catch (Exception e){
                    log.error("IM弹珠游戏退款取消失败，失败原因为",e);
                    result.add(setRefundBuilder(MarblesRespErrEnums.TRANSACTIONID_IS_NOT_FOUND,centerAmount,refundReq.getTransactionId(),null));
                }
            }
            refundResp.setCode(result.get(0).getCode());
            refundResp.setMessage(result.get(0).getMessage());
            refundResp.setResult(result);
        }
        return refundResp;
    }

    /**
     * 设置退款取消响应
     */
    public MarblesRefundResp.RefundBuilder setRefundBuilder(MarblesRespErrEnums errEnums , String balance, String transactionId, String OperatorTransactionId) {
        MarblesRefundResp.RefundBuilder refundBuilder = new MarblesRefundResp.RefundBuilder();
        refundBuilder.setCode(errEnums.getCode());
        refundBuilder.setMessage(errEnums.getDescription());
        refundBuilder.setOperatorTransactionId(OperatorTransactionId);
        refundBuilder.setBalance(balance);
        refundBuilder.setTransactionId(transactionId);
        return refundBuilder;
    }


    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount,String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);


        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.MARBLES);
        userCoinAddVO.setThirdOrderNo(remark);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }



    public enum TransactionTypeEnum {

        CANCELWAGER("CancelWager", "取消投注"),
        CANCELSETTLEMENT("CancelSettlement","取消结算"),
        CANCELCOMMISSION("CancelCommission","取消重复结算");


        private final String name;

        private final String msg;


        public String getName() {
            return name;
        }

        TransactionTypeEnum(String name, String msg) {
            this.name = name;
            this.msg = msg;
        }

        public static TransactionTypeEnum getCode(final String code){
            for(TransactionTypeEnum transactionTypeEnum : TransactionTypeEnum.values()){
                if(transactionTypeEnum.getName().equals(code)){
                    return transactionTypeEnum;
                }
            }
            return null;
        }
    }


}
