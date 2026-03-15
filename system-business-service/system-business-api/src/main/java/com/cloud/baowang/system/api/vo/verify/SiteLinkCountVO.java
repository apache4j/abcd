package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="授权数量统计VO")
public class SiteLinkCountVO {
    @Schema(title = "通道code")
    private String channelCode;
    @Schema(title = "授权数量")
    private Integer count;
}
