package com.cloud.baowang.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/09/16 12:14
 * @description:
 */
@Data
@Builder
public class VerifyRsp {
    @Schema(description = "验证结果， true 成功 false 失败")
    private Boolean captchaResult;
}
