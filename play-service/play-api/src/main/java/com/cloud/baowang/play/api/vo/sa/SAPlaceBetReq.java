package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAPlaceBetReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 货币代码，例如: USD/EUR 或 mXBT
     */
    private String currency;

    /**
     * 投注金额，最多两位小数
     */
    private BigDecimal amount;

    /**
     * 点数交易编号
     */
    private String txnid;

    /**
     * 时间戳，格式为 yyyy-MM-dd HH:mm:ss.SSS（GMT+8）
     */
    private LocalDateTime timestamp;

    /**
     * 用户 IP
     */
    private String ip;

    /**
     * 游戏类型
     */
    private String gametype;

    /**
     * 平台: 0 - 桌面版，1 - 手机版
     */
    private Byte platform;

    /**
     * 桌台编号
     */
    private Integer hostid;

    /**
     * 游戏局号
     */
    private String gameid;

    /**
     * 投注详细信息，JSON 字符串
     * 示例结构：[{ "类型": "A", "金额": 100.00 }, ...]
     */
    private List<SAPlaceBetDetailsReq> betdetails;

}
