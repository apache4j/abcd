package com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbPanDaSportOrderDetail {


    /**
     * 投注项编号
     */
    private Long betNo;

    /**
     * 投注项Id
     */
    private Long playOptionsId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 比赛开始时间
     */
    private Long beginTime;

    /**
     * 注单金额
     */
    private BigDecimal betAmount;

    /**
     * 联赛名称
     */
    private String matchName;

    /**
     * 比赛对阵
     */
    private String matchInfo;

    /**
     * 投注类型 DbPanDaSportMatchTypeEnum.java
     * 1 ：早盘赛事
     * 2：滚球盘赛事
     * 3：冠军盘赛事
     * 4：虚拟赛事
     * 5：电竞赛事
     */
    private Integer matchType;

    /**
     * 赛种ID
     */
    private Integer sportId;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 投注项(如:主客队)
     */
    private String playOptions;

    /**
     * 游戏名称
     */
    private String sportName;

    /**
     * 联赛ID
     */
    private Long tournamentId;

    /**
     * 投注项名称
     */
    private String playOptionName;

    /**
     * 玩法名称
     */
    private String playName;

    /**
     * DbPanDaSportMarketTypeEnum.java
     * 盘口类型。EU: 欧盘, HK: 香港盘, US:美式盘, ID:印尼盘 , MY:马来盘 ,GB:英式盘
     */
    private String marketType;

    /**
     * 盘口值
     */
    private String marketValue;

    /**
     * 让球值
     */
    private BigDecimal handicap;

    /**
     * 结算比分
     */
    private String settleScore;

    /**
     * 基准分
     */
    private String scoreBenchmark;

    /**
     * 当前赔率，以欧洲盘表示
     */
    private BigDecimal oddsValue;

    /**
     * 注单结算结果 DbPanDaSportBetResultEnum.java
     * 0：无结果
     * 2：走水
     * 3：输
     * 4：赢
     * 5：赢一半
     * 6：输一半
     * 7：赛事取消
     * 8：赛事延期
     * 11：比赛延迟
     * 12：比赛中断
     * 13：未知
     * 15：比赛放弃
     * 16：异常盘口
     * 17：未知赛事状态
     * 18：比赛取消
     * 19：比赛延期
     */
    private String betResult;

    /**
     * 最终赔率，按照marketType的盘口类型表示
     */
    private BigDecimal oddFinally;


}
