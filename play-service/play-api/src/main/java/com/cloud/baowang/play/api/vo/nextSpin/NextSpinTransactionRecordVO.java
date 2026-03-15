package com.cloud.baowang.play.api.vo.nextSpin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextSpinTransactionRecordVO implements Serializable {
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
