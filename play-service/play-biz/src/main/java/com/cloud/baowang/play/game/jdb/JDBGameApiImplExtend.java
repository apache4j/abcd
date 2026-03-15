package com.cloud.baowang.play.game.jdb;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.jdb.JDBErrorEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.play.WinLossEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.jdb.JdbActionVO;
import com.cloud.baowang.play.api.vo.jdb.JdbGameHistory;
import com.cloud.baowang.play.api.vo.jdb.req.*;
import com.cloud.baowang.play.api.vo.jdb.rsp.JDBBaseRsp;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.jdb.constant.JDBConstant;
import com.cloud.baowang.play.game.jdb.enums.JDBCurrencyEnum;
import com.cloud.baowang.play.game.jdb.enums.JDBGameTypeEnums;
import com.cloud.baowang.play.game.jdb.enums.JDBLangEnum;
import com.cloud.baowang.play.game.jdb.utils.AESDecrypt;
import com.cloud.baowang.play.game.jdb.utils.AESEncrypt;
import com.cloud.baowang.play.game.jdb.utils.JDBCryptoConfig;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.jdb.vo.JdbActionVo;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.JDBUserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.JDB)
public class JDBGameApiImplExtend extends GameBaseService implements GameService {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;


    private final UserCoinRecordApi userCoinRecordApi;

    private final JDBCryptoConfig jdbCryptoConfig;


    private final OrderRecordProcessService orderRecordProcessService;
    private final static String SUCCESS_CODE = "0000";

