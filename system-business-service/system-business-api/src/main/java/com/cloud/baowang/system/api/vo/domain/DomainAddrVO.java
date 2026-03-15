package com.cloud.baowang.system.api.vo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainAddrVO {
    private String shortUrl;
    private String domainAddr;
    private String siteCode;
}