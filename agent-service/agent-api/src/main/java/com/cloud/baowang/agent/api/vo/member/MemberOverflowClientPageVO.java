package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员溢出客户端列表分页响应对象")
public class MemberOverflowClientPageVO extends PageVO {

    @Schema(description = "会员账号")
    private String memberName;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "审核状态（0-待处理 1-处理中，2-审核通过，3-审核拒绝）")
    private Integer auditStatus;

    @Schema(description = "审核状态名称")
    private String auditStatusName;

    @Schema(description = "审核完成时间")
    private Long auditDatetime;

    @Schema(description = "备注")
    private String auditRemark;

}
