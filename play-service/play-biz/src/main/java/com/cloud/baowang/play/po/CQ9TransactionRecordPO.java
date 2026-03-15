package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "cq9_transaction_record")
public class CQ9TransactionRecordPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 交易动作
     */
    private String action;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 交易开始时间
     */
    private String createTime;

    /**
     * 交易结束时间
     */
    private String endTime;

    /**
     * 交易状态 (success: 成功, refund: 退款, 失败返回1014)
     */
    private String status;

    /**
     * 交易前余额
     */
    private BigDecimal before;

    /**
     * 交易后余额
     */
    private BigDecimal balance;

    /**
     * 币别
     */
    private String currency;

    /**
     * 状态编码
     */
    private String statusCode;

    /**
     * 状态消息
     */
    private String statusMessage;

    /**
     * 返回时间
     */
    private String responseTime;

    /**
     * 交易代码，唯一值
     */
    private String mtcode;

    /**
     * 该笔交易的金额
     */
    private BigDecimal amount;

    /**
     * 事件发送时间，CQ9 发送交易时间
     */
    private String eventTime;

    /**
     * 请求入参
     */
    private String requestJson;
    /**
     * 注單號
     */
    private String roundId;

    /**
     * 收支类型1收入,2支出
     */
    private String balanceType;

    /**
     * 一组派彩
     */
    private String roundArray;



}