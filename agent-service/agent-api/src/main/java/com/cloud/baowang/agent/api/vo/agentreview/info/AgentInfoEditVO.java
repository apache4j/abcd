package com.cloud.baowang.agent.api.vo.agentreview.info;

import com.cloud.baowang.agent.api.enums.AgentInfoChangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@Schema(description = "代理信息编辑 入参")
public class AgentInfoEditVO {


    @Schema(description = "操作人", hidden = true)
    private String operator;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "代理账号")
    @NotBlank(message = "代理账号不能为空")
    private String agentAccount;

    /**
     * {@link AgentInfoChangeTypeEnum}
     */
    @Schema(description = "变更类型-system_param agent_change_type code值")
    @NotNull(message = "变更类型不能为空")
    private Integer changeType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "账号状态,多选 system_param account_status code值")
    private String accountStatus;

    @Schema(description = "风控层级")
    private String riskLevel;


    @Schema(description = "代理标签")
    private String agentLabel;

    @Schema(description = "代理归属 同system_param agent_attribution code值")
    private Integer agentAttribution;

    @Schema(description = "佣金方案codes")
    private String planCode;

    @Schema(description = "入口权限开启 1是 0否")
    private Integer entrancePerm;


    @Schema(description = "会员福利类型codes，逗号拼接的字符串")
    private String userBenefit;

    @Schema(description = "账号备注")
    private String accountRemark;

    @Schema(description = "FaceBook PixId")
    private String fbPixId;
    @Schema(description = "FaceBook Token")
    private String fbToken;
    @Schema(description = "Google Ads PixId")
    private String googlePixId;
    @Schema(description = "Google Ads Token")
    private String googleToken;

    public String getFbPixId() {
        return StringUtils.hasText(this.fbPixId)?this.fbPixId:"";
    }

    public String getFbToken() {
        return StringUtils.hasText(this.fbToken)?this.fbToken:"";
    }

    public String getGooglePixId() {
        return StringUtils.hasText(this.googlePixId)?this.googlePixId:"";
    }

    public String getGoogleToken() {
        return StringUtils.hasText(this.googleToken)?this.googleToken:"";
    }

}
