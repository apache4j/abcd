package com.cloud.baowang.common.core.vo.base;

import com.cloud.baowang.common.core.utils.TraceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBaseVO {


    /**
     * 消息跟踪ID
     */
    private String msgId;

    /**
     * 站点
     */
    private String siteCode;

    /**
     * 链路id
     */
    private String traceId;


    public String getTraceId() {
        return TraceUtil.getTraceId();
    }
}
