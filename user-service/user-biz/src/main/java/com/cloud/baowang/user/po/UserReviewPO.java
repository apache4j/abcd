package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员审核表
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@TableName("user_review")
@Schema(title = "会员审核表")
public class UserReviewPO extends BasePO {

    @Schema(title= "会员注册信息")
    private String userAccount;

    @Schema(title= "密码")
    private String password;

    @Schema(description = "加密盐")
    private String salt;

    /**
     * 账号类型
     * {@link }
     */
    @Schema(title= "账号类型 1测试 2正式 ")
    private Integer accountType;

    @Schema(title= "主货币")
    private String mainCurrency;

    @Schema(title= "手机号码")
    private String areaCode;

    @Schema(title= "手机号码")
    private String phone;

    @Schema(title= "邮箱")
    private String email;

    @Schema(title= "上级代理id")
    private String superAgentId;

    @Schema(title= "上级代理账号")
    private String superAgentAccount;

    @Schema(title= "vip等级")
    private Integer vipGrade;

    @Schema(title= "vip段位")
    private Integer vipRankCode;


    @Schema(title= "审核单号")
    private String reviewOrderNo;

    @Schema(title= "申请信息")
    private String applyInfo;

    @Schema(title= "申请时间")
    private Long applyTime;

    @Schema(title= "申请人")
    private String applicant;

    /**
     * 此处userId 为 userAccount 而非用户表主键id
     */
    @Schema(title= "用户id")
    private String userId;

    @Schema(title= "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(title= "一审人")
    private String reviewer;

    @Schema(title= "审核操作 1一审审核 2结单查看")
    private Integer reviewOperation;

    @Schema(title= "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private Integer reviewStatus;

    @Schema(title= "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title= "锁单人")
    private String locker;

    @Schema(title= "一审备注")
    private String reviewRemark;

    @Schema(description = "站点code")
    private String siteCode;
}
