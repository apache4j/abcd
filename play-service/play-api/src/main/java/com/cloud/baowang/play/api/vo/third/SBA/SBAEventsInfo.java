package com.cloud.baowang.play.api.vo.third.SBA;

import java.time.LocalDateTime;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育获取盘口信息")
public class SBAEventsInfo implements Serializable {

    /**
     * 体育项目ID
     */
    private Integer sportType;

    /**
     * 体育项目名称
     */
    private String sportName;

    /**
     * 联赛ID
     */
    private String leagueId;

    /**
     * 联赛名称
     */
    private String leagueName;

    /**
     * 联赛图片URL, 如果图片不存在请使用预设联赛图片URL
     * {domain}/LeagueImg/league_flag.png
     * domain请用leagueIconUrl返回的domain取代
     * No
     */
    private String leagueIconUrl;

    /**
     * 赛事ID
     */
    private String eventId;

    /**
     * 赛事代码
     */
    private String eventCode;

    /**
     * 赛事状态
     * running/ closed/ postponed/ deleted
     */
    private String eventStatus;

    /**
     * 是否为主要盘口
     */
    private boolean isMainMarket;

    /**
     * 系统开赛时间 (时区GMT+0)
     */
    private LocalDateTime kickOffTime;

    /**
     * 开赛时间 (时区GMT+0)
     */
    private LocalDateTime globalShowTime;

    /**
     * 联赛国别代码
     */
    private String countryCode;

    /**
     * 赛事比赛有多少节
     */
    private int gameSession;

    /**
     * 该赛事的母赛事ID
     */
    private int parentId;

    /**
     * 是否为测试赛事
     */
    private boolean isTest;

    /**
     * 是否为滚球赛事
     * $filter=islive eq true
     */
    private boolean isLive;

    /**
     * 是否为串关赛事
     * $filter=isparlay eq true
     */
    private boolean isParlay;

    /**
     * 赛事是否支持实时兑现
     * $filter=iscashout eq true
     */
    private boolean isCashout;

    /**
     * 是否为虚拟赛事
     * $filter=isvirtualevent eq true
     */
    private boolean isVirtualEvent;

    /**
     * 是否有滚球盘口
     */
    private boolean hasLiveMarket;

    /**
     * 该赛事的所有盘口数量
     */
    private int marketCount;

    /**
     * 该赛事的所有盘口投注类型
     * 0: None (主要)
     * 1: FullTime (全场)
     * 2: Half (半场)
     * 3: Corners (角球)/Bookings (罚牌)
     * 4: Intervals (区间)
     * 5: Specials (特别产品)
     * 6: Players (选手)
     * 7: FastMarket (快速盘口)
     * 8: Quarter (节)
     * 9: ExtraTime (加时)
     * 10: Penalty (点球)
     * 11-19: E-Sports Map 1-9 (电子竞技适用 – 地图 1~9)
     * No
     */
    private List<Integer> marketCategories;

    /**
     * 视频ID
     */
    private int streamingOption;

    /**
     * 视频代码
     * $filter=channelCode ne null
     */
    private String channelCode;

    /**
     * 沙巴体育赛事队伍信息
     */
    private SBATeamInfo teamInfo;


    public boolean validate() {
        return !ObjectUtil.isEmpty(teamInfo) &&
                !ObjectUtil.isEmpty(sportName) &&
                !ObjectUtil.isEmpty(leagueId) &&
                !ObjectUtil.isEmpty(eventId) &&
                !ObjectUtil.isEmpty(globalShowTime);
    }

    /**
     * 沙巴体育赛事队伍信息验证
     */
    public boolean teamInfoValidate() {
        return !ObjectUtil.isEmpty(teamInfo) &&
                !ObjectUtil.isEmpty(teamInfo.getHomeName()) &&
                !ObjectUtil.isEmpty(teamInfo.getHomeId()) &&
                !ObjectUtil.isEmpty(teamInfo.getAwayId()) &&
                !ObjectUtil.isEmpty(teamInfo.getAwayName());
    }


}
