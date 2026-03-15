package com.cloud.baowang.common.gateway.vo;

import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

@Data
@Builder
public class AreaLimitVO {
    private String countryCode;
    private String ip;
}
