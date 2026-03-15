package com.cloud.baowang.play.api.vo.dbPanDaSport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryTransferResVO {

    /**
     * 交易类型
     * 1：加款
     * 2：扣款
     */
    private Integer transferType;

    /**
     * 交易时间（13位时间戳）
     */
    private Long createTime;

    /**
     * 交易ID
     */
    private String transferId;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 商户代码
     */
    private String merchantCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易前余额
     */
    private BigDecimal beforeTransfer;

    /**
     * 交易后余额
     */
    private BigDecimal afterTransfer;

    /**
     * 交易状态
     * 0：失败
     * 1：成功
     */
    private String status;

    /**
     * 交易模式
     * 1：免转
     * 2：转账
     */
    private Integer transferMode;

    /**
     * 交易涉及订单（字符串形式）
     */
    private String orderStr;

    /**
     * 页码（起始为1）
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总条数
     */
    private Integer totalCount;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 成功或失败原因
     */
    private String mag;

    /**
     * 业务类型（具体可见参数映射字段）
     */
    private Integer bizType;

}
