package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 代理审核表
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@TableName("agent_review")
@Schema(description = "代理审核表")
public class AgentReviewPO extends SiteBasePO {

    @Schema(description = "父节点")
    private String parentId;

    @Schema(description = "上级代理账号")
    private String parentAccount;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理线层级上限")
    private Integer maxLevel;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "登录密码")
    private String agentPassword;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;

    @Schema(description = "IP白名单(只有流量代理需要)，使用英文逗号隔开")
    private String agentWhiteList;

    @Schema(description = "代理契约模式-佣金契约 1是 0否")
    private Integer contractModelCommission;

    @Schema(description = "代理契约模式-返点契约 1是 0否")
    private Integer contractModelRebate;

    @Schema(description = "审核单号")
    private String reviewOrderNo;

    @Schema(description = "申请信息")
    private String applyInfo;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(description = "一审人")
    private String reviewer;

    @Schema(description = "审核操作 1一审审核 2结单查看")
    private Integer reviewOperation;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private Integer reviewStatus;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "锁单人")
    private String locker;

    @Schema(description = "一审备注")
    private String reviewRemark;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;

    /**
     * 所属商务账号
     */
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    private String merchantName;
}
