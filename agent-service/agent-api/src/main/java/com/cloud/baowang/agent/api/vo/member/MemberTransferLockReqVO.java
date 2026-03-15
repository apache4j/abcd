package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "会员转代详情锁单解单请求")
public class MemberTransferLockReqVO {
    @Schema(title = "id")
    @NotNull
    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(title = "操作状态;0未锁 1已锁 ")
    private Integer lockStatus;


}
