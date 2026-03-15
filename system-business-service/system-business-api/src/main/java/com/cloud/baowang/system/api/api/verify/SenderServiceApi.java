package com.cloud.baowang.system.api.api.verify;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: fangfei
 * @createTime: 2024/10/15 19:10
 * @description:
 */
@FeignClient(contextId = "remoteSenderServiceApi", value = ApiConstants.NAME)
@Tag(name = "RPC 验证码发送服务 - SenderServiceApi")
public interface SenderServiceApi {
    String PREFIX = ApiConstants.PREFIX + "/sender/api/";

    @PostMapping(PREFIX +"sendMail")
    @Operation(summary ="发送邮件验证码")
    ResponseVO sendMail(@RequestBody VerifyCodeSendVO verifyCodeSendVO);

    @PostMapping(PREFIX +"sendSms")
    @Operation(summary ="发送短信验证码")
    ResponseVO sendSms(@RequestBody VerifyCodeSendVO verifyCodeSendVO);
}
