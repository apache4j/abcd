package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "用户人工存款审锁单或解锁请求对象")
public class UserManualDepositLockOrUnLockVO {


    @Schema(description = "id")
    @NotEmpty(message = "ID不能为空")
    private String id;
    @Schema(description = "operator",hidden = true)
    private String operator;


}
