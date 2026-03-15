package com.cloud.baowang.common.core.utils;


import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Component;

@Component
public class TraceUtil {
    public static String getTraceId() {
        return TraceContext.traceId();
    }
}
