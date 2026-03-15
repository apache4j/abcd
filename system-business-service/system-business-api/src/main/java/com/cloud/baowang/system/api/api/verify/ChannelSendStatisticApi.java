package com.cloud.baowang.system.api.api.verify;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.verify.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteChannelSendStatisticApi", value = ApiConstants.NAME)
@Tag(name = "RPC 通道发送统计服务 ")
public interface ChannelSendStatisticApi {
    String PREFIX = ApiConstants.PREFIX + "/channelSendStatistic/api/";

    @PostMapping(PREFIX +"pageQuery")
    @Operation(summary ="查询短信/邮箱发送数据 ")
    ChannelSendStatisticRspVO pageQuery(@RequestBody ChannelSendStatisticQueryVO queryVO);


    @Operation(summary = "获取sms发送总量" )
    @PostMapping(value = PREFIX+ "count")
    ResponseVO<Long> count(@RequestBody ChannelSendStatisticQueryVO vo);

    @Operation(summary = "根据站点查sms发送量" )
    @PostMapping(value = PREFIX+ "getChannelSendDetails")
    ChannelSendDetailsTotalRspVO getChannelSendDetails(@RequestBody SiteInfoVO queryVO);
}
