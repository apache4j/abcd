package com.cloud.baowang.play.game.nextSpin.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.nextSpin.NextSpinRespErrEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.nextSpin.*;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.service.NextSpinTransactionRecordService;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.nextSpin.constant.NextSpinConstant;
import com.cloud.baowang.play.game.nextSpin.enums.NextSpinGameTypeEnums;
import com.cloud.baowang.play.game.nextSpin.enums.NextSpinLangCodeEnum;
import com.cloud.baowang.play.game.nextSpin.response.NextSpinResponseVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.nextSpin.params.NextSpinPullBetParams;
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
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.NEXTSPIN)
@AllArgsConstructor
public class NextSpinServiceImpl extends GameBaseService implements GameService {
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

    private final NextSpinTransactionRecordService nextSpinTransactionRecordService;


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String url = venueDetailVO.getApiUrl();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("merchantCode",venueDetailVO.getMerchantNo());
        paramMap.put("serialNo", UUID.randomUUID());
        String param=JSON.toJSONString(paramMap);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type","application/json");
        head.put("API","getDomainList");
        log.info("{} nextSpin-进入游戏request, apiPath: {} , head:{}, request: {}", platformName(),url, JSON.toJSONString(head), param);
        try {
            String response = HttpClientHandler.post(url,head,param);
            log.info("{} nextSpin-进入游戏, url: {} , head:{}, 参数: {} , 返回消息: {}", platformName(),url, JSON.toJSONString(head), param, response);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject != null &&
                    (jsonObject.getIntValue("code") == CommonConstant.business_zero)) {
                String domain=jsonObject.getJSONArray("domains").getString(0);
//                例子
//    https://lobby.gevr8gau.com/ZT9910TEST/auth/?acctId=mufan01test&language=zh_CN
//    &token=fe1a85adc54545d2963b661a22d09c9e&game=aDonkiKong
                domain =domain +"/"+venueDetailVO.getMerchantNo()+"/auth/?acctId="+casinoMemberVO.getVenueUserAccount()
                        +"&language="+ NextSpinLangCodeEnum.fromCode(loginVO.getLanguageCode()).getCode()+"&token="+casinoMemberVO.getCasinoPassword()+"&game="+loginVO.getGameCode();
                GameLoginVo gameLoginVo = GameLoginVo.builder().source(domain)
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(loginVO.getUserAccount())
                        .venueCode(VenueEnum.NEXTSPIN.getVenueCode())
                        .build();
                log.info("{} nextSpin-游戏页面地址信息 {}", platformName(), JSON.toJSONString(gameLoginVo));

                return ResponseVO.success(gameLoginVo);
            }else {
                log.error("nextSpin 登录返回信息错误 {}", response);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }
        } catch (Exception e) {
            log.error("nextSpin-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    /**
     * 注单拉取
     *
     * @param venueInfoVO      场馆信息
     * @param venuePullParamVO 拉单参数
     */
    @Override
    public ResponseVO getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        NextSpinPullBetParams params = new NextSpinPullBetParams();
        params.setStarttime(TimeZoneUtils.formatDate4TimeZone(venuePullParamVO.getStartTime(),shanghaiTimeZone,gmt8TimeZone,DateUtils.yyyyMMddTHHmmss));
        params.setEndtime(TimeZoneUtils.formatDate4TimeZone(venuePullParamVO.getEndTime(),shanghaiTimeZone,gmt8TimeZone,DateUtils.yyyyMMddTHHmmss));
        return getBetRecordListByParams(venueInfoVO, params);
    }

        private String testMsg(){
        String data="{\"serialNo\":\"cf82bfb4-9aa9-4a47-a657-fabd361c6671\",\"code\":0,\"msg\":\"Success\",\"merchantCode\":\"ZT9910TEST\",\"resultCount\":0,\"pageCount\":1,\"list\":[{\"ticketId\":\"202505272329598851461232\",\"acctId\":\"UTEST_43903257\",\"categoryId\":\"SM\",\"gameCode\":\"aDonkiKong\",\"ticketTime\":\"20250710T214158\",\"betIp\":\"182.182.182.182\",\"currency\":\"PTS\",\"betAmount\":1,\"winLoss\":-0.09,\"jackpotAmount\":0,\"result\":\"0:4:0:0,5465:4:0:0,5461:4:0:0,5468:4:0:0,5470:4:0:0,0:4:0:0,5463:4:0:0,5463:4:0:0,5463:4:0:0,5461:4:0:0,5466:4:0:0,5465:3:0:0,5464:3:0:0,5468:4:0:0,0:0:0:0,0:0:0:0,5465:3:0:0,5464:3:0:0,5468:4:0:0,0:0:0:0,0:0:0:0,5461:4:0:0,5468:1:0:0,0:0:0:0,0:0:0:0,0:0:0:0,5462:4:0:0,5462:4:0:0,5465:3:0:0,5468:4:0:0,0:0:0:0,5465:4:0:0,5470:4:0:0,5465:4:0:0,5467:4:0:0,5462:4:0:0\",\"completed\":true,\"luckyDrawId\":0,\"roundId\":0,\"sequence\":0,\"channel\":\"web\",\"balance\":95.8,\"jpWin\":0,\"referenceId\":\"202505272329598851460148\"}]}";
        return data;
    }
    public ResponseVO<Boolean> getBetRecordList(VenueInfoVO venueInfoVO, NextSpinPullBetParams venuePullParamVO) {
        //三方最大支持10000条 默认设定2000担心一下吞吐量过大
        Integer pageSize= 2000;
        Integer pageIndex= 0;
        Integer pageCount= 1;
        String url = venueInfoVO.getApiUrl();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("merchantCode",venueInfoVO.getMerchantNo());
        paramMap.put("serialNo", UUID.randomUUID());
        paramMap.put("beginDate",venuePullParamVO.getStarttime());
        paramMap.put("endDate", venuePullParamVO.getEndtime());
        paramMap.put("pageSize", pageSize);
        String param=null;
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type","application/json");
        head.put("API","getBetHistory");
        List<OrderRecordVO> orderList;
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        try {
            do {
                pageIndex++;
                paramMap.put("pageIndex",pageIndex);
                param=JSON.toJSONString(paramMap);
                log.info("nextSpin-拉单request , apiPath: {} , head:{}, request: {}",url, JSON.toJSONString(head), param);
                String requestData = HttpClientHandler.post(url,head,param);
//                String requestData = testMsg();
                log.info("nextSpin-拉单response, url: {} , head:{}, 参数: {} , response={}",url, JSON.toJSONString(head), param, requestData);
                JSONObject jsonObject = JSONObject.parseObject(requestData);
                pageCount= jsonObject.getIntValue("pageCount");
                if (jsonObject != null &&
                        (jsonObject.getIntValue("code") == CommonConstant.business_zero)) {
                    // 总记录数量
                    Long recordCount = jsonObject.getLong("resultCount");
                    if (Objects.isNull(recordCount)) {
                        break;
                    }
                    // 投注明细列表
                    JSONArray betDetails = jsonObject.getJSONArray("list");;

                    // 解析注单数据
                    orderList = parseOrder(venueInfoVO, betDetails, siteVOMap);
                    log.info("{} 拉取注单参数: {} | {}, page: {}, size: {}, 本次拉取数量: {}, 总记录数: {}", platformName(), venuePullParamVO.getStarttime(), venuePullParamVO.getEndtime(),
                            pageIndex, pageSize, betDetails.size(), recordCount);

                    if (CollectionUtils.isNotEmpty(orderList)) {
                        orderRecordProcessService.orderProcess(orderList);
                    }
                }else{
                    return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                }

            } while (pageIndex < pageCount);
            log.info("nextSpin-拉单完成");
            return ResponseVO.success(null);
        } catch (Exception e) {
            log.error("nextSpin-拉单异常,apiPath: {} , head:{}, request: {}", url, JSON.toJSONString(head), param, e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

//    private String testMsg(){
//        String data="{\"serialNo\":\"cf82bfb4-9aa9-4a47-a657-fabd361c6671\",\"code\":0,\"msg\":\"Success\",\"merchantCode\":\"ZT9910TEST\",\"resultCount\":0,\"pageCount\":1,\"list\":[{\"ticketId\":\"202505272329598851461232\",\"acctId\":\"Utest_43903257\",\"categoryId\":\"SM\",\"gameCode\":\"aDonkiKong\",\"ticketTime\":\"20250710T214158\",\"betIp\":\"182.182.182.182\",\"currency\":\"PTS\",\"betAmount\":1,\"winLoss\":-0.09,\"jackpotAmount\":0,\"result\":\"0:4:0:0,5465:4:0:0,5461:4:0:0,5468:4:0:0,5470:4:0:0,0:4:0:0,5463:4:0:0,5463:4:0:0,5463:4:0:0,5461:4:0:0,5466:4:0:0,5465:3:0:0,5464:3:0:0,5468:4:0:0,0:0:0:0,0:0:0:0,5465:3:0:0,5464:3:0:0,5468:4:0:0,0:0:0:0,0:0:0:0,5461:4:0:0,5468:1:0:0,0:0:0:0,0:0:0:0,0:0:0:0,5462:4:0:0,5462:4:0:0,5465:3:0:0,5468:4:0:0,0:0:0:0,5465:4:0:0,5470:4:0:0,5465:4:0:0,5467:4:0:0,5462:4:0:0\",\"completed\":true,\"luckyDrawId\":0,\"roundId\":0,\"sequence\":0,\"channel\":\"web\",\"balance\":95.8,\"jpWin\":0,\"referenceId\":\"202505272329598851460148\"}]}";
//        return data;
//    }


    /**
     * 解析注单数据
     *
     * @param venueInfoVO 商户信息
     * @param betDetails  注单详情
     * @return 解析的注单
     */
    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, JSONArray betDetails, Map<String, SiteVO> siteVOMap) {
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());

        List<NextSpinResponseVO> betDetailEntityList = Lists.newArrayList();
        if (betDetails != null && !betDetails.isEmpty()) {
            for (int i = 0; i < betDetails.size(); i++) {
                JSONObject betDetail = betDetails.getJSONObject(i);
                try {
                    NextSpinResponseVO betDetailEntity = JSONObject.parseObject(betDetail.toJSONString(), NextSpinResponseVO.class);
                    betDetailEntity.setOriginalBetDetail(JSON.toJSONString(betDetail));
                    betDetailEntityList.add(betDetailEntity);
                } catch (Exception e) {
                    log.error("{} 解析注单时间适配异常:{}", platformName(), betDetail);
                }
            }
        }

        // 场馆用户关联信息
        List<String> venueUserList = betDetailEntityList.stream().map(NextSpinResponseVO::getAcctId).distinct().toList();
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder()
                .venuePlatform(venueInfoVO.getVenuePlatform())
                .venueUserAccountList(venueUserList).build();
        // key:游戏账号 Utest_44905163
        Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberService.getNextSpinCasinoMemberMap(casinoMemberReq);
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
        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.NEXTSPIN.getVenueCode());
        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        for (NextSpinResponseVO nextSpinResponseVO : betDetailEntityList) {
            String account = nextSpinResponseVO.getAcctId().toUpperCase();

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

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            String betIp = userLoginInfoVO.getIp();
            Integer deviceType = Strings.isNotBlank(userLoginInfoVO.getLoginTerminal()) ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

            OrderRecordVO orderRecordVO = new OrderRecordVO();
            // 会员账号
            orderRecordVO.setUserAccount(userInfoVO.getUserAccount());
            // 会员姓名
            orderRecordVO.setUserName(userInfoVO.getUserName());
            orderRecordVO.setUserId(userInfoVO.getUserId());
            orderRecordVO.setSiteCode(userInfoVO.getSiteCode());
            //读取站点名称
            String siteName = null;
            if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
                SiteVO siteVO = siteVOMap.get(userInfoVO.getSiteCode());
                siteName = siteVO.getSiteName();
            }
            orderRecordVO.setSiteName(siteName);
            // 账号类型 1测试 2正式 3商务 4置换
            orderRecordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecordVO.setAgentId(userInfoVO.getSuperAgentId());
            orderRecordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            // 三方会员账号
            orderRecordVO.setCasinoUserName(account);
            // 上级代理账号
            orderRecordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecordVO.setAgentId(userInfoVO.getSuperAgentId());
            // VIP等级
            orderRecordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecordVO.setVipRank(userInfoVO.getVipRank());
            // 三方平台
            orderRecordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
            // 游戏平台名称
            //orderRecordVO.setVenueName(venueInfoVO.getVenueName());
            // 游戏平台CODE
            orderRecordVO.setVenueCode(venueInfoVO.getVenueCode());

            // 游戏大类
            orderRecordVO.setVenueType(gameTypeId);

            String gameType = nextSpinResponseVO.getCategoryId();
            // 游戏大类
            orderRecordVO.setRoomType(gameType);
            orderRecordVO.setRoomTypeName(NextSpinGameTypeEnums.nameOfCode(gameType));
            // 游戏小类名称
            GameInfoPO gameInfoPO = paramToGameInfo.get(nextSpinResponseVO.getGameCode());
            if (gameInfoPO != null) {
                orderRecordVO.setGameId(gameInfoPO.getGameId());
                orderRecordVO.setGameName(gameInfoPO.getGameI18nCode());
            }
            // 游戏名称-添加
            orderRecordVO.setThirdGameCode(nextSpinResponseVO.getGameCode());
            // 游戏Code
            orderRecordVO.setGameCode(nextSpinResponseVO.getGameCode());
            // 房间类型 roomType
            // 房间类型名称 roomTypeName
            // 玩法类型(电子类玩法就是结果)
//            orderRecordVO.setPlayType(nextSpinResponseVO.getResult());
            // 下注内容
            //orderRecordVO.setBetContent(detailEntity.getContent());

            // 投注时间
            orderRecordVO.setBetTime(TimeZoneUtils.parseDate4TimeZoneToTime(nextSpinResponseVO.getTicketTime(),  DateUtils.yyyyMMddTHHmmss, shanghaiTimeZone));
            // 结算时间
            orderRecordVO.setSettleTime(TimeZoneUtils.parseDate4TimeZoneToTime(nextSpinResponseVO.getTicketTime(),  DateUtils.yyyyMMddTHHmmss, shanghaiTimeZone));

            // 变更状态
            orderRecordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());

            // 投注额
            orderRecordVO.setBetAmount(nextSpinResponseVO.getBetAmount());
            // 已结算注单
            BigDecimal validBetAmount = BigDecimal.ZERO;
            if (orderRecordVO.getSettleTime() != null) {
                // 输赢金额
                orderRecordVO.setWinLossAmount(nextSpinResponseVO.getWinLoss() == null ? null : nextSpinResponseVO.getWinLoss());
                // 有效投注
                validBetAmount = nextSpinResponseVO.getBetAmount() == null ? null : nextSpinResponseVO.getBetAmount();
                orderRecordVO.setValidAmount(validBetAmount);

                // 派彩金额=输赢金额+投注金额
                orderRecordVO.setPayoutAmount(orderRecordVO.getWinLossAmount().add(orderRecordVO.getBetAmount()));
            }

            // 注单ID
//            orderRecordVO.setOrderId(nextSpinResponseVO.getTicketId());
            orderRecordVO.setOrderId(OrderUtil.getGameNo());
            // 三方注单ID
            orderRecordVO.setThirdOrderId(nextSpinResponseVO.getReferenceId());
            // 注单状态 回复，只会返回已经结算的注单
            orderRecordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());

            // 注单归类
            orderRecordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(orderRecordVO.getOrderStatus()));
            // 赔率
