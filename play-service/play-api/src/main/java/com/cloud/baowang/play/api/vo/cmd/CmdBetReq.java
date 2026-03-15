package com.cloud.baowang.play.api.vo.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdBetReq implements Serializable {

    // 方法名
    private Integer ActionId;
    // CMD用户名
    private String SourceName;
    // 当前交易金额变更余额
    private BigDecimal TransactionAmount;
    // 注单号
    private String ReferenceNo;
    //玩法类型
    private String TransType;
    //让球数
    private BigDecimal Hdp;
     //是否是上半场
    private Boolean IsFirstHalf;
    //赔率
    private BigDecimal Odds;
    //下注平台 1 = Desktop 7 = Mobile(v1) 9 = New Mobile(v1) 10 = Mobile E-Sport(v3) 11 = New Mobile E-Sport(v3)
    private Integer BetSource;
    //是否为主队让球
    private Boolean IsHomeGive;
    //是否投注主队
    private Boolean IsBetHome;
    //下注时的IP
    private String BetIP;
    //货币代码
    private String Currency;
    //注单赛事ID
    private Integer MatchId;
    //注单赛事系统ID
    private String MatchGroupId;
    //联赛ID
    private Integer LeagueId;
    //主队ID
    private Integer HomeTeamId;
    //客队ID
    private Integer AwayTeamId;
   //SpecialId
    private String SpecialId;
    //运动项目
    private String SportType;
    //球赛开赛日期
    private Long MatchDate;
    //是否为滚球
    private Boolean IsRunning;
    //注单赛事工作日
    private String WorkingDate;
    //主队名称
    private String HomeTeamName;
    //客队名称
    private String AwayTeamName;
    //联赛名称
    private String LeagueName;


}
