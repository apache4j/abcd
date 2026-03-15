package com.cloud.baowang.agent.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(title = "会员溢出申请")
public class AgentUserOverflowApplyVO implements Serializable {

    @Schema(description = "站点编码")
    private String siteCode;

    @NotBlank
    @Schema(description = "溢出会员账号")
    private String memberName;

    @Schema(title = "溢出会员注册信息")
    private String userRegister;

    @NotBlank
    @Schema(description = "转入代理")
    private String transferAgentName;
    /**
     * 推广设备 1APP 2PC 3H5
     * {@link com.cloud.baowang.agent.api.enums.AgentOverFlowDeviceTypeEnum}
     */
    @NotNull
    @Schema(description = "推广设备system_param device_terminal")
    private Integer device;

    @Schema(description = "推广链接")
    private String link;

    @NotNull
    @Schema(description = "上传图片")
    private List<String> image;

    @NotBlank
    @Schema(description = "申请备注信息")
    private String applyRemark;

    @Schema(description = "来源", hidden = true)
    private Integer applySource;


}
