package com.cloud.baowang.user.api.vo.UserDetails;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员详情接参对象")
public class UserDetailsReqVO {
    @Schema(title = "数据标识ID 必传")
    private Long id;
    /**
     * 1:账号状态,
     * 2:风控层级,
     * 3:会员标签,
     * 9:账号备注,
     * 4:出生日期,
     * 5:手机号码,
     * 6:姓名,
     * 7:性别,
     * 8:邮箱,
     * 10:VIP等级,
     * 11:增加流水
     * {@link UserChangeTypeEnum}
     */
    @Schema(title = "变更类型 必传(1:账号状态,2:风控层级,3:会员标签,4:出生日期,5:手机号码,6:姓名,7:性别,8:邮箱,9:账号备注,10:VIP等级 11:增加流水)")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String changeType;
    @Schema(title = "账号状态 多选时用逗号拼接传参")
    private String accountStatus;
    @Schema(title = "风控层级")
    private String riskLevel;
    @Schema(title = "会员标签")
    private List<String> memberLabel;
    @Schema(title = "会员账号")
    private String userAccount;

    /*@Schema(description ="会员注册信息")
    private String userRegister;*/

    @Schema(title = "出生日期")
    private String dateOfBirth;
    @Schema(title = "VIP等级")
    private String vipRank;

    @Schema(title = "手机区号")
    private String areaCode;
    @Schema(title = "手机号码")
    private String phoneNumber;
    @Schema(title = "姓名")
    private String name;
    @Schema(title = "性别")
    private String gender;
    @Schema(title = "邮箱")
    private String mail;
    @Schema(title = "备注")
    private String remark;
    @Schema(title = "账号备注")
    private String accountRemark;

    @Schema(title = "增加流水金额")
    private BigDecimal typingAmount;

    private String userReceiveAccount;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;
    @Schema(title = "站点模式", hidden = true)
    private  Integer handicapMode;

    /**
     * 扩展参数
     */
    @Schema(title = "扩展参数", hidden = true)
    private String extParam;

}
