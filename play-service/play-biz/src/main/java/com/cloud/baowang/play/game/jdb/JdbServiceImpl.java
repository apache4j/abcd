//package com.cloud.baowang.play.game.jdb;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
//import com.cloud.baowang.common.core.enums.ResultCode;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
//import com.cloud.baowang.common.core.utils.*;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
//import com.cloud.baowang.play.api.enums.play.WinLossEnum;
//import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
//import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
//import com.cloud.baowang.play.api.vo.third.LoginVO;
//import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.constants.ServiceType;
//import com.cloud.baowang.play.game.base.GameBaseService;
//import com.cloud.baowang.play.game.base.GameService;
//import com.cloud.baowang.play.game.jdb.enums.JDBCurrencyEnum;
//import com.cloud.baowang.play.game.jdb.enums.JDBGameTypeEnums;
//import com.cloud.baowang.play.game.jdb.enums.JDBLangEnum;
//import com.cloud.baowang.play.game.jdb.utils.AESEncrypt;
//import com.cloud.baowang.play.game.jdb.vo.JdbActionVO;
//import com.cloud.baowang.play.game.jdb.vo.JdbGameHistory;
//import com.cloud.baowang.play.po.CasinoMemberPO;
//import com.cloud.baowang.play.po.GameInfoPO;
//import com.cloud.baowang.play.po.VenueInfoPO;
//import com.cloud.baowang.play.service.OrderRecordProcessService;
//import com.cloud.baowang.play.service.VenueInfoService;
//import com.cloud.baowang.play.task.pulltask.jdb.vo.JdbActionVo;
//import com.cloud.baowang.play.vo.GameLoginVo;
//import com.cloud.baowang.play.vo.VenuePullParamVO;
//import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
//import com.google.common.collect.Lists;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//
//@Slf4j
//@RequiredArgsConstructor
//@Service(ServiceType.GAME_THIRD_API_SERVICE + VenueCodeConstants.JDB)
//public class JdbServiceImpl extends GameBaseService implements GameService {
//
//
//    private final OrderRecordProcessService orderRecordProcessService;
//    private final static String SUCCESS_CODE = "0000";
//
//    public static final String URI = "/apiRequest.do";
//
//    @Value("${play.server.userAccountPrefix:Utest_}")
//    private String userAccountPrefix;
//
//    //    @Value("${play.server.jdb:domain:}")
//    private static final String dc = "WT";
//    //
////    @Value("${play.server.jdb:key:}")
//    private static final String KEY = "9397edcba20ef10b";
//    //
////
////    @Value("${play.server.jdb:iv:}")
//    private static final String IV = "9ca2a2050e68ff85";
//
//    private static final String PARENT = "wtrmbag";
//
//    private final VenueInfoService venueInfoService;
//
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
//        String currencyCode = venueDetailVO.getPullCurrencyCodeList().get(0);
//        String jdbCurrencyCode = JDBCurrencyEnum.getJdbCodeByCode(currencyCode);
//        if (jdbCurrencyCode == null) {
//            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
//        }
//        String parent = venueDetailVO.getMerchantNo();
//        String key = venueDetailVO.getAesKey();
//        String dc = venueDetailVO.getBetKey();
//        String iv = venueDetailVO.getMerchantKey();
//        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
//            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
//        }
//
//        String url = venueDetailVO.getApiUrl() + URI;
//        JdbActionVO actionVO = new JdbActionVO();
//        actionVO.setAction(21);
//        actionVO.setTs(System.currentTimeMillis());
//        actionVO.setParent(parent);
//        actionVO.setLang(JDBLangEnum.conversionLang(loginVO.getLanguageCode()));
//        actionVO.setCurrency(jdbCurrencyCode);
//        actionVO.setUid(casinoMemberVO.getVenueUserAccount());
//        List<String> gameInfos = Arrays.asList(loginVO.getGameCode().split("_"));
//        actionVO.setGType(gameInfos.get(0).toLowerCase());
//        actionVO.setMType(gameInfos.get(1).toLowerCase());
//        String data = JSONObject.toJSONString(actionVO);
//        log.info("JDB三方登录参数 : " + data);
//        String x = AESEncrypt.encrypt(data, key, iv);
//        if (x == null) {
//            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("dc", dc);
//        params.put("x", x);
//        String rsp = HttpClientHandler.post(url, null, params);
//        if (rsp != null) {
//            JSONObject jsonObject = JSONObject.parseObject(rsp);
//            if (jsonObject.getString("status").equals(SUCCESS_CODE)) {
//                String source = jsonObject.getString("path");
//                String userAccount = loginVO.getUserAccount();
//                String venueCode = venueDetailVO.getVenueCode();
//                GameLoginVo gameLoginVo = GameLoginVo.builder()
//                        .source(source)
//                        .type(GameLoginTypeEnums.URL.getType())
//                        .userAccount(userAccount)
//                        .venueCode(venueCode).build();
//                return ResponseVO.success(gameLoginVo);
//            } else {
//                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
//            }
//        } else {
//            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
//        }
//
//    }
//
//    public List<ShDeskInfoVO> queryGameList() {
//        List<ShDeskInfoVO> resultList = Lists.newArrayList();
//        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
//                .eq(VenueInfoPO::getVenueCode, VenueEnum.JDB.getVenueCode())
//                .last(" order by id  limit 1 "));
//        VenueInfoVO venueInfoVO = new VenueInfoVO();
//        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
//        ResponseVO<List<JSONObject>> jsonObjectResponseVO = queryGameList(null, null, venueInfoVO);
//        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
//        for (JSONObject item : jsonList) {
//            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
//        }
//        return resultList;
//    }
//
//
//    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
//        List<JSONObject> resultList = Lists.newArrayList();
//        JSONObject jsonObject = queryGameList(venueInfoVO);
//        if (jsonObject == null) {
//            return ResponseVO.success(resultList);
//        }
//        //deskNumber = gType_mType
//        JSONArray dataArray = jsonObject.getJSONArray("data");
//        for (int i = 0; i < dataArray.size(); i++) {
//            JSONObject obj = dataArray.getJSONObject(i);
//            int gType = obj.getIntValue("gType");
//            JSONArray gameList = obj.getJSONArray("list");
//            for (int j = 0; j < gameList.size(); j++) {
//                JSONObject game = gameList.getJSONObject(j);
//                //170003 掼蛋 (此游戏仅支援RMB)
//                int mType = game.getIntValue("mType");
//                if (mType == 170003) {
//                    continue;
//                }
//                String name = game.getString("name");
//                String gameCode = gType + "_" + mType;
//                JSONObject gameJson = new JSONObject();
//                gameJson.put("deskName", name);
//                gameJson.put("deskNumber", gameCode);
//                resultList.add(gameJson);
//            }
//        }
//        return ResponseVO.success(resultList);
//    }
//
//    public JSONObject queryGameList(VenueInfoVO venueInfoVO) {
//        String parent = venueInfoVO.getMerchantNo();
//        String key = venueInfoVO.getAesKey();
//        String dc = venueInfoVO.getBetKey();
//        String iv = venueInfoVO.getMerchantKey();
//        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
//        String apiUrl = venueInfoVO.getApiUrl() + URI;
//        JdbActionVO actionVO = new JdbActionVO();
//        actionVO.setAction(49);
//        actionVO.setTs(System.currentTimeMillis());
//        actionVO.setParent(parent);
//        actionVO.setLang(JDBLangEnum.CN.getCode());
//
//        String data = JSONObject.toJSONString(actionVO);
//        log.info("JDB三方拉取游戏参数 : " + data);
//        String x = AESEncrypt.encrypt(data, key, iv);
//        if (x == null) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("dc", dc);
//        params.put("x", x);
//        String rsp = HttpClientHandler.post(apiUrl, null, params);
//        if (rsp != null) {
//            return JSONObject.parseObject(rsp);
//        }
//        return null;
//    }
//
//
//    @Override
//    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
//        String parent = venueInfoVO.getMerchantNo();
//        String key = venueInfoVO.getAesKey();
//        String dc = venueInfoVO.getBetKey();
//        String iv = venueInfoVO.getMerchantKey();
//        String apiUrl = venueInfoVO.getApiUrl() + URI;
//        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
//            log.info("JDB拉单参数错误 : " + venueInfoVO);
//            throw new BaowangDefaultException(ResultCode.PULL_TIME_ERR);
//        }
//        boolean pullType = venuePullParamVO.getPullType();
//        JdbActionVo actionVo = new JdbActionVo();
//        if (pullType) {
//            actionVo.setAction(29);
//        } else {
//            actionVo.setAction(64);
//        }
//        actionVo.setTs(System.currentTimeMillis());
//        actionVo.setParent(parent);
//        actionVo.setStarttime(TimeZoneUtils.formatTimestampToJdbDate(venuePullParamVO.getStartTime()));
//        actionVo.setEndtime(TimeZoneUtils.formatTimestampToJdbDate(venuePullParamVO.getEndTime()));
//        String data = JSONObject.toJSONString(actionVo);
////        log.info("JDB三方拉单参数 : " + data);
//        String x = AESEncrypt.encrypt(data, key, iv);
//        if (x == null) {
//            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("dc", dc);
//        params.put("x", x);
//        Map<String, String> header = new HashMap<>();
//        header.put("Accept-Encoding", "gzip");
//        String rsp = HttpClientHandler.post(apiUrl, header, params);
//        JSONObject jsonObject = JSONObject.parseObject(rsp);
//        if (jsonObject == null || !jsonObject.getString("status").equals(SUCCESS_CODE)) {
//            log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueInfoVO.getVenueCode(), rsp,data,System.currentTimeMillis());
//            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
//        }
//        log.info("JDB三方拉单返回结果 : {}  代理ID : {} 请求 : {}",rsp,parent, data);
//        JSONArray dataArray = jsonObject.getJSONArray("data");
//        if (dataArray.isEmpty()) {
//            return ResponseVO.success();
//        }
//        List<JdbGameHistory> gameHistoryList = dataArray.toJavaList(JdbGameHistory.class);
//
//        handleRemoteOrder(gameHistoryList, venueInfoVO);
//
//        return ResponseVO.success();
//    }
//
//    private void handleRemoteOrder(List<JdbGameHistory> gameHistoryList, VenueInfoVO venueInfoVO) {
//        //三方账号
//        List<String> thirdAccounts = gameHistoryList.stream().map(s -> formatString(s.getPlayerId())).distinct().toList();
//
//        Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(thirdAccounts, venueInfoVO.getVenuePlatform());
//
//        //userInfo
//        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
//        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);
//
//        // 用户登录信息
//        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);
//
//        //vip返水配置
//        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.JDB.getVenueCode());
//
//        //站点信息
//        Map<String, String> siteNameMap = getSiteNameMap();
//
//        //游戏信息
//        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.JDB.getVenueCode());
//
//        List<OrderRecordVO> list = new ArrayList<>();
//
//        for (JdbGameHistory order : gameHistoryList) {
//            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(formatString(order.getPlayerId()));
//            if (casinoMemberVO == null) {
//                log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), formatString(order.getPlayerId()));
//                continue;
//            }
//            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
//            if (userInfoVO == null) {
//                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
//                continue;
//            }
//
//            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
//
//            // 映射原始注单
//            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
//            recordVO.setVenueType(VenueEnum.JDB.getType().getCode());
//            list.add(recordVO);
//            if (list.size() == 300) {
//                orderRecordProcessService.orderProcess(list);
//                list.clear();
//            }
//
//        }
//        // 订单处理
//        if (CollectionUtil.isNotEmpty(list)) {
//            orderRecordProcessService.orderProcess(list);
//        }
//
//    }
//
//
//    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, JdbGameHistory order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
//        OrderRecordVO recordVO = new OrderRecordVO();
//        recordVO.setUserAccount(userInfoVO.getUserAccount());
//        recordVO.setUserId(userInfoVO.getUserId());
//        recordVO.setUserName(userInfoVO.getUserName());
//        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
//        recordVO.setAgentId(userInfoVO.getSuperAgentId());
//        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
//        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
//        recordVO.setBetAmount(BigDecimal.valueOf(order.getBet()).abs());
//
//        Long betTime = TimeZoneUtils.convertToTimestamp(order.getGameDate(), "GMT+8", DateUtils.DATE_FORMAT_JDB);
//        Long updatedTime = TimeZoneUtils.convertToTimestamp(order.getLastModifyTime(), "GMT+8", DateUtils.DATE_FORMAT_JDB);
//        recordVO.setBetTime(betTime);
//        recordVO.setSettleTime(updatedTime);
//
//        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
//        recordVO.setVenueCode(venueInfoVO.getVenueCode());
//        recordVO.setCasinoUserName(formatString(order.getPlayerId()));
//        if (userLoginInfoVO != null) {
//            recordVO.setBetIp(userLoginInfoVO.getIp());
//            if (userLoginInfoVO.getLoginTerminal() != null) {
//                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
//            }
//        }
//        recordVO.setCurrency(userInfoVO.getMainCurrency());
//        recordVO.setOrderId(OrderUtil.getGameNo());
//
//        recordVO.setThirdOrderId(String.valueOf(order.getHistoryId()));
//        recordVO.setTransactionId(String.valueOf(order.getHistoryId()));
//        recordVO.setOrderInfo(String.valueOf(order.getHistoryId()));
//
//        BigDecimal netWin = BigDecimal.valueOf(order.getTotal());
//        recordVO.setResultList(getWinLossResult(netWin));
//        recordVO.setWinLossAmount(netWin);
//        recordVO.setPayoutAmount(BigDecimal.valueOf(order.getWin()).abs());
//
//        recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
//        recordVO.setOrderClassify(OrderStatusEnum.SETTLED.getCode());
//        recordVO.setCreatedTime(System.currentTimeMillis());
//        recordVO.setUpdatedTime(System.currentTimeMillis());
//        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
//        recordVO.setChangeStatus(0);
//        recordVO.setReSettleTime(0L);
//        recordVO.setParlayInfo(JSON.toJSONString(order));
//        recordVO.setSiteCode(userInfoVO.getSiteCode());
//        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
//        recordVO.setVipRank(userInfoVO.getVipRank());
//
//        BigDecimal validBetAmount = recordVO.getBetAmount();
//        recordVO.setValidAmount(validBetAmount.abs());
//
//        String gameType = String.valueOf(order.getGType());
//        recordVO.setRoomType(gameType);
//        recordVO.setRoomTypeName(JDBGameTypeEnums.nameOfCode(gameType));
//
//        String gameCode = order.getGType() + "_" + order.getMtype();
//        recordVO.setThirdGameCode(gameCode);
//        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
//        if (gameInfoPO != null) {
//            recordVO.setGameId(gameInfoPO.getGameId());
//            recordVO.setGameName(gameInfoPO.getGameI18nCode());
//            recordVO.setPlayType(gameInfoPO.getGameI18nCode());
//        }
//        return recordVO;
//    }
//
//
//    public static void main(String[] args) {
////        queryGameList();
////        loginTest();
//        String rsp = " {\"status\":\"0000\",\"path\":\"https://game.jdb711.com/?lang=cn&homeUrl=home&isAPP=false&gameType=14&mType=14091&gName=PiggyBank_35d6f36&mute=0&x=e9tkQRED2CAkCfyI2zlvT27EHhHQwfE6lSNY1ZWTTQ_ihxlSpojFaYGe-xTpooGGIotI7_Z4S4e-asJvzNsouDJv8gB8XKTDPrFNtr6ue44\"}\n";
//        JSONObject parse = JSONObject.parse(rsp);
//        String status = parse.getString("status");
//        System.out.println("JdbServiceImpl.main --- " + status.equals(SUCCESS_CODE));
//
//
//    }
//
//
//    public static void loginTest() {
//
//        String jdbCurrencyCode = JDBCurrencyEnum.CNY.getJdbCode();
//        if (jdbCurrencyCode == null) {
//            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
//        }
//
//        String url = "https://api.jdb711.com/" + URI;
//        JdbActionVO actionVO = new JdbActionVO();
//        actionVO.setAction(21);
//        actionVO.setTs(System.currentTimeMillis());
//        actionVO.setParent(PARENT);
//        actionVO.setLang(JDBLangEnum.CN.getCode());
//        actionVO.setCurrency(jdbCurrencyCode);
//        actionVO.setGType(jdbCurrencyCode);
//        actionVO.setGType("0");
//        actionVO.setMType("14088");
//        actionVO.setUid("45623172");
//        String data = JSONObject.toJSONString(actionVO);
//        log.info("JDB三方登录参数 : " + data);
//        String x = AESEncrypt.encrypt(data, KEY, IV);
//        if (x == null) {
//            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
//        }
//        Map<String, String> params = new HashMap<>();
//        params.put("dc", dc);
//        params.put("x", x);
//        String rsp = HttpClientHandler.post(url, null, params);
//        System.out.println("JdbServiceImpl.loginTest ---- " + rsp);
//    }
//
//    public String formatString(String input) {
//        if (StringUtils.isEmpty(input)) {
//            return input;
//        }
//        // 分割字符串为数组，按 "_" 切分
//        String[] parts = input.split("_");
//
//        if (parts.length == 2) {
//            return userAccountPrefix.concat(parts[1]);
//        }
//        return null;
//    }
//
//
//    private String getWinLossResult(BigDecimal winLossAmount) {
//        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
//                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
//                : WinLossEnum.TIE.getName();
//
//    }
//
//}
