package com.cloud.baowang.play.api.vo.cq9.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * CQ9投注实体
 *
 * @author: lavine
 * @creat: 2023/9/12
 */
@Data
public class CQ9BetDetailEntity {

    // 遊戲商
    private String gamehall;
    // 遊戲種類
    private String gametype;
    // 遊戲平台
    private String gameplat;
    // 遊戲代碼
    private String gamecode;
    // 玩家帳號
    private String account;
    // 注單號 ※ round為唯一值
    private String round;
    // 遊戲後餘額
    private BigDecimal balance;
    // 遊戲贏分（已包含彩池獎金及從PC贏得的金額）
    private BigDecimal win;
    // 下注金額
    private BigDecimal bet;
    // 有效下注額 ※ 此欄位值用於牌桌/真人/體彩類遊戲
    private BigDecimal validbet;
    // 彩池獎金
    private BigDecimal jackpot;
    // 彩池獎金貢獻值 ※ 從小彩池到大彩池依序排序 ( Mini / Minor / Major / Grand )
    private String jackpotcontribution;
    // 彩池獎金類別 ※ 此欄位值為空字串時，表示未獲得彩池獎金
    private String jackpottype;
    // 注單狀態 [complete] complete:完成
    private String status;
    // 遊戲結束時間，格式為 RFC3339
    private String endroundtime;
    // 當筆資料建立時間，格式為 RFC3339 ※系統結算時間, 注單結算時間及報表結算時間都是createtime
    private String createtime;
    // 下注時間，格式為 RFC3339
    private String bettime;
    /**
     * 回傳 free game / bonus game / luckydraw / item / reward 資訊
     * ※slot 會回傳 free game / bonus game / luckydraw 資訊
     * ※fish 會回傳 item / reward 資訊
     * ※table/live 會回傳空陣列
     */
    private String detail;
    // [true|false]是否為再旋轉形成的注單
    private String singlerowbet;
    // 免費券id
    private String ticketid;
    /**
     * 免費券類型
     * 1 = 免費遊戲 ( 獲得一局 free game )
     * 2 = 免費 spin ( 獲得一次 free spin )
     */
    private String tickettype;
    /**
     * 免費券取得類型
     * 1 = 活動贈送
     * 101 = 代理贈送
     * 111 = 寶箱贈送
     * 112 = 商城購買
     */
    private String giventype;
    // 免費券下注額
    private BigDecimal ticketbets;
    // 庄(banker) or 閒(player) ※ 此欄位為牌桌遊戲使用，非牌桌遊戲此欄位值為空字串
    private String gamerole;
    /**
     * 對戰玩家是否有真人[pc|human]
     * pc：對戰玩家沒有真人
     * human：對戰玩家有真人
     * ※ 此欄位為牌桌遊戲使用，非牌桌遊戲此欄位值為空字串
     * ※ 如果玩家不支持上庄，只存在與系统對玩。則bankertype 為 PC
     */
    private String bankertype;
    // 抽水金額 ※ 此欄位為牌桌遊戲使用
    private BigDecimal rake;
    // 開房費用
    private BigDecimal roomfee;
    /**
     * 下注玩法
     * 真人參數說明
     * ※ 此欄位為真人遊戲使用，非真人遊戲此欄位值為空陣列
     */
    private String bettype;
    /**
     * 遊戲結果
     * 真人參數說明
     * ※此欄位為真人遊戲使用，非真人遊戲此欄位值為空陣列
     */
    private String gameresult;
    /**
     * 真人注單參數說明名稱 (1=百家，4=龍虎 )
     * 真人參數說明
     * ※此欄位為真人遊戲使用，非真人遊戲此欄位值為空字串
     */
    private String tabletype;
    /**
     * 桌號
     * 真人參數說明
     * ※ 此欄位為真人遊戲使用，非真人遊戲此欄位值為空字串
     */
    private String tableid;
    /**
     * 局號
     * 真人參數說明
     * ※ 此欄位為真人遊戲使用，非真人遊戲此欄位值為空字串
     */
    private String roundnumber;
    // 幣別
    private String currency;
    // 派彩加成金額
    private BigDecimal cardwin;
    // 打賞金額 ※ 此欄位為真人遊戲使用，非真人遊戲此欄位值為0
    private BigDecimal donate;
    // 打賞判別 ※ 此欄位為真人遊戲使用，非真人遊戲此欄位值為false
    private Boolean isdonate;

    // 原始注单
    private String originalBetDetail;
}
