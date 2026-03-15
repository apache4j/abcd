package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "校验用户状态VO")
public class CheckUserStatusVO {

    @Schema(description = "类型 1存款 2取款")
    private String type;
}
