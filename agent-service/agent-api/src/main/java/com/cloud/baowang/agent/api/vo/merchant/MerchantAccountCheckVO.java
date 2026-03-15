package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 21:36
 * @description: 代理登录请求对象
 * */
@Data
@Schema(title = "商务账号验证VO")
public class MerchantAccountCheckVO {

    @NotEmpty(message = ConstantsCode.AGENT_MISSING)
    @Schema(title = "商务账号")
    private String merchantAccount;

    @Schema(title = "验证码")
    @NotEmpty(message = ConstantsCode.CODE_IS_EMPTY)
    private String code;

    @Schema(title = "验证码KEY")
    private String codeKey;

}
