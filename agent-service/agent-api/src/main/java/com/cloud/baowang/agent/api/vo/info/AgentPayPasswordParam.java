package com.cloud.baowang.agent.api.vo.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 21/10/23 10:48 AM
 * @Version : 1.0
 */
@Data
@Schema(name = "AgentPayPasswordParam", description = "代理支付密码对象")
public class AgentPayPasswordParam implements Serializable {

    @Schema(description ="支付密码")
    private String payPassword;

    private String AgentAccount;
}
