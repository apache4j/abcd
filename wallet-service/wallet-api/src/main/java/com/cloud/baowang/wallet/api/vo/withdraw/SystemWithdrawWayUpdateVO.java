package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: qiqi
 **/
@Data
@Schema(description = "提现方式")
public class SystemWithdrawWayUpdateVO extends SystemWithdrawWayAddVO{

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private String id;

}
