package com.cloud.baowang.play.game.dbDj;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.dbDj.*;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.dbDj.*;
import com.cloud.baowang.play.api.vo.dbDj.heroSummoning.DbDJHeroSummoningRes;
import com.cloud.baowang.play.game.dbDj.constant.DbDJConstant;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DB_DJ)
@AllArgsConstructor
public class DbDJGameServiceImpl extends GameBaseService implements GameService {

    private final SiteApi siteApi;

    private final OrderRecordProcessService orderRecordProcessService;

    private final VenueInfoService venueInfoService;


    private String getUserAccount(String account) {
        String userAccount = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userAccount)) {
            return null;
        }
        return userAccount;
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = venueInfoVO.getApiUrl() + DbDJConstant.REGISTER;

        DbDjCurrencyEnum dbDjCurrencyEnum = DbDjCurrencyEnum.byPlatCurrencyCode(casinoMemberVO.getCurrencyCode());

        if (dbDjCurrencyEnum == null) {
            log.info("参数异常创建游戏失败:{}", VenueEnum.DB_DJ.getVenueName());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        long unixSeconds = System.currentTimeMillis() / 1000L;

        String password = casinoMemberVO.getVenueUserAccount() + "abcd123";


        Map<String, String> params = new HashMap<>();
        params.put("username", casinoMemberVO.getVenueUserAccount());
        params.put("password", password);
        params.put("tester", "0");
        params.put("merchant", venueInfoVO.getMerchantNo());
        params.put("currency_code", String.valueOf(dbDjCurrencyEnum.getCode()));
        params.put("time", String.valueOf(unixSeconds));
        String sign = buildSignStr(params, venueInfoVO.getMerchantKey());


        params.put("sign", sign);
        Map<String, String> header = new HashMap<>();
        String resp = HttpClientHandler.get(url, header, params);
        if (StringUtils.isBlank(resp)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        JSONObject resJson = JSONObject.parseObject(resp);
        if (resJson == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        if (resJson.containsKey("status") && resJson.getBoolean("status")) {
            return ResponseVO.success();
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueInfoVO, String venueUserAccount) {

        String url = venueInfoVO.getApiUrl() + DbDJConstant.LOGOUT_GAME;
        long unixSeconds = System.currentTimeMillis() / 1000L;
        Map<String, String> params = new HashMap<>();
        params.put("username", venueUserAccount);
        params.put("merchant", venueInfoVO.getMerchantNo());
        params.put("time", String.valueOf(unixSeconds));
        String sign = buildSignStr(params, venueInfoVO.getMerchantKey());
        params.put("sign", sign);

        Map<String, String> header = new HashMap<>();
        String rsp = HttpClientHandler.get(url, header, params);

        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.success(false);
        }

        JSONObject resultJson = JSONObject.parseObject(rsp);
        if (resultJson == null) {
            return ResponseVO.success(false);
        }
        if (!resultJson.containsKey("status")) {
            return ResponseVO.success(false);
        }
        if (!resultJson.getBoolean("status")) {
            return ResponseVO.success(false);
        }

        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String apiUrl = venueInfoVO.getApiUrl() + DbDJConstant.LOGIN;

        DbDjCurrencyEnum dbDjCurrencyEnum = DbDjCurrencyEnum.byPlatCurrencyCode(loginVO.getCurrencyCode());

        if (dbDjCurrencyEnum == null) {
            log.info("参数异常:{}登录游戏失败,币种异常", VenueEnum.DB_DJ.getVenueCode());
            throw new BaowangDefaultException(ResultCode.VENUE_CURRENCY_NOT);
        }
        String dbLang = DbDjLangEnum.conversionLang(CurrReqUtils.getLanguage());

        String password = casinoMemberVO.getVenueUserAccount() + "abcd123";

        long unixSeconds = System.currentTimeMillis() / 1000L;

        Map<String, String> params = new HashMap<>();
        params.put("username", casinoMemberVO.getVenueUserAccount());
        params.put("password", password);
        params.put("merchant", venueInfoVO.getMerchantNo());
        params.put("time", String.valueOf(unixSeconds));
        params.put("lang", dbLang);
        String sign = buildSignStr(params, venueInfoVO.getMerchantKey());
        params.put("sign", sign);
        Map<String, String> header = new HashMap<>();
        String rsp = HttpClientHandler.get(apiUrl, header, params);
        if (StringUtils.isBlank(rsp)) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }


        JSONObject resultJson = JSONObject.parseObject(rsp);
        if (resultJson == null) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }

        if (!resultJson.containsKey("status") || !resultJson.getBoolean("status")) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }

        if (!resultJson.containsKey("data")) {
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }

        JSONObject data = resultJson.getJSONObject("data");
        String url = data.getString("h5");
        if (DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType())) {
            url = data.getString("pc");
        }

        GameLoginVo loginVo = GameLoginVo.builder()
                .source(url)
                .type(GameLoginTypeEnums.URL.getType())
                .userAccount(casinoMemberVO.getVenueUserAccount())
                .venueCode(VenueEnum.DB_DJ.getVenueCode())
                .merchantNo(venueInfoVO.getMerchantNo())
                .build();
        return ResponseVO.success(loginVo);
    }


    /**
     * 插入干扰值：首尾+第9位后+第17位后
     */
    private static String mixWithRandom(String md5) {
        StringBuilder sb = new StringBuilder();

        // 生成随机2位干扰字符
        String r1 = random2Chars();
        String r2 = random2Chars();
        String r3 = random2Chars();
        String r4 = random2Chars();

        // 插入规则：开头、9位后、17位后、结尾
        sb.append(r1) // 开头
                .append(md5, 0, 9)
                .append(r2)
                .append(md5, 9, 17)
                .append(r3)
                .append(md5.substring(17))
                .append(r4); // 结尾

        return sb.toString();
    }

    /**
     * 生成随机2位字母或数字
     */
    private static String random2Chars() {
        String RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(2);
        for (int i = 0; i < 2; i++) {
            sb.append(RANDOM_CHARS.charAt(rand.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 拉取电子注单
     */
    private ResponseVO<?> toPullEleRecord(int pageSize, VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {



        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.DB_DJ.getVenueCode());
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        String merchantKey = venueDetailVO.getMerchantKey();

        String versionKey = venuePullParamVO.getVersionKey();

        if (ObjectUtil.isEmpty(versionKey)) {
            versionKey = "0";
        }


        while (true) {
            Map<String, String> requestParameters = new HashMap<>();
            requestParameters.put("merchant", venueDetailVO.getMerchantNo());
            requestParameters.put("query_type", "1");//时间查询方式, 1:更新时间, 2:投注时间
            requestParameters.put("start_time", String.valueOf(venuePullParamVO.getStartTime()));//注單狀態： 0：全部狀態 1：已結算狀態 (預設值) 2：已註銷
            requestParameters.put("end_time", String.valueOf(venuePullParamVO.getEndTime()));
            requestParameters.put("last_order_id", versionKey);
            requestParameters.put("page_size", String.valueOf(pageSize));
            requestParameters.put("agency", "false");
            requestParameters.put("client_id", venueDetailVO.getMerchantNo());
            requestParameters.put("invite_code", venueDetailVO.getMerchantKey());
            String sign = buildSignStr(requestParameters, merchantKey);
            sign = mixWithRandom(sign);
            requestParameters.put("sign", sign);

            String url = venueDetailVO.getBetUrl() + DbDJConstant.ORDER_RECORD;
            Map<String, String> headers = new HashMap<>();
            String response = HttpClientHandler.get(url, headers, requestParameters);

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

            if (!resultJson.containsKey("bet")) {
                return ResponseVO.success();
            }

            JSONArray jsonArray = resultJson.getJSONArray("bet");
            if (jsonArray.isEmpty()) {
                return ResponseVO.success();
            }


            //电竞串关解析
            JSONObject detailJson = null;
            if (resultJson.containsKey("detail")) {
                detailJson = resultJson.getJSONObject("detail");
            }


            //联赛名称 中文
            JSONObject tournamentJson = null;
            if (resultJson.containsKey("tournament")) {
                tournamentJson = resultJson.getJSONObject("tournament");
            }


            //联赛名称 英文
            JSONObject tournamentEnJson = null;
            if (resultJson.containsKey("tournament_en")) {
                tournamentEnJson = resultJson.getJSONObject("tournament_en");
            }



            List<DbDJOrderRecordBetRes> betList = jsonArray.toJavaList(DbDJOrderRecordBetRes.class);
            log.info("400012-"+betList.size());

//            DbDjOrderRecordRes recordRes = JSON.parseObject(resultJson.toJSONString(), DbDjOrderRecordRes.class);
            if (CollectionUtil.isEmpty(betList)) {
                log.info("betArray无数据");
                return ResponseVO.success();
            }


            // 场馆用户关联信息
            List<String> thirdUserName = betList.stream().map(DbDJOrderRecordBetRes::getMember_account).distinct().toList();
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


            for (DbDJOrderRecordBetRes item : betList) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(item.getMember_account());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), item.getMember_account());
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

                //获取订单的串关信息
                if (detailJson != null) {
                    if (detailJson.containsKey(String.valueOf(item.getId()))) {
                        JSONArray detailJsonArray = detailJson.getJSONArray(String.valueOf(item.getId()));
                        List<DbDJOrderRecordDetailRes> detailJsonList = detailJsonArray.toJavaList(DbDJOrderRecordDetailRes.class);


                        for (DbDJOrderRecordDetailRes detailItem : detailJsonList){
                            //联赛名称-中文
                            if(tournamentJson != null){
                                if (tournamentJson.containsKey(String.valueOf(detailItem.getTournament_id()))) {
                                    String tournament = tournamentJson.getString(String.valueOf(detailItem.getTournament_id()));
                                    detailItem.setTournament(tournament);
                                }
                            }

                            //联赛名称-英文
                            if(tournamentEnJson != null){
                                if (tournamentEnJson.containsKey(String.valueOf(detailItem.getTournament_id()))) {
                                    String tournamentEn = tournamentEnJson.getString(String.valueOf(detailItem.getTournament_id()));
                                    detailItem.setTournament_en(tournamentEn);
                                }
                            }
                        }

                        item.setDetail(detailJsonList);
                    }
                }

                //联赛名称-中文
                if(tournamentJson != null){
                    if (tournamentJson.containsKey(String.valueOf(item.getTournament_id()))) {
                        String tournament = tournamentJson.getString(String.valueOf(item.getTournament_id()));
                        item.setTournament(tournament);
                    }
                }


                //联赛名称-英文
                if(tournamentEnJson != null){
                    if (tournamentEnJson.containsKey(String.valueOf(item.getTournament_id()))) {
                        String tournamentEn = tournamentEnJson.getString(String.valueOf(item.getTournament_id()));
                        item.setTournament_en(tournamentEn);
                    }
                }


                // 映射原始注单
                OrderRecordVO recordVO = eleParseRecords(venueDetailVO, item, userInfoVO, userLoginInfoVO);
                recordVO.setVenueType(gameTypeId);
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                list.add(recordVO);
            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            int lastOrderId = 0;
            if (resultJson.containsKey("last_order_id")) {
                lastOrderId = resultJson.getIntValue("last_order_id");
            }
            //拉取结束
            if (lastOrderId <= 0) {
                return ResponseVO.success();
            }
            //每次拉完后同步最后一次下标
            versionKey = String.valueOf(lastOrderId);
            list.clear();
        }
        return ResponseVO.success();

    }


    /**
     * 拉取英雄召唤
     */
    private ResponseVO<?> toPullHeroSummoningRecord(int pageSize, VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.DB_DJ.getVenueCode());
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));
        String merchantKey = venueDetailVO.getMerchantKey();

        String versionKey = venuePullParamVO.getVersionKey();

        if (ObjectUtil.isEmpty(versionKey)) {
            versionKey = "0";
        }


        while (true) {
            Map<String, String> requestParameters = new HashMap<>();
            requestParameters.put("merchant", venueDetailVO.getMerchantNo());
            requestParameters.put("query_type", "1");//时间查询方式, 1:更新时间, 2:投注时间
            requestParameters.put("start_time", String.valueOf(venuePullParamVO.getStartTime()));//注單狀態： 0：全部狀態 1：已結算狀態 (預設值) 2：已註銷
            requestParameters.put("end_time", String.valueOf(venuePullParamVO.getEndTime()));
            requestParameters.put("last_order_id", versionKey);
            requestParameters.put("page_size", String.valueOf(pageSize));
            requestParameters.put("agency", "false");
            requestParameters.put("client_id", venueDetailVO.getMerchantNo());
            requestParameters.put("invite_code", venueDetailVO.getMerchantKey());
            String sign = buildSignStr(requestParameters, merchantKey);
            sign = mixWithRandom(sign);
            requestParameters.put("sign", sign);

            String url = venueDetailVO.getBetUrl() + DbDJConstant.HERO_ORDER_RECORD;
            Map<String, String> headers = new HashMap<>();
            String response = HttpClientHandler.get(url, headers, requestParameters);

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

            if (!resultJson.containsKey("ticketOrder")) {
                return ResponseVO.success();
            }

            JSONArray jsonArray = resultJson.getJSONArray("ticketOrder");
            if (jsonArray.isEmpty()) {
                return ResponseVO.success();
            }
            List<DbDJHeroSummoningRes> betList = jsonArray.toJavaList(DbDJHeroSummoningRes.class);
            if (CollectionUtil.isEmpty(betList)) {
                log.info("betList无数据");
                return ResponseVO.success();
            }


            // 场馆用户关联信息
            List<String> thirdUserName = betList.stream().map(DbDJHeroSummoningRes::getMember_account).distinct().toList();
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


            for (DbDJHeroSummoningRes item : betList) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(item.getMember_account());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), item.getMember_account());
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
                Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DB_DJ.getVenueCode());
                // 映射原始注单
                OrderRecordVO recordVO = heroParseRecords(venueDetailVO, item, userInfoVO, userLoginInfoVO, paramToGameInfo);
                recordVO.setVenueType(gameTypeId);
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setRoomTypeName("单关");//该玩法固定只有单关
                list.add(recordVO);
            }

            // 订单处理
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            int lastOrderId = 0;
            if (resultJson.containsKey("last_order_id")) {
                lastOrderId = resultJson.getIntValue("last_order_id");
            }
            //拉取结束
            if (lastOrderId <= 0) {
                return ResponseVO.success();
            }
            //每次拉完后同步最后一次下标
            versionKey = String.valueOf(lastOrderId);
            list.clear();
        }
        return ResponseVO.success();

    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        int pageSize = 1000;

        //英雄召唤
        toPullHeroSummoningRecord(pageSize, venueDetailVO, venuePullParamVO);

        //拉取电子注单
        return toPullEleRecord(pageSize, venueDetailVO, venuePullParamVO);
    }

    //英雄召唤拉单
    private OrderRecordVO heroParseRecords(VenueInfoVO venueDetailVO, DbDJHeroSummoningRes orderResponseVO, UserInfoVO userInfoVO,
                                          UserLoginInfoVO userLoginInfoVO, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setGameName(orderResponseVO.getTicket_name_en());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBet_amount());
        BigDecimal winLossAmount = orderResponseVO.getWin_amount().subtract(orderResponseVO.getBet_amount());
        //输赢金额 = 可以中奖金额-投注金额
        recordVO.setWinLossAmount(winLossAmount);
        BigDecimal validBetAmount = computerValidBetAmount(orderResponseVO.getBet_amount(), winLossAmount, VenueTypeEnum.ELECTRONIC_SPORTS);
        recordVO.setValidAmount(validBetAmount);
        recordVO.setPayoutAmount(orderResponseVO.getWin_amount());
        recordVO.setBetTime(orderResponseVO.getBet_time());
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getMember_account());
        String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";

        recordVO.setEventInfo(orderResponseVO.getTicket_name_en());

        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

        if (userLoginInfoVO != null) {
            recordVO.setBetIp(betIp);
            recordVO.setDeviceType(deviceType);
        }
        Long gameId = orderResponseVO.getGame_id();

        recordVO.setCurrency(userInfoVO.getMainCurrency());
