package com.cloud.baowang.play.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.play.api.enums.sb.*;
import com.cloud.baowang.play.game.shaba.response.LangName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SBAOrderParseUtil {

    /**
     * 获取联赛，比赛，球队中文名称
     *
     * @param sportJson
     * @return
     */
    public static String getSportName(final Object sportJson) {
        String homeTeamName = "";
        if (ObjectUtil.isEmpty(sportJson)) {
            return "";
        }
        String homeTeamList = JSON.toJSONString(sportJson);
        List<LangName> list = JSONArray.parseArray(homeTeamList, LangName.class);

        for (LangName item : list) {
            if (item.getLang().equals("cs")) {
                homeTeamName = item.getName();
                break;
            }
            homeTeamName = item.getName();
        }

        return homeTeamName;
    }

    public static String getBetTypeName(final Map<String, Object> map) {
        if (BigDecimal.ZERO.compareTo(new BigDecimal(map.get("home_hdp").toString())) == 0) {

        }
        return null;
    }


    /**
     * 获取比赛信息
     *
     * @param map
     */
    public static String getSimpleMatchInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String leagueName = "联赛名称:" + getSportName(map.get("leaguename"));
        stringBuilder.append(leagueName);
        String VS = getCommonMarketInfo(map).contains("冠军") ? "" : getSportName(map.get("hometeamname"))
                + " VS " + getSportName(map.get("awayteamname"));

        stringBuilder.append("\n").append("对阵表:").append(VS);

        String matchId = null == map.get("match_id") ? "" : "赛事ID:" + map.get("match_id");

        stringBuilder.append("\n").append(matchId);
        // 赛事详情
//        return getSportName(map.get("leaguename")) + "\n" + VS + matchId;
        return stringBuilder.toString();
    }

    /**
     * 获取盘口信息
     *
     * @param map
     * @return
     */
    public static String getMarketInfo(final Map<String, Object> map) {
        String bet = getCommonMarketInfo(map);
        String isLive = "";
        if (null != map.get("islive")) {
            isLive = "阶段: " + SBLiveEnums.nameOfCode(map.get("islive").toString());
        }
        return bet + isLive;
    }

    /**
     * 串关类型
     */
    public static String getBetTypeList(String comBoType) {
        StringBuilder builder = new StringBuilder();
        final String text = "个注单";

        if (comBoType == null) {
            return null;
        }
        int i = 1;
        String[] list = comBoType.split(",");
        for (String item : list) {
            item = item.toLowerCase();

            for (SBComboTypeEnum num : SBComboTypeEnum.values()) {
                if (item.contains(num.getName().toLowerCase())) {
                    String value = item.replace(num.getName().toLowerCase(), num.getDescription());
                    if (value.contains(text)) {
                        int index = value.indexOf(text);
                        if (index != -1) {
                            value = value.substring(0, index + text.length() + 1);
                        }
                    } else {
                        value = replaceNonDigitsInParentheses(value);
                        value = value.replace("s", "");//如果还多出s 则直接去掉
                    }
                    builder.append(value);
                    break;
                }
            }
            if (i < list.length) {
                builder.append(",");
            }
            i++;
        }
        return builder.toString();
    }

    public static String replaceNonDigitsInParentheses(String input) {
        return input.replaceAll("\\((\\d+)\\D*\\)", "($1 个注单)");
    }

    public static String getCommonMarketInfo(final Map<String, Object> map) {
        if (null != map.get("bettypename")) {
//            List<Map<String, Object>> list = (List<Map<String, Object>>) JSON.parse(map.get("bettypename").toString());
//            return list.stream().filter(obj -> "cs".equals(obj.get("lang"))).findFirst()
//                    .orElse(new HashMap<>()).get("name").toString() + "\n";
            return getSportName(map.get("bettypename"));
        }

        return "";
    }

    /**
     * 获取注单信息
     *
     * @param map
     * @return
     */
    public static String getOrderInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("投注:");
        if (null != map.get("bet_team")) {
            String betTeam = (String) map.get("bet_team");
            SBBetTeamEnum sbBetTeamEnum = SBBetTeamEnum.of(betTeam);
            if (sbBetTeamEnum != null) {
                stringBuilder.append(" ").append(sbBetTeamEnum.getName());
            }
        }


        String teamName = "";

        //主队
        if (null != map.get("hometeamname")) {
            teamName = getSportName(map.get("hometeamname"));
        } else {//客队
            teamName = getSportName(map.get("awayteamname"));
        }

        stringBuilder.append(" ").append(teamName);


        //让球
