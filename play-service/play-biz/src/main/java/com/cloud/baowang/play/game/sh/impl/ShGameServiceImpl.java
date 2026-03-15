package com.cloud.baowang.play.game.sh.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;
import com.cloud.baowang.play.api.enums.sh.ShTransferTypeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.exception.SHDefaultException;
import com.cloud.baowang.play.api.vo.base.ShBaseRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.sh.*;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.game.sh.enums.*;
import com.cloud.baowang.play.game.sh.request.ShApiLoginReq;
import com.cloud.baowang.play.game.sh.request.ShReqBase;
import com.cloud.baowang.play.game.sh.response.ShDeskStatusListResVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.*;
import com.cloud.baowang.play.util.HashUtil;
import com.cloud.baowang.play.util.SecurityUtil;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.sh.OrderResponseVO;
import com.cloud.baowang.play.vo.sh.Page;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.SH)
@AllArgsConstructor
public class ShGameServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;

    private final ApplicationContext applicationContext;

    private final SiteApi siteApi;

    private final VenueInfoService venueInfoService;

    private final OrderRecordService orderRecordService;

    /**
     * 成功
     **/
    private final static int SUCCESS_CODE = 0;
    /**
     * 用户已存在
     **/
    private final static int ALREADY_EXISTS = 20002;
    /**
     * 游戏房间维护中
     **/
    private final static int GAME_ROOM_MAINTAIN = 30006;