//        recordVO.setRoomType(String.valueOf(orderResponseVO.getIs_live()));
        recordVO.setGameNo(orderResponseVO.getTicket_plan_no());
//        recordVO.setGameNo(String.valueOf(orderResponseVO.getRound()));
        recordVO.setThirdGameCode(String.valueOf(gameId));
        recordVO.setOdds(String.valueOf(orderResponseVO.getOdd()));
        recordVO.setBetContent(orderResponseVO.getBet_num());

        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getOrder_id()));
        recordVO.setTransactionId(String.valueOf(orderResponseVO.getOrder_id()));

        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
        DbDjOrderStatusEnum statusEnum = DbDjOrderStatusEnum.fromCode(orderResponseVO.getBet_status());
        if (statusEnum != null) {
            recordVO.setOrderStatus(statusEnum.getPlatCurrencyStatus());
            recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(statusEnum.getPlatCurrencyStatus()));
        }
        recordVO.setSettleTime(orderResponseVO.getSettle_time()  * 1000);
//        if (ObjectUtil.isNotEmpty(orderResponseVO.getPayoff_at()) && !orderResponseVO.getPayoff_at().equals("Invalid date")) {
//            recordVO.setFirstSettleTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getPayoff_at()));
//        }

        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setResultList(null);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
