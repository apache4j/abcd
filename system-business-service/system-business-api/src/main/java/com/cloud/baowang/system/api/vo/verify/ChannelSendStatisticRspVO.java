package com.cloud.baowang.system.api.vo.verify;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema( description = "通道发送统计报表")
public class ChannelSendStatisticRspVO {
    @Schema(description = "分页数据")
    private Page<ChannelSendingStatisticVO> pages;
    @Schema(description = "小计")
    private ChannelSendingStatisticVO curRecord;
    @Schema(description = "总计")
    private ChannelSendingStatisticVO totalRecord;

}
