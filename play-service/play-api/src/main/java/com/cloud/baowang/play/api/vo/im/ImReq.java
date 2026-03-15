package com.cloud.baowang.play.api.vo.im;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImReq implements Serializable {

    // 命令
    private String cmd;
    // 大厅ID
    private String hall;
    // 大厅key
    private String key;
    // 用户登录
    private String login;
    // 游戏会话的唯一 ID
    private String sessionId;
    // 旋转投注
    private BigDecimal bet;
    // 旋转结果
    private BigDecimal win;
    // 是 writeBet 命令当前请求的唯一 ID
    private String tradeId;
    // 游戏动作
    private String action;
    // 投注信息
    private String betInfo;
    // 游戏id
    private String gameId;
    // 自旋矩阵
    private String matrix;
    // 日期和时间
    private String date;
    // 赢线
    private String winLines;

}
