package com.cloud.baowang.play.game.cmd.utils;

import com.cloud.baowang.play.game.cmd.enums.CmdBetTypeEnum;
import com.cloud.baowang.play.game.cmd.enums.CmdOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CmdOrderParseUtil {

    /**
     * 获取比赛信息
     *
     */
    public static String getSimpleMatchInfo(String leaguename,String homeTeamName,String awayTeamName, String gameNo) {
        StringBuilder stringBuilder = new StringBuilder();
        String leagueName = "联赛名称:" + leaguename;
        stringBuilder.append(leagueName);
        String VS = homeTeamName + " VS " + awayTeamName;
        stringBuilder.append("\n").append("对阵表:").append(VS);
        String matchId ="赛事ID:" + gameNo;
        stringBuilder.append("\n").append(matchId);
        return stringBuilder.toString();
    }

    /**
     * 获取盘口信息
     *
     * @param
     * @return
     */
    public static String getMarketInfo(CmdBetTypeEnum cmdBetTypeEnum, String info,String half) {
        return "投注: "+half+" "+cmdBetTypeEnum.getDescCn()+" 阶段: " + info;
    }


    /**
     * 获取注单信息
     *
     * @param
     * @return
     */
    public static String getOrderInfo(String teamName,String betType,String betInfo,String odds,String planOdds) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" 下注项:").append(betInfo).append("\n");
        stringBuilder.append(" 赔率:").append(odds).append("\n");
        stringBuilder.append(" 盘口:").append(planOdds);
        return stringBuilder.toString();
    }
    /**
     * 获取注单结果
     *
     * @param
     * @return
     */
    public static String getOrderResult(Integer StatusChange,  String dangerStatus , String winLoseStatus) {
        return "注单结果:" + getSportStatus(StatusChange,dangerStatus,winLoseStatus).getDescCn();
    }

    public static CmdOrderStatusEnum getSportStatus(Integer StatusChange,  String dangerStatus , String winLoseStatus) {
        if (StatusChange>=2){
            return CmdOrderStatusEnum.RESETTLEMENT;
        }
        if ("C".equals(dangerStatus) || "R".equals(dangerStatus)){
            return CmdOrderStatusEnum.CANCELED;
        }
        return CmdOrderStatusEnum.of(winLoseStatus);
    }
}