//            orderRecordVO.setOdds(detailEntity.getOdds());
            // 局号/期号
            orderRecordVO.setGameNo(nextSpinResponseVO.getRoundId());
            // 注单详情中 放入注单id
            orderRecordVO.setOrderInfo(nextSpinResponseVO.getReferenceId());
            // 桌号 deskNo
//            orderRecordVO.setDeskNo(detailEntity.getTableid());
            // 靴号 bootNo
            // 结果牌 / 结果 resultList
            if (ObjectUtil.isNotEmpty(orderRecordVO.getWinLossAmount())) {
                orderRecordVO.setResultList(getWinLossResult(orderRecordVO.getWinLossAmount()));
            }
            // 投注IP
            orderRecordVO.setBetIp(betIp);
            // 币种
            orderRecordVO.setCurrency(userInfoVO.getMainCurrency());
            // 设备类型
            orderRecordVO.setDeviceType(deviceType);
            // 体育、电竞、彩票, 需要保存原始注单到串关字段中
            orderRecordVO.setParlayInfo(nextSpinResponseVO.getOriginalBetDetail());
            orderRecordVO.setTransactionId(orderRecordVO.getThirdOrderId());
            orderRecordList.add(orderRecordVO);
        }
        return orderRecordList;
    }
    /**
     * 根据盈亏金额返回对应的输赢结果。
     *
     * @param winLossAmount 盈亏金额，负数表示亏损，正数表示盈利，零表示平局
     * @return 对应的输赢结果字符串：
     * - 若金额小于 0，则返回 {@code WinLossEnum.LOSS.getName()}
     * - 若金额大于 0，则返回 {@code WinLossEnum.WIN.getName()}
     * - 若金额等于 0，则返回 {@code WinLossEnum.TIE.getName()}
     */
    private String getWinLossResult(BigDecimal winLossAmount) {
        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
                : WinLossEnum.TIE.getName();

    }





    public ResponseVO<NextSpinPullBetParams> getBetRecordListByParams(VenueInfoVO venueInfoVO, NextSpinPullBetParams params) {
        //拉单
        getBetRecordList(venueInfoVO,params);
        // 生成下一次拉单参数
        NextSpinPullBetParams pullBetParams = nextPullBetParams(params);
        return ResponseVO.success(pullBetParams);
    }


    /**
     * 生成下一次拉单参数
     *
     * @param params 拉单参数对象
     * @return 参数
     */
    public NextSpinPullBetParams nextPullBetParams(NextSpinPullBetParams params) {
        NextSpinPullBetParams newPullBetParams = new NextSpinPullBetParams();
        newPullBetParams.setStarttime(params.getStarttime());
        newPullBetParams.setEndtime(params.getEndtime());
        Integer timeInterval = params.getTimeInterval() == null ? NextSpinConstant.DEFAULT_TIME_INTERVAL : params.getTimeInterval();
        newPullBetParams.setTimeInterval(timeInterval);
        // GMT+8当前时间
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt8ZoneId);
        // 当前查询区间的起始时间
        Date startAtTime = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getStarttime(), gmt8TimeZone, DateUtils.yyyyMMddTHHmmss);
        // 当前时间减xx分钟的时间
        DateTime beforeTenMinutes = new DateTime(canadaCurrentDate).plusMillis(timeInterval * -1);
        // 如果起始时间 > (当前时间 - 10分钟), 则重新设置起始时间
        assert startAtTime != null;
        if (beforeTenMinutes.isBefore(startAtTime.getTime())) {
            // 开始时间设置（当前时间-10）
            newPullBetParams.setStarttime(TimeZoneUtils.formatDate4TimeZone(beforeTenMinutes.toDate(), gmt8TimeZone, DateUtils.yyyyMMddTHHmmss));
            // 结束时间设置当前时间
            newPullBetParams.setEndtime(TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt8TimeZone, DateUtils.yyyyMMddTHHmmss));
        } else {
            Date endAt = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getEndtime(), gmt8TimeZone, DateUtils.yyyyMMddTHHmmss);
            // 起始时间 = 结束时间 (endAt)
            String starAtStr = TimeZoneUtils.formatDate4TimeZone(endAt, gmt8TimeZone, DateUtils.yyyyMMddTHHmmss);
            // 结束时间 = 结束时间 (endAt) + timeInterval（例如 10 分钟）
            String endAtStr = TimeZoneUtils.formatDate4TimeZone(new DateTime(endAt).plusMillis(timeInterval).toDate(), gmt8TimeZone, DateUtils.yyyyMMddTHHmmss);

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
        String url = venueDetailVO.getApiUrl();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("merchantCode",venueDetailVO.getMerchantNo());
        paramMap.put("serialNo", UUID.randomUUID());
        paramMap.put("acctId",venueUserAccount);
        String param=JSON.toJSONString(paramMap);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type","application/json");
        head.put("API","kickAcct");
        log.info("nextSpin-用户踢线request, apiPath: {} , head:{}, request: {}",url, JSON.toJSONString(head), param);
        try {
            String response = HttpClientHandler.post(url,head,param);
            log.info("{} nextSpin-用户踢线response, url: {} , head:{}, 参数: {} , 返回消息: {}", platformName(),url, JSON.toJSONString(head), param, response);
        } catch (Exception e) {
            log.error("nextSpin-用户踢线异常【{}】", venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }

    public  List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.NEXTSPIN.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO){
        List<JSONObject> resultList = Lists.newArrayList();
        String langCode = CurrReqUtils.getLanguage();
        JSONArray jsonArray = listAllNextSpinGame(venueInfoVO);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String gameCode = json.getString("gameCode"); //游戏代碼
                String gameName = null;
                if (LanguageEnum.ZH_CN.getLang().equals(langCode)){
                    gameName = json.getString("cnName");
                }else{
                    gameName = json.getString("gameName");
                }
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", gameName);
                gameJson.put("deskNumber", gameCode);
                resultList.add(gameJson);
            }
        }
        log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), resultList.size());
        return ResponseVO.success(resultList);
    }

    private JSONArray listAllNextSpinGame(VenueInfoVO venueInfoVO) {
        String url = venueInfoVO.getApiUrl();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("merchantCode",venueInfoVO.getMerchantNo());
        paramMap.put("serialNo", UUID.randomUUID());
        String param=JSON.toJSONString(paramMap);
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type","application/json");
        head.put("API","getGames");
        log.info("{} nextSpin游戏列表, url: {} , head:{}, 参数: {}", platformName(),url, JSON.toJSONString(head), param);
        try {
        String requestData = HttpClientHandler.post(url,head,param);
        log.info("{} nextSpin获取游戏列表, url: {} , head:{}, 参数: {} , 返回消息: {}", platformName(),url, JSON.toJSONString(head), param, requestData);
        JSONObject jsonObject = JSONObject.parseObject(requestData);
            if (jsonObject != null &&
                        (jsonObject.getIntValue("code") == CommonConstant.business_zero)) {
                return jsonObject.getJSONArray("games");
            }
        } catch (Exception e) {
            log.error(String.format("%s 获取游戏列表发生错误!!", platformName()), e);
        }
        return new JSONArray();
    }

    public String platformName() {
        return VenueEnum.NEXTSPIN.getVenueName();
    }

    public static void main(String[] args) {
//        NextSpinServiceImpl n=new NextSpinServiceImpl();
        VenueInfoVO v=new VenueInfoVO();
        v.setApiUrl("https://merchantapi.ns-api-cy2-staging.com/api");
        v.setMerchantNo("ZT9910TEST");
        //获取游戏列表
//        n.queryGameList(null,null,v);
        //登陆游戏页面
//        LoginVO loginVO=new  LoginVO();
//        loginVO.setLanguageCode("zh-CN");
//        loginVO.setGameCode("aDonkiKong");
//        CasinoMemberVO c=new CasinoMemberVO();
//        c.setCasinoPassword("123456789");
//        c.setVenueUserAccount("mufantest1");
//        n.login(loginVO,v,c);
        //用户登出
//        n.logOut(v,"mufantest1");

        VenuePullParamVO venuePullParamVO=new VenuePullParamVO();
        venuePullParamVO.setStartTime(1752035073000L);
        venuePullParamVO.setEndTime(1752035673000L);
        long startTime = venuePullParamVO.getStartTime();
        long endTime = venuePullParamVO.getEndTime();


    }


    public Object oauth(NextSpinReq request) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getAcctId());
        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
        //只有authorize 才有token，所以才对token获取
        if (StringUtils.isNotEmpty(request.getToken())){
            casinoMemberReqVO.setCasinoPassword(request.getToken());
        }
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return NextSpinResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST,request.getMerchantCode(), request.getSerialNo());
        }
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            return NextSpinResp.err(NextSpinRespErrEnums.SYSTEM_ERROR,request.getMerchantCode(), request.getSerialNo());
        }
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return NextSpinResp.err(NextSpinRespErrEnums.SYSTEM_ERROR,request.getMerchantCode(), request.getSerialNo());
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        String mainCurrency =null;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
            mainCurrency = userCenterCoin.getCurrency();
        }else{
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            mainCurrency = userInfoVO.getMainCurrency();
        }
        mainCurrency= NextSpinCurrencyEnums.getByCode(mainCurrency).name();
        AcctInfo acctInfo=new AcctInfo();
        acctInfo.setAcctId(request.getAcctId());
        acctInfo.setUserName(casinoMember.getUserAccount());
        acctInfo.setCurrency(mainCurrency);
        acctInfo.setBalance(balance);
        return NextSpinResp.success(request.getMerchantCode(), request.getSerialNo(),acctInfo);

    }

    public Object checkBalance(NextSpinReq request) {
        return oauth(request);
    }

    public Object bet(NextSpinReq request) {
//        1下注 2取消下注 4派彩 6jackpot的派彩
        Integer type = request.getType();
        return switch (type) {
            case 1:
                yield placeBet(request);
            case 2:
                yield refund(request);
            case 4,6:
                yield settleBet(request);
            default:
                yield NextSpinResp.err(NextSpinRespErrEnums.INVALID_REQUEST,request.getMerchantCode(), request.getSerialNo());
        };
    }

    /**
     * 下注
     * @param req
     * @return
     */
    private Object placeBet(NextSpinReq req) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
        }
        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);
        if(Objects.isNull(userInfoVO)){
            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] not find.",req.getAcctId());
            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
        }
        //用户中心钱包余额
        UserCoinWalletVO userCoinWalletVO = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal userAmount = userCoinWalletVO.getTotalAmount();
        if(venueMaintainClosed(VenuePlatformConstants.NEXTSPIN,userInfoVO.getSiteCode())){
            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.NEXTSPIN.getVenueCode());
            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,userAmount);
        }

        if (venueGameMaintainClosed(VenuePlatformConstants.NEXTSPIN,casinoMember.getSiteCode(),req.getGameCode())){
            log.info("{}:游戏未开启", VenueEnum.NEXTSPIN.getVenueName());
            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,userAmount);
        }

        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
            return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,userAmount);
        }
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenuePlatformConstants.NEXTSPIN, req.getTransferId()));
        try {
            if (!rLock.tryLock()) {
                log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,userAmount);
            }
            if(BigDecimal.ZERO.compareTo(req.getAmount()) == 0){
                String tid=String.valueOf(UUID.randomUUID());
                NextSpinTransactionRecordVO vo=new NextSpinTransactionRecordVO();
                vo.setAccount(req.getAcctId());
                vo.setReturnNumber(tid);
                vo.setTransferId(req.getTransferId());
                vo.setRequestJson(JSONObject.toJSONString(req));
                nextSpinTransactionRecordService.insert(vo);
                return NextSpinBetResp.success( req, tid,userAmount);
            }
            // 检查余额
            if(!compareAmount(userAmount,req.getAmount())){
                log.info("NEXTSPIN 用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getAmount());
                return NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,userAmount);
            }
            //修改余额 记录账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, req.getTransferId(),
