package com.cloud.baowang.play.api.vo.ldg;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestVO {

    // 方法
    private String method;
    // 语言
    private String language;
    // 平台编码
    private String business;
    // 认证令牌
    private String token;
    // 游戏编码
    private String gameCode;

    // 游戏注单相关
    // 游戏投注 ID
    private String betId;

    // 玩家账号
    private String playerName;
    // 当本局游戏为免费游戏时，则记录触发免费游戏局号
    private Object parentId;
    // 免费游戏状态 0-已完结 1-未完结
    private String isComplete;
    // 投注线
    private String betLine;
    // 中奖额
    private BigDecimal betWins;
    // 投注额
    private BigDecimal betPrice;
    // 游戏记录创建时间
    private String createTime;
    // 记录传输时间戳
    private Long timestamp;
//    // 商户自定义标记
//    private String remark;
    // 来源设备
    private String clientType;
    // 货币类型
    private String currency;
    // 奖励类型
    private String rewardType;
    // 免费优惠活奖励记录 id
    private String fcid;
    // 签名
    private String sign;
    // 改变额度
    private BigDecimal creditRemit;

    // ID 主键
    private String id;

    // 货币类型
    private String currencyCode;
    // 异常类型
    private String status;






}
