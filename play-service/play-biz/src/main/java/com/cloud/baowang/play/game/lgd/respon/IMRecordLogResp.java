package com.cloud.baowang.play.game.lgd.respon;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class IMRecordLogResp {
    // 投注id
    private String id;
    // 状态
    private String status;
    // 投注信息
    private String BetInfo;
    // 游戏id
    private String gameId;
    // 游戏名称
    private String gameName;
    // 图像文件
    private String cdn;
    // 投注前余额
    private BigDecimal before;
    // 投注值
    private BigDecimal bet;
    // 赢值
    private BigDecimal win;
    // 用户名
    private String login;
    // 投注日志
    private String date;
    // sessionId
    private String sessionId;
    // tradeId
    private String tradeId;
    // 矩阵信息
    private Object matrix;
}
