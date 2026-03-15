package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "审核详情 返回")
@I18nClass
public class UserReviewDetailsVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "登录密码")
    private String password;

    @Schema(title = "上级代理id")
    private String superAgentId;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(description = "注册端")
    private String registerTerminal;

    @Schema(description = "注册端名称")
    private String registerTerminalName;

    @Schema(title = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE )
    private Integer accountType;

    @Schema(title = "账号类型 1测试 2正式")
    private String accountTypeText;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "会员账邮箱")
    private String email;

    @Schema(title= "手机号码")
    private String areaCode;

    @Schema(title= "手机号码")
    private String phone;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatusName;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip当前等级")
    private String vipGradeCodeName;

    @Schema(description = "vip段位")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "vip段位等级")
    private String vipRankCodeName;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "会员标签名称")
    private String userLabelText;

    /*@Schema(title = "注册信息")
    private String registerInfo;*/

    @Schema(title = "备注信息")
    private String remark;

    @Schema(title = "申请人")
    private String applicant;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请信息")
    private String applyInfo;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(title = "一审人")
    private String reviewer;

    @Schema(title = "一审备注")
    private String reviewRemark;

}
