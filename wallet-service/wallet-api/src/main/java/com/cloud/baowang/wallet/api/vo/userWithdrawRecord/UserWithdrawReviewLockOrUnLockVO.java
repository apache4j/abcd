package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款审锁单或解锁请求对象")
public class UserWithdrawReviewLockOrUnLockVO {


    @Schema(description = "id")
    @NotEmpty(message = "ID不能为空")
    private String id;
    @Schema(description = "operator", hidden = true)
    private String operator;

}
