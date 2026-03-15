package com.cloud.baowang.play.game.shaba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.play.api.enums.sb.*;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.data.transfer.cache.SystemDictCache;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.shaba.response.LangName;
import com.cloud.baowang.play.game.shaba.response.ParlayData;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.util.SBAOrderParseUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.SBA)
public class SBASportsOrderInfoServiceImpl implements VenueOrderInfoService {

    private final SBAGameServiceImpl sbaGameService;

    private final SystemDictCache systemDictCache;

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.SBA;
    }


    /**
     * 注单详情需要去掉红色的标记
     */
    public String getOrderInfo(String input) {
        if (ObjectUtil.isEmpty(input)) {
            return input;
        }

        // 判断是否包含<span>标签
        if (input.matches(".*<span.*?>.*?</span>.*")) {
            System.out.println("包含<span>标签");
        } else {
            System.out.println("不包含<span>标签");
        }

        // 去掉所有HTML标签（如果只想去掉<span>，下一段代码更合适）
        return input.replaceAll("<[^>]+>", "");
    }


    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        boolean isList = false;
        List<Map<String, Object>> list = null;
        if (parlayMap.containsKey("ParlayData") && parlayMap.get("ParlayData") != null) {
            list = (List<Map<String, Object>>) parlayMap.get("ParlayData");
            isList = list.size() > 1;
        }

        String timeZone = CurrReqUtils.getTimezone();
        if (CommonConstant.ADMIN_CENTER_SITE_CODE.equals(timeZone)) {
            timeZone = DateUtils.UTC5_TIME_ZONE;
        }

        if (!isList) {
            Map<String, Object> sportsMap = Maps.newHashMap();
            // 比赛详情
            String matchInfo = SBAOrderParseUtil.getSimpleMatchInfo(parlayMap);
            sportsMap.put("matchInfo", matchInfo);
            // 盘口详情
            String marketInfo = SBAOrderParseUtil.getMarketInfo(parlayMap);
            sportsMap.put("marketInfo", SBAOrderParseUtil.getBetTag(marketInfo, parlayMap));

            String orderInfo = getOrderInfoDetail(parlayMap);

            // 注单详情
            sportsMap.put("orderInfo", getOrderInfo(orderInfo));

            if (parlayMap.containsKey("settlement_time") && parlayMap.get("settlement_time") != null) {
                String settlementTime = parlayMap.get("settlement_time").toString();
                String time = sbaGameService.formatTime(settlementTime);
                if (ObjectUtil.isNotEmpty(time)) {
                    Long settleTime = TimeZoneUtils.convertToTimestamp(time, timeZone, null);
                    sportsMap.put("settleTime", String.valueOf(settleTime));
                }
            }

            // 注单结果
            String orderResult = SBAOrderParseUtil.getOrderResult(parlayMap);
            sportsMap.put("orderResult", orderResult);
            resultList.add(sportsMap);
        } else {
            for (Map<String, Object> map : list) {
                Map<String, Object> sportsMap = Maps.newHashMap();
                String matchInfo = SBAOrderParseUtil.getSimpleMatchInfo(map);
                sportsMap.put("matchInfo", matchInfo);
                // 盘口详情
                String marketInfo = SBAOrderParseUtil.getMarketInfo(map);
                sportsMap.put("marketInfo", SBAOrderParseUtil.getBetTag(marketInfo, parlayMap));

                String orderInfo = getOrderInfoDetail(map);
                // 注单详情
                sportsMap.put("orderInfo", getOrderInfo(orderInfo));
                if (parlayMap.containsKey("settlement_time") && parlayMap.get("settlement_time") != null) {
                    String settlementTime = parlayMap.get("settlement_time").toString();
                    String time = sbaGameService.formatTime(settlementTime);
                    if (ObjectUtil.isNotEmpty(time)) {
                        Long settleTime = TimeZoneUtils.convertToTimestamp(time, timeZone, null);
                        sportsMap.put("settleTime", String.valueOf(settleTime));
                    }
                }

                // 注单结果
                String orderResult = SBAOrderParseUtil.getOrderResult(map);
                sportsMap.put("orderResult", orderResult);
                resultList.add(sportsMap);
            }
        }
        return resultList;
    }


    /**
     * 获取投注信息
     */
    private String getBetOrderInfo(Map<String, Object> parlayMap) {
        StringBuilder stringBuilder = new StringBuilder();

        try {

            String betTeamName = null;
            Integer sportType = null;
            String betTeam = null;
            if (parlayMap.containsKey("bet_type") && parlayMap.containsKey("bet_team")) {
                sportType = (Integer) parlayMap.get("bet_type");
                betTeam = (String) parlayMap.get("bet_team");
                if (ObjectUtil.isNotEmpty(sportType)) {
                    String key = I18MsgKeyEnum.SBA_BET_TEAM_NAME.getCode() + sportType;
                    List<CodeValueVO> list = systemDictCache.getSystemParamByType(key);
                    betTeamName = SBAOrderParseUtil.getBetTeamName(list, parlayMap);
                }
            }

            betTeamName = SBAOrderParseUtil.replaceBetName(sportType, betTeamName, parlayMap);


            if (betTeamName == null && parlayMap.containsKey("betteamname") && parlayMap.get("betteamname") != null) {

                JSONArray betTeamNameArray = JSON.parseArray(JSON.toJSONString(parlayMap.get("betteamname")));

                if (CollectionUtil.isNotEmpty(betTeamNameArray)) {
                    betTeamName = getLangName(betTeamNameArray.toJavaList(LangName.class));
                }
            }

            if (betTeamName != null) {
                stringBuilder.append(betTeamName);
            }

            if (parlayMap.containsKey("hdp") && parlayMap.get("hdp") != null) {
                BigDecimal hdp = new BigDecimal(parlayMap.get("hdp").toString());
                String strHdp = String.valueOf(hdp);

                //只有这4种的 玩法比较特殊需要特殊处理
                List<Integer> betTeamList = List.of(SBBetTypeEnum.SBA_BET_TYPE_28.getCode(),
                        SBBetTypeEnum.SBA_BET_TYPE_453.getCode(),
                        SBBetTypeEnum.SBA_BET_TYPE_477.getCode(),
                        SBBetTypeEnum.SBA_BET_TYPE_487.getCode()
                );

                //下注的类型不是和局并且是 三项让分投注 并且 hdp 大于0
                if (!"x".equals(betTeam) && betTeamList.contains(sportType) && hdp.compareTo(BigDecimal.ZERO) > 0) {
                    strHdp = "+" + hdp;
                }

                //只要不是 三项让分投注 并且是和局的 都要 显示 hdp
                if (!("x".equals(betTeam) && betTeamList.contains(sportType))) {
                    stringBuilder.append(" ").append(strHdp);
                }
            }
        } catch (Exception e) {
            log.info("沙巴转异常:{}", parlayMap, e);
        }


        String result = SBAOrderParseUtil.getBetTag(stringBuilder.toString(), parlayMap);
        result = processTeamString(result, parlayMap);

        //投注项红色显示
        if (ObjectUtil.isNotEmpty(result)) {
            result = "<span style=\"color: red;\">" + result + "</span>";
        }

        return result;
    }

    /**
     * 对指定玩法的说明进行特殊处理
     */


    private String getOrderInfoDetail(Map<String, Object> parlayMap) {
        StringBuilder stringBuilder = new StringBuilder();
        String betInfo = getBetOrderInfo(parlayMap);


        stringBuilder.append("投注:").append(betInfo);

//        if (parlayMap.containsKey("hdp") && parlayMap.get("hdp") != null) {
//            stringBuilder.append(" ").append(parlayMap.get("hdp"));
//        }

        stringBuilder.append("\n");

        if (parlayMap.containsKey("odds") && parlayMap.get("odds") != null) {
            stringBuilder.append("赔率:").append(parlayMap.get("odds"));
        }
        return stringBuilder.toString();
    }

    private String getSBAOrderInfo(Map<String, Object> parlayMap) {
        StringBuilder stringBuilder = new StringBuilder();
        String betInfo = getBetOrderInfo(parlayMap);

        stringBuilder.append("\n").append(betInfo);


        if (parlayMap.containsKey("odds") && parlayMap.get("odds") != null) {
            stringBuilder.append("@").append(parlayMap.get("odds"));
        }

        return stringBuilder.toString();
    }


    public String getBetType(String json) {

        if (ObjectUtil.isEmpty(json)) {
            return "单关";
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!jsonObject.containsKey("combo_type") || jsonObject.get("combo_type") == null) {
            return "单关";
        }
        String comBoType = (String) jsonObject.get("combo_type");

        return SBAOrderParseUtil.getBetTypeList(comBoType);
    }


    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        return footballRecordInfo(recordPO);
    }

    private String footballRecordInfo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);

            Long betTime = recordPO.getBetTime();
            List<ParlayData> parlayDataList = null;
            if (map.get("ParlayData") != null) {
                JSONArray jsonArray = (JSONArray) map.get("ParlayData");
                parlayDataList = jsonArray.toJavaList(ParlayData.class);
            }


            //单关
            if (CollectionUtil.isEmpty(parlayDataList)) {
                stringBuilder.append(SBSportTypeEnum.nameOfId((Integer) map.get("sport_type"))).append("(")
                        .append(TimeZoneUtils.convertTimestampToString(betTime, CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss))
                        .append(")")
                        .append("\n");

                stringBuilder.append(recordPO.getEventInfo());
//                JSONArray homeTeamJsonArray = (JSONArray) map.get("hometeamname");
//                JSONArray awayTeamJsonArray = (JSONArray) map.get("awayteamname");


                if (map.containsKey("hometeamname") && map.containsKey("awayteamname")) {
                    String homeName = map.get("hometeamname").toString();
                    JSONArray homeTeamArray = JSON.parseArray(homeName);

                    String awayName = map.get("awayteamname").toString();
                    JSONArray awayTeamArray = JSON.parseArray(awayName);

                    String homeTeamName = getLangNameByJSONArray(homeTeamArray);
                    String awayTeamName = getLangNameByJSONArray(awayTeamArray);
                    if (ObjectUtil.isNotEmpty(homeTeamName) && ObjectUtil.isNotEmpty(awayTeamName)) {
                        stringBuilder.append("\n").append(homeTeamName)
                                .append(" VS ")
                                .append(awayTeamName)
                                .append("(")
                                .append("赛事ID: ")
                                .append(recordPO.getGameNo())
                                .append(")");
                    }
                }


                String playType = getSBAOrderInfo(map);
//                stringBuilder.append("投注信息:")
                stringBuilder.append(playType);

                String sbLive = SBLiveEnums.nameOfCode(map.get("islive").toString());

                stringBuilder.append("\n").append(sbLive);
                JSONArray betTypeNameJsonArray = (JSONArray) map.get("bettypename");

                List<LangName> betTypeNameList = Lists.newArrayList();
                if (CollectionUtil.isNotEmpty(betTypeNameJsonArray)) {
                    betTypeNameList = betTypeNameJsonArray.toJavaList(LangName.class);

                    String betTypeName = betTypeNameList.stream()
                            .filter(langName -> "cs".equals(langName.getLang()))
                            .map(LangName::getName)
                            .collect(Collectors.joining(", "));
                    stringBuilder.append(" 玩法:").append(SBAOrderParseUtil.getBetTag(betTypeName, map)).append(" ");
                }


                if (map.containsKey("home_score") && map.containsKey("away_score")
                        && map.get("home_score") != null && map.get("away_score") != null) {
//                    Integer homeScore = (Integer) map.get("home_score");
//                    Integer awayScore = (Integer) map.get("away_score");
                    stringBuilder.append("\n").append("全场比分:").append(map.get("home_score")).append(":").append(map.get("away_score"));
                }

            } else {
                for (ParlayData item : parlayDataList) {
                    stringBuilder.append("\n").append(SBSportTypeEnum.nameOfId(item.getSport_type())).append("(")
                            .append(TimeZoneUtils.convertTimestampToString(betTime, CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss))
                            .append(")")
                            .append("\n");


                    //联赛信息
                    String leagueName = getLangName(item.getLeaguename());
                    stringBuilder.append(leagueName).append("\n");
                    String homeTeamName = getLangName(item.getHometeamname());
                    String awayTeamName = getLangName(item.getAwayteamname());

                    if (ObjectUtil.isNotEmpty(homeTeamName) && ObjectUtil.isNotEmpty(awayTeamName)) {
                        stringBuilder.append(homeTeamName)
                                .append(" VS ")
                                .append(awayTeamName)
                                .append("(")
                                .append("赛事ID: ")
                                .append(item.getMatch_id())
                                .append(")");
                    }


                    Map<String, Object> mapDetail = JSONObject.parseObject(JSON.toJSONString(item), Map.class);


                    //玩法
                    String playType = getBetOrderInfo(mapDetail);
//                    stringBuilder.append("投注信息:");
                    stringBuilder.append("\n").append(playType);

                    //让球数
//                    if (mapDetail.containsKey("bet_type") && mapDetail.get("bet_type") != null) {
//                        Integer betType = (Integer) mapDetail.get("bet_type");
//                        if (SBBetTypeEnum.getHandicap().contains(betType)) {
//                            if (mapDetail.containsKey("hdp") && mapDetail.get("hdp") != null) {
//                                stringBuilder.append(" ").append(mapDetail.get("hdp"));
//                            }
//                        }
//                    }


                    //赔率
                    if (ObjectUtil.isNotEmpty(item.getOdds())) {
                        stringBuilder.append("@").append(item.getOdds());
                    }


                    String sbLive = SBLiveEnums.nameOfCode(item.getIslive());

                    stringBuilder.append("\n").append(sbLive).append(" ");

                    if (CollectionUtil.isNotEmpty(item.getBettypename())) {
                        String betTypeName = item.getBettypename().stream()
                                .filter(langName -> "cs".equals(langName.getLang()))
                                .map(LangName::getName)
                                .collect(Collectors.joining(", "));
                        stringBuilder.append(" 玩法:").append(SBAOrderParseUtil.getBetTag(betTypeName, mapDetail)).append(" ");
                    }

                    Integer homeScore = item.getHome_score();
                    Integer awayScore = item.getAway_score();
                    if (ObjectUtil.isNotEmpty(homeScore) && ObjectUtil.isNotEmpty(awayScore)) {
                        stringBuilder.append("\n").append("全场比分:").append(homeScore).append(":").append(awayScore);
                    }
                }
            }

            String virtual = getVirtual(map);
            if (virtual != null) {
                //赛事类型
                stringBuilder.append("\n").append(virtual);
            }


            stringBuilder.append("\n").append(getBetType(JSON.toJSONString(map)));


        } catch (Exception e) {
            log.info("沙巴体育,注单详情异常:{}", recordPO, e);
        }
        return stringBuilder.toString();
    }

    /**
     * 虚拟赛事
     */
    private String getVirtual(Map<String, Object> map) {
        if (map.containsKey("game_group")) {
            if ("true".equals(map.get("game_group")) || map.get("game_group").toString().toLowerCase().contains("pingoal")) {
                return "正常赛事";
            } else if (map.get("game_group").toString().toLowerCase().contains("virtual")) {
                return "虚拟赛事";
            }
        }
        return null;
    }


    private String getLangName(List<LangName> list) {
        return list.stream()
                .filter(langName -> "cs".equals(langName.getLang()))
                .map(LangName::getName)
                .collect(Collectors.joining(", "));
    }

    private String getLangNameByJSONArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        //主队
        List<LangName> homeTeamList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(jsonArray)) {
            homeTeamList = jsonArray.toJavaList(LangName.class);
        }

        return getLangName(homeTeamList);
    }


    /**
     * 主队客队 需要增加 队伍名称
     */
    public String processTeamString(String input, Map<String, Object> parlayMap) {
        if (input == null) return null;

        if (!(parlayMap.containsKey("awayteamname") && parlayMap.get("awayteamname") != null &&
                parlayMap.containsKey("hometeamname") && parlayMap.get("hometeamname") != null)) {
            return input;
        }


        int zIndex = input.lastIndexOf("主队");
        int kIndex = input.lastIndexOf("客队");


        // 获取最后出现的位置
        int keywordIndex = Math.max(zIndex, kIndex);
        if (keywordIndex == -1) {
            return input; // 不包含关键字
        }

        String teamName = null;

        //主队
        if (zIndex > -1) {
            String hometeamname = parlayMap.get("hometeamname").toString();
            JSONArray teamArray = JSON.parseArray(hometeamname);
            if (teamArray == null) {
                return input;
            }
            teamName = getLangNameByJSONArray(teamArray);
            if (ObjectUtil.isEmpty(teamName)) {
                return input;
            }
        }

        if (kIndex > -1) {
            String awayteamname = parlayMap.get("awayteamname").toString();
            JSONArray teamArray = JSON.parseArray(awayteamname);
            if (teamArray == null) {
                return input;
            }
            if (teamName == null) {
                teamName = "";
            }
            teamName += " " + getLangNameByJSONArray(teamArray);
            if (ObjectUtil.isEmpty(teamName)) {
                return input;
            }
        }


        int endOfKeyword = keywordIndex + 2; // “队” 的后一个字符位置

        // 情况1：后面没字符
        if (endOfKeyword >= input.length()) {
            return input + " " + teamName;
        }

        // 情况2 & 3：有字符，检查是否包含空格
        String afterKeyword = input.substring(endOfKeyword);
        if (afterKeyword.contains(" ")) {
            int lastSpaceIndex = input.lastIndexOf(" ");
            if (lastSpaceIndex != -1 && lastSpaceIndex < input.length() - 1) {
                return input.substring(0, lastSpaceIndex + 1)
                        + " " + teamName + " "
                        + input.substring(lastSpaceIndex + 1);
            }
        }

        // 情况3：有字符但没有空格 → 末尾加
        return input + " " + teamName;
    }


}
