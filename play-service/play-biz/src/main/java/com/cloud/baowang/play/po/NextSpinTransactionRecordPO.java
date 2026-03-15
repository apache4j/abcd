package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "nextspin_transaction_record")
public class NextSpinTransactionRecordPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 账户
     */
    private String account;

    /**
     * 收支类型1收入,2支出
     */
    private String transferId;

    /**
     * 收支类型1收入,2支出
     */
    private String requestJson;


    /**
     * 返回交易号
     */
    private String returnNumber;
}