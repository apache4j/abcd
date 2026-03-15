package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "邮箱站点VO")
public class ChannelSiteLinkVO {
    @Schema(title = "站点code")
    private String siteCode;
    @Schema(description = "创建人", hidden = true)
    private String creator;
    @Schema(title = "通道代码列表")
    private List<String> channelCodeList;
}