    public static final String URI = "/apiRequest.do";

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private final VenueInfoService venueInfoService;


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        String currencyCode = venueDetailVO.getPullCurrencyCodeList().get(0);
        String jdbCurrencyCode = JDBCurrencyEnum.getJdbCodeByCode(currencyCode);
        if (jdbCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String parent = venueDetailVO.getMerchantNo();
        String key = venueDetailVO.getAesKey();
        String dc = venueDetailVO.getBetKey();
        String iv = venueDetailVO.getMerchantKey();
        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        String url = venueDetailVO.getApiUrl() + URI;
        JdbActionVO actionVO = new JdbActionVO();
        actionVO.setAction(21);
        actionVO.setTs(System.currentTimeMillis());
        actionVO.setParent(parent);
        actionVO.setLang(JDBLangEnum.conversionLang(loginVO.getLanguageCode()));
        actionVO.setCurrency(jdbCurrencyCode);
        actionVO.setUid(casinoMemberVO.getVenueUserAccount());
        List<String> gameInfos = Arrays.asList(loginVO.getGameCode().split("_"));
        actionVO.setGType(gameInfos.get(0).toLowerCase());
        actionVO.setMType(gameInfos.get(1).toLowerCase());
        String data = JSONObject.toJSONString(actionVO);
        log.info("JDB三方登录参数 : " + data);
        String x = AESEncrypt.encrypt(data, key, iv);
        if (x == null) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }
        Map<String, String> params = new HashMap<>();
        params.put("dc", dc);
        params.put("x", x);
        String rsp = HttpClientHandler.post(url, null, params);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getString("status").equals(SUCCESS_CODE)) {
                String source = jsonObject.getString("path");
                String userAccount = loginVO.getUserAccount();
                String venueCode = venueDetailVO.getVenueCode();
                GameLoginVo gameLoginVo = GameLoginVo.builder()
                        .source(source)
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(userAccount)
                        .venueCode(venueCode).build();
                return ResponseVO.success(gameLoginVo);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

    }


    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.JDB.getVenueCode())
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


    public JSONObject queryGameList(VenueInfoVO venueInfoVO) {
        String parent = venueInfoVO.getMerchantNo();
        String key = venueInfoVO.getAesKey();
        String dc = venueInfoVO.getBetKey();
        String iv = venueInfoVO.getMerchantKey();
        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        String apiUrl = venueInfoVO.getApiUrl() + URI;
        JdbActionVO actionVO = new JdbActionVO();
        actionVO.setAction(49);
        actionVO.setTs(System.currentTimeMillis());
        actionVO.setParent(parent);
        actionVO.setLang(JDBLangEnum.CN.getCode());

        String data = JSONObject.toJSONString(actionVO);
        log.info("JDB三方拉取游戏参数 : " + data);
        String x = AESEncrypt.encrypt(data, key, iv);
        if (x == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        Map<String, String> params = new HashMap<>();
        params.put("dc", dc);
        params.put("x", x);
        String rsp = HttpClientHandler.post(apiUrl, null, params);
        if (rsp != null) {
            return JSONObject.parseObject(rsp);
        }
        return null;
    }



    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        JSONObject jsonObject = queryGameList(venueInfoVO);
        if (jsonObject == null) {
            return ResponseVO.success(resultList);
        }
        //deskNumber = gType_mType
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            int gType = obj.getIntValue("gType");
            JSONArray gameList = obj.getJSONArray("list");
            for (int j = 0; j < gameList.size(); j++) {
                JSONObject game = gameList.getJSONObject(j);
                //170003 掼蛋 (此游戏仅支援RMB)
                int mType = game.getIntValue("mType");
                if (mType == 170003) {
                    continue;
                }
                String name = game.getString("name");
                String gameCode = gType + "_" + mType;
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", name);
                gameJson.put("deskNumber", gameCode);
                resultList.add(gameJson);
            }
        }
        return ResponseVO.success(resultList);
    }



    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        String parent = venueInfoVO.getMerchantNo();
        String key = venueInfoVO.getAesKey();
        String dc = venueInfoVO.getBetKey();
        String iv = venueInfoVO.getMerchantKey();
        String apiUrl = venueInfoVO.getApiUrl() + URI;
        if (StringUtils.isEmpty(parent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(dc) || StringUtils.isEmpty(iv)) {
            log.info("JDB拉单参数错误 : " + venueInfoVO);
            throw new BaowangDefaultException(ResultCode.PULL_TIME_ERR);
        }
        boolean pullType = venuePullParamVO.getPullType();
        JdbActionVo actionVo = new JdbActionVo();
        if (pullType) {
            actionVo.setAction(29);
        } else {
            actionVo.setAction(64);
        }
        actionVo.setTs(System.currentTimeMillis());
        actionVo.setParent(parent);
        actionVo.setStarttime(TimeZoneUtils.formatTimestampToJdbDate(venuePullParamVO.getStartTime()));
        actionVo.setEndtime(TimeZoneUtils.formatTimestampToJdbDate(venuePullParamVO.getEndTime()));
        String data = JSONObject.toJSONString(actionVo);
//        log.info("JDB三方拉单参数 : " + data);
        String x = AESEncrypt.encrypt(data, key, iv);
        if (x == null) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }
        Map<String, String> params = new HashMap<>();
        params.put("dc", dc);
        params.put("x", x);
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Encoding", "gzip");
        String rsp = HttpClientHandler.post(apiUrl, header, params);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        if (jsonObject == null || !jsonObject.getString("status").equals(SUCCESS_CODE)) {
            log.error("{} 拉取注返回异常，返回：{} :请求: {} 当前时间 : {} ", venueInfoVO.getVenueCode(), rsp,data,System.currentTimeMillis());
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        log.info("JDB三方拉单返回结果 : {}  代理ID : {} 请求 : {}",rsp,parent, data);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        if (dataArray.isEmpty()) {
            return ResponseVO.success();
        }
        List<JdbGameHistory> gameHistoryList = dataArray.toJavaList(JdbGameHistory.class);

        handleRemoteOrder(gameHistoryList, venueInfoVO);

        return ResponseVO.success();
    }

    private void handleRemoteOrder(List<JdbGameHistory> gameHistoryList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> thirdAccounts = gameHistoryList.stream().map(s -> formatString(s.getPlayerId())).distinct().toList();

        Map<String, CasinoMemberPO> casinoMemberMap = getCasinoMemberByUsers(thirdAccounts, venueInfoVO.getVenuePlatform());

        //userInfo
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.JDB.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.JDB.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (JdbGameHistory order : gameHistoryList) {
            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(formatString(order.getPlayerId()));
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", venueInfoVO.getVenueCode(), formatString(order.getPlayerId()));
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), casinoMemberVO.getUserAccount());
                continue;
            }

            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            recordVO.setVenueType(VenueEnum.JDB.getType().getCode());
            list.add(recordVO);
            if (list.size() == 300) {
                orderRecordProcessService.orderProcess(list);
                list.clear();
            }

        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }


    private OrderRecordVO parseRecords(VenueInfoVO venueInfoVO, JdbGameHistory order, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(BigDecimal.valueOf(order.getBet()).abs());

        Long betTime = TimeZoneUtils.convertToTimestamp(order.getGameDate(), "GMT+8", DateUtils.DATE_FORMAT_JDB);
        Long updatedTime = TimeZoneUtils.convertToTimestamp(order.getLastModifyTime(), "GMT+8", DateUtils.DATE_FORMAT_JDB);
        recordVO.setBetTime(betTime);
        recordVO.setSettleTime(updatedTime);

        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        recordVO.setVenueCode(venueInfoVO.getVenueCode());
        recordVO.setCasinoUserName(formatString(order.getPlayerId()));
        if (userLoginInfoVO != null) {
            recordVO.setBetIp(userLoginInfoVO.getIp());
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setOrderId(OrderUtil.getGameNo());

        recordVO.setThirdOrderId(String.valueOf(order.getHistoryId()));
        recordVO.setTransactionId(String.valueOf(order.getHistoryId()));
        recordVO.setOrderInfo(String.valueOf(order.getHistoryId()));

        BigDecimal netWin = BigDecimal.valueOf(order.getTotal());
        recordVO.setResultList(getWinLossResult(netWin));
        recordVO.setWinLossAmount(netWin);
        recordVO.setPayoutAmount(BigDecimal.valueOf(order.getWin()).abs());

        recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
        recordVO.setOrderClassify(OrderStatusEnum.SETTLED.getCode());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        recordVO.setChangeStatus(0);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(order));
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
        recordVO.setVipRank(userInfoVO.getVipRank());

        BigDecimal validBetAmount = recordVO.getBetAmount();
        recordVO.setValidAmount(validBetAmount.abs());

        String gameType = String.valueOf(order.getGType());
        recordVO.setRoomType(gameType);
        recordVO.setRoomTypeName(JDBGameTypeEnums.nameOfCode(gameType));

        String gameCode = order.getGType() + "_" + order.getMtype();
        recordVO.setThirdGameCode(gameCode);
        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
        if (gameInfoPO != null) {
            recordVO.setGameId(gameInfoPO.getGameId());
            recordVO.setGameName(gameInfoPO.getGameI18nCode());
            recordVO.setPlayType(gameInfoPO.getGameI18nCode());
        }
        return recordVO;
    }


    public String formatString(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        // 分割字符串为数组，按 "_" 切分
        String[] parts = input.split("_");

        if (parts.length == 2) {
            return userAccountPrefix.concat(parts[1]);
        }
        return null;
    }

    private String getWinLossResult(BigDecimal winLossAmount) {
        return winLossAmount.signum() < 0 ? WinLossEnum.LOSS.getName()
                : winLossAmount.signum() > 0 ? WinLossEnum.WIN.getName()
                : WinLossEnum.TIE.getName();

    }








    public JDBBaseRsp getBalance(JSONObject reqData) {
//        log.info("JDB getBalance : "+reqData);
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
        if (isValid != null) {
            return isValid;
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (ObjectUtil.isNull(userCenterCoin)) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            return JDBBaseRsp.success(BigDecimal.ZERO.doubleValue());
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
        return JDBBaseRsp.success(totalAmount.setScale(2,RoundingMode.DOWN).doubleValue());
    }

    public JDBBaseRsp checkRequestValid(UserInfoVO userInfoVO, JSONObject jsonData,boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }


//        if (venueMaintainClosed(VenueEnum.JDB.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("场馆未开启:{} ", VenueEnum.JDB.getVenueCode());
//            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
//        }



        String userCurrencyCode = userInfoVO.getMainCurrency();
        String jdbCode = JDBCurrencyEnum.enumOfCode(userCurrencyCode).getJdbCode();
        String currency = jsonData.getString("currency");
        String gameCode = jsonData.getString("gType")+"_"+jsonData.getString("mType");

        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                .venueCode(VenueEnum.JDB.getVenueCode())
                .currencyCode(userInfoVO.getMainCurrency()).build();
        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.JDB.getVenueCode());
            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
        }

        if (!isGameAvailable(userInfoVO.getSiteCode(),gameCode,VenueEnum.JDB.getVenueCode(),userCurrencyCode)){
            log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), gameCode);
            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
        }

        if (!jdbCode.equals(currency)) {
            log.info("币种不支持 : {} : 用户 : {}", userCurrencyCode, userInfoVO.getUserId());
            return JDBBaseRsp.failed(JDBErrorEnum.CURRENCY_UNSUPPORTED);
        }

        if (isBetting){
            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.JDB.getVenueCode());
                return JDBBaseRsp.failed(JDBErrorEnum.USER_LOCKED);
            }

        }
        return null;
    }

    /**
     * 游戏开启/关闭校验
     * @param siteCode
     * @param gameCode
     * @param venueCode
     * @return
     */

    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode,String currencyCode) {
        // 判断游戏是否配置
        GameInfoPO gameInfo = getGameInfoByCode(siteCode,gameCode,venueCode);
        if (gameInfo == null) {
            log.error("场馆:{} 没有配置游戏，游戏：{}", venueCode, gameCode);
            return false;
        }
        // 判断游戏是否开启
        if (!Objects.equals(gameInfo.getStatus(), StatusEnum.OPEN.getCode())) {
            log.error("场馆:{} 游戏关闭，游戏：{}", venueCode, gameCode);
            return false;
        }
        //币种
        List<String> currencyList = Arrays.asList(gameInfo.getCurrencyCode().split(CommonConstant.COMMA));
        if (!currencyList.contains(currencyCode)) {
            log.error("场馆:{} 游戏：{} 币种不支持 : {}", venueCode, gameCode, currencyCode);
            return false;
        }
        return true;
    }

    public String userCheck(JSONObject reqData) {
        String userAccount = reqData.getString("uid");
        return userAccount.replaceAll("^[a-zA-Z]+_", "");
    }

    public static void main(String[] args) {
        String userAccount = "prod_72969005";
        String result = userAccount.replaceAll("^[a-zA-Z]+_", "");
        System.out.println("JDBGameApiExtendImpl.main - "+result);
    }

    /**
     * 下注并结算
     *
     * @param reqData
     * @return
     */
    public JDBBaseRsp betNSettle(JSONObject reqData) {
        JDBBetNSettleReq req = reqData.toJavaObject(JDBBetNSettleReq.class);
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,true);
        if (isValid != null) {
            return isValid;
        }

        //最小下注额
        BigDecimal minBalance = BigDecimal.valueOf(req.getMb()).abs();
        //下注额 需取正
        BigDecimal betBalance = BigDecimal.valueOf(req.getBet()).abs();
        //游戏赢分 <派彩> 需取正
        BigDecimal win = BigDecimal.valueOf(req.getWin()).abs();

        BigDecimal netWin = BigDecimal.valueOf(req.getNetWin()).abs();
