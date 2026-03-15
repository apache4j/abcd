package com.cloud.baowang.play.game.acelt.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.api.exceptions.ACELTDefaultException;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.acelt.ACELTCurrencyEnum;
import com.cloud.baowang.play.api.enums.acelt.ACELTInOutTypeEnum;
import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
import com.cloud.baowang.play.api.enums.acelt.ACELTTransferTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueJoinTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.acelt.*;
import com.cloud.baowang.play.api.vo.base.ACELTBaseRes;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.game.ACELTMaxAmount;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.acelt.constant.AceLtConstant;
import com.cloud.baowang.play.game.acelt.enums.AceLtBetResultStatusEnums;
import com.cloud.baowang.play.game.acelt.enums.AceLtPlayTypeEnum;
import com.cloud.baowang.play.game.acelt.enums.AceltGameInfoEnum;
import com.cloud.baowang.play.game.acelt.enums.AceltLangEnum;
import com.cloud.baowang.play.game.acelt.response.AceLtBetRecord;
import com.cloud.baowang.play.game.acelt.response.AceLtLoginRes;
import com.cloud.baowang.play.game.acelt.response.AceLtPageRes;
import com.cloud.baowang.play.game.acelt.response.AceLtRes;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.ThirdPullBetPartitionComponent;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AceltGameBaseServiceImpl extends GameBaseService {

    private final OrderRecordProcessService orderRecordProcessService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SystemParamApi systemParamApi;


    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoRspVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(Boolean.TRUE);
    }


    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        Integer type = DeviceType.PC.getCode();
        UserInfoVO userInfoVO = getByUserId(CurrReqUtils.getOneId());
        String currencyCode = userInfoVO.getMainCurrency();

        if (!Objects.equals(CurrReqUtils.getReqDeviceType(), DeviceType.PC.getCode())) {
            type = 0;//H5彩票就两种类型
        }

        ACELTCurrencyEnum aceltCurrencyEnum = ACELTCurrencyEnum.getPlatCurrencyEnum(currencyCode);

        if (aceltCurrencyEnum == null) {
            log.info("彩票游戏未匹配到币种:{}", currencyCode);
            throw new BaowangDefaultException(ResultCode.VENUE_CURRENCY_NOT);
        }

        Map<String, Object> signMap = new HashMap<>();
        signMap.put("operatorAccount", casinoMemberVO.getVenueUserAccount());
        signMap.put("secretKey", venueInfoVO.getMerchantKey());
        signMap.put("operatorId", venueInfoVO.getMerchantNo());

        JSONObject paramsMap = new JSONObject();
        paramsMap.put("operatorAccount", casinoMemberVO.getVenueUserAccount());
        paramsMap.put("operatorId", venueInfoVO.getMerchantNo());
        paramsMap.put("sign", sign(signMap));
        paramsMap.put("type", type);
        paramsMap.put("currencyType", aceltCurrencyEnum.getCurrencyCode());
        try {
            String path = venueInfoVO.getApiUrl() + AceLtConstant.LOGIN;
            String rsp = HttpClientHandler.post(path, paramsMap.toString());
            AceLtRes<AceLtLoginRes> aceLtRes = JSON.parseObject(rsp, new TypeReference<AceLtRes<AceLtLoginRes>>() {
            });
            if (AceLtConstant.SUCC_CODE.equals(aceLtRes.getCode())) {
                if (null != aceLtRes.getData()) {
                    AceLtLoginRes aceLtLoginRes = aceLtRes.getData();
                    GameLoginVo loginVo = GameLoginVo.builder()
                            .source(aceLtLoginRes.getPlatformToken())
                            .type(GameLoginTypeEnums.TOKEN.getType())
                            .userAccount(addVenueUserAccountPrefix(userInfoVO.getUserId(), venueInfoVO.getVenueCode()))
                            .venueCode(venueInfoVO.getVenueCode())
                            .merchantNo(venueInfoVO.getMerchantNo())
                            .build();

                    Integer venueJoinType = venueInfoVO.getVenueJoinType();
                    //场馆的方式返回URL
                    if (VenueJoinTypeEnum.VENUE.getCode().equals(venueJoinType)) {
                        loginVo.setSource(aceLtLoginRes.getUrl());
                        loginVo.setType(GameLoginTypeEnums.URL.getType());
                    }

                    return ResponseVO.success(loginVo);
                }
            }
        } catch (Exception e) {
            log.error(String.format("用户名【%s】 Ace彩票登录发生错误!!", casinoMemberVO.getVenueUserAccount()), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }

    public List<ShDeskInfoVO> queryGameList() {
        ResponseVO<List<CodeValueVO>> result = systemParamApi.getSystemParamByType(CommonConstant.LT_GAME_TYPE);
        List<CodeValueVO> list = result.getData();
        return list.stream().map(x -> {
            return ShDeskInfoVO.builder().deskNumber(x.getCode()).deskName(I18nMessageUtil.getI18NMessageInAdvice(x.getValue())).build();
        }).toList();
    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        try {
            if (CollectionUtil.isEmpty(gameCategoryCodes) || CollectionUtil.isEmpty(gameCodes)) {
                return ResponseVO.success(Lists.newArrayList());
            }
            gameCategoryCodes = gameCategoryCodes.stream().distinct().toList();
            gameCodes = gameCodes.stream().distinct().toList();
            JSONObject paramsMap = new JSONObject();
            paramsMap.put("operatorId", venueInfoVO.getMerchantNo());
            paramsMap.put("gameCategoryCodes", String.join(",", gameCategoryCodes));
            paramsMap.put("gameCodes", String.join(",", gameCodes));
            paramsMap.put("lang", AceltLangEnum.conversionLang(CurrReqUtils.getLanguage()));
            String path = venueInfoVO.getApiUrl() + AceLtConstant.QUERY_GAME_LIST;
            String rsp = HttpClientHandler.post(path, paramsMap.toString());
            AceLtRes<List<JSONObject>> aceLtRes = JSON.parseObject(rsp, new TypeReference<AceLtRes<List<JSONObject>>>() {
            });
            if (aceLtRes == null) {
                log.info("调用彩票失败....");
                return ResponseVO.success(Lists.newArrayList());
            }

            if (aceLtRes.getCode() == null || aceLtRes.getCode() != 0) {
                log.info("调用彩票失败....");
                return ResponseVO.success(Lists.newArrayList());
            }
            return ResponseVO.success(aceLtRes.getData());
        } catch (Exception e) {
            log.info("调用彩票异常:", e);
        }
        return ResponseVO.success(Lists.newArrayList());
    }


    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        Long startTime = venuePullParamVO.getStartTime();
        Long endTime = venuePullParamVO.getEndTime();

        Map<String, Object> signMap = new HashMap<>();
        signMap.put("secretKey", venueInfoVO.getMerchantKey());
        signMap.put("operatorId", venueInfoVO.getMerchantNo());

        List<SiteVO> siteAll = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteMap = siteAll.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        List<String> siteCodeList = siteAll.stream().map(SiteVO::getSiteCode).toList();

        //每个站点的汇率
        Map<String, Map<String, BigDecimal>> siteFinalRate = siteCurrencyInfoApi.getAllFinalRateBySiteList(siteCodeList);

        Map<String, GameInfoPO> gameInfoPOMap = getGameInfoByVenueCode(venueInfoVO.getVenueCode());
        int page = 1;
        int pageSize = 100;
        while (true) {
            JSONObject paramsMap = new JSONObject();
            paramsMap.put("operatorId", venueInfoVO.getMerchantNo());
            paramsMap.put("sign", sign(signMap));
            paramsMap.put("startTime", startTime);
            paramsMap.put("endTime", endTime);
            paramsMap.put("current", page);
            paramsMap.put("size", pageSize);
            paramsMap.put("lang", "zh");
            String url = venueInfoVO.getApiUrl() + AceLtConstant.BET_ORDER;
            try {
                String result = HttpClientHandler.post(url, paramsMap.toString());
                if (result == null) {
                    log.info("{}:彩票拉单异常:返回空:{}", venueInfoVO.getVenueName(), paramsMap);
                    break;
                }

                AceLtRes<AceLtPageRes<AceLtBetRecord>> aceLtRes = JSON.parseObject(result, new TypeReference<AceLtRes<AceLtPageRes<AceLtBetRecord>>>() {
                });
//                AceLtPageRes<AceLtBetRecord> data = aceLtRes.getData();
                if (AceLtConstant.SUCC_CODE.equals(aceLtRes.getCode())) {
                    List<AceLtBetRecord> betRecords = aceLtRes.getData().getRecords();
                    if (CollectionUtils.isNotEmpty(betRecords)) {
                        Long count = aceLtRes.getData().getTotal();
                        List<List<AceLtBetRecord>> lists = ThirdPullBetPartitionComponent.partition(betRecords, count);
                        lists.forEach(list -> {
                            List<OrderRecordVO> addList = parseOrder(venueInfoVO, list, siteMap, siteFinalRate, gameInfoPOMap);
                            if (CollectionUtil.isNotEmpty(addList)) {
                                orderRecordProcessService.orderProcess(addList);
                                log.info("{} 本次拉取注单处理完成,task.size:{},v:{}", venueInfoVO.getVenueCode(), list.size(), addList.size());
                            }
                        });
                        page++;
                    } else {
                        log.info("{}:彩票无数据从{}到{}，退出 最近一次拉取订单", venueInfoVO.getVenueName(), DateUtils.convertDateToString(new Date(startTime)),
                                DateUtils.convertDateToString(new Date(endTime))
                        );
                        break;
                    }
                } else {
                    log.error("{}:彩票注单拉取异常:param:{},{}", venueInfoVO.getVenueName(), paramsMap, result);
                    break;
                }
            } catch (Exception e) {
                log.error("{}:彩票拉取注单发生错误!!", venueInfoVO.getVenueName(), e);
                break;
            }

//            if (!futureList.isEmpty()) {
//                List<Integer> list = futureList.stream().map(CompletableFuture::join).filter(CollUtil::isNotEmpty)
//                        .map(recordVOS ->
//                                CompletableFuture.supplyAsync(() ->
//                                        orderRecordProcessService.orderProcess(recordVOS), ltPullBetThreadPool))
//                        .map(CompletableFuture::join).toList();
//                log.info("{} 本次拉取注单处理完成,task.size:{},v:{}", venueInfoVO.getVenueCode(), list.size(), futureList.size());
//            }
        }

        return ResponseVO.success();
    }

    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, List<AceLtBetRecord> aceLtBetRecords, Map<String, SiteVO> siteMap,
                                          Map<String, Map<String, BigDecimal>> siteFinalRate, Map<String, GameInfoPO> gameInfoPOMap) {
        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        // 所有注单用户信息
        List<String> thirdUserName = aceLtBetRecords.stream().map(AceLtBetRecord::getOperatorAccount).distinct().toList();

        if (CollectionUtils.isEmpty(thirdUserName)) {
            log.info("{} 拉单异常,没有用户信息", venueInfoVO.getVenueCode());
            return orderRecordList;
        }


        List<CasinoMemberPO> casinoMemberPOS = casinoMemberService.list(Wrappers.<CasinoMemberPO>lambdaQuery()
                .eq(CasinoMemberPO::getVenueCode, venueInfoVO.getVenueCode())
                .in(CasinoMemberPO::getVenueUserAccount, thirdUserName));

        if (CollectionUtils.isEmpty(casinoMemberPOS)) {
            log.info("{} 拉单异常,casinoMember 没有记录:{}", venueInfoVO.getVenueCode(), thirdUserName);
            return orderRecordList;
        }

        Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberPOS.stream()
                .collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, e -> e));

        // 用户信息
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();

        if (CollectionUtils.isEmpty(userIds)) {
            log.info("{} 拉单异常,accountList", venueInfoVO.getVenueCode());
            return orderRecordList;
        }

        userIds = userIds.stream().distinct().toList();

        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);


        List<OrderRecordVO> list = new ArrayList<>();
        for (AceLtBetRecord betRecord : aceLtBetRecords) {
            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(betRecord.getOperatorAccount());
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), betRecord.getOperatorAccount());
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号不存在", venueInfoVO.getVenueCode());
                continue;
            }
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            OrderRecordVO recordVO = new OrderRecordVO();
            SiteVO siteVO = siteMap.get(userInfoVO.getSiteCode());
            if (siteVO != null && !StringUtils.isBlank(siteVO.getSiteName())) {
                recordVO.setSiteName(siteVO.getSiteName());
            }
            //同步最新赢奖金额
            setMaxAmount(venueInfoVO.getVenueCode(), betRecord, userInfoVO, siteFinalRate);
            recordVO.setSiteCode(userInfoVO.getSiteCode());
            recordVO.setUserAccount(userInfoVO.getUserAccount());
            recordVO.setUserId(userInfoVO.getUserId());
            recordVO.setUserName(userInfoVO.getUserName());
            recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            recordVO.setAgentId(userInfoVO.getSuperAgentId());
            recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
            recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
            recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
            recordVO.setBetAmount(betRecord.getBetMoneyTotal());
            recordVO.setBetIp(userLoginInfoVO.getIp());
            String betContent = betRecord.getGameTranslateCode();
            if (StringUtils.isBlank(betContent)) {
                betContent = betRecord.getNums();
            }
            recordVO.setBetContent(betContent);
            recordVO.setBetTime(betRecord.getBetTime());
            recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
            recordVO.setVenueCode(venueInfoVO.getVenueCode());
            recordVO.setCasinoUserName(casinoMemberVO.getVenueUserAccount());
            recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            recordVO.setCurrency(userInfoVO.getMainCurrency());
            recordVO.setGameCode(betRecord.getGameCode());
            recordVO.setThirdGameCode(betRecord.getGameCode());
            recordVO.setGameNo(betRecord.getIssueNo());
            recordVO.setOrderId(OrderUtil.getGameNo());
            recordVO.setPlayType(betRecord.getGamePlayCode());
            recordVO.setThirdOrderId(betRecord.getSerialNumber());
            recordVO.setTransactionId(betRecord.getSerialNumber());
            recordVO.setOdds(betRecord.getCurOdd().toPlainString());
            if (betRecord.getState() == AceLtBetResultStatusEnums.SETTLEMENT.getCode()) {
                recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
                recordVO.setSettleTime(betRecord.getSettleTime());
            } else if (betRecord.getState() == AceLtBetResultStatusEnums.SYS_CANCEL.getCode() ||
                    betRecord.getState() == AceLtBetResultStatusEnums.MANUAL_CANCEL.getCode() ||
                    betRecord.getState() == AceLtBetResultStatusEnums.SYSTEM_CANCELLED.getCode() ||
                    betRecord.getState() == AceLtBetResultStatusEnums.USER_CANCELLED.getCode() ||
                    betRecord.getState() == AceLtBetResultStatusEnums.INVALID.getCode()) {
                recordVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
            } else {
                recordVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
            }
            recordVO.setCreatedTime(System.currentTimeMillis());
            recordVO.setUpdatedTime(System.currentTimeMillis());
            recordVO.setVenueType(VenueTypeEnum.ACELT.getCode());
            if (Objects.equals(recordVO.getOrderStatus(), OrderStatusEnum.SETTLED.getCode())) {
                // 有效投注
                //BigDecimal validBetAmount = sportComputerValidBetAmount(betRecord.getBetMoneyTotal(),betRecord.getWinOrLossAmount());
                // 有输赢并结算的投注金额即为有效投注额
                //recordVO.setValidAmount(validBetAmount);
                recordVO.setWinLossAmount(betRecord.getWinOrLossAmount());
                recordVO.setValidAmount(computerValidBetAmount(recordVO.getBetAmount(), recordVO.getWinLossAmount(), VenueTypeEnum.ACELT));
                recordVO.setPayoutAmount(betRecord.getWinOrLossAmount().add(betRecord.getBetMoneyTotal()));
            }
