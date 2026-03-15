package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterLoginBasicInfoVO {
    @Schema( description = "总人数")
    private Integer total;
    @Schema( description = "后台")
    private Integer backed;
    @Schema( description = "pc")
    private Integer pc;
    @Schema( description = "androidH5")
    private Integer androidH5;
    @Schema( description = "androidAPP")
    private Integer androidAPP;
    @Schema( description = "ios-app")
    private Integer iosAPP;
    @Schema( description = "ios-h5")
    private Integer iosH5;
}
