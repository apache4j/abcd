//package com.cloud.baowang.play.game.sh.impl.shZhCn;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.cloud.baowang.common.core.enums.DeviceType;
//import com.cloud.baowang.common.core.enums.ResultCode;
//import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.HttpClientHandler;
//import com.cloud.baowang.common.core.utils.OrderUtil;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
//import com.cloud.baowang.play.api.enums.ClassifyEnum;
//import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
//import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
//import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
//import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
//import com.cloud.baowang.play.api.vo.third.LoginVO;
//import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.constants.ServiceType;
//import com.cloud.baowang.play.game.base.GameBaseService;
//import com.cloud.baowang.play.game.base.GameService;
//import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
//import com.cloud.baowang.play.game.sh.enums.*;
//import com.cloud.baowang.play.game.sh.request.ShApiLoginReq;
//import com.cloud.baowang.play.game.sh.request.ShReqBase;
//import com.cloud.baowang.play.game.sh.response.ShDeskStatusListResVO;
//import com.cloud.baowang.play.po.CasinoMemberPO;
//import com.cloud.baowang.play.po.VenueInfoPO;
//import com.cloud.baowang.play.service.CasinoMemberService;
//import com.cloud.baowang.play.service.OrderRecordProcessService;
//import com.cloud.baowang.play.service.OrderRecordService;
//import com.cloud.baowang.play.service.VenueInfoService;
//import com.cloud.baowang.play.util.HashUtil;
//import com.cloud.baowang.play.util.SecurityUtil;
//import com.cloud.baowang.play.vo.GameLoginVo;
//import com.cloud.baowang.play.vo.VenuePullParamVO;
//import com.cloud.baowang.play.vo.sh.OrderResponseVO;
//import com.cloud.baowang.play.vo.sh.Page;
//import com.cloud.baowang.system.api.api.site.SiteApi;
//import com.cloud.baowang.system.api.vo.site.SiteVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
//import com.google.common.collect.Lists;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service(ServiceType.GAME_THIRD_API_SERVICE + VenueCodeConstants.SH_ZHCN)
//@AllArgsConstructor
//public class ShZhCnGameServiceImpl extends GameBaseService implements GameService {
//
//    private final OrderRecordService orderRecordService;
//    private final OrderRecordProcessService orderRecordProcessService;
//    private final CasinoMemberService casinoMemberService;
//
//    private final VenueInfoService venueInfoService;
//
//
//    private final SiteApi siteApi;
//
//    /**
//     * 成功
//     **/
//    private final static int SUCCESS_CODE = 0;
//    /**
//     * 用户已存在
//     **/
//    private final static int ALREADY_EXISTS = 20002;
//    /**
//     * 游戏房间维护中
//     **/
//    private final static int GAME_ROOM_MAINTAIN = 30006;
//
//
//    @Override
//    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
//        return ResponseVO.success(true);
//    }
//
//    @Override
//    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
//
//        venueDetailVO.setMerchantNo(venueDetailVO.getMerchantNo());
//        venueDetailVO.setAesKey(venueDetailVO.getAesKey());
//        long timeStamp = System.currentTimeMillis();
//
//        SHCurrencyEnum shCurrencyEnum = SHCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());
//        if (shCurrencyEnum == null) {
//            log.info("视讯登陆游戏失败,userId:{},currency:{},未映射到币种", loginVO.getUserId(), loginVO.getCurrencyCode());
//            throw new BaowangDefaultException(ResultCode.VENUE_CURRENCY_NOT);
//        }
//
//        ShApiLoginReq shApiLoginReq = ShApiLoginReq.builder()
//                .deviceType(DeviceType.convertToSHDevice(CurrReqUtils.getReqDeviceType()))
//                .userName(casinoMemberVO.getVenueUserAccount())
//                .currency(shCurrencyEnum.getCode())
//                .loginIp(CurrReqUtils.getReqIp())
//                .deskNumber(loginVO.getGameCode())
//                .lang(SHLangEnum.conversionLang(CurrReqUtils.getLanguage()))
//                .build();
//
//        String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
//        shApiLoginReq.setHashSign(hashStr);
//        shApiLoginReq.setTimeStamp(timeStamp);
//        shApiLoginReq.setMd5Sign(new HashMap<>(Map.of(
//                "userName", casinoMemberVO.getVenueUserAccount(),
//                "merchantNo", venueDetailVO.getMerchantNo(),
//                "apiSign", venueDetailVO.getAesKey()
//        )));
//
//        shApiLoginReq.setMerchantNo(venueDetailVO.getMerchantNo());
//        String url = venueDetailVO.getApiUrl() + SHConstantApi.LOGIN;
//
//        try {
//            String response = HttpClientHandler.post(url, JSON.toJSONString(shApiLoginReq));
//            log.info("SH-登录游戏response={}", response);
//            if (StringUtils.isBlank(response)) {
//                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//            }
//            ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
//            if (SUCCESS_CODE == responseVO.getCode()) {
//                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(responseVO.getData()));
//                //保存userId
//                if (StringUtils.isBlank(casinoMemberVO.getUserId())) {
//                    casinoMemberVO.setVenueUserId(jsonObject.getString("userId"));
//                    casinoMemberService.updateCasinoMember(casinoMemberVO);
//                }
//                GameLoginVo gameLoginVo = GameLoginVo.builder()
//                        .source(jsonObject.getString("gameUrl"))
//                        .type(GameLoginTypeEnums.URL.getType())
//                        .userAccount(loginVO.getUserAccount())
//                        .venueCode(VenueEnum.SH_ZHCN.getVenueCode())
//                        .build();
//                return ResponseVO.success(gameLoginVo);
//            } else if (GAME_ROOM_MAINTAIN == responseVO.getCode()) {
//                return ResponseVO.fail(ResultCode.GAME_ROOM_MAINTAIN);
//            }
//        } catch (Exception e) {
//            log.error("SH-登录游戏 用户名【{}】 登录游戏发生错误!!", loginVO.getUserAccount(), e);
//        }
//        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//    }
//
//    public List<ShDeskInfoVO> queryGameList(){
//        List<VenueInfoPO> venueInfoList = venueInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
//                .eq(VenueInfoPO::getVenueCode, VenueEnum.SH_ZHCN.getVenueCode()));
//
//        if (ObjectUtil.isEmpty(venueInfoList)) {
//            return Lists.newArrayList();
//        }
//        VenueInfoPO venueInfo = venueInfoList.get(0);
//
//        String url = venueInfo.getApiUrl() + SHConstantApi.DESK_STATUS_LIST;
//        venueInfo.setApiUrl(url);
//        Long timeStamp = System.currentTimeMillis();
//        String hashStr = venueInfo.getMerchantNo() + timeStamp + venueInfo.getAesKey();
//        ShReqBase shReqBase = new ShReqBase();
//        shReqBase.setMerchantNo(venueInfo.getMerchantNo());
//        shReqBase.setTimeStamp(timeStamp);
//        shReqBase.setHashSign(hashStr);
//        shReqBase.setMd5Sign(new HashMap<>(Map.of("merchantNo", venueInfo.getMerchantNo(), "timeStamp", timeStamp)));
//        String response = HttpClientHandler.post(url, JSON.toJSONString(shReqBase));
//        if (StringUtils.isBlank(response)) {
//            return Lists.newArrayList();
//        }
//
//        ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
//        if (0 != responseVO.getCode() || ObjectUtil.isEmpty(responseVO.getData())) {
//            log.info("SH-调用桌台接口异常={}", responseVO);
//            return Lists.newArrayList();
//        }
//
//        List<ShDeskStatusListResVO> list = JSON.parseArray(responseVO.getData().toString(), ShDeskStatusListResVO.class);
//        if (CollectionUtil.isEmpty(list)) {
//            log.info("SH-调用桌台接口转换异常={}", responseVO);
//            return Lists.newArrayList();
//        }
//
//        return list.stream()
//                .flatMap(shDeskStatusListResVO -> shDeskStatusListResVO.getDeskResponseVOList().stream())
//                .map(detail -> new ShDeskInfoVO(detail.getDeskName(), detail.getDeskNumber()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public String genVenueUserPassword() {
//        return null;
//    }
//
//    @Override
//    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
//        Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueDetailVO.getVenueCode());
//        int page = 1;
//        int pageSize = 400;
//
//        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.SH_ZHCN.getVenueCode());
//        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
//        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
//
//
//        while (true) {
//            Long timeStamp = System.currentTimeMillis();
//            JSONObject requestParameters = new JSONObject();
//
//            requestParameters.put("startTime", venuePullParamVO.getStartTime());
//            requestParameters.put("endTime", venuePullParamVO.getEndTime());
//            requestParameters.put("pageIndex", page);
//            requestParameters.put("pageSize", pageSize);
//            requestParameters.put("merchantNo", venueDetailVO.getMerchantNo());
//            requestParameters.put("timeStamp", timeStamp);
//            requestParameters.put("version", "1.0");
//
//
//            String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
//            String hashSign = HashUtil.sha256(hashStr);
//
//            requestParameters.put("hashSign", hashSign);
//
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("merchantNo", venueDetailVO.getMerchantNo());
//            hashMap.put("apiSign", venueDetailVO.getAesKey());
//
//            String md5Sign = SecurityUtil.paramSigns(hashMap);
//            requestParameters.put("md5Sign", md5Sign);
//
//            String url = venueDetailVO.getApiUrl() + SHConstantApi.GET_BET_ORDER_LIST;
////            log.info("SH拉取注单 url:{}, 参数：{}", url, requestParameters);
//            String response = HttpClientHandler.post(url, requestParameters.toString());
//
//            if (ObjectUtil.isEmpty(response)) {
//                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//            }
//            ResponseVO responseVO = JSONObject.parseObject(response, ResponseVO.class);
//            if (responseVO == null || responseVO.getCode() != 0) {
//                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
//            }
//
//            Page<OrderResponseVO> dataPage = JSONObject.parseObject(JSON.toJSONString(responseVO.getData()), Page.class);
//            log.info("SH 拉取注单 返回:{}", dataPage);
//            if (dataPage.getData().isEmpty()) {
//                log.info("SH 拉取注单 返回:空");
//                break;
//            }
//
//            //注单保存
//            List<OrderResponseVO> betData = JSON.parseArray(JSON.toJSONString(dataPage.getData()), OrderResponseVO.class);
//            List<OrderRecordVO> list = new ArrayList<>();
//            betData = betData.stream()
//                    .collect(Collectors.toMap(OrderResponseVO::getOrderId, e -> e, (o, n) -> n))
//                    .values()
//                    .stream()
//                    .toList();
//            // 场馆用户关联信息
//            List<String> thirdUserName = betData.stream().map(OrderResponseVO::getUserName).distinct().toList();
//
//            List<CasinoMemberPO> casinoMemberPOS = casinoMemberService.list(Wrappers.<CasinoMemberPO>lambdaQuery()
//                    .eq(CasinoMemberPO::getVenueCode, venueDetailVO.getVenueCode())
//                    .in(CasinoMemberPO::getVenueUserAccount, thirdUserName));
//
//            if (CollectionUtils.isEmpty(casinoMemberPOS)) {
//                log.info("{} 拉单异常,casinoMember 没有记录:{}", venueDetailVO.getVenueCode(), thirdUserName);
//                break;
//            }
//
//            Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberPOS.stream()
//                    .collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, e -> e));
//
//            // 用户信息
//            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();
//
//            if (CollectionUtils.isEmpty(userIds)) {
//                log.info("{} 拉单异常,accountList", venueDetailVO.getVenueCode());
//                break;
//            }
//
//            userIds = userIds.stream().distinct().toList();
//
//            Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
//            // 用户登录信息
//            Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
//
//
//            for (OrderResponseVO orderResponseVO : betData) {
//                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderResponseVO.getUserName());
//                if (casinoMemberVO == null) {
//                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), orderResponseVO.getUserName());
//                    continue;
//                }
//                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
//                if (userInfoVO == null) {
//                    log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
//                    continue;
//                }
//
//                //读取站点名称
//                String siteName = null;
//                if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
//                    SiteVO siteVO = siteVOMap.get(userInfoVO.getSiteCode());
//                    siteName = siteVO.getSiteName();
//                }
//
//                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
//
//                // 映射原始注单
//                OrderRecordVO recordVO = parseRecords(venueDetailVO, orderResponseVO, userInfoVO, userLoginInfoVO, rebateMap);
//                recordVO.setVenueType(gameTypeId);
//                recordVO.setGameName(String.valueOf(orderResponseVO.getGameTypeId()));
//                recordVO.setPlayInfo(getPlayType(orderResponseVO));
//                recordVO.setPlayType(getPlayType(orderResponseVO));
//                recordVO.setSiteName(siteName);
//                recordVO.setSiteCode(userInfoVO.getSiteCode());
//                recordVO.setUserName(userInfoVO.getUserName());
//
//                if(SHGameTypeEnum.SDLH.getCode().equals(orderResponseVO.getGameTypeId())){
//                    recordVO.setResultList(orderResponseVO.getBetResultVoucherSource());
//                }else{
//                    recordVO.setResultList(orderResponseVO.getBetResultProtocol());
//                }
//                list.add(recordVO);
//            }
//
//            // 重结算注单
////            if (CollectionUtils.isNotEmpty(list)) {
////                List<String> thirdOrderIds = list.stream().map(OrderRecordVO::getThirdOrderId).toList();
////                List<OrderRecordPO> thirdOrderList = orderRecordService.findByThirdOrderIds(thirdOrderIds,VenueEnum.SH.getVenueCode());
////                if (CollectionUtils.isNotEmpty(thirdOrderList)) {
////                    Map<String, Integer> orderMap = thirdOrderList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, OrderRecordPO::getOrderStatus));
////                    for (OrderRecordVO orderRecordVO : list) {
////                        Integer orderStatus = orderMap.get(orderRecordVO.getThirdOrderId());
////                        if (ClassifyEnum.SETTLED.getCode().equals(orderStatus) ||
////                                ClassifyEnum.RESETTLED.getCode().equals(orderStatus)) {
////                            orderRecordVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
////                        }
////                    }
////                }
////            }
//
//            // 订单处理
//            if (CollectionUtil.isNotEmpty(list)) {
//                orderRecordProcessService.orderProcess(list);
//            }
//            list.clear();
//            page++;
//            try {
//                Thread.sleep(2000);
//            }catch (Exception e){}
//        }
//
//
//        return ResponseVO.success();
//
//    }
//
//
//    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, OrderResponseVO orderResponseVO, UserInfoVO userInfoVO,
//                                       UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap) {
//        OrderRecordVO recordVO = new OrderRecordVO();
//        recordVO.setUserAccount(userInfoVO.getUserAccount());
//
//        recordVO.setUserId(userInfoVO.getUserId());
//        recordVO.setUserName(userInfoVO.getUserName());
//        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
//        recordVO.setAgentId(userInfoVO.getSuperAgentId());
//        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
//        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
//        recordVO.setBetAmount(orderResponseVO.getCostAmount());
//        recordVO.setBetIp(orderResponseVO.getBetIp() == null ? null : orderResponseVO.getBetIp());
//        recordVO.setBetContent(orderResponseVO.getPlayType());
//        recordVO.setBetTime(orderResponseVO.getBetTimestamp());
//        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
//        recordVO.setVenueCode(venueDetailVO.getVenueCode());
//        recordVO.setCasinoUserName(orderResponseVO.getUserName());
//        if (userLoginInfoVO != null) {
//            if (userLoginInfoVO.getLoginTerminal() != null) {
//                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
//            }
//        }
//        Integer gameTypeId = orderResponseVO.getGameTypeId();
//
//        recordVO.setCurrency(userInfoVO.getMainCurrency());
//        recordVO.setRoomType(orderResponseVO.getDeskNumber());
////        recordVO.setPlayType(GameEnum.PlayTypeEnum.nameOfCode(orderResponseVO.getPlayType()));
//        recordVO.setGameNo(orderResponseVO.getGameNo());
//        recordVO.setThirdGameCode(String.valueOf(gameTypeId));
//        recordVO.setOrderId(OrderUtil.getGameNo());
//        recordVO.setThirdOrderId(orderResponseVO.getOrderId());
//        recordVO.setTransactionId(orderResponseVO.getOrderId());
//        recordVO.setValidAmount(orderResponseVO.getValidAmount());
//        recordVO.setWinLossAmount(orderResponseVO.getWinLossAmount());
//        recordVO.setPayoutAmount(orderResponseVO.getPayAmount());
//        recordVO.setOdds(orderResponseVO.getRate().toString());
//        recordVO.setCreatedTime(System.currentTimeMillis());
//        recordVO.setUpdatedTime(System.currentTimeMillis());
//        Integer orderStatus = getLocalOrderStatus(orderResponseVO.getOrderStatus());
//        recordVO.setOrderStatus(orderStatus);
//        recordVO.setSettleTime(orderResponseVO.getSettlementTimestamp());
//        if (Objects.equals(orderStatus, OrderStatusEnum.CANCEL.getCode())) {
//            recordVO.setFirstSettleTime(orderResponseVO.getSettlementTimestamp());
//        }
//        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
//        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
////        recordVO.setChangeStatus(getChangeStatus(orderResponseVO.getOrderStatus(), recordVO.getOrderId()));
//        recordVO.setDeskNo(orderResponseVO.getDeskNo());
//        recordVO.setBootNo(orderResponseVO.getBootNo());
//        recordVO.setResultList(orderResponseVO.getBetResult());
//        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
//        recordVO.setReSettleTime(0L);
//        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
//        recordVO.setOrderInfo(String.valueOf(orderResponseVO.getGameTypeId()));
//        recordVO.setPlayType(getPlayType(orderResponseVO));
//        recordVO.setGameName(String.valueOf(gameTypeId));
//
//        // 返水
////        if (!rebateMap.isEmpty() && (recordVO.getOrderClassify().equals(ClassifyEnum.SETTLED.getCode()) ||
////                recordVO.getOrderClassify().equals(ClassifyEnum.RESETTLED.getCode()))) {
////            Map<String, BigDecimal> resultRate = ThirdUtil.getRebate(userInfoVO, recordVO, rebateMap, validBetAmount);
////            recordVO.setRebateRate(resultRate.get("rebate"));
////            recordVO.setRebateAmount(resultRate.get("scale"));
////        }
//        // 玩法 ADD by xiaozhi 注单展示需要
////        recordVO.setOrderInfo(GameEnum.PlayTypeEnum.nameOfCode(orderResponseVO.getPlayType()));
//        return recordVO;
//    }
//
//
//    private String getPlayType(OrderResponseVO orderResponseVO) {
//        //如果状态是 -2 代表打赏,
//        if (ShOrderStatusEnum.ALREADY_SETTLED.getCode().equals(orderResponseVO.getOrderStatus())) {
//            return SHPlayTypeEnum.TIPS.getCode();
//        }
//
//        if (ObjectUtil.isNotEmpty(orderResponseVO.getPlayType())) {
//            return orderResponseVO.getPlayType();
//        }
//        return null;
//    }
//
//
//    public BigDecimal getValidAmount(OrderResponseVO orderResponseVO) {
//        if (orderResponseVO.getOrderStatus() == 0
//                || orderResponseVO.getOrderStatus() == 2
//                || orderResponseVO.getOrderStatus() == -1) {
//            return BigDecimal.ZERO;
//        }
//
//
//        BigDecimal validAmount = computerValidBetAmount(orderResponseVO.getCostAmount(), orderResponseVO.getWinLossAmount(), VenueTypeEnum.SH);
//
//        return validAmount == null ? BigDecimal.ZERO : validAmount;
//    }
//
//    public Integer getLocalOrderStatus(Integer orderStatus) {
//        Map<Integer, Integer> statusMap = new HashMap<>();
//        statusMap.put(ShOrderStatusEnum.ALREADY_SETTLED.getCode(), OrderStatusEnum.SETTLED.getCode());
//        statusMap.put(ShOrderStatusEnum.NOT_SETTLEMENT.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
//        statusMap.put(ShOrderStatusEnum.SETTLEMENT.getCode(), OrderStatusEnum.SETTLED.getCode());
//        statusMap.put(ShOrderStatusEnum.CANCEL.getCode(), OrderStatusEnum.CANCEL.getCode());
//        statusMap.put(ShOrderStatusEnum.RECALCULATE.getCode(), OrderStatusEnum.RESETTLED.getCode());
//        Integer localStatus = statusMap.get(orderStatus);
//
//        if (localStatus != null) {
//            return localStatus;
//        }
//
//        log.info("视讯返回未知订单状态:{},", orderStatus);
//        return null;
//    }
//
//    public Integer getChangeStatus(Integer orderStatus, String orderId) {
//        if (Objects.equals(orderStatus, ShOrderStatusEnum.NOT_SETTLEMENT.getCode()) ||
//                Objects.equals(orderStatus, ShOrderStatusEnum.INITIAL.getCode()) ||
//                Objects.equals(orderStatus, ShOrderStatusEnum.SETTLEMENT.getCode())) {
//            return ChangeStatusEnum.NOT_CHANGE.getCode();
//        } else if (Objects.equals(orderStatus, ShOrderStatusEnum.CANCEL.getCode())) {
//            //如果是撤销单，则需要判断之前的订单有没有结算，没有结算则变更状态为未变更，已结算则为已变更
//            OrderRecordVO orderRecordVO = orderRecordService.queryByOrderId(orderId);
//            if (orderRecordVO != null) {
//                if (orderRecordVO.getOrderClassify().equals(ClassifyEnum.SETTLED.getCode()) ||
//                        orderRecordVO.getOrderClassify().equals(ClassifyEnum.RESETTLED.getCode())) {
//                    return ChangeStatusEnum.CHANGED.getCode();
//                }
//            }
//            return ChangeStatusEnum.NOT_CHANGE.getCode();
//        }
//
//        return ChangeStatusEnum.CHANGED.getCode();
//    }
//
//
//    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
//
//        venueDetailVO.setMerchantNo(venueDetailVO.getMerchantNo());
//        venueDetailVO.setAesKey(venueDetailVO.getAesKey());
//        long timeStamp = System.currentTimeMillis();
//        ShApiLoginReq shApiLoginReq = ShApiLoginReq.builder()
//                .userName(venueUserAccount)
//                .build();
//        String hashStr = venueDetailVO.getMerchantNo() + timeStamp + venueDetailVO.getAesKey();
//        shApiLoginReq.setHashSign(hashStr);
//        shApiLoginReq.setTimeStamp(timeStamp);
//        shApiLoginReq.setMd5Sign(new HashMap<>(Map.of(
//                "userName", venueUserAccount,
//                "merchantNo", venueDetailVO.getMerchantNo(),
//                "apiSign", venueDetailVO.getAesKey()
//        )));
//
//        shApiLoginReq.setMerchantNo(venueDetailVO.getMerchantNo());
//        String url = venueDetailVO.getApiUrl() + SHConstantApi.LOGOUT;
//
//        try {
//            String response = HttpClientHandler.post(url, JSON.toJSONString(shApiLoginReq));
//            log.info("SH-退出登录游戏response={}", response);
//            if (StringUtils.isBlank(response)) {
//                return ResponseVO.success(false);
//            }
//            return ResponseVO.success(true);
//        } catch (Exception e) {
//            log.info("SH-退出登录游戏异常", e);
//        }
//        return ResponseVO.success(false);
//    }
//
//}
