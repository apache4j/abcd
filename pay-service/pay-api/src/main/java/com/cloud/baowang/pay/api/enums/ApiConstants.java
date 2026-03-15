package com.cloud.baowang.pay.api.enums;


import com.cloud.baowang.common.feign.constants.RpcConstants;

/**
 * API 相关的枚举
 *
 */
public class ApiConstants {

    /**
     * 服务名
     * <p>
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "pay-biz";

    /**
     * path
     * <p>
     * 注意，需要保证和 server.servlet.context-path 保持一致
     */
    public static final String PATH = "pay";

    public static final String PREFIX = RpcConstants.NACOS_API_PREFIX;

    public static final String VERSION = "1.0.0";

}
