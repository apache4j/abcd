package com.cloud.baowang.play.game.pp.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.pp.enums.PPErrorCodeEnum;
import com.cloud.baowang.play.game.pp.utils.CsvParserUtil;
import com.cloud.baowang.play.game.pp.utils.HashValidatorUtils;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.google.common.collect.Maps;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.plugins.PluginsService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {

//        Map<String, Object> paramMap = Maps.newHashMap();
//        paramMap.put("secureLogin", "wnt_winto1");
//        //paramMap.put("symbol", "vswayslions");
//        //paramMap.put("language", "en");
//        //paramMap.put("externalPlayerId", "10000111");
//
//        //paramMap.put("currency", "USD");
//        //可以不传 paramMap.put("currency", "CNY");
//        //log.info("{} PP游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
//        String hashStr = "";
//        try {
//            hashStr = HashValidatorUtils.calculateHash(paramMap, "LtV9S1YAvHNRGRx5");
//        } catch (Exception e) {
//            System.out.println(e.toString());
//            //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
//        }
//        System.out.println(getPlayersFRB());
//        System.out.println(giveFRB());
//        System.out.println(cancelFRB());

        ;

       //System.out.println(Test.getTransactionsByCSV());

//        GetCasinoGames();
//       System.out.println(Test.giveFRB());
       //System.out.println(Test.getPlayersFRB());
//       System.out.println(Test.cancelFRB());



        /*String time = "2025-06-11 08:11:10";
        String time2 = "2025-06-11 20:11:10";
       DateTime parse = DateUtil.parse(time, DatePattern.NORM_DATETIME_FORMAT);

        System.out.println(parse.getTime());

//
//        DateTime parse = DateUtil.parse(time, DatePattern.NORM_DATETIME_FORMAT);

        LocalDateTime utcTime = LocalDateTime.parse(time.replace(" ", "T"));


//        LocalDateTime utcTime = LocalDateTime.parse(time.replace(" ", "T"));
        long timestamp = utcTime.toInstant(ZoneOffset.UTC).toEpochMilli();


        long utc = utcTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toInstant().toEpochMilli();

        System.out.println(timestamp);
        System.out.println(utc);
        System.out.println(utcTime);*/

        String token = "SAHhAsbZ";
        String providerId = "PragmaticPlay";

        HashMap<String, Object> objectObjectHashMap = new HashMap<>();

        objectObjectHashMap.put("token", token);
        objectObjectHashMap.put("providerId", providerId);

        String hash = HashValidatorUtils.calculateHash(objectObjectHashMap, "739721332dAd4b97");


        System.out.println(hash);


    }




    //6.10获得赌注范围
    private static JSONArray giveLimitGameLine() {
        // /IntegrationService/v3/http/CasinoGameAPI/getBetScales
        String url = "https://api.prerelease-env.biz/IntegrationService/v3/http/CasinoGameAPI/getBetScales";

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", "wnt_winto01");
        //paramMap.put("gameIDs", "....");
        paramMap.put("currencies", "CNY");
        //可以不传 paramMap.put("currency", "CNY");
        //log.info("{} PP游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);
        JSONArray jsonArrayBack = new JSONArray();
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            System.out.println(JSON.toJSONString(response.body()));
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.getString("error");
                if ("0".equals(error)) {
                    JSONArray gameList = jsonObject.getJSONArray("gameList");
                    /*for (Object object : gameList) {
                        JSONObject temp = new JSONObject();
                        JSONObject oneObj = JSON.to(JSONObject.class, object);
                        temp.put("gameId", oneObj.getString("gameID"));

                        JSONObject betValuesObj = new JSONObject();
                        betValuesObj.put("currency", "CNY");
                        betValuesObj.put("betPerLine", 1);
                        JSONArray betValuesArray = new JSONArray();
                        betValuesArray.add(betValuesObj);

                        temp.put("betValues", betValuesArray);

                        jsonArrayBack.add(temp);
                    }
                    return jsonArrayBack;*/
                    String jsonString = JSON.toJSONString(gameList);
                    return gameList;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} 获取游戏列表发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return jsonArrayBack;

    }

    //6.3 创建免费回合  POST /FreeRoundsBonusAPI/v2/bonus/create/
    private static boolean giveFRB(){

        //String url = venueInfoVO.getApiUrl() + "/DataFeeds/transactions";
        String url  = "https://api.prerelease-env.biz/IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/player/create/";

        Map<String, Object> paramMap = Maps.newHashMap();

        long startDate = System.currentTimeMillis()/1000;
        Long expirationDate = startDate+(60*2);
        String bonusCode = "Utest_38735174" + "_" +DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);


        paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("bonusCode", bonusCode);//vswayslions
        paramMap.put("startDate", startDate);
        paramMap.put("expirationDate", expirationDate);
        paramMap.put("validityDate", expirationDate);
        paramMap.put("rounds", 20);
        paramMap.put("playerId", "Utest_38735174");
        paramMap.put("currency", "CNY");

        //JSONArray array = giveLimitGameLine();
        JSONArray array = new JSONArray();

        JSONObject temp = new JSONObject();
        temp.put("gameId", "vs20olympgold");
        JSONObject betValuesObj = new JSONObject();
        betValuesObj.put("currency", "CNY");
        betValuesObj.put("betPerLine", 100);
        JSONArray betValuesArray = new JSONArray();
        betValuesArray.add(betValuesObj);

        temp.put("betValues", betValuesArray);

        array.add(temp);
        String hashStr = "";
        if (!CollUtil.isEmpty(array)){


            try {
                hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
            } catch (Exception e) {
                System.out.println(e.toString());
                //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
            }
            Map<String, Object> gameListMap = Maps.newHashMap();

            gameListMap.put("gameList", array);
//            gameListMap.put("gameList", "all game");

            paramMap.put("hash", hashStr);
            url+="?secureLogin=wnt_winto01";
            url+="&bonusCode="+ bonusCode;
            url+="&startDate="+startDate;
            url+="&expirationDate="+expirationDate;
            url+="&validityDate="+expirationDate;
            url+="&rounds=20";
            url+="&playerId=Utest_38735174";
            url+="&currency=CNY";
            url+="&hash="+hashStr;


            try (HttpResponse response = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .form(paramMap)
                    .body(JSON.toJSONString(gameListMap))
                    .execute())
            {

                if (response.isOk()) {
                    JSONObject jsonObject = JSON.parseObject(response.body());
                    Object error = jsonObject.getString("error");
                    if ("0".equals(error)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                //log.error("{} CreatePlayer发生错误!!", VenueEnum.PP.getVenueName(), e);
            }
        }
        return false;

    }

    //6.4 取消免费回合  POST /FreeRoundsBonusAPI/v2/bonus/cancel/
    private static boolean cancelFRB(){

        //String url = venueInfoVO.getApiUrl() + "/DataFeeds/transactions";
        String url  = "https://api.prerelease-env.biz/IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/cancel/";

        Map<String, Object> paramMap = Maps.newHashMap();

        String bonusCode = "Utest_38735174_20250624142726";

        paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("bonusCode", bonusCode);

        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap)
                .execute())
        {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                if (PPErrorCodeEnum.CODE_0.getCode().equals(jsonObject.getString("error"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            //log.error("{} CreatePlayer发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return false;

    }

    //6.5 获取玩家FRB  POST /FreeRoundsBonusAPI/getPlayersFRB/
    private static JSONArray getPlayersFRB(){

        //String url = venueInfoVO.getApiUrl() + "/DataFeeds/transactions";
        String url  = "https://api.prerelease-env.biz/IntegrationService/v3/http/FreeRoundsBonusAPI/getPlayersFRB/";

        Map<String, Object> paramMap = Maps.newHashMap();


        paramMap.put("secureLogin", "wnt_winto01");//9YVN5P20304165
        paramMap.put("playerId", "Utest_38735174");//vswayslions

        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap)
                .execute())
        {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                if (PPErrorCodeEnum.CODE_0.getCode().equals(jsonObject.getString("error"))) {
                    return jsonObject.getJSONArray("bonuses");
                }
            }
        } catch (Exception e) {
            //log.error("{} CreatePlayer发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONArray();

    }


    //8.3 游戏内交易 GET/DataFeeds/transactions/
    private static JSONArray getTransactionsByCSV(){

        //String url = venueInfoVO.getApiUrl() + "/DataFeeds/transactions";
        String url  = "https://api.prerelease-env.biz/IntegrationService/v3/DataFeeds/gamerounds/finished/?login=wnt_winto01&password=739721332dAd4b97&timepoint=1749456355000&options=addRoundDetails";
        String url2 = "https://api.prerelease-env.biz/IntegrationService/v3/DataFeeds/gamerounds/finished/?login=wnt_winto01&password=739721332dAd4b97&timepoint=1749634620&options=addRoundDetails,addBonusBetWin";

        Map<String, Object> paramMap = Maps.newHashMap();
        /*paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("symbol", "vswayslions");
        paramMap.put("language", "en");
        paramMap.put("externalPlayerId", "10000111");*/

        //url+="?login=wnt_winto01&password=739721332dAd4b97&timepoint=1749276223818&options=addRoundDetails";
        /*paramMap.put("login", "wnt_winto01");
        paramMap.put("password", "739721332dAd4b97");
        paramMap.put("timepoint", 1749276223818L);
        paramMap.put("options", "addTransactionStatus");*/

        //log.info("{} getTransactionsByCSV游戏注单, url: {}, timepoint参数: {}", VenueEnum.PP.getVenueName(), url, venuePullParamVO.getStartTime());

        try (HttpResponse response = HttpRequest.get(url2)
                .header(Header.CONTENT_TYPE, "text/csv")
                .execute()) {

            if (response.isOk()) {
                return CsvParserUtil.parseCsv(response.body());
            }
        } catch (Exception e) {
            //log.error("{} CreatePlayer发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        /*try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);

            //CSVReader csvReader = new CSVReader(new InputStreamReader(response.getEntity().getContent()));
            System.out.println(response);
            //csvReader.readAll();
            //return csvReader.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        return new JSONArray();

    }

    //getGameUrl  /CasinoGameAPI/game/url
    public static JSONObject getGameUrl(){


        //String url = venueInfoVO.getApiUrl() + " /player/account/create/";
        String url = "https://api.prerelease-env.biz/IntegrationService/v3/http/CasinoGameAPI/game/url";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("symbol", "vswayslions");
        paramMap.put("language", "en");
        paramMap.put("externalPlayerId", "10000111");

        paramMap.put("currency", "USD");
        //可以不传 paramMap.put("currency", "CNY");
        //log.info("{} PP游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} PP游戏获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            System.out.println(JSON.toJSONString(response.body()));
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.getString("error");
                if ("0".equals(error)) {
                    return jsonObject;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            //log.error("{} 获取游戏列表发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONObject();
    }

    public static JSONObject createPlayer(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        String url = "https://api.prerelease-env.biz/IntegrationService/v3/http/CasinoGameAPI/player/account/create";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("externalPlayerId", "test10010");
        paramMap.put("currency", "USD");
        //log.info("{} createPlayer游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            //log.error("{} createPlayer获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            System.out.println(response);
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.get("error");
                if ("0".equals(error)) {
                    return jsonObject;
                }else {
                   // log.error("{} CreatePlayer返回错误, 请求返回body: {}", VenueEnum.PP.getVenueName(), response.body());
                }
            }else {
               // log.error("{} CreatePlayer返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            //log.error("{} CreatePlayer发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONObject();
    }


    //NOTE 2.1 GetCasinoGames
    public static JSONArray GetCasinoGames() {
        String url = "https://api.prerelease-env.biz/IntegrationService/v3/http/CasinoGameAPI/getCasinoGames";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", "wnt_winto01");
        paramMap.put("options", "GetFrbDetails,GetLines");//&options=GetFrbDetails,GetLines
        //log.info("{} GetCasinoGames游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, "739721332dAd4b97");
        } catch (Exception e) {
            System.out.println(e);
            //log.error("{} 获取游戏列表Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
        }
        paramMap.put("hash", hashStr);
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.get("error");
                if ("0".equals(error)) {
                    System.out.println(JSON.toJSONString(jsonObject.getJSONArray("gameList")));
                    return jsonObject.getJSONArray("gameList");
                } else {
                    //log.error("{} GetCasinoGames返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response.body());
                }
            } else {
                //log.error("{} GetCasinoGames请求错误, 返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            //log.error("{} GetCasinoGames获取游戏列表发生错误!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONArray();
    }


}
