package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : leiding
 * @Date : 11/12/24 13:38 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DomainInfoAddr返回VO")
public class DomainInfoAddrVO implements Serializable {
    @Schema(description="域名地址")
    private String domainAddr;
    @Schema(description="合营代码")
    private String inviteCode;
}
