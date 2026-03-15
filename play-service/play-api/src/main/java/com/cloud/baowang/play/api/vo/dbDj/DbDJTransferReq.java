package com.cloud.baowang.play.api.vo.dbDj;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class DbDJTransferReq {

    /**
     * 用户名（2-32位）
     */
    private String username;

    /**
     * 转账类型
     * 1: 投注扣款
     * 2: 注单结算加款
     * 3: 注单回滚扣款
     * 8: 其他扣款
     * 9: 其他加款
     */
    private Integer type;

    /**
     * 转账金额 (>=0.01)
     */
    private BigDecimal amount;

    /**
     * 转账订单号（12~32位）
     */
    private String merOrderId;

    /**
     * Unix时间戳（毫秒）
     * 同一个上下分请求出现错误，发起重试，该时间戳不变
     */
    private Long time;

    /**
     * 密钥（MD5加密计算）
     */
    private String sign;


    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(username, type, amount, merOrderId, time, sign);
    }
}
