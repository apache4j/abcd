package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "查看AES秘钥")
public class CheckAesSecretKeyVO {

    @Schema(title = "AES密钥")
    private String aesSecretKey;
}
