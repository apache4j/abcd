package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "调线申请客户端查询请求参数")
public class MemberOverflowClientPageReqVO extends PageVO {
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核状态（0-待处理 1-处理中，2-审核通过，3-审核拒绝）字典code:agent_overflow_audit_status")
    private String auditStatus;

    @Schema(description = "申请人", hidden = true)
    private String applyName;

    @Schema(description = "会员账号")
    private String memberName;

}