//    @Override
//    public ResponseVO<Boolean> createMember(VenueDetailVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
//        String casinoUserName = casinoMemberVO.getVenueUserAccount();
//        Long timeStamp = System.currentTimeMillis();
//        JSONObject requestParameters = new JSONObject();
//        requestParameters.put("userName", casinoUserName);
//        requestParameters.put("nickName", casinoUserName);
//        requestParameters.put("password", casinoMemberVO.getCasinoPassword());
//        requestParameters.put("loginIp", "127.0.0.1");
//        requestParameters.put("merchantNo", venueDetailVO.getMerchantNo());
//        requestParameters.put("timeStamp", timeStamp);
//        requestParameters.put("version", "1.0");
//        requestParameters.put("currency", "USD");
//        String hashSign = HashUtil.sha256(venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey());
//        requestParameters.put("hashSign", hashSign);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("userName", casinoUserName);
//        hashMap.put("merchantNo", venueDetailVO.getMerchantNo());
//        hashMap.put("apiSign", venueDetailVO.getAesKey());
//        requestParameters.put("md5Sign", SecurityUtil.paramSigns(hashMap));
//        String url = venueDetailVO.getApiUrl() + "/game/api/createMember";
//        log.info("SH-创建会员request apiPath={}, request={}", url, requestParameters);
//        try{
//            String response = HttpClientHandler.post(url, requestParameters.toString());
//            log.info("SH-创建会员response={}", response);
//            if (StringUtils.isBlank(response)) {
//                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//            }
//            ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
//            if (SUCCESS_CODE == responseVO.getCode() || ALREADY_EXISTS == responseVO.getCode()) {
//                if (!Objects.isNull(responseVO.getData())) {
//                    JSONObject result = JSONObject.parseObject(responseVO.getData().toString());
//                    casinoMemberVO.setVenueUserId(result.getString("userId"));
//                    return ResponseVO.success(true);
//                }
//            }
//        }catch (Exception e){
//            log.error("SH 用户名【{}】 创建会员发生错误!!", casinoMemberVO.getVenueUserAccount(), e);
//        }
//        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//    }


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        venueDetailVO.setMerchantNo(venueDetailVO.getMerchantNo());
        venueDetailVO.setAesKey(venueDetailVO.getAesKey());
        long timeStamp = System.currentTimeMillis();

        SHCurrencyEnum shCurrencyEnum = SHCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());
        if (shCurrencyEnum == null) {
            log.info("视讯登陆游戏失败,userId:{},currency:{},未映射到币种", loginVO.getUserId(), loginVO.getCurrencyCode());
            throw new BaowangDefaultException(ResultCode.VENUE_CURRENCY_NOT);
        }

        ShApiLoginReq shApiLoginReq = ShApiLoginReq.builder()
                .deviceType(DeviceType.convertToSHDevice(CurrReqUtils.getReqDeviceType()))
                .userName(casinoMemberVO.getVenueUserAccount())
                .currency(shCurrencyEnum.getCode())
                .loginIp(CurrReqUtils.getReqIp())
                .deskNumber(loginVO.getGameCode())
                .lang(SHLangEnum.conversionLang(CurrReqUtils.getLanguage()))
                .build();

        String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
        shApiLoginReq.setHashSign(hashStr);
        shApiLoginReq.setTimeStamp(timeStamp);
        shApiLoginReq.setMd5Sign(new HashMap<>(Map.of(
                "userName", casinoMemberVO.getVenueUserAccount(),
                "merchantNo", venueDetailVO.getMerchantNo(),
                "apiSign", venueDetailVO.getAesKey()
        )));

        shApiLoginReq.setMerchantNo(venueDetailVO.getMerchantNo());
        String url = venueDetailVO.getApiUrl() + SHConstantApi.LOGIN;

        try {
            String response = HttpClientHandler.post(url, JSON.toJSONString(shApiLoginReq));
            log.info("SH-登录游戏response={}", response);
            if (StringUtils.isBlank(response)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
            if (SUCCESS_CODE == responseVO.getCode()) {
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(responseVO.getData()));
                //保存userId
                if (StringUtils.isBlank(casinoMemberVO.getUserId())) {
                    casinoMemberVO.setVenueUserId(jsonObject.getString("userId"));
                    casinoMemberService.updateCasinoMember(casinoMemberVO);
                }
                GameLoginVo gameLoginVo = GameLoginVo.builder()
                        .source(jsonObject.getString("gameUrl"))
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(loginVO.getUserAccount())
                        .venueCode(VenueEnum.SH.getVenueCode())
                        .build();
                return ResponseVO.success(gameLoginVo);
            } else if (GAME_ROOM_MAINTAIN == responseVO.getCode()) {
                return ResponseVO.fail(ResultCode.GAME_ROOM_MAINTAIN);
            }
        } catch (Exception e) {
            log.error("SH-登录游戏 用户名【{}】 登录游戏发生错误!!", loginVO.getUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    public List<ShDeskInfoVO> queryGameList() {
        List<VenueInfoPO> venueInfoList = venueInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.SH.getVenueCode()));

        if (ObjectUtil.isEmpty(venueInfoList)) {
            return Lists.newArrayList();
        }
        VenueInfoPO venueInfo = venueInfoList.get(0);

        String url = venueInfo.getApiUrl() + SHConstantApi.DESK_STATUS_LIST;
        venueInfo.setApiUrl(url);
        Long timeStamp = System.currentTimeMillis();
        String hashStr = venueInfo.getMerchantNo() + timeStamp + venueInfo.getAesKey();
        ShReqBase shReqBase = new ShReqBase();
        shReqBase.setMerchantNo(venueInfo.getMerchantNo());
        shReqBase.setTimeStamp(timeStamp);
        shReqBase.setHashSign(hashStr);
        shReqBase.setMd5Sign(new HashMap<>(Map.of("merchantNo", venueInfo.getMerchantNo(), "timeStamp", timeStamp)));
        String response = HttpClientHandler.post(url, JSON.toJSONString(shReqBase));
        if (StringUtils.isBlank(response)) {
            return Lists.newArrayList();
        }

        ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
        if (0 != responseVO.getCode() || ObjectUtil.isEmpty(responseVO.getData())) {
            log.info("SH-调用桌台接口异常={}", responseVO);
            return Lists.newArrayList();
        }

        List<ShDeskStatusListResVO> list = JSON.parseArray(responseVO.getData().toString(), ShDeskStatusListResVO.class);
        if (CollectionUtil.isEmpty(list)) {
            log.info("SH-调用桌台接口转换异常={}", responseVO);
            return Lists.newArrayList();
        }

        return list.stream()
                .flatMap(shDeskStatusListResVO -> shDeskStatusListResVO.getDeskResponseVOList().stream())
                .map(detail -> new ShDeskInfoVO(detail.getDeskName(), detail.getDeskNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public String genVenueUserPassword() {
        return null;
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueDetailVO.getVenueCode());
        int page = 1;
        int pageSize = 400;

        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.SH.getVenueCode());
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));


        while (true) {
            Long timeStamp = System.currentTimeMillis();
            JSONObject requestParameters = new JSONObject();

            requestParameters.put("startTime", venuePullParamVO.getStartTime());
            requestParameters.put("endTime", venuePullParamVO.getEndTime());
            requestParameters.put("pageIndex", page);
            requestParameters.put("pageSize", pageSize);
            requestParameters.put("merchantNo", venueDetailVO.getMerchantNo());
            requestParameters.put("timeStamp", timeStamp);
            requestParameters.put("version", "1.0");


            String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
            String hashSign = HashUtil.sha256(hashStr);

            requestParameters.put("hashSign", hashSign);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("merchantNo", venueDetailVO.getMerchantNo());
            hashMap.put("apiSign", venueDetailVO.getAesKey());

            String md5Sign = SecurityUtil.paramSigns(hashMap);
            requestParameters.put("md5Sign", md5Sign);

            String url = venueDetailVO.getApiUrl() + SHConstantApi.GET_BET_ORDER_LIST;
//            log.info("SH拉取注单 url:{}, 参数：{}", url, requestParameters);
            String response = HttpClientHandler.post(url, requestParameters.toString());

            if (ObjectUtil.isEmpty(response)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            ResponseVO responseVO = JSONObject.parseObject(response, ResponseVO.class);
            if (responseVO == null || responseVO.getCode() != 0) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            Page<OrderResponseVO> dataPage = JSONObject.parseObject(JSON.toJSONString(responseVO.getData()), Page.class);
            log.info("SH 拉取注单 返回:{}", dataPage);
            if (dataPage.getData().isEmpty()) {
                log.info("SH 拉取注单 返回:空");
                break;
            }

            //注单保存
            List<OrderResponseVO> betData = JSON.parseArray(JSON.toJSONString(dataPage.getData()), OrderResponseVO.class);
            List<OrderRecordVO> list = new ArrayList<>();
            betData = betData.stream()
                    .collect(Collectors.toMap(OrderResponseVO::getOrderId, e -> e, (o, n) -> n))
                    .values()
                    .stream()
                    .toList();
            // 场馆用户关联信息
            List<String> thirdUserName = betData.stream().map(OrderResponseVO::getUserName).distinct().toList();

            List<CasinoMemberPO> casinoMemberPOS = casinoMemberService.list(Wrappers.<CasinoMemberPO>lambdaQuery()
                    .eq(CasinoMemberPO::getVenueCode, venueDetailVO.getVenueCode())
                    .in(CasinoMemberPO::getVenueUserAccount, thirdUserName));

            if (CollectionUtils.isEmpty(casinoMemberPOS)) {
                log.info("{} 拉单异常,casinoMember 没有记录:{}", venueDetailVO.getVenueCode(), thirdUserName);
                break;
            }

            Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberPOS.stream()
                    .collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, e -> e));

            // 用户信息
            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();

            if (CollectionUtils.isEmpty(userIds)) {
                log.info("{} 拉单异常,accountList", venueDetailVO.getVenueCode());
                break;
            }

            userIds = userIds.stream().distinct().toList();

            Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
            // 用户登录信息
            Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);


            for (OrderResponseVO orderResponseVO : betData) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getUserName());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), orderResponseVO.getUserName());
                    continue;
                }
                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                if (userInfoVO == null) {
                    log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
                    continue;
                }

                //读取站点名称
                String siteName = null;
                if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
                    SiteVO siteVO = siteVOMap.get(userInfoVO.getSiteCode());
                    siteName = siteVO.getSiteName();
                }

                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                // 映射原始注单
                OrderRecordVO recordVO = parseRecords(venueDetailVO, orderResponseVO, userInfoVO, userLoginInfoVO, rebateMap);
                recordVO.setVenueType(gameTypeId);
                recordVO.setGameName(String.valueOf(orderResponseVO.getGameTypeId()));
                recordVO.setPlayInfo(getPlayType(orderResponseVO));
                recordVO.setPlayType(getPlayType(orderResponseVO));
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setUserName(userInfoVO.getUserName());

                if(SHGameTypeEnum.SDLH.getCode().equals(orderResponseVO.getGameTypeId())){
                    recordVO.setResultList(orderResponseVO.getBetResultVoucherSource());
                }else{
                    recordVO.setResultList(orderResponseVO.getBetResultProtocol());
                }
                list.add(recordVO);
            }

            // 重结算注单
