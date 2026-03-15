package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 通道状态改变请求对象
 *
 */
@Data
@Schema(description = "站点信息请求对象")
@AllArgsConstructor
@NoArgsConstructor
public class SiteInfoVO extends PageVO implements Serializable {

    @Schema(description ="开始时间")
    private Long startTime;

    @Schema(description ="结束时间")
    private Long endTime;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(title = "通道ID")
    private List<String> channelId;

    @Schema(title = "通道类型 1-短信 2-邮箱 (前端不需要给)")
    private String channelType;


    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

    @Schema(title = "通道名称")
    private String channelName;
    @Schema(title = "通道代码")
    private String channelCode;


}
