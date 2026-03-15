package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: qiqi
 **/
@Data
@Schema(description = "充值类型状态操作")
public class SystemWithdrawTypeStatusVO {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private String id;

    @Schema(description = "操作人 ")
    private String operatorUserNo;

}
