package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAPlayerLostReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 货币代码 (例如 USD/EUR/mXBT)，长度3
     */
    private String currency;

    /**
     * 有效投注额/洗码量
     */
    private BigDecimal rolling;

    /**
     * 点数交易编号，唯一，最长16字符
     */
    private String txnid;

    /**
     * 交易时间戳，格式：yyyy-MM-dd HH:mm:ss.SSS（时区 GMT+8）
     */
    private LocalDateTime timestamp;

    /**
     * 游戏类型（可参阅定义）
     */
    private String gametype;

    /**
     * 派彩时间
     */
    private LocalDateTime payouttime;

    /**
     * 桌台编号（hostid）
     */
    private Integer hostid;

    /**
     * 游戏局号（唯一）
     */
    private String gameid;

    /**
     * 重试次数（首次=0，重试=1）
     */
    private Integer retry;

    /**
     * 投注详情（PlayerWin）
     */
    private Integer payoutdetails;

}
