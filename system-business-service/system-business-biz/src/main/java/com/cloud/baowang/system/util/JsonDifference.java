package com.cloud.baowang.system.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import kotlin.text.UStringsKt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonDifference {
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
    public JsonDifference(DifferenceType type, String path, Object oldValue, Object newValue) {
        this.type = type;
        this.path = path;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
