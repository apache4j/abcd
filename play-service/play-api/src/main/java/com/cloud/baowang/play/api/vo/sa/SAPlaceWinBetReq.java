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
public class SAPlaceWinBetReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 货币代码，例如 USD、EUR 或 mXBT，最大长度 16
     */
    private String currency;

    /**
     * 返回给会员的赢额，最多保留2位小数
     */
    private BigDecimal amount;

    /**
     * 有效投注额/洗码量，最多保留2位小数
     */
    private BigDecimal rolling;

    /**
     * 唯一的点数交易编号，最大长度 16
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
     * 派彩时间，格式 yyyy-MM-dd HH:mm:ss.SSS，时区 GMT+8
     */
    private LocalDateTime payouttime;

    /**
     * 桌台编号（hostid）
     */
    private Integer hostid;

    /**
     * 游戏局号
     */
    private String gameid;

    /**
     * 重试标识，首次发送=0，重试=1
     */
    private Integer retry;

    /**
     * 投注详情（详细投注列表，可参阅 PlayerWin 结构）
     */
    private String payoutdetails;

}
