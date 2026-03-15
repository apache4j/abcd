package com.cloud.baowang.system.api.verify;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.ChannelSendStatisticApi;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.service.verify.ChannelSendingStatisticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class ChannelSendStatisticApiImpl implements ChannelSendStatisticApi {
    private final ChannelSendingStatisticService statisticService;


    @Override
    public ChannelSendStatisticRspVO pageQuery( ChannelSendStatisticQueryVO queryVO) {
        return statisticService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<Long> count(ChannelSendStatisticQueryVO vo) {
        return statisticService.sendCount(vo);
    }

    @Override
    public ChannelSendDetailsTotalRspVO getChannelSendDetails(SiteInfoVO queryVO) {
        return statisticService.getChannelSendDetails(queryVO);
    }
}
