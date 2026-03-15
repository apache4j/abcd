package com.cloud.baowang.play.game.dbPandaSport;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.enums.DbPanDaSportDefaultException;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.dbPanDaSport.*;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.base.DbPanDaBaseRes;
import com.cloud.baowang.play.api.vo.dbPanDaSport.*;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderDetail;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderRecordRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.order.client.OrderMultipleBetVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.DbSportTransferRecordDetailVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.dbPandaSport.constant.DbPanDaSportConstant;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.DbSportTransferRecordPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.DbSportTransferRecordDetailPO;
import com.cloud.baowang.play.service.DbSportTransferRecordService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.TransferRecordDetailService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_PANDA_SPORT)
@AllArgsConstructor
public class DbPanDaSportServiceImpl extends GameBaseService implements GameService {

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final VenueInfoService venueInfoService;

    private final UserCoinApi userCoinApi;

    private final DbSportTransferRecordService dbSportTransferRecordService;

    private final TransferRecordDetailService transferRecordDetailService;

    private final SiteApi siteApi;

    private final OrderRecordProcessService orderRecordProcessService;

    private String getSign(String userName, String merchantCode, String timestamp, String key) {
        String str = userName + "&" + merchantCode + "&" + timestamp;
        return MD5Util.md5(MD5Util.md5(str) + "&" + key);
    }


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + DbPanDaSportConstant.REGISTER;

        DbPanDaSportCurrencyEnum dbDjCurrencyEnum = DbPanDaSportCurrencyEnum.byPlatCurrencyCode(casinoMemberVO.getCurrencyCode());

