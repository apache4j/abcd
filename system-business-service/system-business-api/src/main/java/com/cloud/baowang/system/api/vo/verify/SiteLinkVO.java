package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="sms邮箱客服通道VO")
public class SiteLinkVO {
    @Schema(title = "sms通道ID")
    private String channelId;
    @Schema(title = "SMS通道code")
    private String channelCode;
}
