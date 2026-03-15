package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : leiding
 * @Date : 11/12/24 13:38 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DomainInfoAddrList返回VO")
public class DomainInfoAddrListVO implements Serializable {
//    @Schema(description="域名地址")
//    private String domainAddr;
    @Schema(description="域名地址List")
    private List<DomainInfoAddrVO> domainAddrList;
}
