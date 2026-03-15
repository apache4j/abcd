package com.cloud.baowang.play.game.tf.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.I18nInitConstant;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.tf.TfTransferTypeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.tf.*;
import com.cloud.baowang.play.api.vo.tf.TfOrderRespVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.TFPlayTypeEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.order.client.EventOrderClientResVO;
import com.cloud.baowang.play.api.vo.order.client.OrderMultipleBetVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientRespVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordVenueClientReqVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.mapper.OrderRecordEsMapper;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.OrderRecordPO;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.TF)
public class TFGameServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;

    private final OrderRecordEsMapper orderRecordEsMapper;

    private final static String ORDER_INFO = "%s %s@%s";

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

//    public static void main(String[] args) {
//        String url = "https://v4-staging.r4espt.com/api/v4/launch/url/?auth=f3547a86176148a7847b6ffdd9babf5e";
//        try {
//            String response = HttpClientHandler.get(url, null);
//            System.out.println(response);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String casinoPassword = casinoMemberVO.getCasinoPassword();
        String url = venueDetailVO.getGameUrl() + "/api/v4/launch/url/?auth=" + venueDetailVO.getMerchantKey();
        try {
            String response = HttpClientHandler.get(url, null, null);
            LoginRespVO loginRespVO = JSONObject.parseObject(response, LoginRespVO.class);
            String launchUrl = loginRespVO.getLaunchUrl();
            String loginUrl = String.format("%s?auth=%s&token=%s&lang=%s", launchUrl, venueDetailVO.getMerchantKey(), casinoPassword,switchLang(loginVO.getLanguageCode()));
            GameLoginVo gameLoginVo = GameLoginVo.builder()
                    .source(loginUrl)
                    .type(GameLoginTypeEnums.URL.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.TF.getVenueCode())
                    .build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("TF-登录游戏 用户名【{}】 登录游戏发生错误!!", loginVO.getUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);

    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueDetailVO.getVenueCode());
        int page = 1;
        int pageSize = 200;
        try {
            while (true) {
                Map<String, String> params = Maps.newHashMap();
                Long startTime = venuePullParamVO.getStartTime();
                String fromDate = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(startTime / 1000, TimeZoneUtils.UTC0.toZoneId(), TimeZoneUtils.patten_yMdTHmsZ);
                params.put("from_modified_datetime", fromDate);
                Long endTime = venuePullParamVO.getEndTime();
                String endDate = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(endTime / 1000, TimeZoneUtils.UTC0.toZoneId(), TimeZoneUtils.patten_yMdTHmsZ);
                params.put("to_modified_datetime", endDate);
                params.put("page", String.valueOf(page));
                params.put("page_size", String.valueOf(pageSize));
                params.put("lang","zh");
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Authorization", "Token " + venueDetailVO.getBetKey());
                String url = venueDetailVO.getApiUrl() + "/api/v2/bet-transaction";
                log.info("TF拉单参数, url:{},参数：{}", url, params);
                String response = HttpClientHandler.get(url, headers, params);
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject == null || jsonObject.get("code") != null) {
                    log.error("tf 拉取注返回异常，返回：{}", response);
                    return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
                }
                TfOrderRespVO tfOrderRespVO = JSONObject.parseObject(response, TfOrderRespVO.class);
                if (tfOrderRespVO.getCount() == 0) {
                    break;
                }
                log.info("TF 拉取注单 返回:{}", tfOrderRespVO);
                List<TfOrderInfoVO> betData = tfOrderRespVO.getResults();
                // 场馆用户关联信息
                List<String> usernames = betData.stream().map(TfOrderInfoVO::getMemberCode).distinct().toList();
                Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueDetailVO.getVenuePlatform());
                if (MapUtil.isEmpty(casinoMemberMap)) {
                    log.info("TF未找到三方关联信息 玩家列表{}", usernames);
                    break;
                }
                // 用户信息
                List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
                Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
                if (CollUtil.isEmpty(userMap)) {
                    log.info("TF游戏用户账号不存在{}", userIds);
                    break;
                }
                // 用户登录信息
                Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

                List<OrderRecordVO> list = Lists.newArrayList();
                Map<String, String> siteNameMap = getSiteNameMap();
                for (TfOrderInfoVO orderResponseVO : betData) {
                    CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getMemberCode());
                    if (casinoMemberVO == null) {
                        log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), orderResponseVO.getMemberCode());
                        continue;
                    }
                    UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                    if (userInfoVO == null) {
                        log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
                        continue;
                    }

                    UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                    // 映射原始注单
                    OrderRecordVO recordVO = parseRecords(venueDetailVO, orderResponseVO, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap);
                    recordVO.setVenueType(VenueEnum.TF.getType().getCode());
                    list.add(recordVO);
                }
                // 订单处理
                if (CollectionUtil.isNotEmpty(list)) {
                    orderRecordProcessService.orderProcess(list);
                }
                list.clear();
                if (jsonObject.get("next") == null) {
                    break;
                }
                page++;
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("tf 执行拉单异常", e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }

    public static Long convertUtcToTimestamp(String utcDateTime) {
        try{
            if(StringUtils.isEmpty(utcDateTime)){
                return null;
            }
            // 解析时间字符串为OffsetDateTime（假设输入是UTC时间）
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(utcDateTime, formatter);

            // 转换为Instant（UTC时区）
            Instant instant = offsetDateTime.toInstant();

            // 返回时间戳（秒级）
            return instant.toEpochMilli();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, TfOrderInfoVO orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getAmount());

        recordVO.setBetContent(orderResponseVO.getBetSelection());
        String betTimeStr = orderResponseVO.getDateCreated();
        Long betTime = convertUtcToTimestamp(betTimeStr);
        recordVO.setBetTime(betTime);
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getMemberCode());
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(orderResponseVO.getId());
        recordVO.setTransactionId(orderResponseVO.getId());

        recordVO.setWinLossAmount(orderResponseVO.getMemberWinLossF());
        // BigDecimal payoutAmount= (Objects.isNull(orderResponseVO.getEarnings())?BigDecimal.ZERO:orderResponseVO.getEarnings()).add(orderResponseVO.getAmount());

        BigDecimal memberWinLossF = Objects.nonNull(orderResponseVO.getMemberWinLossF())?orderResponseVO.getMemberWinLossF():BigDecimal.ZERO;
        BigDecimal betAmount = Objects.nonNull(recordVO.getBetAmount())?recordVO.getBetAmount():BigDecimal.ZERO;
        BigDecimal payoutAmount = memberWinLossF.add(betAmount);

        recordVO.setPayoutAmount(payoutAmount);

        recordVO.setOdds(orderResponseVO.getOdds().toString());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        String settlementDateTimeStr = orderResponseVO.getSettlementDateTime();
        Long settleTime = convertUtcToTimestamp(settlementDateTimeStr);
        recordVO.setSettleTime(settleTime);
        recordVO.setOrderStatus(getLocalOrderStatus(orderResponseVO.getSettlementStatus(), orderResponseVO.isUnsettled()));
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(getChangeStatus(orderResponseVO.isUnsettled()));
        recordVO.setPlayType(orderResponseVO.getBetTypeName());
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setLeagueName(orderResponseVO.getCompetitionName());
        recordVO.setGameName(orderResponseVO.getGameTypeName());
        //recordVO.setEventInfo(orderResponseVO.getEventName());
        recordVO.setEventInfo(orderResponseVO.getCompetitionName());
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setGameId(String.valueOf(orderResponseVO.getGameTypeId()));
        recordVO.setThirdGameCode(String.valueOf(orderResponseVO.getGameTypeId()));
        recordVO.setVipRank(userInfoVO.getVipRank());
        recordVO.setRoomType(orderResponseVO.isCombo()?"1":"0");
        recordVO.setRoomTypeName(orderResponseVO.isCombo()?"串关":"单关");
        TFPlayTypeEnum tfPlayTypeEnum = TFPlayTypeEnum.nameOfCode(orderResponseVO.getBetTypeName());
        if (tfPlayTypeEnum != null) {
            recordVO.setPlayInfo(tfPlayTypeEnum.getName());
        }
        recordVO.setGameNo(orderResponseVO.getEventId().toString());
        BigDecimal validBetAmount = computerValidBetAmount(recordVO.getBetAmount(), recordVO.getWinLossAmount(), VenueTypeEnum.ELECTRONIC_SPORTS);
        recordVO.setValidAmount(validBetAmount);
        if (recordVO.getOrderStatus().equals(OrderStatusEnum.RESETTLED.getCode())) {
            String modifiedDateTimeStr = orderResponseVO.getModifiedDateTime();
            Long modifiedDateTime = convertUtcToTimestamp(modifiedDateTimeStr);
            recordVO.setSettleTime(modifiedDateTime);

            if(StringUtils.isNotEmpty(orderResponseVO.getResultStatus()) && orderResponseVO.getResultStatus().equalsIgnoreCase("CANCELLED")){
                recordVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
                recordVO.setWinLossAmount(new BigDecimal(0));
                recordVO.setPayoutAmount(orderResponseVO.getAmount());
                recordVO.setValidAmount(new BigDecimal(0));
            }
        }
        //recordVO.setEventInfo(orderResponseVO.getCompetitionName());
        //recordVO.setTeamInfo(orderResponseVO.getEventName());
        // 返水
