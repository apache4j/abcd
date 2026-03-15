package com.cloud.baowang.system.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonDifferenceVO {
    public enum DifferenceType {
        MODIFIED, ADDED, REMOVED, TYPE_CHANGED, ARRAY_SIZE_CHANGED
    }
    private DifferenceType type;
    private Object oldValue;
    private Object newValue;
    private String path;
    private String pathName;
    private String changeType;
    // 构造方法
    public JsonDifferenceVO(DifferenceType type, String path, Object oldValue, Object newValue) {
        this.type = type;
        this.path = path;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