//        recordVO.setOrderInfo(orderResponseVO.getResult());
//        recordVO.setPlayType(orderResponseVO.getResult());
//        GameInfoPO gameInfoPO = paramToGameInfo.get(String.valueOf(gameId));
//        if (gameInfoPO != null) {
//            recordVO.setGameId(gameInfoPO.getGameId());
//            recordVO.setGameName(gameInfoPO.getGameI18nCode());
//        }

        return recordVO;
    }


    //电子拉单
    private OrderRecordVO eleParseRecords(VenueInfoVO venueDetailVO, DbDJOrderRecordBetRes orderResponseVO, UserInfoVO userInfoVO,
                                          UserLoginInfoVO userLoginInfoVO) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBet_amount());
        Integer order_type = orderResponseVO.getOrder_type();
        DbDjOrderTypeEnum dbPanDaSportSerialTypeEnum = DbDjOrderTypeEnum.fromCode(order_type);
        String rootTypeName = "单关";
        if(dbPanDaSportSerialTypeEnum != null){
            rootTypeName = dbPanDaSportSerialTypeEnum.getDesc();
        }
        recordVO.setRoomTypeName(rootTypeName);
        BigDecimal winLossAmount = orderResponseVO.getWin_amount().subtract(orderResponseVO.getBet_amount());
        //输赢金额 = 可以中奖金额-投注金额
        recordVO.setWinLossAmount(winLossAmount);
        BigDecimal validBetAmount = computerValidBetAmount(orderResponseVO.getBet_amount(), winLossAmount, VenueTypeEnum.ELECTRONIC_SPORTS);
        recordVO.setValidAmount(validBetAmount);
        recordVO.setPayoutAmount(orderResponseVO.getWin_amount());
