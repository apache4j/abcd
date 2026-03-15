package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理基本信息
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Data
@Schema(description = "代理基本信息-部分信息")
public class AgentInfoPartVO {

    @Schema(description = "代理id")
    private String id;

    @Schema(description = "代理id-短")
    private String agentId;



    @Schema(description = "父节点")
    private String parentId;

    @Schema(description = "上级代理账号")
    private String parentAccount;

    @Schema(description = "层次id 逗号分隔")
    private String path;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理线层级上限")
    private Integer maxLevel;

    @Schema(description = "代理账号")
    private String agentAccount;


    @Schema(description = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;



    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;


    @Schema(description = "契约状态 1已签约 0未签约")
    private Integer contractStatus;



    @Schema(description = "离线天数")
    private Integer offlineDays;

    @Schema(description = "合营代码")
    private String inviteCode;





    @Schema(description = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;

    // ----------------------------------------------------------------



    @Schema(description = "商务账号")
    private String merchantAccount;


}
