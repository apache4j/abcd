package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通道发送统计表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title ="通道发送统计分页对象")
public class ChannelSendDetailsRspVO  {

    @Schema(description = "站点名称")
    private String siteName;
    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "发送总量")
    private Long sendCount;

}
