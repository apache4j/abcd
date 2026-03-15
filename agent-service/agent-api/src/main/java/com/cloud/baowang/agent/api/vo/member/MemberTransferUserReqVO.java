package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "会员转代申请用户查询请求")
public class MemberTransferUserReqVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "会员ID")
    private String userAccount;

    @Schema(title = "转代会员注册信息")
    private String userRegister;


}
