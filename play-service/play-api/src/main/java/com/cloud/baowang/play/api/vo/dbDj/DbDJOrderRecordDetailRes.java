package com.cloud.baowang.play.api.vo.dbDj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbDJOrderRecordDetailRes {

    /**
     * 子单ID
     */
    private Long id;

    /**
     * 注单ID
     */
    private Long order_id;

    /**
     * 游戏ID
     */
    private Long game_id;

    /**
     * 联赛ID
     */
    private Long tournament_id;

    /**
     * 赛事ID
     */
    private Long match_id;

    /**
     * match_type 赛事类型 正常-1 冠军-2 大逃杀-3 篮球-4 主播盘-5 足球-6
     */
    private Integer match_type;

    /**
     * 盘口ID
     */
    private Long market_id;

    /**
     * 盘口中文名
     */
    private String market_cn_name;

    /**
     * 盘口英文名
     */
    private String market_en_name;

    /**
     * 队伍名称 主客队用 , 拼接
     */
    private String team_names;

    /**
     * 第几局
     */
    private Integer round;

    /**
     * 赛事阶段 1-初盘 2-滚盘
     */
    private Integer is_live;

    /**
     * 投注项ID
     */
    private Long odd_id;

    /**
     * 投注项名称
     */
    private String odd_name;

    /**
     * 投注项名称(英文)
     */
    private String odd_en_name;

    /**
     * 赔率
     */
    private String odd;

    /**
     * 注单状态 1-待结算 2-已取消 3-已中奖 4-未中奖 5-撤销 6-赢半 7-输半 8-走水
     */
    private Integer status;

    /**
     * 投注时间（毫秒）
     */
    private Long bet_time;

    /**
     * 赛事开始时间
     */
    private Long match_start_time;

    /**
     * 更新时间
     */
    private Long update_time;

    /**
     * 结算时间
     */
    private Long settle_time;

    /**
     * 结算次数
     */
    private Integer settle_count;

    /**
     * 队伍ID 主客队用 , 拼接
     */
    private String team_id;

    /**
     * 队伍中文名称 主客队用 , 拼接
     */
    private String team_cn_names;

    /**
     * 队伍英文名称 主客队用 , 拼接
     */
    private String team_en_names;

    /**
     * 联赛中文名称对象
     */
    private String tournament;

    /**
     * 联赛英文名称对象
     */
    private String tournament_en;

    /**
     * 联赛ID
     */
    private Long key;

    /**
     * 联赛名称
     */
    private String value;

    /**
     * 滚球比分 fifa、nba2k 两个游戏专有
     */
    private String live_score;


}
