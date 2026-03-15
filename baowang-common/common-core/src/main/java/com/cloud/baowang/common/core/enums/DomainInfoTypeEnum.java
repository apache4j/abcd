package com.cloud.baowang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 域名管理-域名类型下拉
 * system_param site_domain_type
 */
@AllArgsConstructor
@Getter
public enum DomainInfoTypeEnum {
    GATEWAY_URL(0, "网关"),
    AGENT_BACKEND(1, "代理后台"),
    WEB_PORTAL(2, "网页端"),
    SITE_BACKEND(3, "站点后台"),
    BACKEND(4, "后端"),
    DOWNLOAD_PAGE(5, "下载页"),
    PC_DOWNLOAD_ADDRESS(6, "PC扫码地址"),
    AGENT_MERCHANT(7, "商务端"),
    MAINTENANCE_PAGE(8, "维护页"),
    H5_PAGE(9, "H5域名")
    ;
    private final Integer type;
    private final String name;


}