//            if (CollectionUtils.isNotEmpty(list)) {
//                List<String> thirdOrderIds = list.stream().map(OrderRecordVO::getThirdOrderId).toList();
//                List<OrderRecordPO> thirdOrderList = orderRecordService.findByThirdOrderIds(thirdOrderIds,VenueEnum.SH.getVenueCode());
//                if (CollectionUtils.isNotEmpty(thirdOrderList)) {
//                    Map<String, Integer> orderMap = thirdOrderList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, OrderRecordPO::getOrderStatus));
//                    for (OrderRecordVO orderRecordVO : list) {
//                        Integer orderStatus = orderMap.get(orderRecordVO.getThirdOrderId());
//                        if (ClassifyEnum.SETTLED.getCode().equals(orderStatus) ||
//                                ClassifyEnum.RESETTLED.getCode().equals(orderStatus)) {
//                            orderRecordVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
//                        }
//                    }
//                }
//            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            list.clear();
            page++;
            try {
                Thread.sleep(2000);
            }catch (Exception e){}
        }


        return ResponseVO.success();

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, OrderResponseVO orderResponseVO, UserInfoVO userInfoVO,
                                       UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getCostAmount());
        recordVO.setBetIp(orderResponseVO.getBetIp() == null ? null : orderResponseVO.getBetIp());
        recordVO.setBetContent(orderResponseVO.getPlayType());
        recordVO.setBetTime(orderResponseVO.getBetTimestamp());
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUserName());
        if (userLoginInfoVO != null) {
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        Integer gameTypeId = orderResponseVO.getGameTypeId();

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(orderResponseVO.getDeskNumber());
//        recordVO.setPlayType(GameEnum.PlayTypeEnum.nameOfCode(orderResponseVO.getPlayType()));
        recordVO.setGameNo(orderResponseVO.getGameNo());
        recordVO.setThirdGameCode(String.valueOf(gameTypeId));
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(orderResponseVO.getOrderId());
        recordVO.setTransactionId(orderResponseVO.getOrderId());
        recordVO.setValidAmount(orderResponseVO.getValidAmount());
        recordVO.setWinLossAmount(orderResponseVO.getWinLossAmount());
        recordVO.setPayoutAmount(orderResponseVO.getPayAmount());
        recordVO.setOdds(orderResponseVO.getRate().toString());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        Integer orderStatus = getLocalOrderStatus(orderResponseVO.getOrderStatus());
        recordVO.setOrderStatus(orderStatus);
        recordVO.setSettleTime(orderResponseVO.getSettlementTimestamp());
        if (Objects.equals(orderStatus, OrderStatusEnum.CANCEL.getCode())) {
            recordVO.setFirstSettleTime(orderResponseVO.getSettlementTimestamp());
        }
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
//        recordVO.setChangeStatus(getChangeStatus(orderResponseVO.getOrderStatus(), recordVO.getOrderId()));
        recordVO.setDeskNo(orderResponseVO.getDeskNo());
        recordVO.setBootNo(orderResponseVO.getBootNo());
        recordVO.setResultList(orderResponseVO.getBetResult());
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        recordVO.setOrderInfo(String.valueOf(orderResponseVO.getGameTypeId()));
        recordVO.setPlayType(getPlayType(orderResponseVO));
        recordVO.setGameName(String.valueOf(gameTypeId));

        // 返水
//        if (!rebateMap.isEmpty() && (recordVO.getOrderClassify().equals(ClassifyEnum.SETTLED.getCode()) ||
//                recordVO.getOrderClassify().equals(ClassifyEnum.RESETTLED.getCode()))) {
//            Map<String, BigDecimal> resultRate = ThirdUtil.getRebate(userInfoVO, recordVO, rebateMap, validBetAmount);
//            recordVO.setRebateRate(resultRate.get("rebate"));
//            recordVO.setRebateAmount(resultRate.get("scale"));
//        }
        // 玩法 ADD by xiaozhi 注单展示需要
//        recordVO.setOrderInfo(GameEnum.PlayTypeEnum.nameOfCode(orderResponseVO.getPlayType()));
        return recordVO;
    }


    private String getPlayType(OrderResponseVO orderResponseVO) {
        //如果状态是 -2 代表打赏,
        if (ShOrderStatusEnum.ALREADY_SETTLED.getCode().equals(orderResponseVO.getOrderStatus())) {
            return SHPlayTypeEnum.TIPS.getCode();
        }

        if (ObjectUtil.isNotEmpty(orderResponseVO.getPlayType())) {
            return orderResponseVO.getPlayType();
        }
        return null;
    }


    public BigDecimal getValidAmount(OrderResponseVO orderResponseVO) {
        if (orderResponseVO.getOrderStatus() == 0
                || orderResponseVO.getOrderStatus() == 2
                || orderResponseVO.getOrderStatus() == -1) {
            return BigDecimal.ZERO;
        }


        BigDecimal validAmount = computerValidBetAmount(orderResponseVO.getCostAmount(), orderResponseVO.getWinLossAmount(), VenueTypeEnum.SH);

        return validAmount == null ? BigDecimal.ZERO : validAmount;
    }

    public Integer getLocalOrderStatus(Integer orderStatus) {
        Map<Integer, Integer> statusMap = new HashMap<>();
        statusMap.put(ShOrderStatusEnum.ALREADY_SETTLED.getCode(), OrderStatusEnum.SETTLED.getCode());
        statusMap.put(ShOrderStatusEnum.NOT_SETTLEMENT.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
        statusMap.put(ShOrderStatusEnum.SETTLEMENT.getCode(), OrderStatusEnum.SETTLED.getCode());
        statusMap.put(ShOrderStatusEnum.CANCEL.getCode(), OrderStatusEnum.CANCEL.getCode());
        statusMap.put(ShOrderStatusEnum.RECALCULATE.getCode(), OrderStatusEnum.RESETTLED.getCode());
        Integer localStatus = statusMap.get(orderStatus);

        if (localStatus != null) {
            return localStatus;
        }

        log.info("视讯返回未知订单状态:{},", orderStatus);
        return null;
    }

    public Integer getChangeStatus(Integer orderStatus, String orderId) {
        if (Objects.equals(orderStatus, ShOrderStatusEnum.NOT_SETTLEMENT.getCode()) ||
                Objects.equals(orderStatus, ShOrderStatusEnum.INITIAL.getCode()) ||
                Objects.equals(orderStatus, ShOrderStatusEnum.SETTLEMENT.getCode())) {
            return ChangeStatusEnum.NOT_CHANGE.getCode();
        } else if (Objects.equals(orderStatus, ShOrderStatusEnum.CANCEL.getCode())) {
            //如果是撤销单，则需要判断之前的订单有没有结算，没有结算则变更状态为未变更，已结算则为已变更
            OrderRecordVO orderRecordVO = orderRecordService.queryByOrderId(orderId);
            if (orderRecordVO != null) {
                if (orderRecordVO.getOrderClassify().equals(ClassifyEnum.SETTLED.getCode()) ||
                        orderRecordVO.getOrderClassify().equals(ClassifyEnum.RESETTLED.getCode())) {
                    return ChangeStatusEnum.CHANGED.getCode();
                }
            }
            return ChangeStatusEnum.NOT_CHANGE.getCode();
        }

        return ChangeStatusEnum.CHANGED.getCode();
    }


    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {

        venueDetailVO.setMerchantNo(venueDetailVO.getMerchantNo());
        venueDetailVO.setAesKey(venueDetailVO.getAesKey());
        long timeStamp = System.currentTimeMillis();
        ShApiLoginReq shApiLoginReq = ShApiLoginReq.builder()
                .userName(venueUserAccount)
                .build();
        String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
        shApiLoginReq.setHashSign(hashStr);
        shApiLoginReq.setTimeStamp(timeStamp);
        shApiLoginReq.setMd5Sign(new HashMap<>(Map.of(
                "userName", venueUserAccount,
                "merchantNo", venueDetailVO.getMerchantNo(),
                "apiSign", venueDetailVO.getAesKey()
        )));

        shApiLoginReq.setMerchantNo(venueDetailVO.getMerchantNo());
        String url = venueDetailVO.getApiUrl() + SHConstantApi.LOGOUT;

        try {
            String response = HttpClientHandler.post(url, JSON.toJSONString(shApiLoginReq));
            log.info("SH-退出登录游戏response={}", response);
            if (StringUtils.isBlank(response)) {
                return ResponseVO.success(false);
            }
            return ResponseVO.success(true);
        } catch (Exception e) {
            log.info("SH-退出登录游戏异常", e);
        }
        return ResponseVO.success(false);
    }




    /**
     * 根据商户查询场馆
     * @param merchantNo 商户
     * @param venuePlatform 平台
     * @return 场馆
     */
    private VenueEnum getVenueByUser(String merchantNo, String venuePlatform) {
        VenueEnum venueEnum = getVenueCodeByMerchantNoAndVenuePlatform(merchantNo,venuePlatform );
        if (venueEnum == null) {
            log.info("未查到用户登陆数据:{},{}", merchantNo, venuePlatform);
            throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }
        return venueEnum;
    }


    public ShBaseRes<ShBalanceRes> queryBalance(ShQueryBalanceReq request) {

        if (!request.validate()) {
            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(request.getMerchantNo(), VenuePlatformConstants.SH);
        if(venueInfoVO == null){
            log.error(" adjustBalance userName[{}] not find.",  request.getUserName());
            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }

        if (this.validateMD5Sign(request.getMd5Sign(), request.getUserName(),venueInfoVO.getVenueCode())) {
            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
        }

        try {
            String userId = super.getVenueUserAccount(request.getUserName());

            UserInfoVO userInfoVO = getByUserId(userId);
            if (Objects.isNull(userInfoVO)) {
                log.error("{} queryBalance userName[{}] not find.", venueInfoVO.getVenueName(), request.getUserName());
                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }
            if (venueMaintainClosed(venueInfoVO.getVenueCode(), userInfoVO.getSiteCode())) {
                log.info("{}:场馆未开启", venueInfoVO.getVenueName());
                return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
            }
            BigDecimal venueAmount = checkJoinActivity(userInfoVO.getUserId(),userInfoVO.getSiteCode(),venueInfoVO.getVenueCode());

            //不允许下注
            if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
                return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
            }


            if(venueMaintainClosed(venueInfoVO.getVenueCode(),userInfoVO.getSiteCode())){
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueInfoVO.getVenueCode());
                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
            if (Objects.isNull(userCenterCoin)) {
                log.error("SH queryBalance userName[{}] not find.", request.getUserName());
                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }
            ShBalanceRes balanceRes = ShBalanceRes.builder().balanceAmount(userCenterCoin.getTotalAmount())
                    .userName(request.getUserName())
                    .currencyCode(userCenterCoin.getCurrency())
                    .updatedTime(Instant.now().toEpochMilli()).build();
            return ShBaseRes.success(balanceRes);
        } catch (Exception e) {
            log.error("SH queryBalance error", e);
        }
        return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
    }


    public ShBaseRes<ShAdjustBalanceRes> adjustBalance(ShAdjustBalanceReq request) {

        if (!request.validate()) {
            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
        }

        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(request.getMerchantNo(), VenuePlatformConstants.SH);
        if(venueInfoVO == null){
            log.error(" adjustBalance userName[{}] not find.",  request.getUserName());
            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }

        if (this.validateMD5Sign(request.getMd5Sign(), request.getUserName(),venueInfoVO.getVenueCode())) {
            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
        }

        String userId = super.getVenueUserAccount(request.getUserName());

        UserInfoVO userInfoVO = getByUserId(userId);
        if (Objects.isNull(userInfoVO)) {
            log.error("{} queryUserInfoByAccount userName[{}] not find.", venueInfoVO.getVenueName(), request.getUserName());
            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }

        if (venueMaintainClosed(venueInfoVO.getVenueCode(),userInfoVO.getSiteCode())) {
            log.info("{}:场馆未开启", venueInfoVO.getVenueName());
            return ShBaseRes.failed(ShResultCodeEnums.PARAM_ERROR);
        }
        BigDecimal venueAmount = checkJoinActivity(userInfoVO.getUserId(),userInfoVO.getSiteCode(),venueInfoVO.getVenueCode());

        //不允许下注
        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("游戏校验失败,不允许下注:{}", venueAmount);
            return ShBaseRes.failed(ShResultCodeEnums.SIGN_ERROR);
        }


        if (ObjectUtil.isNotEmpty(userInfoVO.getAccountStatus())) {
            List<String> accountStatusList = Arrays.asList(userInfoVO.getAccountStatus().split(","));
            if (CollectionUtil.isNotEmpty(accountStatusList) && accountStatusList.contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.error("{} queryUserInfoByAccount userName[{}] account lock.", venueInfoVO.getVenueName(), request.getUserName());
                return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }
        }

        ShGameServiceImpl shService = applicationContext.getBean(ShGameServiceImpl.class);
        return shService.toCoin(userInfoVO, request,venueInfoVO.getVenueCode());
    }

    @DistributedLock(name = RedisConstants.SH_COIN_LOCK, unique = "#userInfoVO.userId", waitTime = 3, leaseTime = 180)
    public ShBaseRes<ShAdjustBalanceRes> toCoin(UserInfoVO userInfoVO, ShAdjustBalanceReq request,String venueCode) {

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (Objects.isNull(userCenterCoin)) {
            log.info("{} queryBalance userName[{}] not find.", venueCode, request.getUserName());
            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }

        ShAdjustBalanceRes build = ShAdjustBalanceRes.builder()
                .transferNo(request.getTransferNo())
                .userName(request.getUserName())
                .currentAmount(userCenterCoin.getTotalAmount())
                .realAmount(request.getTotalAmount())
                .build();


        // 余额如果小于0，说明余额不足
        BigDecimal balance = userCenterCoin.getTotalAmount().add(request.getTotalAmount());
        if (balance.compareTo(BigDecimal.ZERO) < 0 && (ShTransferTypeEnums.CANCEL.getCode().intValue() != request.getTransferType() &&
                ShTransferTypeEnums.RESETTLEMENT.getCode().intValue() != request.getTransferType())) {
            log.info("{} Service updateBalance error, transferNo = {}, totalAmount = {}, amount = {}",
                    venueCode, request.getTransferNo(), request.getTotalAmount(), userCenterCoin.getTotalAmount());
            return ShBaseRes.failed(ShResultCodeEnums.INSUFFICIENT_BALANCE, build);
        }

        //视讯扣费传过来的是集合订单,所以把集合里面每个订单都检查一便看看是否有单出现重复账变,如果有则直接拒绝这一批次的所有订单
        for (ShTransferOrderReq item : request.getTransferOrderVOList()) {
            validCoin(request.getTransferType(), item.getOrderNo(), userInfoVO.getUserId(), item.getAmount());
        }
        try {
            for (ShTransferOrderReq item : request.getTransferOrderVOList()) {

                UserCoinAddVO userCoinAddVO = this.buildUserCoinAddVO(request.getTransferType(), item.getOrderNo(), item.getAmount(), userCenterCoin, userInfoVO);
                userCoinAddVO.setVenueCode(venueCode);
//                CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
                CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);

                if (ObjectUtil.isEmpty(coinRecordResultVO)) {
                    log.error("SH调用扣费失败:orderNo:{},amount:{},req:{}", item.getOrderNo(), item.getAmount(), request);
                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR, build);
                }

                //重复扣费的单直接通过.例如多笔订单扣费的过程中某笔订单扣费失败 直接拒绝 ,等待三方重新回来调用
                if (!coinRecordResultVO.getResult() && UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS.equals(coinRecordResultVO.getResultStatus())) {
                    return ShBaseRes.failed(ShResultCodeEnums.REPEAT_TRANSACTIONS, build);
                }

                if (!coinRecordResultVO.getResult()) {
                    log.error("SH调用扣费失败,扣费拒绝:orderNo:{},amount:{},req:{}", item.getOrderNo(), item.getAmount(), request);
                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
                }

                //修改余额 记录账变
                UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
                if (resultStatus.getCode().equals(UpdateBalanceStatusEnums.SUCCESS.getCode())
                        || resultStatus.getCode().equals(UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.getCode())) {
                    continue;
                }

                if (resultStatus.getCode().equals(UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE.getCode()) ||
                        resultStatus.getCode().equals(UpdateBalanceStatusEnums.WALLET_NOT_EXIST.getCode())) {//余额不足
                    log.error("SH调用扣费失败:玩家余额不足,orderNo:{},amount:{},req:{},result:{}", item.getOrderNo(), item.getAmount(), request, coinRecordResultVO);
                    return ShBaseRes.failed(ShResultCodeEnums.INSUFFICIENT_BALANCE);
                } else {
                    log.error("SH调用扣费失败:其他异常,orderNo:{},amount:{},req:{},result:{}", item.getOrderNo(), item.getAmount(), request, coinRecordResultVO);
                    return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
                }
            }


        }catch (SHDefaultException sh){
            return ShBaseRes.failed(sh.getResultCode());
        }
        catch (Exception e) {
            log.info("视讯扣费异常",e);
            return ShBaseRes.failed(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }
        UserCoinWalletVO userCoinWalletVO = getUserCenterCoin(userInfoVO.getUserId());
        return ShBaseRes.success(ShAdjustBalanceRes.builder()
                .transferNo(request.getTransferNo())
                .userName(request.getUserName())
                .currentAmount(userCoinWalletVO.getTotalAmount())
                .realAmount(request.getTotalAmount()).build());

    }


    private UserCoinAddVO buildUserCoinAddVO(String orderId, BigDecimal amount, UserCoinWalletVO userCenterCoin, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderId);
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(userCenterCoin.getCurrency());
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        } else {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        }
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        return userCoinAddVO;
    }

    private UserCoinAddVO buildUserCoinAddVO(Integer transferType, String orderId, BigDecimal amount, UserCoinWalletVO userCenterCoin, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderId);
        userCoinAddVO.setThirdOrderNo(orderId);
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(userCenterCoin.getCurrency());

        toSetUserCoinAddVO(userCoinAddVO, transferType, amount);

        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        return userCoinAddVO;
    }

    /**
     * 下注前校验
     *
     * @param transferType 视讯类型
     * @param orderId      订单
     * @param userId       用户
     * @param amount       金额
     */
    private void validCoin(Integer transferType, String orderId, String userId, BigDecimal amount) {
        UserCoinRecordRequestVO userCoinAddVO = new UserCoinRecordRequestVO();
        userCoinAddVO.setUserId(userId);
        userCoinAddVO.setOrderNo(orderId);
        //下注类型
        if (ShTransferTypeEnums.BET.getCode().equals(transferType) || ShTransferTypeEnums.TIPS.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());

            //下注类型 只能是扣钱 下注类型加钱就是有异常
            if (amount.compareTo(BigDecimal.ZERO) >= 0) {
                log.info("下注类型异常,transferType:{},下注类型存在非负数:{}", transferType, amount);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            //正常结算
        } else if (ShTransferTypeEnums.PAYOUT.getCode().equals(transferType)) {

            //正常结算 只能是加钱 正常结算扣钱就是有异常
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("下注类型异常,transferType:{},正常结算类型存在负数:{}", transferType, amount);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            //跳局=退钱
        } else if (ShTransferTypeEnums.JUMP.getCode().equals(transferType)) {

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("下注类型异常,transferType:{},跳局类型存在负数:{}", transferType, amount);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            //重算局 跟 取消都是重新结算
        } else if (ShTransferTypeEnums.RESETTLEMENT.getCode().equals(transferType)) {
            //重算局不做处理,重算局 会多次账变
            return;
        }else if (ShTransferTypeEnums.CANCEL.getCode().equals(transferType)) {
            //取消局(有可能负数 扣款)
            //取消局比较特殊,场景是 先下注扣钱 然后结算加钱 然后取消在扣钱,所以会存在一笔单出现多次重复 加扣的场景.所以这种类型的单
            //在我们系统重复账变逻辑 只能出现一次
//            if (amount.compareTo(BigDecimal.ZERO) > 0) {
//                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//            } else {
//                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            }

            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setUserId(userId);
            coinRecordRequestVO.setOrderNo(orderId);
            coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
//            if (amount.compareTo(BigDecimal.ZERO) > 0) {
//                coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//                coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//            } else {
//                coinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//                coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            }

            List<UserCoinRecordVO> recordVOS = getUserCoinRecords(coinRecordRequestVO);
            if(CollectionUtil.isNotEmpty(recordVOS)){
                log.info("取消局 下注前校验,该单已经进行过取消局,不允许第二次重新账变:{}", coinRecordRequestVO);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }
            return;
        }else if (ShTransferTypeEnums.TRANSACTION_ROLLBACK.getCode().equals(transferType)) {//下注 交易回滚
            //视讯下注交易回滚类型 9 ,该类型的请求只有加款行为.
            // 这笔单在我们系统订单是必须 已经存在扣款记录,并且这比单没有进行加款记录(防止重复加款),
            //并且我们这边根据请求订单的金额的绝对值去对比我们我们数据库里的扣款金额的绝对值.一致的时候进行加款
            //不符合这种条件的情况下.拒绝请求

            //加款
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                log.info("下注前校验,交易回滚,transferType:{},不能等于0:{}", transferType, amount);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            String orderNo = userCoinAddVO.getOrderNo();//订单扣费记录

            //先判断是否有扣费记录
            UserCoinRecordRequestVO betRecordRequestVO = new UserCoinRecordRequestVO();
            betRecordRequestVO.setUserId(userCoinAddVO.getUserId());
            betRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            betRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            betRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            betRecordRequestVO.setOrderNo(orderNo);
            List<UserCoinRecordVO> recordVOS = getUserCoinRecords(betRecordRequestVO);

            if (CollectionUtil.isEmpty(recordVOS)) {//该单没有下注记录
                log.info("下注前校验,交易回滚,该单没有下注记录,不进行回滚:{}", betRecordRequestVO);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }


            UserCoinRecordVO coinRecordVO = recordVOS.get(0);
            BigDecimal coinValue = coinRecordVO.getCoinValue().abs();//账变金额

            amount = amount.abs();//绝对值

            if (coinValue.compareTo(amount) != 0) {
                log.info("下注前校验,交易回滚,回滚金额与扣费金额不一致:回滚金额:{},扣费金额:{}", amount, coinValue);
                throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
            }

            //判断是否有加款记录
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setUserId(userCoinAddVO.getUserId());

        }  else {
            log.info("视讯扣费类型异常:{},", transferType);
            throw new SHDefaultException(ShResultCodeEnums.SERVER_INTERNAL_ERROR);
        }

        Long addCount = userCoinRecordPageCount(userCoinAddVO);
        if (addCount > 0) {//该单已经加扣过款,不进行重复加款
            log.info("下注前校验,交易回滚,该单已经加扣过款,不进行重复加款:{}", userCoinAddVO);
            throw new SHDefaultException(ShResultCodeEnums.REPEAT_TRANSACTIONS);
        }


    }


    /**
     * 账变类型转换
     *
     * @param userCoinAddVO 对象
     * @param transferType  视讯类型
     * @param amount        账变金额
     */
    private void toSetUserCoinAddVO(UserCoinAddVO userCoinAddVO, Integer transferType, BigDecimal amount) {

        //下注 只能是扣钱
        if (ShTransferTypeEnums.BET.getCode().equals(transferType)) {

            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

            //打赏
        }else if(ShTransferTypeEnums.TIPS.getCode().equals(transferType)){
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_TIPS.getCode());

            //正常结算=加钱
        } else if (ShTransferTypeEnums.PAYOUT.getCode().equals(transferType)) {

            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
            //跳局=退钱
        } else if (ShTransferTypeEnums.JUMP.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
            //重算局 跟 取消都是重新结算
        } else if (ShTransferTypeEnums.RESETTLEMENT.getCode().equals(transferType) || ShTransferTypeEnums.CANCEL.getCode().equals(transferType)) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
            } else {
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
            }
        } else if (ShTransferTypeEnums.TRANSACTION_ROLLBACK.getCode().equals(transferType)) {//下注 交易回滚
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        }
    }



    private boolean validateMD5Sign(String reqMD5Sign, String userName,String venueCode) {
        VenueInfoVO venueConfigInfo = getVenueInfo(venueCode
                , "");
        String localSign = MD5Util.md5(venueConfigInfo.getMerchantKey() + "|" + userName);
        return !reqMD5Sign.equals(localSign.toUpperCase());
    }


}
