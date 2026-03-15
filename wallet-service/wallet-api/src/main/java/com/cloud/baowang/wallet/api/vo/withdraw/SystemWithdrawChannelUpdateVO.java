package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 11:38
 * @Version: V1.0
 **/
@Data
@Schema(description = "提现渠道")
public class SystemWithdrawChannelUpdateVO extends SystemWithdrawChannelAddVO{

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;


}
