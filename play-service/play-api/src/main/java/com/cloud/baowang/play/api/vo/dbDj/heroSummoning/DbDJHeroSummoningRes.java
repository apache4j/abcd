package com.cloud.baowang.play.api.vo.dbDj.heroSummoning;

import com.alibaba.fastjson2.JSONObject;
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
/**
 * DB 电竞 英雄召唤注单
 */
public class DbDJHeroSummoningRes {

    private Long order_id;               // 注单ID
    private String ticket_plan_no;       // 期号
    private String member_account;       // 会员账号
    private Long member_id;              // 会员ID
    private Long merchant_id;            // 商户ID
    private String merchant_account;     // 商户账号
    private String parent_merchant_account; // 上级商户账号
    private Long parent_merchant_id;     // 上级商户ID
    private Long top_merchant_id;        // 顶级商户Id
    private String top_merchant_account; // 顶级商户账号
    private Long game_id;                // 游戏ID
    private BigDecimal bet_amount;           // 投注金额 （元）
    private String bet_num;              // 投注号码
    private String series_name;          // 彩系名称
    private Long bet_time;               // 投注时间（毫秒）
    private Long plan_sales_start_time;  // 彩系开始时间
    private Integer bet_nums;            // 投注注数
    private Integer bet_multiple;        // 投注倍数
    private Integer bet_status;          // 注单状态
    private Integer cancel_status;       // 撤单状态
    private Integer cancel_type;         // 撤单类型
    private String play_level;           // 玩法群名
    private String play_name;            // 玩法名
    private String bet_content;          // 前台投注内容
    private BigDecimal win_amount;           // 中奖金额
    private Integer win_nums;            // 中奖注数
    private Long chase_id;               // 追号id
    private Integer chase_order;         // 注单类型
    private Long settle_time;            // 结算时间
    private Long update_time;            // 更新时间
    private BigDecimal theory_bonus;         // 理论奖金
    private Integer device;              // 设备
    private Integer currency_code;       // 币种编码
    private Integer tester;              // 是否测试账户
    private BigDecimal exchange_rate;        // 币种汇率
    private BigDecimal odd;                  // 赔率
    private Integer settle_status;       // 结算状态
    private String play_name_cn;         // 盘口名-中文
    private String play_name_en;         // 盘口名-英文
    private String bet_content_en;       // 注单详情名-英文
    private String bet_content_cn;       // 注单详情名-中文
    private String ticket_name_cn;       // 彩系名称-中文
    private String ticket_name_en;       // 彩系名称-英文

    private JSONObject ticket_hero; // 开奖结果英雄对象

}
