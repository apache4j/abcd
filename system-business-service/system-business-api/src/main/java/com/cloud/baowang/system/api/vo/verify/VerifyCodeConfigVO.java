package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/05/08 23:39
 * @description: 短信邮箱验证码配置
 */
@Data
@Schema(title ="发送请求VO")
public class VerifyCodeConfigVO {
    @Schema(title = "类型 0 邮箱  1 短信")
    private Integer type;
    @Schema(title = "供应商名称")
    private String platformName;
    @Schema(title = "供应商code")
    private String platformCode;
    @Schema(title = "地址", hidden = true)
    private String host;
    @Schema(title = "端口", hidden = true)
    private Integer port;
    @Schema(title = "用户id", hidden = true)
    private String userId;
    @Schema(title = "用户账号", hidden = true)
    private String userAccount;
    @Schema(title = "密码", hidden = true)
    private String password;
    @Schema(title = "发送者  部分邮箱平台需要", hidden = true)
    private String sender;
    @Schema(title = "密钥", hidden = true)
    private String apiKey;
    @Schema(title = "发送文本模板", hidden = true)
    private String template;
    @Schema(title = "接收者", hidden = true)
    private String receiver;
    @Schema(title = "验证码", hidden = true)
    private String verifyCode;
}