        if (dbDjCurrencyEnum == null) {
            log.info("参数异常创建游戏失败:{}", VenueEnum.DB_PANDA_SPORT.getVenueName());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        String unixSeconds = String.valueOf(System.currentTimeMillis());

        String sign = getSign(casinoMemberVO.getVenueUserAccount(), venueInfoVO.getMerchantNo(), unixSeconds, venueInfoVO.getMerchantKey());

        Map<String, String> params = new HashMap<>();
        params.put("userName", casinoMemberVO.getVenueUserAccount());
        params.put("nickname", casinoMemberVO.getVenueUserAccount());
        params.put("timestamp", unixSeconds);
        params.put("merchantCode", venueInfoVO.getMerchantNo());
        params.put("currency", String.valueOf(dbDjCurrencyEnum.getCode()));
        params.put("signature", sign);
        Map<String, String> header = new HashMap<>();
        String resp = HttpClientHandler.post(url, header, params);
        if (StringUtils.isBlank(resp)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        JSONObject resJson = JSONObject.parseObject(resp);
        if (resJson == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        if (resJson.containsKey("status") && resJson.getBoolean("status") && resJson.containsKey("code")
                && resJson.getString("code").equals(DbPanDaSportResultCodeEnum.SUCCESS.getCode())) {
            return ResponseVO.success();
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueInfoVO, String venueUserAccount) {

        String url = venueInfoVO.getApiUrl() + DbPanDaSportConstant.LOGOUT_GAME;
        String unixSeconds = String.valueOf(System.currentTimeMillis());


        String str = venueInfoVO.getMerchantNo() + "&" + venueUserAccount + "&" + unixSeconds;
        String sign = MD5Util.md5(MD5Util.md5(str) + "&" + venueInfoVO.getMerchantKey());

        Map<String, String> params = new HashMap<>();
        params.put("userName", venueUserAccount);
        params.put("timestamp", unixSeconds);
        params.put("merchantCode", venueInfoVO.getMerchantNo());
        params.put("signature", sign);

        Map<String, String> header = new HashMap<>();
        String rsp = HttpClientHandler.post(url, header, params);
        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.success(false);
        }

        JSONObject resJson = JSONObject.parseObject(rsp);
        if (resJson == null) {
            return ResponseVO.success(false);
        }

        if (resJson.containsKey("status")) {
            if (!resJson.getBoolean("status") && resJson.containsKey("code")
                    && resJson.getString("code").equals(DbPanDaSportResultCodeEnum.PLAYER_NAME_DUPLICATE.getCode())) {
                return ResponseVO.success(true);
            }

            if (resJson.getBoolean("status") && resJson.containsKey("code")
                    && resJson.getString("code").equals(DbPanDaSportResultCodeEnum.SUCCESS.getCode())) {
                return ResponseVO.success(true);
            }
        }


        return ResponseVO.success(false);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + DbPanDaSportConstant.LOGIN;
        DbPanDaSportCurrencyEnum dbDjCurrencyEnum = DbPanDaSportCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());

        if (dbDjCurrencyEnum == null) {
            log.info("参数异常创建游戏失败:{}", VenueEnum.DB_PANDA_SPORT.getVenueName());
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        String unixSeconds = String.valueOf(System.currentTimeMillis());


        String terminal = "mobile";

        Integer deviceType = CurrReqUtils.getReqDeviceType();
        if (DeviceType.PC.getCode().equals(deviceType)) {
            terminal = "pc";
        }

        String str = venueInfoVO.getMerchantNo() + "&" + casinoMemberVO.getVenueUserAccount() + "&" + terminal + "&" + unixSeconds;
        String sign = MD5Util.md5(MD5Util.md5(str) + "&" + venueInfoVO.getMerchantKey());

        Map<String, String> params = new HashMap<>();
        params.put("userName", casinoMemberVO.getVenueUserAccount());
        params.put("merchantCode", venueInfoVO.getMerchantNo());
        params.put("terminal", terminal);
        params.put("currency", String.valueOf(dbDjCurrencyEnum.getCode()));
        params.put("timestamp", unixSeconds);
        params.put("signature", sign);
        params.put("language", DbPanDaSportLangEnum.byPlatLang(CurrReqUtils.getLanguage()));
        Map<String, String> header = new HashMap<>();
        String resp = HttpClientHandler.post(url, header, params);
        if (StringUtils.isBlank(resp)) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        JSONObject resJson = JSONObject.parseObject(resp);
        if (resJson == null) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        if (resJson.containsKey("status") && resJson.getBoolean("status") && resJson.containsKey("code")
                && resJson.getString("code").equals(DbPanDaSportResultCodeEnum.SUCCESS.getCode())) {
            JSONObject data = resJson.getJSONObject("data");
            if (data != null) {
                GameLoginVo loginVo = GameLoginVo.builder()
                        .source(data.getString("loginUrl"))
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(casinoMemberVO.getVenueUserAccount())
                        .venueCode(VenueEnum.DB_PANDA_SPORT.getVenueCode())
                        .build();
                return ResponseVO.success(loginVo);
            }

        }

        return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
    }

    /**
     * 拉取注单
     */
    private ResponseVO<?> toPullEleRecord(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        queryTransferList(venueDetailVO);
        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.DB_PANDA_SPORT.getVenueCode());
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        String merchantKey = venueDetailVO.getMerchantKey();
        String merchantNo = venueDetailVO.getMerchantNo();
        String startTime = String.valueOf(venuePullParamVO.getStartTime());
        String endTime = String.valueOf(venuePullParamVO.getEndTime());
        Long now = System.currentTimeMillis();

        String sign = MD5Util.md5(MD5Util.md5(merchantNo + "&" + startTime + "&" + endTime + "&" + now) + "&" + merchantKey);

        int pageNumber = 1;
        while (true) {
            Map<String, String> requestParameters = new HashMap<>();
            requestParameters.put("merchantCode", merchantNo);
            requestParameters.put("timestamp", String.valueOf(now));
            requestParameters.put("startTime", startTime);
            requestParameters.put("endTime", endTime);
            requestParameters.put("signature", sign);
            requestParameters.put("pageNum", Integer.toString(pageNumber));
            requestParameters.put("pageSize", "1000");
            String url = venueDetailVO.getBetUrl() + DbPanDaSportConstant.ORDER_RECORD;
            Map<String, String> headers = new HashMap<>();
            String response = HttpClientHandler.post(url, headers, requestParameters);

            if (ObjectUtil.isEmpty(response)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            JSONObject resultJson = JSON.parseObject(response);
            if (resultJson == null) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            if (!resultJson.containsKey("status")) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            List<OrderRecordVO> list = new ArrayList<>();

            String state = resultJson.getString("status");
            if (StringUtils.isBlank(state)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            if (!resultJson.getBoolean("status")) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            if (!resultJson.containsKey("data")) {
                return ResponseVO.success();
            }

            JSONObject data = resultJson.getJSONObject("data");
            if (!data.containsKey("list")) {
                return ResponseVO.success();
            }

            JSONArray jsonArray = data.getJSONArray("list");
            if (jsonArray.isEmpty()) {
                return ResponseVO.success();
            }
            List<DbPanDaSportOrderRecordRes> betList = jsonArray.toJavaList(DbPanDaSportOrderRecordRes.class);

//            DbDjOrderRecordRes recordRes = JSON.parseObject(resultJson.toJSONString(), DbDjOrderRecordRes.class);
            if (CollectionUtil.isEmpty(betList)) {
                log.info("betArray无数据");
                return ResponseVO.success();
            }


            // 场馆用户关联信息
            List<String> thirdUserName = betList.stream().map(DbPanDaSportOrderRecordRes::getUserName).distinct().toList();
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


            for (DbPanDaSportOrderRecordRes item : betList) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(item.getUserName());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), item.getUserName());
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
                OrderRecordVO recordVO = parseRecords(venueDetailVO, item, userInfoVO, userLoginInfoVO);
                recordVO.setVenueType(gameTypeId);
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                list.add(recordVO);
            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            pageNumber++;
            list.clear();
        }
        return ResponseVO.success();
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, DbPanDaSportOrderRecordRes orderResponseVO, UserInfoVO userInfoVO,
                                       UserLoginInfoVO userLoginInfoVO) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getOrderAmount());
        BigDecimal validBetAmount = computerValidBetAmount(orderResponseVO.getOrderAmount(), orderResponseVO.getProfitAmount(), VenueTypeEnum.SPORTS);
        recordVO.setValidAmount(validBetAmount);
        recordVO.setWinLossAmount(orderResponseVO.getProfitAmount());
        recordVO.setPayoutAmount(orderResponseVO.getProfitAmount());

//        recordVO.setBetContent(orderResponseVO.getCategory());
//        recordVO.setPlayInfo(orderResponseVO.getCategory());
        recordVO.setBetTime(orderResponseVO.getCreateTime());
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUserName());
        String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

        if (userLoginInfoVO != null) {
            recordVO.setBetIp(betIp);
            recordVO.setDeviceType(deviceType);
        }
        Integer gameId = orderResponseVO.getMatchCode();
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(String.valueOf(orderResponseVO.getSeriesType()));
//        recordVO.setGameNo(String.valueOf(orderResponseVO.getRound()));
        recordVO.setThirdGameCode(String.valueOf(gameId));
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getOrderNo()));
        recordVO.setTransactionId(String.valueOf(orderResponseVO.getOrderNo()));

        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        DbPanDaSportOrderStatusEnum statusEnum = DbPanDaSportOrderStatusEnum.fromCode(orderResponseVO.getOrderStatus());
        if (statusEnum != null) {
            Map<Integer, Integer> statusMap = Maps.newHashMap();
            statusMap.put(DbPanDaSportOrderStatusEnum.PENDING.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
            statusMap.put(DbPanDaSportOrderStatusEnum.SETTLED.getCode(), OrderStatusEnum.SETTLED.getCode());
            statusMap.put(DbPanDaSportOrderStatusEnum.CANCELLED_MANUAL.getCode(), OrderStatusEnum.MANUAL_CANCEL.getCode());
            statusMap.put(DbPanDaSportOrderStatusEnum.CONFIRMING.getCode(), OrderStatusEnum.NOT_SETTLE.getCode());
            statusMap.put(DbPanDaSportOrderStatusEnum.RISK_REJECT.getCode(), OrderStatusEnum.RISK_REJECT.getCode());
            statusMap.put(DbPanDaSportOrderStatusEnum.CANCELLED_EVENT.getCode(), OrderStatusEnum.GAME_REVOKE.getCode());
            recordVO.setOrderStatus(statusMap.get(statusEnum.getCode()));
            recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(statusMap.get(statusEnum.getCode())));
        }
        recordVO.setSettleTime(orderResponseVO.getSettleTime()==null? orderResponseVO.getModifyTime() : orderResponseVO.getSettleTime());

