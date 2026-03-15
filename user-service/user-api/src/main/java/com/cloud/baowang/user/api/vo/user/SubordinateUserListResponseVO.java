package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "下级会员列表 ResponseVO")
public class SubordinateUserListResponseVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(title = "vip当前等级 - Name")
    private String vipGradeCodeName;

    @Schema(title = "姓名")
    private String userName;

    @Schema(title = "是否实名")
    private String isRealName;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "存款")
    private BigDecimal allDepositAmount;

    @Schema(title = "取款")
    private BigDecimal allWithdrawAmount;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "总输赢")
    private BigDecimal betWinLose;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "分配次数")
    private Long transAgentTime;

    @Schema(title = "会员标签")
    private List<String> userLabels;

    @Schema(title = "会员备注")
    private String acountRemark;
}
