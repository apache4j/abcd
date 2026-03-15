package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "新增调线申请")
public class AgentUserOverflowClientApplyVO {
    @Schema(description = "站点code")
    private String siteCode;

    @NotBlank
    @Schema(description = "会员账号")
    private String memberName;

    @Schema(description = "转入代理", hidden = true)
    private String transferAgentName;

    @NotNull
    @Schema(description = "上传图片")
    private List<String> image;

    @Schema(description = "溢出链接")
    private String link;

    @Schema(description = "申请理由")
    private String applyRemark;

    @Schema(description = "验证码")
//    @NotBlank
    private String verifyCode;

    @Schema(description = "验证码KEY")
//    @NotBlank
    private String codeKey;

    @Schema(description = "发起来源",hidden = true)
    private Integer applySource;
}
