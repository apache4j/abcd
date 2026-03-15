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
public class SAPlayerCancelReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 货币代码，例如 USD、EUR 或 mXBT，最大长度 16
     */
    private String currency;

    /**
     * 需要返回给会员的赢额，最多保留2位小数
     */
    private BigDecimal amount;

    /**
     * 点数交易编号（唯一），最大长度 16
     */
    private String txnid;

    /**
     * 时间戳，格式 yyyy-MM-dd HH:mm:ss.SSS，时区 GMT+8
     */
    private LocalDateTime timestamp;

    /**
     * 游戏类型
     */
    private String gametype;

    /**
     * 桌台编号（hostid）
     */
    private Integer hostid;

    /**
     * 游戏局号（gameid）
     */
    private String gameid;

    /**
     * 撤单对应的下注单号（即原 PlaceBet 的 txnid）
     */
    private String txn_reverse_id;

    /**
     * 重试标识：0=首次发送，1=重试发送
     */
    private Integer retry;

    /**
     * 取消原因简述：
     * 1 - 因荷官操作问题需要取消；
     * 0 - 其他原因 (例如：下注回覆超时或返回错误码)
     */
    private Integer gamecancel;

}
