package com.cloud.baowang.play.api.vo.dbDj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbDJOrderRecordBetRes {

    /**
     * 注单ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long member_id;

    /**
     * 会员账号
     */
    private String member_account;

    /**
     * 商户ID
     */
    private Long merchant_id;

    /**
     * 商户账号
     */
    private String merchant_account;

    /**
     * 上级商户账号
     */
    private String parent_merchant_account;

    /**
     * 上级商户ID
     */
    private Long parent_merchant_id;

    /**
     * 是否测试会员 0-正式 1-测试
     */
    private Integer tester;

    /**
     * 注单类型 1-普通注单 2-普通串关注单 3-局内串关注单 4-复合玩法注单
     */
    private Integer order_type;

    /**
     * 串关类型 1-普通注单 2:2串1 3:3串1 ... 8:8串1
     */
    private Integer parley_type;

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
     * 赛事类型 正常-1 冠军-2 大逃杀-3 篮球-4 主播盘-5 足球-6
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
     * 第几局
     */
    private Integer round;

    /**
     * 赔率
     */
    private String odd;

    /**
     * 投注金额
     */
    private BigDecimal bet_amount;

    /**
     * 中奖金额
     */
    private BigDecimal win_amount;

    /**
     * 赛事阶段 1-初盘 2-滚盘
     */
    private Integer is_live;

    /**
     * 注单状态 3-待结算 4-已取消 5-赢 6-输 7-已撤销 8-赢半 9-输半 10-走水
     */
    private Integer bet_status;

    /**
     * 确认方式 1-自动确认 2-手动待确认 3-手动确认 4-手动拒绝
     */
    private Integer confirm_type;

    /**
     * 投注时间（毫秒）
     */
    private Long bet_time;

    /**
     * 结算时间
     */
    private Long settle_time;

    /**
     * 赛事开始时间
     */
    private Long match_start_time;

    /**
     * 更新时间
     */
    private Long update_time;

    /**
     * 结算次数
     */
    private Integer settle_count;

    /**
     * 队伍ID 主客队用 , 拼接
     */
    private String team_id;

    /**
     * 设备 [1-PC 2-H5 3-Android 4-IOS]
     */
    private Integer device;

    /**
     * 投注IP
     */
    private Long bet_ip;

    /**
     * 队伍中文名称 主客队用 , 拼接
     */
    private String team_cn_names;

    /**
     * 队伍英文名称 主客队用 , 拼接
     */
    private String team_en_names;

    /**
     * 基准分 fifa、nba2k 两个游戏专有
     */
    private String score_benchmark;

    /**
     * 币种编码
     */
    private Integer currency_code;

    /**
     * 币种汇率
     */
    private BigDecimal exchange_rate;

    /**
     * 联赛名称-中文
     */
    private String tournament;

    /**
     * 联赛名称-英文
     */
    private String tournament_en;


    //串关信息
    private List<DbDJOrderRecordDetailRes> detail;


}
