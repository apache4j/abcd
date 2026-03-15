package com.cloud.baowang.play.api.third;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.MqRecordApi;
import com.cloud.baowang.play.api.api.third.ThirdApi;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.OrderRecordMqErrPO;
import com.cloud.baowang.play.repositories.OrderRecordMqErrRepository;
import com.cloud.baowang.play.service.GamePlayService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class MqRecordApiImpl implements MqRecordApi {

    private final GameServiceFactory gameServiceFactory;
    private final OrderRecordMqErrRepository orderRecordMqErrRepository;


    /**
     * 处理失败mq注单
     * 尝试处理三次，如果仍然失败，则需要人工操作补偿
     */
    @Override
    public void errRecordDeal() {
        List<OrderRecordMqErrPO> orderRecordMqErrPOS = orderRecordMqErrRepository.selectList(Wrappers.<OrderRecordMqErrPO>lambdaQuery()
                .eq(OrderRecordMqErrPO::getStatus, CommonConstant.business_zero)
                .lt(OrderRecordMqErrPO::getTimes,3)
                .last("limit 100")
        );
        if (CollectionUtil.isEmpty(orderRecordMqErrPOS)) {
            return;
        }
        List<String> successIds = Lists.newArrayList();
        for (OrderRecordMqErrPO orderRecordMqErrPO : orderRecordMqErrPOS) {
            try {
                String venueCode = orderRecordMqErrPO.getVenueCode();
                GameService gameService = gameServiceFactory.getGameService(venueCode);
                if (gameService == null) {
                    log.error("未找到对应服务");
                    return;
                }
                OrderRecordMqVO orderRecordMqVO = JSON.parseObject(orderRecordMqErrPO.getJsonStr(), OrderRecordMqVO.class);
                gameService.orderListParse(Lists.newArrayList(orderRecordMqVO));
                successIds.add(orderRecordMqErrPO.getId());
            } catch (Exception e) {
                log.error("异常mq注单补偿操作失败; 注单信息：{}",orderRecordMqErrPO,e);
                // 更新补偿次数
                orderRecordMqErrRepository.update(null, Wrappers.<OrderRecordMqErrPO>lambdaUpdate().set(OrderRecordMqErrPO::getTimes,orderRecordMqErrPO.getTimes() + 1).eq(OrderRecordMqErrPO::getId, orderRecordMqErrPO.getId()));
            }
        }
        if (CollectionUtil.isNotEmpty(successIds)){
            orderRecordMqErrRepository.update(null, Wrappers.<OrderRecordMqErrPO>lambdaUpdate().set(OrderRecordMqErrPO::getStatus,1).in(OrderRecordMqErrPO::getId, successIds));
        }

    }
}
