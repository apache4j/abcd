package com.cloud.baowang.system.api.vo.adminLogin;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * 后台管理系统登录请求对象
 *
 * @author qiqi
 */
@Data
@Schema(description = "管理系统登录请求对象")
public class AdminLoginParamVO {

    @Schema(description =  "用戶账号")
    @NotEmpty(message = ConstantsCode.ACCOUNT_NOT_NULL)
    private String userName;

    @Schema(description =  "用戶密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(description =  "校验码")
    private String verifyCode;

    @Schema(description = "站点编号")
    private String siteCode;

}
