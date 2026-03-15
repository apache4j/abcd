package com.cloud.baowang.user.api.vo.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "外层会员登录日志返水对象")
@I18nClass
public class UserLoginLogVO implements Serializable {

    @Schema(description = "会员登录日志分页")
    private Page<UserLoginInfoVO> userLoginPage;

    @Schema(description = "总登录次数")
    private Long totalLoginNum;

    @Schema(description = "登录成功")
    private Long loginSuccess;

    @Schema(description = "登录失败")
    private Long loginError;
}
