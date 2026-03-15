package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 15/11/23 5:19 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "短连接返回VO")
public class AgentDomainShortVO implements Serializable {

    @Schema(description="域名")
    private String domainName;
    @Schema(description="域名地址")
    private String domainAddr;
    @Schema(description="域名类型: 1=PC、2=H5、3=APP、4=专属")
    private Integer domainType;
    @Schema(description="合营代码")
    private String inviteCode;

    private String siteCode;
}