//        if (map.get("sport_type") != null) {
//            Integer sportType = (Integer) map.get("sport_type");
//            if (SBBetTypeEnum.getHandicap().contains(sportType)) {
//                if (map.get("hdp") != null) {
//                    stringBuilder.append(" ").append(map.get("hdp"));
//                }
//            }
//        }

        if (map.get("hdp") != null) {
            stringBuilder.append(" ").append(map.get("hdp"));
        }


        //赔率
        stringBuilder.append(" ").append("赔率:").append(map.get("odds"));


        return stringBuilder.toString();
//        return getCommonMarketInfo(map) + "@" + map.get("odds");

    }

    /**
     * 获取结算时间
     *
     * @param map
     * @return
     */
    public static String getSettleTime(final Map<String, Object> map) {
        return null == map.get("settlement_time") ? "" : map.get("settlement_time")
                .toString();
    }

    /**
     * 获取注单结果
     *
     * @param map
     * @return
     */
    public static String getOrderResult(final Map<String, Object> map) {
        if (null != map.get("ticket_status")) {
            return "注单结果:" + SBTicketStatusEnum.nameOfCode(map.get("ticket_status").toString());
        }
        return null;
    }

    public static Map<String, Object> getParlayInfoList(String parlayInfo) {
        Map<String, Object> parlayMap;
        if (StringUtils.isNotBlank(parlayInfo)) {
            try {
                parlayMap = (Map<String, Object>) JSONObject.parse(parlayInfo);
                return parlayMap;
            } catch (Exception e) {
                log.error("注单明细类型转换map发生异常", e);
            }
        }
        return null;
    }

    public static Map<String, String> getNewOrderInfo(String parlayInfo) {
        Map<String, String> result = Maps.newHashMap();
        Map<String, Object> parlayMap = getParlayInfoList(parlayInfo);
        StringBuilder playInfo = new StringBuilder();
        StringBuilder gameName = new StringBuilder();

        StringBuilder orderInfo = new StringBuilder();
        if (null != parlayMap && parlayMap.containsKey("ParlayData") && parlayMap.get("ParlayData") != null) {
            String parlayData = parlayMap.get("ParlayData").toString();
//            JSONArray jsonArray = JSONArray.parseArray(parlayData);
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
//            List<Map<String, Object>> list = gson.fromJson(jsonArray.toString(), listType);
            try {


                ObjectMapper objectMapper = new ObjectMapper();
                // 将字符串转换为 List<Map<String, Object>>
                List<Map<String, Object>> list = objectMapper.readValue(parlayData,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
                for (Map<String, Object> map : list) {
                    orderInfo.append(getOrderInfo(map)).append(",");
                }

                //串关取第一个数组中的数据,因为都是一样的。
                if (CollectionUtils.isNotEmpty(list)) {
                    Map<String, Object> detailMap = list.get(0);
                    if (ObjectUtil.isNotEmpty(detailMap)) {
                        playInfo.append(getCommonMarketInfo(detailMap)).append(",");
                        if (detailMap.get("sportname") != null) {
                            gameName.append(getSportName(detailMap.get("sportname")));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("转换异常:{}", parlayInfo, e);
            }
        } else if (null != parlayMap && !parlayMap.containsKey("ParlayData")) {
//            gameName.append(getSportName(parlayMap.get("sportname"))).append(" ")
//                    .append(getMatchInfo(parlayMap)).append(",");
            playInfo.append(getCommonMarketInfo(parlayMap)).append(",");
            orderInfo.append(getOrderInfo(parlayMap)).append(",");
        }
        if (parlayMap != null && parlayMap.get("sportname") != null) {
            gameName.append(getSportName(parlayMap.get("sportname")));
        }
        result.put("gameName", gameName.toString());

//        result.put("gameName", ObjectUtil.isEmpty(gameName) ? null : gameName
//                .substring(0, gameName.toString().length() - 1));
        result.put("playInfo", ObjectUtil.isEmpty(playInfo) ? null : playInfo
                .substring(0, playInfo.toString().length() - 1));
        result.put("orderInfo", ObjectUtil.isEmpty(orderInfo) ? null : orderInfo
                .substring(0, orderInfo.toString().length() - 1));
        return result;
    }


    /**
     * X Y 变量字符串替换
     */
    public static String getBetTag(String betTeamName, Map<String, Object> parlayMap) {
        if (ObjectUtil.isEmpty(betTeamName) || parlayMap == null) {
            return betTeamName;
        }

        if (!parlayMap.containsKey("bet_tag") || parlayMap.get("bet_tag") == null) {
            return betTeamName;
        }
        String betTag = (String) parlayMap.get("bet_tag");
        if (StringUtils.isBlank(betTag)) {
            return betTeamName;
        }
        List<String> betTagArray = Arrays.asList(betTag.split("-"));

        betTeamName = betTeamName.toLowerCase();

        if (betTeamName.contains("x") && betTeamName.contains("y")) {
            if (betTagArray.size() > 1) {
                String x = betTagArray.get(0);
                x = getBetTeamName(x);
                String y = betTagArray.get(1);
                y = getBetTeamName(y);
                betTeamName = betTeamName.replace("x", x);
                betTeamName = betTeamName.replace("y", y);
                return betTeamName;
            }
        } else if (betTeamName.contains("x")) {
            String x = betTagArray.get(0);
            x = getBetTeamName(x);
            betTeamName = betTeamName.replace("x", x);
            return betTeamName;
        }

        return betTeamName;
    }

    /**
     * 如果第一位数是0,则直接去掉
     */
    private static String getBetTeamName(String betTeamName) {
        if (ObjectUtil.isEmpty(betTeamName)) {
            return betTeamName;
        }
        if (betTeamName.length() > 1 && betTeamName.charAt(0) == '0') {
            betTeamName = betTeamName.substring(1);  // 去掉第一个字符
        }
        return betTeamName;
    }

    public static String getBetTeamName(List<CodeValueVO> list, Map<String, Object> parlayMap) {
        String betTeamName = null;
        Integer sportType = null;
        String betTeam = null;
        if (parlayMap.containsKey("bet_type") && parlayMap.containsKey("bet_team")) {
            sportType = (Integer) parlayMap.get("bet_type");
            betTeam = (String) parlayMap.get("bet_team");
            if (ObjectUtil.isNotEmpty(sportType)) {
                if (CollectionUtil.isNotEmpty(list)) {
                    Map<String, String> map = list.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));
                    String value = map.get(betTeam);
                    if (ObjectUtil.isNotEmpty(value)) {
                        betTeamName = value;
                    }
                }
            }
        }
        return betTeamName;
    }

    /**
     * 对指定玩法的说明进行特殊处理
     */
    public static String replaceBetName(Integer betType, String betTeamName, Map<String, Object> parlayMap) {
        if (betType == null || ObjectUtil.isEmpty(betTeamName)) {
            return betTeamName;
        }

        //只有这4种的 玩法比较特殊需要特殊处理
        List<Integer> betTeamList = SBBetTypeEnum.getThreeHandicap();

        if (!betTeamList.contains(betType)) {
            return betTeamName;
        }


        BigDecimal hdp = null;

        if (parlayMap.containsKey("hdp") && parlayMap.get("hdp") != null) {
            hdp = new BigDecimal(parlayMap.get("hdp").toString());
        }

        if (hdp != null) {
            if (hdp.compareTo(BigDecimal.ZERO) > 0) {
                betTeamName = betTeamName.replace("n", "+" + hdp);
            } else if (hdp.compareTo(BigDecimal.ZERO) < 0) {
                betTeamName = betTeamName.replace("n", "-" + hdp);
            } else {
                betTeamName = betTeamName.replace("n", "");
            }
        }

        String xTeam = null;
        if (hdp != null) {

//            为正数的情况下home_hdp和away_hdp反着取。
//            意思是说home_hdp有值就表示客，away_hdp就表示主。
//            为负数的情况下home_hdp和away_hdp正着取。
//            意思是说home_hdp有值就表示主，away_hdp就表示主客。
            boolean isHdpPositive = BigDecimal.ZERO.compareTo(hdp) > 0;

            BigDecimal homeHdp = parlayMap.containsKey("home_hdp") ? (BigDecimal) parlayMap.get("home_hdp") : null;
            BigDecimal awayHdp = parlayMap.containsKey("away_hdp") ? (BigDecimal) parlayMap.get("away_hdp") : null;

            String lang = CurrReqUtils.getLanguage();
            boolean boolLang = LanguageEnum.ZH_CN.getLang().equals(lang);

            if (!isHdpPositive) {
                if (homeHdp != null && homeHdp.compareTo(BigDecimal.ZERO) > 0) {
                    if(boolLang){
                        xTeam = "客";
                    }else{
                        xTeam = "away";
                    }

                }
                if (awayHdp != null && awayHdp.compareTo(BigDecimal.ZERO) > 0) {
                    if(boolLang){
                        xTeam = "主";
                    }else{
                        xTeam = "home";
                    }

                }
            } else {
                if (homeHdp != null && homeHdp.compareTo(BigDecimal.ZERO) > 0) {
                    if(boolLang){
                        xTeam = "主";
                    }else {
                        xTeam = "home";
                    }

                }
                if (awayHdp != null && awayHdp.compareTo(BigDecimal.ZERO) > 0) {
                    if(boolLang){
                        xTeam = "客";
                    }else {
                        xTeam = "away";
                    }

                }
            }
        }

        //存在x 就是需要替换
        if (xTeam != null && betTeamName.contains("x")) {
            betTeamName = betTeamName.replace("x", xTeam);
        }
        return betTeamName;
    }

}
