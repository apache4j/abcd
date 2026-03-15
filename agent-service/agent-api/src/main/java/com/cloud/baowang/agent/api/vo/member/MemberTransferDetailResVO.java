package com.cloud.baowang.agent.api.vo.member;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员转代详情响应")
@I18nClass
public class MemberTransferDetailResVO {

    @Schema(description = "本次申请信息")
    private UserTransferAgentApplyInfo applyInfo;
    @Schema(description = "转代会员信息")
    private UserTransferAgentUserDetail userDetail;

    @Schema(title = "当前代理信息")
    private MemberTransferModifyInfoVO beforeFixing;

    @Schema(title = "转入后代理信息")
    private MemberTransferModifyInfoVO afterModification;

}