//        recordVO.setBetContent(orderResponseVO.getCategory());
//        recordVO.setPlayInfo(orderResponseVO.getCategory());
        recordVO.setBetTime(orderResponseVO.getBet_time());
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getMember_account());
        String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

        if (userLoginInfoVO != null) {
            recordVO.setBetIp(betIp);
            recordVO.setDeviceType(deviceType);
        }
        Long gameId = orderResponseVO.getGame_id();

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(String.valueOf(orderResponseVO.getIs_live()));
//        recordVO.setGameNo(orderResponseVO.getLobby_id());
        recordVO.setGameNo(String.valueOf(orderResponseVO.getMatch_id()));
        recordVO.setThirdGameCode(String.valueOf(gameId));


        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getId()));
        recordVO.setTransactionId(String.valueOf(orderResponseVO.getId()));

        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());
//        DbDjOrderStatusEnum statusEnum = DbDjOrderStatusEnum.fromCode(orderResponseVO.getBet_status());

        recordVO.setOrderStatus(getOrderStatus(orderResponseVO));
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));

        if (orderResponseVO.getSettle_time() != 0L){
            recordVO.setSettleTime(orderResponseVO.getSettle_time() * 1000);

        }
//        if (ObjectUtil.isNotEmpty(orderResponseVO.getPayoff_at()) && !orderResponseVO.getPayoff_at().equals("Invalid date")) {
//            recordVO.setFirstSettleTime(TimeZoneUtils.convertIso8601ToTimestamp(orderResponseVO.getPayoff_at()));
//        }

        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setResultList(null);
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
//        recordVO.setOrderInfo(orderResponseVO.getResult());
//        recordVO.setPlayType(orderResponseVO.getResult());
//        GameInfoPO gameInfoPO = paramToGameInfo.get(String.valueOf(gameId));
//        if (gameInfoPO != null) {
//            recordVO.setGameId(gameInfoPO.getGameId());
//        }
//
        recordVO.setGameName(orderResponseVO.getMarket_en_name());
        return recordVO;
    }

    public Integer getOrderStatus(DbDJOrderRecordBetRes orderResponseVO){
        DbDjOrderStatusEnum statusEnum = DbDjOrderStatusEnum.fromCode(orderResponseVO.getBet_status());
        if(statusEnum == null){
            return null;
        }
        Integer settleCount = orderResponseVO.getSettle_count();
        if (settleCount != null && settleCount > CommonConstant.business_one)  {
            //重结算
            return OrderStatusEnum.RESETTLED.getCode();
        }
        return statusEnum.getPlatCurrencyStatus();
    }

    public DBBalanceRes queryBalance(DBBalanceReq req) {
        //参数失败
        if (!req.valid()) {
            log.info("{}:参数校验失败", VenueEnum.DB_DJ.getVenueName());
            return DBBalanceRes
                    .builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.DATA_ERROR.getMessageEn())
                    .build();
        }

        String userId = getUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.info("{}:用户账号解析失败,不存在:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.USER_DOES_NOT_EXIST.getMessageEn())
                    .build();
        }


        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("{}:用户账号查询失败,不存在:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.USER_DOES_NOT_EXIST.getMessageEn())
                    .build();

        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.LOGIN_PROHIBITED.getMessageEn())
                    .build();
        }

        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.DB_DJ.getVenueCode(), null);
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.info("场馆未开启不允许下注:{} ", VenueEnum.DB_DJ.getVenueCode());
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.FAILED.getMessageEn())
                    .build();

        }

        String userCurrencyCode = userInfoVO.getMainCurrency();

        DbDjCurrencyEnum dbDjCurrencyEnum = DbDjCurrencyEnum.byPlatCurrencyCode(userCurrencyCode);

        if (dbDjCurrencyEnum == null || dbDjCurrencyEnum.getPlatCurrencyCode() == null) {
            log.info("币种:{} 没有映射成功", VenueEnum.DB_DJ.getVenueCode());
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.FALSE))
                    .message(DbDJResultCodeEnum.FAILED.getMessageEn())
                    .build();
        }


        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.TRUE))
                    .message(DbDJResultCodeEnum.SUCCEED.getMessageEn())
                    .data(BigDecimal.ZERO)
                    .build();
        }

        if (userCenterCoin.getTotalAmount() == null) {
            return DBBalanceRes.builder()
                    .status(String.valueOf(Boolean.TRUE))
                    .message(DbDJResultCodeEnum.SUCCEED.getMessageEn())
                    .data(BigDecimal.ZERO)
                    .build();
        }

        return DBBalanceRes.builder()
                .status(String.valueOf(Boolean.TRUE))
                .data(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN))
                .message(DbDJResultCodeEnum.SUCCEED.getMessageEn())
                .build();
    }


    @DistributedLock(name = RedisConstants.DB_DJ_COIN_LOCK, unique = "#req.merOrderId", waitTime = 3, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public DbDJTransferRes transfer(DbDJTransferReq req) {
        // 参数校验

        if (!req.valid() || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("参数校验失败:{},req:{}", VenueEnum.DB_DJ.getVenueName(), req);
            return buildRes(false, DbDJResultCodeEnum.DATA_ERROR, 0);
        }

        String userId = getUserAccount(req.getUsername());
        if (StringUtils.isBlank(userId)) {
            log.info("用户账号解析失败,不存在:{},{}", VenueEnum.DB_DJ.getVenueName(), req.getUsername());
            return buildRes(false, DbDJResultCodeEnum.USER_DOES_NOT_EXIST, 0);
        }

        UserInfoVO userInfoVO = getByUserId(userId);
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("用户账号查询失败:{},不存在:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return buildRes(false, DbDJResultCodeEnum.USER_DOES_NOT_EXIST, 0);
        }

        DbDjTransferEnum djTransferEnum = DbDjTransferEnum.fromCode(req.getType());
        if (djTransferEnum == null) {
            log.info("类型异常:{},userId:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }

        DbDJTransferRes res = switch (djTransferEnum) {
            //扣款
            case BET_DEDUCTION, OTHER_DEDUCTION -> handleBetDeduction(req, userInfoVO);

            //回滚扣款
            case ROLLBACK_DEDUCTION -> handleRollbackDeduction(req, userInfoVO);

            //加款
            case SETTLE_ADD, OTHER_ADD -> handleSettleAdd(req, userInfoVO);
            default -> buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        };

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);

        res.setAccount_balance(userCenterCoin.getTotalAmount());
        return res;
    }


    /**
     * 处理投注扣款
     */
    private DbDJTransferRes handleBetDeduction(DbDJTransferReq req, UserInfoVO userInfoVO) {
        String userId = userInfoVO.getUserId();
        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(
                userInfoVO.getSiteCode(), VenueEnum.DB_DJ.getVenueCode(), null);
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.info("场馆未开启不允许下注:{},", VenueEnum.DB_DJ.getVenueName());
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("用户被打标签,登录锁定不允许下注:{},userId:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("用户钱包不存在:{},userId:{}", VenueEnum.DB_DJ.getVenueName(), userId);
            return buildRes(false, DbDJResultCodeEnum.BALANCE_NOT_ENOUGH, 0);
        }

        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();
        if (userTotalAmount.subtract(req.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}:用户余额不足,用户金额::{},扣款金额:{}", VenueEnum.DB_DJ.getVenueName(), userTotalAmount, req.getAmount());
            return buildRes(false, DbDJResultCodeEnum.BALANCE_NOT_ENOUGH, 0);
        }

        return doAddCoin(req, userInfoVO,
                CoinBalanceTypeEnum.EXPENSES.getCode(),
                WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode(),
                WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode(),
                WalletEnum.CoinTypeEnum.GAME_BET.getCode(),
                AccountCoinTypeEnums.GAME_BET.getCode());
    }

    /**
     * 处理注单回滚扣款
     */
    private DbDJTransferRes handleRollbackDeduction(DbDJTransferReq req, UserInfoVO userInfoVO) {
        // 查询是否有加款记录
        UserCoinRecordRequestVO addCoinRecordRequestVO = new UserCoinRecordRequestVO();
        addCoinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        addCoinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        addCoinRecordRequestVO.setUserId(userInfoVO.getUserId());
        addCoinRecordRequestVO.setOrderNo(req.getMerOrderId());
        addCoinRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        addCoinRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());

        List<UserCoinRecordVO> addUserCoinRecords = getUserCoinRecords(addCoinRecordRequestVO);
        if (CollectionUtil.isEmpty(addUserCoinRecords)) {
            log.info("这笔单没有加款或查询失败:{},userCoinAddVO:{}", VenueEnum.DB_DJ.getVenueName(), addCoinRecordRequestVO);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }

        // 查询是否已处理过取消加款
        UserCoinRecordRequestVO cancelAddRecordRequestVO = new UserCoinRecordRequestVO();
        cancelAddRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        cancelAddRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        cancelAddRecordRequestVO.setUserId(userInfoVO.getUserId());
        cancelAddRecordRequestVO.setOrderNo(req.getMerOrderId());
        cancelAddRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        cancelAddRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());

        List<UserCoinRecordVO> cancelAddUserCoinRecords = getUserCoinRecords(cancelAddRecordRequestVO);
        if (CollectionUtil.isEmpty(cancelAddUserCoinRecords)) {
            log.info("查询取消加款失败:{},userCoinAddVO:{}", VenueEnum.DB_DJ.getVenueName(), cancelAddRecordRequestVO);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }
        if (CollectionUtil.isNotEmpty(cancelAddUserCoinRecords)) {
            log.info("取消加款已处理过,不允许重复扣款:{},userCoinAddVO:{}", VenueEnum.DB_DJ.getVenueName(), cancelAddRecordRequestVO);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 0);
        }

        // 扣款
        return doAddCoin(req, userInfoVO,
                CoinBalanceTypeEnum.EXPENSES.getCode(),
                WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode(),
                WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode(),
                WalletEnum.CoinTypeEnum.GAME_BET.getCode(),
                AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
    }

    /**
     * 处理结算加款
     */
    private DbDJTransferRes handleSettleAdd(DbDJTransferReq req, UserInfoVO userInfoVO) {
        return doAddCoin(req, userInfoVO,
                CoinBalanceTypeEnum.INCOME.getCode(),
                WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode(),
                WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode(),
                WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode(),
                AccountCoinTypeEnums.GAME_PAYOUT.getCode());
    }

    /**
     * 通用加扣款
     */
    private DbDJTransferRes doAddCoin(DbDJTransferReq req, UserInfoVO userInfoVO,
                                      String balanceType, String businessCoinType, String customerCoinType,
                                      String coinType,String accountCoinType) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getMerOrderId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setBalanceType(balanceType);
        userCoinAddVO.setBusinessCoinType(businessCoinType);
        userCoinAddVO.setCustomerCoinType(customerCoinType);
        userCoinAddVO.setCoinType(coinType);
        userCoinAddVO.setAccountCoinType(accountCoinType);
        userCoinAddVO.setVenueCode(VenuePlatformConstants.DB_DJ);
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("调用账变失败:{},userCoinAddVO:{}", VenueEnum.DB_DJ.getVenueName(), userCoinAddVO);
            return buildRes(false, DbDJResultCodeEnum.FAILED, 1);
        }
        return buildRes(true, DbDJResultCodeEnum.SUCCEED, 0);
    }

    /**
     * 统一返回构造
     */
    private DbDJTransferRes buildRes(boolean success, DbDJResultCodeEnum codeEnum, int retry) {
        return DbDJTransferRes.builder()
                .status(String.valueOf(success))
                .message(codeEnum.getMessageEn())
                .code(codeEnum.getCode())
                .retry(retry)
                .build();
    }


    /**
     * 把 Map 转换成 query string 格式，并按 key 排序
     */
    private static String buildSignStr(Map<String, String> params, String merchantKey) {
        Map<String, String> paramMap = new HashMap<>(params);
        paramMap.put("key", merchantKey);

        List<String> keys = new ArrayList<>(paramMap.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = paramMap.get(key);
            if (value != null) {
                if (!sb.isEmpty()) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(value);
            }
        }
        return MD5Util.md5(sb.toString());
    }


}
