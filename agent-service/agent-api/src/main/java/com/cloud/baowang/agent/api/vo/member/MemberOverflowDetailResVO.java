package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.agent.api.vo.manualup.ReviewInfoVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "会员溢出详情响应")
@I18nClass
public class MemberOverflowDetailResVO {

    @Schema(description = "本次申请信息")
    private AgentOverflowApplyInfo applyInfo;

    @Schema(description = "转代会员信息")
    private UserTransferAgentUserDetail userDetail;

    @Schema(title = "当前代理信息")
    private MemberTransferModifyInfoVO beforeFixing;

    @Schema(title = "转入后代理信息")
    private MemberTransferModifyInfoVO afterModification;

    @Schema(description = "本次审核详情")
    private List<ReviewInfoVO> reviewInfoVOS;

}
