package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(title = "代理提款审锁单或解锁请求对象")
public class AgentWithdrawReviewLockOrUnLockVO {


    @Schema(description = "id")
    @NotEmpty(message = "ID不能为空")
    private String id;
    @Schema(description = "operator",hidden = true)
    private String operator;


}
