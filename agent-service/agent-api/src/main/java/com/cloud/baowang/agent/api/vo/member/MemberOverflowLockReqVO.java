package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "会员溢出详情锁单解单请求")
public class MemberOverflowLockReqVO {

    @Schema(description = "id")
    @NotNull
    private String id;
    /**
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     */
    @Schema(description = "操作状态;1-锁单;2-解单")
    private Integer lockStatus;

}