//        log.info(" betNSettle : " + reqData);

        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        if (userCoin == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        BigDecimal totalAmount = userCoin.getTotalAmount();
        String orderId = req.getHistoryId();
        //bet = 0,奖励
        if (betBalance.compareTo(BigDecimal.ZERO)==0){
            //"bet":0,"win":0,"netWin":0
            if (win.compareTo(BigDecimal.ZERO)==0 || netWin.compareTo(BigDecimal.ZERO)==0){
                return JDBBaseRsp.success(totalAmount.setScale(2,RoundingMode.DOWN).doubleValue());
            }else {
                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransferId());
                if (settleResult != null ) {
                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())
                            || UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())){
                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
                    }else {
                        log.error(" JDB道具奖励异常 : 请求数据 : " + reqData + " 结算数据 : " + settleResult);
                        return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
                    }
                }else {
                    log.error(" JDB道具奖励异常 : 请求数据 : " + reqData);
                    return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
                }
            }

        }
        // 玩家余额 < bet 請返回錯誤代碼 6006.
        if (totalAmount.subtract(betBalance).compareTo(BigDecimal.ZERO) < 0 || totalAmount.subtract(minBalance).compareTo(BigDecimal.ZERO) < 0) {
            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH);
        }

        //生成 下注/结算账变
        //查询账变
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(userCoinRecords)) {
            //重复投注
            return JDBBaseRsp.failed(JDBErrorEnum.DUPLICATE_TRANSACTION);
        }

        CoinRecordResultVO betResult = this.handleBet(userInfoVO, orderId, betBalance, req.getTransferId());
        if (betResult != null) {
            UpdateBalanceStatusEnums betStatus = betResult.getResultStatus();
            if (UpdateBalanceStatusEnums.SUCCESS.equals(betStatus)) {
                //下注成功,处理结算
                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransferId());
                if (settleResult != null ) {
                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())){
                        //都处理成功
                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
                        //玩家余额 < mb 则此笔请求应回覆 6006
//                        boolean isEnough = coinAfterBalance.subtract(minBalance).compareTo(BigDecimal.ZERO) > 0;
//                        if (isEnough) {
//                            return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
//                        } else {
//                            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH, coinAfterBalance.doubleValue());
//                        }
                    }else if (UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())) {
                        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
                        BigDecimal coinAfterBalance = userCenterCoin.getTotalAmount();
                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
//                        boolean isEnough = coinAfterBalance.subtract(minBalance).compareTo(BigDecimal.ZERO) > 0;
//                        if (isEnough) {
//                            return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
//                        } else {
//                            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH, coinAfterBalance.doubleValue());
//                        }
                    }else {
                        return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
                    }
                } else {
                    //取消下注订单
                    CoinRecordResultVO cancelBetResult = this.handleCancelBet(userInfoVO, orderId, betBalance, req.getTransferId());
                    if (cancelBetResult == null || !cancelBetResult.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
                        log.error(" JDB下注并结算 : 异常 订单号 : " + orderId);
                    }
                    return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
                }
            } else {
                log.error(" JDB下注并结算 : 失败 订单号 : " + orderId);
                return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
            }
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }
    }

    /**
     * 取消下注并结算 action 4 超时<></>
     *
     * @param reqData
     * @return
     */
    public JDBBaseRsp cancelBetNSettle(JSONObject reqData) {
//        log.info("JDB cancelBetNSettle : "+reqData.toString());
        JDBBetNSettleReq cancelBetNSettleReq = reqData.toJavaObject(JDBBetNSettleReq.class);
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
        if (isValid != null) {
            return isValid;
        }
        //查询账变
        String orderId = cancelBetNSettleReq.getHistoryId();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        List<UserCoinRecordVO> coinRecordVOS = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(coinRecordVOS)) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BigDecimal totalAmount = userCoin.getTotalAmount();
            BigDecimal rspAmount = userCoin.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0 ? totalAmount : BigDecimal.ZERO;
            if (coinRecordVOS.isEmpty()) {
                //如果不存在账变 通知三方取消订单
                return JDBBaseRsp.success(rspAmount.setScale(2,RoundingMode.DOWN).doubleValue());
            } else {
                //已经处理过 remark = 6106
                UserCoinRecordVO cancelFlag = coinRecordVOS.stream()
                        .filter(item -> JDBErrorEnum.BET_IS_VALID.getCode().equals(item.getRemark()))
                        .findAny()
                        .orElse(null);
                if (cancelFlag != null) {
                    return JDBBaseRsp.success(rspAmount.setScale(2,RoundingMode.DOWN).doubleValue());
                }
                //如果已存在账变 -> 取消
                BigDecimal amount = handleBetAndCancel(userInfoVO, coinRecordVOS);
                return JDBBaseRsp.success(amount.doubleValue());
            }
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.BET_TRY_AGAIN_LATER);
        }
    }

    private BigDecimal handleBetAndCancel(UserInfoVO userInfoVO, List<UserCoinRecordVO> coinRecordVOS) {
        BigDecimal amount = BigDecimal.ZERO;
        for (UserCoinRecordVO coinRecordVO : coinRecordVOS) {
            if (coinRecordVO.getBalanceType().equals(CoinBalanceTypeEnum.EXPENSES.getCode())){
                //支出 ->取消下注
                String orderNo = coinRecordVO.getOrderNo();
                BigDecimal coinValue = coinRecordVO.getCoinValue();
                CoinRecordResultVO coinRecordResultVO = handleCancelBet(userInfoVO, orderNo, coinValue, JDBErrorEnum.BET_IS_VALID.getCode());
                amount = coinRecordResultVO.getCoinAfterBalance();

            } else if (coinRecordVO.getBalanceType().equals(CoinBalanceTypeEnum.INCOME.getCode())) {
                //收入 -> 取消派彩
                String orderNo = coinRecordVO.getOrderNo();
                BigDecimal coinValue = coinRecordVO.getCoinValue();
                CoinRecordResultVO coinRecordResultVO = handleCancelSettle(userInfoVO, orderNo, coinValue, JDBErrorEnum.BET_IS_VALID.getCode());
                amount = coinRecordResultVO.getCoinAfterBalance();

            }
        }
        return amount;
    }

    /**
     * 下注 (游戏类型支援范围：街机 (9)、棋牌 (18))
     *
     * @param reqData
     * @return
     */
    public JDBBaseRsp bet(JSONObject reqData) {
//        log.info("JDB bet : "+reqData.toString());
        JDBBetReq req = reqData.toJavaObject(JDBBetReq.class);
        if (!req.valid()) {
            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
        }

        String userId = userCheck(reqData);
        UserInfoVO userInfoVO = getByUserId(userId);
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,true);
        if (isValid != null) {
            return isValid;
        }
        String orderId = req.getGameRoundSeqNo();

        //订单状态
        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,true);
        if (jdbBaseRsp != null) {
            return jdbBaseRsp;
        }
        //检查余额
        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }

        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.JDB.getVenueCode(), req, userCoin, amount);
            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH);
        }

        //账变
        CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, amount, req.getTransferId());
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }
    }


    /**
     * 结算
     *
     * @param reqData
     * @return
     */
    public JDBBaseRsp settle(JSONObject reqData) {
//        log.info("JDB settle : "+reqData.toString());
        JDBSettleReq req = reqData.toJavaObject(JDBSettleReq.class);
        if (!req.valid()) {
            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
        }
//        Boolean roundClosed = req.getRoundClosed();
        //牌局未结束 -》 不处理结算
//        if (!roundClosed) {
//            log.error("JDB未结算订单 : 三方订单 : {}", reqData);
//            return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
//        }
        //用户校验
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        //合法性校验
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
        if (isValid != null) {
            return isValid;
        }
        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }
        //结算: 以局号座位订单的结算标记
        String transferId = req.getTransferId();

        String orderId = req.getHistoryId();
        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,amount);
        if (jdbBaseRsp != null) {
            return jdbBaseRsp;
        }
        //修改余额 记录账变 <取三方金额>
        CoinRecordResultVO coinRecordResultVO = this.handleSettle(userInfoVO, orderId, amount, transferId);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }
    }

    /**
     * 取消派彩
     * @param reqData
     * @return
     */

    /**
     * JDB 取消派彩,需要记录订单状态
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleCancelSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.JDB.getVenueCode());
        return toUserCoinHandle(userCoinAddVOPayout);
    }

    /**
     * 取消下注
     *
     * @param reqData
     * @return
     */
    public JDBBaseRsp cancelBet(JSONObject reqData) {
//        log.info("JDB cancelBet : "+reqData.toString());
        JDBSettleReq req = reqData.toJavaObject(JDBSettleReq.class);
        if (!req.valid()) {
            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
        }

        String userId = userCheck(reqData);
        UserInfoVO userInfoVO = getByUserId(userId);
        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
        if (isValid != null) {
            return isValid;
        }

        String orderId = req.getGameRoundSeqNo();
        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,false);
        if (jdbBaseRsp != null) {
            return jdbBaseRsp;
        }
        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }

        //取消下注,校验订单号,金额是否对上
        List<String> refTransferIds = req.getRefTransferIds().stream().map(String::valueOf).collect(Collectors.toList());

        UserCoinRecordRequestVO betOrderCheck = new UserCoinRecordRequestVO();
        betOrderCheck.setUserAccount(userInfoVO.getUserAccount());
        betOrderCheck.setUserId(userInfoVO.getUserId());
        betOrderCheck.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        betOrderCheck.setRemarkList(refTransferIds);
        List<UserCoinRecordVO> betOrdersData = getUserCoinRecords(betOrderCheck);
        if(CollectionUtil.isNotEmpty(betOrdersData)){
            log.info("betOrdersData 已存在注单 : "+betOrdersData);
        }
        if (betOrdersData.isEmpty()) {
            //无注单
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }
//        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
//        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
//        userCoinRecordVo.setUserId(userInfoVO.getUserId());
//        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        String oldReferIds = String.join(",", refTransferIds);
//        userCoinRecordVo.setRemark(oldReferIds);
//        log.info(" 检查已取消参数 : "+userCoinRecordVo);
//        ResponseVO<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
//        UserCoinRecordVO coinRecordsData = userCoinRecords.getData();
//        if (coinRecordsData != null) {
//            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }


        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        String oldReferIds = String.join(",", refTransferIds);
        userCoinRecordVo.setRemark(oldReferIds);
        log.info(" 检查已取消参数 : "+userCoinRecordVo);
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if(CollectionUtil.isNotEmpty(userCoinRecords)){
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }

        //修改余额 记录账变 <取三方金额>
        CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, amount, oldReferIds);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }
    }



    /**
     * 免费场次派彩
     *
     * @param reqData
     * @return
     */
    private JDBBaseRsp freeSpinReward(JSONObject reqData) {
//        log.info("JDB freeSpinReward : "+reqData.toString());
        JDBFreeSpinRewardReq req = reqData.toJavaObject(JDBFreeSpinRewardReq.class);
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        String orderId = req.getEventId();
        String transferId = req.getTransferId();

        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        coinRecordRequestVO.setRemark(transferId);
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(userCoinRecords)) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }

        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
        //账变
        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, amount, transferId);
        log.info("JDB免费场次派彩 : 用户 : {} transferId : {} eventId : {} amount :{} accumulatedTurnover : {}  accumulatedWin : {}",
                userId, transferId,orderId, req.getAmount(), req.getAccumulatedTurnover(), req.getAccumulatedWin());
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().doubleValue());
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }

    }

    /**
     * 活动派彩
     *
     * @param reqData
     * @return
     */
    private JDBBaseRsp spinReward(JSONObject reqData) {
//        log.info("JDB spinReward : "+reqData.toString());
        JDBSpinRewardReq req = reqData.toJavaObject(JDBSpinRewardReq.class);
        String userId = userCheck(reqData);
        if (userId == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
        }

        String orderId = req.getActivityNo();
        String transferId = req.getTransferId();
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        coinRecordRequestVO.setRemark(transferId);
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(userCoinRecords)) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }
        //派彩直接加 <只有重复订单号>
        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, amount, transferId);
        log.info("JDB活动派彩 : 用户 : {} transferId : {} activityNo : {} activityDate : {} activityName : {} amount : {} ",
                userId, transferId, orderId, req.getActivityDate(), req.getActivityName(), req.getAmount());
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
        } else {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }

    }

    /**
     * 订单校验
     * @param userInfoVO
     * @param orderId
     * @return
     */
    public JDBBaseRsp checkExistCoinRecord(UserInfoVO userInfoVO, String orderId,boolean betting) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(betting ? WalletEnum.CoinTypeEnum.GAME_BET.getCode() : WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (CollectionUtil.isNotEmpty(cancelBetRecord)) {
            //订单已处理
            UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
            return JDBBaseRsp.success(userCoin.getTotalAmount().doubleValue());
        }
        return null;
    }

    /**
     * 结算订单校验
     * @param userInfoVO
     * @param orderId
     * @return
     */
    public JDBBaseRsp checkExistCoinRecord(UserInfoVO userInfoVO, String orderId,BigDecimal amount) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setOrderNo(orderId);
        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        List<UserCoinRecordVO> settleRecord = getUserCoinRecords(coinRecordRequestVO);
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (CollectionUtil.isNotEmpty(settleRecord)) {
            //订单已处理
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
        }
        return null;
    }



    /**
     * 解密
     *
     * @param x
     * @return
     */
    private JSONObject decrypt(String x,String key,String iv) {
        JSONObject reqData;
        try {
            String decrypt = AESDecrypt.decrypt(x, key, iv);
            reqData = JSON.parseObject(decrypt);
            return reqData;
        } catch (Exception e) {
            log.error(" jdb数据解密失败 - " + e.getMessage());
            return null;
        }
    }

    /**
     * 三方行动
     *
     * @param x
     * @return
     */
    public JDBBaseRsp doAction(String x) {
        String key = jdbCryptoConfig.getKey();
        String iv = jdbCryptoConfig.getIV();
        JSONObject reqData = decrypt(x,key,iv);
        if (reqData == null) {
            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
        }
        log.info("JDB doAction : "+reqData);
        Integer actionType = reqData.getInteger("action");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        JDBBaseRsp response = switch (actionType) {
            case JDBConstant.BALANCE -> getBalance(reqData);
            case JDBConstant.BET_N_SETTLE -> betNSettle(reqData);
            case JDBConstant.CANCEL_BET_N_SETTLE -> cancelBetNSettle(reqData);
            case JDBConstant.BET -> bet(reqData);
            case JDBConstant.SETTLE -> settle(reqData);
            case JDBConstant.CANCEL_BET -> cancelBet(reqData);
            case JDBConstant.SPIN_REWARD -> spinReward(reqData);
            case JDBConstant.FREE_SPIN_REWARD -> freeSpinReward(reqData);
            default -> JDBBaseRsp.builder().status(JDBErrorEnum.FAILED.getCode()).build();
        };
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        if (totalTimeMillis>=3000){
            log.info("JDB-Action 请求: {} 返回: {} 耗时: {}ms", reqData,response, totalTimeMillis);
        }
        return response;

    }

    /**
     * 处理取消下注
     *
     * @param userInfoVO
     * @param orderNo
     * @param amount
     * @param remark
     * @return
     */

    protected CoinRecordResultVO handleCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.JDB.getVenueCode());
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }

    /**
     * JDB 结算,需要记录局号
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.JDB.getVenueCode());
        return toUserCoinHandle(userCoinAddVOPayout);
    }

    /**
     * 下注
     *
     * @param userInfoVO
     * @param transactionId
     * @param transferAmount
     * @return
     */
    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }
}
