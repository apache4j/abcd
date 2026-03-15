package com.cloud.baowang.common.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemParamVO implements Serializable {
    private String type;
    private String code;
    private String value;
}
