package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "调线申请客户端列表响应对象")
@I18nClass
public class MemberOverflowClientPageResVO {

    @Schema(description = "会员账号")
    private String memberName;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "审核状态（0-待处理 1-处理中，2-审核通过，3-审核拒绝）")
    private Integer auditStatus;

    @Schema(description = "审核状态名称")
    @I18nField
    private String auditStatusName;

    @Schema(description = "审核完成时间")
    private Long auditDatetime;

    @Schema(description = "备注")
    private String auditRemark;

    @Schema(description = "申请备注")
    private String applyRemark;

    @Schema(description = "上传图片")
    private List<String> images;

    @Schema(description = "上传图片key", hidden = true)
    private String image;

    @Schema(description = "推广链接")
    private String link;
}
