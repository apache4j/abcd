package com.cloud.baowang.play.api.vo.dbDj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbDJTransferRes {

    /**
     * 成功:true, 失败:false
     */
    private String status;

    /**
     * 状态码
     * 成功: 0
     * 失败: 参考附录 (如 600: 余额不足)
     */
    private Integer code;

    /**
     * 信息
     * 成功: 成功信息
     * 失败: 错误信息
     */
    private String message;

    /**
     * 加扣款后余额
     */
    private BigDecimal account_balance;

    /**
     * 是否需要重试
     * 1: 需要重试
     * 0: 不重试
     */
    private Integer retry;


}
