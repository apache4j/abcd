package com.cloud.baowang.play.game.cmd.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.cmd.CmdRespErrEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.cmd.CmdBetReq;
import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import com.cloud.baowang.play.api.vo.cmd.CmdUpdateBaseReq;
import com.cloud.baowang.play.api.vo.cmd.CmdUpdateDataReq;
import com.cloud.baowang.play.game.db.sh.utils.AESUtil;
import com.cloud.baowang.play.util.cmd.CmdBaseRsp;
import com.cloud.baowang.play.util.cmd.CmdUserRsp;
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
import com.cloud.baowang.play.game.cmd.constant.CmdConstant;
import com.cloud.baowang.play.game.cmd.enums.*;
import com.cloud.baowang.play.game.cmd.response.CmdCollusionReqVO;
import com.cloud.baowang.play.game.cmd.response.CmdResponseVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.cmd.params.CmdPullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserAccountListVO;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.CMD)
@AllArgsConstructor
public class CmdServiceImpl extends GameBaseService implements GameService {
    public static final ZoneId gmt8ZoneId = ZoneId.of("Asia/Shanghai");
    public static final TimeZone gmt8TimeZone = TimeZone.getTimeZone("GMT+8");
    /**
     * 时区：GMT+8 (东8区)
     */
    public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");

    private final OrderRecordProcessService orderRecordProcessService;
    private final SiteApi siteApi;
    private final UserInfoApi userInfoApi;
    private final VenueInfoService venueInfoService;


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        if (checkCasinoMemberVO(casinoMemberVO)) {
            register(loginVO, venueDetailVO, casinoMemberVO);
        }
        try {
            String api = null;
            Integer reqDeviceType = CurrReqUtils.getReqDeviceType();
            String view = "";
            if (Objects.isNull(reqDeviceType) || reqDeviceType == DeviceType.PC.getCode()) {
                //web
                api = venueDetailVO.getBetUrl() + "/auth.aspx?";
                view = "v2";
            } else {
                //h5
                api = venueDetailVO.getGameUrl() + "/auth.aspx?";
                view = "v1";
            }
            api = api + "lang=" + CMDLangCodeEnum.fromCode(loginVO.getLanguageCode()).getCode()
                    + "&user=" + casinoMemberVO.getVenueUserAccount() + "&token=" + casinoMemberVO.getCasinoPassword() + "&currency=" + CMDCurrencyEnums.of(loginVO.getCurrencyCode()).getGameCurrencyCode()
                    + "&templateName=darker&View=" + view;
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(api)
                    .type(GameLoginTypeEnums.URL.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.CMD.getVenueCode())
                    .build();
            log.info("{} CMD-游戏页面地址信息 {}", platformName(), JSON.toJSONString(gameLoginVo));
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("CMD-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    private Boolean checkCasinoMemberVO(CasinoMemberVO casinoMemberVO) {
        CasinoMemberReq casinoMemberReq = new CasinoMemberReq();
        casinoMemberReq.setVenueUserAccount(casinoMemberVO.getVenueUserAccount());
        casinoMemberReq.setVenueCode(VenueEnum.CMD.getVenueCode());
        CasinoMemberVO respVO = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (ObjectUtil.isNotEmpty(respVO)) {
            return false;
        } else {
            String token = casinoMemberVO.getVenueUserAccount();
            String key = String.format(RedisConstants.VENUE_TOKEN, VenueEnum.CMD.getVenueCode(), token);
            RedisUtil.deleteKey(key);
            RedisUtil.setValue(key, casinoMemberVO, 60L, TimeUnit.MINUTES);
            return true;
        }
    }

    public void register(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        String api = venueDetailVO.getApiUrl();
        // 运营商唯一代码
        params.put("Method", "createmember");
        params.put("PartnerKey", venueDetailVO.getAesKey());
        params.put("UserName", casinoMemberVO.getVenueUserAccount());
        params.put("Currency", loginVO.getCurrencyCode());
        String respones = HttpClientHandler.get(api, headers, params);
        log.info("CMD体育注册用户入参params={},url={},{}", params, api, respones);
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (Objects.nonNull(jsonObject) && jsonObject.getIntValue("Code") != 0) {
            log.error("CMD体育注册用参数：venueCode={}, 响应参数={}", venueDetailVO.getVenueCode(), respones);
        }
    }

    @Override
    public ResponseVO getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        CmdPullBetParams params = new CmdPullBetParams();
        params.setStarttime(TimeZoneUtils.formatDate4TimeZone(venuePullParamVO.getStartTime(), shanghaiTimeZone, gmt8TimeZone, DateUtils.pattenT));
        params.setEndtime(TimeZoneUtils.formatDate4TimeZone(venuePullParamVO.getEndTime(), shanghaiTimeZone, gmt8TimeZone, DateUtils.pattenT));
        return getBetRecordListByParams(venueInfoVO, params);
    }