//        if (!rebateMap.isEmpty() && (recordVO.getOrderClassify().equals(ClassifyEnum.SETTLED.getCode()) ||
//                recordVO.getOrderClassify().equals(ClassifyEnum.RESETTLED.getCode()))) {
//            Map<String, BigDecimal> resultRate = ThirdUtil.getRebate(userInfoVO, recordVO, rebateMap, validBetAmount);
//            recordVO.setRebateRate(resultRate.get("rebate"));
//            recordVO.setRebateAmount(resultRate.get("scale"));
//        }
//        recordVO.setPlayInfo(GameEnum.GameTypeEnum.nameOfId(orderResponseVO.getGameTypeId()));
        recordVO.setOrderInfo(String.format(ORDER_INFO, recordVO.getPlayInfo(), orderResponseVO.getBetSelection(), orderResponseVO.getMemberOdds()));
        if (orderResponseVO.isCombo()) {
            log.info("TF 当前注单是串关: {}", orderResponseVO);
            List<TfOrderInfoVO> tickets = orderResponseVO.getTickets();
            TfOrderInfoVO first = tickets.get(0);
            recordVO.setGameId(String.valueOf(first.getGameTypeId()));
            recordVO.setGameName(first.getGameTypeName());
            recordVO.setThirdGameCode(String.valueOf(first.getGameTypeId()));
            TFPlayTypeEnum firstPlayType = TFPlayTypeEnum.nameOfCode(orderResponseVO.getBetTypeName());
            if (firstPlayType != null) {
                recordVO.setPlayInfo(firstPlayType.getName());
            }
            recordVO.setOrderId(OrderUtil.getGameNo());
            recordVO.setOrderInfo(String.format(ORDER_INFO, recordVO.getPlayInfo(), first.getBetSelection(), recordVO.getOdds()));
            recordVO.setOrderInfo(first.getBetTypeName());
            // 如果有串关，拼接所有的游戏名称
            String collect = tickets.stream().map(TfOrderInfoVO::getCompetitionName).collect(Collectors.joining(","));
            recordVO.setEventInfo(collect);
            String gameNos = tickets.stream().map(TfOrderInfoVO::getEventId)
                    .map(String::valueOf).collect(Collectors.joining(","));
            recordVO.setGameNo(gameNos);
        }
        return recordVO;
    }

    private Integer getChangeStatus(Boolean isUnsettle) {
        return isUnsettle ? ChangeStatusEnum.CHANGED.getCode() : ChangeStatusEnum.NOT_CHANGE.getCode();
    }

    private Integer getLocalOrderStatus(String settleStatus, Boolean isUnsettle) {
        if (settleStatus == null) {
            return OrderStatusEnum.NOT_SETTLE.getCode();
        }
        if (isUnsettle) {
            return OrderStatusEnum.RESETTLED.getCode();
        }
        switch (settleStatus) {
            case "settled", "SETTLED":
                return OrderStatusEnum.SETTLED.getCode();
            case "cancelled", "CANCELLED":
                return OrderStatusEnum.CANCEL.getCode();
            default:
                return OrderStatusEnum.NOT_SETTLE.getCode();
        }
    }

    private BigDecimal getValidAmount(TfOrderInfoVO orderResponseVO) {

        return null;
    }

    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount){
        Map<String, String> params = Maps.newHashMap();
        params.put("member", venueUserAccount);
        params.put("operator_id", venueDetailVO.getMerchantNo());
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", "Token " + venueDetailVO.getBetKey());
        String url = venueDetailVO.getGameUrl() + "/api/v2/partner-account-logout/" ;
        try {
            log.info("TF 踢线请求 api:{},param:{},headers:{}",url,JSONObject.toJSONString(params),headers);
            String response = HttpClientHandler.post(url, headers, JSONObject.toJSONString(params));
            log.info("TF 踢线请求 response:{}",response);
        } catch (Exception e) {
            log.error("TF-踢线请求 用户名【{}】 踢线请求发生错误!!", venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }

    private final static String COMBO_EVENT_INFO = "%s";

    @Override
    public ResponseVO<OrderRecordClientRespVO> orderClientQuery(OrderRecordVenueClientReqVO resVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        OrderRecordClientRespVO ret = new OrderRecordClientRespVO();
        int page = resVO.getPageNumber();
        int pageSize = resVO.getPageSize();
        try {
            Map<String, String> params = Maps.newHashMap();
            Long startTime = resVO.getBetStartTime();
            String fromDate = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(startTime / 1000, TimeZoneUtils.UTC0.toZoneId(), TimeZoneUtils.patten_yMdTHmsZ);
            params.put("from_datetime", fromDate);
            Long endTime = resVO.getBetEndTime();
            String endDate = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(endTime / 1000, TimeZoneUtils.UTC0.toZoneId(), TimeZoneUtils.patten_yMdTHmsZ);
            params.put("to_datetime", endDate);
            params.put("page", String.valueOf(page));
            params.put("page_size", String.valueOf(pageSize));
            params.put("lang", switchLang(resVO.getLang()));
            params.put("LoginName", casinoMemberVO.getVenueUserAccount());
            settleStatus(resVO, params);
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Authorization", "Token " + venueDetailVO.getBetKey());
            String url = venueDetailVO.getApiUrl() + "/api/v2/bet-transaction";
            String response = HttpClientHandler.get(url, headers, params);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject == null || (jsonObject.get("code") != null && jsonObject.getIntValue("code") != 1)) {
                log.error("tf 实时注单查询返回异常，返回：{}", response);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            TfOrderRespVO tfOrderRespVO = JSONObject.parseObject(response, TfOrderRespVO.class);
            log.info("TF 实时注单查询 返回:{}", tfOrderRespVO);
            if (tfOrderRespVO.getCount() == 0) {
                return ResponseVO.success(ret);
            }
            List<TfOrderInfoVO> betData = tfOrderRespVO.getResults();
            Page<EventOrderClientResVO> pageRes = new Page<>();
            pageRes.setTotal(tfOrderRespVO.getCount());
            List<EventOrderClientResVO> eventOrder = Lists.newArrayList();


            List<String> orderIdList = betData.stream().map(TfOrderInfoVO::getId).toList();
            LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
            wrapper.in(OrderRecordPO::getThirdOrderId,orderIdList);
            List<OrderRecordPO> orderList = orderRecordEsMapper.selectList(wrapper);
            Map<String, OrderRecordPO> orderMap = orderList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, Function.identity()));


            for (TfOrderInfoVO orderVO : betData) {
                EventOrderClientResVO cliOrderVO = new EventOrderClientResVO();

                //有效投注
                OrderRecordPO orderRecordPO = orderMap.get(orderVO.getId());
                if(orderRecordPO != null){
                    cliOrderVO.setOrderId(orderRecordPO.getOrderId());
                    cliOrderVO.setValidAmount(orderRecordPO.getValidAmount());
                }
                cliOrderVO.setVenueCode(VenueEnum.TF.getVenueCode());
                cliOrderVO.setEventInfo(orderVO.getCompetitionName());
                cliOrderVO.setTeamInfo(orderVO.getEventName());
                cliOrderVO.setBetAmount(orderVO.getAmount());
                cliOrderVO.setOdds(ObjectUtil.isNotEmpty(orderVO.getMemberOdds()) ? orderVO.getMemberOdds().toString() : null);
                cliOrderVO.setBetContent(orderVO.getBetSelection());
                cliOrderVO.setWinLossAmount(orderVO.getEarnings());
                String betTimeStr = orderVO.getDateCreated();
                Long betTime = TimeZoneUtils.parseDate4TimeZoneToTime(betTimeStr, TimeZoneUtils.patten_yMdTHmsSSSSSSZ, TimeZoneUtils.UTC0);
                cliOrderVO.setBetTime(betTime);
                cliOrderVO.setMultipleBet(false);
                cliOrderVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(getLocalOrderStatus(orderVO.getSettlementStatus(), orderVO.isUnsettled())));
                if (orderVO.isCombo()) {
                    cliOrderVO.setEventInfo(I18nMessageUtil.getI18NMessageInAdvice(I18nInitConstant.BIZ_ORDER_MUTIL_DESC));
                    cliOrderVO.setMultipleBet(true);
                    List<TfOrderInfoVO> tickets = orderVO.getTickets();
                    List<OrderMultipleBetVO> comboVOs = Lists.newArrayList();
                    for (TfOrderInfoVO comboInfo : tickets) {
                        OrderMultipleBetVO orderMultipleBetVO = new OrderMultipleBetVO();
                        transInfo(comboInfo, orderMultipleBetVO);
                        comboVOs.add(orderMultipleBetVO);
                    }
                    cliOrderVO.setOrderMultipleBetList(comboVOs);
                }
                eventOrder.add(cliOrderVO);
            }
            eventOrder = eventOrder.stream().sorted(Comparator.comparing(EventOrderClientResVO::getBetTime).reversed()).toList();
            pageRes.setRecords(eventOrder);
            ret.setEventOrderPage(pageRes);
            return ResponseVO.success(ret);
        } catch (Exception e) {
            log.error("TF 实时注单查询异常 ", e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }

    private void transInfo(TfOrderInfoVO comboInfo, OrderMultipleBetVO orderMultiBetVO) {
        orderMultiBetVO.setBetAmount(comboInfo.getAmount());
        orderMultiBetVO.setBetContent(comboInfo.getBetSelection());
        orderMultiBetVO.setEventInfo(comboInfo.getCompetitionName());
        orderMultiBetVO.setOdds(ObjUtil.isNotNull(comboInfo.getMemberOdds()) ? comboInfo.getMemberOdds().toString() : null);
        orderMultiBetVO.setTeamInfo(comboInfo.getEventName());
        orderMultiBetVO.setWinLossAmount(comboInfo.getEarnings());
        orderMultiBetVO.setOrderClassify(getLocalOrderStatus(comboInfo.getSettlementStatus(), comboInfo.isUnsettled()));
        orderMultiBetVO.setWinlossStatus(getWinLossStatus(comboInfo));
    }

    private Integer getWinLossStatus(TfOrderInfoVO comboInfo) {
        String resultStatus = comboInfo.getResultStatus();
        if (resultStatus == null) {
            return null;
        } else if (resultStatus.equalsIgnoreCase("win")) {
            return 1;
        } else if (resultStatus.equalsIgnoreCase("draw")) {
            return 0;
        } else if (resultStatus.equalsIgnoreCase("loss")) {
            return -1;
        }
        return null;
    }

    private void settleStatus(OrderRecordVenueClientReqVO resVO, Map<String, String> params) {
        if (CollectionUtil.isNotEmpty(resVO.getOrderClassifyList())) {
            // 仅支持 结算与未结算筛选
            Integer i = resVO.getOrderClassifyList().get(0);
            ClassifyEnum classifyEnum = ClassifyEnum.nameOfCode(i);
            switch (classifyEnum) {
                case SETTLED -> {
                    params.put("settlement_status", "settled");
                }
                case NOT_SETTLE -> {
                    params.put("settlement_status", "confirmed");
                }
            }
        }
    }

    private String switchLang(String lang) {
        return LANGUAGE_MAP.get(lang);
    }

    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    //en = english 英文
    //zh = chinese 中文
    //th = thai 泰文
    //vn = viet 越南文
    //id = indonesia bahasa 印尼语
    //ms = malay 马来文
    //jp = japanese 日文
    //kr = korea 韩文
    //es = spanish 西班牙文
    //ru = russia 俄语
    //pt = portuguese  葡萄牙语
    static {
        LANGUAGE_MAP.put("zh-CN", "zh");
        LANGUAGE_MAP.put("en-US", "en");
        LANGUAGE_MAP.put("hi-IN", "en");//TF不支持 北印度语-印度
        LANGUAGE_MAP.put("id-ID", "id");
        LANGUAGE_MAP.put("pt-BR", "pt");
        LANGUAGE_MAP.put("zh-TW", "zh");
        LANGUAGE_MAP.put("vi-VN", "vn");

        LANGUAGE_MAP.put(LanguageEnum.JA_JP.getLang(), "jp");
        LANGUAGE_MAP.put(LanguageEnum.KO_KR.getLang(), "kr");
        LANGUAGE_MAP.put(LanguageEnum.MS_MY.getLang(), "ms");
        LANGUAGE_MAP.put(LanguageEnum.PT_BR.getLang(), "pt");
        LANGUAGE_MAP.put(LanguageEnum.RU_RU.getLang(), "ru");
        LANGUAGE_MAP.put(LanguageEnum.ES_ES.getLang(), "es");
        LANGUAGE_MAP.put(LanguageEnum.TH_TH.getLang(), "th");
        LANGUAGE_MAP.put(LanguageEnum.ID_ID.getLang(), "id");

    }



    public TfValidResp validate(TfValidReq req) {
        String token = req.getToken();
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setCasinoPassword(token);
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        TfValidResp resp = new TfValidResp();
        if (casinoMember == null) {
            log.info("tf valid 用户信息不存在，token:{}", req.getToken());
            return resp;
        }
        resp.setLoginName(casinoMember.getVenueUserAccount());
        return resp;
    }

    public TfWalletResp wallet(String loginName) {

        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(loginName);
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        TfWalletResp resp = new TfWalletResp();
        if (casinoMember == null) {
            log.error("tf 三方余额查询失败，查询用户{}", loginName);
//            response.setStatus(400);
            return resp;
        }

        if (venueMaintainClosed(VenuePlatformConstants.TF,casinoMember.getSiteCode())){
            log.info("{}: wallet 场馆未开启", VenueEnum.TF.getVenueName());
//            response.setStatus(400);
            return  new TfWalletResp();
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} wallet userInfoVO不存在, bet, 参数:{} ", VenueEnum.TF.getVenueCode(), loginName);
//            response.setStatus(400);
            return  new TfWalletResp();
        }

        if (userGameLock(userInfoVO)) {
            log.error("{} wallet user game lock, user 参数:{} ", VenueEnum.TF.getVenueCode(), userInfoVO);
//            response.setStatus(400);
            return  new TfWalletResp();
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }
        resp.setBalance(balance);
        return resp;
    }

    public TfTransferResp transfer(TfTransferReq req) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
        casinoMemberReqVO.setVenueUserAccount(req.getLoginName());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (venueMaintainClosed(VenuePlatformConstants.TF,casinoMember.getSiteCode())){
            log.info("{}:transfer 场馆未开启", VenueEnum.TF.getVenueName());
//            response.setStatus(400);
            return  new TfTransferResp();
        }
        TfTransferTypeEnums type = getTransferType(req);
        if (type == null) {
//            response.setStatus(400);
            return new TfTransferResp();
        }
        ResponseVO<Boolean> resp = switch (type) {
            case BET -> bet(req);
            case CANCELLED -> cancelled(req);
            case PAYOUT -> payout(req);
            case LOSS -> ResponseVO.success(true);
            case RESETTLEMENT -> resettle(req);
        };
        if (!resp.isOk()) {
//            response.setStatus(400);
        }
        return new TfTransferResp();
    }

    private ResponseVO<Boolean> cancelled(TfTransferReq req) {
        BigDecimal amount = req.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        String loginName = req.getLoginName();
        String orderNo = req.getTicketDetail().getId();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.TF, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("tf cancel error get locker error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
            casinoMemberReqVO.setVenueUserAccount(loginName);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                log.error("tf cancel error casinoMember error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("tf cancel error userInfoVO error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.error("tf cancel error wallet error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isEmpty(userCoinBetRecRespVO)) {
                log.error("tf cancel error userRecord error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(amount.abs());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);

            // 账变
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancel(userInfoVO, orderNo, amount);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            return switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);

            };
        } catch (Exception e) {
            log.error("tf cancel error system error, req:{}", req);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    public TfTransferResp rollback(TfTransferReq req) {
        BigDecimal amount = req.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        String loginName = req.getLoginName();
        String orderNo = req.getTicketNum();
        TfTransferResp resp = new TfTransferResp();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.TF, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("tf rollback error get locker error, req:{}", req);
//                response.setStatus(400);
                return resp;
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
            casinoMemberReqVO.setVenueUserAccount(loginName);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
//                response.setStatus(400);
                return resp;
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("tf rollback error queryUserInfoByAccount userName[{}] not find.", userId);
//                response.setStatus(400);
                return resp;
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.error("tf rollback error wallet is not exist user account:{}.", userId);
//                response.setStatus(400);
                return resp;
            }
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(orderNo);
            coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isEmpty(userCoinBetRecRespVO)) {
                log.error("tf rollback bet error feign error or order not exist feign resp{}.", userCoinBetRecRespVO);
//                response.setStatus(400);
                return resp;
            }

            // 账变
            CoinRecordResultVO coinRecordResultVO = this.updateBalanceCancelBet(userInfoVO, orderNo, amount, req.getTicketNum());
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ResponseVO<Boolean> balanceResp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);

            };
            if (!balanceResp.isOk()) {
//                response.setStatus(400);
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error("tf rollback error ", e);
//            response.setStatus(400);
            return resp;

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.TF);
        userCoinAddVO.setThirdOrderNo(remark);

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);

    }

    private ResponseVO<Boolean> resettle(TfTransferReq req) {
        BigDecimal amount = req.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        String loginName = req.getLoginName();
        String orderNo = req.getTicketNum();
        String ticketNum = req.getTicketNum();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.TF, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("tf resettle error get locker error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
            casinoMemberReqVO.setVenueUserAccount(loginName);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("tf resettle error queryUserInfoByAccount userName[{}] not find.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.error("tf resettle error wallet is not exist user account:{}.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            //NOTE 验证重复提交
            TfOrderInfoVO ticketDetail = req.getTicketDetail();

            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinBetRecRespVO)) {
                if (userCoinBetRecRespVO.stream().anyMatch(vo -> ticketNum.equals(vo.getRemark()))){
                    log.error("tf resettle but operation has been done, resp{}.", userCoinBetRecRespVO);
                    return ResponseVO.success(true);
                }
                if (userCoinBetRecRespVO.stream().noneMatch(vo -> CoinBalanceTypeEnum.INCOME.getCode().equals(vo.getBalanceType()))){
                    log.error("tf resettle but payout operation no exist, resp{}.", userCoinBetRecRespVO);
                    return ResponseVO.success(true);
                }
            }

            // 账变
            CoinRecordResultVO coinRecordResultVO = updateBalanceResettle(userInfoVO, orderNo, amount, ticketNum);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ResponseVO<Boolean> resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);

            };
            if (!resp.isOk()) {
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error("tf resettle error ", e);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }



    private ResponseVO<Boolean> payout(TfTransferReq req) {
        BigDecimal amount = req.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        String loginName = req.getLoginName();
        String orderNo = req.getTicketDetail().getId();
        String ticketNum = req.getTicketNum();

        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.TF, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("tf paytout error get locker error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
            casinoMemberReqVO.setVenueUserAccount(loginName);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("tf paytout error queryUserInfoByAccount userName[{}] not find.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin)) {
                log.error("tf paytout error wallet is not exist user account:{}.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            UserCoinAddVO userCoinAddVO = updateBalancePayout(userInfoVO, orderNo, amount, ticketNum);

            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> userCoinBetRecRespVO = getUserCoinRecords(coinRecordRequestVO);

            if (CollectionUtil.isEmpty(userCoinBetRecRespVO)) {
                log.error("tf payout but bet operation no found, resp{}.", userCoinBetRecRespVO);
                return ResponseVO.success(true);
            }else {
                if (userCoinBetRecRespVO.stream().anyMatch(vo -> ticketNum.equals(vo.getRemark()))){
                    log.error("tf payout but operation has been done, resp{}.", userCoinBetRecRespVO);
                    return ResponseVO.success(true);
                }
                if (userCoinBetRecRespVO.size()>1){
                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                }
            }
            // 账变
            UpdateBalanceStatusEnums resultStatus = toUserCoinHandle(userCoinAddVO).getResultStatus();
            ResponseVO<Boolean> resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> ResponseVO.fail(ResultCode.PARAM_ERROR);

            };
            if (!resp.isOk()) {
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error("tf paytout error ", e);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    public UserCoinAddVO updateBalancePayout(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.TF);
        userCoinAddVO.setThirdOrderNo(remark);
        //修改余额 记录账变
        return userCoinAddVO;
    }

    /**
     * 重结算, 逻辑,
     * NOTE 正数, 就给玩家减钱
     * NOTE 负数, 就给玩家加钱
     */
    protected CoinRecordResultVO updateBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        if (amount.compareTo(BigDecimal.ZERO)>0){
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        }else {
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        }
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.TF);
        userCoinAddVO.setThirdOrderNo(remark);

        userCoinAddVO.setRemark(remark);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);

    }

    private ResponseVO<Boolean> bet(TfTransferReq req) {
        BigDecimal amount = req.getAmount();
        String loginName = req.getLoginName();
        String orderNo = req.getTicketNum();

        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.TF, orderNo));
        try {
            if (!rLock.tryLock()) {
                log.error("tf bet error get locker error, req:{}", req);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.TF);
            casinoMemberReqVO.setVenueUserAccount(loginName);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (casinoMember == null) {
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            String userId = casinoMember.getUserId();
            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("tf bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            if (userGameLock(userInfoVO)) {
                log.error("tf bet error userName[{}] game lock.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            if (venueMaintainClosed(VenuePlatformConstants.TF,userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.TF.getVenueCode());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(amount.abs()) < 0) {
                log.error("tf bet error wallet is not exist user account:{}.", userId);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(orderNo);
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(amount.abs());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

            userCoinAddVO.setVenueCode(VenueEnum.TF.getVenueCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
            userCoinAddVO.setThirdOrderNo(orderNo);
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
            // 账变
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            ResponseVO<Boolean> resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO,REPEAT_TRANSACTIONS -> ResponseVO.success(true);
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    yield ResponseVO.fail(ResultCode.PARAM_ERROR);
                }

            };
            if (!resp.isOk()) {
                return resp;
            }
            return resp;
        } catch (Exception e) {
            log.error("tf bet error ", e);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);

        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }



    private TfTransferTypeEnums getTransferType(TfTransferReq req) {
        Boolean betFlag = req.getPlaceBet();
        if (betFlag != null && betFlag) {
            return TfTransferTypeEnums.BET;
        }

        if (TfTransferTypeEnums.PAYOUT.getDescription().equals(req.getDescription())) {
            String resultStatus = req.getTicketDetail().getResultStatus();
            if (resultStatus != null && resultStatus.equals(TfTransferTypeEnums.CANCELLED.getDescription())){
                return TfTransferTypeEnums.CANCELLED;
            }
            return TfTransferTypeEnums.PAYOUT;
        } else if (TfTransferTypeEnums.LOSS.getDescription().equals(req.getDescription())) {
            return TfTransferTypeEnums.LOSS;
        } else if (TfTransferTypeEnums.RESETTLEMENT.getDescription().equals(req.getDescription())) {
            return TfTransferTypeEnums.RESETTLEMENT;
        }

        return null;
    }
}
