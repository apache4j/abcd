package com.cloud.baowang.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/09/16 13:03
 * @description:
 */
@Data
public class VerifyReq {
    @Schema(description = "三方回传的参数")
    private String captchaVerifyParam;
}
