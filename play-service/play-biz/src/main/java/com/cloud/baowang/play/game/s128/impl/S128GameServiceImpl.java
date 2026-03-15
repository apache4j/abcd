package com.cloud.baowang.play.game.s128.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.s128.S128BetErrorCodeEnum;
import com.cloud.baowang.play.api.enums.s128.S128GetBalanceErrorCodeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.s128.*;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.s128.vo.RecordDataRespVO;
import com.cloud.baowang.play.game.s128.vo.S128RecordOriginDataVO;
import com.cloud.baowang.play.game.s128.vo.SessionDataRespVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.mq.UserGamePayoutMqVO;
import com.cloud.baowang.wallet.api.vo.mq.UserGamePayoutVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.S128)
public class S128GameServiceImpl extends GameBaseService implements GameService {
    private final static String ERROR_CODE_SUCCESS = "00";
    private final OrderRecordService orderRecordService;
    private final OrderRecordProcessService orderRecordProcessService;

    private final VenueInfoService venueInfoService;



    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    public static void main(String[] args) {
//        Map<String, String> param = new HashMap<>();
//        param.put("name", "test_test");
//        param.put("login_id", "test_test");
//        param.put("agent_code", "Z4Z10000");
//        param.put("api_key", "9AB7B34637A9473FAA3BD8077B708A27");
//
//        String url = "https://api8745.cfb2.net/" + "get_session_id.aspx";
//
//        String response = null;
//        try {
//            response = HttpClientHandler.post(url, param);
//            System.out.println(response);
//            XmlMapper xmlMapper = new XmlMapper();
//            SessionDataRespVO sessionDataRespVO = xmlMapper.readValue(response, SessionDataRespVO.class);
//
//            if (!sessionDataRespVO.getStatusCode().equals(ERROR_CODE_SUCCESS)) {
//
//
//            }
//            String sessionId = sessionDataRespVO.getSessionId();
//            System.out.println(sessionId);

//            String url1 = "https://kss.cfb2.net/" + "api/auth_login.aspx";
//            Map<String, String> param1 = new HashMap<>();
//            param1.put("session_id", sessionId);
//            param1.put("lang", "en-US");
//            param1.put("login_id", "test_test");
//            response = HttpClientHandler.post(url1, param1);
//            System.out.println(response);
//
//            String url2 = "https://digmaantest.cm3645.com/" + "api/cash/auth";
//            Map<String, String> param2 = new HashMap<>();
//            param1.put("session_id", sessionId);
//            param1.put("lang", "en-US");
//            param1.put("login_id", "test_test");
//            response = HttpClientHandler.post(url2, param2);
//            System.out.println(response);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Long startTime = (1720531800000L) / 1000;
        String startTimeStr = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(startTime, TimeZoneUtils.shanghaiZoneId, TimeZoneUtils.patten_yyyyMMddHHmm);
        Long endTime = (1720532940000L) / 1000;
        String endTimeStr = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(endTime, TimeZoneUtils.shanghaiZoneId, TimeZoneUtils.patten_yyyyMMddHHmm);

        Map<String, String> param = new HashMap<>();
        param.put("api_key", "9AB7B34637A9473FAA3BD8077B708A27");
        param.put("agent_code", "Z4Z10000");
        param.put("start_datetime", startTimeStr);
        param.put("end_datetime", endTimeStr);

        String url = "https://api8745.cfb2.net/" + "get_cockfight_processed_ticket_2.aspx";

        String response = null;
        try {
            response = HttpClientHandler.post(url, param);
            XmlMapper xmlMapper = new XmlMapper();
            RecordDataRespVO sessionDataRespVO = xmlMapper.readValue(response, RecordDataRespVO.class);
            String data = sessionDataRespVO.getData();
            List<S128RecordOriginDataVO> originRecList = toOriginRecList(data);
            System.out.println(originRecList);

        } catch (Exception e) {

        }

    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        // getSessionId
        Map<String, String> param = new HashMap<>();
        param.put("name", casinoMemberVO.getVenueUserAccount());
        param.put("login_id", casinoMemberVO.getVenueUserAccount());
        param.put("agent_code", venueDetailVO.getMerchantNo());
        param.put("api_key", venueDetailVO.getMerchantKey());

        String url = venueDetailVO.getApiUrl() + "/get_session_id.aspx";
        try {
            String response = HttpClientHandler.post(url, param);
            if (StringUtils.isEmpty(response)) {
                log.error("{}-进入游戏异常,当前用户:{}", venueDetailVO.getVenueCode(), casinoMemberVO.getVenueUserAccount());
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            XmlMapper xmlMapper = new XmlMapper();
            SessionDataRespVO sessionDataRespVO = xmlMapper.readValue(response, SessionDataRespVO.class);
            if (!sessionDataRespVO.getStatusCode().equals(ERROR_CODE_SUCCESS)) {
                log.error("{}-进入游戏异常,当前用户:{}, 异常信息：{}", venueDetailVO.getVenueCode(), casinoMemberVO.getVenueUserAccount(), response);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            GameLoginVo gameLoginVo = GameLoginVo.builder()
                    .source(sessionDataRespVO.getSessionId())
                    .venueCode(VenueEnum.S128.getVenueCode())
                    .type(GameLoginTypeEnums.TOKEN.getType())
                    .userAccount(casinoMemberVO.getVenueUserAccount())
                    .build();

            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("{}-进入游戏异常【{}】", venueDetailVO.getVenueCode(), casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @Override
    public ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        if (CollectionUtil.isEmpty(orderRecordMqVOList)) {
            return ResponseVO.success();
        }
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenuePlatformConstants.S128);

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
            log.info("S128 游戏用户账号不存在{}", userIds);
            return null;
        }
        List<String> orderIdList = Optional.of(orderRecordMqVOList).orElse(Lists.newArrayList()).stream()
                .map(OrderRecordMqVO::getOrderId).collect(Collectors.toList());
        List<OrderRecordPO> orderRecordPOList = orderRecordService.list(Wrappers.<OrderRecordPO>lambdaQuery()
                .select(OrderRecordPO::getOrderId, OrderRecordPO::getBetAmount, OrderRecordPO::getBetTime)
                .in(OrderRecordPO::getOrderId, orderIdList));
        Map<String, OrderRecordPO> orderRecordPOMap = orderRecordPOList.stream().collect(Collectors.toMap(OrderRecordPO::getOrderId, e -> e));

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);

        Map<String, String> siteNameMap = getSiteNameMap();
        Integer venueType = venueInfoService.getVenueTypeByCode(VenueEnum.S128.getVenueCode());
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

            OrderRecordPO orderRecordPO = orderRecordPOMap.get(orderRecordMqVO.getOrderId());
            if (orderRecordPO != null) {
                orderRecord.setBetAmount(orderRecordPO.getBetAmount());
                orderRecord.setBetTime(orderRecordPO.getBetTime());
            }
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            orderRecord.setUserAccount(userInfoVO.getUserAccount());
            orderRecord.setUserName(userInfoVO.getUserName());
            orderRecord.setUserId(userInfoVO.getUserId());
            orderRecord.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecord.setAgentId(userInfoVO.getSuperAgentId());
            orderRecord.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecord.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecord.setVenueType(venueType);
            orderRecord.setBetIp(userLoginInfoVO.getIp());
            orderRecord.setSiteCode(userInfoVO.getSiteCode());
            orderRecord.setSiteName(siteNameMap.get(userInfoVO.getSiteCode()));
            orderRecord.setCurrency(userInfoVO.getMainCurrency());
            orderRecord.setChangeTime(0L);
            orderRecord.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecord.setVipRank(userInfoVO.getVipRank());
            orderRecord.setCurrency(userInfoVO.getMainCurrency());
            orderRecord.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                orderRecord.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
            BigDecimal validBetAmount = computerValidBetAmount(orderRecord.getBetAmount(), orderRecord.getWinLossAmount(), VenueTypeEnum.COCKFIGHTING);
            orderRecord.setValidAmount(validBetAmount);
            orderRecord.setOrderInfo(orderRecord.getBetContent());
            orderRecord.setGameName("斗鸡");
            String gameCode = orderRecord.getGameCode();
            orderRecord.setOrderId(OrderUtil.getGameNo());
            GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
            if (gameInfoPO != null) {
                orderRecord.setGameId(gameInfoPO.getGameId());
                orderRecord.setGameName(gameInfoPO.getGameI18nCode());
            }
            orderRecord.setOrderId(OrderUtil.getGameNo());
            orderRecordVOS.add(orderRecord);
        }
        orderRecordProcessService.orderProcess(orderRecordVOS);

        return ResponseVO.success(true);
    }
    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount){
        // getSessionId
        Map<String, String> param = new HashMap<>();
        param.put("login_id", venueUserAccount);
        param.put("agent_code", venueDetailVO.getMerchantNo());
        param.put("api_key", venueDetailVO.getMerchantKey());

        String url = venueDetailVO.getApiUrl() + "/kickout_player.aspx";
        try {
            String response = HttpClientHandler.post(url, param);
            if (StringUtils.isEmpty(response)) {
                log.error("{}-用户踢出异常,当前用户:{}", venueDetailVO.getVenueCode(), venueUserAccount);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            XmlMapper xmlMapper = new XmlMapper();
            SessionDataRespVO sessionDataRespVO = xmlMapper.readValue(response, SessionDataRespVO.class);
            if (!sessionDataRespVO.getStatusCode().equals(ERROR_CODE_SUCCESS)) {
                log.error("{}-用户踢出异常,当前用户:{}, 异常信息：{}", venueDetailVO.getVenueCode(), venueUserAccount, response);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
        } catch (Exception e) {
            log.error("{}-用户踢出异常【{}】", venueDetailVO.getVenueCode(), venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        Map<String, String> param = new HashMap<>();
        param.put("api_key", venueDetailVO.getMerchantKey());
        param.put("agent_code", venueDetailVO.getMerchantNo());
        Long startTime = venuePullParamVO.getStartTime();
        String startTimeStr = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(startTime / 1000, TimeZoneUtils.shanghaiZoneId, TimeZoneUtils.patten_yyyyMMddHHmmss);
        Long endTime = venuePullParamVO.getEndTime();
        String endTimeStr = TimeZoneUtils.convertSecondsToLocalDateTimeOfPatten(endTime / 1000, TimeZoneUtils.shanghaiZoneId, TimeZoneUtils.patten_yyyyMMddHHmmss);
        param.put("start_datetime", startTimeStr);
        param.put("end_datetime", endTimeStr);
        String url = venueDetailVO.getApiUrl() + "/get_cockfight_processed_ticket_2.aspx";

        try {
            log.info("{}-拉取注单请求参数:{}", venueDetailVO.getVenueCode(), param);
            String response = HttpClientHandler.post(url, param);
            log.info("{}-拉取注单结果:{}", venueDetailVO.getVenueCode(), response);
            if (StringUtils.isEmpty(response)) {
                log.error("{}-拉取注单异常, 拉取参数:{}", venueDetailVO.getVenueCode(), venuePullParamVO);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            XmlMapper xmlMapper = new XmlMapper();
            RecordDataRespVO sessionDataRespVO = xmlMapper.readValue(response, RecordDataRespVO.class);
            if (!sessionDataRespVO.getStatusCode().equals(ERROR_CODE_SUCCESS)) {
                log.error("{}-拉取注单异常, 异常信息：{}", venueDetailVO.getVenueCode(), response);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            Integer totalRecords = sessionDataRespVO.getTotalRecords();
            if (totalRecords.equals(0)) {
                return ResponseVO.success();
            }


            String originRecStr = sessionDataRespVO.getData();
            List<S128RecordOriginDataVO> originRecList = toOriginRecList(originRecStr);
            // 注单解析
            // 场馆用户关联信息
            List<String> usernames = originRecList.stream().map(S128RecordOriginDataVO::getLoginId).distinct().toList();
            Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueDetailVO.getVenuePlatform());
            if (MapUtil.isEmpty(casinoMemberMap)) {
                log.error("S128未找到三方关联信息 玩家列表{}", usernames);
                return ResponseVO.success();
            }
            // 用户信息
            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
            Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
            if (CollUtil.isEmpty(userMap)) {
                log.error("S128游戏用户账号不存在{}", userIds);
                return ResponseVO.success();
            }
            // 用户登录信息
            Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

            Map<String, String> siteNameMap = getSiteNameMap();

            Integer venueType = venueInfoService.getVenueTypeByCode(VenueEnum.S128.getVenueCode());
            List<OrderRecordVO> orderRecordVOList = Lists.newArrayList();
            for (S128RecordOriginDataVO recordOriginDataVO : originRecList) {

                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(recordOriginDataVO.getLoginId());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), recordOriginDataVO.getLoginId());
                    continue;
                }
                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                if (userInfoVO == null) {
                    log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
                    continue;
                }
                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                OrderRecordVO recordVO = new OrderRecordVO();
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());

                recordVO.setBetAmount(recordOriginDataVO.getStake());

                recordVO.setBetContent(recordOriginDataVO.getBetOn());
                Long betTime = TimeZoneUtils.parseDate4TimeZoneToTime(recordOriginDataVO.getCreatedDatetime(), TimeZoneUtils.patten_yyyyMMddHHmmss, TimeZoneUtils.ShangHaiTimeZone);
                recordVO.setBetTime(betTime);
                recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
                recordVO.setVenueCode(venueDetailVO.getVenueCode());
                recordVO.setCasinoUserName(recordOriginDataVO.getLoginId());
                recordVO.setVenueType(venueType);
                if (userLoginInfoVO.getLoginTerminal() != null) {
                    recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
                }
                recordVO.setBetIp(userLoginInfoVO.getIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setGameName("斗鸡");
                recordVO.setThirdOrderId(recordOriginDataVO.getTicketId());
                recordVO.setTransactionId(recordOriginDataVO.getTicketId());

                BigDecimal validAmount =  super.computerValidBetAmount(recordVO.getBetAmount(),recordOriginDataVO.getWinloss(),VenueTypeEnum.COCKFIGHTING);

                recordVO.setValidAmount(validAmount);
                recordVO.setWinLossAmount(recordOriginDataVO.getWinloss());
                recordVO.setPayoutAmount(recordOriginDataVO.getPayout());
                recordVO.setOdds(recordOriginDataVO.getOddsGiven());
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(recordOriginDataVO.getProcessedDatetime(), TimeZoneUtils.patten_yyyyMMddHHmmss, TimeZoneUtils.ShangHaiTimeZone);
                recordVO.setSettleTime(settleTime);
                recordVO.setOrderStatus(getStatus(recordOriginDataVO.getStatus()));
                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());

                recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
                recordVO.setReSettleTime(0L);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setParlayInfo(JSON.toJSONString(recordOriginDataVO));
                recordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());
                recordVO.setGameNo(recordOriginDataVO.getArenaCode());
                recordVO.setEventInfo(recordOriginDataVO.getArenaNameCn());
                recordVO.setPlayType(recordOriginDataVO.getMatchType());
                recordVO.setPlayInfo(recordOriginDataVO.getMeronCock() + " VS " + recordOriginDataVO.getWalaCock());
                recordVO.setResultList(recordOriginDataVO.getFightResult());
                recordVO.setHomeName(recordOriginDataVO.getMeronCock());
                recordVO.setAwayName(recordOriginDataVO.getWalaCock());
                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setVipRank(userInfoVO.getVipRank());
                recordVO.setOrderInfo(recordOriginDataVO.getBetOn());
                recordVO.setEventInfo(recordOriginDataVO.getArenaCode());
                recordVO.setOrderId(OrderUtil.getGameNo());
                orderRecordVOList.add(recordVO);
            }
            // 结算
            List<UserGamePayoutVO> userGamePayoutVOS = BeanUtil.copyToList(orderRecordVOList, UserGamePayoutVO.class);
            List<List<UserGamePayoutVO>> listList = CollectionUtil.split(userGamePayoutVOS, 100);
            for (List<UserGamePayoutVO> gamePayoutVOS : listList) {
                UserGamePayoutMqVO userGamePayoutVOMq = new UserGamePayoutMqVO();
                userGamePayoutVOMq.setUserRecordPayoutVOList(gamePayoutVOS);
                KafkaUtil.send(TopicsConstants.THIRD_GAME_PAYOUT_TOPIC, userGamePayoutVOMq);
            }
            // 注单处理
            orderRecordProcessService.orderProcess(orderRecordVOList);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("{}-注单处理异常", venueDetailVO.getVenueCode(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    private Integer getStatus(String status) {
        Integer ret = OrderStatusEnum.NOT_SETTLE.getCode();
        if (status == null) {
            return null;
        }
        switch (status) {
            case "WIN", "LOSE","REFUND":
                ret = OrderStatusEnum.SETTLED.getCode();
                break;
           /* case "REFUND":
                ret = OrderStatusEnum.RESETTLED.getCode();
                break;*/
            case "CANCEL":
                ret = OrderStatusEnum.CANCEL.getCode();
                break;
            case "VOID":
                break;
        }
        return ret;
    }

    /**
     * result str to list
     *
     * @param originRecStr
     * @return
     */
    private static List<S128RecordOriginDataVO> toOriginRecList(String originRecStr) {
        List<S128RecordOriginDataVO> ret = Lists.newArrayList();
        String[] recRows = originRecStr.split("\\|");
        for (int i = 0; i < recRows.length; i++) {
            S128RecordOriginDataVO recOriginVO = new S128RecordOriginDataVO();
            String recStr = recRows[i];
            String[] columns = recStr.split(",");

            recOriginVO.setTicketId(columns[0]);
            recOriginVO.setLoginId(columns[1]);
            recOriginVO.setArenaCode(columns[2]);
            recOriginVO.setArenaNameCn(columns[3]);
            recOriginVO.setMatchNo(columns[4]);
            recOriginVO.setMatchType(columns[5]);
            recOriginVO.setMatchDate(columns[6]);
            recOriginVO.setFightNo(Integer.valueOf(columns[7]));
            recOriginVO.setFightDatetime(columns[8]);
            recOriginVO.setMeronCock(columns[9]);
            recOriginVO.setMeronCockCn(columns[10]);
            recOriginVO.setWalaCock(columns[11]);
            recOriginVO.setWalaCockCn(columns[12]);
            recOriginVO.setBetOn(columns[13]);
            recOriginVO.setOddsType(columns[14]);
            recOriginVO.setOddsAsked(columns[15]);
            recOriginVO.setOddsGiven(columns[16]);
            recOriginVO.setStake(new BigDecimal(columns[17]));
            recOriginVO.setStakeMoney(new BigDecimal(columns[18]));
            recOriginVO.setBalanceOpen(new BigDecimal(columns[19]));
            recOriginVO.setBalanceClose(new BigDecimal(columns[20]));
            recOriginVO.setCreatedDatetime(columns[21]);
            recOriginVO.setFightResult(columns[22]);
            recOriginVO.setStatus(columns[23]);
            recOriginVO.setWinloss(new BigDecimal(columns[24]));
            recOriginVO.setCommEarned(new BigDecimal(columns[25]));
            recOriginVO.setPayout(new BigDecimal(columns[26]));
            recOriginVO.setBalanceOpen1(new BigDecimal(columns[27]));
            recOriginVO.setBalanceClose1(new BigDecimal(columns[28]));
            recOriginVO.setProcessedDatetime(columns[29]);
            recOriginVO.setTaxMoney(new BigDecimal(columns[30]));
            ret.add(recOriginVO);
        }
        return ret;
    }




    public GetBalanceRes getBalance(GetBalanceReq req) {
        String agentCode = req.getAgentCode();
        if (StringUtils.isEmpty(agentCode)) {
            log.error("s128 getBalance error: agent_code is empty, req: {}", req);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "agent_code is empty");
        }
        String apiKey = req.getApiKey();
        if (StringUtils.isEmpty(apiKey)) {
            log.error("s128 getBalance error: api_key is empty, req: {}", req);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "api_key is empty");
        }
        String loginId = req.getLoginId();
        if (StringUtils.isEmpty(loginId)) {
            log.error("s128 getBalance error: login_id is empty, req: {}", req);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "login_id is empty");
        }
        CasinoMemberReq memberReq = new CasinoMemberReq();
        memberReq.setVenueUserAccount(loginId);
        memberReq.setVenueCode(VenuePlatformConstants.S128);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(memberReq);
        if(casinoMember == null || casinoMember.getUserId()==null){
            log.error("s128 getBalance error: casinoMember not exist req: {}", req);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.LOGIN_NOT_FOUND, null);
        }
        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        //NOTE 查询场馆状态
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueCodeConstants.S128)
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = responseVO.getData();
//        if (Objects.isNull(venueInfoVO)|| !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//            log.info("getBalance 斗鸡场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "system error");
//        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(apiKey,VenuePlatformConstants.S128);
        if(venueInfoVO == null){
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "system error");
        }

        if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            log.error("s128 getBalance error: current merchant closed  req: {}", req);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "game lock");
        }
        if (userGameLock(userInfoVO)) {
            log.error("s128 bet error  userInfoVO[{}] game lock.", userInfoVO);
            return GetBalanceRes.fail(S128GetBalanceErrorCodeEnum.OTHER_ERR, "game lock");
        }

        return GetBalanceRes.success(getUserCenterCoin(casinoMember.getUserId()).getTotalAmount());
    }

    public BetRes bet(BetReq req) {

        if (req.valid()) {
            log.error("s128 bet error: param is empty, req: {}", req);
            return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "param error");
        }
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.S128, req.getTicketId()));
        try {
            if (!rLock.tryLock()) {
                log.error("s128 bet error get locker error, req:{}", req);
                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "repeat request");
            }
            String loginId = req.getLoginId();
            String agentCode = req.getAgentCode();
            String apiKey = req.getApiKey();

            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(loginId);
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.S128);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (Objects.isNull(casinoMember) || StrUtil.isEmpty(casinoMember.getUserId())) {
                log.error("s128 bet error: casinoMember not exist req: {}", req);
                return BetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());

            String userId = casinoMember.getUserId();
            if (Objects.isNull(userInfoVO)) {
                log.error("s128 bet error queryUserInfoByAccount userName[{}] not find.", userId);
                return BetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
            }
            if (userGameLock(userInfoVO)) {
                log.error("s128 bet error  userName[{}] game lock.", userId);
                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "game lock");
            }
            //NOTE 查询场馆状态
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.S128)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = responseVO.getData();
//            if (!responseVO.isOk()|| venueInfoVO==null
//                    || !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//                log.info("斗鸡场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
//            }


            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(apiKey,VenuePlatformConstants.S128);
            if(venueInfoVO == null){
                log.info("斗鸡场馆未开启, 场馆参数，商户号：{}，商户Key：{}", agentCode,apiKey);
                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
            }

            if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.S128.getVenueCode());
                return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "game lock");
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin) || userCenterCoin.getTotalAmount().compareTo(req.getStakeMoney()) < 0) {
                log.error("s128 bet error wallet is not exist user account:{}.", userId);
                return BetRes.fail(S128BetErrorCodeEnum.INSUFFICIENT_FUND, null);
            }
            BigDecimal transferAmount = req.getStakeMoney();
            String txid = req.getTicketId();
            CoinRecordResultVO coinRecordResultVO = updateBalanceBet(userInfoVO, txid, transferAmount,null,VenuePlatformConstants.S128);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            BetRes resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO -> BetRes.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL ->
                        BetRes.fail(S128BetErrorCodeEnum.INSUFFICIENT_FUND, null);
                case REPEAT_TRANSACTIONS -> BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "err: repeat_transaction");
            };

            UserCoinWalletVO afterCoin = getUserCenterCoin(userId);
            resp.setBalance(afterCoin.getTotalAmount());
            resp.setRefId(txid);
            if (!resp.isOk()) {
                return resp;
            }

            // 注单发送

            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setBetTime(TimeZoneUtils.parseDate4TimeZoneToTime(req.getCreatedDatetime(),TimeZoneUtils.patten_yyyyMMddHHmmss,TimeZoneUtils.ShangHaiTimeZone));

            orderRecordMqVO.setOrderId(casinoMember.getVenueCode() + txid);
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setThirdOrderId(txid);
            orderRecordMqVO.setGameName(req.getArenaCode());
            //非结算增加gameNo的显示.
            orderRecordMqVO.setGameNo(req.getArenaCode());
            orderRecordMqVO.setGameCode(req.getArenaCode());
            orderRecordMqVO.setBetContent(req.getBetOn());
            orderRecordMqVO.setBetAmount(BigDecimal.valueOf(req.getStake()));
            orderRecordMqVO.setOrderClassify(ClassifyEnum.NOT_SETTLE.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
            //屏蔽商户账号密钥
            req.setAgentCode(null);
            req.setApiKey(null);
            orderRecordMqVO.setParlayInfo(JSONObject.toJSONString(req));
            orderRecordMqVO.setEventInfo(req.getMatchNo());
            orderRecordMqVO.setOdds(req.getOddsGiven().toPlainString());
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("s128 bet error ", e);
            return BetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet error");
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }


    public CancelBetRes cancelBet(CancelBetReq req) {

        if (req.valid()) {
            log.error("s128 cancelBet error: param is empty, req: {}", req);
            return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "param error");
        }
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.S128, req.getTicketId()));
        try {
            if (!rLock.tryLock()) {
                log.error("s128 cancelBet error get locker error, req:{}", req);
                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "repeat request");
            }
            String loginId = req.getLoginId();
            String agentCode = req.getAgentCode();
            String apiKey = req.getApiKey();
            CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
            casinoMemberReqVO.setVenueUserAccount(loginId);
            casinoMemberReqVO.setVenueCode(VenuePlatformConstants.S128);
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
            if (Objects.isNull(casinoMember)|| StrUtil.isEmpty(casinoMember.getUserId())) {
                log.error("s128 cancelBet error: casinoMember not exist req: {}", req);
                return CancelBetRes.fail(S128BetErrorCodeEnum.LOGIN_NOT_FOUND, null);
            }


            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            //NOTE 查询场馆状态
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueCodeConstants.S128)
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = responseVO.getData();
//            if (!responseVO.isOk()|| venueInfoVO==null
//                    || !venueInfoVO.getMerchantNo().equals(agentCode) || !venueInfoVO.getMerchantKey().equals(apiKey)) {
//                log.info("斗鸡取消投注, 场馆未开启, 场馆参数，商户号：{}，商户Key：{}，返回结果： {}", agentCode,apiKey, responseVO);
//                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
//            }

            VenueInfoVO venueInfoVO = getVenueInfoByMerchantKey(apiKey,VenuePlatformConstants.S128);
            if(venueInfoVO == null){
                log.info("斗鸡取消投注, 场馆未开启, 场馆参数，商户号：{}，商户Key：{}", agentCode,apiKey);
                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "system error");
            }



            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setOrderNo(req.getTicketId());
            coinRecordRequestVO.setUserAccount(casinoMember.getUserAccount());
            List<UserCoinRecordVO> betCoinRecords = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isEmpty(betCoinRecords)) {
                log.error("s128 cancel bet error feign error or order not exist. feign resp{}.", betCoinRecords);
                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet record not exist");
            }

            coinRecordRequestVO.setOrderNo(req.getTicketId());
            coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
            List<UserCoinRecordVO> userCoinRecRespVO = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(userCoinRecRespVO)) {
                log.error("s128 cancel bet error feign error or already settled.feign resp{}.", userCoinRecRespVO);
                return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "bet record already settled");
            }

            UserCoinRecordVO userCoinRecordVO = betCoinRecords.get(0);
            BigDecimal transferAmount = userCoinRecordVO.getCoinValue();
            String txid = req.getTicketId();

            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(txid);
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            // 账变类型
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(transferAmount.abs());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVO.setVenueCode(VenuePlatformConstants.S128);
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);

//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBetCancel(userInfoVO, txid, transferAmount,null);
            UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
            CancelBetRes resp = switch (resultStatus) {
                case SUCCESS, AMOUNT_LESS_ZERO -> CancelBetRes.success();
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                    yield CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "canceled failed");
                }
                case REPEAT_TRANSACTIONS -> {
                    yield CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "already canceled");
                }
            };

            if (!resp.isOk()) {
                return resp;
            }
            // 注单发送
            OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(req, orderRecordMqVO);
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setVenueCode(casinoMember.getVenueCode());
            orderRecordMqVO.setWinLossAmount(BigDecimal.ZERO);
            orderRecordMqVO.setOrderId(OrderUtil.getGameNo());
            orderRecordMqVO.setCasinoUserName(casinoMember.getVenueUserAccount());
            orderRecordMqVO.setThirdOrderId(req.getTicketId());
            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);
            return resp;
        } catch (Exception e) {
            log.error("s128 cancel bet error ", e);
            return CancelBetRes.fail(S128BetErrorCodeEnum.OTHER_ERR, "cancel bet error");
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }


}
