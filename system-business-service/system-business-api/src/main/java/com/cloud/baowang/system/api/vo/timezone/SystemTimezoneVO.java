package com.cloud.baowang.system.api.vo.timezone;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "系统时区视图对象")
public class SystemTimezoneVO implements Serializable {

    @Schema(description = "时区代码")
    private String timezoneCode;

}
