package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="短信站点VO")
public class SmsSiteLinkVO {
    @Schema(title = "站点code")
    private String siteCode;
    @Schema(title = "通道代码")
    private String channelCode;
}
