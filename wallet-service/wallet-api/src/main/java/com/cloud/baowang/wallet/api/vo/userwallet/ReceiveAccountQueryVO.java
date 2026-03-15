package com.cloud.baowang.wallet.api.vo.userwallet;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "收款账号查询VO")
public class ReceiveAccountQueryVO {

    @Schema(description = "收款账号/收款地址")
    private String receiveAccount;
}
