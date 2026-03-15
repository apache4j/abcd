package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询短信通道列表接参对象")
public class ChannelSendStatisticQueryVO extends PageVO implements Serializable {

    @Schema(description ="开始时间")
    private Long startTime;
    @Schema(description ="结束时间")
    private Long endTime;

    @Schema(title = "通道名称")
    private String channelName;
    @Schema(title = "通道代码")
    private String channelCode;

    @Schema(title = "通道类型 1-短信 2-邮箱(前端不需要给)")
    private String channelType;

    private String siteCode;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

}
