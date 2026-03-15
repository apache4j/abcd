package com.cloud.baowang.system.api.vo.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class ChannelSendDetailsTotalRspVO {
    @Schema(description = "本页集合")
    private Page<ChannelSendDetailsRspVO> page;
    @Schema(description = "小计")
    private ChannelSendDetailsRspVO curData;
    @Schema(description = "总计")
    private ChannelSendDetailsRspVO totalData;


}
