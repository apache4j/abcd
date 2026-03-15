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
public class SABalanceAdjustmentReq {

    /**
     * 用户名
     * 最大长度: 48
     */
    private String username;

    /**
     * 货币代码（标准 ISO 3 字元，例如 USD）
     * 最大长度: 16
     */
    private String currency;

    /**
     * 需要调整的金额（最多 2 位小数）
     */
    private BigDecimal amount;

    /**
     * 独一的点数交易编号
     * 最大长度: 16
     */
    private String txnid;

    /**
     * 时间标记 (yyyy-MM-dd HH:mm:ss.SSS, 时区 GMT+8)
     * 示例: 2019-10-22 13:34:45.456
     */
    private LocalDateTime timestamp;

    /**
     * 调整类型
     * 2 = 赠送奖赏
     * 3 = 取消奖赏
     */
    private Integer adjustmenttype;

    /**
     * 结算时间 (yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime adjustmenttime;

    /**
     * 用户的 IP (仅适用于 adjustmenttype = 2)
     */
    private String ip;

    /**
     * 游戏类型 (仅适用于 adjustmenttype = 2)
     */
    private String gametype;

    /**
     * 平台 (仅适用于 adjustmenttype = 2)
     * 0 - 桌面版
     * 1 - 手机版
     */
    private Byte platform;

    /**
     * 桌台编号 (仅适用于 adjustmenttype = 2)
     * 详细定义请参阅文档第10节
     */
    private Integer hostid;

    /**
     * 重试次数 (仅适用于 adjustmenttype = 3)
     * 第一次发送：重试 = 0
     * 第二次发送：重试 = 1
     */
    private Integer retry;

    /**
     * 调整详情 (JSON 格式)
     * <p>
     * 如果 adjustmentType = 2: gifttype – 奖赏类型<br>
     * 如果 adjustmentType = 3: canceltxnid – 待取消的奖赏交易ID
     */
    private String adjustmentdetails;

}