//        if (ObjectUtil.isNotEmpty(orderResponseVO.getPayoff_at()) && !orderResponseVO.getPayoff_at().equals("Invalid date")) {
//            recordVO.setFirstSettleTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getPayoff_at()));
//        }
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setResultList(null);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));


        List<DbPanDaSportOrderDetail> detailList = orderResponseVO.getDetailList();
        StringBuilder matchNameBuild = new StringBuilder();
        for (DbPanDaSportOrderDetail item : detailList) {
            if(ObjectUtil.isNotEmpty(item.getMatchName())){
                matchNameBuild.append(item.getMatchName()).append(",");
            }
        }
        recordVO.setEventInfo(matchNameBuild.toString());//联赛名字

        return recordVO;
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        return toPullEleRecord(venueInfoVO, venuePullParamVO);
    }

    private VenueInfoVO getVenueInfo(){
        //获取密钥信息
        List<VenueInfoVO> venueInfoVOList = venueInfoService.getAdminVenueInfoByVenueCodeList(VenueEnum.DB_PANDA_SPORT.getVenueCode());
        if(CollectionUtil.isEmpty(venueInfoVOList)){
            log.info("未获取到密钥配置");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }
        return venueInfoVOList.get(0);
    }

    public DbPanDaBaseRes<String> getBalance(DbPanDaBalanceReq req) {
        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUserName());
        UserInfoVO userInfoVOS = getByUserId(userId);
        if (userInfoVOS == null) {
            log.info("用户不存在");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_NOT_FOUND);
        }

        if (!req.valid()) {
            log.info("参数校验失败");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }
        VenueInfoVO info = getVenueInfoByMerchant(req.getMerchantCode(), VenuePlatformConstants.DB_PANDA_SPORT);
        if(info == null){
            log.info("获取用于余额,数据库商户配置对不上:{},{}",req.getMerchantCode(),req.getMerchantCode());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SIGN_VERIFY_FAIL);
        }

        if(!info.getMerchantNo().equals(req.getMerchantCode())){
            log.info("获取用于余额,数据库商户配置对不上:{},{}",info.getMerchantNo(),req.getMerchantCode());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SIGN_VERIFY_FAIL);
        }

        String md5Str = info.getMerchantNo() + "&" + req.getUserName() + "&" + req.getTimestamp();
        String sign = MD5Util.md5(MD5Util.md5(md5Str) + "&" + info.getMerchantKey());
        if (!sign.equals(req.getSignature())) {
            log.info("获取用于余额,签名解析异常:{},{}", sign, req.getSignature());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (userCenterCoin == null) {
            return DbPanDaBaseRes.success(String.valueOf(BigDecimal.ZERO));
        }
        return DbPanDaBaseRes.success(String.valueOf(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN)));
    }

    @DistributedLock(name = RedisConstants.DB_PANDA_SPORT_COIN_LOCK, unique = "#req.transferId", waitTime = 3, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public DbPanDaBaseRes<DbPanDaSportBetResVO> transfer(DbPanDaSportBetReqVO req) {

        if (!req.valid()) {
            log.info("参数缺失");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        String bizType = req.getBizType();
        DbPanDaSportBizTypeEnum reqBizTypeEnum = DbPanDaSportBizTypeEnum.fromCode(Integer.valueOf(bizType));

        if (reqBizTypeEnum == null) {
            log.info("转账投注类型异常");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        String transferType = req.getTransferType();
        DbPanDaSportTransferTypeEnum transferTypeEnum = DbPanDaSportTransferTypeEnum.fromCode(Integer.valueOf(transferType));
        if (transferTypeEnum == null) {
            log.info("转账类型异常");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        if (!reqBizTypeEnum.getTransferTypeEnum().equals(transferTypeEnum)) {
            log.info("投注类型与转账类型对不上");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        //获取密钥信息
        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(req.getMerchantCode(), VenuePlatformConstants.DB_PANDA_SPORT);
        if(venueInfoVO == null){
            log.info("获取用于余额,数据库商户配置对不上:{},{}",req.getMerchantCode(),req.getMerchantCode());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SIGN_VERIFY_FAIL);
        }

        if(!venueInfoVO.getMerchantNo().equals(req.getMerchantCode())){
            log.info("加扣款,数据库商户配置对不上:{},{}",venueInfoVO.getMerchantNo(),req.getMerchantCode());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SIGN_VERIFY_FAIL);
        }

        String md5Str = req.getUserName() + "&" + req.getBizType() + "&" + venueInfoVO.getMerchantNo() + "&" + req.getTransferId() +
                "&" + req.getAmount() + "&" + req.getTransferType() + "&" + req.getTimestamp();
        String sign = MD5Util.md5(MD5Util.md5(md5Str) + "&" + venueInfoVO.getMerchantKey());
        if(!sign.equals(req.getSignature())){
            log.info("加扣款,签名解析异常:{},{}",sign,req.getSignature());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUserName());
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_NOT_FOUND);
        }

        //下注 扣款 的情况下要校验这个用户是不是被锁了
        if (DbPanDaSportTransferTypeEnum.DEDUCT.equals(transferTypeEnum)
                && UserStatusEnum.GAME_LOCK.getCode().equals(userInfoVO.getAccountStatus())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_DISABLED);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (ObjectUtils.isEmpty(userCenterCoin)) {
            log.info("{},用户没有钱包 ", VenueEnum.DB_PANDA_SPORT.getVenueCode());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_BALANCE_NOT_ENOUGH);
        }

        BigDecimal totalAmount = req.getAmount();
        //扣费需要校验用户余额是否足够
        if (DbPanDaSportTransferTypeEnum.DEDUCT.equals(transferTypeEnum)) {

            if(!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())){
                log.info("{}  场馆未开启", VenueEnum.DB_PANDA_SPORT.getVenueName());
                throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_BALANCE_NOT_ENOUGH);
            }

            // 余额如果小于0，说明余额不足
            BigDecimal balance = userCenterCoin.getTotalAmount().subtract(totalAmount);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}  的总金额,对比余额  余额不足 userAccount:[{}],result:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), userCenterCoin);
                throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PLAYER_BALANCE_NOT_ENOUGH);
            }
        }

        Long count = dbSportTransferRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(DbSportTransferRecordPO.class)
                .eq(DbSportTransferRecordPO::getVenueCode, VenueEnum.DB_PANDA_SPORT.getVenueCode())
                .eq(DbSportTransferRecordPO::getTransId, req.getTransferId())
        );

        if (count > 0) {
            log.info("{}  订单重复执行 userAccount:[{}],id:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.DUPLICATE_SUBMIT);
        }
        List<DbPanDaBetDetailVO> orderStr = req.getOrderList();

        BigDecimal listTotalAmount = orderStr.stream()
                .map(DbPanDaBetDetailVO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (listTotalAmount.compareTo(totalAmount) != 0) {
            log.info("{}  订单金额与汇总金额不一致 userAccount:[{}],id:{},汇总金额:{},请求金额:{}",
                    VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId(), listTotalAmount, totalAmount);
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }
        List<DbSportTransferRecordDetailPO> addDetail = Lists.newArrayList();

        List<String> orderList = orderStr.stream().map(DbPanDaBetDetailVO::getOrderNo).toList();
        List<DbSportTransferRecordDetailPO> detailList = transferRecordDetailService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(DbSportTransferRecordDetailPO.class)
                        .in(DbSportTransferRecordDetailPO::getBetId, orderList));
        if (CollectionUtil.isNotEmpty(detailList)) {
            DbSportTransferRecordDetailPO detailPO = detailList.get(0);
            //存在未确认的单.订单状态是一笔一笔的来.如果还存在未确认的状态,那这笔单就不可以进入下个订单状态,必须从未确认到已确认后才可以进入下一次状态
            if (DbPanDaBetTypeEnum.UN_CONFIRMED.getCode().equals(detailPO.getType())) {
                log.info("{}  存在未确认的单 id:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), orderList);
                throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
            }

            if (DbPanDaSportTransferTypeEnum.ADD.equals(transferTypeEnum)) {
                if(detailPO.getSettleCount() >= 3){
                    log.info("{}  一笔单不可以重复加扣超过3次,防止多次给用户加钱 id:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), orderList);
                    throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
                }
            }

            //重制订单状态
            for (DbPanDaBetDetailVO item : orderStr) {
                DbSportTransferRecordDetailPO recordDetailPO = DbSportTransferRecordDetailPO
                        .builder()
                        .type(DbPanDaBetTypeEnum.UN_CONFIRMED.getCode())
                        .orderStatus(reqBizTypeEnum.getCode())
                        .transferType(reqBizTypeEnum.getTransferTypeEnum().getCode())
                        .amount(item.getAmount())
                        .build();
                transferRecordDetailService.getBaseMapper().update(recordDetailPO, Wrappers.lambdaQuery(DbSportTransferRecordDetailPO.class)
                        .eq(DbSportTransferRecordDetailPO::getBetId, item.getOrderNo()));
            }

        } else {
            //订单状态
            for (DbPanDaBetDetailVO item : orderStr) {
                if (CollectionUtil.isEmpty(detailList)) {
                    addDetail.add(DbSportTransferRecordDetailPO
                            .builder()
                            .venueCode(VenueEnum.DB_PANDA_SPORT.getVenueCode())
                            .siteCode(userInfoVO.getSiteCode())
                            .userId(userInfoVO.getUserId())
                            .betId(item.getOrderNo())
                            .amount(item.getAmount())
                            .orderStatus(reqBizTypeEnum.getCode())
                            .type(DbPanDaBetTypeEnum.UN_CONFIRMED.getCode())
                            .transferType(transferTypeEnum.getCode())
                            .build());
                }
            }
        }


        //父订单
        List<DbSportTransferRecordPO> addRecord = Lists.newArrayList();
        for (DbPanDaBetDetailVO item : orderStr) {
            DbSportTransferRecordPO recordPO = DbSportTransferRecordPO.builder()
                    .venueCode(VenueEnum.DB_PANDA_SPORT.getVenueCode())
                    .siteCode(userInfoVO.getSiteCode())
                    .userId(userInfoVO.getUserId())
                    .transId(req.getTransferId())
                    .betId(item.getOrderNo())
                    .amount(item.getAmount())
                    .orderStatus(reqBizTypeEnum.getCode())
                    .transferType(transferTypeEnum.getCode())
                    .amount(req.getAmount())
                    .build();
            addRecord.add(recordPO);
        }

        if (!dbSportTransferRecordService.saveBatch(addRecord)) {
            log.info("{}  父单执行异常 userAccount:[{}],id:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }

        if (CollectionUtil.isNotEmpty(addDetail)) {
            if (!transferRecordDetailService.saveBatch(addDetail)) {
                log.info("{}  子单执行异常 userAccount:[{}],id:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId());
                throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
            }
        }

        //如果是扣款则进行冻结
        //如果是加款则不处理,等第二次收到加款通知接口的时候直接加款
        if (DbPanDaSportTransferTypeEnum.DEDUCT.equals(transferTypeEnum)) {
            for (DbPanDaBetDetailVO item : orderStr) {
                UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                userCoinAddVO.setOrderNo(item.getOrderNo());
                userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
                userCoinAddVO.setUserId(userInfoVO.getUserId());
                userCoinAddVO.setCoinValue(req.getAmount());
                userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.FREEZE.getCode());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET_FREEZD.getCode());
                userCoinAddVO.setVenueCode(VenuePlatformConstants.DB_PANDA_SPORT);
                userCoinAddVO.setThirdOrderNo(item.getOrderNo());
                CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
                if (!recordResultVO.getResult() || !UpdateBalanceStatusEnums.SUCCESS.equals(recordResultVO.getResultStatus())) {
                    log.info("{}  下注冻结失败, userAccount:[{}],id:{},orderNo:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(),
                            userInfoVO.getUserId(), req.getTransferId(), item.getOrderNo());
                    throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
                }
            }
        }
        UserCoinWalletVO walletVO = getUserCenterCoin(userId);
        if (walletVO == null) {
            log.info("调用服务失败 获取中心钱包余额");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }
        return DbPanDaBaseRes.success(DbPanDaSportBetResVO.builder().balance(walletVO.getTotalAmount()).build());
    }


    /**
     * 确认订单状态
     */
    public DbPanDaBaseRes<Void> confirmBet(DbPanDaConfirmBetReq req) {
        if (!req.valid()) {
            log.info("参数缺失");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        DbPanDaSuccessTypeEnum successType = DbPanDaSuccessTypeEnum.fromCode(req.getStatus());
        if (successType == null) {
            log.info("参数异常");
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }

        List<DbPanDaBetDetailVO> orderList = req.getBetOrderList();
        List<String> orderIds = orderList.stream().map(DbPanDaBetDetailVO::getOrderNo).toList();

        List<DbSportTransferRecordDetailPO> detailList = transferRecordDetailService.getBaseMapper().selectList(
                Wrappers.lambdaQuery(DbSportTransferRecordDetailPO.class)
                        .eq(DbSportTransferRecordDetailPO::getType, DbPanDaBetTypeEnum.UN_CONFIRMED.getCode())
                        .in(DbSportTransferRecordDetailPO::getBetId, orderIds));

        //如果是失败单.并且库中不存在,说明这笔单在加扣款的时候呗拒绝了.这种情况下直接返回成功
        if(DbPanDaSuccessTypeEnum.FAIL.equals(successType) && CollectionUtil.isEmpty(detailList)){
            return DbPanDaBaseRes.success();
        }

        if (CollectionUtil.isEmpty(detailList)) {
            log.info("单不存在或者已被处理:{}", orderList);
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.GAME_ORDER_NOT_FOUND);
        }
        if (detailList.size() != orderList.size()) {
            log.info("订单数量对不上,:{}", orderList);
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.DUPLICATE_SUBMIT);
        }

        //订单的总额
        BigDecimal dbTotalAmount = detailList.stream()
                .map(DbSportTransferRecordDetailPO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        DbSportTransferRecordDetailPO detailPO = detailList.get(0);
        DbPanDaSportTransferTypeEnum transferTypeEnum = DbPanDaSportTransferTypeEnum.fromCode(detailPO.getTransferType());
        UserInfoVO userInfoVO = getByUserId(detailPO.getUserId());

        if(userInfoVO == null || userInfoVO.getUserId() == null){
            log.info("调用userApi服务失败,:{}", detailPO.getUserId());
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
        }

        //by mufan  因为派彩也会走这里如果 判断走 订单请求进来的金额则会一致包 订单金额与汇总金额不一致 导致走不下去 start
        //订单请求进来的金额
        BigDecimal reqTotalAmount = orderList.stream()
                .map(DbPanDaBetDetailVO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (reqTotalAmount.compareTo(dbTotalAmount) != 0) {
            log.info("{}  订单金额与汇总金额不一致 userAccount:[{}],id:{},汇总金额:{},请求金额:{}",
                    VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId(), dbTotalAmount, reqTotalAmount);
            throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.PARAM_INVALID);
        }
        //by mufan  因为派彩也会走这里如果 判断走 订单请求进来的金额则会一致包 订单金额与汇总金额不一致 导致走不下去 end

        // 成功处理
        if (DbPanDaSuccessTypeEnum.SUCCESS.equals(successType)) {
            for (DbPanDaBetDetailVO item : orderList) {
                //派彩金额为0直接跳过
                if(Objects.isNull(item.getAmount())|| BigDecimal.ZERO.compareTo(item.getAmount()) == 0 ) {
                    log.error("DbPanDa success orderNo:{} , amount:{}",item.getOrderNo(),item.getAmount());
                    continue;
                }

                LambdaUpdateWrapper<DbSportTransferRecordDetailPO> updateWrapper = Wrappers.lambdaUpdate(DbSportTransferRecordDetailPO.class)
                        .eq(DbSportTransferRecordDetailPO::getBetId, item.getOrderNo())
                        .eq(DbSportTransferRecordDetailPO::getType, DbPanDaBetTypeEnum.UN_CONFIRMED.getCode())
                        .set(DbSportTransferRecordDetailPO::getType, DbPanDaBetTypeEnum.CONFIRMED.getCode());


                UserCoinAddVO vo;
                if (DbPanDaSportTransferTypeEnum.ADD.equals(transferTypeEnum)) { // 加款
                    vo = buildUserCoinAddVO(item, userInfoVO,
                            CoinBalanceTypeEnum.INCOME, WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT,
                            WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT,WalletEnum.CoinTypeEnum.GAME_PAYOUT);
                    vo.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
                    //只要是加款 都要累计.因为重试次数是有限制的.当达到某个值的时候拒绝加款
                    updateWrapper.setSql("settle_count = settle_count +1 ");
                } else { // 扣款
                    vo = buildUserCoinAddVO(item, userInfoVO,
                            CoinBalanceTypeEnum.EXPENSES, WalletEnum.BusinessCoinTypeEnum.GAME_BET,
                            WalletEnum.CustomerCoinTypeEnum.GAME_BET,WalletEnum.CoinTypeEnum.GAME_BET);
                    vo.setFreezeFlag(FreezeFlagEnum.UNFREEZE.getCode());
                    vo.setAccountCoinType(AccountCoinTypeEnums.GAME_BET_CONFIRM.getCode());
                }

                CoinRecordResultVO result = toUserCoinHandle(vo);
                if (!result.getResult() || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                    log.info("调用加款失败,{}  {}, userAccount:[{}],id:{},orderNo:{}",
                            VenueEnum.DB_PANDA_SPORT.getVenueName(),
                            DbPanDaSportTransferTypeEnum.ADD.equals(transferTypeEnum) ? "确认派彩加款" : "确认扣款",
                            userInfoVO.getUserId(), req.getTransferId(), item.getOrderNo());
                    throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
                }

                // 更新订单状态为已确认
                int count = transferRecordDetailService.getBaseMapper().update(null, updateWrapper);
                if (count <= 0) {
                    log.info("{}  确认扣款单失败, userAccount:[{}],id:{},orderNo:{}",
                            VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId(), orderIds);
                    throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
                }
            }


        } else {
            // 失败处理
            if (DbPanDaSportTransferTypeEnum.DEDUCT.equals(transferTypeEnum)) {
                for (DbPanDaBetDetailVO item : orderList) {
                    UserCoinAddVO vo = buildUserCoinAddVO(item, userInfoVO,
                            CoinBalanceTypeEnum.UN_FREEZE, WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT,
                            WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT,WalletEnum.CoinTypeEnum.GAME_PAYOUT);
                    vo.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
                    CoinRecordResultVO result = toUserCoinHandle(vo);
                    if (!result.getResult() || !UpdateBalanceStatusEnums.SUCCESS.equals(result.getResultStatus())) {
                        log.info("{}  解冻, userAccount:[{}],id:{},orderNo:{}",
                                VenueEnum.DB_PANDA_SPORT.getVenueName(),
                                userInfoVO.getUserId(), req.getTransferId(), item.getOrderNo());
                        throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
                    }
                }
            }

            // 更新订单状态为已取消
            DbSportTransferRecordDetailPO upRecord = DbSportTransferRecordDetailPO.builder()
                    .type(DbPanDaBetTypeEnum.CANCELED.getCode())
                    .build();

            int count = transferRecordDetailService.getBaseMapper().update(
                    upRecord,
                    Wrappers.lambdaQuery(DbSportTransferRecordDetailPO.class)
                            .in(DbSportTransferRecordDetailPO::getBetId, orderIds)
                            .eq(DbSportTransferRecordDetailPO::getType, DbPanDaBetTypeEnum.UN_CONFIRMED.getCode()));

            if (count <= 0) {
                log.info("{}  取消加款单失败, userAccount:[{}],id:{},orderNo:{}",
                        VenueEnum.DB_PANDA_SPORT.getVenueName(), userInfoVO.getUserId(), req.getTransferId(), orderIds);
                throw new DbPanDaSportDefaultException(DbPanDaSportResultCodeEnum.SYSTEM_ERROR);
            }
        }
        return DbPanDaBaseRes.success();
    }

    /**
     * 构建 UserCoinAddVO
     */
    private UserCoinAddVO buildUserCoinAddVO(DbPanDaBetDetailVO item, UserInfoVO userInfoVO,
                                             CoinBalanceTypeEnum balanceType,
                                             WalletEnum.BusinessCoinTypeEnum businessType,
                                             WalletEnum.CustomerCoinTypeEnum customerType,
                                             WalletEnum.CoinTypeEnum coinTypeEnum) {
        UserCoinAddVO vo = new UserCoinAddVO();
        vo.setOrderNo(item.getOrderNo());
        vo.setCurrency(userInfoVO.getMainCurrency());
        vo.setUserId(userInfoVO.getUserId());
        vo.setCoinValue(item.getAmount());
        vo.setCoinType(coinTypeEnum.getCode());
        vo.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        vo.setBalanceType(balanceType.getCode());
        vo.setBusinessCoinType(businessType.getCode());
        vo.setCustomerCoinType(customerType.getCode());
        vo.setVenueCode(VenuePlatformConstants.DB_PANDA_SPORT);
        vo.setThirdOrderNo(item.getOrderNo());
        return vo;
    }


    /**
     * 同步三方未确认的订单
     */
    private void queryTransferList(VenueInfoVO venueInfoVO) {
        List<DbSportTransferRecordDetailVO> recordDetailVOS = transferRecordDetailService.queryUnConfirmErrorOrder();
        if (CollectionUtil.isEmpty(recordDetailVOS)) {
            return;
        }
        try {
            Long now = System.currentTimeMillis();
            String merchantNo = venueInfoVO.getMerchantNo();
            String merchantKey = venueInfoVO.getMerchantKey();
            String url = venueInfoVO.getApiUrl() + DbPanDaSportConstant.QUERY_TRANSFER_LIST;
            log.info("{}:进入获取未确认的交易记录", VenueEnum.DB_PANDA_SPORT.getVenueName());


            List<QueryTransferResVO> queryTransferResVOS = Lists.newArrayList();

            for (DbSportTransferRecordDetailVO item : recordDetailVOS) {
                String userId = item.getUserId();
                String userName = venueUserAccountConfig.addVenueUserAccountPrefix(userId);
                String startTime = item.getCreatedTime().toString();
                String endTime = String.valueOf(now);
                String sign = MD5Util.md5(MD5Util.md5(merchantNo + "&" + userName + "&" + startTime + "&" + endTime + "&" + now) + "&" + merchantKey);

                Map<String, String> requestParameters = new HashMap<>();
                requestParameters.put("userName", userName);
                requestParameters.put("merchantCode", merchantNo);
                requestParameters.put("startTime", startTime);
                requestParameters.put("endTime", endTime);
                requestParameters.put("timestamp", String.valueOf(now));
                requestParameters.put("signature", sign);
                Map<String, String> headers = new HashMap<>();
                String response = HttpClientHandler.post(url, headers, requestParameters);
                if (ObjectUtil.isEmpty(response)) {
                    return;
                }

                JSONObject resultJson = JSON.parseObject(response);
                if (resultJson == null) {
                    return;
                }
                if (!resultJson.containsKey("status") || !resultJson.containsKey("code") || !resultJson.containsKey("data")) {
                    return;
                }
                if (!resultJson.getBoolean("status") || !resultJson.getString("code").equals("0000")) {
                    return;
                }

                JSONObject data = resultJson.getJSONObject("data");

                if (data == null) {
                    return;
                }
                if (!resultJson.containsKey("list")) {
                    return;
                }

                JSONArray jsonArray = data.getJSONArray("list");
                List<QueryTransferResVO> betList = jsonArray.toJavaList(QueryTransferResVO.class);


                Map<String, String> transferMap = Maps.newHashMap();


                for (QueryTransferResVO transferResVO : betList) {

                    if (ObjectUtil.isEmpty(transferResVO.getOrderStr())) {
                        log.info("进入获取未确认的交易记录:{}订单详情为空", transferResVO.getTransferId());
                        continue;
                    }
                    List<DbPanDaBetDetailVO> orderStr = JSONArray.parseArray(transferResVO.getOrderStr(), DbPanDaBetDetailVO.class);
                    BigDecimal listTotalAmount = orderStr.stream()
                            .map(DbPanDaBetDetailVO::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (listTotalAmount.compareTo(transferResVO.getAmount()) != 0) {
                        log.info("{}  进入获取未确认的交易记录,订单金额与汇总金额不一致 userAccount:[{}],id:{},汇总金额:{},请求金额:{}",
                                VenueEnum.DB_PANDA_SPORT.getVenueName(), transferResVO.getUserId(), transferResVO.getTransferId(),
                                listTotalAmount, transferResVO.getAmount());
                        continue;
                    }

                    //重复的单不处理
                    if (transferMap.containsKey(transferResVO.getTransferId())) {
                        continue;
                    }
                    transferMap.put(transferResVO.getTransferId(), transferResVO.getTransferId());
                    queryTransferResVOS.add(transferResVO);
                }
            }


            for (DbSportTransferRecordDetailVO item : recordDetailVOS) {
                Map<String, QueryTransferResVO> transferResVOMap = queryTransferResVOS.stream()
                        .collect(Collectors.toMap(QueryTransferResVO::getTransferId, Function.identity()));
                QueryTransferResVO transferResVO = transferResVOMap.get(item.getTransId());
                if (ObjectUtil.isEmpty(transferResVO)) {
                    continue;
                }

                Integer dbTransferType = item.getTransferType();

                //这笔单数据库中的状态类型跟查的不一样.不处理
                if (!dbTransferType.equals(transferResVO.getTransferType())) {
                    log.info("这笔单数据库中的状态类型跟查的不一样.不处理:{}", item.getTransId());
                    continue;
                }

                BigDecimal amount = item.getAmount();

                if (amount.compareTo(transferResVO.getAmount()) != 0) {
                    log.info("这笔单数据库中的金额查的不一样.不处理:{}", item.getTransId());
                    continue;
                }

                DbPanDaConfirmBetReq confirmBetReq = new DbPanDaConfirmBetReq();
                confirmBetReq.setMerchantCode(venueInfoVO.getMerchantNo());
                confirmBetReq.setTransferId(item.getTransId());
                confirmBetReq.setTimestamp(String.valueOf(System.currentTimeMillis()));
                confirmBetReq.setStatus(transferResVO.getStatus());
                confirmBetReq.setMsg(transferResVO.getMag());
                confirmBetReq.setOrderList(transferResVO.getOrderStr());
            }


        } catch (Exception e) {
            log.error("熊猫体育,同步三方未确认订单异常:", e);
        }
    }

//    public static void main(String[] args) {
//
//        Long now = System.currentTimeMillis();
//
//        String merchantNo = "703981";
//        String merchantKey = "2li*+6_6Htv%ncwYY~_Vng6FHlOV!b";
//        String userName = "Utest_73598778";
//        String startTime = String.valueOf(TimeZoneUtils.getStartOfDayInTimeZone(1759994575512L,"UTC+8"));
//        String endTime = String.valueOf(TimeZoneUtils.getEndOfDayInTimeZone(1759994575512L,"UTC+8"));
//        String sign = MD5Util.md5(MD5Util.md5(merchantNo + "&" + userName + "&" + startTime+ "&" + endTime+ "&" + now) + "&" + merchantKey);
//
//        Map<String, String> requestParameters = new HashMap<>();
//        requestParameters.put("userName", userName);
//        requestParameters.put("merchantCode", merchantNo);
//        requestParameters.put("startTime", startTime);
//        requestParameters.put("endTime", endTime);
//        requestParameters.put("timestamp", String.valueOf(now));
//        requestParameters.put("signature", sign);
//        String url = "https://sandbox-gateway.dbsporxxxw1box.com" + DbPanDaSportConstant.QUERY_TRANSFER_LIST;
//        Map<String, String> headers = new HashMap<>();
//        String response = HttpClientHandler.post(url, headers, requestParameters);
//        System.err.println(response);
//
//
//
//    }
}
