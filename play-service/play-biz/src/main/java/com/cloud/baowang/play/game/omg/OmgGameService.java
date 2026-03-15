package com.cloud.baowang.play.game.omg;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.omg.OmgRespErrEnums;
import com.cloud.baowang.play.api.enums.omg.OmgTransferTypeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.omg.OmgReq;
import com.cloud.baowang.play.api.vo.omg.OmgResp;
import com.cloud.baowang.play.api.vo.omg.OmgRespData;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.omg.resp.OmgOrderDataRespVO;
import com.cloud.baowang.play.game.omg.resp.OmgOrderRespVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
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
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class OmgGameService extends GameBaseService implements GameService {
    @Autowired
    private OrderRecordProcessService orderRecordProcessService;
    @Autowired
    private CasinoMemberService casinoMemberService;

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        String traceId = "trace_id="+UUID.randomUUID();
        String api =   "https://api-gametest.omgapi.cc/api/usr/ingame?" + traceId;
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        params.put("app_id","1137");
        params.put("gameid","1");
        params.put("token","1243125");
        params.put("nick","test");
        params.put("lang","en");
        params.put("cid",175);
        headers.put("sign",getSign(traceId,JSONObject.toJSONString(params),"a7e7ccaa3d7a991f2718c5fdde0dcb0b"));

        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        System.out.println(respones);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String traceId = "trace_id=" + UUID.randomUUID();
        String api = venueDetailVO.getApiUrl() + "/api/usr/ingame?" + traceId;
        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Content-Type","application/json; charset=utf-8");
        Map<String, Object> params = new HashMap<>();
        params.put("app_id",venueDetailVO.getMerchantNo());
        params.put("gameid",loginVO.getGameCode());
        params.put("token",casinoMemberVO.getCasinoPassword());
        params.put("nick",casinoMemberVO.getVenueUserAccount());
        params.put("lang", StringUtils.isNotEmpty(LANGUAGE_MAP.get(loginVO.getLanguageCode()))?LANGUAGE_MAP.get(loginVO.getLanguageCode()):"en");
        params.put("cid",CID_MAP.get(loginVO.getCurrencyCode()));
        headers.put("sign",getSign(traceId,JSONObject.toJSONString(params),venueDetailVO.getMerchantKey()));

        String respones = HttpClientHandler.post(api, headers, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (jsonObject == null || jsonObject.getIntValue("code") != CommonConstant.business_zero){
            log.error("{} 获取登录链接失败,参数：{}",venueDetailVO.getVenueCode(),respones);
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        GameLoginVo gameLoginVO = GameLoginVo.builder().source(jsonObject.getJSONObject("data").getString("gameurl"))
                .type(GameLoginTypeEnums.URL.getType())
                .userAccount(loginVO.getUserAccount())
                .venueCode(venueDetailVO.getVenueCode())
                .build();
        casinoMemberService.updatePasswordById(casinoMemberVO);
        return ResponseVO.success(gameLoginVO);

    }

    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        return GameService.super.logOut(venueDetailVO, venueUserAccount);
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        String venueCode = venueInfoVO.getVenueCode();
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueCode);
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueCode);
        int page = 1;
        int pageSize = 300;
        try {
            while (true) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("start_time", venuePullParamVO.getStartTime() / 1000);
                params.put("end_time", venuePullParamVO.getEndTime() / 1000);
                params.put("app_id", venueInfoVO.getMerchantNo());
                params.put("page", page);
                params.put("size", pageSize);
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Content-Type","application/json; charset=utf-8");
                String traceId = "trace_id=" + UUID.randomUUID();
                String url = venueInfoVO.getBetUrl() + "/api/v1/merchant/outer/record/GetGameRecordList?" + traceId;
                headers.put("sign",getSign(traceId,JSONObject.toJSONString(params), venueInfoVO.getMerchantKey()));
                log.info("{} 拉单参数, url:{},参数：{}", venueCode, url, params);
                String response = HttpClientHandler.post(url, headers, JSONObject.toJSONString(params));
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject == null || jsonObject.getIntValue("code") != CommonConstant.business_one) {
                    log.error("{} 拉取注返回异常，返回：{}", venueCode, response);
                    return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
                }
                OmgOrderRespVO omgOrderRespVO = JSONObject.parseObject(response, OmgOrderRespVO.class);
                if (omgOrderRespVO.getTotal() == 0) {
                    break;
                }
                log.info("{} 拉取注单 返回:{}", venueCode,JSONObject.toJSONString(omgOrderRespVO));
                List<OmgOrderDataRespVO> betData = omgOrderRespVO.getData();
                int size = betData.size();
                Integer platform =  getGamePlatform(venueCode);
                if (platform != null){
                    betData.removeIf(e-> !platform.equals(e.getPlatform()));
                }
                // 场馆用户关联信息
                List<String> usernames = betData.stream().map(OmgOrderDataRespVO::getAccount).distinct().toList();
                Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueInfoVO.getVenuePlatform());
                if (MapUtil.isEmpty(casinoMemberMap)) {
                    log.info("{}未找到三方关联信息 玩家列表{}",venueCode, usernames);
                    if (size != pageSize) {
                        break;
                    }
                    page++;
                    continue;
                }
                // 用户信息
                List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
                Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
                if (CollUtil.isEmpty(userMap)) {
                    log.info("{}游戏用户账号不存在{}",venueCode, userIds);
                    if (size != pageSize) {
                        break;
                    }
                    page++;
                    continue;
                }
                // 用户登录信息
                Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

                List<OrderRecordVO> list = Lists.newArrayList();
                Map<String, String> siteNameMap = getSiteNameMap();
                for (OmgOrderDataRespVO orderResponseVO : betData) {
                    CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getAccountId());
                    if (casinoMemberVO == null) {
                        log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), orderResponseVO.getAccountId());
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
                    recordVO.setVenueType(VenueEnum.JILIPLUS.getType().getCode());
                    list.add(recordVO);
                }
                // 订单处理
                if (CollectionUtil.isNotEmpty(list)) {
                    orderRecordProcessService.orderProcess(list);
                    //processChangeRecords(list,userMap,venueCode);
                }
                list.clear();
                if (size != pageSize) {
                    break;
                }
                page++;
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("omg 执行拉单异常", e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, OmgOrderDataRespVO orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBet());
        Long betTime = orderResponseVO.getCreateTime() * 1000;
        recordVO.setBetTime(betTime);
        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getAccount());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(orderResponseVO.getRoundIdStr());
        recordVO.setWinLossAmount(orderResponseVO.getWin().subtract(orderResponseVO.getBet()));
        recordVO.setPayoutAmount(orderResponseVO.getWin());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setSettleTime(betTime);
        recordVO.setOrderStatus(getOrderStatus(recordVO.getWinLossAmount()));
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());
        BigDecimal validBetAmount = recordVO.getBetAmount();
        recordVO.setValidAmount(validBetAmount);

        String gameCode = orderResponseVO.getGameId();
        recordVO.setThirdGameCode(gameCode);
        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
        }
        recordVO.setTransactionId(recordVO.getThirdOrderId());
        return recordVO;
    }

    private Integer getOrderStatus(BigDecimal winLossAmount) {
        if(winLossAmount.compareTo(BigDecimal.ZERO) > 0){
            return OrderStatusEnum.WIN.getCode();
        }else if (winLossAmount.compareTo(BigDecimal.ZERO) == 0){
            return OrderStatusEnum.DRAW.getCode();
        }else {
            return OrderStatusEnum.LOSS.getCode();
        }
    }

    private Integer getGamePlatform(String venueCode) {
        if (venueCode.startsWith(VenueEnum.JILIPLUS.getVenueCode())){
            return 3;
        }else if(venueCode.startsWith(VenueEnum.PGPLUS.getVenueCode())){
            return 2;
        }else if (venueCode.startsWith(VenueEnum.PPPLUS.getVenueCode())){
            return 4;
        }
        return null;

    }

    private static String getSign(String urlParam, String jsonParam, String key){
        return MD5Util.md5(urlParam + jsonParam + key ).toLowerCase();
    }

    private static final Map<String,String> LANGUAGE_MAP = new HashMap<>();
    static {
        LANGUAGE_MAP.put(LanguageEnum.ZH_CN.getLang(), "zh");
        LANGUAGE_MAP.put(LanguageEnum.EN_US.getLang(), "en");
        LANGUAGE_MAP.put(LanguageEnum.VI_VN.getLang(), "vi");
        LANGUAGE_MAP.put(LanguageEnum.PT_BR.getLang(), "pt");
        LANGUAGE_MAP.put(LanguageEnum.ZH_TW.getLang(), "zt");

    }

    private static final Map<String,Integer> CID_MAP = new HashMap<>();
    static {
        CID_MAP.put(CurrencyEnum.AUD.getCode(), 1);
        CID_MAP.put(CurrencyEnum.AZN.getCode(), 2);
        CID_MAP.put(CurrencyEnum.ANG.getCode(), 3);
        CID_MAP.put(CurrencyEnum.BGN.getCode(), 4);
        CID_MAP.put(CurrencyEnum.BHD.getCode(), 5);
        CID_MAP.put(CurrencyEnum.BOB.getCode(), 7);
        CID_MAP.put(CurrencyEnum.BRL.getCode(), 8);
        CID_MAP.put(CurrencyEnum.BWP.getCode(), 10);
        CID_MAP.put(CurrencyEnum.CAD.getCode(), 12);
        CID_MAP.put(CurrencyEnum.CNY.getCode(), 79);
        CID_MAP.put(CurrencyEnum.COP.getCode(), 82);
        CID_MAP.put(CurrencyEnum.DKK.getCode(), 131);
        CID_MAP.put(CurrencyEnum.EGP.getCode(), 132);
        CID_MAP.put(CurrencyEnum.EUR.getCode(), 135);
        CID_MAP.put(CurrencyEnum.GBP.getCode(), 136);
        CID_MAP.put(CurrencyEnum.HKD.getCode(), 140);
        CID_MAP.put(CurrencyEnum.HNL.getCode(), 141);
        CID_MAP.put(CurrencyEnum.IDR.getCode(), 143);
        CID_MAP.put(CurrencyEnum.INR.getCode(), 144);
        CID_MAP.put(CurrencyEnum.JPY.getCode(), 146);
        CID_MAP.put(CurrencyEnum.KRW.getCode(), 148);
        CID_MAP.put(CurrencyEnum.MMK.getCode(), 154);
        CID_MAP.put(CurrencyEnum.MNT.getCode(), 155);
        CID_MAP.put(CurrencyEnum.MYR.getCode(), 156);
        CID_MAP.put(CurrencyEnum.MXN.getCode(), 158);
        CID_MAP.put(CurrencyEnum.NOK.getCode(), 159);
        CID_MAP.put(CurrencyEnum.NGN.getCode(), 162);
        CID_MAP.put(CurrencyEnum.PHP.getCode(), 165);
        CID_MAP.put(CurrencyEnum.PLN.getCode(), 168);
        CID_MAP.put(CurrencyEnum.PYG.getCode(), 169);
        CID_MAP.put(CurrencyEnum.SEK.getCode(), 171);
        CID_MAP.put(CurrencyEnum.SGD.getCode(), 172);
        CID_MAP.put(CurrencyEnum.THB.getCode(), 173);
        CID_MAP.put(CurrencyEnum.TRY.getCode(), 174);
        CID_MAP.put(CurrencyEnum.USD.getCode(), 175);
        CID_MAP.put(CurrencyEnum.USDT.getCode(), 176);
        CID_MAP.put(CurrencyEnum.VND.getCode(), 179);
        CID_MAP.put(CurrencyEnum.KVND.getCode(), 181);
        CID_MAP.put(CurrencyEnum.BDT.getCode(), 183);
        CID_MAP.put(CurrencyEnum.NPR.getCode(), 184);
        CID_MAP.put(CurrencyEnum.BND.getCode(), 185);
        CID_MAP.put(CurrencyEnum.NZD.getCode(), 186);
        CID_MAP.put(CurrencyEnum.RUB.getCode(), 187);
        CID_MAP.put(CurrencyEnum.ZAR.getCode(), 188);
        CID_MAP.put(CurrencyEnum.LAK.getCode(), 189);
        CID_MAP.put(CurrencyEnum.MOP.getCode(), 190);
        CID_MAP.put(CurrencyEnum.ZMW.getCode(), 191);
        CID_MAP.put(CurrencyEnum.KHR.getCode(), 192);
        CID_MAP.put(CurrencyEnum.AOA.getCode(), 193);
        CID_MAP.put(CurrencyEnum.ARS.getCode(), 194);
        CID_MAP.put(CurrencyEnum.CDF.getCode(), 195);
        CID_MAP.put(CurrencyEnum.CLP.getCode(), 197);
        CID_MAP.put(CurrencyEnum.DZD.getCode(), 198);
        CID_MAP.put(CurrencyEnum.ETB.getCode(), 199);
        CID_MAP.put(CurrencyEnum.GHS.getCode(), 200);
        CID_MAP.put(CurrencyEnum.KES.getCode(), 201);
        CID_MAP.put(CurrencyEnum.MAD.getCode(), 202);
        CID_MAP.put(CurrencyEnum.PEN.getCode(), 203);
        CID_MAP.put(CurrencyEnum.TND.getCode(), 204);
        CID_MAP.put(CurrencyEnum.TZS.getCode(), 205);
        CID_MAP.put(CurrencyEnum.UGX.getCode(), 206);
        CID_MAP.put(CurrencyEnum.TWD.getCode(), 207);
        CID_MAP.put(CurrencyEnum.PGK.getCode(), 218);
        CID_MAP.put(CurrencyEnum.UZS.getCode(), 219);
        CID_MAP.put(CurrencyEnum.AMD.getCode(), 223);
        CID_MAP.put(CurrencyEnum.LKR.getCode(), 224);
        CID_MAP.put(CurrencyEnum.KHR.getCode(), 225);
        CID_MAP.put(CurrencyEnum.KZT.getCode(), 226);
        CID_MAP.put(CurrencyEnum.PKR.getCode(), 84);

    }


    public OmgResp verify(OmgReq req) {
        log.info("OMG verify request{}", req);
        String token = req.getOperatorPlayerSession();
        if (StringUtils.isEmpty(token)) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(token);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
        String venueCode = casinoMember.getVenueCode();

        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(req.getAppId(), venueCode);

//        ResponseVO<List<VenueInfoVO>> venueInfoVO=playVenueInfoApi.getVenueInfoList(venueCode);
        if (venueInfoVO == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
//        List<String> AppIds = venueInfoVO.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
//                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
//                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        OmgRespData data = new OmgRespData();
        data.setNickname(casinoMember.getVenueUserAccount());
        data.setBalance(balance);
        data.setUname(casinoMember.getVenueUserAccount());
        return OmgResp.success(data);
    }

    public OmgResp getBalance(OmgReq req) {
        log.info("OMG getBalance request{}", req);
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(req.getPlayerLoginToken());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
//                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
//                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
//        }
//        ResponseVO<List<VenueInfoVO>> venueInfoVO=playVenueInfoApi.getVenueInfoList(venueCode);
//        if (!venueInfoVO.isOk() || venueInfoVO.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(req.getAppId(), venueCode);
        if (venueInfoVO == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
//        List<String> AppIds = venueInfoVO.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        OmgRespData data = new OmgRespData();
        data.setNickname(casinoMember.getVenueUserAccount());
        data.setBalance(balance);
        data.setUname(casinoMember.getVenueUserAccount());
        return OmgResp.success(data);
    }

    public OmgResp changeBalance(OmgReq req) {
//        String uname = req.getUname();
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(req.getPlayerLoginToken());

        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }
        String venueCode = casinoMember.getVenueCode();
        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(req.getAppId(), venueCode);
        if (venueInfoVO == null) {
            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
        }


//        ResponseVO<List<VenueInfoVO>> venueInfoVOs=playVenueInfoApi.getVenueInfoList(venueCode);
//        if (!venueInfoVOs.isOk() || venueInfoVOs.getData() == null) {
//            return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//        }
//        List<String> AppIds = venueInfoVOs.getData().stream()
//                .map(v -> v.getMerchantNo())
//                .collect(Collectors.toList());
//        if (!AppIds.contains(req.getAppId())){
//            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
//        }
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.JILIPLUS.getVenueCode(),VenueEnum.PGPLUS.getVenueCode(),
//                VenueEnum.PPPLUS.getVenueCode(),VenueEnum.JILIPLUS_02.getVenueCode(),VenueEnum.PGPLUS_02.getVenueCode(),VenueEnum.PPPLUS_02.getVenueCode()
//                ,VenueEnum.JILIPLUS_03.getVenueCode(),VenueEnum.PGPLUS_03.getVenueCode(),VenueEnum.PPPLUS_03.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return OmgResp.fail(OmgRespErrEnums.PLAYER_NOT_FOUND);
//        }
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return OmgResp.fail(OmgRespErrEnums.GAME_CLOSED);
        }

        String orderNo = req.getSessionId();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("omg errorBet error get locker error, req:{}", req);
                return OmgResp.fail(OmgRespErrEnums.FAILURE);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("PG queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
                return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
            }
            Integer type = req.getType();
            OmgTransferTypeEnums omgTransferTypeEnums = OmgTransferTypeEnums.fromCode(type);
            if (omgTransferTypeEnums == null){
                log.error("omg transfer type not support,{}",req);
                return OmgResp.fail(OmgRespErrEnums.FAILURE);
            }

            BigDecimal userAmount = getUserBalance(userInfoVO.getUserId());
            if(type.equals(OmgTransferTypeEnums.BET.getCode()) && !compareAmount(userAmount,req.getMoney())){
                log.info("用户余额不足，userId:{},siteCode:{},money:{},errorCore={}",userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getMoney(),OmgRespErrEnums.INSUFFICIENT_BALANCE);
                return OmgResp.fail(OmgRespErrEnums.INSUFFICIENT_BALANCE);
            }

            if(type.equals(OmgTransferTypeEnums.BET.getCode()) && venueMaintainClosed(venueCode,userInfoVO.getSiteCode())) {
//                SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                        .siteCode(userInfoVO.getSiteCode())
//                        .venueCode(venueCode)
//                        .currencyCode(userInfoVO.getMainCurrency()).build();
//                ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//                VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//                if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                    log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueCode);
                    return OmgResp.fail(OmgRespErrEnums.LOGIN_TOKEN_FAILED);
//                }
            }

            CoinRecordResultVO coinRecordResultVO =  switch (omgTransferTypeEnums){
                case BET->{
                    yield  bet(req,userInfoVO);
                }
                case PAYOUT->{
                    yield payout(req,userInfoVO);
                }
                case CANCEL->{
                    yield cancelGame(req,userInfoVO);
                }
                case END->{
                    yield endGame(req, userInfoVO);
                }
                case LUCKWIN->{
                    yield luckwin(req, userInfoVO);
                }
                case Future->{
                    yield future(req,userInfoVO);
                }
            };
            if (coinRecordResultVO == null){
                log.info("omg coinRecordResultVO ={}",omgTransferTypeEnums.getCode());
                return  OmgResp.fail(OmgRespErrEnums.FAILURE);
            }
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            OmgResp resp = switch (resultStatus) {
                case SUCCESS-> OmgResp.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO -> {
                    log.info("omg amount resp={}",resultStatus);
                    yield OmgResp.fail(OmgRespErrEnums.FAILURE);
                }
            };
            if (resp.getCode() == OmgRespErrEnums.SUCCESS.getCode()){
                UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
                BigDecimal balance = BigDecimal.ZERO;
                if (!Objects.isNull(userCenterCoin)) {
                    balance = userCenterCoin.getTotalAmount();
                }
                OmgRespData data = new OmgRespData();
                data.setBalance(balance);
                resp.setData(data);
            }
            return resp;
        }catch (Exception e){
            log.error("omg amount change error ", e);
            return OmgResp.fail(OmgRespErrEnums.FAILURE);
        }finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private CoinRecordResultVO cancelGame(OmgReq req, UserInfoVO userInfoVO) {
        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setOrderNo(req.getOrderId());
        List<UserCoinRecordVO> data = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isEmpty(data)){
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
            return coinRecordResultVO;
        }
        List<String> coinTypes = data.stream().map(UserCoinRecordVO::getCoinType).toList();
        if (coinTypes.contains(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())){
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
            return coinRecordResultVO;
        }
        return updateBalanceBetCancel(userInfoVO, req.getSessionId(), req.getMoney().abs(),null,VenueEnum.JILIPLUS.getVenueCode());
    }

    private CoinRecordResultVO future(OmgReq req, UserInfoVO userInfoVO) {
        return null;
    }

    private CoinRecordResultVO luckwin(OmgReq req, UserInfoVO userInfoVO) {
        return null;
    }

    private CoinRecordResultVO endGame(OmgReq req, UserInfoVO userInfoVO) {
        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
        coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.SUCCESS);
        return coinRecordResultVO;
    }

    private CoinRecordResultVO payout(OmgReq req, UserInfoVO userInfoVO) {
        String order = req.getRoundId();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setOrderNo(order);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        List<UserCoinRecordVO> data = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(data)) {
            UserCoinRecordVO userCoinRecordVO = data.get(0);
            if (req.getOrderId().equals(userCoinRecordVO.getRemark())){
                CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
                coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
                return coinRecordResultVO;
            }
           /* // 注单号已经派彩，后续拼接
            String orderId = req.getOrderId();
            // 截取流水单号作为附加单号
            int from = orderId.length() > 12 ? orderId.length() - 12 : 0;
            order = order + "_" + orderId.substring(from);*/
        }

        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(req.getRoundId());
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(req.getMoney().abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(null);
        userCoinAddVOPayout.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVOPayout);
//        return updateBalancePayoutLocal(userInfoVO, req.getRoundId(), req.getMoney().abs(),null);
    }

    private CoinRecordResultVO bet(OmgReq req, UserInfoVO userInfoVO) {
        String orderNo = req.getSessionId();

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getMoney().abs());
        userCoinAddVO.setRemark(req.getOrderId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setThirdOrderNo(orderNo);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.JILIPLUS);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
//        return updateBalanceBet(userInfoVO, orderNo,req.getMoney().abs(),req.getOrderId());
    }


}