//            recordVO.setChangeStatus(0);
            recordVO.setResultList(betRecord.getNumsName());
            recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
            recordVO.setReSettleTime(0L);
            recordVO.setParlayInfo(JSON.toJSONString(betRecord));

            String gameCode = betRecord.getGameCode();
            GameInfoPO gameInfoPO = gameInfoPOMap.get(gameCode);
            if (gameInfoPO != null) {
                recordVO.setGameId(gameInfoPO.getGameId());
                recordVO.setGameName(gameInfoPO.getGameI18nCode());
                recordVO.setRoomTypeName(gameInfoPO.getGameId());
            }
            recordVO.setPlayInfo(AceLtPlayTypeEnum.getNameByCode(betRecord.getGamePlayCode()));
            recordVO.setOrderInfo(betRecord.getNums());
//            }
            list.add(recordVO);
        }
        return list;
    }


    /**
     * 更新最新的最大赢奖金额
     *
     * @param aceLtBetRecord 注单
     * @param userInfoVO     站点
     */
    private void setMaxAmount(String venueCode, AceLtBetRecord aceLtBetRecord, UserInfoVO userInfoVO, Map<String, Map<String, BigDecimal>> siteFinalRate) {
        if (StringUtils.isBlank(userInfoVO.getSiteCode())) {
            log.info("更新最新的最大赢奖金额异常,未获取到用户站点:{}", userInfoVO);
            return;
        }

        if (StringUtils.isBlank(userInfoVO.getMainCurrency())) {
            log.info("更新最新的最大赢奖金额异常,未获取到用户币种:{}", userInfoVO);
            return;
        }
        //用户所在站点下的币种汇率
        Map<String, BigDecimal> fianlRateMap = siteFinalRate.get(userInfoVO.getSiteCode());
        if (CollectionUtil.isEmpty(fianlRateMap)) {
            log.info("更新最新的最大赢奖金额异常,未获取到用户下到汇率信息:{}", userInfoVO);
            return;
        }

        BigDecimal rate = fianlRateMap.get(userInfoVO.getMainCurrency());

        try {
            BigDecimal newWinAmount = aceLtBetRecord.getWinOrLossAmount();
            if (ObjectUtil.isNotEmpty(aceLtBetRecord.getGameCode()) && newWinAmount != null && newWinAmount.compareTo(BigDecimal.ZERO) > 0) {

                //转成平台币
                BigDecimal platNewWinAmount = AmountUtils.divide(newWinAmount, rate);
                if (platNewWinAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("更新最新的最大赢奖金额异常,用户金额转化成平台币异常:siteCode:{},currency:{},amount:{},newAmount:{}", userInfoVO.getSiteCode(), userInfoVO.getMainCurrency(), newWinAmount, platNewWinAmount);
                    return;
                }
                String typeCode = AceltGameInfoEnum.getGameTypeByGameCode(aceLtBetRecord.getGameCode());
                if (ObjectUtil.isNotEmpty(typeCode)) {
                    String key = String.format(RedisConstants.ACE_MAX_AMOUNT, venueCode, userInfoVO.getSiteCode(), aceLtBetRecord.getGameCode(), typeCode);
                    ACELTMaxAmount maxAmountObj = RedisUtil.getValue(key);
                    if (ObjectUtil.isEmpty(maxAmountObj)) {
                        RedisUtil.setValue(key, ACELTMaxAmount.builder().maxAmount(platNewWinAmount).build());
                    } else if (platNewWinAmount.compareTo(maxAmountObj.getMaxAmount()) > 0) {
                        RedisUtil.setValue(key, ACELTMaxAmount.builder().maxAmount(platNewWinAmount).build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("彩票同步最新赢奖金额异常,:{}", aceLtBetRecord, e);
        }
    }


    public ACELTBaseRes<ACELTGetBalanceRes> queryBalance(ACELTGetBalanceReq aceltGetBalanceReq, String venuePlatform) {
        //参数失败
        if (!aceltGetBalanceReq.valid()) {
            log.info("{}:参数校验失败", venuePlatform);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
        }

        VenueEnum venueEnum = getVenueCodeByMerchantNoAndVenuePlatform(aceltGetBalanceReq.getOperatorId(), venuePlatform);
        if (venueEnum == null) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        if (validateMD5Sign(aceltGetBalanceReq.getSign(), aceltGetBalanceReq.getOperatorAccount(), venueEnum)) {
            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        String userId = getVenueUserAccount(aceltGetBalanceReq.getOperatorAccount());
        if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }

        if (venueMaintainClosed(venueEnum.getVenueCode(), userInfoVO.getSiteCode())) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
        }

        BigDecimal checkBig = checkJoinActivity(userInfoVO.getUserId(), userInfoVO.getSiteCode(), venueEnum.getVenueCode());
        if (Objects.isNull(checkBig)) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            return ACELTBaseRes.success(ACELTGetBalanceRes.builder()
                    .balance(BigDecimal.ZERO)
                    .username(aceltGetBalanceReq.getOperatorAccount())
                    .build());
        }

        if (userCenterCoin.getTotalAmount() == null) {
            return ACELTBaseRes.success(ACELTGetBalanceRes.builder()
                    .balance(BigDecimal.ZERO)
                    .username(aceltGetBalanceReq.getOperatorAccount())
                    .build());
        }

        ACELTGetBalanceRes res = ACELTGetBalanceRes.builder()
                .balance(userCenterCoin.getTotalAmount())
                .username(aceltGetBalanceReq.getOperatorAccount())
                .build();
        setBase(res, aceltGetBalanceReq.getOperatorAccount(), venueEnum);
        return ACELTBaseRes.success(res);
    }

    @DistributedLock(name = RedisConstants.ACELT_COIN_LOCK, unique = "#req.transferReference", waitTime = 3, leaseTime = 180)
    public ACELTBaseRes<ACELTAccountCheangeRes> accountChange(ACELTAccountCheangeReq req, String venuePlatform) {
        if (!req.valid()) {
            log.info("{}:参数校验失败,req:{}", venuePlatform, req);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
        }

        VenueEnum venueEnum = getVenueCodeByMerchantNoAndVenuePlatform(req.getOperatorId(), venuePlatform);
        if (venueEnum == null) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        if (validateMD5Sign(req.getSign(), req.getOperatorAccount(), venueEnum)) {
            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        ACELTInOutTypeEnum inOutTypeEnum = ACELTInOutTypeEnum.fromCode(req.getInOut());
        if (inOutTypeEnum == null) {
            log.info("{}:参数校验失败,收支类型异常,:{}", venueEnum.getVenueName(), req.getInOut());
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
        }

        ACELTTransferTypeEnum transferTypeEnum = ACELTTransferTypeEnum.fromCode(req.getTransferType());
        if (transferTypeEnum == null) {
            log.info("{}:参数校验失败,交易类型代码异常,:{}", venueEnum.getVenueName(), req.getInOut());
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
        }

        ACELTCurrencyEnum aceltCurrencyEnum = ACELTCurrencyEnum.getCurrencyEnum(req.getCurrencyType());
        if (aceltCurrencyEnum == null) {
            log.info("{}:币种映射有误", venueEnum.getVenueName());
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
        }

        String userId = getVenueUserAccount(req.getOperatorAccount());
        if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }
        if (venueMaintainClosed(venueEnum.getVenueCode(), userInfoVO.getSiteCode())) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
        }

        if (!userInfoVO.getMainCurrency().equals(aceltCurrencyEnum.getPlatformCurrencyCode())) {
            log.info("{}:币种与用户的法币,不一致:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INSUFFICIENT_BALANCE);
        }

        BigDecimal tradeAmount = req.getTotalAmount();//账变金额
        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额
        if (inOutTypeEnum.getCode().equals(ACELTInOutTypeEnum.OUT.getCode())) {
            if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}:用户钱包,余额不足:{},用户金额:{},扣款金额:{}", venueEnum.getVenueName(), userId, userTotalAmount, tradeAmount);
                throw new ACELTDefaultException(ACELTResultCodeEnums.INSUFFICIENT_BALANCE);
            }
        }

        String balanceType = inOutTypeEnum.getCode().equals(ACELTInOutTypeEnum.IN.getCode()) ? CoinBalanceTypeEnum.INCOME.getCode() :
                CoinBalanceTypeEnum.EXPENSES.getCode();


        for (ACELTDetailReq item : req.getDetail()) {
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(item.getBetNum());
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(balanceType);
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(item.getTradeAmount());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            toSetUserCoinAddVO(userCoinAddVO, req.getTransferType());
            CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

            if (ObjectUtil.isEmpty(recordResultVO)) {
                log.info("{}:调用扣费失败,userCoinAddVO:{}", venueEnum.getVenueName(), userCoinAddVO);
                throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
            }

            if (!recordResultVO.getResult()) {
                UpdateBalanceStatusEnums resultStatus = recordResultVO.getResultStatus();
                //重复订单不做拒绝
                if (resultStatus.getCode().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS.getCode())) {
                    continue;
                }
                throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
            }
        }
        UserCoinWalletVO centerCoin = getUserCenterCoin(userId);

        ACELTAccountCheangeRes res = ACELTAccountCheangeRes
                .builder()
                .afterAmount(centerCoin.getTotalAmount())
                .beforeAmount(userTotalAmount)
                .tradeAmount(tradeAmount)
                .currencyCode(userInfoVO.getMainCurrency())
                .inOut(req.getInOut())
                .transferReference(req.getTransferReference())
                .username(req.getOperatorAccount())
                .build();
        setBase(res, req.getOperatorAccount(), venueEnum);


        return ACELTBaseRes.success(res);
    }


    /**
     * 账变类型转换
     *
     * @param userCoinAddVO 对象
     * @param transferType  类型
     */
    private void toSetUserCoinAddVO(UserCoinAddVO userCoinAddVO, Integer transferType) {
        if (ACELTTransferTypeEnum.PT.getCode().equals(transferType) || ACELTTransferTypeEnum.ZH.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        } else if (ACELTTransferTypeEnum.JJ.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        } else if (ACELTTransferTypeEnum.HJ.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        } else if (ACELTTransferTypeEnum.GR.getCode().equals(transferType) || ACELTTransferTypeEnum.XT.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        }
    }


    public ACELTBaseRes<ACELTAccountCheangeRes> accountChangeCallBack(ACELTAccountChangeCallBackReq req, String venuePlatform) {
        if (!req.valid()) {
            log.info("{}:参数校验失败,req:{}", venuePlatform, req);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
        }
        VenueEnum venueEnum = getVenueCodeByMerchantNoAndVenuePlatform(req.getOperatorId(), venuePlatform);
        if (venueEnum == null) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        if (validateMD5Sign(req.getSign(), req.getOperatorId(), venueEnum)) {
            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
        }

        String userId = getVenueUserAccount(req.getOperatorId());
        if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", venueEnum.getVenueName(), userId);
            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
        }
        if (venueMaintainClosed(venueEnum.getVenueCode(), userInfoVO.getSiteCode())) {
            log.info("{}:场馆未开启", venueEnum.getVenueName());
            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
        }
        UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
        userCoinRecordRequestVO.setUserId(userInfoVO.getUserId());
        userCoinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        userCoinRecordRequestVO.setOrderNo(req.getTransferReference());
        List<UserCoinRecordVO> userCoinRecord = getUserCoinRecords(userCoinRecordRequestVO);
        if (CollectionUtil.isEmpty(userCoinRecord)) {
            throw new ACELTDefaultException(ACELTResultCodeEnums.DATA_NOT_EXIST);
        }
        UserCoinRecordVO userCoinRecordVO = userCoinRecord.get(0);
        String balanceType = userCoinRecordVO.getBalanceType();
        Integer inOut = null;
        if (balanceType.equals(CoinBalanceTypeEnum.INCOME.getCode())) {
            inOut = ACELTInOutTypeEnum.IN.getCode();
        } else if (balanceType.equals(CoinBalanceTypeEnum.EXPENSES.getCode())) {
            inOut = ACELTInOutTypeEnum.OUT.getCode();
        } else {
            //彩票的订单 只会出现 两种,收入与支出,如果是第三种状态则需要排查
            log.info("{}:查询账变记录异常,收支状态异常:{}", venueEnum.getVenueName(), userCoinRecordVO);
            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
        }
        return ACELTBaseRes.success(ACELTAccountCheangeRes.builder()
                .username(req.getOperatorId())
                .inOut(inOut)
                .currencyCode(ACELTCurrencyEnum.getPlatCurrencyEnum(userInfoVO.getMainCurrency()).getCurrencyCode())
                .tradeAmount(userCoinRecordVO.getCoinValue())
                .afterAmount(userCoinRecordVO.getCoinTo())
                .beforeAmount(userCoinRecordVO.getCoinFrom())
                .username(req.getOperatorId())
                .transferReference(req.getTransferReference())
                .build());
    }


    private void setBase(ACELTBaseReq baseReq, String userName, VenueEnum venueEnum) {
        VenueInfoVO venueConfigInfo = getVenueInfo(venueEnum.getVenueCode(), "");
        Map<String, Object> signMap = new HashMap<>();
        signMap.put("operatorAccount", userName);
        signMap.put("secretKey", venueConfigInfo.getMerchantKey());
        signMap.put("operatorId", venueConfigInfo.getMerchantNo());
        baseReq.setSign(sign(signMap));
        baseReq.setTimestamp(System.currentTimeMillis());
        baseReq.setOperatorId(venueConfigInfo.getMerchantNo());
    }

    private boolean validateMD5Sign(String reqMD5Sign, String userName, VenueEnum venueEnum) {
        VenueInfoVO venueConfigInfo = getVenueInfo(venueEnum.getVenueCode()
                , "");
        Map<String, Object> signMap = new HashMap<>();
        signMap.put("operatorAccount", userName);
        signMap.put("secretKey", venueConfigInfo.getMerchantKey());
        signMap.put("operatorId", venueConfigInfo.getMerchantNo());
        return !reqMD5Sign.equals(sign(signMap));
    }


    private static String sign(Map<String, Object> paramMap) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue() + "");
        }
        StringBuilder sb = new StringBuilder();
        //生成
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            String k = entry.getKey();
            if (!"sign".equals(k)) {
                sb.append(k).append("=").append(entry.getValue()).append("&");
            }
        }
        return MD5Util.md5(sb.toString());
    }


}