    public ResponseVO<CmdPullBetParams> getBetRecordListByParams(VenueInfoVO venueInfoVO, CmdPullBetParams params) {
        //拉单
        getBetRecordList(venueInfoVO, params);
        // 生成下一次拉单参数
        CmdPullBetParams pullBetParams = nextPullBetParams(params);
        return ResponseVO.success(pullBetParams);
    }

    public ResponseVO<Boolean> getBetRecordList(VenueInfoVO venueInfoVO, CmdPullBetParams venuePullParamVO) {
        //三方最大1000条如果等于1000则还有下一个分页记录当前最大的id放到version中
        Integer pageSise = 1000;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        String url = venueInfoVO.getApiUrl();
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("Method", "betrecordbydate");
        paramMap.put("PartnerKey", venueInfoVO.getAesKey());
        paramMap.put("TimeType", "2");
        paramMap.put("StartDate", venuePullParamVO.getStarttime());
        paramMap.put("EndDate", venuePullParamVO.getEndtime());
        Long Version = 0L;
        String param = null;
        List<OrderRecordVO> orderList;
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        String head = JSON.toJSONString(headers);
        Integer count = 0;
        try {
            do {
                paramMap.put("Version", Version + "");
                param = JSON.toJSONString(paramMap);
                log.info("CMD-拉单request , apiPath: {} , head:{}, request: {}", url, head, param);
                String requestData = HttpClientHandler.get(url, headers, paramMap);
//                String requestData = testMsg();
                log.info("CMD-拉单response, url: {} , head:{}, 参数: {} , response={}", url, head, param, requestData);
                JSONObject jsonObject = JSONObject.parseObject(requestData);
                if (jsonObject != null &&
                        (jsonObject.getIntValue("Code") == CommonConstant.business_zero)) {
                    // 总记录数量
                    // 投注明细列表
                    JSONArray betDetails = jsonObject.getJSONArray("Data");
                    count = betDetails.size();
                    if (count >= pageSise) {
                        Version = betDetails.getJSONObject(count - 1).getLong("Id");
                    }
                    // 解析注单数据
                    orderList = parseOrder(venueInfoVO, betDetails, siteVOMap);
                    log.info("{} 拉取注单参数: {} | {}, size: {}, 本次拉取数量: {}", platformName(), venuePullParamVO.getStarttime(), venuePullParamVO.getEndtime(),
                            count, betDetails.size());
                    if (CollectionUtils.isNotEmpty(orderList)) {
                        orderRecordProcessService.orderProcess(orderList);
                    }
                } else {
                    return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                }
            } while (count >= pageSise);
            log.info("CMD-拉单完成");
            return ResponseVO.success(null);
        } catch (Exception e) {
            log.error("CMD-拉单异常,apiPath: {} , head:{}, request: {}", url, JSON.toJSONString(head), param, e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, JSONArray betDetails, Map<String, SiteVO> siteVOMap) {
        List<CmdResponseVO> betDetailEntityList = Lists.newArrayList();
        if (betDetails != null && !betDetails.isEmpty()) {
            for (int i = 0; i < betDetails.size(); i++) {
                JSONObject betDetail = betDetails.getJSONObject(i);
                try {
                    CmdResponseVO betDetailEntity = JSONObject.parseObject(betDetail.toJSONString(), CmdResponseVO.class);
                    betDetailEntityList.add(betDetailEntity);
                } catch (Exception e) {
                    log.error("{} 解析注单时间适配异常:{}", platformName(), betDetail);
                }
            }
        }
        // 场馆用户关联信息
        List<String> venueUserList = betDetailEntityList.stream().map(CmdResponseVO::getSourceName).distinct().toList();
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder()
                .venuePlatform(venueInfoVO.getVenuePlatform())
                .venueUserAccountList(venueUserList).build();
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
        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.CMD.getVenueCode());
        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        for (CmdResponseVO cmdResponseVO : betDetailEntityList) {
            String account = cmdResponseVO.getSourceName();
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
            UserLoginInfoVO userLoginInfoVO = loginVOMap.get(casinoMemberVO.getUserId());
            OrderRecordVO recordVO = populateOrderRecordVO(cmdResponseVO, userLoginInfoVO, userInfoVO, venueInfoVO, gameTypeId, siteVOMap);
            orderRecordList.add(recordVO);
        }
        return orderRecordList;
    }

    /**
     * 填充 OrderRecordVO 对象的基本信息。
     */
    private OrderRecordVO populateOrderRecordVO(CmdResponseVO entity, UserLoginInfoVO userLoginInfoVO, UserInfoVO userInfoVO, VenueInfoVO venueInfoVO,
                                                Integer gameTypeId, Map<String, SiteVO> siteMap) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setUserId(userInfoVO.getUserId());
        // 会员账号
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        // 会员姓名
        recordVO.setUserName(userInfoVO.getUserName());
        // 账号类型 1测试 2正式 3商务 4置换
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        // 三方会员账号
        recordVO.setCasinoUserName(entity.getSourceName());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        // 三方平台
        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        // 游戏平台名称
        // 游戏平台CODE
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        // 游戏大类
        recordVO.setVenueType(gameTypeId);
        // 游戏Code
        recordVO.setGameCode(VenueEnum.CMD.getVenueCode());
        SiteVO siteVO = siteMap.get(userInfoVO.getSiteCode());
        if (ObjectUtil.isNotEmpty(siteVO)) {
            recordVO.setSiteName(siteVO.getSiteName());
        }
        // 投注时间
        recordVO.setBetTime(getChangeDate(entity.getTransDate()));

        //判断赔率是否为负数算出马来的投注金额
        entity.setBetAmount(isNegativeSignum(entity.getOdds()) ? AmountUtils.multiply(entity.getBetAmount(), entity.getOdds().negate()) : entity.getBetAmount());
        // 投注额
        recordVO.setBetAmount(entity.getBetAmount());
        // 输赢金额
        recordVO.setWinLossAmount(entity.getWinAmount());
        // 有效投注, 需要根据输赢金额进行计算
        BigDecimal validBetAmount = super.computerValidBetAmount(entity.getBetAmount(), entity.getWinAmount(), VenueTypeEnum.SPORTS);
        recordVO.setValidAmount(validBetAmount);
        // 派彩金额
        recordVO.setPayoutAmount(BigDecimal.ZERO.compareTo(recordVO.getWinLossAmount()) != 0 ? recordVO.getBetAmount().add(recordVO.getWinLossAmount()) : null);
        // 注单状态
        CmdOrderStatusEnum cmdOrderStatusEnum = getSportStatus(entity.getStatusChange(), entity.getDangerStatus(), entity.getWinLoseStatus());
        recordVO.setOrderStatus(cmdOrderStatusEnum.getOrderStatusEnum().getCode());
        //取消的注单更改派彩金额、有效金额、输赢金额
        if (cmdOrderStatusEnum.getOrderStatusEnum().getCode() == OrderStatusEnum.CANCEL.getCode()) {
            recordVO.setValidAmount(BigDecimal.ZERO);
            recordVO.setPayoutAmount(BigDecimal.ZERO);
            recordVO.setWinLossAmount(BigDecimal.ZERO);
        }
        //不是待定状态的则存入结算时间
        if (!cmdOrderStatusEnum.getCode().equals(CmdOrderStatusEnum.P.getCode())) {
            // 结算时间
            recordVO.setSettleTime(getChangeDate(entity.getStateUpdateTs()));
        }
        // 结果牌 结果 resultList
        recordVO.setResultList(cmdOrderStatusEnum.getDescEn());
        // 注单归类
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        // 三方注单ID
        recordVO.setThirdOrderId(entity.getReferenceNo());
        recordVO.setOrderId(entity.getReferenceNo());
        recordVO.setThirdGameCode(entity.getSportType());
        recordVO.setGameName(entity.getSportType());
        // 赔率
        recordVO.setOdds(entity.getOdds().toString());

        String betIp = entity.getBetIp();
        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;
        // 投注IP
        recordVO.setBetIp(betIp);
        // 币种
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        // 设备类型
        recordVO.setDeviceType(deviceType);
        // 创建时间
        recordVO.setCreatedTime(System.currentTimeMillis());
        CmdOddTypeEnum oddTypeName = CmdOddTypeEnum.of(entity.getOddsType());
        //联赛信息
        StringBuffer leagueNames = new StringBuffer();
        StringBuffer orderInfo = new StringBuffer();
        StringBuffer gameNo = new StringBuffer();
        CmdBetTypeEnum cmdBetTypeEnum = CmdBetTypeEnum.of(entity.getTransType(), entity.getIsFirstHalf());
        if (ObjectUtils.isNotEmpty(cmdBetTypeEnum)) {
            // 下注玩法
            recordVO.setPlayType(cmdBetTypeEnum.getDescCn());
            recordVO.setPlayInfo(cmdBetTypeEnum.getDescCn());
            // 下注选项内容
            recordVO.setBetContent(ObjectUtils.isNotEmpty(cmdBetTypeEnum.getMapValue()) ? cmdBetTypeEnum.getMapValue().get(entity.getChoice()) : "");
            //串关内容解析
            if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())) {
                entity.setCollusionBetDetails(this.getCollusion(venueInfoVO, entity.getStateUpdateTs(), entity.getSocTransId()));
                for (CmdCollusionReqVO vo : entity.getCollusionBetDetails()) {
                    //串关单有ParTransType
                    CmdBetTypeEnum cmdCollusionBetTypeEnum = CmdBetTypeEnum.of(vo.getParTransType(), vo.getIsFH());
                    String betType = cmdCollusionBetTypeEnum.getDescCn();
                    String betContent = ObjectUtils.isNotEmpty(cmdCollusionBetTypeEnum.getMapCnValue()) ? cmdCollusionBetTypeEnum.getMapCnValue().get(vo.getChoice()) : "";
                    //是否主队去主队还是客队信息
                    Integer collusionteamId = vo.getIsBetHome() ? vo.getHomeId() : vo.getAwayId();
                    String collusionteamNames = getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), collusionteamId, null);
                    BigDecimal odds = vo.getParOdds();
                    orderInfo.append(getBetInfoStr(collusionteamNames, betType, betContent, odds.toString(), oddTypeName)).append(",");
                    leagueNames.append(getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(), entity.getLeagueId(), null)).append(",");
                    gameNo.append(String.valueOf(vo.getMatchId())).append(",");
                }
            } else {
                //是否主队去主队还是客队信息
                Integer teamId = entity.getIsBetHome() ? entity.getHomeTeamId() : entity.getAwayTeamId();
                String teamNames = getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), teamId, null);
                // 玩法注单展示需要
                orderInfo.append(getBetInfoStr(teamNames, recordVO.getPlayType(), recordVO.getBetContent(), entity.getOdds().toString(), oddTypeName)).append(",");
                leagueNames.append(getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(), entity.getLeagueId(), null)).append(",");
                gameNo.append(String.valueOf(entity.getMatchID())).append(",");
            }
        }
        // 局号/期号
        recordVO.setGameNo(gameNo.substring(0, gameNo.length() - 1));
        // 体育、电竞、彩票, 需要保存原始注单到串关字段中
        recordVO.setParlayInfo(JSON.toJSONString(entity));
        recordVO.setOrderInfo(orderInfo.substring(0, orderInfo.length() - 1));
        recordVO.setEventInfo(leagueNames.substring(0, leagueNames.length() - 1));
        recordVO.setTransactionId(recordVO.getThirdOrderId());
        return recordVO;
    }


    public String getBetInfoStr(String teamNames, String playType, String betContent, String odds, CmdOddTypeEnum oddTypeName) {
        return "投注:" + teamNames + " 投注类型:" + playType + " 投注项:" + betContent + " 赔率: " + odds + " 盘口:" + oddTypeName.getDescCn();
    }

    /**
     * 获取串关内容
     * 根据最新更新时间去最新的串关内容状态
     *
     * @param venueDetailVO
     * @param socTransId
     * @return
     */
    public List<CmdCollusionReqVO> getCollusion(VenueInfoVO venueDetailVO, Long stateUpdateTs, String socTransId) {
        String key = CacheConstants.VENUE_CMD_COLLUSION + VenuePlatformConstants.CMD + ":" + stateUpdateTs + ":" + socTransId;
        JSONObject reqdata = JSONObject.parseObject(RedisUtil.getValue(key));
        List<CmdCollusionReqVO> data = new ArrayList<>();
        if (ObjectUtils.isEmpty(reqdata)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            Map<String, String> params = new HashMap<>();
            String api = venueDetailVO.getApiUrl();
            // 运营商唯一代码
            params.put("Method", "parlaybetrecord");
            params.put("PartnerKey", venueDetailVO.getAesKey());
            params.put("SocTransId", socTransId);
            String respones = HttpClientHandler.get(api, headers, params);
            log.info("CMD体育获取串关params={},url={},{}", params, api, respones);
            reqdata = JSONObject.parseObject(respones);
            if (Objects.nonNull(reqdata) && reqdata.getIntValue("Code") != 0) {
                log.error("CMD体育获取串关：venueCode={}, 响应参数={}", venueDetailVO.getVenueCode(), respones);
            }
            if (reqdata != null && (reqdata.getIntValue("Code") == CommonConstant.business_zero)) {
                RedisUtil.setValue(key, respones, 31L, TimeUnit.DAYS);
            }
        }
        JSONArray betDetails = reqdata.getJSONArray("Data");
        if (betDetails != null && !betDetails.isEmpty()) {
            for (int i = 0; i < betDetails.size(); i++) {
                JSONObject betDetail = betDetails.getJSONObject(i);
                try {
                    CmdCollusionReqVO betDetailEntity = JSONObject.parseObject(betDetail.toJSONString(), CmdCollusionReqVO.class);
                    data.add(betDetailEntity);
                } catch (Exception e) {
                    log.error("{} 解析注单时间适配异常:{}", platformName(), betDetail);
                }
            }
        }
        return data;
    }

    public String getLanguageInfo(VenueInfoVO venueDetailVO, String type, Integer id, String lang) {
        String key = CacheConstants.VENUE_CMD_LANG + VenuePlatformConstants.CMD + ":" + type + ":" + id;
        JSONObject reqdata = JSONObject.parseObject(RedisUtil.getValue(key));
        CMDLangCodeEnum cmdLangCodeEnum = CMDLangCodeEnum.fromCode(lang);
        if (ObjectUtils.isNotEmpty(reqdata)) {
            return reqdata.getString(cmdLangCodeEnum.getCode());
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        String api = venueDetailVO.getApiUrl();
        // 运营商唯一代码
        params.put("Method", "languageinfo");
        params.put("PartnerKey", venueDetailVO.getAesKey());
        params.put("Type", type);
        params.put("ID", id + "");
        String respones = HttpClientHandler.get(api, headers, params);
        log.info("CMD体育获取国际化params={},url={},{}", params, api, respones);
        JSONObject jsonObject = JSONObject.parseObject(respones);
        if (Objects.nonNull(jsonObject) && jsonObject.getIntValue("Code") != 0) {
            log.error("CMD体育获取国际化：venueCode={}, 响应参数={}", venueDetailVO.getVenueCode(), respones);
        }
        if (jsonObject != null &&
                (jsonObject.getIntValue("Code") == CommonConstant.business_zero)) {
            reqdata = jsonObject.getJSONObject("Data");
            RedisUtil.setValue(key, reqdata.toJSONString(), 31L, TimeUnit.DAYS);
            return reqdata.getString(cmdLangCodeEnum.getCode());
        }
        return null;
    }


    /**
     * 是否为负数
     *
     * @param transactionAmount
     * @return
     */
    public boolean isNegativeSignum(BigDecimal transactionAmount) {
        return transactionAmount.signum() == -1;
    }

    public CmdOrderStatusEnum getSportStatus(Integer statusChange, String dangerStatus, String winLoseStatus) {
//        if (statusChange>=2){
//            return CmdOrderStatusEnum.RESETTLEMENT;
//        }
        if (dangerStatus.equals("C") || dangerStatus.equals("R")) {
            return CmdOrderStatusEnum.CANCELED;
        }
        return CmdOrderStatusEnum.of(winLoseStatus);
    }


    /**
     * 生成下一次拉单参数
     *
     * @param params 拉单参数对象
     * @return 参数
     */
    public CmdPullBetParams nextPullBetParams(CmdPullBetParams params) {
        CmdPullBetParams newPullBetParams = new CmdPullBetParams();
        newPullBetParams.setStarttime(params.getStarttime());
        newPullBetParams.setEndtime(params.getEndtime());
        Integer timeInterval = params.getTimeInterval() == null ? CmdConstant.DEFAULT_TIME_INTERVAL : params.getTimeInterval();
        newPullBetParams.setTimeInterval(timeInterval);
        // GMT+8当前时间
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt8ZoneId);
        // 当前查询区间的起始时间
        Date startAtTime = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getStarttime(), gmt8TimeZone, DateUtils.pattenT);
        // 当前时间减xx分钟的时间
        DateTime beforeTenMinutes = new DateTime(canadaCurrentDate).plusMillis(timeInterval * -1);
        // 如果起始时间 > (当前时间 - 10分钟), 则重新设置起始时间
        assert startAtTime != null;
        if (beforeTenMinutes.isBefore(startAtTime.getTime())) {
            // 开始时间设置（当前时间-10）
            newPullBetParams.setStarttime(TimeZoneUtils.formatDate4TimeZone(beforeTenMinutes.toDate(), gmt8TimeZone, DateUtils.pattenT));
            // 结束时间设置当前时间
            newPullBetParams.setEndtime(TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt8TimeZone, DateUtils.pattenT));
        } else {
            Date endAt = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getEndtime(), gmt8TimeZone, DateUtils.pattenT);
            // 起始时间 = 结束时间 (endAt)
            String starAtStr = TimeZoneUtils.formatDate4TimeZone(endAt, gmt8TimeZone, DateUtils.pattenT);
            // 结束时间 = 结束时间 (endAt) + timeInterval（例如 10 分钟）
            String endAtStr = TimeZoneUtils.formatDate4TimeZone(new DateTime(endAt).plusMillis(timeInterval).toDate(), gmt8TimeZone, DateUtils.pattenT);

            newPullBetParams.setStarttime(starAtStr);
            newPullBetParams.setEndtime(endAtStr);
        }
        log.info("{} 下一次拉单参数: {} | {}", platformName(), newPullBetParams.getStarttime(), newPullBetParams.getEndtime());
        return newPullBetParams;
    }


    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        String api = venueDetailVO.getApiUrl();
        // 运营商唯一代码
        params.put("Method", "kickuser");
        params.put("PartnerKey", venueDetailVO.getAesKey());
        params.put("UserName", venueUserAccount);
        log.info("CMD-用户踢线request, apiPath: {} , head:{}, request: {}", api, JSON.toJSONString(headers), params);
        try {
            String respones = HttpClientHandler.get(api, headers, params);
            log.info("{} CMD-用户踢线response, url: {} , head:{}, 参数: {} , 返回消息: {}", platformName(), api, JSON.toJSONString(headers), params, respones);
        } catch (Exception e) {
            log.error("CMD-用户踢线异常【{}】", venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }

    public String platformName() {
        return VenueEnum.CMD.getVenueName();
    }

    public static Long getChangeDate(Long transdate) {
        //获取时间
//        long s1 = 638905178030000000L; // Transdate GMT+8
        Long s2 = 621355968000000000L; // 1970-01-01 GMT+0 in .NET ticks  - 8小时 8*3600 28800
        return (((transdate - s2) / 10000000) - (28800)) * 1000;
    }


    public String doAction(CmdReq cmdReq) {
        VenueInfoVO venueInfoVO = venueInfoService.getVenueInfoByVenueCode(VenueEnum.CMD.getVenueCode());
        String key = venueInfoVO.getAesKey();
        String method = cmdReq.getMethod();
        String returnData = null;
        String data = AESUtil.decrypt(cmdReq.getBalancePackage(), key);
        JSONObject obj = JSONObject.parseObject(data);
        log.info(" {} Cmd-doAction :{}", method, obj.toString());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        returnData = switch (method) {
            case "GetBalance" -> getBalance(cmdReq, obj);
            case "DeductBalance" -> bet(cmdReq, obj);
            case "UpdateBalance" -> batchProcess(cmdReq, obj);
            default -> CmdBaseRsp.err(CmdRespErrEnums.METHON_NOT_FOUND, cmdReq);
        };
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        if (totalTimeMillis >= 3000) {
            log.info("Cmd-doAction 请求: {} 返回: {} 耗时: {}ms", obj.toString(), returnData, totalTimeMillis);
        }
        String respData = AESUtil.encrypt(returnData, key);
        log.info("Cmd-doAction 加密前: {} 加密后: {}", returnData, respData);
        return respData;
    }

    public String token(String token) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueCode(VenueEnum.CMD.getVenueCode());
        //只有authorize 才有token，所以才对token获取
        if (StringUtils.isNotEmpty(token)) {
            casinoMemberReqVO.setCasinoPassword(token);
        }
        CasinoMemberVO respVO = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("authenticate");
        if (respVO == null) {
            root.addElement("member_id").addText("");
            root.addElement("status_code").addText(CmdRespErrEnums.MEMBER_NOT_EXIST.getCode() + "");
            root.addElement("message").addText(CmdRespErrEnums.MEMBER_NOT_EXIST.getDescription());
        } else {
            root.addElement("member_id").addText(respVO.getVenueUserAccount());
            root.addElement("status_code").addText("0");
            root.addElement("message").addText(CmdRespErrEnums.SUCCESS.getDescription());
        }
        return document.asXML();
    }

    private String getBalance(CmdReq cmdReq, JSONObject request) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getString("SourceName"));
        casinoMemberReqVO.setVenueCode(VenueEnum.CMD.getVenueCode());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return CmdUserRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST, cmdReq);
        }
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = Lists.newArrayList(VenueEnum.CMD.getVenueCode());
        if (!venueCodes.contains(venueCode)) {
            return CmdUserRsp.err(CmdRespErrEnums.GAME_MAINTAINED, cmdReq);
        }
        if (venueMaintainClosed(venueCode, casinoMember.getSiteCode())) {
            return CmdUserRsp.err(CmdRespErrEnums.GAME_MAINTAINED, cmdReq);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }
        return CmdUserRsp.success(cmdReq, balance);
    }

    private String bet(CmdReq cmdReq, JSONObject request) {
        log.info("CMD bet : " + request.toString());
        CmdBetReq req = request.toJavaObject(CmdBetReq.class);
        req.setTransactionAmount(isNegativeSignum(req.getTransactionAmount()) ? req.getTransactionAmount().abs() : req.getTransactionAmount());
        CasinoMemberVO casinoMember = userCheck(req.getSourceName());
        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        String isValid = checkRequestValid(userInfoVO, cmdReq, true);
        if (isValid != null) {
            return isValid;
        }
        String venueCode = casinoMember.getVenueCode();
        UserCoinWalletVO userCoinWalletVO = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal centerAmount = BigDecimal.ZERO;
        if(userCoinWalletVO != null){
            centerAmount = userCoinWalletVO.getCenterAmount();
        }
        //用户中心钱包余额
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, req.getReferenceNo()));
        try {
            if (!rLock.tryLock()) {
                log.error("CMD errorBet error get locker error, req:{}", req);
                return CmdUserRsp.err(CmdRespErrEnums.ACCOUNT_LOCKED, cmdReq, centerAmount);
            }


            // 检查余额
            if (!(centerAmount.compareTo(BigDecimal.ZERO) > 0) && centerAmount.compareTo(req.getTransactionAmount().abs()) >= 0) {
                log.info("CMD 用户余额不足，userId:{},siteCode:{},money:{},", userInfoVO.getUserId(), userInfoVO.getSiteCode(), req.getTransactionAmount());
                return CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq, centerAmount);
            }
            //投注金额为0直接跳过
            if (BigDecimal.ZERO.compareTo(req.getTransactionAmount()) == 0) {
                CmdUserRsp.success(cmdReq, centerAmount);
            }

            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(req.getReferenceNo());
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(req.getTransactionAmount().abs());
            userCoinAddVO.setRemark(cmdReq.getPackageId());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));


            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
            userCoinAddVO.setVenueCode(VenuePlatformConstants.CMD);
            userCoinAddVO.setThirdOrderNo(cmdReq.getPackageId());
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS -> {
                    yield CmdUserRsp.success(cmdReq, coinRecordResultVO.getCoinAfterBalance());
                }
                case REPEAT_TRANSACTIONS -> {
                    yield CmdUserRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq, centerAmount);
                }
                default -> {
                    yield CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq, centerAmount);
                }
            };
        } catch (Exception e) {
            log.error("CMD failed bet error {}", e.getMessage());
            return CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq, centerAmount);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private String batchProcess(CmdReq cmdReq, JSONObject request) {
        CmdUpdateBaseReq req = request.toJavaObject(CmdUpdateBaseReq.class);
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueEnum.CMD.getVenueCode(), cmdReq.getPackageId()));
        try {
            return Optional.ofNullable(req.getTicketDetails()).map(lst -> {
                String lastResult = null;
                for (CmdUpdateDataReq item : lst) {
                    lastResult = processItem(item, cmdReq, req.getActionId()); // 调用处理方法
                }
                return lastResult; // 返回最后一次调用的结果
            }).orElse(CmdBaseRsp.success(cmdReq));
        } catch (Exception e) {
            log.error("CMD failed bet error {}", e.getMessage());
            return CmdUserRsp.err(CmdRespErrEnums.BETORDERNO_NOT_HAVA, cmdReq);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }


    private String processItem(CmdUpdateDataReq item, CmdReq cmdReq, Integer actionId) {
        if (Objects.isNull(item)) {
            return CmdUserRsp.success(cmdReq);
        }
        CasinoMemberVO casinoMember = userCheck(item.getSourceName());
        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (Objects.isNull(userInfoVO)) {
            log.error("CMD processItem userName:{} not find.", item.getSourceName());
            return CmdBaseRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST, cmdReq);
        }
        //派彩金额为0直接跳过
        if (Objects.isNull(item.getTransactionAmount()) || BigDecimal.ZERO.compareTo(item.getTransactionAmount()) == 0) {
            log.error("CMD processItem success userName:{} , amount:{}", item.getSourceName(), item.getTransactionAmount());
            return CmdUserRsp.success(cmdReq);
        }
        //检测是否有投注注单不存在则直接退出
        if (checkBetUserCoinRecordVO(userInfoVO, item.getReferenceNo(), CoinBalanceTypeEnum.EXPENSES.getCode(), WalletEnum.CoinTypeEnum.GAME_BET.getCode(), null)) {
            return CmdBaseRsp.err(CmdRespErrEnums.BETORDERNO_NOT_HAVA, cmdReq);
        }
        return switch (actionId) {
            case CmdConstant.DangerRefund, CmdConstant.ResettleTicket -> {
                yield cancel(userInfoVO, item, cmdReq);
            }
            default -> {
                yield updateBalance(userInfoVO, item, cmdReq);
            }
        };
    }

    private String cancel(UserInfoVO userInfoVO, CmdUpdateDataReq item, CmdReq cmdReq) {

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(item.getReferenceNo());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(item.getTransactionAmount().abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenuePlatformConstants.CMD);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        userCoinAddVO.setThirdOrderNo(cmdReq.getPackageId());
        userCoinAddVO.setRemark(cmdReq.getPackageId());
        CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
//        CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancel(userInfoVO, item.getReferenceNo(), item.getTransactionAmount(), cmdReq.getPackageId());
        return switch (coinRecordResultVO.getResultStatus()) {
            case SUCCESS -> {
                yield CmdBaseRsp.success(cmdReq);
            }
            default -> {
                yield CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq);
            }
        };
    }

    private String updateBalance(UserInfoVO userInfoVO, CmdUpdateDataReq item, CmdReq cmdReq) {
        //根据三方给的金额判断是收入还是支出
        String coinBalanceType = isNegativeSignum(item.getTransactionAmount()) ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode();
        String checkCoinType = isNegativeSignum(item.getTransactionAmount()) ? WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode() : WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode();
        //检测当前注单本批次有没有派彩是否处理过
        if (!checkBetUserCoinRecordVO(userInfoVO, item.getReferenceNo(), coinBalanceType, null, cmdReq.getPackageId())) {
            return CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq);
        }
        //根据 收入|支出 和 派彩|派彩取消 查询订单号是否有存在的，然后给派彩或者重派彩，如果状态为植入则给出取消派彩
        boolean isHaveCoinRecord = checkBetUserCoinRecordVO(userInfoVO, item.getReferenceNo(), coinBalanceType, checkCoinType, null);
        String coinType = null;
        String businessCoinType = null;
        String customerCoinType = null;
        String accountCoinType = null;
        if (CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType) && isHaveCoinRecord) {
            coinType = WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode();
            businessCoinType = WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode();
            customerCoinType = WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode();
            accountCoinType = AccountCoinTypeEnums.GAME_PAYOUT.getCode();
        } else if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(coinBalanceType) && isHaveCoinRecord) {
            coinType = WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
            businessCoinType = WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
            customerCoinType = WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
            accountCoinType = AccountCoinTypeEnums.GAME_CANCEL_BET.getCode();
        } else {
            coinType = WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode();
            businessCoinType = CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType) ? WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode() : WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
            customerCoinType = CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType) ? WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode() : WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
            accountCoinType = CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType) ?
                    AccountCoinTypeEnums.GAME_PAYOUT.getCode() : AccountCoinTypeEnums.GAME_CANCEL_BET.getCode();

        }
        // 正常派彩
        item.setTransactionAmount(isNegativeSignum(item.getTransactionAmount()) ? item.getTransactionAmount().abs() : item.getTransactionAmount());
        CoinRecordResultVO coinRecordResultVO = updateBalancePayoutLocal(userInfoVO, item.getReferenceNo(), item.getTransactionAmount(), cmdReq.getPackageId(),
                coinBalanceType, coinType, businessCoinType, customerCoinType,accountCoinType);
        return switch (coinRecordResultVO.getResultStatus()) {
            case SUCCESS -> {
                yield CmdBaseRsp.success(cmdReq);
            }
            case REPEAT_TRANSACTIONS -> {
                yield CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq);
            }
            default -> {
                yield CmdBaseRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq);
            }
        };
    }

    protected CoinRecordResultVO updateBalancePayoutLocal(UserInfoVO userInfoVO, String transactionId, BigDecimal payoutAmount, String remark,
                                                          String balanceType, String coinType, String businessCoinType, String customerCoinType,
                                                          String accountCoinType) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(transactionId);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(balanceType);
        userCoinAddVOPayout.setCoinType(coinType);
        userCoinAddVOPayout.setBusinessCoinType(businessCoinType);
        userCoinAddVOPayout.setCustomerCoinType(customerCoinType);
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setAccountCoinType(accountCoinType);
        userCoinAddVOPayout.setVenueCode(VenuePlatformConstants.CMD);
        userCoinAddVOPayout.setThirdOrderNo(remark);
        return toUserCoinHandle(userCoinAddVOPayout);
    }


    /**
     * 投注取消
     */
    protected CoinRecordResultVO updateBalanceBetCancel(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));


        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.CMD);
        userCoinAddVO.setThirdOrderNo(remark);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    private Boolean checkBetUserCoinRecordVO(UserInfoVO userInfoVO, String referenceNo, String coinBalanceType, String coinType, String remark) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setOrderNo(referenceNo);
        if (StringUtils.isNotBlank(remark)) {
            coinRecordRequestVO.setRemark(remark);
        }
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setBalanceType(coinBalanceType);
        if (StringUtils.isNotBlank(coinType)) {
            coinRecordRequestVO.setCoinType(coinType);
        }
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        boolean flag = false;
        if (CollectionUtil.isEmpty(userCoinRecords) || userCoinRecords.isEmpty()) {
            log.error("CMD 查询交易订单为空 或不存在 req:{}", referenceNo);
            flag = true;
        }
        return flag;
    }


    public CasinoMemberVO userCheck(String userAccount) {

        CasinoMemberReq casinoMember = new CasinoMemberReq();
        casinoMember.setVenueUserAccount(userAccount);
        casinoMember.setVenueCode(VenueEnum.CMD.getVenueCode());
        return casinoMemberService.getCasinoMember(casinoMember);
    }

    public String checkRequestValid(UserInfoVO userInfoVO, CmdReq cmdReq, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("CMD getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.CMD.getVenueName());
            return CmdBaseRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST, cmdReq);
        }


        if (venueMaintainClosed(VenueEnum.CMD.getVenueCode(), userInfoVO.getSiteCode())) {
            log.info("场馆未开启:{} ", VenueEnum.CMD.getVenueCode());
            return CmdBaseRsp.err(CmdRespErrEnums.GAME_MAINTAINED, cmdReq);
        }

        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.CMD.getVenueCode());
            return CmdBaseRsp.err(CmdRespErrEnums.ACCOUNT_LOCKED, cmdReq);
        }

        if (isBetting) {
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(),
                    VenueEnum.CMD.getVenueCode(), userInfoVO.getMainCurrency());
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.CMD.getVenueCode());
                return CmdBaseRsp.err(CmdRespErrEnums.VENUE_CLOSE, cmdReq);
            }
        }
        return null;
    }



}
