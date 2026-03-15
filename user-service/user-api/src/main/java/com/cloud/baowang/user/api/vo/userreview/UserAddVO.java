package com.cloud.baowang.user.api.vo.userreview;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author: kimi
 */
@Data
@Schema(description = "新增会员 Request")
public class UserAddVO {

    @Schema(description = "账号类型")
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    //@NotNull(message = "账号类型不能为空")
    private Integer accountType;

    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "会员标签")
    private String userLabelId;

    /*@Schema(description = "注册信息 1手机号码 2电子邮箱")
    @NotNull(message = "注册信息不能为空")
    private Integer registerType;*/

    @Schema(description = "会员账号")
    //@NotEmpty(message = "会员账号不能为空")
    @NotEmpty(message = ConstantsCode.MISSING_PARAMETERS)
    @Length(min = 4, max = 11, message = "会员账号在4-11个字符")
    private String userAccount;

    @Schema(description = "登录密码")
    @NotEmpty(message = "登录密码不能为空")
    @Length(min = 8, max = 16, message = "登录密码介于8-16个字符!")
    private String password;

    /*@Schema(description = "确认密码", required = true)
    @NotEmpty(message = "确认密码不能为空")
    @Length(min = 8, max = 16, message = "确认密码介于8-16个字符!")
    private String confirmPassword;*/

    @Schema(description = "主货币")
    @NotEmpty(message = ConstantsCode.MISSING_PARAMETERS)
    private String mainCurrency;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "vip等级")
    private Integer vipGrade;
    //MAX_LENGTH
    @Schema(description = "提交审核备注")
    //@Length(max = 100, message = "提交审核备注介于0-100个字符!")
    @Length(max = 100, message = ConstantsCode.MAX_LENGTH)
    private String applyInfo;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
