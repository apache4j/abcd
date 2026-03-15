package com.cloud.baowang.play.game.v8.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.v8.V8RespErrEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.v8.SeamlesswalletResp;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.v8.enums.V8ServiceEnums;
import com.cloud.baowang.play.game.v8.resp.SingleV8OrderDetaiVO;
import com.cloud.baowang.play.game.v8.resp.V8OrderDataRespVO;
import com.cloud.baowang.play.game.v8.resp.V8OrderDetailRespVO;
import com.cloud.baowang.play.game.v8.resp.V8OrderRespVO;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.util.Encrypt;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
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
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.V8)
public class V8GameServiceImpl extends GameBaseService implements GameService {

    @Autowired
    private OrderRecordProcessService orderRecordProcessService;

    @Autowired
    private OrderRecordService orderRecordService;

    // 时区：GMT+8 (东8区)
    public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");



    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        Date date = new Date();
        long timestamp = date.getTime();
        String orderid = venueDetailVO.getMerchantNo() + DateUtils.dateToyyyyMMddHHmmssSSS(date)+casinoMemberVO.getVenueUserAccount();
        String currencyCode = venueDetailVO.getPullCurrencyCodeList().get(0);

        if(CurrencyEnum.KVND.getCode().equals(currencyCode)){
            currencyCode = currenyMap.get(CurrencyEnum.KVND.getCode());
        }else if(CurrencyEnum.USDT.getCode().equals(currencyCode)){
            currencyCode = currenyMap.get(CurrencyEnum.USDT.getCode());
        }
        StringBuffer sb = new StringBuffer("s=0&account=");
        sb.append(casinoMemberVO.getVenueUserAccount()).append("&money=0").append("&orderid=")
                .append(orderid).append("&ip=").append(loginVO.getIp())
                .append("&lineCode=").append(loginVO.getSiteCode())
                .append("&KindID=").append(loginVO.getGameCode())
                    .append("&currency=")
                 .append(currencyCode);
        log.info("v8拼接的参数{}",sb);
        String params = Encrypt.AESEncrypt(sb.toString(),venueDetailVO.getAesKey());
        String key = Encrypt.MD5(venueDetailVO.getMerchantNo()+timestamp + venueDetailVO.getMerchantKey());
        // 登陆的URL
        String loginUrl = venueDetailVO.getApiUrl()+ V8ServiceEnums.CHANNELHANDLE.getPath() + "?agent="+ venueDetailVO.getMerchantNo()
                 + "&timestamp=" + timestamp + "&param="+ params + "&key="+key;
        log.info("v8 game loginUrl:{}",loginUrl);
        String data = HttpClientHandler.get(loginUrl,null,null);
        log.info("data={}",data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        log.info(data);
        if (jsonObject == null || jsonObject.getJSONObject("d") == null || jsonObject.getJSONObject("d").getIntValue("code") != CommonConstant.business_zero){
            log.error("{} 获取登录链接失败,参数：{}",venueDetailVO.getVenueCode(),data);
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
        GameLoginVo gameLoginVO = GameLoginVo.builder().source(jsonObject.getJSONObject("d").getString("url"))
                .type(GameLoginTypeEnums.URL.getType())
                .userAccount(loginVO.getUserAccount())
                .venueCode(venueDetailVO.getVenueCode())
                .build();
        return ResponseVO.success(gameLoginVO);
    }


    @Override
    public ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        if (CollectionUtil.isEmpty(orderRecordMqVOList)) {
            return ResponseVO.success();
        }
        List<String> orderIdList = Optional.of(orderRecordMqVOList).orElse(Lists.newArrayList()).stream()
                .map(OrderRecordMqVO::getThirdOrderId).collect(Collectors.toList());
        List<OrderRecordPO> orderRecordPOList = orderRecordService.list(Wrappers.<OrderRecordPO>lambdaQuery()
                .in(OrderRecordPO::getThirdOrderId, orderIdList)
                .eq(OrderRecordPO::getVenueCode, VenueEnum.V8.getVenueCode()));
        Map<String, OrderRecordPO> orderRecordPOMap = orderRecordPOList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, e -> e));
        List<OrderRecordVO> orderRecordVOS = Lists.newArrayList();
        for (OrderRecordMqVO orderRecordMqVO : orderRecordMqVOList) {
            OrderRecordPO orderRecordPO = orderRecordPOMap.get(orderRecordMqVO.getThirdOrderId());
            if (Objects.isNull(orderRecordPO)) {
                log.info("v8订单不存在");
                continue;
            }
            log.info("V8取消退款时orderRecordMqVO={}", JSONObject.toJSONString(orderRecordMqVO));
            OrderRecordVO orderRecord = BeanUtil.toBean(orderRecordPO, OrderRecordVO.class);
            orderRecord.setOrderStatus(orderRecordMqVO.getOrderStatus());
            orderRecord.setOrderClassify(orderRecordMqVO.getOrderClassify());
            orderRecord.setCurrency(orderRecordMqVO.getCurrency());
            orderRecordVOS.add(orderRecord);
        }
        orderRecordProcessService.orderProcess(orderRecordVOS);
        return ResponseVO.success("同步v8注单成功");
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {

        try{
            String venueCode = venueInfoVO.getVenueCode();
            long timestamp = venuePullParamVO.getEndTime();
            String url = venueInfoVO.getBetUrl().concat(V8ServiceEnums.GETRECORDHANDLE.getPath());
            String key = Encrypt.MD5(venueInfoVO.getMerchantNo()+timestamp+venueInfoVO.getMerchantKey());
            String param = "s=6&startTime="+venuePullParamVO.getStartTime()+"&endTime="+venuePullParamVO.getEndTime();
            url =  url.concat("?agent=").concat(venueInfoVO.getMerchantNo())
                    .concat("&timestamp=").concat(timestamp+"")
                    .concat("&param=").concat(Encrypt.AESEncrypt(param,venueInfoVO.getAesKey()))
                    .concat("&key=").concat(key);
            log.info("V8请求参数{}",param);
            log.info("v8url={}", url);
            String data = HttpClientHandler.get(url,null,null);
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (jsonObject == null ||
                    (jsonObject.getJSONObject("d").getIntValue("code")!= CommonConstant.business_zero)) {
                if(jsonObject != null && (jsonObject.getJSONObject("d").getIntValue("code") == CommonConstant.business_five
                     || jsonObject.getJSONObject("d").getIntValue("code") == 16)){
                    log.warn("V8拉取注无数据返回，code={}, {}", jsonObject.getJSONObject("d").getIntValue("code"), data);
                    return ResponseVO.success();
                }
                log.warn("V8拉取注返回异常,43=拉单太频繁，code=16是无注单记录返回：{}, {}", venueInfoVO.getVenueCode(), data);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("V8拉取注json={}",jsonObject);
            V8OrderRespVO v8OrderRespVO = JSONObject.parseObject(data, V8OrderRespVO.class);
            if (v8OrderRespVO.getD().getCount() == 0) {
                log.info("{} 拉取注返回数据为空", venueCode);
                return ResponseVO.success();
            }

            Map<String, String> rebateMap = getVIPRebateSingleVOMap(venueCode);
            Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueCode);

            log.info("v8 拉单数据{},{}", venueCode, v8OrderRespVO);

            V8OrderDataRespVO respVO = v8OrderRespVO.getD();
            // 场馆用户关联信息
            List<String> usernames = respVO.getList().getAccounts();
            if(!CollectionUtil.isEmpty(usernames)){
                usernames = usernames.stream().map(s-> getAccount(s) ).distinct().collect(Collectors.toList());
            }
            Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(usernames, venueInfoVO.getVenuePlatform());
            if (MapUtil.isEmpty(casinoMemberMap)) {
                log.info("{}未找到三方关联信息 玩家列表{}",venueCode, usernames);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            // 用户信息
            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
            Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
            if (CollUtil.isEmpty(userMap)) {
                log.info("{}游戏用户账号不存在{}",venueCode, userIds);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            V8OrderDetailRespVO respVOList = respVO.getList();
            // 用户登录信息
            Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

            List<OrderRecordVO> list = Lists.newArrayList();
            Map<String, String> siteNameMap = getSiteNameMap();

            for (int i = 0; i < respVOList.getGameID().size(); i++) {
                String accounts = respVOList.getAccounts().get(i);
                BigDecimal allBet = respVOList.getAllBet().get(i);
                Date gameStartTime = respVOList.getGameStartTime().get(i);
                Date gameEndTime = respVOList.getGameEndTime().get(i);
                String gameId= respVOList.getGameID().get(i);
                BigDecimal profit = respVOList.getProfit().get(i);
                BigDecimal cellScore = respVOList.getCellScore().get(i);
                String lineCode = respVOList.getLineCode().get(i);
                Integer channelId = respVOList.getChannelID().get(i);
                Integer kindID = respVOList.getKindID().get(i);
                String cardValue = respVOList.getCardValue().get(i);
                String currency = respVOList.getCurrency().get(i);


                SingleV8OrderDetaiVO singleV8OrderDetaiVO = new SingleV8OrderDetaiVO();
                singleV8OrderDetaiVO.setAccounts(accounts);
                singleV8OrderDetaiVO.setAllBet(allBet);
                singleV8OrderDetaiVO.setGameStartTime(gameStartTime);
                singleV8OrderDetaiVO.setGameEndTime(gameEndTime);
                singleV8OrderDetaiVO.setGameID(gameId);
                singleV8OrderDetaiVO.setProfit(profit);
                singleV8OrderDetaiVO.setCellScore(cellScore);
                singleV8OrderDetaiVO.setLineCode(lineCode);
                singleV8OrderDetaiVO.setChannelID(channelId);
                singleV8OrderDetaiVO.setKindID(kindID);
                singleV8OrderDetaiVO.setCardValue(cardValue);
                singleV8OrderDetaiVO.setCurrency(currency);
                singleV8OrderDetaiVO.setLanguage(respVOList.getLanguage().get(i));
                singleV8OrderDetaiVO.setTableID(respVOList.getTableID().get(i));
                singleV8OrderDetaiVO.setServerID(respVOList.getServerID().get(i));
                singleV8OrderDetaiVO.setChairID(respVOList.getChairID().get(i));
                singleV8OrderDetaiVO.setRevenue(respVOList.getRevenue().get(i));
                String splitCounts = getAccount(accounts);
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(splitCounts);
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), splitCounts);
                    continue;
                }
                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                if (userInfoVO == null) {
                    log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
                    continue;
                }

                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                // 映射原始注单
                OrderRecordVO recordVO = parseRecords(venueInfoVO, userInfoVO, userLoginInfoVO, rebateMap,
                        siteNameMap,paramToGameInfo, singleV8OrderDetaiVO);
                // recordVO.setVenueType(gameTypeId);
                recordVO.setVenueType(VenueEnum.V8.getType().getCode());
                list.add(recordVO);
            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            return ResponseVO.success(list);
        }catch (Exception e){
            log.error("v8-拉取注单数据异常, venueCode:{}, data:{}", venueInfoVO.getVenueCode(), e.getMessage());
            e.printStackTrace();
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }



    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, UserInfoVO userInfoVO,
                                       UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap,
                                       Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo,
                                       SingleV8OrderDetaiVO singleV8OrderDetaiVO) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(singleV8OrderDetaiVO.getAllBet());
        // startTime 开始当投注， endTime 结束当派奖
        String gemeStartime = DateUtils.convertDateToString(singleV8OrderDetaiVO.getGameStartTime(),DateUtils.FULL_FORMAT_1);
        recordVO.setBetTime(TimeZoneUtils.parseDate4TimeZoneToTime(gemeStartime,DateUtils.FULL_FORMAT_1,shanghaiTimeZone));
        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(getAccount(singleV8OrderDetaiVO.getAccounts()));
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(singleV8OrderDetaiVO.getGameID());
        recordVO.setTransactionId(singleV8OrderDetaiVO.getGameID());
        BigDecimal finalProfit = Objects.nonNull(singleV8OrderDetaiVO.getProfit())?singleV8OrderDetaiVO.getProfit():BigDecimal.ZERO;
        BigDecimal finalAllBet = Objects.nonNull(singleV8OrderDetaiVO.getAllBet())?singleV8OrderDetaiVO.getAllBet():BigDecimal.ZERO;
        // 输赢
        recordVO.setWinLossAmount(finalProfit);
        // 输赢金额=派彩金额-投注
        recordVO.setPayoutAmount(finalAllBet.add(finalProfit));
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        String gameEndTime = DateUtils.convertDateToString(singleV8OrderDetaiVO.getGameEndTime(),DateUtils.FULL_FORMAT_1);
        recordVO.setSettleTime(TimeZoneUtils.parseDate4TimeZoneToTime(gameEndTime,DateUtils.FULL_FORMAT_1,shanghaiTimeZone));
        recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setOrderClassify(OrderStatusEnum.SETTLED.getCode());
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSONObject.toJSONString(singleV8OrderDetaiVO));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());
        // 有效流水用投注或输赢最小绝对值来计算有效投注
        BigDecimal validAmount = super.computerValidBetAmount(recordVO.getBetAmount(),recordVO.getWinLossAmount(), VenueTypeEnum.CHESS);
        recordVO.setValidAmount(validAmount);

        GameInfoPO gameInfoPO = paramToGameInfo.get(singleV8OrderDetaiVO.getKindID()+"");
        recordVO.setThirdGameCode(singleV8OrderDetaiVO.getKindID()+"");
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
        }
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

    public String getAccount(final String account){
        if(StringUtils.isEmpty(account)){
            return account;
        }
        int i = account.indexOf("_");
        if(i != -1){
            return account.substring(i+1);
        }
        return account;
    }


    private static Map<String,String> currenyMap = new HashMap<>(1);

    static {
        currenyMap.put(CurrencyEnum.USDT.getCode(), CurrencyEnum.USD.getCode());
        currenyMap.put(CurrencyEnum.KVND.getCode(), "VNDK");
    }



    public SeamlesswalletResp seamlessWallet(HttpServletRequest request) {
        String agent = request.getParameter("agent");
        String param = request.getParameter("param");
        String key = request.getParameter("key");
        String timestamp = request.getParameter("timestamp");
        log.info("v8获取账号余额agent={},param={},key={},timestamp={}",agent,param,key,timestamp);
        int status = com.alibaba.excel.util.StringUtils.isEmpty(agent)?0:Integer.parseInt(agent);
        if(com.alibaba.excel.util.StringUtils.isEmpty(agent) || com.alibaba.excel.util.StringUtils.isEmpty(param)){
            log.info(("v8参数为空"));
            return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,status);
        }
        VenueInfoVO venueInfoVO = getVenueInfoByMerchant(agent,VenueEnum.V8.getVenueCode());
        if(Objects.isNull(venueInfoVO)){
            log.info("v8 未查询到商户号");
            return SeamlesswalletResp.success(V8RespErrEnums.AGENT_EXIST_NOT,status);
        }
        JSONObject json = new JSONObject();

        try{
            String paramKey = Encrypt.AESDecrypt(param,venueInfoVO.getAesKey(),false);
            if(com.alibaba.excel.util.StringUtils.isEmpty(paramKey)){
                log.info("v8参数解密失败{}",param);
                return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,status);
            }
            String[] splitParams = StringUtil.split(paramKey,"&");
            for (String splitParam : splitParams) {
                String[] split = StringUtil.split(splitParam,"=");
                if(split.length == 2){
                    if(!com.alibaba.excel.util.StringUtils.isEmpty(split[0])  && split[0].equals("currency")){
                        if("VNDK".equals(split[1])){
                            json.put(split[0],CurrencyEnum.KVND.getCode());
                        }else{
                            json.put(split[0],split[1]);
                        }
                    }else{
                        json.put(split[0],split[1]);
                    }
                }
            }
            // 校验key是否一致
            String pKey = agent+timestamp+venueInfoVO.getMerchantKey();
            if(!Encrypt.MD5(pKey).equals(key)){
                log.info("v8 key不一致{},{}",Encrypt.MD5(pKey), key);
                return SeamlesswalletResp.success(V8RespErrEnums.MD5_KEY_ERROR,status);
            }
        }catch (Exception e){
            e.printStackTrace();
            return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,1001);
        }
        log.info("解析后的参数json={}",json);
        String type = json.getString("s");
        String account = json.getString("account");
        // 经与第三方确认，数组里面只有1条内容
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(account);
        casinoMemberReqVO.setVenueCode(VenueEnum.V8.getVenueCode());
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type, account, null);
        }
        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);
        if(Objects.isNull(userInfoVO)){
            log.error("v8 games queryUserInfoByAccount userName[{}] not find.",account);
            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type,account, null);
        }

        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = Lists.newArrayList(VenueEnum.V8.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type,account, null);
        }
        // 场馆关闭
        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
            log.info("{}:场馆未开启", VenueEnum.MARBLES.getVenueName());
            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,type,account, null);
        }
        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("v8 game locked userName{}", userInfoVO.getUserName());
            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,type,account, null);
        }

        log.info("v8 入参解析{}",json);
        switch (type){
            case "1001":
                return getBalance(json,casinoMember);
            case "1002":
                return placeBet(json,userInfoVO,casinoMember);
            case "1003":
                return returnBalance(json, casinoMember, userInfoVO);
            case "1004":
                return getOrderStatus(json, casinoMember, userInfoVO);
            case "1005":
                return cancelBet(json,casinoMember,userInfoVO);

        }
        return SeamlesswalletResp.success();
    }

    /**
     * 查询余额
     */
    public SeamlesswalletResp getBalance(JSONObject json, CasinoMemberVO casinoMember) {
        String account = json.getString("account");
        String s = json.getString("s");
        String venueCode = casinoMember.getVenueCode();
        List<String> venueCodes = Lists.newArrayList(VenueEnum.V8.getVenueCode());
        if (!venueCodes.contains(venueCode)){
            log.info("v8场馆不存在={}",venueCode);
            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,s,account, null);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        if (!Objects.isNull(userCenterCoin)) {
            balance = userCenterCoin.getTotalAmount();
        }

        if (venueMaintainClosed(venueCode,userCenterCoin.getSiteCode())) {
            log.info("v8场馆已关闭={}",venueCode);
            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,s,account, null);
        }

        balance.setScale(2, RoundingMode.DOWN);
        return SeamlesswalletResp.success(V8RespErrEnums.SUCCESS,s,account, balance.floatValue());
    }


    /**
     * 获取订单状态
     * @param json
     * @param casinoMember
     * @param userInfoVO
     * @return
     */
    public SeamlesswalletResp getOrderStatus(JSONObject json, CasinoMemberVO casinoMember , UserInfoVO userInfoVO){
        log.info("v8 获取订单状态{},{},{}",json,casinoMember,userInfoVO);
        String account = json.getString("account");
        String gameNo = json.getString("gameNo");
        String orderId = json.getString("orderId");
        String s = json.getString("s");
        try{
            UserCoinRecordRequestVO vo = new UserCoinRecordRequestVO();
            vo.setRemark(orderId);
            vo.setOrderNo(gameNo);
            List<UserCoinRecordVO> resp = getUserCoinRecords(vo);
            if (CollectionUtil.isEmpty(resp)) {
                log.info("V8获取订单状态,userCoinAddVO:{},{}", VenueEnum.V8.getVenueName(), vo);
                // 参考第三方文档查无订单返回4
                return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,4, s);
            }
            return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,1, s);
        }catch (Exception e){
            log.error("v8获取订单状态异常{},{},{}",json,casinoMember,userInfoVO,e);
            e.printStackTrace();
            // 参考第三方文档失败返回2
            return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,2, s);
        }
    }

    /**
     * 返还余额
     * 当我方请求返还余额时，在一定的时间内没有收到贵司正确的回应或没有任何的回应，该笔订单将会列为订单异常。
     *  若该笔订单是派彩失败的异常订单号，我方重发时就会通过s1003 将金额返还哦
     */
    public SeamlesswalletResp returnBalance(JSONObject json, CasinoMemberVO casinoMember , UserInfoVO userInfoVO) {
        log.info("v8 返还余额{},{},{}",json,casinoMember,userInfoVO);
        String account = json.getString("account");
        String money = json.getString("money");
        String orderId = json.getString("orderId");
        String gameId = json.getString("gameId");
        String kindId = json.getString("kindId");
        String s = json.getString("s");
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId+System.currentTimeMillis()));
        try {
            if (!rLock.tryLock()) {
                log.error("v8 errorBet error get locker error, req:{}", json);
                return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (Objects.isNull(userCenterCoin)) {
                log.info("{}:用户钱包,不存在:{}", VenueEnum.V8.getVenueName(), userInfoVO.getUserId());
                return SeamlesswalletResp.success(V8RespErrEnums.WALLET_NOT_EXIST,s, account, null);
            }

            BigDecimal amount = StringUtils.isEmpty(money)? BigDecimal.ZERO: new BigDecimal(money);
            CoinRecordResultVO coinRecordResultVO = updateBalanceCancelBet(userInfoVO, gameId, amount,orderId);
            SeamlesswalletResp resp = switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS,AMOUNT_LESS_ZERO -> {
                    UserCoinWalletVO coin = getUserCenterCoin(userInfoVO.getUserId());
                    BigDecimal balance = BigDecimal.ZERO;
                    if (!Objects.isNull(userCenterCoin)) {
                        balance = coin.getTotalAmount();
                    }
                    yield SeamlesswalletResp.successBet(V8RespErrEnums.SUCCESS, s,account, balance.floatValue());
                }
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS  ->{
                    yield SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,1);
                }
            };

            if(resp.getD().getCode() != 0){
                return resp;
            }
            return resp;
        }catch (Exception e){
            e.printStackTrace();
            return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
        }
    }

    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount,String orderId) {
        log.info("v8账变,orderId:{}, transferAmount:{}, transactionId:{}", orderId, transferAmount, transactionId);
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(orderId);
        userCoinAddVO.setVenueCode(VenueEnum.V8.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setThirdOrderNo(orderId);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }


    /**
     * 下注
     */
    public SeamlesswalletResp placeBet(JSONObject json, UserInfoVO userInfoVO, CasinoMemberVO casinoMember) {
        log.info("v8 下注placeBet下注入参{}",json);
        String account = json.getString("account");
        String orderId = json.getString("orderId");
        String gameId = json.getString("gameId");
        String money = json.getString("money");
        String kindId = json.getString("kindId");
        String s = json.getString("s");
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId));
        try {
            if (!rLock.tryLock()) {
                log.error("v8 errorBet error get locker error, req:{}", json);
                return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
            }

            if (venueMaintainClosed(VenueEnum.V8.getVenueCode(),userInfoVO.getSiteCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.V8.getVenueCode());
                return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,"1002",account, null);
            }


            BigDecimal amount = StringUtils.isEmpty(money)? BigDecimal.ZERO: new BigDecimal(money);


            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());

            // 检查余额
            if(!(userCoin.getCenterAmount().compareTo(BigDecimal.ZERO) > 0 && userCoin.getCenterAmount().compareTo(amount.abs()) >= 0)){
                log.info("v8 bet用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),money);
                return SeamlesswalletResp.success(V8RespErrEnums.INSUFFICIENT_BALANCE,s,account, amount.floatValue());
            }
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, gameId, amount,orderId,VenuePlatformConstants.V8);

            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS -> {
                    yield SeamlesswalletResp.success(V8RespErrEnums.SUCCESS,s,account, coinRecordResultVO.getCoinAfterBalance().floatValue());
                }
                case REPEAT_TRANSACTIONS -> SeamlesswalletResp.success(V8RespErrEnums.DUPLICATE_ORDER,s,account, coinRecordResultVO.getCoinAfterBalance().floatValue());
                case AMOUNT_LESS_ZERO, INSUFFICIENT_BALANCE, WALLET_NOT_EXIST ->{
                    yield SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
                }
                case FAIL -> SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
            };
        }catch (Exception e){
            log.error("v8 bet failed processAdjustBet error", e);
            e.printStackTrace();
            return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
        }
    }





    /**
     * 取消下注
     *
     * 已下注取消
     * 结算取消-派彩取消
     *
     * 1.当贵司成功扣款且在我方成功下注，但游戏判定为下注超时，就会发起1005取消下注通知，贵司必须处理不然玩家会有账变问题
     *
     * 2. 下注失败没有注单记录，所以连扣款都没有成功，则会发起1005取消下注通知。如贵司有回复成功，则重试的取消下注状态为成功
     *
     * 3. 如贵司一直未回复订单状态，就会进入重送，但超过15分钟后就不会再重送，将由人工进行处理
     *
     */
    public SeamlesswalletResp cancelBet(JSONObject json, CasinoMemberVO casinoMember, UserInfoVO userInfoVO) {
        log.info("v8cancelBet req:{}", json);
        String account = json.getString("account");
        String gameNo = json.getString("gameNo");
        String gameId = json.getString("gameId");
        String money = json.getString("money");
        String currency = json.getString("currency");
        String orderId = json.getString("orderId");
        String s = json.getString("s");
        String kindId = json.getString("kindId");
        log.info("v8取消下注 account:{},gameNo:{},gameId:{},money:{},currency:{},orderId:{}",account,gameNo,gameId,money,currency,orderId);
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId+System.currentTimeMillis()));
        try{
            if(!rLock.tryLock()){
                log.error("v8 error cancelBet error get locker error, req:");
                return SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,0);
            }
            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
            coinRecordRequestVO.setRemark(orderId);
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
            if(CollectionUtil.isEmpty(userCoinRecords) ){
                log.error("v8取消下注 订单查询为空 orderId:{}",orderId);
                return SeamlesswalletResp.successCancel(V8RespErrEnums.SUCCESS,s,0);
            }
            /*List<String> coinTypes = userCoinRecords.getData().stream().map(UserCoinRecordVO::getCoinType).toList();
            if (coinTypes.contains(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())){
                return SeamlesswalletResp.success(V8RespErrEnums.CANCEL_NOT_EXIST,s,account,null);
            }*/
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancelLocal(userInfoVO, gameNo, new BigDecimal(money).abs(),orderId);
            SeamlesswalletResp resp = switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS ->{
                    yield SeamlesswalletResp.successCancel(V8RespErrEnums.SUCCESS,s,0);
                }
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO ->{
                    yield SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,1);
                }
            };

            if(resp.getD().getCode() != 0){
                return resp;
            }

            // 第三方回复： 没有取消，重算

            // 拉单的数状态都是已结算，故当取消时， 更新注单的状态
            /*OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
            orderRecordMqVO.setThirdOrderId(gameId);
            if("VNDK".equals(currency)){
                orderRecordMqVO.setCurrency("KVND");
            }else{
                orderRecordMqVO.setCurrency(currency);
            }
            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
            orderRecordMqVO.setVenueCode(VenueEnum.V8.getVenueCode());
            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);*/
            return resp;
        }catch (Exception e){
            log.error("v8取消下注失败，失败原因为{}",e.getMessage());
            e.printStackTrace();
            return SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,0);
        }
    }







    protected CoinRecordResultVO updateBalanceBetCancelLocal(UserInfoVO userInfoVO, String orderNo, BigDecimal amount,String remark) {
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
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.V8.getVenueCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setThirdOrderNo(remark);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

}
