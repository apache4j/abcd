package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 通道状态改变请求对象
 *
 */
@Data
@Schema(description = "根据站点统计返回vo")
public class SiteStatisticRspVO {

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "发送总量")
    private Long sendCount;

}
