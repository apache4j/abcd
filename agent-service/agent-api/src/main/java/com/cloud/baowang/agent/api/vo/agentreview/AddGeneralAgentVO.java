package com.cloud.baowang.agent.api.vo.agentreview;

import com.cloud.baowang.agent.api.vo.BaseReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "新增总代 Request")
public class AddGeneralAgentVO extends BaseReqVO {

    @Schema(description = "代理线层级上限",hidden = true)
    //@NotNull(message = "代理线层级上限不能为空")
    private Integer maxLevel;

    @Schema(description = "代理归属 1推广 2招商 3官资")
   // @NotNull(message = "代理归属不能为空")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    @NotNull(message = "代理类别不能为空")
    private Integer agentCategory;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    @NotNull(message = "代理类型不能为空")
    private Integer agentType;


    @Schema(description = "代理账号")
    @NotEmpty(message = "代理账号不能为空")
    private String agentAccount;

    @Schema(description = "登录密码")
    @NotEmpty(message = "登录密码不能为空")
    private String agentPassword;

    @Schema(description = "IP白名单(只有流量代理需要)，使用英文逗号隔开")
    private String whiteList;

    @Schema(description = "备注信息")
    private String remark;

    @Schema(description = "分配直属上级")
    private String upAgentAccount;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;

    /**
     * 所属商务账号
     */
    @Schema(description = "所属商务账号")
    @NotEmpty(message = "所属商务账号不能为空")
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    @Schema(description = "所属商务名称")
    @NotEmpty(message = "所属商务名称不能为空")
    private String merchantName;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;
}

