package com.cloud.baowang.system.api.vo.verify;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.annotations.I18nClass;

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
public class ChannelSendingStatisticVO extends BaseVO {

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "通道代码")
    private String channelCode;


    @Schema(description = "通道ID")
    private String channelId;

    @Schema(description = "发送总量")
    private Long sendCount;

    @Schema(description = "通道地址")
    private String host;

}
