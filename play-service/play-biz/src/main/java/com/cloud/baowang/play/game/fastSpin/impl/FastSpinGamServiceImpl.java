package com.cloud.baowang.play.game.fastSpin.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.fastSpin.AcctInfo;
import com.cloud.baowang.play.api.vo.fastSpin.req.AuthorizeReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.api.vo.fastSpin.res.FSBalanceRes;
import com.cloud.baowang.play.api.vo.fastSpin.res.FSBaseRes;
import com.cloud.baowang.play.api.vo.fastSpin.res.FSTransferRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.fastSpin.enums.*;
import com.cloud.baowang.play.game.fastSpin.utils.DigestUtils;
import com.cloud.baowang.play.game.fastSpin.utils.FSESUtil;
import com.cloud.baowang.play.game.fastSpin.vo.BetInfo;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.fastSpin.params.FastSpinPullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.FASTSPIN)
@AllArgsConstructor
public class FastSpinGamServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;
    private final CasinoMemberService casinoMemberService;
    private final UserInfoApi userInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final UserCoinApi userCoinApi;
    private final VenueInfoService venueInfoService;

    private final static String NULL_STR = "null";

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
                    .venueCode(VenueEnum.FASTSPIN.getVenueCode()).type(GameLoginTypeEnums.URL.getType()).build());
        } else {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
    }

    //4.1 通过 API 启动游戏
    private ResponseVO<String> getGameURL(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        String optApi = "getAuthorize";
        FSCurrencyEnum fsCurrencyEnum = FSCurrencyEnum.fromCode(loginVO.getCurrencyCode());

        FSLanguageEnum fsLanguageEnum = FSLanguageEnum.fromCode(loginVO.getLanguageCode());
        AcctInfo acctInfo = AcctInfo.builder()
                .acctId(casinoMemberVO.getVenueUserAccount())
                .currency(fsCurrencyEnum.name())
                .build();
        AuthorizeReq authorizeReq = AuthorizeReq.builder()
                .acctInfo(acctInfo)
                .merchantCode(venueInfoVO.getMerchantNo())
                .token(UUID.randomUUID().toString().replaceAll("-", ""))
                .acctIp(loginVO.getIp())
                .game(loginVO.getGameCode())
                .language(fsLanguageEnum.name())
                .menuMode(false)
                .build();
        String jsonData = JSON.toJSONString(authorizeReq);
        try (HttpResponse response = HttpRequest.post(venueInfoVO.getApiUrl())
                .header(Header.CONTENT_TYPE, "application/json;charset=utf-8")
                .header(API, optApi)
                .header(DATATYPE, DATATYPE_JSON)
                .header(DIGEST, DigestUtils.digest(jsonData, venueInfoVO.getMerchantKey()))
                .body(jsonData).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                String code = jsonObject.getString(CODE);
                if (FSResCodeEnum.CODE_0.getCode().equals(code)) {
                    return ResponseVO.success(jsonObject.getString("gameUrl"));
                } else {
                    log.error("{} getGameURL 返回错误, 请求返回 {}", VenueEnum.FASTSPIN.getVenueName(), response.body());
                }
            } else {
                log.error("{} getGameURL 请求错误, 返回 {}", VenueEnum.FASTSPIN.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} getGameURL 获取游戏列表发生错误!", VenueEnum.FASTSPIN.getVenueName(), e);
        }
        return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);

    }

    @Override
    public ResponseVO<FastSpinPullBetParams> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {

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
                BetInfo betInfo = obj.to(BetInfo.class);
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
                recordVO.setVenueType(VenueEnum.FASTSPIN.getType().getCode());
                recordVO.setCasinoUserName(userVenueAccount);
                recordVO.setBetIp(betInfo.getBetIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                recordVO.setThirdOrderId(betInfo.getTicketId());
                recordVO.setPayoutAmount(betInfo.getWinLoss().subtract(betInfo.getBetAmount()));
                //NOTE 中奖减投注
                recordVO.setWinLossAmount(betInfo.getWinLoss());
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                FSCategoryEnum fsCategoryEnum = FSCategoryEnum.fromName(betInfo.getCategoryId());
                recordVO.setRoomType(fsCategoryEnum.name());
                recordVO.setRoomTypeName(fsCategoryEnum.getDesc());

                //NOTE 结算时间 暂时以下注时间一样
                if (recordVO.getBetTime() != null) {
                    recordVO.setSettleTime(recordVO.getBetTime());
                }
                //NOTE 状态
                if (betInfo.getWinLoss() == null || betInfo.getCompleted() == null || !betInfo.getCompleted()) {
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
                recordVO.setTransactionId(betInfo.getReferenceId());
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
        log.info("FastSpin 电子, getBetRecordList 拉单结束, 共拉单: {}条", list.size());
        return ResponseVO.success();
    }


    //4.4.1 查询用户下注记录
    JSONArray getBetHistory(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO, int pageIndex, DateTimeFormatter dateTimeFormatter) {

        try {
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
            paramMap.put("serialNo", FSESUtil.generalSerialNo());
            String jsonData = JSON.toJSONString(paramMap);
            String digest = MD5Util.md5(jsonData + venueInfoVO.getMerchantKey());

            System.out.println();

            try (HttpResponse response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/json;charset=utf-8")
                    .header(API, optApi)
                    .header(DATATYPE, DATATYPE_JSON)
                    .header(DIGEST, digest)
                    .body(jsonData).execute()) {
                if (response.isOk()) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    String code = jsonObject.getString(CODE);
                    if (FSResCodeEnum.CODE_0.getCode().equals(code)) {
                        JSONArray list = jsonObject.getJSONArray("list");
                        if (CollUtil.isNotEmpty(list)) {
                            return list;
                        }
                    } else {
                        log.error("{} getBetHistory 返回错误, 请求返回 {}", VenueEnum.FASTSPIN.getVenueName(), response);
                    }
                } else {
                    log.error("{} getBetHistory 请求错误, 返回 {}", VenueEnum.FASTSPIN.getVenueName(), response);
                }
            } catch (Exception e) {
                log.error("{} getBetHistory 获取游戏记录发生错误!", VenueEnum.FASTSPIN.getVenueName(), e);
            }
        } catch (Exception e) {
            log.error("{} getBetHistory 调用接口前发生错误.", VenueEnum.FASTSPIN.getVenueName(), e);
        }
        return new JSONArray();
    }


    /**
     * 4.3.1 查询用户余额 接口名：getBalance
     */
    public Object getBalance(FSBalanceReq vo, String digest) {

        try {
            String acctId = vo.getAcctId();
            String merchantCode = vo.getMerchantCode();

            if (StrUtil.isEmpty(acctId) || acctId.equals(NULL_STR) || acctId.split("_").length != 2) {
                log.error("FS balance 但参数不全, acctId:{} ", acctId);
                return FSResCodeEnum.CODE_105.toResVO(new FSBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FASTSPIN.getVenueCode()).userId(split[1]).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FS balance casinoMember不存在, balance, 参数:{} ", vo);
                return FSResCodeEnum.C_50100.toResVO(new FSBaseRes());
            }

            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} FS balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.C_50100.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} getBalance game lock, user参数:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
                return FSResCodeEnum.C_50102.toResVO();
            }

            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FASTSPIN.getVenueCode(), null);
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{} FS balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.C_5003.toResVO();
            }
            if (StrUtil.isNotEmpty(merchantCode) && !merchantCode.equals(venueInfoVO.getMerchantNo())) {
                return FSResCodeEnum.C_10113.toResVO();
            }

            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!DigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return FSResCodeEnum.C_50104.toResVO();
            }

            FSCurrencyEnum fsCurrencyEnum = FSCurrencyEnum.fromCode(userInfoVO.getMainCurrency());

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            AcctInfo acctInfo = AcctInfo.builder().acctId(casinoMember.getVenueUserAccount())
                    .balance(userCenterCoin.getTotalAmount()).currency(fsCurrencyEnum.name()).build();

            return FSResCodeEnum.CODE_0.toResVO(FSBalanceRes.builder().acctInfo(acctInfo)
                    .merchantCode(venueInfoVO.getMerchantNo()).serialNo(FSESUtil.generalSerialNo()).build());
        } catch (Exception e) {
            log.error("{} FS balance Exception, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
        }

        return FSResCodeEnum.CODE_1.toResVO();

    }

    /*
     * 4.3.2 转帐 接口名：transfer
     *
     * 使用场景：
        1. 户的余额中扣除；
        2. 户的余额
     *
     */
    public Object transfer(FSTransferReq vo, String digest) {

        try {
            String acctId = vo.getAcctId();
            String merchantCode = vo.getMerchantCode();

            if (StrUtil.isEmpty(acctId) || acctId.equals(NULL_STR) || acctId.split("_").length != 2) {
                log.error("FS transfer 但参数不全, acctId:{} ", acctId);
                return FSResCodeEnum.CODE_105.toResVO(new FSBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FASTSPIN.getVenueCode()).userId(split[1]).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FS transfer casinoMember不存在, balance, 参数:{} ", vo);
                return FSResCodeEnum.C_50100.toResVO(new FSBaseRes());
            }

            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} FS transfer userInfoVO不存在, balance, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.C_50100.toResVO();
            }

            //NOTE 查询场馆状态 type == bet 才会限制
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FASTSPIN.getVenueCode(), null);
            if (venueInfoVO == null || (StrUtil.isNotEmpty(merchantCode) && !merchantCode.equals(venueInfoVO.getMerchantNo()))) {
                log.error("{} FS transfer venueInfoVO不存, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.C_10113.toResVO();
            }
            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!DigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return FSResCodeEnum.C_50104.toResVO();
            }

            TransferTypeEnum typeEnum = TransferTypeEnum.fromCode(vo.getType());

            if (typeEnum == null || TransferTypeEnum.PLACE_BET == typeEnum) {
                if (!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                    log.error("{} FS transfer venueInfoVO不存在或不开放, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
                    return FSResCodeEnum.C_5003.toResVO();
                }
            }
            FSCurrencyEnum.fromCode(userInfoVO.getMainCurrency());

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            FSTransferRes transferRes = FSTransferRes.builder()
                    .transferId(vo.getTransferId())
                    .merchantCode(merchantCode)
                    .acctId(acctId)
                    .balance(userCenterCoin.getTotalAmount())
                    .serialNo(FSESUtil.generalSerialNo())
                    .build();
            //账变金额为 < 0 不处理
            if (vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("{} transfer, 无账变金额, 请求参数: {}", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.CODE_0.toResVO(transferRes);
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
                    userCoinAddVO.setThirdOrderNo(vo.getTransferId());
                    getBetCoinAddVO(userCoinAddVO);
                    if (venueGameMaintainClosed(venueInfoVO.getVenueCode(),casinoMember.getSiteCode(),vo.getGameCode())){
                        log.info("{}:游戏未开启", VenueEnum.FASTSPIN.getVenueName());
                        return FSResCodeEnum.C_5003.toResVO(transferRes);
                    }
                    if (userGameLock(userInfoVO)) {
                        log.error("{} transfer bet game lock, user参数:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
                        return FSResCodeEnum.C_50102.toResVO();
                    }
                    //账变金额为 < 0 不处理
                    if (vo.getAmount().compareTo(userCenterCoin.getTotalAmount()) > 0) {
                        log.error("{} transfer PLACE_BET, 余额不足, 请求参数: {}", VenueEnum.FASTSPIN.getVenueCode(), vo);
                        return FSResCodeEnum.C_50110.toResVO(transferRes);
                    }

                    break;
                case PAYOUT:
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case BONUS:
                    log.info("FS transfer bonus operation: {}", vo);
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case CANCEL_BET:
                    log.info("FS transfer cancel bet operation: {}", vo);
                    userCoinAddVO.setOrderNo(vo.getReferenceId());
                    userCoinAddVO.setRemark(vo.getSerialNo());
                    userCoinAddVO.setThirdOrderNo(vo.getSerialNo());
                    getCancelBetCoinAddVO(userCoinAddVO);

                    //NOTE 确认成功下注
                    UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
                    userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
                    userCoinRecordVo.setUserId(userInfoVO.getUserId());
                    userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
                    userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                    List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
                    if (CollUtil.isEmpty(userCoinRecords)) {
                        log.error("{} transfer 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", VenueEnum.FASTSPIN.getVenueCode(), vo);
                        return FSResCodeEnum.CODE_0.toResVO(transferRes);
                    }
                    break;
            }

            if (StrUtil.isEmpty(userCoinAddVO.getOrderNo())) {
                return FSResCodeEnum.CODE_105.toResVO(transferRes);
            }

            //NOTE 去重操作
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordVo.setRemark(userCoinAddVO.getRemark());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isEmpty(userCoinRecords)) {
                log.error("{} transfer失败, 当前reference已经被处理, 请求参数: {}", VenueEnum.FASTSPIN.getVenueCode(), vo);
                return FSResCodeEnum.CODE_0.toResVO(transferRes);
            }

            userCoinAddVO.setVenueCode(VenuePlatformConstants.FASTSPIN);
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> FSResCodeEnum.C_50110.toResVO(transferRes);
                //NOTE 走默认： case FAIL, WALLET_NOT_EXIST -> FSResCodeEnum.CODE_1.toResVO(transferRes);
                //NOTE 重复操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS -> FSResCodeEnum.CODE_0.toResVO(transferRes);
                case SUCCESS -> {
                    transferRes.setBalance(recordResultVO.getCoinAfterBalance());
                    FSResCodeEnum.CODE_0.toResVO(transferRes);
                }
                default -> FSResCodeEnum.CODE_1.toResVO(transferRes);

            }
            return transferRes;
        } catch (Exception e) {
            log.error("{} transfer Exception, 参数:{} ", VenueEnum.FASTSPIN.getVenueCode(), vo);
        }
        return FSResCodeEnum.CODE_1.toResVO();

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
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", VenueEnum.FASTSPIN.getVenueName(), 0);
            return ResponseVO.success(resultList);
        }
        return ResponseVO.success(resultList);
    }

    //4.4.2 查询游戏列表信息
    public JSONArray getGameList(VenueInfoVO venueInfoVO) {
        String optApi = "getGames";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("serialNo", FSESUtil.generalSerialNo());
        paramMap.put("merchantCode", venueInfoVO.getMerchantNo());
        String jsonData = JSON.toJSONString(paramMap);
        try (HttpResponse response = HttpRequest.post(venueInfoVO.getApiUrl())
                .header(Header.CONTENT_TYPE, "application/json;charset=utf-8")
                .header(API, optApi)
                .header(DATATYPE, DATATYPE_JSON)
                .header(DIGEST, DigestUtils.digest(jsonData, venueInfoVO.getMerchantKey()))
                .body(jsonData).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                if (FSResCodeEnum.CODE_0.getCode().equals(jsonObject.getString(CODE))) {
                    return jsonObject.getJSONArray("games");
                } else {
                    log.error("{} getGameList 返回错误, 请求返回 {}", VenueEnum.FASTSPIN.getVenueName(), response.body());
                }
            } else {
                log.error("{} getGameList 请求错误, 返回 {}", VenueEnum.FASTSPIN.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} getGameList 获取游戏列表发生错误!", VenueEnum.FASTSPIN.getVenueName(), e);
        }
        return new JSONArray();
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
    }
}