//                    req.getAmount(),req.getTransferId());


            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setOrderNo(req.getTransferId());
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(req.getAmount().abs());
            userCoinAddVO.setRemark(req.getTransferId());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
            userCoinAddVO.setThirdOrderNo(req.getTransferId());
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS -> {
                    UserCoinRecordRequestVO coinRecord = new UserCoinRecordRequestVO();
                    coinRecord.setUserId(userId);
                    coinRecord.setRemark(req.getTransferId());
                    coinRecord.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                    List<UserCoinRecordVO> coinRecordOne = getUserCoinRecords(coinRecord);
                    yield NextSpinBetResp.success(req,coinRecordOne.get(0).getId(),coinRecordResultVO.getCoinAfterBalance());
                }
                case AMOUNT_LESS_ZERO ->{
                    yield NextSpinBetResp.success(req,UUID.randomUUID().toString(),coinRecordResultVO.getCoinAfterBalance());
                }
                case REPEAT_TRANSACTIONS ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,userAmount);
                }
                case  INSUFFICIENT_BALANCE->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,userAmount);
                }
                case  WALLET_NOT_EXIST->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,userAmount);
                }
                default ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,userAmount);
                }
            };
        }catch (Exception e){
            log.error("NEXTSPIN failed processAdjustBet error {}", e.getMessage());
            return NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,userAmount);
        }finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
    /**
     * 取消下注
     */
    public Object refund(NextSpinReq req) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
        }
        String venueCode = casinoMember.getVenueCode();
        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);
        //用户中心钱包余额

        UserCoinWalletVO userCoinWalletVO = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal centerAmount=userCoinWalletVO.getTotalAmount();
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
        }
        String orderNo = req.getTransferId();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
        try{
            if (!rLock.tryLock()) {
                log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
            }
            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
            }
            // 场馆关闭
            if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
                log.info("{}:场馆未开启", VenueEnum.NEXTSPIN.getVenueName());
                return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
            }

            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
            coinRecordRequestVO.setUserId(userId);
            coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
            coinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            coinRecordRequestVO.setRemark(req.getReferenceId());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
            if(CollectionUtil.isEmpty(userCoinRecords)) {
                log.error("NEXTSPIN 查询交易订单为空 或不存在 req:{}", req);
                return NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO,req,centerAmount);
            }
            UserCoinRecordVO userCoinRecordVO = userCoinRecords.get(0);
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancel(userInfoVO, req.getReferenceId(),
                    userCoinRecordVO.getCoinValue(),req.getTransferId(),VenuePlatformConstants.NEXTSPIN);
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS,AMOUNT_LESS_ZERO ->{

                    UserCoinRecordRequestVO coinRecord = new UserCoinRecordRequestVO();
                    coinRecord.setUserId(userId);
                    coinRecord.setRemark(req.getTransferId());
                    coinRecord.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                    List<UserCoinRecordVO> coinRecordOne = getUserCoinRecords(coinRecord);
                    String tid=null;
                    if (CollectionUtil.isEmpty(coinRecordOne)){
                        tid=UUID.randomUUID().toString();
                    }else{
                        tid= coinRecordOne.get(0).getId();
                    }
                    yield NextSpinBetResp.success(req,tid,coinRecordResultVO.getCoinAfterBalance());
                }
                case REPEAT_TRANSACTIONS ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,centerAmount);
                }
                case  INSUFFICIENT_BALANCE->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
                }
                case  WALLET_NOT_EXIST->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,centerAmount);
                }
                default ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
                }
            };
        }catch (Exception e){
            log.error("NEXTSPIN游戏退款取消失败，失败原因为{}",e.getMessage());
            return NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO,req,centerAmount);
        }finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }


    /**
     * 派彩
     */
    public Object settleBet(NextSpinReq req) {
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(req.getAcctId());
        casinoMemberReqVO.setVenueCode(VenueEnum.NEXTSPIN.getVenueCode());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);
        if(Objects.isNull(userInfoVO)){
            log.error("NEXTSPIN queryUserInfoByAccount userName[{}] not find.",req.getAcctId());
            return NextSpinBetResp.err(NextSpinRespErrEnums.MEMBER_NOT_EXIST, req,null);
        }


        //用户中心钱包余额
        BigDecimal centerAmount = getUserBalance(userInfoVO.getUserId());
        if(venueMaintainClosed(VenuePlatformConstants.NEXTSPIN,userInfoVO.getSiteCode())){
            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.NEXTSPIN.getVenueCode());
            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
        }
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = Lists.newArrayList(VenueEnum.NEXTSPIN.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            return NextSpinBetResp.err(NextSpinRespErrEnums.SERVICE_UNAVAILABLE, req,centerAmount);
        }
        // 三方的参数没有唯一性，故加上时间戳
        String orderNo = req.getTransferId();
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, orderNo));
        try{
            if (!rLock.tryLock()) {
                log.error("NEXTSPIN errorBet error get locker error, req:{}", req);
                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
            }
            // 游戏锁定
            if (userGameLock(userInfoVO)) {
                log.error("NEXTSPIN queryUserInfoByAccount userName[{}] game lock.", userInfoVO.getUserName());
                return NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_LOCKED, req,centerAmount);
            }

            if(BigDecimal.ZERO.compareTo(req.getAmount()) == 0){
                return NextSpinBetResp.success( req, String.valueOf(UUID.randomUUID()),centerAmount);
            }

            if(ObjectUtils.isEmpty(req.getReferenceId())){
                return NextSpinBetResp.err(NextSpinRespErrEnums.MISSING_REQUIRED_PARAM, req,centerAmount);
            }
            // 根据关联的transactionId和preTransactionId 查询出对应的orderNo
            CoinRecordResultVO coinRecordResultVO = null;
            String refTransaction = req.getReferenceId();
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setRemark(refTransaction);
            coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
            coinRecordRequestVO.setUserId(userId);
            coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
            List<UserCoinRecordVO> data = getUserCoinRecords(coinRecordRequestVO);
            if (CollectionUtil.isNotEmpty(data)) {
                UserCoinRecordVO userCoinRecordVO = data.get(0);
                // 查询出是否有已经派彩的
                coinRecordRequestVO = new UserCoinRecordRequestVO();
                coinRecordRequestVO.setOrderNo(userCoinRecordVO.getOrderNo());
                coinRecordRequestVO.setRemark(req.getTransferId());
                coinRecordRequestVO.setUserId(userId);
                coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
                coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                List<UserCoinRecordVO> recordsList = getUserCoinRecords(coinRecordRequestVO);
                if (CollectionUtil.isNotEmpty(recordsList)) {
                    return  NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req, centerAmount);
                }
            }else{
                NextSpinTransactionRecordVO nextSpinTransactionRecordVO = nextSpinTransactionRecordService.getByTransferId(req.getReferenceId());
                if (Objects.isNull(nextSpinTransactionRecordVO)){
                    return NextSpinBetResp.err(NextSpinRespErrEnums.BATCH_NO_NOT_EXIST, req, centerAmount);
                }
            }

            UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
            userCoinAddVOPayout.setOrderNo(req.getReferenceId());
            userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
            userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
            userCoinAddVOPayout.setCoinValue(req.getAmount().abs());
            userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVOPayout.setRemark(req.getTransferId());
            userCoinAddVOPayout.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
            userCoinAddVOPayout.setVenueCode(VenuePlatformConstants.MARBLES);
            userCoinAddVOPayout.setThirdOrderNo(req.getTransferId());

            // 正常派彩
            coinRecordResultVO =  toUserCoinHandle(userCoinAddVOPayout);
            if(coinRecordResultVO == null){
                log.error("NEXTSPIN error settleBet error updateBalancePayout error, req:{}", req);
                return  NextSpinBetResp.err(NextSpinRespErrEnums.BATCH_NO_NOT_EXIST, req, centerAmount);
            }
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS ->{

                    UserCoinRecordVO uvo = getUserCoinRecordByRemarkNo(req.getTransferId(),userInfoVO.getUserId(),CoinBalanceTypeEnum.INCOME.getCode());
                    yield NextSpinBetResp.success(req,uvo.getId(),coinRecordResultVO.getCoinAfterBalance());
                }
                case REPEAT_TRANSACTIONS ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.DUPLICATE_ID_NO, req,centerAmount);
                }
                case  INSUFFICIENT_BALANCE->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.INSUFFICIENT_BALANCE, req,centerAmount);
                }
                case  WALLET_NOT_EXIST->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.ACCOUNT_INFO_ABNORMAL, req,centerAmount);
                }
                default ->{
                    yield NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
                }
            };
        }catch (Exception e){
            log.error("NEXTSPIN 退款取消失败， 失败原因为{}",e.getMessage());
            return NextSpinBetResp.err(NextSpinRespErrEnums.SYSTEM_ERROR, req,centerAmount);
        }finally
        {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
